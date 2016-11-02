package com.gisgraphy.addressparser.format;

import java.util.List;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;


/**
 * AddressFormater for Gisgraphy entities
 *  @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class GisFeatureAddresFormater extends BasicAddressFormater {

	static GisFeatureAddresFormater instance;

	public static GisFeatureAddresFormater getInstance(){
		return new GisFeatureAddresFormater();
	}

	GisFeatureAddresFormater() {
		super();
	}

	public List<String> getLines(OpenStreetMap street,ScriptType scriptTypeparam){
		return null;
	}

	public List<String> getLines(GisFeature gisFeature,ScriptType scriptTypeparam){
		return null;
	}




}
