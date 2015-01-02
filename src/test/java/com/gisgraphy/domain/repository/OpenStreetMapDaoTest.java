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
package com.gisgraphy.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.geoloc.entity.event.EventManager;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeletedEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureStoredEvent;
import com.gisgraphy.domain.geoloc.entity.event.PlaceTypeDeleteAllEvent;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.SRID;
import com.gisgraphy.domain.valueobject.StreetDistance;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.street.StreetFactory;
import com.gisgraphy.street.StreetSearchMode;
import com.gisgraphy.street.StreetType;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;


public class OpenStreetMapDaoTest extends AbstractIntegrationHttpSolrTestCase{

 IOpenStreetMapDao openStreetMapDao;
    
 @Test
 public void testCouldNotSaveNonUniqueGID(){
     OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	OpenStreetMap streetOSM2 = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	try {
	    openStreetMapDao.save(streetOSM2);
	    openStreetMapDao.flushAndClear();
	    fail("we should not save street with non unique GID");
	} catch (DataIntegrityViolationException e) {
	  assertTrue("a ConstraintViolationException should be throw when saving an openstreetmap with a non unique gid ",e.getCause() instanceof ConstraintViolationException);
	}
	
	
 }
 
 
  @Test
  public void testGetNearestAndDistanceFromShouldNotAcceptNullStreetSearchModeIfNameIsNotNull() {
	 try {
		openStreetMapDao.getNearestAndDistanceFrom(GeolocHelper.createPoint(30.1F, 30.1F), 10000, 1, 1, null, null,"john keN",null,true);
		fail("getNearestAndDistanceFrom should not accept a null streetSearchmode if name is not null");
	} catch (IllegalArgumentException e) {
		//ok
	}
 }
  
  
  @Test
  public void testGetMaxOpenstreetMapId(){
	  OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	  openStreetMapDao.save(streetOSM);
	  
	  long actual = openStreetMapDao.getMaxOpenstreetMapId();
	  Assert.assertEquals(streetOSM.getOpenstreetmapId().longValue(), actual);
  }
  
  @Test
  public void testGetMaxGid(){
	  OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	  long gid = 3456L;
	  streetOSM.setGid(gid);
	  openStreetMapDao.save(streetOSM);
	  
	  long actual = openStreetMapDao.getMaxGid();
	  Assert.assertEquals(gid, actual);
  }
  
  @Test
  public void testGetNearestByosmIds(){
	  LineString shape = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.99999)");
	    shape.setSRID(SRID.WGS84_SRID.getSRID());
	    Long id1= 10L;		
	    Long id2=20L;
		OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
		streetOSM.setShape(shape);
		streetOSM.setGid(1L);
		streetOSM.setOpenstreetmapId(id1);
		openStreetMapDao.save(streetOSM);
		assertNotNull(openStreetMapDao.get(streetOSM.getId()));
		
