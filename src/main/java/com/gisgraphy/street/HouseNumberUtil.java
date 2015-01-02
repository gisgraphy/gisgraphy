package com.gisgraphy.street;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 *  * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class HouseNumberUtil {
	
	private static Pattern DEFAULT_PATTERN = Pattern.compile("[\\-\\–\\一]?(\\d+)[^\\d]*",Pattern.CASE_INSENSITIVE);
	
	private static Pattern CZ_SK_PATTERN = Pattern.compile("(?:(?:\\d+[/\\-\\–\\一])|(?:[^\\d]?))(\\d+)",Pattern.CASE_INSENSITIVE);
	
	public static String normalizeNumber(String numberAsString){
		if (numberAsString ==null){
			return null;
		}
		Matcher matcher = DEFAULT_PATTERN.matcher(numberAsString);
		if (matcher.find()){
			return matcher.group(1);
		}
		return null;
	}
	
	/**
	 * specific case for slovakia and czech
	 * see http://www.pitt.edu/~votruba/qsonhist/housenumbersslovakia.html
	 */
	public static String normalizeSkCzNumber(String numberAsString){
		if (numberAsString ==null){
			return null;
		}
		Matcher matcher = CZ_SK_PATTERN.matcher(numberAsString);
		if (matcher.find()){
			return matcher.group(1);
		}
		return null;
	}

}
