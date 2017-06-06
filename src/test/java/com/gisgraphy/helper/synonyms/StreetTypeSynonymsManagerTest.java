package com.gisgraphy.helper.synonyms;

import org.junit.Assert;
import org.junit.Test;

public class StreetTypeSynonymsManagerTest {

	@Test
	public void init() {
		Assert.assertNotNull(StreetTypeSynonymsManager.getInstance()); 
	}
	
	

	@Test
	public void getSynonymsFinderFromLanguage(){
		StreetTypeSynonymsManager sm =  StreetTypeSynonymsManager.getInstance();
		Assert.assertNull(sm.getSynonymsFinderFromLanguage(""));
		Assert.assertNull(sm.getSynonymsFinderFromLanguage(" "));
		Assert.assertNull(sm.getSynonymsFinderFromLanguage("foo"));
		Assert.assertNotNull(sm.getSynonymsFinderFromLanguage("pt"));
		Assert.assertNotNull(sm.getSynonymsFinderFromLanguage("pt"));
	}
}
