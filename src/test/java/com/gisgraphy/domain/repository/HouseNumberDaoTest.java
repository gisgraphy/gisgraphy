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
package com.gisgraphy.domain.repository;

import java.util.SortedSet;

import net.sf.jstester.util.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class HouseNumberDaoTest extends AbstractIntegrationHttpSolrTestCase {

    @Autowired
    private IOpenStreetMapDao openStreetMapDao;

    @Autowired
    private IhouseNumberDao houseNumberDao;

    //TODO hn save street shoud save housenumber
    @Test
    public void testSaveHouseNumberForAnExistingStreet() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
	openStreetMapDao.save(street);
	

	

    }

    
    @Test
    public void testSave(){
    	HouseNumber houseNumber = GisgraphyTestHelper.createHouseNumber();
    	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
    	street = openStreetMapDao.save(street);
    	//houseNumber.setStreet(street);
    	street.addHouseNumber(houseNumber);
    	houseNumberDao.save(houseNumber);
    	Assert.assertNotNull(houseNumber.getId());
    	
    	HouseNumber retrieved = houseNumberDao.get(houseNumber.getId());
    	Assert.assertNotNull(retrieved);
    	
    	OpenStreetMap retrievedStreet = openStreetMapDao.get(street.getId());
    	SortedSet<HouseNumber> houseNumbers = retrievedStreet.getHouseNumbers();
		Assert.assertNotNull(houseNumbers);
    	Assert.assertEquals("the street should have the housenumber associated",1, houseNumbers.size());
    	Assert.assertEquals(retrieved.getId(), houseNumbers.first().getId());
    	
    }
    
    @Test
    public void testSaveWithNullNumber(){
    	HouseNumber houseNumber = GisgraphyTestHelper.createHouseNumber();
    	houseNumber.setNumber(null);
    	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
    	openStreetMapDao.save(street);
    	houseNumber.setStreet(street);
    	houseNumberDao.save(houseNumber);
    	Assert.assertNotNull(houseNumber.getId());
    	
    	HouseNumber retrieved = houseNumberDao.get(houseNumber.getId());
    	Assert.assertNotNull(retrieved);
    }
}
