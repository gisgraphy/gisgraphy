/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.geocoding;

import static com.gisgraphy.fulltext.Constants.ADDRESSES_PLACETYPE;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressQuery;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.addressparser.IAddressParserService;
import com.gisgraphy.addressparser.StructuredAddressQuery;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.addressparser.exception.AddressParserException;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.importer.ImporterConfig;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsageType;
import com.gisgraphy.street.HouseNumberDto;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.Point;

public class GeocodingServiceTest {

    IStatsUsageService statsUsageService;
    GisgraphyConfig gisgraphyConfig;
    public boolean geocodeIsCalled = false;

    public boolean findCitiesCalled = false;
    public boolean findStreetCalled = false;
    public boolean GeocodeAdressCalled = false;
    public boolean populatecalled = false;

    @Before
    public void beforeTest() {
	statsUsageService = EasyMock.createMock(IStatsUsageService.class);
	gisgraphyConfig = new GisgraphyConfig();
    }
    
    
    /*
                                    _     _                   
	__ __ __ ___      __   __ _  __| | __| |_ __ ___  ___ ___ 
	| '__/ _` \ \ /\ / /  / _` |/ _` |/ _` | '__/ _ \/ __/ __|
	| | | (_| |\ V  V /  | (_| | (_| | (_| | | |  __/\__ \__ \
	|_|  \__,_| \_/\_/    \__,_|\__,_|\__,_|_|  \___||___/___/
	                                                          
     */

    @Test(expected = IllegalArgumentException.class)
    public void geocodeRawAdressShouldThrowIfAddressIsNull() {
	IGeocodingService geocodingService = new GeocodingService();
	String rawAddress = null;
	AddressQuery query = new AddressQuery(rawAddress, "US");
	geocodingService.geocode(query);
    }

    @Test(expected = IllegalArgumentException.class)
    public void geocodeRawAdressShouldThrowIfAddressIsEmpty() {
	IGeocodingService geocodingService = new GeocodingService();
	String rawAddress = " ";
	AddressQuery query = new AddressQuery(rawAddress, "US");
	geocodingService.geocode(query);
    }

