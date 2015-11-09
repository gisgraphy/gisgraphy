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
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.vividsolutions.jts.geom.Point;

/**
 * Interface of data access object for {@link CitySubdivision}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface ICitySubdivisionDao extends IGisDao<CitySubdivision> {

    
    /**
     * @return the nearest citySubdivision in the given city from the given point, the result can be search within a distance in meter
     */
	public CitySubdivision getNearestInCity(final Point location,final long cityId, final Float maxDistance);
    
    /**
  	 * link suburb and neighborhood to their city
  	 * @return the number of elements saved
  	 */
  	public int linkCitySubdivisionToTheirCity();
  	
  	 /**
     * @return the city that the given point belongs by searching by shape. the countrycode is optionnal
     */
    public CitySubdivision getByShape(Point location,final String countryCode);
    
  
}
