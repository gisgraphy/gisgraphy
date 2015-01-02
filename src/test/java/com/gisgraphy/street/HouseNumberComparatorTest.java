package com.gisgraphy.street;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;

import edu.emory.mathcs.backport.java.util.Collections;

public class HouseNumberComparatorTest {

	@Test
	public void compare() {
		HouseNumber h1 = new HouseNumber();
		h1.setNumber("1");
		HouseNumber h2 = new HouseNumber();
		h2.setNumber("2");
		HouseNumber h0 = new HouseNumber();
		HouseNumber hnull= null;
		List<HouseNumber> houseNumbers = new ArrayList<HouseNumber>();
		houseNumbers.add(h1);
		houseNumbers.add(h2);
		houseNumbers.add(h0);
		houseNumbers.add(hnull);
		
		Collections.sort(houseNumbers,new HouseNumberComparator());
		Assert.assertEquals(h1, houseNumbers.get(0));
		Assert.assertEquals(h2, houseNumbers.get(1));
		Assert.assertEquals(h0, houseNumbers.get(2));
		Assert.assertEquals(hnull, houseNumbers.get(3));
	}

}
