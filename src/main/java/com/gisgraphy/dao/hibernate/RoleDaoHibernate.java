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
package com.gisgraphy.dao.hibernate;

import java.util.List;

import com.gisgraphy.dao.RoleDao;
import com.gisgraphy.model.Role;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete and
 * retrieve Role objects.
 * 
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 */
public class RoleDaoHibernate extends GenericDaoHibernate<Role, Long> implements
	RoleDao {

    /**
     * Constructor to create a Generics-based version using Role as the entity
     */
    public RoleDaoHibernate() {
	super(Role.class);
    }

    /**
     * {@inheritDoc}
     */
    public Role getRoleByName(String rolename) {
	List<?> roles = getHibernateTemplate().find("from Role where name=?",
		rolename);
	if (roles.isEmpty()) {
	    return null;
	} else {
	    return (Role) roles.get(0);
	}
    }

    /**
     * {@inheritDoc}
     */
    public void removeRole(String rolename) {
	Object role = getRoleByName(rolename);
	getHibernateTemplate().delete(role);
    }
}
