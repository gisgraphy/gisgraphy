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

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.vividsolutions.jts.geom.Point;

/**
 * A data access object for {@link GisFeature}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class GisFeatureDao extends GenericGisDao<GisFeature> implements
	IGisFeatureDao {

    /**
     * Default Constructor
     */
    public GisFeatureDao() {
	super(GisFeature.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisFeatureDao#listAllFeaturesFromText(java.lang.String,
     *      boolean)
     */
    public List<GisFeature> listAllFeaturesFromText(String name,
	    boolean includeAlternateNames) {
	return listFromText(name, includeAlternateNames, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisFeatureDao#getNearestAndDistanceFrom(com.gisgraphy.domain.geoloc.entity.GisFeature,
     *      double, int, int, java.lang.Class)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    GisFeature gisFeature, double distance, int firstResult,
	    int maxResults,
	    boolean includeDistanceField,
	    Class<? extends GisFeature> requiredClass, boolean isMunicipality) {
	Assert.notNull(gisFeature, "can not get nearest for a null gisFeature");
	return getNearestAndDistanceFrom(gisFeature.getLocation(), gisFeature
		.getId(), distance, firstResult, maxResults, includeDistanceField, requiredClass, isMunicipality);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisFeatureDao#getNearestAndDistanceFrom(com.gisgraphy.domain.geoloc.entity.GisFeature,
     *      double, java.lang.Class)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFromGisFeature(
	    GisFeature gisFeature, double distance,
	    boolean includeDistanceField,
	    Class<? extends GisFeature> requiredClass) {
	Assert.notNull(gisFeature, "can not get nearest for a null gisFeature");
	return getNearestAndDistanceFromGisFeature(gisFeature, distance, -1,
		-1, includeDistanceField,requiredClass, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisFeatureDao#getNearestAndDistanceFrom(com.vividsolutions.jts.geom.Point,
     *      double, int, int, java.lang.Class)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(Point point,
	    double distance, int firstResult, int maxResults,
	    boolean includeDistanceField,
	    Class<? extends GisFeature> requiredClass) {
	return getNearestAndDistanceFrom(point, 0L, distance, firstResult,
		maxResults, includeDistanceField, requiredClass, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisFeatureDao#getNearestAndDistanceFrom(com.vividsolutions.jts.geom.Point,
     *      double, java.lang.Class)
     */
    public List<GisFeatureDistance> getNearestAndDistanceFrom(Point point,
	    double distance, boolean includeDistanceField, Class<? extends GisFeature> requiredClass) {
	return getNearestAndDistanceFrom(point, distance, -1, -1, includeDistanceField, requiredClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IGisFeatureDao#deleteAllExceptAdms()
     */
    public int deleteAllExceptAdmsAndCountries() {
	return ((Integer) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "delete from "
				+ persistentClass.getSimpleName()
				+ " as g where (g.featureCode NOT IN ('ADM1','ADM2','ADM3','ADM4') OR g.featureCode is null) AND g.featureId NOT IN (select featureId from Country)  ";

			Query qry = session.createQuery(queryString);
			// Need to flush to avoid optimisticLock exception
			session.flush();
			session.clear();
			qry.setCacheable(false);

			return Integer.valueOf(qry.executeUpdate());

		    }
		})).intValue();

    }
    
  
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.repository.IGisFeatureDao#getMaxFeatureId()
     */
    public long getMaxFeatureId(){
	return (Long) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "select max(featureId) from "
				+ GisFeature.class.getSimpleName();

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);
			Long count = (Long) qry.uniqueResult();
			return count==null?0:count;
		    }
		});
    }

}
