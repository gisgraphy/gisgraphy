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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.exception.RepositoryException;
import com.gisgraphy.geoloc.GeolocSearchEngine;

/**
 * A class based on the stategy pattern
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Strategy_pattern">Strategy pattern</a>
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Component
public class RepositoryStrategy implements IRepositoryStrategy {

    @Autowired
    protected IGisDao<? extends GisFeature>[] daos;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(GeolocSearchEngine.class);

    /**
     * A Map with GisFeature call as keys and Dao as Values. A (Gis)dao is
     * associated to a Class
     */
    public final static Map<Class<? extends GisFeature>, IGisDao<? extends GisFeature>> daoMap = new HashMap<Class<? extends GisFeature>, IGisDao<? extends GisFeature>>();

    @PostConstruct
    public void initMap() throws Exception {
	logger.info("gisdao size=" + daos.length);
	for (int i = 0; i < daos.length; i++) {
	    daoMap.put((Class<? extends GisFeature>) daos[i]
		    .getPersistenceClass(), daos[i]);
	    logger.info("iGisdao[" + i + "]=" + daos[i].getPersistenceClass());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IRepositoryStrategy#GetDao(java.lang.Class)
     */
    public IGisDao<? extends GisFeature> getDao(
	    Class<?> placeType) {
	IGisDao<? extends GisFeature> dao = daoMap.get(GisFeature.class);
	if (placeType != null) {
	    dao = daoMap.get(placeType);
	} else {
	    return dao;
	}
	if (dao == null) {
	    throw new RepositoryException(
		    "No gisFeatureDao or no placetype can be found for "
			    + placeType + " can be found.");
	}
	return dao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IRepositoryStrategy#getAvailablesDaos()
     */
    public Collection<IGisDao<? extends GisFeature>> getAvailablesDaos() {
	if (daos == null) {
	    return new ArrayList<IGisDao<? extends GisFeature>>();
	}
	return RepositoryStrategy.daoMap.values();
    }
}
