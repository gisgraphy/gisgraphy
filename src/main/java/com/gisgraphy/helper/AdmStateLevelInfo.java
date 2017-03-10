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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
@SuppressWarnings("serial")
public class AdmStateLevelInfo {

	/**
	 * The logger
	 */
	public static final Logger logger = LoggerFactory
			.getLogger(AdmStateLevelInfo.class);

	private static Map<String, Integer> lowLevel = new HashMap<String, Integer>();
	private static Map<String, Integer> HighLevel = new HashMap<String, Integer>();
	private static Map<String, List<Integer>> cityLevels = new HashMap<String, List<Integer>>();
	
	public static final int DEFAULT_HIGH_LEVEL =7 ; 
	public static final int LOW_HIGH_LEVEL =4 ;

	private static final int DEFAULT_CITY_LEVEL = 8; 
	

	static {
		lowLevel.put("AZ", 3);
		lowLevel.put("BR", 3);
		lowLevel.put("BT", 3);
		lowLevel.put("CN", 3);
		lowLevel.put("EH", 3);
		lowLevel.put("GQ", 3);
		lowLevel.put("GS", 3);
		lowLevel.put("GW", 3);
		lowLevel.put("IN", 3);
		lowLevel.put("LB", 3);
		lowLevel.put("MA", 3);
		lowLevel.put("MG", 3);
		lowLevel.put("PG", 3);
		lowLevel.put("PH", 3);
		lowLevel.put("RU", 3);
		lowLevel.put("SE", 3);
		lowLevel.put("SH", 3);
		lowLevel.put("SK", 3);
		lowLevel.put("SN", 3);
		lowLevel.put("TR", 3);
		lowLevel.put("TZ", 3);
		lowLevel.put("VE", 3);
		
		HighLevel.put("BA", 5);
		HighLevel.put("BD", 6);
		HighLevel.put("BH", 5);
		HighLevel.put("CF", 5);
		HighLevel.put("CN", 5);
		HighLevel.put("CO", 5);
		HighLevel.put("DK", 6);
		HighLevel.put("DZ", 6);
		HighLevel.put("GE", 5);
		HighLevel.put("HN", 8);
		HighLevel.put("HR", 6);
		HighLevel.put("ID", 6);
		HighLevel.put("IE", 7);
		HighLevel.put("IN", 6);
		HighLevel.put("IQ", 5);
		HighLevel.put("IR", 5);
		HighLevel.put("IS", 5);
		HighLevel.put("JP", 6);
		HighLevel.put("KR", 6);
		HighLevel.put("LV", 6);
		HighLevel.put("LY", 4);
		HighLevel.put("MK", 5);
		HighLevel.put("MY", 9);
		HighLevel.put("NO", 6);
		HighLevel.put("NZ", 6);
		HighLevel.put("PH", 5);
		HighLevel.put("PT", 6);
		HighLevel.put("RO", 5);
		HighLevel.put("RS", 6);
		HighLevel.put("SE", 6);
		HighLevel.put("SK", 6);
		HighLevel.put("SL", 5);
		HighLevel.put("TN", 4);
		HighLevel.put("TR", 5);
		HighLevel.put("TZ", 8);
		HighLevel.put("UA", 6);
		HighLevel.put("UG", 5);
		HighLevel.put("US", 6);
		
		cityLevels.put("BA", new ArrayList<Integer>(){{add(6);;add(7);add(8);}});
		cityLevels.put("BD", new ArrayList<Integer>(){{add(7);add(8);add(9);}});
		cityLevels.put("BH", new ArrayList<Integer>(){{add(6);add(8);}});
		cityLevels.put("CF", new ArrayList<Integer>(){{add(6);add(8);}});
		cityLevels.put("CN", new ArrayList<Integer>(){{add(6);add(7);add(8);add(10);}});
		cityLevels.put("CO", new ArrayList<Integer>(){{add(6);add(7);add(8);}});
		cityLevels.put("DK", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("DZ", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("EE", new ArrayList<Integer>(){{add(8);add(9);}});
		cityLevels.put("FI", new ArrayList<Integer>(){{add(8);add(9);}});
		cityLevels.put("GE", new ArrayList<Integer>(){{add(6);add(7);add(8);add(9);}});
		cityLevels.put("HN", new ArrayList<Integer>(){{add(9);}});
		cityLevels.put("HR", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("ID", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("IE", new ArrayList<Integer>(){{add(8);add(9);}});
		cityLevels.put("IN", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("IQ", new ArrayList<Integer>(){{add(6);}});
		cityLevels.put("IR", new ArrayList<Integer>(){{add(6);}});
		cityLevels.put("IS", new ArrayList<Integer>(){{add(6);}});
		cityLevels.put("JP", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("KR", new ArrayList<Integer>(){{add(7);add(8);add(10);}});
		cityLevels.put("LV", new ArrayList<Integer>(){{add(7);add(8);add(9);}});
		cityLevels.put("LY", new ArrayList<Integer>(){{add(6);}});
		cityLevels.put("MD", new ArrayList<Integer>(){{add(8);add(9);}});
		cityLevels.put("MK", new ArrayList<Integer>(){{add(6);add(7);add(8);}});
		cityLevels.put("MY", new ArrayList<Integer>(){{add(10);}});
		cityLevels.put("NO", new ArrayList<Integer>(){{add(7);}});
		cityLevels.put("NZ", new ArrayList<Integer>(){{add(7);}});
		cityLevels.put("PH", new ArrayList<Integer>(){{add(6);add(7);}});
		cityLevels.put("PL", new ArrayList<Integer>(){{add(8);add(10);}});
		cityLevels.put("PT", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("RO", new ArrayList<Integer>(){{add(6);add(8);}});
		cityLevels.put("RS", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("SE", new ArrayList<Integer>(){{add(8);}});
		cityLevels.put("SK", new ArrayList<Integer>(){{add(7);add(8);add(9);}});
		cityLevels.put("SL", new ArrayList<Integer>(){{add(6);}});
		cityLevels.put("TH", new ArrayList<Integer>(){{add(8);add(10);}});
		cityLevels.put("TN", new ArrayList<Integer>(){{add(5);}});
		cityLevels.put("TR", new ArrayList<Integer>(){{add(6);add(8);}});
		cityLevels.put("TZ", new ArrayList<Integer>(){{add(9);}});
		cityLevels.put("UA", new ArrayList<Integer>(){{add(7);add(8);}});
		cityLevels.put("UG", new ArrayList<Integer>(){{add(6);add(8);add(9);}});
		cityLevels.put("US", new ArrayList<Integer>(){{add(7);add(8);}});
		
		
	}

	public static int getLowLevel(String countryCode) {
		if (countryCode != null) {
			Integer level = lowLevel.get(countryCode.toUpperCase());
			if (level == null){
				return LOW_HIGH_LEVEL;
			} else {
				return level;
			}
		}
		return LOW_HIGH_LEVEL;
	}
	
	public static int getHighLevel(String countryCode) {
		if (countryCode != null) {
			Integer level = HighLevel.get(countryCode.toUpperCase());
			if (level == null){
				return DEFAULT_HIGH_LEVEL;
			} else {
				return level;
			}
		}
		return DEFAULT_HIGH_LEVEL;
	}
	
	public static boolean isCityLevel(String countryCode, String level){
		try {
			return isCityLevel(countryCode, Integer.parseInt(level));
		} catch (NumberFormatException e) {
			return false;
		}
	
	}
	
	public static boolean isCityLevel(String countryCode, int level){
		if (countryCode != null){
				List<Integer> levels = cityLevels.get(countryCode.toUpperCase());
				if (levels!=null){
						if (levels.contains(level)){
							return true;
						} else {
							return false;
						}
				} else {
					return level == DEFAULT_CITY_LEVEL;
				}
		}
		return level == DEFAULT_CITY_LEVEL;
		
	}
	
	public static boolean shouldBeImportedAsAdm(String countryCode,int osmLevel){
		if (countryCode!=null && osmLevel >0 && osmLevel >= getLowLevel(countryCode) && osmLevel <= getHighLevel(countryCode)){
			return true;
		}
		return false;
		
	}

	
}
