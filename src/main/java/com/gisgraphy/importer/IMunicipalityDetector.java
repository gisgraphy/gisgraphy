package com.gisgraphy.importer;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GISSource;

public interface IMunicipalityDetector {


	@Deprecated
	public boolean isMunicipalityByGisFeature(String countrycode, GisFeature gisFeature);
	
	public boolean isMunicipality(String countryCode,String placetype,String type,GISSource source);

}