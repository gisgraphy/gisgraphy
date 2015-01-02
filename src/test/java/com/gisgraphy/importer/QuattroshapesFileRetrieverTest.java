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

public class QuattroshapesFileRetrieverTest {

	 @Test
	    public void processShouldExtractFilesEvenIfRetrieveFileIsFalse(){
		 final List<String> methodCalled = new ArrayList<String>();
		 final String downloadFlag = "download";
		 final String decompressFlag = "decompress";
		
		 QuattroshapesFileRetriever quattroshapesFileRetriever = new QuattroshapesFileRetriever(){
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
		List<String> filesToDownload =new ArrayList<String>();
		String fileTobeDownload = "shapes.test.tar.bz2";
		filesToDownload.add(fileTobeDownload);
		importerConfig.setQuattroshapesFilesToDownload(fileTobeDownload);
		importerConfig.setQuattroshapesImporterEnabled(true);
		importerConfig.setRetrieveFiles(false);
		quattroshapesFileRetriever.setInternationalisationService(createMockInternationalisationService());
		quattroshapesFileRetriever.setImporterConfig(importerConfig);
		quattroshapesFileRetriever.process();
		Assert.assertEquals(decompressFlag, methodCalled.get(0));
		
	    }
	    
	    @Test
	    public void processShouldExtractAndDownloadFilesIfRetrieveFileIsTrue(){
		 final List<String> methodCalled = new ArrayList<String>();
		 final String downloadFlag = "download";
		 final String decompressFlag = "decompress";
		
		 QuattroshapesFileRetriever quattroshapesFileRetriever = new QuattroshapesFileRetriever(){
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
		List<String> filesToDownload =new ArrayList<String>();
		String fileTobeDownload = "shapes.test.tar.bz2";
		filesToDownload.add(fileTobeDownload);
		importerConfig.setQuattroshapesImporterEnabled(true);
		importerConfig.setRetrieveFiles(true);
		quattroshapesFileRetriever.setInternationalisationService(createMockInternationalisationService());
		quattroshapesFileRetriever.setImporterConfig(importerConfig);
		quattroshapesFileRetriever.process();
		Assert.assertEquals(downloadFlag, methodCalled.get(0));
		Assert.assertEquals(decompressFlag, methodCalled.get(1));
		
	    }
	    
	    @Test
	    public void processShouldDoNothingIfopenstreetmapIsDisabled(){
		 final List<String> methodCalled = new ArrayList<String>();
		 final String downloadFlag = "download";
		 final String decompressFlag = "decompress";
		
		 QuattroshapesFileRetriever quattroshapesFileRetriever = new QuattroshapesFileRetriever(){
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
		List<String> filesToDownload =new ArrayList<String>();
		String fileTobeDownload = "shapes.test.tar.bz2";
		filesToDownload.add(fileTobeDownload);
		importerConfig.setQuattroshapesImporterEnabled(false);
		importerConfig.setRetrieveFiles(true);
		quattroshapesFileRetriever.setInternationalisationService(createMockInternationalisationService());
		quattroshapesFileRetriever.setImporterConfig(importerConfig);
		quattroshapesFileRetriever.process();
		Assert.assertEquals(0, methodCalled.size());
		
	    }
	    
	    @Test
	    public void process() {
	    	QuattroshapesFileRetriever quattroshapesFileRetriever = new QuattroshapesFileRetriever();
		quattroshapesFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setQuattroshapesDownloadURL(ImporterConfigTest.GISGRAPHY_DOWNLOAD_SERVER2+"/quattroshapes");
		
		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass()
			.getSimpleName());

		// get files to download
		List<String> filesToDownload =new ArrayList<String>();
		String fileTobeDownload = "shapes.test.tar.bz2";
		filesToDownload.add(fileTobeDownload);
		importerConfig.setQuattroshapesFilesToDownload(fileTobeDownload);
		importerConfig.setRetrieveFiles(true);

		importerConfig.setQuattroshapesDir(tempDir.getAbsolutePath());

		// check that the directory is ending with the / or \ according to the
		// System
		Assert.assertTrue("quattroshape dir must ends with" + File.separator,
			importerConfig.getQuattroshapesDir().endsWith(File.separator));
		
		quattroshapesFileRetriever.setImporterConfig(importerConfig);
		quattroshapesFileRetriever.process();

		// check that URL ends with '/' : normally "/" is added
		// if not
		Assert.assertTrue("quattroshapesURL must ends with '/' but was "
			+ importerConfig.getQuattroshapesDownloadURL(), importerConfig
			.getQuattroshapesDownloadURL().endsWith("/"));

		// check that files have been Downloaded
		File file = null;
		for (String fileToDownload : filesToDownload) {
		    file = new File(importerConfig.getQuattroshapesDir() + fileToDownload);
		    if (importerConfig.isRetrieveFiles()) {
			Assert.assertTrue("Le fichier " + fileToDownload
				+ " have not been downloaded in "
				+ importerConfig.getQuattroshapesDir(), file.exists());
		    } else {
			Assert.assertFalse("Le fichier " + fileToDownload
				+ " have been downloaded in "
				+ importerConfig.getQuattroshapesDir()
				+ " even if the option retrievefile is"
				+ importerConfig.isRetrieveFiles(), file.exists());
		    }
		}

		// check that files have been untar
		String fileToDownload = "shape.txt";
		    file = new File(importerConfig.getQuattroshapesDir()
			    + fileToDownload);
		    if (importerConfig.isRetrieveFiles()) {
			Assert.assertTrue("Le fichier " + fileToDownload
				+ " have not been untar in "
				+ importerConfig.getQuattroshapesDir(), file.exists());
		    } else {
			Assert.assertFalse("Le fichier " + fileToDownload
				+ " have been unzip in "
				+ importerConfig.getQuattroshapesDir()
				+ " even if the option retrievefile is"
				+ importerConfig.isRetrieveFiles(), file.exists());
		    }

		// delete temp dir
		Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
			.DeleteNonEmptyDirectory(tempDir));


	    }
	    
	    
	    @Test
	    public void processWhenNotExistingFile() {
	    	QuattroshapesFileRetriever quattroshapeFileRetriever = new QuattroshapesFileRetriever();
	    quattroshapeFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setQuattroshapesDownloadURL(ImporterConfigTest.GISGRAPHY_DOWNLOAD_SERVER+"/quattroshapes");
		
		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass()
			.getSimpleName());

		// get files to download
		List<String> filesToDownload =new ArrayList<String>();
		String fileTobeDownload = "notExisting.bz2";
		filesToDownload.add(fileTobeDownload);
		importerConfig.setQuattroshapesFilesToDownload(fileTobeDownload);
		importerConfig.setRetrieveFiles(true);

		importerConfig.setQuattroshapesDir(tempDir.getAbsolutePath());


		quattroshapeFileRetriever.setImporterConfig(importerConfig);
		try {
		    quattroshapeFileRetriever.process();
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
	    	QuattroshapesFileRetriever quattroshapeFileRetriever = new QuattroshapesFileRetriever(){
		    @Override
		    public void decompressFiles() throws IOException {
		       return;
		    }
		};
		quattroshapeFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setQuattroshapesImporterEnabled(false);
		quattroshapeFileRetriever.setImporterConfig(importerConfig);
		quattroshapeFileRetriever.process();
		Assert.assertEquals(ImporterStatus.SKIPPED, quattroshapeFileRetriever.getStatus());
		ImporterStatusDto statusDto = new ImporterStatusDto(quattroshapeFileRetriever);
		Assert.assertEquals(0, statusDto.getPercent());
	    }
	    
	    @Test
	    public void StatusShouldBeEqualsToPROCESSEDIfNoError(){
	    	QuattroshapesFileRetriever quattroshapeFileRetriever = new QuattroshapesFileRetriever();
		quattroshapeFileRetriever.setInternationalisationService(createMockInternationalisationService());
		ImporterConfig importerConfig = EasyMock.createMock(ImporterConfig.class);
		EasyMock.expect(importerConfig.isRetrieveFiles()).andReturn(true).times(2);
		EasyMock.expect(importerConfig.isQuattroshapesImporterEnabled()).andReturn(true);
		EasyMock.expect(importerConfig.isGeonamesImporterEnabled()).andReturn(true);
		//EasyMock.expect(importerConfig.getGeonamesDownloadFilesListFromOption()).andStubReturn(new ArrayList<String>());
		EasyMock.expect(importerConfig.getQuattroshapesDir()).andStubReturn("");
		EasyMock.expect(importerConfig.getQuattroshapesDownloadURL()).andStubReturn("");
		EasyMock.expect(importerConfig.getQuattroshapesFilesDownloadFilesListFromOption()).andStubReturn(new ArrayList<String>());
		
		EasyMock.replay(importerConfig);
		quattroshapeFileRetriever.setImporterConfig(importerConfig);
		quattroshapeFileRetriever.process();
		Assert.assertEquals(ImporterStatus.PROCESSED, quattroshapeFileRetriever.getStatus());
		ImporterStatusDto statusDto = new ImporterStatusDto(quattroshapeFileRetriever);
		Assert.assertEquals(100, statusDto.getPercent());
	    }
	    
	    @Test
		public void shouldBeSkipped(){
			QuattroshapesFileRetriever importer = new QuattroshapesFileRetriever();
			ImporterConfig importerConfig = new ImporterConfig();
			importerConfig.setGeonamesImporterEnabled(true);
			importerConfig.setQuattroshapesImporterEnabled(true);
			importer.setImporterConfig(importerConfig);
			Assert.assertFalse(importer.shouldBeSkipped());
			
			importerConfig = new ImporterConfig();
			importerConfig.setGeonamesImporterEnabled(true);
			importerConfig.setQuattroshapesImporterEnabled(false);
			importer.setImporterConfig(importerConfig);
			Assert.assertTrue(importer.shouldBeSkipped());
			
			
			importerConfig = new ImporterConfig();
			importerConfig.setGeonamesImporterEnabled(false);
			importerConfig.setQuattroshapesImporterEnabled(true);
			importer.setImporterConfig(importerConfig);
			Assert.assertTrue(importer.shouldBeSkipped());
			
			importerConfig = new ImporterConfig();
			importerConfig.setGeonamesImporterEnabled(false);
			importerConfig.setQuattroshapesImporterEnabled(false);
			importer.setImporterConfig(importerConfig);
			Assert.assertTrue(importer.shouldBeSkipped());
		}

	    
	    @Test
	    public void getFilesToDownloadShouldReturnTheImporterConfigOption(){
		ImporterConfig importerConfig = new ImporterConfig();
		String fileTobeDownload = "shapes.tar.bz2";
		List<String> filesToDownload =new ArrayList<String>();
		filesToDownload.add(fileTobeDownload);
		importerConfig.setQuattroshapesFilesToDownload(fileTobeDownload);
		QuattroshapesFileRetriever quattroshapesFileRetriever = new QuattroshapesFileRetriever();
		quattroshapesFileRetriever.setImporterConfig(importerConfig);
		Assert.assertEquals("getFilesToDownload should return the importerConfig Option",filesToDownload, quattroshapesFileRetriever.getFilesToDownload());
		
		
	    }
	    
	    

}
