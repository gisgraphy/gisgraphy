package com.gisgraphy.reversegeocoding;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.Point;

public class AddressHelperTest {
	
	
		AddressHelper addressHelper = new AddressHelper();
	

	@Test
	public void getNearestHouse_WrongParameter() {
		Assert.assertNull(addressHelper.getNearestHouse(new TreeSet<HouseNumber>(), null));
		Assert.assertNull(addressHelper.getNearestHouse(null, GeolocHelper.createPoint(3D, 4D)));
		Assert.assertNull(addressHelper.getNearestHouse(new TreeSet<HouseNumber>(), GeolocHelper.createPoint(3D, 4D)));
	}
	
	@Test
	public void getNearestHouse_OneHouse() {
		TreeSet<HouseNumber> houses = new TreeSet<HouseNumber>();
		Point houseLocation = GeolocHelper.createPoint(3D, 4D);
		HouseNumber house = new HouseNumber("1",houseLocation);
		houses.add(house);
		Point searchPoint = GeolocHelper.createPoint(6D, 7D);
		HouseNumberDistance nearestHouse = addressHelper.getNearestHouse(houses, searchPoint);
		Assert.assertEquals(new HouseNumberDistance(house, GeolocHelper.distance(searchPoint, houseLocation)),nearestHouse);
	}
	
	@Test
	public void getNearestHouse_SeveralHouse() {
		TreeSet<HouseNumber> houses = new TreeSet<HouseNumber>();
		Point houseLocation = GeolocHelper.createPoint(4D, 5D);
		HouseNumber house_far = new HouseNumber("far",houseLocation);
		
		Point houseLocation2 = GeolocHelper.createPoint(3.1D, 4.1D);
		HouseNumber house2_near = new HouseNumber("near",houseLocation2);
		
		houses.add(house_far);
		houses.add(house2_near);
		
		Point searchPoint = GeolocHelper.createPoint(3D, 4D);
		HouseNumberDistance nearestHouse = addressHelper.getNearestHouse(houses, searchPoint);
		Assert.assertEquals(new HouseNumberDistance(house2_near, GeolocHelper.distance(searchPoint, houseLocation2)),nearestHouse);
	}
	
	@Test
	public void buildAddressFromOpenstreetMap_NullOpenstreetmap(){
		Assert.assertNull(addressHelper.buildAddressFromOpenstreetMap(null));
	}
	
	@Test
	public void buildFullAddressString(){
		Address address= new Address();
		address.setStreetName("foo bar street");
		address.setHouseNumber("3");
		address.setCity("paris");
		address.setCitySubdivision("3e arrondissement");
		address.setAdm1Name("adm1");
		address.setAdm2Name("adm2");
		address.setAdm3Name("adm3");
		address.setAdm4Name("adm4");
		address.setAdm5Name("adm5");
		address.setCountryCode("FR");
		String actual = addressHelper.buildFullAddressString(address);
		Assert.assertEquals("3, foo bar street, 3e arrondissement, paris, adm5, adm4, adm3, adm2, adm1, France, FR", actual);

		//with only state fill
		address.setAdm1Name(null);
		address.setAdm2Name(null);
		address.setAdm3Name(null);
		address.setAdm4Name(null);
		address.setAdm5Name(null);
		address.setState("state");
		actual = addressHelper.buildFullAddressString(address);
		Assert.assertEquals("3, foo bar street, 3e arrondissement, paris, state, France, FR", actual);

		//without any state info
		address.setAdm1Name(null);
		address.setAdm2Name(null);
		address.setAdm3Name(null);
		address.setAdm4Name(null);
		address.setAdm5Name(null);
		address.setState(null);
		actual = addressHelper.buildFullAddressString(address);
		Assert.assertEquals("3, foo bar street, 3e arrondissement, paris, France, FR", actual);

		//without any state info and city subdivision
		address.setAdm1Name(null);
		address.setAdm2Name(null);
		address.setAdm3Name(null);
		address.setAdm4Name(null);
		address.setAdm5Name(null);
		address.setState(null);
		address.setCitySubdivision(null);
		actual = addressHelper.buildFullAddressString(address);
		Assert.assertEquals("3, foo bar street, paris, France, FR", actual);
		//withunknow country
		address.setAdm1Name(null);
		address.setAdm2Name(null);
		address.setAdm3Name(null);
		address.setAdm4Name(null);
		address.setAdm5Name(null);
		address.setState(null);
		address.setCountryCode("XX");;

		address.setCitySubdivision(null);
		actual = addressHelper.buildFullAddressString(address);
		Assert.assertEquals("3, foo bar street, paris, XX", actual);

	}
	
