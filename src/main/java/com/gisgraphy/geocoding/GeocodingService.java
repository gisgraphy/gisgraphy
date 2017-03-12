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

import static com.gisgraphy.helper.StringHelper.isEmptyString;
import static com.gisgraphy.helper.StringHelper.isNotEmptyString;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.addressparser.exception.AddressParserException;
import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.addressparser.format.DisplayMode;
import com.gisgraphy.compound.Decompounder;
import com.gisgraphy.compound.Decompounder.state;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.HouseNumberAddressDto;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.FullTextSearchEngine;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextQuerySolrHelper;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.SmartStreetDetection;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.fulltext.SolrResponseDtoDistanceComparator;
import com.gisgraphy.helper.CountryDetector;
import com.gisgraphy.helper.CountryDetectorDto;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.importer.ImporterConfig;
import com.gisgraphy.importer.LabelGenerator;
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
	private static final Pattern GERMAN_SYNONYM_PATTEN = Pattern.compile("(?<=\\w)(str\\b)[\\.]?",Pattern.CASE_INSENSITIVE);
	private static final int INTERPOLATION_CURVE_TOLERANCE = 45;
	private IStatsUsageService statsUsageService;
	private ImporterConfig importerConfig;
	private IAddressParserService addressParser;
	private FullTextSearchEngine fullTextSearchEngine;
	private GisgraphyConfig gisgraphyConfig;
	
	private LabelGenerator labelGenerator = LabelGenerator.getInstance();
	private BasicAddressFormater addressFormater = BasicAddressFormater.getInstance();
	
	CountryDetector countryDetector = new CountryDetector();
	
	SmartStreetDetection smartStreetDetection = new SmartStreetDetection();

	public final static int ACCEPT_DISTANCE_BETWEEN_CITY_AND_STREET = 15000;
	public final static Output LONG_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.LONG);
	public final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);
	public final static Output FULL_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.FULL);
	
	
	public final static Pagination ONE_RESULT_PAGINATION = Pagination.paginate().from(0).to(1);
	public final static Pagination FIVE_RESULT_PAGINATION = Pagination.paginate().from(0).to(5);
	public final static Pagination TEN_RESULT_PAGINATION = Pagination.paginate().from(0).to(10);
	public final static SolrResponseDtoDistanceComparator comparator = new SolrResponseDtoDistanceComparator();
	//public final static Pattern HOUSENUMBERPATTERN = Pattern.compile("((((?:\\b\\d{1,4}[\\-\\–\\一]\\d{1,4}))\\b(?:[\\s,;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b))(?=\\w+)+?))");
	public final static Pattern HOUSENUMBERPATTERN = Pattern.compile("((("
			+ "(?:\\b\\d{1,4}[\\-\\–\\一]\\d{1,4}))\\b(?:[\\s,;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b))(?=\\w+)+?)"
			+ "|(?:^\\b\\d{1,4}(?:\\s?(?:[a-d]\\b\\s)?)\\b)(?:\\s?(?:bis|ter)?)(?!(?:st\\b|th\\b|rd\\b|nd\\b))"
			+ "|(((?:\\b\\d{1,4}(?:\\s?(?:[a-d]\\b)?)))\\b(?:[\\s,;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b))(?=\\w+)+?)"
			+ "|\\s?(?:\\b\\d{1,4}\\s?(?:[a-d])?\\b$)"
			+")",
			Pattern.CASE_INSENSITIVE);
	
	/*public final static Pattern HOUSENUMBERPATTERN = Pattern.compile("((("
			+ "(?:\\b\\d{1,4}[\\-\\–\\一]\\d{1,4}))\\b(?:[\\s,;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b))(?=\\w+)+?)"
			//+ "|(?:^\\b\\d{1,4}(?:\\s?(?:[a-d]\\b\\s)?)\\b)(?:\\s?(?:bis|ter)?)(?!(?:st\\b|th\\b|rd\\b|nd\\b))"
			+ "|(((?:\\b\\d{1,4}(?:\\s?(?:[a-d]\\b[\\s,])?)))\\b(?:[\\s,;]+)(?!(?:st\\b|th\\b|rd\\b|nd\\b))(?=\\w+)+?)"
			//+ "|\\s?(?:\\b\\d{1,4}\\s?(?:[a-d])?\\b$)"
			+")",
			Pattern.CASE_INSENSITIVE);
			
			*/
	public final static Pattern FIRST_NUMBER_EXTRACTION_PATTERN = Pattern.compile("^([0-9]+)");
	public final static List<String> countryWithZipIs4Number= new ArrayList<String>(){
		{
			add("GE");
			add("AS");
			add("AU");
			add("BD");
			add("CH");
			add("CK");
			add("CR");
			add("CY");
			add("HU");
			add("HM");
			add("LR");
			add("SJ");
			add("MK");
			add("MZ");
			add("NE");
			add("NZ");
			add("PH");
			add("VE");
			add("CV");
			add("CX");
			add("ET");
			add("GW");
			add("ZA");
			add("LI");
			add("LU");
			add("PY");
			}
	};
	
	public final static List<String> countryWithZipIs3Number= new ArrayList<String>(){
		{
			add("GN");
			add("IS");
			add("LS");
			add("OM");
			add("PG");
			}
	};
	
	
	Decompounder decompounder = new Decompounder();

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
			AddressResultsDto addressResultsDto = geocode(address, countryCode);
			if (shouldSetParseAddress(query)){
				addressResultsDto.setParsedAddress(address);
			}
			return addressResultsDto;
		}
		String rawAddress = query.getAddress();
		if (isEmptyString(rawAddress)) {
			throw new GeocodingException("Can not geocode a null or empty address");
		}
	//	rawAddress = prepareQuery(rawAddress);
		//always search for country to remove it
			CountryDetectorDto detectorDto = countryDetector.detectAndRemoveCountry(rawAddress);
			if (detectorDto != null && detectorDto.getCountryCode()!=null){
				if (detectorDto.getAddress()!= null && !detectorDto.getAddress().trim().equals("")){
					rawAddress = detectorDto.getAddress();
					if (countryCode ==null){
						countryCode = detectorDto.getCountryCode();
					}
				} else {
					//it is a country we use the raw address without countrycode.
					List<SolrResponseDto> countries = findInText(rawAddress,null,null,com.gisgraphy.fulltext.Constants.ONLY_COUNTRY_PLACETYPE, false, null);
					return buildAddressResultDtoFromSolrResponseDtoCountry(countries);
					
				}
			//}
		}
		if (countryCode !=null  && countryCode.trim().length() != 2) {
			throw new GeocodingException("countrycode should have two letters : " + countryCode);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Raw address to geocode : '" + rawAddress + "' for country code : " + countryCode);
		}
		Long startTime = System.currentTimeMillis();
		AddressQuery addressQuery = new AddressQuery(rawAddress, countryCode);
		AddressResultsDto addressResultDto = null;
		logger.debug("is postal address : " +query.isPostal());
		boolean needParsing = needParsing(query.getAddress());
		if ((gisgraphyConfig.useAddressParserWhenGeocoding || query.isPostal()) && needParsing) {
			try {
				logger.debug("address parser is enabled");
				addressResultDto = addressParser.execute(addressQuery);
			} catch (AddressParserException e) {
				logger.error("An error occurs during parsing of address" + e.getMessage(), e);
			}
		} else {
			logger.debug("won't parse "+rawAddress);
		}
		if (addressResultDto != null && addressResultDto.getResult().size() >= 1 && isGeocodable(addressResultDto.getResult().get(0))) {
			if (logger.isDebugEnabled()) {
				logger.debug("successfully parsed address : " + rawAddress + " : " + addressResultDto.getResult().get(0));
			}
			Address address = addressResultDto.getResult().get(0);
			AddressResultsDto addressesDto = geocode(address, countryCode);
			if (shouldSetParseAddress(query)){
				addressesDto.setParsedAddress(address);
			}
			return addressesDto;
		} else
			//if (importerConfig.isOpenStreetMapFillIsIn()) 
			{
		//	logger.debug("is_in is active");
			statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
			AddressResultsDto results;
			HouseNumberAddressDto houseNumberAddressDto = findHouseNumber(rawAddress, countryCode);
			String newAddress = rawAddress;
			
			String houseNumber = null;
			if (houseNumberAddressDto != null){
				houseNumber = houseNumberAddressDto.getHouseNumber();
				newAddress = houseNumberAddressDto.getAddressWithoutHouseNumber();
			} 
			List<String> streettypes = smartStreetDetection.getStreetTypes(newAddress);
			for (String streettype : streettypes){
				logger.info("found street type : "+streettype);
			}
				boolean smartstreetdetection = false;
			String alternativeGermanAddress =null;
			if (streettypes!=null && streettypes.size()==1){
				smartstreetdetection = true;
				if (Decompounder.isDecompoudCountryCode(countryCode) || decompounder.getSate(newAddress)!=state.NOT_APPLICABLE){
					/*logger.error("find specific german address");
					alternativeGermanAddress = decompounder.getOtherFormatForText(newAddress);
					logger.error("alternativeGermanAddress= "+alternativeGermanAddress);
					alternativeGermanAddress = replaceGermanSynonyms(alternativeGermanAddress);*/
					newAddress = replaceGermanSynonyms(newAddress);
					logger.error("new rawAddress with synonyms ="+newAddress);
					//logger.error("new alternative with synonyms ="+alternativeGermanAddress);
					
				} else {
					logger.error("don't proces specific german address");
				}
			}
				results = doSearch(newAddress,alternativeGermanAddress, countryCode, 
						needParsing,houseNumber, false, query.getPoint(), query.getRadius(), smartstreetdetection);
				
					
					
					//try in fuzzy
					if (results.getNumFound()==0 && query.isFuzzy()){
						results = doSearch(newAddress,alternativeGermanAddress, countryCode,
								needParsing,houseNumber, true, query.getPoint(), query.getRadius(), smartstreetdetection);
					}
					
				//------------------------------------------------------------------------------------
				/*if (!needParsing && exactMatches!=null && exactMatches.size() >=1){
					results = buildAddressResultDtoFromSolrResponseDto(exactMatches, houseNumber);
				} else {*/
				/*List<SolrResponseDto> cities = null;
				cities = findCitiesInText(newAddress, countryCode);
				Point cityLocation = null;
				String cityName=null;
				if (cities != null && cities.size() > 0 && cities.get(0) != null) {
					logger.debug("city found "+cities.get(0).getName()+"/"+cities.get(0).getFeature_id());
					cityLocation = GeolocHelper.createPoint(cities.get(0).getLng().floatValue(), cities.get(0).getLat().floatValue());
					cityName = cities.get(0).getName();
				}
				List<SolrResponseDto> fulltextResultsDto = null;
							*/		
				/*if (cityName!=null){
					/*String addressNormalized = StringHelper.normalize(newAddress);
					String cityNormalized = StringHelper.normalize(cityName);
					String addressNormalizedWithoutCity = addressNormalized.replace(cityNormalized, "");
					//String addressNormalizedWithoutCity = newAddress;
						if (isNotEmptyString(addressNormalizedWithoutCity)) {
							//if (logger.isDebugEnabled()) {
								logger.debug("normalized address without city '+"+cityNormalized+"' : was '" +addressNormalized+"' and is now " + addressNormalizedWithoutCity);
							//}
							fulltextResultsDto = findStreetInText(addressNormalizedWithoutCity, countryCode, cityLocation);
						} 
				} else {
					fulltextResultsDto = findStreetInText(newAddress, countryCode, cityLocation);
				}*/
				
				//fulltextResultsDto = findStreetInText(newAddress, countryCode, cityLocation);
				//------------------------------------------------------------------------------------------------
				
				
				
				
				
				/*	List<SolrResponseDto> mergedResults = mergeSolrResponseDto(exactMatches, fulltextResultsDto);
					results = buildAddressResultDtoFromSolrResponseDto(mergedResults, houseNumber);*/
					//results = buildAddressResultDtoFromStreetsAndCities(mergedResults, cities, houseNumber);
				//}
			Long endTime = System.currentTimeMillis();
			long qTime = endTime - startTime;
			results.setQTime(qTime);
			logger.info("geocoding of "+query + " and country="+countryCode+" took " + (qTime) + " ms and returns "
					+ results.getNumFound() + " results");
			return results;
		}//disable all is_in inactive code 
		/*else {
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
				HouseNumberAddressDto houseNumberAddressDto = findHouseNumber(rawAddress, countryCode);
				String newAddress = rawAddress;
				String houseNumber = null;
				if (houseNumberAddressDto != null){
					houseNumber = houseNumberAddressDto.getHouseNumber();
					newAddress = houseNumberAddressDto.getAddressWithoutHouseNumber();
				} 
				List<SolrResponseDto> streets = findStreetInText(newAddress, countryCode, null, false, null);
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
				HouseNumberAddressDto houseNumberAddressDto = findHouseNumber(rawAddress, countryCode);
				String newAddress = rawAddress;
				String houseNumber = null;
				if (houseNumberAddressDto != null){
					houseNumber = houseNumberAddressDto.getHouseNumber();
					newAddress = houseNumberAddressDto.getAddressWithoutHouseNumber();
				} 
				Point cityLocation = GeolocHelper.createPoint(city.getLng().floatValue(), city.getLat().floatValue());
				List<SolrResponseDto> streets = null;
				if (importerConfig.isOpenStreetMapFillIsIn()) {
					streets = findStreetInText(newAddress, countryCode, cityLocation, false, null);
				} else {
					String addressNormalized = StringHelper.normalize(newAddress);
					String cityNormalized = StringHelper.normalize(city.getName());
					String addressNormalizedWithoutCity = addressNormalized.replace(cityNormalized, "");
					if (isNotEmptyString(addressNormalizedWithoutCity)) {
						if (logger.isDebugEnabled()) {
							logger.debug("normalized address without city : " + addressNormalizedWithoutCity);
						}
						streets = findStreetInText(addressNormalizedWithoutCity, countryCode, cityLocation, false, null);
					}
				}
				AddressResultsDto results = buildAddressResultDtoFromStreetsAndCities(streets, cities, houseNumber);
				Long endTime = System.currentTimeMillis();
				long qTime = endTime - startTime;
				results.setQTime(qTime);
				logger.info("geocoding of "+query + " and country="+countryCode+" took " + (qTime) + " ms and returns "
						+ results.getNumFound() + " results");
				return results;

			}
		}*/

	}

	private String prepareQueryCompound(String rawAddress) {
		if (rawAddress == null){
			return rawAddress;
		}
		/*Pattern p = Pattern.compile(" (?:straße|str|strasse)\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(rawAddress);
		if (m.find()){
			logger.error("will concatenate address"+rawAddress);
			return prepareQueryConcatenate(rawAddress);
		}*/
		
		
		
		String newAddress = "";
		List<String> words = new ArrayList<String>(){
			{
				add("weg.");
				add("clic");
				add("str.");
				add("straße.");
				add("strasse.");
				add("plätze.");
				add("platze.");
				add("wald.");
				add("str.");
			}
		};
		Decompounder decompounder = new Decompounder(words);
		String[] addressWords = rawAddress.split(",|;|-|\\s");
		for (String addressWord:addressWords){
			String[] decompoundWords = decompounder.decompound(addressWord);
			for (String word:decompoundWords){
				newAddress= newAddress+", "+word;
			}
			//if (decompoundWords.length>1){
		//		newAddress = newAddress+ " "+addressWord+" ";
			//}
		}
		newAddress = newAddress.toLowerCase();
		newAddress.replaceAll("str", "straße").replaceAll("[\\s]+", " ");
		logger.error("newAddress compound="+newAddress);
		return newAddress;
	}
	
	
	/*private String prepareQueryConcatenate(String rawAddress) {
		String newAddress = rawAddress;
		newAddress = newAddress.toLowerCase();
		newAddress = newAddress.replaceAll(" (?:straße|str|strasse)\\b", "straße").replaceAll("[\\s]+", " ");
		logger.error("newAddress concatenate ="+newAddress);
		return newAddress;
	}*/

	protected AddressResultsDto doSearch( String rawaddress,String alternativeStreetAddress,
			String countryCode, boolean needParsing,String houseNumber, boolean fuzzy, Point point, Double radius, boolean smartStreetDetection) {
		AddressResultsDto results;
		List<SolrResponseDto> exactMatches  ;
		if (!smartStreetDetection){
			 exactMatches = doSearchExact(rawaddress,
					countryCode, fuzzy, point, radius, null);
		} else {
			//bypass exact search
			 exactMatches  =new ArrayList<SolrResponseDto>();
		}
	
			//have been probably found by exact match, so we search for address and so a street*/
			if (!needParsing && exactMatches!=null && exactMatches.size() >=1){
				//only one word and exact match ok
				results = buildAddressResultDtoFromSolrResponseDto(exactMatches, houseNumber);
			} else {
				
				List<SolrResponseDto> fulltextResultsDto = doSearchStreet(
						rawaddress, countryCode, fuzzy, point, radius);
				if(alternativeStreetAddress!=null){
					logger.error("will search for altenative german Address : "+alternativeStreetAddress);
					List<SolrResponseDto> alternativeResults = doSearchStreet(
							alternativeStreetAddress, countryCode, fuzzy, point, radius);
					if (fulltextResultsDto.size()==0 ||(alternativeResults!=null && alternativeResults.size()>0 && fulltextResultsDto!=null && fulltextResultsDto.size() > 0 
							&& alternativeResults.get(0)!=null && fulltextResultsDto.get(0)!=null
							&& alternativeResults.get(0).getScore()>fulltextResultsDto.get(0).getScore())){
						logger.error("alternative results score is higher");
						fulltextResultsDto = alternativeResults;
					}
					
					
					
				} 
				
				
				if (fulltextResultsDto!=null){
					exactMatches.addAll(fulltextResultsDto);
				}
				
				logger.error("-------------------merged--------------------------");
				if (exactMatches!=null){
				for (SolrResponseDto result: exactMatches){
					logger.error(result.getScore()+" : "+(result.getOpenstreetmap_id()==null?result.getFeature_id():result.getOpenstreetmap_id())+"-"+result.getFully_qualified_name());
				}
				}
				results = buildAddressResultDtoFromSolrResponseDto(exactMatches, houseNumber);
			}
		return results;
	}

	protected String replaceGermanSynonyms(String alternativeGermanAddress) {
		StringBuffer sb = new StringBuffer();
		Matcher m = GERMAN_SYNONYM_PATTEN.matcher(alternativeGermanAddress);
		  while (m.find()) {
			m.appendReplacement(sb, "straße");
		}
		m.appendTail(sb);
		String s = sb.toString();
		return s;
	}

	protected List<SolrResponseDto> doSearchStreet(String rawaddress,
			String countryCode, boolean fuzzy, Point point, Double radius) {
		logger.debug("will search for street"+(fuzzy?"in fuzzy mode":" in strict mode"));
		List<SolrResponseDto> fulltextResultsDto = findStreetInText(rawaddress, countryCode, point, fuzzy, radius); //we search for street because we think that it is not a city nor an adm that 
		//List<SolrResponseDto> mergedResults = mergeSolrResponseDto(exactMatches, fulltextResultsDto);
		return fulltextResultsDto;
	}

	protected List<SolrResponseDto> doSearchExact(String rawaddress,
			String countryCode, boolean fuzzy, Point point, Double radius, Class[] placetype) {
		logger.debug("will search for exact match "+(fuzzy?"in fuzzy mode":" in strict mode"));
		List<SolrResponseDto> exactMatches = findExactMatches(rawaddress, countryCode, fuzzy, point, radius, placetype);
		//filter result where name is not the same
		if (exactMatches!=null){
			List<SolrResponseDto> filterResults = new ArrayList<SolrResponseDto>();
			for (SolrResponseDto result: exactMatches){
				boolean added= false;
				if(result!=null && result.getName()!=null && StringHelper.isSameName(rawaddress, result.getName(),1)){
					filterResults.add(result);
					added =true;
					logger.error("filter same name, adding "+(result.getOpenstreetmap_id()==null?result.getOpenstreetmap_id():result.getFeature_id())+"-"+result.getName()+" / "+result.getFully_qualified_name() );
				} else if (!added){
					for (String nameAlternate : result.getName_alternates()){
						if (nameAlternate!=null && StringHelper.isSameName(rawaddress, nameAlternate,1)){
							logger.error("filter same name, adding alternate "+(result.getOpenstreetmap_id()==null?result.getOpenstreetmap_id():result.getFeature_id())+" :  "+nameAlternate+" / "+result.getFully_qualified_name() );
							filterResults.add(result);
							added=true;
							break;
						}
					}
					
				}
				if (!added){
					logger.error("filter same name, ignoring :"+result.getFully_qualified_name() );
				}
			}
			if (!filterResults.isEmpty()){
				exactMatches = filterResults;
			}
			
		}
		logger.error("-------------------exact--------------------------");
		if (exactMatches!=null){
		for (SolrResponseDto result: exactMatches){
			logger.error(result.getScore()+" : "+(result.getOpenstreetmap_id()==null?result.getFeature_id():result.getOpenstreetmap_id())+"-"+result.getFully_qualified_name());
		}
		}
		return exactMatches;
	}

	protected boolean needParsing(String query) {
		if (query !=null){
			String str = query.trim();
			return str.length() > 0 && (str.indexOf(" ") != -1 || str.indexOf(",") != -1 || str.indexOf(";") != -1);
		}
		return false;
	}
	
	protected boolean shouldSetParseAddress(AddressQuery query){
	if (query !=null && query.getParsedAddressUnlockKey()!=0 && importerConfig.getParsedAddressUnlockKey() !=0 
			&& query.getParsedAddressUnlockKey()==importerConfig.getParsedAddressUnlockKey()){
		return true;
	}
	return false;
	}

	protected boolean isGeocodable(Address address) {
		if (isEmptyString(address.getStreetName()) && isEmptyString(address.getState()) && isEmptyString(address.getCity()) && isEmptyString(address.getZipCode()) && isEmptyString(address.getPostTown()) && isEmptyString(address.getCitySubdivision())) {
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
		if (address.getCountryCode()==null && countryCode!=null){
			//usefull for formater
			address.setCountryCode(countryCode);
		}
		statsUsageService.increaseUsage(StatsUsageType.GEOCODING);
		Long startTime = System.currentTimeMillis();
		
		AddressResultsDto results;
		List<SolrResponseDto> streets = new ArrayList<SolrResponseDto>();
		String houseNumber = address.getHouseNumber();
		address.setHouseNumber(null);
		address.setHouseNumberInfo(null);
		String rawAddress = addressFormater.getEnvelopeAddress(address, DisplayMode.COMMA);
		if (rawAddress!=null){
		if (!isEmptyString(address.getStreetName())){
			//search for street
			//buildAddress string
			streets = doSearchStreet(rawAddress,countryCode,false,null,null);
				if (streets==null || streets.size()==0){
					//retry in fuzzy
					streets = doSearchStreet(rawAddress,countryCode,true,null,null);
				} 
			
		} else {
			//not a street, search for city, Adm, subdivision
			Class[] placetype = com.gisgraphy.fulltext.Constants.CITY_CITYSUB_ADM_PLACETYPE;
			if(address!=null){
				if (address.getCity()!=null || address.getZipCode()!=null ||address.getCitySubdivision()!=null){
					placetype= com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE;
				}
				if (address.getState()!=null && (address.getCity()!=null && address.getZipCode()!=null && address.getCitySubdivision()!=null)){
					placetype=com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE;
				}
			}
			streets =  doSearchExact(rawAddress, countryCode, false, null, null, placetype);
			if (streets==null || streets.size()==0){
				streets =  doSearchExact(rawAddress, countryCode, true, null, null, placetype);
			}
		}
		}
		results = buildAddressResultDtoFromSolrResponseDto(streets, houseNumber);
		/*
		if (isEmptyString(address.getCity()) && isEmptyString(address.getZipCode()) && isEmptyString(address.getPostTown())) {
			String streetToSearch = address.getStreetName();
			if (address.getStreetType()!=null){
				streetToSearch = address.getStreetName()+ " "+address.getStreetType();
			}
			List<SolrResponseDto> streets = findStreetInText(streetToSearch, countryCode, null, false, null);
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
			SolrResponseDto city;
			String cityName="";
			if (cities != null && cities.size() > 0 && cities.get(0) != null) {
				 city = cities.get(0);
				 cityName=city.getName();
				logger.debug("city found "+city.getName()+"/"+city.getFeature_id());
				cityLocation = GeolocHelper.createPoint(cities.get(0).getLng().floatValue(), cities.get(0).getLat().floatValue());
			}
			List<SolrResponseDto> fulltextResultsDto = null;
			if (address.getStreetName() != null) {
				String streetSentenceToSearch = address.getStreetName();//+ "^0.5 "+address.getStreetName();
				if (address.getStreetType()!=null){
					streetSentenceToSearch = "("+streetSentenceToSearch+ " "+address.getStreetType()+ ")^7 "+streetSentenceToSearch+address.getStreetType()+"^8";
				} else {
					
					streetSentenceToSearch = streetSentenceToSearch+ "^7 ";
				}
				streetSentenceToSearch = streetSentenceToSearch +" "+cityName;
			//	List<String> streettype = smartStreetDetection.getStreetTypes(streetSentenceToSearch);
				//for (String s : streettype){
					//streetSentenceToSearch = streetSentenceToSearch.replace(s, " "+s+" ");
					//logger.info("splitstreettype ("+s+")"+streetSentenceToSearch);
				//}
				
				fulltextResultsDto = findStreetInText(streetSentenceToSearch, countryCode, cityLocation, false, null);
			}
			
			AddressResultsDto results = buildAddressResultDtoFromStreetsAndCities(fulltextResultsDto, cities, address.getHouseNumber());*/
			//results.setParsedAddress(address);
			Long endTime = System.currentTimeMillis();
			long qTime = endTime - startTime;
			results.setQTime(qTime);
			logger.info("geocoding of "+address + " and country="+countryCode+" took " + (qTime) + " ms and returns "
					+ results.getNumFound() + " results");
			return results;
		//}
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

	

	protected HouseNumberDtoInterpolation processApproximativeHouseNumber(String houseNumberToFind, Integer houseNumberToFindAsInt,
			HouseNumberDtoInterpolation bestApprox, String countryCode,
			HouseNumberDtoInterpolation houseNumberDtoToProcess) {
			Point bestApproxLocation = null;
			Integer bestApproxHN = null;
			Integer bestDif = null;
			Integer curentDif = null;
		if (bestApprox != null){
			 bestApproxLocation = bestApprox.getExactLocation();
			 bestApproxHN = bestApprox.getExactNumber();
			 bestDif = bestApprox.getHouseNumberDif();
		}
		if (houseNumberDtoToProcess.getHigherNumber()==null && houseNumberDtoToProcess.getLowerNumber()!=null){
			logger.info("approx : there is only a lower "+ houseNumberDtoToProcess.getLowerNumber());
			if (bestApproxHN == null || Math.abs(houseNumberDtoToProcess.getLowerNumber()-houseNumberToFindAsInt) < Math.abs(bestApproxHN-houseNumberToFindAsInt)){
				logger.info("approx : lower  "+ houseNumberDtoToProcess.getLowerNumber()+ " closer than "+bestApproxHN);
				bestApproxLocation = houseNumberDtoToProcess.getLowerLocation();
				bestApproxHN = houseNumberDtoToProcess.getLowerNumber();
			} else {
				logger.info("approx : lower  "+ houseNumberDtoToProcess.getLowerNumber()+ " not closer than "+bestApproxHN);
			}
		}
		else if (houseNumberDtoToProcess.getHigherNumber()!=null && houseNumberDtoToProcess.getLowerNumber()==null){
			logger.info("approx : there is only a higher "+ houseNumberDtoToProcess.getHigherNumber());
			if (bestApproxHN == null || Math.abs(houseNumberDtoToProcess.getHigherNumber()-houseNumberToFindAsInt) < Math.abs(bestApproxHN-houseNumberToFindAsInt)){
				logger.info("approx : higher  "+ houseNumberDtoToProcess.getHigherNumber()+ " closer than "+bestApproxHN);
				bestApproxLocation = houseNumberDtoToProcess.getHigherLocation();
				bestApproxHN = houseNumberDtoToProcess.getHigherNumber();
			} else {
				logger.info("approx : higher  "+ houseNumberDtoToProcess.getHigherNumber()+ " not closer than "+bestApproxHN);
			}
		}
		else if (houseNumberDtoToProcess.getHigherNumber()!=null && houseNumberDtoToProcess.getLowerNumber()!=null){
			logger.info("approx : there is lower "+houseNumberDtoToProcess.getLowerNumber()+ " and higher "+ houseNumberDtoToProcess.getHigherNumber()+ ",bestDif="+bestDif+" and best hn is actually "+bestApproxHN );
			curentDif = Math.abs(houseNumberDtoToProcess.getLowerNumber()-houseNumberDtoToProcess.getHigherNumber());
			logger.info("currentdif="+curentDif);
			if (bestDif == null ||(bestDif != null && curentDif <  bestDif)){
				logger.debug("approx : curentDif "+curentDif+" < bestDif "+bestDif);
				bestApproxLocation = GeolocHelper.interpolatedPoint(houseNumberDtoToProcess.getLowerLocation(), houseNumberDtoToProcess.getHigherLocation(), houseNumberDtoToProcess.getLowerNumber(), houseNumberDtoToProcess.getHigherNumber(), houseNumberToFindAsInt);
				bestApproxHN = houseNumberToFindAsInt;
				
			}
		}
		logger.debug("approx : best house approx found "+bestApproxHN);
		HouseNumberDtoInterpolation result = new HouseNumberDtoInterpolation(bestApproxLocation, bestApproxHN) ;
		result.setHouseNumberDif(curentDif);
		return result;
	}

	protected HouseNumberDtoInterpolation searchHouseNumber(Integer houseNumberToFindAsInt, List<HouseNumberDto> houseNumbersList,String countryCode, boolean doInterpolation) { //TODO pass the house as int directly
		if(houseNumberToFindAsInt==null || houseNumbersList==null || houseNumbersList.size()==0){
			logger.info("no house number to search : ");
			return null;
		}
		Integer nearestLower = null;
		Integer nearestUpper = null;
		HouseNumberDto nearestHouseLower = null;
		HouseNumberDto nearestHouseUpper = null;
		//for debug purpose, need to be removed
		StringBuffer sb = new StringBuffer();
		for (HouseNumberDto candidate :houseNumbersList){
			if (candidate!=null){
				sb.append(candidate.getNumber()).append(",");
			}
		}
		logger.info("will analyze HN  : "+sb.toString());
		
		for (HouseNumberDto candidate :houseNumbersList){
			if (candidate != null && candidate.getNumber()!=null){
				Integer candidateNormalized;
				if (countryCode!=null && ("SK".equalsIgnoreCase(countryCode) || "CZ".equalsIgnoreCase(countryCode))){
					candidateNormalized = HouseNumberUtil.normalizeSkCzNumberToInt(candidate.getNumber());
				} else {
					candidateNormalized = HouseNumberUtil.normalizeNumberToInt(candidate.getNumber());
				}
				if (candidateNormalized!=null && candidateNormalized == houseNumberToFindAsInt){
					logger.info("house number candidate found : "+candidate.getNumber());
					return new HouseNumberDtoInterpolation(candidate.getLocation(),houseNumberToFindAsInt);
				} else if (candidateNormalized < houseNumberToFindAsInt ){
					if (nearestLower ==null || candidateNormalized > nearestLower){
						nearestLower = candidateNormalized;
						nearestHouseLower = candidate;
					}
				} else if (candidateNormalized > houseNumberToFindAsInt){
					if (nearestUpper == null || candidateNormalized < nearestUpper){
						nearestUpper = candidateNormalized;
						nearestHouseUpper = candidate;
					}
				}
		}
		}
		logger.info("no exact house number candidate found for "+houseNumberToFindAsInt);
		//do interpolation
		if (nearestHouseLower == null && nearestHouseUpper ==null){
			logger.info(" no lower, nor upper house number found");
			return null;
		}
		HouseNumberDtoInterpolation result = new HouseNumberDtoInterpolation();
		if (nearestHouseUpper !=null){
			logger.info(" higher : "+nearestUpper);
			result.setHigherLocation(nearestHouseUpper.getLocation());
			result.setHigherNumber(nearestUpper);
		}
		if (nearestHouseLower != null){
			logger.info(" lower : "+nearestLower);
			result.setLowerLocation(nearestHouseLower.getLocation());
			result.setLowerNumber(nearestLower);
		}
			//this do interpolation, but if the street is not a line or is curve the point will be out
			if (doInterpolation){
				if (nearestHouseLower !=null && nearestHouseUpper != null){
					Point location = GeolocHelper.interpolatedPoint(nearestHouseLower.getLocation(), nearestHouseUpper.getLocation(), nearestUpper, nearestLower, houseNumberToFindAsInt);
					if (location !=null){
						return new HouseNumberDtoInterpolation(location,houseNumberToFindAsInt);
					} else {
						return null;
					}
				}
			}
		return result;
	}
	protected AddressResultsDto buildAddressResultDtoFromSolrResponseDtoCountry(List<SolrResponseDto> solResponseDtos){
		List<Address> addresses = new ArrayList<Address>();

		if (solResponseDtos != null && solResponseDtos.size() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("found " + solResponseDtos.size() + " results");
			}
			SolrResponseDto response = solResponseDtos.get(0);
			Address address = new Address();
			address.setCountry(response.getName());
			address.setName(response.getName());
			address.setLat(response.getLat());
			address.setLng(response.getLng());
			address.setId(response.getFeature_id());
			address.setGeocodingLevel(GeocodingLevels.COUNTRY);
			addresses.add(address);
		}
		return new AddressResultsDto(addresses, 0L);
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
				address.setScore(solrResponseDto.getScore());
				//address.setName(solrResponseDto.getName());
				address.setLat(solrResponseDto.getLat());
				address.setLng(solrResponseDto.getLng());
				if (solrResponseDto.getOpenstreetmap_id()!=null){
					address.setId(solrResponseDto.getOpenstreetmap_id());
				} else {
					address.setId(solrResponseDto.getFeature_id());
				}
				String countryCode = solrResponseDto.getCountry_code();
				address.setCountryCode(countryCode);
				if (solrResponseDto.getPlacetype().equalsIgnoreCase(Adm.class.getSimpleName())) {
					address.setState(solrResponseDto.getName());
				}else if (solrResponseDto.getAdm1_name() != null) {
					address.setState(solrResponseDto.getAdm1_name());
				} else if (solrResponseDto.getAdm2_name() != null) {
					address.setState(solrResponseDto.getAdm2_name());
				} 
				address.setAdm1Name(solrResponseDto.getAdm1_name());
				address.setAdm2Name(solrResponseDto.getAdm2_name());
				address.setAdm3Name(solrResponseDto.getAdm3_name());
				address.setAdm4Name(solrResponseDto.getAdm4_name());
				address.setAdm5Name(solrResponseDto.getAdm5_name());
				if (solrResponseDto.getZipcodes() != null && solrResponseDto.getZipcodes().size() > 0) {
					address.setZipCode(labelGenerator.getBestZipString(solrResponseDto.getZipcodes()));
				} else if (solrResponseDto.getIs_in_zip()!=null && solrResponseDto.getIs_in_zip().size()>=1){
					address.setZipCode(labelGenerator.getBestZipString(solrResponseDto.getIs_in_zip()));
				}
				Integer houseNumberToFindAsInt;
				if (countryCode!=null && ("SK".equalsIgnoreCase(countryCode) || "CZ".equalsIgnoreCase(countryCode))){
					houseNumberToFindAsInt = HouseNumberUtil.normalizeSkCzNumberToInt(houseNumberToFind);
				} else {
					houseNumberToFindAsInt = HouseNumberUtil.normalizeNumberToInt(houseNumberToFind);
				}
				if (solrResponseDto.getPlacetype().equalsIgnoreCase(Street.class.getSimpleName())) {
					String streetName = solrResponseDto.getName();
					String isIn = solrResponseDto.getFully_qualified_name();
					if (!isEmptyString(streetName)){ 
						if(streetName.equals(lastName) && isIn!=null && isIn.equalsIgnoreCase(lastIsin)){//probably the same street
							if (housenumberFound){
								continue;
								//do nothing it has already been found in the street
							}else {
								numberOfStreetThatHaveTheSameName++;
							address.setStreetName(solrResponseDto.getName());
							address.setCity(solrResponseDto.getIs_in());
							address.setState(solrResponseDto.getIs_in_adm());
							if (solrResponseDto.getIs_in_zip()!=null && solrResponseDto.getIs_in_zip().size()>=1){
								address.setZipCode(solrResponseDto.getIs_in_zip().iterator().next());
							}
							address.setDependentLocality(solrResponseDto.getIs_in_place());
							//now search for houseNumber
							List<HouseNumberDto> houseNumbersList = solrResponseDto.getHouse_numbers();
							//if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){ //don't verify if it is null or not because if the first streets have no house number, we won't
							//count them as street that has same streetname
							boolean doInterpolation = false;
							if (allowInterpolation(solrResponseDto) ){
								doInterpolation=true;
							}
							HouseNumberDtoInterpolation houseNumber = searchHouseNumber(houseNumberToFindAsInt,houseNumbersList,countryCode, doInterpolation);
								if (houseNumber !=null){
									if (houseNumber.isApproximative()){
										
									} else {
										housenumberFound=true;
										address.setHouseNumber(houseNumber.getExactNumerAsString());
										address.setLat(houseNumber.getExactLocation().getY());
										address.setLng(houseNumber.getExactLocation().getX());
										//remove the last results added
										for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
											addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
										}
									}
								} else{
									housenumberFound=false;
								}
							//}
						}
						} else { //the streetName is different, 
							
							//remove the last results added
							for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
								addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
							}
							numberOfStreetThatHaveTheSameName=0;
							//populate fields
							address.setStreetName(solrResponseDto.getName());
							address.setCity(solrResponseDto.getIs_in());
							address.setState(solrResponseDto.getIs_in_adm());
							if (solrResponseDto.getIs_in_zip()!=null && solrResponseDto.getIs_in_zip().size()>=1){
								address.setZipCode(solrResponseDto.getIs_in_zip().iterator().next());
							}
							address.setDependentLocality(solrResponseDto.getIs_in_place());
							//search for housenumber
							List<HouseNumberDto> houseNumbersList = solrResponseDto.getHouse_numbers();
							if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){
								boolean doInterpolation = false;
								if (allowInterpolation(solrResponseDto) ){
									doInterpolation=true;
								}
								HouseNumberDtoInterpolation houseNumber = searchHouseNumber(houseNumberToFindAsInt,houseNumbersList,countryCode, doInterpolation);
							if (houseNumber !=null){
								if (houseNumber.isApproximative()){
									
								} else {
									housenumberFound=true;
									address.setHouseNumber(houseNumber.getExactNumerAsString());
									address.setLat(houseNumber.getExactLocation().getY());
									address.setLng(houseNumber.getExactLocation().getX());
								}
							} else {
								housenumberFound=false;
							}
							}
						}
			  } else {//streetname is null, we search for housenumber anyway
					address.setCity(solrResponseDto.getIs_in());
					address.setState(solrResponseDto.getIs_in_adm());
					if (solrResponseDto.getIs_in_zip()!=null && solrResponseDto.getIs_in_zip().size()>=1){
						address.setZipCode(solrResponseDto.getIs_in_zip().iterator().next());
					}
					address.setDependentLocality(solrResponseDto.getIs_in_place());
				  List<HouseNumberDto> houseNumbersList = solrResponseDto.getHouse_numbers();
					if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){
						boolean doInterpolation = false;
						if (allowInterpolation(solrResponseDto) ){
							doInterpolation=true;
						}
						HouseNumberDtoInterpolation houseNumber = searchHouseNumber(houseNumberToFindAsInt,houseNumbersList,countryCode, doInterpolation);
					if (houseNumber !=null){
						if (houseNumber.isApproximative()){
							
						} else {
							housenumberFound=true;
							address.setHouseNumber(houseNumber.getExactNumerAsString());
							address.setLat(houseNumber.getExactLocation().getY());
							address.setLng(houseNumber.getExactLocation().getX());
						}
					} else {
						housenumberFound=false;
					}
				}
			  }
					lastName=streetName;
					lastIsin = isIn;
				} else if (solrResponseDto.getPlacetype().equalsIgnoreCase(City.class.getSimpleName())){
					address.setCity(solrResponseDto.getName());
					//populateAddressFromCity(solrResponseDto, address);
				} else if (solrResponseDto.getPlacetype().equalsIgnoreCase(CitySubdivision.class.getSimpleName())) {
					address.setQuarter(solrResponseDto.getName());
				}
				 
				if (logger.isDebugEnabled() && solrResponseDto != null) {
					logger.debug("=>place (" + (solrResponseDto.getOpenstreetmap_id()==null?solrResponseDto.getFeature_id():solrResponseDto.getOpenstreetmap_id())+") : "+solrResponseDto.getName() +" in "+solrResponseDto.getIs_in());
				}
				address.getGeocodingLevel();//force calculation of geocodingLevel
				address.setFormatedFull(labelGenerator.getFullyQualifiedName(address));
				address.setFormatedPostal(addressFormater.getEnvelopeAddress(address, DisplayMode.COMMA));
				//set the street type after postal because street type is something like RESIDENTIAL and 
				//has not the same meaning than with address parsing
				address.setStreetType(solrResponseDto.getStreet_type());
				addresses.add(address);

			}
		}
		return new AddressResultsDto(addresses, 0L);
	}

	protected boolean allowInterpolation(SolrResponseDto solrResponseDto) {
		return 
				solrResponseDto.getAzimuth_start()!=null && 
				solrResponseDto.getAzimuth_end()!=null &&
				Math.abs(solrResponseDto.getAzimuth_start()-solrResponseDto.getAzimuth_end()) < INTERPOLATION_CURVE_TOLERANCE;
	}
	


	private boolean isIntersection(Address address) {
		return address.getStreetNameIntersection() != null;
	}

	/*protected List<SolrResponseDto> findCitiesInText(String text, String countryCode) {
		return findInText(text, countryCode, null, com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE,false, null);
	}*/
	/*protected List<SolrResponseDto> processAddress(Address address, boolean fuzzy) {
		if (address==null) {
			return new ArrayList<SolrResponseDto>();
		}
		Output output;
		if (address.getStreetName()!=null) {
			output = MEDIUM_OUTPUT;
		} else {
			output = LONG_OUTPUT;
		}
		FulltextResultsDto results = fullTextSearchEngine.executeAddressQuery(address, fuzzy);
		if (results.getResultsSize() >= 1) {
			return results.getResults();
		} else {
			return new ArrayList<SolrResponseDto>();
		}
	}*/

	protected List<SolrResponseDto> findStreetInText(String text, String countryCode, Point point, boolean fuzzy, Double radius) {
		List<SolrResponseDto> streets = findInText(text, countryCode, point, com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, fuzzy, radius);
		//now that we use bounding box it is to necessary to sort by distance 
		/*Point location;
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
		}*/
		return streets;
	}

	protected List<SolrResponseDto> findInText(String text, String countryCode, Point point, Class<?>[] placetypes,boolean fuzzy, Double radius) {
		if (isEmptyString(text)) {
			return new ArrayList<SolrResponseDto>();
		}
		FulltextQuery query = new FulltextQuery(text, Pagination.paginate().from(0).to(FulltextQuerySolrHelper.NUMBER_OF_STREET_TO_RETRIEVE), LONG_OUTPUT, placetypes, countryCode);
		query.withAllWordsRequired(false).withoutSpellChecking().withFuzzy(fuzzy);
		if (fuzzy){
			query.withFuzzy(fuzzy);
		}
		if (point != null) {
			query.around(point);
			if (radius!=null){
				query.withRadius(radius);
			}
		}
		FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
		if (results.getResultsSize() >= 1) {
			return results.getResults();
		} else {
			return new ArrayList<SolrResponseDto>();
		}
	}

	

