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
package com.gisgraphy.street;

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

import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.StreetDistance;
import com.gisgraphy.domain.valueobject.StreetSearchResultsDto;
import com.gisgraphy.serializer.common.UniversalSerializerConstant;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.service.ServiceException;
import com.gisgraphy.stats.StatsUsageType;

/**
 * Default (threadsafe) implementation of {@link IStreetSearchEngine}.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class StreetSearchEngine implements IStreetSearchEngine {

    @Resource
    IOpenStreetMapDao openStreetMapDao;

    @Resource
    IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer;

    @Resource
    IStatsUsageService statsUsageService;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(StreetSearchEngine.class);

    public StreetSearchResultsDto executeQuery(StreetSearchQuery query)
	    throws ServiceException {
	statsUsageService.increaseUsage(StatsUsageType.STREET);
	Assert.notNull(query, "Can not execute a null query");
	long start = System.currentTimeMillis();

	List<StreetDistance> results = openStreetMapDao
		.getNearestAndDistanceFrom(query.getPoint(), query.getRadius(),
			query.getFirstPaginationIndex(), query
				.getMaxNumberOfResults(),
			query.getStreetType(), query.getOneWay(), query
				.getName(),query.getStreetSearchMode(), query.hasDistanceField());

	long end = System.currentTimeMillis();
	long qTime = end - start;
	logger.info(query + " took " + (qTime) + " ms and returns "
		+ results.size() + " results");
	return new StreetSearchResultsDto(results, qTime,query.getName());

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.IQueryProcessor#executeAndSerialize(com.gisgraphy.domain.geoloc.service.AbstractGisQuery,
     *      java.io.OutputStream)
     */
    public void executeAndSerialize(StreetSearchQuery query, OutputStream outputStream)
	    throws ServiceException {
	Assert.notNull(query, "Can not execute a null query");
	Assert.notNull(outputStream,
		"Can not serialize into a null outputStream");
	StreetSearchResultsDto streetSearchResultsDto = executeQuery(query);
	Map<String, Object> extraParameter = new HashMap<String, Object>();
	extraParameter.put(StreetSearchResultsDtoSerializer.START_PAGINATION_INDEX_EXTRA_PARAMETER, query.getFirstPaginationIndex());
	extraParameter.put(UniversalSerializerConstant.CALLBACK_METHOD_NAME, query.getCallback());
	streetSearchResultsDtoSerializer.serialize(outputStream, query
		.getOutputFormat(), streetSearchResultsDto, query.isOutputIndented(),
		extraParameter);
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.IQueryProcessor#executeQueryToString(com.gisgraphy.domain.geoloc.service.AbstractGisQuery)
     */
    public String executeQueryToString(StreetSearchQuery query)
	    throws ServiceException {
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	executeAndSerialize(query, outputStream);
	try {
	    return outputStream.toString(Constants.CHARSET);
	} catch (UnsupportedEncodingException e) {
	   throw new StreetSearchException("unsupported encoding "+Constants.CHARSET);
	}
    }

}
