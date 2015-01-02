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
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.URLUtils;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * test cityDao and GenericDao
 */
public class CityDaoTest extends AbstractIntegrationHttpSolrTestCase {

    private ICityDao cityDao;

    private IGisFeatureDao gisFeatureDao;

    @Resource
    private GisgraphyTestHelper geolocTestHelper;

    
    // it is the genericDaotest apply to a city

   @Test
    public void testGetAllShouldRetrieveAllTheCityInTheDataStore() {
	int nbToInsert = 2;
	for (int i = 0; i < nbToInsert; i++) {
	    City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris"
		    + i, 3);
	    City savedParis = this.cityDao.save(paris);
	    City retrievedParis = this.cityDao.get(savedParis.getId());
	    assertNotNull(retrievedParis);
	    assertEquals(paris.getId(), retrievedParis.getId());
	}
	List<City> cities = this.cityDao.getAll();
	assertNotNull(cities);
	assertEquals(nbToInsert, cities.size());
    }

    @Test
    public void testCitiesShouldbeSavedInABatch() {
	int nbToInsert = 10;
	for (int i = 0; i < nbToInsert; i++) {
	    City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris"
		    + i, 0);
	    City savedParis = this.cityDao.save(paris);
	    City retrievedParis = this.cityDao.get(savedParis.getId());
	    assertNotNull(retrievedParis);
	    assertEquals(paris.getId(), retrievedParis.getId());
	}
	List<City> cities = this.cityDao.getAll();
	assertNotNull(cities);
	assertEquals(nbToInsert, cities.size());
    }

    @Test
    public void testGetAllpaginateShouldPaginate() {
	int nbToInsert = 10;
	int from = 3;
	int max = 5;
	// save 0,1,2,3,4,5,6,7,8,9
	for (int i = 0; i < nbToInsert; i++) {
	    City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris"
		    + i, 0);
	    City savedParis = this.cityDao.save(paris);
	    City retrievedParis = this.cityDao.get(savedParis.getId());
	    assertNotNull(retrievedParis);
	    assertEquals(paris.getId(), retrievedParis.getId());
	}
	List<City> cities = this.cityDao.getAllPaginate(from, max);
	// should be 2,3,4,5,6
	assertNotNull(cities);
	assertEquals(max, cities.size());
	// check values
	for (int i = 0; i < cities.size(); i++) {
	    assertEquals("paris" + (i + from - 1), cities.get(i).getName());
	}
    }

    @Test
    public void testGetAllpaginateShouldNotConsiderFromIfItIsLessThan1() {
	int nbToInsert = 10;
	int from = 0;
	int max = 5;
	// save 0,1,2,3,4,5,6,7,8,9
	for (int i = 0; i < nbToInsert; i++) {
	    City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris"
		    + i, 0);
	    City savedParis = this.cityDao.save(paris);
	    City retrievedParis = this.cityDao.get(savedParis.getId());
	    assertNotNull(retrievedParis);
	    assertEquals(paris.getId(), retrievedParis.getId());
	}
	List<City> cities = this.cityDao.getAllPaginate(from, max);
	// should be 0,1,2,3,4
	assertNotNull(cities);
	assertEquals(max, cities.size());
	// check values
	for (int i = 0; i < cities.size(); i++) {
	    assertEquals("paris" + (i), cities.get(i).getName());
	}
    }

    @Test
    public void testGetAllpaginateShouldNotConsiderMaxIfItIsLessOrEqualsTo0() {
	int nbToInsert = 10;
	int from = 3;
	int max = 0;
	// save 0,1,2,3,4,5,6,7,8,9
	for (int i = 0; i < nbToInsert; i++) {
	    City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris"
		    + i, 0);
	    City savedParis = this.cityDao.save(paris);
	    City retrievedParis = this.cityDao.get(savedParis.getId());
	    assertNotNull(retrievedParis);
	    assertEquals(paris.getId(), retrievedParis.getId());
	}
	List<City> cities = this.cityDao.getAllPaginate(from, max);
	// should be 2,3,4,5,6,7,8,9
	assertNotNull(cities);
	assertEquals(8, cities.size());
	// check values
	for (int i = 0; i < cities.size(); i++) {
	    assertEquals("paris" + (i + from - 1), cities.get(i).getName());
	}
    }

    @Test
    public void testSaveShouldSaveTheInheritedGisFeature() {
	City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris", 0);
	City savedParis = this.cityDao.save(paris);
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());
	GisFeature retrievedgisFeature = this.gisFeatureDao.get(retrievedParis
		.getId());
	assertNotNull(retrievedgisFeature);
    }

    @Test
    public void testsaveCityInABatchShouldCascadeAlternateNames() {
	int nbToInsert = 20;
	for (int i = 0; i < nbToInsert; i++) {
	    City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris"
		    + i, 3);
	    City savedParis = this.cityDao.save(paris);
	    City retrievedParis = this.cityDao.get(savedParis.getId());
	    assertNotNull(retrievedParis);
	    assertEquals(paris.getId(), retrievedParis.getId());
	}
	List<City> cities = this.cityDao.getAll();
	assertNotNull(cities);
	assertEquals(nbToInsert, cities.size());
    }

    @Test
    public void testSaveCityShouldCascadeAlternateNames() {
	City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris", 3);
	assertNotNull(paris.getAlternateNames());
	assertEquals(3, paris.getAlternateNames().size());
	City savedParis = this.cityDao.save(paris);
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());
	assertNotNull(retrievedParis.getAlternateNames());
	assertEquals(3, retrievedParis.getAlternateNames().size());
    }

    @Test
    public void testGetShouldRetrieveNullIfTheSpecifiedIdDoesntExists() {
	City city = this.cityDao.get(100000L);
	assertEquals(null, city);
    }

    @Test
    public void testGetShouldRetrieveCorrectData() {
	City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris", 0);
	City savedParis = this.cityDao.save(paris);
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());
	GisFeature retrievedgisFeature = this.gisFeatureDao.get(retrievedParis
		.getId());
	assertNotNull(retrievedgisFeature);
    }

    @Test
    public void testGetAllShouldRetrieveanEmptyListIfNoCitiesInTheDatastore() {
	List<City> cities = this.cityDao.getAll();
	assertEquals(0, cities.size());
    }

    @Test
    public void testExistsShouldReturnFalseWhenNoCityWithTheSpecifiedIdExists() {
	assertFalse(this.cityDao.exists(-1L));
    }

    @Test
    public void testListByNameShouldRetrieveTheCorrectCity() {
	City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris", 3);
	City savedParis = this.cityDao.save(paris);
	List<City> results = this.cityDao.listByName("paris");
	assertNotNull(results);
	assertEquals(1, results.size());
	assertEquals(savedParis, results.get(0));
    }

    @Test
    public void testListByNameShouldNotRetrieveNullIfNoCityExistsWithTheSpecifiedName() {
	List<City> results = this.cityDao.listByName("paris");
	assertNotNull(results);
    }

    @Test
    public void testRemoveShouldRemoveTheCity() {
	// create and save city
	City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris", 3);
	City savedParis = this.cityDao.save(paris);

	// check it is saved
	Long id = savedParis.getId();
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());
	// remove city
	this.cityDao.remove(savedParis);
	// check city is removed
	City retrievedParisafterRemove = this.cityDao.get(id);
	assertNull(retrievedParisafterRemove);
    }

    @Test
    public void testRemoveCityShouldRemoveTheCityAndTheInheritedGisFeatureInCascade() {
	City paris = GisgraphyTestHelper.createCityWithAlternateNames("paris", 3);
	// save city
	City savedParis = this.cityDao.save(paris);
	// chek city is well saved
	Long id = savedParis.getId();
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());
	// check gisFeature is well saved
	Long savedGisFeatureId = retrievedParis.getId();
	GisFeature savedGisFeature = this.gisFeatureDao.get(savedGisFeatureId);
	assertNotNull(savedGisFeature);

	// remove city
	this.cityDao.remove(savedParis);

	// check city is removed
	City retrievedParisafterRemove = this.cityDao.get(id);
	assertEquals(null, retrievedParisafterRemove);

	// check gisFeature is remove
	GisFeature savedGisFeatureafterRemove = this.gisFeatureDao
		.get(savedGisFeatureId);
	assertNull(savedGisFeatureafterRemove);
    }

    // distance

    @Test
    public void testGetNearestAndDistanceFromShouldReturnCorrectDistance() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	List<GisFeatureDistance> results = this.cityDao
		.getNearestAndDistanceFrom(p1.getLocation(), 1000000);
	assertEquals(3, results.size());
	checkDistancePercentError(p1, results);

    }
    
    
    @Test
    public void testGetNearest() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	p1.addAlternateName(new AlternateName("Paname", AlternateNameSource.PERSONAL));
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	City result = this.cityDao
		.getNearest(p1.getLocation(),"FR",false, 1000000);
	Assert.assertNotNull(result);
	Assert.assertEquals(1, result.getAlternateNames().size());
	Assert.assertEquals(1, result.getZipCodes().size());
    }
    
    @Test
    public void testGetNearest_wrongCountryCode() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	p1.addAlternateName(new AlternateName("Paname", AlternateNameSource.PERSONAL));
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	City result = this.cityDao
		.getNearest(p1.getLocation(),"DE",false, 1000000);
	Assert.assertNull(result);
    }
    
    @Test
    public void testGetNearest_filterMunicipality() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	p1.addAlternateName(new AlternateName("Paname", AlternateNameSource.PERSONAL));
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	City result = this.cityDao
		.getNearest(p1.getLocation(),"FR",true, 1000000);
	Assert.assertNull(result);
    }

    @Test
    public void testGetNearestAndDistanceFromShouldReturnAfullFilledDTO() {
	City p1 = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(1L);
	p1.setAdm4Code("D4");
	p1.setAdm4Name("adm");


	this.cityDao.save(p1);
	List<GisFeatureDistance> results = this.cityDao
		.getNearestAndDistanceFrom(p1.getLocation(), 1000000);
	assertEquals(1, results.size());
	GisFeatureDistance gisFeatureDistance = results.get(0);
	assertEquals(p1.getFeatureId(), gisFeatureDistance.getFeatureId());
	assertEquals(p1.getName(), gisFeatureDistance.getName());
	assertEquals(p1.getAsciiName(), gisFeatureDistance.getAsciiName());
	assertEquals(p1.getLocation().toText(), gisFeatureDistance
		.getLocation().toText());
	assertEquals(p1.getLatitude(), gisFeatureDistance.getLat());
	assertEquals(p1.getLongitude(), gisFeatureDistance.getLng());
	assertEquals(p1.getAdm1Code(), gisFeatureDistance.getAdm1Code());
	assertEquals(p1.getAdm2Code(), gisFeatureDistance.getAdm2Code());
	assertEquals(p1.getAdm3Code(), gisFeatureDistance.getAdm3Code());
	assertEquals(p1.getAdm4Code(), gisFeatureDistance.getAdm4Code());
	assertEquals(p1.getAdm1Name(), gisFeatureDistance.getAdm1Name());
	assertEquals(p1.getAdm2Name(), gisFeatureDistance.getAdm2Name());
	assertEquals(p1.getAdm3Name(), gisFeatureDistance.getAdm3Name());
	assertEquals(p1.getAdm4Name(), gisFeatureDistance.getAdm4Name());
	assertEquals(p1.getFeatureClass(), gisFeatureDistance.getFeatureClass());
	assertEquals(p1.getFeatureCode(), gisFeatureDistance.getFeatureCode());
	assertEquals(p1.getCountryCode(), gisFeatureDistance.getCountryCode());
	assertEquals(p1.getPopulation(), gisFeatureDistance.getPopulation());
	assertEquals(p1.getElevation(), gisFeatureDistance.getElevation());
	assertEquals(p1.getGtopo30(), gisFeatureDistance.getGtopo30());
	assertEquals(p1.getTimezone(), gisFeatureDistance.getTimezone());
	assertEquals("gisfeatureDistance should have the same number of zipCodes as the original features",p1.getZipCodes().size(),
			gisFeatureDistance.getZipCodes().size());
	Iterator<ZipCode> iterator = p1.getZipCodes().iterator();
	assertTrue(gisFeatureDistance.getZipCodes().contains(iterator.next().getCode()));
	assertTrue(gisFeatureDistance.getZipCodes().contains(iterator.next().getCode()));
	assertEquals("city", gisFeatureDistance.getPlaceType());
	// check transcient field
	assertEquals(URLUtils.createCountryFlagUrl(p1.getCountryCode()),
		gisFeatureDistance.getCountry_flag_url());
	assertEquals(URLUtils
		.createGoogleMapUrl(p1.getLocation()),
		gisFeatureDistance.getGoogle_map_url());
	assertEquals(URLUtils.createYahooMapUrl(p1.getLocation()),
		gisFeatureDistance.getYahoo_map_url());
	assertNotNull(gisFeatureDistance.getDistance());

	checkDistancePercentError(p1, results);

    }
    
    @Test
    public void testGetNearestAndDistanceFromShouldPaginateWhenThereIsMoreThanOneZipCodes() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("vanves", 48.82F, 2.289F, 2L);
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 3L);
	p2.addZipCode(new ZipCode("75000"));
	p2.addZipCode(new ZipCode("75001"));
	p2.addZipCode(new ZipCode("75002"));
	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	List<GisFeatureDistance> results = this.cityDao
		.getNearestAndDistanceFrom(p1.getLocation(), 1000000, 1, 2,true, false);
	assertEquals(2, results.size());
	// check values and sorted
	assertEquals(p1.getName(), results.get(0).getName());
	assertEquals(p2.getName(), results.get(1).getName());
    }

    @Test
    public void testgetNearestAndDistanceFromShouldPaginate() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	List<GisFeatureDistance> results = this.cityDao
		.getNearestAndDistanceFrom(p1.getLocation(), 1000000, 2, 5,true, false);
	assertEquals(2, results.size());
	// check values and sorted
	assertEquals(p3.getName(), results.get(0).getName());
	assertEquals(p2.getName(), results.get(1).getName());

    }
    
    @Test
    public void testgetNearestAndDistanceShouldSetPlacetypeIfFeatureClassCodeIsNotSet() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	p2.setFeatureClass(null);
	p2.setFeatureCode(null);

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	List<GisFeatureDistance> results = this.cityDao
		.getNearestAndDistanceFrom(p1.getLocation(), 1000000, 2, 5,true, false);
	assertEquals(1, results.size());
	// check values and sorted
	assertEquals(p2.getClass().getSimpleName().toLowerCase(), results.get(0).getPlaceType());

    }

    @Test
    public void testGetNearestAndDistanceFromGisFeatureShouldReturnCorrectDistance() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	// for city dao
	List<GisFeatureDistance> results = this.cityDao
		.getNearestAndDistanceFromGisFeature(p1, 1000000, -1, -1,true);
	assertEquals(2, results.size());
	checkDistancePercentError(p1, results);

	results = this.cityDao.getNearestAndDistanceFromGisFeature(p1, 100,true);
	assertNotNull("getNearestAndDistanceFrom should never return null",
		results);
	assertTrue(results.isEmpty());

	results = this.cityDao.getNearestAndDistanceFromGisFeature(p1, 22000,true);
	assertTrue(results.isEmpty());

	results = this.cityDao.getNearestAndDistanceFromGisFeature(p1, 23000,true);
	assertEquals(1, results.size());
	checkDistancePercentError(p1, results);
	// will try with the gisFeature implementation
	this.cityDao.remove(p1);
	this.cityDao.remove(p2);
	this.cityDao.remove(p3);
	assertEquals(0, this.cityDao.getAll().size());

	GisFeature p4 = GisgraphyTestHelper.createGisFeature("_paris", 48.86667F,
		2.3333F, 1L);
	GisFeature p5 = GisgraphyTestHelper.createGisFeature("_bordeaux",
		44.83333F, -0.56667F, 3L);
	GisFeature p6 = GisgraphyTestHelper.createGisFeature("_goussainville",
		49.01667F, 2.46667F, 2L);

	this.gisFeatureDao.save(p4);
	this.gisFeatureDao.save(p5);
	this.gisFeatureDao.save(p6);
	assertEquals(3, this.gisFeatureDao.getAll().size());

	// for gisfeatureDao because there is two implementation

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		1000000,true);
	assertEquals(2, results.size());
	checkDistancePercentError(p4, results);

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		100,true);
	assertNotNull("getNearestAndDistanceFrom should never return null",
		results);
	assertTrue(results.isEmpty());

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		22000,true);
	assertTrue(results.isEmpty());

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		23000,true);
	assertEquals(1, results.size());
	checkDistancePercentError(p4, results);

    }

    @Test
    public void testgetNearestAndDistanceFromGisFeatureShouldPaginate() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);// N
	// 48°
	// 52'
	// 0''
	// 2°
	// 20'
	// 0''
	// E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);// 44
	// 50 0
	// N; 0
	// 34 0
	// W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);// N
	// 49°
	// 1'
	// 0''
	// E 2°
	// 28'
	// 0''

	this.gisFeatureDao.save(p1);
	this.gisFeatureDao.save(p2);
	this.gisFeatureDao.save(p3);
	// for city dao
	List<GisFeatureDistance> results = this.cityDao
		.getNearestAndDistanceFromGisFeature(p1, 1000000, 1, 5,true);
	assertEquals(2, results.size());
	assertEquals(p3.getName(), results.get(0).getName());
	assertEquals(p2.getName(), results.get(1).getName());

	results = this.cityDao.getNearestAndDistanceFromGisFeature(p1, 1000000,
		2, 5,true);
	assertEquals(1, results.size());
	assertEquals(p2.getName(), results.get(0).getName());

	results = this.cityDao.getNearestAndDistanceFromGisFeature(p1, 1000000,
		1, 1,true);
	assertEquals(1, results.size());
	assertEquals(p3.getName(), results.get(0).getName());

	results = this.cityDao.getNearestAndDistanceFromGisFeature(p1, 1000000,
		0, 1,true);
	assertEquals(1, results.size());
	assertEquals(p3.getName(), results.get(0).getName());

	results = this.cityDao.getNearestAndDistanceFromGisFeature(p1, 1000000,
		1, 0,true);
	assertEquals(2, results.size());
	assertEquals(p3.getName(), results.get(0).getName());
	assertEquals(p2.getName(), results.get(1).getName());
	// remove city and replace with gisFeature
	this.cityDao.remove(p1);
	this.cityDao.remove(p2);
	this.cityDao.remove(p3);
	assertEquals(0, this.cityDao.getAll().size());

	GisFeature p4 = GisgraphyTestHelper.createCity("paris", 48.86667F,
		2.3333F, 1L);// N
	// 48°
	// 52'
	// 0''
	// 2°
	// 20'
	// 0''
	// E
	GisFeature p5 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F,
		-0.56667F, 3L);// N 44
	// 50 0
	// ; 0
	// 34 0
	// W
	GisFeature p6 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);// N
	// 49°
	// 1'
	// 0''
	// E 2°
	// 28'
	// 0''

	this.gisFeatureDao.save(p4);
	this.gisFeatureDao.save(p5);
	this.gisFeatureDao.save(p6);
	assertEquals(3, this.gisFeatureDao.getAll().size());

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		1000000, 2, 5,true);
	assertEquals(1, results.size());
	assertEquals(p5.getName(), results.get(0).getName());

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		1000000, 1, 1,true);
	assertEquals(1, results.size());
	assertEquals(p6.getName(), results.get(0).getName());

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		1000000, 0, 1,true);
	assertEquals(1, results.size());
	assertEquals(p6.getName(), results.get(0).getName());

	results = this.gisFeatureDao.getNearestAndDistanceFromGisFeature(p4,
		1000000, 1, 0,true);
	assertEquals(2, results.size());
	assertEquals(p6.getName(), results.get(0).getName());
	assertEquals(p5.getName(), results.get(1).getName());

    }

    private void checkDistancePercentError(GisFeature p1,
	    List<GisFeatureDistance> results) {
	Double lastOne = null;
	for (GisFeatureDistance gisFeatureDistance : results) {
	    double calculatedDist = p1.distanceTo(gisFeatureDistance
		    .getLocation());
	    double retrieveDistance = gisFeatureDistance.getDistance();
	    if (lastOne != null) {
		assertNotNull(retrieveDistance);
		assertNotNull(lastOne);
		if (!(lastOne.doubleValue() <= retrieveDistance)) {
		    fail("The results are not sorted");
		}
	    }
	    double percent = (Math.abs(calculatedDist - retrieveDistance) * 100)
		    / Math.min(retrieveDistance, calculatedDist);
	    log.info("Distance difference beetween " + p1.getName() + " and "
		    + gisFeatureDistance.getName() + " is " + percent + "%");
	    if (calculatedDist > 0.001) {
		assertTrue(
			"The results are not at the expected distance : should be "
				+ calculatedDist + " but was "
				+ retrieveDistance + " (purcent error="
				+ percent + ")",
			(percent < GisgraphyTestHelper.DISTANCE_PURCENT_ERROR_ACCEPTED)
				|| (calculatedDist == retrieveDistance));// tolerence
	    }
	    lastOne = retrieveDistance;
	}

    }

    @Test
    public void testIsCityShouldReturnTrueIfItIsACity() {
	City city = GisgraphyTestHelper.createCityWithAlternateNames("paris", 0);
	assertTrue(city.isCity());
    }

    @Test
    public void testIsCityShouldReturnFalseIfItIsNotACity() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("",
		20F, 30F, null, 2);
	assertFalse(gisFeature.isCity());
    }

    public void testGetByFeatureIdsShouldOnlyReturnCities() {
	City city1 = GisgraphyTestHelper.createCity("cityGisFeature", null, null,
		100L);
	City city2 = GisgraphyTestHelper.createCity("cityGisFeature", null, null,
		200L);
	GisFeature gisFeature = GisgraphyTestHelper
		.createGisFeatureWithAlternateNames("gisfeature", 0);
	gisFeature.setFeatureId(300L);
	GisFeature gisFeature2 = GisgraphyTestHelper
		.createGisFeatureWithAlternateNames("gisfeature", 0);
	gisFeature2.setFeatureId(400L);

	this.gisFeatureDao.save(city1);
	this.gisFeatureDao.save(city2);
	this.gisFeatureDao.save(gisFeature);
	this.gisFeatureDao.save(gisFeature2);

	// check it is well saved
	List<GisFeature> gisFeatures = this.gisFeatureDao.getAll();
	assertNotNull(gisFeatures);
	assertEquals(4, gisFeatures.size());

	List<Long> ids = new ArrayList<Long>();
	ids.add(100L);
	ids.add(200L);
	ids.add(300L);
	List<City> gisByIds = this.cityDao.listByFeatureIds(ids);
	assertNotNull(gisByIds);
	assertEquals(2, gisByIds.size());

    }

    @Test
    public void testListByZipCodeShouldReturnCorrectValues() {
	City city1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F,
		1L);
	city1.addZipCode(new ZipCode("75003"));
	city1.setCountryCode("FR");
	City city2 = GisgraphyTestHelper.createCity("paris2", 48.86667F, 2.3333F,
		2L);
	city2.addZipCode(new ZipCode("75003"));
	city2.setCountryCode("EN");
	this.cityDao.save(city1);
	this.cityDao.save(city2);
	List<City> results = this.cityDao.listByZipCode("75003", null);
	assertEquals(2, results.size());
	results = this.cityDao.listByZipCode("75004", null);
	assertNotNull(results);
	assertEquals(0, results.size());
	results = this.cityDao.listByZipCode("75003", "fr");
	assertEquals(
		"ListByZipCode should be case insensitive for countrycode", 1,
		results.size());

    }
    
    
    @Test
    public void testGetByShape(){
    	City city1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F,
    			1L);
    	city1.setCountryCode("FR");
    	Geometry shape = GeolocHelper.convertFromHEXEWKBToGeometry("0103000020E6100000010000001C0200007E31A53F45FF1440038A479858C747406B5E7AA0BAFE1440E2AE5E4546C747409693F540D0FE14404F6B894B44C74740D272A087DAFE1440400CBE7A26C74740F7764B72C0FE1440D99EB4CB12C74740458C8D2F90FE1440EA70BE2209C74740A788C21D5EFE1440F3CF0CE203C74740F6622827DAFD1440B5746094FBC6474064C3E457BDFD1440E273CC1E0DC74740B23B93ECC7FC1440F37FA20D1BC74740044070F0E0FA144043CBBA7F2CC7474024FA10AF46F914409224085740C74740DFCD099057F614402175E04158C74740AD472B082FF31440236EA9DE75C74740DCEFF55A75F21440EF0D74A37EC74740CD199B6736F11440DFF9EA4F87C74740F401DC8717EF144022D390A79BC74740450603194BEA1440DB47B8DAD4C74740171EEA2DD4E81440D9E5A5ACF0C74740623DA4CE92E61440751EBA4505C847401B80B2CE9DE514403E1C6ED113C847406ED113E005E314409C3B551921C8474086CC9541B5E1144089F83DA022C8474005334BA71AE2144047BE011C31C84740D913B8D0F0E11440D90F0C7B35C847404F81824188E2144047D0F30247C84740F8883D59D6E214402F77C13B54C847407A162939CCDC1440729F6692A2C847408E36D8E5A5DC144024BA675DA3C847403727E5A4E6D91440D0D38041D2C84740E6A4E66157D914403609394AB9C8474055089A852CD914402450583DBBC84740D2B1DE03CFD81440D6C33CD0AFC8474037740EF9C2D714402B4C84B295C847403E51233EC1D71440C1594A9693C847407E9C1F35DCD61440105CE50984C84740BF07F9EAF4D51440333A7BC26DC84740FC500E0B5AD51440A320787C7BC84740663046240AD5144012DBDD0374C8474069DF81A6CAD414405FCE119E75C847408879A05FA5D41440749CDB847BC84740C09E0A13A1D41440A9AF9DDE7BC84740256E5F515BD4144012DE1E8480C84740139B34B2D0D31440FDD357A192C84740A1664815C5D314402C52BC2594C84740C0DB72E437D314408D5B71169BC8474071EBC9575DD214408850002082C8474053C83AC1A3D2144044A275F97BC84740B73DE6A848D21440F06C8FDE70C847401CE846FD9AD11440FD3D67C17AC84740114DEA1560D1144006E8AD7081C8474029B7483547D114408DA5A37785C8474099E02FC104D114402FD7EC9282C84740AAFAF087FAD01440874AC8AC83C84740654C0BA881D01440226239F878C84740FA0D130D52D01440616AF06371C84740E9E62CFD95CF1440380E61A17BC847403253FFC5D8CE1440A89432045BC8474008019E59B7CE14405640EB2B59C847403A9270C6D5CD1440B9A2395739C847409E52149E86CE144095BDA59C2FC847402E2DD96784CD1440D1C65BF80BC84740E81890BDDECD1440F10B546B06C84740E8C1DD59BBCD14403BFE0B0401C84740F9F9EFC16BCF1440950F41D5E8C747403FAF78EA91CE14400A4D124BCAC7474014D9BDCBFBCE1440DEB87B6FC2C7474028501FDCF8CE1440E54DD944C1C747409159075108CE144093F30F6CA6C74740C22DC48FD6CD14400054162AA4C74740B3A2618CA3CD14407B185A9D9CC747403E659016C2CD144052FB08579BC74740EDB4DA68A5CD14408E8FBBAD99C747403ED4111B87CD1440888153C48EC74740AAA1687979CD14402948C73082C7474043C70E2A71CD1440BE970C1181C74740CA3F8E4182CD1440B6357DD179C74740F4C7B4368DCD144052007B3B78C7474040D2F1E20ECD1440CA56F20C75C74740098F9147CBCC1440FA92D79475C74740BAD9C46E44CC14406231EA5A7BC74740C4D4963AC8CB1440852FA75F7DC74740950D6B2A8BCA1440132C0E677EC74740F24B58761CCA1440C4CA0D2B81C74740554A743BA0C91440816DB36785C74740370D9B125AC8144073A25D8594C74740350AEEAC82C51440E40DD539ABC74740F908466F02C51440E02AF46679C747406AE78FC426C51440286728EE78C74740553772384EC51440189D876E51C74740AE19749DA1C31440A7C7009A52C74740316FC44950C2144012F4BCC051C74740F6AFF6C143C2144081E5AD5F55C74740A6892DF30CC01440ACE39DE85FC747407049699148BE1440290EFB986AC7474062B4D83741BD144093A4106D6CC74740666E19CB4FBD144016BAB7C777C7474087B0D0BD3DBE1440CF97288C77C74740ECE7A8FE9CBD1440E3885A3FA2C74740A139A1C673BC1440D5676215CAC74740A8604326CFBB14402409675CDDC74740D924E428E5BA1440E847687000C847408408821145BA14408D4F5CE910C847400BEB6B02B1B914409FF7109E1AC84740743F4C67CCB91440DFAC1CB51DC84740DEF016A309B714406977483140C84740A4A60293C0B31440B21CD7755EC8474030325C78B9B31440004A8D1A5DC847400EA72787AAB31440123FB5B05DC847403A7CD28904B314407DE47BEB68C847409A43520B25B3144096664EF27EC84740C1B057B329B21440CFD1F4238FC84740CA49720A3CB11440EDB4DA68A5C84740B720A523CAB01440E75608ABB1C84740DD4834DCDBB01440EBBF18DBB5C847401B4EF454E2AD144000FF3971DEC847406425E65949AB144088FE64E7C8C94740BA86191A4FAC1440B79FE7AAD4C9474054CC9C8944AC144016D0FA4AD6C94740835C983BE9AB144020589FBCD9C9474011C1DDFE01AB144048F6AD7BD0C94740EE5F596952AA1440476CC19CEAC94740AD67AD0DBAA91440A3EFC91EFCC94740D99F1FA1C1A91440BA3FCD7FFEC94740FB0967B796A9144080E0E0C105CA47402CA4575E4DA9144011960C5B0ECA47400334000AE4A814409165C1C41FCA4740994FB1C5C9A814405CC8C8FE1ECA47403B02B859BCA8144096AC2FB720CA47402E3F709527A81440DC10887C3CCA47405A30F14751A71440E006C60B33CA47408DC8C1B68FA61440F9D9C87553CA4740079ACFB9DBA51440B5CF19074DCA4740E68DDD4FD7A41440E27A14AE47CA47400300112C58A41440A08B868C47CA4740847C75FAE6A314408609FE124CCA4740838D469968A31440AA5E23EE56CA474065D12AE917A314405F04D7265FCA4740CE5BD0D61CA31440ABF70AC160CA47402D2C13D962A21440D27BBEC172CA4740D3307C444CA1144086E2337E86CA474008A3A36659A114404F7B4ACE89CA4740D88BFC55DBA0144098569E9B91CA47404043499231A01440CA4054D0A3CA4740A2F48590F39E14401A33897AC1CA4740233B25D6989E1440855C4EAECECA47405EA7EC4F3D9D14405D7347FFCBCA474014ED2AA4FC9C1440FE14223BCACA474026F8F076CE98144098EAB8D04BCB4740FA916CBFC6971440A95E6D7B71CB474003ECA35357961440DF7B6E579ECB4740607E0283FF9014409E3358271DCC4740097250C24C8B144025DA441CA1CC47409EA7F0564C8B144029E73004A5CC4740C81231804B8B14404266C28AAECC4740BDEE63BB318B144089580E3EDECC47407CB5487FD48B14400AEC7B1EEDCC4740ECCD5E6C108C1440F4114251EACC47401A8057152B8C14406816574BF0CC4740D6A7C1D9528C14404867052BF3CC4740D31ADE077B8E14402FA3FDB4F6CC474000259930F58E1440F7216FB9FACC4740A0DDC60B8E8F144094E1783E03CD47405B0641ECF18F144024B149230BCD47400ED30847DA8F144081A7DAB80FCD4740C17630629F9014406D33BA281FCD47400427367A90911440B6ACB13A28CD47402D2C13D962921440021077F52ACD474056212FB5EF93144031D692E92BCD47407B53ECC3D5941440C76D8F392ACD4740E4F2C418FD951440E4AE14B82BCD4740F3BC0ADFB1961440EAFBBA1D2BCD4740C637143E5B971440286893C327CD474010EA2285B2981440B5E8F8C32ACD4740F2D3B837BF991440F5948BE722CD4740AE5AE37D669A1440E38BF67821CD4740FA556FC3DE9A14408C0AE6F91DCD4740B4C5DA84209C1440C5FC811722CD47403C6E5397E79C144054E57B4622CD4740606F078FDB9C14408BEA083B20CD4740D9DAB1C7FA9C1440A452EC681CCD4740AD0617E1DC9D144034805C870FCD474046C1429C3D9E144018B8978FFFCC4740EF8E311C749E14402C499EEBFBCC474056698B6B7C9E1440E6A210B7F9CC4740BF7F9829529E1440E110AAD4ECCC474054CD075FF39D14403D122F4FE7CC4740747FF5B86F9D14400953944BE3CC4740D08C34A7819D1440CB4DD4D2DCCC47400032BE79BB9F14402786E464E2CC47406849DB53CD9F14408E1546C4DECC474090DF36AEDAA01440AC79E981EACC47403EA0223DFBA0144021393EFFE2CC4740188DC6EB0BA2144025462AE7E6CC4740666EBE11DDA3144013578451FFCC4740B97768B345A51440CDA5023807CD4740CA2AB693D2A51440E878BB140DCD4740C2E3367579A61440481ADCD616CD474005BDDCCC8DA61440F07E260C14CD4740F17096ED9EA714401D3B4D0B03CD4740D7B09586D0A71440029F1F4608CD47400E805DF2E4A71440686AC82E07CD474025B20FB22CA8144099147A5803CD474061600C9BB7A814400C83E8FF0BCD4740749DA1139DA81440DA907F6610CD4740E2B3D02923A91440289364671BCD4740C9D2E2E71AA9144072D014F021CD47407E70E3BB39A91440E8CC2CF823CD4740D8416F3B7EA91440A321E3512ACD474057E1E01CBFA9144099D0C9F731CD47408A86D6D4C3A91440CE0B660234CD47409EE05119B5A91440A42A5C4535CD4740DDBCCC0B0BA914405333B5B63ACD47400848EAF307A91440D681621A3CCD47407108B02369AA14406E49B31E63CD4740EA25C632FDAA14403F4459AE6DCD4740500537AD6FAB1440B406EFAB72CD47405B00643266AC1440984AE4277ACD474053CDACA580AC1440324BF1A77CCD47407B53ECC3D5AC14409A0F632D99CD47407C04487E0EAD1440AA46544DB5CD47405636621C12AD1440D6E0229CBBCD474053454CE4DDAC1440730289DCC2CD4740C680368309AD1440757396FECACD4740AD16D86322AD144044BBAF6FCCCD47404FACF82B09AD1440E07DFA74E1CD474001F676F0B8AD14408BB5AE87E5CD4740C5A63A89ADAD1440583F918202CE4740FFB5BC72BDAD1440ABE408BE0ECE47404053AF5B04AE1440D0CCDDF824CE4740AA79D85592AE1440BE3F941E3CCE4740BA6702EA72AF14405958CBF852CE474046D6750360AF1440FF4E498C54CE474069965F611CAF144025C742194FCE4740E049B0DDE2AE1440F9382E3E60CE4740D28B7F44B8AE14401F44D72A66CE474081E37CFB84AD144086C88E3287CE4740C99640A5A5AD1440A203DC3D8ACE4740D11B936A44AD1440230D13B298CE474020A9746671AD1440434F1432A1CE4740BCF4E5BB4AAD144040654689A7CE4740517D31A53FAD144018E71489AECE47404520A8644AAD14403799A729B8CE4740B27D6DA23BAD1440B929D489BACE4740DAA4EC3E11AD1440AC47D04EBCCE4740E668441FE2AD1440D932CF00CDCE4740B42A1D07B9AE14402B76EA80DACE4740CD7B9C69C2AE1440A777F17EDCCE4740FC1BB4571FAF144010441669E2CE4740F3AC495C6CAF14408B598A3FE5CE47401A33897AC1AF14405E7A4501ECCE4740205498ADABB01440FD0CB963FBCE4740E94A04AA7FB01440FF4FB46103CF4740EE07E1760DAF1440F89D70C0F8CE47407A347ADA86AE1440CE4CD5E2F8CE474009C95DDFE2AC1440ADE4BE30F4CE47404AA8CFC42AAC14408EC5DBEFF5CE4740EA57DF652BAC1440EB45A3F1FACE4740FF4701FD19A91440CCB3379D09CF47408FC70C54C6A714409C01898109CF4740791CAB39E5A61440F621B94615CF4740B7A79A0FBEA614405CC8C8FE1ECF4740929735B1C0A714404F6848CB37CF474090B234A616A81440FCE82F4F42CF47406E1F4D501EA9144081E5AD5F55CF4740C6B4256195A914409944622761CF4740E0A52FDF55AA14408CAD56DC6ECF4740BA0155922BAB1440B0027CB779CF474031B9AC1D20AB1440D17EFF417FCF474014D3E01170AB14402CC5443987CF4740A8CBBD1B66AB1440D69A9C908CCF4740B11A4B581BAB144078C6AD388BCF474006476EF200AB1440D382BCC392CF47407080F4A853A91440B62792F991CF474058A2FD593DA91440187F36CD96CF4740CBE9A16C80A814402307DB3E9ACF4740CB8EE8F92EA814403C55192197CF4740B563343C07A814404E92F82697CF4740A1377062A3A714404544D6BF90CF4740E449777C20A614406CEAE1708BCF4740A4FD0FB056A51440540438BD8BCF4740057E9E5099A41440743804D891CF474033CDCF6806A414404533AA679CCF474051BA9976D6A314402BF3A0EAA1CF4740798A66AF88A31440ADBA5862AFCF4740E827E66A75A314402336B3F1BBCF4740708D19F219A214406B33A9FCC6CF47403AEAE8B81AA11440FA241C1FD2CF47408FF4B3A2179F144025ABC722F2CF47403845ECB8869E14406C515557F4CF47408D4C1B69049E14402C5D0BC4FCCF474024B6604E759D144069931EE10FD04740855BE3D81F9D1440FA23B14923D047403E0D73DDEF9B14403774B33F50D04740B96EEFBF3A9B14403C2C79E173D0474036142D2FAF9A1440328E36339FD0474051A15F00869A1440F6285C8FC2D04740514EB4AB909A144062026D61CCD0474095EC7D4F519A1440A20BEA5BE6D0474087A2E5E5559A1440BE5DE5BFF6D047402B57D350FE99144085285FD042D147400EF96706F1991440A2FFD42E5CD14740F0D1D160099A1440CC2BD7DB66D1474029C8748D3B9A144086EAE6E26FD147400EA320787C9B144055F6025889D14740DB3F0576EB9B144097EE096D94D1474018659181979C1440268458479FD147401C2CF75D6C9D1440AAE7EE84A8D14740119DAFA3BB9D14406CA3F08FADD1474000A13F7E809F144072BD12EDCFD14740595AFC5C23A014408027D2A2E3D147406C2409C215A014409052094FE8D147409C447353B99F1440DAC3036FEDD1474040016FDCBD9F144045D0E2D6EED147408B6C8C4237A01440AA1496D3F9D147400F08196DFAA01440D23131A715D24740B89B3B5519A11440191241E614D24740B4D5404EF3A11440C37A489D25D247402F22403C01A3144080153B7540D24740A1788489F5A41440A8F397CC67D247405F375B2F3CA51440F00A332372D24740073CD5C67DA81440C879A466B4D247406AE9C028F7A914405A057B5DD0D24740AFC44D57C1A914401D5DA5BBEBD247409A0D32C9C8A914409844076EEED24740214322C89CAA1440AB347392F7D2474069B16F82CAAB1440364A4D710BD34740512E32A605AC1440304ED76E16D34740527C218903AD1440190DCF0138D347406B0F7BA180AD1440CD086F0F42D3474013245B0295AE144093C9A99D61D34740B6B5CF74C0AF14404E0D349F73D3474004858B2661B014409354A69883D347400E011B6BDAB0144092B64CE19CD34740E76D11BD31B1144068203B14AAD34740B08971A36DB1144002DFC897ABD34740B05CCA541CB21440093A5AD592D34740AE7BD058A0B21440F4D8E08E81D34740C3482F6AF7B3144073B8FBC165D34740A4816962CBB41440C10A4B9759D34740118134AD5EB714408C61985D41D3474058C7968A32B814402A2158552FD34740600A6A53D0B81440C9B08A3732D347401E88D11852BA144051D6146D33D34740F038A000E5BA1440DB7DD81C31D34740729989D816BC1440B85F3E5931D347403FECE0BB28BD14402D42B11534D347401FE85729F3BD1440F21593ED32D347404E017A2B5CC01440BA2F67B62BD347405F20521DBCC01440095D1D5B2AD347409ED32CD0EEC01440FF36D5A425D3474048F2A66CA2C01440A9B8BB1814D347406730A1DD7CBE14402A66738BAFD24740912342C81EBE1440A6A3778599D24740B2BB404981BD144010B1C1C249D2474091A3946B65BD14403DBD529621D24740814EF7F01BBD1440B1CF52680FD24740FB0A2D46B8BC144007D15AD1E6D14740A36021CE1EBB144015843CCCA8D14740DD30C0F4ACBA1440F68079C894D147409E7DE5417ABA144045CBCBAB84D147407855568968BA144006F6984869D14740408BA548BEBA1440C001D2A34ED14740C744EFF906BB144033079C001ED1474013DF3FCC14B9144075D42C2BA8D04740C24362CCA7B81440234097BA75D04740D58DC1D4E0B7144076429E1331D0474080796DDB9CB714402DB4739A05D04740165454FD4AB7144084B064D872CF474020B6F468AAB714405CD8898164CF47408FF92587F4B81440D419CEEB3ECF4740CE678A4A34BA14404D6C3EAE0DCF47409ED497A59DBA1440A6A20CB0EACE47401C8AF150CABA14404F5DF92CCFCE4740442D173B2BBB14400DFD135CACCE4740F0B03F3F42BB14409DBD33DAAACE47407A7077D66EBB1440F1AFD46993CE474016A17D5127BD14400DA9A27895CE4740E3772EE7ADBD144056BDFC4E93CE4740CB9D3EA7C5BD1440B84DA72B8ECE4740CA54C1A8A4BE1440DFBAACD392CE4740514CDE0033BF144006E0FA0B98CE47407BA35698BEBF1440F9CFE4AC99CE474060ED8387C4C01440FB99D59695CE4740D111AFA18AC1144058456E3C8ECE474057DE44E33FC31440738577B988CE474028F38FBE49C31440C9E6AA798ECE47404CF9B59A1AC514408F3056E58CCE4740B6D5517A4BC614403B4668BA8DCE4740214E716605C81440B41B221395CE474022AD31E884C81440B884324399CE47404F475BF0FDC8144093043CC49ACE4740D5586721A6C91440E78CCD339BCE4740C2189128B4CC1440D39C610F92CE47402176A6D079CD14407443F8BC87CE474010D48448E1CD1440ADA2E47A80CE4740083B6AF1CECE1440DC0022B369CE4740CF58EA0F83CF1440B2135E8253CE47400B28D4D347D01440AC39403047CE474089DA9145F5D114402A86F5903ACE47404757E9EE3AD3144031A248522DCE474092C20655B4D3144064E2A0CE26CE4740B82D80DF75D4144072F5AD2017CE4740A2A476757DD51440145A7B44F1CD47401CDE6234E1D5144084A10E2BDCCD474026C1C01836D71440F3EA1C03B2CD4740DE859CAD72D71440C527F801A3CD47407128D76F81D714407168DB7A97CD47402FE7AD15C8D71440BBDB508C87CD4740F49F7FCC18D814403EB72B4F7BCD4740A9EAD44B42D81440A0138C9477CD4740524832AB77D8144005DF347D76CD4740357F4C6BD3D81440BCD80F0C7BCD474066016E71E8D814402705CC327ACD4740C1A442869FD914402491C71D80CD474055E0641BB8DB14408C2FDAE385CD4740673BF07BE5DB1440BCC4A2337ACD4740E91615CCF3DB1440A4D57F8C6FCD4740A6EFDA4A54DC14400FA0DFF76FCD47404E7FF62345DC14400F23298EA8CD474017DBA4A2B1DE14401C86EA419CCD47401C4BB3D4D5E014401E30B4DF7FCD47401250E10852E11440CDC5843D92CD47408D6BD7DF6DE114403BED84F299CD47408B9EA57565E114405FA40689A3CD474002FB438E08E11440261708A7AACD4740F9DD74CB0EE1144090C18A53ADCD4740844DF80038E1144003081F4AB4CD4740C6B01E5267E1144016319177B3CD47403FE2FCF26EE11440B66AD784B4CD4740B547B945AAE1144061634D1BC4CD4740087A032736E214403F07F1DCD6CD47404998B38872E31440E8082AF40BCE47404186E9D6C6E314409A85D10726CE4740F648DE944DE414406C6F01B221CE4740C6B5EBEFB6E414402BD1A3F32BCE47405D9C42F861E61440C26966D24BCE474022307B7EBDE71440D576B8C260CE47400E2EC2B92BE81440879A32816CCE47407801405FC4E814402A7C11C880CE4740921040C5CCE9144080E6BD7B91CE47405F984C158CEA1440D54FEE2D9BCE47402E5D1CF054EB14400012972DA3CE4740AAFCC63258EC14402241A7D6B1CE47409616E41D96EC1440488556CCADCE47403E72B55F88EE14408B2AB28CC3CE4740437EECE4C2EF14406E9681B9CCCE47406D55B71F95EF144051FC1873D7CE47404F475BF0FDF014406883A7EBE4CE474066587B8E7EF21440C85B53C5F9CE4740E47D665AACF214404008B76BF8CE47403A67FADB54F314401033EA10EECE47402003C30314F41440B356FEC6E8CE4740EB84E16DEFF4144077A565A4DECE4740A007D22060F81440E4C8B9032BCF47407D92E0B2AFF91440C1FE902342CF47406D776A89F0FA1440D80462235ACF4740A741D13C80FD1440A78988AC7FCF4740D72027A7D1FF1440E979DC5CA1CF4740EBA9D55757051540CD7B9C69C2CF47405A9E077767051540D9857A55C2CF47400722307B7E05154052B41776BDCF47406759411DA00515406511D43ABBCF4740C36856B60F091540C3B92B60A7CF474058676B3304061540C04B040539CF4740CD5944B9D904154009991A5712CF4740F47F5880000315405219B5B1C8CE47407B747EE59CFB144009E7AE809DCD4740D43C9171D6F91440A046109D54CD47408529CAA5F1F314409473BDC85FCC4740B066536463F41440A4B5584057CC47408E0EA37B31F714406E3C331C2ACC4740EEFF2D1224F81440311D84CA09CC4740D0543EBAC7F814405C71169BFBCB47407EA2FCEE67FA1440AFE13323CDCB4740102ED3E583FC1440E0C95D3A9CCB47400800338408FD144041A77BF88DCB47407781374998FE1440223999B855CB474036D07CCEDDFE14405F5A796E46CB474007CCE8EC09FF1440A926D2472ACB47401E82F45EC6FE14405680947DFCCA4740B5673B4B3500154066A208A9DBCA47401028F62BF80015408E9EB6A1BDCA474036DDFCD0160115400E7C56E3B6CA47402DC02D0E1D0115404ECCC4BEAECA474007240626DC001540F515A4198BCA474039A91E1FE30015405223997A82CA47409ABE8D83010215401E77A5C05DCA47402A92AF0452021540BE0056A247CA474008F7B990FD0215401A2433CD2ACA4740D5022093310315408DCE9E701BCA4740898043A852031540E0162CD505CA4740EC0FDE6811031540EFFBDC75DBC94740A8AED74F5A031540749EB12FD9C9474001A370E250041540E665039FD5C94740543D997FF4051540360FBB4AD2C94740B4B684D7890615403EBE73DEB5C947403BDC589AA506154000B03A72A4C94740774B72C0AE0615409722540493C94740F991110654061540072AE3DF67C94740884446AC6A061540377D2C335BC947404D6DA983BC06154049CB37914FC94740DC87BCE5EA071540C703801942C94740A9A04731E50815403C95C2723AC9474007DA780B7F0915402FB07DB72FC94740C9A1348A9B091540F6A974C12AC9474076D2B139BD0915407B771A1F1CC94740961D2CADD00915407431618FE4C8474083781332460A1540E790D442C9C8474020E97871870A15406EC1525DC0C84740844A5CC7B80A154069435953B4C847401BA8E738120B15404A4F47B6A9C8474000C80913460B154027A435069DC847402201A3CB9B0B154084CD4AA47EC8474064A428E1530A1540676FDF5971C847409FFC27C984091540935C59FD6CC84740541D7233DC08154032F77FC465C84740633DFF8705081540BB4D131159C847401211A38C5D07154041CE458D53C84740BB83D89942071540EC7BC33357C84740AE69DE718A061540696F95CC56C84740E2293F04B0041540D2A34EE559C84740B3C6EAA05C04154036F570B845C847404D4233993B0415406C5ED5592DC847407D66A4390D04154077FDDD9623C84740872FB88BD5031540441669E21DC847400C46802E7503154053A8B8BB18C8474053509B824602154009E1D1C611C84740838DEBDFF50115400FB8AE9811C8474077F69507E901154010ECF82F10C847403B866CC5A3011540B2DAFCBFEAC74740AD10FBA99601154085A7469EDAC7474089073994460115400FA8ED15CCC747409B6E7E688B0015400FE4EA11B4C7474060E16E5A3A001540CB2CE7F7A2C747401C3A877CE1FF1440C4B068DF81C74740C781FC112B001540AEA81CEE7EC74740DCDD5E2DD2FF1440672728EA71C74740E650D037B3FF144054234FED67C747407E31A53F45FF1440038A479858C74740");
    	city1.setShape(shape);
    	city1.setMunicipality(false);
    	this.cityDao.save(city1);
    	Point location = (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000002A2F5CC0ACCA1440CCDC92C9C2CC4740");
    	
    	City actual = cityDao.getByShape(location,null,true);
    	Assert.assertNull("the city should be null cause we filters by municipality",actual);
    	actual = cityDao.getByShape(location,null,false);
    	Assert.assertEquals("the city should be found cause we don't filter by municipality",city1.getFeatureId(), actual.getFeatureId());
    	Assert.assertNotNull(actual.getZipCodes());
    	Assert.assertEquals(1,actual.getZipCodes().size());
    	
    	actual = cityDao.getByShape(location,"FR",false);
    	Assert.assertEquals("the city should be found cause we don't filter by municipality and countrycode is correct",city1.getFeatureId(), actual.getFeatureId());
    	
    	actual = cityDao.getByShape(location,"DE",false);
    	Assert.assertNull("the city should be null cause we countrycode is not correct",actual);
    }
    
    @Test
    public void testCreateGISTIndexForLocationColumnShouldNotThrow(){
	cityDao.createGISTIndexForLocationColumn();
    }

    @Required
    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    @Required
    public void setGisFeatureDao(IGisFeatureDao gisFeatureDao) {
	this.gisFeatureDao = gisFeatureDao;
    }

}
