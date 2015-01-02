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
package com.gisgraphy.importer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.NameValueDTO;

// TODO v2 a factory and the ability to extract a specific featureclasscode
/**
 * Extract 4 files in CSV format in order to import Adm. This files will be in
 * the same format as the Geonames Adm1Codes.txt file
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class AdmExtracter extends AbstractSimpleImporterProcessor {

    private File adm1file;

    private File adm2file;

    private File adm3file;

    private File adm4file;

    private OutputStreamWriter adm1fileOutputStreamWriter;

    private OutputStreamWriter adm2fileOutputStreamWriter;

    private OutputStreamWriter adm3fileOutputStreamWriter;

    private OutputStreamWriter adm4fileOutputStreamWriter;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm:ss");

    private StringBuffer sb = new StringBuffer();

    @Autowired
    private GeonamesAdm1Importer geonamesAdm1Importer;

    @Autowired
    private GeonamesAdm2Importer geonamesAdm2Importer;

    @Autowired
    private GeonamesAdm3Importer geonamesAdm3Importer;

    @Autowired
    private GeonamesAdm4Importer geonamesAdm4Importer;

    /**
     * Default Constructor
     */
    public AdmExtracter() {
	super();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
     */
    @Override
    protected void processData(String line) {
	String[] fields = line.split("\t");

	/*
	 * line table has the following fields :
	 * --------------------------------------------------- 0 geonameid : 1
	 * name 2 asciiname 3 alternatenames 4 latitude 5 longitude 6 feature
	 * class 7 feature code 8 country code 9 cc2 10 admin1 code 11 admin2
	 * code 12 admin3 code 13 admin4 code 14 population 15 elevation 16
	 * gtopo30 17 timezone 18 modification date last modification in
	 * yyyy-MM-dd format
	 */

	// isEmptyField(fields,0,true);
	// isEmptyField(fields,1,true);
	checkNumberOfColumn(fields);
	if (!isEmptyField(fields, 6, false) && !isEmptyField(fields, 7, false)) {
	    // fields = ImporterHelper.virtualizeADMD(fields);
	    fields = ImporterHelper.correctLastAdmCodeIfPossible(fields);
	    if (checkAdmTypeAndLevel(1, fields[6], fields[7])) {
		processAdm1ToGeonamesExportFormat(fields);
	    } else if (checkAdmTypeAndLevel(2, fields[6], fields[7])) {
		processAdm2ToGeonamesExportFormat(fields);
	    } else if (checkAdmTypeAndLevel(3, fields[6], fields[7])) {
		processAdm3ToGeonamesExportFormat(fields);
	    } else if (checkAdmTypeAndLevel(4, fields[6], fields[7])) {
		processAdm4ToGeonamesExportFormat(fields);
	    }
	} else {
	    logger.info("featureid " + fields[0]
		    + " has featurecode or featureclass with a null value");
	}
    }
    
   
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped()
     */
    @Override
    public boolean shouldBeSkipped() {
	return !importerConfig.isGeonamesImporterEnabled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#tearDown()
     */
    @Override
    protected void tearDown() {
	super.tearDown();
	closeOutputStreams();
	// Force number of line to be processed after extract
	if (importerConfig.getAdmExtracterStrategyOptionsForAdm(1) != AdmExtracterStrategyOptions.skip) {
	    geonamesAdm1Importer.numberOfLinesToProcess = 0;
	}
	if (importerConfig.getAdmExtracterStrategyOptionsForAdm(2) != AdmExtracterStrategyOptions.skip) {
	    geonamesAdm2Importer.numberOfLinesToProcess = 0;
	}
	if (importerConfig.getAdmExtracterStrategyOptionsForAdm(3) != AdmExtracterStrategyOptions.skip) {
	    geonamesAdm3Importer.numberOfLinesToProcess = 0;
	}
	if (importerConfig.getAdmExtracterStrategyOptionsForAdm(4) != AdmExtracterStrategyOptions.skip) {
	    geonamesAdm4Importer.numberOfLinesToProcess = 0;
	}
    }

    /**
     * @param fields
     *                The array of fields for the current read line Process the
     *                line and write it in Geonames CSV format to the Adm4 file
     */
    private void processAdm4ToGeonamesExportFormat(String[] fields) {
	if (adm4fileOutputStreamWriter != null) {
	    String stringToWrite = "";
	    if (!isEmptyField(fields, 8, true)
		    && !isEmptyField(fields, 10, true)
		    && !isEmptyField(fields, 11, true)
		    && !isEmptyField(fields, 12, true)
		    && !isEmptyField(fields, 13, true)
		    && !isEmptyField(fields, 1, true)) {
		sb = sb.delete(0, sb.length());
		sb = sb.append(fields[8]).append(".").append(fields[10])
			.append(".").append(fields[11]).append(".").append(
				fields[12]).append(".").append(fields[13])
			.append("\t").append(fields[1].trim()).append("\r\n");
		stringToWrite = sb.toString();
		try {
		    adm4fileOutputStreamWriter.write(stringToWrite);
		    flushAndClear();
		} catch (IOException e) {
		    throw new RuntimeException(
			    "An error has occurred when writing in adm4 file",
			    e);
		}
	    }
	}
    }

    /**
     * @param fields
     *                The array of fields for the current read line Process the
     *                line and write it in Geonames CSV format to the Adm3 file
     */
    private void processAdm3ToGeonamesExportFormat(String[] fields) {
	if (adm3fileOutputStreamWriter != null) {
	    String stringToWrite = "";
	    if (!isEmptyField(fields, 8, true)
		    && !isEmptyField(fields, 10, true)
		    && !isEmptyField(fields, 11, true)
		    && !isEmptyField(fields, 12, true)
		    && !isEmptyField(fields, 1, true)) {
		sb = sb.delete(0, sb.length());
		sb = sb.append(fields[8]).append(".").append(fields[10])
			.append(".").append(fields[11]).append(".").append(
				fields[12]).append("\t").append(
				fields[1].trim()).append("\r\n");
		stringToWrite = sb.toString();
		try {
		    adm3fileOutputStreamWriter.write(stringToWrite);
		    flushAndClear();
		} catch (IOException e) {
		    throw new RuntimeException(
			    "an error has occurred when writing in adm3 file",
			    e);
		}
	    }
	}
    }

    /**
     * @param fields
     *                The array of fields for the current read line Process the
     *                line and write it in Geonames CSV format to the Adm2 file
     *                The adm2 format is different from Adm1 ,3 and 4 because
     *                Ascii name and FeatureId are also exported
     */
    private void processAdm2ToGeonamesExportFormat(String[] fields) {
	if (adm2fileOutputStreamWriter != null) {
	    String stringToWrite = "";
	    if (!isEmptyField(fields, 8, true)
		    && !isEmptyField(fields, 10, true)
		    && !isEmptyField(fields, 11, true)
		    && !isEmptyField(fields, 1, true)
		    && !isEmptyField(fields, 0, true)) {
		sb = sb.delete(0, sb.length());
		sb = sb.append(fields[8]).append(".").append(fields[10])
			.append(".").append(fields[11]).append("\t").append(
				fields[1].trim()).append("\t").append(
				fields[2].trim()).append("\t")
			.append(fields[0]).append("\r\n");
		stringToWrite = sb.toString();
		try {
		    adm2fileOutputStreamWriter.write(stringToWrite);
		    flushAndClear();
		} catch (IOException e) {
		    throw new RuntimeException(
			    "an error has occurred when writing in adm4 file",
			    e);
		}
	    }
	}
    }

    /**
     * @param fields
     *                The array of fields for the current read line Process the
     *                line and write it in Geonames CSV format to the Adm1 file
     */
    private void processAdm1ToGeonamesExportFormat(String[] fields) {
	if (adm1fileOutputStreamWriter != null) {
	    String stringToWrite = "";
	    if (!isEmptyField(fields, 8, true)
		    && !isEmptyField(fields, 10, true)
		    && !isEmptyField(fields, 1, true)) {
		sb = sb.delete(0, sb.length());
		sb = sb.append(fields[8]).append(".").append(fields[10])
			.append("\t").append(fields[1]).append("\r\n");
		stringToWrite = sb.toString();
		try {
		    adm1fileOutputStreamWriter.write(stringToWrite);
		    flushAndClear();
		} catch (IOException e) {
		    throw new RuntimeException(
			    "an error has occurred when writing in adm4 file",
			    e);
		}
	    }
	}
    }

    private void closeOutputStreams() {
	if (adm1fileOutputStreamWriter != null) {
	    try {
		adm1fileOutputStreamWriter.close();
	    } catch (IOException e) {
		throw new RuntimeException("can not close adm1 outputStream", e);
	    }
	}
	if (adm2fileOutputStreamWriter != null) {
	    try {
		adm2fileOutputStreamWriter.close();
	    } catch (IOException e) {
		throw new RuntimeException("can not close adm2 outputStream", e);
	    }
	}
	if (adm3fileOutputStreamWriter != null) {
	    try {
		adm3fileOutputStreamWriter.close();
	    } catch (IOException e) {
		throw new RuntimeException("can not close adm3 outputStream", e);
	    }
	}
	if (adm4fileOutputStreamWriter != null) {
	    try {
		adm4fileOutputStreamWriter.close();
	    } catch (IOException e) {
		throw new RuntimeException("can not close adm4 outputStream", e);
	    }
	}
    }

    private boolean checkAdmTypeAndLevel(int expectedLevel,
	    String featureClass, String featureCode) {
	if (featureClass.equals("A") && featureCode.startsWith("ADM")
		&& featureCode.endsWith(expectedLevel + "")) {
	    return true;
	}
	return false;
    }

    private OutputStreamWriter getWriter(File file, int admLevel)
	    throws FileNotFoundException {
	OutputStream o = null;
	OutputStreamWriter w = null;
	try {
	    if (!file.exists()
		    || (file.exists() && importerConfig
			    .getAdmExtracterStrategyOptionsForAdm(admLevel) == AdmExtracterStrategyOptions.reprocess)) {
		o = new BufferedOutputStream(new FileOutputStream(file));
		w = new OutputStreamWriter(o, Constants.CHARSET);
		return w;
	    } else {
		// file exists
		if (importerConfig
			.getAdmExtracterStrategyOptionsForAdm(admLevel) == AdmExtracterStrategyOptions.backup) {
		    o = new BufferedOutputStream(new FileOutputStream(
			    createFileAndBackupIfAlreadyExists(file)));
		    w = new OutputStreamWriter(o, Constants.CHARSET);
		    return w;
		} else {
		    // skip
		    return null;
		}

	    }
	} catch (UnsupportedEncodingException e) {
	    logger.warn("UnsupportedEncodingException for " + Constants.CHARSET
		    + " : Can not extract Data");
	    return null;
	}

    }

    private void initFiles() {
	adm1file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm1FileName());
	adm2file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm2FileName());
	adm3file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm3FileName());
	adm4file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm4FileName());
	try {
	    adm1fileOutputStreamWriter = getWriter(adm1file, 1);
	    adm2fileOutputStreamWriter = getWriter(adm2file, 2);
	    adm3fileOutputStreamWriter = getWriter(adm3file, 3);
	    adm4fileOutputStreamWriter = getWriter(adm4file, 4);
	} catch (FileNotFoundException e) {
	    closeOutputStreams();
	    throw new RuntimeException(
		    "An error has occurred during creation of outpuStream : "
			    + e.getMessage(), e);
	}
    }

    /**
     * 
     */
    private File createFileAndBackupIfAlreadyExists(File file) {
	if (file == null) {
	    throw new ImporterException(
		    "Can not create or backup a null File ");
	}

	if (file.exists()) {
	    checkWriteRights(file);
	    // rename
	    logger.info("File " + file.getName()
		    + " already exists and will be renamed ");
	    file.renameTo(new File(importerConfig.getGeonamesDir()
		    + file.getName() + "-" + sdf.format(new Date()) + ".bkup"));
	}
	try {
	    // create
	    file = new File(importerConfig.getGeonamesDir() + file.getName());
	    file.createNewFile();
	    checkWriteRights(file);
	} catch (IOException e) {
	    throw new RuntimeException(
		    "An error has occurred during the creation of adm3file "
			    + importerConfig.getGeonamesDir() + file.getName(),
		    e);
	}
	return file;
    }

    /**
     * @param file
     */
    private void checkWriteRights(File file) {
	if (!file.canWrite()) {
	    throw new RuntimeException(
		    "you must have write rights in order to export adm in file "
			    + file.getAbsolutePath());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#setup()
     */
    @Override
    public void setup() {
	super.setup();
	initFiles();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreFirstLine()
     */
    @Override
    protected boolean shouldIgnoreFirstLine() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreComments()
     */
    @Override
    protected boolean shouldIgnoreComments() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#setCommitFlushMode()
     */
    @Override
    protected void setCommitFlushMode() {
	return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear()
     */
    @Override
    protected void flushAndClear() {
	if (adm1fileOutputStreamWriter != null) {
	    try {
		adm1fileOutputStreamWriter.flush();
	    } catch (IOException e) {
		closeOutputStreams();
		throw new RuntimeException("can not flush adm1file : "
			+ e.getMessage(), e);
	    }
	}
	if (adm2fileOutputStreamWriter != null) {
	    try {
		adm2fileOutputStreamWriter.flush();
	    } catch (IOException e) {
		closeOutputStreams();
		throw new RuntimeException("can not flush adm2file : "
			+ e.getMessage(), e);
	    }
	}
	if (adm3fileOutputStreamWriter != null) {
	    try {
		adm3fileOutputStreamWriter.flush();
	    } catch (IOException e) {
		closeOutputStreams();
		throw new RuntimeException("can not flush adm3file : "
			+ e.getMessage(), e);
	    }
	}
	if (adm4fileOutputStreamWriter != null) {
	    try {
		adm4fileOutputStreamWriter.flush();
	    } catch (IOException e) {
		closeOutputStreams();
		throw new RuntimeException("can not flush adm4 file : "
			+ e.getMessage(), e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
	return 19;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
	return ImporterHelper.listCountryFilesToImport(importerConfig
		.getGeonamesDir());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
	List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
	adm1file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm1FileName());
	deleteFile(adm1file, deletedObjectInfo);
	adm2file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm2FileName());
	deleteFile(adm2file, deletedObjectInfo);
	adm3file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm3FileName());
	deleteFile(adm3file, deletedObjectInfo);
	adm4file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAdm4FileName());
	deleteFile(adm4file, deletedObjectInfo);
	resetStatus();
	return deletedObjectInfo;
    }

    private void deleteFile(File file,
	    List<NameValueDTO<Integer>> deletedObjectInfo) {
	if (file.delete()) {
	    deletedObjectInfo.add(new NameValueDTO<Integer>(file.getName(), 1));
	    logger.info("File " + file.getName() + " has been deleted");
	} else {
	    deletedObjectInfo.add(new NameValueDTO<Integer>(file.getName(), 0));
	    logger.info("File " + file.getName() + " has not been deleted");
	}
    }

}
