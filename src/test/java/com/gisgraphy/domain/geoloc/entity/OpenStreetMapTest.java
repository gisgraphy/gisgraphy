package com.gisgraphy.domain.geoloc.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.jstester.util.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.test.GisgraphyTestHelper;

public class OpenStreetMapTest {

	@Test
	public void testAddHouseNumberShouldAddAndNotReplace() {
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		
		SortedSet<HouseNumber> houseNumbers = new TreeSet<HouseNumber>();
		HouseNumber n1 = GisgraphyTestHelper.createHouseNumber();
		
		houseNumbers.add(n1);
		street.setHouseNumbers(houseNumbers);
		
		Assert.assertEquals(1, street.getHouseNumbers().size());
		
		HouseNumber n2 = new HouseNumber();
		street.addHouseNumber(n2);
		
		Assert.assertEquals("add street should add street, not replace",2, street.getHouseNumbers().size());
		
		//check doubleset
		Assert.assertEquals("double set should be done",street, n2.getStreet());
		
	}
	
	@Test
	public void testAddHouseNumberShouldSort() {
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		
		HouseNumber n1 = GisgraphyTestHelper.createHouseNumber();
		n1.setNumber("10");
		street.addHouseNumber(n1);
		Assert.assertEquals(1, street.getHouseNumbers().size());
		
		HouseNumber n2 = new HouseNumber();
		n2.setNumber("2");
		street.addHouseNumber(n2);
		
		Assert.assertEquals("add street should add street, not replace",2, street.getHouseNumbers().size());
		Assert.assertEquals(n2, street.getHouseNumbers().first());
		//check doubleset
		Assert.assertEquals("double set should be done",street, n2.getStreet());
		
	}
	
	@Test
	public void testAddHouseNumberwhenNoHouseNumberAlreadyAdded() {
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		
		HouseNumber n1 = GisgraphyTestHelper.createHouseNumber();
		
		street.addHouseNumber(n1);
		
		Assert.assertEquals("housenumberlist should be initialize when no house number have been added",1, street.getHouseNumbers().size());
		
		//check doubleset
		Assert.assertEquals("double set should be done",street, n1.getStreet());
		
	}
	
	@Test
	public void testAddHouseNumbers() {
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		
		List<HouseNumber> houseNumbers = new ArrayList<HouseNumber>();
		HouseNumber n1 = GisgraphyTestHelper.createHouseNumber();
		
		houseNumbers.add(n1);
		street.addHouseNumbers(houseNumbers);
		
		Assert.assertEquals(1, street.getHouseNumbers().size());
		
		//check doubleset
		Assert.assertEquals("double set should be done",street, n1.getStreet());
		
	}
	
	
	
	@Test
	public void testAddZips() {
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		
		List<String> zips = new ArrayList<String>();
		
		zips.add("75000");
		street.addZips(zips);
		
		Assert.assertEquals(1, street.getIsInZip().size());
		street.addZip("78000");
		Assert.assertEquals(2, street.getIsInZip().size());
		
		
	}
	
	@Test
	public void testAddAlternateNames() {
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		
		List<AlternateOsmName> names = new ArrayList<AlternateOsmName>();
		
		names.add(new AlternateOsmName("foo", AlternateNameSource.OPENSTREETMAP));
		street.addAlternateNames(names);
		
		Assert.assertEquals(1, street.getAlternateNames().size());
		Assert.assertEquals("double set is not correct",street, street.getAlternateNames().get(0).getStreet());
		
		street.addAlternateName(new AlternateOsmName("bar", AlternateNameSource.OPENSTREETMAP));
		Assert.assertEquals("add should add not replace",2, street.getAlternateNames().size());
		
		Assert.assertEquals("double set is not correct",street, street.getAlternateNames().get(1).getStreet());
		
	}
	
	@Test
	public void testAddAlternateNamesShouldNotAddTooLongNames() {
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		
		List<AlternateOsmName> names = new ArrayList<AlternateOsmName>();
		
		names.add(new AlternateOsmName("foo", AlternateNameSource.OPENSTREETMAP));
		street.addAlternateNames(names);
		
		Assert.assertEquals(1, street.getAlternateNames().size());
		Assert.assertEquals("double set is not correct",street, street.getAlternateNames().get(0).getStreet());
		
		street.addAlternateName(new AlternateOsmName(StringUtils.repeat("a", OpenStreetMap.MAX_ALTERNATENAME_SIZE+1), AlternateNameSource.OPENSTREETMAP));
		Assert.assertEquals("add should add not too long names",1, street.getAlternateNames().size());
		
		Assert.assertEquals("double set is not correct",street, street.getAlternateNames().get(0).getStreet());
		
	}

}
