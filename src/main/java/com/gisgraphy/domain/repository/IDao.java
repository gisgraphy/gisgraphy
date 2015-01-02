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
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;

/**
 * Interface of a Generic (java-5 meaning) data access object
 * 
 * @see IGisDao
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IDao<T, PK extends Serializable> {

    /**
     * @return The class, this DAO 'process'
     */
    public Class<T> getPersistenceClass();

    /**
     * Generic method used to get all objects of a particular type. This is the
     * same as lookup up all rows in a table.
     * 
     * @return List of populated objects (never return null, but an empty list)
     */
    public List<T> getAll();

    /**
     * Returns all object of a particular type (i.e getAllPaginate(2,5) will
     * return the [2,3,4,5,6] object
     * 
     * @param from
     *                The first result to return numbered from 1
     * @param maxResults
     *                The maximum list size to return
     * @return List of populated objects, (never return null, but an empty list)
     * @throws HibernateException
     *                 if thrown by the Hibernate API
     * @throws SQLException
     *                 if thrown by JDBC API
     */
    public List<T> getAllPaginate(int from, int maxResults);

    /**
     * Flush all Memory Objects to the database, and clear the L1 cache, of the
     * current thread-bound Hibernate Session.
     * 
     * @throws HibernateException
     *                 that Indicates problems flushing the session or talking
     *                 to the database
     */
    public void flushAndClear();

    /**
     * Saves the passed object, and returns an attached entity. It is very very
     * very important to use the returned entity, because of the way the
     * underlying mechanism possibly works. For instance, when using Db4o, this
     * is completly useless, but when using JPA and the merge method of the
     * EntityManager, things are going to be buggy (Duplicate Key exceptions) if
     * you don't use the returned object. Please consult EJB3 (or the concrete
     * persistence framework doc) Spec for more information about the way the
     * merge method works.
     * 
     * @param o
     *                The object to save
     * @return The saved instance
     * @throws DataAccessException
     *                 in case of errors
     */
    public T save(final T o);

    /**
     * Sets the flush mode (i.e. when objects are flushed to the database) of
     * the current thread-bound session. By default, it is equivalent (in the
     * case of JPA persistence) to {@link FlushModeType#AUTO}, which lets the
     * persistence framework handle that issue. However, for performance
     * reasons, it might be necessary to set it to {@link FlushModeType#COMMIT}.
     * Warning : this sets the default flush mode of the session (either
     * hibernate {@link Session}, JPA {@link EntityManager}, or similar) that
     * is currently bound to the current thread. This means that it has
     * absolutely no effect if no transaction is currently opened.
     * 
     * @param flushMode
     *                The flush mode To set for this dao
     * @throws HibernateException
     *                 if thrown by the Hibernate API
     * @throws SQLException
     *                 if thrown by JDBC API
     */
    public void setFlushMode(FlushMode flushMode);

    /**
     * remove the object from the datastore
     * 
     * @param o
     *                The object to remove
     * @throws DataAccessException
     *                 In case of errors
     */
    public void remove(T o);

    /**
     * Checks for existence of an object of type T using the id arg.
     * 
     * @param id
     *                the id of the entity
     * @return - true if it exists, false if it doesn't
     * @throws DataAccessException
     *                 in case of errors
     */
    boolean exists(PK id);

    /**
     * Retrieve the Object whith the specified primary key
     * 
     * @param id
     *                the primarey key
     * @return The object
     * @throws DataAccessException
     *                 in case of errors
     */
    public T get(PK id);

    /**
     * @return the number of element in the Datastore
     * @throws HibernateException
     *                 if thrown by the Hibernate API
     * @throws SQLException
     *                 if thrown by JDBC API
     */
    public long count();

    /**
     * Delete all the specified object from the datastore
     * 
     * @param list
     *                the list of element to delete
     * @throws DataAccessException
     *                 in case of errors
     */
    public void deleteAll(List<T> list);

    /**
     * Delete all the object from the datastore
     * 
     * @return the number of deleted objects
     * 
     * @throws DataAccessException
     *                 in case of errors
     */
    public int deleteAll();

}