    @Test(expected = GeocodingException.class)
    public void geocodeRawAdressShouldThrowIfCountryCodehasOnlyOneLetter() {
	IGeocodingService geocodingService = new GeocodingService();
	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, "d");
	geocodingService.geocode(query);
    }
    
    @Test(expected = GeocodingException.class)
    public void geocodeRawAdressShouldThrowIfCountryCodehasThreeLetters() {
	IGeocodingService geocodingService = new GeocodingService();
	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, "ddd");
	geocodingService.geocode(query);
    }
    
    @Test
    public void geocodeRawAdressShouldNotThrowIfCountryCodeisEmpty() {
    	GeocodingService geocodingService = new GeocodingService(){
    	    @Override
    	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
    	        return new ArrayList<SolrResponseDto>();
    	    }
    	   @Override
    	protected List<SolrResponseDto> findExactMatches(String text,
    			String countryCode) {
    		   return new ArrayList<SolrResponseDto>();
    	}
    	};
    	ImporterConfig config  = new ImporterConfig();
    	config.setOpenStreetMapFillIsIn(true);
    	geocodingService.setImporterConfig(config);
    	geocodingService.setGisgraphyConfig(gisgraphyConfig);
    	geocodingService.setStatsUsageService(statsUsageService);
    	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
    	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubThrow(new AddressParserException());
    	geocodingService.setStatsUsageService(statsUsageService);
    	EasyMock.replay(mockAddressParserService);
    	geocodingService.setAddressParser(mockAddressParserService);
    	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, " ");
	geocodingService.geocode(query);
    }
    
    @Test
    public void geocodeRawAdressShouldNotThrowIfCountryCodeisNull() {
    	GeocodingService geocodingService = new GeocodingService(){
    	    @Override
    	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
    	        return new ArrayList<SolrResponseDto>();
    	    }
    	   @Override
    	protected List<SolrResponseDto> findExactMatches(String text,
    			String countryCode) {
    		   return new ArrayList<SolrResponseDto>();
    	}
    	};
    	ImporterConfig config  = new ImporterConfig();
    	config.setOpenStreetMapFillIsIn(true);
    	geocodingService.setImporterConfig(config);
    	geocodingService.setGisgraphyConfig(gisgraphyConfig);
    	geocodingService.setStatsUsageService(statsUsageService);
    	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
    	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubThrow(new AddressParserException());
    	geocodingService.setStatsUsageService(statsUsageService);
    	EasyMock.replay(mockAddressParserService);
    	geocodingService.setAddressParser(mockAddressParserService);
    	String rawAddress = "t";
    	AddressQuery query = new AddressQuery(rawAddress, null);
    	geocodingService.geocode(query);
    }

    @Test(expected = GeocodingException.class)
    public void geocodeRawAdressShouldThrowIfCountryCodeHasenTALengthOf2() {
	GeocodingService geocodingService = new GeocodingService();
	geocodingService.setStatsUsageService(statsUsageService);
	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, "abc");
	geocodingService.geocode(query);
    }

    @Test
    public void geocodeRawAdressShouldNotThrowGeocodingExceptionWhenAddressParserExceptionOccurs() {
	GeocodingService geocodingService = new GeocodingService(){
	    @Override
	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
	        return new ArrayList<SolrResponseDto>();
	    }
	   @Override
	protected List<SolrResponseDto> findExactMatches(String text,
			String countryCode) {
		   return new ArrayList<SolrResponseDto>();
	}
	};
	ImporterConfig config  = new ImporterConfig();
	config.setOpenStreetMapFillIsIn(true);
	geocodingService.setImporterConfig(config);
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	geocodingService.setStatsUsageService(statsUsageService);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubThrow(new AddressParserException());
	geocodingService.setStatsUsageService(statsUsageService);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, "ac");
	try {
	    geocodingService.geocode(query);
	} catch (GeocodingException e) {
	   fail("geocoder should be tolerant to addressparser errors");
	}
    }
    
    @Test
    public void geocodeRawAddressToOutputStreamShouldHaveGeocodingLevelSet(){
    	
    	
    }

    @Test
    public void geocodeRawAdressShouldCallGeocodeAddressIfParsedAddressIsSuccess() {
	GeocodeAdressCalled = false;
	GeocodingService geocodingService = new GeocodingService() {
	    @Override
	    public AddressResultsDto geocode(Address address, String countryCode) throws GeocodingException {
		GeocodeAdressCalled = true;
		return new AddressResultsDto();
	    }
	};
	ImporterConfig importerConfig = EasyMock.createMock(ImporterConfig.class);
	EasyMock.expect(importerConfig.isOpenStreetMapFillIsIn()).andStubReturn(true);
	geocodingService.setImporterConfig(importerConfig);
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	gisgraphyConfig.setUseAddressParserWhenGeocoding(true);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	List<Address> addressList = new ArrayList<Address>() {
	    {
	    	Address address = new Address();
			address.setStreetName("streetName");
			address.setCity("city");
			add(address);
	    }
	};
	AddressResultsDto addressresults = new AddressResultsDto(addressList, 3L);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andReturn(addressresults);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, "ac");
	AddressResultsDto addressResultsDto = geocodingService.geocode(query);
	Assert.assertEquals("Parsed address should be set when the address paresed is not null",addressList.get(0), addressResultsDto.getParsedAddress());
	Assert.assertTrue(GeocodeAdressCalled);
    }
    
    
    @Test
    public void geocodeRawAdressShouldNotCallGeocodeAddressIfParsedAddressIsNotParsable() {
    	findCitiesCalled = false;
    	findStreetCalled = false;
    	ImporterConfig importerConfig = new ImporterConfig();
    	importerConfig.setOpenStreetMapFillIsIn(false);
    	GeocodingService geocodingService = new GeocodingService() {

    	    @Override
    	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
    		findCitiesCalled = true;
    		return null;
    	    }

    	    @Override
    	    protected java.util.List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
    		findStreetCalled = true;
    		return null;
    	    };
    	};
    	geocodingService.setStatsUsageService(statsUsageService);
    	geocodingService.setImporterConfig(importerConfig);
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	List<Address> addressList = new ArrayList<Address>() {
	    {
		Address notGeocodableAddress = new Address();
		add(notGeocodableAddress);
	    }
	};
	AddressResultsDto addressresults = new AddressResultsDto(addressList, 3L);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andReturn(addressresults);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, "ac");
	geocodingService.geocode(query);
	Assert.assertTrue(findCitiesCalled);
	Assert.assertTrue(findStreetCalled);
    }

    @Test
    public void geocodeRawAddressShouldCallFindCityInTextIfParsedAddressIsNullAndIsInIsFalse() {
	findCitiesCalled = false;
	findStreetCalled = false;
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenStreetMapFillIsIn(false);
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		findCitiesCalled = true;
		return null;
	    }

	    @Override
	    protected java.util.List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		findStreetCalled = true;
		return null;
	    };
	};
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setImporterConfig(importerConfig);
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	String rawAddress = "t";
	AddressQuery query = new AddressQuery(rawAddress, "ac");
	geocodingService.geocode(query);
	Assert.assertTrue(findCitiesCalled);
	Assert.assertTrue(findStreetCalled);
    }

    @Test
    public void geocodeRawAdressShouldCallFindCityInTextIfParsedAddressIsNullThenFindStreetInTextIfCityFoundAndIsInIsFalse() {
	findCitiesCalled = false;
	findStreetCalled = false;
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenStreetMapFillIsIn(false);
	final SolrResponseDto cityResult = EasyMock.createMock(SolrResponseDto.class);
	final Double latitude = 2.1d;
	EasyMock.expect(cityResult.getLat()).andStubReturn(latitude);
	final Double longitude = 5.2d;
	EasyMock.expect(cityResult.getLng()).andStubReturn(longitude);
	EasyMock.expect(cityResult.getName()).andStubReturn("paris");
	EasyMock.expect(cityResult.getAdm2_name()).andStubReturn("ile de france");
	EasyMock.expect(cityResult.getAdm1_name()).andStubReturn("paris region");
	EasyMock.expect(cityResult.getZipcodes()).andStubReturn(null);
	EasyMock.expect(cityResult.getCountry_code()).andStubReturn("FR");
	EasyMock.expect(cityResult.getFeature_id()).andStubReturn(123L);
	Set<String> zips = new HashSet<String>();
	zips.add("zip");
	EasyMock.expect(cityResult.getIs_in_zip()).andStubReturn(zips);
	EasyMock.expect(cityResult.getIs_in_adm()).andStubReturn("isinAdm");
	EasyMock.expect(cityResult.getIs_in_place()).andStubReturn("isinPlace");
	EasyMock.replay(cityResult);
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		findCitiesCalled = true;
		List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
		cities.add(cityResult);
		return cities;
	    }

	    @Override
	    protected java.util.List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		findStreetCalled = true;
		return null;
	    };
	};
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setImporterConfig(importerConfig);
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	String rawAddress = "paris champs ellysees";
	AddressQuery query = new AddressQuery(rawAddress, "ac");
	geocodingService.geocode(query);
	Assert.assertTrue(findCitiesCalled);
	Assert.assertTrue(findStreetCalled);
    }

    @Test
    public void geocodeRawAdressShouldCallFindCityInTextIfParsedAddressIsNullThenFindStreetInTextIfCityFound_onlyCityInQueryAndIsInIsFalse() {
	findCitiesCalled = false;
	findStreetCalled = false;
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenStreetMapFillIsIn(false);
	final SolrResponseDto cityResult = EasyMock.createMock(SolrResponseDto.class);
	final Double latitude = 2.1d;
	EasyMock.expect(cityResult.getLat()).andStubReturn(latitude);
	final Double longitude = 5.2d;
	EasyMock.expect(cityResult.getLng()).andStubReturn(longitude);
	EasyMock.expect(cityResult.getName()).andStubReturn("paris");
	EasyMock.expect(cityResult.getAdm2_name()).andStubReturn("ile de france");
	EasyMock.expect(cityResult.getAdm1_name()).andStubReturn("paris");
	EasyMock.expect(cityResult.getZipcodes()).andStubReturn(null);
	EasyMock.expect(cityResult.getCountry_code()).andStubReturn("FR");
	EasyMock.expect(cityResult.getFeature_id()).andStubReturn(123L);
	Set<String> zips = new HashSet<String>();
	zips.add("zip");
	EasyMock.expect(cityResult.getIs_in_zip()).andStubReturn(zips);
	EasyMock.expect(cityResult.getIs_in_adm()).andStubReturn("isinAdm");
	EasyMock.expect(cityResult.getIs_in_place()).andStubReturn("isinPlace");
	EasyMock.replay(cityResult);
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		findCitiesCalled = true;
		List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
		cities.add(cityResult);
		return cities;
	    }

	    @Override
	    protected java.util.List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		findStreetCalled = true;
		return null;
	    };
	};
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setImporterConfig(importerConfig);
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	String rawAddress = "paris";
	AddressQuery query = new AddressQuery(rawAddress, "ac");
	geocodingService.geocode(query);
	Assert.assertTrue(findCitiesCalled);
	Assert.assertFalse(findStreetCalled);
    }
    
    
    

    @Test
    public void geocodeRawAdressShouldCallFindStreetInTextIfParsedAddressIsNullIsInIsTrue() {
	//fail("todo");
	findCitiesCalled = false;
	findStreetCalled = false;
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setOpenStreetMapFillIsIn(true);
	final SolrResponseDto cityResult = EasyMock.createMock(SolrResponseDto.class);
	final Double latitude = 2.1d;
	EasyMock.expect(cityResult.getLat()).andStubReturn(latitude);
	final Double longitude = 5.2d;
	EasyMock.expect(cityResult.getLng()).andStubReturn(longitude);
	EasyMock.expect(cityResult.getName()).andStubReturn("paris");
	EasyMock.expect(cityResult.getAdm2_name()).andStubReturn("ile de france");
	EasyMock.expect(cityResult.getAdm1_name()).andStubReturn("paris region");
	EasyMock.expect(cityResult.getZipcodes()).andStubReturn(null);
	EasyMock.expect(cityResult.getCountry_code()).andStubReturn("FR");
	EasyMock.expect(cityResult.getFeature_id()).andStubReturn(123L);
	EasyMock.replay(cityResult);
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		findCitiesCalled = true;
		List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
		cities.add(cityResult);
		return cities;
	    }

	    @Override
	    protected java.util.List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		findStreetCalled = true;
		return null;
	    };
	    
	    @Override
	    protected List<SolrResponseDto> findExactMatches(String text,
	    		String countryCode) {
	    	return null;
	    }
	};
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setImporterConfig(importerConfig);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	String rawAddress = "paris champs ellysees";
	AddressQuery query = new AddressQuery(rawAddress, "ac");
	geocodingService.geocode(query);
	Assert.assertFalse(findCitiesCalled);
	Assert.assertTrue(findStreetCalled);
    }
    
    /*
	 _       _                        _                  _   _               _     
	(_)_ __ | |_ ___ _ __ _ __   __ _| |  _ __ ___   ___| |_| |__   ___   __| |___ 
	| | '_ \| __/ _ \ '__| '_ \ / _` | | | '_ ` _ \ / _ \ __| '_ \ / _ \ / _` / __|
	| | | | | ||  __/ |  | | | | (_| | | | | | | | |  __/ |_| | | | (_) | (_| \__ \
	|_|_| |_|\__\___|_|  |_| |_|\__,_|_| |_| |_| |_|\___|\__|_| |_|\___/ \__,_|___/
	                                                                               

     * 
     */
    
    
    
    @Test
    public void buildAddressResultDtoFromStreetsAndCitiesShouldFindHouseNumber() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
	
	List<HouseNumberDto> houseNumbersOneAndTwo = new ArrayList<HouseNumberDto>();
	HouseNumberDto number1 = new HouseNumberDto(GeolocHelper.createPoint(2D, 3D), "1");
	HouseNumberDto number2 = new HouseNumberDto(GeolocHelper.createPoint(4D, 5D), "2");
	houseNumbersOneAndTwo.add(number1);
	houseNumbersOneAndTwo.add(number2);
	
	List<HouseNumberDto> houseNumbersThreeAndFour = new ArrayList<HouseNumberDto>();
	HouseNumberDto number3 = new HouseNumberDto(GeolocHelper.createPoint(2D, 3D), "3");
	HouseNumberDto number4 = new HouseNumberDto(GeolocHelper.createPoint(4D, 5D), "4");
	houseNumbersThreeAndFour.add(number3);
	houseNumbersThreeAndFour.add(number4);
	//street1
	
	//3 segment, number in street2=>we only keep the one that have the number
	SolrResponseDto street1WithName1 = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,"streetname1",houseNumbersOneAndTwo,1L);
	streets.add(street1WithName1);
	SolrResponseDto street2WithName1AndNumberInSegment = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,"streetname1",houseNumbersThreeAndFour,2L);
	streets.add(street2WithName1AndNumberInSegment);
	SolrResponseDto street3WithName1 = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,"streetname1",houseNumbersOneAndTwo,3L);
	streets.add(street3WithName1);
	//only one segment number found=>we keep it
	SolrResponseDto street4WithName2NumberFound = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,"streetname2",houseNumbersThreeAndFour,4L);
	streets.add(street4WithName2NumberFound);
	//only one segment, number not found
	SolrResponseDto street5WithName3NumberNotFound = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,"streetname3",houseNumbersOneAndTwo,5L);
	streets.add(street5WithName3NumberNotFound);
	//one segment name null
	SolrResponseDto street6WithNameNull = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,null,houseNumbersOneAndTwo,6L);
	streets.add(street6WithNameNull);
	//2 segments, no number not found
	SolrResponseDto street7WithName4NumberNotFound = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,"streetname4",houseNumbersOneAndTwo,7L);
	streets.add(street7WithName4NumberNotFound);
	SolrResponseDto street8WithName4NumberNotFound = GisgraphyTestHelper.createSolrResponseDtoForStreet(null,"streetname4",houseNumbersOneAndTwo,8L);
	streets.add(street8WithName4NumberNotFound);
	
	SolrResponseDto city = GisgraphyTestHelper.createSolrResponseDtoForCity();
	List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
	cities.add(city);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromStreetsAndCities(streets, cities, "3");

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(6, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("the first street should be street 2 because it is the only street that have the number", 2L,address.getId().longValue());
	Assert.assertEquals("the first street should have name 1", "streetname1",address.getStreetName());
	Assert.assertEquals("because housenumber is found, level should be "+GeocodingLevels.HOUSE_NUMBER, GeocodingLevels.HOUSE_NUMBER,address.getGeocodingLevel());
	
	address = addressResultsDto.getResult().get(1);
	Assert.assertEquals("the second street should be street 4 because it is the only street that this name", 4L,address.getId().longValue());
	Assert.assertEquals("the second street should have name 2", "streetname2",address.getStreetName());
	Assert.assertEquals("because housenumber is found, level should be "+GeocodingLevels.HOUSE_NUMBER, GeocodingLevels.HOUSE_NUMBER,address.getGeocodingLevel());
	
	address = addressResultsDto.getResult().get(2);
	Assert.assertEquals("the 3rd street should be street 5 because it is the only street that this name", 5L,address.getId().longValue());
	Assert.assertEquals("the 3rd street should have name 3", "streetname3",address.getStreetName());
	Assert.assertEquals("because housenumber is not found, level should be "+GeocodingLevels.STREET, GeocodingLevels.STREET,address.getGeocodingLevel());
	
	address = addressResultsDto.getResult().get(3);
	Assert.assertEquals("the 4th street should be street 6 because it is the only street that this name", 6L,address.getId().longValue());
	Assert.assertEquals("the 4th street should have name null", null,address.getStreetName());
	Assert.assertEquals("because housenumber is not found, level should be "+GeocodingLevels.STREET, GeocodingLevels.STREET,address.getGeocodingLevel());
	
	address = addressResultsDto.getResult().get(4);
	Assert.assertEquals("the 5th street should be street 7 because it is the first segment and no num found", 7L,address.getId().longValue());
	Assert.assertEquals("the 5th street should have name 4", "streetname4",address.getStreetName());
	Assert.assertEquals("because housenumber is not found, level should be "+GeocodingLevels.STREET, GeocodingLevels.STREET,address.getGeocodingLevel());
	
	address = addressResultsDto.getResult().get(5);
	Assert.assertEquals("the 6th street should be street 8 because it is the second segment and no num found", 8L,address.getId().longValue());
	Assert.assertEquals("the 6th street should have name 4", "streetname4" ,address.getStreetName());
	Assert.assertEquals("because housenumber is not found, level should be "+GeocodingLevels.STREET, GeocodingLevels.STREET,address.getGeocodingLevel());
	
    }
    
    @Test
    public void searchHouseNumberTest(){
    	GeocodingService service = new GeocodingService();
    	List<HouseNumberDto> houseNumbers = new ArrayList<HouseNumberDto>();
    	HouseNumberDto number1 = new HouseNumberDto(GeolocHelper.createPoint(2D, 3D), "1");
    	HouseNumberDto number2 = new HouseNumberDto(GeolocHelper.createPoint(4D, 5D), "2");
    	houseNumbers.add(number1);
    	houseNumbers.add(number2);
    	
    	Assert.assertNull(service.searchHouseNumber("3", houseNumbers,"FR"));
    	
    	HouseNumberDto result = service.searchHouseNumber("2", houseNumbers,"FR");
    	Assert.assertEquals(4D, result.getLocation().getX(),0.0001);
    	Assert.assertEquals(5D, result.getLocation().getY(),0.0001);
    	
    	result = service.searchHouseNumber("2bis", houseNumbers,"FR");
    	Assert.assertEquals(4D, result.getLocation().getX(),0.0001);
    	Assert.assertEquals(5D, result.getLocation().getY(),0.0001);
    }
    
    @Test
    public void searchHouseNumberTestWithNull(){
    	GeocodingService service = new GeocodingService();
    	List<HouseNumberDto> houseNumbers = new ArrayList<HouseNumberDto>();
    	HouseNumberDto number1 = new HouseNumberDto(GeolocHelper.createPoint(2D, 3D), "1");
    	HouseNumberDto number2 = new HouseNumberDto(GeolocHelper.createPoint(4D, 5D), "2");
    	houseNumbers.add(number1);
    	houseNumbers.add(number2);
    	
    	Assert.assertNull(service.searchHouseNumber("3", houseNumbers,null));
    	
    	HouseNumberDto result = service.searchHouseNumber("2", houseNumbers,null);
    	Assert.assertEquals(4D, result.getLocation().getX(),0.0001);
    	Assert.assertEquals(5D, result.getLocation().getY(),0.0001);
    	
    	result = service.searchHouseNumber("2bis", houseNumbers,null);
    	Assert.assertEquals(4D, result.getLocation().getX(),0.0001);
    	Assert.assertEquals(5D, result.getLocation().getY(),0.0001);
    }
    
    @Test
    public void searchHouseNumberTestforCZSK(){
    	GeocodingService service = new GeocodingService();
    	List<HouseNumberDto> houseNumbers = new ArrayList<HouseNumberDto>();
    	HouseNumberDto number1 = new HouseNumberDto(GeolocHelper.createPoint(2D, 3D), "1");
    	HouseNumberDto number2 = new HouseNumberDto(GeolocHelper.createPoint(4D, 5D), "2");
    	houseNumbers.add(number1);
    	houseNumbers.add(number2);
    	
    	Assert.assertNull(service.searchHouseNumber("3", houseNumbers,"CZ"));
    	
    	HouseNumberDto result = service.searchHouseNumber("2", houseNumbers,"CZ");
    	Assert.assertEquals(4D, result.getLocation().getX(),0.0001);
    	Assert.assertEquals(5D, result.getLocation().getY(),0.0001);
    	
    	result = service.searchHouseNumber("1/2", houseNumbers,"CZ");
    	Assert.assertEquals(4D, result.getLocation().getX(),0.0001);
    	Assert.assertEquals(5D, result.getLocation().getY(),0.0001);
    	
    	result = service.searchHouseNumber("1/2", houseNumbers,"SK");
    	Assert.assertEquals(4D, result.getLocation().getX(),0.0001);
    	Assert.assertEquals(5D, result.getLocation().getY(),0.0001);
    }
    
    @Test
    public void searchHouseNumber_WithNullValues(){
    	GeocodingService service = new GeocodingService();
    	List<HouseNumberDto> houseNumbers = new ArrayList<HouseNumberDto>();
    	Assert.assertNull(service.searchHouseNumber("3", null,"FR"));
    	Assert.assertNull(service.searchHouseNumber(null, houseNumbers,"FR"));
    	Assert.assertNull(service.searchHouseNumber(null, null,"FR"));
    }
    
    @Test
    public void findCitiesInText() {
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	SolrResponseDto solrResponseDto = EasyMock.createNiceMock(SolrResponseDto.class);
	results.add(solrResponseDto);
	FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
	EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
	EasyMock.replay(mockResultDTO);

	GeocodingService geocodingService = new GeocodingService();
	String text = "toto";
	String countryCode = "FR";
	IFullTextSearchEngine mockfullFullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	FulltextQuery query = new FulltextQuery(text, Pagination.DEFAULT_PAGINATION, GeocodingService.LONG_OUTPUT, com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE, countryCode);
	query.withAllWordsRequired(false).withoutSpellChecking();
	EasyMock.expect(mockfullFullTextSearchEngine.executeQuery(query)).andReturn(mockResultDTO);
	EasyMock.replay(mockfullFullTextSearchEngine);
	geocodingService.setFullTextSearchEngine(mockfullFullTextSearchEngine);

	List<SolrResponseDto> actual = geocodingService.findCitiesInText(text, countryCode);
	Assert.assertEquals(solrResponseDto, actual.get(0));
	EasyMock.verify(mockfullFullTextSearchEngine);
    }

    @Test
    public void findStreetInText() {
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	SolrResponseDto solrResponseDto = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(solrResponseDto.getLat()).andReturn(2D);
	EasyMock.expect(solrResponseDto.getLng()).andReturn(3D);
	EasyMock.replay(solrResponseDto);
	results.add(solrResponseDto);
	FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
	EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
	EasyMock.replay(mockResultDTO);

	GeocodingService geocodingService = new GeocodingService();
	String text = "toto";
	String countryCode = "FR";
	IFullTextSearchEngine mockfullFullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	FulltextQuery query = new FulltextQuery(text, Pagination.DEFAULT_PAGINATION, GeocodingService.MEDIUM_OUTPUT, com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, countryCode);
	query.withAllWordsRequired(false).withoutSpellChecking();
	EasyMock.expect(mockfullFullTextSearchEngine.executeQuery(query)).andReturn(mockResultDTO);
	EasyMock.replay(mockfullFullTextSearchEngine);
	geocodingService.setFullTextSearchEngine(mockfullFullTextSearchEngine);

	List<SolrResponseDto> actual = geocodingService.findStreetInText(text, countryCode, null);
	Assert.assertEquals(results, actual);
	EasyMock.verify(mockfullFullTextSearchEngine);
    }
    
    
    @Test
    public void findExactMatches() {
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	SolrResponseDto solrResponseDto = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(solrResponseDto.getLat()).andReturn(2D);
	EasyMock.expect(solrResponseDto.getLng()).andReturn(3D);
	EasyMock.replay(solrResponseDto);
	results.add(solrResponseDto);
	FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
	EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
	EasyMock.replay(mockResultDTO);

	GeocodingService geocodingService = new GeocodingService();
	String text = "toto";
	String countryCode = "FR";
	IFullTextSearchEngine mockfullFullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	FulltextQuery query = new FulltextQuery(text, GeocodingService.FIVE_RESULT_PAGINATION, GeocodingService.LONG_OUTPUT, ADDRESSES_PLACETYPE, countryCode);
	query.withAllWordsRequired(true).withoutSpellChecking();
	EasyMock.expect(mockfullFullTextSearchEngine.executeQuery(query)).andReturn(mockResultDTO);
	EasyMock.replay(mockfullFullTextSearchEngine);
	geocodingService.setFullTextSearchEngine(mockfullFullTextSearchEngine);

	List<SolrResponseDto> actual = geocodingService.findExactMatches(text, countryCode);
	Assert.assertEquals(results, actual);
	EasyMock.verify(mockfullFullTextSearchEngine);
    }
    
    
    @Test
    public void findStreetInTextWithNullOrEmptyText() {
	List<SolrResponseDto> expected = new ArrayList<SolrResponseDto>();
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> actual = geocodingService.findStreetInText("", "fr", null);
	Assert.assertEquals(expected, actual);
	actual = geocodingService.findStreetInText(null, "fr", null);
	Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void findCitiesInTextWithNullOrEmptyText() {
	List<SolrResponseDto> expected = new ArrayList<SolrResponseDto>();
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> actual = geocodingService.findCitiesInText("", "fr");
	Assert.assertEquals(expected, actual);
	actual = geocodingService.findCitiesInText(null, "fr");
	Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void buildAddressResultDtoFromSolrResponseDto_street() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
	SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet("is_in");
	streets.add(street);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromSolrResponseDto(streets, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("latitude is not correct", street.getLat(), address.getLat());
	Assert.assertEquals("longitude is not correct", street.getLng(), address.getLng());
	Assert.assertEquals("id is not correct", street.getFeature_id(), address.getId());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.STREET, address.getGeocodingLevel());
	Assert.assertEquals("street name is not correct", street.getName(), address.getStreetName());
	Assert.assertEquals("name is not correct", street.getName(), address.getName());
	Assert.assertEquals("street type is not correct", street.getStreet_type(), address.getStreetType());
	Assert.assertEquals("city name is not correct", street.getIs_in(), address.getCity());
	Assert.assertEquals("countrycode is not correct", street.getCountry_code(), address.getCountryCode());
	
	Assert.assertEquals("zip is not correct", street.getIs_in_zip().iterator().next(), address.getZipCode());
	Assert.assertEquals("Adm Name should not be the deeper one but the is_inadm one", street.getIs_in_adm(), address.getState());
	Assert.assertEquals("place is not correct", street.getIs_in_place(), address.getDependentLocality());
	
    }
    
    @Test
    public void buildAddressResultDtoFromSolrResponseDto_street_houseNumber() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
	SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet("is_in");
	streets.add(street);
	String houseNumberToFind = "2";
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromSolrResponseDto(streets, houseNumberToFind);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("latitude is not correct, it should be the house number one", 5D, address.getLat(),0.001);
	Assert.assertEquals("longitude is not correct, it should be the house number one", 4D, address.getLng(),0.001);
	Assert.assertEquals("id is not correct", street.getFeature_id(), address.getId());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.HOUSE_NUMBER, address.getGeocodingLevel());
	Assert.assertEquals("street name is not correct", street.getName(), address.getStreetName());
	Assert.assertEquals("name is not correct", street.getName(), address.getName());
	Assert.assertEquals("street type is not correct", street.getStreet_type(), address.getStreetType());
	Assert.assertEquals("city name is not correct", street.getIs_in(), address.getCity());
	Assert.assertEquals("countrycode is not correct", street.getCountry_code(), address.getCountryCode());
	
	Assert.assertEquals("zip is not correct", street.getIs_in_zip().iterator().next(), address.getZipCode());
	Assert.assertEquals("Adm Name should not be the deeper one but the is_inadm one", street.getIs_in_adm(), address.getState());
	Assert.assertEquals("place is not correct", street.getIs_in_place(), address.getDependentLocality());
	
    }
    
    @Test
    public void buildAddressResultDtoFromSolrResponseDto_severalType() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	SolrResponseDto city = GisgraphyTestHelper.createSolrResponseDtoForCity();
	SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet("is_in");
	results.add(street);
	results.add(city);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromSolrResponseDto(results, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(2, addressResultsDto.getResult().size());
	Address address1 = addressResultsDto.getResult().get(0);
	Address address2 = addressResultsDto.getResult().get(1);
	Assert.assertEquals("id is not correct for address 1", street.getFeature_id(), address1.getId());
	Assert.assertEquals("id is not correct for address 2", city.getFeature_id(), address2.getId());
    }
    
    @Test
    public void buildAddressResultDtoFromSolrResponseDto_city() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	SolrResponseDto city = GisgraphyTestHelper.createSolrResponseDtoForCity();
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	results.add(city);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromSolrResponseDto(results, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("id is not correct", city.getFeature_id(), address.getId());
	Assert.assertEquals("latitude is not correct", city.getLat(), address.getLat());
	Assert.assertEquals("longitude is not correct", city.getLng(), address.getLng());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.CITY, address.getGeocodingLevel());
	Assert.assertNull("street name is not correct", address.getStreetName());
	Assert.assertEquals("city name is not correct", city.getName(), address.getCity());
	Assert.assertEquals("name is not correct", city.getName(), address.getName());
	Assert.assertNull("street type is not correct", address.getStreetType());
	Assert.assertEquals("zipcode is not correct", city.getZipcodes().iterator().next(), address.getZipCode());
	Assert.assertEquals("Adm Name should be the deeper one", city.getAdm2_name(), address.getState());
	Assert.assertEquals("countrycode is not correct", city.getCountry_code(), address.getCountryCode());
    }
    
    @Test
    public void buildAddressResultDtoFromSolrResponseDto_citySubdivision() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	SolrResponseDto citySubdivision = GisgraphyTestHelper.createSolrResponseDtoForCitySudivision();
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	results.add(citySubdivision);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromSolrResponseDto(results, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("id is not correct", citySubdivision.getFeature_id(), address.getId());
	Assert.assertEquals("latitude is not correct", citySubdivision.getLat(), address.getLat());
	Assert.assertEquals("longitude is not correct", citySubdivision.getLng(), address.getLng());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.CITY, address.getGeocodingLevel());
	Assert.assertNull("street name is not correct", address.getStreetName());
	Assert.assertEquals("quarter name is not correct", citySubdivision.getName(), address.getQuarter());
	Assert.assertNull("city name is not correct", address.getCity());
	Assert.assertEquals("name is not correct", citySubdivision.getName(), address.getName());
	Assert.assertNull("street type is not correct", address.getStreetType());
	Assert.assertEquals("zipcode is not correct", citySubdivision.getZipcodes().iterator().next(), address.getZipCode());
	Assert.assertEquals("Adm Name should be the deeper one", citySubdivision.getAdm2_name(), address.getState());
	Assert.assertEquals("countrycode is not correct", citySubdivision.getCountry_code(), address.getCountryCode());
    }
    
    @Test
    public void buildAddressResultDtoFromSolrResponseDto_adm() {
    	GeocodingService geocodingService = new GeocodingService();
    	SolrResponseDto adm = GisgraphyTestHelper.createSolrResponseDtoForAdm();
    	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
    	results.add(adm);
    	// exercise
    	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromSolrResponseDto(results, null);

    	// verify
    	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
    	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
    	Assert.assertEquals(1, addressResultsDto.getResult().size());
    	Address address = addressResultsDto.getResult().get(0);
    	Assert.assertEquals("id is not correct", adm.getFeature_id(), address.getId());
    	Assert.assertEquals("latitude is not correct", adm.getLat(), address.getLat());
    	Assert.assertEquals("longitude is not correct", adm.getLng(), address.getLng());
    	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.STATE, address.getGeocodingLevel());
    	Assert.assertNull("street name is not correct", address.getStreetName());
    	Assert.assertNull("city name is not correct", address.getCity());
    	Assert.assertEquals("name is not correct", adm.getName(), address.getName());
    	Assert.assertNull("street type is not correct", address.getStreetType());
    	Assert.assertNull("zipcode is not correct", address.getZipCode());
    	Assert.assertEquals("Adm Name should be the deeper one", adm.getName(), address.getState());
    	Assert.assertEquals("countrycode is not correct", adm.getCountry_code(), address.getCountryCode());
    }
    
    @Test
    public void buildAddressResultDtoFromSolrResponseDto_gisFeature() {
    	GeocodingService geocodingService = new GeocodingService();
    	SolrResponseDto feature = GisgraphyTestHelper.createSolrResponseDtoForGisFeature();
    	List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
    	cities.add(feature);
    	// exercise
    	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromSolrResponseDto(cities, null);

    	// verify
    	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
    	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
    	Assert.assertEquals(1, addressResultsDto.getResult().size());
    	Address address = addressResultsDto.getResult().get(0);
    	Assert.assertEquals("id is not correct", feature.getFeature_id(), address.getId());
    	Assert.assertEquals("latitude is not correct", feature.getLat(), address.getLat());
    	Assert.assertEquals("longitude is not correct", feature.getLng(), address.getLng());
    	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.STATE, address.getGeocodingLevel());
    	Assert.assertNull("street name is not correct", address.getStreetName());
    	Assert.assertNull("city name is not correct", address.getCity());
    	Assert.assertEquals("name is not correct", feature.getName(), address.getName());
    	Assert.assertNull("street type is not correct", address.getStreetType());
    	Assert.assertNull("zipcode is not correct", address.getZipCode());
    	Assert.assertEquals("Adm Name should be the deeper one", feature.getAdm2_name(), address.getState());
    	Assert.assertEquals("countrycode is not correct", feature.getCountry_code(), address.getCountryCode());
    }

    @Test
    public void buildAddressResultDtoFromStreetsAndCities_isInIsNull() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
	SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet(null);
	streets.add(street);
	SolrResponseDto city = GisgraphyTestHelper.createSolrResponseDtoForCity();
	List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
	cities.add(city);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromStreetsAndCities(streets, cities, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("latitude is not correct", street.getLat(), address.getLat());
	Assert.assertEquals("longitude is not correct", street.getLng(), address.getLng());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.STREET, address.getGeocodingLevel());
	Assert.assertEquals("street name is not correct", street.getName(), address.getStreetName());
	Assert.assertEquals("street type is not correct", street.getStreet_type(), address.getStreetType());
	Assert.assertEquals("city name is not correct", city.getName(), address.getCity());
	Assert.assertEquals("Adm Name should be the deeper one", city.getAdm2_name(), address.getState());
	Assert.assertEquals("countrycode is not correct", city.getCountry_code(), address.getCountryCode());
    }
    

    @Test
    public void buildAddressResultDtoFromStreetsAndCitiesWithNullCity_isInIsNull() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
	SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet(null);
	streets.add(street);
	List<SolrResponseDto> cities = null;
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromStreetsAndCities(streets, cities, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("latitude is not correct", street.getLat(), address.getLat());
	Assert.assertEquals("longitude is not correct", street.getLng(), address.getLng());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.STREET, address.getGeocodingLevel());
	Assert.assertEquals("street name is not correct", street.getName(), address.getStreetName());
	Assert.assertEquals("street type is not correct", street.getStreet_type(), address.getStreetType());
	Assert.assertNull("city name is not correct", address.getCity());
	Assert.assertNull("Adm Name should be the deeper one", address.getState());
	Assert.assertEquals("countryCode should be filled",street.getCountry_code(),address.getCountryCode());
    }
    
    @Test
    public void buildAddressResultDtoFromStreetsAndCities_isInIsNotNull() {
	// setup
	String is_in = "paris";
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
	SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet(is_in);
	streets.add(street);
	SolrResponseDto city = GisgraphyTestHelper.createSolrResponseDtoForCity();
	List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
	cities.add(city);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromStreetsAndCities(streets, cities, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("latitude is not correct", street.getLat(), address.getLat());
	Assert.assertEquals("longitude is not correct", street.getLng(), address.getLng());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.STREET, address.getGeocodingLevel());
	Assert.assertEquals("street name is not correct", street.getName(), address.getStreetName());
	Assert.assertEquals("street type is not correct", street.getStreet_type(), address.getStreetType());
	Assert.assertEquals("city name is not correct", is_in, address.getCity());
	
	Assert.assertEquals("zipcode is not correct", street.getIs_in_zip().iterator().next(), address.getZipCode());
	Assert.assertEquals("adm name is not correct", street.getIs_in_adm(), address.getState());
	Assert.assertEquals("place is not correct", street.getIs_in_place(), address.getDependentLocality());
	
	Assert.assertEquals("countryCode should be filled",street.getCountry_code(),address.getCountryCode());
    }

    @Test
    public void buildAddressResultDtoFromStreetsAndCitiesWithNullCity_isInNotNull() {
	// setup
	String is_in = "paris";
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
	SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet(is_in);
	streets.add(street);
	List<SolrResponseDto> cities = null;
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromStreetsAndCities(streets, cities, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("latitude is not correct", street.getLat(), address.getLat());
	Assert.assertEquals("longitude is not correct", street.getLng(), address.getLng());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.STREET, address.getGeocodingLevel());
	Assert.assertEquals("street name is not correct", street.getName(), address.getStreetName());
	Assert.assertEquals("street type is not correct", street.getStreet_type(), address.getStreetType());
	Assert.assertEquals("city name is not correct",is_in, address.getCity());
	
	Assert.assertEquals("zipcode is not correct", street.getIs_in_zip().iterator().next(), address.getZipCode());
	Assert.assertEquals("adm name is not correct", street.getIs_in_adm(), address.getState());
	Assert.assertEquals("place is not correct", street.getIs_in_place(), address.getDependentLocality());
	
	Assert.assertEquals("countryCode should be filled",street.getCountry_code(),address.getCountryCode());
    }

    @Test
    public void buildAddressResultDtoFromStreetsAndCitiesWithNullStreet() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = null;
	SolrResponseDto city = GisgraphyTestHelper.createSolrResponseDtoForCity();
	List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
	cities.add(city);
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromStreetsAndCities(streets, cities, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(1, addressResultsDto.getResult().size());
	Address address = addressResultsDto.getResult().get(0);
	Assert.assertEquals("latitude is not correct", city.getLat(), address.getLat());
	Assert.assertEquals("id is not correct", city.getFeature_id().longValue(), address.getId().longValue());
	Assert.assertEquals("longitude is not correct", city.getLng(), address.getLng());
	Assert.assertEquals("geocoding level is not correct", GeocodingLevels.CITY, address.getGeocodingLevel());
	Assert.assertNull("street name is not correct", address.getStreetName());
	Assert.assertNull("street type is not correct", address.getStreetType());
	Assert.assertEquals("city name is not correct", city.getName(), address.getCity());
	Assert.assertEquals("Adm Name should be the deeper one", city.getAdm2_name(), address.getState());
	Assert.assertEquals("countryCode should be filled",city.getCountry_code(),address.getCountryCode());
    }

    @Test
    public void buildAddressResultDtoFromStreetsAndCitiesWithNullStreetAndCity() {
	// setup
	GeocodingService geocodingService = new GeocodingService();
	List<SolrResponseDto> streets = null;
	List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
	// exercise
	AddressResultsDto addressResultsDto = geocodingService.buildAddressResultDtoFromStreetsAndCities(streets, cities, null);

	// verify
	Assert.assertNotNull("qtime should not be null", addressResultsDto.getQTime());
	Assert.assertNotNull("results should not be null, but at least empty list", addressResultsDto.getResult());
	Assert.assertEquals(0, addressResultsDto.getResult().size());
    }
    
    @Test
    public void testStatsShouldBeIncreaseForGeocode_addressQuery() {
	GeocodingService geocodingService = new GeocodingService() {
	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		return new ArrayList<SolrResponseDto>();
	    }
	    @Override
	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		return new ArrayList<SolrResponseDto>();
	    }
	};
	geocodingService.setStatsUsageService(statsUsageService);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	List<Address> addressList = new ArrayList<Address>() {
	    {
		Address address = new Address();
		address.setCity("city");
		add(address);
		
	    }
	};
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	gisgraphyConfig.setSearchForExactMatchWhenGeocoding(false);
	AddressResultsDto addressresults = new AddressResultsDto(addressList, 3L);
	statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
	EasyMock.replay(statsUsageService);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(addressresults);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	AddressQuery addressQuery = new AddressQuery("paris", "fr");
	geocodingService.setImporterConfig(new ImporterConfig());
	geocodingService.geocode(addressQuery);
	EasyMock.verify(statsUsageService);
    }

    @Test
    public void testStatsShouldBeIncreaseForGeocode_address() {
	GeocodingService geocodingService = new GeocodingService() {
	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		return new ArrayList<SolrResponseDto>();
	    }
	    
	    @Override
	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		return new ArrayList<SolrResponseDto>();
	    }
	};
	geocodingService.setStatsUsageService(statsUsageService);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	List<Address> addressList = new ArrayList<Address>() {
	    {
		Address address = new Address();
		address.setCity("city");
		add(address);
		
	    }
	};
	AddressResultsDto addressresults = new AddressResultsDto(addressList, 3L);
	statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
	EasyMock.replay(statsUsageService);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(addressresults);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	Address address = new Address();
	address.setCity("city");
	geocodingService.setImporterConfig(new ImporterConfig());
	geocodingService.geocode(address, "fr");
	EasyMock.verify(statsUsageService);
    }
    
    
    @Test
    public void testParsedAddressShouldBeSetForGeocodeAddress_city() {
	GeocodingService geocodingService = new GeocodingService() {
	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		return new ArrayList<SolrResponseDto>();
	    }
	    
	    @Override
	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		return new ArrayList<SolrResponseDto>();
	    }
	};
	geocodingService.setStatsUsageService(statsUsageService);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	List<Address> addressList = new ArrayList<Address>() {
	    {
		Address address = new Address();
		address.setCity("city");
		add(address);
		
	    }
	};
	AddressResultsDto addressresults = new AddressResultsDto(addressList, 3L);
	statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
	EasyMock.replay(statsUsageService);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(addressresults);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	Address address = new Address();
	address.setCity("city");
	geocodingService.setImporterConfig(new ImporterConfig());
	AddressResultsDto addressResultsDto = geocodingService.geocode(address, "fr");
	Assert.assertEquals("parsed address should be null when a structured address is provided",null, addressResultsDto.getParsedAddress());
	EasyMock.verify(statsUsageService);
    }
    
    @Test
    public void testParsedAddressShouldBeSetForGeocodeAddress_OnlyStreet() {
	findStreetCalled = false;
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected java.util.List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		findStreetCalled = true;
		return null;
	    }
	};
	geocodingService.setStatsUsageService(statsUsageService);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	Address address = new Address();
	address.setStreetName("foo");

	AddressResultsDto addressResultsDto = geocodingService.geocode(address, "ac");
	Assert.assertEquals("parsed address should be null when a structured address is provided",null, addressResultsDto.getParsedAddress());
	Assert.assertTrue(findStreetCalled);
    }
    
    @Test
    public void getBestCitySearchSentence(){
	GeocodingService geocodingService = new GeocodingService();
	
	//only city
	Address address = new Address();
	address.setCity("city with space");
	String sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("city with space",sentence);
	
	//only postTown
	address = new Address();
	address.setPostTown("postTown with space");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("postTown with space",sentence);
	
	//postTown and city
	address = new Address();
	address.setPostTown("postTown with space");
	address.setCity("city with space");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("city with space",sentence);
		
	
	//only zip
	address = new Address();
	address.setZipCode("75002");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("75002",sentence);
	
	//zip and city
	address = new Address();
	address.setCity("city with space");
	address.setZipCode("75002");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("city with space 75002",sentence);
	//---------------
	//dependent locality only
	address = new Address();
	address.setDependentLocality("dep loc");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("dep loc",sentence);
	
	//dependent locality and zip
	address = new Address();
	address.setDependentLocality("dep loc");
	address.setZipCode("75002");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("75002 dep loc",sentence);
	
	//dependent locality and state
	address = new Address();
	address.setDependentLocality("dep loc");
	address.setState("state");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("state dep loc",sentence);
	
	//dependent locality and state and zip
	address = new Address();
	address.setDependentLocality("dep loc");
	address.setState("state");
	address.setZipCode("75002");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("75002 state dep loc",sentence);
	
	//zip and state
	address = new Address();
	address.setState("state");
	address.setZipCode("75002");
	sentence = geocodingService.getBestCitySearchSentence(address);
	Assert.assertEquals("75002 state",sentence);
	
    }
    
    @Test
    public void mergeSolrResponseDto(){
    	GeocodingService geocodingService = new GeocodingService();
    	Assert.assertTrue(geocodingService.mergeSolrResponseDto(null, null).size()==0);
    	Assert.assertTrue(geocodingService.mergeSolrResponseDto(null, new ArrayList<SolrResponseDto>()).size()==0);
    	Assert.assertTrue(geocodingService.mergeSolrResponseDto(new ArrayList<SolrResponseDto>(), null).size()==0);
    	Assert.assertTrue(geocodingService.mergeSolrResponseDto(new ArrayList<SolrResponseDto>(), new ArrayList<SolrResponseDto>()).size()==0);
    	String is_in="city";
		SolrResponseDto street = GisgraphyTestHelper.createSolrResponseDtoForStreet(is_in);
		SolrResponseDto city = GisgraphyTestHelper.createSolrResponseDtoForCity();
		SolrResponseDto city_other = GisgraphyTestHelper.createSolrResponseDtoForCity_other();
		
		
		List<SolrResponseDto> list1 = new ArrayList<SolrResponseDto>();
		list1.add(street);
		List<SolrResponseDto> list2 = new ArrayList<SolrResponseDto>();
		list2.add(city);
		list2.add(null);
		list2.add(street);
		list2.add(city_other);
		
		//aproximative size greater than exact
		List<SolrResponseDto> mergeSolrResponseDto =geocodingService.mergeSolrResponseDto(list1, list2);
		Assert.assertTrue(mergeSolrResponseDto.size()==3);
		Assert.assertEquals(street,mergeSolrResponseDto.get(0));
		Assert.assertEquals(city,mergeSolrResponseDto.get(1));
		Assert.assertEquals(city_other,mergeSolrResponseDto.get(2));
		
		//exact matche size greater than aproximative
		mergeSolrResponseDto = geocodingService.mergeSolrResponseDto(list2, list1);
		Assert.assertTrue(mergeSolrResponseDto.size()==3);
		Assert.assertEquals(city,mergeSolrResponseDto.get(0));
		Assert.assertEquals(street,mergeSolrResponseDto.get(1));
		Assert.assertEquals(city_other,mergeSolrResponseDto.get(2));
		
    }

    @Test
    public void isGeocodable(){
    	GeocodingService service = new GeocodingService();
    	Address address = new Address();
    	address.setCity("city");
    	Assert.assertTrue(service.isGeocodable(address));
    	
    	address = new Address();
    	address.setZipCode("zipCode");
    	Assert.assertTrue(service.isGeocodable(address));
    	
    	address = new Address();
    	address.setStreetName("streetName");
    	Assert.assertTrue(service.isGeocodable(address));
    	
    	address = new Address();
    	address.setPostTown("postTown");
    	Assert.assertTrue(service.isGeocodable(address));
    	
    	address = new Address();
    	Assert.assertFalse(service.isGeocodable(address));
    	
    }
    
 /*  _                   _                      _ 
 ___| |_ _ __ _   _  ___| |_ _   _ _ __ ___  __| |
/ __| __| '__| | | |/ __| __| | | | '__/ _ \/ _` |
\__ \ |_| |  | |_| | (__| |_| |_| | | |  __/ (_| |
|___/\__|_|   \__,_|\___|\__|\__,_|_|  \___|\__,_|
                                                 
*/
    
    @Test(expected = IllegalArgumentException.class)
    public void geocodeStructuredAdressShouldThrowIfAddressIsNull() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = null;
	AddressQuery query = new StructuredAddressQuery(address, "US");
	geocodingService.geocode(query);
    }

  
    @Test(expected = GeocodingException.class)
    public void geocodeStructuredAdressShouldThrowIfCountryCodeIsNull() {
	IGeocodingService geocodingService = new GeocodingService();
	String countrycode = null;
	AddressQuery query = new StructuredAddressQuery(new Address(), countrycode);
	geocodingService.geocode(query);
    }

    @Test(expected = GeocodingException.class)
    public void geocodeStructuredAdressShouldThrowIfCountryCodeHasenTALengthOf2() {
	GeocodingService geocodingService = new GeocodingService();
	geocodingService.setStatsUsageService(statsUsageService);
	AddressQuery query = new StructuredAddressQuery(new Address(), "abc");
	geocodingService.geocode(query);
    }

    @Test
    public void geocodeStructuredAdressShouldCallGeocodeAddressIfParsedAddressIsSuccess() {
	GeocodeAdressCalled = false;
	GeocodingService geocodingService = new GeocodingService() {
	    @Override
	    public AddressResultsDto geocode(Address address, String countryCode) throws GeocodingException {
		GeocodeAdressCalled = true;
		return null;
	    }
	};
	ImporterConfig importerConfig = EasyMock.createMock(ImporterConfig.class);
	EasyMock.expect(importerConfig.isOpenStreetMapFillIsIn()).andStubReturn(true);
	geocodingService.setImporterConfig(importerConfig);
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setGisgraphyConfig(gisgraphyConfig);
	gisgraphyConfig.setUseAddressParserWhenGeocoding(true);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	List<Address> addressList = new ArrayList<Address>() {
	    {
	    	Address address = new Address();
			address.setStreetName("streetName");
			address.setCity("city");
			add(address);
	    }
	};
	AddressResultsDto addressresults = new AddressResultsDto(addressList, 3L);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andReturn(addressresults);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	AddressQuery query = new StructuredAddressQuery(new Address(), "ac");
	geocodingService.geocode(query);
	Assert.assertTrue(GeocodeAdressCalled);
    }
    
    
    

  /*          _     _                   
	  __ _  __| | __| |_ __ ___  ___ ___ 
	 / _` |/ _` |/ _` | '__/ _ \/ __/ __|
	| (_| | (_| | (_| | | |  __/\__ \__ \
	 \__,_|\__,_|\__,_|_|  \___||___/___/
	                                     

   * 
   */

    @Test
    public void geocodeAdressShouldCallFindStreetInTextIfCityIsNull() {
	findStreetCalled = false;
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected java.util.List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		findStreetCalled = true;
		return null;
	    }
	};
	geocodingService.setStatsUsageService(statsUsageService);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	Address address = new Address();
	address.setStreetName("foo");

	geocodingService.geocode(address, "ac");
	Assert.assertTrue(findStreetCalled);
    }

    @Test
    public void geocodeAdressShouldCallFindCityInTextIfCityIsNotNull_cityNotFound() {
	String city = "city";
	findStreetCalled = false;
	findCitiesCalled = false;
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		findCitiesCalled = true;
		return null;
	    }

	    @Override
	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		if (point != null) {
		    Assert.fail("when no city is found we shoud search without location restriction");
		}
		findStreetCalled = true;
		return null;
	    }
	};
	geocodingService.setImporterConfig(new ImporterConfig());
	geocodingService.setStatsUsageService(statsUsageService);
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	Address address = new Address();
	address.setStreetName("foo");
	address.setCity(city);

	geocodingService.geocode(address, "ac");
	Assert.assertTrue(findStreetCalled);
	Assert.assertTrue(findCitiesCalled);
    }

    @Test
    public void geocodeAdressShouldCallFindCityInTextIfCityIsNotNull_cityFound() {
	String city = "city";
	findStreetCalled = false;
	findCitiesCalled = false;
	populatecalled = false;
	final SolrResponseDto cityResult = EasyMock.createMock(SolrResponseDto.class);
	final Double latitude = 2.1d;
	EasyMock.expect(cityResult.getLat()).andStubReturn(latitude);
	final Double longitude = 5.2d;
	EasyMock.expect(cityResult.getLng()).andStubReturn(longitude);
	EasyMock.expect(cityResult.getFeature_id()).andStubReturn(123L);
	EasyMock.expect(cityResult.getName()).andStubReturn("name");
	EasyMock.replay(cityResult);
	GeocodingService geocodingService = new GeocodingService() {

	    @Override
	    protected void populateAddressFromCity(SolrResponseDto city, Address address) {
		populatecalled = true;
	    }

	    @Override
	    protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		findCitiesCalled = true;
		List<SolrResponseDto> cities = new ArrayList<SolrResponseDto>();
		cities.add(cityResult);
		return cities;
	    }

	    @Override
	    protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		Point checkedPoint = GeolocHelper.createPoint(longitude.floatValue(), latitude.floatValue());
		if (point.getX() != checkedPoint.getX() || point.getY() != checkedPoint.getY()) {
		    Assert.fail("when city is found we shoud search with location restriction");
		}
		findStreetCalled = true;
		return null;
	    }
	};
	geocodingService.setStatsUsageService(statsUsageService);
	geocodingService.setImporterConfig(new ImporterConfig());
	IAddressParserService mockAddressParserService = EasyMock.createMock(IAddressParserService.class);
	EasyMock.expect(mockAddressParserService.execute((AddressQuery) EasyMock.anyObject())).andStubReturn(null);
	EasyMock.replay(mockAddressParserService);
	geocodingService.setAddressParser(mockAddressParserService);
	Address address = new Address();
	address.setStreetName("foo");
	address.setCity(city);

	geocodingService.geocode(address, "ac");
	Assert.assertTrue(findStreetCalled);
	Assert.assertTrue(findCitiesCalled);
	Assert.assertTrue(populatecalled);
    }

    @Test(expected = GeocodingException.class)
    public void geocodeAddressShouldThrowIfAddressIsNull() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = null;
	geocodingService.geocode(address, "DE");
    }

    @Test(expected = GeocodingException.class)
    public void geocodeAddressShouldThrowIfStreetNameCityAndZipAreNull() {
	GeocodingService geocodingService = new GeocodingService();
	geocodingService.setStatsUsageService(statsUsageService);
	Address address = new Address();
	geocodingService.geocode(address, "De");
    }

    @Test(expected = GeocodingException.class)
    public void geocodeAddressShouldThrowIfStreetIntersection() {
	GeocodingService geocodingService = new GeocodingService();
	geocodingService.setStatsUsageService(statsUsageService);
	Address address = new Address();
	address.setStreetNameIntersection("intersection");
	geocodingService.geocode(address, "De");
    }

    @Test(expected = GeocodingException.class)
    public void geocodeAddressShouldNotThrowIfCountryCodeIsNull() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = new Address();
	geocodingService.geocode(address, null);
    }
    
    @Test(expected = GeocodingException.class)
    public void geocodeAddressShouldThrowIfCountryCodeHasOneLetter() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = new Address();
	geocodingService.geocode(address, "a");
    }
    
    @Test(expected = GeocodingException.class)
    public void geocodeAddressShouldThrowIfCountryisEmpty() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = new Address();
	geocodingService.geocode(address, " ");
    }
    
    @Test(expected = GeocodingException.class)
    public void geocodeAddressWithToolessInformations_null() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = new Address();
	geocodingService.geocode(address, "FR");
    }
    
    @Test(expected = GeocodingException.class)
    public void geocodeAddressWithToolessInformations_emptyString() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = new Address();
	address.setCity("");
	address.setStreetName("");
	address.setZipCode("");
	geocodingService.geocode(address, "FR");
    }

    @Test(expected = GeocodingException.class)
    public void geocodeAdressShouldThrowIfCountryCodeHasenTALengthOf2() {
	IGeocodingService geocodingService = new GeocodingService();
	Address address = new Address();
	geocodingService.geocode(address, "abc");
    }

    @Test(expected = GeocodingException.class)
    public void testGeocodeWithNullQueryShouldThrows() {
	IGeocodingService geocodingService = new GeocodingService();
	geocodingService.geocode(null);
    }

    @Test(expected = GeocodingException.class)
    public void testGeocodeToStringWithNullQueryShouldThrows() {
	IGeocodingService geocodingService = new GeocodingService();
	geocodingService.geocodeToString(null);
	fail("executeQueryToString does not accept null query");
    }

    @Test(expected = GeocodingException.class)
    public void testGeocodeAndSerializeWithNullQueryShouldThrows() {
	IGeocodingService geocodingService = new GeocodingService();
	geocodingService.geocodeAndSerialize(null, new ByteArrayOutputStream());
	fail("executeAndSerialize does not accept null query");
    }

    @Test(expected = GeocodingException.class)
    public void testGeocodeAndSerializeWithNullOutputStreamShouldThrows() {
	IGeocodingService geocodingService = new GeocodingService();
	geocodingService.geocodeAndSerialize(new AddressQuery("address", "XX"), null);
	fail("executeAndSerialize does not accept null query");
    }

    @Test
    public void testGeocodeToStringShouldTakeTheCallbackParameterIntoAccount() {
	IGeocodingService geocodingService = new GeocodingService() {
	    @Override
	    public AddressResultsDto geocode(AddressQuery query) throws GeocodingException {
		return new AddressResultsDto();
	    }
	};
	AddressQuery addressQuery = new AddressQuery("paris", "fr");
	String callBackName = "doIt";
	addressQuery.setCallback(callBackName);
	addressQuery.setFormat(OutputFormat.JSON);
	String result = geocodingService.geocodeToString(addressQuery);
	Assert.assertTrue(result.startsWith(callBackName));
    }

    @Test
    public void testGeocodeToStringShouldCallGeocode() {
	geocodeIsCalled = false;
	IGeocodingService geocodingService = new GeocodingService() {
	    @Override
	    public AddressResultsDto geocode(AddressQuery query) throws GeocodingException {
		geocodeIsCalled = true;
		return new AddressResultsDto();
	    }
	};
	AddressQuery addressQuery = new AddressQuery("paris", "fr");
	geocodingService.geocodeToString(addressQuery);
	Assert.assertTrue(geocodeIsCalled);
    }

    @Test
    public void testGeocodeAndSerializeShouldCallGeocode() {
	geocodeIsCalled = false;
	IGeocodingService geocodingService = new GeocodingService() {
	    @Override
	    public AddressResultsDto geocode(AddressQuery query) throws GeocodingException {
		geocodeIsCalled = true;
		return new AddressResultsDto();
	    }
	};
	AddressQuery addressQuery = new AddressQuery("paris", "fr");
	geocodingService.geocodeAndSerialize(addressQuery, new ByteArrayOutputStream());
	Assert.assertTrue(geocodeIsCalled);
    }
    
    @Test
    public void findHouseNumber(){
    	GeocodingService geocodingService = new GeocodingService();
    	Assert.assertEquals("4", geocodingService.findHouseNumber("4-6 rue de la gare 59000 lille",null));
    	Assert.assertEquals("165", geocodingService.findHouseNumber("165 rue de la gare 59000 lille",null));
    	Assert.assertEquals("3", geocodingService.findHouseNumber("3 rue de la gare 59000 lille",null));
    	Assert.assertEquals(null, geocodingService.findHouseNumber("3rd rue de la gare 59000 lille",null));
    	Assert.assertEquals(null, geocodingService.findHouseNumber("2nd rue de la gare 59000 lille",null));
    	Assert.assertEquals(null, geocodingService.findHouseNumber("1st rue de la gare 59000 lille",null));
    	Assert.assertEquals("36", geocodingService.findHouseNumber("36 rue de la gare 59000 lille",null));
    	Assert.assertEquals("367", geocodingService.findHouseNumber("367 rue de la gare 59000 lille",null));
    	Assert.assertEquals("3677", geocodingService.findHouseNumber("3677 rue de la gare 59000 lille",null));
    	Assert.assertEquals("5900", geocodingService.findHouseNumber("rue de la gare 5900 lille",null));
    	Assert.assertEquals(null, geocodingService.findHouseNumber("rue de la gare 59000 lille",null));
    }

   
}
