package com.gisgraphy.domain.geoloc.entity;

import net.sf.jstester.util.Assert;

import org.junit.Test;

import com.gisgraphy.helper.GeolocHelper;

public class HouseNumberTest {

	@Test
	public void testConstructorPointNumber(){
		HouseNumber houseNumber = new HouseNumber("foo",GeolocHelper.createPoint(3D, 4D));
		Assert.assertEquals("foo", houseNumber.getNumber());
		Assert.assertNotNull(houseNumber.getLocation());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorPointNumber_NullNumber(){
		new HouseNumber(null,GeolocHelper.createPoint(3D, 4D));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorPointNumber_EmptyNumber(){
		new HouseNumber("",GeolocHelper.createPoint(3D, 4D));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorPointNumber_NullLocation(){
		new HouseNumber("foo",null);
	}
	
	
	@Test
	public void testEquals() {
		HouseNumber hn1 = new HouseNumber();
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		hn1.setNumber("1");
		hn1.setStreet(street);
		hn1.setId(1L);
		
		HouseNumber same = new HouseNumber();
		same.setStreet(street);
		same.setNumber("1");
		
		Assert.assertFalse("same number and street=>not equals but same",hn1.equals(same));
		
		
		HouseNumber hnNotSameNumber = new HouseNumber();
		hnNotSameNumber.setStreet(street);
		hnNotSameNumber.setNumber("2");
		
		Assert.assertFalse("not same number=>not equals",hn1.equals(hnNotSameNumber));
		
		Long openstreetmapId=1L;
		//now we put the same openstreetmapid
		hn1.setOpenstreetmapId(openstreetmapId);
		hnNotSameNumber.setOpenstreetmapId(openstreetmapId);
		Assert.assertFalse("same openstreetmapId=>equals",hn1.equals(hnNotSameNumber));

		HouseNumber hnNotSameStreet = new HouseNumber();
		OpenStreetMap street2 = new OpenStreetMap();
		street2.setId(2L);
		hnNotSameStreet.setNumber("1");
		hnNotSameStreet.setStreet(street2);
		
		Assert.assertFalse("not same street=>not equals",hn1.equals(hnNotSameStreet));
		
		//now we put the same openstreetmapid
		hn1.setOpenstreetmapId(openstreetmapId);
		hnNotSameStreet.setOpenstreetmapId(openstreetmapId);
		Assert.assertFalse("same openstreetmapId=>not equals",hn1.equals(hnNotSameStreet));
		
		//test when name is not the same and number is not null
		HouseNumber hn3 = new HouseNumber();
		hn3.setNumber("1");
		hn3.setName("name");
		hn3.setStreet(street);
		
		HouseNumber hn4 = new HouseNumber();
		hn4.setNumber("1");
		hn4.setStreet(street);
		
		Assert.assertTrue("name should not impact equals when number is not null",hn4.equals(hn3));
		//now we put the same openstreetmapid
		hn4.setOpenstreetmapId(openstreetmapId);
		hn3.setOpenstreetmapId(openstreetmapId);
		Assert.assertTrue("same openstreetmapId=>equals",hn4.equals(hn3));
		
		
		//if number is null, they should have the same street and name
		HouseNumber hnWithoutNumber = new HouseNumber();
		hnWithoutNumber.setName("name");
		hnWithoutNumber.setStreet(street);
		
		HouseNumber hnWithoutNumberButSameName = new HouseNumber();
		hnWithoutNumberButSameName.setName("name");
		hnWithoutNumberButSameName.setStreet(street);
		
		Assert.assertTrue("number is null, name should be checked",hnWithoutNumber.is_same(hnWithoutNumberButSameName));
		
		HouseNumber hnWithoutNumberDifferentName = new HouseNumber();
		hnWithoutNumberDifferentName.setName("differentName");
		hnWithoutNumberDifferentName.setStreet(street);
		
		Assert.assertFalse("number is null, name should be checked",hnWithoutNumber.is_same(hnWithoutNumberDifferentName));
		//now we put the same openstreetmapid
		hnWithoutNumber.setOpenstreetmapId(openstreetmapId);
		hnWithoutNumberDifferentName.setOpenstreetmapId(openstreetmapId);
		Assert.assertTrue("same openstreetmapId=>equals",hnWithoutNumberDifferentName.equals(hnWithoutNumber));
	}
	
	@Test
	public void testIs_Same() {
		HouseNumber hn1 = new HouseNumber();
		OpenStreetMap street = new OpenStreetMap();
		street.setId(1L);
		hn1.setNumber("1");
		hn1.setStreet(street);
		
		HouseNumber hn2 = new HouseNumber();
		hn2.setStreet(street);
		hn2.setNumber("1");
		
		HouseNumber hnNotSameNumber = new HouseNumber();
		hnNotSameNumber.setStreet(street);
		hnNotSameNumber.setNumber("2");
		
		Assert.assertFalse("not same number=>not equals",hn1.is_same(hnNotSameNumber));

		HouseNumber hnNotSameStreet = new HouseNumber();
		OpenStreetMap street2 = new OpenStreetMap();
		street2.setId(2L);
		hnNotSameStreet.setNumber("1");
		hnNotSameStreet.setStreet(street2);
		
		Assert.assertFalse("not same street=>not equals",hn1.is_same(hnNotSameStreet));
		
		//test when name is not the same and number is not null
		HouseNumber hn3 = new HouseNumber();
		hn3.setNumber("1");
		hn3.setName("name");
		hn3.setStreet(street);
		
		HouseNumber hn4 = new HouseNumber();
		hn4.setNumber("1");
		hn4.setStreet(street);
		
		Assert.assertTrue("name should not impact equals when number is not null",hn4.is_same(hn3));
		
		
		//if number is null, they should have the same street and name
		HouseNumber hnWithoutNumber = new HouseNumber();
		hnWithoutNumber.setName("name");
		hnWithoutNumber.setStreet(street);
		
		HouseNumber hnWithoutNumberButSameName = new HouseNumber();
		hnWithoutNumberButSameName.setName("name");
		hnWithoutNumberButSameName.setStreet(street);
		
		Assert.assertTrue("number is null, name should be checked",hnWithoutNumber.is_same(hnWithoutNumberButSameName));
		
		HouseNumber hnWithoutNumberDifferentName = new HouseNumber();
		hnWithoutNumberDifferentName.setName("differentName");
		hnWithoutNumberDifferentName.setStreet(street);
		
		Assert.assertFalse("number is null, name should be checked",hnWithoutNumber.is_same(hnWithoutNumberDifferentName));
	}

}
