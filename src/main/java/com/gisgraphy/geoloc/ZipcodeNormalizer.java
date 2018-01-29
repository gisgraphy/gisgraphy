/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.geoloc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipcodeNormalizer {
    private static Logger logger = LoggerFactory.getLogger(ZipcodeNormalizer.class);

    private final static int REGEXP_CASEINSENSITIVE_FLAG = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
    private final static String CA_PATTERN_EXPRESSION = "(?<=[a-z]\\d[a-z])[\\s–\\-]?\\d[a-z]\\d";
    private final static Pattern CA_PATTERN = Pattern.compile(CA_PATTERN_EXPRESSION, REGEXP_CASEINSENSITIVE_FLAG);
    public static final Pattern ZIP_PATTERN = Pattern.compile("(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9‌​][0-9]?)|(([A-Z-[QVX‌​]][0-9][A-HJKSTUW])|‌​([A-Z-[QVX]][A-Z-[IJ‌​Z]][0-9][ABEHMNPRVWX‌​Y]))))\\s?[0-9][A-Z-[‌​CIKMOV]]{2})",Pattern.CASE_INSENSITIVE);
    
    private final static String GB_PATTERN_EXPRESSION = "(?<=[A-Z]{2}\\d[A-Z])\\s?\\d[A-Z]{2}|(?<=[A-Z]{2}\\d{2})\\s?\\d[A-Z]{2}|(?<=[A-Z]\\d)\\s?\\d[A-Z]{2}|(?<=[A-Z]{2}\\d)\\s?\\d[A-Z]{2}|(?<=[A-Z]\\d[A-Z])\\s?\\d[A-Z]{2}|(?<=[A-Z]\\d{2})\\s?\\d[A-Z]{2}|(?<=GIR)\\s?0AA|(?<=[A-Z]{4})\\s?1ZZ";
  //LLNL NLL|LLNN NLL|LN NLL|LLN NLL|LNL NLL|LNN NLL|
    private final static Pattern GB_PATTERN = Pattern.compile(GB_PATTERN_EXPRESSION, REGEXP_CASEINSENSITIVE_FLAG);

    public static String normalize_ca(String string) {
	return normalize_country(string, CA_PATTERN);
    }
    
    public static String normalize_gb(String string) {
	return normalize_country(string, GB_PATTERN);
    }
    
    /**
     * @return a string that prepare zipcode to be search
     * because for canada we only got first char and so does for GB
     */
    public static String normalize(String string,String countryCode){
	if (string==null){
	    return null;
	}
	if (countryCode == null || "".equals(countryCode.trim())|| "CA".equalsIgnoreCase(countryCode)){
	    return  normalize_ca(string);
	} /*else if("GB".equalsIgnoreCase(countryCode)){
	    return normalize_gb(string);
	} else if ("CA".equalsIgnoreCase(countryCode)){
	    return normalize_ca(string); 
	} */else {
	    return string;
	}
	
    }

    private static String normalize_country(String string, Pattern pattern) {
	if (string==null){
	    return null;
	}
	Matcher matcher = pattern.matcher(string);

	if (logger.isInfoEnabled()) {
	    if (matcher.find()) {
		logger.info("found one or more zipcode to normalize");
		String[] splitedString = new String[matcher.groupCount()];
		for (int j = 1; j <= matcher.groupCount(); j++) {
		    String group = matcher.group(j);
		    if (group != null) {
			group = group.trim();
		    }
		    splitedString[j - 1] = group;
		     if (logger.isInfoEnabled()) {
		     logger.info("[" + (j - 1) + "]=" + group);
		     }
		}
	    }
	}
	return pattern.matcher(string).replaceAll("").trim();
    }
    
    public static String fixGBZipCode(String zipcode){
		if (zipcode!=null){
			Matcher m = ZIP_PATTERN.matcher(zipcode);
			if (m.find()){
				zipcode=zipcode.replace(m.group(0), m.group(0).replace(" ", "-"));
			}
		}
		System.out.println("return new ZIP"+zipcode);
		return zipcode;
		
		
	}
    
    public static boolean containsGBPostCode(String zipcode){
		if (zipcode!=null){
			Matcher m = ZIP_PATTERN.matcher(zipcode);
			if (m.find()){
				return true;
			}
		}return false;
		
		
	}

   
}
