package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.addressparser.format.DisplayMode;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Restaurant;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.reversegeocoding.HouseNumberDistance;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.Point;

public class LabelGeneratorTest {

	LabelGenerator generator = LabelGenerator.getInstance();
	
	BasicAddressFormater formater =  BasicAddressFormater.getInstance();
	
@Test
public void testGenerateLabel_GisFeature(){
	GisFeature gisFeature = new City();
	Assert.assertNull(generator.generateLabel(gisFeature));
	
	gisFeature.setName("name");
	Assert.assertEquals("name", generator.generateLabel(gisFeature));
	
	gisFeature.setIsIn("isIn");
	Assert.assertEquals("name, isIn", generator.generateLabel(gisFeature));
	
}

@Test
public void testGenerateLabel_GisFeature_nameAndIsInEquals(){
	GisFeature gisFeature = new City();
	Assert.assertNull(generator.generateLabel(gisFeature));
	
	gisFeature.setName("name");
	Assert.assertEquals("name", generator.generateLabel(gisFeature));
	
	gisFeature.setIsIn("name");
	Assert.assertEquals("name", generator.generateLabel(gisFeature));
	
}


@Test
public void testGenerateLabel_osm(){
	OpenStreetMap osm = new OpenStreetMap();
	Assert.assertNull(generator.generateLabel(osm));
	
	osm.setName("name");
	Assert.assertEquals("name", generator.generateLabel(osm));
	
	osm.setIsIn("isIn");
	Assert.assertEquals("name, isIn", generator.generateLabel(osm));
	
}

@Test
public void testGenerateLabel_Adm(){
	Adm osm = new Adm(1);
	Assert.assertNull(generator.generateLabel(osm));

	osm.setName("name");
	Assert.assertEquals("name", generator.generateLabel(osm));

	osm = new Adm(2);
	Assert.assertNull(generator.generateLabel(osm));

	osm.setName("name");
	Assert.assertEquals("name", generator.generateLabel(osm));

	osm.setAdm1Name("adm1name");
	Assert.assertEquals("name, adm1name", generator.generateLabel(osm));


	osm = new Adm(3);
	Assert.assertNull(generator.generateLabel(osm));

	osm.setName("name");
	Assert.assertEquals("name", generator.generateLabel(osm));

	osm.setAdm1Name("adm1name");
	Assert.assertEquals("name, adm1name", generator.generateLabel(osm));

}
	
	/*-------------------------------labels-----------------------------------------*/
	
