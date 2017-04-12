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
import java.util.HashSet;
import java.util.List;
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
	
	private static final Pattern ORDINAL_PATTERN = Pattern.compile("\\d\\s?(?:rd|st|nd|th)");
	
	protected static Pattern synonyms_Pattern= Pattern.compile("(saint|santa)", Pattern.CASE_INSENSITIVE);
	private final static Pattern RN_PATTERN = Pattern.compile("\\b(rn)\\s?(\\d{1,4}\\b)", Pattern.CASE_INSENSITIVE);
	private final static Pattern ZIPCONCATENATE_2_3_PATTERN = Pattern.compile("(.*)\\s\\b(\\d{2})[\\s-](\\d{3}\\b)");
	private final static Pattern ZIPCONCATENATE_3_2__PATTERN = Pattern.compile("(.*)\\s\\b(\\d{3})[\\s-](\\d{2}\\b)");
	private static final Pattern GERMAN_SYNONYM_PATTEN = Pattern.compile("(str\\b)[\\.]?",Pattern.CASE_INSENSITIVE);
	
	private static Decompounder decompounder = new Decompounder();

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
			if (decompounder.getSate(actual)!=state.NOT_APPLICABLE){
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

			//first we check if actual has more long words than expected
			//saint jean is not saint jean de luz, but 'la petite maison' is ok for 'petite maison'
			List<String> actualSplitedLong = new ArrayList<String>();
			for (String word:actualSplited){
				if (word.length()>3){
					if (word!=null){
						actualSplitedLong.add(normalize(word));
					}
				}  else if (word.equals("st")){
					Matcher m =synonyms_Pattern.matcher(expected);
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
					Matcher m =synonyms_Pattern.matcher(actual);
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
	
	public static boolean isSameStreetName(String expected, String actual, String countrycode){

		if (actual!=null && expected!=null){
			if (countrycode!=null && Decompounder.isDecompoudCountryCode(countrycode) && decompounder.getSate(actual)!=state.NOT_APPLICABLE){
				actual=expandStreetSynonyms(actual, null);
				expected=expandStreetSynonyms(expected, null);
				return isSameStreetName_intern(expected,actual) || isSameStreetName_intern(expected,decompounder.getOtherFormat(actual));
			}
			else {
				return isSameStreetName_intern(expected,actual);
			}
		}
		return false;
	}
	
	private static boolean isSameStreetName_intern(String expected, String actual){
		Pattern ordinalPattern = Pattern.compile("(\\d+)\\s?(?:rd|st|nd|th)?\\b");
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
					Matcher m =synonyms_Pattern.matcher(expected);
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
					Matcher m =synonyms_Pattern.matcher(actual);
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
				Matcher matcher1 = ordinalPattern.matcher(word);
				if (matcher1.find()){
					boolean foundOrdinal = false;
					for (String expectedLong:expectedSplitedLong){
						Matcher matcher2 = ordinalPattern.matcher(expectedLong);
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
	
	public static String expandStreetSynonyms(String alternativeGermanAddress, String countryCode) {
		StringBuffer sb = new StringBuffer();
		Matcher m = GERMAN_SYNONYM_PATTEN.matcher(alternativeGermanAddress);
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
