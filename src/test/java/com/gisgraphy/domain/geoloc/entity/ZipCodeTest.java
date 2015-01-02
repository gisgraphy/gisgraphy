/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.domain.geoloc.entity;

import junit.framework.Assert;

import org.junit.Test;


public class ZipCodeTest {

	private String code ="code";
	
	@Test
	public void testSetCodeShouldUppercase(){
		ZipCode zipCode = new ZipCode("");
		zipCode.setCode(code);
		Assert.assertEquals("zipcode constructor should upper case","CODE", zipCode.getCode());
	}
	
	@Test
	public void testConstructorShouldUpperCaseCode(){
		ZipCode zipCode = new ZipCode(code);
		Assert.assertEquals("zipcode constructor should upper case","CODE", zipCode.getCode());
	}
	
	@Test
	public void testToString(){
		ZipCode zipCode = new ZipCode(code);
		Assert.assertEquals("toString should return the code ","CODE", zipCode.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testZipCodeMustHaveACode(){
		new ZipCode(null);
	}
	
	@Test
	public void testEquals(){
		ZipCode zipcode1 = new ZipCode(code);
		ZipCode zipcodeEquals = new ZipCode(code);
		ZipCode zipcodeNotEquals = new ZipCode(code+code);
		Assert.assertTrue("Equals must be base on the code",zipcode1.equals(zipcodeEquals));
		Assert.assertFalse("Equals must be base on the code",zipcode1.equals(zipcodeNotEquals));
		
	}
}
