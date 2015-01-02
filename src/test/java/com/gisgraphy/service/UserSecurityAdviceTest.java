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

import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gisgraphy.Constants;
import com.gisgraphy.dao.UserDao;
import com.gisgraphy.model.Role;
import com.gisgraphy.model.User;

public class UserSecurityAdviceTest extends MockObjectTestCase {
    Mock userDao = null;

    ApplicationContext ctx = null;

    SecurityContext initialSecurityContext = null;

    @Override
    protected void setUp() throws Exception {
	super.setUp();

	// store initial security context for later restoration
	initialSecurityContext = SecurityContextHolder.getContext();

	SecurityContext context = new SecurityContextImpl();
	User user = new User("user");
	user.setId(1L);
	user.setPassword("password");
	user.addRole(new Role(Constants.USER_ROLE));

	UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
		user.getUsername(), user.getPassword(), user.getAuthorities());
	token.setDetails(user);
	context.setAuthentication(token);
	SecurityContextHolder.setContext(context);
    }

    @Override
    protected void tearDown() {
	SecurityContextHolder.setContext(initialSecurityContext);
    }

    public void testAddUserWithoutAdminRole() throws Exception {
	Authentication auth = SecurityContextHolder.getContext()
		.getAuthentication();
	assertTrue(auth.isAuthenticated());
	UserManager userManager = makeInterceptedTarget();
	User user = new User("admin");
	user.setId(2L);

	try {
	    userManager.saveUser(user);
	    fail("AccessDeniedException not thrown");
	} catch (AccessDeniedException expected) {
	    assertNotNull(expected);
	    assertEquals(expected.getMessage(),
		    UserSecurityAdvice.ACCESS_DENIED);
	}
    }

    public void testAddUserAsAdmin() throws Exception {
	SecurityContext context = new SecurityContextImpl();
	User user = new User("admin");
	user.setId(2L);
	user.setPassword("password");
	user.addRole(new Role(Constants.ADMIN_ROLE));
	UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
		user.getUsername(), user.getPassword(), user.getAuthorities());
	token.setDetails(user);
	context.setAuthentication(token);
	SecurityContextHolder.setContext(context);

	UserManager userManager = makeInterceptedTarget();
	User adminUser = new User("admin");
	adminUser.setId(2L);

	userDao.expects(once()).method("saveUser");
	userManager.saveUser(adminUser);
    }

    public void testUpdateUserProfile() throws Exception {
	UserManager userManager = makeInterceptedTarget();
	User user = new User("user");
	user.setId(1L);
	user.getRoles().add(new Role(Constants.USER_ROLE));

	userDao.expects(once()).method("saveUser");
	userManager.saveUser(user);
    }

    // Test fix to http://issues.appfuse.org/browse/APF-96
    public void testChangeToAdminRoleFromUserRole() throws Exception {
	UserManager userManager = makeInterceptedTarget();
	User user = new User("user");
	user.setId(1L);
	user.getRoles().add(new Role(Constants.ADMIN_ROLE));

	try {
	    userManager.saveUser(user);
	    fail("AccessDeniedException not thrown");
	} catch (AccessDeniedException expected) {
	    assertNotNull(expected);
	    assertEquals(expected.getMessage(),
		    UserSecurityAdvice.ACCESS_DENIED);
	}
    }

    // Test fix to http://issues.appfuse.org/browse/APF-96
    public void testAddAdminRoleWhenAlreadyHasUserRole() throws Exception {
	UserManager userManager = makeInterceptedTarget();
	User user = new User("user");
	user.setId(1L);
	user.getRoles().add(new Role(Constants.ADMIN_ROLE));
	user.getRoles().add(new Role(Constants.USER_ROLE));

	try {
	    userManager.saveUser(user);
	    fail("AccessDeniedException not thrown");
	} catch (AccessDeniedException expected) {
	    assertNotNull(expected);
	    assertEquals(expected.getMessage(),
		    UserSecurityAdvice.ACCESS_DENIED);
	}
    }

    // Test fix to http://issues.appfuse.org/browse/APF-96
    public void testAddUserRoleWhenHasAdminRole() throws Exception {
	SecurityContext context = new SecurityContextImpl();
	User user1 = new User("user");
	user1.setId(1L);
	user1.setPassword("password");
	user1.addRole(new Role(Constants.ADMIN_ROLE));
	UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
		user1.getUsername(), user1.getPassword(), user1
			.getAuthorities());
	token.setDetails(user1);
	context.setAuthentication(token);
	SecurityContextHolder.setContext(context);

	UserManager userManager = makeInterceptedTarget();
	User user = new User("user");
	user.setId(1L);
	user.getRoles().add(new Role(Constants.ADMIN_ROLE));
	user.getRoles().add(new Role(Constants.USER_ROLE));

	userDao.expects(once()).method("saveUser");
	userManager.saveUser(user);
    }

    // Test fix to http://issues.appfuse.org/browse/APF-96
    public void testUpdateUserWithUserRole() throws Exception {
	UserManager userManager = makeInterceptedTarget();
	User user = new User("user");
	user.setId(1L);
	user.getRoles().add(new Role(Constants.USER_ROLE));

	userDao.expects(once()).method("saveUser");
	userManager.saveUser(user);
    }

    private UserManager makeInterceptedTarget() {
	ctx = new ClassPathXmlApplicationContext("/applicationContext-test.xml");

	UserManager userManager = (UserManager) ctx.getBean("targeted");

	// Mock the userDao
	userDao = new Mock(UserDao.class);
	userManager.setUserDao((UserDao) userDao.proxy());
	return userManager;
    }
}
