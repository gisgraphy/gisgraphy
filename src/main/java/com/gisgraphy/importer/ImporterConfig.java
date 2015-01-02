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

import static com.gisgraphy.importer.ImporterHelper.checkUrl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.helper.CommentedProperties;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *         Represents a configuration for importers For more informations for
 *         this options see the User Guide
 */
public class ImporterConfig {
	
	 public static final String PROPERTIES_CONFIG_FILE_CLASSPATH = "/env.properties";

	public static final String OPENSTREETMAP_FILL_ISIN_FIELD_NAME = "importerConfig.openstreetmap.fill.isin.field";

	/**
     * A list of options is separated by this. e.g : a list of regexp options
     */
    public static final String OPTION_SEPARATOR = ";";
    
    /**
     * A list of options is separated by this. e.g : a list of regexp options
     */
    public static final String REGEXP_SEPARATOR = "|";

    /**
     * The relative path of the directory that contains importer metadata
     */
    public static final String IMPORTER_METADATA_RELATIVE_PATH = "IMPORTER-METADATA-DO_NOT_REMOVE";

    /**
     * the name of the file that gives the information if the import is done or
     * not
     */
    public static final String ALREADY_DONE_FILE_NAME = "importNotAlreadyDone";

    /**
     * The default feature code if no one is specified
     */
    public final static String DEFAULT_FEATURE_CODE = "UNK";
    /**
     * The default feature class if no one is specified
     */
    public final static String DEFAULT_FEATURE_CLASS = "UNK";

    /**
     * The default regexp if no one is specified in the env.properties file
     */
    public final static String BASE_ACCEPT_REGEX = "ADM|COUNTRY|";

    /**
     * The regexp to use to import all the city
     */
    public final static String DEFAULT_ACCEPT_REGEX_CITY = BASE_ACCEPT_REGEX + "CITY$";

    /**
     * The regexp to use to import all the feature class / code
     */
    public final static String ACCEPT_ALL_REGEX_OPTION = ".*";

    /**
     * Default value for {@link #maxInsertsBeforeFlush}
     */
    public final static int DEFAULT_MAX_INSERT_BEFORE_FLUSH = 1000;

    /**
     * How many lines do we have to process before flushing
     * 
     * @see #DEFAULT_MAX_INSERT_BEFORE_FLUSH
     */
    private int maxInsertsBeforeFlush = DEFAULT_MAX_INSERT_BEFORE_FLUSH;

    public final static String OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD = "allcountries.tar.bz2";
    
    public final static String OPENSTREETMAP_HOUSENUMBER_DEFAULT_FILES_TO_DOWNLOAD = "allcountries.tar.bz2";
    
    public final static String OPENSTREETMAP_CITIES_DEFAULT_FILES_TO_DOWNLOAD = "allcountries.tar.bz2";
    
    public final static String OPENSTREETMAP_POI_DEFAULT_FILES_TO_DOWNLOAD = "allcountries.tar.bz2";
    
    public final static String QUATTROSHAPES_DEFAULT_FILES_TO_DOWNLOAD = "shapes.tar.bz2";

    public final static String GEONAMES_ALTERNATENAME_ZIP_FILE="alternateNames.zip";
    
    public final static String GEONAMES_COMPRESSED_FILE_EXTENSION=".zip";
    
    public final static String OPENSTREETAMP_COMPRESSED_FILE_EXTENSION=".tar.bz2";
    
    public final static String GEONAMES_DEFAULT_FILES_TO_DOWNLOAD = "allCountries.zip"+OPTION_SEPARATOR+GEONAMES_ALTERNATENAME_ZIP_FILE;
    
    

    /**
     * Default option if the Adm1 file has already been processed
     * 
     * @see AdmExtracterStrategyOptions
     */
    public AdmExtracterStrategyOptions DEFAULT_ADM1_EXTRACTER_STRATEGY_OPTION = AdmExtracterStrategyOptions.reprocess;
    /**
     * Default option if the adm2 file has already been processed
     * 
     * @see AdmExtracterStrategyOptions
     */
    public AdmExtracterStrategyOptions DEFAULT_ADM2_EXTRACTER_STRATEGY_OPTION = AdmExtracterStrategyOptions.reprocess;

    /**
     * Default option if the Adm3 file has already been processed
     * 
     * @see AdmExtracterStrategyOptions
     */
    public AdmExtracterStrategyOptions DEFAULT_ADM3_EXTRACTER_STRATEGY_OPTION = AdmExtracterStrategyOptions.reprocess;
    /**
     * Default option if the adm4 file has already been processed
     * 
     * @see AdmExtracterStrategyOptions
     */
    public AdmExtracterStrategyOptions DEFAULT_ADM4_EXTRACTER_STRATEGY_OPTION = AdmExtracterStrategyOptions.reprocess;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory.getLogger(ImporterConfig.class);

   

    private boolean wrongNumberOfFieldsThrows = false;

    private boolean missingRequiredFieldThrows = false;

    private boolean importGisFeatureEmbededAlternateNames = false;

    private String geonamesDir;

    private String openStreetMapDir;
    
    private String quattroshapesDir;
    
    private String openStreetMapHouseNumberDir;

    private String openStreetMapCitiesDir;
    
    private String openStreetMapPoisDir;
    
    private String geonamesZipCodeDir;

    private String openstreetMapDownloadURL;
    
    private String quattroshapesDownloadURL;
    
    private String openstreetMapHouseNumbersDownloadURL;
    
    private String openstreetMapCitiesDownloadURL;
    
    private String openstreetMapPoisDownloadURL;

    private String geonamesDownloadURL;

    private String geonamesZipCodeDownloadURL;

    private boolean retrieveFiles = false;

    private String geonamesFilesToDownload = "";

    private String openStreetMapFilesToDownload = "";
    
    private String quattroshapesFilesToDownload = "";
    
    private String openStreetMapHouseNumberFilesToDownload = "";
    
    private String openStreetMapCitiesFilesToDownload = "";
    
    private String openStreetMapPoisFilesToDownload = "";

    private boolean geonamesImporterEnabled = true;

    private boolean openstreetmapImporterEnabled = true;
    
    private boolean quattroshapesImporterEnabled = true;
    
    private boolean openstreetmapHouseNumberImporterEnabled = true;

    private boolean openStreetMapFillIsIn = true;
    
    private String adm1FileName;

    private String adm2FileName;

    private String adm3FileName;

    private String adm4FileName;

    private String languageFileName;

    private String countriesFileName;

    private String alternateNamesFileName;

    private String acceptRegExString = "*";

    private boolean tryToDetectAdmIfNotFound = true;

    private boolean syncAdmCodesWithLinkedAdmOnes = true;

    private AdmExtracterStrategyOptions adm1ExtracterStrategyIfAlreadyExists;

    private AdmExtracterStrategyOptions adm2ExtracterStrategyIfAlreadyExists;

    private AdmExtracterStrategyOptions adm3ExtracterStrategyIfAlreadyExists;

    private AdmExtracterStrategyOptions adm4ExtracterStrategyIfAlreadyExists;

    private String alternateNameFeaturesFileName;

    private String alternateNameAdm1FileName;

    private String alternateNameAdm2FileName;

    private String alternateNameCountryFileName;
    
    private boolean renameFilesAfterProcessing = false;
    
    
    /*
     *  
     __ _  ___  ___  _ __   __ _ _ __ ___   ___  ___ 
 	/ _` |/ _ \/ _ \| '_ \ / _` | '_ ` _ \ / _ \/ __|
   | (_| |  __/ (_) | | | | (_| | | | | | |  __/\__ \
 	\__, |\___|\___/|_| |_|\__,_|_| |_| |_|\___||___/
 	|___/ 
     * 
     */
    
