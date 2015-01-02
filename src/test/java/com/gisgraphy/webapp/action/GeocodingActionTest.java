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
package com.gisgraphy.webapp.action;

import static com.gisgraphy.fulltext.Constants.ONLY_CITY_PLACETYPE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jstester.JsTester;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.opensymphony.xwork2.Action;

public class GeocodingActionTest {

    @Test
    public void getCountries() {
	ICountryDao countryDaoMock = EasyMock.createMock(ICountryDao.class);
	EasyMock.expect(countryDaoMock.getAllSortedByName()).andReturn((new ArrayList<Country>()));
	EasyMock.replay(countryDaoMock);

	GeocodingAction action = new GeocodingAction();
	action.setCountryDao(countryDaoMock);

	action.getCountries();

	EasyMock.verify(countryDaoMock);

    }
   
    @Test
    public void getLatLongJson() {
	GeocodingAction action = new GeocodingAction();
	List<SolrResponseDto> ambiguousCities = createAmbiguousCities();
	action.setAmbiguousCities(ambiguousCities);
	String feed = action.getLatLongJson();

	JsTester jsTester = null;
	try {
	    jsTester = new JsTester();
	    jsTester.onSetUp();

	    // JsTester
	    jsTester.eval("evalresult= eval(" + feed + ");");
	    Assert.assertNotNull(jsTester.eval("evalresult"));
	    Assert.assertEquals(ambiguousCities.get(0).getLat().toString(), (jsTester.eval("evalresult[0]['lat']")).toString());
	    Assert.assertEquals(ambiguousCities.get(0).getLng(), jsTester.eval("evalresult[0]['lng']"));
	    Assert.assertEquals(ambiguousCities.get(1).getLat().toString(), (jsTester.eval("evalresult[1]['lat']")).toString());
	    Assert.assertEquals(ambiguousCities.get(1).getLng(), jsTester.eval("evalresult[1]['lng']"));
	}finally {
	    if (jsTester != null){
		jsTester.onTearDown();
	    }
	}

    }

    private List<SolrResponseDto> createAmbiguousCities() {
	List<SolrResponseDto> ambiguousCities = new ArrayList<SolrResponseDto>();
	SolrResponseDto city1 = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(city1.getLat()).andStubReturn(3.2D);
	EasyMock.expect(city1.getLng()).andStubReturn(4.5D);
	EasyMock.replay(city1);

	SolrResponseDto city2 = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(city2.getLat()).andStubReturn(3.2D);
	EasyMock.expect(city2.getLng()).andStubReturn(4.5D);
	EasyMock.replay(city2);

	ambiguousCities.add(city1);
	ambiguousCities.add(city2);
	return ambiguousCities;
    }
    
    
    @Test
    public void getGoogleMapAPIKey(){
	 String savedGoogleMapAPIKey  = GisgraphyConfig .googleMapAPIKey;
	try {
           GeocodingAction action = new GeocodingAction();
	   GisgraphyConfig.googleMapAPIKey = null;
	   Assert.assertEquals("getGoogleMapAPIKey should return an empty string if the config value is null ","", action.getGoogleMapAPIKey());
	   String newKey = "new key";
	GisgraphyConfig.googleMapAPIKey=newKey;
	   Assert.assertEquals("getGoogleMapAPIKey should return the config value ",newKey, action.getGoogleMapAPIKey());
	   
	} finally {
	    GisgraphyConfig.googleMapAPIKey = savedGoogleMapAPIKey;
	}
    }
    
    @Test
    public void executeWithCityAndNoCountry() throws Exception{
	GeocodingAction action = new GeocodingAction(){

	    @Override
	    public String getText(String textName) {
	       return "search.country.required".equals(textName)?"returnedValue": null;
	    }
	};
	action.setCity("city");
	action.setCountryCode(null);
	Assert.assertEquals(Action.SUCCESS,action.execute());
	Assert.assertEquals("geocoding action should return the getText(search.country.required) value","returnedValue", action.getErrorMessage());
    
    }
    
