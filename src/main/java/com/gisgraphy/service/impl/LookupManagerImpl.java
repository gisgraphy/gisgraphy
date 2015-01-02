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

import com.gisgraphy.dao.LookupDao;
import com.gisgraphy.model.LabelValue;
import com.gisgraphy.model.Role;
import com.gisgraphy.service.LookupManager;

/**
 * Implementation of LookupManager interface to talk to the persistence layer.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@SuppressWarnings("unchecked")
public class LookupManagerImpl extends UniversalManagerImpl implements
	LookupManager {
    private LookupDao dao;

    /**
     * Method that allows setting the DAO to talk to the data store with.
     * 
     * @param dao
     *                the dao implementation
     */
    public void setLookupDao(LookupDao dao) {
	super.dao = dao;
	this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    public List<LabelValue> getAllRoles() {
	List<Role> roles = dao.getRoles();
	List<LabelValue> list = new ArrayList<LabelValue>();

	for (Role role1 : roles) {
	    list.add(new LabelValue(role1.getName(), role1.getName()));
	}

	return list;
    }
}
