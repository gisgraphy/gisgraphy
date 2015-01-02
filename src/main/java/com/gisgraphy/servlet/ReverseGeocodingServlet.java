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
package com.gisgraphy.servlet;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.geoloc.GeolocErrorVisitor;
import com.gisgraphy.geoloc.GeolocQuery;
import com.gisgraphy.helper.HTMLHelper;
import com.gisgraphy.reversegeocoding.IReverseGeocodingService;
import com.gisgraphy.reversegeocoding.ReverseGeocodingQuery;
import com.gisgraphy.reversegeocoding.ReverseGeocodingQueryHttpBuilder;
import com.gisgraphy.serializer.common.IoutputFormatVisitor;
import com.gisgraphy.serializer.common.OutputFormat;

/**
 * Provides a servlet Wrapper around The Gisgraphy Reverse geocoding Service, it Maps web
 * parameters to create a {@linkplain ReverseGeocodingQuery}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class ReverseGeocodingServlet extends GisgraphyServlet {

   
    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
	try {
	    super.init();
	    WebApplicationContext springContext = WebApplicationContextUtils
		    .getWebApplicationContext(getServletContext());
	    reverseGeocodingService = (IReverseGeocodingService) springContext
		    .getBean("reverseGeocodingService");
	    logger
		    .info("geolocSearchEngine is injected :"
			    + reverseGeocodingService);
	} catch (Exception e) {
	    logger.error("Can not start ReverseGeocodingServlet : " + e.getMessage(),e);
	}
    }

    /**
     * Default serialVersionUID
     */
    private static final long serialVersionUID = -9054548241743095743L;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(ReverseGeocodingServlet.class);
  

    private IReverseGeocodingService reverseGeocodingService;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	OutputFormat format = OutputFormat.getDefault();
	try {
	    format = setResponseContentType(req, resp);
	    // check empty query
	    if (HTMLHelper
		    .isParametersEmpty(req, GeolocQuery.LAT_PARAMETER, GeolocQuery.LONG_PARAMETER)) {
		sendCustomError(ResourceBundle.getBundle(
			Constants.BUNDLE_ERROR_KEY).getString(
			"error.emptyLatLong"), format, resp,req);
		return;
	    }
	    ReverseGeocodingQuery query = ReverseGeocodingQueryHttpBuilder.getInstance().buildFromHttpRequest(req);
	    if (logger.isDebugEnabled()){
	    logger.debug("query=" + query);
	    logger.debug("reverse geocoding engine=" + reverseGeocodingService);
	    }
	    String UA = req.getHeader("User-Agent");
	    String referer = req.getHeader("Referer");
	    if (logger.isInfoEnabled()){
		logger.info("A reverse  geocoding request from "+req.getRemoteHost()+" / "+req.getRemoteAddr()+" was received , Referer : "+referer+" , UA : "+UA);
	    }

	    reverseGeocodingService.executeAndSerialize(query, resp
		    .getOutputStream());
	} catch (RuntimeException e) {
	    logger.error("error while execute a reverse geocoding query from http request : " + e,e);
	    String errorMessage = isDebugMode() ? " : " + e.getMessage() : "";
	    sendCustomError(ResourceBundle
		    .getBundle(Constants.BUNDLE_ERROR_KEY).getString(
			    "error.error")
		    + errorMessage, format, resp,req);
	    return;
	}

    }


    /* (non-Javadoc)
     * @see com.gisgraphy.servlet.GisgraphyServlet#getGisgraphyServiceType()
     */
    @Override
    public GisgraphyServiceType getGisgraphyServiceType() {
	return GisgraphyServiceType.REVERSEGEOCODING;
    }


    /* (non-Javadoc)
     * @see com.gisgraphy.servlet.GisgraphyServlet#getErrorVisitor(java.lang.String)
     */
    @Override
    public IoutputFormatVisitor getErrorVisitor(String errorMessage) {
	return new GeolocErrorVisitor(errorMessage);
    }



	public void setReverseGeocodingService(
			IReverseGeocodingService reverseGeocodingService) {
		this.reverseGeocodingService = reverseGeocodingService;
	}

}
