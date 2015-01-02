/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/
package com.gisgraphy.domain.repository;

import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.Language;
import com.gisgraphy.domain.repository.exception.DuplicateNameException;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;

public class CountryDaoTest extends AbstractIntegrationHttpSolrTestCase {

    private ICountryDao countryDao;

    private ILanguageDao languageDao;

    @Test
    public void testGetByIso639Alpha2Code() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao.getByIso3166Alpha2Code("FR");
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry.getId(), retrievedCountry.getId());

    }

    @Test
    public void testGetByIso639Alpha3Code() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao
		.getByIso3166Alpha3Code("FRA");
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry.getId(), retrievedCountry.getId());
    }

    @Test
    public void testGetByIso639Alpha2CodeIsCaseInsensitive() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao.getByIso3166Alpha2Code("fr");
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry.getId(), retrievedCountry.getId());

    }

    @Test
    public void testGetByIso639Alpha3CodeIsCaseInsensitive() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao
		.getByIso3166Alpha3Code("fra");
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry.getId(), retrievedCountry.getId());
    }

    @Test
    public void testGetByIso3166CodeWithAIso639Alpha2Code() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao.getByIso3166Code("FR");
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry.getId(), retrievedCountry.getId());

    }

    @Test
    public void testGetByIso3166CodeWithAIso639Alpha3Code() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao.getByIso3166Code("FRA");
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry.getId(), retrievedCountry.getId());

    }

    @Test
    public void testGetByIso3166CodeWithAWrongLengthCodeShouldReturnNull() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	assertNull(this.countryDao.getByIso3166Code("FRAE"));

    }

    @Test
    public void testGetByIso3166CodeWithANullCodeShouldReturnNull() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	assertNull(this.countryDao.getByIso3166Code(null));
    }

    @Test
    public void testGetByIso639Alpha2CodeWithANullCodeShouldReturnNull() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());
	assertNull(this.countryDao.getByIso3166Alpha2Code(null));

    }

    @Test
    public void testGetByIso639Alpha3CodeWithANullCodeShouldReturnNull() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	assertNull(this.countryDao.getByIso3166Alpha3Code(null));
    }

    @Test
    public void testGetByIso639Alpha2CodeWithAWrongLengthCodeShouldReturnNull() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	assertNull(this.countryDao.getByIso3166Alpha2Code("F"));

    }

    @Test
    public void testGetByIso639Alpha3CodeWithAWrongLengthCodeShouldReturnNull() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	assertNull(this.countryDao.getByIso3166Alpha3Code("F"));

    }

    @Test
    public void testGetByNameWithANullNameShouldReturnNull() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	assertNull(this.countryDao.getByName(null));

    }

    @Test
    public void testGetByNameShouldReturnTheCorrectCountry() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrieved = this.countryDao.getByName(savedCountry.getName());
	assertNotNull(retrieved);
	assertEquals(savedCountry, retrieved);

    }

    @Test
    public void testSaveShouldThrowsIfACountryWithTheSameNameAlreadyExists() {
	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());
	Country duplicate = GisgraphyTestHelper.createCountryForFrance();

	try {
	    this.countryDao.save(duplicate);
	    fail("a country with a same name can not be saved if the id is null");
	} catch (DuplicateNameException e) {

	}

	try {
	    this.countryDao.save(country);
	} catch (DuplicateNameException e) {
	    fail("a country with a different name can be updated if the id is not null");
	}

    }

    // save
    @Test
    public void testSaveWithSpokenlanguagesCascade() {
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);

	Country country = GisgraphyTestHelper.createCountryForFrance();
	country.addSpokenLanguage(lang);
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao.getByIso3166Alpha2Code("FR");
	assertNotNull(retrievedCountry);
	assertEquals(savedCountry.getId(), retrievedCountry.getId());
	assertNotNull(retrievedCountry.getSpokenLanguages());
	assertEquals(1, retrievedCountry.getSpokenLanguages().size());
	assertEquals(savedLang, retrievedCountry.getSpokenLanguages().get(0));
    }

    @Test
    public void testDeleteAllShoulDeleteCountryButNotTheLanguages() {
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);

	Country country = GisgraphyTestHelper.createCountryForFrance();
	country.addSpokenLanguage(lang);
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry.getId());

	Country retrievedCountry = this.countryDao.getByIso3166Alpha2Code("FR");
	assertNotNull(retrievedCountry);

	Country country2 = new Country("US", "USA", 00);
	country2.setFeatureId(new Random().nextLong());
	country2.setLocation(GisgraphyTestHelper.createPoint(0F, 0F));
	country2.setName("usa");
	country2.setSource(GISSource.GEONAMES);
	country2.addSpokenLanguage(lang);
	Country savedCountry2 = this.countryDao.save(country2);
	assertNotNull(savedCountry2.getId());

	assertEquals(2, countryDao.deleteAll());
	assertEquals(0, countryDao.count());

	assertEquals(1, languageDao.count());
    }

    @Test
    public void testGetAllSortedByName() {
    	Country country1 = createCountry("FR","FRA",33,25L,"c");
    	Country country2 = createCountry("BE","BEL",34,26L,"a"); 
    	Country country3 = createCountry("DE","DEL",35,27L,"B");
	countryDao.save(country1);
	countryDao.save(country2);
	countryDao.save(country3);
	List<Country> expected = countryDao.getAllSortedByName();
	assertEquals(3, expected.size());
	assertEquals("a", expected.get(0).getName());
	assertEquals("B", expected.get(1).getName());
	assertEquals("c", expected.get(2).getName());
    }
    
    @Test
    public void testlistFeatureIds() {
	Country country1 = createCountry("FR","FRA",33,25L,"c");
	Country country2 = createCountry("BE","BEL",34,26L,"a"); 
	Country country3 = createCountry("DE","DEL",35,27L,"B");new Country("DE", "DEL", 35);
	countryDao.save(country1);
	countryDao.save(country2);
	countryDao.save(country3);
	List<Long> expected = countryDao.listFeatureIds();
	assertEquals("The list of featureIds has not the expected size", 3,expected.size());
	assertTrue("the featureId of the first country is not present", expected.contains(country1.getFeatureId()));
	assertTrue("the featureId of the second country is not present", expected.contains(country2.getFeatureId()));
	assertTrue("the featureId of the third country is not present", expected.contains(country3.getFeatureId()));
    }

	private Country createCountry(String iso3166Alpha2Code, String iso3166Alpha3Code,
		    int iso3166NumericCode,Long featureID,String name) {
		Country country1 = new Country(iso3166Alpha2Code,iso3166Alpha3Code, iso3166NumericCode);
		country1.setFeatureId(featureID);
		country1.setFeatureClass("A");
		country1.setFeatureCode("PCL");
		country1.setLocation(GisgraphyTestHelper.createPoint(0F, 0F));
		country1.setName(name);
		country1.setSource(GISSource.GEONAMES);
		return country1;
	}

    @Test
    public void testGetAllSortedByNameShouldReturnAnEmptyListIfNoResult() {
	List<Country> expected = countryDao.getAllSortedByName();
	assertNotNull(expected);
	assertEquals(0, expected.size());

    }

    @Test
    public void testDeleteAllShoulReturn0ifNoCountry() {
	assertEquals(0, countryDao.deleteAll());
    }

    @Required
    public void setCountryDao(ICountryDao countryDao) {
	this.countryDao = countryDao;
    }

    @Required
    public void setLanguageDao(ILanguageDao languageDao) {
	this.languageDao = languageDao;
    }

}
