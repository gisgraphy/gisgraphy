package com.gisgraphy.street;

import static com.gisgraphy.street.HouseNumberDeserializer.HOUSENUMBER_AND_LOCATION_SEPARATOR;
import static com.gisgraphy.street.HouseNumberDeserializer.HOUSE_NUMBERS_SEPARATOR;
import static com.gisgraphy.street.HouseNumberDeserializer.LAT_LON_SEPARATOR;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;

import edu.emory.mathcs.backport.java.util.Collections;

public class HouseNumberSerializer {
	
	private Pattern clean_pattern = Pattern.compile(HouseNumberDeserializer.HOUSENUMBER_AND_LOCATION_SEPARATOR);
	private HouseNumberComparator comparator = new HouseNumberComparator();
	
	 /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(HouseNumberSerializer.class);
	

	public String serializeList(List<HouseNumber> houseNumberList){
		if (houseNumberList==null || houseNumberList.size()==0){
			return null;
		}
		Collections.sort(houseNumberList,comparator);
		StringBuffer sb = new StringBuffer();
		for (HouseNumber houseNumber:houseNumberList){
			if (houseNumber==null || houseNumber.getNumber()==null || houseNumber.getLocation()==null){
				continue;
			}
			String serialized = serialize(houseNumber);
			if (serialized!=null){
			sb.append(serialized)
			.append(HOUSE_NUMBERS_SEPARATOR);
			}
		}
		return sb.toString().trim();
	}
	
	public String serialize(HouseNumber houseNumber){
		if (houseNumber==null ){
			return null;
		}
		StringBuffer sb = new StringBuffer();
			if (houseNumber==null || houseNumber.getNumber()==null || houseNumber.getLocation()==null){
				return null;
			}
			String cleaned = clean_pattern.matcher(houseNumber.getNumber()).replaceAll("");
			sb.append(cleaned)
			.append(HOUSENUMBER_AND_LOCATION_SEPARATOR)
			.append(String.format(Locale.US, "%s", houseNumber.getLongitude().doubleValue()))
			.append(LAT_LON_SEPARATOR)
			.append(String.format(Locale.US, "%s", houseNumber.getLatitude().doubleValue()));
		return sb.toString().trim();
	}
	
	
}
