package com.gisgraphy.importer.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.helper.GeolocHelper;



public class HouseNumberPointComparatorTest {

	@Test
	public void compare() {
		HouseNumberPoint h1 = new HouseNumberPoint(GeolocHelper.createPoint(2F, 3F),"1");
		HouseNumberPoint h2 = new HouseNumberPoint(GeolocHelper.createPoint(2F, 3F),"2");
		HouseNumberPoint h0 = new HouseNumberPoint(GeolocHelper.createPoint(2F, 3F),null);
		HouseNumberPoint hnull= null;
		List<HouseNumberPoint> houseNumbers = new ArrayList<HouseNumberPoint>();
		houseNumbers.add(h1);
		houseNumbers.add(h2);
		houseNumbers.add(h0);
		houseNumbers.add(hnull);
		
		Collections.sort(houseNumbers,new HouseNumberPointComparator());
		Assert.assertEquals(h1, houseNumbers.get(0));
		Assert.assertEquals(h2, houseNumbers.get(1));
		Assert.assertEquals(h0, houseNumbers.get(2));
		Assert.assertEquals(hnull, houseNumbers.get(3));
	}

}
