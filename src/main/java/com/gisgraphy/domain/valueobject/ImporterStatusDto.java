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
package com.gisgraphy.domain.valueobject;

import com.gisgraphy.importer.IImporterProcessor;

/**
 * Represents a status of an IgeonamesProcessor
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * @see ImporterStatus
 */
public class ImporterStatusDto {

    private static final int NUMBER_OF_FIELDS = 7;
    /**
     * The csv line separator
     * 
     * @see #toCSV()
     */
    public static final String CSV_LINE_SEPARATOR = "\r\n";
    /**
     * The csv field separator (we have chosen a very improbable one)
     * 
     * @see #toCSV()
     */
    public static final String CSV_FIELD_SEPARATOR = "ŝ";
    /**
     * the default value of the currentFile if it is null
     */
    public static final String DEFAULT_CURRENT_FILE = " any file ";
    
    private String processorName = "";
    private String currentFileName = DEFAULT_CURRENT_FILE;
    private long currentLine = 0;
    private long numberOfLinelefts = 0;
    private long numberOfLineToProcess = 0;
    private long numberOfLineProcessed = 0;
    private int percent = 0;
    private String statusMessage = "";
    private ImporterStatus status = ImporterStatus.UNKNOW;

    /**
     * @param processorName
     *                The name of the processor (typically the className)
     * @param currentFileName
     *                wich file is currently processed
     * @param currentLine
     *                which line of the currentFileName is processed
     * @param numberOfLineToProcess
     *                the total of line to be process by the importer
     * @param numberOfLineProcessed
     *                The total of line already process by this importer
     * @param statusMessage
     *                a message
     * @param status
     */
    public ImporterStatusDto(String processorName, String currentFileName,
	    long currentLine, long numberOfLineToProcess,
	    long numberOfLineProcessed, String statusMessage,
	    ImporterStatus status) {
	super();
	this.processorName = processorName;
	setCurrentFileName(currentFileName);
	this.currentLine = currentLine;
	this.numberOfLineToProcess = numberOfLineToProcess;
	this.numberOfLineProcessed = numberOfLineProcessed;
	this.statusMessage = statusMessage;
	this.status = status;
	calculateFields();
    }


    public ImporterStatusDto(IImporterProcessor processor) {
	this.processorName = processor.getClass().getSimpleName();
	setCurrentFileName(processor.getCurrentFileName());

	this.status = processor.getStatus();
	this.statusMessage = processor.getStatusMessage();
	this.currentLine = processor.getReadFileLine();
	this.numberOfLineToProcess = processor.getNumberOfLinesToProcess();
	this.numberOfLineProcessed = processor.getTotalReadLine();
	calculateFields();
    }

    /**
     * Construct a {@linkplain ImporterStatusDto} from a csv line
     * 
     * @param csv
     *                the String that represent the
     *                {@linkplain ImporterStatusDto}
     */
    public ImporterStatusDto(String csv) {
	if (csv.endsWith(CSV_LINE_SEPARATOR)) {
	    // it is not a csv from a readline
	    csv = csv.substring(0, csv.length() - CSV_LINE_SEPARATOR.length());
	}
	String[] fields = csv.split(CSV_FIELD_SEPARATOR);
	if (fields.length != NUMBER_OF_FIELDS) {
	    throw new IllegalArgumentException("CSV must have "
		    + NUMBER_OF_FIELDS + " fields");
	}
	this.processorName = fields[0];
	setCurrentFileName(fields[1]);
	this.currentLine = Long.valueOf(fields[2]);
	this.numberOfLineToProcess = Long.valueOf(fields[3]);
	this.numberOfLineProcessed = Long.valueOf(fields[4]);
	this.statusMessage = fields[5];
	try {
	    this.status = ImporterStatus.valueOf(fields[6]);
	} catch (Exception e) {
	    
	   
	}
	calculateFields();
    }

    private void calculateFields() {
    	if (numberOfLineToProcess <0 || numberOfLineProcessed < 0){
    		status=ImporterStatus.UNKNOW;//todo test
    		this.numberOfLinelefts = 0;
    		this.percent = 0;
    		return;
    	}
	this.numberOfLinelefts = (this.numberOfLineToProcess - this.numberOfLineProcessed);
	if (numberOfLineToProcess != 0) {
	    this.percent = new Long((numberOfLineProcessed * 100)
		    / numberOfLineToProcess).intValue();
	}
	else if (numberOfLineProcessed==0 && (status == ImporterStatus.PROCESSED || status == ImporterStatus.PROCESSING )){
	    percent = 100;
	}
    }

    /**
     * @return the processorName
     */
    public String getProcessorName() {
	return processorName;
    }

    /**
     * @return the currentFile
     */
    public String getCurrentFileName() {
	return currentFileName;
    }

    /**
     * @return the currentLine
     */
    public long getCurrentLine() {
	return currentLine;
    }

    /**
     * @return the numberOfLineToProcess
     */
    public long getNumberOfLineToProcess() {
	return numberOfLineToProcess;
    }

    /**
     * @return the numberOfLineProcessed
     */
    public long getNumberOfLineProcessed() {
	return numberOfLineProcessed;
    }

    /**
     * @return the percent
     */
    public int getPercent() {
	return percent;
    }

    /**
     * @return the status
     */
    public ImporterStatus getStatus() {
	return status;
    }

    /**
     * @return the numberOfLinelefts
     */
    public long getNumberOfLinelefts() {
	return numberOfLinelefts;
    }

    /**
     * @return the errorMessage
     */
    public String getStatusMessage() {
	return statusMessage;
    }

    /**
     * @param currentFileName
     *                the currentFileName to set, the CurrentFileName will be
     *                set to {@link #DEFAULT_CURRENT_FILE} if the argument is
     *                null
     */
    private void setCurrentFileName(String currentFileName) {
	if (currentFileName != null) {
	    this.currentFileName = currentFileName;
	} else {
	    this.currentFileName = DEFAULT_CURRENT_FILE;
	}
    }

    public String toCSV() {
	StringBuffer sb = new StringBuffer();
	return sb.append(this.processorName).append(CSV_FIELD_SEPARATOR)
		.append(this.currentFileName).append(CSV_FIELD_SEPARATOR)
		.append(this.currentLine).append(CSV_FIELD_SEPARATOR).append(
			this.numberOfLineToProcess).append(CSV_FIELD_SEPARATOR)
		.append(this.numberOfLineProcessed).append(CSV_FIELD_SEPARATOR)
		.append(this.statusMessage).append(CSV_FIELD_SEPARATOR).append(
			status.name()).append(CSV_LINE_SEPARATOR).toString();
    }

}
