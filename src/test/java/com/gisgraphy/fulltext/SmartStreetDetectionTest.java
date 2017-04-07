package com.gisgraphy.fulltext;

import java.util.regex.Pattern;

import org.junit.Assert;
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
		
		//Straße, case sensitive
		Assert.assertEquals(1, ssd.getStreetTypes("Steinau an der Straße").size());
		Assert.assertEquals("Straße", ssd.getStreetTypes("Steinau an der Straße").get(0));
		
		Assert.assertEquals(1, ssd.getStreetTypes("Der Queckborn unterm Schießplatze").size());
		Assert.assertEquals("platze", ssd.getStreetTypes("Der Queckborn unterm Schießplatze").get(0));
		
		//two ß
		Assert.assertEquals(1, ssd.getStreetTypes("Der Queckboßrn unterm Schießplatze").size());
		Assert.assertEquals("platze", ssd.getStreetTypes("Der Quecßkborn unterm Schießplatze").get(0));
		
		//two ß
		Assert.assertEquals(2, ssd.getStreetTypes("Der Straße unterm Schießplatze").size());
		Assert.assertEquals("platze", ssd.getStreetTypes("Der Straße unterm Schießplatze").get(1));
		Assert.assertEquals("Straße", ssd.getStreetTypes("Der Straße unterm Schießplatze").get(0));
		
		
		

		//FRENCH
		Assert.assertEquals(1, ssd.getStreetTypes("rue saint denis").size());
		Assert.assertEquals("rue", ssd.getStreetTypes("rue saint denis").get(0));
		
		//without accent
		Assert.assertEquals(1, ssd.getStreetTypes("place saint denis").size());
		Assert.assertEquals("place", ssd.getStreetTypes("place saint denis").get(0));
		
		//with accent
		Assert.assertEquals(1, ssd.getStreetTypes("plâce saint denis").size());
		Assert.assertEquals("plâce", ssd.getStreetTypes("plâce saint denis").get(0));
		
		//CASE
		Assert.assertEquals(1, ssd.getStreetTypes("RUE saint denis").size());
		Assert.assertEquals("RUE", ssd.getStreetTypes("RUE saint denis").get(0));
		
		//DECOMPOUND
		Assert.assertEquals(1, ssd.getStreetTypes("saint denis straße").size());
		Assert.assertEquals("straße", ssd.getStreetTypes("saint denis straße").get(0));
		
		//one ß not at the end
		Assert.assertEquals(1, ssd.getStreetTypes("saint straße denis ").size());
		Assert.assertEquals("straße", ssd.getStreetTypes("saint straße denis").get(0));
		
		//several ß not at the end
		Assert.assertEquals(2, ssd.getStreetTypes("saint straße denis straße").size());
		Assert.assertEquals("straße", ssd.getStreetTypes("saint straße denis straße").get(0));
		Assert.assertEquals("straße", ssd.getStreetTypes("saint straße denis straße").get(1));
		
		//NOT DECOMPOUND
		Assert.assertEquals(1, ssd.getStreetTypes("saintdenisstraße").size());
		Assert.assertEquals("straße", ssd.getStreetTypes("saintdenisstraße").get(0));
		//NOT DECOMPOUND
		Assert.assertEquals(1, ssd.getStreetTypes("saintdenisstr.").size());
		Assert.assertEquals("str", ssd.getStreetTypes("saintdenisstr.").get(0));
		
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
