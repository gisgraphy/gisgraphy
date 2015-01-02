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
package com.gisgraphy.service.impl;

import org.jmock.Mock;
import org.junit.Test;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.test.AssertThrows;

import com.gisgraphy.dao.UniversalDao;
import com.gisgraphy.model.User;

/**
 * This class tests the generic UniversalManager and UniversalManagerImpl
 * implementation.
 */
public class UniversalManagerTest extends BaseManagerMockTestCase {
    protected UniversalManagerImpl manager = new UniversalManagerImpl();

    protected Mock dao;

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	dao = new Mock(UniversalDao.class);
	manager.setDao((UniversalDao) dao.proxy());
    }

    @Override
    protected void tearDown() throws Exception {
	manager = null;
	dao = null;
    }

    /**
     * Simple test to verify BaseDao works.
     */
    @Test
    public void testCreate() {
	User user = createUser();
	dao.expects(once()).method("save").will(returnValue(user));
	user = (User) manager.save(user);
    }

    @Test
    public void testRetrieve() {
	User user = createUser();
	dao.expects(once()).method("get").will(returnValue(user));
	user = (User) manager.get(User.class, user.getUsername());
    }

    @Test
    public void testUpdate() {
	User user = createUser();
	dao.expects(once()).method("save").isVoid();
	user.getAddress().setCountry("USA");
	user = (User) manager.save(user);
    }

    @Test
    public void testDelete() {
	Exception ex = new ObjectRetrievalFailureException(User.class, "foo");
	dao.expects(once()).method("remove").isVoid();
	dao.expects(once()).method("get").will(throwException(ex));
	manager.remove(User.class, "foo");
	new AssertThrows(ObjectRetrievalFailureException.class) {
	    @Override
	    public void test() {
		manager.get(User.class, "foo");
	    }
	}.runTest();
    }

    private User createUser() {
	User user = new User();
	// set required fields
	user.setUsername("foo");
	return user;
    }
}
