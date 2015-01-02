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

import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.valueobject.StreetDistance;
import com.gisgraphy.street.StreetSearchMode;
import com.gisgraphy.street.StreetType;
import com.vividsolutions.jts.geom.Point;

public interface IOpenStreetMapDao extends IDao<OpenStreetMap, java.lang.Long> {

    /**
     * base method for all findNearest
     * 
     * @param point
     *                The point from which we want to find GIS Object
     * @param distance
     *                The radius in meters
     * @param firstResult
     *                the firstResult index (for pagination), numbered from 1,
     *                if < 1 : it will not be taken into account
     * @param maxResults
     *                The Maximum number of results to retrieve (for
     *                pagination), if <= 0 : it will not be taken into acount
     * @param streetType
     *                The type of street
     * @param oneWay
     *                whether the street should be oneway or not
     * @param name
     *                the name the street name must contains
     * 
     * @param streetSearchMode if we search in fulltext or contain mode
     * @param includeDistanceField if we have to calculate the distance or not
     * @return A List of StreetDistance with the nearest elements or an
     *         empty list (never return null), ordered by distance.
     * @see StreetDistance
     */
    public List<StreetDistance> getNearestAndDistanceFrom(
	    final Point point, final double distance,
	    final int firstResult, final int maxResults,
	    final StreetType streetType,Boolean oneWay, final String name,
	    final StreetSearchMode streetSearchMode,
	    final boolean includeDistanceField) ;
    
    /**
     * @param gid the gid of the openstreetmap entity we want to retrieve
     * @return the OpenstreetMap entity or null if not found
     */
    public OpenStreetMap getByGid(final Long gid) ;
    
    /** 
     * Update the ts_vector column for the street name search 
     * (partial search and fulltext search)
     * @return the number of line updated
     * @see StreetSearchMode
     */    
    public Integer updateTS_vectorColumnForStreetNameSearch();
    
    /** 
     * Update the ts_vector column for the street name search from the gid that are >= from and < to
     * @param from the start pagination index of gid
     * @param to the end pagination index 
     * (partial search and fulltext search)
     * @return the number of line updated
     * @see StreetSearchMode
     */    
    public Integer updateTS_vectorColumnForStreetNameSearchPaginate(long from, long to ) ;
    
    /**
     * Create the database GIST  for openstreetMap
     * to improve performances
     */
    public void createSpatialIndexes();
    
    /**
     * Create the fulltext index for openstreetMap
     * to improve performances
     */
    public void createFulltextIndexes();
    
    
    
    /**
     * @return the number of streets based on the highest gid 
     */
    public long countEstimate();
	
    /**
     * @param openstreetmapId the openstreetmap id (not the id, not the gid)
     * @return the openstreetmap
     */
    public OpenStreetMap getByOpenStreetMapId(Long openstreetmapId);
    
    
    /**
     * @param point the point to search around
     * @param ids the openstreetmap ids of streets we want to restrict search
     * @return the nearest street of all the streets with the ids specified, for the given point 
     */
    public OpenStreetMap getNearestByosmIds(final Point point, final List<Long> ids) ;
    
    /**
     * @return the highest openstreetMapId 
     */
    public long getMaxOpenstreetMapId();
    
    /**
     * @return the highest Gid 
     */
    public long getMaxGid();
    
    /**
     * find the nearest street based on the shape, not the middle point
     * 
     * @param point
     *                The point from which we want to find street
     */
    public OpenStreetMap getNearestFrom(
    	    final Point point) ;
    
    /**
     * find the nearest road based on the shape, not the middle point and exclude footway
     * 
     * @param point
     *                The point from which we want to find street
     */
    public OpenStreetMap getNearestRoadFrom(
    	    final Point point);

    
    /**
     * @param point
     * @param onlyroad 
     * @param filterEmptyName filter street with name=null
     * @return  the nearest street based on the shape, not the middle point
     */
    public OpenStreetMap getNearestFrom(
    	    final Point point,final boolean onlyroad,boolean filterEmptyName);
    /**
     * @param gid the gid of the feature
     * @return the shape as wkt
     */
    public String getShapeAsWKTByGId(final Long gid);
}
