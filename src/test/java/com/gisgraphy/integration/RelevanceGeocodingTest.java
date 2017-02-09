package com.gisgraphy.integration;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.jstester.util.Assert;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.fulltext.FullTextSearchEngine;
import com.gisgraphy.fulltext.FulltextClient;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.SolrClient;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.rest.IRestClient;
import com.gisgraphy.rest.RestClient;
import com.gisgraphy.serializer.common.OutputFormat;
import com.vividsolutions.jts.geom.Point;

/**
 * Read a fil with some Geocoding URL and retrieve the location and compare with the expected result
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
//@Ignore
public class RelevanceGeocodingTest {

	public final static String FILEPATH = "integrationGeococodingUrls.csv";
	public final static String BASE_SERVER_URL ="http://127.0.0.1:8080/";
	public final static String GEOCODING_BASE_SERVER_URI ="geocoding/geocoding?address=";
	public final static String GEOCODING_BASE_STRUCTURED_SERVER_URI ="geocoding/geocoding?";
	public final static String FULLTEXT_BASE_SERVER_URI ="fulltext/search?q=";
	public final static String OUTPUT_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output.csv";
	public final static String OUTPUT_FAIL_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output_fail.csv";
	
	IRestClient restClient = new RestClient();
	
	FulltextClient fulltextClient = new FulltextClient(BASE_SERVER_URL+"fulltext/");
	
	//@Autowired
	//private IsolrClient solrClient;

	//because sometimes we do some relevance test on server 
	//where not all the countries has been imported, we specified a list of imported country on the server we test
	List<String> countryTest = new ArrayList<String>(){
		{
			add("FR");
		}
	};
	
	public final static long STOMER_CAEN_ID = 4039279022L;
	//should be 280330 when bug in osm id will be fixed
	
	/*
           _           
  __ _  __| |_ __ ___  
 / _` |/ _` | '_ ` _ \ 
| (_| | (_| | | | | | |
 \__,_|\__,_|_| |_| |_|
       
	 */
	@Test
	public void adm2() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "pas de calais";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(7394,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void adm1() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "normandie";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(3793170,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	
	/*
	 * 
	 *   
	 *_ _         
  ___(_) |_ _   _ 
 / __| | __| | | |
| (__| | |_| |_| |
 \___|_|\__|\__, |
            |___/ 

	 */
	
	
	@Test
	public void cityCountryTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "paris france";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(7444,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void cityCountryDetectionTest() throws InterruptedException, IOException{
		if (countryTest.contains("ALL")){
			String rawAddress = "paris usa";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{6678712L,33299478L,130722L,140787L,126166L,197171L,},addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void cityOnlyTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(7444,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void countryOnlyTest() throws InterruptedException, IOException{
			String rawAddress = "france";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(3017382,addressResultsDto.getResult(), rawAddress);
	}
	
	@Test
	public void bigCityNameWithStateTest() throws InterruptedException, IOException{
		if (countryTest.contains("ALL")){
			String rawAddress = "paris texas";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, null);
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(0,addressResultsDto.getResult(), rawAddress);
		}
	}
	
		
	@Test
	public void postalCodeSeveralResult() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//goussainville,FONTENAY EN PARISIS,CHATENAY EN FRANCE
			isCorrectByIds(new Long[]{161530L,398224L,138537L},addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isCorrectById(161530L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void postalCodeAndPartialCityName() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190 FONTENAY";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(398224,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void postalCodeAndCityName() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190 FONTENAY EN PARISIS";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(398224L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWithTwoPossibleResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isCorrectById(94401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWith2UnnecessaryWordResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer truc";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
		/*	//the two city named saint omer in france
			isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isCorrectById(94401L,addressResultsDto.getResult(), rawAddress);*/
			isNoResult(addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWith1UnnecessaryWordResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer trucfoo foobar";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			/*//the two city named saint omer in france
			isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isCorrectById(94401L,addressResultsDto.getResult(), rawAddress);*/
			isNoResult(addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityAdm() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer normandie";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			//first result should be the biggest population
			isCorrectByAtLeastOneIds(new Long[]{4039279022L,94401L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void citySynnomysSaint() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "st omer";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isCorrectById(94401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void city_nameAlternate() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "touquet";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByIds(new Long[]{2972304L,2095264L},addressResultsDto.getResult(), rawAddress);
			//hard to determine wich one should be took : most popular or exact one
			//isCorrectById(2972304L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void city_nameAlternate2() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "le touquet";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			//touquet should be first because exact match
			isCorrectById(2095264L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void cityWithTwoPossibleAroundAPoint_boostNearest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			//specify a point to promote 
			AddressResultsDto addressResultsDto = doGeocodingOnCountryArroundAndRadius(rawAddress, "FR",GeolocHelper.createPoint( -0.4380443, 48.9381357),0);
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			//when radius 0 is specified the point impact the result (location bias).
			//in this case the two cities saint omer should be find
		//	isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//...and first result should be the nearest one
			isCorrectById(STOMER_CAEN_ID,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWithTwoPossibleAroundAPoint_NoRadiusSpecified() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			//specify a point to promote 
			AddressResultsDto addressResultsDto = doGeocodingOnCountryArround(rawAddress, "FR",GeolocHelper.createPoint( -0.4380443, 48.9381357));
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			//when radius 0 is specified the point impact the result (location bias).
			//in this case the two cities saint omer should be find
		//	isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//...and first result should be the nearest one
			isCorrectById(STOMER_CAEN_ID,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWithTwoPossibleAroundAPoint_boundingbox() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			//specify a point to promote 
			AddressResultsDto addressResultsDto = doGeocodingOnCountryArroundAndRadius(rawAddress, "FR",GeolocHelper.createPoint( -0.4380443, 48.9381357),2000);
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			//when radius 0 is specified the point impact the result (location bias).
			//in this case the two cities saint omer should be find
		//	isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//...and first result should be the nearest one
			isCorrectById(STOMER_CAEN_ID,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWithTwoPossibleAroundAPoint_boundingbox_smallradius() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			//specify a point to promote 
			AddressResultsDto addressResultsDto = doGeocodingOnCountryArroundAndRadius(rawAddress, "FR",GeolocHelper.createPoint( -0.4380443, 48.9381357),2);
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			//when radius 0 is specified the point impact the result (location bias).
			//in this case the two cities saint omer should be find
		//	isCorrectByIds(new Long[]{94401L,STOMER_CAEN_ID},addressResultsDto.getResult(), rawAddress);
			//...and first result should be the nearest one
			isNoResult(addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWithTwoPossibleAroundAFarPointWithRadiusShouldNotReturnResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			AddressResultsDto addressResultsDto = doGeocodingOnCountryArroundAndRadius(rawAddress, "FR",GeolocHelper.createPoint(	-0.5800364,20.841225),10000);
			Assert.assertNotNull(addressResultsDto);
			//when radius >0 is specified it is a bounding box.
			//in this case the two cities saint omer is not in this box
			isNoResult(addressResultsDto.getResult(), rawAddress);
			//first result should be the nearest one
			//isCorrectById(STOMER_CAEN_ID,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	/*
	 * 
	 * 
	 * 
           _     _                   
  __ _  __| | __| |_ __ ___  ___ ___ 
 / _` |/ _` |/ _` | '__/ _ \/ __/ __|
| (_| | (_| | (_| | | |  __/\__ \__ \
 \__,_|\__,_|\__,_|_|  \___||___/___/
                                     

	 * 
	 */
	

	
	@Test
	public void streetWithCitySynonymTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paname";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void streetWithCitySynonymAccentInStreetTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Plâce Vendôme, Paname";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetZipTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, 75000";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void zipStreetTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "75000 Place Vendôme";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCityTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreetTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreetWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Vendôme";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			
			//should find route de paris at vendome first
			isFirstCorrectbyIds(new Long[]{142104835L,406556865L,406556866L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void StreetcityWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Vendôme Paris ";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			
			//should find place vendome first
			isFirstCorrectbyIds(new Long[]{4234145L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_1UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_2UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto tutu";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_LotUnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto tutu tata ";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_1UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_2UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto tata";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_lotUncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto tata tutu tete";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void synonymStreetTest() throws InterruptedException, IOException{
		http://www.openstreetmap.org/way/4234145#map=19/48.86747/2.32943
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place des Conquêtes, Paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetSynonymWithCitySynonymTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place des Conquêtes, Paname";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	

	
	@Test
	public void addressStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweizer grenoble";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressCityFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweitzer grenobe";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressCityAndStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweitzer grenobe";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressStreetNameIsACity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "rue de dunkerque, paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{24666331L,362231942L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressStreetNameIsABigCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "rue de paris, dunkerque";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds( new Long[]{ 2428047946L,2428047947L,20944097L,72518994L,181223657L,181224204L,234781172L,235661698L,235661699L,355469642L},addressResultsDto.getResult(), rawAddress);
		}
	}
	

	@Test
	public void addressWithoutStreetTypeCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "jean jaures bailleul";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(79424401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressWithoutStreetTypeZip() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "jean jaures 59270";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(79424401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	@Test
	public void addressStreetZip() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "jean jaures 59270";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(79424401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	/*
     _                   _                      _ 
 ___| |_ _ __ _   _  ___| |_ _   _ _ __ ___  __| |
/ __| __| '__| | | |/ __| __| | | | '__/ _ \/ _` |
\__ \ |_| |  | |_| | (__| |_| |_| | | |  __/ (_| |
|___/\__|_|   \__,_|\___|\__|\__,_|_|  \___|\__,_|
  
	 */
	
	@Test
	public void structuredStreetWithCitySynonymTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Vendôme");
			address.setStreetType("Place");
			address.setZipCode("");
			address.setCity("Paname");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
		}
	}

	
	@Test
	public void structuredStreetWithCitySynonymAccentInStreetTypeTest() throws InterruptedException, IOException{
		//accent for street detection
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Vendôme");
			address.setStreetType("Plâce");
			address.setZipCode("");
			address.setCity("Paname");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredStreetZipTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Vendôme");
			address.setStreetType("Place");
			address.setZipCode("75000");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredStreetCityAsZipTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Vendôme");
			address.setStreetType("Place");
			address.setCity("75000");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
		}
	}

	
	
	@Test
	public void structuredStreetWithCityTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Vendôme");
			address.setStreetType("Place");
			address.setZipCode("");
			address.setCity("Paris");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	
	
	@Test
	public void structuredCityStreetWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = " ";
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Paris");
			address.setStreetType("");
			address.setZipCode("");
			address.setCity("Vendôme");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			
			//should find route de paris at vendome first
			isFirstCorrectbyIds(new Long[]{142104835L},addressResultsDto.getResult(), address.toString());
		}
	}
	

	
	@Test
	public void structuredCityStreet_1UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Vendôme toto");
			address.setStreetType("Place");
			address.setZipCode("");
			address.setCity("Paris");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
		}
	}

	@Test
	public void structuredCityStreet_2UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
		address.setHouseNumber("");
		address.setStreetName("Vendôme toto tutu");
		address.setStreetType("Place");
		address.setZipCode("");
		address.setCity("Paris");
		address.setState("");
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
		Assert.assertNotNull(addressResultsDto);
		isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
	}
	}
	
	@Test
	public void structuredCityStreet_LotUnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
		address.setHouseNumber("");
		address.setStreetName("Vendôme toto tutu tata");
		address.setStreetType("Place");
		address.setZipCode("");
		address.setCity("Paris");
		address.setState("");
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
		Assert.assertNotNull(addressResultsDto);
		isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
	}
	}
	
	@Test
	public void structuredStreetWithCity_1UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("Vendôme");
			address.setStreetType("Place");
			address.setZipCode("");
			address.setCity("Paris toto");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredStreetWithCity_2UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
		address.setHouseNumber("");
		address.setStreetName("Vendôme");
		address.setStreetType("Place");
		address.setZipCode("");
		address.setCity("Paris toto tata");
		address.setState("");
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
		Assert.assertNotNull(addressResultsDto);
		isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
	}
	}
	
	@Test
	public void structuredStreetWithCity_lotUncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
		address.setHouseNumber("");
		address.setStreetName("Vendôme");
		address.setStreetType("Place");
		address.setZipCode("");
		address.setCity("Paris toto tata tutu tete");
		address.setState("");
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
		Assert.assertNotNull(addressResultsDto);
		isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
	}
	}

	@Test
	public void structuredSynonymStreetTest() throws InterruptedException, IOException{
		http://www.openstreetmap.org/way/4234145#map=19/48.86747/2.32943
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("des Conquêtes");
			address.setStreetType("Place");
			address.setZipCode("");
			address.setCity("Paris");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
		}
	}

	@Test
	public void structuredStreetSynonymWithCitySynonymTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
		address.setHouseNumber("");
		address.setStreetName("des Conquêtes");
		address.setStreetType("Place");
		address.setZipCode("");
		address.setCity("paname");
		address.setState("");
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
		Assert.assertNotNull(addressResultsDto);
		isCorrectById(4234145,addressResultsDto.getResult(), address.toString());
	}
	}


	
	@Test
	public void structuredAddressStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("docteur schweizer");
			address.setStreetType("");
			address.setZipCode("");
			address.setCity("grenoble");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredAddressCityFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("docteur schweitzer");
			address.setStreetType("");
			address.setZipCode("");
			address.setCity("grenobe");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredAddressCityAndStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = " ";
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("docteur schweitzer");
			address.setStreetType("");
			address.setZipCode("");
			address.setCity("grenobe");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredAddressStreetNameIsACity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("de dunkerque");
			address.setStreetType("rue");
			address.setZipCode("");
			address.setCity("paris");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{24666331L,362231942L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredAddressStreetNameIsABigCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("de paris");
			address.setStreetType("rue ");
			address.setZipCode("");
			address.setCity("dunkerque");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds( new Long[]{ 2428047946L,2428047947L,20944097L,72518994L,181223657L,181224204L,234781172L,235661698L,235661699L,355469642L},addressResultsDto.getResult(), address.toString());
		}
	}


	@Test
	public void structuredAddressWithoutStreetTypeCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("jean jaures");
			address.setStreetType("");
			address.setZipCode("");
			address.setCity("bailleul");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(79424401L,addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredAddressWithoutStreetTypeZip() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("jean jaures");
			address.setStreetType("");
			address.setZipCode("59270");
			address.setCity("");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(79424401L,addressResultsDto.getResult(), address.toString());
		}
	}

	
	
	
	/*
             _ 
 _ __   ___ (_)
| '_ \ / _ \| |
| |_) | (_) | |
| .__/ \___/|_|
|_|      
	 */
	
/*
	@Test
	public void poi() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "tour eiffel";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{5013364L,6254976L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void poiFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "tour eifel";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{5013364L,6254976L},addressResultsDto.getResult(), rawAddress);
		}
	}
	*/
/*	@Test
	public void poiCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Chez papa  paris";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(1830649549L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void poiTypeCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "restaurant chez papa  paris";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(1830649549L,addressResultsDto.getResult(), rawAddress);
		}
	}
	*/
	
	
	
	/*
	 * 
           _         _ _       _     _             
 ___ _   _| |__   __| (_)_   _(_)___(_) ___  _ __  
/ __| | | | '_ \ / _` | \ \ / / / __| |/ _ \| '_ \ 
\__ \ |_| | |_) | (_| | |\ V /| \__ \ | (_) | | | |
|___/\__,_|_.__/ \__,_|_| \_/ |_|___/_|\___/|_| |_|
 
	 */
	@Test
	public void subdivision() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "la defense";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(8504417L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void quaterCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Vaugirard, Paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByIds(new Long[]{10665004L,2970479L,27728576L},addressResultsDto.getResult(), rawAddress);
			isCorrectById(2970479L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void quaterZip() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Vaugirard, Paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByIds(new Long[]{10665004L,2970479L,27728576L},addressResultsDto.getResult(), rawAddress);
			isCorrectById(2970479L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void arrondissement() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris 15e";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(2970479L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	
/*
   __       _ _ _            _   
 / _|_   _| | | |_ _____  _| |_ 
| |_| | | | | | __/ _ \ \/ / __|
|  _| |_| | | | ||  __/>  <| |_ 
|_|  \__,_|_|_|\__\___/_/\_\\__|
      
 */
	
/*	@Test
	public void cityShouldBeFirstResult() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
			Assert.assertNotNull(fulltextResultsDto);
			IsCorrectPlaceType("City",fulltextResultsDto.getResults().get(0).getPlacetype());
			isFulltextCorrectById(2214006L,fulltextResultsDto.getResults(), rawAddress);
		}
	}
	
	@Test
	public void admShouldBeFirstResult() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "normandie";
			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
			Assert.assertNotNull(fulltextResultsDto);
			IsCorrectPlaceType("Adm",fulltextResultsDto.getResults().get(0).getPlacetype());
			isFulltextCorrectById(3793170L,fulltextResultsDto.getResults(), rawAddress);
		}
	}*/
	
	@Test
	public void fulltextTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
			Assert.assertNotNull(fulltextResultsDto);
			printResult(fulltextResultsDto,0);
			printResult(fulltextResultsDto,1);
			isfulltextCorrectByAtLeastOneIds(new Long[]{ 94401L,250501383L},fulltextResultsDto.getResults(), rawAddress);
			IsCorrectPlaceType("City",fulltextResultsDto.getResults().get(0).getPlacetype());
		}
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "normandie";
			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
			Assert.assertNotNull(fulltextResultsDto);
			isFulltextCorrectById(3793170L,fulltextResultsDto.getResults(), rawAddress);
			IsCorrectPlaceType("Adm",fulltextResultsDto.getResults().get(0).getPlacetype());
		}
	}
	
	public static void printResult(FulltextResultsDto fulltextResultsDto, int position){
		SolrResponseDto solrResponseDto = fulltextResultsDto.getResults().get(position);
		System.out.println("("+solrResponseDto.placetype+"/"+solrResponseDto.getOpenstreetmap_id()+")"+solrResponseDto.getFully_qualified_name());
	}
	
	/*
	
@Test
public void ExactMatchShouldHaveMoreImportanceThanFuzzy(){
	String URLToCall = "/geocoding/?&country=DE&address=Lindenstra%C3%9Fe%203%2C%20Leonberg%2C%2071229&postal=true";
	String fullURLToCall = BASE_SERVER_URL+URLToCall +"&format=json";
	System.out.println(fullURLToCall);
	AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
	System.out.println(result.getResult());
	Assert.assertEquals("Lindenstraße", result.getResult().get(0).getStreetName());
}

@Test
public void FuzzyShouldWork(){
	//todo /geocoding/?&country=DE&address=Rheinstra%C3%9Fe%2029%2C%20Wiesbaden%2C%2065185

	String URLToCall = "geocoding/?&country=DE&address=Demmelsjochstr%2052%2C%20Bad%20T%C3%B6lz%2C%2083646&postal=true";
	String fullURLToCall = BASE_SERVER_URL+URLToCall +"&format=json";
	System.out.println(fullURLToCall);
	AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
	System.out.println(result.getResult());
	Assert.assertEquals("Demmeljochstraße", result.getResult().get(0).getStreetName());
	Assert.assertEquals("Bad Tölz", result.getResult().get(0).getCity());
}
*/

//@Test
public void coefFinder(){
	MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
	SolrClient solrclient= new SolrClient("http://import.gisgraphy.com:8983/solr/", multiThreadedHttpConnectionManager);
	FullTextSearchEngine engine = new FullTextSearchEngine(multiThreadedHttpConnectionManager);
	engine.setSolrClient(solrclient);
	
	for (int a=1;a<=50;a++){
		for (int b=1;b<=50;b++){
					System.out.println("testing "+a+"-"+b);
					boolean ok1=false;
					boolean ok2=false;
					String q = "_query_:\"{!edismax qf=' all_name^1.1 iso_all_name^1.3  zipcode^1.2 all_adm1_name^0.5 all_adm2_name^0.5  is_in^0.9 is_in_place^0.7  is_in_adm^0.4 is_in_zip^0.2 is_in_cities^0.7 ' mm='1<1 2<1 3<1'   pf='name^1.8' ps=0 bq=' ' bf='pow(map(population,0,0,0.0001),0.3)  pow(map(city_population,0,0,0.0000001),0.3)   ' }\\\"Weihlbachstr\\\"^"+a+" Weihlbachstr~^"+b+" \\\"Flörsheim\\\"^"+a+" Flörsheim~^"+b+"\"";
					FulltextResultsDto result  = engine.executeRawQuery(q);
					//System.out.println(result.getResults().get(0).toString());
					if ("Weilbacher Straße".equals(result.getResults().get(0).getName()) && "Flörsheim".equals(result.getResults().get(0).getIs_in())){
						ok1=true;
						System.out.println("ok1 for "+a+"-"+b);
					}
					
					//
					if (ok1){
					 		q = "_query_:\"{!edismax qf=' all_name^1.1 iso_all_name^1.3  zipcode^1.2 all_adm1_name^0.5 all_adm2_name^0.5  is_in^0.9 is_in_place^0.7  is_in_adm^0.4 is_in_zip^0.2 is_in_cities^0.7 ' mm='1<1 2<1 3<1'   pf='name^1.8' ps=0 bq=' ' bf='pow(map(population,0,0,0.0001),0.3)  pow(map(city_population,0,0,0.0000001),0.3)   ' }\\\"Lindnstrasse\\\"^"+a+" Lindnstrasse~^"+b+" \\\"Grünheide\\\"^"+a+" Grünheide~^"+b+"\"";
					 result  = engine.executeRawQuery(q);
					//System.out.println(result.getResults().get(0).toString());
					if ("Große Lindenstraße".equals(result.getResults().get(0).getName()) && "Grünheide".equals(result.getResults().get(0).getIs_in())){
						ok2=true;
						System.out.println("ok2 for "+a+"-"+b);
					}
					}
					if (ok1 && ok2){
						System.out.println("!!!!!!!!!!!success!!!!!!!!!!!!!!!!!!!");
						System.out.println("ok1 et 2 for "+a+"-"+b);
						return;
						
			}
		}
	}
	
}
//------------------------------------------------------------------------------------
private FulltextResultsDto doFulltext(String text){
	String fullURLToCall = BASE_SERVER_URL+FULLTEXT_BASE_SERVER_URI+URLEncoder.encode(text)+"&format=json";
	System.out.println(fullURLToCall);
	FulltextQuery query = new FulltextQuery(text);
	FulltextResultsDto result  = fulltextClient.executeQuery(query);
	return result;
}
private FulltextResultsDto doFulltextOnPlacetype(String text,String placetype){
	String fullURLToCall = BASE_SERVER_URL+FULLTEXT_BASE_SERVER_URI+URLEncoder.encode(text)+"&format=json&placetype="+placetype;
	FulltextResultsDto result  = restClient.get(fullURLToCall, FulltextResultsDto.class, OutputFormat.JSON);
	return result;
}

private FulltextResultsDto doFulltextOnPlacetypes(String text,String[] placeTypes){
	String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(text)+"&format=json";
	for (String placetype:placeTypes){
		fullURLToCall = fullURLToCall+"&placetype="+placetype;
	}
	FulltextResultsDto result  = restClient.get(fullURLToCall, FulltextResultsDto.class, OutputFormat.JSON);
	return result;
}
//------------------------------------------------------------------------------------
	private AddressResultsDto doGeocoding (String address){
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json";
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		return result;
	}
	private AddressResultsDto doGeocodingPostal(String address){
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json&postal=true";
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		return result;
	}
	
	
		
	private AddressResultsDto doGeocodingOnCountry(String address,String countryCode){
		@SuppressWarnings("deprecation")
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json&country="+countryCode;
		System.out.println(fullURLToCall);
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		if (result!=null && result.getNumFound()!=0){
			System.out.println(address+ " : "+result.getResult().get(0));
		} else {
			System.out.println(address+ " : no results");
		}
		return result;
	}
	
	private AddressResultsDto doGeocodingOnCountry(Address address,String countryCode){
		@SuppressWarnings("deprecation")
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_STRUCTURED_SERVER_URI+"apikey=111";//just add apikey to not to have to managed ? vs &
		if (address.getStreetType()!=null){
			fullURLToCall=fullURLToCall+"&streetType="+URLEncoder.encode(address.getStreetType());
		}
		if (address.getStreetName()!=null){
			fullURLToCall=fullURLToCall+"&streetName="+URLEncoder.encode(address.getStreetName());
		}
		if (address.getCity()!=null){
			fullURLToCall=fullURLToCall+"&city="+URLEncoder.encode(address.getCity());
		}
		if (address.getState()!=null){
			fullURLToCall=fullURLToCall+"&state="+URLEncoder.encode(address.getState());
		}
		if (address.getZipCode()!=null){
			fullURLToCall=fullURLToCall+"&zipCode="+URLEncoder.encode(address.getZipCode());
		}
		if (address.getHouseNumber()!=null){
			fullURLToCall=fullURLToCall+"&houseNumber="+URLEncoder.encode(address.getHouseNumber());
		}
		if (countryCode!=null){
			fullURLToCall=fullURLToCall+"&country="+countryCode;
		}
		
		fullURLToCall=fullURLToCall+"&format=json";
		System.out.println(fullURLToCall);
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		if (result!=null && result.getNumFound()!=0){
			System.out.println(address+ " : "+result.getResult().get(0));
		} else {
			System.out.println(address+ " : no results");
		}
		return result;
	}
	
		
	private AddressResultsDto doGeocodingOnCountryArroundAndRadius(String address,String countryCode, Point location,int radius){
		@SuppressWarnings("deprecation")
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json&country="+countryCode+"&lat="+location.getY()+"&lng="+location.getX()+"&radius="+radius;
		System.out.println(fullURLToCall);
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		if (result!=null && result.getNumFound()!=0){
			System.out.println(address+ " : "+result.getResult().get(0));
		} else {
			System.out.println(address+ " : no results");
		}
		return result;
	}
	
	private AddressResultsDto doGeocodingOnCountryArround(String address,String countryCode, Point location){
		@SuppressWarnings("deprecation")
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json&country="+countryCode+"&lat="+location.getY()+"&lng="+location.getX();
		System.out.println(fullURLToCall);
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		if (result!=null && result.getNumFound()!=0){
			System.out.println(address+ " : "+result.getResult().get(0));
		} else {
			System.out.println(address+ " : no results");
		}
		return result;
	}
	//-------------------------------------------------------------------------------
	private boolean IsCorrectPlaceType(GeocodingLevels expected, List<Address> actual){
		if (actual == null){
			return false;
		}
		if(actual.size()>0 && actual.get(0)!=null && actual.get(0).getGeocodingLevel().equals(expected)){
			return true;
		}else {
			return false;
		}
	}
	
	private boolean IsCorrectLocation(Double expectedlat, Double expectedlng,List<Address> actual, double distanceTolérance){
		if(actual.size()>0 && actual.get(0)!=null && actual.get(0).getLat()!=null && actual.get(0).getLng()!=null){
			Address address = actual.get(0);
			Point actualPoint = GeolocHelper.createPoint(address.getLng(), address.getLat());
			Point expected = GeolocHelper.createPoint(expectedlng, expectedlat);
			double distance = GeolocHelper.distance(actualPoint, expected);
			if (distance > distanceTolérance){
				return false;
			} 
			return true;
		}
		return false;
	}
	
	private boolean isCorrectById(long expectedOpenstreetmapId,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			Address address = actual.get(0);
			if (expectedOpenstreetmapId != address.getId()){
				Assert.fail(rawAddress +": expected "+expectedOpenstreetmapId+ " but was " +address.getId()+" / "+address);
				return false;
			} 
			return true;
		}
		Assert.fail(rawAddress +": expected "+expectedOpenstreetmapId+ " but no result ");
		return false;
	}
	
	private boolean isFulltextCorrectById(long expectedOpenstreetmapId,List<SolrResponseDto> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			SolrResponseDto result = actual.get(0);
			Long id = result.getOpenstreetmap_id()!=null?result.getOpenstreetmap_id():result.getFeature_id();
			if (expectedOpenstreetmapId != id){
				Assert.fail(rawAddress +": expected "+expectedOpenstreetmapId+ " but was " +result.getOpenstreetmap_id()+":"+result.getPlacetype()+" / "+result.getFully_qualified_name());
				return false;
			} 
			return true;
		}
		Assert.fail(rawAddress +": expected "+expectedOpenstreetmapId+ " but no result ");
		return false;
	}
	
	private boolean isNoResult(List<Address> actual, String rawAddress){
		if(actual.size()==0){
			return true;
		}
		Assert.fail(rawAddress+" should have return no results but we found "+actual.get(0));
		return false;
	}
	
	private boolean isCorrectByIds(Long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
			for(Address address: actual){
				if (address!=null){
					ids.add(address.getId());
				}
			}
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (!ids.contains(expectedOpenstreetmapId)){
					Assert.fail(rawAddress +": expected "+expectedOpenstreetmapId+ " but was not found");
					return false;
				} 
			}
			return true;
		}
		Assert.fail(rawAddress +": expected "+expectedOpenstreetmapIds+ " but no result ");
		return false;
	}
	
	private boolean isFulltextCorrectByIds(Long[] expectedOpenstreetmapIds,List<SolrResponseDto> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
			for(SolrResponseDto address: actual){
				if (address!=null){
					ids.add(address.getOpenstreetmap_id());
				}
			}
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (!ids.contains(expectedOpenstreetmapId)){
					Assert.fail(rawAddress +": expected "+expectedOpenstreetmapId+ " but was not found");
					return false;
				} 
			}
			return true;
		}
		Assert.fail(rawAddress +": expected "+expectedOpenstreetmapIds+ " but no result ");
		return false;
	}
	
	private boolean isCorrectByAtLeastOneIds(Long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
			Address address = actual.get(0);
					ids.add(address.getId());
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (ids.contains(expectedOpenstreetmapId)){
					return true;
				} 
			}
			Assert.fail(rawAddress +": no ids were found");
			return false;
		}
		Assert.fail(rawAddress +": no results were found");
		return false;
	}
	
	private boolean isfulltextCorrectByAtLeastOneIds(Long[] expectedOpenstreetmapIds,List<SolrResponseDto> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
					ids.add(actual.get(0).getOpenstreetmap_id());
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (ids.contains(expectedOpenstreetmapId)){
					return true;
				} 
			}
			Assert.fail(rawAddress +": no ids were found");
			return false;
		}
		Assert.fail(rawAddress +": no results were found");
		return false;
	}
	
	private boolean isFirstCorrectbyIds(Long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			Long actualId = actual.get(0).getId(); 
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (actualId==expectedOpenstreetmapId){
					return true;
				} 
			}
			Assert.fail(rawAddress +": no ids were found");
			return false;
		}
		Assert.fail(rawAddress +": no results were found");
		return false;
	}
	

	private boolean IsCorrectGeocodingLevel(GeocodingLevels expectedLevel,List<Address> actual){
		if(actual.size()>0 && actual.get(0)!=null){
			Address address = actual.get(0);
			if (expectedLevel != address.getGeocodingLevel()){
				return false;
			} 
			return true;
		}
		return false;
	}
	
	//-------------------------------------------------------------
	private boolean IsCorrectPlaceType(String expected, String actual){
		if (actual != null  && expected.equals(actual)){
			return true;
		}
		Assert.fail("placetype is not correct, expected "+expected+", but was "+actual);
		return false;
	}
	
	private boolean IsCorrectLocation(Double expectedlat, Double expectedlng, Double actualLat,Double actualLng, double distanceTolérance){
		return true;
	}
	
	private boolean IsCorrectById(long expectedOpenstreetmapId, long actualOpenstreetmapId){
		return true;
	}





}
