package com.gisgraphy.reversegeocoding;

import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
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
	public void buildAddressFromOpenstreetMap(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		Address address = addressHelper.buildAddressFromOpenstreetMap(osm);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals(osm.getIsInZip().iterator().next(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
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
		
		//
		Assert.assertEquals(distance, address.getDistance().doubleValue(),0.01);
		Assert.assertEquals(name, address.getName());
		Assert.assertEquals(number, address.getHouseNumber());
		
	}
	

}
