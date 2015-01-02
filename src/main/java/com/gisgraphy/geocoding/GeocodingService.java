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
import static com.gisgraphy.helper.StringHelper.isEmptyString;
import static com.gisgraphy.helper.StringHelper.isNotEmptyString;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressQuery;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.addressparser.IAddressParserService;
import com.gisgraphy.addressparser.StructuredAddressQuery;
import com.gisgraphy.addressparser.exception.AddressParserException;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.fulltext.SolrResponseDtoDistanceComparator;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.importer.ImporterConfig;
import com.gisgraphy.serializer.UniversalSerializer;
import com.gisgraphy.serializer.common.UniversalSerializerConstant;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsageType;
import com.gisgraphy.street.HouseNumberDto;
import com.gisgraphy.street.HouseNumberUtil;
import com.vividsolutions.jts.geom.Point;

/**
 * 
 * Geocode internationnal address via gisgraphy services
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
@Service
public class GeocodingService implements IGeocodingService {
	private IStatsUsageService statsUsageService;
	private ImporterConfig importerConfig;
	private IAddressParserService addressParser;
	private IFullTextSearchEngine fullTextSearchEngine;
	private GisgraphyConfig gisgraphyConfig;

	public final static int ACCEPT_DISTANCE_BETWEEN_CITY_AND_STREET = 30000;
	public final static Output LONG_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.LONG);
	public final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);
	public final static Pagination ONE_RESULT_PAGINATION = Pagination.paginate().from(0).to(1);
	public final static Pagination FIVE_RESULT_PAGINATION = Pagination.paginate().from(0).to(5);
	public final static SolrResponseDtoDistanceComparator comparator = new SolrResponseDtoDistanceComparator();
	public final static Pattern HOUSENUMBERPATTERN = Pattern.compile("((?:^\\b\\d{1,4})(?!(?:st\\b|th\\b|rd\\b|nd\\b))|(((?:\\b\\d{1,4}))\\b(?:[\\s,;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b))(?=\\w+)+?)|\\s?(?:\\b\\d{1,4}$))");

	/**
	 * The logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.geocoding.IGeocodingService#geocodeAndSerialize(com.gisgraphy
	 * .addressparser.AddressQuery, java.io.OutputStream)
	 */
	public void geocodeAndSerialize(AddressQuery query, OutputStream outputStream) throws GeocodingException {
		if (query == null) {
			throw new GeocodingException("Can not geocode a null query");
		}
		if (outputStream == null) {
			throw new GeocodingException("Can not serialize into a null outputStream");
		}
		AddressResultsDto geolocResultsDto = geocode(query);
		Map<String, Object> extraParameter = new HashMap<String, Object>();
		// extraParameter.put(GeolocResultsDtoSerializer.START_PAGINATION_INDEX_EXTRA_PARAMETER,
		// query.getFirstPaginationIndex());
		extraParameter.put(UniversalSerializerConstant.CALLBACK_METHOD_NAME, query.getCallback());
		UniversalSerializer.getInstance().write(outputStream, geolocResultsDto, false, extraParameter, query.getFormat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.geocoding.IGeocodingService#geocodeToString(com.gisgraphy
	 * .addressparser.AddressQuery)
	 */
	public String geocodeToString(AddressQuery query) throws GeocodingException {
		if (query == null) {
			throw new GeocodingException("Can not geocode a null query");
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		geocodeAndSerialize(query, outputStream);
		try {
			return outputStream.toString(Constants.CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("unknow encoding " + Constants.CHARSET);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.geocoding.IGeocodingService#geocode(java.lang.String)
	 */
	public AddressResultsDto geocode(AddressQuery query) throws GeocodingException {
		if (query == null) {
			throw new GeocodingException("Can not geocode a null query");
		}
		logger.info(query.toString());
		String countryCode = query.getCountry();
		if (countryCode !=null  && countryCode.trim().length() != 2) {
			throw new GeocodingException("countrycode should have two letters : " + countryCode);
		}
		if (query instanceof StructuredAddressQuery){
			Address address = ((StructuredAddressQuery)query).getStructuredAddress();
			if (logger.isDebugEnabled()) {
				logger.debug("structured address to geocode : '" + address + "' for country code : " + countryCode);
			}
			return geocode(address, countryCode);
		}
		String rawAddress = query.getAddress();
		if (isEmptyString(rawAddress)) {
			throw new GeocodingException("Can not geocode a null or empty address");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Raw address to geocode : '" + rawAddress + "' for country code : " + countryCode);
		}
		Long startTime = System.currentTimeMillis();
		AddressQuery addressQuery = new AddressQuery(rawAddress, countryCode);
		AddressResultsDto addressResultDto = null;
		logger.debug("is postal address : " +query.isPostal());
		if (gisgraphyConfig.useAddressParserWhenGeocoding || query.isPostal()) {
			try {
				logger.debug("address parser is enabled");
				addressResultDto = addressParser.execute(addressQuery);
			} catch (AddressParserException e) {
				logger.error("An error occurs during parsing of address" + e.getMessage(), e);
			}
		}
		if (addressResultDto != null && addressResultDto.getResult().size() >= 1 && isGeocodable(addressResultDto.getResult().get(0))) {
			if (logger.isDebugEnabled()) {
				logger.debug("successfully parsed address : " + rawAddress + " : " + addressResultDto.getResult().get(0));
			}
			Address address = addressResultDto.getResult().get(0);
			AddressResultsDto addressesDto = geocode(address, countryCode);
			addressesDto.setParsedAddress(address);
			return addressesDto;
		} else if (importerConfig.isOpenStreetMapFillIsIn()) {
			logger.debug("is_in is active");
			statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
			AddressResultsDto results;
			List<SolrResponseDto> aproximativeMatches = findStreetInText(rawAddress, countryCode, null);
			String houseNumber = findHouseNumber(rawAddress, countryCode);
			if (GisgraphyConfig.searchForExactMatchWhenGeocoding) {
				List<SolrResponseDto> exactMatches = findExactMatches(rawAddress, countryCode);
				List<SolrResponseDto> mergedResults = mergeSolrResponseDto(exactMatches, aproximativeMatches);
				results = buildAddressResultDtoFromSolrResponseDto(mergedResults, houseNumber);
			} else {
				results = buildAddressResultDtoFromStreetsAndCities(aproximativeMatches, null, houseNumber);
			}
			Long endTime = System.currentTimeMillis();
			long qTime = endTime - startTime;
			results.setQTime(qTime);
			logger.info("geocoding of "+query + " and country="+countryCode+" took " + (qTime) + " ms and returns "
					+ results.getNumFound() + " results");
			return results;
		} else {
			logger.debug("is_in is inactive");
			// we call the stats here because we don't want to call it twice
			// when we call geocode before
			statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
			if (logger.isDebugEnabled()) {
				logger.debug("unsuccessfull address parsing of  : '" + rawAddress + "'");
			}
			List<SolrResponseDto> cities = null;
			SolrResponseDto city = null;
			cities = findCitiesInText(rawAddress, countryCode);
			if (cities != null && cities.size() > 0) {
				city = cities.get(0);
			}
			if (city == null) {
				if (logger.isDebugEnabled()) {
					logger.debug(" no city found for '" + rawAddress + "'");
				}
				List<SolrResponseDto> streets = findStreetInText(rawAddress, countryCode, null);
				String houseNumber = findHouseNumber(rawAddress, countryCode);
				AddressResultsDto results = buildAddressResultDtoFromStreetsAndCities(streets, cities, houseNumber);
				Long endTime = System.currentTimeMillis();
				long qTime = endTime - startTime;
				results.setQTime(qTime);
				logger.info("geocoding of "+query + " and country="+countryCode+" took " + (qTime) + " ms and returns "
						+ results.getNumFound() + " results");
				return results;

			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("found city : " + city.getName() + " for '" + rawAddress + "'");
				}
				Point cityLocation = GeolocHelper.createPoint(city.getLng().floatValue(), city.getLat().floatValue());
				List<SolrResponseDto> streets = null;
				if (importerConfig.isOpenStreetMapFillIsIn()) {
					streets = findStreetInText(rawAddress, countryCode, cityLocation);
				} else {
					String addressNormalized = StringHelper.normalize(rawAddress);
					String cityNormalized = StringHelper.normalize(city.getName());
					String addressNormalizedWithoutCity = addressNormalized.replace(cityNormalized, "");
					if (isNotEmptyString(addressNormalizedWithoutCity)) {
						if (logger.isDebugEnabled()) {
							logger.debug("normalized address without city : " + addressNormalizedWithoutCity);
						}
						streets = findStreetInText(addressNormalizedWithoutCity, countryCode, cityLocation);
					}
				}
				String houseNumber = findHouseNumber(rawAddress, countryCode);
				AddressResultsDto results = buildAddressResultDtoFromStreetsAndCities(streets, cities, houseNumber);
				Long endTime = System.currentTimeMillis();
				long qTime = endTime - startTime;
				results.setQTime(qTime);
				logger.info("geocoding of "+query + " and country="+countryCode+" took " + (qTime) + " ms and returns "
						+ results.getNumFound() + " results");
				return results;

			}
		}

	}

	protected boolean isGeocodable(Address address) {
		if (isEmptyString(address.getStreetName()) && isEmptyString(address.getState()) && isEmptyString(address.getCity()) && isEmptyString(address.getZipCode()) && isEmptyString(address.getPostTown())) {
			logger.info(address+" is no geocodable");
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.geocoding.IGeocodingService#geocode(com.gisgraphy.addressparser
	 * .Address)
	 */
	public AddressResultsDto geocode(Address address, String countryCode) throws GeocodingException {
		if (address == null) {
			throw new GeocodingException("Can not geocode a null address");
		}
		if (countryCode!=null &&  countryCode.trim().length() != 2) {
			throw new GeocodingException("wrong countrycode : " + countryCode);
		}
		if (isIntersection(address)) {
			throw new GeocodingException("street intersection is not managed yet");
		}
		if (!isGeocodable(address)) {
			throw new GeocodingException("City, street name, posttown and zip is not set, we got too less informations to geocode ");
		}
		statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
		Long startTime = System.currentTimeMillis();
		if (isEmptyString(address.getCity()) && isEmptyString(address.getZipCode()) && isEmptyString(address.getPostTown())) {
			List<SolrResponseDto> streets = findStreetInText(address.getStreetName(), countryCode, null);
			AddressResultsDto results = buildAddressResultDtoFromStreetsAndCities(streets, null, address.getHouseNumber());
			//results.setParsedAddress(address);
			Long endTime = System.currentTimeMillis();
			long qTime = endTime - startTime;
			results.setQTime(qTime);
			logger.info("geocoding of "+address + " and country="+countryCode+" took " + (qTime) + " ms and returns "
					+ results.getNumFound() + " results");
			return results;
		} else {
			String bestSentence = getBestCitySearchSentence(address);
			List<SolrResponseDto> cities = null;
			cities = findCitiesInText(bestSentence, countryCode);
			Point cityLocation = null;
			if (cities != null && cities.size() > 0 && cities.get(0) != null) {
				logger.debug("city found "+cities.get(0).getName()+"/"+cities.get(0).getFeature_id());
				cityLocation = GeolocHelper.createPoint(cities.get(0).getLng().floatValue(), cities.get(0).getLat().floatValue());
			}
			// TODO iterate over cities
			List<SolrResponseDto> fulltextResultsDto = null;
			if (address.getStreetName() != null) {
				String streetSentenceToSearch = address.getStreetName();
				if (address.getPreDirection()!= null) {
					streetSentenceToSearch += address.getPreDirection() +" "+ address.getCity();
				}
				if (address.getPostDirection()!= null) {
					streetSentenceToSearch += streetSentenceToSearch+" " +address.getPostDirection();
				}
				fulltextResultsDto = findStreetInText(streetSentenceToSearch, countryCode, cityLocation);
			}
			AddressResultsDto results = buildAddressResultDtoFromStreetsAndCities(fulltextResultsDto, cities, address.getHouseNumber());
			//results.setParsedAddress(address);
			Long endTime = System.currentTimeMillis();
			long qTime = endTime - startTime;
			results.setQTime(qTime);
			logger.info("geocoding of "+address + " and country="+countryCode+" took " + (qTime) + " ms and returns "
					+ results.getNumFound() + " results");
			return results;
		}
	}

	protected String getBestCitySearchSentence(Address address) {
		String sentence = "";
		if (isNotEmptyString(address.getCity())) {
			sentence += " " + address.getCity();
		} else if (isNotEmptyString(address.getPostTown())){
			sentence += " " + address.getPostTown();
		}
		if (isNotEmptyString(address.getZipCode())) {
			sentence += " " + address.getZipCode();
		}
		String dependentLocality = address.getDependentLocality();
		String state = address.getState();
		String choice = "";
		if (isEmptyString(state) && isNotEmptyString(dependentLocality)) {
			choice = " " + dependentLocality;
		} else if (isNotEmptyString(state) && isEmptyString(dependentLocality)) {
			choice = " " + state;
		} else if (isNotEmptyString(state) && isNotEmptyString(dependentLocality)) {
			choice = " " + state + " " + dependentLocality;
		}
		return new String(sentence + choice).trim();
	}

	

	protected AddressResultsDto buildAddressResultDtoFromStreetsAndCities(List<SolrResponseDto> streets, List<SolrResponseDto> cities, String houseNumberToFind) {
		List<Address> addresses = new ArrayList<Address>();

		if (streets != null && streets.size() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("found " + streets.size() + " streets");
			}
			SolrResponseDto city = null;
			if (cities != null && cities.size() > 0) {
				city = cities.get(0);
			}
			String lastName=null;
			boolean housenumberFound =false;
			int numberOfStreetThatHaveTheSameName = 0;
			for (SolrResponseDto street : streets) {
				Address address = new Address();

				address.setLat(street.getLat());
				address.setLng(street.getLng());
				address.setId(street.getFeature_id());
				address.setCountryCode(street.getCountry_code());

				String streetName = street.getName();
				if (logger.isDebugEnabled() && streets != null) {
					logger.debug("=>street : " + streetName +" ("+street.getScore()+")");
				}
				address.setStreetName(streetName);
				address.setStreetType(street.getStreet_type());
				String is_in = street.getIs_in();
				if (!isEmptyString(is_in)) {
					address.setCity(is_in);
					address.setState(street.getIs_in_adm());
					if (street.getIs_in_zip()!=null && street.getIs_in_zip().size()>=1){
						address.setZipCode(street.getIs_in_zip().iterator().next());
					}
					address.setDependentLocality(street.getIs_in_place());
				} else {
					populateAddressFromCity(city, address);
				}
				//now search for house number!
				List<HouseNumberDto> houseNumbersList = street.getHouse_numbers();
				if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){
				if (!isEmptyString(streetName)){ 
					if(streetName.equals(lastName) && city!=null){//probably the same street
						if (housenumberFound){
							continue;
							//do nothing it has already been found in the street
							//TODO do we have to search and if we find, we add it?
						}else {
							numberOfStreetThatHaveTheSameName++;
							HouseNumberDto houseNumber = searchHouseNumber(houseNumberToFind,houseNumbersList,street.getCountry_code());
							if (houseNumber !=null){
								housenumberFound=true;
								address.setHouseNumber(houseNumber.getNumber());
								address.setLat(houseNumber.getLocation().getY());
								address.setLng(houseNumber.getLocation().getX());
								//remove the last results added
								for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
									addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
								}
							} else {
								housenumberFound=false;
							}
						}
					} else { //the streetName is different, 
						numberOfStreetThatHaveTheSameName=0;
						HouseNumberDto houseNumber = searchHouseNumber(houseNumberToFind,houseNumbersList,street.getCountry_code());
						if (houseNumber !=null){
							housenumberFound=true;
							address.setHouseNumber(houseNumber.getNumber());
							address.setLat(houseNumber.getLocation().getY());
							address.setLng(houseNumber.getLocation().getX());
						} else {
							housenumberFound=false;
						}
					}
				} else {//streetname is null, we search for housenumber anyway
					numberOfStreetThatHaveTheSameName=0;
					HouseNumberDto houseNumber = searchHouseNumber(houseNumberToFind,houseNumbersList,street.getCountry_code());
					if (houseNumber !=null){
						housenumberFound=true;
						address.setHouseNumber(houseNumber.getNumber());
						address.setLat(houseNumber.getLocation().getY());
						address.setLng(houseNumber.getLocation().getX());
					} else {
						housenumberFound=false;
					}
				}
				}
				lastName=streetName;
				address.getGeocodingLevel();//force calculation of geocodingLevel
				addresses.add(address);

			}
		} else {
			if (cities != null && cities.size() > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("No street found, only cities");
				}
				for (SolrResponseDto city : cities) {
					// the best we can do is city
					Address address = buildAddressFromCity(city);
					address.getGeocodingLevel();//force calculation of geocodingLevel
					addresses.add(address);
				}
			} else if (logger.isDebugEnabled()) {
				logger.debug("No street and no city found");
			}
		}
		return new AddressResultsDto(addresses, 0L);
	}

	protected HouseNumberDto searchHouseNumber(String houseNumberToFind, List<HouseNumberDto> houseNumbersList,String countryCode) {
		if(houseNumberToFind==null || houseNumbersList==null || houseNumbersList.size()==0){
			return null;
		}
		String HouseNumberToFind;
		if (countryCode!=null && ("SK".equalsIgnoreCase(countryCode) || "CZ".equalsIgnoreCase(countryCode))){
			HouseNumberToFind = HouseNumberUtil.normalizeSkCzNumber(houseNumberToFind);
		} else {
			HouseNumberToFind = HouseNumberUtil.normalizeNumber(houseNumberToFind);
		}
		for (HouseNumberDto candidate :houseNumbersList){
			if (candidate!=null && candidate.getNumber()!=null && candidate.getNumber().equals(HouseNumberToFind)){
				logger.info("house number candidate found : "+candidate.getNumber());
				return candidate;
			}
		}
		logger.info("no house number candidate found for "+houseNumberToFind);
		return null;
	}

	protected AddressResultsDto buildAddressResultDtoFromSolrResponseDto(List<SolrResponseDto> solResponseDtos, String houseNumberToFind) {
		List<Address> addresses = new ArrayList<Address>();

		if (solResponseDtos != null && solResponseDtos.size() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("found " + solResponseDtos.size() + " results");
			}
			String lastName=null;
			String lastIsin=null;
			boolean housenumberFound =false;
			int numberOfStreetThatHaveTheSameName = 0;
			for (SolrResponseDto solrResponseDto : solResponseDtos) {
				Address address = new Address();
				if (solrResponseDto == null) {
					continue;
				}
				address.setName(solrResponseDto.getName());
				address.setLat(solrResponseDto.getLat());
				address.setLng(solrResponseDto.getLng());
				address.setId(solrResponseDto.getFeature_id());
				address.setCountryCode(solrResponseDto.getCountry_code());
				if (solrResponseDto.getPlacetype().equalsIgnoreCase(Adm.class.getSimpleName())) {
					address.setState(solrResponseDto.getName());
				}else if (solrResponseDto.getAdm2_name() != null) {
					address.setState(solrResponseDto.getAdm2_name());
				} else if (solrResponseDto.getAdm1_name() != null) {
					address.setState(solrResponseDto.getAdm1_name());
				} 
				if (solrResponseDto.getZipcodes() != null && solrResponseDto.getZipcodes().size() > 0) {
					address.setZipCode(solrResponseDto.getZipcodes().iterator().next());
				}
				if (solrResponseDto.getPlacetype().equalsIgnoreCase(Street.class.getSimpleName())) {
					String streetName = solrResponseDto.getName();
					String isIn = solrResponseDto.getIs_in();
					if (!isEmptyString(solrResponseDto.getName())){ 
						if(streetName.equals(lastName) && isIn!=null && isIn.equalsIgnoreCase(lastIsin)){//probably the same street
							if (housenumberFound){
								continue;
								//do nothing it has already been found in the street
								//TODO do we have to search and if we find, we add it?
							}else {
								numberOfStreetThatHaveTheSameName++;
							address.setStreetName(solrResponseDto.getName());
							address.setStreetType(solrResponseDto.getStreet_type());
							address.setCity(solrResponseDto.getIs_in());
							address.setState(solrResponseDto.getIs_in_adm());
							if (solrResponseDto.getIs_in_zip()!=null && solrResponseDto.getIs_in_zip().size()>=1){
								address.setZipCode(solrResponseDto.getIs_in_zip().iterator().next());
							}
							address.setDependentLocality(solrResponseDto.getIs_in_place());
							//now search for houseNumber
							List<HouseNumberDto> houseNumbersList = solrResponseDto.getHouse_numbers();
							if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){
								HouseNumberDto houseNumber = searchHouseNumber(houseNumberToFind,houseNumbersList,solrResponseDto.getCountry_code());
								if (houseNumber !=null){
									housenumberFound=true;
									address.setHouseNumber(houseNumber.getNumber());
									address.setLat(houseNumber.getLocation().getY());
									address.setLng(houseNumber.getLocation().getX());
									//remove the last results added
									for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
										addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
									}
								} else{
									housenumberFound=false;
								}
							}
						}
						} else { //the streetName is different, 
							
							//remove the last results added
							for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
								addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
							}
							numberOfStreetThatHaveTheSameName=0;
							//populate fields
							address.setStreetName(solrResponseDto.getName());
							address.setStreetType(solrResponseDto.getStreet_type());
							address.setCity(solrResponseDto.getIs_in());
							address.setState(solrResponseDto.getIs_in_adm());
							if (solrResponseDto.getIs_in_zip()!=null && solrResponseDto.getIs_in_zip().size()>=1){
								address.setZipCode(solrResponseDto.getIs_in_zip().iterator().next());
							}
							address.setDependentLocality(solrResponseDto.getIs_in_place());
							//search for housenumber
							List<HouseNumberDto> houseNumbersList = solrResponseDto.getHouse_numbers();
							if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){
							HouseNumberDto houseNumber = searchHouseNumber(houseNumberToFind,houseNumbersList,solrResponseDto.getCountry_code());
							if (houseNumber !=null){
								housenumberFound=true;
								address.setHouseNumber(houseNumber.getNumber());
								address.setLat(houseNumber.getLocation().getY());
								address.setLng(houseNumber.getLocation().getX());
							} else {
								housenumberFound=false;
							}
							}
						}
			  } else {//streetname is null, we search for housenumber anyway
				  address.setStreetType(solrResponseDto.getStreet_type());
					address.setCity(solrResponseDto.getIs_in());
					address.setState(solrResponseDto.getIs_in_adm());
					if (solrResponseDto.getIs_in_zip()!=null && solrResponseDto.getIs_in_zip().size()>=1){
						address.setZipCode(solrResponseDto.getIs_in_zip().iterator().next());
					}
					address.setDependentLocality(solrResponseDto.getIs_in_place());
				  List<HouseNumberDto> houseNumbersList = solrResponseDto.getHouse_numbers();
					if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){
					HouseNumberDto houseNumber = searchHouseNumber(houseNumberToFind,houseNumbersList,solrResponseDto.getCountry_code());
					if (houseNumber !=null){
						housenumberFound=true;
						address.setHouseNumber(houseNumber.getNumber());
						address.setLat(houseNumber.getLocation().getY());
						address.setLng(houseNumber.getLocation().getX());
					} else {
						housenumberFound=false;
					}
				}
			  }
					lastName=streetName;
					lastIsin = isIn;
				} else if (solrResponseDto.getPlacetype().equalsIgnoreCase(City.class.getSimpleName())){
					address.setCity(solrResponseDto.getName());
				} else if (solrResponseDto.getPlacetype().equalsIgnoreCase(CitySubdivision.class.getSimpleName())) {
					address.setQuarter(solrResponseDto.getName());
				}
				 
				if (logger.isDebugEnabled() && solrResponseDto != null) {
					logger.debug("=>place (" + solrResponseDto.getFeature_id()+") : "+solrResponseDto.getName() +" in "+solrResponseDto.getIs_in());
				}
				address.getGeocodingLevel();//force calculation of geocodingLevel
				addresses.add(address);

			}
		}
		return new AddressResultsDto(addresses, 0L);
	}

	private Address buildAddressFromCity(SolrResponseDto city) {
		Address address = new Address();
		address.setLat(city.getLat());
		address.setLng(city.getLng());
		populateAddressFromCity(city, address);
		address.setId(city.getFeature_id());
		return address;
	}
	

	protected void populateAddressFromCity(SolrResponseDto city, Address address) {
		if (city != null) {
			address.setCity(city.getName());
			if (city.getAdm2_name() != null) {
				address.setState(city.getAdm2_name());
			} else if (city.getAdm1_name() != null) {
				address.setState(city.getAdm1_name());
			} else if (city.getIs_in_adm()!=null){
				address.setState(city.getIs_in_adm());
			}
			if (city.getZipcodes() != null && city.getZipcodes().size() > 0) {
				address.setZipCode(city.getZipcodes().iterator().next());
			} else if (city.getIs_in_zip()!=null && city.getIs_in_zip().size()>=1){
				address.setZipCode(city.getIs_in_zip().iterator().next());
			}
			address.setCountryCode(city.getCountry_code());
			address.setDependentLocality(city.getIs_in_place());
		}
	}

	private boolean isIntersection(Address address) {
		return address.getStreetNameIntersection() != null;
	}

	protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		return findInText(text, countryCode, null, com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE);
	}

	protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point) {
		List<SolrResponseDto> streets = findInText(text, countryCode, point, com.gisgraphy.fulltext.Constants.STREET_PLACETYPE);
		Point location;
		if (point != null) {
			for (SolrResponseDto solrResponseDto : streets) {
				Double longitude = solrResponseDto.getLng();
				Double latitude = solrResponseDto.getLat();
				if (latitude != null && longitude != null) {
					location = GeolocHelper.createPoint(longitude.floatValue(), latitude.floatValue());
					Double distance = GeolocHelper.distance(location, point);
					solrResponseDto.setDistance(distance);
				}
			}
			Collections.sort(streets, comparator);
		}
		return streets;
	}

	protected List<SolrResponseDto> findInText(String text, String countryCode, Point point, Class<?>[] placetypes) {
		if (isEmptyString(text)) {
			return new ArrayList<SolrResponseDto>();
		}
		Output output;
		if (placetypes != null && placetypes.length == 1 && placetypes[0] == Street.class) {
			output = MEDIUM_OUTPUT;
		} else {
			output = LONG_OUTPUT;
		}
		FulltextQuery query = new FulltextQuery(text, Pagination.DEFAULT_PAGINATION, output, placetypes, countryCode);
		query.withAllWordsRequired(false).withoutSpellChecking();
		if (point != null) {
			query.around(point);
			query.withRadius(ACCEPT_DISTANCE_BETWEEN_CITY_AND_STREET);
		}
		FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
		if (results.getResultsSize() >= 1) {
			return results.getResults();
		} else {
			return new ArrayList<SolrResponseDto>();
		}
	}

	/**
	 * @param exactMatches
	 * @param aproximativeMatches
	 * @return a list of {@link SolrResponseDto} with
	 *         list1[0],list2[0],list1[1],list2[1],... it remove duplicates and
	 *         null
	 */
	protected List<SolrResponseDto> mergeSolrResponseDto(List<SolrResponseDto> exactMatches, List<SolrResponseDto> aproximativeMatches) {
		/*find common id and put them first
		retirer duplicate de exact (si street)
		retirer duplicate de approximate (si street)
		merger*/
		if (exactMatches == null || exactMatches.size() == 0) {
			if (aproximativeMatches == null) {
				return new ArrayList<SolrResponseDto>();
			} else {
				return aproximativeMatches;
			}
		} else if (aproximativeMatches == null || aproximativeMatches.size() == 0) {
			return exactMatches;

		} else {
			List<SolrResponseDto> merged = new ArrayList<SolrResponseDto>();
			int maxSize = Math.max(exactMatches.size(), aproximativeMatches.size());
			for (int i = 0; i < maxSize; i++) {
				if (i < exactMatches.size() && !merged.contains(exactMatches.get(i)) && exactMatches.get(i) != null) {
					merged.add(exactMatches.get(i));
				}
				if (i < aproximativeMatches.size() && !merged.contains(aproximativeMatches.get(i)) && aproximativeMatches.get(i) != null) {
					merged.add(aproximativeMatches.get(i));
				}
			}
			return merged;
		}
	}

	protected List<SolrResponseDto> findExactMatches(String text, String countryCode) {
		if (isEmptyString(text)) {
			return new ArrayList<SolrResponseDto>();
		}
		FulltextQuery query = new FulltextQuery(text, FIVE_RESULT_PAGINATION, LONG_OUTPUT, ADDRESSES_PLACETYPE, countryCode);
		query.withAllWordsRequired(true).withoutSpellChecking();
		FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
		if (results.getResultsSize() >= 1) {
			return results.getResults();
		} else {
			return new ArrayList<SolrResponseDto>();
		}
	}
	
	protected String findHouseNumber(String address,String countryCode){
		Matcher m = HOUSENUMBERPATTERN.matcher(address);
		if (m.find()) {
			String houseNumber = m.group();
			logger.info("found house number"+houseNumber+" in "+address);
			return houseNumber.trim();
		} else {
			logger.info("no house number found in "+address);
			return null;
		}
		
	}

	@Autowired
	public void setAddressParser(IAddressParserService addressParser) {
		this.addressParser = addressParser;
	}

	@Autowired
	public void setFullTextSearchEngine(IFullTextSearchEngine fullTextSearchEngine) {
		this.fullTextSearchEngine = fullTextSearchEngine;
	}

	@Autowired
	public void setStatsUsageService(IStatsUsageService statsUsageService) {
		this.statsUsageService = statsUsageService;
	}

	@Autowired
	public void setImporterConfig(ImporterConfig importerConfig) {
		this.importerConfig = importerConfig;
	}

	@Autowired
	public void setGisgraphyConfig(GisgraphyConfig gisgraphyConfig) {
		this.gisgraphyConfig = gisgraphyConfig;
	}

}
