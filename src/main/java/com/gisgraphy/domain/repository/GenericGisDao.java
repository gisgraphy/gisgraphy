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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernatespatial.GeometryUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.geoloc.entity.ZipCodesAware;
import com.gisgraphy.domain.geoloc.entity.event.EventManager;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeleteAllEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeletedEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureStoredEvent;
import com.gisgraphy.domain.geoloc.entity.event.PlaceTypeDeleteAllEvent;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.domain.valueobject.SRID;
import com.gisgraphy.fulltext.FullTextFields;
import com.gisgraphy.fulltext.IsolrClient;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.IntrospectionHelper;
import com.gisgraphy.hibernate.criterion.DistanceRestriction;
import com.gisgraphy.hibernate.criterion.ProjectionOrder;
import com.gisgraphy.hibernate.criterion.ResultTransformerUtil;
import com.gisgraphy.hibernate.projection.ProjectionBean;
import com.gisgraphy.hibernate.projection.SpatialProjection;
import com.gisgraphy.importer.ImporterConfig;
import com.vividsolutions.jts.geom.Point;

/**
 * Generic Dao for Gis Object (java-5 meaning) It suppose that the PK is of type
 * long because its goal is to be used with class gisfeatures and class that
 * extends GisFeature. if it is note the case. it is possible to create an other
 * inteface<br>
 * it adds some method to the GenericDao in order to acess GIS objects
 * 
 * @see GenericDao
 * @param <T>
 *                the type of the object the Gis Dao apply
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
/**
 * @author gisgraphy
 *
 * @param <T>
 */
