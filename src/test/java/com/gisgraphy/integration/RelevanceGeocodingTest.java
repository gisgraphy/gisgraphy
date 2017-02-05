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
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.SolrClient;
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
	public final static String BASE_SERVER_URI ="geocoding/geocoding?address=";
	public final static String OUTPUT_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output.csv";
	public final static String OUTPUT_FAIL_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output_fail.csv";
	
	IRestClient restClient = new RestClient();
	
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(7394,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void adm1() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "normandie";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(7444,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void cityCountryDetectionTest() throws InterruptedException, IOException{
		if (countryTest.contains("ALL")){
			String rawAddress = "paris usa";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{6678712L,33299478L,130722L,140787L,126166L,197171L,},addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void cityOnlyTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "paris";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(7444,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void countryOnlyTest() throws InterruptedException, IOException{
			String rawAddress = "france";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(3017382,addressResultsDto.getResult(), rawAddress);
	}
	
	@Test
	public void bigCityNameWithStateTest() throws InterruptedException, IOException{
		if (countryTest.contains("ALL")){
			String rawAddress = "paris texas";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, null);
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(0,addressResultsDto.getResult(), rawAddress);
		}
	}
	
		
	@Test
	public void postalCodeSeveralResult() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(398224,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void postalCodeAndCityName() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190 FONTENAY EN PARISIS";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(398224L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWithTwoPossibleResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			//touquet should be first because exact match
			isCorrectById(2095264L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	/*
	@Test
	public void cityWithTwoPossibleAroundAPoint() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			AddressResultsDto addressResultsDto = doRequestOnCountryArroundAndRadius(rawAddress, "FR",GeolocHelper.createPoint(	48.9381357, -0.4380443),0);
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
	public void cityWithTwoPossibleAroundAFarPointWithRadiusShouldNotReturnResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			AddressResultsDto addressResultsDto = doRequestOnCountryArroundAndRadius(rawAddress, "FR",GeolocHelper.createPoint(	44.841225, -0.5800364),10000);
			Assert.assertNotNull(addressResultsDto);
			//when radius >0 is specified it is a bounding box.
			//in this case the two cities saint omer is not in this box
		//	isNoResult(addressResultsDto.getResult(), rawAddress);
			//first result should be the nearest one
			isCorrectById(STOMER_CAEN_ID,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	*/
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
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCityTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectbyIds(new Long[]{4234145L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreetTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectbyIds(new Long[]{4234145L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreetWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Vendôme";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			
			//should find route de paris at vendome first
			isFirstCorrectbyIds(new Long[]{142104835L,406556865L,406556866L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void StreetcityWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Vendôme Paris ";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			
			//should find place vendome first
			isFirstCorrectbyIds(new Long[]{4234145L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_1UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_2UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto tutu";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_LotUnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto tutu tata ";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_1UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_2UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto tata";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_lotUncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto tata tutu tete";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void synonymStreetTest() throws InterruptedException, IOException{
		http://www.openstreetmap.org/way/4234145#map=19/48.86747/2.32943
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place des Conquêtes, Paris";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetSynonymWithCitySynonymTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place des Conquêtes, Paname";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(4234145,addressResultsDto.getResult(), rawAddress);
		}
	}
	

	
	@Test
	public void addressStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweizer grenoble";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressCityFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweitzer grenobe";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressCityAndStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweitzer grenobe";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds(new Long[]{488881135L,204138290L,204138293L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressStreetNameIsACity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "rue de dunkerque, paris";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isCorrectByAtLeastOneIds(new Long[]{24666331L,362231942L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressStreetNameIsABigCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "rue de paris, dunkerque";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectByAtLeastOneIds( new Long[]{ 2428047946L,2428047947L,20944097L,72518994L,181223657L,181224204L,234781172L,235661698L,235661699L,355469642L},addressResultsDto.getResult(), rawAddress);
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
	@Test
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
	
	@Test
	public void addressWithoutStreetTypeCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "jean jaures bailleul";
			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isCorrectById(79424401L,addressResultsDto.getResult(), rawAddress);
		}
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
	private AddressResultsDto doRequest (String address){
		String fullURLToCall = BASE_SERVER_URL+BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json";
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		return result;
	}
	private AddressResultsDto doRequestPostal(String address){
		String fullURLToCall = BASE_SERVER_URL+BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json&postal=true";
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		return result;
	}
		
	private AddressResultsDto doRequestOnCountry(String address,String countryCode){
		@SuppressWarnings("deprecation")
		String fullURLToCall = BASE_SERVER_URL+BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json&countrycode="+countryCode;
		System.out.println(fullURLToCall);
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		if (result!=null && result.getNumFound()!=0){
			System.out.println(address+ " : "+result.getResult().get(0));
		} else {
			System.out.println(address+ " : no results");
		}
		return result;
	}
	
		
	private AddressResultsDto doRequestOnCountryArroundAndRadius(String address,String countryCode, Point location,int radius){
		@SuppressWarnings("deprecation")
		String fullURLToCall = BASE_SERVER_URL+BASE_SERVER_URI+URLEncoder.encode(address)+"&format=json&countrycode="+countryCode+"&lat="+location.getY()+"&lng="+location.getX()+"&radius=";
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
	
	private boolean isCorrectByAtLeastOneIds(Long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
			for(Address address: actual){
				if (address!=null){
					ids.add(address.getId());
				}
			}
			
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
		return true;
	}
	
	private boolean IsCorrectLocation(Double expectedlat, Double expectedlng, Double actualLat,Double actualLng, double distanceTolérance){
		return true;
	}
	
	private boolean IsCorrectById(long expectedOpenstreetmapId, long actualOpenstreetmapId){
		return true;
	}





}
