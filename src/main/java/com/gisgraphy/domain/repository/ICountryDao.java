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
/**
 *
 */
package com.gisgraphy.domain.repository;

import java.util.List;

import com.gisgraphy.domain.geoloc.entity.Country;

/**
 * Interface of data access object for {@link Country}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface ICountryDao extends IGisDao<Country> {

    /**
     * Get By ISO 3166 Alpha 2 (2-letter) code <u>NOTE</u> : The country code
     * will be automaticaly converted in upperCase
     * 
     * @see #getByIso3166Alpha3Code(String)
     * @see #getByIso3166Code(String)
     * @param iso3166Alpha2Code
     *                The ISO 3166 Alpha 2 code in upper case
     * @return The expected country for the specified alpha 2 code or null if
     *         the iso639Alpha2LanguageCode is null
     */
    public Country getByIso3166Alpha2Code(String iso3166Alpha2Code);

    /**
     * Get by ISO 639 Alpha 3 (3-letter) code <u>NOTE</u> : The country code
     * will be automaticaly converted in upperCase
     * 
     * @see #getByIso3166Alpha2Code(String)
     * @see #getByIso3166Code(String)
     * @param iso3166Alpha3Code
     *                The ISO 3166 Alpha 2 code in upper case
     * @return The expected country for the specified alpha 3 code or null if
     *         the iso639Alpha3Code is null
     */
    public Country getByIso3166Alpha3Code(String iso3166Alpha3Code);

    /**
     * Wrapper method around {@link #getByIso3166Alpha2Code(String)} and
     * {@link #getByIso3166Alpha3Code(String)}. This method is to use when you
     * want to get the country with an iso 3166 code that you don't know if it
     * is an alpha 2 or 3 Get by ISO 639 Alpha 2 or 3 code
     * 
     * @see #getByIso3166Alpha3Code(String)
     * @see #getByIso3166Alpha2Code(String)
     * @param iso3166Code
     *                the iso 3166 Code
     * @return the expected country or null if the code is null or it is not a 2
     *         or 3 char code
     */
    public Country getByIso3166Code(String iso3166Code);

    /**
     * returns the country for the specified name
     * 
     * @param name
     *                the name of the country to retrieve
     * @return the expected country or null if the code is null or it is not a 2
     *         or 3 char code
     */
    public Country getByName(String name);

    /**
     * @return all the countries sorted by name, never return null but an empty
     *         list
     */
    public List<Country> getAllSortedByName();
    
    /**
     * List all the featureId of countries 
     * @return a list of all featureId for all the countries
     */
    public List<Long> listFeatureIds();
}
