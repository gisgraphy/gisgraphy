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

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Language;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;

public class LanguageDaoTest extends AbstractIntegrationHttpSolrTestCase {

    private ILanguageDao languageDao;

    // private ICountryDao countryDao;

    @Test
    public void testFindByIso639Alpha2CodeShouldReturnCorrectLanguage() {
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);

	Language found = this.languageDao.getByIso639Alpha2Code("FR");
	assertEquals(retrievedLang, found);
    }

    @Test
    public void testFindByIso639Alpha3CodeShouldReturnCorrectLanguage() {
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);

	Language found = this.languageDao.getByIso639Alpha3Code("FRA");
	assertEquals(retrievedLang, found);
    }

    @Test
    public void testFindByIso639CodeShouldReturnCorrectLanguage() {
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);

	Language found = this.languageDao.getByIso639Code("FRA");
	assertEquals(retrievedLang, found);

	found = this.languageDao.getByIso639Code("FR");
	assertEquals(retrievedLang, found);

	try {
	    this.languageDao.getByIso639Code(null);
	    fail();
	} catch (IllegalArgumentException e) {
	}

	try {
	    this.languageDao.getByIso639Code("");
	    fail();
	} catch (IllegalArgumentException e) {
	}

	try {
	    this.languageDao.getByIso639Code("FRAR");
	    fail();
	} catch (IllegalArgumentException e) {
	}

    }

    /*
     * @Test public void testDeleteAllShouldDeleteLanguagesButNotCountry() {
     * Language lang = new Language("french", "FR", "FRA"); Language savedLang =
     * languageDao.save(lang); Language retrievedLang =
     * languageDao.get(savedLang.getId()); assertEquals(savedLang,
     * retrievedLang);
     * 
     * Country country = GeolocTestHelper.createCountryForFrance();
     * country.addSpokenLanguage(lang); Country savedCountry =
     * this.countryDao.save(country); assertNotNull(savedCountry.getId());
     * 
     * Country retrievedCountry = this.countryDao.getByIso3166Alpha2Code("FR");
     * assertNotNull(retrievedCountry);
     * 
     * Country country2 = new Country("US", "USA", 00);
     * country2.setFeatureId(new Random().nextLong());
     * country2.setLocation(GeolocTestHelper.createPoint(0F, 0F));
     * country2.setName("usa"); country2.setSource(GISSource.GEONAMES);
     * country2.addSpokenLanguage(lang); Country savedCountry2 =
     * this.countryDao.save(country2); assertNotNull(savedCountry2.getId());
     * 
     * assertEquals(1, languageDao.deleteAll()); assertEquals(2,
     * countryDao.count());
     * 
     * assertEquals(0, languageDao.count()); }
     * 
     * @Test public void testDeleteAllShouldReturn0ifNoLanguages() {
     * assertEquals(0, languageDao.deleteAll()); }
     */

    public void setLanguageDao(ILanguageDao languageDao) {
	this.languageDao = languageDao;
    }

    /*
     * @Required public void setCountryDao(ICountryDao countryDao) {
     * this.countryDao = countryDao; }
     */

}
