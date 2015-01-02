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

import static com.gisgraphy.test.GisgraphyTestHelper.alternateOsmNameContains;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.GisFeatureDistanceFactory;
import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.street.StreetType;
import com.vividsolutions.jts.geom.Point;



public class OpenStreetMapSimpleImporterTest extends AbstractIntegrationHttpSolrTestCase {
    
    private IImporterProcessor openStreetMapImporter;
    
    private IOpenStreetMapDao openStreetMapDao;
    
    private IIdGenerator idGenerator;
    
    static boolean setupIsCalled = false;
    
    @Test
    public void testSetup(){
    	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
    	IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	idGenerator.sync();
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
    	
    	importer.setup();
    	EasyMock.verify(idGenerator);
    }
  
    @Test
    public void testRollback() throws Exception {
    	OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
    	IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
    	EasyMock.expect(openStreetMapDao.getPersistenceClass()).andReturn(OpenStreetMap.class);
    	EasyMock.expect(openStreetMapDao.deleteAll()).andReturn(5);
    	EasyMock.replay(openStreetMapDao);
    	openStreetMapImporter.setOpenStreetMapDao(openStreetMapDao);
    	List<NameValueDTO<Integer>> deleted = openStreetMapImporter
    		.rollback();
    	assertEquals(1, deleted.size());
    	assertEquals(5, deleted.get(0).getValue().intValue());
	}
    
    @Test
    public void testImporterShouldImport() throws InterruptedException{
	System.out.println(openStreetMapImporter.getClass());
	openStreetMapImporter.process();
	/*if (openStreetMapImporter.getClass() != OpenStreetMapImporter.class){
	    Thread.sleep(10000L);
	}*/
	assertEquals(4L,openStreetMapDao.count());
	openStreetMapDao.getAll();
	Long firstIdAssigned = (idGenerator.getGid()-4+1);
	OpenStreetMap openStreetMap = openStreetMapDao.getByGid(firstIdAssigned);
	assertTrue("The oneWay attribute is not correct",openStreetMap.isOneWay());
	assertEquals("The countryCode is not correct ","FR",openStreetMap.getCountryCode());
	assertEquals("The is_in is not correct ","a city",openStreetMap.getIsIn());
	assertEquals("The openstreetmapId is not correct ",new Long(11),openStreetMap.getOpenstreetmapId());
	assertEquals("The streetType is not correct",StreetType.RESIDENTIAL, openStreetMap.getStreetType());
	assertEquals("The name is not correct","Bachlettenstrasse", openStreetMap.getName());
	assertEquals("The location->X is not correct ",((Point)GeolocHelper.convertFromHEXEWKBToGeometry("010100000006C82291A0521E4054CC39B16BC64740")).getX(), openStreetMap.getLocation().getX());
	assertEquals("The location->Y is not correct ",((Point)GeolocHelper.convertFromHEXEWKBToGeometry("010100000006C82291A0521E4054CC39B16BC64740")).getY(), openStreetMap.getLocation().getY());
	assertEquals("The length is not correct",0.00142246604529, openStreetMap.getLength());
	assertEquals("The shape is not correct ",GeolocHelper.convertFromHEXEWKBToGeometry("01020000000200000009B254CD6218024038E22428D9EF484075C93846B217024090A8AB96CFEF4840").toString(), openStreetMap.getShape().toString());
	
	//check alternate names when there is 2
	Assert.assertEquals(2, openStreetMap.getAlternateNames().size());
	Assert.assertTrue(alternateNamesContain(openStreetMap.getAlternateNames(),"Rue de Bachlettenstrasse"));
	Assert.assertTrue(alternateNamesContain(openStreetMap.getAlternateNames(),"Bachletten strasse"));
	
	//check alternate names when there is no name but alternate
	openStreetMap = openStreetMapDao.getByGid(firstIdAssigned+1);
		Assert.assertEquals(1, openStreetMap.getAlternateNames().size());
		Assert.assertEquals("When there is no name and some alternatename, the first alternatename is set to name ","noName BUT an alternate",openStreetMap.getName());
		Assert.assertTrue(alternateNamesContain(openStreetMap.getAlternateNames(),"other an"));
		
		//one alternate name
		openStreetMap = openStreetMapDao.getByGid(firstIdAssigned+2);
		Assert.assertEquals(1, openStreetMap.getAlternateNames().size());
		Assert.assertTrue(alternateNamesContain(openStreetMap.getAlternateNames(),"Friedhof"));
		
		//no alternate names
		openStreetMap = openStreetMapDao.getByGid(firstIdAssigned+3);
		Assert.assertEquals(0, openStreetMap.getAlternateNames().size());
    }

