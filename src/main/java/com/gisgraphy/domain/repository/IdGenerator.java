/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.domain.repository;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

/**
 * Convenience class to generate if (for openstreetmap, geonames) when they are
 * not provided, in case of custom add. this can be used in multithreading
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
@Service
public class IdGenerator implements IIdGenerator {


    /**
     * shift value to allow the addition of geonames features after import
     */ 
    public final static long FEATUREID_INCREMENT_NO_CONFLICT = 20000000;
    

    /**
     * shift value to allow the addition of openstreetmap features after import
     */ 
    public final static long OPENSTREETMAP_GID_NO_CONFLICT = 100000000;
    
   
	
	private IGisFeatureDao gisFeatureDao;
	
	private IOpenStreetMapDao openStreetMapDao;
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IIdGenerator#getNextFeatureId()
	 */
	public long getNextFeatureId() {
		return featureId.incrementAndGet();
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IIdGenerator#getNextGId()
	 */
	public synchronized long getNextGId() {
		return openstreetmapGid.incrementAndGet();
	}
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IIdGenerator#getNextFeatureId()
	 */
	public long getFeatureId() {
		return featureId.get();
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IIdGenerator#getNextGId()
	 */
	public long getGid() {
		return openstreetmapGid.get();
	}

	private AtomicLong featureId ;
	
	
	private AtomicLong openstreetmapGid;

	@PostConstruct
	public void sync() {
		//order is important
		initFeatureId();
		initGid();
	}


	private void initGid() {
		long maxGidInDatabase = openStreetMapDao.getMaxGid();
		if (maxGidInDatabase<OPENSTREETMAP_GID_NO_CONFLICT){
			maxGidInDatabase= OPENSTREETMAP_GID_NO_CONFLICT;
		}
		openstreetmapGid = new AtomicLong(Math.max(featureId.get(), maxGidInDatabase));
		
	}




	private void initFeatureId() {
		long maxFeatureIdInDatabase = gisFeatureDao.getMaxFeatureId();
		if(maxFeatureIdInDatabase<FEATUREID_INCREMENT_NO_CONFLICT){
		    maxFeatureIdInDatabase = FEATUREID_INCREMENT_NO_CONFLICT;
		}
		featureId = new AtomicLong(maxFeatureIdInDatabase);
	}


	/**
	 * @param gisFeatureDao the gisFeatureDao to set
	 */
	@Required
	public void setGisFeatureDao(IGisFeatureDao gisFeatureDao) {
		this.gisFeatureDao = gisFeatureDao;
	}


	/**
	 * @param openStreetMapDao the openStreetMapDao to set
	 */
	@Required
	public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
		this.openStreetMapDao = openStreetMapDao;
	}

}
