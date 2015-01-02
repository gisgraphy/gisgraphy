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
package com.gisgraphy.street;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.helper.GeolocHelper;
import com.vividsolutions.jts.geom.Point;


public class StreetFactoryTest {

    @Test
    public void  createShouldCreate(){
	long gid = 12345L;
	long openstreetmapId = 5678L;
	String name= "california street"; 
	Double length = 5.6D;
	boolean oneWay = true;
	String isIn = "los angeles";
	String isInPlace = "french quater";
	String isInAdm = "adm";
	Set<String> isInZip = new HashSet<String>();
	String isInZip1 = "zip";
	String isInZip2 = "zip2";
	isInZip.add(isInZip1);
	isInZip.add(isInZip2);
	String fullyQualifiedAddress ="fullyQualifiedAddress";
	StreetType streetType = StreetType.SECONDARY_LINK;
	String countryCode = "FR";
	Point location = GeolocHelper.createPoint(10.2F, 9.5F);
	OpenStreetMap openStreetMap = new OpenStreetMap();
	openStreetMap.setGid(gid);
	openStreetMap.setCountryCode(countryCode);
	openStreetMap.setName(name);
	openStreetMap.setLocation(location);
	openStreetMap.setLength(length);
	openStreetMap.setStreetType(streetType);
	openStreetMap.setOneWay(oneWay);
	openStreetMap.setOpenstreetmapId(openstreetmapId);
	openStreetMap.setIsIn(isIn);
	openStreetMap.setIsInAdm(isInAdm);
	openStreetMap.setIsInPlace(isInPlace);
	openStreetMap.setIsInZip(isInZip);
	openStreetMap.setFullyQualifiedAddress(fullyQualifiedAddress);
	HouseNumber houseNumber = new HouseNumber();
	houseNumber.setLocation(GeolocHelper.createPoint(10.3F, 9.6F));
	houseNumber.setNumber("1");
	SortedSet<HouseNumber> houseNumbers = new TreeSet<HouseNumber>();
	houseNumbers.add(houseNumber);
	openStreetMap.setHouseNumbers(houseNumbers);
	
	List<AlternateOsmName> alternateNames = new ArrayList<AlternateOsmName>();
	alternateNames.add(new AlternateOsmName("altname",AlternateNameSource.PERSONAL));
	alternateNames.add(new AlternateOsmName("altname2",AlternateNameSource.OPENSTREETMAP));
	openStreetMap.addAlternateNames(alternateNames);
	
	Set<String> citiesAlternateNames = new HashSet<String>();
	citiesAlternateNames.add("city1");
	citiesAlternateNames.add("city2");
	openStreetMap.addIsInCitiesAlternateNames(citiesAlternateNames);
	
	StreetFactory factory = new StreetFactory();
	Street street = factory.create(openStreetMap);
	
	Assert.assertEquals(new Long(gid), street.getFeatureId());
	Assert.assertEquals(new Long(openstreetmapId), street.getOpenstreetmapId());
	Assert.assertEquals(name, street.getName());
	Assert.assertEquals(location, street.getLocation());
	Assert.assertEquals(length, street.getLength());
	Assert.assertEquals(streetType, street.getStreetType());
	Assert.assertEquals(oneWay, street.isOneWay());
	Assert.assertEquals(countryCode, street.getCountryCode());
	Assert.assertEquals(isIn, street.getIsIn());
	Assert.assertEquals(isInPlace, street.getIsInPlace());
	Assert.assertEquals(isInAdm, street.getIsInAdm());
	Assert.assertEquals(isInZip, street.getIsInZip());
	Assert.assertEquals(fullyQualifiedAddress, street.getFullyQualifiedAddress());
	Assert.assertEquals(houseNumbers, street.getHouseNumbers());
	Assert.assertEquals(2, street.getAlternateNames().size());
	Assert.assertTrue(alternateNamesContains(street.getAlternateNames(), "altname"));
	Assert.assertTrue(alternateNamesContains(street.getAlternateNames(), "altname2"));
	
	Assert.assertTrue(alternateCitiesNamesContain(street.getIsInCityAlternateNames(), "city1"));
	Assert.assertTrue(alternateCitiesNamesContain(street.getIsInCityAlternateNames(), "city2"));
	
	
    }
    
    private boolean alternateNamesContains(Collection<AlternateName> names, String name){
    	if (names!=null ){
    		for(AlternateName nameToTest:names){
    			if (nameToTest!=null && nameToTest.getName().equals(name)){
    				return true;
    			}
    		}
    	} else {
    		return false;
    	}
    	Assert.fail("alternateNames doesn't contain "+name);
    	return false;
    }
    
    private boolean alternateCitiesNamesContain(Collection<String> names, String name){
    	if (names!=null ){
    		for(String nameToTest:names){
    			if (nameToTest!=null && nameToTest.equals(name)){
    				return true;
    			}
    		}
    	} else {
    		return false;
    	}
    	Assert.fail("cities alternateNames doesn't contain "+name);
    	return false;
    }
    
}
