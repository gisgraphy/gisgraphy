package com.gisgraphy.importer.dto;

import java.util.ArrayList;
import java.util.List;

import net.sf.jstester.util.Assert;

import org.junit.Test;

public class AssociatedStreetHouseNumberTest {

	@Test
	public void getStreetMembers() {
		//setup
		AssociatedStreetHouseNumber street = new AssociatedStreetHouseNumber();
		
		Assert.assertNotNull(street.getStreetMembers());
		Assert.assertEquals(0, street.getStreetMembers().size());
		
		AssociatedStreetMember m1 = new AssociatedStreetMember();
		m1.setRole("HoUsE");
		
		AssociatedStreetMember m2 = new AssociatedStreetMember();
		m2.setRole("StReEt");//street
		Assert.assertTrue(m2.isStreet());
		
		
		List<AssociatedStreetMember> associatedStreetMember = new ArrayList<AssociatedStreetMember>();
		associatedStreetMember.add(m1);
		associatedStreetMember.add(m2);
		street.setAssociatedStreetMember(associatedStreetMember );
		
		//exercise
		List<AssociatedStreetMember> streetMember = street.getStreetMembers();
		
		//verify
		Assert.assertNotNull(streetMember);
		Assert.assertEquals(1, streetMember.size());
		Assert.assertEquals(m2, streetMember.get(0));
	}
	
	@Test
	public void getHouseMembers() {
		//setup
		AssociatedStreetHouseNumber street = new AssociatedStreetHouseNumber();
		
		Assert.assertNotNull(street.getHouseMembers());
		Assert.assertEquals(0, street.getHouseMembers().size());
		
		AssociatedStreetMember m1 = new AssociatedStreetMember();
		m1.setRole("HoUsE");//house
		
		AssociatedStreetMember m2 = new AssociatedStreetMember();
		m2.setRole("StReEt");
		List<AssociatedStreetMember> associatedStreetMember = new ArrayList<AssociatedStreetMember>();
		associatedStreetMember.add(m1);
		associatedStreetMember.add(m2);
		street.setAssociatedStreetMember(associatedStreetMember );
		
		//exercise
		List<AssociatedStreetMember> houseMember = street.getHouseMembers();
		
		//verify
		Assert.assertNotNull(houseMember);
		Assert.assertEquals(1, houseMember.size());
		Assert.assertEquals(m1, houseMember.get(0));
	}

}