/*
	protected AddressResultsDto buildAddressResultDtoFromStreetsAndCities(List<SolrResponseDto> streets, List<SolrResponseDto> cities, String houseNumberToFind) {
		List<Address> addresses = new ArrayList<Address>();

		if (streets != null && streets.size() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("found " + streets.size() + " streets");
			}
			Double cityLat=0D;
			Double cityLng=0D;
			SolrResponseDto city = null;
			if (cities != null && cities.size() > 0) {
				city = cities.get(0);
				cityLat =city.getLat();
				cityLng = city.getLng();
			}
			String lastName=null;
			String lastIsIn=null;
			boolean housenumberFound =false;
			HouseNumberDtoInterpolation bestApproxDto = null;
			//Integer bestApproxHN = null;
			int numberOfStreetThatHaveTheSameName = 0;
			int count = 0;
			for (SolrResponseDto street : streets) {
				count++;
				Address address = new Address();
				address.setLat(street.getLat());
				address.setLng(street.getLng());
				if (street.getOpenstreetmap_id()!=null){
					address.setId(street.getOpenstreetmap_id());
				} else {
					address.setId(street.getFeature_id());
				}
				address.setCountryCode(street.getCountry_code());

				String streetName = street.getName();
				if (logger.isDebugEnabled() && streets != null) {
					logger.debug("=>street : " + streetName +" ("+street.getScore()+") in "+street.getIs_in()+"/id="+street.getFeature_id());
				}
				address.setStreetName(streetName);
				String is_in = street.getIs_in();
				  if  (!isEmptyString(is_in)) {
					address.setCity(is_in);
					address.setState(street.getIs_in_adm());
					address.setAdm1Name(street.getAdm1_name());
					address.setAdm2Name(street.getAdm2_name());
					address.setAdm3Name(street.getAdm3_name());
					address.setAdm4Name(street.getAdm4_name());
					address.setAdm5Name(street.getAdm5_name());
					if (street.getZipcodes()!=null && street.getZipcodes().size()==1){
						address.setZipCode(street.getZipcodes().iterator().next());
					} else if (street.getIs_in_zip()!=null && street.getIs_in_zip().size()>=1){
						address.setZipCode(labelGenerator.getBestZipString(street.getZipcodes()));
					}
					address.setDependentLocality(street.getIs_in_place());
				} else if (city!=null){
					populateAddressFromCity(city, address);
				}
				//now search for house number!
				List<HouseNumberDto> houseNumbersList = street.getHouse_numbers();
				//if(houseNumberToFind!=null && houseNumbersList!=null && houseNumbersList.size()>0){ //don't verify if it is null or not because if the first streets have no house number, we won't
				//count them as street that has same streetname
				if (!isEmptyString(streetName) && streetName.equalsIgnoreCase(lastName)
						//&& city!=null
						&& (!isEmptyString(is_in) && is_in.equalsIgnoreCase(lastIsIn))){//probably the same street
					logger.info(streetName+"("+numberOfStreetThatHaveTheSameName+") is the same in "+is_in);
						if (housenumberFound){
							continue;
							//do nothing it has already been found in the street that have the same name
						}else {
							numberOfStreetThatHaveTheSameName++;
							Integer houseNumberToFindAsInt;
							String countryCode = street.getCountry_code();
							if (countryCode!=null && ("SK".equalsIgnoreCase(countryCode) || "CZ".equalsIgnoreCase(countryCode))){
								houseNumberToFindAsInt = HouseNumberUtil.normalizeSkCzNumberToInt(houseNumberToFind);
							} else {
								houseNumberToFindAsInt = HouseNumberUtil.normalizeNumberToInt(houseNumberToFind);
							}
							HouseNumberDtoInterpolation houseNumberDto = searchHouseNumber(houseNumberToFindAsInt,houseNumbersList,countryCode);
							if (houseNumberDto !=null){
								if (houseNumberDto.isApproximative()){
									bestApproxDto = processApproximativeHouseNumber(houseNumberToFind,houseNumberToFindAsInt, bestApproxDto,street.getCountry_code(), houseNumberDto);
									
									
								} else {
									bestApproxDto = null;
									housenumberFound=true;
									address.setHouseNumber(houseNumberDto.getExactNumerAsString());
									address.setLat(houseNumberDto.getExactLocation().getY());
									address.setLng(houseNumberDto.getExactLocation().getX());
									//remove the last results added
									for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
										addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
									}
								}
							}// else { //same street but no house number
								//housenumberFound=false;
							//	bestApproxLocation = null;
							//	bestApproxHN = null;
							//}
						}
					} else { //the streetName is different or null, 
						//we overide the lastname with the approx one
						logger.info(streetName+"/"+lastName+" ("+numberOfStreetThatHaveTheSameName+") is different in "+is_in+"/"+lastIsIn);
						//remove the last results added
						logger.info("removing "+numberOfStreetThatHaveTheSameName+" streets");
						for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
							addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
						}
						if (bestApproxDto!=null){
							Address lastAddressWTheName= addresses.get(addresses.size()-1);
							//we set the number of the last address
							//lastAddressWTheName.setHouseNumber(bestApproxDto.getExactNumerAsString());
							logger.info("the nearest hn is "+bestApproxDto.getExactNumber()+" at "+bestApproxDto.getExactLocation());
							lastAddressWTheName.setLat(bestApproxDto.getExactLocation().getY());
							lastAddressWTheName.setLng(bestApproxDto.getExactLocation().getX());
						}
						//reinit parameter for a new loop if it is not the last, if so we need to know how many segment we have to removre
						if (streets.size()!= count){
							numberOfStreetThatHaveTheSameName=0;
							bestApproxDto = null;
						}
						//we process the new street
						Integer houseNumberToFindAsInt;
						String countryCode = street.getCountry_code();
						if (countryCode!=null && ("SK".equalsIgnoreCase(countryCode) || "CZ".equalsIgnoreCase(countryCode))){
							houseNumberToFindAsInt = HouseNumberUtil.normalizeSkCzNumberToInt(houseNumberToFind);
						} else {
							houseNumberToFindAsInt = HouseNumberUtil.normalizeNumberToInt(houseNumberToFind);
						}
						HouseNumberDtoInterpolation houseNumberDto = searchHouseNumber(houseNumberToFindAsInt,houseNumbersList,countryCode);
						if (houseNumberDto !=null){
							if (houseNumberDto.isApproximative()){
								//bestapproxdto is null because the streetname is different and we don't want to handle old information
								bestApproxDto = processApproximativeHouseNumber(houseNumberToFind, houseNumberToFindAsInt,null,street.getCountry_code(), houseNumberDto);
								
							} else {
								housenumberFound=true;
								bestApproxDto = null;
								logger.info("the nearest hn is "+houseNumberDto.getExactNumerAsString()+" at "+houseNumberDto.getExactLocation());
								address.setHouseNumber(houseNumberDto.getExactNumerAsString());
								address.setLat(houseNumberDto.getExactLocation().getY());
								address.setLng(houseNumberDto.getExactLocation().getX());
							}
						} else {
							housenumberFound=false;
							bestApproxDto = null;
						}
					}
							//	}
				lastName=streetName;
				lastIsIn = is_in;
				address.getGeocodingLevel();//force calculation of geocodingLevel
				
				if (bestApproxDto!=null && streets.size()== count){ //we are at the end of the loop and we have found an approx number
					//remove the last results added
					for (numberOfStreetThatHaveTheSameName--;numberOfStreetThatHaveTheSameName>=0;numberOfStreetThatHaveTheSameName--){
						addresses.remove(addresses.size()-1-numberOfStreetThatHaveTheSameName);
					}
					//we set the number of the last address
					//lastAddressWTheName.setHouseNumber(bestApproxDto.getExactNumerAsString());
					address.setLat(bestApproxDto.getExactLocation().getY());
					address.setLng(bestApproxDto.getExactLocation().getX());
				}
				if (city!=null){
				address.setDistance(GeolocHelper.distance(GeolocHelper.createPoint(address.getLng(), address.getLat()), GeolocHelper.createPoint(cityLng, cityLat)));
				}
				address.setFormatedFull(labelGenerator.getFullyQualifiedName(address));
				address.setFormatedPostal(labelGenerator.generatePostal(address));
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
	}*/

	/*private Address buildAddressFromCity(SolrResponseDto city) {
		Address address = new Address();
		address.setLat(city.getLat());
		address.setLng(city.getLng());
		populateAddressFromCity(city, address);
		if (city.getOpenstreetmap_id()!=null){
			address.setId(city.getOpenstreetmap_id());
		} else {
			address.setId(city.getFeature_id());
		}
		return address;
	}*/
	

