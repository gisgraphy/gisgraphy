package com.gisgraphy.helper;

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

}