    /**
     * @return true if the importer should process the import of Geonames data
     */
    public boolean isGeonamesImporterEnabled() {
    	return geonamesImporterEnabled;
    }
    
    /**
     * @param geonamesImporterEnabled
     *            enable or disable Geonames importer
     * @see ImporterConfig#isGeonamesImporterEnabled()
     */
    @Required
    public void setGeonamesImporterEnabled(boolean geonamesImporterEnabled) {
    	this.geonamesImporterEnabled = geonamesImporterEnabled;
    }

    /**
     * @return The option
     * @see #setGeonamesDownloadURL(String)
     */
    public String getGeonamesDownloadURL() {
    	return geonamesDownloadURL;
    }

    /**
     * The HTTP URL of the directory Where Geonames files are to be download
     * from
     * 
     * @param importerGeonamesDownloadURL
     *            The option
     */
    @Required
    public void setGeonamesDownloadURL(String importerGeonamesDownloadURL) {
	if (!importerGeonamesDownloadURL.endsWith("/")) {
	    this.geonamesDownloadURL = importerGeonamesDownloadURL + "/";
	} else {
	    this.geonamesDownloadURL = importerGeonamesDownloadURL;
	}
	logger.debug("set geonamesDownloadURL to " + this.geonamesDownloadURL);
    }
    

   
    /**
     * @return The option
     * @see #setGeonamesDir(String)
     */
    public String getGeonamesDir() {
    	return this.geonamesDir;
    }

    
    /**
     * The directory where the Geonames files will be retrieved and processed.
     * It must ends with / or \ according to the System
     * 
     * @param importerGeonamesDir
     *            the option
     */
    @Required
    public void setGeonamesDir(String importerGeonamesDir) {
	if (!importerGeonamesDir.endsWith(File.separator)) {
	    logger.debug(importerGeonamesDir + " does not end with " + File.separator);
	    this.geonamesDir = importerGeonamesDir + File.separator;
	} else {
	    this.geonamesDir = importerGeonamesDir;
	}
	logger.debug("set geonamesDir to " + this.geonamesDir);
    }

    /**
     * @return true if the directory with the file to import exists and is
     *         accessible
     */
    public boolean isGeonamesDownloadDirectoryAccessible() {
    	return isDirectoryAccessible(getGeonamesDir());
    }
   
    /**
     * @return The option
     * @see #setGeonamesFilesToDownload(String)
     */
    public String getGeonamesFilesToDownload() {
    	return this.geonamesFilesToDownload;
    }
    
    /**
     * The list of the Geonames files to be download from the
     * {@link #geonamesDownloadURL}. the several files will be separated by
     * {@link #OPTION_SEPARATOR}, if not set or null, defaulting to {@link #GEONAMES_DEFAULT_FILES_TO_DOWNLOAD}
     * 
     * @param geonamesFilesToDownload
     *            the filesToDownload to set
     */
    @Required
    public void setGeonamesFilesToDownload(String geonamesFilesToDownload) {
	if (geonamesFilesToDownload == null || geonamesFilesToDownload.trim().equals("")) {
	    logger.warn("the option geonamesFilesToDownload is not set and will be set to his default value : " + GEONAMES_DEFAULT_FILES_TO_DOWNLOAD);
	    this.geonamesFilesToDownload = GEONAMES_DEFAULT_FILES_TO_DOWNLOAD;
	} else {
	    this.geonamesFilesToDownload = geonamesFilesToDownload;
	    logger.info("geonamesFilesToDownload=" + geonamesFilesToDownload);
	}
    }
    
   
    
    /**
     * @return A list of string with the files to be download, processed from
     *         {@link #geonamesFilesToDownload}
     */
    public List<String> getGeonamesDownloadFilesListFromOption() {
    	return splitSemiColmunStringToList(geonamesFilesToDownload);
    }
    
    /*
     *       _       
 		 ___(_)_ __  
		|_  / | '_ \ 
 		 / /| | |_) |
		/___|_| .__/ 
      		|_|   
     */
    
    /**
     * @return The option
     * @see #setGeonamesZipCodeDownloadURL(String)
     */
    public String getGeonamesZipCodeDownloadURL() {
    	return geonamesZipCodeDownloadURL;
    }

    /**
     * The HTTP URL of the directory Where Geonames zip files are to be download
     * from
     * 
     * @param geonamesZipCodeDownloadURL
     *            The option
     */
    @Required
    public void setGeonamesZipCodeDownloadURL(String geonamesZipCodeDownloadURL) {
	if (!geonamesZipCodeDownloadURL.endsWith("/")) {
	    this.geonamesZipCodeDownloadURL = geonamesZipCodeDownloadURL + "/";
	} else {
	    this.geonamesZipCodeDownloadURL = geonamesZipCodeDownloadURL;
	}
	logger.debug("set geonamesZipCodeDownloadURL to " + this.geonamesZipCodeDownloadURL);
    }

    
    
    /**
     * @return the zipcode directory where the zipcode data are
     */
    public String getGeonamesZipCodeDir() {
    	return geonamesZipCodeDir;
    }
    
    /**
     * The directory where the zip code files will be retrieved and
     * processed. It must ends with / or \ according to the System
     * 
     * @param geonamesZipCodeDir
     *            the option
     */
    @Required
    public void setGeonamesZipCodeDir(String geonamesZipCodeDir) {
	if (!geonamesZipCodeDir.endsWith(File.separator)) {
	    logger.debug(geonamesZipCodeDir + " does not end with " + File.separator);
	    this.geonamesZipCodeDir = geonamesZipCodeDir + File.separator;
	} else {
	    this.geonamesZipCodeDir = geonamesZipCodeDir;
	}
	logger.debug("set geonamesZipCodeDir to " + this.geonamesZipCodeDir);
    }

   

    
    
    
    /*
	                           _                 _       
	  ___  ___ _ __ ___    ___| |_ _ __ ___  ___| |_ ___ 
	 / _ \/ __| '_ ` _ \  / __| __| '__/ _ \/ _ \ __/ __|
	| (_) \__ \ | | | | | \__ \ |_| | |  __/  __/ |_\__ \
	 \___/|___/_| |_| |_| |___/\__|_|  \___|\___|\__|___/
	                                                     
     */
    
    /**
     * @param openstreetmapImporterEnabled
     *            enable or disable Openstreetmap importer
     * @see ImporterConfig#isOpenstreetmapImporterEnabled()
     */
    @Required
    public void setOpenstreetmapImporterEnabled(boolean openstreetmapImporterEnabled) {
    	this.openstreetmapImporterEnabled = openstreetmapImporterEnabled;
    	if (!openstreetmapImporterEnabled){
    		this.openstreetmapHouseNumberImporterEnabled = false;
    	}
    }
    
    /**
     * @return true if the importer should process the import of Openstreetmap
     *         data
     * @see ImporterConfig#isGeonamesImporterEnabled()
     */
    public boolean isOpenstreetmapImporterEnabled() {
    	return openstreetmapImporterEnabled;
    }
    
  
    
    /**
     * @return The option
     * @see #setOpenstreetMapDownloadURL(String)
     */
    public String getOpenstreetMapDownloadURL() {
    	return openstreetMapDownloadURL;
    }
    
