package com.gisgraphy.domain.valueobject;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.helper.URLUtils;

public class GisFeatureDistanceFactory {
    
    protected static final Logger logger = LoggerFactory
	    .getLogger(GisFeatureDistance.class);
    
    public GisFeatureDistance fromGisFeature(GisFeature gisFeature, Double distance) {
	GisFeatureDistance gisFeatureDistance = new GisFeatureDistance();
	gisFeatureDistance.setDistance(distance) ;
	if (gisFeature != null) {
		
	    gisFeatureDistance.setId(gisFeature.getId());
	    gisFeatureDistance.setAdm1Code(gisFeature.getAdm1Code());
	    gisFeatureDistance.setAdm2Code(gisFeature.getAdm2Code());
	    gisFeatureDistance.setAdm3Code(gisFeature.getAdm3Code());
	    gisFeatureDistance.setAdm4Code(gisFeature.getAdm4Code());

	    gisFeatureDistance.setAdm1Name(gisFeature.getAdm1Name());
	    gisFeatureDistance.setAdm2Name(gisFeature.getAdm2Name());
	    gisFeatureDistance.setAdm3Name(gisFeature.getAdm3Name());
	    gisFeatureDistance.setAdm4Name(gisFeature.getAdm4Name());

	    if (gisFeature.getAsciiName() != null) {
		gisFeatureDistance.setAsciiName(gisFeature.getAsciiName().trim());
	    }
	    if (gisFeature.getCountryCode() != null) {
		gisFeatureDistance.setCountryCode(gisFeature.getCountryCode().toUpperCase());
	    }
	    gisFeatureDistance.setElevation(gisFeature.getElevation());
	    gisFeatureDistance.setFeatureClass(gisFeature.getFeatureClass());
	    gisFeatureDistance.setFeatureCode(gisFeature.getFeatureCode());
	    gisFeatureDistance.setFeatureId(gisFeature.getFeatureId());
	    gisFeatureDistance.setGtopo30(gisFeature.getGtopo30());
	    gisFeatureDistance.setLocation(gisFeature.getLocation());
	    gisFeatureDistance.setName(gisFeature.getName().trim());
	    gisFeatureDistance.setPopulation(gisFeature.getPopulation());
	    gisFeatureDistance.setTimezone(gisFeature.getTimezone());
	    gisFeatureDistance.setOpenstreetmapId(gisFeature.getOpenstreetmapId());
	    gisFeatureDistance.setAmenity(gisFeature.getAmenity());
	    gisFeatureDistance.setZipCodes(new HashSet<String>());//TODO tests zip without zipcode
		Set<ZipCode> gisFeatureZipCodes = gisFeature.getZipCodes();
			if (gisFeatureZipCodes != null){
				for (ZipCode zipCode :gisFeatureZipCodes){
				    gisFeatureDistance.getZipCodes().add(zipCode.getCode());
			    }
			}
		gisFeatureDistance.setPlaceType(gisFeature.getClass().getSimpleName()
		    .toLowerCase());
	    updateFields(gisFeatureDistance,null);
	    }
	return gisFeatureDistance;
    }
    
    public GisFeatureDistance fromAdm(Adm adm, Double distance) {
	GisFeatureDistance gisFeatureDistance = fromGisFeature(adm, distance);
	gisFeatureDistance.setLevel(adm.getLevel());
	return gisFeatureDistance;
    }
    
    public GisFeatureDistance fromStreet(Street street, Double distance) {
	GisFeatureDistance gisFeatureDistance = fromGisFeature(street,distance);
	gisFeatureDistance.setLength(street.getLength());
	gisFeatureDistance.setOneWay(street.isOneWay());
	gisFeatureDistance.setStreetType(street.getStreetType());
	gisFeatureDistance.setIsIn(street.getIsIn());
	gisFeatureDistance.setIsInAdm(street.getIsInAdm());
	if (street.getIsInZip()!=null && street.getIsInZip().size()>=1){
		//we took the first one
		gisFeatureDistance.setIsInZip(street.getIsInZip().iterator().next());
	}
	gisFeatureDistance.setIsInPlace(street.getIsInPlace());
	gisFeatureDistance.setFullyQualifiedAddress(street.getFullyQualifiedAddress());
	return gisFeatureDistance;
    }
    
