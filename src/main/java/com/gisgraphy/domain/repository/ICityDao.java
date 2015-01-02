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

import com.gisgraphy.domain.geoloc.entity.City;
import com.vividsolutions.jts.geom.Point;

/**
 * Interface of data access object for {@link City}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface ICityDao extends IGisDao<City> {

    /**
     * @param zipcode
     *                the zipcode to found
     * @param countrycode
     *                the countrycode to limit the search, if null: search in
     *                all country
     * @return A list a city for the specified parameters, if countrycode is
     *         specified, the list should have one city. To search for name or
     *         zip code use : {@link CityDao#listFromText(String, boolean)}.
     */
    public List<City> listByZipCode(String zipcode, String countrycode);
    
    /**
     * @return the city that the given point belongs by searching by shape. the countrycode is optionnal
     * we can filter the result if the city is a municipality or not
     */
    public City getByShape(Point location,final String countryCode,boolean filterMunicipality);
    
  
    
  
}