public class GenericGisDao<T extends GisFeature> extends
	GenericDao<T, java.lang.Long> implements IGisDao<T> {

    public static final Type GEOMETRY_TYPE = new CustomType(
	    GeometryUserType.class, null);

    public static final int MAX_FULLTEXT_RESULTS = 100;

    @Autowired
    @Qualifier("solrClient")
    private IsolrClient solrClient;

    private EventManager eventManager;

    /**
     * Constructor
     * 
     * @param persistentClass
     *                The specified Class for the GenericGisDao
     */
    public GenericGisDao(final Class<T> persistentClass) {
	super(persistentClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getNearestAndDistanceFrom(com.gisgraphy.domain.geoloc.entity.GisFeature,
     *      double, int, int)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    final GisFeature gisFeature, final double distance,
	    final int firstResult, final int maxResults, boolean includeDistanceField) {
	return getNearestAndDistanceFrom(gisFeature.getLocation(), gisFeature
		.getId(), distance, firstResult, maxResults, includeDistanceField, persistentClass, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getNearestAndDistanceFrom(com.gisgraphy.domain.geoloc.entity.GisFeature,
     *      double)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    GisFeature gisFeature, double distance, boolean includeDistanceField) {
	return getNearestAndDistanceFrom(gisFeature.getLocation(), gisFeature
		.getId(), distance, -1, -1, includeDistanceField, persistentClass, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getNearestAndDistanceFrom(com.vividsolutions.jts.geom.Point,
     *      double)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(Point point,
	    double distance) {
	return getNearestAndDistanceFrom(point, 0L, distance, -1, -1, true,
		persistentClass, false);
    }

    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IGisDao#getNearestAndDistanceFrom(com.vividsolutions.jts.geom.Point, double, int, int, boolean, boolean)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(Point point,
	    double distance, int firstResult, int maxResults, boolean includeDistanceField, boolean isMunicipality) {
	return getNearestAndDistanceFrom(point, 0L, distance, firstResult,
		maxResults,includeDistanceField, persistentClass, isMunicipality);
    }

    /**
     * base method for all findNearest* 
     * 
     * @param point
     *                The point from which we want to find GIS Object
     * @param pointId
     *                the id of the point that we don't want to be include, it
     *                is used to not include the gisFeature from which we want
     *                to find the nearest
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
     * @return a list of gisFeature (never return null but an empty list)
     */
    @SuppressWarnings("unchecked")
    protected List<GisFeatureDistance> getNearestAndDistanceFrom(
	    final Point point, final Long pointId, final double distance,
	    final int firstResult, final int maxResults,
	    final boolean includeDistanceField,
	    final Class<? extends GisFeature> requiredClass, final boolean isMunicipality) {
	Assert.notNull(point);
	return (List<GisFeatureDistance>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			Criteria criteria = session
				.createCriteria(requiredClass);
			
			if (maxResults > 0) {
			    criteria = criteria.setMaxResults(maxResults);
			}
			if (firstResult >= 1) {
			    criteria = criteria.setFirstResult(firstResult - 1);
			}
			criteria = criteria.add(new DistanceRestriction(point,
				distance));
			List<String> fieldList = IntrospectionHelper
				.getFieldsAsList(requiredClass);
			ProjectionList projections = ProjectionBean.fieldList(
				fieldList,true);
			if (includeDistanceField){
			    projections.add(
				SpatialProjection.distance_sphere(point,GisFeature.LOCATION_COLUMN_NAME).as(
					"distance"));
			}
			criteria.setProjection(projections);
			if (pointId != 0) {
			    // remove The From Point
			    criteria = criteria.add(Restrictions.not(Restrictions.idEq(pointId)));
			}
			if (includeDistanceField){
			    criteria.addOrder(new ProjectionOrder("distance"));
			}
			if (isMunicipality && (requiredClass == City.class || requiredClass == GisFeature.class)){
				criteria.add(Restrictions.eq(City.MUNICIPALITY_FIELD_NAME, isMunicipality));
			}
			
			criteria.setCacheable(true);
			List<Object[]> queryResults = criteria.list();
			
			String[] aliasList;
			if (includeDistanceField){
			aliasList = (String[]) ArrayUtils
				.add(
					IntrospectionHelper
						.getFieldsAsArray(requiredClass),
					"distance");
			} else {
			    aliasList = IntrospectionHelper
				.getFieldsAsArray(requiredClass);
			}
			int idPropertyIndexInAliasList=0;
			for (int i=0;i<aliasList.length;i++){
			    if (aliasList[i]=="id"){
				idPropertyIndexInAliasList = i;
				break;
			    }
			}
			
			
			boolean hasZipCodesProperty = ZipCodesAware.class.isAssignableFrom(requiredClass);
			Map<Long, Set<String>> idToZipCodesMap = null;
			if (hasZipCodesProperty && queryResults.size()>0){
			List<Long> ids = new ArrayList<Long>();
			for (Object[] tuple: queryResults){
			    ids.add((Long)tuple[idPropertyIndexInAliasList]);
			}
			String zipCodeQuery = "SELECT code as code,gisfeature as id FROM "+ZipCode.class.getSimpleName().toLowerCase() +" zip where zip.gisfeature in (:ids)" ;
			Query qry = session.createSQLQuery(zipCodeQuery).addScalar("code", Hibernate.STRING).addScalar("id", Hibernate.LONG);
			qry.setCacheable(true);

			qry.setParameterList("ids", ids);
			List<Object[]> zipCodes = (List<Object[]>) qry.list();
			
			if (zipCodes.size() > 0) {
			    idToZipCodesMap = new HashMap<Long, Set<String>>();
			    for (Object[] zipCode : zipCodes){
				Long idFromZipcode = (Long) zipCode[1];
				Set<String> zipCodesFromMap  = idToZipCodesMap.get(idFromZipcode);
				if (zipCodesFromMap == null){
				    Set<String> zipCodesToAdd = new HashSet<String>();
				    idToZipCodesMap.put(idFromZipcode, zipCodesToAdd);
				    zipCodesFromMap = zipCodesToAdd;
				} 
				zipCodesFromMap.add((String)zipCode[0]);
			    }
			}
			}
			List<GisFeatureDistance> results = ResultTransformerUtil
			.transformToGisFeatureDistance(
					aliasList,
				queryResults,idToZipCodesMap,requiredClass);
			return results;
		    }
		});

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#findByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<T> listByName(final String name) {
	Assert.notNull(name);
	return (List<T>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ persistentClass.getSimpleName()
				+ " as c where c.name= ?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, name);
			List<T> results = (List<T>) qry.list();
			if (results == null) {
			    results = new ArrayList<T>();
			}
			return results;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getByFeatureId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public T getByFeatureId(final Long featureId) {
	Assert.notNull(featureId);
	return (T) this.getHibernateTemplate().execute(new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {
		String queryString = "from " + persistentClass.getSimpleName()
			+ " as g where g.featureId= ?";

		Query qry = session.createQuery(queryString);
		qry.setCacheable(true);

		qry.setParameter(0, featureId);
		T result = (T) qry.uniqueResult();

		return result;
	    }
	});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getDirty()
     */
    @SuppressWarnings("unchecked")
    public List<T> getDirties() {
	return (List<T>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "from "
				+ persistentClass.getSimpleName()
				+ " as g where g.featureCode ='"
				+ ImporterConfig.DEFAULT_FEATURE_CODE
				+ "' or g.location=? or g.featureClass='"
				+ ImporterConfig.DEFAULT_FEATURE_CLASS + "'";

			final Query qry = session.createQuery(queryString);
			qry.setParameter(0, GeolocHelper.createPoint(0F, 0F),
				GEOMETRY_TYPE);
			qry.setCacheable(true);

			List<T> result = (List<T>) qry.list();
			if (result == null) {
			    result = new ArrayList<T>();
			}

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericDao#save(java.lang.Object)
     */
    @Override
    public T save(T GisFeature) {
	T savedgisFeature = super.save(GisFeature);
	GisFeatureStoredEvent CreatedEvent = new GisFeatureStoredEvent(
		GisFeature);
	eventManager.handleEvent(CreatedEvent);
	return savedgisFeature;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericDao#remove(java.lang.Object)
     */
    @Override
    public void remove(T gisFeature) {
	super.remove(gisFeature);
	GisFeatureDeletedEvent gisFeatureDeletedEvent = new GisFeatureDeletedEvent(
		gisFeature);
	eventManager.handleEvent(gisFeatureDeletedEvent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getByFeatureIds(java.util.List)
     */
    @SuppressWarnings("unchecked")
    public List<T> listByFeatureIds(final List<Long> ids) {
	if (ids == null || ids.size() == 0) {
	    return new ArrayList<T>();
	}
	return (List<T>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "from "
				+ persistentClass.getSimpleName()
				+ " as g where g.featureId in (:ids)";

			final Query qry = session.createQuery(queryString);
			qry.setParameterList("ids", ids);
			qry.setCacheable(true);

			List<T> result = (List<T>) qry.list();
			if (result == null) {
			    result = new ArrayList<T>();
			}

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getFromText(java.lang.String,
     *      boolean)
     */
    public List<T> listFromText(String name, boolean includeAlternateNames) {
	return listFromText(name, includeAlternateNames, persistentClass);
    }

    /**
     * Do a full text search for the given name. The search will be case,
     * iso-latin, comma-separated insensitive<br>
     * search for 'saint-André', 'saint-Andre', 'SaInT-andré', 'st-andré', etc
     * will return the same results. <u>note</u> : search for zipcode too
     * Polymorphism is not supported, e.g : if clazz=GisFeature : the results
     * will only be of that type and no other feature type (e.g : City that
     * extends gisFeature...etc) will be returned. The results will be sort by
     * relevance.
     * 
     * @param name
     *                The name to search for
     * @param includeAlternateNames
     *                Wether we search in the alternatenames too
     * @param clazz
     *                specify the features we want to search for, if null : no
     *                restriction is apply
     * @return a list of gisFeatures of type of the class for the given text.
     *         the max list size is {@link GenericGisDao#MAX_FULLTEXT_RESULTS};
     * @see IGisFeatureDao#listAllFeaturesFromText(String, boolean)
     */
    protected List<T> listFromText(String name, boolean includeAlternateNames,
	    Class<T> clazz) {
	logger.debug("getFromText " + name);
	// Set up a simple query
	// Check for a null or empty string query
	if (name == null || name.length() == 0) {
	    return new ArrayList<T>();
	}

	SolrQuery query = new SolrQuery();
	String namefield = FullTextFields.ALL_NAME.getValue();
	if (!includeAlternateNames) {
	    namefield = FullTextFields.NAME.getValue();
	}
	String queryString = "(" + namefield + ":\"" + name + "\" OR "
		+ FullTextFields.ZIPCODE.getValue() + ":\"" + name + "\")";
	if (clazz != null) {
	    queryString += " AND placetype:" + persistentClass.getSimpleName();
	}
	query.setQuery(queryString);
	query.setQueryType(Constants.SolrQueryType.advanced.toString());
	query.setFields(FullTextFields.FEATUREID.getValue());
	query.setRows(MAX_FULLTEXT_RESULTS);

	QueryResponse results = null;
	try {
	    results = solrClient.getServer().query(query);
	} catch (SolrServerException e) {
	    throw new RuntimeException(e);
	}

	List<Long> ids = new ArrayList<Long>();
	for (SolrDocument doc : results.getResults()) {
	    ids.add((Long) doc.getFieldValue(FullTextFields.FEATUREID
		    .getValue()));
	}
	// log
	List<T> gisFeatureList = this.listByFeatureIds(ids);
	if (logger.isDebugEnabled()) {
	    logger.debug("search on " + name + " returns "
		    + gisFeatureList.size());
	    for (GisFeature gisFeature : gisFeatureList) {
		logger.debug("search on " + name + " returns "
			+ gisFeature.getName());
	    }
	}

	return gisFeatureList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericDao#deleteAll(java.util.List)
     */
    @Override
    public void deleteAll(List<T> list) {
	super.deleteAll(list);
	GisFeatureDeleteAllEvent gisFeatureDeleteAllEvent = new GisFeatureDeleteAllEvent(
		list);
	eventManager.handleEvent(gisFeatureDeleteAllEvent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericDao#deleteAll()
     */
    @Override
    public int deleteAll() {
	int numberOfGisDeleted = super.deleteAll();
	PlaceTypeDeleteAllEvent placeTypeDeleteAllEvent = new PlaceTypeDeleteAllEvent(
		this.getPersistenceClass());
	eventManager.handleEvent(placeTypeDeleteAllEvent);
	return numberOfGisDeleted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisDao#getEager(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public T getEager(final Long id) {
	Assert.notNull(id, "Can not retrieve an Ogject with a null id");
	T returnValue = null;
	try {
	    return (T) this.getHibernateTemplate().execute(
		    new HibernateCallback() {

			public Object doInHibernate(Session session)
				throws PersistenceException {
			    String queryString = "from "
				    + persistentClass.getSimpleName()
				    + " o where o.id=" + id;

			    Query qry = session.createQuery(queryString);
			    qry.setCacheable(true);
			    GisFeature feature = (GisFeature) qry
				    .uniqueResult();

			    feature.getAdm().getAdm1Code();
			    feature.getAlternateNames().size();
			    return feature;

			}
		    });
	} catch (Exception e) {
	    logger.info("could not retrieve object of type "
		    + persistentClass.getSimpleName() + " with id " + id, e);
	}
	return returnValue;
    }
    
    

    @Required
    public void setEventManager(EventManager eventManager) {
	this.eventManager = eventManager;
    }
    
    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IGisDao#createGISTIndexForLocationColumn()
     */
    public void createGISTIndexForLocationColumn() {
	 this.getHibernateTemplate().execute(
			 new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				session.flush();
				
				logger.info("will create GIST index for  "+persistentClass.getSimpleName());
				String locationIndexName = "locationIndex"+persistentClass.getSimpleName();
				logger.info("checking if "+locationIndexName+" exists");
				String checkingLocationIndex= "SELECT 1 FROM   pg_class c  JOIN   pg_namespace n ON n.oid = c.relnamespace WHERE  c.relname = '"+locationIndexName+"'";
				Query checkingLocationIndexQuery = session.createSQLQuery(checkingLocationIndex);
				Object locationIndexExists = checkingLocationIndexQuery.uniqueResult();
				if (locationIndexExists != null){
					logger.info("will create GIST index for  the "+OpenStreetMap.SHAPE_COLUMN_NAME+" column");
					String createIndex = "CREATE INDEX "+locationIndexName+" ON "+persistentClass.getSimpleName().toLowerCase()+" USING GIST (location)";  
					Query createIndexQuery = session.createSQLQuery(createIndex);
					createIndexQuery.executeUpdate();
				} else {
					logger.info("won't create GIST index for "+persistentClass.getSimpleName()+" because it already exists");
				}
				
				return null;
			    }
			});
    }
    
    @SuppressWarnings("unchecked")
	public T getNearest(final Point location,final String countryCode,final boolean filterMunicipality,final int distance) {
		Assert.notNull(location);
		return (T) this.getHibernateTemplate().execute(new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
		    String pointAsString = "ST_GeometryFromText('POINT("+location.getX()+" "+location.getY()+")',"+SRID.WGS84_SRID.getSRID()+")";
			String queryString = "from " + persistentClass.getSimpleName()
				+ " as c  where st_distance_sphere(c.location,"+pointAsString+") < "+distance;//left outer join c.zipCodes z
			if (filterMunicipality){
				queryString+=" and c.municipality=true";
			}
			if (countryCode!=null ){
				queryString+=" and c.countryCode='"+countryCode+"'";
			}
			queryString = queryString+ " order by st_distance_sphere(c.location,"+pointAsString+")";

			Query qry = session.createQuery(queryString).setMaxResults(1);

			//qry.setParameter("point2", location);
			City result = (City) qry.uniqueResult();

			return result;
		    }
		});
	}

    public void createGISTIndexForShapeColumn() {
		 this.getHibernateTemplate().execute(
				 new HibernateCallback() {

				    public Object doInHibernate(Session session)
					    throws PersistenceException {
					session.flush();
					
					logger.info("will create GIST index for "+persistentClass.getSimpleName().toLowerCase()+" shape column");
					String shapeIndexName = "shapeIndex"+persistentClass.getSimpleName();
					logger.info("checking if "+shapeIndexName+" exists");
					String checkingShapeIndex= "SELECT 1 FROM   pg_class c  JOIN   pg_namespace n ON n.oid = c.relnamespace WHERE  c.relname = '"+shapeIndexName+"'";
					Query checkingShapeIndexQuery = session.createSQLQuery(checkingShapeIndex);
					Object shapeIndexExists = checkingShapeIndexQuery.uniqueResult();
					if (shapeIndexExists != null){
						logger.info("will create GIST index for  the "+OpenStreetMap.SHAPE_COLUMN_NAME+" column");
						String createIndex = "CREATE INDEX "+shapeIndexName+" ON "+persistentClass.getSimpleName().toLowerCase()+" USING GIST ("+GisFeature.SHAPE_COLUMN_NAME+")";  
						Query createIndexQuery = session.createSQLQuery(createIndex);
						createIndexQuery.executeUpdate();
					} else {
						logger.info("won't create GIST index for "+persistentClass.getSimpleName()+" because it already exists");
					}
					
					return null;
				    }
		
	});
	}

	public String getShapeAsWKTByFeatureId(final Long featureId) {
		if (featureId ==null){
			return null;
		}
		return (String) this.getHibernateTemplate().execute(
				new HibernateCallback() {

				    public Object doInHibernate(Session session)
					    throws PersistenceException {
					String queryString = "select ST_AsText("+GisFeature.SHAPE_COLUMN_NAME+") from " + persistentClass.getSimpleName()
			+ " as g where g.featureId= ?";

					Query qry = session.createQuery(queryString);
					qry.setParameter(0, featureId);
					qry.setCacheable(true);
					return (String) qry.uniqueResult();
				    }
				});
	}
    

    
}
