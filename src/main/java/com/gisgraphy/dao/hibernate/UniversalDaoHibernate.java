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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.gisgraphy.dao.UniversalDao;

/**
 * This class serves as the a class that can CRUD any object witout any Spring
 * configuration. The only downside is it does require casting from Object to
 * the object class.
 * 
 * @author Bryan Noll
 */
public class UniversalDaoHibernate extends HibernateDaoSupport implements
	UniversalDao {
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass())
     * from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * {@inheritDoc}
     */
    public Object save(Object o) {
	return getHibernateTemplate().merge(o);
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Class<?> clazz, Serializable id) {
	Object o = getHibernateTemplate().get(clazz, id);

	if (o == null) {
	    throw new ObjectRetrievalFailureException(clazz, id);
	}

	return o;
    }

    /**
     * {@inheritDoc}
     */
    public List<?> getAll(Class<?> clazz) {
	return getHibernateTemplate().loadAll(clazz);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Class<?> clazz, Serializable id) {
	getHibernateTemplate().delete(get(clazz, id));
    }
}
