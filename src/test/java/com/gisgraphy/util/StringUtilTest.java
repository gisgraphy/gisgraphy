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
package com.gisgraphy.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {
   
	@Test
	public void testEncodePassword() throws Exception {
		String password = "tomcat";
		String encrypted = "536c0b339345616c1b33caf454454d8b8a190d6c";
		assertEquals(StringUtil.encodePassword(password, "SHA"), encrypted);
	}
  
    @Test
    public void testContainsDigit(){
    	Assert.assertTrue(StringUtil.containsDigit("1"));
    	Assert.assertTrue(StringUtil.containsDigit("a1"));
    	Assert.assertTrue(StringUtil.containsDigit("2a"));
    	Assert.assertFalse(StringUtil.containsDigit("aa"));
    	Assert.assertFalse(StringUtil.containsDigit(null));
    	
    }
}
