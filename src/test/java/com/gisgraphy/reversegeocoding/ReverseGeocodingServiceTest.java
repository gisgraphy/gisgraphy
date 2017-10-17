package com.gisgraphy.reversegeocoding;

import static com.gisgraphy.reversegeocoding.ReverseGeocodingService.DEFAULT_STREET_RADIUS;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.service.impl.StatsUsageServiceImpl;
import com.gisgraphy.stats.StatsUsageType;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.Point;

public class ReverseGeocodingServiceTest {

	@Test
	public void testExecuteQuery_RoadWithHouseNumber() {
		Point searchPoint = GeolocHelper.createPoint(2D, 3D);
		ReverseGeocodingQuery query = new ReverseGeocodingQuery(searchPoint);
		ReverseGeocodingService reverseGeocodingService = new ReverseGeocodingService();
		IStatsUsageService statsService = EasyMock.createMock(StatsUsageServiceImpl.class);
		statsService.increaseUsage(StatsUsageType.REVERSEGEOCODING);
		EasyMock.replay(statsService);
		
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		String number = "2";
		HouseNumber houseNumber = new HouseNumber(number,GeolocHelper.createPoint(3D, 4D),"fr");
		String name = "houseName";
		houseNumber.setName(name);
		osm.addHouseNumber(houseNumber);;
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(osm );
		EasyMock.replay(openStreetMapDao);
		
		//com.gisgraphy.reversegeocoding.AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		
		/*HouseNumberDistance houseNumberDistance = new HouseNumberDistance(houseNumber, 3D);
		EasyMock.expect(addressHelper.getNearestHouse(osm.getHouseNumbers(), query.getPoint())).andReturn(houseNumberDistance);
		Address address = new Address();
		address.setStreetName("streetName");
		EasyMock.expect(addressHelper.buildAddressFromHouseNumberDistance(houseNumberDistance )).andReturn(new AddressHelper().buildAddressFromHouseNumberDistance(houseNumberDistance));
		EasyMock.replay(addressHelper);*/
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		//reverseGeocodingService.addressHelper = addressHelper;
		
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		//Assert.assertEquals(new AddressHelper().buildAddressFromHouseNumberDistance(houseNumberDistance), addressResultsDto.getResult().get(0));
		Assert.assertTrue(addressResultsDto.getResult().get(0).getFormatedFull().contains("2"));
		
		EasyMock.verify(statsService);
		EasyMock.verify(openStreetMapDao);
		//EasyMock.verify(addressHelper);
	}
	
	
	@Test
	public void testExecuteQuery_RoadWithoutHouseNumber() {
		Point searchPoint = GeolocHelper.createPoint(2D, 3D);
		ReverseGeocodingQuery query = new ReverseGeocodingQuery(searchPoint);
		ReverseGeocodingService reverseGeocodingService = new ReverseGeocodingService();
		IStatsUsageService statsService = EasyMock.createMock(StatsUsageServiceImpl.class);
		statsService.increaseUsage(StatsUsageType.REVERSEGEOCODING);
		EasyMock.replay(statsService);
		
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(osm );
		EasyMock.replay(openStreetMapDao);
		
	//	AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		
		/*Address address = new Address();
		address.setStreetName("streetName");
		EasyMock.expect(addressHelper.buildAddressFromOpenstreetMapAndPoint(osm,query.getPoint())).andReturn(address);
		EasyMock.replay(addressHelper);*/
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		//reverseGeocodingService.addressHelper = addressHelper;
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		Assert.assertNotNull(addressResultsDto.getResult().get(0));
	
		
		EasyMock.verify(statsService);
		EasyMock.verify(openStreetMapDao);
	//	EasyMock.verify(addressHelper);
	}
	
