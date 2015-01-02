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
package com.gisgraphy.reversegeocoding;

import javax.servlet.http.HttpServletRequest;

import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.vividsolutions.jts.geom.Point;

/**
 * A GeolocQuery Query builder. it build geolocQuery from HTTP Request
 * 
 * @see Pagination
 * @see Output
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class ReverseGeocodingQueryHttpBuilder {
	
    
  
	private static ReverseGeocodingQueryHttpBuilder instance = new ReverseGeocodingQueryHttpBuilder();
	
	public static ReverseGeocodingQueryHttpBuilder getInstance() {
		return instance;
	}

    
    /**
     * @param req
     *                an HttpServletRequest to construct a {@link ReverseGeocodingQuery}
     */
    public ReverseGeocodingQuery buildFromHttpRequest(HttpServletRequest req) {
    
	// point
	Float latitude=null;
	Float longitude=null;
	// lat
	try {
			String latParameter = req.getParameter(ReverseGeocodingQuery.LAT_PARAMETER);
				if (latParameter!=null){
					latitude = GeolocHelper.parseInternationalDouble(latParameter);
				} else  {
					throw new ReverseGeocodingException("latitude is empty");
				} 
		} catch (Exception e) {
			throw new ReverseGeocodingException("latitude is not correct");
		}

	// long
	try {
	    String longParameter = req
		    .getParameter(ReverseGeocodingQuery.LONG_PARAMETER);
	    if (longParameter!=null){
	    	longitude = GeolocHelper.parseInternationalDouble(longParameter);
	    } else {
	    		throw new ReverseGeocodingException(
	    		"longitude is empty");
	    	}
	} catch (Exception e) {
	    throw new ReverseGeocodingException(
		    "longitude is not correct ");
	}
	
	// point
	Point point = null ;
	try {
		if (latitude!=null && longitude!=null ){
	    point = GeolocHelper.createPoint(longitude, latitude);
		} 
	} catch (RuntimeException e1) {
	    	throw new ReverseGeocodingException("can not determine Point");
	}
	
	ReverseGeocodingQuery geolocQuery = new ReverseGeocodingQuery(point);

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
	
	// apiKey
	String apiKey = req.getParameter(GisgraphyServlet.APIKEY_PARAMETER);
	geolocQuery.setApikey(apiKey);
	
	String CallBackParameter = req.getParameter(ReverseGeocodingQuery.CALLBACK_PARAMETER);
	if (CallBackParameter!=null){
	    geolocQuery.withCallback(CallBackParameter);
	}
	
	geolocQuery.withOutput(output);
	return geolocQuery;

}
}
