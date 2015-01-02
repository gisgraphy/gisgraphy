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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.helper.PropertiesHelper;
import com.gisgraphy.importer.IImporterManager;
import com.gisgraphy.importer.ImporterConfig;
import com.gisgraphy.importer.ImporterManager;
import com.gisgraphy.importer.ImporterMetaDataException;

public class ImportConfirmActionTest {

    private IImporterManager createMockImporterManager(boolean inProgress, boolean alreadyDone) throws Exception {
	IImporterManager mockImporterManager = EasyMock.createMock(IImporterManager.class);
	EasyMock.expect(mockImporterManager.isInProgress()).andStubReturn(inProgress);
	EasyMock.expect(mockImporterManager.isAlreadyDone()).andStubReturn(alreadyDone);
	EasyMock.replay(mockImporterManager);
	return mockImporterManager;
    }
    
    private IImporterManager createImporterManagerThatThrowsWhenIsAlreadyDoneIsCalled(String ErrorMessage) throws ImporterMetaDataException {
	IImporterManager mockImporterManager = createMock(ImporterManager.class);
	expect(mockImporterManager.isAlreadyDone()).andStubThrow(new ImporterMetaDataException(ErrorMessage));
	EasyMock.replay(mockImporterManager);
	return mockImporterManager;
    }

    @Test
    public void executeShouldReturnStatusViewIfInProgress() throws Exception {
	IImporterManager mockImporterManager = createMockImporterManager(true, false);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterManager(mockImporterManager);
	assertEquals(ImportConfirmAction.STATUS, action.execute());
    }

    @Test
    public void executeShouldReturnErrorViewIfIsALreadyDoneThrows() throws Exception {
	String ErrorMessage = "MyMessageToCheck";
	IImporterManager mockImporterManager = createImporterManagerThatThrowsWhenIsAlreadyDoneIsCalled(ErrorMessage);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterManager(mockImporterManager);
	assertEquals(ImportConfirmAction.ERRORCONFIG, action.execute());
	assertEquals("incorect eror message ", ErrorMessage, action.getErrorMessage());
    }

    @Test
    public void executeShouldReturnSuccessViewIfNotInProgress() throws Exception {
	IImporterManager mockImporterManager = createMockImporterManager(false, false);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterManager(mockImporterManager);
	assertEquals(ImportConfirmAction.SUCCESS, action.execute());
    }

    @Test
    public void executeShouldReturnSuccessViewIfNotAlreadyDone() throws Exception {
	IImporterManager mockImporterManager = createMockImporterManager(false, false);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterManager(mockImporterManager);
	assertEquals(ImportConfirmAction.SUCCESS, action.execute());
    }

    @Test
    public void executeShouldReturnStatusViewIfAlreadyDone() throws Exception {
	IImporterManager mockImporterManager = createMockImporterManager(false, true);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(ImportConfirmAction.STATUS, action.execute());
    }


