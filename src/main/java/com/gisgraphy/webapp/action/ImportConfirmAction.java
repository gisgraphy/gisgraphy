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
package com.gisgraphy.webapp.action;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.helper.PropertiesHelper;
import com.gisgraphy.importer.IImporterManager;
import com.gisgraphy.importer.ImporterConfig;
import com.gisgraphy.importer.ImporterManager;
import com.gisgraphy.importer.ImporterMetaDataException;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action that retrieve the configuration and
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * @see ImporterManager
 */
public class ImportConfirmAction extends ActionSupport {

	protected static final String IMPORT_VIEW_NAME = "import";
	protected static final String CHECK_CONFIG_VIEW_NAME = "checkconfig";
	protected static final String ERRORCONFIG = "errorconfig";

	private static Logger logger = LoggerFactory.getLogger(ImportConfirmAction.class);

	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 2387732133217655558L;

	private ImporterConfig importerConfig;

	private IImporterManager importerManager;

	private String errorMessage = "";

	public static final String STATUS = "status";

	public static final String STEP_BASE_VIEW_NAME = "importWizardStep";

	private IFullTextSearchEngine fullTextSearchEngine;

	private boolean importallcountries = true;

	private boolean importallplacetype = true;

	private List<String> countryCodes;

	private List<String> placetypes;

	private int step = 1;
	
