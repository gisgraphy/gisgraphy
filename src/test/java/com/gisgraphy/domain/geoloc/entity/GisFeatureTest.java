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
package com.gisgraphy.domain.geoloc.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;

public class GisFeatureTest extends AbstractIntegrationHttpSolrTestCase {

    private ICountryDao countryDao;

    private ICityDao cityDao;
    
    @Test
    public void CheckAllEntitiesHasTheirPlacetype(){
    	Reflections reflections = new Reflections("com.gisgraphy.domain.geoloc.entity");

    	 Set<Class<? extends Object>> allClasses = 
    	     reflections.getSubTypesOf(Object.class);
    	 
    	 for (Class c: allClasses){
    		 System.out.println(c);
    	 }
    }

    @Test
    public void testAddAlternateNamesShouldNotAddTooLongAlternateNames() {
	GisFeature gisFeature = GisgraphyTestHelper
		.createGisFeatureWithAlternateNames("toto", 3);
	List<AlternateName> alternateNames = new ArrayList<AlternateName>();
	AlternateName a1 = new AlternateName();
	a1.setName(StringUtils.repeat("a", GisFeature.MAX_ALTERNATENAME_SIZE+1));
	AlternateName a2 = new AlternateName();
	a2.setName("bar");
	alternateNames.add(a1);
	alternateNames.add(a2);
	gisFeature.addAlternateNames(alternateNames);
	assertEquals("The long alternateName should not be added",4, gisFeature.getAlternateNames().size());
    }
    
    @Test
    public void testAddAlternateNamesShouldAddChildrenAndNotReplace() {
	GisFeature gisFeature = GisgraphyTestHelper
		.createGisFeatureWithAlternateNames("toto", 3);
	List<AlternateName> alternateNames = new ArrayList<AlternateName>();
	AlternateName a1 = new AlternateName();
	a1.setName("foo");
	AlternateName a2 = new AlternateName();
	a2.setName("bar");
	alternateNames.add(a1);
	alternateNames.add(a2);
	gisFeature.addAlternateNames(alternateNames);
	assertEquals(5, gisFeature.getAlternateNames().size());
    }
    
    @Test
    public void testAddAlternateNamesShouldNotHaveDuplicates() {
	GisFeature gisFeature = GisgraphyTestHelper
		.createGisFeatureWithAlternateNames("toto", 3);
	AlternateName a1 = new AlternateName();
	a1.setName("foo");
	AlternateName a2 = new AlternateName();
	a2.setName("foo");
	gisFeature.addAlternateName(a1);
	gisFeature.addAlternateName(a2);
	assertEquals(4, gisFeature.getAlternateNames().size());
    }

    @Test
    public void testAddAlternateNamesShouldDoADoubleSet() {
	GisFeature gisFeature = GisgraphyTestHelper
		.createGisFeatureWithAlternateNames("toto", 3);
	List<AlternateName> alternateNames = new ArrayList<AlternateName>();
	AlternateName a1 = new AlternateName();
	a1.setName("foo");
	AlternateName a2 = new AlternateName();
	a2.setName("bar");
	alternateNames.add(a2);
	alternateNames.add(a1);
	gisFeature.addAlternateNames(alternateNames);
	assertEquals(5, gisFeature.getAlternateNames().size());
	for (AlternateName alternateName : gisFeature.getAlternateNames()) {
	    assertEquals(gisFeature, alternateName.getGisFeature());
	}
    }

    @Test
    public void testSetFeatureClassShouldAlwaysSetInUpperCase() {
	GisFeature gisFeature = new GisFeature();
	gisFeature.setFeatureClass("a");
	assertEquals("A", gisFeature.getFeatureClass());
    }

    @Test
    public void testSetFeatureCodeShouldAlwaysSetInUpperCase() {
	GisFeature gisFeature = new GisFeature();
	gisFeature.setFeatureCode("a");
	assertEquals("A", gisFeature.getFeatureCode());
    }

    @Test
    public void testSetFeatureClassWithNullValueShouldNotThrow() {
	GisFeature gisFeature = new GisFeature();
	try {
	    gisFeature.setFeatureClass(null);
	} catch (RuntimeException e) {
	    fail("setting a null feture class should not throw");
	}
    }

    @Test
    public void testSetFeatureCodeWithNullShouldnotThrow() {
	GisFeature gisFeature = new GisFeature();
	try {
	    gisFeature.setFeatureCode(null);
	} catch (RuntimeException e) {
	    fail("setting a null feature code should not throw");
	}
    }

    @Test
    public void testGetCountryShouldReturnTheCountryObject() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	GisFeature gisFeature = GisgraphyTestHelper.createCity("cityGisFeature",
		null, null, new Random().nextLong());
	City paris = new City(gisFeature);
	paris.setCountryCode("FR");

	// save city
	City savedParis = this.cityDao.save(paris);

