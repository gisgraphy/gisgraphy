package com.gisgraphy.helper;

import org.junit.Assert;
import org.junit.Test;

public class CountryDetectorTest {

	
	
	@Test
	public void detectAndRemoveCountry() {
		CountryDetector detector = new CountryDetector();
		CountryDetectorDto dto = detector.detectAndRemoveCountry("via alba,1 12100 Cuneo italy");
		Assert.assertEquals("IT", dto.getCountryCode());
		Assert.assertEquals("via alba,1 12100 Cuneo", dto.getAddress());
	
		dto = detector.detectAndRemoveCountry("via italy,1 12100 Cuneo italy");
		Assert.assertEquals("IT", dto.getCountryCode());
		Assert.assertEquals("via italy,1 12100 Cuneo", dto.getAddress());
		
		dto = detector.detectAndRemoveCountry("via italy,1 12100 Cuneo Italie");
		Assert.assertEquals("IT", dto.getCountryCode());
		Assert.assertEquals("via italy,1 12100 Cuneo", dto.getAddress());
		
		dto = detector.detectAndRemoveCountry("via alba,1 12100 Cuneo");
		Assert.assertEquals(null, dto.getCountryCode());
		Assert.assertEquals("via alba,1 12100 Cuneo", dto.getAddress());
		
		dto = detector.detectAndRemoveCountry("via alba,1 12100 Cuneo Singapour");
		Assert.assertEquals(null, dto.getCountryCode());
		Assert.assertEquals("via alba,1 12100 Cuneo Singapour", dto.getAddress());
	
	}
	
	@Test
	public void DetectAndRemoveCountry_performance() {
		CountryDetector detector = new CountryDetector();
		long start = System.currentTimeMillis();
		for (int i=0;i<1000;i++){
			CountryDetectorDto dto = detector.detectAndRemoveCountry("via alba,1 12100 Cuneo italy");
			Assert.assertEquals("IT", dto.getCountryCode());
			Assert.assertEquals("via alba,1 12100 Cuneo", dto.getAddress());
		}
		System.out.println(System.currentTimeMillis()-start);
	}

}
