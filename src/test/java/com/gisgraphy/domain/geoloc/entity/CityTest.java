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
/**
 * 
 */
package com.gisgraphy.domain.geoloc.entity;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.test.GisgraphyTestHelper;

/**
 * @author david
 * 
 */
public class CityTest {

	@Test
	public void testGetFullyQualifiedNameShouldContainsZipCodeIfCityHasOneZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		//Note that city has already a zipcode
		Assert.assertTrue(city.getFullyQualifiedName(false).contains(city.getZipCodes().iterator().next().getCode()));
	}

	@Test
	public void testGetFullyQualifiedNameShouldNotContainsZipCodeIfCityHasMoreThanOneZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);
		String zipcode = "95000";
		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		//Note that city has already a zipcode
		zipcodes.add(new ZipCode(zipcode));
		Assert.assertFalse(city.getFullyQualifiedName(false).contains("95000"));
		Assert.assertFalse(city.getFullyQualifiedName(false).contains("96000"));

	}

	@Test
	public void testGetFullyQualifiedNameWhenNoZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);
		city.getFullyQualifiedName(false);
	}
	
	@Test
	public void testPopulateShouldAddZipCodesForTheCurrentFeatures(){
		City city = GisgraphyTestHelper.createCity("city", 3.0F, 4.0F, 1L);
		City cityToBePopulated = new City();
		cityToBePopulated.setFeatureId(1234567L);
		cityToBePopulated.populate(city);
		Assert.assertEquals("when populate is called, the name of the cities should be equals, maybe super is not called",city.getName(), cityToBePopulated.getName());
		Assert.assertEquals("when populate is called, the zipcodes should be added",city.getZipCodes().size(),cityToBePopulated.getZipCodes().size());
		Assert.assertEquals("when populate is called, the zipcodes should be associated to The features populated, not the 'arg one'",cityToBePopulated.getFeatureId(),cityToBePopulated.getZipCodes().iterator().next().getGisFeature().getFeatureId());
	}

}
