package com.gisgraphy.importer;

import static com.gisgraphy.test.GisgraphyTestHelper.alternateNameContains;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.PostOffice;
import com.gisgraphy.domain.geoloc.entity.Religious;
import com.gisgraphy.domain.geoloc.entity.Restaurant;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.vividsolutions.jts.geom.Point;

public class OpenStreetMapPoisSimpleImporterTest {

	@Test
	public void populateAlternateNames() {
		String RawAlternateNames="Karl-Franzens-Universität Graz___Universidad de Graz___Université de Graz___Грацский университет имени Карла и Франца";
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		GisFeature poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(4, poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Karl-Franzens-Universität Graz"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Universidad de Graz"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Université de Graz"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Грацский университет имени Карла и Франца"));
		
		Iterator<AlternateName> iterator = poi.getAlternateNames().iterator();
		while (iterator.hasNext()){
			Assert.assertEquals(AlternateNameSource.OPENSTREETMAP,iterator.next().getSource());
		}
		
	}

	
	@Test
	public void populatePoiWithEmptyNames(){
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(1234L);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
    	ImporterConfig importerConfig = new ImporterConfig();
    	importerConfig.setOpenStreetMapFillIsIn(false);
    	importer.setImporterConfig(importerConfig);
		
		String line= "N\t2371848041\t\t  FR\t\t  0101000020E610000012ED743117C205407ED179E816024840\tpost_office_______________________________________";
		String[] fields = line.split("\t");
		String amenity= fields[6];
		List<GisFeature> pois = importer.createAndpopulatePoi(fields, amenity);
		Assert.assertNotNull(pois);
		Assert.assertNotNull(pois.get(0));
		Assert.assertEquals("name is null so the pois should have the placetype in camelcase",StringHelper.splitCamelCase(PostOffice.class.getSimpleName()).toLowerCase(),pois.get(0).getName());
	}
	
	@Test
	public void populatePoiWithdoublequotesNames(){
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(1234L);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
    	ImporterConfig importerConfig = new ImporterConfig();
    	importerConfig.setOpenStreetMapFillIsIn(false);
    	importer.setImporterConfig(importerConfig);
		
		String line= "N\t2371848041\t\"\"\t  FR\t\t  0101000020E610000012ED743117C205407ED179E816024840\tpost_office_______________________________________";
		String[] fields = line.split("\t");
		String amenity= fields[6];
		List<GisFeature> pois = importer.createAndpopulatePoi(fields, amenity);
		Assert.assertNotNull(pois);
		Assert.assertNotNull(pois.get(0));
		Assert.assertEquals("name is null so the pois should have the placetype in camelcase",StringHelper.splitCamelCase(PostOffice.class.getSimpleName()).toLowerCase(),pois.get(0).getName());
	}
	
	@Test
	public void populatePoi(){
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(1234L);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
    	ImporterConfig importerConfig = new ImporterConfig();
    	importerConfig.setOpenStreetMapFillIsIn(false);
    	importer.setImporterConfig(importerConfig);
		
		String line= "W\t90139043\tPfarrkirche Heiliger Johannes der Täufer\tAT\tPfarrkirche Heiliger Johannes der Täufer___Parish church Saint John Baptist\t0101000020E61000000E6D653509482C40B01EF706AB514740\tplace_of_worship__________________________________________";
		String[] fields = line.split("\t");
		String amenity= fields[6];
		List<GisFeature> pois = importer.createAndpopulatePoi(fields, amenity);
		Assert.assertEquals(1, pois.size());
		GisFeature poi = pois.get(0);
		Assert.assertEquals(90139043L, poi.getOpenstreetmapId().longValue());
		Assert.assertEquals("Pfarrkirche Heiliger Johannes der Täufer", poi.getName());
		Assert.assertEquals("AT", poi.getCountryCode());
		Assert.assertEquals("place_of_worship", poi.getAmenity());
		Assert.assertEquals(GISSource.OSM, poi.getSource());;
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, poi.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, poi.getFeatureClass());
		Assert.assertEquals(Religious.class, poi.getClass());
		Assert.assertEquals(1234L, poi.getFeatureId().longValue());
		Assert.assertNotNull(poi.getLocation());
		
		
		//an
		Assert.assertEquals(2, poi.getAlternateNames().size());
		
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Pfarrkirche Heiliger Johannes der Täufer"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Parish church Saint John Baptist"));
		
		Iterator<AlternateName> iterator = poi.getAlternateNames().iterator();
		while (iterator.hasNext()){
			Assert.assertEquals(AlternateNameSource.OPENSTREETMAP,iterator.next().getSource());
		}
		
	}
	
	
	@Test
	public void processDataWithWrongName(){
		
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		

		IGisFeatureDao gisFeatureDao = EasyMock.createMock(IGisFeatureDao.class);
		//The dao should not be called
		EasyMock.replay(gisFeatureDao);
		importer.setGisFeatureDao(gisFeatureDao);
		ImporterConfig importerConfig = new ImporterConfig();
    	importerConfig.setOpenStreetMapFillIsIn(false);
    	importer.setImporterConfig(importerConfig);
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(1234L);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
		
    	String line= "W\t90139043\tPfarrkirche Heiliger Johannes der Täufer\tAT\tPfarrkirche Heiliger Johannes der Täufer___Parish church Saint John Baptist\tfoo\tplace_of_worship";
		
		importer.processData(line);
		EasyMock.verify(gisFeatureDao);
		
	}
	
	@Test
	public void processData(){
		
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		

		IGisFeatureDao gisFeatureDao = EasyMock.createMock(IGisFeatureDao.class);
		Religious poi = new Religious();
		long featureId = 1234L;
		poi.setFeatureId(featureId);
		EasyMock.expect(gisFeatureDao.save(poi)).andReturn(poi);
		EasyMock.replay(gisFeatureDao);
		importer.setGisFeatureDao(gisFeatureDao);
		ImporterConfig importerConfig = new ImporterConfig();
    	importerConfig.setOpenStreetMapFillIsIn(false);
    	importer.setImporterConfig(importerConfig);
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(featureId);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
		
    	String line= "W\t90139043\tPfarrkirche Heiliger Johannes der Täufer\tAT\tPfarrkirche Heiliger Johannes der Täufer___Parish church Saint John Baptist\t0101000020E61000000E6D653509482C40B01EF706AB514740\tplace_of_worship";
		
		importer.processData(line);
		EasyMock.verify(gisFeatureDao);
		
	}
	
	@Test
	public void testSplitTags(){
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		String[] tags = importer.splitTags("place_of_worship");
		Assert.assertEquals(14, tags.length);
		Assert.assertEquals("place_of_worship", tags[0]);
		
		 tags = importer.splitTags("place_of_worship___f______y______u___i________________________");
		 Assert.assertEquals(14, tags.length);
		 Assert.assertEquals("place_of_worship", tags[0]);
		 Assert.assertEquals("f", tags[1]);
		 Assert.assertEquals("y", tags[3]);
		 Assert.assertEquals("u", tags[5]);
		 Assert.assertEquals("i", tags[6]);
		 
		 tags = importer.splitTags("place_of_worship___f______y______u___i_____________________toto");
		 Assert.assertEquals(14, tags.length);
		 Assert.assertEquals("place_of_worship", tags[0]);
		 Assert.assertEquals("f", tags[1]);
		 Assert.assertEquals("y", tags[3]);
		 Assert.assertEquals("u", tags[5]);
		 Assert.assertEquals("i", tags[6]);
		 Assert.assertEquals("toto", tags[13]);
		 
		 tags = importer.splitTags("_______________________________________");
		 Assert.assertEquals(14, tags.length);
		 for (int i=0;i<tags.length;i++){
			 Assert.assertEquals(null, tags[0]);
		 }
		
	}
	
	 @Test
	    public void testPplxToPPL(){
	    	OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
	    	Assert.assertEquals(null,importer.pplxToPPL(null));
	    	Assert.assertEquals("Paris",importer.pplxToPPL("Paris"));
	    	Assert.assertEquals("Paris",importer.pplxToPPL("Paris 10 Entrepôt"));
	    	Assert.assertEquals("Marseille",importer.pplxToPPL("Marseille 01"));
	    }
	 
	 @Test
		public void getDeeperAdmName(){
		 OpenStreetMapPoisSimpleImporter openStreetMapPoiImporter = new OpenStreetMapPoisSimpleImporter();
			City city = new City();
			city.setAdm5Name("adm5Name");
			city.setAdm4Name("adm4Name");
			city.setAdm3Name("adm3Name");
			city.setAdm2Name("adm2Name");
			city.setAdm1Name("adm1Name");
			Assert.assertEquals("adm5Name",openStreetMapPoiImporter.getDeeperAdmName(city));
			
			
			city.setAdm5Name(null);
			Assert.assertEquals("adm4Name",openStreetMapPoiImporter.getDeeperAdmName(city));
			
			city.setAdm4Name(null);
			Assert.assertEquals("adm3Name",openStreetMapPoiImporter.getDeeperAdmName(city));
			
			city.setAdm3Name(null);
			Assert.assertEquals("adm2Name",openStreetMapPoiImporter.getDeeperAdmName(city));
			
			city.setAdm2Name(null);
			Assert.assertEquals("adm1Name",openStreetMapPoiImporter.getDeeperAdmName(city));
			
			city.setAdm1Name(null);
			Assert.assertNull(openStreetMapPoiImporter.getDeeperAdmName(city));
			
		}
	
	 
	 
	 @Test
		public void testGetNearestCity(){
			ImporterConfig importerConfig = new ImporterConfig();
			importerConfig.setGeonamesImporterEnabled(true);
			importerConfig.setOpenStreetMapFillIsIn(true);
			OpenStreetMapPoisSimpleImporter openStreetMapPoiImporter = new OpenStreetMapPoisSimpleImporter();
			openStreetMapPoiImporter.setImporterConfig(importerConfig);
			final String  cityName= "cityName";
			final Integer population = 123;
			final City city = new City();
			city.setName(cityName);
			city.setPopulation(population);
			
			ICityDao citydao = EasyMock.createMock(ICityDao.class);
			Point location= GeolocHelper.createPoint(2F, 3F);
			String countryCode ="FR";
			EasyMock.expect(citydao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.replay(citydao);
			
			openStreetMapPoiImporter.setCityDao(citydao);
			
			City actual = openStreetMapPoiImporter.getNearestCity(location,countryCode,false);
			Assert.assertEquals(cityName, actual.getName());
			Assert.assertEquals(population, actual.getPopulation());
			EasyMock.verify(citydao);
			
		}
		
		@Test
		public void testGetNearestCity_filterMunicipality(){
			ImporterConfig importerConfig = new ImporterConfig();
			importerConfig.setGeonamesImporterEnabled(true);
			importerConfig.setOpenStreetMapFillIsIn(true);
			OpenStreetMapPoisSimpleImporter openStreetMapImporter = new OpenStreetMapPoisSimpleImporter();
			openStreetMapImporter.setImporterConfig(importerConfig);
			final String  cityName= "cityName";
			final Integer population = 123;
			final City city = new City();
			city.setName(cityName);
			city.setPopulation(population);
			
			ICityDao citydao = EasyMock.createMock(ICityDao.class);
			Point location= GeolocHelper.createPoint(2F, 3F);
			String countryCode ="FR";
			EasyMock.expect(citydao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.replay(citydao);
			
			openStreetMapImporter.setCityDao(citydao);
			
			City actual = openStreetMapImporter.getNearestCity(location,countryCode,true);
			Assert.assertEquals(cityName, actual.getName());
			Assert.assertEquals(population, actual.getPopulation());
			EasyMock.verify(citydao);
			
		}
		
	    @Test
	    public void testSetIsInFields_first_null_second_ok(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
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
			EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(null);
			EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.replay(cityDao);
			OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);
	    	
			Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	Set<String> expectedZip =new HashSet<String>();
	    	expectedZip.add("ZIP1");
	    	Assert.assertEquals(expectedZip, restaurant.getIsInZip());
	    	Assert.assertEquals("adm2name", restaurant.getIsInAdm());
	    	Assert.assertEquals("cityName", restaurant.getIsIn());
	    	Assert.assertEquals(null, restaurant.getIsInPlace());
	    	Assert.assertTrue(restaurant.getIsInCityAlternateNames().size()==2);
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }
	    
	    @Test
	    public void testSetIsInFields_both_ok_same_id(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
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
	    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city2);
	    	EasyMock.replay(cityDao);
	    	OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);

	    	Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	
	    	
	     	Set<String> expectedZip =new HashSet<String>();
	    	expectedZip.add("ZIP1");
	    	Assert.assertEquals(expectedZip, restaurant.getIsInZip());
	    	Assert.assertEquals("adm2name", restaurant.getIsInAdm());
	    	Assert.assertEquals("cityName", restaurant.getIsIn());
	    	Assert.assertEquals(null, restaurant.getIsInPlace());
	    	Assert.assertTrue(restaurant.getIsInCityAlternateNames().size()==2);
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }
	    
	    
	    @Test
	    public void testSetIsInFields_both_ok_different_id_municipality_far(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
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
			EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city2);
			EasyMock.replay(cityDao);
			OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);
	    	    	
			Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	
	     	Set<String> expectedZip =new HashSet<String>();
	    	expectedZip.add("ZIP1");
	    	expectedZip.add("ZIP2");
	    	Assert.assertEquals(expectedZip, restaurant.getIsInZip());
	    	Assert.assertEquals("adm2name", restaurant.getIsInAdm());
	    	Assert.assertEquals("cityName", restaurant.getIsIn());
	    	Assert.assertEquals("isIn place should be filled if result are different and municipality is not the nearest",cityName2, restaurant.getIsInPlace());
	    	Assert.assertEquals("isIn should be filled with municipality if result are different and municipality is not the nearest",cityName, restaurant.getIsIn());
	    	Assert.assertTrue(restaurant.getIsInCityAlternateNames().size()==2);
	    	
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }
	    
	    
	    
	    
	    @Test
	    public void testSetIsInFields_both_ok_different_id_municipality_near(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
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
	    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city2);
	    	EasyMock.replay(cityDao);
	    	OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);

	    	Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	
	    	
	     	Set<String> expectedZip =new HashSet<String>();
	    	expectedZip.add("ZIP1");
	    	Assert.assertEquals(expectedZip, restaurant.getIsInZip());
	    	Assert.assertEquals("adm2name", restaurant.getIsInAdm());
	    	Assert.assertEquals("cityName", restaurant.getIsIn());
	    	Assert.assertEquals("isIn should not be filled with only isin if result are different and municipality is the nearest",cityName, restaurant.getIsIn());
	    	Assert.assertEquals("isIn place should not be filled if result are different and municipality is the nearest",null, restaurant.getIsInPlace());
	    	Assert.assertTrue(restaurant.getIsInCityAlternateNames().size()==2);
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }
	    
	    
	    @Test
	    public void testSetIsInFields_first_ok_second_null(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
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
	    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(null);
	    	EasyMock.replay(cityDao);
	    	OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);
	    	
	    	Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	
	    	
	     	Set<String> expectedZip =new HashSet<String>();
	    	expectedZip.add("ZIP1");
	    	Assert.assertEquals(expectedZip, restaurant.getIsInZip());
	    	Assert.assertEquals("adm2name", restaurant.getIsInAdm());
	    	Assert.assertEquals("cityName", restaurant.getIsIn());
	    	Assert.assertEquals(null, restaurant.getIsInPlace());
	    	Assert.assertTrue(restaurant.getIsInCityAlternateNames().size()==2);
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }
	    
	    @Test
	    public void testSetIsInFields_first_ok_second_null_isInAlreadyFilled(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
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
	    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(null);
	    	EasyMock.replay(cityDao);
	    	OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);
	    	
	    	Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	restaurant.setIsIn("AlreadyFilled");
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	
	    	
	     	Set<String> expectedZip =new HashSet<String>();
	    	expectedZip.add("ZIP1");
	    	Assert.assertEquals(expectedZip, restaurant.getIsInZip());
	    	Assert.assertEquals("adm2name", restaurant.getIsInAdm());
	    	Assert.assertEquals("AlreadyFilled", restaurant.getIsIn());
	    	Assert.assertEquals(null, restaurant.getIsInPlace());
	    	Assert.assertTrue(restaurant.getIsInCityAlternateNames().size()==2);
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }
	    
	    
	    @Test
	    public void testSetIsInFields_GetByShape(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
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
	    	OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);

	    	Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	
	    	
	     	Set<String> expectedZip =new HashSet<String>();
	    	expectedZip.add("ZIP");
	    	Assert.assertEquals(expectedZip, restaurant.getIsInZip());
	    	Assert.assertEquals("admName", restaurant.getIsInAdm());
	    	Assert.assertEquals("name", restaurant.getIsIn());
	    	Assert.assertEquals(null, restaurant.getIsInPlace());
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }
	    
	    @Test
	    public void testSetIsInFields_both_null(){
	    	OpenStreetMapPoisSimpleImporter OpenStreetMapPoisSimpleImporter = new OpenStreetMapPoisSimpleImporter();
	    	
			final City city = new City();
			city.setMunicipality(false);
			final List<ZipCode> zipCodes = new ArrayList<ZipCode>();
			zipCodes.add(new ZipCode("zip1"));
			Point location= GeolocHelper.createPoint(2F, 3F);
			
	    	String countryCode = "FR";
	    	
	    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
	    	EasyMock.expect(cityDao.getByShape(EasyMock.anyObject(Point.class),EasyMock.anyObject(String.class),EasyMock.eq(true))).andReturn(null);
	    	EasyMock.expect(cityDao.getNearest(location, countryCode, true, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(city);
			EasyMock.expect(cityDao.getNearest(location, countryCode, false, OpenStreetMapPoisSimpleImporter.DISTANCE)).andReturn(null);
	    	EasyMock.replay(cityDao);
	    	OpenStreetMapPoisSimpleImporter.setCityDao(cityDao);

	    	Restaurant restaurant = new Restaurant();
	    	restaurant.setCountryCode(countryCode);
	    	restaurant.setLocation(location);
	    	OpenStreetMapPoisSimpleImporter.setIsInFields(restaurant);
	    	
	    	
	    	Assert.assertEquals(null, restaurant.getIsInZip());
	    	Assert.assertEquals(null, restaurant.getIsInAdm());
	    	Assert.assertEquals(null, restaurant.getIsIn());
	    	Assert.assertEquals(null, restaurant.getIsInPlace());
	    	
	    	EasyMock.verify(cityDao);
	    	
	    }

	/*
	@Test
	public void process(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);
		EasyMock.replay(solrResponseDtoCity);
		
		OpenStreetMapPoisSimpleImporter importer = new OpenStreetMapPoisSimpleImporter();
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(1234L);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
		
		IGisFeatureDao cityDao = EasyMock.createMock(IGisFeatureDao.class);
		City city=new City();
		city.setFeatureId(123L);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		
		String line= "W\t90139043\tPfarrkirche Heiliger Johannes der Täufer\tAT\tPfarrkirche Heiliger Johannes der Täufer___Parish church Saint John Baptist\t0101000020E61000000E6D653509482C40B01EF706AB514740\tplace_of_worship";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
		EasyMock.verify(admDao);
	}
	*/
	
}
