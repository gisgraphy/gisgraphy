/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.importer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.test.GisgraphyTestHelper;

public class GeonamesAlternateNamesExtracterTest {

	public boolean initFilesIsCalled = false;
	ImporterConfig importerConfig;

	@Before
	public void setup() {
		importerConfig = new ImporterConfig();
		importerConfig.setAlternateNameAdm1FileName("alternateNames-adm1.txt");
		importerConfig.setAlternateNameAdm2FileName("alternateNames-adm2.txt");
		importerConfig.setAlternateNameCountryFileName("alternateNames-country.txt");
		importerConfig.setAlternateNameFeaturesFileName("alternateNames-features.txt");
	}

	@Test
	public void testShouldBeSkipShouldReturnCorrectValue() {
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter();
		extracter.setImporterConfig(importerConfig);

		importerConfig.setGeonamesImporterEnabled(false);
		importerConfig.setImportGisFeatureEmbededAlternateNames(false);
		Assert.assertTrue(extracter.shouldBeSkipped());

		importerConfig.setGeonamesImporterEnabled(false);
		importerConfig.setImportGisFeatureEmbededAlternateNames(true);
		Assert.assertTrue(extracter.shouldBeSkipped());

		importerConfig.setGeonamesImporterEnabled(true);
		importerConfig.setImportGisFeatureEmbededAlternateNames(false);
		Assert.assertFalse(extracter.shouldBeSkipped());

		importerConfig.setGeonamesImporterEnabled(true);
		importerConfig.setImportGisFeatureEmbededAlternateNames(true);
		Assert.assertTrue(extracter.shouldBeSkipped());
	}

	@Test
	public void testLineIsAnAlternatNameForAdm2() {
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter();
		extracter.setImporterConfig(importerConfig);
		HashMap<Long, String> hashMap = new HashMap<Long, String>();
		long existingFeatureId = 3L;
		hashMap.put(existingFeatureId, "");
		extracter.adm2Map = hashMap;
		Assert.assertTrue("the method should return true when the adm2 featureid exists", extracter.lineIsAnAlternatNameForAdm2(existingFeatureId));
		Assert.assertFalse("the method should return false when the adm2 featureid doesn't exists", extracter.lineIsAnAlternatNameForAdm2(4L));
	}

	@Test
	public void testLineIsAnAlternatNameForAdm1() {
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter();
		extracter.setImporterConfig(importerConfig);
		HashMap<Long, String> hashMap = new HashMap<Long, String>();
		long existingFeatureId = 3L;
		hashMap.put(existingFeatureId, "");
		extracter.adm1Map = hashMap;
		Assert.assertTrue("the method should return true when the adm2 featureid exists", extracter.lineIsAnAlternateNameForAdm1(existingFeatureId));
		Assert.assertFalse("the method should return false when the adm2 featureid doesn't exists", extracter.lineIsAnAlternateNameForAdm1(4L));
	}

	@Test
	public void testLineIsAnAlternatNameForCountry() {
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter();
		extracter.setImporterConfig(importerConfig);
		HashMap<Long, String> hashMap = new HashMap<Long, String>();
		long existingFeatureId = 3L;
		hashMap.put(existingFeatureId, "");
		extracter.countryMap = hashMap;
		Assert.assertTrue("the method should return true when the adm2 featureid exists", extracter.lineIsAnAlternateNameForCountry(existingFeatureId));
		Assert.assertFalse("the method should return false when the adm2 featureid doesn't exists", extracter.lineIsAnAlternateNameForCountry(4L));
	}

	@Test
	public void testInitFilesShouldCreateTheFilesAndWriter() {
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter();
		extracter.setImporterConfig(importerConfig);
		// create a temporary directory to download files
		File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
		importerConfig.setGeonamesDir(tempDir.getAbsolutePath());

		extracter.initFiles();
		Assert.assertTrue("the adm1 file should have been created", extracter.adm1file.exists());
		Assert.assertNotNull("the adm1 writer should not be null", extracter.adm1fileOutputStreamWriter != null);

		Assert.assertTrue("the adm2 file should have been created", extracter.adm2file.exists());
		Assert.assertNotNull("the adm2 writer should not be null", extracter.adm2fileOutputStreamWriter != null);

		Assert.assertTrue("the country file should have been created", extracter.countryFile.exists());
		Assert.assertNotNull("the country writer should not be null", extracter.countryfileOutputStreamWriter != null);

		Assert.assertTrue("the featuresFile should have been created", extracter.featuresFile.exists());
		Assert.assertNotNull("the featuresfileOutputStreamWriter writer should not be null", extracter.featuresfileOutputStreamWriter != null);
		GisgraphyTestHelper.DeleteNonEmptyDirectory(tempDir);
	}

	@Test
	public void testSetup() {
		ICountryDao countryDao = createMockCountryDao();

		IAdmDao admDao = createMockAdmDao();

		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter() {

			@Override
			protected void initFiles() {
				initFilesIsCalled = true;
			}
		};
		extracter.setImporterConfig(importerConfig);
		extracter.setAdmDao(admDao);
		extracter.setCountryDao(countryDao);

		extracter.setup();
		assertTrue("initfiles should be called in setup", initFilesIsCalled);
		assertEquals("the adm1 map has not the expected number of element ", 2, extracter.adm1Map.size());
		assertTrue("the adm1 map is missing a value", extracter.adm1Map.containsKey(3L));
		assertTrue("the adm1 map is missing a value", extracter.adm1Map.containsKey(4L));

		assertEquals("the adm2 map has not the expected number of element ", 2, extracter.adm2Map.size());
		assertTrue("the adm2 map is missing a value", extracter.adm2Map.containsKey(5L));
		assertTrue("the adm2 map is missing a value", extracter.adm2Map.containsKey(6L));

		assertEquals("the countries map has not the expected number of element ", 2, extracter.countryMap.size());
		assertTrue("the countries map is missing a value", extracter.countryMap.containsKey(1L));
		assertTrue("the countries map is missing a value", extracter.countryMap.containsKey(2L));
		verify(countryDao);
		verify(admDao);

	}

	

