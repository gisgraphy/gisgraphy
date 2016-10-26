package com.gisgraphy.importer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class AdmDTOTest {

	@Test
	public void compare() {
		AdmDTO dto1 = new AdmDTO("admlevel1", 1, 10L);
		AdmDTO dto2 = new AdmDTO("admlevel2", 2, 20L);
		AdmDTO dto3 = new AdmDTO("admlevel3", 3, 30L);
		List<AdmDTO> adms = new ArrayList<AdmDTO>();
		adms.add(dto2);
		adms.add(dto3);
		adms.add(dto1);
		Collections.sort(adms);
		Assert.assertEquals(dto1, adms.get(0) );
		Assert.assertEquals(dto2, adms.get(1) );
		Assert.assertEquals(dto3, adms.get(2) );
		System.out.println(adms);
		
	}

}
