package com.gisgraphy.fulltext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gisgraphy.helper.StringHelper;

/**
* A class to detect if a text conatins a street type
* 
* @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
* 
*/
public class SmartStreetDetection {
	
	private static final Pattern p = Pattern.compile("straße");
	
	private final static List<String> STREET_TYPES = new ArrayList<String>(){
		private static final long serialVersionUID = -3194005170253765829L;

		{
			add("rue");
			add("boulevard");
			add("autoroute");
			add("bd");
			add("blvd");
			add("avenue");
			add("chemin");
			add("rte");
			add("route");
			add("impasse");
			add("passage");
			add("place");
			add("sentier");
			add("voie");
			add("allee");
			add("alley");
			add("avenue");
			add("blvd");
			add("boulevard");
			add("highway");
			add("hiway");
			add("motorway");
			add("plaza");
			add("road");
			add("route");
			add("street");
			add("rua");
			add("plaza");
			add("carrera");
			add("camino");
			add("passatge");
			add("autovia");
			add("autopista");
			add("autobahn");
			add("fleck");
			
		};
	};
	
	private static final List<String> STREET_TYPES_DECOMPOUND = new ArrayList<String>(){
		{
			add("str");
			add("straße");
			add("strasse");
			add("plätze");
			add("platze");
			add("landstraße");
			add("landstrasse");
		}
	};
	
	private static final String STREET_REGEXP = getRegexp();
	private static final Pattern STREET_PATTERN = Pattern.compile(STREET_REGEXP,Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);

	public List<String> getStreetTypes(String textToTest){
		String textToTestNormalize = textToTest;
		int nbSpecialchar = 0;
		if (textToTest!=null){
			textToTestNormalize = StringHelper.normalize(textToTest);
			textToTest = textToTest.trim();
			nbSpecialchar = countNumberOfstrasse(textToTest);
		} else {
			return new ArrayList<String>();
		}
		Matcher matcher = STREET_PATTERN.matcher(textToTestNormalize);
		List<String> splitedString = new ArrayList<String>();
		int counter =0;
    	while (matcher.find()) {
    	    for (int j = 1; j <= matcher.groupCount(); j++) {
    	    	int shift=0;
    	    	if (nbSpecialchar > 0 && matcher.group(j).indexOf("strasse")>=0){
    	    		nbSpecialchar--;
    	    		counter++;
    	    		shift = 1;
    	    	}
    	    	//textToTest.length()
    		String realTextNotNormalized =  textToTest.substring(matcher.start(j)-(shift*(counter-1)),(matcher.end(j)-(shift*counter)));
    		    if (realTextNotNormalized!= null && !"".equals(realTextNotNormalized.trim())){
    		    	splitedString.add(realTextNotNormalized);
    		    }
    	    }
    	}
    	return splitedString;
		
	}
	
	private int countNumberOfstrasse(String text){
		int i = 0;
		
		Matcher m = p.matcher(text);
		while (m.find()) {
		    i++;
		}
		return i;
	}

	static String getRegexp() {
		StringBuffer sb =new StringBuffer("((?:");
		for (int i=0;i<STREET_TYPES.size();i++){
			sb.append("\\b").append(STREET_TYPES.get(i)).append("\\b");
			if (i!=STREET_TYPES.size()-1){
				sb.append("|");
			}
		}
		sb.append("\\b)|(?:");
		for (int i=0;i<STREET_TYPES_DECOMPOUND.size();i++){
			sb.append(STREET_TYPES_DECOMPOUND.get(i)).append("\\b");
			if (i!=STREET_TYPES_DECOMPOUND.size()-1){
				sb.append("|");
			}
		}
		
		sb.append("))");
		return sb.toString();
	}

}
