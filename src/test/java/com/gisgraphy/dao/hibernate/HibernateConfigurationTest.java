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

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.EntityPersister;

import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;

public class HibernateConfigurationTest extends AbstractTransactionalTestCase {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
	this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public void testColumnMapping() throws Exception {
	Session session = sessionFactory.openSession();
	try {
	    Map metadata = sessionFactory.getAllClassMetadata();
	    for (Object o : metadata.values()) {
		EntityPersister persister = (EntityPersister) o;
		String className = persister.getEntityName();
		log.debug("Trying select * from: " + className);
		Query q = session.createQuery("from " + className + " c");
		q.iterate();
		log.debug("ok: " + className);
	    }
	} finally {
	    session.close();
	}
    }
}
