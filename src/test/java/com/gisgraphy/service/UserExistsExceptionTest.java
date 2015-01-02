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

import org.junit.Test;
import org.springframework.beans.BeanUtils;

import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.model.User;

public class UserExistsExceptionTest extends AbstractTransactionalTestCase {
    private UserManager userManager = null;

    @Test
    public void testAddExistingUser() throws Exception {
	logger.debug("entered 'testAddExistingUser' method");
	assertNotNull(userManager);

	User user = userManager.getUser("-1");

	// create new object with null id - Hibernate doesn't like setId(null)
	User user2 = new User();
	BeanUtils.copyProperties(user, user2);
	user2.setId(null);
	user2.setVersion(null);
	user2.setRoles(null);

	// try saving as new user, this should fail b/c of unique keys
	try {
	    userManager.saveUser(user2);
	    fail("Duplicate user didn't throw UserExistsException");
	} catch (UserExistsException uee) {
	    assertNotNull(uee);
	}
    }
    
    public void setUserManager(UserManager userManager) {
	this.userManager = userManager;
    }
}
