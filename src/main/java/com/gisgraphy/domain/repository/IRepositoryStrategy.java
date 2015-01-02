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

import java.util.Collection;

import com.gisgraphy.domain.geoloc.entity.GisFeature;

public interface IRepositoryStrategy {

    /**
     * @param placeType
     *                the placetype we'd like to retrieve the dao. if the place
     *                type is null the gisFeatureDao will be return
     * @return the dao corresponding to the specified class
     * @throws a
     *                 RepositoryException if no dao is found for the specified
     *                 placetype
     */
    public abstract IGisDao<?> getDao(
	    Class<?> placeType);

    /**
     * @return all the availables daos. never return null but an empty Arraylist
     *         if there is no dao
     */
    public abstract Collection<IGisDao<? extends GisFeature>> getAvailablesDaos();

}