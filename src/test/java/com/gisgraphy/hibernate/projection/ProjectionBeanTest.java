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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.gisgraphy.test._DaoHelper;

public class ProjectionBeanTest extends AbstractIntegrationHttpSolrTestCase {

    private _DaoHelper testDao;

    private ICityDao cityDao;

    @SuppressWarnings("unchecked")
    @Test
    public void testPropertiesList() {

	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);

	this.cityDao.save(p1);
	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		Criteria testCriteria = session.createCriteria(City.class);
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("name");
		fieldList.add("featureId");
		ProjectionList projection = ProjectionBean.fieldList(fieldList,
			true);
		testCriteria.setProjection(projection);
		testCriteria.setResultTransformer(Transformers
			.aliasToBean(City.class));

		List<City> results = testCriteria.list();
		return results;
	    }
	};

	List<City> cities = (List<City>) testDao
		.testCallback(hibernateCallback);
	assertEquals(1, cities.size());
	assertEquals("paris", cities.get(0).getName());
	assertEquals("1", cities.get(0).getFeatureId() + "");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBeanFieldList() {

	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);

	this.cityDao.save(p1);
	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		try {
		    Criteria testCriteria = session.createCriteria(City.class);
		    String[] ignoreFields = { "distance" };
		    ProjectionList projection = ProjectionBean.beanFieldList(
			    _CityDTO.class, ignoreFields, true);
		    testCriteria.setProjection(projection);
		    testCriteria.setResultTransformer(Transformers
			    .aliasToBean(_CityDTO.class));

		    List<_CityDTO> results = testCriteria.list();
		    return results;
		} catch (HibernateException e) {
		    fail("An exception has occured : maybe ignoreFields are not taken into account if the error is 'could not resolve property: distance... :"
			    + e);
		    throw e;
		}
	    }
	};

	List<_CityDTO> cities = (List<_CityDTO>) testDao
		.testCallback(hibernateCallback);
	assertEquals(1, cities.size());
	assertEquals("paris", cities.get(0).getName());
	assertEquals("1", cities.get(0).getFeatureId() + "");
    }

    @SuppressWarnings("unchecked")
    public void testBeanFieldListWithOutAutoAliasing() {

	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);

	this.cityDao.save(p1);
	HibernateCallback hibernateCallback = new HibernateCallback() {

	    public Object doInHibernate(Session session)
		    throws PersistenceException {

		try {
		    Criteria testCriteria = session.createCriteria(City.class);
		    String[] ignoreFields = { "distance" };
		    ProjectionList projection = ProjectionBean.beanFieldList(
			    _CityDTO.class, ignoreFields, false);
		    testCriteria.setProjection(projection);
		    testCriteria.add(Restrictions.eq("name", "paris"));

		    List<Object[]> results = testCriteria.list();
		    return results;
		} catch (HibernateException e) {
		    fail("An exception has occured : there is maybe a bug with autoaliasing"
			    + e);
		    throw e;
		}
	    }
	};

	List<Object[]> cities = (List<Object[]>) testDao
		.testCallback(hibernateCallback);
	assertEquals(1, cities.size());
	assertEquals(1L, cities.get(0)[0]);
    }

    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    public void setTestDao(_DaoHelper testDao) {
	this.testDao = testDao;
    }

}
