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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICitySubdivisionDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.GisFeatureDistanceFactory;
import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.domain.valueobject.SpeedMode;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.street.StreetType;
import com.vividsolutions.jts.geom.Point;



public class OpenStreetMapSimpleImporterTest extends AbstractIntegrationHttpSolrTestCase
{
    
    private IImporterProcessor openStreetMapImporter;
    
    private IOpenStreetMapDao openStreetMapDao;
    
    private IIdGenerator idGenerator;
    
    static boolean setupIsCalled = false;
    
   LabelGenerator labelGenerator =  LabelGenerator.getInstance();
   
   
  
    
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
    	Assert.assertEquals(1, deleted.size());
    	Assert.assertEquals(5, deleted.get(0).getValue().intValue());
	}
    
    @Test
    public void testImporterShouldImport() throws InterruptedException{
	openStreetMapImporter.process();
	assertEquals(4L,openStreetMapDao.count());
	openStreetMapDao.getAll();
	Long firstIdAssigned = (idGenerator.getGid()-4+1);
	OpenStreetMap openStreetMap = openStreetMapDao.getByGid(firstIdAssigned);
	assertTrue("The oneWay attribute is not correct",openStreetMap.isOneWay());
	assertEquals("The countryCode is not correct ","FR",openStreetMap.getCountryCode());
	assertEquals("The is_in is not correct, we don't use osm anymore ",null,openStreetMap.getIsIn());
	assertEquals("The openstreetmapId is not correct ",new Long(11),openStreetMap.getOpenstreetmapId());
	assertEquals("The streetType is not correct",StreetType.RESIDENTIAL, openStreetMap.getStreetType());
	assertEquals("The name is not correct","Bachlettenstrasse", openStreetMap.getName());
	assertEquals("The location->X is not correct ",((Point)GeolocHelper.convertFromHEXEWKBToGeometry("010100000006C82291A0521E4054CC39B16BC64740")).getX(), openStreetMap.getLocation().getX());
	assertEquals("The location->Y is not correct ",((Point)GeolocHelper.convertFromHEXEWKBToGeometry("010100000006C82291A0521E4054CC39B16BC64740")).getY(), openStreetMap.getLocation().getY());
	assertEquals("The length is not correct",0.00142246604529, openStreetMap.getLength());
	assertEquals("The shape is not correct ",GeolocHelper.convertFromHEXEWKBToGeometry("01020000000200000009B254CD6218024038E22428D9EF484075C93846B217024090A8AB96CFEF4840").toString(), openStreetMap.getShape().toString());
	
	assertEquals("The lanes is not correct ",4, openStreetMap.getLanes().intValue());
	assertEquals("The maxspeed is not correct ","70", openStreetMap.getMaxSpeed());
	
	assertEquals("The maxspeed Backard is not correct ","30 mp/h",openStreetMap.getMaxSpeedBackward());
	
	assertEquals("The speedmode is not correct ",SpeedMode.OSM, openStreetMap.getSpeedMode());
	
	assertEquals("The surface is not correct ","asphalt", openStreetMap.getSurface());
	
	assertTrue("The toll is not correct ", openStreetMap.isToll());
	
	assertEquals("The azimuth is not correct ",100, openStreetMap.getAzimuthStart().intValue());
	assertEquals("The azimuth is not correct ",150, openStreetMap.getAzimuthEnd().intValue());
	
	assertEquals("label is not correct ",labelGenerator.generateLabel(openStreetMap), openStreetMap.getLabel());
	assertEquals("label postal is not correct ",labelGenerator.generatePostal(openStreetMap), openStreetMap.getLabelPostal());
	assertEquals("fully qualified name is not correct ",labelGenerator.getFullyQualifiedName(openStreetMap, false), openStreetMap.getFullyQualifiedName());
//	
	Assert.assertNull("alternate labels are only for fulltext ", openStreetMap.getAlternateLabels());
	
	
	
	//check alternate names when there is 2
	Assert.assertEquals(2, openStreetMap.getAlternateNames().size());
	Assert.assertTrue(alternateNamesContains(openStreetMap.getAlternateNames(),"Rue de Bachlettenstrasse","FR"));
	Assert.assertTrue(alternateNamesContains(openStreetMap.getAlternateNames(),"Bachletten strasse","DE"));
	
	//check alternate names when there is no name but alternate
	openStreetMap = openStreetMapDao.getByGid(firstIdAssigned+1);
		Assert.assertEquals(1, openStreetMap.getAlternateNames().size());
		Assert.assertEquals("When there is no name and some alternatename, the first alternatename is set to name ","noName BUT an alternate",openStreetMap.getName());
		Assert.assertTrue(alternateNamesContains(openStreetMap.getAlternateNames(),"other an","FR"));
		
		//one alternate name
		openStreetMap = openStreetMapDao.getByGid(firstIdAssigned+2);
		Assert.assertEquals(1, openStreetMap.getAlternateNames().size());
		Assert.assertTrue(alternateNamesContains(openStreetMap.getAlternateNames(),"Friedhof","DE"));
		
		//no alternate names
		openStreetMap = openStreetMapDao.getByGid(firstIdAssigned+3);
		Assert.assertEquals(0, openStreetMap.getAlternateNames().size());
    }

    private boolean alternateNamesContains(List<AlternateOsmName> names, String name,String lang){
    	if (names!=null ){
    		for(AlternateOsmName nameToTest:names){
    			if (nameToTest!=null && nameToTest.getName().equals(name) && (lang !=null && nameToTest.getLanguage().equals(lang))){
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
    public void testParseAzimuth(){
    	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
    	Assert.assertNull(importer.parseAzimuth(null));
    	Assert.assertNull(importer.parseAzimuth(""));
    	Assert.assertNull(importer.parseAzimuth("-400"));
    	Assert.assertNull(importer.parseAzimuth("toto"));
    	Assert.assertNull(importer.parseAzimuth("400"));
    	
    	Assert.assertEquals(0,importer.parseAzimuth("0").intValue());
    	Assert.assertEquals(100,importer.parseAzimuth("100").intValue());
    	Assert.assertEquals(360,importer.parseAzimuth("360").intValue());
    	
    	Assert.assertEquals(200,importer.parseAzimuth("200.58").intValue());
    }
    
    @Test
	public void testPopulateAlternateNames_nameWithCommaOrSemiColumn() {
    	String RawAlternateNames="name:fr===Cheka Jedid,Chekia Atiq:Chekia Jedide;Chekia Jedidé___name:nl===Karl-Franzens-Universität Graz";
		OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
		OpenStreetMap street = new OpenStreetMap();
		street = importer.populateAlternateNames(street, RawAlternateNames);
		Assert.assertEquals(4, street.getAlternateNames().size());
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Chekia Atiq"));
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Chekia Jedide"));
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Chekia Jedidé"));
		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Karl-Franzens-Universität Graz"));
		
		Assert.assertNotNull("street should be filled if the name is null",street.getName());
		Assert.assertEquals("Cheka Jedid", street.getName());
		
		Iterator<AlternateOsmName> iterator = street.getAlternateNames().iterator();
		while (iterator.hasNext()){
			Assert.assertEquals(AlternateNameSource.OPENSTREETMAP,iterator.next().getSource());
		}
		//----------------------------------------------
		  	 RawAlternateNames="\"alt_name===Night Fire Drive___old_name===David Evans Road___note:name===The highway signs say \"\"Night Fire ROAD,\"\" other sources say \"\"Night Fire DRIVE.\"\" Confusingly, Dawson County GIS says \"\"David Evans Road.\"\" Going with the highway signs as correct, but listing NF Drive as an alt_name and DE Road as an old_name.___source:name===survey 2014-08-30\"";
		street = new OpenStreetMap();
		street.setName("name");
   		street = importer.populateAlternateNames(street, RawAlternateNames);
   		Assert.assertEquals(2, street.getAlternateNames().size());
   		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"Night Fire Drive"));
   		Assert.assertTrue(alternateOsmNameContains(street.getAlternateNames(),"David Evans Road"));
   		
   		iterator = street.getAlternateNames().iterator();
   		while (iterator.hasNext()){
   			Assert.assertEquals(AlternateNameSource.OPENSTREETMAP,iterator.next().getSource());
   		}
   		
		
	}
    
    
    
    @Test
    public void testPopulateMaxSpeed(){
    	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
    	OpenStreetMap street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "70___20___30");
    	Assert.assertEquals("we keep the fisrt value even if there is some forward value","70", street.getMaxSpeed());
    	Assert.assertEquals("20", street.getMaxSpeedBackward());
    	Assert.assertEquals(SpeedMode.OSM, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "70___20___");
    	Assert.assertEquals("we keep the fisrt value even if there is some forward value","70", street.getMaxSpeed());
    	Assert.assertEquals("20", street.getMaxSpeedBackward());
    	Assert.assertEquals(SpeedMode.OSM, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "___20___30");
    	Assert.assertEquals("we keep the last value if there is no forward","30", street.getMaxSpeed());
    	Assert.assertEquals("20", street.getMaxSpeedBackward());
    	Assert.assertEquals(SpeedMode.OSM, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "50______");
    	Assert.assertEquals("we keep the last value if there is no forward","50", street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(SpeedMode.OSM, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "50______30");
    	Assert.assertEquals("we keep the last value if there is no forward","50", street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(SpeedMode.OSM, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "___50___30");
    	Assert.assertEquals("we keep the last value if there is no forward","30", street.getMaxSpeed());
    	Assert.assertEquals("50", street.getMaxSpeedBackward());
    	Assert.assertEquals(SpeedMode.OSM, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "50 Mp/h______30");
    	Assert.assertEquals("we keep the last value if there is no forward","50 Mp/h", street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(SpeedMode.OSM, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "______");
    	Assert.assertEquals(null, street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(null, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "");
    	Assert.assertEquals(null, street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(null, street.getSpeedMode());
    	
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, null);
    	Assert.assertEquals(null, street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(null, street.getSpeedMode());
    	
    	//no_digit_back
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "Mp/h______toto");
    	Assert.assertEquals("we don't populate if there is no digit",null, street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(null, street.getSpeedMode());
    	
    	//no_digit_speed
    	street = new OpenStreetMap();
    	importer.PopulateMaxSpeed(street, "___toto___tata");
    	Assert.assertEquals("we keep the last value if there is no forward",null, street.getMaxSpeed());
    	Assert.assertEquals(null, street.getMaxSpeedBackward());
    	Assert.assertEquals(null, street.getSpeedMode());
    	
    	
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


	}
	
//	@Test
//	public void testGetNearestCity(){
//		ImporterConfig importerConfig = new ImporterConfig();
//		importerConfig.setGeonamesImporterEnabled(true);
//		importerConfig.setOpenStreetMapFillIsIn(true);
//		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
//		openStreetMapImporter.setImporterConfig(importerConfig);
//		final String  cityName= "cityName";
//		final Integer population = 123;
//		final City city = new City();
//		city.setName(cityName);
//		city.setPopulation(population);
//		
//		ICityDao citydao = EasyMock.createMock(ICityDao.class);
//		Point location= GeolocHelper.createPoint(2F, 3F);
//		String countryCode ="FR";
//		EasyMock.expect(citydao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
//		EasyMock.replay(citydao);
//		
//		openStreetMapImporter.setCityDao(citydao);
//		
//		City actual = openStreetMapImporter.getNearestCity(location,countryCode,false);
//		Assert.assertEquals(cityName, actual.getName());
//		Assert.assertEquals(population, actual.getPopulation());
//		EasyMock.verify(citydao);
//		
//	}
	
//	@Test
//	public void testGetNearestCity_filterMunicipality(){
//		ImporterConfig importerConfig = new ImporterConfig();
//		importerConfig.setGeonamesImporterEnabled(true);
//		importerConfig.setOpenStreetMapFillIsIn(true);
//		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
//		openStreetMapImporter.setImporterConfig(importerConfig);
//		final String  cityName= "cityName";
//		final Integer population = 123;
//		final City city = new City();
//		city.setName(cityName);
//		city.setPopulation(population);
//		
//		ICityDao citydao = EasyMock.createMock(ICityDao.class);
//		Point location= GeolocHelper.createPoint(2F, 3F);
//		String countryCode ="FR";
//		EasyMock.expect(citydao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
//		EasyMock.replay(citydao);
//		
//		openStreetMapImporter.setCityDao(citydao);
//		
//		City actual = openStreetMapImporter.getNearestCity(location,countryCode,true);
//		Assert.assertEquals(cityName, actual.getName());
//		Assert.assertEquals(population, actual.getPopulation());
//		EasyMock.verify(citydao);
//		
//	}
	
	@Test
	public void getBestAdmName(){
		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
		City city = new City();
		city.setAdm5Name("adm5Name");
		city.setAdm4Name("adm4Name");
		city.setAdm3Name("adm3Name");
		city.setAdm2Name("adm2Name");
		city.setAdm1Name("adm1Name");
		
		Assert.assertEquals("adm1Name",openStreetMapImporter.getBestAdmName(city));
		
		city.setAdm1Name(null);
		Assert.assertEquals("adm2Name",openStreetMapImporter.getBestAdmName(city));
		
		city.setAdm2Name(null);
		Assert.assertEquals("adm3Name",openStreetMapImporter.getBestAdmName(city));
		
		city.setAdm3Name(null);
		Assert.assertEquals("adm4Name",openStreetMapImporter.getBestAdmName(city));
		
		city.setAdm4Name(null);
		Assert.assertEquals("adm5Name",openStreetMapImporter.getBestAdmName(city));
		
		city.setAdm5Name(null);
		Assert.assertNull(openStreetMapImporter.getBestAdmName(city));
		
	}
	
	@Test
	public void getBestAdmName_specificLevel(){
		OpenStreetMapSimpleImporter openStreetMapImporter = new OpenStreetMapSimpleImporter();
		City city = new City();
		city.setCountryCode("IT");//it has admlevel to 3
		city.setAdm5Name("adm5Name");
		city.setAdm4Name("adm4Name");
		city.setAdm3Name("adm3Name");
		city.setAdm2Name("adm2Name");
		city.setAdm1Name("adm1Name");
		
		Assert.assertEquals("adm3Name",openStreetMapImporter.getBestAdmName(city));
		
		city.setAdm3Name(null);
		Assert.assertEquals("adm1Name",openStreetMapImporter.getBestAdmName(city));
		
		city.setAdm1Name(null);
		Assert.assertNull(openStreetMapImporter.getBestAdmName(city));
		
	}
		
    
    @Test
    public void testProcessLineWithBadShapeShouldNotTryToSaveLine(){
	String line = "11\tBachlettenstrasse\t010100000006C82291A0521E4054CC39B16BC64740\t0.00142246604529\tFR\ta city\t59000\t\tresidential\ttrue\tBADSHAPE\t70___20___30\t4\tyes\tasphalt\t100\t200name:fr===Rue de Bachlettenstrasse___name:de===Bachletten strasse";
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
	    Assert.fail("The import should have failed");
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
    	Assert.assertTrue(OpenStreetMapSimpleImporterTest.setupIsCalled);
    }
    
    @Test
    public void testSetIsInFields_first_null_second_ok(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setPopulation(population);
		city.setName(cityName);
		city.setFeatureId(1L);
		city.setId(123L);
		city.setAdm1Name("adm1Name");
    	city.setAdm2Name("adm2Name");
    	city.setAdm3Name("adm3Name");
    	city.setAdm4Name("adm4Name");
    	city.setAdm5Name("adm5Name");
    	city.setMunicipality(false);
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
		List<City> cities = new ArrayList<City>();
		cities.add(city);
		EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
		//EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		EasyMock.replay(cityDao);
		openStreetMapSimpleImporter.setCityDao(cityDao);
    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(labelGenerator.getBestZipString(expectedZip), street.getZipCode());
    	
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(city), street.getIsInAdm());
    	Assert.assertEquals("adm1Name", street.getAdm1Name());
    	Assert.assertEquals("adm2Name", street.getAdm2Name());
    	Assert.assertEquals("adm3Name", street.getAdm3Name());
    	Assert.assertEquals("adm4Name", street.getAdm4Name());
    	Assert.assertEquals("adm5Name", street.getAdm5Name());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals(123L, street.getCityId().longValue());
    	Assert.assertEquals(null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    @Test
    public void testSetIsInFields_both_ok_same_id(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setPopulation(population);
		city.setName(cityName);
		city.setMunicipality(false);
		city.setFeatureId(1L);
		city.setId(123L);
		city.setAdm1Name("adm1Name");
    	city.setAdm2Name("adm2Name");
    	city.setAdm3Name("adm3Name");
    	city.setAdm4Name("adm4Name");
    	city.setAdm5Name("adm5Name");
    	city.setMunicipality(true);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		
		final String  cityName2= "cityName2";
		final Integer population2 = 456;
		final String adm2name2= "adm2name2";
		final City city2 = new City();
		city2.setMunicipality(true);
		city2.setPopulation(population2);
		city2.setAdm2Name(adm2name2);
		city2.setName(cityName2);
		city2.setFeatureId(1L);
		city2.setId(456L);
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
    	
    	List<City> cities = new ArrayList<City>();
		cities.add(city);
		cities.add(city2);
		EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
    	
    	//EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		//EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city2);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);

    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals(labelGenerator.getBestZipString(expectedZip), street.getZipCode());
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(city), street.getIsInAdm());
    	Assert.assertEquals("adm1Name", street.getAdm1Name());
    	Assert.assertEquals("adm2Name", street.getAdm2Name());
    	Assert.assertEquals("adm3Name", street.getAdm3Name());
    	Assert.assertEquals("adm4Name", street.getAdm4Name());
    	Assert.assertEquals("adm5Name", street.getAdm5Name());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals(123L, street.getCityId().longValue());
    	Assert.assertEquals(null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    
    @Test
    public void testSetIsInFields_both_ok_different_id_municipality_far(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setPopulation(population);
		city.setName(cityName);
		city.setFeatureId(1L);
		city.setId(123L);
		city.setAdm1Name("adm1NameCity");
    	city.setAdm2Name("adm2NameCity");
    	city.setAdm3Name("adm3NameCity");
    	city.setAdm4Name("adm4NameCity");
    	city.setAdm5Name("adm5NameCity");
		city.setMunicipality(true);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		city.setLocation(GeolocHelper.createPoint(4F, 5F));
		
		final String  cityName2= "cityName2";
		final Integer population2 = 456;
		final String adm2name2= "adm2name2City2";
		final City city2 = new City();
		city2.setPopulation(population2);
		city2.setAdm2Name(adm2name2);
		city2.setName(cityName2);
		city2.setFeatureId(2L);
		city2.setId(456L);
		city2.setMunicipality(false);
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
		
		List<City> cities = new ArrayList<City>();
		cities.add(city2);
		cities.add(city);
		EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
		
		
		//EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		//EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city2);
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
    	Assert.assertEquals(labelGenerator.getBestZipString(expectedZip), street.getZipCode());
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(city), street.getIsInAdm());
    	Assert.assertEquals("adm1NameCity", street.getAdm1Name());
    	Assert.assertEquals("admnames should be mixed with preference to the municipality","adm2NameCity", street.getAdm2Name());
    	Assert.assertEquals("adm3NameCity", street.getAdm3Name());
    	Assert.assertEquals("adm4NameCity", street.getAdm4Name());
    	Assert.assertEquals("adm5NameCity", street.getAdm5Name());
    	Assert.assertEquals(123L, street.getCityId().longValue());
    	Assert.assertEquals("isIn place should be filled if result are different and municipality is not the nearest",cityName2, street.getIsInPlace());
    	Assert.assertEquals("isIn should be filled with municipality if result are different and municipality is not the nearest",cityName, street.getIsIn());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    @Test
    public void testSetIsInFields_shouldNotOverrideTheZipcodeIsAlreadySpecified(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setPopulation(population);
		city.setName(cityName);
		city.setFeatureId(1L);
		city.setId(123L);
		city.setAdm1Name("adm1NameCity");
    	city.setAdm2Name("adm2NameCity");
    	city.setAdm3Name("adm3NameCity");
    	city.setAdm4Name("adm4NameCity");
    	city.setAdm5Name("adm5NameCity");
		city.setMunicipality(false);
		final Set<ZipCode> zipCodes = new HashSet<ZipCode>();
		zipCodes.add(new ZipCode("zip1"));
		city.addZipCodes(zipCodes);
		city.setLocation(GeolocHelper.createPoint(4F, 5F));
		
		final String  cityName2= "cityName2";
		final Integer population2 = 456;
		final String adm2name2= "adm2name2City2";
		final City city2 = new City();
		city2.setPopulation(population2);
		city2.setAdm2Name(adm2name2);
		city2.setName(cityName2);
		city2.setFeatureId(2L);
		city2.setId(456L);
		final Set<ZipCode> zipCodes2 = new HashSet<ZipCode>();
		zipCodes2.add(new ZipCode("zip2"));
		city2.addZipCodes(zipCodes2);
		city2.setLocation(GeolocHelper.createPoint(2.1F, 5.1F));
		city.setMunicipality(true);
		
		Point location= GeolocHelper.createPoint(2F, 3F);
    	String countryCode = "FR";
    	
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
		//EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		//EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city2);
		
		List<City> cities = new ArrayList<City>();
		cities.add(city2);
		cities.add(city);
		EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
		
		EasyMock.replay(cityDao);
		openStreetMapSimpleImporter.setCityDao(cityDao);
    	    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	street.setZipCode("alreadySetzipCode");
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	expectedZip.add("ZIP2");
    	Assert.assertEquals("if the zipcode is already set, we don't populate the isInZip",null, street.getIsInZip());
    	Assert.assertEquals("alreadySetzipCode", street.getZipCode());
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(city), street.getIsInAdm());
    	Assert.assertEquals("adm1NameCity", street.getAdm1Name());
    	Assert.assertEquals("admnames should be mixed with preference to the municipality","adm2NameCity", street.getAdm2Name());
    	Assert.assertEquals("adm3NameCity", street.getAdm3Name());
    	Assert.assertEquals("adm4NameCity", street.getAdm4Name());
    	Assert.assertEquals("adm5NameCity", street.getAdm5Name());
    	Assert.assertEquals(123L, street.getCityId().longValue());
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
		final City city = new City();
		city.setFeatureId(1L);
		city.setId(123L);
		city.setPopulation(population);
		city.setName(cityName);
		city.setAdm1Name("adm1Name");
    	city.setAdm2Name("adm2Name");
    	city.setAdm3Name("adm3Name");
    	city.setAdm4Name("adm4Name");
    	city.setAdm5Name("adm5Name");
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
		city2.setId(456L);
		city2.setPopulation(population2);
		city2.setAdm2Name(adm2name2);
		city2.setName(cityName2);
		final Set<ZipCode> zipCodes2 = new HashSet<ZipCode>();
		zipCodes2.add(new ZipCode("zip2"));
		city2.addZipCodes(zipCodes2);
		city2.setLocation(GeolocHelper.createPoint(4F, 5F));
		city.setMunicipality(true);
		
		
		Point location= GeolocHelper.createPoint(2F, 3F);
		
    	String countryCode = "FR";
    	
		AlternateName an1 = new AlternateName("an1",AlternateNameSource.OPENSTREETMAP);
		AlternateName an2 = new AlternateName("an2",AlternateNameSource.OPENSTREETMAP);
		city.addAlternateName(an1);
		city.addAlternateName(an2);
    	
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
    	//EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
		//EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city2);
    	
    	
    	List<City> cities = new ArrayList<City>();
		cities.add(city2);
		cities.add(city);
		EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
    	
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);

    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(city), street.getIsInAdm());
    	Assert.assertEquals("adm1Name", street.getAdm1Name());
    	Assert.assertEquals("adm2Name", street.getAdm2Name());
    	Assert.assertEquals("adm3Name", street.getAdm3Name());
    	Assert.assertEquals("adm4Name", street.getAdm4Name());
    	Assert.assertEquals("adm5Name", street.getAdm5Name());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals(123L, street.getCityId().longValue());
    	Assert.assertEquals("isIn should not be filled with only isin if result are different and municipality is the nearest",cityName, street.getIsIn());
    	Assert.assertEquals("isIn place should not be filled if result are different and municipality is the nearest",null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    /* this test is not possible in the real life because if we filter first the first city is to be a municipality,
     *  and if we want the second to be null, it is impossible because if we don't filter we will find 
     * at least the municipality*/
    
   @Test
    public void testSetIsInFields_first_ok_second_null(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setPopulation(population);
		city.setAdm1Name("adm1Name");
    	city.setAdm2Name("adm2Name");
    	city.setAdm3Name("adm3Name");
    	city.setAdm4Name("adm4Name");
    	city.setAdm5Name("adm5Name");
		city.setName(cityName);
		city.setMunicipality(true);
		city.setFeatureId(1L);
		city.setId(123L);
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
    	
    	List<City> cities = new ArrayList<City>();
		cities.add(city);
		EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
    	
    	
    	
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);
    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(city), street.getIsInAdm());
    	Assert.assertEquals("adm1Name", street.getAdm1Name());
    	Assert.assertEquals("adm2Name", street.getAdm2Name());
    	Assert.assertEquals("adm3Name", street.getAdm3Name());
    	Assert.assertEquals("adm4Name", street.getAdm4Name());
    	Assert.assertEquals("adm5Name", street.getAdm5Name());
    	Assert.assertEquals("cityName", street.getIsIn());
    	Assert.assertEquals(123L, street.getCityId().longValue());
    	Assert.assertEquals(null, street.getIsInPlace());
    	Assert.assertTrue(street.getIsInCityAlternateNames().size()==2);
    	
    	EasyMock.verify(cityDao);
    	
    }
    
    @Test
    public void testSetIsInFields_first_ok_second_null_isInAlreadyFilled(){
    	OpenStreetMapSimpleImporter openStreetMapSimpleImporter = new OpenStreetMapSimpleImporter();
    	
    	final String  cityName= "cityName";
		final Integer population = 123;
		final City city = new City();
		city.setPopulation(population);
		city.setAdm1Name("adm1Name");
    	city.setAdm2Name("adm2Name");
    	city.setAdm3Name("adm3Name");
    	city.setAdm4Name("adm4Name");
    	city.setAdm5Name("adm5Name");
		city.setName(cityName);
		city.setMunicipality(true);
		city.setFeatureId(1L);
		city.setId(123L);
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
    	List<City> cities = new ArrayList<City>();
    	cities.add(city);
    	EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
    	EasyMock.replay(cityDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);
    	
    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	street.setIsIn("AlreadyFilled");
    	street.setCityId(456L);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP1");
    	//Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(city), street.getIsInAdm());
    	Assert.assertEquals("adm1Name", street.getAdm1Name());
    	Assert.assertEquals("adm2Name", street.getAdm2Name());
    	Assert.assertEquals("adm3Name", street.getAdm3Name());
    	Assert.assertEquals("adm4Name", street.getAdm4Name());
    	Assert.assertEquals("adm5Name", street.getAdm5Name());
    	Assert.assertEquals("AlreadyFilled", street.getIsIn());
    	Assert.assertEquals(456L, street.getCityId().longValue());
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
    	ICitySubdivisionDao citySubdivisionDao = EasyMock.createMock(ICitySubdivisionDao.class);
    	City cityByShape= new City();
    	cityByShape.addZipCode(new ZipCode("zip"));
    	cityByShape.setName("name");
    	cityByShape.setPopulation(1000000);
    	cityByShape.setAdm1Name("adm1Name");
    	cityByShape.setAdm2Name("adm2Name");
    	cityByShape.setAdm3Name("adm3Name");
    	cityByShape.setAdm4Name("adm4Name");
    	cityByShape.setAdm5Name("adm5Name");
    	cityByShape.setFeatureId(1L);
    	cityByShape.setId(123L);
    	CitySubdivision citySubdivision = new CitySubdivision();
    	citySubdivision.setName("citySubdivisionName");
    	
    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(cityByShape);
    	EasyMock.replay(cityDao);

    	EasyMock.expect(citySubdivisionDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class))).andReturn(citySubdivision);
    	EasyMock.replay(citySubdivisionDao);
    	openStreetMapSimpleImporter.setCityDao(cityDao);
    	openStreetMapSimpleImporter.setCitySubdivisionDao(citySubdivisionDao);

    	OpenStreetMap street = new OpenStreetMap();
    	street.setCountryCode(countryCode);
    	street.setLocation(location);
    	openStreetMapSimpleImporter.setIsInFields(street);
    	
    	
     	Set<String> expectedZip =new HashSet<String>();
    	expectedZip.add("ZIP");
    	Assert.assertEquals(expectedZip, street.getIsInZip());
    	Assert.assertEquals(true, street.isCityConfident());
    	//3 is the deeper adm level we consider as the best one
    	Assert.assertEquals("isInAdm should contains the best admlevel",openStreetMapSimpleImporter.getBestAdmName(cityByShape), street.getIsInAdm());
    	Assert.assertEquals("adm1Name", street.getAdm1Name());
    	Assert.assertEquals("adm2Name", street.getAdm2Name());
    	Assert.assertEquals("adm3Name", street.getAdm3Name());
    	Assert.assertEquals("adm4Name", street.getAdm4Name());
    	Assert.assertEquals("adm5Name", street.getAdm5Name());
    	Assert.assertEquals("citySubdivisionName", street.getIsInPlace());
    	Assert.assertEquals("name", street.getIsIn());
    	Assert.assertEquals(123L, street.getCityId().longValue());
    	
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
    //	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(city);
	//	EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapSimpleImporter.DISTANCE)).andReturn(null);
    	List<City> cities = new ArrayList<City>();
		EasyMock.expect(cityDao.getNearests(location, countryCode, false,OpenStreetMapSimpleImporter.DISTANCE,10)).andReturn(cities);
    	
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
    public void testSetAdms(){
    	OpenStreetMap street = new OpenStreetMap();
    	City city = new City();
    	city.setAdm1Name("adm1Name");
    	city.setAdm2Name("adm2Name");
    	city.setAdm3Name("adm3Name");
    	city.setAdm4Name("adm4Name");
    	city.setAdm5Name("adm5Name");
    	OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
    	importer.setAdmNames(street, city);
    	Assert.assertEquals(city.getAdm1Name(), street.getAdm1Name());
    	Assert.assertEquals(city.getAdm2Name(), street.getAdm2Name());
    	Assert.assertEquals(city.getAdm3Name(), street.getAdm3Name());
    	Assert.assertEquals(city.getAdm4Name(), street.getAdm4Name());
    	Assert.assertEquals(city.getAdm5Name(), street.getAdm5Name());
    	
    	//test with some null values
    	city.setAdm1Name(null);
    	street = new OpenStreetMap();
    	importer.setAdmNames(street, city);
    	Assert.assertNull(street.getAdm1Name());
    	Assert.assertEquals(city.getAdm2Name(), street.getAdm2Name());
    	Assert.assertEquals(city.getAdm3Name(), street.getAdm3Name());
    	Assert.assertEquals(city.getAdm4Name(), street.getAdm4Name());
    	Assert.assertEquals(city.getAdm5Name(), street.getAdm5Name());
    	
    	
    	
    }
    
    @Test
    public void testGetNearestCityFromList(){
 	   List<City> cities = new ArrayList<City>();
 	   City municipality1 = new City();
 	   
 	   municipality1.setId(1L);
 	   municipality1.setMunicipality(true);
 	   
 	   City municipality2 = new City();
 	   municipality2.setId(2L);
 	   municipality2.setMunicipality(true);
 	   
 	   City notMunicipality1 = new City();
 	   notMunicipality1.setId(3L);
 	   notMunicipality1.setMunicipality(false);
 	   
 	   City municipality3 = new City();
 	   municipality3.setId(4L);
 	   municipality3.setMunicipality(true);
 	   cities.add(municipality1);
 	   cities.add(municipality2);
 	   cities.add(notMunicipality1);
 	   cities.add(municipality3);
 	   
 	   OpenStreetMapSimpleImporter importer = new OpenStreetMapSimpleImporter();
 	   
 	   City actual = importer.getNearestCityFromList(cities, false);
 	   Assert.assertEquals(1L, actual.getId().longValue());
 	   
 	    actual = importer.getNearestCityFromList(cities, true);
 	   Assert.assertEquals(1L, actual.getId().longValue());
 	   
 	   cities = new ArrayList<City>();
 	   cities.add(notMunicipality1);
 	   cities.add(municipality1);
 	   cities.add(municipality2);
 	   
 	    actual = importer.getNearestCityFromList(cities, false);
 	   Assert.assertEquals(3L, actual.getId().longValue());
 	   
 	    actual = importer.getNearestCityFromList(cities, true);
 	   Assert.assertEquals(1L, actual.getId().longValue());
 	   
 	  cities = new ArrayList<City>();
	   cities.add(notMunicipality1);
	   
	   actual = importer.getNearestCityFromList(cities, false);
 	   Assert.assertEquals(3L, actual.getId().longValue());
 	   
 	    actual = importer.getNearestCityFromList(cities, true);
 	   Assert.assertEquals(null, actual);
 	   
 	   
 	  cities = new ArrayList<City>();
	   cities.add(municipality1);
	   
	   actual = importer.getNearestCityFromList(cities, false);
 	   Assert.assertEquals(1L, actual.getId().longValue());
 	   
 	    actual = importer.getNearestCityFromList(cities, true);
 	   Assert.assertEquals(1L, actual.getId().longValue());
 	   
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
