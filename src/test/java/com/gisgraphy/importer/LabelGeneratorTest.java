package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
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
		
	}
	
	
	@Test
	public void testGetFullyQualifiedNameShouldContainsZipCodeIfCityHasOneZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);

		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		//Note that city has already a zipcode
		Assert.assertTrue(generator.getFullyQualifiedName(city,false).contains(city.getZipCodes().iterator().next().getCode()));
	}

	@Test
	public void testGetFullyQualifiedNameShouldNotContainsZipCodeIfCityHasMoreThanOneZipCode() {
		City city = GisgraphyTestHelper.createCity("Paris", 1F, 2F, 3L);
		String zipcode = "95000";
		List<ZipCode> zipcodes = new ArrayList<ZipCode>();
		//Note that city has already a zipcode
		zipcodes.add(new ZipCode(zipcode));
		Assert.assertFalse(generator.getFullyQualifiedName(city,false).contains("95000"));
		Assert.assertFalse(generator.getFullyQualifiedName(city,false).contains("96000"));

	}

	@Test
	public void testGetFullyQualifiedNameWhenNoZipCode() {
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
	    public void testGetFullyQualifiedNameGisFeatureBoolean() {
		GisFeature gisFeature = new GisFeature();
		generator.getFullyQualifiedName(gisFeature, true);
	    }

	    private GisFeature createGisFeatureMock() {
		GisFeature gisFeature = EasyMock.createMock(GisFeature.class);
		EasyMock.expect(gisFeature.getAdm1Name()).andReturn("adm1name");
		EasyMock.expect(gisFeature.getAdm2Name()).andReturn("adm2name");
		EasyMock.expect(gisFeature.getName()).andReturn("name");
		
		return gisFeature;
	    }

	    @Test
	    public void testGetFullyQualifiedNameGisFeature() {
		GisFeature gisFeature = createGisFeatureMock();
		EasyMock.replay(gisFeature);
		generator.getFullyQualifiedName(gisFeature);
		EasyMock.verify(gisFeature);
	    }

	    @Test
	    public void testGetCountry() {
		//fail("Not yet implemented");
	    }

	  

}
