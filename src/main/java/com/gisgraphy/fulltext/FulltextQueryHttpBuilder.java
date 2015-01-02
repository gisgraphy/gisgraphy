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
/**
 *
 */
package com.gisgraphy.fulltext;

import javax.servlet.http.HttpServletRequest;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.geoloc.GeolocQuery;
import com.gisgraphy.geoloc.GeolocSearchException;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.GisHelper;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.service.AbstractGisQuery;
import com.gisgraphy.servlet.FulltextServlet;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.vividsolutions.jts.geom.Point;

/**
 * A Fulltext Query builder. it build Fulltext query from HTTP Request
 * 
 * @see Pagination
 * @see Output
 * @see IFullTextSearchEngine
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class FulltextQueryHttpBuilder {
	
	
 private static FulltextQueryHttpBuilder instance = new FulltextQueryHttpBuilder();
 
 public static FulltextQueryHttpBuilder getInstance(){
	 return instance;
	 
 }
    /**
     * @param req
     *                an HttpServletRequest to construct a {@link FulltextQueryHttpBuilder}
     */
    public FulltextQuery buildFromRequest(HttpServletRequest req) {
    	FulltextQuery query = null;
	String httpQueryParameter = req.getParameter(FulltextQuery.QUERY_PARAMETER);
	if (httpQueryParameter != null){
		query = new FulltextQuery(httpQueryParameter.trim());
	}
	if (httpQueryParameter == null || "".equals(httpQueryParameter.trim())) {
	    throw new FullTextSearchException("query is not specified or empty");
	}
	if (httpQueryParameter.length() > FulltextQuery.QUERY_MAX_LENGTH) {
	    throw new FullTextSearchException("query is limited to "
		    + FulltextQuery.QUERY_MAX_LENGTH + "characters");
	}
	
	// point
	Float latitude=null;
	Float longitude=null;
	// lat
	try {
			String latParameter = req.getParameter(FulltextQuery.LAT_PARAMETER);
				if (latParameter!=null && !latParameter.trim().equals("")){
					
					latitude = GeolocHelper.parseInternationalDouble(latParameter);
					if (latitude < -90 || latitude > 90){
						throw new GeolocSearchException("latitude is not correct"); 
					}
				} 
		} catch (Exception e) {
			throw new GeolocSearchException("latitude is not correct");
		}

	// long
	try {
	    String longParameter = req
		    .getParameter(FulltextQuery.LONG_PARAMETER);
	    if (longParameter!=null && !longParameter.trim().equals("")){
	    	longitude = GeolocHelper.parseInternationalDouble(longParameter);
	    	if (latitude < -180 || latitude > 180){
				throw new GeolocSearchException("latitude is not correct"); 
			}
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
	query.around(point);
	
	// radius
	double radius;
	try {
	    radius = GeolocHelper.parseInternationalDouble(req
		    .getParameter(FulltextQuery.RADIUS_PARAMETER));
	} catch (Exception e) {
	    radius = GeolocQuery.DEFAULT_RADIUS;
	}
	query.withRadius(radius);
	
	// pagination
	Pagination pagination = null;
	int from;
	int to;
	try {
	    from = Integer.valueOf(
		    req.getParameter(FulltextServlet.FROM_PARAMETER))
		    .intValue();
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

	pagination = Pagination.paginateWithMaxResults(FulltextQuery.DEFAULT_MAX_RESULTS).from(from).to(to)
		.limitNumberOfResults(FulltextQuery.DEFAULT_MAX_RESULTS);
	// output
	OutputFormat format = OutputFormat.getFromString(req
		.getParameter(FulltextServlet.FORMAT_PARAMETER));
	format = OutputFormatHelper.getDefaultForServiceIfNotSupported(format, GisgraphyServiceType.FULLTEXT);
	OutputStyle style = OutputStyle.getFromString(req
		.getParameter(FulltextQuery.STYLE_PARAMETER));
	String languageparam = req.getParameter(FulltextQuery.LANG_PARAMETER);
	Output output = Output.withFormat(format).withLanguageCode(
		languageparam).withStyle(style);

	// placetype
	String[] placetypeParameters = req
		.getParameterValues(FulltextQuery.PLACETYPE_PARAMETER);
	Class<? extends GisFeature>[] clazzs = null;
	if (placetypeParameters!=null){
		clazzs = new Class[placetypeParameters.length]; 
		for (int i=0;i<placetypeParameters.length;i++){
			Class<? extends GisFeature> classEntityFromString = GisHelper.getClassEntityFromString(placetypeParameters[i]);
				clazzs[i]= classEntityFromString;
		}
	}
	

	// countrycode
	String countrycodeParam = req
		.getParameter(FulltextQuery.COUNTRY_PARAMETER);
	if (countrycodeParam == null){
		query.limitToCountryCode(null);
	
	} else {
		query.limitToCountryCode(countrycodeParam
				.toUpperCase());
		
	}

	//indentation
	if ("true".equalsIgnoreCase(req
		.getParameter(GisgraphyServlet.INDENT_PARAMETER))
		|| "on".equalsIgnoreCase(req
			.getParameter(GisgraphyServlet.INDENT_PARAMETER))) {
	    output.withIndentation();
	}
	
	//auto suggestion / auto completion
	if ("true".equalsIgnoreCase(req
			.getParameter(FulltextQuery.SUGGEST_PARAMETER))
			|| "on".equalsIgnoreCase(req
				.getParameter(FulltextQuery.SUGGEST_PARAMETER))) {
		    query.withSuggest(true);
		}
	
	//spellchecking
	if ("true".equalsIgnoreCase(req
			.getParameter(FulltextQuery.SPELLCHECKING_PARAMETER))
			|| "on".equalsIgnoreCase(req
				.getParameter(FulltextQuery.SPELLCHECKING_PARAMETER))) {
		    query.withSpellChecking();
		}
	else if ("false".equalsIgnoreCase(req.getParameter(FulltextQuery.SPELLCHECKING_PARAMETER))) {
		query.withoutSpellChecking();
	}
	
	if ("true".equalsIgnoreCase(req
		.getParameter(FulltextQuery.ALLWORDSREQUIRED_PARAMETER))
		|| "on".equalsIgnoreCase(req
			.getParameter(FulltextQuery.ALLWORDSREQUIRED_PARAMETER))) {
	    query.withAllWordsRequired(true);
	}
	else if ("false".equalsIgnoreCase(req.getParameter(FulltextQuery.ALLWORDSREQUIRED_PARAMETER))) {
		query.withAllWordsRequired(false);
	}
	
	// apiKey
	String apiKey = req.getParameter(GisgraphyServlet.APIKEY_PARAMETER);
	query.setApikey(apiKey);

	
	query.withPagination(pagination);
	query.withPlaceTypes(clazzs);
	query.withOutput(output);
	
	return query;
    } 

    

}
