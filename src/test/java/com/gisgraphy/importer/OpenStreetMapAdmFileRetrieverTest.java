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

import static junit.framework.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.ImporterStatusDto;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.service.IInternationalisationService;
import com.gisgraphy.test.GisgraphyTestHelper;


public class OpenStreetMapAdmFileRetrieverTest {
    @Test
    public void processShouldExtractFilesEvenIfRetrieveFileIsFalse(){
	 final List<String> methodCalled = new ArrayList<String>();
	 final String downloadFlag = "download";
	 final String decompressFlag = "decompress";
	
	 OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever(){
	    @Override
	    protected void downloadFiles() {
		methodCalled.add(downloadFlag);
	    }
	    
	    @Override
	    public void decompressFiles() throws IOException {
		methodCalled.add(decompressFlag);
	    }
	};
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenstreetmapImporterEnabled(true);
	importerConfig.setRetrieveFiles(false);
	openStreetMapAdmFileRetriever.setInternationalisationService(createMockInternationalisationService());
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	openStreetMapAdmFileRetriever.process();
	Assert.assertEquals(decompressFlag, methodCalled.get(0));
	
    }
    
    @Test
    public void processShouldExtractAndDownloadFilesIfRetrieveFileIsTrue(){
	 final List<String> methodCalled = new ArrayList<String>();
	 final String downloadFlag = "download";
	 final String decompressFlag = "decompress";
	
	 OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever(){
	    @Override
	    protected void downloadFiles() {
		methodCalled.add(downloadFlag);
	    }
	    
	    @Override
	    public void decompressFiles() throws IOException {
		methodCalled.add(decompressFlag);
	    }
	};
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenstreetmapImporterEnabled(true);
	importerConfig.setRetrieveFiles(true);
	openStreetMapAdmFileRetriever.setInternationalisationService(createMockInternationalisationService());
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	openStreetMapAdmFileRetriever.process();
	Assert.assertEquals(downloadFlag, methodCalled.get(0));
	Assert.assertEquals(decompressFlag, methodCalled.get(1));
	
    }
    
    @Test
    public void processShouldDoNothingIfopenstreetmapIsDisabled(){
	 final List<String> methodCalled = new ArrayList<String>();
	 final String downloadFlag = "download";
	 final String decompressFlag = "decompress";
	
	 OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever(){
	    @Override
	    protected void downloadFiles() {
		methodCalled.add(downloadFlag);
	    }
	    
	    @Override
	    public void decompressFiles() throws IOException {
		methodCalled.add(decompressFlag);
	    }
	};
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenstreetmapImporterEnabled(false);
	importerConfig.setRetrieveFiles(true);
	openStreetMapAdmFileRetriever.setInternationalisationService(createMockInternationalisationService());
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	openStreetMapAdmFileRetriever.process();
	Assert.assertEquals(0, methodCalled.size());
	
    }
    
    @Test
    public void process() {
    	 OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever();
    	 openStreetMapAdmFileRetriever.setInternationalisationService(createMockInternationalisationService());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenstreetMapAdmDownloadURL(ImporterConfigTest.GISGRAPHY_DOWNLOAD_SERVER+"/openstreetmap/version_3_0/");
	
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());

	// get files to download
	List<String> filesToDownload =new ArrayList<String>();
	String fileTobeDownload = "NU.tar.bz2";
	filesToDownload.add(fileTobeDownload);
	importerConfig.setOpenStreetMapAdmFilesToDownload(fileTobeDownload);
	importerConfig.setRetrieveFiles(true);

	importerConfig.setOpenStreetMapAdmDir(tempDir.getAbsolutePath());

	// check that the directory is ending with the / or \ according to the
	// System
	Assert.assertTrue("openstreetmap adms dir must ends with" + File.separator,
		importerConfig.getOpenStreetMapAdmDir().endsWith(File.separator));
	
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	openStreetMapAdmFileRetriever.process();

	// check that openStreetmapURL ends with '/' : normally "/" is added
	// if not
	Assert.assertTrue("openstreetmapAdmDownloadURL must ends with '/' but was "
		+ importerConfig.getOpenstreetMapAdmDownloadURL(), importerConfig
		.getOpenstreetMapAdmDownloadURL().endsWith("/"));

	// check that files have been Downloaded
	File file = null;
	for (String fileToDownload : filesToDownload) {
	    file = new File(importerConfig.getOpenStreetMapAdmDir() + fileToDownload);
	    if (importerConfig.isRetrieveFiles()) {
		Assert.assertTrue("Le fichier " + fileToDownload
			+ " have not been downloaded in "
			+ importerConfig.getOpenStreetMapAdmDir(), file.exists());
	    } else {
		Assert.assertFalse("Le fichier " + fileToDownload
			+ " have been downloaded in "
			+ importerConfig.getOpenStreetMapAdmDir()
			+ " even if the option retrievefile is"
			+ importerConfig.isRetrieveFiles(), file.exists());
	    }
	}