	@Test
	public void buildAddressFromOpenstreetMap(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		osm.setAdm1Name("adm1Name");
		osm.setAdm2Name("adm2Name");
		osm.setAdm3Name("adm3Name");
		osm.setAdm4Name("adm4Name");
		osm.setAdm5Name("adm5Name");
		Address address = addressHelper.buildAddressFromOpenstreetMap(osm);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals(osm.getAdm1Name(), address.getAdm1Name());
		Assert.assertEquals(osm.getAdm2Name(), address.getAdm2Name());
		Assert.assertEquals(osm.getAdm3Name(), address.getAdm3Name());
		Assert.assertEquals(osm.getAdm4Name(), address.getAdm4Name());
		Assert.assertEquals(osm.getAdm5Name(), address.getAdm5Name());
		Assert.assertEquals(osm.getIsInZip().iterator().next(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(addressHelper.buildFullAddressString(address), address.getFormatedFull());
		System.out.println(addressHelper.buildFullAddressString(address));
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());
		
		
		
	}
	
	@Test
	public void buildAddressFromOpenstreetMapAndPoint(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		Point point = GeolocHelper.createPoint(2D, 3D);
		Address address = addressHelper.buildAddressFromOpenstreetMapAndPoint(osm,point);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals(osm.getIsInZip().iterator().next(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());
		
		//
		Assert.assertEquals(GeolocHelper.distance(point, osm.getLocation()), address.getDistance().doubleValue(),0.01);
		
		
		
	}
	
	@Test
	public void buildAddressFromHousenumberDistance(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		String number = "2";
		HouseNumber houseNumber = new HouseNumber(number,GeolocHelper.createPoint(3D, 4D));
		String name = "houseName";
		houseNumber.setName(name);
		osm.addHouseNumber(houseNumber);
		Double distance =55D;
		HouseNumberDistance houseNumberDistance = new HouseNumberDistance(houseNumber, distance );
		Address address = addressHelper.buildAddressFromHouseNumberDistance(houseNumberDistance);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals(osm.getIsInZip().iterator().next(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(houseNumber.getLatitude(),address.getLat());
		Assert.assertEquals(houseNumber.getLongitude(), address.getLng());
		Assert.assertEquals(GeocodingLevels.HOUSE_NUMBER, address.getGeocodingLevel());
		Assert.assertTrue(address.getFormatedFull().contains("2,"));
		
		//
		Assert.assertEquals(distance, address.getDistance().doubleValue(),0.01);
		Assert.assertEquals(name, address.getName());
		Assert.assertEquals(number, address.getHouseNumber());
		
	}
	
	@Test
	public void buildAddressFromcity(){
		City city = new City();
		city.setName("name");
		city.setIsInAdm("isInAdm");
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		city.setIsInAdm("isInAdm");
		Set<ZipCode> zipcodes = new HashSet<ZipCode>();
		zipcodes.add(new ZipCode("zip"));
		city.setZipCodes(zipcodes);
		city.setLocation(GeolocHelper.createPoint(2D, 3D));
		city.setCountryCode("countryCode");
		
		
		Address address = addressHelper.buildAddressFromcity(city);
		Assert.assertEquals(city.getName(), address.getCity());
		Assert.assertEquals(city.getIsInAdm(), address.getState());
		Assert.assertEquals(city.getAdm1Name(), address.getAdm1Name());
		Assert.assertEquals(city.getAdm2Name(), address.getAdm2Name());
		Assert.assertEquals(city.getAdm3Name(), address.getAdm3Name());
		Assert.assertEquals(city.getAdm4Name(), address.getAdm4Name());
		Assert.assertEquals(city.getAdm5Name(), address.getAdm5Name());
		Assert.assertEquals(city.getZipCodes().iterator().next().toString(), address.getZipCode());
		Assert.assertEquals(city.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(GeocodingLevels.CITY, address.getGeocodingLevel());
		Assert.assertEquals(city.getLatitude(), address.getLat());
		Assert.assertEquals(city.getLongitude(), address.getLng());
		Assert.assertEquals(addressHelper.buildFullAddressString(address), address.getFormatedFull());
		System.out.println(addressHelper.buildFullAddressString(address));
		
	}
	
	@Test
	public void buildAddressFromCityAndPoint(){
		City city = new City();
		city.setName("name");
		city.setIsInAdm("isInAdm");
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		city.setIsInAdm("isInAdm");
		Set<ZipCode> zipcodes = new HashSet<ZipCode>();
		zipcodes.add(new ZipCode("zip"));
		city.setZipCodes(zipcodes);
		city.setLocation(GeolocHelper.createPoint(2D, 3D));
		city.setCountryCode("countryCode");
		Point point = GeolocHelper.createPoint(5D, 4D);
		
		
		Address address = addressHelper.buildAddressFromCityAndPoint(city,point);
		Assert.assertEquals(city.getName(), address.getCity());
		Assert.assertEquals(city.getIsInAdm(), address.getState());
		Assert.assertEquals(city.getAdm1Name(), address.getAdm1Name());
		Assert.assertEquals(city.getAdm2Name(), address.getAdm2Name());
		Assert.assertEquals(city.getAdm3Name(), address.getAdm3Name());
		Assert.assertEquals(city.getAdm4Name(), address.getAdm4Name());
		Assert.assertEquals(city.getAdm5Name(), address.getAdm5Name());
		Assert.assertEquals(city.getZipCodes().iterator().next().toString(), address.getZipCode());
		Assert.assertEquals(city.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(GeocodingLevels.CITY, address.getGeocodingLevel());
		Assert.assertEquals(city.getLatitude(), address.getLat());
		Assert.assertEquals(city.getLongitude(), address.getLng());
		Assert.assertEquals(addressHelper.buildFullAddressString(address), address.getFormatedFull());
		System.out.println(addressHelper.buildFullAddressString(address));
		Assert.assertEquals(GeolocHelper.distance(point, city.getLocation()), address.getDistance().doubleValue(),0.0001);
	}
	

}
