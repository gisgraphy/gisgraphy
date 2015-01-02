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
package com.gisgraphy.geoloc;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.IGisDao;
import com.gisgraphy.domain.repository.IRepositoryStrategy;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.geocoloc.IGeolocSearchEngine;
import com.gisgraphy.serializer.common.UniversalSerializerConstant;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.service.ServiceException;
import com.gisgraphy.stats.StatsUsageType;

/**
 * Default (threadsafe) implementation of {@link IGeolocSearchEngine}.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class GeolocSearchEngine implements IGeolocSearchEngine {
	
	/**
	 * very usefull when import is running
	 */
	public static boolean disableLogging=false;
	

    @Resource
    IStatsUsageService statsUsageService;
    
    @Resource
    IGeolocResultsDtoSerializer geolocResultsDtoSerializer;

   
    @Resource
    IRepositoryStrategy repositoryStrategy;
    

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(GeolocSearchEngine.class);



    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.geoloc.IGeolocSearchEngine#executeQuery(com.gisgraphy.domain.geoloc.service.fulltextsearch.FulltextQuery)
     */
    public GeolocResultsDto executeQuery(GeolocQuery query)
	    throws ServiceException {
	statsUsageService.increaseUsage(StatsUsageType.GEOLOC);
	Assert.notNull(query, "Can not execute a null query");
	long start = System.currentTimeMillis();
	Class<?> placetype = query.getPlaceType();
	IGisDao<?> dao = repositoryStrategy
		.getDao(GisFeature.class);
	if (placetype != null) {
	    dao = repositoryStrategy.getDao(placetype);
	}
	if (dao == null) {
	    throw new GeolocSearchException(
		    "No gisFeatureDao or no placetype can be found for "
			    + placetype + " can be found.");
	}
	List<GisFeatureDistance> results = dao.getNearestAndDistanceFrom(query
		.getPoint(), query.getRadius(),
		query.getFirstPaginationIndex(), query.getMaxNumberOfResults(),query.hasDistanceField(), query.hasMunicipalityFilter());

	long end = System.currentTimeMillis();
	long qTime = end - start;
	if (!disableLogging){
		logger.info(query + " took " + (qTime) + " ms and returns "
				+ results.size() + " results");
	}
	return new GeolocResultsDto(results, qTime);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.IQueryProcessor#executeAndSerialize(com.gisgraphy.domain.geoloc.service.AbstractGisQuery,
     *      java.io.OutputStream)
     */
    public void executeAndSerialize(GeolocQuery query, OutputStream outputStream)
	    throws ServiceException {
	Assert.notNull(query, "Can not execute a null query");
	Assert.notNull(outputStream,
		"Can not serialize into a null outputStream");
	GeolocResultsDto geolocResultsDto = executeQuery(query);
	Map<String, Object> extraParameter = new HashMap<String, Object>();
	extraParameter.put(GeolocResultsDtoSerializer.START_PAGINATION_INDEX_EXTRA_PARAMETER, query.getFirstPaginationIndex());
	extraParameter.put(UniversalSerializerConstant.CALLBACK_METHOD_NAME, query.getCallback());
	geolocResultsDtoSerializer.serialize(outputStream, query.getOutputFormat(), geolocResultsDto, query.isOutputIndented(),extraParameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.IQueryProcessor#executeQueryToString(com.gisgraphy.domain.geoloc.service.AbstractGisQuery)
     */
    public String executeQueryToString(GeolocQuery query)
	    throws ServiceException {
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	executeAndSerialize(query, outputStream);
	try {
	    return outputStream.toString(Constants.CHARSET);
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException("unknow encoding "+Constants.CHARSET);
	}
    }

}