	// check that files have been untar
	for (String fileToDownload : filesToDownload) {
	    String fileNameWithCSVExtension = fileToDownload.substring(0,
		    (fileToDownload.length()) - 8)
		    + ".txt";
	    file = new File(importerConfig.getOpenStreetMapAdmDir()
		    + fileNameWithCSVExtension);
	    if (importerConfig.isRetrieveFiles()) {
		Assert.assertTrue("Le fichier " + fileNameWithCSVExtension
			+ " have not been untar in "
			+ importerConfig.getOpenStreetMapAdmDir(), file.exists());
	    } else {
		Assert.assertFalse("Le fichier " + fileToDownload
			+ " have been unzip in "
			+ importerConfig.getOpenStreetMapAdmDir()
			+ " even if the option retrievefile is"
			+ importerConfig.isRetrieveFiles(), file.exists());
	    }
	}

	// delete temp dir
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));


    }
    
    
    @Test
    public void processWhenNotExistingFile() {
    	OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever();
    	openStreetMapAdmFileRetriever.setInternationalisationService(createMockInternationalisationService());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenstreetMapAdmDownloadURL(ImporterConfigTest.GISGRAPHY_DOWNLOAD_SERVER+"/openstreetmap/version_3_0/");
	
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());

	// get files to download
	List<String> filesToDownload =new ArrayList<String>();
	String fileTobeDownload = "notExisting.bz2";
	filesToDownload.add(fileTobeDownload);
	importerConfig.setOpenStreetMapAdmFilesToDownload(fileTobeDownload);
	importerConfig.setRetrieveFiles(true);

	importerConfig.setOpenStreetMapAdmDir(tempDir.getAbsolutePath());


	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	try {
		openStreetMapAdmFileRetriever.process();
	    fail("all the files specify should exists");
	} catch (ImporterException e) {
	    Assert.assertEquals(FileNotFoundException.class, e.getCause().getCause().getClass());
	}


	// delete temp dir
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));


    }

    private IInternationalisationService createMockInternationalisationService() {
	IInternationalisationService internationalisationService = EasyMock.createMock(IInternationalisationService.class);
	EasyMock.expect(internationalisationService.getString((String)EasyMock.anyObject())).andStubReturn("localizedValue");
	EasyMock.replay(internationalisationService);
	return internationalisationService;
    }
    
    @Test
    public void StatusShouldBeEqualsToSkipedIfRetrieveFileIsFalse(){
    	OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever(){
	    @Override
	    public void decompressFiles() throws IOException {
	       return;
	    }
	};
	openStreetMapAdmFileRetriever.setInternationalisationService(createMockInternationalisationService());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenstreetmapImporterEnabled(false);
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	openStreetMapAdmFileRetriever.process();
	Assert.assertEquals(ImporterStatus.SKIPPED, openStreetMapAdmFileRetriever.getStatus());
	ImporterStatusDto statusDto = new ImporterStatusDto(openStreetMapAdmFileRetriever);
	Assert.assertEquals(0, statusDto.getPercent());
    }
    
    @Test
    public void StatusShouldBeEqualsToPROCESSEDIfNoError(){
    	OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever();
    	openStreetMapAdmFileRetriever.setInternationalisationService(createMockInternationalisationService());
	ImporterConfig importerConfig = EasyMock.createMock(ImporterConfig.class);
	EasyMock.expect(importerConfig.isRetrieveFiles()).andReturn(true).times(2);
	EasyMock.expect(importerConfig.isOpenstreetmapImporterEnabled()).andReturn(true);
	EasyMock.expect(importerConfig.getGeonamesDownloadFilesListFromOption()).andStubReturn(new ArrayList<String>());
	EasyMock.expect(importerConfig.getOpenStreetMapAdmDir()).andStubReturn("");
	EasyMock.expect(importerConfig.getOpenstreetMapAdmDownloadURL()).andStubReturn("");
	EasyMock.expect(importerConfig.getOpenStreetMapAdmDownloadFilesListFromOption()).andStubReturn(new ArrayList<String>());
	
	EasyMock.replay(importerConfig);
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	openStreetMapAdmFileRetriever.process();
	Assert.assertEquals(ImporterStatus.PROCESSED, openStreetMapAdmFileRetriever.getStatus());
	ImporterStatusDto statusDto = new ImporterStatusDto(openStreetMapAdmFileRetriever);
	Assert.assertEquals(100, statusDto.getPercent());
    }
    
    @Test
    public void shouldBeSkipShouldReturnCorrectValue(){
	ImporterConfig importerConfig = new ImporterConfig();
	
	OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever();
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	
	importerConfig.setOpenstreetmapImporterEnabled(false);
	Assert.assertTrue(openStreetMapAdmFileRetriever.shouldBeSkipped());

	
	importerConfig.setOpenstreetmapImporterEnabled(true);
	Assert.assertFalse(openStreetMapAdmFileRetriever.shouldBeSkipped());
	
	
    }
    
    @Test
    public void getFilesToDownloadShouldReturnTheImporterConfigOption(){
	ImporterConfig importerConfig = new ImporterConfig();
	String fileTobeDownload = "AD.tar.bz2";
	List<String> filesToDownload =new ArrayList<String>();
	filesToDownload.add(fileTobeDownload);
	importerConfig.setOpenStreetMapAdmFilesToDownload(fileTobeDownload);
	OpenStreetMapAdmFileRetriever openStreetMapAdmFileRetriever = new OpenStreetMapAdmFileRetriever();
	openStreetMapAdmFileRetriever.setImporterConfig(importerConfig);
	Assert.assertEquals("getFilesToDownload should return the importerConfig Option",filesToDownload, openStreetMapAdmFileRetriever.getFilesToDownload());
	
	
    }
    
    
    
   

}