	@Test
	public void generatePostal_Address(){
		Address address = new Address();
		String actual =  generator.generatePostal(address);
		System.out.println(actual);
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA), actual);
	}
	
	@Test
	public void generatePostal_Address_shouldContainsCountryCode(){
		Address address= new Address();
		address.setStreetName("foo bar street");
		address.setHouseNumber("3");
		address.setCity("paris");
		address.setCitySubdivision("3e arrondissement");
		address.setAdm1Name("alabama");
		address.setAdm2Name("adm2");
		address.setAdm3Name("ADM2");
		address.setAdm4Name("adm4");
		address.setAdm5Name("adm5");
		address.setCountryCode("US");
		String actual =  generator.generatePostal(address);
		System.out.println(actual);
		Assert.assertEquals(1, countNumberOfOccurence(actual,"\\(AL\\)"));
		Assert.assertEquals(1, countNumberOfOccurence(actual,"alabama"));
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA), actual);
	}
	
	@Test
	public void generatePostal_Address_shouldNotContainsStreetType(){
		Address address= new Address();
		address.setStreetName("foo bar street");
		address.setStreetType("SERVICE");
		address.setHouseNumber("3");
		address.setCity("paris");
		address.setCitySubdivision("3e arrondissement");
		address.setAdm1Name("alabama");
		address.setAdm2Name("adm2");
		address.setAdm3Name("ADM2");
		address.setAdm4Name("adm4");
		address.setAdm5Name("adm5");
		address.setCountryCode("US");
		String actual =  generator.generatePostal(address);
		System.out.println(actual);
		Assert.assertEquals(0, countNumberOfOccurence(actual,"SERVICE"));
	}
	
	@Test
	public void generatePostal_Openstreetmap(){
		 OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		OpenStreetMap streetOSM2 = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
		String postal  =generator.generatePostal(streetOSM);
		Assert.assertEquals(formater.getEnvelopeAddress(generator.buildAddressFromOpenstreetMap(streetOSM), DisplayMode.COMMA), postal);
		postal  =generator.generatePostal(streetOSM2);
		Assert.assertEquals(formater.getEnvelopeAddress(generator.buildAddressFromOpenstreetMap(streetOSM2), DisplayMode.COMMA), postal);
	}
	
	@Test
	public void generateLabels_street() {
		List<AlternateOsmName> altnames = new ArrayList<AlternateOsmName>();
		altnames.add(new AlternateOsmName("altname", AlternateNameSource.OPENSTREETMAP));
		altnames.add(new AlternateOsmName("altname2", AlternateNameSource.OPENSTREETMAP));
		
		List<String> cities = new ArrayList<String>();
		cities.add("city1");
		cities.add("city2");

		Set<String> labels;
		OpenStreetMap street = new OpenStreetMap();
		
		//alt names and cities
			//name
			street = new OpenStreetMap();
			street.setName("name");
			street.setIsIn("isIn");
			street.addAlternateNames(altnames);
			street.addIsInCitiesAlternateNames(cities);
			labels = generator.generateLabels(street);
			Assert.assertEquals(9, labels.size());
			Assert.assertTrue(labels.contains("name, isIn"));
			Assert.assertTrue(labels.contains("name, city2"));
			Assert.assertTrue(labels.contains("name, city1"));
			Assert.assertTrue(labels.contains("altname, isIn"));
			Assert.assertTrue(labels.contains("altname, city2"));
			Assert.assertTrue(labels.contains("altname, city1"));
			Assert.assertTrue(labels.contains("altname2, isIn"));
			Assert.assertTrue(labels.contains("altname2, city1"));
			Assert.assertTrue(labels.contains("altname2, city2"));
			
			
			
			//noname
			street = new OpenStreetMap();
			street.addAlternateNames(altnames);
			street.addIsInCitiesAlternateNames(cities);
			labels = generator.generateLabels(street);
			Assert.assertEquals(4, labels.size());
			Assert.assertTrue(labels.contains("altname, city1"));
			Assert.assertTrue(labels.contains("altname, city2"));
			Assert.assertTrue(labels.contains("altname2, city1"));
			Assert.assertTrue(labels.contains("altname2, city2"));
			
			//duplicate name
			/*street = new OpenStreetMap();
			street.setName("name");
			street.setIsIn("isIn");
			street.addAlternateName(new AlternateOsmName("name",AlternateNameSource.OPENSTREETMAP));
			street.addIsInCitiesAlternateNames(cities);
			labels = generator.generateLabels(street);
			Assert.assertEquals(5, labels.size());
			Assert.assertTrue(labels.contains("name, isIn"));
			Assert.assertTrue(labels.contains("altname, city1"));
			Assert.assertTrue(labels.contains("altname, city2"));
			Assert.assertTrue(labels.contains("altname2, city1"));
			Assert.assertTrue(labels.contains("altname2, city2"));*/
		
		
		//alt names, and no cities
			//name
			street = new OpenStreetMap();
			street.setName("name");
			street.setIsIn("isIn");
			street.addAlternateNames(altnames);
			labels = generator.generateLabels(street);
			Assert.assertEquals(3, labels.size());
			Assert.assertTrue(labels.contains("name, isIn"));
			Assert.assertTrue(labels.contains("altname, isIn"));
			Assert.assertTrue(labels.contains("altname2, isIn"));
			
			
			//name but no city
			street = new OpenStreetMap();
			street.setName("name");
			street.addAlternateNames(altnames);
			labels = generator.generateLabels(street);
			Assert.assertTrue(labels.contains("name"));
			Assert.assertTrue(labels.contains("altname"));
			Assert.assertTrue(labels.contains("altname2"));
			Assert.assertEquals(3, labels.size());
			
			//noname
			street = new OpenStreetMap();
			street.addAlternateNames(altnames);
			labels = generator.generateLabels(street);
			Assert.assertEquals(2, labels.size());
			Assert.assertTrue(labels.contains("altname"));
			Assert.assertTrue(labels.contains("altname2"));
			
		
		
		//no alt names, no cities
			//name
			street = new OpenStreetMap();
			street.setName("name");
			street.setIsIn("isIn");
			labels = generator.generateLabels(street);
			Assert.assertEquals(1, labels.size());
			Assert.assertTrue(labels.contains("name, isIn"));
			
			street = new OpenStreetMap();
			street.setIsIn("isIn");
			labels = generator.generateLabels(street);
			Assert.assertEquals(0, labels.size());
			
			//noname
			street = new OpenStreetMap();
			labels = generator.generateLabels(street);
			Assert.assertEquals(0, labels.size());
		
		
		//no altnames, cities
			//name
			street = new OpenStreetMap();
			street.setName("name");
			street.setIsIn("isIn");
			street.addIsInCitiesAlternateNames(cities);
			labels = generator.generateLabels(street);
			Assert.assertEquals(3, labels.size());
			Assert.assertTrue(labels.contains("name, isIn"));
			Assert.assertTrue(labels.contains("name, city1"));
			Assert.assertTrue(labels.contains("name, city2"));
			
			//noname
			street = new OpenStreetMap();
			street.addIsInCitiesAlternateNames(cities);
			labels = generator.generateLabels(street);
			Assert.assertEquals(0, labels.size());
			
			//if is_in is null, we should use is_in_place
			street = new OpenStreetMap();
			street.setName("name");
			street.setIsIn(null);
			street.setIsInPlace("isInPlace");
			street.addAlternateNames(altnames);
			street.addIsInCitiesAlternateNames(cities);
			labels = generator.generateLabels(street);
			Assert.assertEquals(9, labels.size());
			Assert.assertTrue(labels.contains("name, isInPlace"));
		
	}
	
	@Test
	public void generateLabels_city() {
		City city = new City();
		city.setName("name");
		
		city.addAlternateName(new AlternateName("alternateName",AlternateNameSource.PERSONAL));
		city.addAlternateName(new AlternateName("alternateName2",AlternateNameSource.PERSONAL));
		
		Set<String> labels = generator.generateLabels(city);
		Assert.assertEquals(3, labels.size());
		Assert.assertTrue(labels.contains("name"));
		Assert.assertTrue(labels.contains("alternateName"));
		Assert.assertTrue(labels.contains("alternateName2"));
		
		//with duplicate
		city.setName("alternateName");
		 labels = generator.generateLabels(city);
		Assert.assertEquals(2, labels.size());
		Assert.assertTrue(labels.contains("alternateName"));
		Assert.assertTrue(labels.contains("alternateName2"));
	
	}
	
	@Test
	public void generateLabels_adm() {
		Adm adm = new Adm(3);
		adm.setName("name");
		
		adm.addAlternateName(new AlternateName("alternateName",AlternateNameSource.PERSONAL));
		adm.addAlternateName(new AlternateName("alternateName2",AlternateNameSource.PERSONAL));
		
		Set<String> labels = generator.generateLabels(adm);
		Assert.assertEquals(3, labels.size());
		Assert.assertTrue(labels.contains("name"));
		Assert.assertTrue(labels.contains("alternateName"));
		Assert.assertTrue(labels.contains("alternateName2"));
		
		//with duplicate
		adm.setName("alternateName");
		 labels = generator.generateLabels(adm);
		Assert.assertEquals(2, labels.size());
		Assert.assertTrue(labels.contains("alternateName"));
		Assert.assertTrue(labels.contains("alternateName2"));
	
	}
	
	@Test
	public void generateLabels_poisShouldCallGenerateLabelsForPois() {
		Restaurant restaurant = new Restaurant();
		restaurant.setFeatureId(456L);
		
		LabelGenerator stub  = new LabelGenerator(){
			
			
			@Override
			public Set<String> generateLabelsForPois(GisFeature poi) {
						Set<String> labels = new HashSet<String>();
						labels.add("toto");
						return labels;
			}
		};
		
		Set<String> labels = stub.generateLabels(restaurant);
		Assert.assertEquals(1, labels.size());
		Assert.assertTrue(labels.contains("toto"));
	
	}
	
	@Test
	public void generateLabelsForPois() {
		
			List<AlternateName> altnames = new ArrayList<AlternateName>();
			altnames.add(new AlternateName("altname", AlternateNameSource.OPENSTREETMAP));
			altnames.add(new AlternateName("altname2", AlternateNameSource.OPENSTREETMAP));
			
			List<String> cities = new ArrayList<String>();
			cities.add("city1");
			cities.add("city2");

			Set<String> labels;
			Restaurant street = new Restaurant();
			
			//alt names and cities
				//name
				street = new Restaurant();
				street.setName("name");
				street.setIsIn("isIn");
				street.addAlternateNames(altnames);
				street.addIsInCitiesAlternateNames(cities);
				labels = generator.generateLabels(street);
				Assert.assertEquals(9, labels.size());
				Assert.assertTrue(labels.contains("name, isIn"));
				Assert.assertTrue(labels.contains("name, city2"));
				Assert.assertTrue(labels.contains("name, city1"));
				Assert.assertTrue(labels.contains("altname, isIn"));
				Assert.assertTrue(labels.contains("altname, city2"));
				Assert.assertTrue(labels.contains("altname, city1"));
				Assert.assertTrue(labels.contains("altname2, isIn"));
				Assert.assertTrue(labels.contains("altname2, city1"));
				Assert.assertTrue(labels.contains("altname2, city2"));
				
				//noname
				street = new Restaurant();
				street.addAlternateNames(altnames);
				street.addIsInCitiesAlternateNames(cities);
				labels = generator.generateLabels(street);
				Assert.assertEquals(4, labels.size());
				Assert.assertTrue(labels.contains("altname, city1"));
				Assert.assertTrue(labels.contains("altname, city2"));
				Assert.assertTrue(labels.contains("altname2, city1"));
				Assert.assertTrue(labels.contains("altname2, city2"));
				
				//duplicate name
				/*street = new OpenStreetMap();
				street.setName("name");
				street.setIsIn("isIn");
				street.addAlternateName(new AlternateOsmName("name",AlternateNameSource.OPENSTREETMAP));
				street.addIsInCitiesAlternateNames(cities);
				labels = generator.generateLabels(street);
				Assert.assertEquals(5, labels.size());
				Assert.assertTrue(labels.contains("name, isIn"));
				Assert.assertTrue(labels.contains("altname, city1"));
				Assert.assertTrue(labels.contains("altname, city2"));
				Assert.assertTrue(labels.contains("altname2, city1"));
				Assert.assertTrue(labels.contains("altname2, city2"));*/
			
			
			//alt names, and no cities
				//name
				street = new Restaurant();
				street.setName("name");
				street.setIsIn("isIn");
				street.addAlternateNames(altnames);
				labels = generator.generateLabels(street);
				Assert.assertEquals(3, labels.size());
				Assert.assertTrue(labels.contains("name, isIn"));
				Assert.assertTrue(labels.contains("altname, isIn"));
				Assert.assertTrue(labels.contains("altname2, isIn"));
				
				
				//name but no city
				street = new Restaurant();
				street.setName("name");
				street.addAlternateNames(altnames);
				labels = generator.generateLabels(street);
				Assert.assertTrue(labels.contains("name"));
				Assert.assertTrue(labels.contains("altname"));
				Assert.assertTrue(labels.contains("altname2"));
				Assert.assertEquals(3, labels.size());
				
				//noname
				street = new Restaurant();
				street.addAlternateNames(altnames);
				labels = generator.generateLabels(street);
				Assert.assertEquals(2, labels.size());
				Assert.assertTrue(labels.contains("altname"));
				Assert.assertTrue(labels.contains("altname2"));
				
			
			
			//no alt names, no cities
				//name
				street = new Restaurant();
				street.setName("name");
				street.setIsIn("isIn");
				labels = generator.generateLabels(street);
				Assert.assertEquals(1, labels.size());
				Assert.assertTrue(labels.contains("name, isIn"));
				
				street = new Restaurant();
				street.setIsIn("isIn");
				labels = generator.generateLabels(street);
				Assert.assertEquals(0, labels.size());
				
				//noname
				street = new Restaurant();
				labels = generator.generateLabels(street);
				Assert.assertEquals(0, labels.size());
			
			
			//no altnames, cities
				//name
				street = new Restaurant();
				street.setName("name");
				street.setIsIn("isIn");
				street.addIsInCitiesAlternateNames(cities);
				labels = generator.generateLabels(street);
				Assert.assertEquals(3, labels.size());
				Assert.assertTrue(labels.contains("name, isIn"));
				Assert.assertTrue(labels.contains("name, city1"));
				Assert.assertTrue(labels.contains("name, city2"));
				
				//noname
				street = new Restaurant();
				street.addIsInCitiesAlternateNames(cities);
				labels = generator.generateLabels(street);
				Assert.assertEquals(0, labels.size());
			
		
	
	}
	
	public static int countNumberOfOccurence(String text,String word){
		int i = 0;
		Pattern p = Pattern.compile(word,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher( text );
		while (m.find()) {
		    i++;
		}
		return i++;
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldContainsZipCodeIfCityHasOneZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertTrue(fullyQualifiedName.contains(city.getZipCodes().iterator().next().getCode()));
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainsadm5LevelAndNameAreTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setName("aname");
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm1Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("aname");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"aname"));
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainIsInLevelAndNameAreTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setName("aname");
		city.setIsIn("aname");
		city.setAdm2Name("adm1Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("aname");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"aname"));
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainsadm1LevelIfThePreviousIsTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm1Name"));
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainsadm2LevelIfThePreviousIsTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm2Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
	}
	
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainsadm3LevelIfThePreviousIsTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm3Name");
		city.setAdm5Name("adm5Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm3Name"));
	}
	
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainsadm4LevelIfThePreviousIsTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm4Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm4Name"));
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainsAllThePreviousIsTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm2Name");
		city.setAdm4Name("adm2Name");
		city.setAdm5Name("adm4Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldhaveStateCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);
		city.setCountryCode("us");

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("alabama");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm2Name");
		city.setAdm4Name("adm2Name");
		city.setAdm5Name("adm4Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"alabama"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"\\(AL\\)"));
	}
	
	@Test
	public void testGetFullyQualifiedNameFeatureShouldNotContainsAllThePreviousIsTheSame_CaseInsensitive() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm2name");
		city.setAdm4Name("adm2Name");
		city.setAdm5Name("adm4Name");
		//Note that city has already a zipcode
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		System.out.println(fullyQualifiedName);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
	}
	
	
	

	@Test
	public void testGetFullyQualifiedNameFeatureShouldContainsbestZipCodeIfCityHasMoreThanOneZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		Set<ZipCode> zipcodes = new HashSet<ZipCode>();
		zipcodes.add(new ZipCode("95000"));
		zipcodes.add(new ZipCode("96000"));
		city.setZipCodes(zipcodes);
		String bestZip = generator.getBestZip(zipcodes);
		String fullyQualifiedName = generator.getFullyQualifiedName(city,false);
		Assert.assertTrue(fullyQualifiedName+" doesn't contains best zip : "+bestZip,fullyQualifiedName.contains(bestZip));

	}

	@Test
	public void testGetFullyQualifiedNameFeatureWhenNoZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);
		generator.getFullyQualifiedName(city,false);
	}
	
	@Test
	public void testPopulateShouldAddZipCodesForTheCurrentFeatures(){
		City city = GisgraphyTestHelper.createCity("city", 3.0F, 4.0F, 1L);
		City cityToBePopulated = new City();
		cityToBePopulated.setFeatureId(1234567L);
		cityToBePopulated.populate(city);
		Assert.assertEquals("when populate is called, the name of the cities should be equals, maybe super is not called",city.getName(), cityToBePopulated.getName());
		Assert.assertEquals("when populate is called, the zipcodes should be added",city.getZipCodes().size(),cityToBePopulated.getZipCodes().size());
		Assert.assertEquals("when populate is called, the zipcodes should be associated to The features populated, not the 'arg one'",cityToBePopulated.getFeatureId(),cityToBePopulated.getZipCodes().iterator().next().getGisFeature().getFeatureId());
	}
	
	@Test
	public void getBestZip(){
		Set<ZipCode> zips = new HashSet<ZipCode>();
		Assert.assertEquals(null, generator.getBestZip(zips));
		
		zips = new HashSet<ZipCode>();
		zips.add(new ZipCode("75002"));
		Assert.assertEquals("75002", generator.getBestZip(zips));
		
		zips = new HashSet<ZipCode>();
		zips.add(new ZipCode("75002"));
		zips.add(new ZipCode("75000"));
		zips.add(new ZipCode("75001"));
		zips.add(new ZipCode("75230 CEDEX 05"));
		
		Assert.assertEquals("75000", generator.getBestZip(zips));
		
		zips = new HashSet<ZipCode>();
		zips.add(new ZipCode("AA"));
		zips.add(new ZipCode("B"));
		
		Assert.assertEquals("AA", generator.getBestZip(zips));
		zips = new HashSet<ZipCode>();
		zips.add(new ZipCode("AD500"));
		zips.add(new ZipCode("AD501"));
		
		Assert.assertEquals("AD500", generator.getBestZip(zips));
	}
	
	@Test
	public void getBestZipString(){
		Set<String> zips = new HashSet<String>();
		Assert.assertEquals(null, generator.getBestZipString(zips));
		
		zips = new HashSet<String>();
		zips.add("75002");
		Assert.assertEquals("75002", generator.getBestZipString(zips));
		
		zips = new HashSet<String>();
		zips.add("75002");
		zips.add("75000");
		zips.add("75001");
		zips.add("75230 CEDEX 05");
		
		Assert.assertEquals("75000", generator.getBestZipString(zips));
		
		zips = new HashSet<String>();
		zips.add("AA");
		zips.add("B");
		
		Assert.assertEquals("AA", generator.getBestZipString(zips));
		zips = new HashSet<String>();
		zips.add("AD500");
		zips.add("AD501");
		
		Assert.assertEquals("AD500", generator.getBestZipString(zips));
	}
	


	    @Test
	    public void testGetFullyQualifiedNameGisFeatureBoolean_noValues() {
		GisFeature gisFeature = new GisFeature();
		String label = generator.getFullyQualifiedName(gisFeature, true);
		System.out.println(label);
		Assert.assertNull(label);
	    }
	    
	    @Test
	    public void testGetFullyQualifiedNameGisFeatureBoolean_noName() {
		GisFeature gisFeature = createGisFeatureMock(null);
		EasyMock.replay(gisFeature);
		String label = generator.getFullyQualifiedName(gisFeature, true);
		System.out.println(label);
		Assert.assertFalse(label.startsWith(","));
	    }
	    
	    @Test
	    public void testGetFullyQualifiedNameOsm_noName() {
		OpenStreetMap osm = createOsmMock(null);
		EasyMock.expect(osm.getZipCode()).andStubReturn("zip");
		EasyMock.replay(osm);
		String label = generator.getFullyQualifiedName(osm, true);
		System.out.println(label);
		Assert.assertNull(label);
		}

	    private GisFeature createGisFeatureMock(String name) {
		GisFeature gisFeature = EasyMock.createMock(GisFeature.class);
		EasyMock.expect(gisFeature.getAdm1Name()).andStubReturn("adm1name");
		EasyMock.expect(gisFeature.getIsInPlace()).andStubReturn("IsInPlace");
		EasyMock.expect(gisFeature.getIsIn()).andStubReturn("IsIn");
		EasyMock.expect(gisFeature.getAdm2Name()).andStubReturn("adm2name");
		EasyMock.expect(gisFeature.getAdm3Name()).andStubReturn("adm3name");
		EasyMock.expect(gisFeature.getAdm4Name()).andStubReturn("adm4name");
		EasyMock.expect(gisFeature.getAdm5Name()).andStubReturn("adm5name");
		EasyMock.expect(gisFeature.getCountryCode()).andStubReturn("US");
		EasyMock.expect(gisFeature.getName()).andStubReturn(name);
		
		return gisFeature;
	    }
	   
	    private OpenStreetMap createOsmMock(String name) {
			OpenStreetMap osm = EasyMock.createMock(OpenStreetMap.class);
			EasyMock.expect(osm.getAdm1Name()).andStubReturn("adm1name");
			EasyMock.expect(osm.getIsInPlace()).andStubReturn("IsInPlace");
			EasyMock.expect(osm.getIsIn()).andStubReturn("IsIn");
			//EasyMock.expect(osm.getZipCode()).andStubReturn("zip");
			EasyMock.expect(osm.getAdm2Name()).andStubReturn("adm2name");
			EasyMock.expect(osm.getAdm3Name()).andStubReturn("adm3name");
			EasyMock.expect(osm.getAdm4Name()).andStubReturn("adm4name");
			EasyMock.expect(osm.getAdm5Name()).andStubReturn("adm5name");
			EasyMock.expect(osm.getCountryCode()).andStubReturn("us");
			EasyMock.expect(osm.getName()).andStubReturn(name);
			
			return osm;
		    }
	    

	    @Test
	    public void testGetFullyQualifiedNameGisFeature() {
		GisFeature gisFeature = createGisFeatureMock("name");
		EasyMock.replay(gisFeature);
		String fullyQualifiedName = generator.getFullyQualifiedName(gisFeature);
		System.out.println(fullyQualifiedName);
		
		EasyMock.verify(gisFeature);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm3Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm4Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm5Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm1Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"^name"));
		
	    }
	    
	    @Test
	    public void testGetFullyQualifiedNameOsm_oneZip() {
		OpenStreetMap osm = createOsmMock("name");
		EasyMock.expect(osm.getZipCode()).andStubReturn("zip");
		EasyMock.replay(osm);
		String fullyQualifiedName = generator.getFullyQualifiedName(osm, false);
		System.out.println(fullyQualifiedName);
		
		EasyMock.verify(osm);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm3Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm4Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm5Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm1Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"^name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"zip"));
		
	    }
	    
	    @Test
	    public void testGetFullyQualifiedNameOsm_severalZip() {
		OpenStreetMap osm = createOsmMock("name");
		EasyMock.expect(osm.getZipCode()).andStubReturn(null);
		Set<String> zips = new HashSet<String>();
		zips.add("123");
		zips.add("456");
		EasyMock.expect(osm.getIsInZip()).andStubReturn(zips);
		EasyMock.replay(osm);
		String fullyQualifiedName = generator.getFullyQualifiedName(osm, false);
		System.out.println(fullyQualifiedName);
		
		EasyMock.verify(osm);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm3Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm4Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm5Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm1Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"^name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"123"));
		
	    }
	    
	    @Test
	    public void testGetFullyQualifiedNameOsm_noZip() {
		OpenStreetMap osm = createOsmMock("name");
		EasyMock.expect(osm.getZipCode()).andStubReturn(null);
		EasyMock.expect(osm.getIsInZip()).andStubReturn(null);
		EasyMock.replay(osm);
		String fullyQualifiedName = generator.getFullyQualifiedName(osm, false);
		System.out.println(fullyQualifiedName);
		
		EasyMock.verify(osm);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm3Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm4Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm5Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm1Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"^name"));
		
	    }
	    
	    @Test
	    public void testGetFullyQualifiedNameOsmShouldContainsStateCode() {
		OpenStreetMap osm = EasyMock.createMock(OpenStreetMap.class);
		EasyMock.expect(osm.getAdm1Name()).andStubReturn("alabama");
		EasyMock.expect(osm.getIsInPlace()).andStubReturn("IsInPlace");
		EasyMock.expect(osm.getIsIn()).andStubReturn("IsIn");
		EasyMock.expect(osm.getAdm2Name()).andStubReturn("adm2name");
		EasyMock.expect(osm.getAdm3Name()).andStubReturn("adm3name");
		EasyMock.expect(osm.getAdm4Name()).andStubReturn("adm4name");
		EasyMock.expect(osm.getAdm5Name()).andStubReturn("adm5name");
		EasyMock.expect(osm.getName()).andStubReturn("name");
		EasyMock.expect(osm.getCountryCode()).andStubReturn("us");
		EasyMock.expect(osm.getZipCode()).andStubReturn(null);
		EasyMock.expect(osm.getIsInZip()).andStubReturn(null);
		EasyMock.replay(osm);
		String fullyQualifiedName = generator.getFullyQualifiedName(osm, false);
		System.out.println(fullyQualifiedName);
		
		EasyMock.verify(osm);
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm3Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm4Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm5Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"adm2Name"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"alabama"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"\\(AL\\)"));
		Assert.assertEquals(1, countNumberOfOccurence(fullyQualifiedName,"^name"));
		
	    }

	    @Test
	    public void testGetCountry() {
	    	Assert.assertEquals("France", generator.getCountry("fr"));
	    	Assert.assertEquals("France", generator.getCountry("FR"));
	    	Assert.assertEquals(null, generator.getCountry(""));
	    	Assert.assertEquals(null, generator.getCountry("XX"));
	    	Assert.assertEquals(null, generator.getCountry(null));
	    }
	    
	    @Test
		public void getFullyQualifiedNameAddressShouldNotContainsDuplicateAdmNames(){
			Address address= new Address();
			address.setStreetName("foo bar street");
			address.setHouseNumber("3");
			address.setCity("paris");
			address.setCitySubdivision("3e arrondissement");
			address.setAdm1Name("adm1");
			address.setAdm2Name("adm2");
			address.setAdm3Name("ADM2");
			address.setAdm4Name("adm4");
			address.setAdm5Name("adm5");
			address.setCountryCode("FR");
			String actual = generator.getFullyQualifiedName(address);
			System.out.println(actual);
			Assert.assertEquals(1, countNumberOfOccurence(actual,"adm2"));
		}
	  
	    
	    @Test
		public void getFullyQualifiedNameAddressShouldContainsStateCode(){
			Address address= new Address();
			address.setStreetName("foo bar street");
			address.setHouseNumber("3");
			address.setCity("paris");
			address.setCitySubdivision("3e arrondissement");
			address.setAdm1Name("alabama");
			address.setAdm2Name("adm2");
			address.setAdm3Name("ADM2");
			address.setAdm4Name("adm4");
			address.setAdm5Name("adm5");
			address.setCountryCode("US");
			String actual = generator.getFullyQualifiedName(address);
			System.out.println(actual);
			Assert.assertEquals(1, countNumberOfOccurence(actual,"\\(AL\\)"));
			Assert.assertEquals(1, countNumberOfOccurence(actual,"alabama"));
		}
		
		@Test
		public void getFullyQualifiedNameAddress(){
			Address address= new Address();
			address.setStreetName("foo bar street");
			address.setHouseNumber("3");
			address.setCity("paris");
			address.setCitySubdivision("3e arrondissement");
			address.setAdm1Name("adm1");
			address.setAdm2Name("adm2");
			address.setAdm3Name("adm3");
			address.setAdm4Name("adm4");
			address.setAdm5Name("adm5");
			address.setCountryCode("FR");
			String actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, 3e arrondissement, paris, adm5, adm4, adm3, adm2, adm1, France", actual);

			//with only state fill
			address.setAdm1Name(null);
			address.setAdm2Name(null);
			address.setAdm3Name(null);
			address.setAdm4Name(null);
			address.setAdm5Name(null);
			address.setState("state");
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, 3e arrondissement, paris, state, France", actual);

			//without any state info
			address.setAdm1Name(null);
			address.setAdm2Name(null);
			address.setAdm3Name(null);
			address.setAdm4Name(null);
			address.setAdm5Name(null);
			address.setState(null);
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, 3e arrondissement, paris, France", actual);

			//without any state info and city subdivision
			address.setAdm1Name(null);
			address.setAdm2Name(null);
			address.setAdm3Name(null);
			address.setAdm4Name(null);
			address.setAdm5Name(null);
			address.setState(null);
			address.setCitySubdivision(null);
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, paris, France", actual);
			//withunknow country
			address.setAdm1Name(null);
			address.setAdm2Name(null);
			address.setAdm3Name(null);
			address.setAdm4Name(null);
			address.setAdm5Name(null);
			address.setState(null);
			address.setCountryCode("XX");;

			address.setCitySubdivision(null);
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, paris", actual);
			
			//with one state and some admin name
			address.setAdm1Name(null);
			address.setAdm2Name("adm2");
			address.setAdm3Name(null);
			address.setAdm4Name("adm4");
			address.setAdm5Name(null);
			address.setCountryCode("FR");
			address.setState("state");
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, paris, adm4, adm2, France", actual);
			
			//with duplicate adm name
			address.setAdm1Name(null);
			address.setAdm2Name("adm2");
			address.setAdm3Name(null);
			address.setAdm4Name("adm2");
			address.setAdm5Name(null);
			address.setCountryCode("FR");
			address.setState("state");
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, paris, adm2, France", actual);
			
			//test city subdivisions
			address.setDependentLocality("dependentLocality");
			address.setDistrict("district");
			address.setQuarter("quarter");
			address.setAdm1Name(null);
			address.setAdm2Name("adm2");
			address.setAdm3Name(null);
			address.setAdm4Name("adm2");
			address.setAdm5Name(null);
			address.setCountryCode("FR");
			address.setState("state");
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3 foo bar street, dependentLocality, district, quarter, paris, adm2, France", actual);

		}
		
		/*------------------------------------------addreshelper--------------------*/

		
	

	@Test
	public void getNearestHouse_WrongParameter() {
		Assert.assertNull(generator.getNearestHouse(new TreeSet<HouseNumber>(), null));
		Assert.assertNull(generator.getNearestHouse(null, GeolocHelper.createPoint(3D, 4D)));
		Assert.assertNull(generator.getNearestHouse(new TreeSet<HouseNumber>(), GeolocHelper.createPoint(3D, 4D)));
	}
	
	@Test
	public void getNearestHouse_OneHouse() {
		TreeSet<HouseNumber> houses = new TreeSet<HouseNumber>();
		Point houseLocation = GeolocHelper.createPoint(3D, 4D);
		HouseNumber house = new HouseNumber("1",houseLocation);
		houses.add(house);
		Point searchPoint = GeolocHelper.createPoint(6D, 7D);
		HouseNumberDistance nearestHouse = generator.getNearestHouse(houses, searchPoint);
		Assert.assertEquals(new HouseNumberDistance(house, GeolocHelper.distance(searchPoint, houseLocation)),nearestHouse);
	}
	
	@Test
	public void getNearestHouse_SeveralHouse() {
		TreeSet<HouseNumber> houses = new TreeSet<HouseNumber>();
		Point houseLocation = GeolocHelper.createPoint(4D, 5D);
		HouseNumber house_far = new HouseNumber("far",houseLocation);
		
		Point houseLocation2 = GeolocHelper.createPoint(3.1D, 4.1D);
		HouseNumber house2_near = new HouseNumber("near",houseLocation2);
		
		houses.add(house_far);
		houses.add(house2_near);
		
		Point searchPoint = GeolocHelper.createPoint(3D, 4D);
		HouseNumberDistance nearestHouse = generator.getNearestHouse(houses, searchPoint);
		Assert.assertEquals(new HouseNumberDistance(house2_near, GeolocHelper.distance(searchPoint, houseLocation2)),nearestHouse);
	}
	
	@Test
	public void buildAddressFromOpenstreetMap_NullOpenstreetmap(){
		Assert.assertNull(generator.buildAddressFromOpenstreetMap(null));
	}
	
	
	
	@Test
	public void buildAddressFromOpenstreetMap(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		osm.setAdm1Name("adm1Name");
		osm.setAdm2Name("adm2Name");
		osm.setAdm3Name("adm3Name");
		osm.setAdm4Name("adm4Name");
		osm.setAdm5Name("adm5Name");
		osm.setZipCode("zipCodeSet");
		osm.setCountryCode("US");
		Address address = generator.buildAddressFromOpenstreetMap(osm);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals(osm.getAdm1Name(), address.getAdm1Name());
		Assert.assertEquals(osm.getAdm2Name(), address.getAdm2Name());
		Assert.assertEquals(osm.getAdm3Name(), address.getAdm3Name());
		Assert.assertEquals(osm.getAdm4Name(), address.getAdm4Name());
		Assert.assertEquals(osm.getAdm5Name(), address.getAdm5Name());
		Assert.assertEquals("When there is a zipcode, we take it","zipCodeSet", address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertNotNull(address.getFormatedPostal());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA), address.getFormatedPostal());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());
		
		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());
		
		Assert.assertEquals(osm.isOneWay(), address.isOneWay());
		Assert.assertEquals("streettype in osm (service,...) is not the streettype in an address",null, address.getStreetType());
		Assert.assertEquals(osm.getLength(), address.getLength());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());
		
	}
	
	@Test
	public void buildAddressFromOpenstreetMap_severalZip(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		osm.setAdm1Name("adm1Name");
		osm.setAdm2Name("adm2Name");
		osm.setAdm3Name("adm3Name");
		osm.setAdm4Name("adm4Name");
		osm.setAdm5Name("adm5Name");
		osm.setCountryCode("US");
		osm.setZipCode(null);
		Assert.assertTrue("the zipcodes should be filled for this set, please fix the dataset",osm.getIsInZip().size()>0);
		Address address = generator.buildAddressFromOpenstreetMap(osm);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals(osm.getAdm1Name(), address.getAdm1Name());
		Assert.assertEquals(osm.getAdm2Name(), address.getAdm2Name());
		Assert.assertEquals(osm.getAdm3Name(), address.getAdm3Name());
		Assert.assertEquals(osm.getAdm4Name(), address.getAdm4Name());
		Assert.assertEquals(osm.getAdm5Name(), address.getAdm5Name());
		Assert.assertEquals("When there is more than one zipcode, and zip is null, we take the best one",generator.getBestZipString(osm.getIsInZip()), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertNotNull(address.getFormatedPostal());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA), address.getFormatedPostal());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());
		
		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());
		
		Assert.assertEquals(osm.isOneWay(), address.isOneWay());
		Assert.assertEquals("streettype in osm (service,...) is not the streettype in an address",null, address.getStreetType());
		Assert.assertEquals(osm.getLength(), address.getLength());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());
		
	}
	
	@Test
	public void buildAddressFromOpenstreetMapAndPoint(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		Point point = GeolocHelper.createPoint(2D, 3D);
		Address address = generator.buildAddressFromOpenstreetMapAndPoint(osm,point);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals("when zipcode is set, we don't take bestzip of is_in",osm.getZipCode(), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(osm.getLatitude(), address.getLat());
		Assert.assertEquals(osm.getLongitude(), address.getLng());
		Assert.assertEquals(GeocodingLevels.STREET, address.getGeocodingLevel());
		
		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());
		
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());
		
		//
		Assert.assertEquals(GeolocHelper.distance(point, osm.getLocation()), address.getDistance().doubleValue(),0.01);
		
		
		
	}
	
	@Test
	public void buildAddressFromHousenumberDistance(){
		OpenStreetMap osm = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
		String number = "2";
		HouseNumber houseNumber = new HouseNumber(number,GeolocHelper.createPoint(3D, 4D));
		String name = "houseName";
		houseNumber.setName(name);
		osm.addHouseNumber(houseNumber);
		osm.setZipCode(null);
		Double distance =55D;
		HouseNumberDistance houseNumberDistance = new HouseNumberDistance(houseNumber, distance );
		Address address = generator.buildAddressFromHouseNumberDistance(houseNumberDistance);
		Assert.assertEquals(osm.getName(), address.getStreetName());
		Assert.assertEquals(osm.getIsIn(), address.getCity());
		Assert.assertEquals(osm.getIsInPlace(), address.getCitySubdivision());
		Assert.assertEquals(osm.getIsInAdm(), address.getState());
		Assert.assertEquals("when zip is null, we take the best one",generator.getBestZipString(osm.getIsInZip()), address.getZipCode());
		Assert.assertEquals(osm.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(houseNumber.getLatitude(),address.getLat());
		Assert.assertEquals(houseNumber.getLongitude(), address.getLng());
		Assert.assertEquals(GeocodingLevels.HOUSE_NUMBER, address.getGeocodingLevel());
		Assert.assertTrue(address.getFormatedFull().contains("2 "));
		
		//
		Assert.assertEquals(distance, address.getDistance().doubleValue(),0.01);
		Assert.assertEquals(name, address.getName());
		Assert.assertEquals(number, address.getHouseNumber());
		
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());
		Assert.assertTrue(address.getFormatedFull().contains(number));
		Assert.assertTrue(address.getFormatedPostal().contains(number));
		
		Assert.assertEquals(osm.isToll(), address.isToll());
		Assert.assertEquals(osm.getSurface(), address.getSurface());
		Assert.assertEquals(osm.getLanes(), address.getLanes());
		Assert.assertEquals(osm.getSpeedMode()+"", address.getSpeedMode());
		Assert.assertEquals(osm.getAzimuthStart(), address.getAzimuthStart());
		Assert.assertEquals(osm.getAzimuthEnd(), address.getAzimuthEnd());
		Assert.assertEquals(osm.getMaxSpeed(), address.getMaxSpeed());
		Assert.assertEquals(osm.getMaxSpeedBackward(), address.getMaxSpeedBackward());
		
	}
	
	@Test
	public void buildAddressFromcity(){
		City city = new City();
		city.setName("name");
		city.setIsInAdm("isInAdm");
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		city.setIsInAdm("isInAdm");
		Set<ZipCode> zipcodes = new HashSet<ZipCode>();
		zipcodes.add(new ZipCode("zip"));
		city.setZipCodes(zipcodes);
		city.setLocation(GeolocHelper.createPoint(2D, 3D));
		city.setCountryCode("countryCode");
		
		
		Address address = generator.buildAddressFromcity(city);
		Assert.assertEquals(city.getName(), address.getCity());
		Assert.assertEquals(city.getIsInAdm(), address.getState());
		Assert.assertEquals(city.getAdm1Name(), address.getAdm1Name());
		Assert.assertEquals(city.getAdm2Name(), address.getAdm2Name());
		Assert.assertEquals(city.getAdm3Name(), address.getAdm3Name());
		Assert.assertEquals(city.getAdm4Name(), address.getAdm4Name());
		Assert.assertEquals(city.getAdm5Name(), address.getAdm5Name());
		Assert.assertEquals(city.getZipCodes().iterator().next().toString(), address.getZipCode());
		Assert.assertEquals(city.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(GeocodingLevels.CITY, address.getGeocodingLevel());
		Assert.assertEquals(city.getLatitude(), address.getLat());
		Assert.assertEquals(city.getLongitude(), address.getLng());
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());
		
	}
	
	@Test
	public void buildAddressFromCityAndPoint(){
		City city = new City();
		city.setName("name");
		city.setIsInAdm("isInAdm");
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm2Name");
		city.setAdm3Name("adm3Name");
		city.setAdm4Name("adm4Name");
		city.setAdm5Name("adm5Name");
		city.setIsInAdm("isInAdm");
		Set<ZipCode> zipcodes = new HashSet<ZipCode>();
		zipcodes.add(new ZipCode("zip"));
		city.setZipCodes(zipcodes);
		city.setLocation(GeolocHelper.createPoint(2D, 3D));
		city.setCountryCode("countryCode");
		Point point = GeolocHelper.createPoint(5D, 4D);
		
		
		Address address = generator.buildAddressFromCityAndPoint(city,point);
		Assert.assertEquals(city.getName(), address.getCity());
		Assert.assertEquals(city.getIsInAdm(), address.getState());
		Assert.assertEquals(city.getAdm1Name(), address.getAdm1Name());
		Assert.assertEquals(city.getAdm2Name(), address.getAdm2Name());
		Assert.assertEquals(city.getAdm3Name(), address.getAdm3Name());
		Assert.assertEquals(city.getAdm4Name(), address.getAdm4Name());
		Assert.assertEquals(city.getAdm5Name(), address.getAdm5Name());
		Assert.assertEquals(city.getZipCodes().iterator().next().toString(), address.getZipCode());
		Assert.assertEquals(city.getCountryCode(), address.getCountryCode());
		Assert.assertEquals(GeocodingLevels.CITY, address.getGeocodingLevel());
		Assert.assertEquals(city.getLatitude(), address.getLat());
		Assert.assertEquals(city.getLongitude(), address.getLng());
		Assert.assertEquals(generator.getFullyQualifiedName(address), address.getFormatedFull());
		Assert.assertEquals(formater.getEnvelopeAddress(address, DisplayMode.COMMA),address.getFormatedPostal());
		Assert.assertEquals(GeolocHelper.distance(point, city.getLocation()), address.getDistance().doubleValue(),0.0001);
	}
	

	  

}
