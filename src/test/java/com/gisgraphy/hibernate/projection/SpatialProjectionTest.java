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
package com.gisgraphy.hibernate.projection;


import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.gisgraphy.test._DaoHelper;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class SpatialProjectionTest extends AbstractIntegrationHttpSolrTestCase {

    private ICityDao cityDao;
    
    private IOpenStreetMapDao openStreetMapDao;

    private _DaoHelper testDao;

    @SuppressWarnings("unchecked")
    @Test
    public void testdistance_pointToLine() {
		LineString shape = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.9154734)");
		
		OpenStreetMap streetOSM = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
		streetOSM.setShape(shape);
		openStreetMapDao.save(streetOSM);
		assertNotNull(openStreetMapDao.get(streetOSM.getId()));
		
		final Point p1 = GeolocHelper.createPoint(6.9412748F, 50.9155829F);

	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(OpenStreetMap.class);
		ProjectionList projection = Projections.projectionList().add(
			Projections.property("name").as("name")).add(
			SpatialProjection.distance_pointToLine(p1,OpenStreetMap.SHAPE_COLUMN_NAME).as(
				"distance")).add(Projections.property("shape").as("shape"));
		// remove the from point
				testCriteria.setProjection(projection);
		testCriteria.setResultTransformer(Transformers
			.aliasToBean(_OpenstreetmapDTO.class));

		List<_OpenstreetmapDTO> results = testCriteria.list();
		return results;
	    }
	};

	List<_OpenstreetmapDTO> streets = (List<_OpenstreetmapDTO>) testDao
		.testCallback(hibernateCallback);
	assertEquals(1, streets.size());
	Double calculatedDist = 14.76D;
	Double retrieveDistance = streets.get(0).getDistance();
	double percent = (Math.abs(calculatedDist - retrieveDistance) * 100)
		/ Math.min(retrieveDistance, calculatedDist);
	assertTrue("There is more than one percent of error beetween the calculated distance ("+calculatedDist+") and the retrieved one ("+retrieveDistance+")",percent < 1);

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testDistance_sphere() {
	final City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F,
		2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);

	this.cityDao.save(p1);
	this.cityDao.save(p2);

	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(City.class);
		ProjectionList projection = Projections.projectionList().add(
			Projections.property("name").as("name")).add(
			SpatialProjection.distance_sphere(p1.getLocation(),GisFeature.LOCATION_COLUMN_NAME).as(
				"distance"));
		// remove the from point
		testCriteria.add(Restrictions.ne("id", p1.getId()))
			.setProjection(projection);
		testCriteria.setResultTransformer(Transformers
			.aliasToBean(_CityDTO.class));

		List<_CityDTO> results = testCriteria.list();
		return results;
	    }
	};

	List<_CityDTO> cities = (List<_CityDTO>) testDao
		.testCallback(hibernateCallback);
	assertEquals(1, cities.size());
	assertEquals("bordeaux", cities.get(0).getName());
	Double calculatedDist = p1.distanceTo(p2.getLocation());
	Double retrieveDistance = cities.get(0).getDistance();
	double percent = (Math.abs(calculatedDist - retrieveDistance) * 100)
		/ Math.min(retrieveDistance, calculatedDist);
	assertTrue("There is more than one percent of error beetween the calculated distance ("+calculatedDist+") and the retrieved one ("+retrieveDistance+")",percent < 1);

    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testDistance(){
	float x1 = -2f;
	float y1 = 2f;
	float x2 = 2f;
	float y2 = 2f;
	Point locationParis = GeolocHelper.createPoint(x1, y1);
	//locationParis.setSRID(-1);
	Point locationbordeaux = GeolocHelper.createPoint(x2, y2);
	//locationbordeaux.setSRID(-1);
	
	final City p1 = GisgraphyTestHelper.createCity("paris", 0F,
		0F, 1L);
	p1.setLocation(locationParis);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 0F, 0F,
		3L);
	p2.setLocation(locationbordeaux);

	this.cityDao.save(p1);
	this.cityDao.save(p2);

	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(City.class);
		ProjectionList projection = Projections.projectionList().add(
			Projections.property("name").as("name")).add(
			SpatialProjection.distance(p1.getLocation(),GisFeature.LOCATION_COLUMN_NAME).as(
				"distance"));
		// remove the from point
		testCriteria.add(Restrictions.ne("id", p1.getId()))
			.setProjection(projection);
		testCriteria.setResultTransformer(Transformers
			.aliasToBean(_CityDTO.class));

		List<_CityDTO> results = testCriteria.list();
		return results;
	    }
	};

	List<_CityDTO> cities = (List<_CityDTO>) testDao
		.testCallback(hibernateCallback);
	assertEquals(1, cities.size());
	assertEquals("bordeaux", cities.get(0).getName());
	Double calculatedDist = Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));//cartesian distance
	Double retrieveDistance = cities.get(0).getDistance();
	double percent = (Math.abs(calculatedDist - retrieveDistance) * 100)
		/ Math.min(retrieveDistance, calculatedDist);
	assertTrue("There is more than one percent of error beetween the calculated distance ("+calculatedDist+") and the retrieved one ("+retrieveDistance+")",percent < 1);
    }

    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    public void setTestDao(_DaoHelper testDao) {
	this.testDao = testDao;
    }

    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }

}
