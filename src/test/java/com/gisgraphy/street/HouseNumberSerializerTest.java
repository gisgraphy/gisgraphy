package com.gisgraphy.street;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.helper.GeolocHelper;

public class HouseNumberSerializerTest {

	@Test
	public void serializeList() {
		HouseNumberSerializer serializer = new HouseNumberSerializer();
		HouseNumber houseNumber = new HouseNumber();
		houseNumber.setLocation(GeolocHelper.createPoint(10.3D, 9.6D));
		houseNumber.setNumber("1:");//with 
		
		HouseNumber houseNumber2 = new HouseNumber();
		houseNumber2.setLocation(GeolocHelper.createPoint(10.4D, 9.7D));
		houseNumber2.setNumber("2");
		
		HouseNumber houseNumberNull = new HouseNumber();
		houseNumberNull.setLocation(GeolocHelper.createPoint(10.4D, 9.7D));
		houseNumberNull.setNumber(null);
		
		List<HouseNumber> houseNumberList = new ArrayList<HouseNumber>();
		houseNumberList.add(houseNumber);
		houseNumberList.add(houseNumber2);
		houseNumberList.add(houseNumberNull);
		houseNumberList.add(null);
		String actual = serializer.serializeList(houseNumberList);
		assertEquals("1:10.3,9.6  2:10.4,9.7", actual);
	}
	
	@Test
	public void serialize() {
		HouseNumberSerializer serializer = new HouseNumberSerializer();
		HouseNumber houseNumber = new HouseNumber();
		houseNumber.setLocation(GeolocHelper.createPoint(10.3D, 9.6D));
		houseNumber.setNumber("1:");//with special char
		String actual = serializer.serialize(houseNumber);
		assertEquals("1:10.3,9.6", actual);
		
		HouseNumber houseNumber2 = new HouseNumber();
		houseNumber2.setLocation(GeolocHelper.createPoint(10.4D, 9.7D));
		houseNumber2.setNumber("2");
		actual = serializer.serialize(houseNumber2);
		assertEquals("2:10.4,9.7", actual);
		
		HouseNumber houseNumberNull = new HouseNumber();
		houseNumberNull.setLocation(GeolocHelper.createPoint(10.4D, 9.7D));
		houseNumberNull.setNumber(null);
		
		actual = serializer.serialize(houseNumberNull);
		assertEquals(null, actual);
		
		actual = serializer.serialize(null);
		assertEquals(null, actual);
		
	}
	

}
