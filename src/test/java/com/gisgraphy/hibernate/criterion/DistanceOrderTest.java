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

import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.junit.Test;

import com.gisgraphy.test.GisgraphyTestHelper;

public class DistanceOrderTest  {

    @Test
    public void distanceOrderPointBooleanShouldTakeAscOrderIntoAccount() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getSQLAlias((Criteria) EasyMock.anyObject()))
		.andReturn("alias ").once();
	EasyMock.replay(criteriaQuery);
	DistanceOrder distanceOrder = new DistanceOrder(GisgraphyTestHelper.createPoint(
		3F, 4F), true);
	String sqlString = distanceOrder.toSqlString(null, criteriaQuery);
	assertTrue(sqlString.contains("asc"));
	assertTrue(sqlString.contains("st_distance_sphere"));
	EasyMock.verify(criteriaQuery);
    }

    @Test
    public void distanceOrderShouldHaveAscOrderByDefault() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getSQLAlias((Criteria) EasyMock.anyObject()))
		.andReturn("alias ").once();
	EasyMock.replay(criteriaQuery);
	DistanceOrder dorder = new DistanceOrder(GisgraphyTestHelper.createPoint(
		3F, 4F));
	String sqlString = dorder.toSqlString(null, criteriaQuery);
	assertTrue(sqlString.contains("asc"));
	assertTrue(sqlString.contains("st_distance_sphere"));
	EasyMock.verify(criteriaQuery);
    }

    @Test
    public void distanceOrderPointBooleanShouldTakeDescOrderIntoAccount() {
	CriteriaQuery criteriaQuery = EasyMock.createMock(CriteriaQuery.class);
	EasyMock.expect(
		criteriaQuery.getSQLAlias((Criteria) EasyMock.anyObject()))
		.andReturn(" alias ").once();
	EasyMock.replay(criteriaQuery);
	DistanceOrder dorder = new DistanceOrder(GisgraphyTestHelper.createPoint(
		3F, 4F), false);
	String sqlString = dorder.toSqlString(null, criteriaQuery);
	assertTrue(sqlString.contains("desc"));
	assertTrue(sqlString.contains("st_distance_sphere"));
	EasyMock.verify(criteriaQuery);
    }

}
