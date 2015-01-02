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
package com.gisgraphy.service;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.gisgraphy.Constants;
import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.helper.PropertiesHelper;
import com.gisgraphy.model.User;

public class UserManagerTest extends AbstractTransactionalTestCase {

    private UserManager userManager = null;

    private RoleManager roleManager = null;

    private Log log = LogFactory.getLog(UserManagerTest.class);

    public User user;

    @Test
    public void testGetUser() throws Exception {
	user = userManager.getUserByUsername("user");
	assertNotNull(user);

	log.debug(user);
	assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testSaveUser() throws Exception {
	user = userManager.getUserByUsername("user");
	user.setPhoneNumber("303-555-1212");

	log.debug("saving user with updated phone number: " + user);

	user = userManager.saveUser(user);
	assertEquals("303-555-1212", user.getPhoneNumber());
	assertEquals(1, user.getRoles().size());
    }

    /**
     * Utility method to populate a javabean-style object with values from a
     * Properties file
     * 
     * @param obj
     *                the model object to populate
     * @return Object populated object
     * @throws Exception
     *                 if BeanUtils fails to copy properly
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object populate(Object obj) throws Exception {
	// loop through all the beans methods and set its properties from
	// its .properties file
	Map map = PropertiesHelper.convertBundleToMap(rb);
	BeanUtils.copyProperties(obj, map);

	return obj;
    }

    
    @Test
    public void testAddAndRemoveUser() throws Exception {
	user = new User();

	// call populate method in super class to populate test data
	// from a properties file matching this class name
	user = (User) populate(user);

	user.addRole(roleManager.getRole(Constants.USER_ROLE));

	user = userManager.saveUser(user);
	assertEquals("john", user.getUsername());
	assertEquals(1, user.getRoles().size());

	log.debug("removing user...");

	userManager.removeUser(user.getId().toString());

	try {
	    user = userManager.getUserByUsername("john");
	    fail("Expected 'Exception' not thrown");
	} catch (Exception e) {
	    log.debug(e);
	    assertNotNull(e);
	}
    }
    
    public void setUserManager(UserManager userManager) {
	this.userManager = userManager;
    }

    public void setRoleManager(RoleManager roleManager) {
	this.roleManager = roleManager;
    }
}
