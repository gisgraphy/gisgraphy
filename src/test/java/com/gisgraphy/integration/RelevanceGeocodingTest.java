package com.gisgraphy.integration;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.jstester.util.Assert;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

	private static final long VAUGIRARD_QUATER_ID = 2142769871L;

	//street, quater
    private static final long[] VAUGIRARD_IDS = new long[]{10665004L,27728576L,VAUGIRARD_QUATER_ID,161231529L};

    private static final long[] NAABSTRASSE_IDS = new long[]{280017483,28971876,372824213};

    private static final long[] MAGDEBURGERSTRASSE = new long[]{83351156L,24554576L,361576252L,420600633,26934369,336289502,91146628};

	private static final long[] TOUQUET = new long[]{2972304L};

	private static final long[] LE_TOUQUET = new long[]{2095264L,2999139l};

	private static final long[] RUE_DUNKERQUE_PARIS = new long[]{24666331L,362231942L,24788408l};

	private static final long[] DOCTEUR_SWEITZER_GRENOBLE = new long[]{488881135L,204138290L,204138293L,5990522L};

	private static final long[] PLACE_VENDOME_PARIS = new long[]{4234144L,4234145L,4234146L,372486666L};

	private static final long[] CHATENAY_95190 = new long[]{3026110,138537l};//138537L;
	private static final long[] FONTENAY_95190 = new long[]{3017922,398224l};//398224L;
	private static final long[] GOUSSAINVILLE_95190 = new long[]{3015490L,161530L};//161530L;
	
	public final static long[] STOMER_CAEN_IDs = new long[]{280330l,4039279022L,2977846L};
	public final static long[] STOMER_NPDC_IDs = new long[]{94401L};//4039279022L;2977846
	public final static long[] STOMER_PAYS_DE_LOIRE_IDs = new long[]{2977847L};//4039279022L;2977846
	
	public final static String FILEPATH = "integrationGeococodingUrls.csv";
	//public final static String BASE_SERVER_URL ="http://127.0.0.1:8080/";
	public final static String BASE_SERVER_URL ="http://relevance.gisgraphy.com:8080/";
	public final static String GEOCODING_BASE_SERVER_URI ="geocoding/geocoding?address=";
	public final static String GEOCODING_BASE_STRUCTURED_SERVER_URI ="geocoding/geocoding?";
	public final static String FULLTEXT_BASE_SERVER_URI ="fulltext/search?q=";
	public final static String OUTPUT_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output.csv";
	public final static String OUTPUT_FAIL_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output_fail.csv";

	private static final boolean houseNumber = true;
	private static int nbTest = 0;
	
	IRestClient restClient = new RestClient();
	
	FulltextClient fulltextClient = new FulltextClient(BASE_SERVER_URL+"fulltext/");
	 
	//@Autowired
	//private IsolrClient solrClient;

	//because sometimes we do some relevance test on server 
	//where not all the countries has been imported, we specified a list of imported country on the server we test
	@SuppressWarnings("serial")
	List<String> countryTest = new ArrayList<String>(){
		{
			add("FR");
			add("GB");
			add("DE");
			add("US");
		}
	};
	
	//us
	
	@BeforeClass
	public static void setup(){
	    nbTest = 0;
	}
	
	@AfterClass
    public static void tearDown(){
        System.out.println("nuber of tests : "+nbTest);
    }
	
	
	
	
	
	
	@Test
	public void washington() throws InterruptedException, IOException{
		if (countryTest.contains("US")|| countryTest.contains("ALL")){
			String rawAddress = "washington";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "US");
			Assert.assertNotNull(addressResultsDto);
			//state/city
			isAllIdsPresentInResults(new long[]{158368533L,4140963},addressResultsDto.getResult(), rawAddress);
			//first result should be city ?
			isFirstCorrectByIds(new long[]{158368533L},addressResultsDto.getResult(), rawAddress);
		}
			
	}

	
	
//	
//           _           
//  __ _  __| |_ __ ___  
// / _` |/ _` | '_ ` _ \ 
//| (_| | (_| | | | | | |
// \__,_|\__,_|_| |_| |_|
//       
	 
	
	
	@Test
	public void adm2() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "pas de calais";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(7394,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void adm1() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "normandie";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(3793170,addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
/*
                       _              
  ___ ___  _   _ _ __ | |_ _ __ _   _ 
 / __/ _ \| | | | '_ \| __| '__| | | |
| (_| (_) | |_| | | | | |_| |  | |_| |
 \___\___/ \__,_|_| |_|\__|_|   \__, |
                                |___/ 

 * 
 * 
 * 
 */
	@Test
	public void cityCountryTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "paris france";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{2988507L,7444L},addressResultsDto.getResult(),rawAddress);
		}
		
	}
	
	
