package com.gisgraphy.reversegeocoding;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
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
		HouseNumber houseNumber = new HouseNumber(number,GeolocHelper.createPoint(3D, 4D));
		String name = "houseName";
		houseNumber.setName(name);
		osm.addHouseNumber(houseNumber);;
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint)).andReturn(osm );
		EasyMock.replay(openStreetMapDao);
		
		com.gisgraphy.reversegeocoding.AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		
		HouseNumberDistance houseNumberDistance = new HouseNumberDistance(houseNumber, 3D);
		EasyMock.expect(addressHelper.getNearestHouse(osm.getHouseNumbers(), query.getPoint())).andReturn(houseNumberDistance);
		Address address = new Address();
		address.setStreetName("streetName");
		EasyMock.expect(addressHelper.buildAddressFromHouseNumberDistance(houseNumberDistance )).andReturn(address);
		EasyMock.replay(addressHelper);
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		reverseGeocodingService.addressHelper = addressHelper;
		
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		Assert.assertEquals(address, addressResultsDto.getResult().get(0));
		
		EasyMock.verify(statsService);
		EasyMock.verify(openStreetMapDao);
		EasyMock.verify(addressHelper);
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
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint)).andReturn(osm );
		EasyMock.replay(openStreetMapDao);
		
		AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		
		Address address = new Address();
		address.setStreetName("streetName");
		EasyMock.expect(addressHelper.buildAddressFromOpenstreetMapAndPoint(osm,query.getPoint())).andReturn(address);
		EasyMock.replay(addressHelper);
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		reverseGeocodingService.addressHelper = addressHelper;
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		Assert.assertEquals(address, addressResultsDto.getResult().get(0));
		
		EasyMock.verify(statsService);
		EasyMock.verify(openStreetMapDao);
		EasyMock.verify(addressHelper);
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
		
		Address address = new Address();
		address.setStreetName("streetName");
		AddressHelper addressHelper = EasyMock.createMock(AddressHelper.class);
		EasyMock.expect(addressHelper.buildAddressFromOpenstreetMapAndPoint(osm,query.getPoint())).andReturn(address);
		EasyMock.replay(addressHelper);
		
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint)).andReturn(null);
		EasyMock.expect(openStreetMapDao.getNearestFrom(searchPoint)).andReturn(osm);
		EasyMock.replay(openStreetMapDao);
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		reverseGeocodingService.addressHelper = addressHelper;
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(1,addressResultsDto.getResult().size());
		Assert.assertEquals(address, addressResultsDto.getResult().get(0));
		
		EasyMock.verify(statsService);
		EasyMock.verify(openStreetMapDao);
	}
	
	
	@Test
	public void testExecuteQuery_noRoadfound_nostreetfound() {
		Point searchPoint = GeolocHelper.createPoint(2D, 3D);
		ReverseGeocodingQuery query = new ReverseGeocodingQuery(searchPoint);
		ReverseGeocodingService reverseGeocodingService = new ReverseGeocodingService();
		IStatsUsageService statsService = EasyMock.createMock(StatsUsageServiceImpl.class);
		statsService.increaseUsage(StatsUsageType.REVERSEGEOCODING);
		EasyMock.replay(statsService);
		
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		
		EasyMock.expect(openStreetMapDao.getNearestRoadFrom(searchPoint)).andReturn(null);
		EasyMock.expect(openStreetMapDao.getNearestFrom(searchPoint)).andReturn(null);
		EasyMock.replay(openStreetMapDao);
		
		reverseGeocodingService.openStreetMapDao =openStreetMapDao;
		reverseGeocodingService.statsUsageService = statsService;
		
		AddressResultsDto addressResultsDto = reverseGeocodingService.executeQuery(query);
		Assert.assertNotNull(addressResultsDto.getQTime());
		Assert.assertNotNull(addressResultsDto.getResult());
		Assert.assertEquals(0,addressResultsDto.getResult().size());
		
		EasyMock.verify(statsService);
		EasyMock.verify(openStreetMapDao);
	}

}