    @Test
    public void isGeonamesImporterEnabled() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isGeonamesImporterEnabled());
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	Assert.assertTrue("isGeonamesImporterEnabled should return the same value as the importerConfig One ", action.isGeonamesImporterEnabled());
	importerConfig.setGeonamesImporterEnabled(false);
	Assert.assertFalse("isGeonamesImporterEnabled should return the same value as the importerConfig One ", action.isGeonamesImporterEnabled());
    }

    @Test
    public void disableGeonamesImporter() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isGeonamesImporterEnabled());

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);

	Assert.assertTrue("isGeonamesImporterEnabled should return the same value as the importerConfig One ", action.isGeonamesImporterEnabled());
	action.setGeonamesImporterEnabled(false);
	Assert.assertFalse("isGeonamesImporterEnabled should return the same value as the importerConfig One ", action.isGeonamesImporterEnabled());
	Assert.assertFalse("isGeonamesImporterEnabled should return the same value as the importerConfig One ", importerConfig.isGeonamesImporterEnabled());
    }

    @Test
    public void enableGeonamesImporter() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isGeonamesImporterEnabled());

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	importerConfig.setGeonamesImporterEnabled(false);
	Assert.assertFalse("isGeonamesImporterEnabled should return the same value as the importerConfig One ", action.isGeonamesImporterEnabled());

	action.setGeonamesImporterEnabled(true);
	Assert.assertTrue("isGeonamesImporterEnabled should return the same value as the importerConfig One ", action.isGeonamesImporterEnabled());
	Assert.assertTrue("isGeonamesImporterEnabled should return the same value as the importerConfig One ", importerConfig.isGeonamesImporterEnabled());
    }
    
    @Test
    public void DisableRetrieveFile() {
	ImporterConfig importerConfig = new ImporterConfig();

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);

	action.setRetrieveFileEnable(false);
	Assert.assertFalse(action.isRetrieveFileEnable());
	Assert.assertFalse(importerConfig.isRetrieveFiles());
    }
    
    @Test
    public void DoImportShouldHandleAllcountriesEnabled() throws Exception{
    	ImportConfirmAction action = new ImportConfirmAction();
    	ImporterConfig importerConfig = new ImporterConfig();
    	action.setImporterConfig(importerConfig);
    	action.setImportallcountries(true);
    	 action.setConfig();
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapFilesToDownload());
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapHouseNumberFilesToDownload());
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapCitiesFilesToDownload());
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapPoisFilesToDownload());
    	Assert.assertEquals(ImporterConfig.GEONAMES_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getGeonamesFilesToDownload());
    }
    
    @Test
    public void DoImportShouldHandleAllcountriesDisable_CountryFileListNotReceived() throws Exception{
    	ImportConfirmAction action = new ImportConfirmAction();
    	ImporterConfig importerConfig = new ImporterConfig();
    	action.setImporterConfig(importerConfig);
    	action.setImportallcountries(false);
    	action.setConfig();
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapFilesToDownload());
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapHouseNumberFilesToDownload());
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapCitiesFilesToDownload());
    	Assert.assertEquals(ImporterConfig.OPENSTREETMAP_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getOpenStreetMapPoisFilesToDownload());
    	Assert.assertEquals(ImporterConfig.GEONAMES_DEFAULT_FILES_TO_DOWNLOAD, importerConfig.getGeonamesFilesToDownload());
    }
    
    @Test
    public void DoImportShouldHandleAllcountriesDisable_CountryFileListReceived() throws Exception{
    	ImportConfirmAction action = new ImportConfirmAction();
    	ImporterConfig importerConfig = new ImporterConfig();
    	action.setImporterConfig(importerConfig);
    	action.setImportallcountries(false);
    	List<String> countryCodes = new ArrayList<String>();
    	countryCodes.add("fr");//test to upper case
    	countryCodes.add("DE");
    	countryCodes.add("US");
    	countryCodes.add("x");//countrycode should only have two letters
    	action.setCountryCodes(countryCodes);
    	action.setConfig();
    	Assert.assertEquals("FR.tar.bz2;DE.tar.bz2;US.tar.bz2", importerConfig.getOpenStreetMapFilesToDownload());
    	Assert.assertEquals("FR.tar.bz2;DE.tar.bz2;US.tar.bz2", importerConfig.getOpenStreetMapHouseNumberFilesToDownload());
    	Assert.assertEquals("FR.tar.bz2;DE.tar.bz2;US.tar.bz2", importerConfig.getOpenStreetMapCitiesFilesToDownload());
    	Assert.assertEquals("FR.tar.bz2;DE.tar.bz2;US.tar.bz2", importerConfig.getOpenStreetMapPoisFilesToDownload());
    	Assert.assertEquals("FR.zip;DE.zip;US.zip;"+ImporterConfig.GEONAMES_ALTERNATENAME_ZIP_FILE, importerConfig.getGeonamesFilesToDownload());
    }
    
    @Test
    public void DoImportShouldHandleAllPlacetypeEnabled() throws Exception{
    	ImportConfirmAction action = new ImportConfirmAction();
    	ImporterConfig importerConfig = new ImporterConfig();
    	action.setImporterConfig(importerConfig);
    	action.setImportallplacetype(true);
    	action.setConfig();
    	Assert.assertEquals(ImporterConfig.ACCEPT_ALL_REGEX_OPTION, importerConfig.getAcceptRegExString());
    	Assert.assertTrue(importerConfig.isRegexpCorrects());
    }
    
    @Test
    public void DoImportShouldHandleAllPlacetypeDisable_plactypeListNotReceived() throws Exception{
    	ImportConfirmAction action = new ImportConfirmAction();
    	ImporterConfig importerConfig = new ImporterConfig();
    	action.setImporterConfig(importerConfig);
    	action.setImportallplacetype(false);
    	action.setConfig();
    	Assert.assertEquals(ImporterConfig.ACCEPT_ALL_REGEX_OPTION, importerConfig.getAcceptRegExString());
    	Assert.assertTrue(importerConfig.isRegexpCorrects());
    }
    
    @Test
    public void DoImportShouldHandleAllPlacetypeDisable_plactypeListReceived() throws Exception{
    	ImportConfirmAction action = new ImportConfirmAction();
    	ImporterConfig importerConfig = new ImporterConfig();
    	action.setImporterConfig(importerConfig);
    	action.setImportallplacetype(false);
    	List<String> Placetypes = new ArrayList<String>();
    	Placetypes.add("ATM");//test to upper case
    	Placetypes.add("bay");
    	action.setPlacetypes(Placetypes);
    	action.setConfig();
    	Assert.assertEquals(ImporterConfig.BASE_ACCEPT_REGEX+"ATM|BAY", importerConfig.getAcceptRegExString());
    	Assert.assertTrue(importerConfig.isRegexpCorrects());
    }

    
    @Test
    public void enableRetrieveFile() {
	ImporterConfig importerConfig = new ImporterConfig();

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	importerConfig.setRetrieveFiles(false);
	Assert.assertFalse(action.isRetrieveFileEnable());

	action.setRetrieveFileEnable(true);
	Assert.assertTrue(action.isRetrieveFileEnable());
	Assert.assertTrue(importerConfig.isRetrieveFiles());
    }
    
    @Test
    public void disableFillIsIn() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isOpenStreetMapFillIsIn());

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);

	Assert.assertTrue(action.isFillIsInEnabled());
	action.setFillIsInEnabled(false);
	Assert.assertFalse(action.isFillIsInEnabled());
	Assert.assertFalse(importerConfig.isOpenStreetMapFillIsIn());
    }

    @Test
    public void enableFillIsIn() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isGeonamesImporterEnabled());
	

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	importerConfig.setOpenStreetMapFillIsIn(false);
	Assert.assertFalse(action.isFillIsInEnabled());

	action.setFillIsInEnabled(true);
	Assert.assertTrue(action.isFillIsInEnabled());
	Assert.assertTrue(importerConfig.isOpenStreetMapFillIsIn());
    }
    
    @Test
    public void disableImportEmbededAlternateNanes() {
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setImportGisFeatureEmbededAlternateNames(true);
	Assert.assertTrue(importerConfig.isImportGisFeatureEmbededAlternateNames());

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);

	Assert.assertTrue(action.isImportEmbededAlternateNames());
	action.setImportEmbededAlternateNames(false);
	Assert.assertFalse(action.isImportEmbededAlternateNames());
	Assert.assertFalse(importerConfig.isImportGisFeatureEmbededAlternateNames());
    }

    @Test
    public void enableImportEmbededAlternateNanes() {
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setImportGisFeatureEmbededAlternateNames(true);
	Assert.assertTrue(importerConfig.isImportGisFeatureEmbededAlternateNames());
	

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	importerConfig.setImportGisFeatureEmbededAlternateNames(false);
	Assert.assertFalse(action.isImportEmbededAlternateNames());

	action.setImportEmbededAlternateNames(true);
	Assert.assertTrue(action.isImportEmbededAlternateNames());
	Assert.assertTrue(importerConfig.isImportGisFeatureEmbededAlternateNames());
    }

    @Test
    public void isOpenStreetMapImporterEnabled() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isOpenstreetmapImporterEnabled());
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	Assert.assertTrue("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", action.isOpenStreetMapImporterEnabled());
	importerConfig.setOpenstreetmapImporterEnabled(false);
	Assert.assertFalse("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", action.isOpenStreetMapImporterEnabled());
    }

    @Test
    public void disableOpenStreetMapImporter() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isOpenstreetmapImporterEnabled());

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);

	Assert.assertTrue("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", action.isOpenStreetMapImporterEnabled());
	action.setOpenStreetMapImporterEnabled(false);
	Assert.assertFalse("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", action.isOpenStreetMapImporterEnabled());
	Assert.assertFalse("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", importerConfig.isOpenstreetmapImporterEnabled());
    }

    @Test
    public void enableOpenStreetMapImporter() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isGeonamesImporterEnabled());

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	importerConfig.setOpenstreetmapImporterEnabled(false);
	Assert.assertFalse("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", action.isOpenStreetMapImporterEnabled());

	action.setOpenStreetMapImporterEnabled(true);
	Assert.assertTrue("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", action.isOpenStreetMapImporterEnabled());
	Assert.assertTrue("isOpenStreetMapImporterEnabled should return the same value as the importerConfig One ", importerConfig.isOpenstreetmapImporterEnabled());
    }
    
    @Test
    public void enableHouseNumberImporter() {
	ImporterConfig importerConfig = new ImporterConfig();
	Assert.assertTrue(importerConfig.isOpenstreetmapHouseNumberImporterEnabled());

	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	importerConfig.setOpenstreetmapHouseNumberImporterEnabled(false);
	Assert.assertFalse("isOpenstreetmapHouseNumberImporterEnabled should return the same value as the importerConfig One ", action.isHousenumberImporterEnabled());

	action.setHousenumberImporterEnabled(true);
	Assert.assertTrue("isHousenumberImporterEnabled should return the same value as the importerConfig One ", action.isHousenumberImporterEnabled());
	Assert.assertTrue("isHousenumberImporterEnabled should return the same value as the importerConfig One ", importerConfig.isOpenstreetmapHouseNumberImporterEnabled());
    }

    @Test
    public void isDownloadDirectoryAccessible() {
	ImporterConfig importerConfig = createMock(ImporterConfig.class);
	expect(importerConfig.isGeonamesDownloadDirectoryAccessible()).andReturn(true);
	replay(importerConfig);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	action.isDownloadDirectoryAccessible();
	EasyMock.verify(importerConfig);
    }

    @Test
    public void isOpenStreetMapDownloadDirectoryAccessible() {
	ImporterConfig importerConfig = createMock(ImporterConfig.class);
	expect(importerConfig.isOpenStreetMapDownloadDirectoryAccessible()).andReturn(true);
	replay(importerConfig);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	action.isOpenStreetMapDownloadDirectoryAccessible();
	EasyMock.verify(importerConfig);
    }

    @Test
    public void isRegexpCorrects() {
	ImporterConfig importerConfig = createMock(ImporterConfig.class);
	expect(importerConfig.isRegexpCorrects()).andReturn(true);
	replay(importerConfig);
	ImportConfirmAction action = new ImportConfirmAction();
	action.setImporterConfig(importerConfig);
	action.isRegexpCorrects();
	EasyMock.verify(importerConfig);
    }

    @Test
    public void getConfigValuesMap() {
	ImportConfirmAction action = new ImportConfirmAction();
	Assert.assertEquals(PropertiesHelper.convertBundleToMap(ResourceBundle.getBundle(Constants.ENVIRONEMENT_BUNDLE_KEY)), action.getConfigValuesMap());
    }
    
    @Test
    public void isConfOK(){
    	IFullTextSearchEngine fullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
    	EasyMock.expect(fullTextSearchEngine.isAlive()).andReturn(true);
    	EasyMock.replay(fullTextSearchEngine);
    	
    	ImporterConfig importerConfig = createMock(ImporterConfig.class);
    	expect(importerConfig.isConfigCorrectForImport()).andReturn(true);
    	
    	replay(importerConfig);
    	
    	ImportConfirmAction action = new ImportConfirmAction();
    	action.setImporterConfig(importerConfig);
    	action.setFullTextSearchEngine(fullTextSearchEngine);
    	Assert.assertTrue(action.isConfigOk());
    	
    	EasyMock.verify(fullTextSearchEngine);
    	EasyMock.verify(importerConfig);
    	
    }
    
}