    /**
     * The HTTP URL of the directory Where openstreetmap streets files are to be
     * download from
     * 
     * @param openstreetMapDownloadURL
     *            The option
     */
    @Required
    public void setOpenstreetMapDownloadURL(String openstreetMapDownloadURL) {
	if (!openstreetMapDownloadURL.endsWith("/")) {
	    this.openstreetMapDownloadURL = openstreetMapDownloadURL + "/";
	} else {
	    this.openstreetMapDownloadURL = openstreetMapDownloadURL;
	}
	logger.debug("set openstreetMapDownloadURL to " + this.openstreetMapDownloadURL);
    }
   
    /**
     * @return The option
     * @see #setOpenStreetMapDir(String)
     */
    public String getOpenStreetMapDir() {
    	return this.openStreetMapDir;
    }
    
    /**
     * The directory where the openStreetMap files will be retrieved and
     * processed. It must ends with / or \ according to the System
     * 
     * @param importerOpenStreetMapDir
     *            the option
     */
    @Required
    public void setOpenStreetMapDir(String importerOpenStreetMapDir) {
	if (!importerOpenStreetMapDir.endsWith(File.separator)) {
	    logger.debug(openStreetMapDir + " does not end with " + File.separator);
	    this.openStreetMapDir = importerOpenStreetMapDir + File.separator;
	} else {
	    this.openStreetMapDir = importerOpenStreetMapDir;
	}
	logger.debug("set openStreetMapDir to " + this.openStreetMapDir);
    }
    
    /**
     * @return true if the directory with the file to import exists and is
     *         accessible
     */
    public boolean isOpenStreetMapDownloadDirectoryAccessible() {
    	return isDirectoryAccessible(getOpenStreetMapDir());
    }
    
    /**
     * @return The option
     * @see #setOpenStreetMapFilesToDownload(String)
     */
    public String getOpenStreetMapFilesToDownload() {
    	return this.openStreetMapFilesToDownload;
    }
    
