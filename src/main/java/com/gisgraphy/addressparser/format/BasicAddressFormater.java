package com.gisgraphy.addressparser.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.StreetTypeOrder;

/**
 *  @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class BasicAddressFormater {
	
	private static final Pattern CAPITALIZE = Pattern.compile("(?:\\b([a-z0-9])([a-z0-9]*)\\b(\\W*))",Pattern.CASE_INSENSITIVE);
	public static final String FILENAME = "format.tsv";
    private static int NUMBER_OF_FIELDS_BY_LINE = 9;
    Map<String, AddressFormatInfo> formatMap = new HashMap<String, AddressFormatInfo>();
    

    static BasicAddressFormater instance = new BasicAddressFormater();
    
    public BasicAddressFormater() {
		super();
		init();
	}

	public static BasicAddressFormater getInstance(){
    	return instance;
    }
    
	
	public AddressFormatInfo getCountryInfo(String countryCode){
		if (countryCode!=null){
			return formatMap.get(countryCode.toUpperCase());
		} else {
			return null;
		}
	}
	
   void init() {
	BufferedReader br = null;
	InputStream bis = null;
	int count = 0;
	try {

	    bis = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILENAME);
	    if (bis == null) {
	    	throw new RuntimeException("file " + FILENAME + " does not exists or is not present in classpath");
	    }
	    br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
	    String line;
	    while ((line = br.readLine()) != null) {
		count++;
		String[] fields = line.split("\\t");
		if (fields.length != NUMBER_OF_FIELDS_BY_LINE) {
		    throw new RuntimeException("Line " + count + " has not the correct number Of fields : expected " + NUMBER_OF_FIELDS_BY_LINE + " but was " + fields.length);
		}
		AddressFormatInfo formatInfo = new AddressFormatInfo();
		//country code
		if (fields[0] == null || fields[0].trim().length() != 2) {
		    throw new RuntimeException("Incorect countrycode '" + fields[0].trim().length() + "' on line " + count);
		}
		//pattern
		if (fields[1] == null) {
		    throw new RuntimeException("Format pattern is null for " + fields[0] + " on line " + count);
		}
		if (fields[1] != null && !"".equals(fields[1].trim())) {
		    formatInfo.setFormatString(fields[1]);
		}
		//
		if (fields[2] != null) {
			if (fields[2].trim().equals("1")){
				formatInfo.setOptionalState(true);
			} else if ( fields[2].trim().equals("0")||"".equals(fields[2].trim())){
				formatInfo.setOptionalState(false);
			} else {
				throw new RuntimeException("address formater : unknow optional state value '"+fields[2]);
			}
		}
		if (fields[3] != null && !"".equals(fields[3].trim())) {
			int value;
			try {
				value = Integer.valueOf(fields[3].trim());
			} catch (Exception e) {
				throw new RuntimeException("address formater : unknow street type before street name value '"+fields[4]);
			}
		   formatInfo.setStateLevel(value);;
		}
		
		
		
		if (fields[4] != null && !"".equals(fields[4].trim())) {
			int value;
			try {
				value = Integer.valueOf(fields[4].trim());
			} catch (Exception e) {
				throw new RuntimeException("address formater : unknow street type before street name value '"+fields[4]);
			}
		   formatInfo.setStreetTypeBeforeStreetName(value);
		}
		if (fields[5] != null) {
			if (fields[5].trim().equals("1")){
				formatInfo.setStateCode(true);
			} else if (fields[5].trim().equals("0") || "".equals(fields[5].trim())){
				formatInfo.setStateCode(false);
			} else {
				throw new RuntimeException("address formater : unknow state code value '"+fields[4]);
			}
		}
		if (fields[6] != null && !"".equals(fields[6].trim())) {
		    formatInfo.setFormatRTLString(fields[6]);
		}
		if (fields[7] != null) {
			if (fields[7].trim().equals("1")){
				formatInfo.setPoBoxOnly(true);
			} else if ( fields[7].trim().equals("0")||"".equals(fields[7].trim())){
				formatInfo.setPoBoxOnly(false);
			} else {
				throw new RuntimeException("address formater : unknow pobox only value '"+fields[6]);
			}
		}
		if (fields[8] != null && !"".equals(fields[8].trim())) {
			Matcher m = CAPITALIZE.matcher(fields[8]);

			StringBuilder sb = new StringBuilder();
		    int last = 0;
		    while (m.find()) {
		    	if (m.group(2).length()==0){
		    		 sb.append(m.group(1).toLowerCase());
		    	} else {
		         sb.append(m.group(1).toUpperCase());
		         sb.append(m.group(2).toLowerCase());
		    	}
		         sb.append(m.group(3));
		         last = m.end();
		    }
		    sb.append(fields[8].substring(last).toLowerCase());

		    String stringCamel = sb.toString();
		    formatInfo.setCountryName(stringCamel);
		}
		formatMap.put(fields[0].toUpperCase(), formatInfo);

	    }
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	} finally {
	    if (bis != null) {
		try {
		    bis.close();
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

   

   
   /**
    * same as {@link #getEnvelopeAddress(Address, ScriptType, DisplayMode)}, assuming scriptType is {@linkplain ScriptType#LTR}
    */
    public String getEnvelopeAddress(Address address,ScriptType scriptType, DisplayMode displayMode) {
	String newLine;
	if (DisplayMode.ENVELOPE == displayMode) {
	    newLine = "\r\n";
	} else if (DisplayMode.HTML == displayMode) {
	    newLine = "<br/>";
	}  else if (DisplayMode.COMMA == displayMode) {
	    newLine = ", ";
	} else {
	    newLine = " ";
	}
	List<String> lines = getLines(address,scriptType);
	return join(lines, newLine);
    }
    
    public String getEnvelopeAddress(Address address, DisplayMode displayMode) {
	return getEnvelopeAddress(address,ScriptType.LTR, displayMode);
    }

    private static boolean isNotNullOrEmpty(String toTest){
    	if (toTest != null && !"".trim().equals(toTest)){
    		return true;
    	}
    	return false;
    }
    
    /**
     * same as {@link #getLines(Address, ScriptType)}, assuming scriptType is {@linkplain ScriptType#LTR}
     */
    protected List<String> getLines(Address address) {
	return getLines(address,ScriptType.LTR);
    }
    /**
     * Gets formatted address. For example,
     * 
     * <p>
     * John Doe<br>
     * Dnar Corp<br>
     * 5th St<br>
     * Santa Monica CA 90123
     * </p>
     * 
     * This method does not validate addresses. Also, it will "normalize" the
     * result strings by removing redundant spaces and empty lines.
     */
    protected List<String> getLines(Address address,ScriptType scriptTypeparam) {
	if (address == null) {
	    throw new RuntimeException("null input address not allowed");
	}

	String regionCode = address.getCountryCode();
	
	if (regionCode != null){
		regionCode = regionCode.toUpperCase();
	}

	ScriptType scriptType = scriptTypeparam==null? ScriptType.LTR:scriptTypeparam;

	List<String> lines = new ArrayList<String>();
	if (scriptType == ScriptType.LTR && (isNotNullOrEmpty(address.getName()) || isNotNullOrEmpty(address.getRecipientName()))){
		lines.add(joinAndSkipNulls(" ",address.getName(),address.getRecipientName()).trim());
	}
	
	String formatString = getFormatString(scriptType, regionCode, address);
	if (address.getStreetName()==null && formatString.indexOf("2")>=0){
		formatString = formatString.replace("*75*", "*5*7*");
	}
	String[] substrings = formatString.split("\\*");
	for (String substr : substrings) {
		StringBuilder currentLine = new StringBuilder();
		int donTputNextComma = -1;
		int current_item = 0;
		for (char c : substr.toCharArray()) {
			boolean lineIsNotEmpty = currentLine.toString().trim().length()!=0;
			String sep= ",";
			current_item++;
			String part = "";
			if (c == '0'){
				/*AddressFormatInfo formatInfo = formatMap.get(regionCode);
				part = formatInfo.getCountryName();
				currentLine.append(part).append(" ");*/
				//will be managed in a separate part
			}
			else if (c == '1'){
				part = joinAndSkipNulls(" ",address.getName(), address.getHouseNumber(), address.getHouseNumberInfo());
				if (!"".equals(part) && current_item !=1  && lineIsNotEmpty){
					part=sep+" "+part;
				}
				donTputNextComma =2;
			}
			else if (c == '2'){
				StreetTypeOrder order = detectStreetTypeOrderFromAddress(address);
				String nameAndType; 
				if (order==StreetTypeOrder.nameThenType){
					if (scriptType == ScriptType.LTR){
						nameAndType=joinAndSkipNulls(" ",address.getStreetName(),address.getStreetType());
					} else {
						nameAndType = joinAndSkipNulls(" ",address.getStreetType(),address.getStreetName());
					}
				}else if (order==StreetTypeOrder.typeThenName){
					if (scriptType == ScriptType.LTR){
							nameAndType = joinAndSkipNulls(" ",address.getStreetType(),address.getStreetName());
						} else {
							nameAndType=joinAndSkipNulls(" ",address.getStreetName(),address.getStreetType());
						}
				} else {
					//default to the most popular one
					nameAndType = joinAndSkipNulls(" ",address.getStreetName(),address.getStreetType());
				}
				part = joinAndSkipNulls(" ",address.getPreDirection(), nameAndType, address.getPostDirection());
				if (!"".equals(part) && current_item !=1  && lineIsNotEmpty && donTputNextComma<1){
					part=", "+part;
				}
			}
			else if (c == '3'){
				part = joinAndSkipNulls(", ",address.getCitySubdivision(), address.getDependentLocality(), address.getQuarter(),address.getDistrict());
				if (!"".equals(part) && current_item !=1  && lineIsNotEmpty  && donTputNextComma<1){
					part=sep+" "+part;
				}
			}
			else if (c == '4'){
				//not managed yet
			}
			else if (c == '5'){
				if (address.getPostTown()!=null && !address.getPostTown().equals(address.getCity())){
					part = joinAndSkipNulls(" ",address.getCity(), address.getPostTown());
				} else if (isNotNullOrEmpty(address.getCity())){
					part = address.getCity();
				}
				if (!"".equals(part) && current_item !=1  && lineIsNotEmpty  && donTputNextComma<1){
					part=sep+" "+part;
				}
			}
			else if (c == '6'){
				AddressFormatInfo info = formatMap.get(regionCode);
				if (info!=null && !info.getOptionalState()){
					String state = getState(address);
					part = joinAndSkipNulls(" ",state);;
				}
				if (!"".equals(part) && current_item !=1  && lineIsNotEmpty){
					part=sep+" "+part;
				}
			}
			else if (c == '7'){
				part = joinAndSkipNulls(" ",address.getZipCode());
			}
			else if (c == '8'){
				//for future use
			}
			else if (c == '9'){
				part = joinAndSkipNulls(", ",address.getPOBox(),address.getPOBoxInfo(),address.getPOBoxAgency(),address.getPostOfficeBox());
				if (!"".equals(part) && current_item !=1  && lineIsNotEmpty  && donTputNextComma<1){
					part=sep+" "+part;
				}
			}
			else {
				part=c+"";
			}
			donTputNextComma--;
			currentLine.append(part);
				currentLine.append(" ");
		}

		String normalizedStr = removeAllRedundantSpaces(currentLine.toString());
		if (normalizedStr.length() > 0) {
			lines.add(normalizedStr.trim());
		}

	}
	if (scriptType == ScriptType.RTL && (isNotNullOrEmpty(address.getName()) || isNotNullOrEmpty(address.getRecipientName()))){
		lines.add(joinAndSkipNulls(" ",address.getName(),address.getRecipientName()).trim());
	}
		
	return lines;
    }

	protected String getState(Address address) {
		String state =null;
		if (address.getCountryCode()==null){
			if ( address.getState()!=null){
				state = address.getState();
			} else if (address.getDistrict()!=null){
				state = address.getDistrict();
			} else {
				state = address.getAdm1Name();
			}
		}else {
			
		AddressFormatInfo info = formatMap.get(address.getCountryCode().toUpperCase());
		if ( address.getState()!=null){
			state = address.getState();
		} else if (address.getDistrict()!=null){
			state = address.getDistrict();
		}
		if (info.getStateLevel()==1 && address.getAdm1Name()!=null){
			state = address.getAdm1Name();
		}
		if (info.getStateLevel()==2){
			if (address.getAdm2Name()!=null){
			state = address.getAdm2Name();
			} else if (address.getAdm1Name()!=null){
				state = address.getAdm1Name();
			}
		}
		if (info.getStateLevel()==3){
			if (address.getAdm3Name()!=null){
			state = address.getAdm3Name();
			} else if (address.getAdm1Name()!=null){
				state = address.getAdm1Name();
			}
		}
		if (info.getStateLevel()==4){
			if (address.getAdm4Name()!=null){
			state = address.getAdm4Name();
			} else if (address.getAdm1Name()!=null){
				state = address.getAdm1Name();
			}
		}if (info.getStateLevel()==5){
			if (address.getAdm5Name()!=null){
			state = address.getAdm5Name();
			} else if (address.getAdm1Name()!=null){
				state = address.getAdm1Name();
			}
		}
		}
		return state;
	}

    /**
     * Joins input string with the given separator. If an input string is null,
     * it will be skipped.
     */
   private  static String joinAndSkipNulls(String separator, String... strings) {
	StringBuilder sb = null;
	for (String s : strings) {
	    if (s != null) {
		s = s.trim();
		if (s.length() > 0) {
		    if (sb == null) {
			sb = new StringBuilder(s);
		    } else {
			sb.append(separator).append(s);
		    }
		}
	    }
	}
	return sb == null ? "" : sb.toString();
    }
    
   public StreetTypeOrder detectStreetTypeOrderFromAddress(Address address) {
   	if (address == null) {
   	    return StreetTypeOrder.unknow;
   	}
   	String countryCode = address.getCountryCode();
	if (countryCode != null) {
   		AddressFormatInfo formatInfo = formatMap.get(countryCode);
		if (formatInfo!=null){
    		int order = formatInfo.getStreetTypeBeforeStreetName();
    		switch (order) {
			case 0:
				return StreetTypeOrder.nameThenType;
			case 1:
				return StreetTypeOrder.typeThenName;
			case 2:
				return StreetTypeOrder.unknow;
			case 3:
				return StreetTypeOrder.unknow;
			case 4:
				return StreetTypeOrder.unknow;
			default:
				return StreetTypeOrder.unknow;
			}
    	}
   	    } 
	return StreetTypeOrder.unknow;
   	}

   
   private String removeAllRedundantSpaces(String str) {
		str = str.trim();
		str = str.replaceAll(" +", " ");
		str = str.replaceAll(" ,", ",");
		return str;
	    }
   
    protected String getFormatString(ScriptType scriptType, String countrycode, Address address) {
    	
	AddressFormatInfo formatInfo = formatMap.get(countrycode);
	if (address !=null && formatInfo!=null && formatInfo.isPoBoxOnly() && address.getPOBox()==null && address.getPOBoxInfo()==null && address.getPostOfficeBox()==null){
		 formatInfo = formatMap.get("ZZ");
		    if (formatInfo==null){
		    	throw new RuntimeException("no default pattern found");
		    }
		    return formatInfo.getFormatString();
	}
	if (formatInfo == null) {
	    formatInfo = formatMap.get("ZZ");
	    if (formatInfo==null){
	    	throw new RuntimeException("no default pattern found");
	    }
	}
	String format;

	if (scriptType == ScriptType.LTR) {
	    format = formatInfo.getFormatString();
	} else {
	    format = formatInfo.getFormatRTLString();
	    if (format == null) {
		format = formatInfo.getFormatString();
	    }
	}
	return format;
    }

    protected static String join(Collection<String> s, String delimiter) {
	StringBuffer buffer = new StringBuffer();
	Iterator<String> iter = s.iterator();
	while (iter.hasNext()) {
	    String next = iter.next();
	    if (next != null && !"".equals(next.trim())){
		buffer.append(next);
	    if (iter.hasNext()) {
		buffer.append(delimiter);
	    }
	    }
	}
	return buffer.toString();
    }
    
    public int getAdmLevelByContryCode(String countryCode){
    	if (countryCode != null){
    		AddressFormatInfo info = formatMap.get(countryCode);
			if (info!=null){
				Boolean optionalState = info.getOptionalState();
				if (optionalState!=null && optionalState==false){
    			return info.getStateLevel();
				}
    		}
    	}
    	return 0;
    }
   
}
