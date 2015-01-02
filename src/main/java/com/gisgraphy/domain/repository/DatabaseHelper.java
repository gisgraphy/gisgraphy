/**
 * 
 */
package com.gisgraphy.domain.repository;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernatespatial.postgis.PostgisDialectNG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.helper.FileLineFilter;

/**
 * Default implementation of {@link IDatabaseHelper}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Component
public class DatabaseHelper extends HibernateDaoSupport implements IDatabaseHelper {

	protected static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);
	
	public static String[] TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT= {"app_user","role","user_role","StatsUsage"};
	
	public static final String NORMALIZE_TEXT_FUNCTION_NAME = "normalize_text";
	
	public static final String NORMALIZE_TEXT_POSTRES_FUNCTION_BODY = String.format("CREATE OR REPLACE FUNCTION %s(text) RETURNS text AS 'BEGIN RETURN replace(replace(replace(replace(replace(translate(trim(lower($1)),''âãäåāăąàèéêëēĕėęěìíîïìĩīĭóôõöōŏőðøùúûüũūŭůçñÿ'',''aaaaaaaaeeeeeeeeeiiiiiiiiooooooooouuuuuuuucny''),''-'','' ''),''.'','' ''),''\\;'','' ''),''\"'','' ''),'''''''','' '') \u003B END '  LANGUAGE 'plpgsql' RETURNS NULL ON NULL INPUT ",DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME);

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IDatabaseHelper#execute(java.io.File, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<String> execute(final File file, final boolean continueOnError) throws Exception {
		if (file == null) {
			throw new IllegalArgumentException("Can not execute a null file");
		}

		if (!file.exists()) {
			throw new IllegalArgumentException("The specified file does not exists and can not be executed : " + file.getAbsolutePath());
		}
		logger.info("will execute sql file " + file.getAbsolutePath());

		return (List<String>) this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws PersistenceException {
			    	List<String> exceptionMessageList = new ArrayList<String>();
				BufferedReader reader;
				
				
				InputStream inInternal = null;
				// uses a BufferedInputStream for better performance
				try {
				    inInternal = new BufferedInputStream(new FileInputStream(file));
				} catch (FileNotFoundException e) {
				    throw new RuntimeException(e);
				}
				try {
				    reader = new BufferedReader(new InputStreamReader(inInternal,
					    Constants.CHARSET));
				} catch (UnsupportedEncodingException e) {
				    throw new RuntimeException(e);
				}
				String line;
				int count = 0;
				try {
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						 // comment or empty or psql command
						if (line.startsWith("--") || line.length() == 0 || line.startsWith("\\"))
						{
							continue;
						} 
						Query createIndexQuery = session.createSQLQuery(line);
						try {
						    int nbupdate = createIndexQuery.executeUpdate();
						    logger.info("execution of line : "+line+" modify "+nbupdate+" lines");
						} catch (Exception e) {
							String msg = "Error on line "+count+" ("+line +") :" +e.getCause();
							logger.error(msg,e);
							exceptionMessageList.add(msg);
							if (!continueOnError){
							    throw new PersistenceException(e.getCause());
							}
						} 
					}
				} catch (IOException e) {
					logger.error("error on line "+count+" : "+e,e);
				} 
				return exceptionMessageList;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IDatabaseHelper#generateSqlCreationSchemaFile(java.io.File)
	 */
	public void generateSqlCreationSchemaFile(File outputFile){
	    logger.info("Will generate file to create tables");
	   createSqlSchemaFile(outputFile,true,false,false);
	}
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IDatabaseHelper#generateSqlDropSchemaFile(java.io.File)
	 */
	public void generateSQLDropSchemaFile(File outputFile){
	    logger.info("Will generate file to drop tables");
	    createSqlSchemaFile(outputFile,false,true,false);
	}
	
	

	private List<SQLException> createSqlSchemaFile(File outputFile,boolean create, boolean drop, boolean execute ){
	Assert.notNull(outputFile,"Can not create a sql schema in a null file, please specify a valid one");
	AnnotationConfiguration config = new AnnotationConfiguration();
	config.setProperty("hibernate.dialect",PostgisDialectNG.class.getName());
		config.configure();
		SchemaExport schemaExporter =null;
		if (execute == true){
		java.sql.Connection connection = getSession().connection();
		schemaExporter = new SchemaExport(config,connection);
		} else {
		   schemaExporter = new SchemaExport(config);
		}
		if (outputFile != null){
		    schemaExporter.setOutputFile(outputFile.getAbsolutePath());
		}
		logger.info("will create the Database schema");
		if (create == true){
		    schemaExporter.create(true, true);
		}else if (drop == true){
		    schemaExporter.drop(true, true);
		}
		schemaExporter.execute(true, execute, drop, create);
		return schemaExporter.getExceptions();
	}

	

	public void generateSQLCreationSchemaFileToRerunImport(File outputFile) {
	    logger.info("Will generate file to create tables to reset import");
	    File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	    File fileToBeFiltered = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "createAllTables.sql");
	    generateSqlCreationSchemaFile(fileToBeFiltered);
	    FileLineFilter filter = new FileLineFilter(DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT);
	    filter.filter(fileToBeFiltered, outputFile);
	    fileToBeFiltered.delete();
	    tempDir.delete();
	}

	public void generateSqlDropSchemaFileToRerunImport(File outputFile) {
	    logger.info("Will generate file to drop tables to reset import");
	    File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	    File fileToBeFiltered = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "dropAllTables.sql");
	    generateSQLDropSchemaFile(fileToBeFiltered);
	    FileLineFilter filter = new FileLineFilter(DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT);
	    filter.filter(fileToBeFiltered, outputFile);
	    fileToBeFiltered.delete();
	    tempDir.delete();
	}

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IDatabaseHelper#createNormalize_textFunction()
	 */
	public void createNormalize_textFunction() {
	    logger.info("will create "+DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" function");
	     this.getHibernateTemplate().execute(
			new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				
				Query qry = session.createSQLQuery(NORMALIZE_TEXT_POSTRES_FUNCTION_BODY);
				qry.executeUpdate();
				return  null;
			    }
			});
	     logger.info(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" function has been created");
	    
	}
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IDatabaseHelper#isNormalize_textFunctionCreated()
	 */
	public boolean isNormalize_textFunctionCreated() {
	    try {
		this.getHibernateTemplate().execute(
			new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				
				Query qry = session.createSQLQuery("select "+DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+"('é-è.ê''à\"ù')");
				Object result = qry.uniqueResult();
				if ("e e e a u"!= result){
				    logger.info(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" function does not return the expected value : we consider that the function is not created");
				    return false;
				}
				return true;
			    }
			});
	    } catch (Exception e) {
		  logger.info(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" function has generate an exception : we consider that the function is not created : "+e);
		  return false;
	    }
	     logger.info(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" function has been successfully called : we consider that the function is created");
	     return true;
	    
	}
	
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IDatabaseHelper#dropNormalize_textFunction()
	 */
	public void dropNormalize_textFunction(){
	    logger.info("will drop "+DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" function");
	     this.getHibernateTemplate().execute(
			new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				
				Query qry = session.createSQLQuery("DROP FUNCTION IF EXISTS "+DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+"normalize_text(text)");
				qry.executeUpdate();
				return  null;
			    }
			});
	     logger.info(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" function has been drop");
	}
	
	
}
