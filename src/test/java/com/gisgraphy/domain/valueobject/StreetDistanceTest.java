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
package com.gisgraphy.domain.valueobject;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.domain.valueobject.StreetDistance.StreetDistanceBuilder;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.street.StreetType;
import com.vividsolutions.jts.geom.Point;


public class StreetDistanceTest {

    @Test
    public void builderShouldSetValuesAndUpdateCalculatedFields(){
	Point point = GeolocHelper.createPoint(45F, 56F);
	StreetDistance streetDistance = StreetDistanceBuilder.streetDistance()
	.withCountryCode("fr")
	.withDistance(3D)
	.withGid(123L)
	.withLength(124.4D)
	.withLocation(point)
	.withName("name")
	.withOneWay(true)
	.withIsIn("los angeles")
	.withStreetType(StreetType.FOOTWAY).build();

	Assert.assertEquals("countryCode Should be upperCased","FR",streetDistance.getCountryCode());
	Assert.assertEquals(3D,streetDistance.getDistance());
	Assert.assertEquals(123L,streetDistance.getGid().longValue());
	Assert.assertEquals(point,streetDistance.getLocation());
	Assert.assertEquals("name",streetDistance.getName());
	Assert.assertEquals("los angeles",streetDistance.getIsIn());
	Assert.assertEquals(Boolean.TRUE,streetDistance.getOneWay());
	Assert.assertEquals(StreetType.FOOTWAY,streetDistance.getStreetType());
	Assert.assertEquals("calculated fields should be process",45F,streetDistance.getLng().floatValue());
	Assert.assertEquals("calculated fields should be process",56F,streetDistance.getLat().floatValue());
    }
    
}