/*	protected void populateAddressFromCity(SolrResponseDto city, Address address) {
		if (city != null) {
			address.setCity(city.getName());
			if (city.getAdm2_name() != null) {
				address.setState(city.getAdm2_name());
			} else if (city.getAdm1_name() != null) {
				address.setState(city.getAdm1_name());
			} else if (city.getIs_in_adm()!=null){
				address.setState(city.getIs_in_adm());
			}
			address.setAdm1Name(city.getAdm1_name());
			address.setAdm2Name(city.getAdm2_name());
			address.setAdm3Name(city.getAdm3_name());
			address.setAdm4Name(city.getAdm4_name());
			address.setAdm5Name(city.getAdm5_name());
			if (city.getZipcodes() != null && city.getZipcodes().size() > 0) {
				address.setZipCode(labelGenerator.getBestZipString(city.getZipcodes()));
			} else if (city.getIs_in_zip()!=null && city.getIs_in_zip().size()>=1){
				address.setZipCode(labelGenerator.getBestZipString(city.getIs_in_zip()));
			}
			address.setCountryCode(city.getCountry_code());
			address.setDependentLocality(city.getIs_in_place());
			address.setFormatedFull(labelGenerator.getFullyQualifiedName(address));
			address.setFormatedPostal(addressFormater.getEnvelopeAddress(address, DisplayMode.COMMA));
		}
	}*/
