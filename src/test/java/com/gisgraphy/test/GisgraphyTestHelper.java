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
/**
 *
 */
package com.gisgraphy.test;

import static com.gisgraphy.domain.valueobject.HouseNumberType.ASSOCIATED;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.domain.valueobject.GisFeatureDistanceFactory;
import com.gisgraphy.domain.valueobject.StreetDistance;
import com.gisgraphy.domain.valueobject.StreetDistance.StreetDistanceBuilder;
import com.gisgraphy.domain.valueobject.StreetSearchResultsDto;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.geoloc.GeolocQuery;
import com.gisgraphy.geoloc.GeolocResultsDto;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.servlet.FulltextServlet;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.gisgraphy.street.HouseNumberDto;
import com.gisgraphy.street.StreetSearchQuery;
import com.gisgraphy.street.StreetType;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class GisgraphyTestHelper {

    protected static Log logger = LogFactory.getLog(GisgraphyTestHelper.class);

    @Resource
    private ICityDao cityDao;
    @Resource
    private IAdmDao admDao;
    @Resource
    private ICountryDao countryDao;
    
    public static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
		}
	}
    
    

    public static boolean isFileContains(File file, String text) {
	if (file == null) {
	    throw new IllegalArgumentException("can not check a null file");
	}
	if (!file.exists()) {
	    throw new IllegalArgumentException("can not check a file that does not exists");
	}
	if (!file.isFile()) {
	    throw new IllegalArgumentException("can only check file, not directory");
	}
	FileInputStream fstream = null;
	DataInputStream in = null;
	try {
	    fstream = new FileInputStream(file);
	    in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    // Read File Line By Line
	    while ((strLine = br.readLine()) != null) {
		if (strLine.contains(text)){
		    return true;
		}
	    }
	} catch (Exception e) {// Catch exception if any
	    throw new IllegalArgumentException("an exception has occured durind the assertion of " + text + " in " + file.getAbsolutePath());
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		}
	    }
	    if (fstream != null) {
		try {
		    fstream.close();
		} catch (IOException e) {
		}
	    }
	}
	return false;
    }
    
    public static GisFeatureDistance createFullFilledGisFeatureDistanceForCityWithFactory() {
	City city = new City();
	city.setAdm1Code("A1");
	city.setAdm2Code("B2");
	city.setAdm3Code("C3");
	city.setAdm4Code("D4");

	city.setAdm1Name("adm1 name");
	city.setAdm2Name("adm2 name");
	city.setAdm3Name("adm3 name");
	city.setAdm4Name("adm4 name");

	city.setAsciiName("ascii");
	city.setCountryCode("FR");
	city.setElevation(3);
	city.setFeatureClass("P");
	city.setFeatureCode("PPL");
	city.setFeatureId(1000L);
	city.setGtopo30(30);
	city.setLocation(createPoint(2F, 4F));
	city.setName("a name");
	city.setPopulation(1000000);
	city.setSource(GISSource.PERSONAL);
	city.setTimezone("gmt+1");
	city.addZipCode(new ZipCode("3456"));
	city.addZipCode(new ZipCode("3457"));
	GisFeatureDistanceFactory factory = new GisFeatureDistanceFactory();
	return factory.fromGisFeature(city, 3.6D);

    }

 public static GisFeatureDistance createFullFilledGisFeatureDistanceWithFactory() {
	GisFeature gisFeature = new GisFeature();
	gisFeature.setAdm1Code("A1");
	gisFeature.setAdm2Code("B2");
	gisFeature.setAdm3Code("C3");
	gisFeature.setAdm4Code("D4");

	gisFeature.setAdm1Name("adm1 name");
	gisFeature.setAdm2Name("adm2 name");
	gisFeature.setAdm3Name("adm3 name");
	gisFeature.setAdm4Name("adm4 name");

	gisFeature.setAsciiName("ascii");
	gisFeature.setCountryCode("FR");
	gisFeature.setElevation(3);
	gisFeature.setFeatureClass("P");
	gisFeature.setFeatureCode("PPL");
	gisFeature.setFeatureId(1002360L);
	gisFeature.setGtopo30(30);
	gisFeature.setLocation(createPoint(2.3F, 4.5F));
	gisFeature.setName("a name");
	gisFeature.setPopulation(1000000);
	gisFeature.setSource(GISSource.PERSONAL);
	gisFeature.setTimezone("gmt+1");
	gisFeature.addZipCode(new ZipCode("75000"));

	GisFeatureDistanceFactory factory = new GisFeatureDistanceFactory();
	return factory.fromGisFeature(gisFeature, 3.6D);

    }
    
    public static GisFeatureDistance createFullFilledGisFeatureDistanceForAdmWithFactory() {
	Adm adm = new Adm(2);
	adm.setAdm1Code("A1");
	adm.setAdm2Code("B2");
	adm.setAdm3Code("C3");
	adm.setAdm4Code("D4");

	adm.setAdm1Name("adm1 name");
	adm.setAdm2Name("adm2 name");
	adm.setAdm3Name("adm3 name");
	adm.setAdm4Name("adm4 name");

	adm.setAsciiName("ascii");
	adm.setCountryCode("FR");
	adm.setElevation(3);
	adm.setFeatureClass("P");
	adm.setFeatureCode("PPL");
	adm.setFeatureId(1000L);
	adm.setGtopo30(30);
	adm.setLocation(createPoint(2F, 4F));
	adm.setName("a name");
	adm.setPopulation(1000000);
	adm.setSource(GISSource.PERSONAL);
	adm.setTimezone("gmt+1");

	GisFeatureDistanceFactory factory = new GisFeatureDistanceFactory();
	return factory.fromAdm(adm, 3.6D);

    }
    
    public static GisFeatureDistance createFullFilledGisFeatureDistanceForStreetWithFactory() {
	Street street = new Street();
	street.setAdm1Code("A1");
	street.setAdm2Code("B2");
	street.setAdm3Code("C3");
	street.setAdm4Code("D4");

	street.setAdm1Name("adm1 name");
	street.setAdm2Name("adm2 name");
	street.setAdm3Name("adm3 name");
	street.setAdm4Name("adm4 name");

	street.setAsciiName("ascii");
	street.setCountryCode("FR");
	street.setElevation(3);
	street.setFeatureClass("P");
	street.setFeatureCode("PPL");
	street.setFeatureId(1000L);
	street.setGtopo30(30);
	street.setLocation(createPoint(2F, 4F));
	street.setName("a name");
	street.setPopulation(1000000);
	street.setSource(GISSource.PERSONAL);
	street.setTimezone("gmt+1");
	street.setIsIn("los angeles");
	street.setIsInPlace("los angeles quarter");
	Set<String> zips = new HashSet<String>();
	zips.add("zip LA");
	zips.add("zip LA2");
	street.setIsInZip(zips);
	street.setIsInAdm("los angeles ADM");
	street.setFullyQualifiedAddress("los angeles FQA");
	
	street.setOneWay(true);
	street.setLength(3.56D);
	street.setStreetType(StreetType.SERVICE);
	street.setOpenstreetmapId(982365L);

	GisFeatureDistanceFactory factory = new GisFeatureDistanceFactory();
	return factory.fromStreet(street, 3.6D);

    }
    
    public static GisFeatureDistance createFullFilledGisFeatureDistanceForCitySubdivisionWithFactory() {
	CitySubdivision citySubdivision = createCitySubdivision();
	GisFeatureDistanceFactory factory = new GisFeatureDistanceFactory();
	return factory.fromGisFeature(citySubdivision, 3.6D);

    }
    

    public static GisFeatureDistance createFullFilledGisFeatureDistanceWithGisFeatureConstructor() {
	GisFeatureDistance gisFeatureDistance = new GisFeatureDistance();
	gisFeatureDistance.setAdm1Code("A1");
	gisFeatureDistance.setAdm2Code("B2");
	gisFeatureDistance.setAdm3Code("C3");
	gisFeatureDistance.setAdm4Code("D4");

	gisFeatureDistance.setAdm1Name("adm1 name");
	gisFeatureDistance.setAdm2Name("adm2 name");
	gisFeatureDistance.setAdm3Name("adm3 name");
	gisFeatureDistance.setAdm4Name("adm4 name");

	gisFeatureDistance.setAsciiName("ascii");
	gisFeatureDistance.setCountryCode("FR");
	gisFeatureDistance.setElevation(3);
	gisFeatureDistance.setFeatureClass("P");
	gisFeatureDistance.setFeatureCode("PPL");
	gisFeatureDistance.setFeatureId(1002360L);
	gisFeatureDistance.setGtopo30(30);
	gisFeatureDistance.setLocation(createPoint(2.3F, 4.5F));
	gisFeatureDistance.setName("a name");
	gisFeatureDistance.setPopulation(1000000);
	gisFeatureDistance.setTimezone("gmt+1");
	gisFeatureDistance.addZipCode("75000");
	gisFeatureDistance.setDistance(3.6D);
	return gisFeatureDistance;

    }
    
    public static GeolocResultsDto createGeolocResultsDto(Long Time) {
	GisFeatureDistance gisFeatureDistance = GisgraphyTestHelper.createFullFilledGisFeatureDistanceWithFactory();
	List<GisFeatureDistance> list = new ArrayList<GisFeatureDistance>();
	list.add(gisFeatureDistance);
	return new GeolocResultsDto(list, 300L);
    }
    


	public static CitySubdivision createCitySubdivision() {
		CitySubdivision citySubdivision = new CitySubdivision();
		citySubdivision.setAdm1Code("A1");
		citySubdivision.setAdm2Code("B2");
		citySubdivision.setAdm3Code("C3");
		citySubdivision.setAdm4Code("D4");

		citySubdivision.setAdm1Name("adm1 name");
		citySubdivision.setAdm2Name("adm2 name");
		citySubdivision.setAdm3Name("adm3 name");
		citySubdivision.setAdm4Name("adm4 name");

		citySubdivision.setAsciiName("ascii");
		citySubdivision.setCountryCode("FR");
		citySubdivision.setElevation(3);
		citySubdivision.setFeatureClass("P");
		citySubdivision.setFeatureCode("PPL");
		citySubdivision.setFeatureId(1000L);
		citySubdivision.setGtopo30(30);
		citySubdivision.setLocation(createPoint(2F, 4F));
		citySubdivision.setName("a name");
		citySubdivision.setPopulation(1000000);
		citySubdivision.setSource(GISSource.PERSONAL);
		citySubdivision.setTimezone("gmt+1");
		citySubdivision.addZipCode(new ZipCode("3456"));
		citySubdivision.addZipCode(new ZipCode("7890"));
		return citySubdivision;
	}
    
    public static Country createFullFilledCountry() {
	Country country = createCountryForFrance();
	country.setAdm1Code("A1");
	country.setAdm2Code("B2");
	country.setAdm3Code("C3");
	country.setAdm4Code("D4");

	country.setAdm1Name("adm1 name");
	country.setAdm2Name("adm2 name");
	country.setAdm3Name("adm3 name");
	country.setAdm4Name("adm4 name");

	country.setAsciiName("ascii");
	country.setCountryCode("FR");
	country.setElevation(3);
	country.setFeatureClass("P");
	country.setFeatureCode("PPL");
	country.setFeatureId(1000L);
	country.setGtopo30(30);
	country.setLocation(createPoint(2F, 4F));
	country.setName("a name");
	country.setPopulation(1000000);
	country.setSource(GISSource.PERSONAL);
	country.setTimezone("gmt+1");
	country.setArea(123456D);
	country.setTld(".fr");
	country.setCapitalName("paris");
	country.setContinent("Europe");
	country.setPostalCodeMask("postalCodeMask");
	country.setPostalCodeRegex("postalCodeRegex");
	country.setCurrencyCode("currencyCode");
	country.setCurrencyName("currencyName");
	country.setEquivalentFipsCode("equivalentFipsCode");
	country.setFipsCode("fipsCode");
	country.setIso3166Alpha2Code("isoA2Code");
	country.setIso3166Alpha3Code("isoA3Code");
	country.setIso3166NumericCode(33);
	country.setPhonePrefix("+33");
	country.setPostalCodeMask("postalCodeMask");
	return country;

    }

    
    /**
     * Note : if there is more than one parameter with the same name, The last
     * one will be put in the map
     * 
     * @param completeURL
     *                the URL to split
     * @param andSign
     *                the string representing and sign ('&' or '&amp;')
     * @return an hashmap<paramName, paramValue> for the URL parameter
     */
    public static HashMap<String, List<String>> splitURLParams(String completeURL,
	    String andSign) {
	int i;
	HashMap<String,  List<String>> searchparms = new HashMap<String,  List<String>>();
	;
	logger.debug("Complete URL: " + completeURL);
	i = completeURL.indexOf("?");
	if (i > -1) {
	    String searchURL = completeURL
		    .substring(completeURL.indexOf("?") + 1);
	    logger.debug("Search URL: " + searchURL);

	    String[] paramArray = searchURL.split(andSign);
	    for (int c = 0; c < paramArray.length; c++) {
		String[] paramSplited = paramArray[c].split("=");
		try {
			if (!searchparms.containsKey(paramSplited[0])){
				searchparms.put(paramSplited[0], new ArrayList<String>());
			}
		    searchparms.get(paramSplited[0]).add(java.net.URLDecoder
			    .decode(paramSplited[1], Constants.CHARSET));
		} catch (UnsupportedEncodingException e) {
		    return new HashMap<String, List<String>>();
		}

	    }
	    // dumpHashtable;
	    java.util.Iterator<String> keys = searchparms.keySet().iterator();
	    logger.debug("--------");
	    while (keys.hasNext()) {
		String s = (String) keys.next();
		logger.debug(s + " : " + searchparms.get(s));
	    }
	    logger.debug("--------");

	}
	return searchparms;
    }

    /**
     * @param featureId
     *                the featureId of the city to save
     * @return a city with full Collection and dependant objects
     */
    public City createAndSaveCityWithFullAdmTreeAndCountry(Long featureId) {
	String adm1Code = "A1";
	String adm1Name = "admGrandGrandParent";
	String adm2Code = "B2";
	String adm2Name = "admGrandParent";
	String adm3Code = "C3";
	String adm3Name = "admParent";
	City gisFeature = GisgraphyTestHelper.createCity("Saint-André", 1.5F, 2.5F,
		featureId);
	// the admXcodes and admXnames should be set by the importer according
	// to the sync option
	gisFeature.setAdm1Code(adm1Code);
	gisFeature.setAdm1Name(adm1Name);
	gisFeature.setAdm2Code(adm2Code);
	gisFeature.setAdm2Name(adm2Name);
	gisFeature.setAdm3Code(adm3Code);
	gisFeature.setAdm3Name(adm3Name);
	// create Adms
	Adm admGrandGrandParent = GisgraphyTestHelper.createAdm(adm1Name, "FR",
		adm1Code, null, null, null, null, 1);
	Adm admGrandParent = GisgraphyTestHelper.createAdm(adm2Name, "FR",
		adm1Code, adm2Code, null, null, null, 2);
	Adm admParent = GisgraphyTestHelper.createAdm(adm3Name, "FR", adm1Code,
		adm2Code, adm3Code, null, null, 3);

	Country france = GisgraphyTestHelper.createCountryForFrance();

	AlternateName countryAlternate = new AlternateName();
	countryAlternate.setName("francia");
	// countryAlternate.setGisFeature(admGrandGrandParent);
	countryAlternate.setSource(AlternateNameSource.ALTERNATENAMES_FILE);

	AlternateName countryAlternateFR = new AlternateName();
	countryAlternateFR.setName("franciaFR");
	// countryAlternate.setGisFeature(admGrandGrandParent);
	countryAlternateFR.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	countryAlternateFR.setLanguage("FR");

	france.addAlternateName(countryAlternate);
	france.addAlternateName(countryAlternateFR);

	this.countryDao.save(france);

	AlternateName alternateNameGGPFR = new AlternateName();
	alternateNameGGPFR.setName("admGGPalternateFR");
	alternateNameGGPFR.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	alternateNameGGPFR.setLanguage("FR");
	AlternateName alternateNameGGP = new AlternateName();
	alternateNameGGP.setName("admGGPalternate");
	alternateNameGGP.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	AlternateName alternateNameGGP2 = new AlternateName();
	alternateNameGGP2.setName("admGGPalternate2");
	alternateNameGGP2.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	admGrandGrandParent.addAlternateName(alternateNameGGP);
	admGrandGrandParent.addAlternateName(alternateNameGGPFR);
	admGrandGrandParent.addAlternateName(alternateNameGGP2);
	Adm ggp = this.admDao.save(admGrandGrandParent);

	AlternateName alternateNameGPFR = new AlternateName();
	alternateNameGPFR.setName("admGPalternateFR");
	alternateNameGPFR.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	alternateNameGPFR.setLanguage("FR");
	AlternateName alternateNameGP = new AlternateName();
	alternateNameGP.setName("admGPalternate");
	alternateNameGP.setSource(AlternateNameSource.ALTERNATENAMES_FILE);

	admGrandParent.addAlternateName(alternateNameGP);
	admGrandParent.addAlternateName(alternateNameGPFR);
	admGrandParent.setParent(ggp);
	Adm gp = this.admDao.save(admGrandParent);

	AlternateName alternateNamePFR = new AlternateName();
	alternateNamePFR.setName("admPAlternateFR");
	alternateNamePFR.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	alternateNamePFR.setLanguage("FR");

	AlternateName alternateNameP = new AlternateName();
	alternateNameP.setName("admPAlternate");
	alternateNameP.setSource(AlternateNameSource.ALTERNATENAMES_FILE);

	admParent.addAlternateName(alternateNameP);
	admParent.addAlternateName(alternateNamePFR);
	admParent.setParent(gp);
	Adm parent = this.admDao.save(admParent);

	AlternateName alternateNamecityFR = new AlternateName();
	alternateNamecityFR.setName("cityalternateFR");
	alternateNamecityFR.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	alternateNamecityFR.setLanguage("FR");
	AlternateName alternateNamecity = new AlternateName();
	alternateNamecity.setName("cityalternate");
	alternateNamecity.setSource(AlternateNameSource.ALTERNATENAMES_FILE);

	gisFeature.addAlternateName(alternateNamecity);
	gisFeature.addAlternateName(alternateNamecityFR);
	City paris = new City(gisFeature);
	paris.addZipCode(new ZipCode("50263"));
	
	paris.setAsciiName("ascii");
	paris.setAmenity("amenity");
	paris.setFeatureClass("P");
	paris.setFeatureCode("PPL");
	paris.setElevation(13456);
	paris.setGtopo30(7654);
	paris.setTimezone("Europe/Paris");
	paris.setAmenity("amenity");
	paris.setMunicipality(true);

	paris.setAdm(parent);
	this.cityDao.save(paris);
	return paris;
    }

    public static String readFileAsString(String filePath)
	    throws java.io.IOException {
	StringBuffer fileData = new StringBuffer(1000);
	BufferedReader reader = new BufferedReader(new FileReader(filePath));
	char[] buf = new char[1024];
	int numRead = 0;
	while ((numRead = reader.read(buf)) != -1) {
	    String readData = String.valueOf(buf, 0, numRead);
	    fileData.append(readData);
	    buf = new char[1024];
	}
	reader.close();
	return fileData.toString();
    }

    public static boolean DeleteNonEmptyDirectory(File path) {
	if (path.exists()) {
	    File[] files = path.listFiles();
	    for (int i = 0; i < files.length; i++) {
		if (files[i].isDirectory()) {
		    DeleteNonEmptyDirectory(files[i]);
		} else {
		    files[i].delete();
		}
	    }
	}
	return (path.delete());
    }

    public static final int DISTANCE_PURCENT_ERROR_ACCEPTED = 1;

    
    
    
    public static OpenStreetMap createOpenStreetMapForJohnKenedyStreet() {
	OpenStreetMap streetOSM = new OpenStreetMap();
	LineString shape2 = GeolocHelper.createLineString("LINESTRING (30 30, 40 40)");
	streetOSM.setShape(shape2);
	streetOSM.setGid(2L);
	//Simulate middle point
	streetOSM.setLocation(GeolocHelper.createPoint(30.11F, 30.11F));
	streetOSM.setOneWay(true);
	streetOSM.setStreetType(StreetType.MOTORWAY);
	streetOSM.setName("John Kénedy");
	streetOSM.setOpenstreetmapId(12345L);
	streetOSM.setIsIn("los angeles");
	streetOSM.setIsInPlace("los angeles quater");
	streetOSM.setIsInAdm("adm LA");
	Set<String> zips = new HashSet<String>();
	zips.add("zip LA");
	zips.add("zip LA2");
	streetOSM.setIsInZip(zips);
	streetOSM.setFullyQualifiedAddress("fullyqulified address LA");
	streetOSM.setCountryCode("XX");
	return StringHelper.updateOpenStreetMapEntityForIndexation(streetOSM);

    }
    
    public static OpenStreetMap createOpenStreetMapForPeterMartinStreet() {
    	OpenStreetMap streetOSM = new OpenStreetMap();
    	LineString shape = GeolocHelper.createLineString("LINESTRING (30.001 30.001, 40 40)");
    	streetOSM.setShape(shape);
    	streetOSM.setGid(1L);
    	streetOSM.setOneWay(false);
    	streetOSM.setStreetType(StreetType.FOOTWAY);
    	streetOSM.setIsIn("chicago");
    	streetOSM.setIsInPlace("foo quater");
    	Set<String> zips = new HashSet<String>();
    	zips.add("zip PM");
    	zips.add("zip PM2");
    	streetOSM.setIsInZip(zips);
    	streetOSM.setIsInAdm("adm PM");
    	streetOSM.setFullyQualifiedAddress("fullyQualifiedAddress");
    	streetOSM.setName("peter martin");
    	streetOSM.setOpenstreetmapId(12346L);
    	streetOSM.setLocation(GeolocHelper.createPoint(30.001F, 40F));
    	streetOSM.setCountryCode("XX");
        return StringHelper.updateOpenStreetMapEntityForIndexation(streetOSM);
    	
        }
    
    public static StreetDistance createStreetDistance() {
	return StreetDistanceBuilder.streetDistance().withName("streetName").withCountryCode("FR").withGid(123L).withLength(3.6D).withOneWay(true)
	.withStreetType(StreetType.MOTORWAY).withLocation(GeolocHelper.createPoint(25.2F, 54.5F)).withDistance(43.5D).withCountryCode("fr").build();

    }
    
    public static StreetSearchResultsDto createStreetSearchResultsDto() {
	List<StreetDistance> list = new ArrayList<StreetDistance>();
	list.add(createStreetDistance());
	return new StreetSearchResultsDto(list,1L,"query");
    }


    public static Country createCountryForFrance() {
	Country country = new Country("FR", "FRA", 33);
	country.setFeatureId(Math.abs(new Random().nextLong()));
	country.setFeatureClass("A");
	country.setFeatureCode("PCL");
	country.setLocation(createPoint(3F, 4F));
	country.setName("France");
	country.setSource(GISSource.GEONAMES);
	return country;
    }

    public static Adm createAdm(String name, String countryCode,
	    String adm1Code, String adm2Code, String adm3Code, String adm4Code,
	    GisFeature gisFeature, Integer level) {
	Adm adm = new Adm(level);
	if (gisFeature != null) {
	    adm.populate(gisFeature);
	}
	adm.setName(name);
	adm.setLocation(createPoint(10F, 20F));
	adm.setSource(GISSource.GEONAMES);
	adm.setCountryCode(countryCode);
	adm.setAdm1Code(adm1Code);
	adm.setAdm2Code(adm2Code);
	adm.setAdm3Code(adm3Code);
	adm.setAdm4Code(adm4Code);
	if (gisFeature == null) {
	    adm.setFeatureId(Math.abs(new Random().nextLong()));
	} else {
	    adm.setFeatureId(gisFeature.getFeatureId());
	}
	return adm;
    }

    public static List<Adm> createAdms(String name, String countryCode,
	    String adm1Code, String adm2Code, String adm3Code, String adm4Code,
	    GisFeature gisFeature, Integer level, int nbToCreate) {
	List<Adm> adms = new ArrayList<Adm>();
	String adm1Codetemp = "";
	String adm2Codetemp = "";
	String adm3Codetemp = "";
	String adm4Codetemp = "";
	for (int i = 0; i < nbToCreate; i++) {
	    // we chenge the admcode according to the level to be realist
	    adm1Codetemp = adm1Code;
	    adm2Codetemp = adm2Code;
	    adm3Codetemp = adm3Code;
	    adm4Codetemp = adm4Code;
	    if (level == 1) {
		adm1Codetemp = adm1Code + i;
	    } else if (level == 2) {
		adm2Codetemp = adm2Code + i;
	    } else if (level == 2) {
		adm3Codetemp = adm3Code + i;
	    } else if (level == 4) {
		adm4Codetemp = adm4Code + i;
	    }
	    adms
		    .add(createAdm(name + i, countryCode, adm1Codetemp,
			    adm2Codetemp, adm3Codetemp, adm4Codetemp,
			    gisFeature, level));
	}
	return adms;
    }

    public static GisFeature createGisFeature(String asciiName,
	    Float longitude, Float latitude, Long featureId) {

	GisFeature gisFeature = new GisFeature();
	gisFeature.setAsciiName(asciiName);
	gisFeature.setCountryCode("FR");
	gisFeature.setElevation(10);

	if (featureId == null) {
	    gisFeature.setFeatureId(Math.abs(new Random().nextLong()));// use
	    // abs
	    // to
	    // have
	    // positive
	    // featureId
	} else {
	    gisFeature.setFeatureId(featureId);
	}
	gisFeature.setGtopo30(30);
	if (longitude == null || latitude == null) {
	    gisFeature.setLocation(createPoint(80F, 90F));
	} else {
	    gisFeature.setLocation(createPoint(longitude, latitude));
	}
	DateTime date = new DateTime().withYear(1978);
	gisFeature.setModificationDate(date.toDate());
	gisFeature.setName(asciiName);
	gisFeature.setPopulation(10000000);
	gisFeature.setSource(GISSource.GEONAMES);
	gisFeature.setTimezone("Europe/paris");

	return gisFeature;
	// double set
	// gisFeature.setAlternateNames(alternateNames);
    }

    public static City createCity(String asciiName, Float longitude,
	    Float latitude, Long featureId) {
	GisFeature gisFeature = createGisFeature(asciiName, longitude,
		latitude, featureId);

	City city = new City(gisFeature);
	city.setFeatureClass("P");
	city.setFeatureCode("PPL");
	city.addZipCode(new ZipCode("75000"));
	return city;
    }

    public static GisFeature createGisFeatureForAdm(String asciiName,
	    Float longitude, Float latitude, Long featureId, Integer level) {
	GisFeature gisFeature = createGisFeature(asciiName, longitude,
		latitude, featureId);
	gisFeature.setFeatureClass("A");
	gisFeature.setFeatureCode("ADM" + level);
	return gisFeature;
    }

    public static Point createPoint(Float longitude, Float latitude) {
	return GeolocHelper.createPoint(longitude, latitude);
    }

    public static City createCityAtSpecificPoint(String asciiName,
	    Float Longitude, Float latitude) {
	GisFeature gisFeature = createCity(asciiName, Longitude, latitude, null);
	City city = createCity(gisFeature);
	return city;

    }

    public static Set<AlternateName> createAlternateNames(int nombres,
	    GisFeature gisFeature) {
	Set<AlternateName> alternateNames = new HashSet<AlternateName>();
	for (int i = 0; i < nombres; i++) {
	    AlternateName alternateName = new AlternateName();
	    alternateName.setName("lutece"+i);
	    alternateName.setGisFeature(gisFeature);
	    alternateName.setSource(AlternateNameSource.ALTERNATENAMES_FILE);
	    alternateNames.add(alternateName);
	}
	return alternateNames;
    }

    public static City createCity(GisFeature gisFeature) {
	City city = new City(gisFeature);
	return city;
    }

    public static City createCityWithAlternateNames(String asciiName,
	    int nbAlternateNames) {
	City city = createCityAtSpecificPoint(asciiName, null, null);

	if (nbAlternateNames > 0) {
	    Set<AlternateName> alternateNames = createAlternateNames(
		    nbAlternateNames, city);
	    city.setAlternateNames(alternateNames);
	}
	// City city = createCity(gisFeature);
	return city;

    }

    public static GisFeature createGisFeatureWithAlternateNames(
	    String asciiName, int nbAlternateNames) {
	GisFeature gisFeature = createGisFeature(asciiName, null, null, null);

	if (nbAlternateNames > 0) {
	    Set<AlternateName> alternateNames = createAlternateNames(
		    nbAlternateNames, gisFeature);
	    gisFeature.setAlternateNames(alternateNames);
	}
	return gisFeature;
    }

    public static MockHttpServletRequest createMockHttpServletRequestForFullText() {
	MockHttpServletRequest request = new MockHttpServletRequest();
	request.addParameter(FulltextQuery.COUNTRY_PARAMETER, "FR");
	request.addParameter(FulltextServlet.FROM_PARAMETER, "3");
	request.addParameter(FulltextServlet.TO_PARAMETER, FulltextQuery.DEFAULT_MAX_RESULTS+20+"");
	request.addParameter(FulltextServlet.FORMAT_PARAMETER, "XML");
	request.addParameter(FulltextQuery.STYLE_PARAMETER, "FULL");
	request.addParameter(FulltextQuery.LANG_PARAMETER, "fr");
	request.addParameter(GisgraphyServlet.INDENT_PARAMETER, "XML");
	request.addParameter(FulltextQuery.PLACETYPE_PARAMETER, "city");
	request.addParameter(FulltextQuery.QUERY_PARAMETER, "query");
	request.addParameter(FulltextQuery.SPELLCHECKING_PARAMETER, "true");
	return request;
    }

    public static MockHttpServletRequest createMockHttpServletRequestForGeoloc() {
	MockHttpServletRequest request = new MockHttpServletRequest();
	request.addParameter(GisgraphyServlet.FROM_PARAMETER, "3");
	request.addParameter(GisgraphyServlet.TO_PARAMETER, GeolocQuery.DEFAULT_MAX_RESULTS+20+"");
	request.addParameter(GisgraphyServlet.FORMAT_PARAMETER, "XML");
	request.addParameter(GeolocQuery.PLACETYPE_PARAMETER, "city");
	request.addParameter(GeolocQuery.LAT_PARAMETER, "1.0");
	request.addParameter(GeolocQuery.LONG_PARAMETER, "2.0");
	request.addParameter(GeolocQuery.LONG_PARAMETER, "3.0");
	return request;
    }
    
    public static MockHttpServletRequest createMockHttpServletRequestForReverseGeocoding() {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addParameter(GisgraphyServlet.FORMAT_PARAMETER, "XML");
    	request.addParameter(GeolocQuery.LAT_PARAMETER, "1.0");
    	request.addParameter(GeolocQuery.LONG_PARAMETER, "2.0");
    	return request;
        }
    
    public static MockHttpServletRequest createMockHttpServletRequestForStreetGeoloc() {
	MockHttpServletRequest request = new MockHttpServletRequest();
	request.addParameter(GisgraphyServlet.FROM_PARAMETER, "3");
	request.addParameter(GisgraphyServlet.TO_PARAMETER, StreetSearchQuery.DEFAULT_MAX_RESULTS+10+"");
	request.addParameter(GisgraphyServlet.FORMAT_PARAMETER, "XML");
	request.addParameter(GeolocQuery.PLACETYPE_PARAMETER, "city");
	request.addParameter(GeolocQuery.LAT_PARAMETER, "1.0");
	request.addParameter(GeolocQuery.LONG_PARAMETER, "2.0");
	request.addParameter(GeolocQuery.LONG_PARAMETER, "3.0");
	return request;
    }
    
    public static SolrResponseDto createSolrResponseDtoForCity() {
		SolrResponseDto city = EasyMock.createMock(SolrResponseDto.class);
    	EasyMock.expect(city.getAdm1_name()).andStubReturn("adm1 Name");
    	EasyMock.expect(city.getAdm2_name()).andStubReturn("adm2 Name");
    	EasyMock.expect(city.getLat()).andStubReturn(1.55D);
    	EasyMock.expect(city.getLng()).andStubReturn(2.36D);
    	EasyMock.expect(city.getName()).andStubReturn("Name");
    	EasyMock.expect(city.getFeature_id()).andStubReturn(123L);
    	EasyMock.expect(city.getCountry_code()).andStubReturn("XX");
    	EasyMock.expect(city.getIs_in()).andStubReturn(null);
    	Set<String> zipcodes = new HashSet<String>();
    	zipcodes.add("zip1");
    	EasyMock.expect(city.getZipcodes()).andStubReturn(zipcodes);
    	EasyMock.expect(city.getPlacetype()).andStubReturn(City.class.getSimpleName());
    	Set<String> zips = new HashSet<String>();
    	zips.add("zip");
    	EasyMock.expect(city.getIs_in_zip()).andStubReturn(zips);
    	EasyMock.expect(city.getIs_in_adm()).andStubReturn("isinAdm");
    	EasyMock.expect(city.getIs_in_place()).andStubReturn("isinPlace");
    	EasyMock.replay(city);
    	return city;
	}
    
    public static SolrResponseDto createSolrResponseDtoForCitySudivision() {
		SolrResponseDto citySubdivision = EasyMock.createMock(SolrResponseDto.class);
    	EasyMock.expect(citySubdivision.getAdm1_name()).andStubReturn("adm1 Name");
    	EasyMock.expect(citySubdivision.getAdm2_name()).andStubReturn("adm2 Name");
    	EasyMock.expect(citySubdivision.getLat()).andStubReturn(1.55D);
    	EasyMock.expect(citySubdivision.getLng()).andStubReturn(2.36D);
    	EasyMock.expect(citySubdivision.getName()).andStubReturn("Name");
    	EasyMock.expect(citySubdivision.getFeature_id()).andStubReturn(123L);
    	EasyMock.expect(citySubdivision.getCountry_code()).andStubReturn("XX");
    	EasyMock.expect(citySubdivision.getIs_in()).andStubReturn(null);
    	Set<String> zipCodes = new HashSet<String>();
    	zipCodes.add("zip1");
    	EasyMock.expect(citySubdivision.getZipcodes()).andStubReturn(zipCodes);
    	EasyMock.expect(citySubdivision.getPlacetype()).andStubReturn(CitySubdivision.class.getSimpleName());
    	EasyMock.replay(citySubdivision);
    	return citySubdivision;
	}
    public static SolrResponseDto createSolrResponseDtoForAdm() {
		SolrResponseDto adm = EasyMock.createMock(SolrResponseDto.class);
    	EasyMock.expect(adm.getAdm1_name()).andStubReturn("adm1 Name");
    	EasyMock.expect(adm.getAdm2_name()).andStubReturn("adm2 Name");
    	EasyMock.expect(adm.getLat()).andStubReturn(1.55D);
    	EasyMock.expect(adm.getLng()).andStubReturn(2.36D);
    	EasyMock.expect(adm.getName()).andStubReturn("Name");
    	EasyMock.expect(adm.getFeature_id()).andStubReturn(123L);
    	EasyMock.expect(adm.getCountry_code()).andStubReturn("XX");
    	EasyMock.expect(adm.getZipcodes()).andStubReturn(null);
    	EasyMock.expect(adm.getIs_in()).andStubReturn(null);
    	EasyMock.expect(adm.getPlacetype()).andStubReturn(Adm.class.getSimpleName());
    	EasyMock.replay(adm);
    	return adm;
	}
    public static SolrResponseDto createSolrResponseDtoForGisFeature() {
		SolrResponseDto feature = EasyMock.createMock(SolrResponseDto.class);
    	EasyMock.expect(feature.getAdm1_name()).andStubReturn("adm1 Name");
    	EasyMock.expect(feature.getAdm2_name()).andStubReturn("adm2 Name");
    	EasyMock.expect(feature.getLat()).andStubReturn(1.55D);
    	EasyMock.expect(feature.getLng()).andStubReturn(2.36D);
    	EasyMock.expect(feature.getName()).andStubReturn("Name");
    	EasyMock.expect(feature.getFeature_id()).andStubReturn(123L);
    	EasyMock.expect(feature.getCountry_code()).andStubReturn("XX");
    	EasyMock.expect(feature.getZipcodes()).andStubReturn(null);
    	EasyMock.expect(feature.getIs_in()).andStubReturn(null);
    	EasyMock.expect(feature.getPlacetype()).andStubReturn(GisFeature.class.getSimpleName());
    	EasyMock.replay(feature);
    	return feature;
	}
    
    
    public static SolrResponseDto createSolrResponseDtoForCity_other() {
		SolrResponseDto city = EasyMock.createMock(SolrResponseDto.class);
    	EasyMock.expect(city.getAdm1_name()).andStubReturn("adm1 Name other");
    	EasyMock.expect(city.getAdm2_name()).andStubReturn("adm2 Name other");
    	EasyMock.expect(city.getLat()).andStubReturn(3.55D);
    	EasyMock.expect(city.getLng()).andStubReturn(4.36D);
    	EasyMock.expect(city.getName()).andStubReturn("Name other");
    	EasyMock.expect(city.getFeature_id()).andStubReturn(1234L);
    	EasyMock.expect(city.getCountry_code()).andStubReturn("XY");
    	EasyMock.expect(city.getIs_in()).andStubReturn(null);
    	Set<String> zipCodes = new HashSet<String>();
    	zipCodes.add("zip2");
    	EasyMock.expect(city.getZipcodes()).andStubReturn(zipCodes);
    	EasyMock.expect(city.getPlacetype()).andStubReturn(City.class.getSimpleName());
    	EasyMock.replay(city);
    	return city;
	}
    public static SolrResponseDto createSolrResponseDtoForCityFarFarAway() {
		SolrResponseDto city = EasyMock.createMock(SolrResponseDto.class);
    	EasyMock.expect(city.getAdm1_name()).andStubReturn("adm1 Name");
    	EasyMock.expect(city.getAdm2_name()).andStubReturn("adm2 Name");
    	EasyMock.expect(city.getLat()).andStubReturn(80.55D);
    	EasyMock.expect(city.getLng()).andStubReturn(80.36D);
    	EasyMock.expect(city.getName()).andStubReturn("Name");
    	EasyMock.expect(city.getFeature_id()).andStubReturn(123456L);
    	Set<String> zipCodes = new HashSet<String>();
    	zipCodes.add("zip1");
    	EasyMock.expect(city.getZipcodes()).andStubReturn(zipCodes);
    	EasyMock.expect(city.getPlacetype()).andStubReturn(City.class.getSimpleName());
    	EasyMock.replay(city);
    	return city;
	}
	
	public static SolrResponseDto createSolrResponseDtoForStreet(String is_in) {
		SolrResponseDto street = EasyMock.createMock(SolrResponseDto.class);
    	EasyMock.expect(street.getAdm1_name()).andStubReturn("adm1 Name");
    	EasyMock.expect(street.getAdm2_name()).andStubReturn("adm2 Name");
    	EasyMock.expect(street.getLat()).andStubReturn(1.53D);
    	EasyMock.expect(street.getLng()).andStubReturn(2.35D);
    	EasyMock.expect(street.getName()).andStubReturn("street Name");
    	EasyMock.expect(street.getIs_in()).andStubReturn(is_in);
    	EasyMock.expect(street.getZipcodes()).andStubReturn(null);
    	EasyMock.expect(street.getPlacetype()).andStubReturn(Street.class.getSimpleName());
    	EasyMock.expect(street.getFeature_id()).andStubReturn(123564L);
    	EasyMock.expect(street.getStreet_type()).andStubReturn("street type");
    	EasyMock.expect(street.getCountry_code()).andStubReturn("FR");
    	EasyMock.expect(street.getScore()).andStubReturn(1F);
    	
    	EasyMock.expect(street.getIs_in_adm()).andStubReturn("isinadm");
    	EasyMock.expect(street.getIs_in_place()).andStubReturn("isinplace");
    	Set<String> zips = new HashSet<String>();
    	zips.add("zip");
    	EasyMock.expect(street.getIs_in_zip()).andStubReturn(zips);
    	List<HouseNumberDto> houseNumbers = new ArrayList<HouseNumberDto>();
    	HouseNumberDto number1 = new HouseNumberDto(GeolocHelper.createPoint(2D, 3D), "1");
    	HouseNumberDto number2 = new HouseNumberDto(GeolocHelper.createPoint(4D, 5D), "2");
    	houseNumbers.add(number1);
    	houseNumbers.add(number2);
    	EasyMock.expect(street.getHouse_numbers()).andStubReturn(houseNumbers);
    	EasyMock.replay(street);
    	return street;
	}
	
	public static SolrResponseDto createSolrResponseDtoForStreet(String is_in,String streetName,List<HouseNumberDto> houseNumbers, Long featureId) {
		SolrResponseDto street = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(street.getFeature_id()).andStubReturn(featureId);
    	EasyMock.expect(street.getAdm1_name()).andStubReturn("adm1 Name");
    	EasyMock.expect(street.getAdm2_name()).andStubReturn("adm2 Name");
    	EasyMock.expect(street.getLat()).andStubReturn(1.53D);
    	EasyMock.expect(street.getLng()).andStubReturn(2.35D);
    	EasyMock.expect(street.getName()).andStubReturn(streetName);
    	EasyMock.expect(street.getIs_in()).andStubReturn(is_in);
    	EasyMock.expect(street.getZipcodes()).andStubReturn(null);
    	EasyMock.expect(street.getPlacetype()).andStubReturn(Street.class.getSimpleName());
    	EasyMock.expect(street.getFeature_id()).andStubReturn(123564L);
    	EasyMock.expect(street.getStreet_type()).andStubReturn("street type");
    	EasyMock.expect(street.getCountry_code()).andStubReturn("FR");
    	EasyMock.expect(street.getScore()).andStubReturn(1F);
    	
    	EasyMock.expect(street.getIs_in_adm()).andStubReturn("isinadm");
    	EasyMock.expect(street.getIs_in_place()).andStubReturn("isinplace");
    	Set<String> zips = new HashSet<String>();
    	zips.add("zip");
    	EasyMock.expect(street.getIs_in_zip()).andStubReturn(zips);
    	EasyMock.expect(street.getHouse_numbers()).andStubReturn(houseNumbers);
    	EasyMock.replay(street);
    	return street;
	}
	
	public static StreetDistance createStreetSearchDtoForStreet() {
		StreetDistance street = EasyMock.createMock(StreetDistance.class);
    	EasyMock.expect(street.getLat()).andStubReturn(1.53D);
    	EasyMock.expect(street.getLng()).andStubReturn(2.35D);
    	EasyMock.expect(street.getLocation()).andStubReturn(GeolocHelper.createPoint(2.35F, 1.53F));
    	EasyMock.expect(street.getName()).andStubReturn("street Name");
    	EasyMock.expect(street.getStreetType()).andStubReturn(StreetType.MOTORWAY);
    	EasyMock.replay(street);
    	return street;
	}

    
    public static int countLinesInFileThatStartsWith(File file, String text) {
	int count = 0;
	if (file == null) {
	    throw new IllegalArgumentException("can not check a null file");
	}
	if (!file.exists()) {
	    throw new IllegalArgumentException("can not check a file that does not exists");
	}
	if (!file.isFile()) {
	    throw new IllegalArgumentException("can only check file, not directory");
	}
	FileInputStream fstream = null;
	DataInputStream in = null;
	try {
	    fstream = new FileInputStream(file);
	    in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    // Read File Line By Line
	    while ((strLine = br.readLine()) != null) {
		if (strLine.trim().startsWith(text)){
		    count++;
		}
	    }
	} catch (Exception e) {// Catch exception if any
	    throw new IllegalArgumentException("an exception has occured durind the assertion of " + text + " in " + file.getAbsolutePath());
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		}
	    }
	    if (fstream != null) {
		try {
		    fstream.close();
		} catch (IOException e) {
		}
	    }
	}
	return count;
    }
    
    public static HouseNumber createHouseNumber(){
    	HouseNumber houseNumber = new HouseNumber();
    	houseNumber.setNumber("10");
    	houseNumber.setLocation(GeolocHelper.createPoint(3F, 4F));
    	houseNumber.setOpenstreetmapId(1L);
    	houseNumber.setType(ASSOCIATED);
    	return houseNumber;
    }
    
    public static boolean alternateNameContains(Collection<AlternateName> alternateNames,String name){
		if (alternateNames!=null){
			for (AlternateName alternateName:alternateNames){
				if (alternateName.getName().equals(name)){
					return true;
				}
			}
			
		} return false;
	}
    
    public static boolean alternateOsmNameContains(Collection<AlternateOsmName> alternateNames,String name){
		if (alternateNames!=null){
			for (AlternateOsmName alternateName:alternateNames){
				if (alternateName.getName().equals(name)){
					return true;
				}
			}
			
		} return false;
	}
    
}
