package com.gisgraphy.street;

import static com.gisgraphy.street.HouseNumberUtil.normalizeNumber;
import static com.gisgraphy.street.HouseNumberUtil.normalizeSkCzNumber;
import net.sf.jstester.util.Assert;

import org.junit.Test;

public class HouseNumberUtilTest {
	
	@Test
	public void normalizeNumberTest(){
		Assert.assertEquals("1", normalizeNumber("-1"));
		Assert.assertEquals(null, normalizeNumber("?"));
		Assert.assertEquals("3", normalizeNumber("ev.3"));
		Assert.assertEquals("11", normalizeNumber("11 d"));
		Assert.assertEquals("2", normalizeNumber("2/1"));
		Assert.assertEquals("1", normalizeNumber("1-3"));
		Assert.assertEquals("26", normalizeNumber("26 bis"));
		Assert.assertEquals(null, normalizeNumber("A"));
		Assert.assertEquals(null, normalizeNumber(""));
		Assert.assertEquals(null, normalizeNumber(null));
	}
	
	@Test
	public void normalizeSKCZNumberTest(){
		Assert.assertEquals("1", normalizeSkCzNumber("-1"));
		Assert.assertEquals(null, normalizeSkCzNumber("?"));
		Assert.assertEquals("3", normalizeSkCzNumber("ev.3"));
		Assert.assertEquals("11", normalizeSkCzNumber("11 d"));
		Assert.assertEquals("1", normalizeSkCzNumber("2/1"));
		Assert.assertEquals("3", normalizeSkCzNumber("1-3"));
		Assert.assertEquals(null, normalizeSkCzNumber("A"));
		Assert.assertEquals(null, normalizeSkCzNumber(""));
		Assert.assertEquals(null, normalizeSkCzNumber(null));
	}

}
