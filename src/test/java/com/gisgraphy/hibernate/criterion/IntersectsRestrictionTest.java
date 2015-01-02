/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.hibernate.criterion;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.transform.Transformers;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.SRID;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.hibernate.projection.ProjectionBean;
import com.gisgraphy.hibernate.projection._OpenstreetmapDTO;
import com.gisgraphy.street.StreetType;
import com.gisgraphy.test._DaoHelper;
import com.vividsolutions.jts.geom.LineString;


public class IntersectsRestrictionTest extends AbstractIntegrationHttpSolrTestCase{
    
    private _DaoHelper testDao;

    private IOpenStreetMapDao openStreetMapDao;
    
    private OpenStreetMap createAndSaveStreet(){
	//we create a multilineString a little bit closest than the first one 
	OpenStreetMap street = new OpenStreetMap();
	LineString shape2 = GeolocHelper.createLineString("LINESTRING (6.9416088 50.9154239,6.9410001 50.9154734)");
	shape2.setSRID(SRID.WGS84_SRID.getSRID());
	
	
	street.setShape(shape2);
	street.setGid(2L);
	//Simulate middle point
	street.setLocation(GeolocHelper.createPoint(6.94130445F , 50.91544865F));
	street.setOneWay(false);
	street.setStreetType(StreetType.FOOTWAY);
	street.setOpenstreetmapId(1234L);
	street.setName("John Kenedy");
	StringHelper.updateOpenStreetMapEntityForIndexation(street);
	openStreetMapDao.save(street);
	assertNotNull(openStreetMapDao.get(street.getId()));
	return street;
    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testPartialWordRestriction() {
	OpenStreetMap streetOSM = createAndSaveStreet();
	
	HibernateCallback hibernateCallbackSuccess = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(OpenStreetMap.class);
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("name");
		fieldList.add("gid");
		
		Projection projection = ProjectionBean.fieldList(fieldList, true);
		testCriteria.setProjection(projection).add(
			new IntersectsRestriction(OpenStreetMap.SHAPE_COLUMN_NAME, GeolocHelper.createPolygonBox(6.94130445F , 50.91544865F, 10000)))
			.setResultTransformer(
				Transformers.aliasToBean(_OpenstreetmapDTO.class));

		List<_OpenstreetmapDTO> results = testCriteria.list();
		return results;
	    }
	};

	List<_OpenstreetmapDTO> streets = (List<_OpenstreetmapDTO>) testDao
		.testCallback(hibernateCallbackSuccess);
	assertEquals(
		"According to the intersects restriction, it should have a result ",
		1, streets.size());
	
	assertEquals(
		"According to the intersects restriction, the result is incorrect",
		streetOSM.getGid(), streets.get(0).getGid());
	
	HibernateCallback hibernateCallbackFail = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(OpenStreetMap.class);
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("name");
		fieldList.add("gid");
		
		Projection projection = ProjectionBean.fieldList(fieldList, true);
		testCriteria.setProjection(projection).add(
			new IntersectsRestriction(OpenStreetMap.SHAPE_COLUMN_NAME, GeolocHelper.createPolygonBox(7.94130445F , 51.91544865F, 10000)))
			.setResultTransformer(
				Transformers.aliasToBean(_OpenstreetmapDTO.class));

		List<_OpenstreetmapDTO> results = testCriteria.list();
		return results;
	    }
	};

	streets = (List<_OpenstreetmapDTO>) testDao
	.testCallback(hibernateCallbackFail);
assertEquals(
	"According to the intersects restriction, it should have no result ",
	0, streets.size());

    }
    
    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }

    public void setTestDao(_DaoHelper testDao) {
	this.testDao = testDao;
    }

}
