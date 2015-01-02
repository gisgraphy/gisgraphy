package com.gisgraphy.fulltext;

import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class SmartStreetDetectionTest {

	@Test
	public void getRegexpShouldCompile() {
		String regexp = SmartStreetDetection.getRegexp();
		Pattern.compile(regexp);
	}
	
	@Test
	public void getStreetTypes() {
		SmartStreetDetection ssd =new SmartStreetDetection();
		//NOTHING
		Assert.assertEquals(0, ssd.getStreetTypes("saint denis").size());

		//FRENCH
		Assert.assertEquals(1, ssd.getStreetTypes("rue saint denis").size());
		Assert.assertEquals("rue", ssd.getStreetTypes("rue saint denis").get(0));
		
		//CASE
		Assert.assertEquals(1, ssd.getStreetTypes("RUE saint denis").size());
		Assert.assertEquals("RUE", ssd.getStreetTypes("RUE saint denis").get(0));
		
		//DECOMPOUND
		Assert.assertEquals(1, ssd.getStreetTypes("saint denis straße").size());
		Assert.assertEquals("straße", ssd.getStreetTypes("saint denis straße").get(0));
		
		//NOT DECOMPOUND
		Assert.assertEquals(1, ssd.getStreetTypes("saintdenisstraße").size());
		Assert.assertEquals("straße", ssd.getStreetTypes("saintdenisstraße").get(0));
		
		//ACCENT
		Assert.assertEquals(1, ssd.getStreetTypes("saint denis plätze").size());
		Assert.assertEquals("plätze", ssd.getStreetTypes("saint denis plätze").get(0));
		
		//SPANISH
		Assert.assertEquals(1, ssd.getStreetTypes("saint denis rua").size());
		Assert.assertEquals("rua", ssd.getStreetTypes("saint denis rua").get(0));
		
		//TWO
		Assert.assertEquals(2, ssd.getStreetTypes("RUE du chemin blanc qui danse").size());
		Assert.assertEquals("RUE", ssd.getStreetTypes("RUE du chemin blanc qui danse").get(0));
		Assert.assertEquals("chemin", ssd.getStreetTypes("RUE du chemin blanc qui danse").get(1));
		
		
	}

}