    public GisFeatureDistance fromCountry(Country country, Double distance) {
	GisFeatureDistance gisFeatureDistance = fromGisFeature(country, distance);
	gisFeatureDistance.setElevation(country.getElevation());
	gisFeatureDistance.setFeatureClass(country.getFeatureClass());
	gisFeatureDistance.setFeatureCode(country.getFeatureCode());
	gisFeatureDistance.setFeatureId(country.getFeatureId());
	gisFeatureDistance.setGtopo30(country.getGtopo30());
	gisFeatureDistance.setLocation(country.getLocation());
	gisFeatureDistance.setName(country.getName().trim());
	gisFeatureDistance.setPopulation(country.getPopulation());
	gisFeatureDistance.setTimezone(country.getTimezone());
	gisFeatureDistance.setArea(country.getArea());
	gisFeatureDistance.setTld(country.getTld());
	gisFeatureDistance.setCapitalName(country.getCapitalName());
	gisFeatureDistance.setContinent(country.getContinent());
	gisFeatureDistance.setPostalCodeMask(country.getPostalCodeMask());
	gisFeatureDistance.setPostalCodeRegex(country.getPostalCodeRegex());
	gisFeatureDistance.setCurrencyCode(country.getCurrencyCode());
	gisFeatureDistance.setCurrencyName(country.getCurrencyName());
	gisFeatureDistance.setEquivalentFipsCode(country.getEquivalentFipsCode());
	gisFeatureDistance.setFipsCode(country.getFipsCode());
	gisFeatureDistance.setIso3166Alpha2Code(country.getIso3166Alpha2Code());
	gisFeatureDistance.setIso3166Alpha3Code(country.getIso3166Alpha3Code());
	gisFeatureDistance.setIso3166NumericCode(country.getIso3166NumericCode());
	gisFeatureDistance.setPhonePrefix(country.getPhonePrefix());
	return gisFeatureDistance;
    }
    
    /**
     * update the calculated fields (GoogleMapUrl,YahooMapURL,CountryFlag,...)
     * 
     */
    public  void  updateFields(GisFeatureDistance gisFeatureDistance,Class clazz) {
   	gisFeatureDistance.setOpenstreetmap_map_url(URLUtils.createOpenstreetmapMapUrl(gisFeatureDistance.getLocation()));
	gisFeatureDistance.setGoogle_map_url(URLUtils.createGoogleMapUrl(gisFeatureDistance.getLocation()));
	gisFeatureDistance.setYahoo_map_url(URLUtils.createYahooMapUrl(gisFeatureDistance.getLocation()));
	gisFeatureDistance.setCountry_flag_url(URLUtils.createCountryFlagUrl(gisFeatureDistance.getCountryCode()));
	if (gisFeatureDistance.getLocation() != null) {
	    gisFeatureDistance.setLat(gisFeatureDistance.getLocation().getY());
	    gisFeatureDistance.setLng(gisFeatureDistance.getLocation().getX());
	}
	if (gisFeatureDistance.getFeatureClass() != null && gisFeatureDistance.getFeatureCode() != null) {
	    try {
		gisFeatureDistance.setPlaceType(FeatureCode.valueOf(
			gisFeatureDistance.getFeatureClass() + "_" + gisFeatureDistance.getFeatureCode()).getObject()
			.getClass().getSimpleName().toLowerCase());
	    } catch (RuntimeException e) {
		logger.warn("no feature code for "+gisFeatureDistance.getFeatureClass() + "_" + gisFeatureDistance.getFeatureCode());
	    }
	} else if (clazz!=null){
		gisFeatureDistance.setPlaceType(clazz.getSimpleName().toLowerCase());
	}
    }

}
