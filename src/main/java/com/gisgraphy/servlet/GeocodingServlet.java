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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gisgraphy.addressparser.AddressQuery;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.geocoding.IGeocodingService;

/**
 * Provides a servlet Wrapper around The Gisgraphy geocoding Service, it Maps web
 * parameters to create a {@linkplain AddressQuery}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeocodingServlet extends AbstractAddressServlet {


   

    public void setGeocodingService(IGeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }



    /**
     * Default serialVersionUID
     */
    private static final long serialVersionUID = -9054548241743095743L;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(GeocodingServlet.class);
  

    private IGeocodingService geocodingService;

  

    /* (non-Javadoc)
     * @see com.gisgraphy.servlet.GisgraphyServlet#getGisgraphyServiceType()
     */
    @Override
    public GisgraphyServiceType getGisgraphyServiceType() {
	return GisgraphyServiceType.GEOCODING;
    }



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
	    geocodingService = (IGeocodingService) springContext
		    .getBean("geocodingService");
	    logger
		    .info("geocodingService is injected :"
			    + geocodingService);
	} catch (Exception e) {
	    logger.error("Can not start GeocodingServlet : " + e.getMessage(),e);
	}
    }



    @Override
    public void processRequest(AddressQuery query, HttpServletResponse resp) throws IOException {
    	geocodingService.geocodeAndSerialize(query, resp.getOutputStream());
    }



	/* (non-Javadoc)
	 * @see com.gisgraphy.servlet.AbstractAddressServlet#checkparameter()
	 */
	@Override
	public boolean checkparameter() {
		//we do not check parameter because address parameter is not required since we can provide structured address
		//and country parameter is not required since v 4.0
		return false;
	}

}
