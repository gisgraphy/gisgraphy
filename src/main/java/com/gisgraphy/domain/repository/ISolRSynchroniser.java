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
package com.gisgraphy.domain.repository;

import com.gisgraphy.domain.geoloc.entity.event.IEvent;

/**
 * We don't use the Hibernate Listeners because we don't want to handle every
 * hibernate operations just a few one (we don't want to check if that class or
 * that one should be sync Synchronise the Gis Object with the full text search
 * engine.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface ISolRSynchroniser {

    /**
     * handle an event in order to synchronise Data (the event may be add,
     * update, delete a GIS object)
     * 
     * @param event
     *            The event to handle
     */
    public void handleEvent(IEvent event);

    /**
     * Send a commit to the full text search engine
     * 
     * @return true if success or false other case. it does not throw exception
     *         because the commit may not be required because of the auto commit
     *         functionnality in solr. it is up to the caller to deal with this
     */
    public boolean commit();

    /**
     * Send a optimize command to the full text search engine
     */
    public void optimize();

    /**
     * /!\USE IT WITH CARE/!\ : The Database and The full text search engine may
     * be de-synchronised, because this method does not delete data in Database
     * Delete all the data in the fulltext search engine <u>AND COMMIT</u>
     */
    public void deleteAll();

}