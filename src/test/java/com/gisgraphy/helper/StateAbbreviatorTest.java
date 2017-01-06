package com.gisgraphy.helper;

import junit.framework.Assert;

import org.junit.Test;

public class StateAbbreviatorTest {

	@Test
	public void testNumberOfCountries() {
		Assert.assertEquals(8, StateAbbreviator.getNumberOfCountries());
	}
	
	@Test
	public void testgetAbbreviation() {
		 Assert.assertNull(StateAbbreviator.getAbbreviation("unknow country", "foo"));
		 Assert.assertNull(StateAbbreviator.getAbbreviation("unknow country", null));
		 Assert.assertNull(StateAbbreviator.getAbbreviation(null, "foo"));
		 Assert.assertNull(StateAbbreviator.getAbbreviation(null, null));
		 Assert.assertEquals("exact","NL",StateAbbreviator.getAbbreviation("MX", "Nuevo León"));
		 Assert.assertEquals("accent","NL",StateAbbreviator.getAbbreviation("MX", "Nuevo Leon"));
		 Assert.assertEquals("casse name","NL",StateAbbreviator.getAbbreviation("MX", "nuevo Leon"));
		 Assert.assertEquals("casse country","NL",StateAbbreviator.getAbbreviation("mx", "nuevo Leon"));
		 Assert.assertEquals("trim name","NL",StateAbbreviator.getAbbreviation("MX", " nuevo Leon"));
		 Assert.assertEquals("trim country code","NL",StateAbbreviator.getAbbreviation("MX ", " nuevo Leon"));
	}
	
	@Test
	public void testaddStateCode(){
		 Assert.assertNull(StateAbbreviator.getAbbreviation("unknow country", "foo"));
		 Assert.assertNull(StateAbbreviator.getAbbreviation("unknow country", null));
		 Assert.assertNull(StateAbbreviator.getAbbreviation(null, "foo"));
		 Assert.assertNull(StateAbbreviator.getAbbreviation(null, null));
		 Assert.assertEquals("exact","Nuevo León (NL)",StateAbbreviator.addStateCode("MX", "Nuevo León"));
		 Assert.assertEquals("accent","Nuevo Leon (NL)",StateAbbreviator.addStateCode("MX", "Nuevo Leon"));
		 Assert.assertEquals("casse name","nuevo Leon (NL)",StateAbbreviator.addStateCode("MX", "nuevo Leon"));
		 Assert.assertEquals("casse country","nuevo Leon (NL)",StateAbbreviator.addStateCode("mx", "nuevo Leon"));
		 Assert.assertEquals("trim name","nuevo Leon (NL)",StateAbbreviator.addStateCode("MX", " nuevo Leon"));
		 Assert.assertEquals("trim country code","nuevo Leon (NL)",StateAbbreviator.addStateCode("MX ", " nuevo Leon"));
	}

}
