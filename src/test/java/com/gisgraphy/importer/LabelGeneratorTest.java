package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Restaurant;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.test.GisgraphyTestHelper;

public class LabelGeneratorTest {

	LabelGenerator generator = LabelGenerator.getInstance();
	
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
	public void testGetFullyQualifiedNameFeatureShouldNotContainsadm1LevelIfThePreviousIsTheSame() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		zipcodes.add(new ZipCode("code"));
		
		city.setAdm1Name("adm1Name");
		city.setAdm2Name("adm1Name");
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

	    private GisFeature createGisFeatureMock() {
		GisFeature gisFeature = EasyMock.createMock(GisFeature.class);
		EasyMock.expect(gisFeature.getAdm1Name()).andReturn("adm1name").times(1);
		EasyMock.expect(gisFeature.getIsInPlace()).andReturn("IsInPlace").times(1);
		EasyMock.expect(gisFeature.getIsIn()).andReturn("IsIn").times(1);
		EasyMock.expect(gisFeature.getAdm2Name()).andReturn("adm2name").times(1);
		EasyMock.expect(gisFeature.getAdm3Name()).andReturn("adm3name").times(1);
		EasyMock.expect(gisFeature.getAdm4Name()).andReturn("adm4name").times(1);
		EasyMock.expect(gisFeature.getAdm5Name()).andReturn("adm5name").times(1);
		EasyMock.expect(gisFeature.getName()).andReturn("name").times(2);
		
		return gisFeature;
	    }

	    @Test
	    public void testGetFullyQualifiedNameGisFeature() {
		GisFeature gisFeature = createGisFeatureMock();
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
			Assert.assertEquals("3, foo bar street, 3e arrondissement, paris, adm5, adm4, adm3, adm2, adm1, France", actual);

			//with only state fill
			address.setAdm1Name(null);
			address.setAdm2Name(null);
			address.setAdm3Name(null);
			address.setAdm4Name(null);
			address.setAdm5Name(null);
			address.setState("state");
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3, foo bar street, 3e arrondissement, paris, state, France", actual);

			//without any state info
			address.setAdm1Name(null);
			address.setAdm2Name(null);
			address.setAdm3Name(null);
			address.setAdm4Name(null);
			address.setAdm5Name(null);
			address.setState(null);
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3, foo bar street, 3e arrondissement, paris, France", actual);

			//without any state info and city subdivision
			address.setAdm1Name(null);
			address.setAdm2Name(null);
			address.setAdm3Name(null);
			address.setAdm4Name(null);
			address.setAdm5Name(null);
			address.setState(null);
			address.setCitySubdivision(null);
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3, foo bar street, paris, France", actual);
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
			Assert.assertEquals("3, foo bar street, paris", actual);
			
			//with one state and some admin name
			address.setAdm1Name(null);
			address.setAdm2Name("adm2");
			address.setAdm3Name(null);
			address.setAdm4Name("adm4");
			address.setAdm5Name(null);
			address.setCountryCode("FR");
			address.setState("state");
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3, foo bar street, paris, adm4, adm2, France", actual);
			
			//with duplicate adm name
			address.setAdm1Name(null);
			address.setAdm2Name("adm2");
			address.setAdm3Name(null);
			address.setAdm4Name("adm2");
			address.setAdm5Name(null);
			address.setCountryCode("FR");
			address.setState("state");
			actual = generator.getFullyQualifiedName(address);
			Assert.assertEquals("3, foo bar street, paris, adm2, France", actual);
			
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
			Assert.assertEquals("3, foo bar street, dependentLocality, district, quarter, paris, adm2, France", actual);

		}

	  

}
