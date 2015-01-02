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
import com.gisgraphy.fulltext.FulltextErrorVisitor;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextQueryHttpBuilder;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.helper.HTMLHelper;
import com.gisgraphy.serializer.common.IoutputFormatVisitor;
import com.gisgraphy.serializer.common.OutputFormat;

/**
 * Provides a servlet Wrapper around The Gisgraphy fulltext Service
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * @see GeolocServlet
 */
public class FulltextServlet extends GisgraphyServlet {

    
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
	    fullTextSearchEngine = (IFullTextSearchEngine) springContext
		    .getBean("fullTextSearchEngine");
	    logger.info("fullTextSearchEngine is injected :"
		    + fullTextSearchEngine);
	} catch (Exception e) {
	    logger.error("Can not start fulltextServlet : " + e.getMessage(),e);
	}
    }

    /**
     * Default serialVersionUID
     */
    private static final long serialVersionUID = -9054548241743095743L;

    /**
     * The logger
     */
    protected static Logger logger = LoggerFactory
	    .getLogger(FulltextServlet.class);

    private IFullTextSearchEngine fullTextSearchEngine;

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
	    if (HTMLHelper.isParametersEmpty(req, FulltextQuery.QUERY_PARAMETER)) {
		sendCustomError(ResourceBundle.getBundle(
			Constants.BUNDLE_ERROR_KEY).getString(
			"error.emptyQuery"), format, resp,req);
		return;
	    }
	    FulltextQuery query = FulltextQueryHttpBuilder.getInstance().buildFromRequest(req);
	    if (logger.isDebugEnabled()){
		logger.debug("query=" + query);
		logger.debug("fulltext engine=" + fullTextSearchEngine);
	    }
	    String UA = req.getHeader(com.gisgraphy.domain.Constants.HTTP_USER_AGENT_HEADER_NAME);
	    String referer = req.getHeader(com.gisgraphy.domain.Constants.HTTP_REFERER_HEADER_NAME);
	    if (logger.isInfoEnabled()){
		logger.info("A fulltext request from "+req.getRemoteHost()+" / "+req.getRemoteAddr()+" was received , Referer : "+referer+" , UA : "+UA);
	    }
	    
	    fullTextSearchEngine.executeAndSerialize(query, resp
		    .getOutputStream());
	} catch (RuntimeException e) {
	    logger.error("error while execute a fulltext query from http request : " + e.getMessage(),e);
	    String errorMessage = isDebugMode() ? " : " + e.getMessage() : "";
	    sendCustomError(ResourceBundle
		    .getBundle(Constants.BUNDLE_ERROR_KEY).getString(
			    "error.error")
		    + errorMessage, format, resp,req);
	    return;
	}

    }
    
   
    /**
     * @param fullTextSearchEngine
     *                the fullTextSearchEngine to set
     */
    public void setFullTextSearchEngine(
	    IFullTextSearchEngine fullTextSearchEngine) {
	this.fullTextSearchEngine = fullTextSearchEngine;
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.servlet.GisgraphyServlet#getGisgraphyServiceType()
     */
    @Override
    public GisgraphyServiceType getGisgraphyServiceType() {
	return GisgraphyServiceType.FULLTEXT;
    }
    
    /* (non-Javadoc)
     * @see com.gisgraphy.servlet.GisgraphyServlet#getErrorVisitor(java.lang.String)
     */
    @Override
    public IoutputFormatVisitor getErrorVisitor(String errorMessage) {
	return new FulltextErrorVisitor(errorMessage);
    }

}
