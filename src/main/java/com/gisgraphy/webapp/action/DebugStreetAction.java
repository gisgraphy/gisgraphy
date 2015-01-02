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
package com.gisgraphy.webapp.action;

import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.opensymphony.xwork2.ActionSupport;

/**
 * stats Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class DebugStreetAction extends ActionSupport {

	/**
     * Default serialId
     */
	private static final long serialVersionUID = -7519735359681028901L;

	

    private OpenStreetMap openstreetmap;
    
    private ICityDao cityDao;
    
    private IOpenStreetMapDao openStreetMapDao;
    
    private Long openstreetmapId;
    
    private City nearestMunicipalityByShape;
    
    private City nearestNonMunicipalityByShape;
    
    private City nearestMunicipalityByVicinity;
    
    private City nearestNonMunicipalityByVicinity;
    
    private String message ;

  

    /*
     * (non-Javadoc)
     * 
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @Override
    public String execute() throws Exception {
    	if (openstreetmapId==null){
    		return SUCCESS;
    	} else {
    		openstreetmap = openStreetMapDao.getByOpenStreetMapId(openstreetmapId);
    		if (openstreetmap!=null){
    			nearestMunicipalityByShape = cityDao.getByShape(openstreetmap.getLocation(), null, true);
    			nearestNonMunicipalityByShape = cityDao.getByShape(openstreetmap.getLocation(), null, false);
    			nearestMunicipalityByVicinity = cityDao.getNearest(openstreetmap.getLocation(), null, true, com.gisgraphy.importer.OpenStreetMapSimpleImporter.DISTANCE);
    			nearestNonMunicipalityByVicinity = cityDao.getNearest(openstreetmap.getLocation(), null, false, com.gisgraphy.importer.OpenStreetMapSimpleImporter.DISTANCE);
    			return SUCCESS;
    		} else {
    			message =  "No street found for "+openstreetmapId;
    			return SUCCESS;
    		}
    	}
    	
    }



	public OpenStreetMap getOpenstreetmap() {
		return openstreetmap;
	}




	public Long getOpenstreetmapId() {
		return openstreetmapId;
	}



	public void setOpenstreetmapId(Long openstreetmapId) {
		this.openstreetmapId = openstreetmapId;
	}

	

	@Required
	public void setCityDao(ICityDao cityDao) {
		this.cityDao = cityDao;
	}


	@Required
	public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
		this.openStreetMapDao = openStreetMapDao;
	}



	public City getNearestMunicipalityByShape() {
		return nearestMunicipalityByShape;
	}



	public City getNearestNonMunicipalityByShape() {
		return nearestNonMunicipalityByShape;
	}



	public City getNearestMunicipalityByVicinity() {
		return nearestMunicipalityByVicinity;
	}



	public City getNearestNonMunicipalityByVicinity() {
		return nearestNonMunicipalityByVicinity;
	}



	public String getMessage() {
		return message;
	}






}
