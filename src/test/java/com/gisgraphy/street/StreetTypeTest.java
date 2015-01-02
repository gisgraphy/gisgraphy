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
package com.gisgraphy.street;

import org.junit.Assert;
import org.junit.Test;

public class StreetTypeTest {

    @Test
    public void getFromString() {
	for (StreetType streetType: StreetType.values()){
	    Assert.assertEquals("getFromString should return correct values",StreetType.valueOf(streetType.toString()), StreetType.getFromString(streetType.toString()));
	    Assert.assertEquals("getFromString should be case sensitive",StreetType.valueOf(streetType.toString()), StreetType.getFromString(streetType.toString().toUpperCase()));
	    Assert.assertEquals("getFromString should be case sensitive",StreetType.valueOf(streetType.toString()), StreetType.getFromString(streetType.toString().toLowerCase()));
	}
	Assert.assertEquals("getFromString should be case sensitive",StreetType.valueOf(StreetType.UNCLASSIFIED.toString()), StreetType.getFromString("UNcLAsSIfIED"));
	Assert.assertNull("null should be return when wrong streettype is given",StreetType.getFromString("unknow"));
    }

}
