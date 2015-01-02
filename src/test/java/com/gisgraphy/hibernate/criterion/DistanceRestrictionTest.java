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
package com.gisgraphy.hibernate.criterion;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.hibernate.projection._CityDTO;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.gisgraphy.test._DaoHelper;

public class DistanceRestrictionTest extends
	AbstractIntegrationHttpSolrTestCase {

    private ICityDao cityDao;

    private _DaoHelper testDao;


    @Test
    public void testDistanceRestrictionPointDoubleBooleanShouldUseIndexIfUseIndexTrue() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getColumn((Criteria) EasyMock.anyObject(),
			EasyMock.eq(GisFeature.LOCATION_COLUMN_NAME)))
		.andReturn("").once();
	EasyMock.expect(
		criteriaQuery.getSQLAlias((Criteria) EasyMock.anyObject()))
		.andReturn("").once();
	EasyMock.replay(criteriaQuery);
	DistanceRestriction dr = new DistanceRestriction(GisgraphyTestHelper
		.createPoint(3F, 4F), 4D, true);
	String sqlString = dr.toSqlString(null, criteriaQuery);
	assertTrue(sqlString.contains(" && "));
	EasyMock.verify(criteriaQuery);
    }

    @Test
    public void testDistanceRestrictionPointDoubleBooleanShouldNotUseIndexIfUseIndexFalse() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getColumn((Criteria) EasyMock.anyObject(),
			EasyMock.eq(GisFeature.LOCATION_COLUMN_NAME)))
		.andReturn(" test ").once();
	EasyMock.replay(criteriaQuery);
	DistanceRestriction dr = new DistanceRestriction(GisgraphyTestHelper
		.createPoint(3F, 4F), 4D, false);
	String sqlString = dr.toSqlString(null, criteriaQuery);
	assertTrue(!sqlString.contains(" && "));
	EasyMock.verify(criteriaQuery);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDistanceRestrictionPointDouble() {
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
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("name");
		Projection projection = Projections.property("name").as("name");
		testCriteria.setProjection(projection).add(
			Restrictions.ne("id", p1.getId())).add(
			new DistanceRestriction(p1.getLocation(), 500000D))
			.setResultTransformer(
				Transformers.aliasToBean(_CityDTO.class));

		List<_CityDTO> results = testCriteria.list();
		return results;
	    }
	};

	List<_CityDTO> cities = (List<_CityDTO>) testDao
		.testCallback(hibernateCallback);
	assertEquals(
		"According to the distance restriction, it should have zero result",
		0, cities.size());

	hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(City.class);
		Projection projection = Projections.property("name").as("name");
		testCriteria.setProjection(projection).add(
			new DistanceRestriction(p1.getLocation(), 600000D))
			.add(Restrictions.ne("id", p1.getId()))
			.setResultTransformer(
				Transformers.aliasToBean(_CityDTO.class));

		List<_CityDTO> results = testCriteria.list();
		return results;
	    }
	};

	cities = (List<_CityDTO>) testDao.testCallback(hibernateCallback);
	assertEquals(
		"According to the distance restriction, it should have one results",
		1, cities.size());
	assertEquals("bordeaux", cities.get(0).getName());

    }
    
    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    public void setTestDao(_DaoHelper testDao) {
	this.testDao = testDao;
    }

}
