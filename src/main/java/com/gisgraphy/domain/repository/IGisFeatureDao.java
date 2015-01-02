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

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.vividsolutions.jts.geom.Point;

/**
 * Interface of data access object for {@link GisFeature}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IGisFeatureDao extends IGisDao<GisFeature> {

    /**
     * Do a full text search for the given name. The search will be case,
     * iso-latin, comma-separated insensitive<br>
     * search for 'saint-André', 'saint-Andre', 'SaInT-andré', 'st-andré', etc
     * will return the same results. The search is done for all type,
     * independentely of the type
     * 
     * @param name
     *                the name or zipcode of the GisFeature to search
     * @param includeAlternateNames
     *                wether we search in the alternatenames and names
     * @return a list of gisFeatures of type of the class for the given text.
     *         the max list size is {@link GenericGisDao#MAX_FULLTEXT_RESULTS};
     * @see IGisDao#listFromText(String, boolean)
     */
    public List<GisFeature> listAllFeaturesFromText(String name,
	    boolean includeAlternateNames);

    /**
     * <u>tips</u> to search the nearestplace use firstresult=1 and
     * maxResults=1
     * 
     * @param gisFeature
     *                The GisFeature from which we want to find GIS Object
     * @param distance
     *                distance The radius in meters
     * @param firstResult
     *                the firstResult index (for pagination), numbered from 1,
     *                if < 1 : it will not be taken into account
     * @param maxResults
     *                The Maximum number of results to retrieve (for
     *                pagination), if <= 0 : it will not be taken into acount
     * @param requiredClass
     *                the class of the object to be retireved
     * @param isMunicipality whether we should filter on city that are flag as 'municipality'.
						act as a filter, if false it doesn't filters( false doesn't mean that we return non municipality)
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance.<u>note</u>
     *         the specified gisFeature will not be included into results
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    final GisFeature gisFeature, final double distance,
	    final int firstResult, final int maxResults,
	    boolean includeDistanceField,
	    final Class<? extends GisFeature> requiredClass, boolean isMunicipality);

    /**
     * 
     * @param gisFeature
     *                The GisFeature from which we want to find GIS Object
     * @param distance
     *                distance The radius in meters
     * @param requiredClass
     *                the class of the object to be retireved
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance.<u>note</u>
     *         the specified gisFeature will not be included into results
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    final GisFeature gisFeature, final double distance,
	    boolean includeDistanceField,
	    final Class<? extends GisFeature> requiredClass);

    /**
     * <u>tips</u> to search the nearestplace use firstresult=1 and
     * maxResults=1
     * 
     * @param point
     *                The point from which we want to find GIS Object
     * @param distance
     *                distance The radius in meters
     * @param firstResult
     *                the firstResult index (for pagination), numbered from 1,
     *                if < 1 : it will not be taken into account
     * @param maxResults
     *                The Maximum number of results to retrieve (for
     *                pagination), if <= 0 : it will not be taken into acount
     * @param requiredClass
     *                the class of the object to be retireved
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance.<u>note</u>
     *         the specified gisFeature will not be included into results
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(
	    final Point point, final double distance, final int firstResult,
	    final int maxResults,
	    final boolean includeDistanceField,
	    final Class<? extends GisFeature> requiredClass);

    /**
     * 
     * @param point
     *                The point from which we want to find GIS Object
     * @param distance
     *                distance The radius in meters
     * @param requiredClass
     *                the class of the object to be retireved
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance.<u>note</u>
     *         the specified gisFeature will not be included into results
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(
	    final Point point, final double distance,
	    final boolean includeDistanceField,
	    final Class<? extends GisFeature> requiredClass);

    /**
     * Delete all gisFeatures and subclass except Adms and countries (gisFeature
     * with null featureCode will be deleted too). this method is usefull when
     * gisFeature 'belongs' to some adms and we want to delete the gisfeature
     * first and then the adms and countries. We don't control the fact that
     * adms should be delete after all gisFeature due to Primary /foreign key
     * with {@linkplain GenericGisDao#deleteAll()}
     * 
     * @return the number of deleted elements
     */
    public int deleteAllExceptAdmsAndCountries();
    
    /**
     * @return the highest featureId (independant from the placetype) 
     */
    public long getMaxFeatureId();

}
