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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.gisgraphy.Constants;
import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.model.Role;

public class RoleDaoTest extends AbstractTransactionalTestCase {
    @Autowired
    @Qualifier("roleDao")
    private RoleDao roleDao;

    /*
     * public void setRoleDao(RoleDao dao) { this.dao = dao; }
     */
    public void testGetRoleInvalid() throws Exception {
	Role role = roleDao.getRoleByName("badrolename");
	assertNull(role);
    }

    public void testGetRole() throws Exception {
	Role role = roleDao.getRoleByName(Constants.USER_ROLE);
	assertNotNull(role);
    }

    public void testUpdateRole() throws Exception {
	Role role = roleDao.getRoleByName("ROLE_USER");
	role.setDescription("test descr");
	roleDao.save(role);
	flush();

	role = roleDao.getRoleByName("ROLE_USER");
	assertEquals("test descr", role.getDescription());
    }

    public void testAddAndRemoveRole() throws Exception {
	Role role = new Role("testrole");
	role.setDescription("new role descr");
	roleDao.save(role);
	flush();

	role = roleDao.getRoleByName("testrole");
	assertNotNull(role.getDescription());

	roleDao.removeRole("testrole");
	flush();

	role = roleDao.getRoleByName("testrole");
	assertNull(role);
    }
}