    private boolean alternateNamesContain(List<AlternateOsmName> names, String name){
    	if (names!=null ){
    		for(AlternateOsmName nameToTest:names){
    			if (nameToTest!=null && nameToTest.getName().equals(name)){
    				return true;
    			}
    		}
    	} else {
    		return false;
    	}
    	Assert.fail("alternateNames doesn't contain "+name);
    	return false;
    }
    
    @Test
	public void populateAlternateNames_nameWithCommaOrSemiColumn() {
		String RawAlternateNames="Karl-Franzens-Universität Graz___Cheka Jedid,Chekia Atiq:Chekia Jedide;Chekia Jedidé";
		OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
		OpenStreetMap street = new OpenStreetMap();
		street = importer.populateAlternateNames(street, RawAlternateNames);
		Assert.assertEquals(5, street.getAlternateNames().size());
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Karl-Franzens-Universität Graz"));
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Cheka Jedid"));
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Chekia Atiq"));
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Chekia Jedide"));
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Chekia Jedidé"));
		
		Iterator<AlternateOsmName> iterator = street.getAlternateNames().iterator();
		while (iterator.hasNext()){
			Assert.assertEquals(AlternateNameSource.OPENSTREETMAP,iterator.next().getSource());
		}
		
	}
   
    
    @Test
    public void testShouldBeSkipShouldReturnCorrectValue(){
	ImporterConfig importerConfig = new ImporterConfig();
	OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
	openStreetMapImporter.setImporterConfig(importerConfig);
	
	importerConfig.setOpenstreetmapImporterEnabled(false);
	Assert.assertTrue(openStreetMapImporter.shouldBeSkipped());
	
	importerConfig.setOpenstreetmapImporterEnabled(true);
	Assert.assertFalse(openStreetMapImporter.shouldBeSkipped());
		
    }
    
	@Test
	public void testshouldFillIsInFieldShouldReturnCorrectValue() {
		ImporterConfig importerConfig = new ImporterConfig();
		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
		openStreetMapImporter.setImporterConfig(importerConfig);

		importerConfig.setGeonamesImporterEnabled(true);
		importerConfig.setOpenStreetMapFillIsIn(true);
		Assert.assertTrue(openStreetMapImporter.shouldFillIsInField());

		importerConfig.setGeonamesImporterEnabled(true);
		importerConfig.setOpenStreetMapFillIsIn(false);
		Assert.assertFalse(openStreetMapImporter.shouldFillIsInField());

		importerConfig.setGeonamesImporterEnabled(false);
		importerConfig.setOpenStreetMapFillIsIn(true);
		Assert.assertFalse(openStreetMapImporter.shouldFillIsInField());

		importerConfig.setGeonamesImporterEnabled(false);
		importerConfig.setOpenStreetMapFillIsIn(false);
		Assert.assertFalse(openStreetMapImporter.shouldFillIsInField());

	}
	
