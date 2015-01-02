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
/**
 *
 */
package com.gisgraphy.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

/**
 * A generic dao That implements basic functions with java 5.0 generics
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GenericDao<T, PK extends Serializable> extends HibernateDaoSupport
	implements IDao<T, PK> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    protected Class<T> persistentClass;

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#getPersistenceClass()
     */
    public Class<T> getPersistenceClass() {
	return persistentClass;
    }

    /**
     * constructor
     * 
     * @param persistentClass
     *                The specified Class for the GenericDao
     */
    public GenericDao(final Class<T> persistentClass) {
	this.persistentClass = persistentClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.Idao#flushAndClear()
     */
    public void flushAndClear() {
	this.getHibernateTemplate().execute(new HibernateCallback() {
	    public Object doInHibernate(Session session)
		    throws PersistenceException {
		session.flush();
		session.clear();
		return null;
	    }
	});

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.Idao#remove(java.io.Serializable)
     */
    public void remove(final T o) {
	Assert.notNull(o, "Can not remove a null object");
	this.getHibernateTemplate().delete(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.Idao#save(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public T save(final T o) {
	Assert.notNull(o);
	this.getHibernateTemplate().saveOrUpdate(o);
	return o;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#setFlushMode(org.hibernate.FlushMode)
     */
    public void setFlushMode(final FlushMode flushMode) {
	Assert.notNull(flushMode);
	this.getHibernateTemplate().execute(new HibernateCallback() {
	    public Object doInHibernate(Session session) {
		session.setFlushMode(flushMode);
		return null;
	    }
	});

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#get(java.io.Serializable)
     */
       public T get(final PK id) {
	Assert.notNull(id, "Can not retrieve an Ogject with a null id");
	T returnValue = null;
	try {
	    returnValue = (T) this.getHibernateTemplate().get(persistentClass, id);
	} catch (Exception e) {
	    log.info("could not retrieve object of type "
		    + persistentClass.getSimpleName() + " with id " + id, e);
	}
	return returnValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#exists(java.io.Serializable)
     */
    public boolean exists(PK id) {
	return get(id) != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.Idao#findAll()
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
	List<T> returnValue = new ArrayList<T>();
	try {
	    return (List<T>) this.getHibernateTemplate().execute(
		    new HibernateCallback() {

			public Object doInHibernate(Session session)
				throws PersistenceException {
			    String queryString = "from "
				    + persistentClass.getSimpleName();

			    Query qry = session.createQuery(queryString);
			    qry.setCacheable(true);
			    List<T> results = (List<T>) qry.list();
			    if (results == null) {
				results = new ArrayList<T>();
			    }
			    return results;
			}
		    });
	} catch (DataAccessResourceFailureException e) {
	    log.info("could not retrieve all object of type "
		    + persistentClass.getName(), e);
	} catch (ObjectNotFoundException e) {
	    log.info("could not retrieve object of type "
		    + persistentClass.getName() + " with id ", e);
	} catch (HibernateException e) {
	    log.info("could not retrieve all object of type "
		    + persistentClass.getName(), e);
	} catch (IllegalStateException e) {
	    log.info("could not retrieve all object of type "
		    + persistentClass.getName(), e);
	}
	return returnValue;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#deleteAll(java.util.List)
     */
    public void deleteAll(List<T> list) {
	Assert.notNull(list);
	getHibernateTemplate().deleteAll(list);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#count()
     */
    public long count() {
	return ((Long) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "select count(*) from "
				+ persistentClass.getSimpleName();

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);
			Long result = (Long) qry.uniqueResult();
			return result;
		    }
		})).longValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#getAllPaginate(int, int)
     */
    @SuppressWarnings("unchecked")
    public List<T> getAllPaginate(final int from, final int maxResults) {
	return (List<T>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ persistentClass.getSimpleName();

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);
			if (maxResults > 0) {
			    qry.setMaxResults(maxResults);
			}
			if (from >= 1) {
			    qry.setFirstResult(from - 1);
			}
			List<T> results = (List<T>) qry.list();
			if (results == null) {
			    results = new ArrayList<T>();
			}
			return results;
		    }
		});

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IDao#deleteAll()
     */
    public int deleteAll() {
	int deleted = ((Integer) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			//TODO zipcodes and alternatename are not deleted in cascade
			String queryString = "DELETE "
				+ persistentClass.getSimpleName();
			
			Query qry = session.createQuery(queryString);
			Integer deleted = Integer.valueOf(qry.executeUpdate());
			return deleted;
		    }
		})).intValue();
	return deleted;

    }

}
