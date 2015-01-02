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
import org.springframework.dao.DataIntegrityViolationException;

import com.gisgraphy.Constants;
import com.gisgraphy.dao.RoleDao;
import com.gisgraphy.dao.UserDao;
import com.gisgraphy.model.Role;
import com.gisgraphy.model.User;
import com.gisgraphy.service.UserExistsException;

public class UserManagerImplTest extends BaseManagerMockTestCase {
    private UserManagerImpl userManager = new UserManagerImpl();

    private RoleManagerImpl roleManager = new RoleManagerImpl();

    private Mock userDao = null;

    private Mock roleDao = null;


    @Override
    protected void setUp() throws Exception {
	super.setUp();
	userDao = new Mock(UserDao.class);
	userManager.setUserDao((UserDao) userDao.proxy());
	roleDao = new Mock(RoleDao.class);
	roleManager.setRoleDao((RoleDao) roleDao.proxy());
    }

    @Test
    public void testGetUser() throws Exception {
	User testData = new User("1");
	testData.getRoles().add(new Role("user"));
	// set expected behavior on dao
	userDao.expects(once()).method("get").with(eq(1L)).will(
		returnValue(testData));

	User user = userManager.getUser("1");
	assertTrue(user != null);
	assertTrue(user.getRoles().size() == 1);
    }

    @Test
    public void testSaveUser() throws Exception {
	User testData = new User("1");
	testData.getRoles().add(new Role("user"));
	// set expected behavior on dao
	userDao.expects(once()).method("get").with(eq(1L)).will(
		returnValue(testData));

	User user = userManager.getUser("1");
	user.setPhoneNumber("303-555-1212");

	userDao.expects(once()).method("saveUser").with(same(user)).will(
		returnValue(user));

	user = userManager.saveUser(user);
	assertTrue(user.getPhoneNumber().equals("303-555-1212"));
	assertTrue(user.getRoles().size() == 1);
    }

    @Test
    public void testAddAndRemoveUser() throws Exception {
	User user = new User();

	// call populate method in super class to populate test data
	// from a properties file matching this class name
	user = (User) populate(user);

	// set expected behavior on role dao
	roleDao.expects(once()).method("getRoleByName").with(eq("ROLE_USER"))
		.will(returnValue(new Role("ROLE_USER")));

	Role role = roleManager.getRole(Constants.USER_ROLE);
	user.addRole(role);

	// set expected behavior on user dao
	userDao.expects(once()).method("saveUser").with(same(user)).will(
		returnValue(user));

	user = userManager.saveUser(user);
	assertTrue(user.getUsername().equals("john"));
	assertTrue(user.getRoles().size() == 1);

	userDao.expects(once()).method("remove").with(eq(5L));
	userManager.removeUser("5");

	userDao.expects(once()).method("get").will(returnValue(null));
	user = userManager.getUser("5");
	assertNull(user);
    }

    @Test
    public void testUserExistsException() {
	// set expectations
	User user = new User("admin");
	user.setEmail("matt@raibledesigns.com");

	Exception ex = new DataIntegrityViolationException("");
	userDao.expects(once()).method("saveUser").with(same(user)).will(
		throwException(ex));

	// run test
	try {
	    userManager.saveUser(user);
	    fail("Expected UserExistsException not thrown");
	} catch (UserExistsException e) {
	    log.debug("expected exception: " + e.getMessage());
	    assertNotNull(e);
	}
    }
}
