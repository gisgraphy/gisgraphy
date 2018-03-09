package com.gisgraphy.geocoding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.HouseNumberAddressDto;
import com.gisgraphy.fulltext.suggest.GisgraphySearchEntry;


/**
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class GeocodingHelper {
	
	protected static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);
	
	public static String ROUTE = "(?:route\\snationale\\s?|autoroute\\b\\s?[A]?|route\\s?|D\\s?|RD\\s?|RN\\s?)";
	
	private static final String HN_REGEXP = "((("
			+ "(?:\\b\\d{1,4}[\\-\\–\\一]\\d{1,4}))\\b(?:[\\s,\\.;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b|street\\b|avenue\\b|de\\b|Januar\\b|janvier\\b|enero\\b|Gennaio\\b|Februar\\b|Febbraio\\b|f[ée]vrier\\b|febrero\\b|M[aä]rz\\b|mars\\b|marzo\\b|A[pvb]ril[e]?\\b|Mai\\b|mayo\\b|maggio\\b|juni[o]?\\b|juin\\b|Giugno\\ß|juli[o]?\\b|juillet\\b|Luglio\\b|august\\b|ao[uû]t\\b|agosto\\b|September\\b|sept[i]?embre\\b|Settembre\\b|o[ckt]tober\\b|o[tc]t[ou]bre\\b|november\\b|nov[i]?embre\\b|de[cz]ember\\b|d[ie]ec[i]embre\\b|dicembre\\b))(?=\\w+)+?)"
			+ "|(?:^\\b\\d{1,4}(?:\\s?(?:[a-d]\\b\\s)?)\\b)(?:[\\s,\\.;]?(?:bis|ter)?)(?:\\s|,)(?!(?:st\\b|th\\b|rd\\b|nd\\b|street\\b$|avenue\\b$|de\\b|Januar\\b|janvier\\b|enero\\b|Gennaio\\b|Februar\\b|Febbraio\\b|f[ée]vrier\\b|febrero\\b|M[aä]rz\\b|mars\\b|marzo\\b|A[pvb]ril[e]?\\b|Mai\\b|mayo\\b|maggio\\b|juni[o]?\\b|juin\\b|Giugno\\ß|juli[o]?\\b|juillet\\b|Luglio\\b|august\\b|ao[uû]t\\b|agosto\\b|September\\b|sept[i]?embre\\b|Settembre\\b|o[ckt]tober\\b|o[tc]t[ou]bre\\b|november\\b|nov[i]?embre\\b|de[cz]ember\\b|d[ie]ec[i]embre\\b|dicembre\\b))"
			+ "|(((?<!"+ROUTE+")(?:\\b\\d{1,4}(?:\\s?(?:[a-d]\\b)?)))\\b(?:[\\s,\\.;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b|street\\b|avenue\\b|de\\b|Januar\\b|janvier\\b|enero\\b|Gennaio\\b|Februar\\b|Febbraio\\b|f[ée]vrier\\b|febrero\\b|M[aä]rz\\b|mars\\b|marzo\\b|A[pvb]ril[e]?\\b|Mai\\b|mayo\\b|maggio\\b|juni[o]?\\b|juin\\b|Giugno\\ß|juli[o]?\\b|juillet\\b|Luglio\\b|august\\b|ao[uû]t\\b|agosto\\b|September\\b|sept[i]?embre\\b|Settembre\\b|o[ckt]tober\\b|o[tc]t[ou]bre\\b|november\\b|nov[i]?embre\\b|de[cz]ember\\b|d[ie]ec[i]embre\\b|dicembre\\b))(?=\\w+)+?)"
			+ "|(?<!"+ROUTE+")\\s?(?:\\b\\d{1,4}\\s?(?:[a-d])?\\b$)"
			+ "|(?<!"+ROUTE+")(?:\\b\\d{1,4}\\b\\s?(?:[a-d])?\\b$)"
			+")";
	public final static Pattern HOUSENUMBERPATTERN = Pattern.compile(HN_REGEXP,
			Pattern.CASE_INSENSITIVE);
	
	public final static Pattern FIRST_NUMBER_EXTRACTION_PATTERN = Pattern.compile("^([0-9]+)");
	public final static List<String> countryWithZipIs4Number= new ArrayList<String>(){
		{
			add("GE");
			add("AS");
			add("AU");
			add("BD");
			add("CH");
			add("CK");
			add("CR");
			add("CY");
			add("HU");
			add("HM");
			add("LR");
			add("SJ");
			add("MK");
			add("MZ");
			add("NE");
			add("NZ");
			add("PH");
			add("VE");
			add("CV");
			add("CX");
			add("ET");
			add("GW");
			add("ZA");
			add("LI");
			add("LU");
			add("PY");
			}
	};
	
	public final static List<String> countryWithZipIs3Number= new ArrayList<String>(){
		{
			add("GN");
			add("IS");
			add("LS");
			add("OM");
			add("PG");
			}
	};
	
	private static List<String> NAME_HOUSE_COUNTRYCODE = new ArrayList<String>() {
        {
            add("DE");
            add("BE");
            add("HR");
            add("IS");
            add("LV");
            add("NL");
            add("NO");
            add("NZ");
            add("PL");
            add("RU");
            add("SI");
            add("SK");
            add("SW");
            add("TR");
        }
    };

	
	public static HouseNumberAddressDto findHouseNumber(String address,
			String countryCode) {
		if (address == null) {
			return null;
		}
		Matcher m = HOUSENUMBERPATTERN.matcher(address);
		if (m.find()) {
			String houseNumber = m.group().trim();
			
			if (houseNumber != null) {

				Matcher m2 = FIRST_NUMBER_EXTRACTION_PATTERN
						.matcher(houseNumber);
				if (m2.find()) {
					houseNumber = m2.group();
				}
			}
			if (houseNumber.length() >=4 && (address.trim().indexOf(houseNumber)+houseNumber.length()) >= address.length()-3){
				//it is probably a zip code
				return null;
			}
			String newAddress;
			if (countryCode !=null){
				countryCode = countryCode.toUpperCase();
			}
			if (houseNumber.length() == 4 && (countryCode == null || (countryCode!= null && countryWithZipIs4Number.contains(countryCode)))  
					|| houseNumber.length() == 3 && (countryCode!= null && countryWithZipIs3Number.contains(countryCode)) 
					){
				logger.info("found house number " + houseNumber + " in '" + address
						+ "' for country '"+countryCode+"' but we don't remove it since it can be a zipcode");
				newAddress = address;
			} else {
				newAddress = m.replaceFirst("").trim();
				newAddress = newAddress.replaceFirst("^[,\\s]+", "");
				
			}
			HouseNumberAddressDto houseNumberAddressDto = new HouseNumberAddressDto(
					newAddress, address, houseNumber);
			logger.info("found house number " + houseNumber + " in '" + address
					+ "' for countrycode = '"+countryCode+"', new address wo housenumber = " + newAddress);
			return houseNumberAddressDto;
		} else {
			logger.info("no house number found in " + address);
			return null;
		}

	}
	
	public static String processLabel(GisgraphySearchEntry entry) {
	    if (entry ==null ){
	        return null;
	    }
        StringBuilder addressFormated = new StringBuilder();
        if (entry.getCountryCode() != null && NAME_HOUSE_COUNTRYCODE.contains(entry.getCountryCode().toUpperCase())) {
            if (entry.getName() !=null){
                addressFormated.append(entry.getName());
            }
            if (entry.getHouseNumber()!=null){
                addressFormated.append(" ").append(entry.getHouseNumber());
            }

        } else {
            if (entry.getHouseNumber()!=null) {
                addressFormated.append(entry.getHouseNumber()).append(", ");
            }
            if (entry.getName() !=null){
                addressFormated.append(entry.getName());
            }

        }
        if (entry.getIsIn() !=null || entry.getIsInPlace()!=null) {
            if (entry.getIsInPlace()!=null) {
                addressFormated.append(", ").append(entry.getIsInPlace());
            }
            if (entry.getIsInZip()!=null && entry.getIsInZip().size()>0) {
                addressFormated.append(", ").append(entry.getIsInZip().get(0));
            } else if (entry.getZipCodes() !=null && entry.getZipCodes().size() > 0){
                addressFormated.append(", ").append(entry.getZipCodes().get(0));
            }
            if (entry.getIsIn()!=null) {
                addressFormated.append(", ").append(entry.getIsIn());
            }
        }
        return addressFormated.toString();

    }


}
