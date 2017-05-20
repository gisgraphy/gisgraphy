package com.gisgraphy.helper.synonyms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.helper.StringHelper;


public class SynonymsFinder {
	
	public static final Logger logger = LoggerFactory.getLogger(SynonymsFinder.class);

	public List<List<String>> synonymsDict = new ArrayList<List<String>>();
	String sourceInfo = "?"; 

	public SynonymsFinder(List<List<String>> synonymsDict) {
		super();
		this.synonymsDict = synonymsDict;
	}



	public SynonymsFinder() {
		super();
		this.synonymsDict = DEFAULT_SYNONYMS;
	}
	
	public SynonymsFinder(String filePath) {
		if (filePath==null){
			throw new RuntimeException("SynonymsFinder : Can not load a null filepath");
		}
		sourceInfo = filePath;
			BufferedReader br = null;
			InputStream bis = null;
			try {

			    bis = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
			    if (bis == null) {
			    	throw new RuntimeException("file " + filePath + " does not exists or is not present in classpath");
			    }
			    br = new BufferedReader(new InputStreamReader(bis, Charset.defaultCharset()));
			    String line;
			    while ((line = br.readLine()) != null) {
					String[] fields = line.split("\\|");
					if (fields.length < 2) {
					   logger.error("line"+line+" should contains 2 words at least");
					   continue;
					}
					List<String> listLine =new ArrayList<String>();
					for (String field:fields){
						if (!"".equals(field.trim())){
							listLine.add(StringHelper.normalize(field));
						}
					}
					if (listLine.size()>0){
						synonymsDict.add(listLine);
					}

			    }
			} catch (Exception e) {
			    throw new RuntimeException(e.getMessage(), e);
			} finally {
			    if (bis != null) {
				try {
				    bis.close();
				} catch (IOException e) {

				}
			    }
			    if (br != null) {
				try {
				    br.close();
				} catch (IOException e) {

				}
			    }
			}
		    }




	public static final List<List<String>> DEFAULT_SYNONYMS = new ArrayList<List<String>>(){{
		List<String> l1 = new ArrayList<String>(){{
			add("saint");//the first one is the one used for normalizing
			add("st.");
			add("st");
			add("santa");
			add("sta");
		}};

		List<String> l2 = new ArrayList<String>(){{
			add("hts");//the first one is the one used for normalizing
			add("heights");
			add("height");
			add("hgts");
		}};
		add(l1);
		add(l2);
	}
	};






	public Set<String> getSynonymsFor(String word){
		Set<String> newWords = new HashSet<String>();
		if (word==null){
			return newWords;
		}
		for (List<String> synonyms : synonymsDict) {
			if (synonyms!=null){
				for (String synonym : synonyms) {
					if (synonym!=null){
						if (synonym.equalsIgnoreCase(word)){
							newWords.addAll(synonyms);
						}
					}
				}
			}
		}
		return newWords;

	}
	
	public String normalizeSynonyms(String word){
		if (word!=null){
			for (List<String> synonyms : synonymsDict) {
				if (synonyms!=null){
					if (synonyms!=null && isWordHasASynonymIn(word,synonyms)){
						return synonyms.get(0);
					}
				}
			}
		}
		return word;
	}

	
	public boolean hasSynonyms(String word){
		return getSynonymsFor(word).size()>0;
	}
	
