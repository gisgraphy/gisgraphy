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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.valueobject.SRID;
import com.vividsolutions.jts.geom.Point;

/**
 * A data access object for {@link CitySubdivision} Object
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class CitySubdivisionDao extends GenericGisDao<CitySubdivision>
	implements ICitySubdivisionDao {
	
	public final static int BATCH_UPDATE_SIZE = 100;
	
	private ICityDao cityDao;
	
    /**
     * Default constructor
     */
    public CitySubdivisionDao() {
	super(CitySubdivision.class);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public CitySubdivision getNearestInCity(final Point location,final long cityId, final Float maxDistance){
		Assert.notNull(location);
		Assert.notNull(cityId);
		return (CitySubdivision) this.getHibernateTemplate().execute(new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
		    	//select name,municipality,source,openstreetmapid from city c 
		    	//where st_contains(c.shape,ST_GeometryFromText('POINT(2.349 48.868)',4326))=true limit 1
		    String pointAsString = "ST_GeometryFromText('POINT("+location.getX()+" "+location.getY()+")',"+SRID.WGS84_SRID.getSRID()+")";
			String queryString = "from " + persistentClass.getSimpleName()
				+ " as c where c.cityId= "+cityId;
				if (maxDistance !=null && maxDistance >0){
					queryString += " and ST_Distance_Sphere(c.location, "+pointAsString+" ) < "+maxDistance;
				}
			queryString = queryString+ " order by ST_Distance_Sphere(c.location, "+pointAsString+" )";
			//we need to sort by distance due to error in osm data 
			//eg : if we search for the nearest city of http://www.openstreetmap.org/way/27904415
			// we can have 2 cities : http://www.openstreetmap.org/way/75509282 vs http://www.openstreetmap.org/relation/388250
			//cause there is the city and the district

			Query qry = session.createQuery(queryString).setMaxResults(1);

			//qry.setParameter("point2", location);
			CitySubdivision result = (CitySubdivision) qry.uniqueResult();

			return result;
		    }
		});
	}
    
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.ICitySubdivisionDao#linkCitySubdivisionToTheirCity()
	 */
	public int linkCitySubdivisionToTheirCity(){
		int nbModify = 0;
		long nbCitySubdivision = count();
		for (int count=1;count<=nbCitySubdivision;count = count+BATCH_UPDATE_SIZE){
			//System.out.println("from = "+count+ " max result="+BATCH_UPDATE_SIZE);
			List<CitySubdivision> list = getAllPaginate(count, BATCH_UPDATE_SIZE);
			//System.out.println("found "+list.size());
			for (CitySubdivision citySubdivision:list){
				City city = cityDao.getByShape(citySubdivision.getLocation(), citySubdivision.getCountryCode(), false);
				if (city != null){
				citySubdivision.setIsIn(city.getName());
				citySubdivision.setCityId(city.getId());
				if (city.getAdm()!=null){
					citySubdivision.setIsInAdm(city.getAdm().getName());
				}
				save(citySubdivision);
				nbModify++;
				}
			}
		}
		return nbModify;
	}
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.ICitySubdivisionDao#getByShape(com.vividsolutions.jts.geom.Point, java.lang.String, boolean)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CitySubdivision getByShape(final Point location,final String countryCode) {
		Assert.notNull(location);
		return (CitySubdivision) this.getHibernateTemplate().execute(new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
		    	//select name,municipality,source,openstreetmapid from city c 
		    	//where st_contains(c.shape,ST_GeometryFromText('POINT(2.349 48.868)',4326))=true limit 1
		    String pointAsString = "ST_GeometryFromText('POINT("+location.getX()+" "+location.getY()+")',"+SRID.WGS84_SRID.getSRID()+")";
			String queryString = "from " + persistentClass.getSimpleName()
				+ " as c where st_contains(c.shape,"+pointAsString+")=true ";
			if (countryCode!=null ){
				queryString+=" and c.countryCode='"+countryCode+"'";
			}
			queryString = queryString+ " order by st_area(c.shape)";
			//we need to sort by shape due to error in osm data 
			//eg : if we search for the nearest city of http://www.openstreetmap.org/way/27904415
			// we can have 2 cities : http://www.openstreetmap.org/way/75509282 vs http://www.openstreetmap.org/relation/388250
			//cause there is the city and the district

			Query qry = session.createQuery(queryString).setMaxResults(1);

			//qry.setParameter("point2", location);
			CitySubdivision result = (CitySubdivision) qry.uniqueResult();

			return result;
		    }
		});
	}

	@Required
	public void setCityDao(ICityDao cityDao) {
		this.cityDao = cityDao;
	}
	
	

	


}