	@Test
	public void testProcessDataWithNonNumericFeatureIdShouldIgnoreTheLine() {
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter();
		extracter.processData("1	nonnumeric	FR	alterantony	1	1");
	}

	@Test
	public void testProcessDataWithMissingAlternateNameIDShouldIgnoreTheLine() {
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter();
		extracter.processData("	nonnumeric	FR	alterantony	1	1");
	}

	@Test
	public void testProcessDataWithCountryFeatureID() throws IOException {
		String line = "1	1	FR	alterantony	1	1";
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter() {
			@Override
			protected boolean lineIsAnAlternateNameForCountry(Long featureId) {
				return true;
			}
		};
		OutputStreamWriter mockWriter = createMockWriter(line);
		extracter.countryfileOutputStreamWriter = mockWriter;
		extracter.setCountryDao(createMockCountryDao());
		extracter.processData(line);
		verify(mockWriter);
	}

	@Test
	public void testProcessDataWithAdm1FeatureID() throws IOException {
		String line = "1	1	FR	alterantony	1	1";
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter() {
			@Override
			protected boolean lineIsAnAlternateNameForCountry(Long featureId) {
				return false;
			}

			@Override
			protected boolean lineIsAnAlternateNameForAdm1(Long featureId) {
				return true;
			}

		};
		OutputStreamWriter mockWriter = createMockWriter(line);
		extracter.adm1fileOutputStreamWriter = mockWriter;
		extracter.setCountryDao(createMockCountryDao());
		extracter.processData(line);
		verify(mockWriter);
	}

	@Test
	public void testProcessDataWithAdm2FeatureID() throws IOException {
		String line = "1	1	FR	alterantony	1	1";
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter() {
			@Override
			protected boolean lineIsAnAlternateNameForCountry(Long featureId) {
				return false;
			}

			@Override
			protected boolean lineIsAnAlternateNameForAdm1(Long featureId) {
				return false;
			}

			@Override
			protected boolean lineIsAnAlternatNameForAdm2(Long featureId) {
				return true;
			}
		};
		OutputStreamWriter mockWriter = createMockWriter(line);
		extracter.adm2fileOutputStreamWriter = mockWriter;
		extracter.setCountryDao(createMockCountryDao());
		extracter.processData(line);
		verify(mockWriter);
	}

	@Test
	public void testProcessDataWithGenericFeatureID() throws IOException {
		String line = "1	1	FR	alterantony	1	1";
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter() {
			@Override
			protected boolean lineIsAnAlternateNameForCountry(Long featureId) {
				return false;
			}

			@Override
			protected boolean lineIsAnAlternateNameForAdm1(Long featureId) {
				return false;
			}

			@Override
			protected boolean lineIsAnAlternatNameForAdm2(Long featureId) {
				return false;
			}
		};
		OutputStreamWriter mockWriter = createMockWriter(line);
		extracter.featuresfileOutputStreamWriter = mockWriter;
		extracter.setCountryDao(createMockCountryDao());
		extracter.processData(line);
		verify(mockWriter);
	}
	
	@Test
	public void testProcessDataWithMissingField() throws IOException {
		String line = "1		FR	alterantony	1	1";
		GeonamesAlternateNamesExtracter extracter = new GeonamesAlternateNamesExtracter() {
			@Override
			protected boolean lineIsAnAlternateNameForCountry(Long featureId) {
				return false;
			}

			@Override
			protected boolean lineIsAnAlternateNameForAdm1(Long featureId) {
				return false;
			}

			@Override
			protected boolean lineIsAnAlternatNameForAdm2(Long featureId) {
				return false;
			}
		};
		
		extracter.processData(line);
	}

	private OutputStreamWriter createMockWriter(String line) throws IOException {
		OutputStreamWriter mockWriter = EasyMock.createMock(OutputStreamWriter.class);
		mockWriter.write(line);
		mockWriter.write("\r\n");
		mockWriter.flush();
		replay(mockWriter);
		return mockWriter;
	}
	
	private IAdmDao createMockAdmDao() {
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		List<Long> adm1Ids = new ArrayList<Long>();
		adm1Ids.add(3L);
		adm1Ids.add(4L);
		List<Long> adm2Ids = new ArrayList<Long>();
		adm2Ids.add(5L);
		adm2Ids.add(6L);
		expect(admDao.listFeatureIdByLevel(1)).andReturn(adm1Ids);
		expect(admDao.listFeatureIdByLevel(2)).andReturn(adm2Ids);
		replay(admDao);
		return admDao;
	}

	private ICountryDao createMockCountryDao() {
		ICountryDao countryDao = EasyMock.createMock(ICountryDao.class);
		List<Long> countriesIds = new ArrayList<Long>();
		countriesIds.add(1L);
		countriesIds.add(2L);
		expect(countryDao.listFeatureIds()).andStubReturn(countriesIds);
		replay(countryDao);
		return countryDao;
	}

}
