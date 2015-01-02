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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.service.IInternationalisationService;
import com.gisgraphy.test.GisgraphyTestHelper;

public class GeonamesZipCodeFileRetrieverTest {

    @Test
    public void rollbackShouldRollback() {
	GeonamesZipCodeFileRetriever geonamesZipCodeFileRetriever = new GeonamesZipCodeFileRetriever();
	ImporterConfig importerConfig = new ImporterConfig();
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	File file = new File(tempDir.getAbsolutePath()
		+ System.getProperty("file.separator") + "FR.zip");
	try {
	    assertTrue(file.createNewFile());
	} catch (IOException e) {
	    fail("Can not create file " + file.getAbsolutePath());
	}

	importerConfig.setGeonamesZipCodeDir(tempDir.getAbsolutePath());
	importerConfig.setGeonamesFilesToDownload("FR.zip");
	geonamesZipCodeFileRetriever.setImporterConfig(importerConfig);
	List<NameValueDTO<Integer>> list = geonamesZipCodeFileRetriever.rollback();
	assertEquals(1, list.size());
	assertFalse("The importable file should have been deleted", file
		.exists());

	// delete temp dir
	assertTrue("The tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));

    }
    
    @Test
    public void shouldBeSkipShouldReturnCorrectValue(){
	ImporterConfig importerConfig = new ImporterConfig();
	GeonamesZipCodeFileRetriever geonamesZipCodeFileRetriever = new GeonamesZipCodeFileRetriever();
	geonamesZipCodeFileRetriever.setImporterConfig(importerConfig);
	
	importerConfig.setGeonamesImporterEnabled(false);
	Assert.assertTrue(geonamesZipCodeFileRetriever.shouldBeSkipped());
	
	importerConfig.setGeonamesImporterEnabled(true);
	Assert.assertFalse(geonamesZipCodeFileRetriever.shouldBeSkipped());
	
    }
    
    @Test
    public void getFilesToDownloadShouldReturnTheImporterConfigOption(){
	ImporterConfig importerConfig = new ImporterConfig();
	String fileTobeDownload = "ZW.zip";
	List<String> filesToDownload =new ArrayList<String>();
	filesToDownload.add(fileTobeDownload);
	importerConfig.setGeonamesFilesToDownload(fileTobeDownload);
	GeonamesZipCodeFileRetriever geonamesZipCodeFileRetriever = new GeonamesZipCodeFileRetriever();
	geonamesZipCodeFileRetriever.setImporterConfig(importerConfig);
	Assert.assertEquals("getFilesToDownload should return the importerConfig Option",filesToDownload, geonamesZipCodeFileRetriever.getFilesToDownload());
    }
    
    @Test
    public void processWithNotExistingFiles() {
	GeonamesZipCodeFileRetriever geonamesZipCodeFileRetriever = new GeonamesZipCodeFileRetriever();
	geonamesZipCodeFileRetriever.setInternationalisationService(createMockInternationalisationService());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setGeonamesZipCodeDownloadURL("http://download.geonames.org/export/zip/");
	
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());

	// get files to download
	List<String> filesToDownload =new ArrayList<String>();
	String fileTobeDownload = "NotExistingFile.zip";
	filesToDownload.add(fileTobeDownload);
	importerConfig.setGeonamesFilesToDownload(fileTobeDownload);
	importerConfig.setRetrieveFiles(true);

	importerConfig.setGeonamesZipCodeDir(tempDir.getAbsolutePath());
	geonamesZipCodeFileRetriever.setImporterConfig(importerConfig);


	    geonamesZipCodeFileRetriever.process();
	// delete temp dir
	assertTrue("The tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));


    }
    
    
    @Test
    public void process() {
	GeonamesZipCodeFileRetriever geonamesZipCodeFileRetriever = new GeonamesZipCodeFileRetriever();
	geonamesZipCodeFileRetriever.setInternationalisationService(createMockInternationalisationService());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setGeonamesZipCodeDownloadURL("http://download.geonames.org/export/zip/");
	
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());

	// get files to download
	List<String> filesToDownload =new ArrayList<String>();
	String fileTobeDownload = "AD.zip";
	filesToDownload.add(fileTobeDownload);
	importerConfig.setGeonamesFilesToDownload(fileTobeDownload);
	importerConfig.setRetrieveFiles(true);

	importerConfig.setGeonamesZipCodeDir(tempDir.getAbsolutePath());

	// check that the directory is ending with the / or \ according to the
	// System
	Assert.assertTrue("geonamesZipDir must ends with" + File.separator,
		importerConfig.getGeonamesZipCodeDir().endsWith(File.separator));
	
	geonamesZipCodeFileRetriever.setImporterConfig(importerConfig);
	geonamesZipCodeFileRetriever.process();

	// check that geonamesDownloadURL ends with '/' : normally "/" is added
	// if not
	Assert.assertTrue("GeonamesDownloadURL must ends with '/' but was "
		+ importerConfig.getGeonamesZipCodeDownloadURL(), importerConfig
		.getGeonamesZipCodeDir().endsWith("/"));

	// check that files have been Downloaded
	File file = null;
	for (String fileToDownload : filesToDownload) {
	    file = new File(importerConfig.getGeonamesZipCodeDir() + fileToDownload);
	    if (importerConfig.isRetrieveFiles()) {
		Assert.assertTrue("Le fichier " + fileToDownload
			+ " have not been downloaded in "
			+ importerConfig.getGeonamesZipCodeDir(), file.exists());
	    } else {
		Assert.assertFalse("Le fichier " + fileToDownload
			+ " have been downloaded in "
			+ importerConfig.getGeonamesZipCodeDir()
			+ " even if the option retrievefile is"
			+ importerConfig.isRetrieveFiles(), file.exists());
	    }
	}

	// check that files have been unzip
	for (String fileToDownload : filesToDownload) {
	    String fileNameWithExtension = fileToDownload.substring(0,
		    (fileToDownload.length()) - 3)
		    + "txt";
	    file = new File(importerConfig.getGeonamesZipCodeDir()
		    + fileNameWithExtension);
	    if (importerConfig.isRetrieveFiles()) {
		Assert.assertTrue("Le fichier " + fileNameWithExtension
			+ " have not been decompress in "
			+ importerConfig.getGeonamesZipCodeDir(), file.exists());
	    } else {
		Assert.assertFalse("Le fichier " + fileToDownload
			+ " have been unzip in "
			+ importerConfig.getGeonamesZipCodeDir()
			+ " even if the option retrievefile is"
			+ importerConfig.isRetrieveFiles(), file.exists());
	    }
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

    

}
