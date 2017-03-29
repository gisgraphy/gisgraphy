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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.gisgraphy.Constants;
import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.model.Address;
import com.gisgraphy.model.Role;
import com.gisgraphy.model.User;

public class UserDaoTest extends AbstractTransactionalTestCase {
    private UserDao dao = null;

    private RoleDao rdao = null;

    @Autowired
    public void setUserDao(UserDao dao) {
	this.dao = dao;
    }

    @Autowired
    public void setRoleDao(RoleDao rdao) {
	this.rdao = rdao;
    }

    @Test
    public void testGetUserInvalid() throws Exception {
	try {
	    dao.get(1000L);
	    Assert.fail("'badusername' found in database, failing test...");
	} catch (DataAccessException d) {
		Assert.assertTrue(d != null);
	}
    }

    @Test
    public void testGetUser() throws Exception {
	User user = dao.get(-1L);

	Assert.assertNotNull(user);
	Assert.assertEquals(1, user.getRoles().size());
	Assert.assertTrue(user.isEnabled());
    }

    @Test
    public void testUpdateUser() throws Exception {
	User user = dao.get(-1L);

	Address address = user.getAddress();
	address.setAddress("new address");

	dao.saveUser(user);
	flush();

	user = dao.get(-1L);
	Assert.assertEquals(address, user.getAddress());
	Assert.assertEquals("new address", user.getAddress().getAddress());

	try {
	    // verify that violation occurs when adding new user with same
	    // username
	    user.setId(null);
	    dao.saveUser(user);
	    flush();
	    Assert.fail("saveUser didn't throw DataIntegrityViolationException");
	} catch (Exception e) {
		Assert.assertNotNull(e);
	    log.debug("expected exception: " + e.getMessage());
	}
    }

    @Test
    public void testAddUserRole() throws Exception {
	User user = dao.get(-1L);
	Assert.assertEquals(1, user.getRoles().size());

	Role role = rdao.getRoleByName(Constants.ADMIN_ROLE);
	user.addRole(role);
	user = dao.saveUser(user);
	flush();

	user = dao.get(-1L);
	Assert.assertEquals(2, user.getRoles().size());

	// add the same role twice - should result in no additional role
	user.addRole(role);
	dao.saveUser(user);
	flush();

	user = dao.get(-1L);
	Assert.assertEquals("more than 2 roles", 2, user.getRoles().size());

	user.getRoles().remove(role);
	dao.saveUser(user);
	flush();

	user = dao.get(-1L);
	Assert.assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testAddAndRemoveUser() throws Exception {
	User user = new User("testuser");
	user.setPassword("testpass");
	user.setFirstName("Test");
	user.setLastName("Last");
	Address address = new Address();
	address.setCity("Denver");
	address.setProvince("CO");
	address.setCountry("USA");
	address.setPostalCode("80210");
	user.setAddress(address);
	user.setEmail("testuser@appfuse.org");
	user.setWebsite("http://raibledesigns.com");

	Role role = rdao.getRoleByName(Constants.USER_ROLE);
	Assert.assertNotNull(role.getId());
	user.addRole(role);

	user = dao.saveUser(user);
	flush();

	Assert.assertNotNull(user.getId());
	user = dao.get(user.getId());
	Assert.assertEquals("testpass", user.getPassword());

	dao.remove(user.getId());
	flush();

	try {
	    dao.get(user.getId());
	    Assert.fail("getUser didn't throw DataAccessException");
	} catch (DataAccessException d) {
		Assert. assertNotNull(d);
	}
    }

    @Test
    public void testUserExists() throws Exception {
	boolean b = dao.exists(-1L);
	Assert.assertTrue(b);
    }

    @Test
    public void testUserNotExists() throws Exception {
	boolean b = dao.exists(111L);
	Assert.assertFalse(b);
    }
}
