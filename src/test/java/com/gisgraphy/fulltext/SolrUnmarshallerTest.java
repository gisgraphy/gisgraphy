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
package com.gisgraphy.fulltext;

import static com.gisgraphy.domain.valueobject.Pagination.paginate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.Language;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.domain.repository.ILanguageDao;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.street.StreetType;
import com.gisgraphy.test.GisgraphyTestHelper;

public class SolrUnmarshallerTest extends AbstractIntegrationHttpSolrTestCase {

    @Resource
    private GisgraphyTestHelper geolocTestHelper;
    
    @Resource
    private ICountryDao countryDao;
    
    @Resource
    private ILanguageDao languageDao;
    
    @Resource
    private IOpenStreetMapDao openStreetMapDao;
    
    @Resource
    private IAdmDao admDao;

    
    @Test
    public void testAllfulltextfieldsShouldBeMapped(){
    	//there is some fields present in fulltextfields but stored=false,some Fulltextfields are suffix, distance is calculated in solrdto
    	//but not in ftf and the score is not mapped.. the difference beetween FulltextFields and dto is 2
    	int numFieldDifference = 2;
    	FullTextFields[] fulltextfields = 	FullTextFields.class.getEnumConstants();
    	Field[] dtoFields = SolrResponseDto.class.getDeclaredFields();
    	List<String> dtoFieldsName = new ArrayList<String>();
    	for(Field field: dtoFields){
    		dtoFieldsName.add(field.getName());
    	}
    	Assert.assertEquals("there is probably "+(fulltextfields.length-numFieldDifference - dtoFields.length)+" field added in Fulltext fields" +
    			" but that is not in solrdto",fulltextfields.length-numFieldDifference, dtoFields.length);
    	
    }
    
    
    @Test
    public void testUnmarshallSolrDocumentShouldReallyUnmarshall() {
	Long featureId = 1002L;
	City city = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(featureId);
	this.solRSynchroniser.commit();
	Pagination pagination = paginate().from(1).to(10);
	Output output = Output.withFormat(OutputFormat.XML).withLanguageCode(
		"FR").withStyle(OutputStyle.FULL).withIndentation();
	FulltextQuery query = new FulltextQuery(city.getName(), pagination,
		output, null, null);
	FulltextResultsDto response = this.fullTextSearchEngine
		.executeQuery(query);
	List<SolrResponseDto> results = response.getResults();
	assertNotNull(
		"There should have a result for a fulltextSearch for "
			+ city.getName()
			+ " and even If no results are return: an empty list should be return,  not null ",
		results);
	assertTrue("There should have a result for a fulltextSearch for "
		+ city.getName(), results.size() == 1);
	SolrResponseDto result = results.get(0);
	assertEquals(city.getName(), result.getName());
	assertNotNull(result.getScore());
	assertEquals(city.getCountryCode(), result.getCountry_code());
	assertEquals(city.getFeatureId(), result.getFeature_id());
	assertEquals(city.getFeatureClass(), result.getFeature_class());
	assertEquals(city.getFeatureCode(), result.getFeature_code());
	assertEquals(city.getAsciiName(), result.getName_ascii());
	assertEquals(city.getElevation(), result.getElevation());
	assertEquals(city.getGtopo30(), result.getGtopo30());
	assertEquals(city.getTimezone(), result.getTimezone());
	//since v 4.0 we don't process calculated fields
	assertEquals(null, result
		.getFully_qualified_name());
	assertEquals(city.getClass().getSimpleName(), result.getPlacetype());
	assertEquals(city.getPopulation(), result.getPopulation());
	assertEquals(city.isMunicipality(), result.isMunicipality());
	assertEquals(city.getAmenity(), result.getAmenity());
	assertEquals(city.getLatitude(), result.getLat());
	assertEquals(city.getLongitude(), result.getLng());
	assertEquals(city.getAdm1Code(), result.getAdm1_code());
	assertEquals(city.getAdm2Code(), result.getAdm2_code());
	assertEquals(city.getAdm3Code(), result.getAdm3_code());
	assertEquals(city.getAdm4Code(), result.getAdm4_code());
	assertEquals(city.getAdm1Name(), result.getAdm1_name());
	assertEquals(city.getAdm2Name(), result.getAdm2_name());
	assertEquals(city.getAdm3Name(), result.getAdm3_name());
	assertEquals(city.getAdm4Name(), result.getAdm4_name());
	Iterator<ZipCode> ZipIterator = city.getZipCodes().iterator();
	assertTrue(result.getZipcodes().contains(ZipIterator.next().getCode()));
	assertTrue(result.getZipcodes().contains(ZipIterator.next().getCode()));
	assertEquals(city.getCountry().getName(), result.getCountry_name());
	assertEquals(null,
			result.getCountry_flag_url());
	assertEquals(null, result.getGoogle_map_url());
	assertEquals(null, result
		.getYahoo_map_url());
	assertEquals(null, result
			.getOpenstreetmap_map_url());

	assertEquals(1, result.getName_alternates().size());
	assertEquals("cityalternate", result.getName_alternates().get(0));

	assertEquals(1, result.getName_alternates_localized().size());
	assertEquals("cityalternateFR", result.getName_alternates_localized()
		.get("FR").get(0));

	assertEquals(2, result.getAdm1_names_alternate().size());
	
	Assert.assertTrue(result.getAdm1_names_alternate().contains("admGGPalternate"));
	Assert.assertTrue(result.getAdm1_names_alternate().contains("admGGPalternate2"));
	assertEquals("admGGPalternate2", result.getAdm1_names_alternate()
		.get(1));

	assertEquals(1, result.getAdm2_names_alternate().size());
	assertTrue(result.getAdm2_names_alternate().contains(city.getAdm().getParent().getAlternateNames().iterator().next().getName()));

	assertEquals(1, result.getCountry_names_alternate().size());
	assertTrue(result.getAdm2_names_alternate().contains(city.getAdm().getParent().getAlternateNames().iterator().next().getName()));
	assertTrue(result.getCountry_names_alternate().contains("francia"));

	assertEquals(1, result.getCountry_names_alternate_localized().size());
	assertEquals("franciaFR", result.getCountry_names_alternate_localized()
		.get("FR").get(0));

	assertEquals(1, result.getAdm1_names_alternate_localized().size());
	assertEquals("admGGPalternateFR", result
		.getAdm1_names_alternate_localized().get("FR").get(0));

	assertEquals(1, result.getAdm2_names_alternate_localized().size());
	assertEquals("admGPalternateFR", result
		.getAdm2_names_alternate_localized().get("FR").get(0));
    }

