package com.gisgraphy.helper;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class AdmStateLevelInfoTest {

	@Test
	public void getLowLevel() {
		Assert.assertEquals(3,AdmStateLevelInfo.getLowLevel("BR"));
		Assert.assertEquals(4,AdmStateLevelInfo.getLowLevel("FR"));
		Assert.assertEquals(4,AdmStateLevelInfo.getLowLevel("ZZ"));
		Assert.assertEquals(4,AdmStateLevelInfo.getLowLevel(null));
		
	}
	
	@Test
	public void getHighLevel() {
		Assert.assertEquals(6,AdmStateLevelInfo.getHighLevel("NO"));
		Assert.assertEquals(7,AdmStateLevelInfo.getHighLevel("FR"));
		Assert.assertEquals(7,AdmStateLevelInfo.getHighLevel("ZZ"));
		Assert.assertEquals(7,AdmStateLevelInfo.getHighLevel(null));
		
	}
	
	@Test
	public void AllcountryShouldHaveCoherentValues(){
		for (String countryCode :CountriesStaticData.getCountryCodes()){
			if (AdmStateLevelInfo.getLowLevel(countryCode)>AdmStateLevelInfo.getHighLevel(countryCode)){
				Assert.fail("wrong level set for country "+countryCode  +" : low="+AdmStateLevelInfo.getLowLevel(countryCode)+" and high="+AdmStateLevelInfo.getHighLevel(countryCode));
			}
			if (AdmStateLevelInfo.getHighLevel(countryCode) - AdmStateLevelInfo.getLowLevel(countryCode)>5){
				Assert.fail("too much difference ("+(AdmStateLevelInfo.getHighLevel(countryCode) - AdmStateLevelInfo.getLowLevel(countryCode))+") for country "+countryCode +" : low="+AdmStateLevelInfo.getLowLevel(countryCode)+" and high="+AdmStateLevelInfo.getHighLevel(countryCode));
			}
		}
		
	}
	
	@Test
	public void shouldBeImportedAsAdm(){
		Assert.assertFalse(AdmStateLevelInfo.shouldBeImportedAsAdm(null,0));
		Assert.assertFalse(AdmStateLevelInfo.shouldBeImportedAsAdm(null,3));
		Assert.assertFalse(AdmStateLevelInfo.shouldBeImportedAsAdm("FR",-1));
		
		Assert.assertFalse(AdmStateLevelInfo.shouldBeImportedAsAdm("FR",3));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("FR",4));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("FR",5));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("FR",6));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("FR",7));
		Assert.assertFalse(AdmStateLevelInfo.shouldBeImportedAsAdm("FR",8));
		
		//specfic country
		Assert.assertFalse(AdmStateLevelInfo.shouldBeImportedAsAdm("NO",3));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("NO",4));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("NO",5));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("NO",6));
		Assert.assertFalse(AdmStateLevelInfo.shouldBeImportedAsAdm("NO",7));
		
		//TN, LY has only one level
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("TN",4));
		Assert.assertTrue(AdmStateLevelInfo.shouldBeImportedAsAdm("LY",4));
		
		
		
	}
	@Test
	public void testIsCityLevelString(){
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel("", "8"));
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel(null, "8"));
		Assert.assertFalse(AdmStateLevelInfo.isCityLevel(null, ""));
		Assert.assertFalse(AdmStateLevelInfo.isCityLevel(null, null));
		Assert.assertFalse(AdmStateLevelInfo.isCityLevel("US", ""));
	
	}
	
	@Test
	public void testIsCityLevel(){
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel("", 8));
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel("ZZ", 8));
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel(null, 8));
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel("FR", 8));
		
		Assert.assertFalse(AdmStateLevelInfo.isCityLevel("LV", 6));
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel("LV", 7));
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel("LV", 8));
		Assert.assertTrue(AdmStateLevelInfo.isCityLevel("LV", 9));
		Assert.assertFalse(AdmStateLevelInfo.isCityLevel("LV", 10));
	}
	
	
	@Test
	public void testIsCitySubdivisionLevelString(){
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("", "8"));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel(null, "8"));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel(null, ""));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel(null, null));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("US", ""));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("US", null));
	
	}
	
	@Test
	public void testIsCitySubdivisionLevel(){
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("", 8));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("ZZ", 8));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel(null, 8));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("FR", 8));
		
		Assert.assertTrue(AdmStateLevelInfo.isCitySubdivisionLevel("", 9));
		Assert.assertTrue(AdmStateLevelInfo.isCitySubdivisionLevel("ZZ", 9));
		Assert.assertTrue(AdmStateLevelInfo.isCitySubdivisionLevel(null, 9));
		Assert.assertTrue(AdmStateLevelInfo.isCitySubdivisionLevel("FR", 9));
		
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("LV", 6));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("LV", 7));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("LV", 8));
		Assert.assertFalse(AdmStateLevelInfo.isCitySubdivisionLevel("LV", 9));
		Assert.assertTrue(AdmStateLevelInfo.isCitySubdivisionLevel("LV", 10));
	}
	

	@Test
	public void testIsCityLevelDoesnTOverlapADM(){
		for (String country: CountryInfo.countryLookupMap.keySet()){
			int[] levels = new int[]{1,2,3,4,5,6,7,8,9,11};
			for (int level: levels){
				if (AdmStateLevelInfo.isCityLevel(country, level) && AdmStateLevelInfo.shouldBeImportedAsAdm(country, level)){
					Assert.fail(level+" for country "+country+ " is acity and an adm level");
				}
				
			}
			
		}
	}

}