    /**
     * The list of the Openstreetmap files to be download from the
     * {@link #openstreetMapDownloadURL}. the several files will be separated by
     * {@link #OPTION_SEPARATOR}. if null or empty, will be set to {
     * {@link #OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD}
     * 
     * @param openStreetMapFilesToDownload
     *            The openstreetmap files to download to set
     */
    @Required
    public void setOpenStreetMapFilesToDownload(String openStreetMapFilesToDownload) {
	if (openStreetMapFilesToDownload == null || openStreetMapFilesToDownload.trim().equals("")) {
	    logger.warn("the option openStreetMapFilesToDownload is not set and will be set to his default value : " + OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
	    this.openStreetMapFilesToDownload = OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD;
	} else {
	    this.openStreetMapFilesToDownload = openStreetMapFilesToDownload;
	    logger.info("openStreetMapFilesToDownload=" + this.openStreetMapFilesToDownload);
	}
    }
    
   
    /**
     * @return A list of string with the files to be download, processed from
     *         {@link #openStreetMapFilesToDownload}
     */
    public List<String> getOpenStreetMapDownloadFilesListFromOption() {
    	return splitSemiColmunStringToList(openStreetMapFilesToDownload);
    }
    
    
   /*
    												_                   
		 ___  ___ _ __ ___    _ __  _   _ _ __ ___ | |__   ___ _ __ ___ 
		/ _ \/ __| '_ ` _ \  | '_ \| | | | '_ ` _ \| '_ \ / _ \ '__/ __|
	   | (_) \__ \ | | | | | | | | | |_| | | | | | | |_) |  __/ |  \__ \
		\___/|___/_| |_| |_| |_| |_|\__,_|_| |_| |_|_.__/ \___|_|  |___/
                         
    */
    
    /**
     * @return true if the importer should process the import of Openstreetmap house numbers
     *         data
     * @see ImporterConfig#isGeonamesImporterEnabled()
     */
    public boolean isOpenstreetmapHouseNumberImporterEnabled() {
    	return  openstreetmapHouseNumberImporterEnabled;
    }
    
    /**
     * @param openstreetmapHouseNumberImporterEnabled
     *            enable or disable Openstreetmap house numbers importer
     * @see ImporterConfig#isOpenstreetmapImporterEnabled()
     */
    @Required
    public void setOpenstreetmapHouseNumberImporterEnabled(boolean openstreetmapHouseNumberImporterEnabled) {
    	this.openstreetmapHouseNumberImporterEnabled = openstreetmapHouseNumberImporterEnabled;
    	if (!openstreetmapImporterEnabled){
    		this.openstreetmapHouseNumberImporterEnabled = false;
    	}
    }
    
    /**
     * @return The option
     * @see #setOpenstreetMaphouseNumbersDownloadURL(String)
     */
    public String getOpenstreetMaphouseNumbersDownloadURL() {
    	return openstreetMapHouseNumbersDownloadURL;
    }
    
    /**
     * The HTTP URL of the directory Where openstreetmap house numbers files are to be
     * download from
     * 
     * @param openstreetMapHouseNumberDownloadURL
     *            The option
     */
    @Required
    public void setOpenstreetMaphouseNumbersDownloadURL(String openstreetMapHouseNumberDownloadURL) {
	if (!openstreetMapHouseNumberDownloadURL.endsWith("/")) {
	    this.openstreetMapHouseNumbersDownloadURL = openstreetMapHouseNumberDownloadURL + "/";
	} else {
	    this.openstreetMapHouseNumbersDownloadURL = openstreetMapHouseNumberDownloadURL;
	}
	logger.debug("set openstreetMaphouseNumberDownloadURL to " + this.openstreetMapHouseNumbersDownloadURL);
    }
    
   
    /**
     * @return The option
     * @see #setOpenStreetMapHouseNumberDir(String)
     */
    public String getOpenStreetMapHouseNumberDir() {
    	return this.openStreetMapHouseNumberDir;
    } 
    
    /**
     * The directory where the openStreetMap files for house numbers will be retrieved and
     * processed. It must ends with / or \ according to the System
     * 
     * @param importerOpenStreetMapHouseNumberDir
     *            the option
     */
    @Required
    public void setOpenStreetMapHouseNumberDir(String importerOpenStreetMapHouseNumberDir) {
	if (!importerOpenStreetMapHouseNumberDir.endsWith(File.separator)) {
	    logger.debug(openStreetMapHouseNumberDir + " does not end with " + File.separator);
	    this.openStreetMapHouseNumberDir = importerOpenStreetMapHouseNumberDir + File.separator;
	} else {
	    this.openStreetMapHouseNumberDir = importerOpenStreetMapHouseNumberDir;
	}
	logger.debug("set openStreetMapHouseNumberDir to " + this.openStreetMapHouseNumberDir);
    }
    
    
    /**
     * @return true if the directory with the file to import exists and is
     *         accessible
     */
    public boolean isOpenStreetMapHouseNumberDownloadDirectoryAccessible() {
    	return isDirectoryAccessible(getOpenStreetMapHouseNumberDir());
    }
    
    /**
     * @return The option
     * @see #setOpenStreetMapHouseNumberFilesToDownload(String)
     */
    public String getOpenStreetMapHouseNumberFilesToDownload() {
    	return this.openStreetMapHouseNumberFilesToDownload;
    }
    
    /**
     * The list of the Openstreetmap house number files to be download from the
     * {@link #openstreetMapHouseNumbersDownloadURL}. the several files will be separated by
     * {@link #OPTION_SEPARATOR}. if null or empty, will be set to {
     * {@link #OPENSTREETMAP_HOUSENUMBER_DEFAULT_FILES_TO_DOWNLOAD}
     * 
     * @param openStreetMapHouseNumberFilesToDownload
     *            The openstreetmap files to download to set
     */
    @Required
    public void setOpenStreetMapHouseNumberFilesToDownload(String openStreetMapHouseNumberFilesToDownload) {
	if (openStreetMapHouseNumberFilesToDownload == null || openStreetMapHouseNumberFilesToDownload.trim().equals("")) {
	    logger.warn("the option openStreetMapHouseNumberFilesToDownload is not set and will be set to his default value : " + OPENSTREETMAP_HOUSENUMBER_DEFAULT_FILES_TO_DOWNLOAD);
	    this.openStreetMapHouseNumberFilesToDownload = OPENSTREETMAP_HOUSENUMBER_DEFAULT_FILES_TO_DOWNLOAD;
	} else {
	    this.openStreetMapHouseNumberFilesToDownload = openStreetMapHouseNumberFilesToDownload;
	    logger.info("openStreetMapHouseNumberFilesToDownload=" + this.openStreetMapHouseNumberFilesToDownload);
	}
    }

    /**
     * @return A list of string with the files to be download, processed from
     *         {@link #openStreetMapHouseNumberFilesToDownload}
     */
    public List<String> getOpenStreetMapHouseNumberDownloadFilesListFromOption() {
    	return splitSemiColmunStringToList(openStreetMapHouseNumberFilesToDownload);
    }
    
    
   /*
    *                          _ _   _           
  	 ___  ___ _ __ ___     ___(_) |_(_) ___  ___ 
 	/ _ \/ __| '_ ` _ \   / __| | __| |/ _ \/ __|
   | (_) \__ \ | | | | | | (__| | |_| |  __/\__ \
 	\___/|___/_| |_| |_|  \___|_|\__|_|\___||___/
    */
    
    
    /**
     * @return The option
     * @see #setOpenstreetMapCitiesDownloadURL(String)
     */
    public String getOpenstreetMapCitiesDownloadURL() {
    	return openstreetMapCitiesDownloadURL;
    }
    
    /**
     * The HTTP URL of the directory Where openstreetmap cities files are to be
     * download from
     * 
     * @param openstreetMapCitiesDownloadURL
     *            The option
     */
    @Required
    public void setOpenstreetMapCitiesDownloadURL(String openstreetMapCitiesDownloadURL) {
	if (!openstreetMapCitiesDownloadURL.endsWith("/")) {
	    this.openstreetMapCitiesDownloadURL = openstreetMapCitiesDownloadURL + "/";
	} else {
	    this.openstreetMapCitiesDownloadURL = openstreetMapCitiesDownloadURL;
	}
	logger.debug("set openstreetMapCitiesDownloadURL to " + this.openstreetMapCitiesDownloadURL);
    }
    

    /**
     * @return The option
     * @see #setOpenStreetMapCitiesDir(String)
     */
    public String getOpenStreetMapCitiesDir() {
    	return this.openStreetMapCitiesDir;
    }
    

    /**
     * The directory where the openStreetMap cities files will be retrieved and
     * processed. It must ends with / or \ according to the System
     * 
     * @param openStreetMapCitiesDir
     *            the option
     */
    @Required
    public void setOpenStreetMapCitiesDir(String openStreetMapCitiesDir) {
	if (!openStreetMapCitiesDir.endsWith(File.separator)) {
	    logger.debug(openStreetMapCitiesDir + " does not end with " + File.separator);
	    this.openStreetMapCitiesDir = openStreetMapCitiesDir + File.separator;
	} else {
	    this.openStreetMapCitiesDir = openStreetMapCitiesDir;
	}
	logger.debug("set openStreetMapCitiesDir to " + this.openStreetMapCitiesDir);
    }
    
    
    
    /**
     * @return true if the directory with the osm cities file to import exists and is
     *         accessible
     */
    public boolean isOpenStreetMapCitiesDirectoryAccessible() {
    	return isDirectoryAccessible(getOpenStreetMapCitiesDir());
    }
    
    /**
     * @return The option
     * @see #setOpenStreetMapCitiesFilesToDownload(String)
     */
    public String getOpenStreetMapCitiesFilesToDownload() {
    	return this.openStreetMapCitiesFilesToDownload;
    }
    
    
   
    
    
    /**
     * The list of the cities Openstreetmap to be download from the
     * {@link #openstreetMapCitiesDownloadURL}. the several files will be separated by
     * {@link #OPTION_SEPARATOR}. if null or empty, will be set to {
     * {@link #OPENSTREETMAP_CITIES_DEFAULT_FILES_TO_DOWNLOAD}
     * 
     * @param openStreetMapCitiesFilesToDownload
     *            The openstreetmap filesToDownload to set
     */
    @Required
    public void setOpenStreetMapCitiesFilesToDownload(String openStreetMapCitiesFilesToDownload) {
	if (openStreetMapCitiesFilesToDownload == null || openStreetMapCitiesFilesToDownload.trim().equals("")) {
	    logger.warn("the option openStreetMapCitiesFilesToDownload is not set and will be set to his default value : " + OPENSTREETMAP_CITIES_DEFAULT_FILES_TO_DOWNLOAD);
	    this.openStreetMapCitiesFilesToDownload = OPENSTREETMAP_CITIES_DEFAULT_FILES_TO_DOWNLOAD;
	} else {
	    this.openStreetMapCitiesFilesToDownload = openStreetMapCitiesFilesToDownload;
	    logger.info("openStreetMapCitiesFilesToDownload=" + openStreetMapCitiesFilesToDownload);
	}
    }
    
    /**
     * @return A list of string with the files to be download, processed from
     *         {@link #openStreetMapCitiesFilesToDownload}
     */
    public List<String> getOpenStreetMapCitiesDownloadFilesListFromOption() {
    	return splitSemiColmunStringToList(openStreetMapCitiesFilesToDownload);
    }
    
    /*
  	 ___  ___ _ __ ___    _ __   ___ (_)
 	/ _ \/ __| '_ ` _ \  | '_ \ / _ \| |
   | (_) \__ \ | | | | | | |_) | (_) | |
 	\___/|___/_| |_| |_| | .__/ \___/|_|
                      	 |_|            

     */

   
    /**
     * @return The option
     * @see #setOpenstreetMapPoisDownloadURL(String)
     */
    public String getOpenstreetMapPoisDownloadURL() {
    	return openstreetMapPoisDownloadURL;
    }

   
    /**
     * The HTTP URL of the directory Where openstreetmap POI files are to be
     * download from
     * 
     * @param openstreetMapPoisDownloadURL
     *            The option
     */
    @Required
    public void setOpenstreetMapPoisDownloadURL(String openstreetMapPoisDownloadURL) {
	if (!openstreetMapPoisDownloadURL.endsWith("/")) {
	    this.openstreetMapPoisDownloadURL = openstreetMapPoisDownloadURL + "/";
	} else {
	    this.openstreetMapPoisDownloadURL = openstreetMapPoisDownloadURL;
	}
	logger.debug("set openstreetMapPoisDownloadURL to " + this.openstreetMapPoisDownloadURL);
    }

    
    /**
     * @return The option
     * @see #setOpenStreetMapPoisDir(String)
     */
    public String getOpenStreetMapPoisDir() {
    	return this.openStreetMapPoisDir;
    }

    
    /**
     * The directory where the openStreetMap POI files will be retrieved and
     * processed. It must ends with / or \ according to the System
     * 
     * @param openStreetMapPoisDir
     *            the option
     */
    @Required
    public void setOpenStreetMapPoisDir(String openStreetMapPoisDir) {
	if (!openStreetMapPoisDir.endsWith(File.separator)) {
	    logger.debug(openStreetMapPoisDir + " does not end with " + File.separator);
	    this.openStreetMapPoisDir = openStreetMapPoisDir + File.separator;
	} else {
	    this.openStreetMapPoisDir = openStreetMapPoisDir;
	}
	logger.debug("set openStreetMapPoisDir to " + this.openStreetMapPoisDir);
    }
    
    /**
     * @return true if the directory with the osm Poi file to import exists and is
     *         accessible
     */
    public boolean isOpenStreetMapPoisDirectoryAccessible() {
    	return isDirectoryAccessible(getOpenStreetMapPoisDir());
    }

    /**
     * @return The option
     * @see #setOpenStreetMapPoisFilesToDownload(String)
     */
    public String getOpenStreetMapPoisFilesToDownload() {
    	return this.openStreetMapPoisFilesToDownload;
    }
    
    /**
     * The list of the Openstreetmap POI to be download from the
     * {@link #openstreetMapPoisDownloadURL}. the several files will be separated by
     * {@link #OPTION_SEPARATOR}. if null or empty, will be set to {
     * {@link #OPENSTREETMAP_POI_DEFAULT_FILES_TO_DOWNLOAD}
     * 
     * @param openStreetMapPoisFilesToDownload
     *            The openstreetmap filesToDownload to set
     */
    @Required
    public void setOpenStreetMapPoisFilesToDownload(String openStreetMapPoisFilesToDownload) {
	if (openStreetMapPoisFilesToDownload == null || openStreetMapPoisFilesToDownload.trim().equals("")) {
	    logger.warn("the option openStreetMapPoisFilesToDownload is not set and will be set to his default value : " + OPENSTREETMAP_POI_DEFAULT_FILES_TO_DOWNLOAD);
	    this.openStreetMapPoisFilesToDownload = OPENSTREETMAP_POI_DEFAULT_FILES_TO_DOWNLOAD;
	} else {
	    this.openStreetMapPoisFilesToDownload = openStreetMapPoisFilesToDownload;
	    logger.info("openStreetMapPoisFilesToDownload=" + openStreetMapPoisFilesToDownload);
	}
    }
    
    
    /**
     * @return A list of string with the files to be download, processed from
     *         {@link #openStreetMapCitiesFilesToDownload}
     */
    public List<String> getOpenStreetMapPoisDownloadFilesListFromOption() {
    	return splitSemiColmunStringToList(openStreetMapPoisFilesToDownload);
    }
    
    /*
                   _   _                 _                           
  __ _ _   _  __ _| |_| |_ _ __ ___  ___| |__   __ _ _ __   ___  ___ 
 / _` | | | |/ _` | __| __| '__/ _ \/ __| '_ \ / _` | '_ \ / _ \/ __|
| (_| | |_| | (_| | |_| |_| | | (_) \__ \ | | | (_| | |_) |  __/\__ \
 \__, |\__,_|\__,_|\__|\__|_|  \___/|___/_| |_|\__,_| .__/ \___||___/
    |_|                                             |_|              

     */
    
    /**
     * @param 
     *            enable or disable quattroshapes importer
     * @see ImporterConfig#isQuattroshapesImporterEnabled()
     */
    @Required
    public void setQuattroshapesImporterEnabled(boolean quattroshapesImporterEnabled) {
    	this.quattroshapesImporterEnabled = quattroshapesImporterEnabled;
    }
    
    /**
     * @return true if the importer should process the import of quattroshapes
     *         data
     * @see ImporterConfig#isQuattroshapesImporterEnabled()
     */
    public boolean isQuattroshapesImporterEnabled() {
    	return quattroshapesImporterEnabled;
    }
    
    /**
     * @return The option
     * @see #setQuattroshapesDownloadURL(String)
     */
    public String getQuattroshapesDownloadURL() {
    	return quattroshapesDownloadURL;
    }

   
    /**
     * The HTTP URL of the directory Where quattroshapes files are to be
     * download from
     * 
     * @param quattroshapesDownloadURL
     *            The option
     */
    @Required
    public void setQuattroshapesDownloadURL(String quattroshapesDownloadURL) {
	if (!quattroshapesDownloadURL.endsWith("/")) {
	    this.quattroshapesDownloadURL = quattroshapesDownloadURL + "/";
	} else {
	    this.quattroshapesDownloadURL = quattroshapesDownloadURL;
	}
	logger.debug("set quattroshapesDownloadURL to " + this.quattroshapesDownloadURL);
    }

    
    /**
     * @return The option
     * @see #setQuattroshapesDir(String)
     */
    public String getQuattroshapesDir() {
    	return this.quattroshapesDir;
    }

    
    /**
     * The directory where the quattroshapes files will be retrieved and
     * processed. It must ends with / or \ according to the System
     * 
     * @param quattroshapesDir
     *            the option
     */
    @Required
    public void setQuattroshapesDir(String quattroshapesDir) {
	if (!quattroshapesDir.endsWith(File.separator)) {
	    logger.debug(quattroshapesDir + " does not end with " + File.separator);
	    this.quattroshapesDir = quattroshapesDir + File.separator;
	} else {
	    this.quattroshapesDir = quattroshapesDir;
	}
	logger.debug("set quattroshapesDir to " + this.quattroshapesDir);
    }
    
    /**
     * @return true if the directory with the quattroshapes file to import exists and is
     *         accessible
     */
    public boolean isQuattroshapesDirectoryAccessible() {
    	return isDirectoryAccessible(getQuattroshapesDir());
    }

    /**
     * @return The option
     * @see #setQuattroshapesFilesToDownload(String)
     */
    public String getQuattroshapesFilesToDownload() {
    	return this.quattroshapesFilesToDownload;
    }
    
    /**
     * The list of the Openstreetmap POI to be download from the
     * {@link #openstreetMapPoisDownloadURL}. the several files will be separated by
     * {@link #OPTION_SEPARATOR}. if null or empty, will be set to {
     * {@link #OPENSTREETMAP_POI_DEFAULT_FILES_TO_DOWNLOAD}
     * 
     * @param openStreetMapPoisFilesToDownload
     *            The openstreetmap filesToDownload to set
     */
    @Required
    public void setQuattroshapesFilesToDownload(String quattroshapesFilesToDownload) {
	if (quattroshapesFilesToDownload == null || quattroshapesFilesToDownload.trim().equals("")) {
	    logger.warn("the option quattroshapesFilesToDownload is not set and will be set to his default value : " + QUATTROSHAPES_DEFAULT_FILES_TO_DOWNLOAD);
	    this.quattroshapesFilesToDownload = QUATTROSHAPES_DEFAULT_FILES_TO_DOWNLOAD;
	} else {
	    this.quattroshapesFilesToDownload = quattroshapesFilesToDownload;
	    logger.info("quattroshapesFilesToDownload=" + quattroshapesFilesToDownload);
	}
    }
    
    
    /**
     * @return A list of string with the files to be download, processed from
     *         {@link #openStreetMapCitiesFilesToDownload}
     */
    public List<String> getQuattroshapesFilesDownloadFilesListFromOption() {
    	return splitSemiColmunStringToList(quattroshapesFilesToDownload);
    }
    
    
    //_____________________________________________________end importer specific config______________________________
    
    private List<String> splitSemiColmunStringToList(String stringToSplit) {
	List<String> list = new ArrayList<String>();
	if (stringToSplit != null && stringToSplit.length() != 0) {
	    String[] splited = stringToSplit.split(OPTION_SEPARATOR);
	    for (int i = 0; i < splited.length; i++) {
		list.add(splited[i]);
	    }
	}
	return list;
    }


    /**
     * What should we do if the Adm file for the specified level has already
     * been processed It is a wrapper method around
     * {@link #DEFAULT_ADM3_EXTRACTER_STRATEGY_OPTION} and
     * {@link #DEFAULT_ADM4_EXTRACTER_STRATEGY_OPTION}
     */
    public AdmExtracterStrategyOptions getAdmExtracterStrategyOptionsForAdm(int admLevel) {
	if (admLevel == 1) {
	    return adm1ExtracterStrategyIfAlreadyExists;
	} else if (admLevel == 2) {
	    return adm2ExtracterStrategyIfAlreadyExists;
	} else if (admLevel == 3) {
	    return adm3ExtracterStrategyIfAlreadyExists;
	} else if (admLevel == 4) {
	    return adm4ExtracterStrategyIfAlreadyExists;
	} else {
	    throw new RuntimeException(" can not get AdmExtracterStrategyOptions For Adm with level " + admLevel);
	}
    }

    /**
     * @return The option
     * @see #setAcceptRegExString(String)
     */
    public String getAcceptRegExString() {
    	return this.acceptRegExString;
    }

    /**
     * @return The option
     * @see #setSyncAdmCodesWithLinkedAdmOnes(boolean)
     */
    public boolean isSyncAdmCodesWithLinkedAdmOnes() {
    	return this.syncAdmCodesWithLinkedAdmOnes;
    }

    /**
     * @return The option
     * @see #setTryToDetectAdmIfNotFound(boolean)
     */
    public boolean isTryToDetectAdmIfNotFound() {
    	return this.tryToDetectAdmIfNotFound;
    }

    /**
     * List of regular expressions for placetype (class name without .class to be
     * import.<br>
     * <br>
     * 
     * "ADM" and "country" are automaticaly
     * imported (Administrative division and country).<br>
     * Examples :
     * <ul>
     * <li>.* : import all gisfeatures, no matter their feature class and
     * feature code</li>
     * <li> {@link #DEFAULT_ACCEPT_REGEX_CITY} : import all adm city and countries</li>
     * <li>ATM : import all ATM</li>
     * <li>ATM|RESTAURANT : import all ATM and restaurant</li>
     * </ul>
     * 
     * @param acceptRegExString
     *            the option
     */
    @Required
    public void setAcceptRegExString(String acceptRegExString) {
	if (acceptRegExString == null || acceptRegExString.trim().equals("") || acceptRegExString.equals(ACCEPT_ALL_REGEX_OPTION)) {
	    logger.warn("the option acceptRegExString is not set and will be set to his default value : " + ACCEPT_ALL_REGEX_OPTION);
	    this.acceptRegExString = ACCEPT_ALL_REGEX_OPTION;
	    return;
	}
	this.acceptRegExString = BASE_ACCEPT_REGEX + acceptRegExString;
		logger.info("acceptRegExString=" + this.acceptRegExString);
    }

    /**
     * The linked Adm may not be the same as the one which would be found with
     * the ADMcodes from the csv file if TryToDetectAdmIfNotFound is set to
     * true. in this case error corecting is done. tis option determine if the
     * ADMXcode must be equals to the linked ADM or if they must be equals to
     * the value in the CSVFile note that the admXnames are always sync with the
     * Linked Adm if true : the AdmXcodes of the imported GisFeature will be the
     * gisFeature.getAdm.getAdmXcode.<br>
     * <br>
     * if false : the AdmXcode for a GisFeature will be the values of the CSV
     * dump file. That means : If the option tryToDetectAdmIfNotFound is set to
     * true : the Adm will be suggest if the AdmXcodes values of the CSV dump
     * file doesn't correspond to an already known Adm. In that case the
     * suggested Adm will have AdmXcodes different from the CSV dump file ones.
     * This option allow you to set The AdmXcodes for the gisFeature with the
     * detected Adm value instead of the CSV file ones.<br/>
     * In other words : AdmXcodes of the linked Adm and AdmXcodes of the
     * gisFeature will always be the same if this option is true. it is
     * recommended to let it to true
     * 
     * @param setAdmCodesWithLinkedAdmObject
     *            The option to set
     */
    @Required
    public void setSyncAdmCodesWithLinkedAdmOnes(boolean setAdmCodesWithLinkedAdmObject) {
    	this.syncAdmCodesWithLinkedAdmOnes = setAdmCodesWithLinkedAdmObject;
    	logger.info("setAdmCodesWithLinkedAdmObject=" + setAdmCodesWithLinkedAdmObject);
    }

    /**
     * If this option is set to true : The importer will try to detect Adm for
     * features if the AdmXcodes values does not correspond to a known Adm. it
     * is a process of error correction if set to false error correction is
     * disabled
     * 
     * @param tryToDetectAdmIfNotFound
     *            The option
     */
    @Required
    public void setTryToDetectAdmIfNotFound(boolean tryToDetectAdmIfNotFound) {
    	this.tryToDetectAdmIfNotFound = tryToDetectAdmIfNotFound;
    	logger.info("tryToDetectAdmIfNotFound=" + tryToDetectAdmIfNotFound);
    }

    /**
     * @return The option
     * @see #setAdm1ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions)
     */
    public AdmExtracterStrategyOptions getAdm1ExtracterStrategyIfAlreadyExists() {
    	return this.adm1ExtracterStrategyIfAlreadyExists;
    }

    /**
     * What should we do if the Adm1 file has already been processed
     * 
     * @see #DEFAULT_ADM1_EXTRACTER_STRATEGY_OPTION
     * @param adm1ExtracterStrategy
     *            The option
     */
    @Required
    public void setAdm1ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions adm1ExtracterStrategy) {
    	this.adm1ExtracterStrategyIfAlreadyExists = adm1ExtracterStrategy;
    }

