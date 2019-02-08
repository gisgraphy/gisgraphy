/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/
package com.gisgraphy.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.addressparser.StreetTypeOrder;
import com.gisgraphy.compound.Decompounder;
import com.gisgraphy.compound.Decompounder.state;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.helper.synonyms.StreetTypeSynonymsManager;
import com.gisgraphy.helper.synonyms.SynonymsFinder;
import com.gisgraphy.importer.TigerSimpleImporter;

/**
 * Provide some usefull method to compute string for autocompletion and fulltextsearch
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class StringHelper {

	public static final int MAX_STRING_INDEXABLE_LENGTH = 40;

	public static final char WHITESPACE_CHAR_DELIMITER = '-';

	protected static final Logger logger = LoggerFactory.getLogger(StringHelper.class);
	
	protected static final int MISSING_WORD_TOLERANCE = 1;
	
	protected static Pattern CITY_PATTERN = Pattern.compile("city of|city$", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern ORDINAL_NUMBER_EN_PATTERN = Pattern.compile("(\\d+)\\s?(?:rd|st|nd|th)?\\b");
	
	private static StreetTypeSynonymsManager synonymsManager = StreetTypeSynonymsManager.getInstance();
	
	protected static final Pattern SYNONYMS_PATTERN= Pattern.compile("(saint|santa)", Pattern.CASE_INSENSITIVE);
	private final static Pattern RN_PATTERN = Pattern.compile("\\b(rn)\\s?(\\d{1,4}\\b)", Pattern.CASE_INSENSITIVE);
	private final static Pattern ZIPCONCATENATE_2_3_PATTERN = Pattern.compile("(.*\\s)?\\b(\\d{2})[\\s](\\d{3}\\b)");
	private final static Pattern ZIPCONCATENATE_3_2__PATTERN = Pattern.compile("(.*)\\s\\b(\\d{3})[\\s-](\\d{2}\\b)");
	private static final Pattern GERMAN_SYNONYM_PATTERN = Pattern.compile("(str\\b)[\\.]?",Pattern.CASE_INSENSITIVE);
	private static final Pattern ORDINAL_TEXT_EN_PATTERN = Pattern.compile("((?:first|second|third)|(?:four|fif|six|seven|eigh|nin[e]?|ten|eleven|twelve|twelf|thir|twentie)(?:teen)?th)",Pattern.CASE_INSENSITIVE);
	

	
	private static final Pattern START_DIRECTION_PATTERN = Pattern.compile("^((?:N|W|E|S|NORTH[-\\s]EAST|NORTH[-\\s]WEST|NORTH|WEST|EAST|SOUTH[-\\s]EAST|SOUTH[-\\s]WEST|SOUTH|NE|NW|SE|SW)\\b)",Pattern.CASE_INSENSITIVE);
	private static final Pattern END_DIRECTION_PATTERN = Pattern.compile("(\\b(?:N|W|E|S|NORTH[-\\s]EAST|NORTH[-\\s]WEST|NORTH|WEST|EAST|SOUTH[-\\s]EAST|SOUTH[-\\s]WEST|SOUTH|NE|NW|SE|SW)$)",Pattern.CASE_INSENSITIVE);
	private static final Pattern STREET_PREFIX_PATTERN = Pattern.compile("^(?:(N|W|E|S|NORTH[-\\s]EAST|NORTH[-\\s]WEST|NORTH|WEST|EAST|SOUTH[-\\s]EAST|SOUTH[-\\s]WEST|SOUTH|NE|NW|SE|SW)\\b\\s)?((?:\\s?(\\b[A-Z][A-Z]\\b)\\s?)?"
	        + "(?:\\bpth\\b|\\bsr\\b|\\bss\\b|\\bbus\\b|\\bbusiness loop\\b|\\bBusiness Interstate\\b|\\bbusiness spur\\b|\\bbusiness|\\bstate\\b|\\bcounty\\b|\\branch\\b|\\bco\\b|\\bu[.]?s[.]?|I-|\\binterstate\\b|\\bstate\\b|\\bFOREST\\b|\\brural\\b|\\brr\\b|\\bfm\\b|\\bpth\\b))",Pattern.CASE_INSENSITIVE);
	public static final int MAX_STREET_NAME_SIZE = 250;

	private static final Map<String,String> US_PREFIX_STREETNAME_NORMALIZE_MAP = new HashMap<String, String>(){
	    {
	        //ALWAYS in lower case, if a key is added,it should be added to STREET_PREFIX_PATTERN
	        put("co","county");
	        put("bus","business");//bus|business|bus loop|business loop|Business Interstate|business spur|bus spur|state|co|county|u[.]?s[.]?|I-|interstate]|state|FOREST
	        put("bus loop","business");
	        put("bus spur","business spur");
	        put("u.s","us");
	        put("u.s.","us");
	        put("i-","interstate");
	        put("i","interstate");
	        put("sr","state route");
           // put("hwy","highway");
            put("pth","perimeter highway");
            put("ss","strada statale");
            put("rr","rural route");
            put("fm","farm to market");
            
	    }
	};
	
	public static final List<String> US_PREFIX_STREETNAME_NORMALIZE_LIST = new ArrayList<String>(){
	    {
	    //We only add long version, the short one will be done by the STREET_PREFIX_PATTERN
	    addAll(US_PREFIX_STREETNAME_NORMALIZE_MAP.values());
	    }
	};
	
	
	private static final Map<String,String> ORDINAL_EN_TEXT_NORMALIZE_MAP = new HashMap<String, String>(){
        {
            //ALWAYS in lower case
            put("first","1st");
            put("second","2nd");
            put("third","3rd");
            put("fourth","4th");
            put("fifth","5th");
            put("sixth","6th");
            put("seventh","7th");
            put("eighth","8th");
            put("nineth","9th");
            put("ninth","9th");
            put("tenth","10th");
            put("eleventh","11th");
            put("twelveth", "12th");
            put("twelfth", "12th");
            put("thirteenth", "13th");
            put("fourteenth", "14th");
            put("fifteenth", "15th");
            put("sixteenth", "16th");
            put("seventeenth", "17th");
            put("eighteenth", "18th");
            put("nineteenth", "19th");
            put("twentieth", "20th");
            
        }
    };
	
	private static final Pattern DIRECTION_LETTER_PATTERN = Pattern.compile("((?:\\bnortheast\\b)|(?:\\bsoutheast\\b)|(?:\\bsouthwest\\b)|(?:\\bnorthwest\\b)|(?:\\b\\s[sewn]$)|(?:^[sewn]\\b\\s)|(?:\\b\\s(?:ne|nw|se|sw)$)|(?:^(?:ne|nw|se|sw)\\b\\s))",Pattern.CASE_INSENSITIVE);
	
	private static Decompounder decompounder = new Decompounder();
	
	private static LevenshteinAlgorithm levenstein = new LevenshteinAlgorithm();

	/**
	 * Process a string to apply filter as lucene and solr does :
	 * 	- remove accent
	 * 	- lowercase
	 * 	- word delimiter ('-', '.'
	 * @param originalString the string to process
	 * @return the transformed String or null if the original String is null
	 */
	public static final String normalize(String originalString) {
		String norm = originalString == null ? null : EncodingHelper.removeAccents(originalString.trim()).toLowerCase();
				return removePunctuation(norm);

	}

	public static String removePunctuation(String norm) {
		if (norm != null) {
			return norm.replace("-", " ").replace(".", " ").replace("\"", " ")
					.replace("'", " ").replace(';', ' ').replaceAll("\\s+", " ");
		} else {
			return null;
		}
	}

	/**
	 * Process a string to in order to be stored in a specific postgres 
	 * field to allow the index usage for ilike (ilike(%String%):
	 * e.g : 'it s ok'=> s ok, s o, it s, t s o, t s, it s ok, ok, it s o, it, t s ok
	 * it remove duplicates and don't put single character.
	 * 
	 * @param originalString the string to process
	 * @param delimiter words will be delimited by this char
	 *  (it should be the same as the one in {@link StringHelper#transformStringForPartialWordSearch(String, char)}. 
	 *  For gisgraphy the char is {@link StringHelper#WHITESPACE_CHAR_DELIMITER}
	 *  IMPORTANT NOTE : if the string is greater than {@link #MAX_STRING_INDEXABLE_LENGTH}, the method will return null;
	 * @return the transformed String (or null if the original String is null) to be used by the postgres function to_ts_vector
	 * @see #transformStringForPartialWordSearch(String, char)
	 */
	public static final String transformStringForPartialWordIndexation(String originalString, char delimiter) {
		if (originalString == null) {
			return null;
		}
		if (originalString.length() > MAX_STRING_INDEXABLE_LENGTH) {
			return null;
		}
		//use hashset to remove duplicate
		String substring = null;
		StringBuffer sb = new StringBuffer();
		Set<String> set = new HashSet<String>();
		originalString = normalize(originalString);
		for (int i = 0; i < originalString.length(); i++) {
			for (int j = i + 1; j <= originalString.length(); j++) {
				substring = originalString.substring(i, j);
				if (!substring.endsWith(" ")) {//we have alredy add the entry the last loop
					if (substring.startsWith(" ")) {//need to trim?
						substring = substring.substring(1);
					}
					if (substring.length() > 1) {//only index string that have length >=2
						set.add(substring.replace(" ", String.valueOf(delimiter)));
					}
				}
			}
		}

		for (String part : set) {
			sb.append(part).append(" ");
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param originalString the string to transform
	 * @param delimiter the delimiter 
	 * 		(it should be the same as the one use in {@link #transformStringForPartialWordIndexation(String, char)})
	 *  For gisgraphy the char is {@link StringHelper#WHITESPACE_CHAR_DELIMITER}
	 * @return the transformed string (or null if the original String is null) to be use by the postgres function plainto_tsquery)
	 * @see #transformStringForPartialWordIndexation(String, char)
	 */
	public static final String transformStringForPartialWordSearch(String originalString, char delimiter) {
		if (originalString == null) {
			return null;
		}
		return normalize(originalString.trim()).replace(" ", String.valueOf(delimiter));
	}

	/**
	 * @param openStreetMap the openStreetMap Entity to update
	 * @return the same openstreetmap entity with the {@link OpenStreetMap#FULLTEXTSEARCH_COLUMN_NAME}
	 */
	public static OpenStreetMap updateOpenStreetMapEntityForIndexation(OpenStreetMap openStreetMap) {
		if (openStreetMap != null  && openStreetMap.getName() != null) {
			openStreetMap.setTextSearchName(StringHelper.normalize(openStreetMap.getName()));
		}
		return openStreetMap;
	}
	
	/**
	 * @param s a camel Case string
	 * @return a human readable string where upper char is replaced by a space and the lowercase char 
	 */
	public static String splitCamelCase(String s) {
	    return s.replaceAll(
	       String.format("%s|%s|%s",
	          "(?<=[A-Z])(?=[A-Z][a-z])",
	          "(?<=[^A-Z])(?=[A-Z])",
	          "(?<=[A-Za-z])(?=[^A-Za-z])"
	       ),
	       " "
	    );
	 }

	
	/**
	 * Usefull method to be compatible with jdk1.5 (jdk 1.6 already have this method)
	 * @param string the string to test 
	 * @return true if the string is not null or empty (trimmed)
	 */
	public static boolean isNotEmptyString(String string){
	    return !isEmptyString(string);
	}
	
	/**
	 * Usefull method to be compatible with jdk1.5 (jdk 1.6 already have this method)
	 * @param string the string to test
	 * @return true if the sting is null or empty (trimmed)
	 */
	public static boolean isEmptyString(String string){
	    if (string==null || 
		    "".equals(string.trim()) ){
		return true;
	    }
	    return false;
	}
	
	/**
	 * @param aThrowable
	 * @return the stacktrace as string
	 */
	public static String getStackTraceAsString(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }
	
	
	public static boolean isSameName(String expected, String actual){
		
		if (actual!=null && expected!=null){
			if (decompounder.isDecompoundName(actual)){
				return isSameName(expected, actual, MISSING_WORD_TOLERANCE) || isSameName(expected, decompounder.getOtherFormat(actual), MISSING_WORD_TOLERANCE);
			}
			else {
				return isSameName(expected,actual,MISSING_WORD_TOLERANCE);
			}
		}
		return false;
	}

	
	/**
	 * @param expected
	 * @param actual
	 * @param tolerance the number of word that can be missing if there is more than two words specified
	 * @return
	 */
	public static boolean isSameName(String expected, String actual,int tolerance){
		if (actual!=null && expected!=null){
			//remove some words
			
			Matcher matcherCity = CITY_PATTERN.matcher(actual);
			StringBuffer sb = new StringBuffer();
			if (matcherCity.find()){
				matcherCity.appendReplacement(sb, "");
				matcherCity.appendTail(sb);
				actual = sb.toString().trim();
			}
			
			matcherCity = CITY_PATTERN.matcher(expected);
			sb = new StringBuffer();
			if (matcherCity.find()){
				matcherCity.appendReplacement(sb, "");
				matcherCity.appendTail(sb);
				expected = sb.toString();
			}
			if (actual.equalsIgnoreCase(expected)){ //shortcut
				return true;
			}
			
			//split the strings
			String[] actualSplited = StringHelper.removePunctuation(actual).split("[,\\s\\-\\–\\一;//]");
			String[] expectedSplited = StringHelper.removePunctuation(expected).split("[,\\s\\-\\–\\一//]");
			
			if (Math.abs(actualSplited.length -expectedSplited.length) >=2){
				return false;
			}

			//first we check if actual has more long words than expected
			//saint jean is not saint jean de luz, but 'la petite maison' is ok for 'petite maison'
			List<String> actualSplitedLong = new ArrayList<String>();
			for (String word:actualSplited){
				if (word.length()>3){
					if (word!=null){
						actualSplitedLong.add(normalize(word));
					}
				}  else if (word.equals("st")){
					Matcher m =SYNONYMS_PATTERN.matcher(expected);
					if (m.find() && m.groupCount()>=1){
						actualSplitedLong.add(m.group(1).toLowerCase());
					}
				} else if (StringUtils.isNumeric(word)){
					actualSplitedLong.add(normalize(word));
				}
			}
			List<String> expectedSplitedLong = new ArrayList<String>();
			for (String word:expectedSplited){
				if (word.length()>3){
					if (word!=null){
						expectedSplitedLong.add(normalize(word));
					}
				} else if (word.equals("st")){
					Matcher m =SYNONYMS_PATTERN.matcher(actual);
					if (m.find()&&m.groupCount()>=1){
						expectedSplitedLong.add(m.group(1).toLowerCase());
					}
				}
			}
			if (actualSplitedLong.size() > expectedSplitedLong.size() ){
				return false;
			}
			if (actualSplitedLong.size() < expectedSplitedLong.size() ){
				return false;
			}
			//same number of word but are they the same ?
			int countMissing = 0;
			for (String word :actualSplitedLong){
				if(!expectedSplitedLong.contains(word)){
					countMissing++;
				}
				if (expectedSplitedLong.size() == actualSplitedLong.size() &&  (expectedSplitedLong.size()==1 || expectedSplitedLong.size()==2)  && countMissing >0){
					//if one or two words, every words should be present
					return false;
				} else if (countMissing > tolerance){
					return false;
				}
			}

			return true;
		}
		return false;
	}
	
	
	
	/**
     * @param expected
     * @param actual
     * @param tolerance the number of word that can be missing if there is more than two words specified
     * @return
     */
    public static int countSameOrApprox(String expected, String actual){
        if (actual!=null && expected!=null){
            //remove some words
            
            Matcher matcherCity = CITY_PATTERN.matcher(actual);
            StringBuffer sb = new StringBuffer();
            if (matcherCity.find()){
                matcherCity.appendReplacement(sb, "");
                matcherCity.appendTail(sb);
                actual = sb.toString().trim();
            }
            
            matcherCity = CITY_PATTERN.matcher(expected);
            sb = new StringBuffer();
            if (matcherCity.find()){
                matcherCity.appendReplacement(sb, "");
                matcherCity.appendTail(sb);
                expected = sb.toString();
            }
            
            //split the strings
            String[] actualSplited = StringHelper.removePunctuation(actual).split("[,\\s\\-\\–\\一;//]");
            String[] expectedSplited = StringHelper.removePunctuation(expected).split("[,\\s\\-\\–\\一//]");
            

            //first we check if actual has more long words than expected
            //saint jean is not saint jean de luz, but 'la petite maison' is ok for 'petite maison'
            List<String> actualSplitedLong = new ArrayList<String>();
            for (String word:actualSplited){
                if (word.length()>3){
                    if (word!=null){
                        actualSplitedLong.add(normalize(word));
                    }
                }  else if (word.equals("st")){
                    Matcher m =SYNONYMS_PATTERN.matcher(expected);
                    if (m.find() && m.groupCount()>=1){
                        actualSplitedLong.add(m.group(1).toLowerCase());
                    }
                } else if (StringUtils.isNumeric(word)){
                    actualSplitedLong.add(normalize(word));
                }
            }
            List<String> expectedSplitedLong = new ArrayList<String>();
            for (String word:expectedSplited){
                if (word.length()>3){
                    if (word!=null){
                        expectedSplitedLong.add(normalize(word));
                    }
                } else if (word.equals("st")){
                    Matcher m =SYNONYMS_PATTERN.matcher(actual);
                    if (m.find()&&m.groupCount()>=1){
                        expectedSplitedLong.add(m.group(1).toLowerCase());
                    }
                }
            }
            //same number of word but are they the same ?
            int count = 0;
            for (String word :actualSplitedLong){
                if(expectedSplitedLong.contains(word)){
                    count++;
                } else {
                    for (String wordexpected : expectedSplitedLong){
                        if (minDistance(wordexpected, word)<=2){
                            count++;
                            break;
                        }
                    }
                }
                
            }

            return count;
        }
        return 0;
    }
    
    public static int minDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
     
        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];
     
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
     
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
     
        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);
     
                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;
     
                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }
     
        return dp[len1][len2];
    }
	
	private static List<String> FR_COUNTRIES = new ArrayList<String>(){{
		add("CA");add("FR");add("BE");add("CH");add("RE");add("GP");add("MF");add("MP");add("DZ");add("MA");add("SD");add("CD");add("CM");add("SN");add("PM");}};
	private static List<String> EN_COUNTRIES = new ArrayList<String>(){{add("US");add("CA");add("CN");add("ID");add("IN");add("AU");add("SG");add("HK");add("IR");add("FI");add("SA");add("VI");add("FK");add("GI");add("GL");add("FO");add("AS");add("IM");add("UM");add("GB");add("UK");add("PR");add("JE");add("SH");add("GS");add("GG");}};
    private static List<String> SP_COUNTRIES = new ArrayList<String>(){{add("AR");add("ES");add("MX");add("CO");add("PA");}};
	private static List<String> IT_COUNTRIES = new ArrayList<String>(){{add("IT");add("SM");add("VA");}};
	private static List<String> PT_COUNTRIES = new ArrayList<String>(){{add("PT");add("BR");}};
	
	public static Language getLanguageFromCountryCode(String countryCode){
		if (countryCode==null){
			return null;
		}
		countryCode = countryCode.toUpperCase();
		if (countryCode.equals("CA")){
			return Language.EN_FR;
		}
		if (EN_COUNTRIES.contains(countryCode.toUpperCase())){
			return Language.EN;
		} else if (FR_COUNTRIES.contains(countryCode.toUpperCase())){
			return Language.FR;
		}
		 else if (SP_COUNTRIES.contains(countryCode.toUpperCase())){
				return Language.ES;
			}
		 else if (IT_COUNTRIES.contains(countryCode.toUpperCase())){
				return Language.IT;
			}
		 else if (PT_COUNTRIES.contains(countryCode.toUpperCase())){
				return Language.PT;
			}
		 else {
			 return null;
		 }
		
	}
	
	
	public static boolean isSameStreetName(String expected, String actual, String countrycode){
		if (actual!=null && expected!=null){
		    if (expected.equalsIgnoreCase(actual)){
		       return true;
		    }
			actual=expandStreetType(actual, countrycode);
			expected=expandStreetType(expected, countrycode);
			actual=expandStreetSynonyms(actual);
			expected=expandStreetSynonyms(expected);
			if (countrycode!=null && (countrycode.equalsIgnoreCase("CA") || countrycode.equalsIgnoreCase("US"))){
				actual=expandStreetDirections(actual);
				expected=expandStreetDirections(expected);
				expected = removeRoutePrefix(expected);
				actual = removeRoutePrefix(actual);
				
				actual = removeDirection(actual);
				expected = removeDirection(expected);
			}
            actual=expandOrdinalText(actual);
            expected=expandOrdinalText(expected);
            
			boolean same = (isSameStreetName_intern(expected,actual) || 
					(actual.replaceAll("[^0-9]", "").equals(expected.replaceAll("[^0-9]", "")) && levenstein.execute(normalize(actual).replaceAll("[\\s-]", ""), normalize(expected).replaceAll("[\\s-]", ""))<=2)//<=2 or simply <2 ?
					);
			if (same){
				return true;
			} else if (countrycode!=null){
				actual = removeStreetType(actual, countrycode);
				expected = removeStreetType(expected, countrycode);
				return (isSameStreetName_intern(expected,actual) || levenstein.execute(normalize(actual).replaceAll("\\s-", ""), normalize(expected).replaceAll("\\s-", ""))<2);
						
			}
		}
		return false;
	}

    protected static String removeRoutePrefix(String expected) {
        Matcher matcherPrefix = STREET_PREFIX_PATTERN.matcher(expected);
        if (matcherPrefix.find()){//specific for state rte
            String prefix = matcherPrefix.group(2);
            String dir = matcherPrefix.group(1);
            int length = prefix.length();
            if (dir!=null &&  !dir.equals("")){
                length= length+dir.length()+1;
            }
            expected = expected.substring(length, expected.length()).trim();
        }
        return expected;
    }
	
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
	
	private static SynonymsFinder synonymsFinder = new SynonymsFinder(mySynonyms);
	
	private static boolean isSameStreetName_intern(String expected, String actual){
		int tolerance = 0;
		if (actual!=null && expected!=null){
			if (actual.equalsIgnoreCase(expected)){ //shortcut
				return true;
			}
			if (!isSameNumberRoad(expected, actual)){//all the number should be the same and in the same order
			    return false;
			}
			//split the strings
			String[] actualSplited = StringHelper.removePunctuation(actual).split("[,\\s\\-\\–\\一;//]");
			String[] expectedSplited =  StringHelper.removePunctuation(expected).split("[,\\s\\-\\–\\一//]");

			
			
			//first we check if actual has more long words than expected
			//saint jean is not saint jean de luz, but 'la petite maison' is ok for 'petite maison'
			List<String> actualSplitedLong = new ArrayList<String>();
			for (String word:actualSplited){
				if (word.length()>3){
					if (word!=null){
						actualSplitedLong.add(normalize(word));
					}
				}  else if (synonymsFinder.hasSynonyms(word)){
						actualSplitedLong.add(normalize(word));
				} else if (StringUtils.isNumeric(word)){
					actualSplitedLong.add(normalize(word));
				}
			}
			List<String> expectedSplitedLong = new ArrayList<String>();
			for (String word:expectedSplited){
				if (word.length()>3){
					if (word!=null){
						expectedSplitedLong.add(normalize(word));
					}
				} else if (synonymsFinder.hasSynonyms(word)){
					expectedSplitedLong.add(normalize(word));
			}else if (StringUtils.isNumeric(word)){
				expectedSplitedLong.add(normalize(word));
			}
			}
			if ((actualSplitedLong.size()>=4 && expectedSplitedLong.size()>=3) || expectedSplitedLong.size()>=4 && actualSplitedLong.size()>=3){
				tolerance=1;
			}
			if (Math.abs(actualSplitedLong.size() - expectedSplitedLong.size()) >tolerance){
				return false;
			}
			/*if (actualSplitedLong.size() - expectedSplitedLong.size()<tolerance ){
				return false;
			}*/
			//same number of word but are they the same ?
			int countMissing = 0;
			List<String> missingWordsInActual = new ArrayList<String>();
			for (String word :actualSplitedLong){
				Matcher matcher1 = ORDINAL_NUMBER_EN_PATTERN.matcher(word);
				if (matcher1.find()){
					boolean foundOrdinal = false;
					for (String expectedLong:expectedSplitedLong){
						Matcher matcher2 = ORDINAL_NUMBER_EN_PATTERN.matcher(expectedLong);
						if (!foundOrdinal && matcher2.find()){
							if(matcher1.group(1).equals(matcher2.group(1))){
							foundOrdinal=true;
						}
						}
					}
					if (!foundOrdinal){
						countMissing++;
					}
				} else {
				if(!expectedSplitedLong.contains(word)){
					countMissing++;
					missingWordsInActual.add(word);
					//System.out.println("missing "+word+" in "+expected);
				}
				for (String missingWord:missingWordsInActual){
					//check if there is a synonyms in expected
					if (synonymsFinder.isWordHasASynonymIn(missingWord, expectedSplitedLong)){
						countMissing--;
					}
					//System.out.println(missingWord);
				}
				
				}
				
				if (expectedSplitedLong.size() == actualSplitedLong.size() &&  (expectedSplitedLong.size()==1 || expectedSplitedLong.size()==2)  && countMissing >0){
					//if one or two words, every words should be present
					return false;
				} else if (countMissing > tolerance){
					return false;
				}
				}
			

			return true;
		}
		return false;
	}
	



	public static boolean isSameAlternateNames(String name, List<String> name_alternates) {
		if (name_alternates!=null && name !=null){
			for (String nameAlternate:name_alternates){
				if (nameAlternate!=null){
					if (isSameName(name, nameAlternate)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static String expandStreetDirections(String street) {
		if (street==null){
			return null;
		}
		street= street.trim();
		Matcher m = DIRECTION_LETTER_PATTERN.matcher(street);
		StringBuffer sb = new StringBuffer();
		if (m.find()){
			String group = m.group(1).trim();
			String replacement =group;
			if (group.equalsIgnoreCase("N")){
				replacement = " north ";
			}
			else if (group.equalsIgnoreCase("E")){
				replacement = " east ";
			}
			else if (group.equalsIgnoreCase("S")){
				replacement = " south ";
			}
			else if (group.equalsIgnoreCase("W")){
				replacement = " west ";
			}
			else if (group.equalsIgnoreCase("NE")){
                replacement = " north east ";
            }
            else if (group.equalsIgnoreCase("SE")){
                replacement = " south east ";
            }
            else if (group.equalsIgnoreCase("NW")){
                replacement = " north west ";
            }
            else if (group.equalsIgnoreCase("SW")){
                replacement = " south west ";
            }
            else if (group.equalsIgnoreCase("northeast")){
                replacement = "north east";
            }
            else if (group.equalsIgnoreCase("northwest")){
                replacement = "north west";
            }
            else if (group.equalsIgnoreCase("southwest")){
                replacement = "south west";
            }
            else if (group.equalsIgnoreCase("southeast")){
                replacement = "south east";
            }
			m.appendReplacement(sb, replacement);
			m.appendTail(sb);
			return sb.toString().trim();
		}
		return street;
	}
	
	public static String expandStreetSynonyms(String street) {
		if (street==null){
			return null;
		}
		street= street.trim();
		Matcher m = Pattern.compile("\\b(st)\\b\\s", Pattern.CASE_INSENSITIVE).matcher(street);
		StringBuffer sb = new StringBuffer();
		if (m.find()){
			m.appendReplacement(sb, " saint ");
			m.appendTail(sb);
			return sb.toString().trim();
		}
		return street;
	}
	
	 public static String removeDirection(String street){
	     if (street!=null){
	         street = street.trim();
	         Matcher matcher = START_DIRECTION_PATTERN.matcher(street);
	         if (matcher.find()){
	             String prefix = matcher.group(1);
	             street =  street.substring(prefix.length(), street.length()).trim();
	         } 
	             matcher = END_DIRECTION_PATTERN.matcher(street);
	             if (matcher.find()){
	                 String prefix = matcher.group(1);
	                 street =  street.substring(0, street.length()-(prefix.length())).trim();
	             } 
	         
	     }
	     return street;
	 }
	 
	 public static boolean hasDirection(String street){
         if (street!=null){
             street = street.trim();
             Matcher matcher = START_DIRECTION_PATTERN.matcher(street);
             if (matcher.find()){
                 return true;
             } 
                 matcher = END_DIRECTION_PATTERN.matcher(street);
                 if (matcher.find()){
                     return true;
                 } 
             
         }
         return false;
     }
	 
	 public static boolean isRef(String streetName){
	     if (!isEmptyString(streetName)){
	         streetName = streetName.toLowerCase();
	         for (String prefix:US_PREFIX_STREETNAME_NORMALIZE_LIST){
	             if (streetName.contains(prefix) || STREET_PREFIX_PATTERN.matcher(streetName).find()){
	                 return true;
	             }
	         }
	         
	     }
	     return false;
	 }
	 
	 public static boolean isSameNumberRoad(String street1,String street2){
	     Pattern numPattern = Pattern.compile("(\\d+)");
	     Matcher m1 = numPattern.matcher(street1);
	     Matcher m2 = numPattern.matcher(street2);
	     while (m1.find()){
	         //String num1 = m1.group(1);
	         if (m2.find()){
	           //  String num2 = m2.group(1);
	             if (!m2.group(1).equals(m1.group(1))){
	                 return false;
	             }
	         } else {
	             return false;
	         }
	     }
	     if (m2.find()){
	         return false;
	     }
	     return true;
	     
	 }
	 
	 
	 
	 public static String getExpandedStreetPrefix(String prefix){
	     if (!isEmptyString(prefix)){
	         String normalize = US_PREFIX_STREETNAME_NORMALIZE_MAP.get(prefix.toLowerCase());
	         if (normalize!=null){
	             return normalize;
	         }
	     }
	    
	     return prefix;
	     
	     
	 }
	 
	 public static String expandOrdinalText(String street){
	     
	     if (street==null){
	            return null;
	        }
	        street= street.trim();
	        Matcher m = ORDINAL_TEXT_EN_PATTERN.matcher(street);
	        StringBuffer sb = new StringBuffer();
	        if (m.find()){
	            String group = m.group(1).trim();
	            String replacement =ORDINAL_EN_TEXT_NORMALIZE_MAP.get(group.toLowerCase());
	            if (replacement == null){
	                return street;
	            }
	            m.appendReplacement(sb, replacement);
	            m.appendTail(sb);
	            return sb.toString().trim();
	        }
	        return street;
	     
	 }
	
	/**
	 * correct the street type according the countrycode. e.g : 
	 * av=>avenue, r=>rue
	 * @param street
	 * @param countryCode
	 * @return
	 */
	 public static String expandStreetType(String street, String countryCode) {
	     if (street==null){
	         return null;
	     }
	     String dir= "";

	     street= street.trim();
	     boolean hasPoint = false;
	     if (countryCode != null ){
	         String save = street;
	         Language languageFromCountry = getLanguageFromCountryCode(countryCode);
	         if (languageFromCountry!=null){
	             List<Language> languages = new ArrayList<Language>();
	             if (languageFromCountry.toString().contains("_")){
	                 for(String l:languageFromCountry.toString().split("_")){
	                     languages.add(Language.valueOf(l));
	                 }
	             } else {
	                 languages.add(languageFromCountry);
	             }
	             String prefix = null;
	             for (Language languageFromCountryCode: languages){
	                 prefix = null;
	                 String word;
	                 String normalizePrefix = "";
	                 String separator = "";
	                 StreetTypeOrder streetTypeOrder = languageFromCountryCode.getStreetTypeOrder();
	                 boolean isNorthAmericaCountryCode = countryCode!=null && (countryCode.equalsIgnoreCase("US") || countryCode.equalsIgnoreCase("CA"));
	                 if (street.matches(".*\\d$") || (isNorthAmericaCountryCode && STREET_PREFIX_PATTERN.matcher(street).find())){
	                     streetTypeOrder=StreetTypeOrder.typeThenName;
	                 }
	                 if (streetTypeOrder==StreetTypeOrder.typeThenName || streetTypeOrder==StreetTypeOrder.unknow){
	                     SynonymsFinder sf = synonymsManager.getSynonymsFinderFromLanguage(languageFromCountryCode);

	                     if (isNorthAmericaCountryCode){
	                         Matcher matcherPrefix = STREET_PREFIX_PATTERN.matcher(street);
	                         if (matcherPrefix.find()){//specific for state rte
	                             prefix = matcherPrefix.group(2);
	                             dir = matcherPrefix.group(1);
	                             int length = prefix.length();
	                             if (!isEmptyString(dir)){
	                                 length= length+dir.length()+1;
	                             }
	                             street = street.substring(length, street.length()).trim();
	                         }
	                     }
	                     if (street.indexOf(' ')>0){
	                         if (isNorthAmericaCountryCode){
	                             Matcher matcher = START_DIRECTION_PATTERN.matcher(street);
	                             if (matcher.find()){
	                                 dir = matcher.group(1);
	                                 street =  street.substring(dir.length(), street.length()).trim();
	                                 if (street.indexOf(' ')==-1){
	                                     street=save;
	                                     dir = "";
	                                 }
	                             } 
	                         }
	                         
                                word = street.substring(0, street.indexOf(' '));
                            

	                         if (word.indexOf(".")>0){
	                             word = word.substring(0, word.indexOf('.'));
	                             hasPoint=true;
	                         }
	                         if (word !=null && sf !=null){
	                             String newWord = sf.normalizeSynonyms(word);
	                             if (sf.hasSynonyms(word.toLowerCase())){
	                                 String toReplace = hasPoint?word+".":word; 

	                                 if (!StringHelper.isEmptyString(prefix)){
	                                     normalizePrefix = getExpandedStreetPrefix(prefix);
	                                     separator = normalizePrefix.endsWith("-")?"":" ";
	                                 } 
	                                 if (!StringHelper.isEmptyString(dir)){
	                                     dir = dir+" ";
	                                 }else {
	                                     dir="";
	                                 }
	                                 return dir+normalizePrefix+separator+street.replaceFirst(toReplace,newWord);
	                                 //return street.replaceFirst(toReplace,newWord);
	                             }
	                         }
	                     }
	                 }
	                 if (!StringHelper.isEmptyString(prefix)){
	                     normalizePrefix = getExpandedStreetPrefix(prefix);
	                     separator = normalizePrefix.endsWith("-")?"":" ";
	                     prefix=null;
	                 } 
	                 if (!StringHelper.isEmptyString(dir)){
	                     dir = dir+" ";
	                 }else {
	                     dir="";
	                 }

	                 street =  dir+normalizePrefix+separator+street;

	                 /* normalizePrefix+separator+street;

				if (prefix!=null){
				    String normalizePrefix = expandStreetPrefix(prefix);
                    String separator = normalizePrefix.endsWith("-")?"":" ";
                    prefix=null;
				street =  normalizePrefix+separator+street;
				}*/

	                 if (streetTypeOrder==StreetTypeOrder.nameThenType || streetTypeOrder==StreetTypeOrder.unknow){ 
	                     SynonymsFinder sf = synonymsManager.getSynonymsFinderFromLanguage(languageFromCountryCode);
	                     if (isNorthAmericaCountryCode){
	                         Matcher matcher = END_DIRECTION_PATTERN.matcher(street);
	                         if (matcher.find()){
	                             dir = matcher.group(1);
	                             street =  street.substring(0, street.length()-(dir.length())).trim();
	                         } 
	                     }
	                     int lastIndex = street.lastIndexOf(" ")+1;
	                     word = street.substring(lastIndex);
	                     int incrPointer = 0;
	                     if (word.indexOf(".")>0){
	                         word = word.substring(0, word.indexOf('.'));
	                         hasPoint=true;
	                         incrPointer = 1;
	                     }
	                     if (word !=null && sf !=null) {
	                         String newWord = sf.normalizeSynonyms(word);
	                         if (!isEmptyString(dir)){
	                             dir = " "+dir;
	                         }
	                         if (sf.hasSynonyms(word.toLowerCase())){
	                             //String toReplace = hasPoint?word+".":word;

	                             return street.substring(0, lastIndex)+newWord+street.substring(lastIndex+incrPointer+word.length())+dir;

	                         }
	                         street =  street+dir;
	                     }
	                 }
	             }
	         }

	     }
	     StringBuffer sb = new StringBuffer();
	     Matcher m = GERMAN_SYNONYM_PATTERN.matcher(street);
	     while (m.find()) {
	         if (countryCode!=null){
	             countryCode = countryCode.toUpperCase();
	             if (countryCode.equals("DE")|countryCode.equals("AT")){
	                 m.appendReplacement(sb, "straße");
	             } else if (countryCode.equals("NL")){
	                 m.appendReplacement(sb, "straat");
	             } else if (countryCode.equals("CH")){
	                 m.appendReplacement(sb, "strasse");
	             }else if (countryCode.equals("DK")){
	                 m.appendReplacement(sb, "stræde");
	             }else if (countryCode.equals("MD")){
	                 m.appendReplacement(sb, "strada");
	             } 
	         }else {
	             //default to straße
	             m.appendReplacement(sb, "straße");
	         }
	     }
	     m.appendTail(sb);
	     String s = sb.toString();
	     return s;
	 }


	//always put in lowercase
	
	
     
   
     
     public static final Collection<String> DE_STREETTYPE_LIST_AFTER_NORMALIZATION=new ArrayList<String>(){
    	 {
    		 add("strassen");
    		 add("strasse");
    		   add("straße");
    		   add("straßen");
    		   add("str");
    		   add("allee");
    		   add("alleen");
    		   add("all");
    		   add("platz");
    		   add("fleck");//place
    		   add("Platze");
    		   add("pl");
    		   add("gewerbegebiet");//ZI
    		   add("gg");
    		   add("damm");
    		   add("damme");
    		   add("res");
    		   add("chausee");
    		   add("chee");
    		   add("brucke");//pont
    		   add("br");
    		   add("gasse");//ruelle
    		   add("gassen");//ruelle
    		   add("pfad");//sentier, chemin
    		   add("weg");
    		   add("landstraße");
    		   add("landstraßen");
    		   add("pfad");
    		   add("pfade");
    		   add("ring");
    		   add("steig");
    		   add("steige");
    		   add("ufer");
    		   add("landstr");
    		   add("park");
    		   add("autobahn");
    		   add("platz");
    		    add("platze");
    		    add("stræde");
    		    add("staede");
    		    add("strada");
    		    add("straat");
    	 }
     };
     
     
     public static String removeStreetType(String street,String countryCode){
    	 if (street==null){
    		 return null;
    	 }
 		street= street.trim();
 		boolean hasPoint = false;
 		if (countryCode != null ){
 			Language languageFromCountry = getLanguageFromCountryCode(countryCode);
 			if (languageFromCountry!=null){
 				List<Language> languages = new ArrayList<Language>();
 				if (languageFromCountry.toString().contains("_")){
 					for(String l:languageFromCountry.toString().split("_")){
 						languages.add(Language.valueOf(l));
 					}
 				} else {
 					languages.add(languageFromCountry);
 				}
 				
 				for (Language languageFromCountryCode: languages){
 				String word;
 				StreetTypeOrder streetTypeOrder = languageFromCountryCode.getStreetTypeOrder();
 				if (streetTypeOrder==StreetTypeOrder.typeThenName || streetTypeOrder==StreetTypeOrder.unknow){
 					SynonymsFinder sf = synonymsManager.getSynonymsFinderFromLanguage(languageFromCountryCode);
 					
 					if (street.indexOf(' ')>0){
 						word = street.substring(0, street.indexOf(' '));

 						if (word.indexOf(".")>0){
 							word = word.substring(0, word.indexOf('.'));
 							hasPoint=true;
 						}
 						if (word !=null && sf !=null){
 						//	String newWord = sf.normalizeSynonyms(word);
 							if (sf.hasSynonyms(word.toLowerCase())){
 								String toReplace = hasPoint?word+".":word;
 								return street.replaceFirst(toReplace,"").trim();
 							}
 						}
 					}
 				}
 				if (streetTypeOrder==StreetTypeOrder.nameThenType || streetTypeOrder==StreetTypeOrder.unknow){ 
 					SynonymsFinder sf = synonymsManager.getSynonymsFinderFromLanguage(languageFromCountryCode);
 					word = street.substring(street.lastIndexOf(" ")+1);
 					if (word.indexOf(".")>0){
 						word = word.substring(0, word.indexOf('.'));
 						hasPoint=true;
 					}
 					if (word !=null && sf !=null) {
 						//String newWord = sf.normalizeSynonyms(word);
 						if (sf.hasSynonyms(word.toLowerCase())){
 							String toReplace = hasPoint?word+".":word;
 							return street.replaceFirst(toReplace, "").trim();
 						}
 					}
 				}
 			}
 			}
 		
 		}
 		if((countryCode!=null && (Decompounder.isDecompoudCountryCode(countryCode)||  "BE".equalsIgnoreCase(countryCode))) || decompounder.getSate(street)!=state.NOT_APPLICABLE){
 			if (street.indexOf(' ')>0){
 				String lastword = street.substring(street.lastIndexOf(" ")+1);
 				if (lastword.indexOf(".")>0){
 					lastword = lastword.substring(0, lastword.indexOf('.'));
 					hasPoint=true;
 				}
 				if (lastword !=null) {
 					if (DE_STREETTYPE_LIST_AFTER_NORMALIZATION.contains(lastword.toLowerCase())){
 						String toReplace = hasPoint?lastword+".":lastword;
 						return street.replaceFirst(toReplace, "").trim();
 					}
 				}
 			}
 			street = decompounder.getOtherFormat(street);
 			if (street.indexOf(' ')>0){
 				String lastword = street.substring(street.lastIndexOf(" ")+1);
 				if (lastword.indexOf(".")>0){
 					lastword = lastword.substring(0, lastword.indexOf('.'));
 					hasPoint=true;
 				}
 				if (lastword !=null) {
 					if (DE_STREETTYPE_LIST_AFTER_NORMALIZATION.contains(lastword.toLowerCase())){
 						String toReplace = hasPoint?lastword+".":lastword;
 						return street.replaceFirst(toReplace, "").trim();
 					}
 				}
 			}
 		}
 		return street;
     }
	
	public static boolean isSameStreetName(String name,OpenStreetMap openstreetmap){
		if (name!=null && openstreetmap!=null){
			if (StringHelper.isSameStreetName(name, openstreetmap.getName(), openstreetmap.getCountryCode())){
				return true;
			}
			//search deeper
			if (openstreetmap.getAlternateNames()!=null){
				for (AlternateOsmName alterString:openstreetmap.getAlternateNames()){
					if (alterString!=null){
						if (StringHelper.isSameStreetName(name, alterString.getName(), openstreetmap.getCountryCode())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	public static String correctStreetName(String streetName,String countryCode){
        if (streetName!=null){
            streetName = cleanupStreetName(streetName);//size, double space, etc
            streetName = StringHelper.expandStreetType(streetName, countryCode);
            if (countryCode!=null && (countryCode.equalsIgnoreCase("US") || countryCode.equalsIgnoreCase("CA"))){
                streetName = StringHelper.expandStreetDirections(streetName);
                streetName = StringHelper.expandOrdinalText(streetName);
            }
            streetName = WordUtils.capitalize(streetName.toLowerCase());
            return streetName;
        }
        return null;
    }
	
	public static String cleanupStreetName(String streetName){
        if (streetName!=null){
            if (streetName.length()>MAX_STREET_NAME_SIZE){
                streetName = streetName.substring(0, MAX_STREET_NAME_SIZE);
            }
            return streetName.trim().replaceAll("[\\s]+", " ").replaceFirst("^0+(?!$)", "").trim();
        }
        return streetName;
    }
	
	public static String prepareQuery(String rawAddress) {
		if (rawAddress == null){
			return rawAddress;
		}
		StringBuffer sb;
		Matcher m = RN_PATTERN.matcher(rawAddress);
		if (m.find()){
			sb = new StringBuffer();
			m.appendReplacement(sb,"route nationale "+m.group(2));
			m.appendTail(sb);
			rawAddress = sb.toString();
		}
		
		m = ZIPCONCATENATE_3_2__PATTERN.matcher(rawAddress);
		if (m.find()){
			sb = new StringBuffer();
			m.appendReplacement(sb,m.group(1)+" "+m.group(2)+m.group(3));
			m.appendTail(sb);
			rawAddress = sb.toString();
		} else {
			m = ZIPCONCATENATE_2_3_PATTERN.matcher(rawAddress);
			if (m.find()){
				sb = new StringBuffer();
				if (m.group(1)!=null){
				    m.appendReplacement(sb,m.group(1)+" "+m.group(2)+m.group(3));
				} else {
				    m.appendReplacement(sb,m.group(2)+m.group(3));
				}
				m.appendTail(sb);
				rawAddress = sb.toString();
			} 
			
		}
		
		if (logger.isDebugEnabled()){
		    logger.debug("prepared address : "+rawAddress);
		}
		return rawAddress;
	}


}
