package com.gisgraphy.helper.synonyms;

import org.junit.Assert;
import org.junit.Test;

public class SynonymsDtoTest {

	@Test
	public void constructor() {
		SynonymsDto dto = new SynonymsDto("Word1 ", "Word2 ", "Lang");
		Assert.assertEquals("word1", dto.getWord1());
		Assert.assertEquals("word2", dto.getWord2());
		Assert.assertEquals("lang", dto.getLang());
		
		//null should not throws
		dto = new SynonymsDto("Word1 ",null, "Lang");
		dto = new SynonymsDto("Word1 ", "Word2 ",null);
		dto = new SynonymsDto(null, "Word2 ", "Lang");
	}
	
	@Test
	public void eq(){
		SynonymsDto dto = new SynonymsDto("Word1 ", "Word2 ", "Lang");
		Assert.assertEquals(dto,  new SynonymsDto("Word2 ", "Word1 ", "Lang"));
		Assert.assertEquals(dto,  new SynonymsDto("Word2 ", "Word1 ", null));
	}

}
