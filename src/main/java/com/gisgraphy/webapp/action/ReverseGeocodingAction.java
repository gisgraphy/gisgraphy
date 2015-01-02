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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.GisgraphyConfig;

/**
 * Reverse geocoding Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class ReverseGeocodingAction extends SearchAction implements GoogleMapApiKeyAware{

   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory
	    .getLogger(ReverseGeocodingAction.class);


    public String lat;

    public String lng;


    /**
     * @return the lat
     */
    public String getLat() {
	return lat;
    }

    /**
     * @param lat
     *                the lat to set
     */
    public void setLat(String lat) {
	this.lat = lat;
    }

    /**
     * @return the lng
     */
    public String getLng() {
	return lng;
    }

    /**
     * @param lng
     *                the lng to set
     */
    public void setLng(String lng) {
	this.lng = lng;
    }

    
    /**
     * @return the googleMapAPIKey
     */
    public String getGoogleMapAPIKey() {
        return GisgraphyConfig.googleMapAPIKey == null ? "" : GisgraphyConfig.googleMapAPIKey;
    }

}
