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
package com.gisgraphy.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.test.FakeBean;

public class IntrospectionHelperTest  {

	@Test
    public void getFieldsAsListShouldIgnoreSerialVersionUID() {
	List<String> fields = IntrospectionHelper
			.getFieldsAsList(Address.class);
	Assert.assertFalse(fields.contains("SerialVersionUID"));
	}
	
	
    @Test
    public void getGisFeatureFieldsAsListShouldIgnoreAnnotedFields() {
	List<String> fields = IntrospectionHelper
		.getFieldsAsList(GisFeature.class);
	assertTrue(fields.contains("id"));
	assertTrue(fields.contains("featureId"));
	assertTrue(fields.contains("name"));
	assertTrue(fields.contains("asciiName"));
	assertTrue(fields.contains("location"));
	assertTrue(fields.contains("adm1Code"));
	assertTrue(fields.contains("adm2Code"));
	assertTrue(fields.contains("adm3Code"));
	assertTrue(fields.contains("adm4Code"));
	assertTrue(fields.contains("adm1Name"));
	assertTrue(fields.contains("adm2Name"));
	assertTrue(fields.contains("adm3Name"));
	assertTrue(fields.contains("adm4Name"));
	assertTrue(fields.contains("featureClass"));
	assertTrue(fields.contains("featureCode"));
	assertTrue(fields.contains("countryCode"));
	assertTrue(fields.contains("population"));
	assertTrue(fields.contains("elevation"));
	assertTrue(fields.contains("gtopo30"));
	assertTrue(fields.contains("timezone"));
	assertTrue(fields.contains("amenity"));
	assertTrue(fields.contains("openstreetmapId"));
	assertEquals(27, fields.size());
    }

    @Test
    public void getGisFeatureFieldsAsListShouldExploreSubClass() {
	List<String> fields = IntrospectionHelper
		.getFieldsAsList(City.class);
	assertEquals(27, fields.size());
	assertTrue(fields.contains("id"));
	assertTrue(fields.contains("featureId"));
	assertTrue(fields.contains("name"));
	assertTrue(fields.contains("asciiName"));
	assertTrue(fields.contains("location"));
	assertTrue(fields.contains("adm1Code"));
	assertTrue(fields.contains("adm2Code"));
	assertTrue(fields.contains("adm3Code"));
	assertTrue(fields.contains("adm4Code"));
	assertTrue(fields.contains("adm1Name"));
	assertTrue(fields.contains("adm2Name"));
	assertTrue(fields.contains("adm3Name"));
	assertTrue(fields.contains("adm4Name"));
	assertTrue(fields.contains("featureClass"));
	assertTrue(fields.contains("featureCode"));
	assertTrue(fields.contains("countryCode"));
	assertTrue(fields.contains("population"));
	assertTrue(fields.contains("elevation"));
	assertTrue(fields.contains("gtopo30"));
	assertTrue(fields.contains("timezone"));
	assertTrue(fields.contains("amenity"));
	assertTrue(fields.contains("openstreetmapId"));
    }

    @Test
    public void getGisFeatureFieldsAsArrayShouldIgnoreAnnotedFields() {
	String[] fields = IntrospectionHelper
		.getFieldsAsArray(GisFeature.class);
	assertEquals(27, fields.length);
    }
    
    @Test
    public void getGisFeatureFieldsAsArrayShouldIgnoreFinalFields() {
	String[] fields = IntrospectionHelper
		.getFieldsAsArray(FakeBean.class);
	assertEquals(28, fields.length);
    }

    @Test
    public void getGisFeatureFieldsAsArrayShouldExploreSubClass() {
	String[] fields = IntrospectionHelper
		.getFieldsAsArray(City.class);
	assertEquals(27, fields.length);//TODO do with a subclass
    }

    @Test
    public void getGisFeatureFieldsAsArrayShouldReturnTheSameValueForSecondCall() {
	String[] fields = IntrospectionHelper
		.getFieldsAsArray(GisFeature.class);
	fields = IntrospectionHelper
		.getFieldsAsArray(GisFeature.class);
	assertEquals(27, fields.length);
	}

    @Test
    public void clearCache() {
	String[] fields = IntrospectionHelper
		.getFieldsAsArray(GisFeature.class);
	assertEquals(27, fields.length);
	IntrospectionHelper.clearCache();
	fields = IntrospectionHelper
		.getFieldsAsArray(GisFeature.class);
	assertEquals(27, fields.length);
    }

    @Test
    public void getGisFeatureFieldsAsListShouldReturnTheSameValueForSecondCall() {
	List<String> fields = IntrospectionHelper
		.getFieldsAsList(GisFeature.class);
	fields = IntrospectionHelper
		.getFieldsAsList(GisFeature.class);
	assertEquals(27, fields.size());
    }
    
    

}
