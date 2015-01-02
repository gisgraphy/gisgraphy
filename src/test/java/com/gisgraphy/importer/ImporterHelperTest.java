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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.test.GisgraphyTestHelper;

public class ImporterHelperTest {

	@Test
	public void testGetHTTPFileSize() {
		Assert.assertEquals(1150, ImporterHelper.getHttpFileSize("http://www.gisgraphy.com/favicon.ico"));
		Assert.assertEquals(-1, ImporterHelper.getHttpFileSize("http://www.gisgraphy.com/FileThatNotExists"));
		Assert.assertEquals(-1, ImporterHelper.getHttpFileSize("http://download.geonames.org/export/zip/notexist.zip"));
	}

	@Test
	public void virtualizeADMDshouldChangeADMDTOADMXAccordingToTheAdmcodes() {
		String[] fields = new String[19];
		fields[6] = "A";
		fields[7] = "ADMD";
		fields[10] = "A1";
		fields[11] = "B2";
		fields[12] = "C3";
		assertEquals("ADM3", ImporterHelper.virtualizeADMD(fields)[7]);
		fields[11] = null;
		fields[7] = "ADMD";
		assertEquals("ADM1", ImporterHelper.virtualizeADMD(fields)[7]);
		fields[7] = "PPL";
		assertEquals("PPL", ImporterHelper.virtualizeADMD(fields)[7]);
	}

