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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.compound.Decompounder;
import com.gisgraphy.compound.Decompounder.state;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;

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
	
	private static final Pattern ORDINAL_PATTERN = Pattern.compile("(\\d+)\\s?(?:rd|st|nd|th)?\\b");
	
	protected static final Pattern SYNONYMS_PATTERN= Pattern.compile("(saint|santa)", Pattern.CASE_INSENSITIVE);
	private final static Pattern RN_PATTERN = Pattern.compile("\\b(rn)\\s?(\\d{1,4}\\b)", Pattern.CASE_INSENSITIVE);
	private final static Pattern ZIPCONCATENATE_2_3_PATTERN = Pattern.compile("(.*)\\s\\b(\\d{2})[\\s-](\\d{3}\\b)");
	private final static Pattern ZIPCONCATENATE_3_2__PATTERN = Pattern.compile("(.*)\\s\\b(\\d{3})[\\s-](\\d{2}\\b)");
	private static final Pattern GERMAN_SYNONYM_PATTEN = Pattern.compile("(str\\b)[\\.]?",Pattern.CASE_INSENSITIVE);

	private static final Pattern DIRECTION_PATTERN = Pattern.compile("((?:\\b\\s[sewn]$)|(?:^[sewn]\\b\\s))",Pattern.CASE_INSENSITIVE);
	
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
		return originalString == null ? null : EncodingHelper.removeAccents(originalString.trim()).toLowerCase().replace("-", " ").replace(".", " ").replace("\"", " ").replace("'", " ").replace(';', ' ');

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
		if (openStreetMap.getName() != null) {
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
			if (decompounder.isDecompoudName(actual)){
				return isSameName(expected, actual, MISSING_WORD_TOLERANCE) || isSameName(expected, decompounder.getOtherFormat(actual), MISSING_WORD_TOLERANCE);
			}
			else {
				return isSameStreetName_intern(expected,actual);
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
			if (actual.equalsIgnoreCase(expected)){ //shortcut
				return true;
			}
			//split the strings
			String[] actualSplited = actual.split("[,\\s\\-\\–\\一;]");
			String[] expectedSplited = expected.split("[,\\s\\-\\–\\一]");
			
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
	
	private static List<String> FR_COUNTRIES = new ArrayList<String>(){{
		add("CA");add("FR");add("BE");add("CH");add("RE");add("GP");add("MF");add("MP");add("DZ");add("MA");add("SD");add("CD");add("CM");add("SN");add("PM");}};
	private static List<String> EN_COUNTRIES = new ArrayList<String>(){{add("US");add("CA");add("CN");add("ID");add("IN");add("AU");add("SG");add("HK");add("IR");add("FI");add("SA");add("VI");add("FK");add("GI");add("GL");add("FO");add("AS");add("IM");add("UM");add("GB");add("UK");add("PR");add("JE");add("SH");add("GS");add("GG");}};
    private static List<String> SP_COUNTRIES = new ArrayList<String>(){{add("AR");add("ES");add("MX");add("CO");add("PA");}};
	private static List<String> IT_COUNTRIES = new ArrayList<String>(){{add("IT");add("SM");add("VA");}};
	
	
	public static boolean isSameStreetName(String expected, String actual, String countrycode){
		if (actual!=null && expected!=null){
			actual=expandStreetType(actual, countrycode);
			expected=expandStreetType(expected, countrycode);
			actual=expandStreetSynonyms(actual);
			expected=expandStreetSynonyms(expected);
			if (countrycode!=null && (countrycode.equalsIgnoreCase("CA") || countrycode.equalsIgnoreCase("US"))){
				actual=expandStreetDirections(actual);
				expected=expandStreetDirections(expected);
			}
			return (isSameStreetName_intern(expected,actual) || 
					(actual.replaceAll("[^0-9]", "").equals(expected.replaceAll("[^0-9]", "")) && levenstein.execute(normalize(actual).replaceAll("\\s-", ""), normalize(expected).replaceAll("\\s-", ""))<2)
					);
		}
		return false;
	}
	
	private static boolean isSameStreetName_intern(String expected, String actual){
		int tolerance = 0;
		if (actual!=null && expected!=null){
			if (actual.equalsIgnoreCase(expected)){ //shortcut
				return true;
			}
			//split the strings
			String[] actualSplited = actual.split("[,\\s\\-\\–\\一;]");
			String[] expectedSplited = expected.split("[,\\s\\-\\–\\一]");

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
				Matcher matcher1 = ORDINAL_PATTERN.matcher(word);
				if (matcher1.find()){
					boolean foundOrdinal = false;
					for (String expectedLong:expectedSplitedLong){
						Matcher matcher2 = ORDINAL_PATTERN.matcher(expectedLong);
						if (!foundOrdinal && matcher2.find()){
							if(matcher1.group(1).equals(matcher2.group(1))){
							foundOrdinal=true;
						}
						}
					}
					if (!foundOrdinal){
						countMissing++;
					}
				}else {
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
		Matcher m = DIRECTION_PATTERN.matcher(street);
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
		street= street.trim();
		boolean hasPoint = false;
		if (countryCode != null && FR_COUNTRIES.contains(countryCode.toUpperCase())){
			if (street.indexOf(' ')>0){
			String firstWord = street.substring(0, street.indexOf(' '));
			
			if (firstWord.indexOf(".")>0){
				firstWord = firstWord.substring(0, firstWord.indexOf('.'));
				hasPoint=true;
			}
			if (firstWord !=null ){
					if (FR_STREET_TYPE_MAP.get(firstWord.toLowerCase())!=null){
						String toReplace = hasPoint?firstWord+".":firstWord;
						return street.replaceFirst(toReplace, FR_STREET_TYPE_MAP.get(firstWord.toLowerCase()));
					}
			}
			}
		}
		if (countryCode != null && SP_COUNTRIES.contains(countryCode.toUpperCase())){
			if (street.indexOf(' ')>0){
			String firstWord = street.substring(0, street.indexOf(' '));
			
			if (firstWord.indexOf(".")>0){
				firstWord = firstWord.substring(0, firstWord.indexOf('.'));
				hasPoint=true;
			}
			if (firstWord !=null ){
					if (SP_STREET_TYPE_MAP.get(firstWord.toLowerCase())!=null){
						String toReplace = hasPoint?firstWord+".":firstWord;
						return street.replaceFirst(toReplace, SP_STREET_TYPE_MAP.get(firstWord.toLowerCase()));
					}
			}
			}
		}
		if (countryCode != null && IT_COUNTRIES.contains(countryCode.toUpperCase())){
			if (street.indexOf(' ')>0){
			String firstWord = street.substring(0, street.indexOf(' '));
			
			if (firstWord.indexOf(".")>0){
				firstWord = firstWord.substring(0, firstWord.indexOf('.'));
				hasPoint=true;
			}
			if (firstWord !=null ){
					if (IT_STREET_TYPE_MAP.get(firstWord.toLowerCase())!=null){
						String toReplace = hasPoint?firstWord+".":firstWord;
						return street.replaceFirst(toReplace, IT_STREET_TYPE_MAP.get(firstWord.toLowerCase()));
					}
			}
			}
		}
		else if  (countryCode != null && (EN_COUNTRIES.contains(countryCode.toUpperCase()))){
			//last word
			if (street.indexOf(' ')>0){
			String lastword = street.substring(street.lastIndexOf(" ")+1);
			if (lastword.indexOf(".")>0){
				lastword = lastword.substring(0, lastword.indexOf('.'));
				hasPoint=true;
			}
			if (lastword !=null) {
				 if (US_STREET_TYPE_MAP.get(lastword.toLowerCase())!=null){
					 String toReplace = hasPoint?lastword+".":lastword;
					return street.replaceFirst(toReplace, US_STREET_TYPE_MAP.get(lastword.toLowerCase()));
			}
			}
			}
			//numbered road
			if (street.indexOf(' ')>0){
				String firstWord = street.substring(0, street.indexOf(' '));
				hasPoint = false;
				if (firstWord.indexOf(".")>0){
					firstWord = firstWord.substring(0, firstWord.indexOf('.'));
					hasPoint=true;
				}
				if (firstWord !=null ){
						if (NUMBERED_STREET_TYPE_MAP.get(firstWord.toLowerCase())!=null){
							String toReplace = hasPoint?firstWord+".":firstWord;
							return street.replaceFirst(toReplace, NUMBERED_STREET_TYPE_MAP.get(firstWord.toLowerCase()));
						}
				}
				}
		} 
		StringBuffer sb = new StringBuffer();
		Matcher m = GERMAN_SYNONYM_PATTEN.matcher(street);
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
	//	s= s.replaceAll(" stra(?:(?:ss)|(?:ß))e", "strasse");
		return s;
	}
	
	//always put in lowercase
	private static final Map<String, String> FR_STREET_TYPE_MAP = new HashMap<String, String>(){
		{
			put("r","rue");
    		put("rte","route");
			put("av","avenue");
    		put("bd","boulevard");
    		put("blvd","boulevard");
    		put("chem","chemin");
    		put("departementale","route departementale");
    		put("rte departementale","route departementale");
    		put("gr","grande randonnee");
		}
		
		
	};
	//always put in lowercase
	private static final Map<String, String> NUMBERED_STREET_TYPE_MAP = new HashMap<String, String>(){
		{
			put("sr","state route");
    		put("hwy","route");
			put("pth","perimeter highway");
    		put("ss","strada statale");
		}
		
		
	};
	
	//only frequent commonly used
	//always put in lowercase
	private static final Map<String, String> US_STREET_TYPE_MAP = new HashMap<String, String>(){
		{
			put("r","rue");
    		put("rte","route");
			put("av","avenue");
			put("ave","avenue");
    		put("bd","boulevard");
    		put("blvd","boulevard");
    		put("aly","alley");
    		put("anx","anex");
    		put("arc","arcade");
    		put("bch","beach");
    		put("boul","boulevard");
    		put("boulv","boulevard");
    		put("brdg","bridge");
    		put("dr","drive");
    		put("drs","drives");
    		put("highwy","highway");
    		put("hiway","highway");
    		put("hiwy","highway");
    		put("hway","highway");
    		put("hwy","highway");
    		put("pl","place");
    		put("rd","road");
    		put("st","street");
    		put("str","street");
    		put("tunl","tunnel");
    		put("tunnl","tunnel");
    		put("trail","trail");
    		put("ct","court");
    		put("cir","circle");
    		put("dr","drive");
    		put("ln","lane");
    		
		}
	};
	
	//always put in lowercase
    public static final Map<String,String> SP_STREET_TYPE_MAP = new HashMap<String,String>(){{
        put("alam","alameda");
        put("angta","angosta");
        
        put("auto","autopista");
        put("autov","autovia");
        
        put("av","avenida");
        put("ave","avenida");
        put("avd","avenida");
        put("avda","avenida");
        put("avinguda","avenida");
        
        put("bulev","bulevar");
        
        put("c","calle");
        
        put("ch","camino hondo");
        put("cn","camino nuevo");
        put("cv","camino viejo");
        put("callecillas","callecilla");
        put("callecitas","callecita");
        
        put("callezonas","callezona");
        put("callezotas","callezota");
        
        put("ccvcn","circunvalacion");
        put("cint","carretera interestatal");
        put("carretera de circunvalacion","circunvalacion");// deviation
        put("cjla","calleja");
        put("cjon","callejon");
        put("cl","calle");
        put("cllja","calleja");
        put("cllon","callejon");
        put("cllzo","callizo");
        put("cllza","calliza");
        put("cmno","camino");
        put("cro","carrero");
        put("cra","carrera");//
        put("cr","carrera");//
        put("crr","carrera");//
        put("cro","carrer");
        put("crril","Carril");
        put("ctra","carretera");
        put("ctrin","Carreterín");
        put("czada","calzada");
        put("diag","diagonal");
        put("err","errepidea");
        put("etorb","etorbidea");
        put("gv","gran vía");
        put("gta","glorieta");
        put("pasaje","passatge");
        put("psaje","passatge");
        put("ptge","passatge");
        put("passeig","passatge");
        put("pg","passatge");
        put("pl","plaça");
        put("plza","plaza");
        put("pza","plaza");
        put("pnte","puente");
        put("pto","puerto");
        put("rbla","rambla");
        put("sedra","sendaera");
        put("send","sendaera");
        put("sendera","sendaera");
        put("trans","tránsito");
        put("trval","transversal");
        put("trva","tranvia");
        put("v","via");
    }
    };
    
    public static final Map<String,String> IT_STREET_TYPE_MAP = new HashMap<String,String>(){{
    	 put("v","via");
    	 put("c","calle");
     }};
     
     public static final Collection<String> IT_STREETTYPE_LIST_AFTER_NORMALIZATION=getlistOfNormalizedStreetType(IT_STREET_TYPE_MAP);
     public static final Collection<String> EN_STREETTYPE_LIST_AFTER_NORMALIZATION=getlistOfNormalizedStreetType(US_STREET_TYPE_MAP);
     public static final Collection<String> FR_STREETTYPE_LIST_AFTER_NORMALIZATION=getlistOfNormalizedStreetType(FR_STREET_TYPE_MAP);
     public static final Collection<String> SP_STREETTYPE_LIST_AFTER_NORMALIZATION=getlistOfNormalizedStreetType(SP_STREET_TYPE_MAP);
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
 		if (countryCode != null && FR_COUNTRIES.contains(countryCode.toUpperCase())){
 			if (street.indexOf(' ')>0){
 			String firstWord = street.substring(0, street.indexOf(' '));
 			
 			if (firstWord.indexOf(".")>0){
 				firstWord = firstWord.substring(0, firstWord.indexOf('.'));
 				hasPoint=true;
 			}
 			if (firstWord !=null ){
 					if (FR_STREETTYPE_LIST_AFTER_NORMALIZATION.contains(firstWord.toLowerCase())){
 						String toReplace = hasPoint?firstWord+".":firstWord;
 						return street.replaceFirst(toReplace, "").trim();
 					}
 			}
 			}
 		}
 		if (countryCode != null && SP_COUNTRIES.contains(countryCode.toUpperCase())){
 			if (street.indexOf(' ')>0){
 			String firstWord = street.substring(0, street.indexOf(' '));
 			
 			if (firstWord.indexOf(".")>0){
 				firstWord = firstWord.substring(0, firstWord.indexOf('.'));
 				hasPoint=true;
 			}
 			if (firstWord !=null ){
 					if (SP_STREETTYPE_LIST_AFTER_NORMALIZATION.contains(firstWord.toLowerCase())){
 						String toReplace = hasPoint?firstWord+".":firstWord;
 						return street.replaceFirst(toReplace, "").trim();
 					}
 			}
 			}
 		}
 		if (countryCode != null && IT_COUNTRIES.contains(countryCode.toUpperCase())){
 			if (street.indexOf(' ')>0){
 			String firstWord = street.substring(0, street.indexOf(' '));
 			
 			if (firstWord.indexOf(".")>0){
 				firstWord = firstWord.substring(0, firstWord.indexOf('.'));
 				hasPoint=true;
 			}
 			if (firstWord !=null ){
 					if (IT_STREETTYPE_LIST_AFTER_NORMALIZATION.contains(firstWord.toLowerCase())){
 						String toReplace = hasPoint?firstWord+".":firstWord;
 						return street.replaceFirst(toReplace, "").trim();
 					}
 			}
 			}
 		}
 		else if  (countryCode != null &&  EN_COUNTRIES.contains(countryCode.toUpperCase())){
 			//last word
 			if (street.indexOf(' ')>0){
 			String lastword = street.substring(street.lastIndexOf(" ")+1);
 			if (lastword.indexOf(".")>0){
 				lastword = lastword.substring(0, lastword.indexOf('.'));
 				hasPoint=true;
 			}
 			if (lastword !=null) {
 				 if (EN_STREETTYPE_LIST_AFTER_NORMALIZATION.contains(lastword.toLowerCase())){
 					 String toReplace = hasPoint?lastword+".":lastword;
 					return street.replaceFirst(toReplace, "").trim();
 			}
 			}
 			}
 		} 
 		else if((countryCode!=null && (Decompounder.isDecompoudCountryCode(countryCode)||  "BE".equalsIgnoreCase(countryCode))) || decompounder.getSate(street)!=state.NOT_APPLICABLE){
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
	
	private static Collection<String> getlistOfNormalizedStreetType(
			Map<String, String> map) {
		Collection<String> results =  new ArrayList<String>(map.values());
				Collection<String> keySet = map.keySet();
				for (String s : map.keySet()){
					results.add(s);
				}
				return results;
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
				m.appendReplacement(sb,m.group(1)+" "+m.group(2)+m.group(3));
				m.appendTail(sb);
				rawAddress = sb.toString();
			} 
			
		}
		
		
		logger.error("prepared address : "+rawAddress);
		return rawAddress;
	}


}