    @Test
    public void testUnmarshallSolrDocumentShouldReallyUnmarshallCountry() {
	Country country = GisgraphyTestHelper
		.createFullFilledCountry();
	
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);

	country.addSpokenLanguage(lang);
	
	AlternateName alternateNameLocalized = new AlternateName("alternateFR",AlternateNameSource.ALTERNATENAMES_FILE);
	alternateNameLocalized.setLanguage("FR");
	AlternateName alternateName = new AlternateName("alternate",AlternateNameSource.ALTERNATENAMES_FILE);
	country.addAlternateName(alternateName);
	country.addAlternateName(alternateNameLocalized);
	Assert.assertEquals(2,country.getAlternateNames().size());
	countryDao.save(country);
	this.solRSynchroniser.commit();
	Pagination pagination = paginate().from(1).to(10);
	Output output = Output.withFormat(OutputFormat.XML).withLanguageCode(
		"FR").withStyle(OutputStyle.FULL).withIndentation();
	FulltextQuery query = new FulltextQuery(country.getName(), pagination,
		output, null, null);
	FulltextResultsDto response = this.fullTextSearchEngine
		.executeQuery(query);
	List<SolrResponseDto> results = response.getResults();
	assertNotNull(
		"There should have a result for a fulltextSearch for "
			+ country.getName()
			+ " and even If no results are return: an empty list should be return,  not null ",
		results);
	assertTrue("There should have a result for a fulltextSearch for "
		+ country.getName(), results.size() == 1);
	SolrResponseDto result = results.get(0);
	assertNotNull(result.getName());
	assertEquals(country.getName(), result.getName());
	assertNotNull(result.getFeature_id());
	assertEquals(country.getFeatureId(), result.getFeature_id());
	assertNotNull(result.getFeature_class());
	assertEquals(country.getFeatureClass(), result.getFeature_class());
	assertNotNull(result.getFeature_code());
	assertEquals(country.getFeatureCode(), result.getFeature_code());
	assertNotNull(result.getName_ascii());
	assertEquals(country.getAsciiName(), result.getName_ascii());
	assertNotNull(result.getElevation());
	assertEquals(country.getElevation(), result.getElevation());
	assertNotNull(result.getGtopo30());
	assertEquals(country.getGtopo30(), result.getGtopo30());
	assertNotNull(result.getTimezone());
	assertEquals(country.getTimezone(), result.getTimezone());
	//since v 4.0 we don't process calculated fields
		assertEquals(null, result
			.getFully_qualified_name());
	assertNotNull(result.getPlacetype());
	assertEquals(country.getClass().getSimpleName(), result.getPlacetype());
	assertNotNull(result.getPopulation());
	assertEquals(country.getPopulation(), result.getPopulation());
	assertNotNull(result.getLat());
	assertEquals(country.getLatitude(), result.getLat());
	assertNotNull(result.getLng());
	assertEquals(country.getLongitude(), result.getLng());
	//since v 4.0 url maps are not preprocessed to reduce storage
	assertEquals(null, result.getGoogle_map_url());
	assertEquals(null,
		result.getCountry_flag_url());
	assertEquals(null, result
		.getYahoo_map_url());