	@Test
	public void correctLastAdmCodeIfPossibleShouldReallyCorrect() {
		String[] fieldsWrongFeatureClass = { "123", "1", "2", "3", "4", "5", "B", "ADM1", "8", "9", "", "11", "12", "13", "14", "15", "16", "17", "18" };
		String[] fieldsWrongFeatureCode = { "123", "1", "2", "3", "4", "5", "A", "PPL", "8", "9", "", "11", "12", "13", "14", "15", "16", "17", "18" };
		String[] fieldsWrongFeatureId = { "", "1", "2", "3", "4", "5", "A", "ADM1", "8", "9", "", "11", "12", "13", "14", "15", "16", "17", "18" };
		String[] fieldsAdm1 = { "123", "1", "2", "3", "4", "5", "A", "ADM1", "8", "9", "", "11", "12", "13", "14", "15", "16", "17", "18" };
		String[] fieldsAdm2 = { "123", "1", "2", "3", "4", "5", "A", "ADM2", "8", "9", "10", "", "", "", "14", "15", "16", "17", "18" };
		String[] fieldsAdm3 = { "123", "1", "2", "3", "4", "5", "A", "ADM3", "8", "9", "10", "11", "", "", "14", "15", "16", "17", "18" };
		String[] fieldsAdm4 = { "123", "1", "2", "3", "4", "5", "A", "ADM4", "8", "9", "10", "11", "12", "", "14", "15", "16", "17", "18" };
		String[] fieldsAdm1Bis = { "123", "1", "2", "3", "4", "5", "A", "ADM1", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" };
		String[] fieldsAdm2Bis = { "123", "1", "2", "3", "4", "5", "A", "ADM2", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" };
		String[] fieldsAdm3Bis = { "123", "1", "2", "3", "4", "5", "A", "ADM3", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" };
		String[] fieldsAdm4Bis = { "123", "1", "2", "3", "4", "5", "A", "ADM4", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" };

		assertTrue("".equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsWrongFeatureClass)[10]));
		assertTrue("".equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsWrongFeatureCode)[10]));
		assertTrue("".equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsWrongFeatureId)[10]));

		assertTrue(fieldsAdm1[0].equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm1)[10]));
		assertTrue(fieldsAdm2[0].equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm2)[11]));
		assertTrue(fieldsAdm3[0].equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm3)[12]));
		assertTrue(fieldsAdm4[0].equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm4)[13]));

		assertTrue("10".equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm1Bis)[10]));
		assertTrue("11".equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm2Bis)[11]));
		assertTrue("12".equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm3Bis)[12]));
		assertTrue("13".equals(ImporterHelper.correctLastAdmCodeIfPossible(fieldsAdm4Bis)[13]));

	}

	@Test
	public void compileRegexShouldCompile() {
		Pattern pattern = ImporterHelper.compileRegex("ATM|FOREST$");
		Assert.assertNotNull(pattern);
	}

	@Test
	public void compileRegexShouldReturnEmptyListForEmptyString() {
		Pattern pattern = ImporterHelper.compileRegex("");
		Assert.assertNull(pattern);
	}

	@Test
	public void compileRegexShouldReturnNullIfRegexAreIncorect() {
		Pattern pattern = ImporterHelper.compileRegex("[ci");
		Assert.assertNull(pattern);
	}

	@Test
	public void formatSecondsShouldFormat() {
		// test value and space
		assertTrue(ImporterHelper.formatSeconds(1000000).contains("277 "));
		assertTrue(ImporterHelper.formatSeconds(1000000).contains(" 46 "));
		assertTrue(ImporterHelper.formatSeconds(1000000).contains(" 40 "));
		// test display vfor value '0'
		assertTrue("minut should not be displayed if 0 : " + ImporterHelper.formatSeconds(3600), !ImporterHelper.formatSeconds(3600).contains("minut"));
		assertTrue("second should not be displayed if 0 : " + ImporterHelper.formatSeconds(3600), !ImporterHelper.formatSeconds(3600).contains("second"));
		assertTrue("hour should not be displayed if 0 : " + ImporterHelper.formatSeconds(100), !ImporterHelper.formatSeconds(100).contains(" hour "));
		// test singular and space
		assertTrue("hour should be singular when less or equals 1 hour : " + ImporterHelper.formatSeconds(3600), ImporterHelper.formatSeconds(3600).contains("hour "));
		assertTrue("minut should be singular when less or equals 1 minut : " + ImporterHelper.formatSeconds(60), ImporterHelper.formatSeconds(60).contains(" minut"));
		assertTrue("second should be singular when less or equals 1 second : " + ImporterHelper.formatSeconds(1), ImporterHelper.formatSeconds(1).contains(" second"));
		// plural and space
		assertTrue("hour should be plural when greater 1 hour : " + ImporterHelper.formatSeconds(3600), ImporterHelper.formatSeconds(7200).contains(" hours"));
		assertTrue("minut should be singular when greater 1 minut : " + ImporterHelper.formatSeconds(60), ImporterHelper.formatSeconds(120).contains(" minuts "));
		assertTrue("second should be singular when greater 1 minut : " + ImporterHelper.formatSeconds(1), ImporterHelper.formatSeconds(2).contains(" seconds"));
	}

	@Test
	public void listCountryFilesToImport() throws IOException {

		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());

		try {
			String tempDirectoryPath = tempDir.getAbsolutePath();

			File goodFilePattern1 = new File(tempDirectoryPath + File.separator + "FR.txt");
			goodFilePattern1.createNewFile();

			File goodFilePattern2 = new File(tempDirectoryPath + File.separator + "OK.txt");
			goodFilePattern2.createNewFile();

			File goodFilePatternUS = new File(tempDirectoryPath + File.separator + "US.1.txt");
			goodFilePatternUS.createNewFile();

			File goodFilePatternUSForGeonames = new File(tempDirectoryPath + File.separator + "US.txt");
			goodFilePatternUSForGeonames.createNewFile();

			File goodFilePatternUS2 = new File(tempDirectoryPath + File.separator + "US.12.txt");
			goodFilePatternUS2.createNewFile();

			File badFilePatternWithLowerCase = new File(tempDirectoryPath + File.separator + "Ko.txt");
			badFilePatternWithLowerCase.createNewFile();

			File badFilePatternWithLowerCase2 = new File(tempDirectoryPath + File.separator + "kO.txt");
			badFilePatternWithLowerCase2.createNewFile();

			File badFilePatternWithExcludedName = new File(tempDirectoryPath + File.separator + ImporterHelper.EXCLUDED_README_FILENAME);
			badFilePatternWithExcludedName.createNewFile();

			File goodFileSplitedPattern = new File(tempDirectoryPath + File.separator + "UM.11.txt");
			goodFileSplitedPattern.createNewFile();

			File goodFileUSSplitedPattern = new File(tempDirectoryPath + File.separator + "US.1.12.txt");
			goodFileUSSplitedPattern.createNewFile();

			File goodFileallcountriesSplitedPattern = new File(tempDirectoryPath + File.separator + "allCountries.11.txt");
			goodFileallcountriesSplitedPattern.createNewFile();

			File badFilePatternWithALLCountriesPattern = new File(tempDirectoryPath + File.separator + ImporterHelper.ALLCOUTRY_FILENAME);
			badFilePatternWithALLCountriesPattern.createNewFile();

			assertEquals("wrongnumber of files created ", 12, tempDir.listFiles().length);

			File[] fileToBeImported = ImporterHelper.listCountryFilesToImport(tempDirectoryPath);
			assertEquals(1, fileToBeImported.length);
			assertEquals("When " + ImporterHelper.ALLCOUTRY_FILENAME + "is present, only this file should be return", ImporterHelper.ALLCOUTRY_FILENAME, fileToBeImported[0].getName());

			assertTrue(" the ImporterHelper.ALLCOUTRY_FILENAME can not be deleted ", badFilePatternWithALLCountriesPattern.delete());

			fileToBeImported = ImporterHelper.listCountryFilesToImport(tempDirectoryPath);
			assertEquals("Only UppercaseFile with two letters and US.NUMBER.txt should be listed ", 5, fileToBeImported.length);
		} finally {
			assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
		}
	}

	@Test
	public void listSplitedFilesToImport() throws IOException {

		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());

		try {
			String tempDirectoryPath = tempDir.getAbsolutePath();

			File goodFilePattern1 = new File(tempDirectoryPath + File.separator + "FR.txt");
			goodFilePattern1.createNewFile();

			File goodFilePattern2 = new File(tempDirectoryPath + File.separator + "OK.txt");
			goodFilePattern2.createNewFile();

			File goodFilePatternUS = new File(tempDirectoryPath + File.separator + "US.1.txt");//
			goodFilePatternUS.createNewFile();

			File goodFilePatternUSForGeonames = new File(tempDirectoryPath + File.separator + "US.txt");
			goodFilePatternUSForGeonames.createNewFile();

			File goodFilePatternUS2 = new File(tempDirectoryPath + File.separator + "US.12.txt");//
			goodFilePatternUS2.createNewFile();

			File badFilePatternWithLowerCase = new File(tempDirectoryPath + File.separator + "Ko.txt");
			badFilePatternWithLowerCase.createNewFile();

			File badFilePatternWithLowerCase2 = new File(tempDirectoryPath + File.separator + "kO.txt");
			badFilePatternWithLowerCase2.createNewFile();

			File badFilePatternWithExcludedName = new File(tempDirectoryPath + File.separator + ImporterHelper.EXCLUDED_README_FILENAME);
			badFilePatternWithExcludedName.createNewFile();

			File goodFileSplitedPattern = new File(tempDirectoryPath + File.separator + "UM.11.txt");//
			goodFileSplitedPattern.createNewFile();

			File goodFileUSSplitedPattern = new File(tempDirectoryPath + File.separator + "US.1.12.txt");
			goodFileUSSplitedPattern.createNewFile();

			File goodFileallcountriesSplitedPattern = new File(tempDirectoryPath + File.separator + "allCountries.11.txt");//
			goodFileallcountriesSplitedPattern.createNewFile();

			File badFilePatternWithALLCountriesPattern = new File(tempDirectoryPath + File.separator + ImporterHelper.ALLCOUTRY_FILENAME);
			badFilePatternWithALLCountriesPattern.createNewFile();

			assertEquals("wrongnumber of files created ", 12, tempDir.listFiles().length);

			File[] fileToBeImported = ImporterHelper.listSplitedFilesToImport(tempDirectoryPath);

			assertEquals("Only spliyted files should be listed ", 4, fileToBeImported.length);
		} finally {
			assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
		}

	}
	
	@Test
	public void listQuattroshapesFilesToImport() throws IOException {

		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());

		try {
			String tempDirectoryPath = tempDir.getAbsolutePath();

			File goodFilePattern1 = new File(tempDirectoryPath + File.separator + "localities.txt");
			goodFilePattern1.createNewFile();

			assertEquals("wrongnumber of files created ", 1, tempDir.listFiles().length);

		} finally {
			assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir));
		}

	}


	@Test
	public void listCountryFilesToImportShouldNotReturnNullIfThereIsNoFile() {
		Assert.assertNotNull(ImporterHelper.listCountryFilesToImport(""));
	}

	@Test
	public void listZipFilesShouldNotReturnNullIfThereIsNoFile() {
		Assert.assertNotNull(ImporterHelper.listZipFiles(""));
	}

	@Test
	public void listTarFilesShouldNotReturnNullIfThereIsNoFile() {
		Assert.assertNotNull(ImporterHelper.listZipFiles(""));
	}

	@Test
	public void checkUrl(){
		Assert.assertTrue(ImporterHelper.checkUrl("http://www.google.com/"));
		Assert.assertFalse(ImporterHelper.checkUrl("http://www.goolge.com/notExistingUri"));
	}
	
	@Test
	public void checkUrls(){
		List<String> urls = new ArrayList<String>();
		urls.add("http://www.google.com/");
		urls.add("http://www.google.com/");
		
		Assert.assertTrue(ImporterHelper.checkUrls(urls));
		
		urls = new ArrayList<String>();
		urls.add("http://www.google.com/");
		urls.add("http://www.goolge.com/notExistingUri");
		
		Assert.assertFalse(ImporterHelper.checkUrls(urls));
	}
	
}
