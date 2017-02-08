package com.gisgraphy.integration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

import net.sf.jstester.util.Assert;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.fulltext.FullTextSearchEngine;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IsolrClient;
import com.gisgraphy.fulltext.SolrClient;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.importer.ImporterException;
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
public class URLFileGeocodingTest {

	public final static String FILEPATH = "integrationGeococodingUrls.csv";
	public final static String BASE_SERVER_URL ="http://localhost:8080/";
	public final static String OUTPUT_CLEAN_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_clean.csv";
	public final static String OUTPUT_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output.csv";
	public final static String OUTPUT_FAIL_FILE = "/home/gisgraphy/Bureau/integrationGeococodingUrls_output_fail.csv";
	
	IRestClient restClient = new RestClient();
	
	@Autowired
	private IsolrClient solrClient;
	
	private boolean fake=true;

	
	@Test
	public void geocodingPostalTest() throws InterruptedException, IOException, ParseException{
		geocodingPostalTest_internal(true);
		
	}
	
	@Test
	public void geocodingNotPostalTest() throws InterruptedException, IOException, ParseException{
		geocodingPostalTest_internal(false);
		
	}

	public void geocodingPostalTest_internal(boolean postal) throws InterruptedException, IOException, ParseException{
		URL url = Thread.currentThread().getContextClassLoader().getResource(FILEPATH);
		File file = new File(url.getPath());
		int nbTest = 0;
		int nb_noresponse=0;
		int cumulativeDistance = 0;
		int cumulativeDistance_when_found = 0;
		int failedNumber = 0;
		int found=0;
		int failedpermissive=0;
		BufferedReader reader;
		if (file == null){
			throw new IllegalArgumentException("file can not be null");
		}
		if (!file.exists() || !file.canRead()){
			throw new RuntimeException(file+" does not exists or can not be read");
		} 
		try {
			reader = new BufferedReader(new  FileReader(file));
		} catch (Exception e) {
			throw new RuntimeException("Exception for file :"+file );
		}
		
		File fout = new File(OUTPUT_FILE);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		
		File foutFail = new File(OUTPUT_FAIL_FILE);
		FileOutputStream fosFail = new FileOutputStream(foutFail);
		BufferedWriter bwFail = new BufferedWriter(new OutputStreamWriter(fosFail));
		
	/*	File faddress = new File(OUTPUT_CLEAN_FILE);
		FileOutputStream faddressOS = new FileOutputStream(faddress);
		BufferedWriter faddressOSBW = new BufferedWriter(new OutputStreamWriter(faddressOS));*/
		
		String input;
		long start = System.currentTimeMillis();
		while ((input = reader.readLine()) !=null){
		
		try {
		    input = reader.readLine();
		} catch (IOException e1) {
		    throw new ImporterException("can not read line ", e1);
		}
		if (input != null) {
			if (input.startsWith("#")){
				continue;
			}
			System.out.println("test no "+(nbTest+1));
			String[] fields = input.split("\\t");
			if (fields.length !=4){
				throw new RuntimeException("lines of files should have 3 fields separated by tabulation");
			}
			/*String addressstr = fields[1].replace("/geocoding/?country=DE&address=", "");
			addressstr = fields[1].replace("/geocoding/?&country=DE&address=", "");
			
			faddressOSBW.write(URLDecoder.decode(addressstr)+"\t"+fields[1]+"\t"+fields[2]);
			faddressOSBW.newLine();
			faddressOSBW.flush();*/
			
			String countrycode = fields[0];
			String fields1 = fields[2];
			String fields2= fields[3];
			Float expectedLat = GeolocHelper.parseInternationalDouble(fields1);
			Float expectedLng =GeolocHelper.parseInternationalDouble(fields2);
			System.out.println("expected : "+expectedLat+" "+expectedLng);
			String rawAddress = fields[1];
			String fullURLToCall;
			if (countrycode!=null && !countrycode.trim().equals("")){
				 fullURLToCall = BASE_SERVER_URL+"/geocoding/?country="+countrycode+"&address="+URLEncoder.encode(rawAddress)+"&format=json";
			} else {
				 fullURLToCall = BASE_SERVER_URL+"/geocoding/?address="+URLEncoder.encode(rawAddress)+"&format=json";
			}
			
			if (postal){
				fullURLToCall+="&postal=true";
			}
			if (fake){
				System.out.println(rawAddress+ ":" +fullURLToCall);
				continue;
			}
			AddressResultsDto result  = restClient.get(fullURLToCall, AddressResultsDto.class, OutputFormat.JSON);
			System.out.println(fullURLToCall);
			//AddressQuery query = new AddressQuery(address);
			List<Address> addresses = result.getResult();
			if (addresses != null && addresses.size() >=1){
				Address firstAddress = addresses.get(0);
				Double lat  = firstAddress.getLat();
				Double lng = firstAddress.getLng();
				System.out.println("current : "+lat+" "+lng);
				Point currentLocation = GeolocHelper.createPoint(expectedLng, expectedLat);
				Point expectedPoint = GeolocHelper.createPoint(lng, lat);
				double distance = GeolocHelper.distance(currentLocation, expectedPoint);
				System.out.println(distance);
				
				if (distance > 500){
					failedNumber++;
					bwFail.write(rawAddress+"\t"+fields1+"\t"+fields2+"\t"+lat+"\t"+lng+"\t"+distance);
					bwFail.newLine();
					bwFail.flush();
					
				} else {
					cumulativeDistance_when_found +=distance;
					found++;
				}
				if (distance > 10000){
					failedpermissive++;
				}
				nbTest++;
				cumulativeDistance+=distance;
				bw.write(rawAddress+"\t"+fields1+"\t"+fields2+"\t"+lat+"\t"+lng+"\t"+distance);
				bw.newLine();
				
			} else {
				nb_noresponse++;
				bwFail.write(rawAddress+"\t"+fields1+"\t"+fields2+"\t\t\t");
				bwFail.newLine();
			}
		} else {
			break;
		}
	//	Thread.sleep(200);
		}
		System.out.println(nbTest+" tests has been done");
		System.out.println("number of no result = "+nb_noresponse);
		System.out.println("average distance = "+cumulativeDistance/nbTest);
		System.out.println("number of test failed = "+failedNumber);
		System.out.println("including number of failed permissive = "+failedpermissive);
		System.out.println("average distance when found = "+cumulativeDistance_when_found/found);
		bw.flush();
		bw.close();
		
		reader.close();
		long end = System.currentTimeMillis();
		System.out.println("test tooks "+((end-start)/1000)+" seconds");
	}
	
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





}
