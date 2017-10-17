package com.gisgraphy.domain.geoloc.entity;

import net.sf.jstester.util.Assert;

import org.junit.Test;

import com.gisgraphy.domain.valueobject.AlternateNameSource;

public class AlternateOsmNameTest {
	
	
	@Test
	public void testEqualsShouldBeCaseInsensitive(){
		AlternateOsmName name = new AlternateOsmName("name","fr", AlternateNameSource.OPENSTREETMAP,"DE");
		Assert.assertTrue(name.equals(new AlternateOsmName("name","fr", AlternateNameSource.OPENSTREETMAP,"DE")));
		Assert.assertTrue(name.equals(new AlternateOsmName("name","FR", AlternateNameSource.OPENSTREETMAP,"DE")));
		Assert.assertTrue(name.equals(new AlternateOsmName("NAME","fr", AlternateNameSource.OPENSTREETMAP,"DE")));
		Assert.assertTrue(name.equals(new AlternateOsmName("NAME","FR", AlternateNameSource.OPENSTREETMAP,"DE")));
		Assert.assertFalse(name.equals(new AlternateOsmName("name2","fr", AlternateNameSource.OPENSTREETMAP,"DE")));
		
		name.setId(123L);
		AlternateOsmName nameSameIdDifferentName =	new AlternateOsmName("nameDiffernetName","FR", AlternateNameSource.OPENSTREETMAP,"DE");
		nameSameIdDifferentName.setId(123L);
		Assert.assertTrue(name.equals(nameSameIdDifferentName));
		
		AlternateOsmName nameNotSameIdSameName =	new AlternateOsmName("name","FR", AlternateNameSource.OPENSTREETMAP,"DE");
		nameNotSameIdSameName.setId(1234L);
		Assert.assertFalse(name.equals(nameNotSameIdSameName));
	}
	

	@Test
	public void testConstructor_LanguageShouldBeInUppercase() {
		AlternateOsmName name = new AlternateOsmName("name","fr", AlternateNameSource.OPENSTREETMAP,"DE");
		Assert.assertEquals("FR", name.getLanguage());
		Assert.assertEquals("name", name.getName());
		Assert.assertEquals(AlternateNameSource.OPENSTREETMAP, name.getSource());
		
	}
	
	@Test
	public void testSetter_LanguageShouldBeInUppercase() {
		AlternateOsmName name = new AlternateOsmName("name","foo", AlternateNameSource.OPENSTREETMAP,"DE");
		name.setLanguage("fr");
		Assert.assertEquals("FR", name.getLanguage());
		Assert.assertEquals("name", name.getName());
		Assert.assertEquals(AlternateNameSource.OPENSTREETMAP, name.getSource());
		
	}

}