	public boolean isASynonymFor(String word,String syn){
		if (syn==null || word==null){
			return false;
		}
		Set<String> synonyms = getSynonymsFor(word);
		for (String synonym:synonyms){
			if (syn.equals(synonym)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isWordHasASynonymIn(String word, List<String> words){
		if (word==null || words==null || words.size()==0){
			return false;
		}
		Set<String> synonyms = getSynonymsFor(word);
		for (String synonym:synonyms){
			for (String syn:words){
			if (syn.equalsIgnoreCase(synonym)){
				return true;
			}
			}
		}
		return false;
		
	}


	/**
	 * @param sentence
	 * @return same as {@link #normalize(String, List)} but with {@link #DEFAULT_SYNONYMS}
	 */
	public  String normalize(String sentence) {
		return normalize(sentence,synonymsDict);
	}

	/**
	 * @param sentence the sentence
	 * @param synonymslist
	 * @return a string with the synonyms replaced by the first one in the list.
	 *  if you call this method for 2 different string with synonyms it should be the same
	 *  It also manage case and special separator
	 */
	public static String normalize(String sentence,
			List<List<String>> synonymslist) {
		sentence = StringHelper.normalize(sentence);
		String result = sentence;
		try {
			String[] wordsArray = sentence.split(" ");
			if (wordsArray.length >= 2) {
				for (int i = 0; i < wordsArray.length; i++) {//for each word in the sentence
					for (List<String> synonyms : synonymslist) {//for all synomysList
						if (synonyms!=null){
							boolean first=true;
							String synomymForReplacement = ""; 
							for (String synonym : synonyms) {//for all synonyms
								if (first == true){
									//the first synonym should be the replacement one
									first=false  ;
									synomymForReplacement =synonym;
									continue;
								}
								if (wordsArray[i] != null
										&& wordsArray[i].equalsIgnoreCase(synonym)) {
									wordsArray[i] = synomymForReplacement;
								}
							}
						}
					}
				}
			}
			return join(wordsArray, " ");
		} catch (Exception e) {
			//ignore
		}
		return result;
	}

	@Override
	public String toString() {
		return SynonymsFinder.class.getSimpleName()+" for "+sourceInfo;
	}

	
	public static List<SynonymsDto> findSynomnymsInSentence(String expected, String actual,String countrycode){
		List<SynonymsDto> synonymsDtos = new ArrayList<SynonymsDto>();
		if (actual!=null && expected!=null){
			if (actual.equalsIgnoreCase(expected)){ //shortcut
				return synonymsDtos;
			}
			//split the strings
			String[] actualSplited = actual.split("[,\\s\\-\\–\\一;]");
			String[] expectedSplited = expected.split("[,\\s\\-\\–\\一]");
			List<String> actualList = new ArrayList<String>();
			List<String> expectedList = new ArrayList<String>();
			
			
			for (int i=0;i<actualSplited.length;i++){
				actualList.add(StringHelper.normalize(actualSplited[i]));
			}
			for (int i=0;i<expectedSplited.length;i++){
				expectedList.add(StringHelper.normalize(expectedSplited[i]));
			}
			//find missing in actual
			List<String> actualMissing = new ArrayList<String>();
			List<String> expectedMissing = new ArrayList<String>();
			boolean atLeastOneWordEquals =false;
			for (String word : actualList){
				if (!expectedList.contains(word)){
					actualMissing.add(word);
				} else {
					atLeastOneWordEquals = true;
				}
			}
			
			for (String word : expectedList){
				if (!actualList.contains(word)){
					expectedMissing.add(word);
				} else {
					atLeastOneWordEquals = true;
				}
			}
			
			if (expectedMissing.size()!= 0 && expectedMissing.size()==actualMissing.size() && atLeastOneWordEquals){
				for (int i=0;i<expectedMissing.size();i++){
					if (expectedMissing.get(i).substring(0, 1).equals(actualMissing.get(i).substring(0, 1))){ //at least first lettre should be the same
				SynonymsDto dto = new SynonymsDto(expectedMissing.get(i), actualMissing.get(i),countrycode);
				synonymsDtos.add(dto);
					}
				}
			}
		}
		return synonymsDtos;
	}

	static String join(String[] array, String delimiter) {
		StringBuilder builder = new StringBuilder();
		for (int i=0;i<array.length;i++){
			builder.append(array[i]);
			if (i+1!=array.length){
				builder.append(delimiter);
			}
		}
		return builder.toString();
	}
}
