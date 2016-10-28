package com.gisgraphy.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class CountriesStaticData {
    
    public static Map<String, List<String>> countryAlternateNames = new HashMap<String, List<String>>();
    
    public static void loadCountryNamesMap(){
	String pathname = "countries-alternatenames.txt";
	//InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathname);
	File file = new File(pathname);
	BufferedReader br = null;
	InputStream is = null;
	try {
		if (file.exists()){
			is = new BufferedInputStream(new FileInputStream(file));
		} else {
			is = Thread.currentThread().getClass().getResourceAsStream("/"+pathname);
			if (is==null){
				is = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathname);
			}
			if (is==null){
				throw new RuntimeException("file "+file.getPath()+" / " + file.getAbsolutePath() + " does not exists or is not present in classpath");
			}
		}
		br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String line ;
		while ((line=br.readLine())!=null){
		    String[] fields = line.split("\t");
		    if (fields.length<=1){
			continue;
		    }
		    String countrycode = fields[0];
		    List<String> alternateNames = new ArrayList<String>();
		    for (int i=1;i<fields.length;i++){
			
			String alternateName = fields[i].replaceAll("\\s+", " ").replaceAll("[\\s\\-\\']+", " ").trim();
			alternateNames.add(alternateName.toLowerCase());
		    }
		    countryAlternateNames.put(countrycode, mergeAndSort(alternateNames));
		}
	} catch (Exception e) {
		throw new RuntimeException(e.getMessage(),e);
	} finally {
		if (is != null) {
			try {
				is.close();
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
    
    static{
	loadCountryNamesMap();
    }
    
    public static List<String> mergeAndSort(Collection<String> collection) {
		Set<String> union = new HashSet<String>(collection);
		String[] set = new String[union.size()];
		List<String> lst = Arrays.asList(union.toArray(set));
		Collections.sort(lst, new Comparator<String>() {
		    public int compare(String o1, String o2) {
			return Integer.valueOf(o2.length()).compareTo(o1.length());
		    }
		});
		return lst;
	    }
    
    
    public final static List<String> countryCodeSortedByPopularity = new ArrayList<String>(){
    	{
    		add("US");
    		add("CA");
    		add("FR");
    		add("DE");
    		add("GB");
    		add("CN");
    		add("IN");
    		add("DK");
    		add("ES");
    		add("ID");
       		add("NL");
       		add("IT");
    		add("BR");
    		add("CO");
    		add("PK");
    		add("NG");
    		add("BD");
    		add("RU");
    		add("JP");
    		add("MX");
    		add("PH");
    		add("ET");
    		add("VN");
    		add("EG");
    		add("IR");
    		add("TR");
    		add("CD");
    		add("TH");
    	
    		add("MM");
    		add("ZA");
    		add("TZ");
    		add("KR");
    		add("VE");
    		
    		add("KE");
    		add("UA");
    		add("AR");
    		add("DZ");
    		add("PL");
    		add("UG");
    		add("IQ");
    		add("SD");
    		add("MA");
    		add("AF");
    		add("NP");
    		add("MY");
    		add("PE");
    		add("AU");
    		add("UZ");
    		add("SA");
    		add("YE");
    		add("GH");
    		add("MZ");
    		add("KP");
    		add("MG");
    		add("CM");
    		add("TW");
    		add("CI");
    		add("SE");
    		add("LK");
    		add("RO");
    		add("AO");
    		add("BF");
    		add("KZ");
    		add("NE");
    		add("MW");
    		add("CL");
    		add("SY");
    		add("ML");
    		add("EC");
    		add("KH");
    		add("ZM");
    		add("GT");
    		add("ZW");
    		add("SN");
    		add("RW");
    		add("SS");
    		add("GN");
    		add("TD");
    		add("BE");
    		add("TN");
    		add("CU");
    		add("PT");
    		add("BO");
    		add("GR");
    		add("BI");
    		add("CZ");
    		add("SO");
    		add("DO");
    		add("BJ");
    		add("HT");
    		add("HU");
    		add("AZ");
    		add("BY");
    		add("HN");
    		add("AT");
    		add("TJ");
    		add("CH");
    		add("JO");
    		add("IL");
    		add("TG");
    		add("BG");
    		add("RS");
    		add("HK");
    		add("LA");
    		add("PY");
    		add("PG");
    		add("ER");
    		add("LY");
    		add("LB");
    		add("SV");
    		add("NI");
    		add("SL");
    		add("AE");
    		add("SG");
    		add("KG");
    		add("FI");
    		add("SK");
    		add("CF");
    		add("TM");
    		add("NO");
    		add("GE");
    		add("IE");
    		add("CR");
    		add("CG");
    		add("HR");
    		add("NZ");
    		add("LR");
    		add("BA");
    		add("PA");
    		add("PR");
    		add("MR");
    		add("MD");
    		add("UY");
    		add("OM");
    		add("AM");
    		add("AL");
    		add("MN");
    		add("JM");
    		add("LT");
    		add("KW");
    		add("PS");
    		add("NA");
    		add("QA");
    		add("BW");
    		add("MK");
    		add("LV");
    		add("SI");
    		add("GM");
    		add("LS");
    		add("XK");
    		add("GW");
    		add("GA");
    		add("SZ");
    		add("BH");
    		add("MU");
    		add("EE");
    		add("TL");
    		add("TT");
    		add("CY");
    		add("FJ");
    		add("DJ");
    		add("KM");
    		add("BT");
    		add("GQ");
    		add("GY");
    		add("RE");
    		add("ME");
    		add("SB");
    		add("MO");
    		add("SR");
    		add("EH");
    		add("LU");
    		add("CV");
    		add("GP");
    		add("BN");
    		add("MT");
    		add("MQ");
    		add("MV");
    		add("BZ");
    		add("IS");
    		add("BS");
    		add("BB");
    		add("PF");
    		add("VU");
    		add("NC");
    		add("AN");
    		add("YT");
    		add("WS");
    		add("ST");
    		add("GF");
    		add("LC");
    		add("GU");
    		add("CW");
    		add("AW");
    		add("GD");
    		add("TO");
    		add("KI");
    		add("FM");
    		add("VI");
    		add("VC");
    		add("JE");
    		add("AG");
    		add("SC");
    		add("IM");
    		add("AD");
    		add("DM");
    		add("MH");
    		add("BM");
    		add("GG");
    		add("GL");
    		add("KY");
    		add("AS");
    		add("MP");
    		add("KN");
    		add("TC");
    		add("FO");
    		add("SX");
    		add("LI");
    		add("VG");
    		add("SM");
    		add("MC");
    		add("GI");
    		add("PW");
    		add("AI");
    		add("WF");
    		add("TV");
    		add("CK");
    		add("NR");
    		add("SH");
    		add("BL");
    		add("PM");
    		add("MS");
    		add("FK");
    		add("NF");
    		add("SJ");
    		add("CX");
    		add("TK");
    		add("NU");
    		add("VA");
    		add("CC");
    		add("PN");
    		add("GS");
    		add("AQ");
    		add("BV");
    		add("IO");
    		add("TF");
    		add("HM");

    	}
    	
    };
    
	public final static Map<String, String> countriesnameToCountryCodeMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 4762318033721437587L;

		{
			put("Afghanistan", "AF");
			put("Aland Islands", "AX");
			put("Albania", "AL");
			put("Algeria", "DZ");
			put("American Samoa", "AS");
			put("Andorra", "AD");
			put("Angola", "AO");
			put("Anguilla", "AI");
			put("Antarctica", "AQ");
			put("Antigua and Barbuda", "AG");
			put("Argentina", "AR");
			put("Armenia", "AM");
			put("Aruba", "AW");
			put("Australia", "AU");
			put("Austria", "AT");
			put("Azerbaijan", "AZ");
			put("Bahamas", "BS");
			put("Bahrain", "BH");
			put("Bangladesh", "BD");
			put("Barbados", "BB");
			put("Belarus", "BY");
			put("Belgium", "BE");
			put("Belize", "BZ");
			put("Benin", "BJ");
			put("Bermuda", "BM");
			put("Bhutan", "BT");
			put("Bolivia", "BO");
			put("Bosnia and Herzegovina", "BA");
			put("Botswana", "BW");
			put("Bouvet Island", "BV");
			put("Brazil", "BR");
			put("British Indian Ocean Territory", "IO");
			put("British Virgin Islands", "VG");
			put("Brunei", "BN");
			put("Bulgaria", "BG");
			put("Burkina Faso", "BF");
			put("Burundi", "BI");
			put("Cambodia", "KH");
			put("Cameroon", "CM");
			put("Canada", "CA");
			put("Cape Verde", "CV");
			put("Cayman Islands", "KY");
			put("Central African Republic", "CF");
			put("Chad", "TD");
			put("Chile", "CL");
			put("China", "CN");
			put("Christmas Island", "CX");
			put("Cocos Islands", "CC");
			put("Colombia", "CO");
			put("Comoros", "KM");
			put("Cook Islands", "CK");
			put("Costa Rica", "CR");
			put("Croatia", "HR");
			put("Cuba", "CU");
			put("Cyprus", "CY");
			put("Czech Republic", "CZ");
			put("Democratic Republic of the Congo", "CD");
			put("Denmark", "DK");
			put("Djibouti", "DJ");
			put("Dominica", "DM");
			put("Dominican Republic", "DO");
			put("East Timor", "TL");
			put("Ecuador", "EC");
			put("Egypt", "EG");
			put("El Salvador", "SV");
			put("Equatorial Guinea", "GQ");
			put("Eritrea", "ER");
			put("Estonia", "EE");
			put("Ethiopia", "ET");
			put("Falkland Islands", "FK");
			put("Faroe Islands", "FO");
			put("Fiji", "FJ");
			put("Finland", "FI");
			put("France", "FR");
			put("French Guiana", "GF");
			put("French Polynesia", "PF");
			put("French Southern Territories", "TF");
			put("Gabon", "GA");
			put("Gambia", "GM");
			put("Georgia", "GE");
			put("Germany", "DE");
			put("Ghana", "GH");
			put("Gibraltar", "GI");
			put("Greece", "GR");
			put("Greenland", "GL");
			put("Grenada", "GD");
			put("Guadeloupe", "GP");
			put("Guam", "GU");
			put("Guatemala", "GT");
			put("Guernsey", "GG");
			put("Guinea", "GN");
			put("Guinea-Bissau", "GW");
			put("Guyana", "GY");
			put("Haiti", "HT");
			put("Heard Island and McDonald Islands", "HM");
			put("Honduras", "HN");
			put("Hong Kong", "HK");
			put("Hungary", "HU");
			put("Iceland", "IS");
			put("India", "IN");
			put("Indonesia", "ID");
			put("Iran", "IR");
			put("Iraq", "IQ");
			put("Ireland", "IE");
			put("Isle of Man", "IM");
			put("Israel", "IL");
			put("Italy", "IT");
			put("Ivory Coast", "CI");
			put("Jamaica", "JM");
			put("Japan", "JP");
			put("Jersey", "JE");
			put("Jordan", "JO");
			put("Kazakhstan", "KZ");
			put("Kenya", "KE");
			put("Kiribati", "KI");
			put("Kosovo", "XK");
			put("Kuwait", "KW");
			put("Kyrgyzstan", "KG");
			put("Laos", "LA");
			put("Latvia", "LV");
			put("Lebanon", "LB");
			put("Lesotho", "LS");
			put("Liberia", "LR");
			put("Libya", "LY");
			put("Liechtenstein", "LI");
			put("Lithuania", "LT");
			put("Luxembourg", "LU");
			put("Macao", "MO");
			put("Macedonia", "MK");
			put("Madagascar", "MG");
			put("Malawi", "MW");
			put("Malaysia", "MY");
			put("Maldives", "MV");
			put("Mali", "ML");
			put("Malta", "MT");
			put("Marshall Islands", "MH");
			put("Martinique", "MQ");
			put("Mauritania", "MR");
			put("Mauritius", "MU");
			put("Mayotte", "YT");
			put("Mexico", "MX");
			put("Micronesia", "FM");
			put("Moldova", "MD");
			put("Monaco", "MC");
			put("Mongolia", "MN");
			put("Montenegro", "ME");
			put("Montserrat", "MS");
			put("Morocco", "MA");
			put("Mozambique", "MO");
			put("Myanmar", "MM");
			put("Namibia", "NA");
			put("Nauru", "NR");
			put("Nepal", "NP");
			put("Netherlands", "NL");
			//put("Netherlands Antilles", "AN");
			put("New Caledonia", "NC");
			put("New Zealand", "NZ");
			put("Nicaragua", "NI");
			put("Niger", "NE");
			put("Nigeria", "NG");
			put("Niue", "NU");
			put("Norfolk Island", "NF");
			put("Northern Mariana Islands", "MP");
			put("North Korea", "KP");
			put("Norway", "NO");
			put("Oman", "OM");
			put("Pakistan", "PK");
			put("Palau", "PW");
			put("Palestinian Territory", "PS");
			put("Panama", "PA");
			put("Papua New Guinea", "PG");
			put("Paraguay", "PY");
			put("Peru", "PE");
			put("Philippines", "PH");
			put("Pitcairn", "PN");
			put("Poland", "PL");
			put("Portugal", "PT");
			put("Puerto Rico", "PR");
			put("Qatar", "QA");
			put("Republic of the Congo", "CG");
			put("Reunion", "RE");
			put("Romania", "RO");
			put("Russia", "RU");
			put("Rwanda", "RW");
			put("Saint BarthÃ©lemy", "BL");
			put("Saint Helena", "SH");
			put("Saint Kitts and Nevis", "KN");
			put("Saint Lucia", "LC");
			put("Saint Martin", "MF");
			put("Saint Pierre and Miquelon", "PM");
			put("Saint Vincent and the Grenadines", "VC");
			put("Samoa", "WS");
			put("San Marino", "SM");
			put("Sao Tome and Principe", "ST");
			put("Saudi Arabia", "SA");
			put("Senegal", "SN");
			put("Serbia", "RS");
			//put("Serbia and Montenegro", "CS");
			put("Seychelles", "SC");
			put("Sierra Leone", "SL");
			put("Singapore", "SG");
			put("Slovakia", "SK");
			put("Slovenia", "SI");
			put("Solomon Islands", "SB");
			put("Somalia", "SO");
			put("South Africa", "ZA");
			put("South Georgia and the South Sandwich Islands", "GS");
			put("South Korea", "KR");
			put("Spain", "ES");
			put("Sri Lanka", "LK");
			put("Sudan", "SD");
			put("Suriname", "SR");
			put("Svalbard and Jan Mayen", "SJ");
			put("Swaziland", "SZ");
			put("Sweden", "SE");
			put("Switzerland", "CH");
			put("Syria", "SY");
			put("Taiwan", "TW");
			put("Tajikistan", "TJ");
			put("Tanzania", "TZ");
			put("Thailand", "TH");
			put("Togo", "TG");
			put("Tokelau", "TK");
			put("Tonga", "TO");
			put("Trinidad and Tobago", "TT");
			put("Tunisia", "TN");
			put("Turkey", "TR");
			put("Turkmenistan", "TM");
			put("Turks and Caicos Islands", "TC");
			put("Tuvalu", "TV");
			put("Uganda", "UG");
			put("Ukraine", "UA");
			put("United Arab Emirates", "AE");
			put("United Kingdom", "GB");
			put("United States", "US");
			put("United States Minor Outlying Islands", "UM");
			put("Uruguay", "UY");
			put("U.S. Virgin Islands", "VI");
			put("Uzbekistan", "UZ");
			put("Vanuatu", "VU");
			put("Vatican", "VA");
			put("Venezuela", "VE");
			put("Vietnam", "VN");
			put("Wallis and Futuna", "WF");
			put("Western Sahara", "EH");
			put("Yemen", "YE");
			put("Zambia", "ZM");
			put("Zimbabwe", "ZW");

		}
	};
	
	public static Collection<String> getCountryCodes(){
	    return countriesnameToCountryCodeMap.values();
	}

	public static ArrayList<String> sortedCountriesName = new ArrayList<String>(CountriesStaticData.countriesnameToCountryCodeMap.keySet()) {
		private static final long serialVersionUID = 1671688713929233996L;
		{
			Collections.sort(this);

		}
	};
	
	/**
	 * @return the sortedCountriesName
	 */
	public static List<String> getSortedCountriesPopularity() {
		return countryCodeSortedByPopularity;
	}

	public static int getNumberOfCountries(){
		return sortedCountriesName.size();
	}

	public static String getCountryCodeFromCountryName(String countryname) {
		return countriesnameToCountryCodeMap.get(countryname);
	}
	
	/**
	 * @param position the position in the sorted list (starts from 0)
	 * @return the country code for the given position, in the sorted @link {@value #sortedCountriesName}
	 * @throws IllegalArgumentException if position is negative
	 * @throws IndexOutOfBoundsException if position is too high
	 *  * @see {@link #getNumberOfCountries()}
	 */
	public static String getCountryCodeFromPosition(int position) {
		if (position<0){
			throw new IllegalArgumentException("position should be positive or null");
		}
		return getCountryCodeFromCountryName(getCountryNameFromPosition(position));
	}
	
	/**
	 * @param position the position in the {@value #sortedCountriesName} (starts from 0)
	 * @return the country name for the given position, in the sorted @link {@value #sortedCountriesName}
	 *  * @throws IllegalArgumentException if position is negative
	 * @throws IndexOutOfBoundsException if position is too high
	 * @see {@link #getNumberOfCountries()}
	 */
	public static String getCountryNameFromPosition(int position) {
		if (position<0){
			throw new IllegalArgumentException("position should be positive or null");
		}
		return sortedCountriesName.get(position);
	}

	/**
	 * @param countryCode the country code (iso 3166 alpha2)
	 * @return the country name
	 */
	public static String getCountryNameFromCountryCode(String countryCode) {
		for (Entry<String, String> entry : countriesnameToCountryCodeMap.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(countryCode)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @param countryCode the country code (iso 3166 alpha2)
	 * @return the position in the sortedCountryList. starts at 0
	 */
	public static int getPositionFromCountryCode(String countryCode) {
		int position = 0;
		String countryNameToSearch = getCountryNameFromCountryCode(countryCode);
		for (String country : sortedCountriesName) {
			if (country.equalsIgnoreCase(countryNameToSearch)) {
				return position;
			}
			position++;
		}
		return 0;
	}
}