	private boolean configGotProblems= false;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		boolean alreadyDone;
		try {
			alreadyDone = importerManager.isAlreadyDone();
			if (importerManager.isInProgress() || alreadyDone) {
				return STATUS;
			}
		} catch (ImporterMetaDataException e) {
			errorMessage = e.getMessage();
			return ERRORCONFIG;
		}
		return super.execute();
	}
	
	public String checkConfig(){
		return CHECK_CONFIG_VIEW_NAME;
		
	}

	public String[] getPlacetypesList() {
		Reflections reflections = new Reflections("com.gisgraphy.domain.geoloc.entity");

		Set<Class<? extends GisFeature>> allClasses = 
				reflections.getSubTypesOf(GisFeature.class);
		String[] placeTypes;
		if (allClasses!=null){
			placeTypes = new String[allClasses.size()];
			int i=0;
			for (Class clazz: allClasses){
				placeTypes[i]=clazz.getSimpleName();
				i++;
			}
		} else {
			placeTypes = new String[0];
		}
		 Arrays.sort(placeTypes);
		 return placeTypes;
	}

	public String doImport() throws Exception {
		setConfig();
		if (isConfigOk()){
			return IMPORT_VIEW_NAME;
		} else {
			step=8;
			return super.execute();
		}
	}

	protected void setConfig() {
		if (importallcountries) {
			importerConfig.setOpenStreetMapFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
			importerConfig.setOpenStreetMapHouseNumberFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
			importerConfig.setOpenStreetMapCitiesFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
			importerConfig.setOpenStreetMapPoisFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
			importerConfig.setGeonamesFilesToDownload(ImporterConfig.GEONAMES_DEFAULT_FILES_TO_DOWNLOAD);
			
		} else {
			if (getCountryCodes() != null && getCountryCodes().size() > 0) {
				StringBuffer geonamesCountryFileList = new StringBuffer();
				StringBuffer openstreetmapCountryFileList = new StringBuffer();
				for (String country : countryCodes) {
					if (country != null && country.length() == 2) {
						geonamesCountryFileList.append(country.toUpperCase()).append(ImporterConfig.GEONAMES_COMPRESSED_FILE_EXTENSION).append(ImporterConfig.OPTION_SEPARATOR);
						openstreetmapCountryFileList.append(country.toUpperCase()).append(ImporterConfig.OPENSTREETAMP_COMPRESSED_FILE_EXTENSION).append(ImporterConfig.OPTION_SEPARATOR);
					}
				}
				geonamesCountryFileList.append(ImporterConfig.GEONAMES_ALTERNATENAME_ZIP_FILE);
				String openstreetmapCountryFileListAsString = openstreetmapCountryFileList.toString();
				if (openstreetmapCountryFileListAsString.endsWith(ImporterConfig.OPTION_SEPARATOR)){
					openstreetmapCountryFileListAsString = openstreetmapCountryFileListAsString.substring(0, openstreetmapCountryFileListAsString.length() - 1);
				}
				String geonamesFileList = geonamesCountryFileList.toString();
				importerConfig.setOpenStreetMapFilesToDownload(openstreetmapCountryFileListAsString);
				importerConfig.setOpenStreetMapHouseNumberFilesToDownload(openstreetmapCountryFileListAsString);
				importerConfig.setOpenStreetMapCitiesFilesToDownload(openstreetmapCountryFileListAsString);
				importerConfig.setOpenStreetMapPoisFilesToDownload(openstreetmapCountryFileListAsString);
				importerConfig.setGeonamesFilesToDownload(geonamesFileList);
				// TODO +log
			} else {
				logger.info("Import all countries is false but no country list recieved,set list to default");
				importerConfig.setOpenStreetMapFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
				importerConfig.setOpenStreetMapHouseNumberFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
				importerConfig.setOpenStreetMapCitiesFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
				importerConfig.setOpenStreetMapPoisFilesToDownload(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD);
				importerConfig.setGeonamesFilesToDownload(ImporterConfig.GEONAMES_DEFAULT_FILES_TO_DOWNLOAD);
			}
		}
		logger.info("openstreetmap files to download wizard : "+importerConfig.getOpenStreetMapFilesToDownload());
		logger.info("geonames files to download wizard : "+importerConfig.getGeonamesFilesToDownload());
		//placetype section
		if (importallplacetype) {
			importerConfig.setAcceptRegExString(ImporterConfig.ACCEPT_ALL_REGEX_OPTION);
		} else {
			if (getPlacetypes() != null && getPlacetypes().size() > 0) {
				StringBuffer placetypeOption = new StringBuffer();
				for(String placetype:getPlacetypes()){
					placetypeOption.append(placetype.toUpperCase()).append(ImporterConfig.REGEXP_SEPARATOR);
				}
				String optionAsString = placetypeOption.toString();
				if (optionAsString.endsWith(ImporterConfig.REGEXP_SEPARATOR)){
					optionAsString = optionAsString.substring(0, optionAsString.length() - 1);
				}
				importerConfig.setAcceptRegExString(optionAsString);
			} else {
				logger.info("Import all placetype is false but no placetype list recieved,set list to default");
				importerConfig.setAcceptRegExString(ImporterConfig.ACCEPT_ALL_REGEX_OPTION);
			}
		}
		logger.info("placetypes wizard :  "+importerConfig.getAcceptRegExString());
	}

	public int getNumberOfProcessors() {
		int numberOfProcessors = Runtime.getRuntime().availableProcessors();
		logger.info(numberOfProcessors + " processor(s) has been detected");
		return numberOfProcessors;
	}

	/**
	 * @return the importerConfig
	 */
	public ImporterConfig getImporterConfig() {
		return importerConfig;
	}

	/**
	 * @return true if the directory with the file to import exists and is
	 *         accessible
	 */
	public boolean isDownloadDirectoryAccessible() {
		return importerConfig.isGeonamesDownloadDirectoryAccessible();
	}

	/**
	 * @return true if the directory with the file to import exists and is
	 *         accessible
	 */
	public boolean isOpenStreetMapDownloadDirectoryAccessible() {
		return importerConfig.isOpenStreetMapDownloadDirectoryAccessible();
	}

	/**
	 * @return true if the regexp of the feature class/ code are correct
	 */
	public boolean isRegexpCorrects() {
		return importerConfig.isRegexpCorrects();
	}

	/**
	 * @return true if he fulltext search engine is alive
	 */
	public boolean isFulltextSearchEngineAlive() {
		return fullTextSearchEngine.isAlive();
	}

	/**
	 * @return true if he fulltext search engine is alive
	 */
	public String getFulltextSearchEngineURL() {
		return fullTextSearchEngine.getURL();
	}

	/**
	 * @return true if the Geonames importer is enabled
	 */
	public boolean isGeonamesImporterEnabled() {
		return importerConfig.isGeonamesImporterEnabled();
	}
	

	/**
	 * Enable / Disable Geonames importer
	 */
	public void setGeonamesImporterEnabled(boolean geonamesImporterEnabled) {
		importerConfig.setGeonamesImporterEnabled(geonamesImporterEnabled);
	}

	/**
	 * @return true if he openStreetMap importer is enabled
	 */
	public boolean isOpenStreetMapImporterEnabled() {
		return importerConfig.isOpenstreetmapImporterEnabled();
	}

	/**
	 * Enable / Disable OpenStreetMap importer
	 */
	public void setOpenStreetMapImporterEnabled(boolean openStreetMapImporter) {
		importerConfig.setOpenstreetmapImporterEnabled(openStreetMapImporter);
	}
	

	/**
	 * @return true if the house number importer is enabled
	 */
	public boolean isHousenumberImporterEnabled() {
		return importerConfig.isOpenstreetmapHouseNumberImporterEnabled();
	}
	
	/**
	 * Enable / Disable housenumber importer
	 */
	public void setHousenumberImporterEnabled(boolean housenumberImporterEnabled) {
		importerConfig.setOpenstreetmapHouseNumberImporterEnabled(housenumberImporterEnabled);
	}
	
	/**
	 * @return true if the quattroshapes importer is enabled
	 */
	public boolean isQuattroshapesImporterEnabled() {
		return importerConfig.isQuattroshapesImporterEnabled();
	}
	
	/**
	 * Enable / Disable quattroshpaes importer
	 */
	public void setQuattroshapesImporterEnabled(boolean quattroshapesImporterEnabled) {
		importerConfig.setQuattroshapesImporterEnabled(quattroshapesImporterEnabled);
	}

	/**
	 * @param importerManager
	 *            the importerManager to set
	 */
	@Required
	public void setImporterManager(IImporterManager importerManager) {
		this.importerManager = importerManager;
	}

	/**
	 * @param fullTextSearchEngine
	 *            the fullTextSearchEngine to set
	 */
	@Required
	public void setFullTextSearchEngine(IFullTextSearchEngine fullTextSearchEngine) {
		this.fullTextSearchEngine = fullTextSearchEngine;
	}

	@Required
	public void setImporterConfig(ImporterConfig importerConfig) {
		this.importerConfig = importerConfig;
	}

	public Map<String, String> getConfigValuesMap() {
		return PropertiesHelper.convertBundleToMap(ResourceBundle.getBundle(GisgraphyConfig.ENVIRONEMENT_PROPERTIES_FILE));
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean isImportallcountries() {
		return importallcountries;
	}

	public void setImportallcountries(boolean importallcountries) {
		this.importallcountries = importallcountries;
	}

	public boolean isImportallplacetype() {
		return importallplacetype;
	}

	public void setImportallplacetype(boolean importallplacetype) {
		this.importallplacetype = importallplacetype;
	}

	public void setPlacetypes(List<String> placetypes) {
		this.placetypes = placetypes;
	}

	public List<String> getPlacetypes() {
		return placetypes;
	}

	public List<String> getCountryCodes() {
		return countryCodes;
	}

	public void setCountryCodes(List<String> countryCodes) {
		this.countryCodes = countryCodes;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public boolean isImportEmbededAlternateNames() {
		return importerConfig.isImportGisFeatureEmbededAlternateNames();
	}

	public void setImportEmbededAlternateNames(boolean importEmbededAlternateNames) {
		importerConfig.setImportGisFeatureEmbededAlternateNames(importEmbededAlternateNames);
	}

	public boolean isFillIsInEnabled() {
		return importerConfig.isOpenStreetMapFillIsIn();
	}

	public void setFillIsInEnabled(boolean fillIsInEnabled) {
		importerConfig.setOpenStreetMapFillIsIn(fillIsInEnabled);
	}

	public boolean isRetrieveFileEnable() {
		return importerConfig.isRetrieveFiles();
	}

	public void setRetrieveFileEnable(boolean isRetrieveFileEnable) {
		importerConfig.setRetrieveFiles(isRetrieveFileEnable);
	}
	
	
	public boolean isConfigOk(){
		boolean configOK =(importerConfig.isConfigCorrectForImport() && isFulltextSearchEngineAlive());
		configGotProblems = ! configOK;
		return configOK;
		
	}

	public boolean isConfigGotProblems() {
		return configGotProblems;
	}

	public void setConfigGotProblems(boolean configGotProblems) {
		this.configGotProblems = configGotProblems;
	}

	

}
