/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/
/**
 *
 */
package com.gisgraphy.importer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.gisgraphy.domain.repository.GisFeatureDao;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.service.IInternationalisationService;

/**
 * Base class for all geonames processor. it provides session management and the
 * ability to process one or more CSV file
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public abstract class AbstractSimpleImporterProcessor implements IImporterProcessor {
    protected int totalReadLine = 0;
    protected int readFileLine = 0;
    protected String statusMessage = "";

    protected ImporterStatus status = ImporterStatus.WAITING;
    
    @Autowired
    protected IInternationalisationService internationalisationService;

    /**
     * @see IImporterProcessor#getNumberOfLinesToProcess()
     */
    int numberOfLinesToProcess = 0;

    /**
     * This fields is use to generate unique featureid when importing features
     * because we don't know yet the featureId and this field is required. it
     * should be multiply by -1 to be sure that it is not in conflict with the
     * Geonames one which are all positive
     * 
     * @see GisFeatureDao#getDirties()
     */
    static Long nbGisInserted = 0L;

    protected ImporterConfig importerConfig;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(AbstractSimpleImporterProcessor.class);

    private File[] filesToProcess;

    /**
     * Lines starting with this prefix are considered as comments
     */
    protected String COMMENT_START = "#";

    private boolean hasConsumedFirstLine = false;

    /**
     * Whether the end of the document has been reached
     */
    private boolean endOfDocument = false;

    /**
     * The bufferReader for the current read Geonames file
     */
    protected BufferedReader in;

    /**
     * The transaction manager
     */
    protected PlatformTransactionManager transactionManager;

    /**
     * Template Method : Whether the processor should ignore the first line of
     * the input
     * 
     * @return true if the processor should ignore first line
     */
    protected abstract boolean shouldIgnoreFirstLine();

    /**
     * Should flush and clear all the Daos that are used by the processor. This
     * avoid memory leak
     */
    protected abstract void flushAndClear();

    /**
     * Will flush after every commit
     * 
     * @see #flushAndClear()
     */
    protected abstract void setCommitFlushMode();

    protected TransactionStatus txStatus = null;

    protected DefaultTransactionDefinition txDefinition;

    /**
     * @return the number of fields the processed Geonames file should have
     */
    protected abstract int getNumberOfColumns();

    /**
     * Whether the filter should ignore the comments (i.e. lines starting with #)
     * 
     * @see AbstractSimpleImporterProcessor#COMMENT_START
     */
    protected abstract boolean shouldIgnoreComments();

    /**
     * Whether we should consider the line as as comment or not (i.e. : it
     * doesn't start with {@link #COMMENT_START})
     * 
     * @param input
     *                the line we want to know if it is a commented line
     * @return true is the specified line is a commented line
     */
    private boolean isNotComment(String input) {
	return (!shouldIgnoreComments())
		|| (shouldIgnoreComments() && !input.startsWith(COMMENT_START));
    }

    /**
     * Default constructor
     */
    public AbstractSimpleImporterProcessor() {
	super();
    }

    /**
     * The current processed file
     */
    protected File currentFile;

    /**
     * Template method that can be override. This method is called before the
     * process start. it is not called for each file processed.
     */
    protected void setup() {
    }

    /**
     * @return The files to be process
     * @see ImporterHelper
     */
    protected abstract File[] getFiles();

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getCurrentFile()
     */
    public String getCurrentFileName() {

	if (this.currentFile != null) {
	    return this.currentFile.getName();
	}
	return "unknow";
    }

    /**
     * Process the line if needed (is not a comment, should ignore first line,
     * is end of document,...)
     * 
     * @return The number of lines that have been processed for the current
     *         processed file
     * @throws ImporterException
     *                 if an error occurred
     */
    public int readLineAndProcessData() throws ImporterException {
	if (this.isEndOfDocument()) {
	    throw new IllegalArgumentException(
		    "Must NOT be called when it is the end of the document");
	}

	String input;
	try {
	    input = (this.in).readLine();
	} catch (IOException e1) {
	    throw new ImporterException("can not read line ", e1);
	}

	if (input != null) {
	    readFileLine++;
	    if (isNotComment(input)) {
		if (this.shouldIgnoreFirstLine() && !hasConsumedFirstLine) {
		    hasConsumedFirstLine = true;
		} else {
		    try {
			this.processData(input);
		    } catch (MissingRequiredFieldException mrfe) {
			if (this.importerConfig.isMissingRequiredFieldThrows()) {
			    logger.error("A requrired field is missing "
				    + mrfe.getMessage());
			    throw new ImporterException(
				    "A requrired field is missing "
					    + mrfe.getMessage(), mrfe);
			} else {
			    logger.warn(mrfe.getMessage());
			}
		    } catch (WrongNumberOfFieldsException wnofe) {
			if (this.importerConfig.isWrongNumberOfFieldsThrows()) {
			    logger
				    .error("wrong number of fields during import "
					    + wnofe.getMessage());
			    throw new ImporterException(
				    "Wrong number of fields during import "
					    + wnofe.getMessage(), wnofe);
			} else {
			    logger.warn(wnofe.getMessage());
			}
		    } catch (Exception e) {
			String message= "An Error occurred on Line "
				+ readFileLine + " for " + input + " : "
				+ e.getMessage();
			throw new ImporterException(
				message, e);
		    }
		}
	    }

	} else {
	    this.endOfDocument = true;
	}
	return readFileLine;
    }

    /**
     * Process a read line of the geonames file, must be implemented by the
     * concrete class
     * 
     * @param line
     *                the line to process
     */
    protected abstract void processData(String line)
	    throws ImporterException;

    /**
     * Manage the transaction, flush Daos, and process all files to be processed
     */
    public void process() {
	try {
	    if (shouldBeSkipped()){
		this.status = ImporterStatus.SKIPPED;
		return;
	    }
	    this.status = ImporterStatus.PROCESSING;
	    this.getNumberOfLinesToProcess();
	    setup();
	    this.filesToProcess = getFiles();
	    if (this.filesToProcess.length == 0) {
	    	logger.info("there is 0 file to process for "
			+ this.getClass().getSimpleName());
	    	this.status= ImporterStatus.SKIPPED;
	    	return;
	    }
	    for (int i = 0; i < filesToProcess.length; i++) {
			currentFile = filesToProcess[i];
			this.endOfDocument = false;
			getBufferReader(filesToProcess[i]);
			processFile();
			closeBufferReader();
			onFileProcessed(filesToProcess[i]);
	    }
	} catch (Exception e) {
	    processError(e);
	} finally {
	    try {
		tearDown();
		this.status = this.status==ImporterStatus.PROCESSING ? ImporterStatus.PROCESSED : this.status;
		if (this.status!= ImporterStatus.ERROR){
		    this.statusMessage="";
		}
	    } catch (Exception e) {
		this.status = ImporterStatus.ERROR;
		String teardownErrorMessage= "An error occured on teardown (the import is done but maybe not optimzed) :"+e.getMessage();
		this.statusMessage = this.statusMessage != ""? this.statusMessage+ " and "+teardownErrorMessage:teardownErrorMessage ;
		logger.error(statusMessage);
	    }
	}
    }

    /**
     * Method called when there is an exception. 
     * the teardown method will be call after this
     * @param e
     */
    protected void processError(Exception e) {
	this.status = ImporterStatus.ERROR;
	this.statusMessage = "An error occurred when processing "
	    + this.getClass().getSimpleName()+ " : " + e.getMessage();
	logger.error(statusMessage,e);
	throw new ImporterException(statusMessage, e.getCause());
    }


   
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.IImporterProcessor#shouldBeSkipped()
     */
    public boolean shouldBeSkipped() {
	return false;
    }

    private void getBufferReader(File file) {
	InputStream inInternal = null;
	// uses a BufferedInputStream for better performance
	try {
	    inInternal = new BufferedInputStream(new FileInputStream(file));
	} catch (FileNotFoundException e) {
	    throw new RuntimeException(e);
	}

	try {
	    this.in = new BufferedReader(new InputStreamReader(inInternal,
		    Constants.CHARSET));
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e);
	}
    }

    private void processFile() throws ImporterException {
	try {
	    hasConsumedFirstLine = false;
	    readFileLine = 0;
	    logger.info("will process " + getCurrentFileName());
	    // Transaction Definition
	    txDefinition = new DefaultTransactionDefinition();
	    txDefinition
		    .setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	    txDefinition.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
	    txDefinition.setReadOnly(false);

	    startTransaction();
	    setCommitFlushMode();
	    while (!isEndOfDocument()) {
		this.readLineAndProcessData();
		incrementReadedFileLine(1);
		if (needCommit()) {
		    logger
			    .info("We need to commit, flushing and clearing: "
				    + totalReadLine);
		    // and commit !
		    commit();
		    startTransaction();
		    setCommitFlushMode();

		}
	    }
	    commit();
	    decrementReadedFileLine(1);// remove a processed line because it has been
	    // incremented on time more
	} catch (Exception e) {
	    rollbackTransaction();
	    throw new ImporterException(
		    "An error occurred when processing "
			    + getCurrentFileName() + " : " + e.getMessage(), e.getCause());
	}
    }

    protected int incrementReadedFileLine(int increment) {
	totalReadLine = totalReadLine+increment;
	return totalReadLine;
	
    }
    
    protected int decrementReadedFileLine(int decrement) {
	totalReadLine = totalReadLine-decrement;
	return totalReadLine;
	
    }

    protected void rollbackTransaction() {
	transactionManager.rollback(txStatus);
    }

    protected boolean needCommit() {
	return totalReadLine % this.getMaxInsertsBeforeFlush() == 0;
    }

    protected void startTransaction() {
    txDefinition = new DefaultTransactionDefinition();
	    txDefinition
		    .setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	    txDefinition.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
	    txDefinition.setReadOnly(false);
	txStatus = transactionManager.getTransaction(txDefinition);
	
    }

    /**
     * Template method that can be override. This method is called after the end
     * of the process. it is not called for each file processed.
     * You should always call super.tearDown() when you overide this method
     */
    protected void tearDown() {
	closeBufferReader();
    }

    private void closeBufferReader() {
	if (in != null) {
	    try {
		in.close();
	    } catch (IOException e) {

	    }
	}
    }
    
    /**
     * hook to do when the file has been processed without error
     * @param file
     */
    protected void onFileProcessed(File file){
    	if (importerConfig.isRenameFilesAfterProcessing()){
    		currentFile.renameTo(new File(currentFile.getAbsoluteFile()+".done"));
    	}
    }

    protected void commit() {
		flushAndClear();
		transactionManager.commit(this.txStatus);
    }

    /**
     * Check that the array is not null, and the fields of the specified
     * position is not empty (after been trimed)
     * 
     * @param fields
     *                The array to test
     * @param position
     *                the position of the field to test in the array
     * @param required
     *                if an exception should be thrown if the field is empty
     * @return true is the field of the specifed position is empty
     * @throws MissingRequiredFieldException
     *                 if the fields is empty and required is true
     */
    protected static boolean isEmptyField(String[] fields, int position,
    		boolean required) {
    	if (fields == null) {
    		if (!required) {
    			return true;
    		} else {
    			throw new MissingRequiredFieldException(
    					"can not chek fields if the array is null");
    		}
    	}
    	if (position < 0) {
    		if (!required) {
    			return true;
    		} else {
    		throw new MissingRequiredFieldException(
    				"position can not be < 0 => position = " + position);
    		}
    	}
    	if (fields.length == 0) {
    		if (!required) {
    			return true;
    		} else {
    		throw new MissingRequiredFieldException("fields is empty");
    		}
    	}
    	if (position > (fields.length - 1)) {

    		if (!required) {
    			return true;
    		} else {
    			throw new MissingRequiredFieldException("fields has "
    					+ (fields.length)
    					+ " element(s), can not get element with position "
    					+ (position) + " : " + dumpFields(fields));
    		}

    	}
    	String string = fields[position];
    	if (string != null && (string.trim().equals("") || string.equals("\"\""))) {
    		if (!required) {
    			return true;
    		} else {
    			throw new MissingRequiredFieldException("fields[" + position
    					+ "] is required for featureID " + fields[0] + " : "
    					+ dumpFields(fields));
    		}
    	}
    	return false;

    }

    /**
     * @param fields
     *                The array to process
     * @return a string which represent a human readable string of the Array
     */
    protected static String dumpFields(String[] fields) {
	String result = "[";
	for (String element : fields) {
	    result = result + element + ";";
	}
	return result + "]";
    }

    /**
     * Utility method which throw an exception if the number of fields is not
     * the one expected (retrieved by {@link #getNumberOfColumns()})
     * 
     * @see #getNumberOfColumns()
     * @param fields
     *                The array to check
     */
    protected void checkNumberOfColumn(String[] fields) {
	if (fields.length != getNumberOfColumns()) {

	    throw new WrongNumberOfFieldsException(
		    "The number of fields is not correct. expected : "
			    + getNumberOfColumns() + ", founds :  "
			    + fields.length+ ". details :"+dumpFields(fields));
	}

    }

    /**
     * @return true if the end of the document for the current processed file is
     *         reached
     */
    protected boolean isEndOfDocument() {
	return endOfDocument;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getReadFileLine()
     */
    public long getReadFileLine() {
	return this.readFileLine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getTotalReadedLine()
     */
    public long getTotalReadLine() {
	return this.totalReadLine;
    }

    @Required
    public void setTransactionManager(
	    PlatformTransactionManager transactionManager) {
	this.transactionManager = transactionManager;
    }

    @Required
    public void setImporterConfig(ImporterConfig importerConfig) {
	this.importerConfig = importerConfig;
    }

    /**
     * @return the number of line to process
     */
    protected int countLines(File[] files) {
    logger.info("counting lines");
	int lines = 0;
	BufferedReader br = null;
	BufferedInputStream bis = null;
	for (int i = 0; i < files.length; i++) {
	    File countfile = files[i];
	    logger.info("counting lines of "+countfile);
	    try {
		bis = new BufferedInputStream(new FileInputStream(countfile));
		br = new BufferedReader(new InputStreamReader(bis,
			Constants.CHARSET));
		while (br.readLine() != null) {
		    lines++;
		}
	    } catch (Exception e) {
		String filename = countfile == null ? null : countfile
			.getName();
		logger.warn("can not count lines for " + filename + " : "
			+ e.getMessage(), e);
		logger.info("end of counting lines");
		return lines;
	    } finally {
		if (bis != null) {
		    try {
			bis.close();
		    } catch (IOException e) {

		    }
		}
		if (br != null) {
		    try {
			br.close();
		    } catch (IOException e) {

		    }
		}
	    }
	}

	logger.info("There is " + lines + " to process for "
		+ this.getClass().getSimpleName());
	return lines;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getNumberOfLinesToProcess()
     */
    public long getNumberOfLinesToProcess() {
	if (this.numberOfLinesToProcess == 0 && this.status == ImporterStatus.PROCESSING) {
	    // it may not have been calculated yet
	    this.numberOfLinesToProcess = countLines(getFiles());
	}
	return this.numberOfLinesToProcess;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getStatus()
     */
    public ImporterStatus getStatus() {
	return this.status;
    }

    /**
     * @return The option
     * @see ImporterConfig#setMaxInsertsBeforeFlush(int)
     */
    protected int getMaxInsertsBeforeFlush() {
    	return importerConfig.getMaxInsertsBeforeFlush();
    }

    public void resetStatus() {
	this.currentFile = null;
	this.readFileLine = 0;
	this.totalReadLine = 0;
	this.numberOfLinesToProcess = 0;
	this.status = ImporterStatus.WAITING;
	this.statusMessage = "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getErrorMessage()
     */
    public String getStatusMessage() {
	return statusMessage;
    }

    public void setInternationalisationService(IInternationalisationService internationalisationService) {
        this.internationalisationService = internationalisationService;
    }

}
