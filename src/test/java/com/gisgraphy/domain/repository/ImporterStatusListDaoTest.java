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
package com.gisgraphy.domain.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.ImporterStatusDto;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.importer.ImporterConfig;
import com.gisgraphy.test.GisgraphyTestHelper;

public class ImporterStatusListDaoTest extends TestCase {

    private static final String STATUS_MESSAGE = "message";

    private static final int TOTAL_READ_LINE = 3;

    private static final int TOTAL_LINE_TO_PROCESS = 10;

    private static final String CURRENT_FILE_NAME = "currentFileName";

    private static final String PROCESSOR_NAME = "processorName";

    private static final String PROCESSOR_NAME_2 = "processorName2";

    private static final int CURRENT_LINE = 2;

    @Test
    public void testSave() {
	ImporterStatusListDao importerstatusDao = new ImporterStatusListDao();
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setGeonamesDir(tempDir.getAbsolutePath());
	importerstatusDao.setImporterConfig(importerConfig);
	ImporterStatusDto importerStatus = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		TOTAL_LINE_TO_PROCESS, TOTAL_READ_LINE, STATUS_MESSAGE,
		ImporterStatus.PROCESSING);

	ImporterStatusDto importerStatus2 = new ImporterStatusDto(
		PROCESSOR_NAME_2, CURRENT_FILE_NAME, CURRENT_LINE,
		TOTAL_LINE_TO_PROCESS, TOTAL_READ_LINE, STATUS_MESSAGE,
		ImporterStatus.PROCESSING);
	List<ImporterStatusDto> importerStatusDtoList = new ArrayList<ImporterStatusDto>();
	importerStatusDtoList.add(importerStatus);
	importerStatusDtoList.add(importerStatus2);
	importerstatusDao.saveOrUpdate(importerStatusDtoList);
	assertTrue(new File(importerstatusDao.getSavedFilePath()).exists());
	FileReader fileReader = null;
	;
	try {
	    fileReader = new FileReader(new File(importerstatusDao
		    .getSavedFilePath()));
	} catch (FileNotFoundException e) {
	    fail(e.getMessage());
	}
	int count = 0;
	BufferedReader bufferReader = null;
	try {
	    bufferReader = new BufferedReader(fileReader);
	    String line = bufferReader.readLine();
	    while (line != null) {
		count++;
		line = bufferReader.readLine();
	    }
	} catch (IOException e) {
	    fail(e.getMessage());
	} finally {
	    if (bufferReader != null) {
		try {
		    bufferReader.close();
		} catch (IOException e) {
		    fail(e.getMessage());
		}
	    }

	}
	assertEquals(2, count);
	GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir);
    }

    @Test
    public void testGet() {
	ImporterStatusListDao importerstatusDao = new ImporterStatusListDao();
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setGeonamesDir(tempDir.getAbsolutePath());
	importerstatusDao.setImporterConfig(importerConfig);
	ImporterStatusDto importerStatus = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		TOTAL_LINE_TO_PROCESS, TOTAL_READ_LINE, STATUS_MESSAGE,
		ImporterStatus.PROCESSING);
	List<ImporterStatusDto> importerStatusDtoList = new ArrayList<ImporterStatusDto>();
	importerStatusDtoList.add(importerStatus);
	importerstatusDao.saveOrUpdate(importerStatusDtoList);
	List<ImporterStatusDto> importerStatusDtoListExpected = importerstatusDao
		.get();
	assertEquals(1, importerStatusDtoListExpected.size());
	assertEquals(importerStatus.toCSV(), importerStatusDtoListExpected.get(
		0).toCSV());
	GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir);
    }
    
    @Test
    public void testGetShouldNotThrowIfThereIsNotTheCorrectNumberOfColumn() {
	ImporterStatusListDao importerstatusDao = new ImporterStatusListDao();
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setGeonamesDir(tempDir.getAbsolutePath());
	importerstatusDao.setImporterConfig(importerConfig);
	String messageWithNewLine = String.format("Hello%sthere!",System.getProperty("line.separator"));
	ImporterStatusDto importerStatus = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		TOTAL_LINE_TO_PROCESS, TOTAL_READ_LINE, messageWithNewLine,
		ImporterStatus.PROCESSING);
	List<ImporterStatusDto> importerStatusDtoList = new ArrayList<ImporterStatusDto>();
	importerStatusDtoList.add(importerStatus);
	importerstatusDao.saveOrUpdate(importerStatusDtoList);
	List<ImporterStatusDto> importerStatusDtoListExpected = importerstatusDao
		.get();
	assertEquals(0, importerStatusDtoListExpected.size());
	GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir);
    }

    @Test
    public void testDelete() {
	ImporterStatusListDao importerstatusDao = new ImporterStatusListDao();
	// create a temporary directory to download files
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setGeonamesDir(tempDir.getAbsolutePath());
	importerstatusDao.setImporterConfig(importerConfig);
	ImporterStatusDto importerStatus = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		TOTAL_LINE_TO_PROCESS, TOTAL_READ_LINE, STATUS_MESSAGE,
		ImporterStatus.PROCESSING);
	List<ImporterStatusDto> importerStatusDtoList = new ArrayList<ImporterStatusDto>();
	importerStatusDtoList.add(importerStatus);
	importerstatusDao.saveOrUpdate(importerStatusDtoList);
	assertTrue("the delete method shoul return true for success",
		importerstatusDao.delete());
	List<ImporterStatusDto> importerStatusDtoListExpected = importerstatusDao
		.get();
	assertEquals("after deletion, no impoortersatusListShouldBe stored", 0,
		importerStatusDtoListExpected.size());
	assertTrue(
		"the delete method shoul return true even if no list were saved",
		importerstatusDao.delete());
	GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir);
    }

}
