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
package com.gisgraphydao;

import java.util.List;

import com.gisgraphy.dao.LookupDao;
import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.model.Role;

/**
 * This class tests the current LookupDao implementation class
 * 
 * @author mraible
 */
public class LookupDaoTest extends AbstractTransactionalTestCase {
    private LookupDao dao;

    public void setLookupDao(LookupDao dao) {
	this.dao = dao;
    }

    public void testGetRoles() {
	List<Role> roles = dao.getRoles();
	log.debug(roles);
	assertTrue(roles.size() > 0);
    }
}
