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
package com.gisgraphy.domain.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;


public class ZipCodeDaoTest extends AbstractIntegrationHttpSolrTestCase{
	
	
	private IZipCodeDao zipCodeDao;
	private IGisFeatureDao gisFeatureDao;
	
	@Test
	public void testGetByCodeAndCountry(){
		String code = "code1";
		String countryCode = "FR";
		ZipCode zip1 = new ZipCode(code);
		
		GisFeature gisFeature = GisgraphyTestHelper.createGisFeature("asciiname", 3F, 4F, 1L);
		gisFeature.setCountryCode(countryCode);
		gisFeature.addZipCode(zip1);
		gisFeatureDao.save(gisFeature);
		
		List<ZipCode> actual = zipCodeDao.getByCodeAndCountry(code, countryCode);
		Assert.assertEquals(zip1, actual.get(0));
		actual = zipCodeDao.getByCodeAndCountry(code, "DE");
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.size()==0);
	}
	
	@Test
	public void testGetByCodeAndCountrySmart_gb(){
		String code = "code1";
		String countryCode = "FR";
		
		String smartCode = "DN16";
		String smartCountryCode = "GB";
		
		ZipCode smartZip = new ZipCode(smartCode);
		ZipCode zip1 = new ZipCode(code);
		
		GisFeature gisFeature = GisgraphyTestHelper.createGisFeature("asciiname", 3F, 4F, 1L);
		gisFeature.setCountryCode(countryCode);
		gisFeature.addZipCode(zip1);
		gisFeatureDao.save(gisFeature);
		
		GisFeature smartGisFeature = GisgraphyTestHelper.createGisFeature("asciiname", 3F, 4F, 2L);
		smartGisFeature.setCountryCode(smartCountryCode);
		smartGisFeature.addZipCode(smartZip);
		gisFeatureDao.save(smartGisFeature);
		
		//common case
		List<ZipCode> actual = zipCodeDao.getByCodeAndCountrySmart(code, countryCode);
		Assert.assertEquals(zip1, actual.get(0));
		actual = zipCodeDao.getByCodeAndCountrySmart(code, "DE");
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.size()==0);
		
		//smart case
		 actual = zipCodeDao.getByCodeAndCountrySmart(smartCode, smartCountryCode);
		Assert.assertEquals("smart search for zipCode should handle strictly equals countrycode",smartZip, actual.get(0));
		
		 actual = zipCodeDao.getByCodeAndCountrySmart("dn16", smartCountryCode);
		 Assert.assertEquals("smart search for zipCode should be case insensitive",smartZip, actual.get(0));
		 
		 actual = zipCodeDao.getByCodeAndCountrySmart("DN16 9AA", smartCountryCode);
		 Assert.assertEquals("smart search for zipCode should search for code that starts with",smartZip, actual.get(0));
	}
	
	@Test
	public void testGetByCodeAndCountrySmart_ca(){
		String code = "code1";
		String countryCode = "FR";
		
		String smartCode = "H3Z";
		String smartCountryCode = "CA";
		
		ZipCode smartZip = new ZipCode(smartCode);
		ZipCode zip1 = new ZipCode(code);
		
		GisFeature gisFeature = GisgraphyTestHelper.createGisFeature("asciiname", 3F, 4F, 1L);
		gisFeature.setCountryCode(countryCode);
		gisFeature.addZipCode(zip1);
		gisFeatureDao.save(gisFeature);
		
		GisFeature smartGisFeature = GisgraphyTestHelper.createGisFeature("asciiname", 3F, 4F, 2L);
		smartGisFeature.setCountryCode(smartCountryCode);
		smartGisFeature.addZipCode(smartZip);
		gisFeatureDao.save(smartGisFeature);
		
		//common case
		List<ZipCode> actual = zipCodeDao.getByCodeAndCountrySmart(code, countryCode);
		Assert.assertEquals(zip1, actual.get(0));
		actual = zipCodeDao.getByCodeAndCountrySmart(code, "DE");
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.size()==0);
		
		//smart case
		 actual = zipCodeDao.getByCodeAndCountrySmart(smartCode, smartCountryCode);
		Assert.assertEquals("smart search for zipCode should handle strictly equals countrycode",smartZip, actual.get(0));
		
		 actual = zipCodeDao.getByCodeAndCountrySmart("h3z", smartCountryCode);
		 Assert.assertEquals("smart search for zipCode should be case insensitive",smartZip, actual.get(0));
		 
		 actual = zipCodeDao.getByCodeAndCountrySmart("H3Z 2Y7", smartCountryCode);
		 Assert.assertEquals("smart search for zipCode should search for code that starts with",smartZip, actual.get(0));
	}
	
	@Test
	public void testListByCode(){
		String code = "code1";
		ZipCode zip1 = new ZipCode(code);
		ZipCode zip2 = new ZipCode(code);
		
		GisFeature gisFeature = GisgraphyTestHelper.createGisFeature("asciiname", 3F, 4F, 1L);
		gisFeature.addZipCode(zip1);
		gisFeatureDao.save(gisFeature);
		
		GisFeature gisFeature2 = GisgraphyTestHelper.createGisFeature("asciiname2", 5F, 6F, 2L);
		gisFeature2.addZipCode(zip2);
		gisFeatureDao.save(gisFeature2);
		
		List<ZipCode> actual = zipCodeDao.listByCode(code);
		Assert.assertEquals(2, actual.size());
		Assert.assertEquals(new Long(1) ,actual.get(0).getGisFeature().getFeatureId());
		Assert.assertEquals(new Long(2) ,actual.get(1).getGisFeature().getFeatureId());
		
		
	}
	
	@Test
	public void testListByCodeShouldReturnAnEmptyListWhenThereIsNoResults(){
		List<ZipCode> actual = zipCodeDao.listByCode("Nocode");
		Assert.assertNotNull(actual);
		Assert.assertEquals(0, actual.size());
	}
	

	public void setZipCodeDao(IZipCodeDao zipCodeDao) {
		this.zipCodeDao = zipCodeDao;
	}

	public void setGisFeatureDao(GisFeatureDao gisFeatureDao) {
		this.gisFeatureDao = gisFeatureDao;
	}

}
