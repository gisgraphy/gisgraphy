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

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.helper.GisHelper;

public class GisHelperTest {
	
   @Test
    public void testgetBoundingBox(){
    	Assert.assertFalse(GisHelper.getBoundingBox("alias", 51.8365537F ,7.0562314F, 10000).contains("NaN"));
    	//System.out.println(GisHelper.getBoundingBox("alias", 48.867138600000004,2.3958578000000004, 500));
    }


    @Test
    public void getClassEntityFromStringShouldReturnCorrectClass() {
	// typic
	Class<? extends GisFeature> clazz = GisHelper
		.getClassEntityFromString("City");
	Assert.assertNotNull(
		"getClassEntityFromString does not return a correct class",
		clazz);
	Assert.assertEquals(City.class, clazz);

	// not existing
	clazz = GisHelper.getClassEntityFromString("nothing");
	Assert.assertNull(clazz);

	// case insensitive
	clazz = GisHelper.getClassEntityFromString("city");
	Assert.assertNotNull("getClassEntityFromString should be case insensitive",
		clazz);
	Assert.assertEquals(City.class, clazz);

	// case insensitive
	clazz = GisHelper.getClassEntityFromString("gisfeature");
	Assert.assertNotNull("getClassEntityFromString should be case insensitive",
		clazz);
	Assert.assertEquals(GisFeature.class, clazz);

	// with null
	clazz = GisHelper.getClassEntityFromString(null);
	Assert.assertNull(clazz);
    }


    
}