    @Test
    public void executeWithCityAndCountryWithNoCityMatches() throws Exception{
	GeocodingAction action = new GeocodingAction();
	
	IFullTextSearchEngine fullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	String countryCode = "FR";
	String cityName = "city";
	FulltextQuery query = new FulltextQuery(cityName,
		Pagination.DEFAULT_PAGINATION, Output.DEFAULT_OUTPUT,
		ONLY_CITY_PLACETYPE, countryCode);
	EasyMock.expect(fullTextSearchEngine.executeQuery(query)).andStubReturn(new FulltextResultsDto());
	EasyMock.replay(fullTextSearchEngine);
	action.setFullTextSearchEngine(fullTextSearchEngine);
	
	action.setCity(cityName);
	action.setCountryCode(countryCode);
	Assert.assertEquals(Action.SUCCESS,action.execute());
    }
    
    @Test
    public void executeWithCityAndCountryWithOneCityMatches() throws Exception{
	GeocodingAction action = new GeocodingAction();
	
	SolrResponseDto cityFound1 = createCityFound1();
	
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	results.add(cityFound1);
	
	FulltextResultsDto fulltextResultsDto = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(fulltextResultsDto.getResults()).andStubReturn(results);
	EasyMock.replay(fulltextResultsDto);
	
	IFullTextSearchEngine fullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	String countryCode = "FR";
	String cityNameSearched = "city";
	FulltextQuery query = new FulltextQuery(cityNameSearched,
		Pagination.DEFAULT_PAGINATION, Output.DEFAULT_OUTPUT,
		ONLY_CITY_PLACETYPE, countryCode);
	EasyMock.expect(fullTextSearchEngine.executeQuery(query)).andStubReturn(fulltextResultsDto);
	EasyMock.replay(fullTextSearchEngine);
	action.setFullTextSearchEngine(fullTextSearchEngine);
	
	action.setCity(cityNameSearched);
	action.setCountryCode(countryCode);
	Assert.assertEquals(Action.SUCCESS,action.execute());
	Assert.assertEquals(action.buildCityDisplayName(cityFound1),action.getCity());
	Assert.assertEquals(results,action.getAmbiguousCities());
	Assert.assertTrue(action.isCityFound());
    }

	private SolrResponseDto createCityFound1() {
		SolrResponseDto cityFound1 =  EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(cityFound1.getLat()).andStubReturn(23D);
		EasyMock.expect(cityFound1.getLng()).andStubReturn(30D);
		String cityNameFound = "cityName";
		String zipCode= "zip";
		EasyMock.expect(cityFound1.getName()).andStubReturn(cityNameFound);
		Set<String> zipCodes = new HashSet<String>();
		zipCodes.add(zipCode);
		EasyMock.expect(cityFound1.getZipcodes()).andStubReturn(zipCodes);
		EasyMock.replay(cityFound1);
		return cityFound1;
	}
    
    @Test
    public void testBuildCityDisplayNameWithCityWithOneZipCode(){
    	GeocodingAction action = new GeocodingAction();
    	SolrResponseDto solrResponseDto = createCityFound1();
    	String displayName = action.buildCityDisplayName(solrResponseDto);
    	Assert.assertEquals(solrResponseDto.getName()+" ("+solrResponseDto.getZipcodes().iterator().next()+")", displayName);
    }
    
    @Test
    public void testBuildCityDisplayNameWithCityWithMoreThanOneZipCode(){
    	GeocodingAction action = new GeocodingAction();
    	SolrResponseDto cityFound1 =  EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(cityFound1.getLat()).andStubReturn(23D);
		EasyMock.expect(cityFound1.getLng()).andStubReturn(30D);
		String cityNameFound = "cityName";
		EasyMock.expect(cityFound1.getName()).andStubReturn(cityNameFound);
		Set<String> zipCodes = new HashSet<String>();
		zipCodes.add("zip1");
		zipCodes.add("zip2");
		EasyMock.expect(cityFound1.getZipcodes()).andStubReturn(zipCodes);
		EasyMock.replay(cityFound1);
    	String displayName = action.buildCityDisplayName(cityFound1);
    	Assert.assertEquals(cityFound1.getName(), displayName);
    }
    

