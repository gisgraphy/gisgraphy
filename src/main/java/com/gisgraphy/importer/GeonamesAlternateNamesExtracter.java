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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.NameValueDTO;

/**
 * Extract the alternateNames into separate files : one for country, one for adm1 and one for adm2
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeonamesAlternateNamesExtracter extends AbstractSimpleImporterProcessor {
	
	protected static final Logger logger = LoggerFactory.getLogger(GeonamesAlternateNamesExtracter.class);

    protected File adm1file;

    protected File adm2file;

    protected File countryFile;
    
    protected File featuresFile;

    protected OutputStreamWriter adm1fileOutputStreamWriter;

    protected OutputStreamWriter adm2fileOutputStreamWriter;

    protected OutputStreamWriter countryfileOutputStreamWriter;
    
    protected OutputStreamWriter featuresfileOutputStreamWriter;

    @Autowired
    private IAdmDao admDao;
    
    @Autowired
    private ICountryDao countryDao;

    protected Map<Long, String> countryMap;
    
	protected Map<Long, String> adm1Map;

	protected Map<Long, String> adm2Map;


    /**
     * Default Constructor
     */
    public GeonamesAlternateNamesExtracter() {
	super();
    }
    
    @Override
    protected void onFileProcessed(File file){
    	//we overrride because we don't want to rename files
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
	 * ----------------------------------------- 0 : alternateNameId : 1 :
	 * geonameid : 2 : isolanguage : iso 639-2 or 3 or or 'post' 3 :
	 * alternate name 4 : isPreferredName 5 : isShortName
	 */

	if (!isEmptyField(fields, 1, false)) {
		Long featureId;
		try {
			featureId = new Long(fields[1]);
		} catch (NumberFormatException e) {
			logger.warn("geonamesid "+fields[1]+" is not a number for line "+line);
			return;
		}
	    if (lineIsAnAlternateNameForCountry(featureId)) {
		writeAlternateName(countryfileOutputStreamWriter,line);
	    } else if (lineIsAnAlternateNameForAdm1(featureId)) {
		writeAlternateName(adm1fileOutputStreamWriter,line);
	    } else if (lineIsAnAlternatNameForAdm2(featureId)) {
		writeAlternateName(adm2fileOutputStreamWriter,line);
	    }else {
		writeAlternateName(featuresfileOutputStreamWriter,line);
	    }
	} else {
	    logger.info("geonameid is null for geonames alternateNameId" + fields[0]);
	}
    }
    
  

    protected boolean lineIsAnAlternatNameForAdm2(Long featureId) {
		return adm2Map.get(featureId)!=null;
    }

    protected boolean lineIsAnAlternateNameForAdm1(Long featureId) {
    	return adm1Map.get(featureId)!=null;
    }

    protected boolean lineIsAnAlternateNameForCountry(Long featureId) {
    	return countryMap.get(featureId)!=null;
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped()
     */
    @Override
    public boolean shouldBeSkipped() {
    	 if (importerConfig.isImportGisFeatureEmbededAlternateNames() || !importerConfig.isGeonamesImporterEnabled()){
             return true ;
         }
         return false;
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
    }

   

    

    private void writeAlternateName(OutputStreamWriter outputStreamWriter, String line) {
	if (outputStreamWriter != null) {
		try {
		    outputStreamWriter.write(line);
		    outputStreamWriter.write("\r\n");
		    flushAndClear();
		} catch (IOException e) {
		    throw new RuntimeException(
			    "an error has occurred when writing in adm4 file",
			    e);
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
	if (countryfileOutputStreamWriter != null) {
	    try {
		countryfileOutputStreamWriter.close();
	    } catch (IOException e) {
		throw new RuntimeException("can not close country outputStream", e);
	    }
	}
	
	if (featuresfileOutputStreamWriter != null) {
	    try {
		featuresfileOutputStreamWriter.close();
	    } catch (IOException e) {
		throw new RuntimeException("can not close features outputStream", e);
	    }
	}
    }

   
    private OutputStreamWriter getWriter(File file)
	    throws FileNotFoundException {
	OutputStream o = null;
	OutputStreamWriter w = null;
	try {
	    if (file.exists()) {
		checkWriteRights(file);
		if (!file.delete()){
			 throw new RuntimeException("The file "+file.getAbsolutePath()+" exists but we can not delete it, to recreate it");    
		}
	    } 
		o = new BufferedOutputStream(new FileOutputStream(file));
		w = new OutputStreamWriter(o, Constants.CHARSET);
		return w;
	} catch (UnsupportedEncodingException e) {
	    logger.warn("UnsupportedEncodingException for " + Constants.CHARSET
		    + " : Can not extract Alternate names");
	    return null;
	}

    }

    protected void initFiles() {
	adm1file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameAdm1FileName());
	adm2file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameAdm2FileName());
	countryFile = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameCountryFileName());
	featuresFile = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameFeaturesFileName());
	try {
	    adm1fileOutputStreamWriter = getWriter(adm1file);
	    adm2fileOutputStreamWriter = getWriter(adm2file);
	    countryfileOutputStreamWriter = getWriter(countryFile);
	    featuresfileOutputStreamWriter = getWriter(featuresFile);
	} catch (FileNotFoundException e) {
	    closeOutputStreams();
	    throw new RuntimeException(
		    "An error has occurred during creation of outpuStream : "
			    + e.getMessage(), e);
	}
    }

    /**
     * 

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
	List<Long> countriesIDs = countryDao.listFeatureIds();
	List<Long> adm1IDs = admDao.listFeatureIdByLevel(1);
	List<Long> adm2IDs = admDao.listFeatureIdByLevel(2);
	adm1Map = populateMapFromList(adm1IDs);
	adm2Map = populateMapFromList(adm2IDs);
	countryMap = populateMapFromList(countriesIDs);
	initFiles();
    }
    
    protected Map<Long,String> populateMapFromList(List<Long> list){
    	Map<Long,String> map = new HashMap<Long,String>(list.size()+1); 
    	for(Long id: list){
    		map.put(id, "");
    	}
    	return map;
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
	if (countryfileOutputStreamWriter != null) {
	    try {
		countryfileOutputStreamWriter.flush();
	    } catch (IOException e) {
		closeOutputStreams();
		throw new RuntimeException("can not flush countryfile : "
			+ e.getMessage(), e);
	    }
	}
	if (featuresfileOutputStreamWriter != null) {
	    try {
		featuresfileOutputStreamWriter.flush();
	    } catch (IOException e) {
		closeOutputStreams();
		throw new RuntimeException("can not flush featuresfile : "
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
	return 6;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
	if (importerConfig.isImportGisFeatureEmbededAlternateNames()) {
	    logger
		    .info("ImportGisFeatureEmbededAlternateNames = true, we do not need to extract alternatenames from "
			    + importerConfig.getAlternateNamesFileName());
	    return new File[0];
	}
	File[] files = new File[1];
	files[0] = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNamesFileName());
	return files;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
	List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
	adm1file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameAdm1FileName());
	deleteFile(adm1file, deletedObjectInfo);
	adm2file = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameAdm2FileName());
	deleteFile(adm2file, deletedObjectInfo);
	countryFile = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameCountryFileName());
	deleteFile(countryFile, deletedObjectInfo);
	featuresFile = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameFeaturesFileName());
	deleteFile(featuresFile, deletedObjectInfo);
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

    @Required
    public void setAdmDao(IAdmDao admDao) {
        this.admDao = admDao;
    }


    @Required
    public void setCountryDao(ICountryDao countryDao) {
        this.countryDao = countryDao;
    }

}
