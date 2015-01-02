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

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.vividsolutions.jts.geom.Point;

/**
 * Interface for a Generic Dao for GIS Object (java-5 meaning) it suppose that
 * the PK is of type long because its goal is to be used with class gisfeatures
 * and class that extends GisFeature. if it is note the case. it is possible to
 * create an other inteface<br>: public interface IGisDao<T,PK extends
 * Serializable> extends IDao<T,PK> it adds some method to the IDao in order to
 * acess GIS objects
 * 
 * @see GenericGisDao
 * @see IDao
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IGisDao<T> extends IDao<T, java.lang.Long> {

    /**
     * It is not the same as {@link IDao#get(java.io.Serializable)} which
     * retrieve an object from his PK.
     * 
     * @param featureId
     *                the featureid of the GIS object to retrieve
     * @return the Gis Object with the specified GisFeature id.
     * @throws IllegalArgumentException
     *                 if the FeatureId is null
     */
    public T getByFeatureId(final Long featureId);

    /**
     * Returns inconsistant object (in most case object With featureid < 0)
     * 
     * @return List of populated objects (never return null, but an empty list)
     */
    public List<T> getDirties();

    /**
     * Same as IDao#get(java.io.Serializable) but load the AlternateNames and
     * The linked Adm
     * 
     * @param id
     *                the id of the features to retrieve
     * @return The Feature with the alternateName and the Adm loaded
     * @see IDao#get(java.io.Serializable)
     */
    public T getEager(final Long id);

    /**
     * 
     * @param gisFeature
     *                The GisFeature from which we want to find GIS Object
     * @param distance
     *                distance The radius in meters
     * @param includeDistanceField 
     * 				Field whether or not we should process calculate the distance
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance. <u>note</u>
     *         the specified gisFeature will not be included into results the
     *         results will be of the type of the currentDao
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    final GisFeature gisFeature, final double distance,boolean includeDistanceField);

    /**
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
     * @param includeDistanceField
     * 				Field whether or not we should process calculate the distance
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance. <u>note</u>
     *         the specified gisFeature will not be included into results the
     *         results will be of the type of the currentDao<br/> <u>tips</u> :
     *         to search the nearest place use firstresult=1 and maxResults=1
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    final GisFeature gisFeature, final double distance,
	    final int firstResult, final int maxResults, final boolean includeDistanceField);

    /**
     * same as
     * {@link #getNearestAndDistanceFromGisFeature(GisFeature, double, int, int, boolean)} but
     * without paginate
     * 
     * @param point
     *                The point from which we want to find GIS Object
     * @param distance
     *                distance The radius in meters
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance. <u>note</u>
     *         the specified gisFeature will not be included into results the
     *         results will be of the type of the currentDao
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(
	    final Point point, final double distance);

    /**
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
     * @param includeDistanceField 
     * 				Field whether or not we should process calculate the distance
     * @param isMunicipality whether we should filter on city that are flag as 'municipality'.
						act as a filter, if false it doesn't filters( false doesn't mean that we return non municipality)
     * @return A List of GisFeatureDistance with the nearest elements or an
     *         emptylist (never return null), ordered by distance. <u>note</u>
     *         the specified gisFeature will not be included into results the
     *         results will be of the type of the currentDao<br/> <u>tips</u>:
     *         to search the nearest place use firstresult=1 and maxResults=1
     * @see GisFeatureDistance
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(
	    final Point point, final double distance, final int firstResult,
	    final int maxResults,final boolean includeDistanceField, boolean isMunicipality );

    /**
     * retrieve the Objects with the specified name (not the ASCII one)
     * 
     * @param name
     *                the name of the objects to retrieve
     * @return List of populated objects list (never return null, but an empty
     *         list)
     * @see #listFromText(String, boolean)
     */

    public List<T> listByName(final String name);

    /**
     * return all the Object with the specified featureIds (not ids :id is the
     * PK (aka datastore id), featureId is the domain value). return an
     * emptylist if the list of ids is null or empty). The features will be of
     * the type of the Dao.
     * 
     * @param ids
     *                the list of the ids of the object to retrieve
     * @return List of populated objects list (never return null, but an empty
     *         list)
     */
    public List<T> listByFeatureIds(final List<Long> ids);

    /**
     * Do a full text search for the given name. The search will be case,
     * iso-latin, comma-separated insensitive<br>
     * search for 'saint-André', 'saint-Andre', 'SaInT-andré', 'st-andré', etc
     * will return the same results Polymorphism is not supported, e.g : if you
     * use gisfeatureDao the results will only be of that type and no feature of
     * type City that extends gisFeature...etc will be returned.
     * 
     * @param name
     *                the name or zipcode of the GisFeature to search
     * @param includeAlternateNames
     *                wether we search in the alternatenames too
     * @return a list of gisFeatures of type of the class for the given text.
     *         the max list size is {@link GenericGisDao#MAX_FULLTEXT_RESULTS};
     * @see IGisFeatureDao#listAllFeaturesFromText(String, boolean)
     */
    public List<T> listFromText(String name, boolean includeAlternateNames);
    
    
    /**
     * Create the database GIST index for the column 'location' for this entity if it doesn't already exists
     */
    public void createGISTIndexForLocationColumn();
    
    /**
     * @param location The point from which we want to find GIS Object
     * @param countryCode restrict the search to a given country code (useful for feature near a frontier
     * @param filterMunicipality if  we should  filter municipality or not
     * @param distance te distance in meter
     * @return the nearest city differ from getnearestAndDistance because doesn't return distance field and with zip and alternateNames are populated
     */
    public T getNearest(final Point location,final String countryCode,final boolean filterMunicipality,final int distance);


    /**
     * create the shape index for the entity if it doesn't already exists. 
     */
    public void createGISTIndexForShapeColumn();
    
    
    /**
     * @param featureId
     * @return the shape as wkt or null;
     */
    public String getShapeAsWKTByFeatureId(Long featureId);
}
