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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NameValueDTOTest {

    @Test
    public void testNameValueDTO() {
	String nameParam = "nameX";
	String valueParam = "val";
	NameValueDTO<String> nv = new NameValueDTO<String>(nameParam,
		valueParam);
	assertEquals(nameParam, nv.getName());
	assertEquals(valueParam, nv.getValue());
    }

    @Test
    public void testGetName() {
	String nameParam = "nameX";
	String valueParam = "val";
	NameValueDTO<String> nv = new NameValueDTO<String>(nameParam,
		valueParam);
	assertEquals(nameParam, nv.getName());
    }

    @Test
    public void testGetValue() {
	String nameParam = "nameX";
	String valueParam = "val";
	NameValueDTO<String> nv = new NameValueDTO<String>(nameParam,
		valueParam);
	assertEquals(valueParam, nv.getValue());
    }

}
