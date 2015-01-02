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
import org.hibernate.transform.Transformers;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.hibernate.projection._CityDTO;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.gisgraphy.test._DaoHelper;

public class NativeSQLOrderTest extends AbstractIntegrationHttpSolrTestCase {

    private ICityDao cityDao;

    private _DaoHelper testDao;

    @SuppressWarnings("unchecked")
    @Test
    public void testNativeSQLOrderShouldSortAscByDefault() {
	final City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F,
		2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);

	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(City.class);
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("name");
		Projection projection = Projections.property("featureId").as(
			"featureId");
		testCriteria.setProjection(projection).addOrder(
			new NativeSQLOrder("featureId")).setResultTransformer(
			Transformers.aliasToBean(_CityDTO.class));

		List<_CityDTO> results = testCriteria.list();
		return results;
	    }
	};

	List<_CityDTO> cities = (List<_CityDTO>) testDao
		.testCallback(hibernateCallback);
	assertEquals(3, cities.size());
	assertEquals("1", cities.get(0).getFeatureId().toString());
	assertEquals("2", cities.get(1).getFeatureId().toString());
	assertEquals("3", cities.get(2).getFeatureId().toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNativeSQLOrderShouldSortDesc() {
	final City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F,
		2.3333F, 1L);
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);

	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(City.class);
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("name");
		Projection projection = Projections.property("featureId").as(
			"featureId");
		testCriteria.setProjection(projection).addOrder(
			new NativeSQLOrder("featureId", false))
			.setResultTransformer(
				Transformers.aliasToBean(_CityDTO.class));

		List<_CityDTO> results = testCriteria.list();
		return results;
	    }
	};

	List<_CityDTO> cities = (List<_CityDTO>) testDao
		.testCallback(hibernateCallback);
	assertEquals(3, cities.size());
	assertEquals("3", cities.get(0).getFeatureId().toString());
	assertEquals("2", cities.get(1).getFeatureId().toString());
	assertEquals("1", cities.get(2).getFeatureId().toString());
    }

    @Test
    public void testToSqlStringCriteriaCriteriaQueryShouldReplaceAlias() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getSQLAlias((Criteria) EasyMock.anyObject()))
		.andReturn("sqlname").once();
	EasyMock.replay(criteriaQuery);
	NativeSQLOrder nso = new NativeSQLOrder("{alias}.name", true);
	String sqlString = nso.toSqlString(null, criteriaQuery);
	assertTrue(sqlString.contains("sqlname.name"));
	EasyMock.verify(criteriaQuery);
    }

    @Test
    public void testToSqlStringCriteriaCriteriaQueryShouldTakeTheAscOrderIntoAcount() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getSQLAlias((Criteria) EasyMock.anyObject()))
		.andReturn("sqlname").once();
	EasyMock.replay(criteriaQuery);
	NativeSQLOrder nso = new NativeSQLOrder("{alias}.name", true);
	String sqlString = nso.toSqlString(null, criteriaQuery);
	assertTrue(sqlString.contains("asc"));
	EasyMock.verify(criteriaQuery);
    }

    @Test
    public void testToSqlStringCriteriaCriteriaQueryShouldTakeTheDescOrderIntoAcount() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getSQLAlias((Criteria) EasyMock.anyObject()))
		.andReturn("sqlname").once();
	EasyMock.replay(criteriaQuery);
	NativeSQLOrder nso = new NativeSQLOrder("{alias}.name", false);
	String sqlString = nso.toSqlString(null, criteriaQuery);
	assertTrue(sqlString.contains("desc"));
	EasyMock.verify(criteriaQuery);
    }

    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    public void setTestDao(_DaoHelper testDao) {
	this.testDao = testDao;
    }

}