//	
//      _         
//  ___(_) |_ _   _ 
// / __| | __| | | |
//| (__| | |_| |_| |
// \___|_|\__|\__, |
//            |___/ 
//
//	 
	
	

    @Test
    public void addressCityOnlyFuzzy() throws InterruptedException, IOException{
        if (countryTest.contains("FR")|| countryTest.contains("ALL")){
            String rawAddress = "ochsenkopft";
            AddressResultsDto addressResultsDto = doGeocoding(rawAddress);
            Assert.assertNotNull(addressResultsDto);
            //the osm one , the geonames one
            isFirstInExpectedIds(new long[]{2858301},addressResultsDto.getResult(), rawAddress);
        }
    }
	
	
	@Test
	public void cityGermanWithFrenchNameTest_2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")){
			String rawAddress = "cologne";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{62578},addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void cityGerman() throws InterruptedException, IOException{
		if (countryTest.contains("DE")){
			String rawAddress = "berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isOneIdsPresentInResults(new long[]{62422,240109189},addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void cityGermanTest_2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")){
			String rawAddress = "Köln";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{62578},addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	
	
	@Test
	public void cityCountryDetectionTest() throws InterruptedException, IOException{
		if (countryTest.contains("ALL")){
			String rawAddress = "paris usa";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{6678712L,33299478L,130722L,140787L,126166L,197171L,},addressResultsDto.getResult(), rawAddress);
		}
			
	}
	
	@Test
	public void cityOnlyTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{2988507L,7444L},addressResultsDto.getResult(),rawAddress);
		}
			
	}
	
	@Test
	public void countryOnlyTest() throws InterruptedException, IOException{
			String rawAddress = "france";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(3017382,addressResultsDto.getResult(), rawAddress);
	}
	
	@Test
	public void bigCityNameWithStateTest() throws InterruptedException, IOException{
		if (countryTest.contains("ALL")){
			String rawAddress = "paris texas";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, null);
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(0,addressResultsDto.getResult(), rawAddress);
		}
	}
	
		
	@Test
	public void postalCodeSeveralResult() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//goussainville,FONTENAY EN PARISIS,CHATENAY EN FRANCE
			isOneIdsPresentInResults(GOUSSAINVILLE_95190,addressResultsDto.getResult(), rawAddress);
			isOneIdsPresentInResults(FONTENAY_95190,addressResultsDto.getResult(), rawAddress);
				isOneIdsPresentInResults(CHATENAY_95190,addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isFirstInExpectedIds(GOUSSAINVILLE_95190,addressResultsDto.getResult(), rawAddress);
		}
	}
	//3015490 3017922 3026110
	
	@Test
	public void postalCodeAndPartialCityName() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190 FONTENAY";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(FONTENAY_95190,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void postalCodeAndCityName() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "95190 FONTENAY EN PARISIS";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(FONTENAY_95190,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityWithTwoPossibleResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			isOneIdsPresentInResults(STOMER_NPDC_IDs,addressResultsDto.getResult(), rawAddress);
			isOneIdsPresentInResults(STOMER_CAEN_IDs,addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isFirstCorrectByIds(STOMER_NPDC_IDs,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityApproxWithTwoPossibleResults() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint luz";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//saint jean de luz and luz saint sauveur
			isAllIdsPresentInResults(new long[]{166727L,399965L},addressResultsDto.getResult(), rawAddress);
			//first result should be saint jean de luz
			isFirstCorrectById(166727,addressResultsDto.getResult(), rawAddress);
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
			isFirstInExpectedIds(STOMER_CAEN_IDs,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void citySynnomysSaint() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "st omer";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the two city named saint omer in france
			isOneIdsPresentInResults(STOMER_CAEN_IDs,addressResultsDto.getResult(), rawAddress);
			isOneIdsPresentInResults(STOMER_NPDC_IDs,addressResultsDto.getResult(), rawAddress);
			//first result should be the biggest population
			isFirstCorrectByIds(STOMER_NPDC_IDs,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void city_nameAlternate() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "touquet";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isOneIdsPresentInResults(TOUQUET, addressResultsDto.getResult(), rawAddress);
			isOneIdsPresentInResults(LE_TOUQUET, addressResultsDto.getResult(), rawAddress);
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
			isFirstCorrectByIds(LE_TOUQUET,addressResultsDto.getResult(), rawAddress);
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
			isFirstCorrectByIds(STOMER_CAEN_IDs,addressResultsDto.getResult(), rawAddress);
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
			isFirstInExpectedIds(STOMER_CAEN_IDs,addressResultsDto.getResult(), rawAddress);
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
			isFirstCorrectByIds(STOMER_CAEN_IDs,addressResultsDto.getResult(), rawAddress);
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
	
	@Test
	public void cityWithStateAlternate() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer Pagi Ligeris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//when radius >0 is specified it is a bounding box.
			//in this case the two cities saint omer is not in this box
			isFirstCorrectByIds(STOMER_PAYS_DE_LOIRE_IDs,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	/*
	 * 
     _                 _   _               
 ___| |_ _ __ ___  ___| |_| | ___  ___ ___ 
/ __| __| '__/ _ \/ _ \ __| |/ _ \/ __/ __|
\__ \ |_| | |  __/  __/ |_| |  __/\__ \__ \
|___/\__|_|  \___|\___|\__|_|\___||___/___/
                                           

	 */
	//FIXME
	/*@Test
    public void StreetLess() throws InterruptedException, IOException{
        if ((countryTest.contains("DE")|| countryTest.contains("ALL")) ){
            String rawAddress = "Mannheim, D3 4";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
            Assert.assertNotNull(addressResultsDto);
            //https://www.openstreetmap.org/way/317583357
            IsCorrectLocation(8.4632422,49.4883707, addressResultsDto.getResult(), 0.5);
        }
    }
	*/
	
//		
//	
//           _     _                   
//  __ _  __| | __| |_ __ ___  ___ ___ 
// / _` |/ _` |/ _` | '__/ _ \/ __/ __|
//| (_| | (_| | (_| | | |  __/\__ \__ \
// \__,_|\__,_|\__,_|_|  \___||___/___/
//                                     

	
	
	
	@Test
	public void StreetalternateCityTest() throws InterruptedException, IOException{
		if ((countryTest.contains("DE")|| countryTest.contains("ALL")) ){
			String rawAddress = "lindenschmitstr. munich";
			AddressResultsDto addressResultsDto = doGeocoding(rawAddress);
			Assert.assertNotNull(addressResultsDto);
			isOneIdsPresentInResults(new long[]{439424581L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
    public void streetIsAPopularcity() throws InterruptedException, IOException{
        if ((countryTest.contains("FR")|| countryTest.contains("ALL")) ){
            String rawAddress = "rue de lille bailleul";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
            Assert.assertNotNull(addressResultsDto);
            //exact
            isOneIdsPresentInResults(new long[]{478614439},addressResultsDto.getResult(), rawAddress);
            //the street named as the city :rue de bailleul lille
            isOneIdsPresentInResults(new long[]{374933447,328548308}, addressResultsDto.getResult(), rawAddress);
        }
    }
	
	
	

	@Test
	public void germanAddressWithHouseNumberWithLetterTest() throws InterruptedException, IOException{
		if ((countryTest.contains("DE")|| countryTest.contains("ALL")) && houseNumber){
			String rawAddress = "lottumstrasse 13a berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			Assert.assertEquals("13", addressResultsDto.getResult().get(0).getHouseNumber());
			isFirstCorrectById(180331460L,addressResultsDto.getResult(), rawAddress);
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
	}
	
	@Test
    public void germanAddressIncorectZipcode() throws InterruptedException, IOException{
	    //we have decrease mm
        if ((countryTest.contains("DE")|| countryTest.contains("ALL")) && houseNumber){
            String rawAddress = "Bräukerweg 17 58708 Menden";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
            Assert.assertNotNull(addressResultsDto);
            Assert.assertEquals("17", addressResultsDto.getResult().get(0).getHouseNumber());
            isFirstCorrectById(152796629,addressResultsDto.getResult(), rawAddress);
            isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
        }
    }
	
	@Test
    public void germanAddressShouldNotDecompound() throws InterruptedException, IOException{
        if ((countryTest.contains("DE")|| countryTest.contains("ALL")) && houseNumber){
            String rawAddress = "Rosenhain 30 53123 Bonn";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
            Assert.assertNotNull(addressResultsDto);
            Assert.assertEquals("30", addressResultsDto.getResult().get(0).getHouseNumber());
            isFirstCorrectById(7833209,addressResultsDto.getResult(), rawAddress);
            isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
        }
    }
	
	
	
	
	
	@Test
	public void streetWithNumberInNameTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Rue du 14 Juillet nantes";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(26571893L,addressResultsDto.getResult(), rawAddress);
			isCorrectGeocodingLevel(GeocodingLevels.STREET, addressResultsDto.getResult());
		}
	}
	
	@Test
	public void addressWithNumberInStreetNameTest() throws InterruptedException, IOException{
		if ((countryTest.contains("FR")|| countryTest.contains("ALL")) && houseNumber){
			String rawAddress = "11 Rue du 14 Juillet nantes";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			Assert.assertEquals("11", addressResultsDto.getResult().get(0).getHouseNumber());
			isFirstCorrectById(26571893L,addressResultsDto.getResult(), rawAddress);
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
	}
	 
	
	@Test
	public void address1SynonymTest() throws InterruptedException, IOException{
		if ((countryTest.contains("FR")|| countryTest.contains("ALL")) && houseNumber){
			String rawAddress = "9 avenue de l'opera paname";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(192903838L,addressResultsDto.getResult(), rawAddress);
			Assert.assertEquals("9", addressResultsDto.getResult().get(0).getHouseNumber());
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
	}
	
	@Test
	public void address1Test() throws InterruptedException, IOException{
		if ((countryTest.contains("FR")|| countryTest.contains("ALL")) && houseNumber){
			String rawAddress = "9 avenue de l'opera paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(192903838L,addressResultsDto.getResult(), rawAddress);
			Assert.assertEquals("9", addressResultsDto.getResult().get(0).getHouseNumber());
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
	}
	
	@Test
	public void germanHouseNumberTest() throws InterruptedException, IOException{
		if ((countryTest.contains("DE")|| countryTest.contains("ALL")) && houseNumber){
			String rawAddress = "lottumstrasse 4 berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(180331460L,addressResultsDto.getResult(), rawAddress);
			Assert.assertEquals("4", addressResultsDto.getResult().get(0).getHouseNumber());
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
			//withoutcountrycode
			 addressResultsDto = doGeocoding(rawAddress);
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(180331460L,addressResultsDto.getResult(), rawAddress);
			Assert.assertEquals("4", addressResultsDto.getResult().get(0).getHouseNumber());
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
	}
	
	@Test
	public void germanHouseNumber_Not_well_placedTest() throws InterruptedException, IOException{
		if ((countryTest.contains("DE")|| countryTest.contains("ALL")) && houseNumber){
			String rawAddress = "4 lottumstrasse berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(180331460L,addressResultsDto.getResult(), rawAddress);
			Assert.assertEquals("4", addressResultsDto.getResult().get(0).getHouseNumber());
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
			//withoutcountrycode
			 addressResultsDto = doGeocoding(rawAddress);
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(180331460L,addressResultsDto.getResult(), rawAddress);
			Assert.assertEquals("4", addressResultsDto.getResult().get(0).getHouseNumber());
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
	}
	
	
	
	@Test
	public void address1InterpolationTest() throws InterruptedException, IOException{
		if ((countryTest.contains("FR")|| countryTest.contains("ALL")) && houseNumber){
			//40 is not a number known in the database, we interpolate it at runtime
			String rawAddress = "40 avenue de l'opera paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(4018042L,addressResultsDto.getResult(), rawAddress);
			Assert.assertEquals("40", addressResultsDto.getResult().get(0).getHouseNumber());
			isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
	}
	
	
	
	
	
	
	
	
	
	@Test
	public void unknownPostcodeShouldNotdisturbtheGeocodingTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			//13000 is not the correct zip but the city one, correct is 13003
			String rawAddress = "rue de crimée 13000 marseille";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(28117508L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void streetWithCitySynonymTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paname";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	
	@Test
	public void streetWithCitySynonymAccentInStreetTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Plâce Vendôme, Paname";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetZipTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, 75000";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void zipStreetTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "75000 Place Vendôme";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCityTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreetTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreetWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Vendôme";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			
			//should find route de paris at vendome first
			isOneIdsPresentInResults(new long[]{406556865L,406556866L},addressResultsDto.getResult(), rawAddress);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void StreetcityWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Vendôme Paris ";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			
			//should find place or passage vendome first
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_1UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_2UnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto tutu";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void cityStreet_LotUnnecessaryWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris Place Vendôme toto tutu tata ";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_1UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_2UncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto tata";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetWithCity_lotUncessecerayWordTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place Vendôme, Paris toto tata tutu tete";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void synonymStreetTest() throws InterruptedException, IOException{
		//http://www.openstreetmap.org/way/4234145#map=19/48.86747/2.32943
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place des Conquêtes, Paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void streetSynonymWithCitySynonymTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Place des Conquêtes, Paname";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	

	
	@Test
	public void addressStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweizer grenoble";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isFirstInExpectedIds(DOCTEUR_SWEITZER_GRENOBLE,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	
	@Test
    public void addressCityFuzzy() throws InterruptedException, IOException{
        if (countryTest.contains("DE")|| countryTest.contains("ALL")){
            String rawAddress = "docteur schweitzer grenobe";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
            Assert.assertNotNull(addressResultsDto);
            //the osm one , the geonames one
            isFirstInExpectedIds(DOCTEUR_SWEITZER_GRENOBLE,addressResultsDto.getResult(), rawAddress);
        }
    }
	
	@Test
	public void addressCityAndStreetNameFuzzy() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "docteur schweitzer grenobe";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(DOCTEUR_SWEITZER_GRENOBLE,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressStreetNameIsACity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "rue de dunkerque, paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//the osm one , the geonames one
			isFirstInExpectedIds(RUE_DUNKERQUE_PARIS,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressStreetNameIsABigCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "rue de paris, dunkerque";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds( new long[]{ 2428047946L,2428047947L,20944097L,72518994L,181223657L,181224204L,234781172L,235661698L,235661699L,355469642L},addressResultsDto.getResult(), rawAddress);
		}
	}
	

	@Test
	public void addressWithoutStreetTypeCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "jean jaures bailleul";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(79424401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void addressWithoutStreetTypeZip() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "jean jaures 59270";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(79424401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	@Test
	public void addressStreetZip() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "jean jaures 59270";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(79424401L,addressResultsDto.getResult(), rawAddress);
		}
	}
	
//	
//     _                   _                      _ 
// ___| |_ _ __ _   _  ___| |_ _   _ _ __ ___  __| |
/// __| __| '__| | | |/ __| __| | | | '__/ _ \/ _` |
//\__ \ |_| |  | |_| | (__| |_| |_| | | |  __/ (_| |
//|___/\__|_|   \__,_|\___|\__|\__,_|_|  \___|\__,_|
//  
//	 
//	
	@Test
	public void structuredAddressGermanHouseNumberTest() throws InterruptedException, IOException{
		if ((countryTest.contains("DE")|| countryTest.contains("ALL")) && houseNumber){
	Address address = new Address();
	address.setHouseNumber("4");
	address.setStreetName("lottumstrasse");
	address.setStreetType("");
	address.setZipCode("");
	address.setCity("berlin");
	address.setState("");
	AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "DE");
	Assert.assertNotNull(addressResultsDto);
	isFirstCorrectById(180331460L,addressResultsDto.getResult(), address.toString());
	Assert.assertEquals("4", addressResultsDto.getResult().get(0).getHouseNumber());
	isCorrectGeocodingLevel(GeocodingLevels.HOUSE_NUMBER, addressResultsDto.getResult());
		}
		}
	
	@Test
	public void structuredAddress1Test() throws InterruptedException, IOException{
		if ((countryTest.contains("FR")|| countryTest.contains("ALL")) && houseNumber){
			Address address = new Address();
			address.setHouseNumber("9");
			address.setStreetName("de l'opéra");
			address.setStreetType("avenue");
			address.setZipCode("");
			address.setCity("paris");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(192903838L,addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredAddress1SynonymTest() throws InterruptedException, IOException{
		if ((countryTest.contains("FR")|| countryTest.contains("ALL")) && houseNumber){
			Address address = new Address();
			address.setHouseNumber("9");
			address.setStreetName("de l'opéra");
			address.setStreetType("avenue");
			address.setZipCode("");
			address.setCity("paname");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(192903838L,addressResultsDto.getResult(), address.toString());
		}
	}
	
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
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds(new long[]{4234145L,4234144L,4234146L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	
	
	@Test
	public void structuredCityStreetWithoutPlaceTypeTest() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
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
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
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
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
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
		isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
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
		isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
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
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
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
		isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
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
		isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
	}
	}

	@Test
	public void structuredSynonymStreetTest() throws InterruptedException, IOException{
		//http://www.openstreetmap.org/way/4234145#map=19/48.86747/2.32943
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
			isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
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
		isFirstCorrectByIds(PLACE_VENDOME_PARIS,addressResultsDto.getResult(), address.toString());
	}
	}
	
	@Test
	public void structuredsubdivision() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("");
			address.setStreetType("");
			address.setZipCode("");
			address.setCitySubdivision("la defense");
			address.setState("");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{8504417L,4545578227L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredquaterCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
				Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("");
			address.setStreetType("");
			address.setZipCode("");
			address.setCitySubdivision("Vaugirard");
			address.setCity("Paris");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(VAUGIRARD_QUATER_ID,addressResultsDto.getResult(), address.toString());
		}
	}
	
		
	@Test
	public void structuredarrondissement() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			Address address = new Address();
			address.setHouseNumber("");
			address.setStreetName("");
			address.setStreetType("");
			address.setZipCode("");
			address.setCitySubdivision("paris 15e");
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(address, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(VAUGIRARD_QUATER_ID,addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds(new long[]{5990522l,488881135L,204138290L,204138293L},addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds(new long[]{5990522,488881135L,204138290L,204138293L},addressResultsDto.getResult(), address.toString());
		}
	}
	
	@Test
	public void structuredAddressCityAndStreetNameFuzzy() throws InterruptedException, IOException{
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
			isFirstInExpectedIds(new long[]{5990522,488881135L,204138290L,204138293L},addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds(RUE_DUNKERQUE_PARIS,addressResultsDto.getResult(), address.toString());
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
			isFirstInExpectedIds( new long[]{ 2428047946L,2428047947L,20944097L,72518994L,181223657L,181224204L,234781172L,235661698L,235661699L,355469642L},addressResultsDto.getResult(), address.toString());
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
			isFirstCorrectById(79424401L,addressResultsDto.getResult(), address.toString());
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
			isFirstCorrectById(79424401L,addressResultsDto.getResult(), address.toString());
		}
	}
	
	

	
	
	
//	
//             _ 
// _ __   ___ (_)
//| '_ \ / _ \| |
//| |_) | (_) | |
//| .__/ \___/|_|
//|_|      
//	
	
//
//	@Test
//	public void poi() throws InterruptedException, IOException{
//		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
//			String rawAddress = "tour eiffel";
//			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
//			Assert.assertNotNull(addressResultsDto);
//			//the osm one , the geonames one
//			isCorrectByAtLeastOneIds(new Long[]{5013364L,6254976L},addressResultsDto.getResult(), rawAddress);
//		}
//	}
//	
//	@Test
//	public void poiFuzzy() throws InterruptedException, IOException{
//		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
//			String rawAddress = "tour eifel";
//			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
//			Assert.assertNotNull(addressResultsDto);
//			//the osm one , the geonames one
//			isCorrectByAtLeastOneIds(new Long[]{5013364L,6254976L},addressResultsDto.getResult(), rawAddress);
//		}
//	}
//	
//	@Test
//	public void poiCity() throws InterruptedException, IOException{
//		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
//			String rawAddress = "Chez papa  paris";
//			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
//			Assert.assertNotNull(addressResultsDto);
//			isCorrectById(1830649549L,addressResultsDto.getResult(), rawAddress);
//		}
//	}
//	
//	@Test
//	public void poiTypeCity() throws InterruptedException, IOException{
//		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
//			String rawAddress = "restaurant chez papa  paris";
//			AddressResultsDto addressResultsDto = doRequestOnCountry(rawAddress, "FR");
//			Assert.assertNotNull(addressResultsDto);
//			isCorrectById(1830649549L,addressResultsDto.getResult(), rawAddress);
//		}
//	}
	
	
	
	
	
//	
//           _         _ _       _     _             
// ___ _   _| |__   __| (_)_   _(_)___(_) ___  _ __  
/// __| | | | '_ \ / _` | \ \ / / / __| |/ _ \| '_ \ 
//\__ \ |_| | |_) | (_| | |\ V /| \__ \ | (_) | | | |
//|___/\__,_|_.__/ \__,_|_| \_/ |_|___/_|\___/|_| |_|
// 
	 
	@Test
	public void subdivision() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "la defense";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{8504417L,4545578227L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void quaterCity() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Vaugirard, Paris";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//Boulevard should be there
			isOneIdsPresentInResults(VAUGIRARD_IDS,addressResultsDto.getResult(), rawAddress);
			//isFirstCorrectById(VAUGIRARD_QUATER_ID,addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void quaterZip() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Vaugirard, 75015";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			//boulevard
			isOneIdsPresentInResults(VAUGIRARD_IDS,addressResultsDto.getResult(), rawAddress);
			//quater
			isOneIdsPresentInResults(new long[]{2970479L},addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void arrondissement() throws InterruptedException, IOException{
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "Paris 15e";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
			Assert.assertNotNull(addressResultsDto);
			isFirstCorrectById(VAUGIRARD_QUATER_ID,addressResultsDto.getResult(), rawAddress);
		}
	}
	
//	
//	  ___ ___  _ __ ___  _ __   ___  _   _ _ __   __| |
//	 / __/ _ \| '_ ` _ \| '_ \ / _ \| | | | '_ \ / _` |
//	| (_| (_) | | | | | | |_) | (_) | |_| | | | | (_| |
//	 \___\___/|_| |_| |_| .__/ \___/ \__,_|_| |_|\__,_|
//	                    |_|                            

		 
//decompound	
 
 
/*
 * we search for a street that is concatenate in the index with ß :Stauffenbergstraße
 */
	
	@Test
	public void compoundConcatenateTwoWord_Concatenate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenbergstraße, Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundConcatenateTwoWord_vs_Separate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenberg straße , Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	//todo
	@Test
	public void compoundConcatenateTwoWord_vs_SeparateSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenberg strasse , Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	@Test
	public void compoundConcatenateTwoWord_concatenateSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenbergstrasse , Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundConcatenateTwoWord_vs_SeparateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenberg str , Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundConcatenateTwoWord_concatenateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenbergstr , Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	
	
	@Test
	public void compoundConcatenateTwoWord_vs_SeparateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenberg str. , Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundConcatenateTwoWord_concatenateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Stauffenbergstr. , Berlin";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{4597354L,465724298L,4597354L}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	
	
	
	 //we search for a street that is concatenate in the index with str :Naabstr, Teublitz: 280017483
	 
		@Test
		public void compoundConcatenateTwoWordStr_Concatenate() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naabstraße, Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(new long[]{280017483,28971876l,372824213}, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		@Test
		public void compoundConcatenateTwoWordStr_vs_Separate() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naab straße, Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(NAABSTRASSE_IDS, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		@Test
		public void compoundConcatenateTwoWordStr_vs_SeparateSynonym1() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naab strasse, Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(NAABSTRASSE_IDS, addressResultsDto.getResult(), rawAddress);
			}
		}
		@Test
		public void compoundConcatenateTwoWordStr_concatenateSynonym1() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naabstrasse, Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(NAABSTRASSE_IDS, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		@Test
		public void compoundConcatenateTwoWordStr_vs_SeparateSynonym2() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naab str, Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(NAABSTRASSE_IDS, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		@Test
		public void compoundConcatenateTwoWordStr_concatenateSynonym2() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naabstr, Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(NAABSTRASSE_IDS, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		@Test
		public void compoundConcatenateTwoWordStr_SeparateSynonym3() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naabstr., Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(NAABSTRASSE_IDS, addressResultsDto.getResult(), rawAddress);
			}
		}
		
	
		
		@Test
		public void compoundConcatenateTwoWordStr_concatenateSynonym3() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Naabstr., Teublitz";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
				Assert.assertNotNull(addressResultsDto);
				isFirstInExpectedIds(NAABSTRASSE_IDS, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		
		
		@Test
		public void compoundConcatenateTwoWord_vs_SeparateSynonym_woCountryCode() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Klopstockstr berlin";
				AddressResultsDto addressResultsDto = doGeocoding(rawAddress);
				Assert.assertNotNull(addressResultsDto);
				
				//we should find all the streets 
				isAllIdsPresentInResults(new long[]{51268850L,22650548L,26590565L,145635383L}, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		
		@Test
		public void compoundConcatenateTwoWord_vs_SeparateSynonym_SynomysExpansion() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Klopstockstr berlin";
				AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress,"DE");
				Assert.assertNotNull(addressResultsDto);
				//we should find all the streets because there is two streets
				isOneIdsPresentInResults(new long[]{51268850L,22650548L,33078711L,26590565L,145635383L}, addressResultsDto.getResult(), rawAddress);
				isOneIdsPresentInResults(new long[]{145635383,22650548L,33078711L,26590565L,145635383L}, addressResultsDto.getResult(), rawAddress);
				isOneIdsPresentInResults(new long[]{26590565L}, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		@Test
		public void compoundConcatenateTwoWord_vs_SeparateSynonym2_woCountryCode() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Klopstockstr. berlin";
				AddressResultsDto addressResultsDto = doGeocoding(rawAddress);
				Assert.assertNotNull(addressResultsDto);
				//we should find all the streets 
				isAllIdsPresentInResults(new long[]{51268850L,22650548L,145635383L,26590565L}, addressResultsDto.getResult(), rawAddress);
			}
		}
		
		@Test
		public void compoundConcatenateTwoWord_vs_SeparateSynonym2__withSuburb_woCountryCode() throws InterruptedException, IOException{
			if (countryTest.contains("DE")|| countryTest.contains("ALL")){
				String rawAddress = "Klopstockstr Zehlendorf berlin";
				AddressResultsDto addressResultsDto = doGeocoding(rawAddress);
				Assert.assertNotNull(addressResultsDto);
				isOneIdsPresentInResults(new long[]{33078711L,145635383L}, addressResultsDto.getResult(), rawAddress);
			}
		}
	
		
		

 //we search for a street that is concatenate in the index with ss : Bachstrasse, sisseln 189022422

@Test
public void compoundConcatenateTwoWordss_Concatenate() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bachstraße, sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}

@Test
public void compoundConcatenateTwoWordss_vs_Separate() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bach straße, sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}

@Test
public void compoundConcatenateTwoWords_vs_SeparateSynonym1() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bach strasse, sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}
@Test
public void compoundConcatenateTwoWords_concatenateSynonym1() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bachstrasse, sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}

@Test
public void compoundConcatenateTwoWords_vs_SeparateSynonym2() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bach str, sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}

@Test
public void compoundConcatenateTwoWords_concatenateSynonym2() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bachstr, sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}

@Test
public void compoundConcatenateTwoWords_vs_SeparateSynonym3() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bach str., sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}

@Test
public void compoundConcatenateTwoWords_concatenateSynonym3() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bachstr., sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}
	

@Test
public void compoundConcatenateTwoWords_concatenateSynonym3_NoCountryCode() throws InterruptedException, IOException{
	if (countryTest.contains("CH")|| countryTest.contains("ALL")){
		String rawAddress = "Bachstr., sisseln";
		AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "CH");
		Assert.assertNotNull(addressResultsDto);
		isFirstInExpectedIds(new long[]{189022422}, addressResultsDto.getResult(), rawAddress);
	}
}
	
	
	
	 // we search for a street that is separte in the index ss :Magdeburger Strasse
	 
	 
	@Test
	public void compoundSeparateTwoWord_vs_Concatenate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "MagdeburgerStraße , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateTwoWord_Separate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Magdeburger Straße , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateTwoWord_SeparateSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Magdeburger Strasse , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateTwoWord_vs_concatenateSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "MagdeburgerStrasse , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateTwoWord_SeparateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Magdeburger Str , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateTwoWord_vs_concatenateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "MagdeburgerStr , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateTwoWord_SeparateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Magdeburger Str. , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateTwoWord_vs_concatenateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "MagdeburgerStr. , oschersleben";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(MAGDEBURGERSTRASSE, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	
	 // we search for a street that is separate in the index ß :Heimfelder Straße : 3235903
	 
	 
	@Test
	public void compoundSeparateBTwoWord_vs_Concatenate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "HeimfelderStraße , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateBTwoWord_Separate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Heimfelder Straße , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateBTwoWordSeparateSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Heimfelder Strasse , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateBTwoWord_vs_concatenateSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "HeimfelderStrasse , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateBTwoWord_SeparateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Heimfelder Str , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateBTwoWord_vs_concatenateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "HeimfelderStr , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateBTwoWord_SeparateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Heimfelder Str. , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	@Test
	public void compoundSeparateBTwoWord_ConcatenateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Heimfelder Str. , hamburg";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{3235903L,27248530L,4228264L,55802670}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	 // we search for a street that is separate in the index str. :Gutenberger Str. : 25199461
	 
	 
	
	@Test
	public void compoundSeparateStrDotTwoWord_vs_Concatenate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "GutenbergerStraße , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateStrDotTwoWord_Separate() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Gutenberger Straße , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateStrDotTwoWordSeparateSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Gutenberger Strasse , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateStrDotTwoWord_ConcatenateeSynonym1() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Gutenberger Strasse , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateStrDotTwoWord_SeparateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Gutenberger Str , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateStrDotTwoWord_vs_ConcatenateSynonym2() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "GutenbergerStr , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateStrDotTwoWord_SeparateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Gutenberger Str. , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
	public void compoundSeparateStrDotTwoWord_ConcatenateSynonym3() throws InterruptedException, IOException{
		if (countryTest.contains("DE")|| countryTest.contains("ALL")){
			String rawAddress = "Gutenberger Str. , owen";
			AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "DE");
			Assert.assertNotNull(addressResultsDto);
			isFirstInExpectedIds(new long[]{25199461L,}, addressResultsDto.getResult(), rawAddress);
		}
	}
	
	@Test
    public void zip_UK() throws InterruptedException, IOException{
        if (countryTest.contains("GB")|| countryTest.contains("ALL")){
            String rawAddress = "CA11 0TQ";
            AddressResultsDto addressResultsDto = doGeocoding(rawAddress);
            Assert.assertNotNull(addressResultsDto);
            isFirstInExpectedIds(new long[]{36860725,2647900}, addressResultsDto.getResult(), rawAddress);
            
            //another one
            rawAddress = "CA9 3BW";
            addressResultsDto = doGeocoding(rawAddress);
            Assert.assertNotNull(addressResultsDto);
            isFirstInExpectedIds(new long[]{37036084}, addressResultsDto.getResult(), rawAddress);
            
        }
    }
	
	
	
//
//   __       _ _ _            _   
// / _|_   _| | | |_ _____  _| |_ 
//| |_| | | | | | __/ _ \ \/ / __|
//|  _| |_| | | | ||  __/>  <| |_ 
//|_|  \__,_|_|_|\__\___/_/\_\\__|
//      
// 
	
//	@Test
//	public void cityShouldBeFirstResult() throws InterruptedException, IOException{
//		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
//			String rawAddress = "saint omer";
//			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
//			Assert.assertNotNull(fulltextResultsDto);
//			IsCorrectPlaceType("City",fulltextResultsDto.getResults().get(0).getPlacetype());
//			isFulltextCorrectById(2214006L,fulltextResultsDto.getResults(), rawAddress);
//		}
//	}
//	
//	@Test
//	public void admShouldBeFirstResult() throws InterruptedException, IOException{
//		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
//			String rawAddress = "normandie";
//			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
//			Assert.assertNotNull(fulltextResultsDto);
//			IsCorrectPlaceType("Adm",fulltextResultsDto.getResults().get(0).getPlacetype());
//			isFulltextCorrectById(3793170L,fulltextResultsDto.getResults(), rawAddress);
//		}
//	}
	
	@Test
	public void fulltextTest() throws InterruptedException, IOException{
		
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "normandie";
			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
			Assert.assertNotNull(fulltextResultsDto);
			isFulltextCorrectById(3793170L,fulltextResultsDto.getResults(), rawAddress);
			IsCorrectPlaceType("Adm",fulltextResultsDto.getResults().get(0).getPlacetype());
		}
		if (countryTest.contains("FR")|| countryTest.contains("ALL")){
			String rawAddress = "saint omer";
			FulltextResultsDto fulltextResultsDto = doFulltext(rawAddress);
			Assert.assertNotNull(fulltextResultsDto);
			printResult(fulltextResultsDto,0);
			printResult(fulltextResultsDto,1);
			isFultextFirstCorrectByIds(STOMER_NPDC_IDs,fulltextResultsDto.getResults(), rawAddress);
			IsCorrectPlaceType("City",fulltextResultsDto.getResults().get(0).getPlacetype());
		}
	}
	
	
	
/*	                     _       _                           
     ___ _ __   ___  ___(_) __ _| |   ___ __ _ ___  ___  ___ 
    / __| '_ \ / _ \/ __| |/ _` | |  / __/ _` / __|/ _ \/ __|
    \__ \ |_) |  __/ (__| | (_| | | | (_| (_| \__ \  __/\__ \
    |___/ .__/ \___|\___|_|\__,_|_|  \___\__,_|___/\___||___/
    |_|                                                  

	*/
	
	
	@Test
    public void RNtheGeocodingTest() throws InterruptedException, IOException{
        if (countryTest.contains("FR")|| countryTest.contains("ALL")){
            //13000 is not the correct zip but the city one, correct is 13003
            String rawAddress = "rn43 62 910";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
            Assert.assertNotNull(addressResultsDto);
            isFirstCorrectById(303318584,addressResultsDto.getResult(), rawAddress);
        }
    }

	/*FIXME
	@Test
    public void RouteNationaleGeocodingTest() throws InterruptedException, IOException{
        if (countryTest.contains("FR")|| countryTest.contains("ALL")){
            //13000 is not the correct zip but the city one, correct is 13003
            String rawAddress = "route nationale 43 serques";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
            Assert.assertNotNull(addressResultsDto);
            isFirstCorrectById(303318584,addressResultsDto.getResult(), rawAddress);
        }
    }
	
	@Test
    public void D_XXXGeocodingTest() throws InterruptedException, IOException{
        if (countryTest.contains("FR")|| countryTest.contains("ALL")){
            //13000 is not the correct zip but the city one, correct is 13003
            String rawAddress = "D 943 serques";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
            Assert.assertNotNull(addressResultsDto);
            isFirstCorrectById(303318584,addressResultsDto.getResult(), rawAddress);
        }
    }
    
    @Test
    public void DXXXGeocodingTest() throws InterruptedException, IOException{
        if (countryTest.contains("FR")|| countryTest.contains("ALL")){
            //13000 is not the correct zip but the city one, correct is 13003
            String rawAddress = "D943 serques";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
            Assert.assertNotNull(addressResultsDto);
            isFirstCorrectById(303318584,addressResultsDto.getResult(), rawAddress);
        }
    }
    
    @Test
    public void RNtheGeocodingTest() throws InterruptedException, IOException{
        if (countryTest.contains("FR")|| countryTest.contains("ALL")){
            //13000 is not the correct zip but the city one, correct is 13003
            String rawAddress = "rn43 62 910";
            AddressResultsDto addressResultsDto = doGeocodingOnCountry(rawAddress, "FR");
            Assert.assertNotNull(addressResultsDto);
            isFirstCorrectById(303318584,addressResultsDto.getResult(), rawAddress);
        }
    }
	*/
//	
//@Test
//public void ExactMatchShouldHaveMoreImportanceThanFuzzy(){
//	String URLToCall = "/geocoding/?&country=DE&address=Lindenstra%C3%9Fe%203%2C%20Leonberg%2C%2071229&postal=true";
//	String fullURLToCall = BASE_SERVER_URL+URLToCall +"&format=json";
//	System.out.println(fullURLToCall);
//	AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
//	System.out.println(result.getResult());
//	Assert.assertEquals("Lindenstraße", result.getResult().get(0).getStreetName());
//}
//
//@Test
//public void FuzzyShouldWork(){
//	//todo /geocoding/?&country=DE&address=Rheinstra%C3%9Fe%2029%2C%20Wiesbaden%2C%2065185
//
//	String URLToCall = "geocoding/?&country=DE&address=Demmelsjochstr%2052%2C%20Bad%20T%C3%B6lz%2C%2083646&postal=true";
//	String fullURLToCall = BASE_SERVER_URL+URLToCall +"&format=json";
//	System.out.println(fullURLToCall);
//	AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
//	System.out.println(result.getResult());
//	Assert.assertEquals("Demmeljochstraße", result.getResult().get(0).getStreetName());
//	Assert.assertEquals("Bad Tölz", result.getResult().get(0).getCity());
//}


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

public static void printResult(FulltextResultsDto fulltextResultsDto, int position){
	SolrResponseDto solrResponseDto = fulltextResultsDto.getResults().get(position);
	System.out.println("("+solrResponseDto.placetype+"/"+solrResponseDto.getOpenstreetmap_id()+")"+solrResponseDto.getFully_qualified_name());
}

@SuppressWarnings("deprecation")
public FulltextResultsDto doFulltext(String text){
    nbTest++;
	String fullURLToCall = BASE_SERVER_URL+FULLTEXT_BASE_SERVER_URI+URLEncoder.encode(text)+"&format=json";
	System.out.println(fullURLToCall);
	FulltextQuery query = new FulltextQuery(text);
	FulltextResultsDto result  = fulltextClient.executeQuery(query);
	return result;
}

@SuppressWarnings("deprecation")
public FulltextResultsDto doFulltextOnPlacetype(String text,String placetype){
    nbTest++;
	String fullURLToCall = BASE_SERVER_URL+FULLTEXT_BASE_SERVER_URI+URLEncoder.encode(text)+"&format=json&placetype="+placetype;
	FulltextResultsDto result  = restClient.get(fullURLToCall, FulltextResultsDto.class, OutputFormat.JSON);
	return result;
}

@SuppressWarnings("deprecation")
public FulltextResultsDto doFulltextOnPlacetypes(String text,String[] placeTypes){
    nbTest++;
	String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(text)+"&format=json";
	for (String placetype:placeTypes){
		fullURLToCall = fullURLToCall+"&placetype="+placetype;
	}
	FulltextResultsDto result  = restClient.get(fullURLToCall, FulltextResultsDto.class, OutputFormat.JSON);
	return result;
}


//------------------------------------------------------------------------------------
@SuppressWarnings("deprecation")
public AddressResultsDto doGeocoding (String address){
    nbTest++;
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&fuzzy=true&format=json";
		System.out.println(fullURLToCall);
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		return result;
	}
@SuppressWarnings("deprecation")
public AddressResultsDto doGeocodingPostal(String address){
    nbTest++;
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&fuzzy=true&format=json&postal=true";
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		return result;
	}
	
	
		
@SuppressWarnings("deprecation")
	private AddressResultsDto doGeocodingOnCountry(String address,String countryCode){
        nbTest++;
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_SERVER_URI+URLEncoder.encode(address)+"&fuzzy=true&format=json&country="+countryCode;
		System.out.println(fullURLToCall);
		AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
		if (result!=null && result.getNumFound()!=0){
			System.out.println(result.getResult().get(0).getSourceId()+" : "+address+ " : "+result.getResult().get(0));
		} else {
			System.out.println(address+ " : no results");
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
    private AddressResultsDto doGeocodingOnCountry(Address address,String countryCode){
	    nbTest++;
		String fullURLToCall = BASE_SERVER_URL+GEOCODING_BASE_STRUCTURED_SERVER_URI+"apikey=111&fuzzy=true";//just add apikey to not to have to managed ? vs &
		if (address.getStreetType()!=null){
			fullURLToCall=fullURLToCall+"&streetType="+URLEncoder.encode(address.getStreetType());
		}
		if (address.getStreetName()!=null){
			fullURLToCall=fullURLToCall+"&streetName="+URLEncoder.encode(address.getStreetName());
		}
		if (address.getCity()!=null){
			fullURLToCall=fullURLToCall+"&city="+URLEncoder.encode(address.getCity());
		}
		if (address.getCitySubdivision()!=null){
			fullURLToCall=fullURLToCall+"&citySubdivision="+URLEncoder.encode(address.getCitySubdivision());
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
	    nbTest++;
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
	    nbTest++;
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
	public boolean IsCorrectPlaceType(GeocodingLevels expected, List<Address> actual){
		if (actual == null){
			return false;
		}
		if(actual.size()>0 && actual.get(0)!=null && actual.get(0).getGeocodingLevel().equals(expected)){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean IsCorrectLocation(Double expectedlat, Double expectedlng,List<Address> actual, double distanceTolérance){
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
	
	/**
	 * 
	 * @param expectedOpenstreetmapId
	 * @param actual
	 * @param rawAddress
	 * @return
	 */
	private boolean isFirstCorrectById(long expectedOpenstreetmapId,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			Address address = actual.get(0);
			if (address.getSourceId() != null && expectedOpenstreetmapId != address.getSourceId()){
				Assert.fail(rawAddress +": expected "+expectedOpenstreetmapId+ " but was " +address.getSourceId()+" / "+address);
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
	
	// all ids should be present
	private boolean isAllIdsPresentInResults(long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
			for(Address address: actual){
				if (address!=null && address.getSourceId()!=null){
					ids.add(address.getSourceId());
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
	
	private boolean isOneIdsPresentInResults(long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
			for(Address address: actual){
				if (address!=null && address.getSourceId()!=null){
					ids.add(address.getSourceId());
				}
			}
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (ids.contains(expectedOpenstreetmapId)){
					return true;
				} 
			}
			return false;
		}
		Assert.fail(rawAddress +": expected "+expectedOpenstreetmapIds+ " but no result ");
		return true;
	}
	
	public boolean isFulltextCorrectByOSMIds(Long[] expectedOpenstreetmapIds,List<SolrResponseDto> actual, String rawAddress){
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
	
	/**
	 * first id should be in the list
	 * @param expectedOpenstreetmapIds
	 * @param actual
	 * @param rawAddress
	 * @return
	 */
	private boolean isFirstInExpectedIds(long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null){
			List<Long> ids = new ArrayList<Long>();
			Address address = actual.get(0);
			if (address.getSourceId()!=null){
				ids.add(address.getSourceId());
			}
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (ids.contains(expectedOpenstreetmapId)){
					return true;
				} 
			}
			Assert.fail(rawAddress +": no ids were found, first is "+ids.get(0)+", "+address.getFormatedFull());
			return false;
		}
		Assert.fail(rawAddress +": no results were found, fisrt is null");
		return false;
	}
	
	private boolean isFultextFirstCorrectByIds(long[] expectedOpenstreetmapIds,List<SolrResponseDto> actual, String rawAddress){
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
	
	private boolean isFirstCorrectByIds(long[] expectedOpenstreetmapIds,List<Address> actual, String rawAddress){
		if(actual.size()>0 && actual.get(0)!=null && actual.get(0).getSourceId()!=null){
			Long actualId = actual.get(0).getSourceId(); 
			
			for (long expectedOpenstreetmapId:expectedOpenstreetmapIds){
				if (actualId==expectedOpenstreetmapId){
					return true;
				} 
			}
			Assert.fail(rawAddress +": no ids were found,id is "+actualId+", "+actual.get(0).getFormatedFull());
			return false;
		}
		Assert.fail(rawAddress +": no results were found");
		return false;
	}
	

	private boolean isCorrectGeocodingLevel(GeocodingLevels expectedLevel,List<Address> actual){
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
	
	public boolean IsCorrectLocation(Double expectedlat, Double expectedlng, Double actualLat,Double actualLng, double distanceTolérance){
		return true;
	}
	
	public boolean IsCorrectById(long expectedOpenstreetmapId, long actualOpenstreetmapId){
		return true;
	}





}
