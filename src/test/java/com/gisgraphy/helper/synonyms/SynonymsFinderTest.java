package com.gisgraphy.helper.synonyms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SynonymsFinderTest {

	public static final List<List<String>> TEST_SYNONYMS = new ArrayList<List<String>>(){{
		List<String> l1 = new ArrayList<String>(){{
			add("st.");
			add("saint");
			add("st");
		}
		};
		add(l1);
	}
	};

	public static final List<List<String>> mySynonyms = new ArrayList<List<String>>(){{
		List<String> l1 = new ArrayList<String>(){{
			add("doctor");
			add("dr");
		}
		};
		List<String> l2 = new ArrayList<String>(){{
			add("drive");
			add("dr");
			add("drv");
		}
		};
		add(l1);
		add(l2);
	};
	};
	
	public static final List<List<String>> mySynonyms_deep = new ArrayList<List<String>>(){{
		List<String> l1 = new ArrayList<String>(){{
			add("doctor");
			add("dr");
		}
		};
		List<String> l2 = new ArrayList<String>(){{
			add("drive");
			add("dr");
			add("drv");
		}
		};
		List<String> l3 = new ArrayList<String>(){{
			add("saint");
			add("st");
			add("santa");
		}
		};
		List<String> l4 = new ArrayList<String>(){{
			add("street");
			add("st");
		}
		};
		add(l1);
		add(l2);
		add(l3);
		add(l4);
	};
	};
	
	public static final List<List<String>> mySynonyms_streets = new ArrayList<List<String>>(){{
		List<String> l1 = new ArrayList<String>(){{
			add("rua");
			add("r");
		}
		};
		List<String> l2 = new ArrayList<String>(){{
			add("drive");
			add("dr");
			add("drv");
		}
		};
		add(l1);
		add(l2);
	};
	};


	@Test
	public void join() {
		Assert.assertEquals("st paul",SynonymsFinder.join(new String[]{"st","paul"}, " "));
	}
	
	@Test
	public void synonymsFinder_FromFile(){
		SynonymsFinder synonymsFinder = new SynonymsFinder("br_synonyms_test.txt");
		List<List<String>> dict = synonymsFinder.synonymsDict;
		Assert.assertEquals(2, dict.size());
		Assert.assertEquals(3, dict.get(0).size());
		Assert.assertEquals(2, dict.get(1).size());
		
		Assert.assertEquals("doctor", dict.get(1).get(0));
		Assert.assertEquals("dr", dict.get(1).get(1));
		
	}

	@Test
	public void normalize(){
		String result =  SynonymsFinder.normalize("Saint Jean de luz", SynonymsFinder.DEFAULT_SYNONYMS);
		Assert.assertEquals("saint jean de luz", result);
		result =  SynonymsFinder.normalize("St Jean de luz", SynonymsFinder.DEFAULT_SYNONYMS);
		Assert.assertEquals("saint jean de luz", result);
		//with '-' as separator
		result =  SynonymsFinder.normalize("Saint-Jean-de-luz", SynonymsFinder.DEFAULT_SYNONYMS);
		Assert.assertEquals("saint jean de luz", result);
		result =  SynonymsFinder.normalize("St-Jean-de-luz", SynonymsFinder.DEFAULT_SYNONYMS);
		Assert.assertEquals("saint jean de luz", result);


	}

	
	@Test
	public void normalizeSynonyms(){
		SynonymsFinder synonymsFinder = new SynonymsFinder(mySynonyms_streets);
		Assert.assertEquals("drive",synonymsFinder.normalizeSynonyms("drive"));
		Assert.assertEquals("drive",synonymsFinder.normalizeSynonyms("dr"));


	}
	
	
	@Test
	public void SynonymFinderTest(){
		List<SynonymsDto>  dtos  =	SynonymsFinder.findSynomnymsInSentence("Capitão Francisco Holanda Moura", "CAP FRANCISCO HOLANDA MOURA","br");
		Assert.assertTrue(dtos.contains(new SynonymsDto("capitao", "cap", "br")));

		//two synonyms
		dtos  =	SynonymsFinder.findSynomnymsInSentence("r Capitão Francisco Holanda Moura", "rua CAP FRANCISCO HOLANDA MOURA","br");
		Assert.assertTrue(dtos.contains(new SynonymsDto("capitao", "cap", "br")));
		Assert.assertTrue(dtos.contains(new SynonymsDto("rua", "r", "br")));

		dtos  =	SynonymsFinder.findSynomnymsInSentence("Dourtor Olavo Vilela", "Dr Olavo Vilela","br");
		Assert.assertTrue(dtos.contains(new SynonymsDto("dourtor", "dr", "br")));
		Assert.assertTrue(dtos.contains(new SynonymsDto("dr", "dourtor", "br")));

		dtos  =	SynonymsFinder.findSynomnymsInSentence("AVENIDA ARQT ALVARO MANCINI", "Avenida Arquiteto Álvaro Mancini","br");
		Assert.assertTrue(dtos.contains(new SynonymsDto("ARQT", "Arquiteto", "br")));

		dtos  =	SynonymsFinder.findSynomnymsInSentence("AVE ARQT ALVARO MANCINI", "Avenida Arquiteto Álvaro Mancini","br");
		Assert.assertTrue(dtos.contains(new SynonymsDto("ARQT", "Arquiteto", "br")));
		Assert.assertTrue(dtos.contains(new SynonymsDto("ave", "avenida", "br")));

		dtos  =	SynonymsFinder.findSynomnymsInSentence("foo bar", "nada niet","br");
		Assert.assertEquals(0, dtos.size());



	}

	@Test
	public void hasSynonyms(){

		SynonymsFinder synonymsFinder = new SynonymsFinder(mySynonyms_deep);
		Assert.assertTrue(synonymsFinder.hasSynonyms("drive"));
		Assert.assertTrue(synonymsFinder.hasSynonyms("st"));
		Assert.assertTrue(synonymsFinder.hasSynonyms("dr"));
		Assert.assertTrue(synonymsFinder.hasSynonyms("doctor"));
		Assert.assertFalse(synonymsFinder.hasSynonyms("foo"));
		


	}
	

	@Test
	public void getSynonymsFor(){

		SynonymsFinder synonymsFinder = new SynonymsFinder(mySynonyms);
		Set<String> actual = synonymsFinder.getSynonymsFor("drive");
		Assert.assertEquals(3, actual.size());
		Assert.assertTrue(actual.contains("dr"));
		Assert.assertTrue(actual.contains("drv"));

		actual = synonymsFinder.getSynonymsFor("dr");
		Assert.assertEquals(4, actual.size());
		Assert.assertTrue(actual.contains("dr"));
		Assert.assertTrue(actual.contains("drv"));
		Assert.assertTrue(actual.contains("doctor"));
		Assert.assertTrue(actual.contains("drive"));

		actual = synonymsFinder.getSynonymsFor("drz");
		Assert.assertEquals(0, actual.size());


	}

	@Test
	public void isASynonymFor(){

		SynonymsFinder synonymsFinder = new SynonymsFinder(mySynonyms);
		Assert.assertTrue(synonymsFinder.isASynonymFor("dr", "drive"));
		Assert.assertTrue(synonymsFinder.isASynonymFor("drive", "dr"));
		Assert.assertTrue(synonymsFinder.isASynonymFor("doctor", "dr"));
		Assert.assertFalse(synonymsFinder.isASynonymFor("drive", "doctor"));

	}

	@Test
	public void isWordHAsASynonymIn(){
		SynonymsFinder synonymsFinder = new SynonymsFinder(mySynonyms);
		List<String> wordsOfSentence = new ArrayList<String>();
		wordsOfSentence.add("foo");
		wordsOfSentence.add("dr");
		Assert.assertTrue(synonymsFinder.isWordHasASynonymIn("drive", wordsOfSentence));
		Assert.assertTrue(synonymsFinder.isWordHasASynonymIn("doctor", wordsOfSentence));
		Assert.assertFalse(synonymsFinder.isWordHasASynonymIn("bar", wordsOfSentence));
	}









}
