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
package com.gisgraphy.webapp.action;

import org.apache.struts2.ServletActionContext;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.model.User;
import com.gisgraphy.service.UserManager;

public class UserActionTest extends BaseActionTestCase {
    private UserAction userAction;

    public void setUserAction(UserAction action) {
	this.userAction = action;
    }

    @Test
    public void testCancel() throws Exception {
	assertEquals(userAction.cancel(), "mainMenu");
	assertFalse(userAction.hasActionErrors());

	userAction.setFrom("list");
	assertEquals("cancel", userAction.cancel());
    }

    @Test
    public void testEdit() throws Exception {
	// so request.getRequestURL() doesn't fail
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/editUser.html");
	ServletActionContext.setRequest(request);

	userAction.setId("-1"); // regular user
	assertNull(userAction.getUser());
	assertEquals("success", userAction.edit());
	assertNotNull(userAction.getUser());
	assertFalse(userAction.hasActionErrors());
    }

    @Test
    public void testSave() throws Exception {
	UserManager userManager = (UserManager) applicationContext
		.getBean("userManager");
	User user = userManager.getUserByUsername("user");
	user.setPassword("user");
	user.setConfirmPassword("user");
	userAction.setUser(user);
	userAction.setFrom("list");

	MockHttpServletRequest request = new MockHttpServletRequest();
	request.addParameter("encryptPass", "true");
	ServletActionContext.setRequest(request);

	assertEquals("input", userAction.save());
	assertNotNull(userAction.getUser());
	assertFalse(userAction.hasActionErrors());
    }

    @Test
    public void testSaveConflictingUser() throws Exception {
	UserManager userManager = (UserManager) applicationContext
		.getBean("userManager");
	User user = userManager.getUserByUsername("user");
	user.setPassword("user");
	user.setConfirmPassword("user");
	// e-mail address from existing user
	User existingUser = (User) userManager.getUsers(null).get(0);
	user.setEmail(existingUser.getEmail());
	userAction.setUser(user);
	userAction.setFrom("list");

	Integer originalVersionNumber = user.getVersion();
	log.debug("original version #: " + originalVersionNumber);

	MockHttpServletRequest request = new MockHttpServletRequest();
	request.addParameter("encryptPass", "true");
	ServletActionContext.setRequest(request);

	assertEquals("input", userAction.save());
	assertNotNull(userAction.getUser());
	assertEquals(originalVersionNumber, user.getVersion());
	assertTrue(userAction.hasActionErrors());
    }

    @Test
    public void testSearch() throws Exception {
	assertNull(userAction.getUsers());
	assertEquals("success", userAction.list());
	assertNotNull(userAction.getUsers());
	assertFalse(userAction.hasActionErrors());
    }

    @Test
    public void testRemove() throws Exception {
	User user = new User("admin");
	user.setId(-2L);
	userAction.setUser(user);
	assertEquals("success", userAction.delete());
	assertFalse(userAction.hasActionErrors());
    }
}
