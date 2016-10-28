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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.addressparser.commons.CountriesStaticData;
import com.gisgraphy.importer.AbstractAdvancedImporterProcessor;

/**
 * 
 * Detect Country
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class CountryDetector {
	
	/**
	 * The logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(CountryDetector.class);

	 public CountryDetectorDto detectAndRemoveCountry(String cleanedAddress) {
		 for (String countryCode: com.gisgraphy.helper.CountriesStaticData.countryCodeSortedByPopularity){
	    	if (countryCode == null || "SG".equalsIgnoreCase(countryCode) || "PA".equalsIgnoreCase(countryCode)) {
	    		// shortcut for specific country that got countryname and city is
	    		// the same (panama, singapour
	    		continue;
	    	}
	    	List<String> alternateNames = CountriesStaticData.countryAlternateNames.get(countryCode.toUpperCase());
	    	if (alternateNames != null) {
	    		String sanitarizeAddress = cleanedAddress.replaceAll("[\\s\\-\\']+", " ").trim().toLowerCase();
	    		for (String alternateName : alternateNames) {
	    			if (alternateName!=null && alternateName.toLowerCase().contains("mexico")){
	    				 //special case for mexico =>we should not remove because it is also a city and a sate
	    				continue;
	    			}
	    			if (sanitarizeAddress.endsWith(alternateName.toLowerCase())) {
	    				//String pattern = "(?i)" + alternateName + "\\s*$";
	    				Matcher matcher = Pattern.compile("(?i)" + alternateName + "\\s*$").matcher(sanitarizeAddress);
	    				if (matcher.find()){
	    					int index = matcher.start();
	    					String result = cleanedAddress.substring(0, index).trim();
	    					if (result != null && Pattern.matches(".*[,-]$", result) ){
	    						result = result.substring(0, result.length()-1).trim();
	    					}
	    					logger.info("'"+alternateName +"' has been detected as a country name and removed from "+cleanedAddress +" : "+result);
	    					return new CountryDetectorDto(result, countryCode);
	    				}
	    			}
	    		}
	    	}

	    }
		 return new CountryDetectorDto(cleanedAddress, null);
	 }
}
