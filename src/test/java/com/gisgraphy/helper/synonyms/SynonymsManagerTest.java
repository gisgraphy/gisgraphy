package com.gisgraphy.helper.synonyms;

import org.junit.Assert;
import org.junit.Test;

public class SynonymsManagerTest {

	@Test
	public void init() {
		Assert.assertNotNull(SynonymsManager.getInstance()); 
	}
	
	

	@Test
	public void getSynonymsFinderFromLanguage(){
		SynonymsManager sm =  SynonymsManager.getInstance();
		Assert.assertNull(sm.getSynonymsFinderFromLanguage(""));
		Assert.assertNull(sm.getSynonymsFinderFromLanguage(" "));
		Assert.assertNull(sm.getSynonymsFinderFromLanguage("foo"));
		Assert.assertNotNull(sm.getSynonymsFinderFromLanguage("pt"));
		Assert.assertNotNull(sm.getSynonymsFinderFromLanguage("pt"));
	}
}
