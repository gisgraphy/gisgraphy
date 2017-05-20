package com.gisgraphy.importer;

import net.sf.jstester.util.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

public class OpenAddressesSimpleImporterTest {

	@Test
	public void extractCountrycode() {
		OpenAddressesSimpleImporter importer = new OpenAddressesSimpleImporter();
		Assert.assertEquals("AR", importer.extractCountrycode("ar:d9728f782bae5457"));
		Assert.assertEquals(null, importer.extractCountrycode("d9728f782bae5457"));
		Assert.assertEquals(null, importer.extractCountrycode(""));
		Assert.assertEquals(null, importer.extractCountrycode(null));
	}
	
	@Test
	public void testIsAllRequiredFieldspresent(){
		OpenAddressesSimpleImporter importer = new OpenAddressesSimpleImporter();
		String[] array = {"","",""};
		Assert.assertFalse(importer.isAllRequiredFieldspresent(array));
		
		 array = new String[] {"","b",""};
		Assert.assertFalse(importer.isAllRequiredFieldspresent(array));
		
		 array = new String[] {"","","c"};
		Assert.assertFalse(importer.isAllRequiredFieldspresent(array));
		
		 array = new String[] {"a","b",""};
		Assert.assertFalse(importer.isAllRequiredFieldspresent(array));
		
		 array = new String[] {"","b","c"};
		Assert.assertFalse(importer.isAllRequiredFieldspresent(array));
		
		 array = new String[] {"a","",""};
		Assert.assertFalse(importer.isAllRequiredFieldspresent(array));
		
		 array = new String[]{"a","b","c"};
		Assert.assertTrue(importer.isAllRequiredFieldspresent(array));
		
	}
	
	@Test
	public void cleanupStreetName(){
		OpenAddressesSimpleImporter importer = new OpenAddressesSimpleImporter();
		Assert.assertEquals("1st avenue", importer.cleanupStreetName("01st  avenue "));
		Assert.assertEquals(null, importer.cleanupStreetName(null));
		
		String longString = RandomStringUtils.random(OpenAddressesSimpleImporter.MAX_NAME_SIZE+10,new char[] {'e'});
		longString = importer.cleanupStreetName(longString);
		Assert.assertEquals("the string to test is not of the expected size the test will fail",OpenAddressesSimpleImporter.MAX_NAME_SIZE, longString.length());
	}
	
	@Test
	public void isZeroHouseNumber(){
		OpenAddressesSimpleImporter importer = new OpenAddressesSimpleImporter();
		Assert.assertTrue(importer.isZeroHouseNumber("0"));
		Assert.assertTrue(importer.isZeroHouseNumber("00"));
		Assert.assertFalse(importer.isZeroHouseNumber(""));
		Assert.assertFalse(importer.isZeroHouseNumber(null));
		Assert.assertFalse(importer.isZeroHouseNumber("01"));
	}
	
	@Test
	public void isUnWantedHouseNumber(){
		OpenAddressesSimpleImporter importer = new OpenAddressesSimpleImporter();
		Assert.assertTrue(importer.isUnWantedHouseNumber("0"));
	}
	
	@Test
	public void isUnWantedStreetName(){
		OpenAddressesSimpleImporter importer = new OpenAddressesSimpleImporter();
		Assert.assertTrue(importer.isUnWantedStreetName("UNAVAILABLE"));
		Assert.assertTrue(importer.isUnWantedStreetName("NULL"));
		Assert.assertTrue(importer.isUnWantedStreetName("UNDEFINED"));
		Assert.assertTrue(importer.isUnWantedStreetName(""));
		Assert.assertTrue(importer.isUnWantedStreetName(" "));
		Assert.assertTrue(importer.isUnWantedStreetName(null));
		Assert.assertTrue(importer.isUnWantedStreetName("nr"));
		Assert.assertFalse(importer.isUnWantedStreetName("foo"));
	}
	
	@Test
	public void cleanNumber(){
		OpenAddressesSimpleImporter importer = new OpenAddressesSimpleImporter();
		Assert.assertEquals(null, importer.cleanNumber(""));
		Assert.assertEquals(null, importer.cleanNumber(" "));
		Assert.assertEquals(null, importer.cleanNumber(" 000000000000000000 "));
		Assert.assertEquals(null, importer.cleanNumber("000000000000000000"));
		Assert.assertEquals("3", importer.cleanNumber("0003"));
	}

}
