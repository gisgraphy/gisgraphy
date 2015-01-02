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
import com.gisgraphy.addressparser.IAddressParserService;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsageType;

/**
 * Provides a servlet Wrapper around The Gisgraphy address parser Service, it Maps web
 * parameters to create a {@linkplain AddressQuery}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class AddressParserServlet extends AbstractAddressServlet {

   
    private IAddressParserService addressParser;
    
    private IStatsUsageService statsUsageService;

    /**
     * Default serialVersionUID
     */
    private static final long serialVersionUID = -9054548241743095743L;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(AddressParserServlet.class);
  

    /* (non-Javadoc)
     * @see com.gisgraphy.servlet.GisgraphyServlet#getGisgraphyServiceType()
     */
    @Override
    public GisgraphyServiceType getGisgraphyServiceType() {
	return GisgraphyServiceType.ADDRESS_PARSER;
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
	    addressParser = (IAddressParserService) springContext
		    .getBean("addressParser");
	    statsUsageService = (IStatsUsageService) springContext
			    .getBean("statsUsageService");
	    logger
		    .info("Address parser is injected :"
			    + addressParser);
	    logger
	    .info("statsusage service is injected :"
		    + statsUsageService);
	} catch (Exception e) {
	    logger.error("Can not start AdressParserServlet : " + e.getMessage(),e);
	}
    }



    @Override
    public void processRequest(AddressQuery query, HttpServletResponse resp) throws IOException {
   	statsUsageService.increaseUsage(StatsUsageType.ADDRESSPARSER);
	addressParser.executeAndSerialize(query, resp.getOutputStream());
    }



    public void setAddressParserService(IAddressParserService addressParser) {
        this.addressParser = addressParser;
    }
    
	public void setStatsUsageService(IStatsUsageService statsUsageService) {
		this.statsUsageService = statsUsageService;
	}



	@Override
	public boolean checkparameter() {
		return true;
	}

}
