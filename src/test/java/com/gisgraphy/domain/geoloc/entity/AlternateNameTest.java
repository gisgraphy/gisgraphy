package com.gisgraphy.domain.geoloc.entity;

import net.sf.jstester.util.Assert;

import org.junit.Test;

import com.gisgraphy.domain.valueobject.AlternateNameSource;

public class AlternateNameTest {

	@Test
	public void testConstructor_LanguageShouldBeInUppercase() {
		AlternateName name = new AlternateName("name","fr", AlternateNameSource.OPENSTREETMAP);
		Assert.assertEquals("FR", name.getLanguage());
		Assert.assertEquals("name", name.getName());
		Assert.assertEquals(AlternateNameSource.OPENSTREETMAP, name.getSource());
		
	}
	
	@Test
	public void testSetter_LanguageShouldBeInUppercase() {
		AlternateName name = new AlternateName("name","foo", AlternateNameSource.OPENSTREETMAP);
		name.setLanguage("fr");
		Assert.assertEquals("FR", name.getLanguage());
		Assert.assertEquals("name", name.getName());
		Assert.assertEquals(AlternateNameSource.OPENSTREETMAP, name.getSource());
		
	}

}