	@Test
	public void testExecuteQuery_NoRoadfound_streetfound() {
		Point searchPoint = GeolocHelper.createPoint(2D, 3D);
		ReverseGeocodingQuery query = new ReverseGeocodingQuery(searchPoint);
		ReverseGeocodingService reverseGeocodingService = new ReverseGeocodingService();
		IStatsUsageService statsService = EasyMock.createMock(StatsUsageServiceImpl.class);
		statsService.increaseUsage(StatsUsageType.REVERSEGEOCODING);
		EasyMock.replay(statsService);
		
		
		
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		
		/*Address address = new Address();
		address.setStreetName("streetName");
		AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		EasyMock.expect(addressHelper.buildAddressFromOpenstreetMapAndPoint(osm,query.getPoint())).andReturn(address);
		EasyMock.replay(addressHelper);*/
		
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(null);
		EasyMock.expect(openStreetMapDao.getNearestFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(osm);
		EasyMock.replay(openStreetMapDao);
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		//reverseGeocodingService.addressHelper = addressHelper;
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		Assert.assertNotNull(addressResultsDto.getResult().get(0));
		
		EasyMock.verify(statsService);
		EasyMock.verify(openStreetMapDao);
	}
	
	
	@Test
	public void testExecuteQuery_noRoadfound_nostreetfound_cityByVicinityFound() {
		Point searchPoint = GeolocHelper.createPoint(2D, 3D);
		ReverseGeocodingQuery query = new ReverseGeocodingQuery(searchPoint);
		ReverseGeocodingService reverseGeocodingService = new ReverseGeocodingService();
		IStatsUsageService statsService = EasyMock.createMock(StatsUsageServiceImpl.class);
		statsService.increaseUsage(StatsUsageType.REVERSEGEOCODING);
		EasyMock.replay(statsService);
		
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		
		City city = new City();
		city.setName("city");
		
		/*Address address = new Address();
		address.setStreetName("streetName");
		AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		EasyMock.expect(addressHelper.buildAddressFromCityAndPoint(city, searchPoint)).andReturn(address);
		EasyMock.replay(addressHelper);*/
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		EasyMock.expect(cityDao.getByShape(searchPoint, null, false)).andReturn(null);
		EasyMock.expect(cityDao.getNearest(searchPoint, null, false, ReverseGeocodingService.DEFAULT_CITY_RADIUS)).andReturn(city);
		EasyMock.replay(cityDao);
		
		
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(null);
		EasyMock.expect(openStreetMapDao.getNearestFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(null);
		EasyMock.replay(openStreetMapDao);
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		//reverseGeocodingService.addressHelper = addressHelper;
		reverseGeocodingService.cityDao= cityDao;
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		
		EasyMock.verify(statsService);
		EasyMock.verify(cityDao);
		//EasyMock.verify(addressHelper);
		EasyMock.verify(openStreetMapDao);
	}
	
	@Test
	public void testExecuteQuery_noRoadfound_nostreetfound_cityByShapeFound() {
		Point searchPoint = GeolocHelper.createPoint(2D, 3D);
		ReverseGeocodingQuery query = new ReverseGeocodingQuery(searchPoint);
		ReverseGeocodingService reverseGeocodingService = new ReverseGeocodingService();
		IStatsUsageService statsService = EasyMock.createMock(StatsUsageServiceImpl.class);
		statsService.increaseUsage(StatsUsageType.REVERSEGEOCODING);
		EasyMock.replay(statsService);
		
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		
		City city = new City();
		city.setName("city");
		
		/*Address address = new Address();
		address.setStreetName("streetName");
		AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		EasyMock.expect(addressHelper.buildAddressFromCityAndPoint(city, searchPoint)).andReturn(address);
		EasyMock.replay(addressHelper);*/
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		EasyMock.expect(cityDao.getByShape(searchPoint, null, false)).andReturn(city);
		EasyMock.replay(cityDao);
		
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(null);
		EasyMock.expect(openStreetMapDao.getNearestFrom(searchPoint,DEFAULT_STREET_RADIUS)).andReturn(null);
		EasyMock.replay(openStreetMapDao);
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		//reverseGeocodingService.addressHelper = addressHelper;
		reverseGeocodingService.cityDao= cityDao;
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		
		EasyMock.verify(statsService);
		EasyMock.verify(cityDao);
		//EasyMock.verify(addressHelper);
		EasyMock.verify(openStreetMapDao);
	}

}
