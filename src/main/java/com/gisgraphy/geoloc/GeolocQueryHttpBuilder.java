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
package com.gisgraphy.geoloc;

import javax.servlet.http.HttpServletRequest;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.GisHelper;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.service.AbstractGisQuery;
import com.gisgraphy.servlet.FulltextServlet;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.vividsolutions.jts.geom.Point;

/**
 * A GeolocQuery Query builder. it build geolocQuery from HTTP Request
 * 
 * @see Pagination
 * @see Output
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeolocQueryHttpBuilder {
	
    
  
	private static GeolocQueryHttpBuilder instance = new GeolocQueryHttpBuilder();
	
	public static GeolocQueryHttpBuilder getInstance() {
		return instance;
	}

    
    /**
     * @param req
     *                an HttpServletRequest to construct a {@link GeolocQuery}
     */
    public GeolocQuery buildFromHttpRequest(HttpServletRequest req) {
    
	// point
	Float latitude=null;
	Float longitude=null;
	// lat
	try {
			String latParameter = req.getParameter(GeolocQuery.LAT_PARAMETER);
				if (latParameter!=null){
					latitude = GeolocHelper.parseInternationalDouble(latParameter);
				} else if(isPointRequired()) {
					throw new GeolocSearchException("latitude is empty");
				} 
		} catch (Exception e) {
			throw new GeolocSearchException("latitude is not correct");
		}

	// long
	try {
	    String longParameter = req
		    .getParameter(GeolocQuery.LONG_PARAMETER);
	    if (longParameter!=null){
	    	longitude = GeolocHelper.parseInternationalDouble(longParameter);
	    } else if(isPointRequired()){
	    		throw new GeolocSearchException(
	    		"longitude is empty");
	    	}
	} catch (Exception e) {
	    throw new GeolocSearchException(
		    "longitude is not correct ");
	}
	
	
	// point
	
	Point point = null ;
	try {
		if (latitude!=null && longitude!=null ){
	    point = GeolocHelper.createPoint(longitude, latitude);
		} 
	} catch (RuntimeException e1) {
	    	throw new GeolocSearchException("can not determine Point");
	}
	
	// radius
	double radius;
	try {
	    radius = GeolocHelper.parseInternationalDouble(req
		    .getParameter(GeolocQuery.RADIUS_PARAMETER));
	} catch (Exception e) {
	    radius = GeolocQuery.DEFAULT_RADIUS;
	}
	
	GeolocQuery geolocQuery = constructMinimalQuery(point, radius);

	// pagination
	Pagination pagination = null;
	int from;
	int to;
	try {
	    from = Integer.valueOf(
		    req.getParameter(GisgraphyServlet.FROM_PARAMETER)).intValue();
	} catch (Exception e) {
	    from = Pagination.DEFAULT_FROM;
	}

	try {
	    to = Integer
		    .valueOf(req.getParameter(FulltextServlet.TO_PARAMETER))
		    .intValue();
	} catch (NumberFormatException e) {
	    to = from+AbstractGisQuery.DEFAULT_NB_RESULTS-1;
	}

	pagination = Pagination.paginateWithMaxResults(getMaxResults()).from(from).to(to)
		.limitNumberOfResults(getMaxResults());
	// output format
	OutputFormat format = OutputFormat.getFromString(req
		.getParameter(GisgraphyServlet.FORMAT_PARAMETER));
	format = OutputFormatHelper.getDefaultForServiceIfNotSupported(format, GisgraphyServiceType.GEOLOC);
	Output output = Output.withFormat(format);

	// indent
	if ("true".equalsIgnoreCase(req
		.getParameter(GisgraphyServlet.INDENT_PARAMETER))
		|| "on".equalsIgnoreCase(req
			.getParameter(GisgraphyServlet.INDENT_PARAMETER))) {
	    output.withIndentation();
	}
	
	// municipality
		if ("true".equalsIgnoreCase(req
			.getParameter(GeolocQuery.MUNICIPALITY_PARAMETER))
			|| "on".equalsIgnoreCase(req
				.getParameter(GeolocQuery.MUNICIPALITY_PARAMETER))) {
		    geolocQuery.withMunicipalityFilter(true);
		}

	//placetype
	Class<? extends GisFeature> clazz = GisHelper
		.getClassEntityFromString(req
			.getParameter(GeolocQuery.PLACETYPE_PARAMETER));

	//distance field
	if ("false".equalsIgnoreCase(req
		.getParameter(GeolocQuery.DISTANCE_PARAMETER))
		|| "off".equalsIgnoreCase(req
			.getParameter(GeolocQuery.DISTANCE_PARAMETER))) {
	    geolocQuery.withDistanceField(false);
	}
	
	// apiKey
	String apiKey = req.getParameter(GisgraphyServlet.APIKEY_PARAMETER);
	geolocQuery.setApikey(apiKey);
	
	String CallBackParameter = req.getParameter(GeolocQuery.CALLBACK_PARAMETER);
	if (CallBackParameter!=null){
	    geolocQuery.withCallback(CallBackParameter);
	}
	
	geolocQuery.withPagination(pagination);
	if(clazz == null){
		geolocQuery.withPlaceType(GisgraphyConfig.defaultGeolocSearchPlaceTypeClass);
		}else {
			geolocQuery.withPlaceType(clazz);
    }
	geolocQuery.withOutput(output);
	return geolocQuery;

}


	protected int getMaxResults() {
		return GeolocQuery.DEFAULT_MAX_RESULTS;
	}

	/**
	 * Create a basic GeolocQuery. this method must be overide 
	 * if we need to create inheritance object
	 * 
	 * @param point the JTS point to create the query
	 * @param radius the radius to search around
	 */
	protected GeolocQuery constructMinimalQuery(Point point, double radius) {
		GeolocQuery geolocQuery = new GeolocQuery(point,radius);
		return geolocQuery;
	}
	
	/**
	 * @return true if the point is required
	 */
	protected boolean isPointRequired(){
		return  true;
	    }
}
