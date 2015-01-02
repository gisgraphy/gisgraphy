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
package com.gisgraphy.street;

import javax.servlet.http.HttpServletRequest;

import com.gisgraphy.geoloc.GeolocQuery;
import com.gisgraphy.geoloc.GeolocQueryHttpBuilder;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.gisgraphy.servlet.StreetServlet;
import com.vividsolutions.jts.geom.Point;

/**
 *  a query to be execute by the @link {@link StreetSearchEngine}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class StreetSearchQueryHttpBuilder extends GeolocQueryHttpBuilder {
	
    private static StreetSearchQueryHttpBuilder instance = new StreetSearchQueryHttpBuilder();
	
	public static StreetSearchQueryHttpBuilder getInstance() {
		return instance;
	}
    
	@Override
	protected StreetSearchQuery constructMinimalQuery(Point point, double radius) {
		StreetSearchQuery streetSearchQuery = new StreetSearchQuery(point,radius);
		return streetSearchQuery;
	}
	
    @Override
    public StreetSearchQuery buildFromHttpRequest(HttpServletRequest req) {
	StreetSearchQuery streetSearchQuery = (StreetSearchQuery) super.buildFromHttpRequest(req);
	//streettype
	streetSearchQuery.withStreetType(StreetType.getFromString(req
	.getParameter(StreetSearchQuery.STREETTYPE_PARAMETER)));
	
	//OneWay
	String oneWayParameter = req
		.getParameter(StreetSearchQuery.ONEWAY_PARAMETER);
	if ("true".equalsIgnoreCase(oneWayParameter)
		|| "on".equalsIgnoreCase(oneWayParameter)) {
		streetSearchQuery.withOneWay(Boolean.TRUE);
	}
	else if ("false".equalsIgnoreCase(oneWayParameter)){
		streetSearchQuery.withOneWay(Boolean.FALSE);
	}
	//name
	streetSearchQuery.withName(req.getParameter(StreetSearchQuery.NAME_PARAMETER));
	
	String StreetSearchModeparameter = req.getParameter(StreetServlet.STREET_SEARCH_MODE_PARAMETER);
	streetSearchQuery.withStreetSearchMode(StreetSearchMode.getFromString(StreetSearchModeparameter));
	
	// apiKey
	String apiKey = req.getParameter(GisgraphyServlet.APIKEY_PARAMETER);
	streetSearchQuery.setApikey(apiKey);
	
	return streetSearchQuery;

    }

	
       /**
	 * @return true if the point is required
	 */
    @Override
	protected boolean isPointRequired(){
		return  false;
	    }
   
    @Override
    protected int getMaxResults() {
		return StreetSearchQuery.DEFAULT_MAX_RESULTS;
	}
	
    
}
