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

import java.util.ArrayList;
import java.util.List;

import org.jmock.Mock;
import org.junit.Test;

import com.gisgraphy.Constants;
import com.gisgraphy.dao.LookupDao;
import com.gisgraphy.model.LabelValue;
import com.gisgraphy.model.Role;

public class LookupManagerImplTest extends BaseManagerMockTestCase {
    private LookupManagerImpl mgr = new LookupManagerImpl();

    private Mock lookupDao = null;

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	lookupDao = new Mock(LookupDao.class);
	mgr.setLookupDao((LookupDao) lookupDao.proxy());
    }

    @Test
    public void testGetAllRoles() {
	log.debug("entered 'testGetAllRoles' method");

	// set expected behavior on dao
	Role role = new Role(Constants.ADMIN_ROLE);
	List<Role> testData = new ArrayList<Role>();
	testData.add(role);
	lookupDao.expects(once()).method("getRoles").withNoArguments().will(
		returnValue(testData));

	List<LabelValue> roles = mgr.getAllRoles();
	assertTrue(roles.size() > 0);
    }
}
