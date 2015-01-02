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

public class OpenStreetMapPoisFileRetrieverTest {

	 @Test
	    public void processShouldExtractFilesEvenIfRetrieveFileIsFalse(){
		 final List<String> methodCalled = new ArrayList<String>();
		 final String downloadFlag = "download";
		 final String decompressFlag = "decompress";
		
		OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever(){
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
		openStreetMapPoiFileRetriever.setInternationalisationService(createMockInternationalisationService());
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		openStreetMapPoiFileRetriever.process();
		Assert.assertEquals(decompressFlag, methodCalled.get(0));
		
	    }
	    
	    @Test
	    public void processShouldExtractAndDownloadFilesIfRetrieveFileIsTrue(){
		 final List<String> methodCalled = new ArrayList<String>();
		 final String downloadFlag = "download";
		 final String decompressFlag = "decompress";
		
		 OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever(){
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
		openStreetMapPoiFileRetriever.setInternationalisationService(createMockInternationalisationService());
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		openStreetMapPoiFileRetriever.process();
		Assert.assertEquals(downloadFlag, methodCalled.get(0));
		Assert.assertEquals(decompressFlag, methodCalled.get(1));
		
	    }
	    
	    @Test
	    public void processShouldDoNothingIfopenstreetmapIsDisabled(){
		 final List<String> methodCalled = new ArrayList<String>();
		 final String downloadFlag = "download";
		 final String decompressFlag = "decompress";
		
		 OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever(){
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
		openStreetMapPoiFileRetriever.setInternationalisationService(createMockInternationalisationService());
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		openStreetMapPoiFileRetriever.process();
		Assert.assertEquals(0, methodCalled.size());
		
	    }
	    
	    @Test
	    public void process() {
	    	OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever();
		openStreetMapPoiFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setOpenstreetMapPoisDownloadURL(ImporterConfigTest.GISGRAPHY_DOWNLOAD_SERVER+"/openstreetmap/version_3_0/");
		
		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass()
			.getSimpleName());

		// get files to download
		List<String> filesToDownload =new ArrayList<String>();
		String fileTobeDownload = "NU.tar.bz2";
		filesToDownload.add(fileTobeDownload);
		importerConfig.setOpenStreetMapPoisFilesToDownload(fileTobeDownload);
		importerConfig.setRetrieveFiles(true);

		importerConfig.setOpenStreetMapPoisDir(tempDir.getAbsolutePath());

		// check that the directory is ending with the / or \ according to the
		// System
		Assert.assertTrue("openstreetmap Poi dir must ends with" + File.separator,
			importerConfig.getOpenStreetMapPoisDir().endsWith(File.separator));
		
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		openStreetMapPoiFileRetriever.process();

		// check that openStreetmapURL ends with '/' : normally "/" is added
		// if not
		Assert.assertTrue("openstreetmapPoiDownloadURL must ends with '/' but was "
			+ importerConfig.getOpenstreetMapPoisDownloadURL(), importerConfig
			.getOpenstreetMapPoisDownloadURL().endsWith("/"));

		// check that files have been Downloaded
		File file = null;
		for (String fileToDownload : filesToDownload) {
		    file = new File(importerConfig.getOpenStreetMapPoisDir() + fileToDownload);
		    if (importerConfig.isRetrieveFiles()) {
			Assert.assertTrue("Le fichier " + fileToDownload
				+ " have not been downloaded in "
				+ importerConfig.getOpenStreetMapPoisDir(), file.exists());
		    } else {
			Assert.assertFalse("Le fichier " + fileToDownload
				+ " have been downloaded in "
				+ importerConfig.getOpenStreetMapPoisDir()
				+ " even if the option retrievefile is"
				+ importerConfig.isRetrieveFiles(), file.exists());
		    }
		}

		// check that files have been untar
		for (String fileToDownload : filesToDownload) {
		    String fileNameWithCSVExtension = fileToDownload.substring(0,
			    (fileToDownload.length()) - 8)
			    + ".txt";
		    file = new File(importerConfig.getOpenStreetMapPoisDir()
			    + fileNameWithCSVExtension);
		    if (importerConfig.isRetrieveFiles()) {
			Assert.assertTrue("Le fichier " + fileNameWithCSVExtension
				+ " have not been untar in "
				+ importerConfig.getOpenStreetMapPoisDir(), file.exists());
		    } else {
			Assert.assertFalse("Le fichier " + fileToDownload
				+ " have been unzip in "
				+ importerConfig.getOpenStreetMapPoisDir()
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
	    OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever();
	    openStreetMapPoiFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setOpenstreetMapPoisDownloadURL(ImporterConfigTest.GISGRAPHY_DOWNLOAD_SERVER+"/openstreetmap/version_3_0/");
		
		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass()
			.getSimpleName());

		// get files to download
		List<String> filesToDownload =new ArrayList<String>();
		String fileTobeDownload = "notExisting.bz2";
		filesToDownload.add(fileTobeDownload);
		importerConfig.setOpenStreetMapPoisFilesToDownload(fileTobeDownload);
		importerConfig.setRetrieveFiles(true);

		importerConfig.setOpenStreetMapPoisDir(tempDir.getAbsolutePath());


		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		try {
		    openStreetMapPoiFileRetriever.process();
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
	    	OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever(){
		    @Override
		    public void decompressFiles() throws IOException {
		       return;
		    }
		};
		openStreetMapPoiFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setOpenstreetmapImporterEnabled(false);
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		openStreetMapPoiFileRetriever.process();
		Assert.assertEquals(ImporterStatus.SKIPPED, openStreetMapPoiFileRetriever.getStatus());
		ImporterStatusDto statusDto = new ImporterStatusDto(openStreetMapPoiFileRetriever);
		Assert.assertEquals(0, statusDto.getPercent());
	    }
	    
	    @Test
	    public void StatusShouldBeEqualsToPROCESSEDIfNoError(){
	    	OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever();
		openStreetMapPoiFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = EasyMock.createMock(ImporterConfig.class);
		EasyMock.expect(importerConfig.isRetrieveFiles()).andReturn(true).times(2);
		EasyMock.expect(importerConfig.isOpenstreetmapImporterEnabled()).andReturn(true);
		EasyMock.expect(importerConfig.getGeonamesDownloadFilesListFromOption()).andStubReturn(new ArrayList<String>());
		EasyMock.expect(importerConfig.getOpenStreetMapPoisDir()).andStubReturn("");
		EasyMock.expect(importerConfig.getOpenstreetMapPoisDownloadURL()).andStubReturn("");
		EasyMock.expect(importerConfig.getOpenStreetMapPoisDownloadFilesListFromOption()).andStubReturn(new ArrayList<String>());
		
		EasyMock.replay(importerConfig);
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		openStreetMapPoiFileRetriever.process();
		Assert.assertEquals(ImporterStatus.PROCESSED, openStreetMapPoiFileRetriever.getStatus());
		ImporterStatusDto statusDto = new ImporterStatusDto(openStreetMapPoiFileRetriever);
		Assert.assertEquals(100, statusDto.getPercent());
	    }
	    
	    @Test
	    public void shouldBeSkipShouldReturnCorrectValue(){
		ImporterConfig importerConfig = new ImporterConfig();
		
		OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever();
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		
		importerConfig.setOpenstreetmapImporterEnabled(false);
		Assert.assertTrue(openStreetMapPoiFileRetriever.shouldBeSkipped());

		
		importerConfig.setOpenstreetmapImporterEnabled(true);
		Assert.assertFalse(openStreetMapPoiFileRetriever.shouldBeSkipped());
		
		
	    }
	    
	    @Test
	    public void getFilesToDownloadShouldReturnTheImporterConfigOption(){
		ImporterConfig importerConfig = new ImporterConfig();
		String fileTobeDownload = "AD.tar.bz2";
		List<String> filesToDownload =new ArrayList<String>();
		filesToDownload.add(fileTobeDownload);
		importerConfig.setOpenStreetMapPoisFilesToDownload(fileTobeDownload);
		OpenStreetMapPoisFileRetriever openStreetMapPoiFileRetriever = new OpenStreetMapPoisFileRetriever();
		openStreetMapPoiFileRetriever.setImporterConfig(importerConfig);
		Assert.assertEquals("getFilesToDownload should return the importerConfig Option",filesToDownload, openStreetMapPoiFileRetriever.getFilesToDownload());
		
		
	    }
	    
	    

}
