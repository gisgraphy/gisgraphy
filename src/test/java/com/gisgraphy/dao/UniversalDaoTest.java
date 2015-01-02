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
package com.gisgraphy.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.ObjectRetrievalFailureException;

import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.model.User;

/**
 * This class tests the generic GenericDao and BaseDao implementation.
 */
public class UniversalDaoTest extends AbstractTransactionalTestCase {
    protected UniversalDao universalDao;

    /**
     * Simple test to verify CRUD works.
     */
    @Test
    public void testCRUD() {
	User user = new User();
	// set required fields
	user.setUsername("foo");
	user.setPassword("bar");
	user.setFirstName("first");
	user.setLastName("last");
	user.getAddress().setCity("Denver");
	user.getAddress().setPostalCode("80465");
	user.setEmail("foo@bar.com");

	// create
	user = (User) universalDao.save(user);
	flush();
	assertNotNull(user.getId());

	// retrieve
	user = (User) universalDao.get(User.class, user.getId());
	assertNotNull(user);
	assertEquals("last", user.getLastName());

	// update
	user.getAddress().setCountry("USA");
	universalDao.save(user);
	flush();

	user = (User) universalDao.get(User.class, user.getId());
	assertEquals("USA", user.getAddress().getCountry());

	// delete
	universalDao.remove(User.class, user.getId());
	flush();
	try {
	    universalDao.get(User.class, user.getId());
	    fail("User 'foo' found in database");
	} catch (ObjectRetrievalFailureException e) {
	    assertNotNull(e.getMessage());
	} catch (InvalidDataAccessApiUsageException e) { // Spring 2.0 throws
	    // this one
	    assertNotNull(e.getMessage());
	}
    }

    @Required
    public void setUniversalDao(UniversalDao universalDao) {
	this.universalDao = universalDao;
    }

}
