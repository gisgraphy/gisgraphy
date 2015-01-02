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
package com.gisgraphy.domain.geoloc.entity;

import org.junit.Test;

import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;

public class CountryTest extends AbstractIntegrationHttpSolrTestCase {

    @Test
    public void testCountryStringStringIntShouldSetIsoCountryAlphaCodeToUpperCaseAndCountryCode() {
	Country country = new Country("fr", "fra", 33);
	assertEquals("the iso 3166-2 code should be in uppercase", "FR",
		country.getIso3166Alpha2Code());
	assertEquals("The iso 3166-3 code should be in uppercase", "FRA",
		country.getIso3166Alpha3Code());
	assertNotNull("The countryCcode should be set", country
		.getCountryCode());
    }

    @Test
    public void testSetIso3166Alpha2CodeShouldSetIsoCountryAlpha2CodeToUpperCaseAndCountryCode() {
	Country country = new Country();
	country.setIso3166Alpha2Code("fr");
	assertEquals("the iso 3166-2 code should be in uppercase", "FR",
		country.getIso3166Alpha2Code());
	assertNotNull("The countryCcode should be set", country
		.getCountryCode());
    }

    @Test
    public void testSetIso3166Alpha3CodeShouldBeSetInUpperCase() {
	Country country = new Country();
	country.setIso3166Alpha3Code("fra");
	assertEquals("the iso 3166-2 code should be in uppercase", "FRA",
		country.getIso3166Alpha3Code());
    }

    @Test
    public void testGetCurrencyShouldReturnTheCorrectCurency() {
	Country country = new Country();
	country.setCurrencyCode("EUR");
	assertNotNull(country.getCurrency());
	assertEquals("EUR", country.getCurrency().getCurrencyCode());
    }

    @Test
    public void testGetCurrencyWithNullCurrencycodeShouldreturnNull() {
	Country country = new Country();
	assertNull(country.getCurrency());
    }

    @Test
    public void testGetCurrencyWithWrongCurrencycodeShouldreturnNull() {
	Country country = new Country();
	country.setCurrencyCode("ERROR");
	assertNull(country.getCurrency());
    }

}
