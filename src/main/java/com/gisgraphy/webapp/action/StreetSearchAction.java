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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.StreetSearchResultsDto;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.street.IStreetSearchEngine;
import com.gisgraphy.street.StreetSearchQuery;
import com.gisgraphy.street.StreetSearchQueryHttpBuilder;
import com.gisgraphy.street.StreetType;

/**
 * Street search Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class StreetSearchAction extends SearchAction {
    
    private boolean autosubmit = false;

    /**
     * 
     */
    private static final long serialVersionUID = -9018894533914543310L;

    private static Logger logger = LoggerFactory
	    .getLogger(StreetSearchAction.class);

    private IStreetSearchEngine streetSearchEngine;

    private StreetSearchResultsDto streetSearchResultsDto = null;

    public String lat;

    public String lng;

    public String radius;

    private String streetType;
    
    private String name;
    
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
	return getStreetSearchResultsDto() != null;
    }

    private void executeQuery() {
	try {
	    StreetSearchQuery streetSearchQuery = StreetSearchQueryHttpBuilder.getInstance().buildFromHttpRequest(getRequest());
	    this.streetSearchResultsDto = streetSearchEngine.executeQuery(streetSearchQuery);
	    setFrom(streetSearchQuery.getFirstPaginationIndex());
	    setTo(streetSearchQuery.getLastPaginationIndex());
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
     * @return the request
     */
    protected HttpServletRequest getRequest() {
	return ServletActionContext
	    .getRequest();
    }

    /**
     * Execute a StreetSearch from the request parameters
     * 
     * @return SUCCESS if the search is successfull
     * @throws Exception
     *                 in case of errors
     */
    public String search() throws Exception {
	executeQuery();
	return SUCCESS;
    }

    public StreetType[] getStreetTypes(){
	return StreetType.values();
    }
    
    /**
     * Execute a streetSearch from the request parameters
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
	return OutputFormatHelper.listFormatByService(GisgraphyServiceType.STREET);
    }

   public Map<String, String> getNameOptions(){
       HashMap<String, String> nameOptions = new HashMap<String, String>();
       nameOptions.put("", getText("search.street.includeNoNameStreet"));
       nameOptions.put("%", getText("search.street.dont.includeNoNameStreet"));
       return nameOptions;
   }



    /**
     * @param streetSearchEngine the streetSearchEngine to set
     */
    public void setStreetSearchEngine(IStreetSearchEngine streetSearchEngine) {
        this.streetSearchEngine = streetSearchEngine;
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
     * @param streetType
     *                the streettype to set
     */
    public void setStreetType(String streetType) {
	this.streetType = streetType;
    }

    /**
     * @return the placetype
     */
    public String getStreetType() {
	  return streetType;
	}

    /**
     * @return the response
     */
    public StreetSearchResultsDto getStreetSearchResultsDto() {
	return this.streetSearchResultsDto;
    }

    /**
     * @return the autosubmit
     */
    public boolean isAutosubmit() {
        return autosubmit;
    }

    /**
     * @param autosubmit the autosubmit to set
     */
    public void setAutosubmit(boolean autosubmit) {
        this.autosubmit = autosubmit;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
  

}
