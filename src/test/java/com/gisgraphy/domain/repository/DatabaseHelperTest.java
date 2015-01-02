/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.domain.repository;

import static com.gisgraphy.test.GisgraphyTestHelper.isFileContains;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.test.GisgraphyTestHelper;

public class DatabaseHelperTest extends AbstractTransactionalTestCase {

    IDatabaseHelper databaseHelper;

    /**
     * @param databaseHelper
     *            the databaseHelper to set
     */
    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
	this.databaseHelper = databaseHelper;
    }

    
    @Test
    public void testCreateNormalize_textFunctionShouldNotThrow() throws Exception {
	databaseHelper.createNormalize_textFunction();
    }
    
    @Test
    public void testdropNormalize_textFunctionShouldNotThrow() throws Exception {
	databaseHelper.dropNormalize_textFunction();
    }
    
    @Test
    public void testisNormalize_textFunctionCreatedShouldReturnTrueAfterCreation() throws Exception {
	databaseHelper.createNormalize_textFunction();
	Assert.assertTrue("after creation the Normalize_text() sql function should be created in postgres",databaseHelper.isNormalize_textFunctionCreated()); 
    }
    
    @Test
    public void testIsNormalize_textFunctionCreatedShouldReturnFalseAfterDrop() throws Exception {
	databaseHelper.dropNormalize_textFunction();
    }

    @Test
    public void testExecuteFileSuccess() throws Exception {
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File file = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "sqlErrorFile.sql");
	FileOutputStream fos = null;
	OutputStreamWriter out = null;
	try {
	    fos = new FileOutputStream(file);
	    out = new OutputStreamWriter(fos, "UTF-8");

	    out.write("\\connect psql command that should be ignore\r\n");
	    out.write("create table test_Table (id int8 not null);\r\n");
	    out.write("-- comment \r\n");
	    out.write("drop table test_Table;\r\n");
	    out.flush();
	} finally {
	    try {
		if (fos != null) {
		    fos.flush();
		    fos.close();
		}
		if (out != null) {
		    out.flush();
		    out.close();
		}
	    } catch (Exception ignore) {
		// ignore
	    }
	}

	Assert.assertTrue("The file has not been process correctly", databaseHelper.execute(new File(file.getAbsolutePath()), false).isEmpty());
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }

    @Test
    public void testExecuteFileFailureWithContinueOnError() throws Exception {
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File file = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "sqlErrorFile.sql");
	FileOutputStream fos = null;
	OutputStreamWriter out = null;
	try {
	    fos = new FileOutputStream(file);
	    out = new OutputStreamWriter(fos, "UTF-8");

	    out.write("create table test_Table (id int8 not null);\r\n");
	    out.write("-- comment \r\n");
	    out.write("do something postgres don't understand\r\n");
	    out.write("drop table test_Table;\r\n");
	    out.flush();
	} finally {
	    try {
		if (fos != null) {
		    fos.flush();
		    fos.close();
		}
		if (out != null) {
		    out.flush();
		    out.close();
		}
	    } catch (Exception ignore) {
		// ignore
	    }
	}

	Assert.assertFalse("The file has not been process correctly", databaseHelper.execute(new File(file.getAbsolutePath()), true).isEmpty());
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }

    @Test
    public void testGenerateSqlCreationSchemaFileShouldCreateTheFileAndNotContainsDropInstruction() {
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File file = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "createTables.sql");
	databaseHelper.generateSqlCreationSchemaFile(file);
	assertFalse("the creation script should not contains 'drop'", isFileContains(file, "drop"));
	assertTrue("the creation script should contains 'create'", isFileContains(file, "create"));
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }

    @Test
    public void testGenerateSqlDropSchemaFileShouldCreateTheFileAndNotContainsCreateInstruction() {
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File file = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "dropTables.sql");
	databaseHelper.generateSQLDropSchemaFile(file);
	assertTrue("the drop SQL script should 'drop'", isFileContains(file, "drop"));
	assertFalse("the drop SQL script should contains 'create'", isFileContains(file, "create"));
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }

    @Test
    public void testGenerateSQLCreationSchemaFileToRerunImport() {
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File file = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "createTablesToRerunImport.sql");
	databaseHelper.generateSQLCreationSchemaFileToRerunImport(file);
	assertFalse("The creation script to re-run import should not contains 'drop'", isFileContains(file, "drop"));
	assertTrue("The creation script to re-run import should contains 'create'", isFileContains(file, "create"));
	for (int i = 0; i < DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT.length; i++) {
	    assertFalse("The creation script to re-run import should not contains " + DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT[i], 
		    isFileContains(file, DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT[i]));
	}
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }
    

    @Test
    public void testGenerateSqlDropSchemaFileToRerunImport() {
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File file = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "dropTablesToRerunImport.sql");
	databaseHelper.generateSqlDropSchemaFileToRerunImport(file);
	assertTrue("The drop script to re-run import should contains 'drop'", isFileContains(file, "drop"));
	assertFalse("The drop script to re-run import should not contains create", isFileContains(file, "create"));
	for (int i = 0; i < DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT.length; i++) {
	    assertFalse("The drop script to re-run import should not contains " + DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT[i], 
		    isFileContains(file, DatabaseHelper.TABLES_NAME_THAT_MUST_BE_KEPT_WHEN_RESETING_IMPORT[i]));
	}
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }
    
    @Test
    public void testGenerateSQLCreationSchemaFileToRerunImportShouldBeCoherentWithGenerateSqlDropSchemaFileToRerunImport(){
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File fileDrop = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "dropTablesToRerunImport.sql");
	databaseHelper.generateSqlDropSchemaFileToRerunImport(fileDrop);
	
	File fileCreate = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "createTablesToRerunImport.sql");
	databaseHelper.generateSQLCreationSchemaFileToRerunImport(fileCreate);
	
	int numberOfTablesDeletion = GisgraphyTestHelper.countLinesInFileThatStartsWith(fileDrop, "drop table");
	int numberOfTablesCreation = GisgraphyTestHelper.countLinesInFileThatStartsWith(fileCreate, "create table");
	Assert.assertEquals("number of table deletion = "+numberOfTablesDeletion+" but number of tables creation="+numberOfTablesCreation,numberOfTablesCreation,numberOfTablesDeletion );
	
	int numberOfSequenceDeletion = GisgraphyTestHelper.countLinesInFileThatStartsWith(fileDrop, "drop sequence");
	int numberOfSequenceCreation = GisgraphyTestHelper.countLinesInFileThatStartsWith(fileCreate, "create sequence");
	Assert.assertEquals("number of sequence deletion = "+numberOfSequenceDeletion+" but number of sequence creation="+numberOfSequenceCreation,numberOfSequenceDeletion,numberOfSequenceCreation );
	
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }
    
    @Test
    public void testExecuteSqlDropSchemaFileToRerunImportAndCreateShouldNotThrows() throws Exception {
	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File fileDrop = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "dropTablesToRerunImport.sql");
	databaseHelper.generateSqlDropSchemaFileToRerunImport(fileDrop);
	
	File fileCreate = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "createTablesToRerunImport.sql");
	databaseHelper.generateSQLCreationSchemaFileToRerunImport(fileCreate);
	List<String> dropErrors = databaseHelper.execute(fileDrop,true);
	List<String> createErrors = databaseHelper.execute(fileCreate,true);
	Assert.assertTrue("the drop Database script has generate "+dropErrors.size()+" errors : "+concatenateErrors(dropErrors),dropErrors.isEmpty());
	Assert.assertTrue("the create Database script has generate "+createErrors.size()+" errors : "+concatenateErrors(createErrors),createErrors.isEmpty());
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
    }
    
    private String concatenateErrors(List<String> errorsList){
    	String concatenateErrors= "";
    	for (String error: errorsList){
    		concatenateErrors = concatenateErrors + " "+ error+"\r\n";
    	}
    	return concatenateErrors;
    }
  
    @Test
    public void testGenerateSqlCreationSchemaFileThrowsIfFileIsNull() {
	try {
	    databaseHelper.generateSqlCreationSchemaFile(null);
	    fail("we should not allow creation of sql schema in a null file");
	} catch (IllegalArgumentException ignore) {
	}
    }
    
  

}