    /**
     * @return The option
     * @see #setAdm2ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions)
     */
    public AdmExtracterStrategyOptions getAdm2ExtracterStrategyIfAlreadyExists() {
    	return this.adm2ExtracterStrategyIfAlreadyExists;
    }

    /**
     * What should we do if the Adm2 file has already been processed
     * 
     * @see #DEFAULT_ADM2_EXTRACTER_STRATEGY_OPTION
     * @param adm2ExtracterStrategy
     *            The option
     */
    @Required
    public void setAdm2ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions adm2ExtracterStrategy) {
    	this.adm2ExtracterStrategyIfAlreadyExists = adm2ExtracterStrategy;
    }

    /**
     * @return The option
     * @see #setAdm3ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions)
     */
    public AdmExtracterStrategyOptions getAdm3ExtracterStrategyIfAlreadyExists() {
    	return this.adm3ExtracterStrategyIfAlreadyExists;
    }

    /**
     * What should we do if the Adm3 file has already been processed
     * 
     * @see #DEFAULT_ADM3_EXTRACTER_STRATEGY_OPTION
     * @param adm3ExtracterStrategy
     *            The option
     */
    @Required
    public void setAdm3ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions adm3ExtracterStrategy) {
    	this.adm3ExtracterStrategyIfAlreadyExists = adm3ExtracterStrategy;
    }

    /**
     * @return the option
     * @see ImporterConfig#setAdm4ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions)
     */
    public AdmExtracterStrategyOptions getAdm4ExtracterStrategyIfAlreadyExists() {
    	return this.adm4ExtracterStrategyIfAlreadyExists;
    }

    /**
     * What should we do if the Adm4 file has already been processed
     * 
     * @see #DEFAULT_ADM3_EXTRACTER_STRATEGY_OPTION
     * @param adm4ExtracterStrategy
     *            The option
     */
    @Required
    public void setAdm4ExtracterStrategyIfAlreadyExists(AdmExtracterStrategyOptions adm4ExtracterStrategy) {
    	adm4ExtracterStrategyIfAlreadyExists = adm4ExtracterStrategy;
    }

    /**
     * @return The option
     * @see #setMissingRequiredFieldThrows(boolean)
     * @see MissingRequiredFieldException
     */
    public boolean isMissingRequiredFieldThrows() {
    	return this.missingRequiredFieldThrows;
    }

    /**
     * Set to true this options force the import process to stop if a required
     * field is missing.<br>
     * Set to false it ignore the error and try to continue (recommended)
     * 
     * @param missingRequiredFieldThrows
     *            The option
     * @see MissingRequiredFieldException
     */
    @Required
    public void setMissingRequiredFieldThrows(boolean missingRequiredFieldThrows) {
    	this.missingRequiredFieldThrows = missingRequiredFieldThrows;
    }

    /**
     * @return The option
     * @see #setWrongNumberOfFieldsThrows(boolean)
     */
    public boolean isWrongNumberOfFieldsThrows() {
    	return this.wrongNumberOfFieldsThrows;
    }

    /**
     * Set to true this option force the import process to stop if an error is
     * throw.<br>
     * Set to false it ignore the error and try to continue (recommended)
     * 
     * @param wrongNumberOfFieldsThrows
     *            The option
     */
    @Required
    public void setWrongNumberOfFieldsThrows(boolean wrongNumberOfFieldsThrows) {
    	this.wrongNumberOfFieldsThrows = wrongNumberOfFieldsThrows;
    }

    /**
     * @return The option
     * @see #setImportGisFeatureEmbededAlternateNames(boolean)
     */
    public boolean isImportGisFeatureEmbededAlternateNames() {
    	return importGisFeatureEmbededAlternateNames;
    }

    /**
     * Set to true the alternate names of the country dump are imported. Set to
     * false it will import the alternate names from the alternatenames dump
     * file
     * 
     * @param importGisFeatureEmbededAlternateNames
     *            The option
     */
    @Required
    public void setImportGisFeatureEmbededAlternateNames(boolean importGisFeatureEmbededAlternateNames) {
    	this.importGisFeatureEmbededAlternateNames = importGisFeatureEmbededAlternateNames;
    }

   
    
   
    /**
     * @return The option
     * @see #setRetrieveFiles(boolean)
     */
    public boolean isRetrieveFiles() {
    	return this.retrieveFiles;
    }

    /**
     * Whether we should download the geonames file or use the one already
     * present in the {@link #geonamesDir}
     * 
     * @param retrieveFiles
     *            The options
     */
    @Required
    public void setRetrieveFiles(boolean retrieveFiles) {
    	this.retrieveFiles = retrieveFiles;
    }

    
    
   
    /**
     * @return the option
     * @see #getAdm1FileName()
     */
    public String getAdm1FileName() {
    	return this.adm1FileName;
    }

    /**
     * The name of the Geonames dump file containing the ADM with level 1
     * 
     * @param adm1FileName
     *            The option
     */
    @Required
    public void setAdm1FileName(String adm1FileName) {
    	this.adm1FileName = adm1FileName;
    }

    /**
     * @return The option
     * @see #getAdm2FileName()
     */
    public String getAdm2FileName() {
    	return this.adm2FileName;
    }

    /**
     * The name of the Geonames dump file containing the ADM with level 2
     * 
     * @param adm2FileName
     *            The option
     */
    @Required
    public void setAdm2FileName(String adm2FileName) {
    	this.adm2FileName = adm2FileName;
    }

    /**
     * @return The option
     * @see #getAdm3FileName()
     */
    public String getAdm3FileName() {
    	return this.adm3FileName;
    }

    /**
     * The name of the Geonames dump file containing the ADM with level 3
     * 
     * @param adm3FileName
     *            the adm3FileName to set
     */
    @Required
    public void setAdm3FileName(String adm3FileName) {
    	this.adm3FileName = adm3FileName;
    }

    /**
     * @return The option
     * @see #getAdm4FileName()
     */
    public String getAdm4FileName() {
    	return adm4FileName;
    }

    /**
     * The name of the Geonames dump file containing the ADM with level 4
     * 
     * @param adm4FileName
     *            The option
     */
    @Required
    public void setAdm4FileName(String adm4FileName) {
    	this.adm4FileName = adm4FileName;
    }

    /**
     * @return The Option
     * @see #getCountriesFileName()
     */
    public String getCountriesFileName() {
    	return this.countriesFileName;
    }

    /**
     * The name of the Geonames dump file containing the countries informations
     * 
     * @param countryFileName
     *            The option
     */
    @Required
    public void setCountriesFileName(String countryFileName) {
    	this.countriesFileName = countryFileName;
    }

    /**
     * @return The option
     * @see #setLanguageFileName(String)
     */
    public String getLanguageFileName() {
    	return this.languageFileName;
    }

    /**
     * The name of the Geonames dump file containing the language informations
     * 
     * @param languageFileName
     *            The option
     */
    @Required
    public void setLanguageFileName(String languageFileName) {
    	this.languageFileName = languageFileName;
    }

    /**
     * @return The option
     * @see #getAlternateNamesFileName()
     */
    public String getAlternateNamesFileName() {
    	return alternateNamesFileName;
    }

    /**
     * The name of the Geonames dump file containing the alternate names
     * 
     * @param alternateNamesFileName
     *            The option
     */
    @Required
    public void setAlternateNamesFileName(String alternateNamesFileName) {
    	this.alternateNamesFileName = alternateNamesFileName;
    }

    /**
     * Optional setting that allows to specify the number of inserts that can be
     * done before flushing. This is useful since most ORM technologies use a
     * so-called Level-2 cache that will store all the persisted data until they
     * are either comitted or flushed...default value is
     * {@link #DEFAULT_MAX_INSERT_BEFORE_FLUSH}
     * 
     * @param maxInsertsBeforeFlush
     *            The option
     */
    @Required
    public void setMaxInsertsBeforeFlush(int maxInsertsBeforeFlush) {
    	this.maxInsertsBeforeFlush = maxInsertsBeforeFlush;
    }

    /**
     * @return The option
     * @see #setMaxInsertsBeforeFlush(int)
     */
    public int getMaxInsertsBeforeFlush() {
    	return this.maxInsertsBeforeFlush;
    }

    /**
     * @param directoryPath
     *            The directory to check. it can be absolute or relative
     * @return true if the path is a directory (not a file) AND exists AND is
     *         writable
     */
    private boolean isDirectoryAccessible(String directoryPath) {
    	File dir = new File(directoryPath);
		boolean ok = dir.exists() && dir.isDirectory() && dir.canWrite();
    	if (!ok){
    		logger.error("directory "+directoryPath+"' is not accessible");
    	} 
    	return ok;
    }



    /**
     * @return true if the regexp of the feature class/ code are correct
     */
    public boolean isRegexpCorrects() {
    	boolean ok = ImporterHelper.compileRegex(getAcceptRegExString()) != null;
    	if (!ok){
    		logger.error("regexp "+getAcceptRegExString()+"' is not correct");
    	} 
    	return ok;
    }

    /**
     * @return true if the config is Ok to process the import
     */
    public boolean isConfigCorrectForImport() {
    	boolean firstcondition =  isRegexpCorrects() && isGeonamesDownloadDirectoryAccessible() && isOpenStreetMapDownloadDirectoryAccessible() && isOpenStreetMapHouseNumberDownloadDirectoryAccessible()
    			&& isOpenStreetMapCitiesDirectoryAccessible() && isOpenStreetMapPoisDirectoryAccessible() && isQuattroshapesDirectoryAccessible();
    		if (isRetrieveFiles()){
    			return firstcondition && isAllFilesDownloadables();
    		} else {
    			return firstcondition;
    		}
    }
    
    
    public boolean isAllFilesDownloadables(){
    	//geonames
    	List<String> filenames ;
    	if(isGeonamesImporterEnabled()){
	    	 filenames = getGeonamesDownloadFilesListFromOption();
	    	for (String filename:filenames){
	    		if (!checkUrl(getGeonamesDownloadURL()+filename)){
	    			return false;
	    		}
	    	}
	    	//zip
	    	//because the zipcode importer is tolerant to non existing url we skip the tests
	    	
    	}
    	
    	if(isOpenstreetmapHouseNumberImporterEnabled()){
	    	//osm house number
	    	filenames = getOpenStreetMapHouseNumberDownloadFilesListFromOption();
	    	for (String filename:filenames){
	    		if (!checkUrl(getOpenstreetMaphouseNumbersDownloadURL()+filename)){
	    			return false;
	    		}
	    	}
    	}
    	
    	if(isOpenstreetmapImporterEnabled()){
    		//osmstreet
	    	filenames = getOpenStreetMapDownloadFilesListFromOption();
	    	for (String filename:filenames){
	    		if (!checkUrl(getOpenstreetMapDownloadURL()+filename)){
	    			return false;
	    		}
	    	}
	    	//osm cities
	    	filenames = getOpenStreetMapCitiesDownloadFilesListFromOption();
	    	for (String filename:filenames){
	    		if (!checkUrl(getOpenstreetMapCitiesDownloadURL()+filename)){
	    			return false;
	    		}
	    	}
	    	//osm poi
	    	filenames = getOpenStreetMapPoisDownloadFilesListFromOption();
	    	for (String filename:filenames){
	    		if (!checkUrl(getOpenstreetMapPoisDownloadURL()+filename)){
	    			return false;
	    		}
	    	}
	    	}
    	if (quattroshapesImporterEnabled){
    		filenames = getQuattroshapesFilesDownloadFilesListFromOption();
    		for (String filename:filenames){
	    		if (!checkUrl(getQuattroshapesDownloadURL()+filename)){
	    			return false;
	    		}
	    	}
    	}
    	
    	
    	logger.info("All files are downloadables");
    	return true;
    }
   
    /**
     * @return the path to the file that give the information if the import is
     *         done or not
     */
    public String getAlreadyDoneFilePath() {
    	return getImporterMetadataDirectoryPath() + ALREADY_DONE_FILE_NAME;
    }

    /**
     * @return the path to the file that give the information if the import is
     *         done or not
     */
    public String getImporterMetadataDirectoryPath() {
    	return getGeonamesDir() + IMPORTER_METADATA_RELATIVE_PATH + File.separator;
    }

    /**
     * Create the importerMetadataDirectory
     * 
     * @return the path to the importerMetadataDirectory
     */
    public String createImporterMetadataDirIfItDoesnTExist() {
	if (!isGeonamesDownloadDirectoryAccessible()) {
	    if (!new File(getGeonamesDir()).mkdir()) {
		throw new RuntimeException("the geonameDirectory doesn't exists and we can not create it ");
	    }
	}
	String dirpath = getGeonamesDir() + IMPORTER_METADATA_RELATIVE_PATH + File.separator;
	File directory = new File(dirpath);
	if (!directory.exists()) {
	    if (!directory.mkdir()) {
		throw new RuntimeException("Can not create ImporterMetadataDirectory");
	    }
	}
	return dirpath;
    }

    /**
     * Get the name of the file where the alternate names of features that are
     * not adm1, adm2, or country are
     * 
     * @see #setAlternateNameFeaturesFileName(String)
     * @return The name of the file
     */
    public String getAlternateNameFeaturesFileName() {
    	return alternateNameFeaturesFileName;
    }

    /**
     * Set the name of the file where the alternate names of features that are
     * not adm1, adm2, or country are
     * 
     * @see #getAlternateNameFeaturesFileName()
     * @param alternateNameFeaturesFileName
     *            The name of the file to set
     */
    @Required
    public void setAlternateNameFeaturesFileName(String alternateNameFeaturesFileName) {
    	this.alternateNameFeaturesFileName = alternateNameFeaturesFileName;
    }

    /**
     * Get the name of the file where the alternate names of adm with level 1
     * are
     * 
     * @see #setAlternateNameAdm1FileName(String)
     * @return The name of the file
     */
    public String getAlternateNameAdm1FileName() {
    	return alternateNameAdm1FileName;
    }

    /**
     * Set the name of the file where the alternate names of adm with level 1
     * are
     * 
     * @see #getAlternateNameAdm1FileName()
     * @param alternateNameAdm1FileName
     *            The name of the file to set
     */
    @Required
    public void setAlternateNameAdm1FileName(String alternateNameAdm1FileName) {
    	this.alternateNameAdm1FileName = alternateNameAdm1FileName;
    }

    /**
     * Get the name of the file where the alternate names of adm with level 2
     * are
     * 
     * @see #setAlternateNameAdm2FileName(String)
     * @return The name of the file
     */
    public String getAlternateNameAdm2FileName() {
    	return alternateNameAdm2FileName;
    }

    /**
     * Set the name of the file where the alternate names of adm with level 2
     * are
     * 
     * @see #getAlternateNameAdm2FileName()
     * @param alternateNameAdm2FileName
     *            The name of the file to set
     */
    @Required
    public void setAlternateNameAdm2FileName(String alternateNameAdm2FileName) {
    	this.alternateNameAdm2FileName = alternateNameAdm2FileName;
    }

    /**
     * Get the name of the file where the alternate names of countries are
     * 
     * @see #setAlternateNameCountryFileName(String)
     * @return The name of the file
     */
    public String getAlternateNameCountryFileName() {
    	return alternateNameCountryFileName;
    }

    /**
     * Set the name of the file where the alternate names of countries are
     * 
     * @see #getAlternateNameCountryFileName()
     * @param alternateNameCountryFileName
     *            The name of the file to set
     */
    @Required
    public void setAlternateNameCountryFileName(String alternateNameCountryFileName) {
    	this.alternateNameCountryFileName = alternateNameCountryFileName;
    }

    /**
     * if we search for the nearest city in geonames data to fill the is_in
     * field this increase the time of the importer but strongly increase the
     * relevance of the geocoder
     */
    public boolean isOpenStreetMapFillIsIn() {
    	return openStreetMapFillIsIn;
    }

    /**
     * @see #isOpenStreetMapFillIsIn()
     */
    public void setOpenStreetMapFillIsIn(boolean openStreetMapFillIsIn) {
    	this.openStreetMapFillIsIn = openStreetMapFillIsIn;
    	CommentedProperties.editPropertyFromClassPathRessource(PROPERTIES_CONFIG_FILE_CLASSPATH, OPENSTREETMAP_FILL_ISIN_FIELD_NAME, String.valueOf(openStreetMapFillIsIn));
    }


	public boolean isRenameFilesAfterProcessing() {
		return renameFilesAfterProcessing;
	}

	public void setRenameFilesAfterProcessing(boolean renameFilesAfterProcessing) {
		this.renameFilesAfterProcessing = renameFilesAfterProcessing;
	}

}