	@Test
	public void testGetNearestCity(){
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setGeonamesImporterEnabled(true);
		importerConfig.setOpenStreetMapFillIsIn(true);
		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
		openStreetMapImporter.setImporterConfig(importerConfig);
		final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setName(cityName);
		city.setPopulation(population);
		
		ICityDao citydao = EasyMock.createMock(ICityDao.class);
		Point location= GeolocHelper.createPoint(2F, 3F);
		String countryCode ="FR";
		EasyMock.expect(citydao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.replay(citydao);
		
		openStreetMapImporter.setCityDao(citydao);
		
		City actual = openStreetMapImporter.getNearestCity(location,countryCode,false);
		Assert.assertEquals(cityName, actual.getName());
		Assert.assertEquals(population, actual.getPopulation());
		EasyMock.verify(citydao);
		
	}
	
	@Test
	public void testGetNearestCity_filterMunicipality(){
		ImporterConfig importerConfig = new ImporterConfig();
		importerConfig.setGeonamesImporterEnabled(true);
		importerConfig.setOpenStreetMapFillIsIn(true);
		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
		openStreetMapImporter.setImporterConfig(importerConfig);
		final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setName(cityName);
		city.setPopulation(population);
		
		ICityDao citydao = EasyMock.createMock(ICityDao.class);
		Point location= GeolocHelper.createPoint(2F, 3F);
		String countryCode ="FR";
		EasyMock.expect(citydao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.replay(citydao);
		
		openStreetMapImporter.setCityDao(citydao);
		
		City actual = openStreetMapImporter.getNearestCity(location,countryCode,true);
		Assert.assertEquals(cityName, actual.getName());
		Assert.assertEquals(population, actual.getPopulation());
		EasyMock.verify(citydao);
		
	}
	
	@Test
	public void getDeeperAdmName(){
		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
		City city = new City();
		city.setAdm5Name("adm5Name");
		city.setAdm4Name("adm4Name");
		city.setAdm3Name("adm3Name");
		city.setAdm2Name("adm2Name");
		city.setAdm1Name("adm1Name");
		GisFeatureDistanceFactory factory = new GisFeatureDistanceFactory();
		Assert.assertEquals("adm5Name",openStreetMapImporter.getDeeperAdmName(city));
		
		city.setAdm5Name(null);
		Assert.assertEquals("adm4Name",openStreetMapImporter.getDeeperAdmName(city));
		
		city.setAdm4Name(null);
		Assert.assertEquals("adm3Name",openStreetMapImporter.getDeeperAdmName(city));
		
		city.setAdm3Name(null);
		Assert.assertEquals("adm2Name",openStreetMapImporter.getDeeperAdmName(city));
		
		city.setAdm2Name(null);
		Assert.assertEquals("adm1Name",openStreetMapImporter.getDeeperAdmName(city));
		
		city.setAdm1Name(null);
		Assert.assertNull(openStreetMapImporter.getDeeperAdmName(city));
		
	}
	 @Test
	    public void testProcessLineWithoutAlternateNames(){
		String line = "		010100000029F2C9850F79E4BFFCAEFE473CE14740	19406.7343711266	FR	8257014	road	false	BADSHAPE\t";
		OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
	 }
	
    
    @Test
    public void testProcessLineWithBadShapeShouldNotTryToSaveLine(){
	String line = "		010100000029F2C9850F79E4BFFCAEFE473CE14740	19406.7343711266	FR	8257014	road	false	BADSHAPE\tfoo";
	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
	IOpenStreetMapDao dao = EasyMock.createMock(IOpenStreetMapDao.class);
	//now we simulate the fact that the dao should not be called
	EasyMock.expect(dao.save((OpenStreetMap)EasyMock.anyObject())).andThrow(new RuntimeException());
	EasyMock.replay(dao);
	importer.setOpenStreetMapDao(dao);
	importer.processData(line);
	//EasyMock.verify(idGenerator);
    }
    
    @Test
    public void testImportWithErrors(){
	OpenStreetMapSimpleImporter importer = createImporterThatThrows();
	try {
	    importer.process();
	    fail("The import should have failed");
	} catch (Exception ignore) {
	    //ok
	}
	Assert.assertNotNull("status message is not set",importer.getStatusMessage() );
	Assert.assertFalse("status message should not be empty",importer.getStatusMessage().trim().length()==0 );
	Assert.assertEquals(ImporterStatus.ERROR, importer.getStatus());
    }

    private OpenStreetMapSimpleImporter createImporterThatThrows() {
	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter(){
	    @Override
	    public boolean shouldBeSkipped() {
	        return false;
	    }
	    
	    @Override
	    public long getNumberOfLinesToProcess() {
	        return 2L;
	    }
	    
	    @Override
	    protected void tearDown() {
	       return;
	    }
	};
	
	//ImporterConfig config = new ImporterConfig();
	//config.setOpenStreetMapDir(this.openStreetMapImporter.importerConfig.getOpenStreetMapDir());
	IOpenStreetMapDao dao = EasyMock.createNiceMock(IOpenStreetMapDao.class);
	//now we simulate the fact that the dao should not be called
	EasyMock.expect(dao.save((OpenStreetMap)EasyMock.anyObject())).andThrow(new RuntimeException("message"));
	EasyMock.replay(dao);
	importer.setOpenStreetMapDao(dao);
	importer.setImporterConfig(new ImporterConfig());
	//importer.setTransactionManager(openStreetMapImporter.transactionManager);
	return importer;
    }
    
   
    
    @Test
    public void testSetupIsCalled(){
    	
    	OpenStreetMapSimpleImporterTest.setupIsCalled = false;
    	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter(){
    		@Override
    		protected void setup() {
    			OpenStreetMapSimpleImporterTest.setupIsCalled = true;
    		}
    		@Override
    		protected void tearDown() {
    			return;
    		}
    		
    		@Override
    		public long getNumberOfLinesToProcess() {
    			return 0;
    		}
    		
    		@Override
    		public boolean shouldBeSkipped() {
    			return false;
    		}
    		
    		@Override
    		protected File[] getFiles() {
    			return new File[]{};
    		}
    	};
    	importer.process();
    	assertTrue(OpenStreetMapSimpleImporterTest.setupIsCalled);
    }
    
    @Test
    public void testSetIsInFields_first_null_second_ok(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final String adm2name= "adm2name";
		final City city = new City();
		city.setPopulation(population);
		city.setAdm2Name(adm2name);
		city.setName(cityName);
		city.setFeatureId(1L);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		Point location= GeolocHelper.createPoint(2F, 3F);
		
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);
		
    	String countryCode = "FR";
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
		EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(null);
		EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.replay(cityDao);
		openStreetMapSimpleImporter.setCityDao(cityDao);
    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("adm2name", street.getIsInAdm());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals(null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    @Test
    public void testSetIsInFields_both_ok_same_id(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final String adm2name= "adm2name";
		final City city = new City();
		city.setPopulation(population);
		city.setAdm2Name(adm2name);
		city.setName(cityName);
		city.setMunicipality(false);
		city.setFeatureId(1L);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		
		final String  cityName2= "cityName2";
		final Integer population2 = 456;
		final String adm2name2= "adm2name2";
		final City city2 = new City();
		city2.setPopulation(population2);
		city2.setAdm2Name(adm2name2);
		city2.setName(cityName2);
		city2.setFeatureId(1L);
		final Set<ZipCode> zipCodes2 = new HashSet<ZipCode>();
		zipCodes2.add(new ZipCode("zip2"));
		city2.addZipCodes(zipCodes2);
		
		
		Point location= GeolocHelper.createPoint(2F, 3F);
		
    	String countryCode = "FR";
    	
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city2);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);

    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("adm2name", street.getIsInAdm());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals(null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    
    @Test
    public void testSetIsInFields_both_ok_different_id_municipality_far(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final String adm2name= "adm2name";
		final City city = new City();
		city.setPopulation(population);
		city.setAdm2Name(adm2name);
		city.setName(cityName);
		city.setFeatureId(1L);
		city.setMunicipality(false);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		city.setLocation(GeolocHelper.createPoint(4F, 5F));
		
		final String  cityName2= "cityName2";
		final Integer population2 = 456;
		final String adm2name2= "adm2name2";
		final City city2 = new City();
		city2.setPopulation(population2);
		city2.setAdm2Name(adm2name2);
		city2.setName(cityName2);
		city2.setFeatureId(2L);
		final Set<ZipCode> zipCodes2 = new HashSet<ZipCode>();
		zipCodes2.add(new ZipCode("zip2"));
		city2.addZipCodes(zipCodes2);
		city2.setLocation(GeolocHelper.createPoint(2.1F, 5.1F));
		
		Point location= GeolocHelper.createPoint(2F, 3F);
    	String countryCode = "FR";
    	
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
		EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city2);
		EasyMock.replay(cityDao);
		openStreetMapSimpleImporter.setCityDao(cityDao);
    	    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	expectedZip.add("ZIP2");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("adm2name", street.getIsInAdm());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals("isIn place should be filled if result are different and municipality is not the nearest",cityName2, street.getIsInPlace());
    	Assert.assertEquals("isIn should be filled with municipality if result are different and municipality is not the nearest",cityName, street.getIsIn());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    
    
    
    @Test
    public void testSetIsInFields_both_ok_different_id_municipality_near(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final String adm2name= "adm2name";
		final City city = new City();
		city.setFeatureId(1L);
		city.setPopulation(population);
		city.setAdm2Name(adm2name);
		city.setName(cityName);
		city.setMunicipality(false);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		city.setLocation(GeolocHelper.createPoint(2.1F, 5.1F));
		
		final String  cityName2= "cityName2";
		final Integer population2 = 456;
		final String adm2name2= "adm2name2";
		final City city2 = new City();
		city2.setFeatureId(2L);
		city2.setPopulation(population2);
		city2.setAdm2Name(adm2name2);
		city2.setName(cityName2);
		final Set<ZipCode> zipCodes2 = new HashSet<ZipCode>();
		zipCodes2.add(new ZipCode("zip2"));
		city2.addZipCodes(zipCodes2);
		city2.setLocation(GeolocHelper.createPoint(4F, 5F));
		
		
		Point location= GeolocHelper.createPoint(2F, 3F);
		
    	String countryCode = "FR";
    	
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city2);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);

    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("adm2name", street.getIsInAdm());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals("isIn should not be filled with only isin if result are different and municipality is the nearest",cityName, street.getIsIn());
    	Assert.assertEquals("isIn place should not be filled if result are different and municipality is the nearest",null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    
    @Test
    public void testSetIsInFields_first_ok_second_null(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final String adm2name= "adm2name";
		final City city = new City();
		city.setPopulation(population);
		city.setAdm2Name(adm2name);
		city.setName(cityName);
		city.setMunicipality(false);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		Point location= GeolocHelper.createPoint(2F, 3F);
		
    	String countryCode = "FR";
    	
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);

    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(null);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);
    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("adm2name", street.getIsInAdm());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals(null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    @Test
    public void testSetIsInFields_first_ok_second_null_isInAlreadyFilled(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final String adm2name= "adm2name";
		final City city = new City();
		city.setPopulation(population);
		city.setAdm2Name(adm2name);
		city.setName(cityName);
		city.setMunicipality(false);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		Point location= GeolocHelper.createPoint(2F, 3F);
		
    	String countryCode = "FR";
    	
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);

    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(null);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);
    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	street.setIsIn("AlreadyFilled");
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("adm2name", street.getIsInAdm());
    	Assert.assertEquals("AlreadyFilled", street.getIsIn());
    	Assert.assertEquals(null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    
    @Test
    public void testSetIsInFields_GetByShape(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	Point location= GeolocHelper.createPoint(2F, 3F);
	
		
    	String countryCode = "FR";
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	City cityByShape= new City();
    	cityByShape.addZipCode(new ZipCode("zip"));
    	cityByShape.setName("name");
    	cityByShape.setPopulation(1000000);
    	Adm adm = new Adm(2);
    	adm.setName("admName");
    	cityByShape.setAdm(adm);
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(cityByShape);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);

    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals(true, street.isCityConfident());
    	Assert.assertEquals("admName", street.getIsInAdm());
    	Assert.assertEquals("name", street.getIsIn());
    	Assert.assertEquals(null, street.getIsInPlace());
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    @Test
    public void testSetIsInFields_both_null(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
		final City city = new City();
		city.setMunicipality(false);
		final List<ZipCode> zipCodes = new ArrayList<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		Point location= GeolocHelper.createPoint(2F, 3F);
		
    	String countryCode = "FR";
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(null);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);

    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
    	Assert.assertEquals(null, street.getIsInZip());
    	Assert.assertEquals(null, street.getIsInAdm());
    	Assert.assertEquals(null, street.getIsIn());
    	Assert.assertEquals(null, street.getIsInPlace());
    	
    	EasyMock.verify(cityDao);
    	
    }
    
	  
    @Test
    public void testPplxToPPL(){
    	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
    	Assert.assertEquals(null,importer.pplxToPPL(null));
    	Assert.assertEquals("Paris",importer.pplxToPPL("Paris"));
    	Assert.assertEquals("Paris",importer.pplxToPPL("Paris 10 Entrepôt"));
    	Assert.assertEquals("Marseille",importer.pplxToPPL("Marseille 01"));
    }
    
    
    @Required
    public void setIdGenerator(IIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
    
    
    @Required
    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }

    @Required
    public void setOpenStreetMapImporter(IImporterProcessor openStreetMapImporter) {
        this.openStreetMapImporter = openStreetMapImporter;
    }
    
  
}
