package com.gisgraphy.reversegeocoding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.addressparser.format.DisplayMode;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.geocoding.GeocodingService;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.importer.LabelGenerator;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.Point;

public class AddressHelperTest {


	AddressHelper addressHelper = new AddressHelper();
	BasicAddressFormater formater = BasicAddressFormater.getInstance();
	LabelGenerator generator = LabelGenerator.getInstance();


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
		HouseNumber house = new HouseNumber("1",houseLocation,"fr");
		houses.add(house);
		Point searchPoint = GeolocHelper.createPoint(6D, 7D);
		HouseNumberDistance nearestHouse = addressHelper.getNearestHouse(houses, searchPoint);
		Assert.assertEquals(new HouseNumberDistance(house, GeolocHelper.distance(searchPoint, houseLocation)),nearestHouse);
	}

	@Test
	public void getNearestHouse_SeveralHouse() {
		TreeSet<HouseNumber> houses = new TreeSet<HouseNumber>();
		Point houseLocation = GeolocHelper.createPoint(4D, 5D);
		HouseNumber house_far = new HouseNumber("far",houseLocation,"fr");

		Point houseLocation2 = GeolocHelper.createPoint(3.1D, 4.1D);
		HouseNumber house2_near = new HouseNumber("near",houseLocation2,"fr");

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
		osm.setAdm1Name("adm1Name");
		osm.setAdm2Name("adm2Name");
		osm.setAdm3Name("adm3Name");
		osm.setAdm4Name("adm4Name");
		osm.setAdm5Name("adm5Name");
		osm.setZipCode("zipCodeSet");
		osm.setCountryCode("US");
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
		Assert.assertEquals("When there is a zipcode, we take it","zipCodeSet", address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertNotNull(address.getFormatedPostal());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA), address.getFormatedPostal());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());

		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

		Assert.assertEquals(osm.isOneWay(), address.isOneWay());
		Assert.assertEquals(osm.getStreetType().toString(), address.getStreetType());
		Assert.assertEquals(osm.getLength(), address.getLength());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

	}

	@Test
	public void buildAddressFromOpenstreetMap_severalZip(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		osm.setAdm1Name("adm1Name");
		osm.setAdm2Name("adm2Name");
		osm.setAdm3Name("adm3Name");
		osm.setAdm4Name("adm4Name");
		osm.setAdm5Name("adm5Name");
		osm.setCountryCode("US");
		osm.setZipCode(null);
		Assert.assertTrue("the zipcodes should be filled for this set, please fix the dataset",osm.getIsInZip().size()>0);
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
		Assert.assertEquals("When there is more than one zipcode, we take the best one except if already fill",generator.getBestZipString(osm.getIsInZip()), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertNotNull(address.getFormatedPostal());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA), address.getFormatedPostal());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());

		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

		Assert.assertEquals(osm.isOneWay(), address.isOneWay());
		Assert.assertEquals(osm.getStreetType().toString(), address.getStreetType());
		Assert.assertEquals(osm.getLength(), address.getLength());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

	}

	@Test
	public void buildAddressFromOpenstreetMap_severalZip_zip_already_filled(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		osm.setAdm1Name("adm1Name");
		osm.setAdm2Name("adm2Name");
		osm.setAdm3Name("adm3Name");
		osm.setAdm4Name("adm4Name");
		osm.setAdm5Name("adm5Name");
		osm.setCountryCode("US");
		Assert.assertTrue("the zipcodes should be filled for this set, please fix the dataset",osm.getIsInZip().size()>0);
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
		Assert.assertEquals("When there is more than one zipcode, we take the best one except if already fill",osm.getZipCode(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertNotNull(address.getFormatedPostal());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA), address.getFormatedPostal());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());

		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

		Assert.assertEquals(osm.isOneWay(), address.isOneWay());
		Assert.assertEquals(osm.getStreetType().toString(), address.getStreetType());
		Assert.assertEquals(osm.getLength(), address.getLength());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

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
		Assert.assertEquals(osm.getZipCode(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());

		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());

		//
		Assert.assertEquals(GeolocHelper.distance(point, osm.getLocation()), address.getDistance().doubleValue(),0.01);



	}

	@Test
	public void buildAddressFromHousenumberDistance(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		String number = "2";
		HouseNumber houseNumber = new HouseNumber(number,GeolocHelper.createPoint(3D, 4D),"fr");
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
		Assert.assertEquals(osm.getZipCode(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(houseNumber.getLatitude(),address.getLat());
		Assert.assertEquals(houseNumber.getLongitude(), address.getLng());
		Assert.assertEquals(GeocodingLevels.HOUSE_NUMBER, address.getGeocodingLevel());
		Assert.assertTrue(address.getFormatedFull().contains("2 "));

		//
		Assert.assertEquals(distance, address.getDistance().doubleValue(),0.01);
		Assert.assertEquals(name, address.getName());
		Assert.assertEquals(number, address.getHouseNumber());

		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());
		Assert.assertTrue(address.getFormatedFull().contains(number));
		Assert.assertTrue(address.getFormatedPostal().contains(number));

		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());

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
		zipcodes.add(new ZipCode("zip","fr"));
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
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());

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
		zipcodes.add(new ZipCode("zip","fr"));
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
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());
		Assert.assertEquals(GeolocHelper.distance(point, city.getLocation()), address.getDistance().doubleValue(),0.0001);
	}

	@Test
	public void limitNbResult(){
		//null dto
		Assert.assertNotNull(AddressHelper.limitNbResult(null, 3));
		//higher limit than size
		AddressResultsDto dto  = createAddresses(3);
		AddressHelper.limitNbResult(dto, 5);
		Assert.assertEquals(3, dto.getResult().size());

		//smaller limit than size
		dto  = createAddresses(3);
		AddressHelper.limitNbResult(dto, 2);
		Assert.assertEquals(2, dto.getResult().size());

		// limit == size
		dto  = createAddresses(3);
		AddressHelper.limitNbResult(dto, 3);
		Assert.assertEquals(3, dto.getResult().size());

		//limit =0
		dto  = createAddresses(3);
		AddressHelper.limitNbResult(dto, 0);
		Assert.assertEquals(3, dto.getResult().size());

		//limit <0
		dto  = createAddresses(3);
		AddressHelper.limitNbResult(dto, -1);
		Assert.assertEquals(3, dto.getResult().size());

		//empty adresses
		dto  = new AddressResultsDto();
		AddressHelper.limitNbResult(dto, 2);
		Assert.assertEquals(0, dto.getResult().size());
	}


	protected AddressResultsDto createAddresses(int nb) {
		List<Address> addresses = new ArrayList<Address>();
		for (int i =0;i<nb;i++){
			addresses.add(new Address());
		}

		return new AddressResultsDto(addresses,1L);
	}


}
