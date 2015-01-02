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

import static com.gisgraphy.hibernate.projection.SpatialProjection.DISTANCE_SPHERE_FUNCTION;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.gisgraphy.GisgraphyException;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.geoloc.entity.event.EventManager;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeletedEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureStoredEvent;
import com.gisgraphy.domain.geoloc.entity.event.PlaceTypeDeleteAllEvent;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.SRID;
import com.gisgraphy.domain.valueobject.StreetDistance;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.IntrospectionHelper;
import com.gisgraphy.hibernate.criterion.DistanceRestriction;
import com.gisgraphy.hibernate.criterion.FulltextRestriction;
import com.gisgraphy.hibernate.criterion.IntersectsRestriction;
import com.gisgraphy.hibernate.criterion.NativeSQLOrder;
import com.gisgraphy.hibernate.criterion.ProjectionOrder;
import com.gisgraphy.hibernate.criterion.ResultTransformerUtil;
import com.gisgraphy.hibernate.projection.ProjectionBean;
import com.gisgraphy.hibernate.projection.SpatialProjection;
import com.gisgraphy.street.IStreetFactory;
import com.gisgraphy.street.StreetFactory;
import com.gisgraphy.street.StreetSearchMode;
import com.gisgraphy.street.StreetType;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * A data access object for {@link OpenStreetMap} Object
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class OpenStreetMapDao extends GenericDao<OpenStreetMap, Long> implements IOpenStreetMapDao
{
	private IStreetFactory streetFactory = new StreetFactory();
	 
	 private EventManager eventManager;
	
	/**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(OpenStreetMapDao.class);

	protected static final int DEFAULT_DISTANCE = 500;
	
    /**
     * Default constructor
     */
    public OpenStreetMapDao() {
	    super(OpenStreetMap.class);
    }
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#getNearestAndDistanceFrom(com.vividsolutions.jts.geom.Point, double, int, int, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<StreetDistance> getNearestAndDistanceFrom(
	    final Point point, final double distance,
	    final int firstResult, final int maxResults,
	    final StreetType streetType, final Boolean oneWay ,final String name, final StreetSearchMode streetSearchMode,final boolean includeDistanceField) {
	if (streetSearchMode==StreetSearchMode.FULLTEXT && !GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE){
		throw new GisgraphyException("The fulltext mode has been removed in gisgraphy v 3.0 and has been replaced by fulltext webservice with placetype=street. please Consult user guide.");
	}
	if (name != null && streetSearchMode==null){
		throw new IllegalArgumentException("streetSearchmode can not be null if name is provided");
	}
	if (point == null && streetSearchMode==StreetSearchMode.CONTAINS){
		throw new IllegalArgumentException("you must specify lat/lng when streetsearchmode = "+StreetSearchMode.CONTAINS);
	}
	return (List<StreetDistance>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			Criteria criteria = session
				.createCriteria(OpenStreetMap.class);
			
			List<String> fieldList = IntrospectionHelper
				.getFieldsAsList(OpenStreetMap.class);

			ProjectionList projections = ProjectionBean.fieldList(
				fieldList,false);
				if (includeDistanceField && point!=null){
				projections.add(
//				SpatialProjection.distance_sphere(point, GisFeature.LOCATION_COLUMN_NAME).as(
//					"distance"));
						SpatialProjection.distance_pointToLine(point, OpenStreetMap.SHAPE_COLUMN_NAME).as(
						"distance"));
				}
			criteria.setProjection(projections);
			if (includeDistanceField && point !=null){
			    criteria.addOrder(new ProjectionOrder("distance"));
			}
			if (maxResults > 0) {
			    criteria = criteria.setMaxResults(maxResults);
			}
			if (firstResult >= 1) {
			    criteria = criteria.setFirstResult(firstResult - 1);
			}
			if (point!=null){
			    Polygon polygonBox = GeolocHelper.createPolygonBox(point.getX(), point.getY(), distance);
			    criteria = criteria.add(new IntersectsRestriction(OpenStreetMap.SHAPE_COLUMN_NAME, polygonBox));
			}
			if (name != null) {
					if (streetSearchMode==StreetSearchMode.CONTAINS){
					    	criteria = criteria.add(Restrictions.isNotNull("name"));//optimisation!
					    	criteria = criteria.add(Restrictions.ilike(OpenStreetMap.FULLTEXTSEARCH_PROPERTY_NAME, "%"+name+"%"));
					    	//criteria = criteria.add(new PartialWordSearchRestriction(OpenStreetMap.PARTIALSEARCH_VECTOR_COLUMN_NAME, name));
					} else if (streetSearchMode == StreetSearchMode.FULLTEXT){
						  criteria = criteria.add(new FulltextRestriction(OpenStreetMap.FULLTEXTSEARCH_VECTOR_PROPERTY_NAME, name));
					} else {
						throw new NotImplementedException(streetSearchMode+" is not implemented for street search");
					}
			}
			if (streetType != null) {
			    criteria = criteria.add(Restrictions.eq("streetType",streetType));
			}
			if (oneWay != null) {
			    criteria = criteria.add(Restrictions.eq("oneWay",oneWay));
			}
			criteria.setCacheable(true);
			// List<Object[]> queryResults =testCriteria.list();
			List<?> queryResults = criteria.list();
			
			if (queryResults != null && queryResults.size()!=0){
			    String[] propertiesNameArray ;
			    if (includeDistanceField && point!=null){
			propertiesNameArray = (String[]) ArrayUtils
			    	.add(
			    		IntrospectionHelper
			    			.getFieldsAsArray(OpenStreetMap.class),
			    		"distance");
			    } else  {
				propertiesNameArray = IntrospectionHelper
	    			.getFieldsAsArray(OpenStreetMap.class);
			    }
			List<StreetDistance> results = ResultTransformerUtil
				.transformToStreetDistance(
					propertiesNameArray,
					queryResults);
			return results;
			} else {
			    return new ArrayList<StreetDistance>();
			}
			
		    }
		});
    }
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#getByGid(java.lang.Long)
     */
    public OpenStreetMap getByGid(final Long gid) {
	Assert.notNull(gid);
	return (OpenStreetMap) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ OpenStreetMap.class.getSimpleName()
				+ " as c where c.gid= ?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, gid);

			OpenStreetMap result = (OpenStreetMap) qry.uniqueResult();
			return result;
		    }
		});
    }

    
  
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#buildIndexForStreetNameSearch()
     */
    public Integer updateTS_vectorColumnForStreetNameSearch() {
	return (Integer) this.getHibernateTemplate().execute(
			 new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				session.flush();
				logger.info("will update "+OpenStreetMap.FULLTEXTSEARCH_VECTOR_PROPERTY_NAME.toLowerCase()+" field");
				String updateFulltextField = "UPDATE openStreetMap SET "+OpenStreetMap.FULLTEXTSEARCH_VECTOR_PROPERTY_NAME.toLowerCase()+" = to_tsvector('simple',coalesce("+OpenStreetMap.FULLTEXTSEARCH_COLUMN_NAME+",'')) where name is not null";  
				Query qryUpdateFulltextField = session.createSQLQuery(updateFulltextField);
				int numberOfLineUpdatedForFulltext = qryUpdateFulltextField.executeUpdate();
				int numberOfLineUpdatedForPartial = 0;
				return Integer.valueOf(numberOfLineUpdatedForFulltext + numberOfLineUpdatedForPartial);
				
			    }
			});
    }
    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#buildIndexForStreetNameSearch()
     */
    public Integer updateTS_vectorColumnForStreetNameSearchPaginate(final long from,final long to ) {
	return (Integer) this.getHibernateTemplate().execute(
			 new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				session.flush();
				logger.info("will update "+OpenStreetMap.FULLTEXTSEARCH_VECTOR_PROPERTY_NAME.toLowerCase()+" field");
				String updateFulltextField = "UPDATE openStreetMap SET "+OpenStreetMap.FULLTEXTSEARCH_VECTOR_PROPERTY_NAME.toLowerCase()+" = to_tsvector('simple',coalesce("+OpenStreetMap.FULLTEXTSEARCH_COLUMN_NAME+",'')) where gid >= "+from+" and gid <= "+to+" and name is not null";  
				Query qryUpdateFulltextField = session.createSQLQuery(updateFulltextField);
				int numberOfLineUpdatedForFulltext = qryUpdateFulltextField.executeUpdate();
				int numberOfLineUpdatedForPartial = 0;
				return Integer.valueOf(numberOfLineUpdatedForFulltext + numberOfLineUpdatedForPartial);
				
			    }
			});
    }


    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#createGISTIndex()
     */
    public void createSpatialIndexes() {
	 this.getHibernateTemplate().execute(
			 new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				session.flush();

				String locationIndexName = OpenStreetMap.LOCATION_COLUMN_NAME.toLowerCase()+"indexopenstreetmap";
				logger.info("checking if "+locationIndexName+" exists");
				String checkingLocationIndex= "SELECT 1 FROM   pg_class c  JOIN   pg_namespace n ON n.oid = c.relnamespace WHERE  c.relname = '"+locationIndexName+"'";
				Query checkingLocationIndexQuery = session.createSQLQuery(checkingLocationIndex);
				Object locationIndexExists = checkingLocationIndexQuery.uniqueResult();
				if (locationIndexExists != null){
					logger.info("will create GIST index for  the "+OpenStreetMap.LOCATION_COLUMN_NAME+" column");
					String createIndexForLocation = "CREATE INDEX "+locationIndexName+" ON openstreetmap USING GIST ("+OpenStreetMap.LOCATION_COLUMN_NAME.toLowerCase()+")";  
					Query qryUpdateLocationIndex = session.createSQLQuery(createIndexForLocation);
					qryUpdateLocationIndex.executeUpdate();
				} else {
					logger.info("won't create GIST index for  the "+OpenStreetMap.LOCATION_COLUMN_NAME+" column because it already exists");
				}
				
				String shapeIndexName=OpenStreetMap.SHAPE_COLUMN_NAME.toLowerCase()+"indexopenstreetmap";
				logger.info("checking if "+shapeIndexName+" exists");
				String checkingShapeIndex= "SELECT 1 FROM   pg_class c  JOIN   pg_namespace n ON n.oid = c.relnamespace WHERE  c.relname = '"+shapeIndexName+"'";
				Query checkingShapeIndexQuery = session.createSQLQuery(checkingShapeIndex);
				Object shapeIndexExists = checkingShapeIndexQuery.uniqueResult();
				if (shapeIndexExists!=null){
					logger.info("will create GIST index for  the "+OpenStreetMap.SHAPE_COLUMN_NAME+" column");
					String createIndexForShape = "CREATE INDEX "+OpenStreetMap.SHAPE_COLUMN_NAME.toLowerCase()+"indexopenstreetmap ON openstreetmap USING GIST ("+OpenStreetMap.SHAPE_COLUMN_NAME.toLowerCase()+")";  
					Query qryUpdateShapeIndex = session.createSQLQuery(createIndexForShape);
					qryUpdateShapeIndex.executeUpdate();
				} else {
					logger.info("won't create GIST index for  the "+OpenStreetMap.SHAPE_COLUMN_NAME+" column because it already exists");
				}
				return null;
			    }
			});
   }
    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#createGISTIndex()
     */
    public void createFulltextIndexes() {
	 this.getHibernateTemplate().execute(
			 new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				session.flush();
				logger.info("will create Fulltext index");
				String createFulltextIndex = "CREATE INDEX "+OpenStreetMap.FULLTEXTSEARCH_VECTOR_PROPERTY_NAME.toLowerCase()+"indexopenstreetmap ON openstreetmap USING gin("+OpenStreetMap.FULLTEXTSEARCH_VECTOR_PROPERTY_NAME.toLowerCase()+")";  
				Query fulltextIndexQuery = session.createSQLQuery(createFulltextIndex);
				fulltextIndexQuery.executeUpdate();
				
				return null;
			    }
			});
   }
    

    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#countEstimate()
     */
    public long countEstimate(){
	return (Long) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "select max(gid)-min(gid)+1 from "
				+ persistentClass.getSimpleName();

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);
			Long count = (Long) qry.uniqueResult();
			return count==null?0:count;
		    }
		});
    }
    
    @Override
    public OpenStreetMap save(OpenStreetMap openStreetMap) {
	OpenStreetMap savedEntity = super.save(openStreetMap);
	Street street = streetFactory.create(savedEntity);
	GisFeatureStoredEvent CreatedEvent = new GisFeatureStoredEvent(
		street);
	eventManager.handleEvent(CreatedEvent);
	return savedEntity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericDao#remove(java.lang.Object)
     */
    @Override
    public void remove(OpenStreetMap openStreetMap) {
	super.remove(openStreetMap);
	Street street = streetFactory.create(openStreetMap);
	GisFeatureDeletedEvent gisFeatureDeletedEvent = new GisFeatureDeletedEvent(
		street);
	eventManager.handleEvent(gisFeatureDeletedEvent);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericDao#deleteAll()
     */
    @Override
    public int deleteAll() {
	int numberOfOpenStreetMapDeleted = super.deleteAll();
	PlaceTypeDeleteAllEvent placeTypeDeleteAllEvent = new PlaceTypeDeleteAllEvent(
		Street.class);
	eventManager.handleEvent(placeTypeDeleteAllEvent);
	return numberOfOpenStreetMapDeleted;
    }
    

	public OpenStreetMap getByOpenStreetMapId(final Long openstreetmapId) {
		Assert.notNull(openstreetmapId);
		return (OpenStreetMap) this.getHibernateTemplate().execute(
			new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				String queryString = "from "
					+ OpenStreetMap.class.getSimpleName()
					+ " as c where c.openstreetmapId= ?";

				Query qry = session.createQuery(queryString);
				qry.setMaxResults(1);
				//we need to limit to 1 because a street can be in two countries
				qry.setCacheable(true);

				qry.setParameter(0, openstreetmapId);

				OpenStreetMap result = (OpenStreetMap) qry.uniqueResult();
				return result;
			    }
			});
	}


	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#getMaxOpenstreetMapId()
	 */
	public long getMaxOpenstreetMapId() {
			return (Long) this.getHibernateTemplate().execute(
				new HibernateCallback() {

				    public Object doInHibernate(Session session)
					    throws PersistenceException {
					String queryString = "select max(openstreetmapId) from "
						+ OpenStreetMap.class.getSimpleName();

					Query qry = session.createQuery(queryString);
					qry.setCacheable(true);
					Long count = (Long) qry.uniqueResult();
					return count==null?0:count;
				    }
				});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OpenStreetMap getNearestByosmIds(final Point point,final List<Long> ids) {
		if (ids==null || ids.size()==0){
			return null;
		}
		return (OpenStreetMap) this.getHibernateTemplate().execute(
				new HibernateCallback() {

				    public Object doInHibernate(Session session)
					    throws PersistenceException {
				    	
				    	Criteria criteria = session
								.createCriteria(OpenStreetMap.class);
							
							criteria.add(new DistanceRestriction(point,DEFAULT_DISTANCE,true));
							criteria.add(Restrictions.in("openstreetmapId", ids));
							
							String pointAsString = "ST_GeometryFromText('POINT("+point.getX()+" "+point.getY()+")',"+SRID.WGS84_SRID.getSRID()+")";
							String distanceCondition = new StringBuffer()
							.append(DISTANCE_SPHERE_FUNCTION)
							.append("(")
								.append(pointAsString)
								.append(",")
								.append(SpatialProjection.ST_CLOSEST_POINT)
								.append("(")
									.append("this_.").append(OpenStreetMap.SHAPE_COLUMN_NAME)
									.append(",")
									.append(pointAsString)
								.append(")")
							.append(")")
							.toString();
							criteria.addOrder(new NativeSQLOrder(distanceCondition));
							criteria = criteria.setMaxResults(1);
							criteria.setCacheable(true);
							// List<Object[]> queryResults =testCriteria.list();
							OpenStreetMap openStreetMap = (OpenStreetMap)criteria.uniqueResult();
							
							return openStreetMap;
							
						    }
						});
		    }
		    
	
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IOpenStreetMapDao#getMaxOpenstreetMapId()
	 */
	public long getMaxGid() {
			return (Long) this.getHibernateTemplate().execute(
				new HibernateCallback() {

				    public Object doInHibernate(Session session)
					    throws PersistenceException {
					String queryString = "select max(gid) from "
						+ OpenStreetMap.class.getSimpleName();

					Query qry = session.createQuery(queryString);
					qry.setCacheable(true);
					Long count = (Long) qry.uniqueResult();
					return count==null?0:count;
				    }
				});
	}



    @Autowired
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }


    public void setStreetFactory(IStreetFactory streetFactory) {
        this.streetFactory = streetFactory;
    }
    
    public OpenStreetMap getNearestRoadFrom(
    	    final Point point) {
    	return getNearestFrom(point,true,true);
    
    }
    
    public OpenStreetMap getNearestFrom(
    	    final Point point) {
    	return getNearestFrom(point,false,true);
    }
    
 
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public OpenStreetMap getNearestFrom(
	    final Point point,final boolean onlyroad,final boolean filterEmptyName) {
    	if (point==null){
    		return null;
    	}
	return (OpenStreetMap) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
		    	
		    	Criteria criteria = session
						.createCriteria(OpenStreetMap.class);
					
					criteria.add(new DistanceRestriction(point,DEFAULT_DISTANCE,true));
					if (onlyroad) {
						criteria = criteria.add(Restrictions.ne("streetType",StreetType.FOOTWAY));
					}
					if (filterEmptyName){
						criteria = criteria.add(Restrictions.isNotNull("name"));
					}
					
					String pointAsString = "ST_GeometryFromText('POINT("+point.getX()+" "+point.getY()+")',"+SRID.WGS84_SRID.getSRID()+")";
					String distanceCondition = new StringBuffer()
					.append(DISTANCE_SPHERE_FUNCTION)
					.append("(")
						.append(pointAsString)
						.append(",")
						.append(SpatialProjection.ST_CLOSEST_POINT)
						.append("(")
							.append("this_.").append(OpenStreetMap.SHAPE_COLUMN_NAME)
							.append(",")
							.append(pointAsString)
						.append(")")
					.append(")")
					.toString();
					criteria.addOrder(new NativeSQLOrder(distanceCondition));
					criteria = criteria.setMaxResults(1);
					criteria.setCacheable(true);
					// List<Object[]> queryResults =testCriteria.list();
					OpenStreetMap openStreetMap = (OpenStreetMap)criteria.uniqueResult();
					
					return openStreetMap;
					
				    }
				});
    }
    
    
    public String getShapeAsWKTByGId(final Long gid) {
		if (gid ==null){
			return null;
		}
		return (String) this.getHibernateTemplate().execute(
				new HibernateCallback() {

				    public Object doInHibernate(Session session)
					    throws PersistenceException {
					String queryString = "select ST_AsText("+GisFeature.SHAPE_COLUMN_NAME+") from " + persistentClass.getSimpleName()
			+ " as o where o.gid=?";

					Query qry = session.createQuery(queryString);
					qry.setParameter(0, gid);
					qry.setCacheable(true);
					return (String) qry.uniqueResult();
				    }
				});
	}



}
