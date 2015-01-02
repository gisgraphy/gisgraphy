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

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.geocoloc.IGeolocSearchEngine;
import com.gisgraphy.geoloc.GeolocQuery;
import com.gisgraphy.geoloc.GeolocQueryHttpBuilder;
import com.gisgraphy.geoloc.GeolocResultsDto;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.serializer.common.OutputFormat;

/**
 * Geolocalisation search Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeolocSearchAction extends SearchAction {

    /**
     * 
     */
    private static final long serialVersionUID = -9018894533914543310L;

    private static Logger logger = LoggerFactory
	    .getLogger(GeolocSearchAction.class);

    private IGeolocSearchEngine geolocSearchEngine;

    private GeolocResultsDto responseDTO;

    public String lat;

    public String lng;

    public String radius;

    private String placetype;
    
    private boolean distance = true;

    public boolean isDistance() {
        return distance;
    }

    public void setDistance(boolean distance) {
        this.distance = distance;
    }

    /**
     * @return Wether the search has been done and the results should be
     *         displayed
     */
    public boolean isDisplayResults() {
	return getResponseDTO() != null;
    }

    private void executeQuery() {
	try {
	    GeolocQuery geolocQuery = GeolocQueryHttpBuilder.getInstance().buildFromHttpRequest(ServletActionContext
		    .getRequest());
	    this.responseDTO = geolocSearchEngine.executeQuery(geolocQuery);
	    setFrom(geolocQuery.getFirstPaginationIndex());
	    setTo(geolocQuery.getLastPaginationIndex());
	} catch (RuntimeException e) {
	    if (e.getCause() != null) {
		logger.error("An error occured during search : "
			+ e.getCause().getMessage());
	    } else {
		logger.error("An error occured during search : "
			+ e.getMessage());
	    }
	    this.errorMessage = e.getMessage() == null? getText("errorPage.heading"):e.getMessage();
	}
    }

    /**
     * Execute a GeolocSearch from the request parameters
     * 
     * @return SUCCESS if the search is successful
     * @throws Exception
     *                 in case of errors
     */
    public String search() throws Exception {
	executeQuery();
	return SUCCESS;
    }

    /**
     * Execute a geolocSearch from the request parameters
     * 
     * @return POPUPVIEW if the search is successfull The view will not be
     *         decorated by sitemesh (see decorators.xml)
     * @throws Exception
     *                 in case of errors
     */
    public String searchpopup() throws Exception {
	executeQuery();
	return POPUP_VIEW;
    }

    /**
     * @return the available formats for fulltext
     */
    public OutputFormat[] getFormats() {
	return OutputFormatHelper.listFormatByService(GisgraphyServiceType.GEOLOC);
    }

    /**
     * @param geolocSearchEngine
     *                the geolocSearchEngine to set
     */
    @Required
    public void setGeolocSearchEngine(IGeolocSearchEngine geolocSearchEngine) {
	this.geolocSearchEngine = geolocSearchEngine;
    }

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
     * @return the radius
     */
    public String getRadius() {
	return radius;
    }

    /**
     * @param radius
     *                the radius to set
     */
    public void setRadius(String radius) {
	this.radius = radius;
    }

    /**
     * @param placetype
     *                the placetype to set
     */
    public void setPlacetype(String placetype) {
	this.placetype = placetype;
    }

    /**
     * @return the placetype
     */
    public String getPlacetype() {
	if (placetype == null
		&& GisgraphyConfig.defaultGeolocSearchPlaceTypeClass != null) {
	    return GisgraphyConfig.defaultGeolocSearchPlaceTypeClass
		    .getSimpleName().toLowerCase();
	} else {
	    return placetype;
	}
    }

    /**
     * @return the response
     */
    public GeolocResultsDto getResponseDTO() {
	return this.responseDTO;
    }

}