		//we create a multilineString a little bit closest to the first one 
		OpenStreetMap streetOSM2 = new OpenStreetMap();
		LineString shape2 = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.9154734)");
		shape2.setSRID(SRID.WGS84_SRID.getSRID());
		
		
		streetOSM2.setShape(shape2);
		streetOSM2.setOpenstreetmapId(id2);
		streetOSM2.setGid(2L);
		//Simulate middle point
		streetOSM2.setLocation(GeolocHelper.createPoint(6.94130445F , 50.91544865F));
		streetOSM2.setOneWay(false);
		streetOSM2.setStreetType(StreetType.FOOTWAY);
		streetOSM2.setName("John Kenedy");
		StringHelper.updateOpenStreetMapEntityForIndexation(streetOSM2);
		openStreetMapDao.save(streetOSM2);
		assertNotNull(openStreetMapDao.get(streetOSM2.getId()));
		
		ArrayList<Long> ids = new ArrayList<Long>();
		ids.add(id1);
		ids.add(id2);
		
		Point searchPoint = GeolocHelper.createPoint(6.9412748F, 50.9155829F);
		OpenStreetMap actual = openStreetMapDao.getNearestByosmIds(searchPoint,ids);
		Assert.assertEquals(id2, actual.getOpenstreetmapId());
		
		//no ids found
		actual = openStreetMapDao.getNearestByosmIds(searchPoint,new ArrayList<Long>());
		Assert.assertNull(actual);
		
		ArrayList<Long> fakeIds = new ArrayList<Long>();
		fakeIds.add(333L);
		actual = openStreetMapDao.getNearestByosmIds(searchPoint,fakeIds);
		Assert.assertNull(actual);
	  
  }
 
    @Test
    public void testGetNearestAndDistanceFromShouldReturnValidDTO() {
    LineString shape = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.99999)");
    shape.setSRID(SRID.WGS84_SRID.getSRID());
	
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	streetOSM.setShape(shape);
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	//we create a multilineString a little bit closest to the first one 
	OpenStreetMap streetOSM2 = new OpenStreetMap();
	LineString shape2 = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.9154734)");
	shape2.setSRID(SRID.WGS84_SRID.getSRID());
	
	
	streetOSM2.setShape(shape2);
	streetOSM2.setGid(2L);
	//Simulate middle point
	streetOSM2.setLocation(GeolocHelper.createPoint(6.94130445F , 50.91544865F));
	streetOSM2.setOneWay(false);
	streetOSM2.setStreetType(StreetType.FOOTWAY);
	streetOSM2.setName("John Kenedy");
	streetOSM2.setOpenstreetmapId(123456L);
	StringHelper.updateOpenStreetMapEntityForIndexation(streetOSM2);
	openStreetMapDao.save(streetOSM2);
	assertNotNull(openStreetMapDao.get(streetOSM2.getId()));
	
	int numberOfLineUpdated = openStreetMapDao.updateTS_vectorColumnForStreetNameSearch();
	
			assertEquals("It should have 2 lines updated : (streetosm +streetosm2) for fulltext",2, numberOfLineUpdated);
	
	
	Point searchPoint = GeolocHelper.createPoint(6.9412748F, 50.9155829F);
	
	List<StreetDistance> nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null,null, null,null,true);
	assertEquals(1,nearestStreet.size());
	assertEquals("The street is not the expected one, there is probably a problem with the distance",streetOSM2.getGid(),nearestStreet.get(0).getGid());
	//test distance 
	//the following line test the distance when the nearest point is taken, with distance_sphere
	Assert.assertEquals("There is probably a problem with the distance",14.7, nearestStreet.get(0).getDistance(),0.1);
	//the following line test the distance when the middle of the street is taken with distance
	//Assert.assertEquals("There is probably a problem with the distance",searchPoint.distance(streetOSM2.getShape()), nearestStreet.get(0).getDistance().longValue(),5);
	//the following line test the distance when the middle of the street is taken with distance_sphere
	//Assert.assertEquals(GeolocHelper.distance(searchPoint, nearestStreet.get(0).getLocation()), nearestStreet.get(0).getDistance().longValue(),5);
	
	//test hasdistance field
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null,null, null,null,false);
	Assert.assertNull("When includeDistance=false, distance should not be included ",nearestStreet.get(0).getDistance());
	
	//test streettype
	assertEquals(0,openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, StreetType.UNCLASSIFIED,null, null,null,true).size());
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, StreetType.FOOTWAY,null, null,null,true);
	assertEquals(1,nearestStreet.size());
	assertEquals(streetOSM2.getGid(),nearestStreet.get(0).getGid());
	
	//test name in full text
	if (GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE){
		nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"keN",StreetSearchMode.FULLTEXT,true);
		assertEquals("the street name should not match if a part of the name is given and street search mode is "+StreetSearchMode.FULLTEXT,0,nearestStreet.size());
	
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"Kenedy",StreetSearchMode.FULLTEXT,true);
	assertEquals("the street name should  match if a name is given with an entire word and street search mode is "+StreetSearchMode.FULLTEXT,1,nearestStreet.size());
	
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"Kenedy john",StreetSearchMode.FULLTEXT,true);
	assertEquals("the street name should match if a name is given with more than one entire word and street search mode is "+StreetSearchMode.FULLTEXT,1,nearestStreet.size());
	
	
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"Kenedy smith",StreetSearchMode.FULLTEXT,true);
	assertEquals("the street name should not match if only one word is given and street search mode is "+StreetSearchMode.FULLTEXT,0,nearestStreet.size());

	//test nullpoint
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(null, 10000, 1, 1, null, null,"John",StreetSearchMode.CONTAINS,true);
	assertEquals(1,nearestStreet.size());
	assertEquals(streetOSM2.getGid(),nearestStreet.get(0).getGid());
	Assert.assertNull("When the point is null, distance field should be null",nearestStreet.get(0).getDistance());
	}
	
	
	//test name with contains
	assertEquals(0,openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null,null, "unknow name",StreetSearchMode.CONTAINS,true).size());
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"John",StreetSearchMode.CONTAINS,true);
	assertEquals(1,nearestStreet.size());
	assertEquals(streetOSM2.getGid(),nearestStreet.get(0).getGid());
	
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"john keN",StreetSearchMode.CONTAINS,true);
	assertEquals("the name should be case insensitive",1,nearestStreet.size());
	assertEquals(streetOSM2.getGid(),nearestStreet.get(0).getGid());
	
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"keN",StreetSearchMode.CONTAINS,true);
	assertEquals("the street name should match if a part of the name is given and street search mode is "+StreetSearchMode.CONTAINS,1,nearestStreet.size());

	
	//test OneWay
	assertEquals(0,openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null,true, null,null,true).size());
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null,false,null,null,true);
	assertEquals(1,nearestStreet.size());
	assertEquals(streetOSM2.getGid(),nearestStreet.get(0).getGid());
	
	//test pagination
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 2, null,null, null,null,true);
	assertEquals(2,nearestStreet.size());
	
	//test Order
	nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 2, null,null, null,null,true);
	assertEquals(2,nearestStreet.size());
	Double firstDist = nearestStreet.get(0).getDistance();
	Double secondDist = nearestStreet.get(1).getDistance();
	assertTrue("result should be sorted by distance : "+firstDist +"  should be < " +secondDist ,firstDist < secondDist);
	
	
    
    }

    @Test
    public void testCountEstimate(){
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	OpenStreetMap streetOSM2 = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
	long minGid = 1000L;
	streetOSM.setGid(minGid);
	long maxGid = 1500L;
	streetOSM2.setGid(maxGid);
	
	openStreetMapDao.save(streetOSM);
	openStreetMapDao.save(streetOSM2);
	
	long estimateCount = openStreetMapDao.countEstimate();
	Assert.assertEquals("countestimate should return the max gid, the estimation is based on the fact that gid are sequential)",maxGid-minGid+1, estimateCount);
	
    }
    
    @Test
    public void testCount(){
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	OpenStreetMap streetOSM2 = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
	long minGid = 1000L;
	streetOSM.setGid(minGid);
	long maxGid = 1500L;
	streetOSM2.setGid(maxGid);
	
	openStreetMapDao.save(streetOSM);
	openStreetMapDao.save(streetOSM2);
	
	long count = openStreetMapDao.count();
	Assert.assertEquals(2, count);
	
    }
    
    @Test
    public void testCountEstimateWithOutStreetInDatabase(){
	long estimateCount = openStreetMapDao.countEstimate();
	Assert.assertEquals("countestimate should return the max gid, the estimation is based on the fact that the importer start the gid to 0",0L, new Long(estimateCount).intValue());
	
    }


    
    @Test
    public void testGetByGidShouldRetrieveIfEntityExists(){
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	OpenStreetMap retrieveOSM = openStreetMapDao.getByGid(streetOSM.getGid());
	assertNotNull("getByGid should not return null if the entity exists",retrieveOSM);
	assertEquals("getByGid should return the entity if the entity exists",streetOSM, retrieveOSM);
	
    }
    
    @Test
    public void testGetByOpenstreetMapId(){
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	Long openstreetmapId = 12345678L;
	streetOSM.setOpenstreetmapId(openstreetmapId );
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	OpenStreetMap retrieveOSM = openStreetMapDao.getByOpenStreetMapId(openstreetmapId);
	assertNotNull("getByOpenStreetMapId should not return null if the entity exists",retrieveOSM);
	assertEquals("getByOpenStreetMapId should return the entity if the entity exists",streetOSM.getId(), retrieveOSM.getId());
	
    }
    
    

    @Test
    public void testGetByOpenstreetMapId_withTwoStreets(){
    	//sometimes a streets starts in one country and ends in an other so there is two streets with the same id
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	Long openstreetmapId = 12345678L;
	streetOSM.setOpenstreetmapId(openstreetmapId );
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	OpenStreetMap streetOSM2 = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	streetOSM2.setGid(3567L);
	streetOSM2.setOpenstreetmapId(openstreetmapId );
	openStreetMapDao.save(streetOSM2);
	assertNotNull(openStreetMapDao.get(streetOSM2.getId()));
	
	OpenStreetMap retrieveOSM = openStreetMapDao.getByOpenStreetMapId(openstreetmapId);
	assertNotNull("getByOpenStreetMapId should not return null if the entity exists",retrieveOSM);
	//we don't know the one that will be return , just check it is not null
	//assertEquals("getByOpenStreetMapId should return the entity if the entity exists",streetOSM.getId(), retrieveOSM.getId());
	
    }
    
    @Test
    public void testSaveShouldsaveLongName(){
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	String longString = RandomStringUtils.random(StringHelper.MAX_STRING_INDEXABLE_LENGTH+1,new char[] {'e'});
	Assert.assertEquals("the string to test is not of the expected size the test will fail",StringHelper.MAX_STRING_INDEXABLE_LENGTH+1, longString.length());
	streetOSM.setName(longString);
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	OpenStreetMap retrieveOSM = openStreetMapDao.getByGid(streetOSM.getGid());
	assertNotNull("getByGid should not return null if the entity exists",retrieveOSM);
	assertEquals("getByGid should return the entity if the entity exists",streetOSM, retrieveOSM);
	
    }
    
    @Test
    public void testGetByGidShouldReturnNullIfEntityDoesnTExist(){
	OpenStreetMap retrieveOSM = openStreetMapDao.getByGid(1L);
	assertNull("getByGid should return null if the entity doesn't exist",retrieveOSM);
	
    }
    
    @Test
    public void testGetByGidShouldThrowsIfGidIsNull(){
	try {
	    openStreetMapDao.getByGid(null);
	    fail("getByGid should throws if gid is null");
	} catch (RuntimeException e) {
	    	//ok
	}
	
    }
    
    
    @Test
    public void testUpdateTS_vectorColumnForStreetNameSearch(){
    	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
    	openStreetMapDao.save(streetOSM);
    	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
    	    	
    	Point searchPoint = GeolocHelper.createPoint(30.1F, 30.1F);
    	
    	List<StreetDistance> nearestStreet = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 10000, 1, 1, null, null,"John",StreetSearchMode.CONTAINS,true);
    	assertEquals(0,nearestStreet.size());
    	
    	int numberOfLineUpdated = openStreetMapDao.updateTS_vectorColumnForStreetNameSearch();
    		assertEquals("It should have 1 lines updated : streetosm for fulltext",1, numberOfLineUpdated);
    	
    }
    
    
    
    @Test
    public void testCreateSpatialIndexesShouldNotThrow(){
    	openStreetMapDao.createSpatialIndexes();
    }
    
    @Test
    public void testCreateFulltextIndexesShouldNotThrow(){
    	openStreetMapDao.createFulltextIndexes();
    }
    

    
    @Test
    public void testSaveShouldCallEventManager(){
	OpenStreetMap openStreetMap = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	OpenStreetMapDao openStreetMapDao = new OpenStreetMapDao();
	
	EventManager mockEventManager = EasyMock.createMock(EventManager.class);
	mockEventManager.handleEvent((GisFeatureStoredEvent)EasyMock.anyObject());
	EasyMock.replay(mockEventManager);
	
	StreetFactory mockStreetFactory = EasyMock.createMock(StreetFactory.class);
	EasyMock.expect(mockStreetFactory.create(openStreetMap)).andReturn(new Street());
	EasyMock.replay(mockStreetFactory);
	
	HibernateTemplate mockHibernateTemplate = EasyMock.createMock(HibernateTemplate.class);
	mockHibernateTemplate.saveOrUpdate(EasyMock.anyObject());
	EasyMock.replay(mockHibernateTemplate);
	
	openStreetMapDao.setHibernateTemplate(mockHibernateTemplate );
	openStreetMapDao.setStreetFactory(mockStreetFactory);
	openStreetMapDao.setEventManager(mockEventManager);
	
	
	openStreetMapDao.save(openStreetMap);
	
	EasyMock.verify(mockEventManager);
	EasyMock.verify(mockStreetFactory);
	EasyMock.verify(mockHibernateTemplate);
    }
    
    @Test
    public void testRemoveShouldCallEventManager(){
	OpenStreetMap openStreetMap = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	OpenStreetMapDao openStreetMapDao = new OpenStreetMapDao();
	
	EventManager mockEventManager = EasyMock.createMock(EventManager.class);
	mockEventManager.handleEvent((GisFeatureDeletedEvent)EasyMock.anyObject());
	EasyMock.replay(mockEventManager);
	
	StreetFactory mockStreetFactory = EasyMock.createMock(StreetFactory.class);
	EasyMock.expect(mockStreetFactory.create(openStreetMap)).andReturn(new Street());
	EasyMock.replay(mockStreetFactory);
	
	HibernateTemplate mockHibernateTemplate = EasyMock.createMock(HibernateTemplate.class);
	mockHibernateTemplate.delete((EasyMock.anyObject()));
	EasyMock.replay(mockHibernateTemplate);
	
	openStreetMapDao.setHibernateTemplate(mockHibernateTemplate );
	openStreetMapDao.setStreetFactory(mockStreetFactory);
	openStreetMapDao.setEventManager(mockEventManager);
	
	
	openStreetMapDao.remove(openStreetMap);
	
	EasyMock.verify(mockEventManager);
	EasyMock.verify(mockStreetFactory);
	EasyMock.verify(mockHibernateTemplate);
	
    }

    @Test
    public void testDeleteAllShouldCallEventManager(){
	OpenStreetMapDao openStreetMapDao = new OpenStreetMapDao();
	
	EventManager mockEventManager = EasyMock.createMock(EventManager.class);
	mockEventManager.handleEvent(new PlaceTypeDeleteAllEvent(Street.class));
	EasyMock.replay(mockEventManager);
	
	
	HibernateTemplate mockHibernateTemplate = EasyMock.createMock(HibernateTemplate.class);
	EasyMock.expect(mockHibernateTemplate.execute(((HibernateCallback)EasyMock.anyObject()))).andReturn(3);
	EasyMock.replay(mockHibernateTemplate);
	
	openStreetMapDao.setHibernateTemplate(mockHibernateTemplate );
	openStreetMapDao.setEventManager(mockEventManager);
	
	
	openStreetMapDao.deleteAll();
	
	EasyMock.verify(mockEventManager);
	EasyMock.verify(mockHibernateTemplate);
	
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPointIsMandatoryWhenSearchModeEqualsContains() {
	try {
	    openStreetMapDao.getNearestAndDistanceFrom(null, 1D, 1, 10, StreetType.BRIDLEWAY, true, "foo", StreetSearchMode.CONTAINS, true);
	    fail("Point is required when searchmode= " + StreetSearchMode.CONTAINS+". An exception should have been thrown");
	} catch (IllegalArgumentException e) {
	    // OK
	}

    }
    
    @Test
    public void testSaveCascadeHousenumber(){
    	HouseNumber houseNumber = GisgraphyTestHelper.createHouseNumber();
    	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
    	//houseNumber.setStreet(street);
    	street.addHouseNumber(houseNumber);
    	street = openStreetMapDao.save(street);
    	Assert.assertNotNull(houseNumber.getId());
    	
    	
    	OpenStreetMap retrievedStreet = openStreetMapDao.get(street.getId());
    	SortedSet<HouseNumber> houseNumbers = retrievedStreet.getHouseNumbers();
		Assert.assertNotNull(houseNumbers);
    	Assert.assertEquals("the street should have the housenumber associated",1, houseNumbers.size());
    }
    
    @Test
    public void testGetNearestFrom() {
    LineString shape = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.99999)");
    shape.setSRID(SRID.WGS84_SRID.getSRID());
	
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	streetOSM.setShape(shape);
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	//we create a multilineString a little bit closer than the first one 
	OpenStreetMap streetOSM2 = new OpenStreetMap();
	LineString shape2 = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.9154734)");
	shape2.setSRID(SRID.WGS84_SRID.getSRID());
	
	
	streetOSM2.setShape(shape2);
	streetOSM2.setGid(2L);
	//Simulate middle point
	streetOSM2.setLocation(GeolocHelper.createPoint(6.94130445F , 50.91544865F));
	streetOSM2.setOneWay(false);
	streetOSM2.setStreetType(StreetType.FOOTWAY);
	streetOSM2.setName("John Kenedy");
	streetOSM2.setOpenstreetmapId(123456L);
	HouseNumber houseNumber = new HouseNumber("3",GeolocHelper.createPoint(6.94130446F , 50.91544866F));
	houseNumber.setNumber("3");
	streetOSM2.addHouseNumber(houseNumber);
	
	StringHelper.updateOpenStreetMapEntityForIndexation(streetOSM2);
	openStreetMapDao.save(streetOSM2);
	assertNotNull(openStreetMapDao.get(streetOSM2.getId()));
	
	int numberOfLineUpdated = openStreetMapDao.updateTS_vectorColumnForStreetNameSearch();
    assertEquals("It should have 2 lines updated : (streetosm +streetosm2) for fulltext",2, numberOfLineUpdated);
	
	Point searchPoint = GeolocHelper.createPoint(6.9412748F, 50.9155829F);
	
	OpenStreetMap nearestStreet = openStreetMapDao.getNearestFrom(searchPoint);
	List<StreetDistance> list = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 1000, 1,1, null, null, null, null, true);
	assertEquals("The street is not the expected one, there is probably a problem with the distance",streetOSM2,nearestStreet);
	Double distanceFromNearestPointOnStreet = list.get(0).getDistance();
	double distanceFromMiddleOfStreet = GeolocHelper.distance(searchPoint, nearestStreet.getLocation());
	//System.out.println("distanceFromMiddleOfStreet="+distanceFromMiddleOfStreet+", distanceFromNearestPointOnStreet="+distanceFromNearestPointOnStreet);
	Assert.assertTrue("the distance from the middle of the street should be greater than the one from the nearest point",distanceFromMiddleOfStreet>distanceFromNearestPointOnStreet);
	Assert.assertNotNull("The housenumbers shouldBe retrieved",nearestStreet.getHouseNumbers());
	Assert.assertEquals("The housenumbers shouldBe retrieved",1,nearestStreet.getHouseNumbers().size());
    }
    
    @Test
    public void testGetNearestFromShouldFilterEmptyName() {
    LineString shape = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.99999)");
    shape.setSRID(SRID.WGS84_SRID.getSRID());
	
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	streetOSM.setShape(shape);
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	//we create a multilineString a little bit closer than the first one 
	OpenStreetMap streetOSM2 = new OpenStreetMap();
	LineString shape2 = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.9154734)");
	shape2.setSRID(SRID.WGS84_SRID.getSRID());
	
	
	streetOSM2.setShape(shape2);
	streetOSM2.setGid(2L);
	//Simulate middle point
	streetOSM2.setLocation(GeolocHelper.createPoint(6.94130445F , 50.91544865F));
	streetOSM2.setOneWay(false);
	streetOSM2.setStreetType(StreetType.FOOTWAY);
	streetOSM2.setName(null);
	streetOSM2.setOpenstreetmapId(123456L);
	HouseNumber houseNumber = new HouseNumber("3",GeolocHelper.createPoint(6.94130446F , 50.91544866F));
	houseNumber.setNumber("3");
	streetOSM2.addHouseNumber(houseNumber);
	
	StringHelper.updateOpenStreetMapEntityForIndexation(streetOSM2);
	openStreetMapDao.save(streetOSM2);
	assertNotNull(openStreetMapDao.get(streetOSM2.getId()));
	openStreetMapDao.count();
	int numberOfLineUpdated = openStreetMapDao.updateTS_vectorColumnForStreetNameSearch();
    assertEquals("It should have 1 lines updated : (streetosm +streetosm2) for fulltext",1, numberOfLineUpdated);
	
	Point searchPoint = GeolocHelper.createPoint(6.9412748F, 50.9155829F);
	
	OpenStreetMap nearestStreetFilterEmptyName = openStreetMapDao.getNearestFrom(searchPoint, false, true);
	OpenStreetMap nearestStreet = openStreetMapDao.getNearestFrom(searchPoint, false, false);
	
	assertNull("no street should be return when we filter name",nearestStreetFilterEmptyName);
	
	List<StreetDistance> list = openStreetMapDao.getNearestAndDistanceFrom(searchPoint, 1000, 1,1, null, null, null, null, true);
	assertEquals("The street is not the expected one, there is probably a problem with the distance",streetOSM2,nearestStreet);
	Double distanceFromNearestPointOnStreet = list.get(0).getDistance();
	double distanceFromMiddleOfStreet = GeolocHelper.distance(searchPoint, nearestStreet.getLocation());
	//System.out.println("distanceFromMiddleOfStreet="+distanceFromMiddleOfStreet+", distanceFromNearestPointOnStreet="+distanceFromNearestPointOnStreet);
	Assert.assertTrue("the distance from the middle of the street should be greater than the one from the nearest point",distanceFromMiddleOfStreet>distanceFromNearestPointOnStreet);
	Assert.assertNotNull("The housenumbers shouldBe retrieved",nearestStreet.getHouseNumbers());
	Assert.assertEquals("The housenumbers shouldBe retrieved",1,nearestStreet.getHouseNumbers().size());
    }

    
    @Test
    public void testGetNearestRoadFrom() {
    LineString shape = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.99999)");
    shape.setSRID(SRID.WGS84_SRID.getSRID());
	
	OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	streetOSM.setShape(shape);
	openStreetMapDao.save(streetOSM);
	assertNotNull(openStreetMapDao.get(streetOSM.getId()));
	
	//we create a multilineString a little bit closest than the first one 
	OpenStreetMap streetOSM2 = new OpenStreetMap();
	LineString shape2 = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.9154734)");
	shape2.setSRID(SRID.WGS84_SRID.getSRID());
	
	
	streetOSM2.setShape(shape2);
	streetOSM2.setGid(2L);
	//Simulate middle point
	streetOSM2.setLocation(GeolocHelper.createPoint(6.94130445F , 50.91544865F));
	streetOSM2.setOneWay(false);
	streetOSM2.setStreetType(StreetType.SECONDARY);
	streetOSM2.setName("John Kenedy");
	streetOSM2.setOpenstreetmapId(123456L);
	HouseNumber houseNumber = new HouseNumber("3",GeolocHelper.createPoint(6.94130446F , 50.91544866F));
	houseNumber.setNumber("3");
	streetOSM2.addHouseNumber(houseNumber);
	
	StringHelper.updateOpenStreetMapEntityForIndexation(streetOSM2);
	openStreetMapDao.save(streetOSM2);
	assertNotNull(openStreetMapDao.get(streetOSM2.getId()));
	
	int numberOfLineUpdated = openStreetMapDao.updateTS_vectorColumnForStreetNameSearch();
    assertEquals("It should have 2 lines updated : (streetosm +streetosm2) for fulltext",2, numberOfLineUpdated);
	
	Point searchPoint = GeolocHelper.createPoint(6.9412748F, 50.9155829F);
	
	OpenStreetMap nearestRoad = openStreetMapDao.getNearestRoadFrom(searchPoint);
	assertEquals("The street is not the expected one, there is probably a problem with the distance",streetOSM2,nearestRoad);
	//System.out.println("distanceFromMiddleOfStreet="+distanceFromMiddleOfStreet+", distanceFromNearestPointOnStreet="+distanceFromNearestPointOnStreet);
	Assert.assertNotNull("The housenumbers shouldBe retrieved",nearestRoad.getHouseNumbers());
	Assert.assertEquals("The housenumbers shouldBe retrieved",1,nearestRoad.getHouseNumbers().size());
	
	//now we change the StreetType
	streetOSM2.setStreetType(StreetType.FOOTWAY);
	openStreetMapDao.save(streetOSM2);
	
	nearestRoad = openStreetMapDao.getNearestRoadFrom(searchPoint);
	Assert.assertNull("Road should be null because it is a footway street",nearestRoad);
    }

    @Test
    public void testGetShapeAsWKTByGId(){
    	 LineString shape = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.99999)");
    	    shape.setSRID(SRID.WGS84_SRID.getSRID());
    		
    		OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
    		streetOSM.setShape(shape);
    		openStreetMapDao.save(streetOSM);
    		assertNotNull(openStreetMapDao.get(streetOSM.getId()));
    	
    	String shapeAsWKT = this.openStreetMapDao.getShapeAsWKTByGId(null);
    	Assert.assertNull(shapeAsWKT);
    	
    	
    	shapeAsWKT = this.openStreetMapDao.getShapeAsWKTByGId(streetOSM.getGid());
    	Assert.assertEquals("LINESTRING(6.9416088 50.9154239,6.9410001 50.99999)", shapeAsWKT);
    }
    
    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }
    
}