	assertEquals(1, result.getCountry_names_alternate().size());
	assertNotNull(result.getCountry_names_alternate().get(0));
	assertTrue(result.getCountry_names_alternate().contains("alternate"));
			

	assertEquals(1, result.getCountry_names_alternate_localized().size());
	assertNotNull(result.getCountry_names_alternate_localized()
		.get(alternateNameLocalized.getLanguage()).get(0));
	assertEquals(alternateNameLocalized.getName(), result.getCountry_names_alternate_localized()
		.get(alternateNameLocalized.getLanguage()).get(0));
	assertNotNull(result.getContinent());
	assertEquals(country.getContinent(), result.getContinent());
	assertNotNull(result.getCurrency_code());
	assertEquals(country.getCurrencyCode(), result.getCurrency_code());
	assertNotNull(result.getCurrency_name());
	assertEquals(country.getCurrencyName(), result.getCurrency_name());
	assertNotNull(result.getFips_code());
	assertEquals(country.getFipsCode(), result.getFips_code());
	assertNotNull(result.getIsoalpha3_country_code());
	assertEquals(country.getIso3166Alpha2Code(), result.getIsoalpha2_country_code());
	assertNotNull(result.getIsoalpha3_country_code());
	assertEquals(country.getIso3166Alpha3Code(), result.getIsoalpha3_country_code());
	assertNotNull(result.getPostal_code_mask());
	assertEquals(country.getPostalCodeMask(), result.getPostal_code_mask());
	assertNotNull(result.getPostal_code_regex());
	assertEquals(country.getPostalCodeRegex(), result.getPostal_code_regex());
	assertNotNull(result.getPhone_prefix());
	assertEquals(country.getPhonePrefix(), result.getPhone_prefix());
	assertNotNull(result.getSpoken_languages().get(0));
	assertEquals(country.getSpokenLanguages().get(0).getIso639LanguageName(), result.getSpoken_languages().get(0));
	assertNotNull(result.getTld());
	assertEquals(country.getTld(), result.getTld());
	assertNotNull(result.getCapital_name());
	assertEquals(country.getCapitalName(), result.getCapital_name());
	assertNotNull(result.getArea());
	assertEquals(country.getArea(), result.getArea());
	
    }

    @Test
    public void testUnmarshallSolrDocumentShouldReallyUnmarshallAdm() {
	Adm adm = geolocTestHelper
		.createAdm("AdmName", "FR", "A1", "B2", null, null, null, 2);

	admDao.save(adm);
	
	this.solRSynchroniser.commit();
	Pagination pagination = paginate().from(1).to(10);
	Output output = Output.withFormat(OutputFormat.XML).withLanguageCode(
		"FR").withStyle(OutputStyle.FULL).withIndentation();
	FulltextQuery query = new FulltextQuery(adm.getName(), pagination,
		output, null, null);
	FulltextResultsDto response = this.fullTextSearchEngine
		.executeQuery(query);
	List<SolrResponseDto> results = response.getResults();
	assertNotNull(
		"There should have a result for a fulltextSearch for "
			+ adm.getName()
			+ " and even If no results are return: an empty list should be return,  not null ",
		results);
	assertTrue("There should have a result for a fulltextSearch for "
		+ adm.getName(), results.size() == 1);
	SolrResponseDto result = results.get(0);
	assertNotNull(result.getName());
	assertEquals(adm.getName(), result.getName());
	assertNotNull(result.getAdm1_code());
	assertEquals(adm.getAdm1Code(), result.getAdm1_code());
	assertNotNull(result.getAdm2_code());
	assertEquals(adm.getAdm2Code(), result.getAdm2_code());
	assertEquals("Level should be fill when an Adm is saved ",adm.getLevel(), result.getLevel());
	
	
    }
    
    @Test
    public void testUnmarshallSolrDocumentShouldReallyUnmarshallStreet() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	street.setOneWay(true);
	StreetType streetType = StreetType.BRIDLEWAY;
	street.setStreetType(streetType);
	double length = 1.6D;
	street.setLength(length);
	HouseNumber houseNumber = new HouseNumber();
	houseNumber.setNumber("3");
	houseNumber.setLocation(GeolocHelper.createPoint(4F, 5F));
	street.addHouseNumber(houseNumber);
	openStreetMapDao.save(street);
	
	this.solRSynchroniser.commit();
	Pagination pagination = paginate().from(1).to(10);
	Output output = Output.withFormat(OutputFormat.XML).withStyle(OutputStyle.FULL).withIndentation();
	FulltextQuery query = new FulltextQuery(street.getName(), pagination,
		output, null, null);
	FulltextResultsDto response = this.fullTextSearchEngine
		.executeQuery(query);
	List<SolrResponseDto> results = response.getResults();
	assertNotNull(
		"There should have a result for a fulltextSearch for "
			+ street.getName()
			+ " and even If no results are return: an empty list should be return,  not null ",
		results);
	assertTrue("There should have a result for a fulltextSearch for "
		+ street.getName(), results.size() == 1);
	SolrResponseDto result = results.get(0);
	assertNotNull(result.getName());
	 Assert.assertEquals("The results are not correct", street.getName(),
		 result.getName());
	    Assert.assertEquals("The length is not correct", length,
		    result.getLength());
	    Assert.assertEquals("The one_way is not correct", true,
		    result.getOne_way().booleanValue());
	    Assert.assertEquals("The street type is not correct", streetType.toString(),
		    result.getStreet_type().toString());
	    Assert.assertEquals("The is_in is not correct", street.getIsIn(),
			    result.getIs_in());
	    Assert.assertEquals("The is_in_place is not correct", street.getIsInPlace(),
			    result.getIs_in_place());
	    Assert.assertEquals("The is_in_zip is not correct", street.getIsInZip(),
			    result.getIs_in_zip());
	    Assert.assertEquals("The is_in_adm is not correct", street.getIsInAdm(),
			    result.getIs_in_adm());
	   /* Assert.assertEquals("The fullyqualified address is not correct", street.getFullyQualifiedAddress(),
			    result.getFully_qualified_address());*/
	    Assert.assertEquals("The openstreetmap id is not correct", street.getOpenstreetmapId(),
			    result.getOpenstreetmap_id());
	    
	    HouseNumber first = street.getHouseNumbers().first();
		Assert.assertEquals("The houseNumber latitude location  is not correct", first.getLocation().getY(),
			    result.getHouse_numbers().get(0).getLatitude());
	    
	    Assert.assertEquals("The houseNumber longitude location  is not correct", first.getLocation().getX(),
			    result.getHouse_numbers().get(0).getLongitude());
	    
	    Assert.assertEquals("The house number is not correct", first.getNumber(),
			    result.getHouse_numbers().get(0).getNumber());
	
	
    }

    
    
    @Test
    public void testUnmarshallQueryResponseShouldReturnAnEmptyListIfNoResultsAreFound() {
	FulltextQuery query = new FulltextQuery("fake");
	FulltextResultsDto response = this.fullTextSearchEngine
		.executeQuery(query);
	List<SolrResponseDto> results = response.getResults();
	assertNotNull(
		"If no results are return: an empty list should be return,  not null ",
		results);
	assertTrue("If no results are return: an empty list should be return",
		results.size() == 0);
    }

}