    @Test
    public void testBuildCityDisplayNameWithCityWithoutZipCode(){
    	GeocodingAction action = new GeocodingAction();
    	SolrResponseDto cityFound1 =  EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(cityFound1.getLat()).andStubReturn(23D);
		EasyMock.expect(cityFound1.getLng()).andStubReturn(30D);
		String cityNameFound = "cityName";
		EasyMock.expect(cityFound1.getName()).andStubReturn(cityNameFound);
		Set<String> zipCodes = new HashSet<String>();
		EasyMock.expect(cityFound1.getZipcodes()).andStubReturn(zipCodes);
		EasyMock.replay(cityFound1);
    	String displayName = action.buildCityDisplayName(cityFound1);
    	Assert.assertEquals(cityFound1.getName(), displayName);
    }
    
    @Test
    public void executeWithCityAndCountryWithMoreThanOneCityMatches() throws Exception{
	GeocodingAction action = new GeocodingAction();
	
	SolrResponseDto cityFound1 =  createCityFound1();
	
	SolrResponseDto cityFound2 =  EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(cityFound2.getLat()).andStubReturn(24D);
	EasyMock.expect(cityFound2.getLng()).andStubReturn(31D);
	EasyMock.expect(cityFound2.getName()).andStubReturn("cityName2");
	Set<String> zipCodes2 = new HashSet<String>();
	zipCodes2.add("zip2");
	EasyMock.expect(cityFound2.getZipcodes()).andStubReturn(zipCodes2);
	EasyMock.replay(cityFound2);
	
	
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	results.add(cityFound1);
	results.add(cityFound2);
	
	FulltextResultsDto fulltextResultsDto = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(fulltextResultsDto.getResults()).andStubReturn(results);
	EasyMock.replay(fulltextResultsDto);
	
	IFullTextSearchEngine fullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	String countryCode = "FR";
	String cityNameSearched = "city";
	FulltextQuery query = new FulltextQuery(cityNameSearched,
		Pagination.DEFAULT_PAGINATION, Output.DEFAULT_OUTPUT,
		ONLY_CITY_PLACETYPE, countryCode);
	EasyMock.expect(fullTextSearchEngine.executeQuery(query)).andStubReturn(fulltextResultsDto);
	EasyMock.replay(fullTextSearchEngine);
	action.setFullTextSearchEngine(fullTextSearchEngine);
	
	action.setCity(cityNameSearched);
	action.setCountryCode(countryCode);
	Assert.assertEquals(Action.SUCCESS,action.execute());
	Assert.assertEquals(cityNameSearched,action.getCity());
	Assert.assertEquals(results,action.getAmbiguousCities());
	Assert.assertFalse(action.isCityFound());
    }
    
    @Test
    public void executeWithAmbiguousCities() throws Exception{
	GeocodingAction action = new GeocodingAction();
	action.setCity(null);
	action.setAmbiguousCity("");
	Assert.assertEquals(Action.SUCCESS,action.execute());
	Assert.assertEquals("city should be null if ambiguous city is not",null,action.getCity());
	Assert.assertEquals("ambiguous cities should be null because ambiguousCity is not",null,action.getAmbiguousCities());
	Assert.assertFalse("isCityfound should be true because ambiguous city is set",action.isCityFound());
    }
    
    @Test
    public void executeThrowsException() throws Exception{
	GeocodingAction action = new GeocodingAction(){
	    @Override
	    public String getText(String textName, String defaultValue) {
	       if ("search.error".equals(textName)){
		   return "returnedValue";
	       }
	       return null;
	    }
	};
	

	
	IFullTextSearchEngine fullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	String countryCode = "FR";
	String cityNameSearched = "city";
	FulltextQuery query = new FulltextQuery(cityNameSearched,
		Pagination.DEFAULT_PAGINATION, Output.DEFAULT_OUTPUT,
		ONLY_CITY_PLACETYPE, countryCode);
	EasyMock.expect(fullTextSearchEngine.executeQuery(query)).andThrow(new RuntimeException());
	EasyMock.replay(fullTextSearchEngine);
	action.setFullTextSearchEngine(fullTextSearchEngine);
	
	action.setCity(cityNameSearched);
	action.setCountryCode(countryCode);
	Assert.assertEquals(Action.SUCCESS,action.execute());
	Assert.assertEquals("returnedValue",action.getErrorMessage());
    }
    

}