/*	/**
	 * @param exactMatches
	 * @param aproximativeMatches
	 * @return a list of {@link SolrResponseDto} with
	 *         list1[0],list2[0],list1[1],list2[1],... it remove duplicates and
	 *         null
	 *
	protected List<SolrResponseDto> mergeSolrResponseDto(List<SolrResponseDto> exactMatches, List<SolrResponseDto> aproximativeMatches) {
		//find common id and put them first
		//retirer duplicate de exact (si street)
		//retirer duplicate de approximate (si street)
		//merger*
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
	}*/

	protected List<SolrResponseDto> findExactMatches(String text, String countryCode,boolean fuzzy, Point point, Double radius, Class[] placetypes) {
		if (isEmptyString(text)) {
			return new ArrayList<SolrResponseDto>();
		}
		if (placetypes==null){
			placetypes = com.gisgraphy.fulltext.Constants.CITY_CITYSUB_ADM_PLACETYPE;
		}
		FulltextQuery query = new FulltextQuery(text, TEN_RESULT_PAGINATION, LONG_OUTPUT,placetypes , countryCode);
		query.withAllWordsRequired(true).withoutSpellChecking().withFuzzy(fuzzy);
		if (point!=null){
			query.around(point);
			query.withRadius(radius);
		}
		FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
		if (results.getResultsSize() >= 1) {
			return results.getResults();
		} else {
			return new ArrayList<SolrResponseDto>();
		}
	}

	protected HouseNumberAddressDto findHouseNumber(String address,
			String countryCode) {
		if (address == null) {
			return null;
		}
		Matcher m = HOUSENUMBERPATTERN.matcher(address);
		if (m.find()) {
			String houseNumber = m.group().trim();
			if (houseNumber != null) {

				Matcher m2 = FIRST_NUMBER_EXTRACTION_PATTERN
						.matcher(houseNumber);
				if (m2.find()) {
					houseNumber = m2.group();
				}
			}
			String newAddress;
			if (countryCode !=null){
				countryCode = countryCode.toUpperCase();
			}
			if (houseNumber.length() == 4 && (countryCode == null || (countryCode!= null && countryWithZipIs4Number.contains(countryCode)))  
					|| houseNumber.length() == 3 && (countryCode!= null && countryWithZipIs3Number.contains(countryCode)) 
					){
				logger.info("found house number " + houseNumber + " in '" + address
						+ "' for country '"+countryCode+"' but we don't remove it since it can be a zipcode");
				newAddress = address;
			} else {
				newAddress = m.replaceFirst("").trim();
				newAddress = newAddress.replaceFirst("^[,\\s]+", "");
				
			}
			HouseNumberAddressDto houseNumberAddressDto = new HouseNumberAddressDto(
					newAddress, address, houseNumber);
			logger.info("found house number " + houseNumber + " in '" + address
					+ "' for countrycode = '"+countryCode+"', new address wo housenumber = " + newAddress);
			return houseNumberAddressDto;
		} else {
			logger.info("no house number found in " + address);
			return null;
		}

	}

	@Autowired
	public void setAddressParser(IAddressParserService addressParser) {
		this.addressParser = addressParser;
	}

	@Autowired
	public void setFullTextSearchEngine(FullTextSearchEngine fullTextSearchEngine) {
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