	// chek city is well saved
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());

	Country retrievedCountry = savedParis.getCountry();
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry, retrievedCountry);

    }

    @Test
    public void testGetCountryShouldReturnNullIfNoCountryCodeisSpecified() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	GisFeature gisFeature = GisgraphyTestHelper.createCity("cityGisFeature",
		null, null, new Random().nextLong());
	City paris = new City(gisFeature);
	paris.setCountryCode(null);

	// save city
	City savedParis = this.cityDao.save(paris);

	// chek city is well saved
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());

	Country retrievedCountry = savedParis.getCountry();
	assertNull(retrievedCountry);

    }

    @Test
    public void testGetCountryShouldReturnNullIfUnknowCountryCode() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	GisFeature gisFeature = GisgraphyTestHelper.createCity("cityGisFeature",
		null, null, new Random().nextLong());
	City paris = new City(gisFeature);
	paris.setCountryCode("ER");

	// save city
	City savedParis = this.cityDao.save(paris);

	// chek city is well saved
	City retrievedParis = this.cityDao.get(savedParis.getId());
	assertNotNull(retrievedParis);
	assertEquals(paris.getId(), retrievedParis.getId());

	Country retrievedCountry = savedParis.getCountry();
	assertNull(retrievedCountry);

    }

    @Test
    public void testDistanceShouldRetrunCorrectDistance() {
	GisFeature point1 = new GisFeature();
	point1.setLocation(GisgraphyTestHelper.createPoint(48.867F, 2.333F));

	GisFeature point2 = new GisFeature();
	point2.setLocation(GisgraphyTestHelper.createPoint(49.017F, 2.467F));

	assertEquals(Math.round(point1.distanceTo(point2.getLocation())), Math
		.round(point2.distanceTo(point1.getLocation())));
	assertEquals(22, Math
		.round(point2.distanceTo(point1.getLocation()) / 1000));
    }

    public void testDistanceShouldHaveCorrectParameters() {
	GisFeature point1 = new GisFeature();
	point1.setLocation(GisgraphyTestHelper.createPoint(0F, 0F));

	try {
	    point1.distanceTo(null);
	    fail("Distance for a null feature must throws");
	} catch (RuntimeException e) {
	}

	GisFeature point2 = new GisFeature();
	// point2.setLocation(this.geolocTestHelper.createPoint(1F, 1F));

	try {
	    point1.distanceTo(point2.getLocation());
	    fail("Distance with a null location must throws");
	} catch (RuntimeException e) {
	}

	point1.setLocation(null);
	try {
	    point1.distanceTo(point2.getLocation());
	    fail("Distance with a null location must throws");
	} catch (RuntimeException e) {
	}

    }

    @Test
    public void testPopulateAcityShouldsetZipCode() {
	City city1 = GisgraphyTestHelper.createCity("name", 1.5F, 1.6F, 2L);
	city1.addZipCode(new ZipCode("10000"));
	City city2 = new City();
	city2.populate(city1);
	for (ZipCode zipcode : city1.getZipCodes())
	assertTrue("Populate a city with a city should set the zipcode",
		 city2.getZipCodes().contains(zipcode));

    }

    @Test
    public void testToStringShouldContainsTheClassName() {
	City city1 = GisgraphyTestHelper.createCity("name", 1.5F, 1.6F, 2L);
	city1.addZipCode(new ZipCode("10000"));
	assertTrue(city1.toString().startsWith(City.class.getSimpleName()));
    }
    
    @Test
    public void testAddZipCodesShouldDoADoubleSet(){
	GisFeature gisFeature = new GisFeature();
	gisFeature.setFeatureId(3L);
	ZipCode zipCode1 = new ZipCode("zip1");
	ZipCode zipCode2 = new ZipCode("zip2");
	List<ZipCode> zipCodes = new ArrayList<ZipCode>();
	zipCodes.add(zipCode1);
	zipCodes.add(zipCode2);
	gisFeature.addZipCodes(zipCodes);
	Assert.assertEquals("all the zipcodes of the list should be added",zipCodes.size(), gisFeature.getZipCodes().size());
	Assert.assertTrue("zipCode1 is missing", gisFeature.getZipCodes().contains(zipCode1));
	Assert.assertTrue("zipCode2 is missing", gisFeature.getZipCodes().contains(zipCode2));
	Assert.assertEquals("A double set should be done, gisfeature should be set in the the zipCode entity",
		gisFeature.getFeatureId(), gisFeature.getZipCodes().iterator().next().getGisFeature().getFeatureId() );
    }
    
    @Test
    public void testAddZipCodeShouldDoADoubleSet(){
	GisFeature gisFeature = new GisFeature();
	gisFeature.setFeatureId(3L);
	ZipCode zipCode1 = new ZipCode("zip2");
	gisFeature.addZipCode(zipCode1);
	Assert.assertEquals("all the zipcodes of the list should be added",1, gisFeature.getZipCodes().size());
	Assert.assertTrue("zipCode1 is missing", gisFeature.getZipCodes().contains(zipCode1));
	Assert.assertEquals("A double set should be done, gisfeature should be set in the the zipCode entity",
		gisFeature.getFeatureId(), gisFeature.getZipCodes().iterator().next().getGisFeature().getFeatureId() );
    }

    @Required
    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    @Required
    public void setCountryDao(ICountryDao countryDao) {
	this.countryDao = countryDao;
    }

}
