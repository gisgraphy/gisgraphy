package com.gisgraphy.importer.dto;

import net.sf.jstester.util.Assert;

import org.junit.Test;

public class AssociatedStreetMemberTest {

	@Test
	public void isStreet() {
		AssociatedStreetMember m1 = new AssociatedStreetMember();
		m1.setRole("HoUsE");
		Assert.assertFalse(m1.isStreet());
		
		AssociatedStreetMember m2 = new AssociatedStreetMember();
		m2.setRole("StReEt");
		Assert.assertTrue(m2.isStreet());
		
		AssociatedStreetMember m3 = new AssociatedStreetMember();
		m3.setRole("Way");
		m3.setHouseNumber("3");//
		Assert.assertFalse("not a street because only contains number",m3.isStreet());
		
		AssociatedStreetMember m6 = new AssociatedStreetMember();
		m6.setRole("Way");
		m6.setHouseNumber("3as");//
		Assert.assertTrue("A street because not only contains number",m6.isStreet());
		
		AssociatedStreetMember m4 = new AssociatedStreetMember();
		m4.setRole("Way");
		m4.setHouseNumber("foo");//
		Assert.assertTrue("a street because not contains number",m4.isStreet());
		
		AssociatedStreetMember m5 = new AssociatedStreetMember();
		m5.setHouseNumber("foo");
		m5.setStreetName("foo");//a street because house=streetname
		Assert.assertTrue("a street because house=streetname",m5.isStreet());
	}
	
	@Test
	public void isHouse() {
		AssociatedStreetMember m1 = new AssociatedStreetMember();
		m1.setRole("HoUsE");
		Assert.assertTrue(m1.isHouse());
		
		AssociatedStreetMember m2 = new AssociatedStreetMember();
		m2.setRole("StReEt");
		Assert.assertFalse(m2.isHouse());
		
		AssociatedStreetMember m3 = new AssociatedStreetMember();
		m3.setRole("nodE");
		m3.setHouseNumber("3");//
		Assert.assertTrue("a house because only contains number",m3.isHouse());
		
		AssociatedStreetMember m6 = new AssociatedStreetMember();
		m6.setRole("nodE");
		m6.setHouseNumber("3as");//
		Assert.assertTrue("A street because not only contains number",m6.isHouse());
		
		AssociatedStreetMember m4 = new AssociatedStreetMember();
		m4.setRole("node");
		m4.setHouseNumber("foo");//
		Assert.assertTrue("a street because not contains number",m4.isHouse());
		
		AssociatedStreetMember m5 = new AssociatedStreetMember();
		m5.setHouseNumber("foo");
		m5.setStreetName("foo");//a street because house=streetname
		Assert.assertFalse("a street because house=streetname",m5.isHouse());
	}

}
