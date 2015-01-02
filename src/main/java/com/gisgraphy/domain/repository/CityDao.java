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
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.valueobject.SRID;
import com.vividsolutions.jts.geom.Point;

/**
 * A data access object for {@link City}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class CityDao extends GenericGisDao<City> implements ICityDao {

    /**
     * Default constructor
     */
    public CityDao() {
	super(City.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ICityDao#ListByZipCode(int,
     *      java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<City> listByZipCode(final String zipcode, final String countrycode) {
	return (List<City>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ City.class.getSimpleName()
				+ " as c left outer join c.zipCodes z where z.code = ?";
			if (countrycode != null) {
			    queryString = queryString + " and c.countryCode=?";
			}

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, zipcode);
			if (countrycode != null) {
			    qry.setParameter(1, countrycode.toUpperCase());
			}

			List<City> results = (List<City>) qry.list();
			if (results == null) {
			    results = new ArrayList<City>();
			}
			return results;
		    }
		});
    }
    
   
    

	public City getByShape(final Point location,final String countryCode,final boolean filterMunicipality) {
		Assert.notNull(location);
		return (City) this.getHibernateTemplate().execute(new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
		    	//select name,municipality,source,openstreetmapid from city c 
		    	//where st_contains(c.shape,ST_GeometryFromText('POINT(2.349 48.868)',4326))=true limit 1
		    String pointAsString = "ST_GeometryFromText('POINT("+location.getX()+" "+location.getY()+")',"+SRID.WGS84_SRID.getSRID()+")";
			String queryString = "from " + persistentClass.getSimpleName()
				+ " as c where st_contains(c.shape,"+pointAsString+")=true ";
			if (filterMunicipality){
				queryString+=" and c.municipality=true";
			}
			if (countryCode!=null ){
				queryString+=" and c.countryCode='"+countryCode+"'";
			}
			queryString = queryString+ " order by st_area(c.shape)";
			//we need to sort by distance due to error in osm data 
			//eg : if we search for the nearest city of http://www.openstreetmap.org/way/27904415
			// we can have 2 cities : http://www.openstreetmap.org/way/75509282 vs http://www.openstreetmap.org/relation/388250
			//cause there is the city and the district

			Query qry = session.createQuery(queryString).setMaxResults(1);

			//qry.setParameter("point2", location);
			City result = (City) qry.uniqueResult();

			return result;
		    }
		});
	}

	
}
