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
package com.gisgraphy.reversegeocoding;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.addressparser.AddressResultsDtoSerializer;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.serializer.common.UniversalSerializerConstant;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.service.ServiceException;
import com.gisgraphy.stats.StatsUsageType;
import com.vividsolutions.jts.geom.Point;

/**
 * Default (threadsafe) implementation of {@link IReverseGeocodingService}.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
@Service
public class ReverseGeocodingService implements IReverseGeocodingService {

	private static final ArrayList<Address> NO_ADDRESS_LIST = new ArrayList<Address>();

	@Autowired
	protected IOpenStreetMapDao openStreetMapDao;

	@Resource
	protected IStatsUsageService statsUsageService;
	
	@Autowired
	protected AddressHelper addressHelper;
	
	AddressResultsDtoSerializer addressResultsDtoSerializer = new AddressResultsDtoSerializer();

	/**
	 * The logger
	 */
	protected static final Logger logger = LoggerFactory
			.getLogger(ReverseGeocodingService.class);


	public AddressResultsDto executeQuery(ReverseGeocodingQuery query)
			throws ServiceException {
		Assert.notNull(query, "Can not execute a null query");
		Point point = query.getPoint();
		Assert.notNull(point, "Can not execute a query without a valid point");
		long start = System.currentTimeMillis();
		statsUsageService.increaseUsage(StatsUsageType.REVERSEGEOCODING);
		OpenStreetMap openStreetMap = openStreetMapDao.getNearestRoadFrom(point);
		if (openStreetMap==null){
			logger.debug("no road found, try to search deeper");
			openStreetMap =openStreetMapDao.getNearestFrom(point);
		}
		if (openStreetMap!= null){
			logger.debug("found a street "+openStreetMap);
			if (openStreetMap.getHouseNumbers()!=null && openStreetMap.getHouseNumbers().size() >=1 ){
				logger.debug("the street has "+openStreetMap.getHouseNumbers().size()+" housenumbers");
				HouseNumberDistance houseNumberDistance = addressHelper.getNearestHouse(openStreetMap.getHouseNumbers(), point);
				if (houseNumberDistance!=null){
					Address address = addressHelper.buildAddressFromHouseNumberDistance(houseNumberDistance);
					if (address!=null){
						logger.debug("found an address at a house number level");
						List<Address> addresses = new ArrayList<Address>();
						addresses.add(address);
						long end = System.currentTimeMillis();
						long qTime = end - start;
						logger.info(query + " took " + (qTime) + " ms and returns a result");
						return new AddressResultsDto(addresses, qTime);
					}  else {
						logger.debug("found an address at a street  level");
						List<Address> addresses = new ArrayList<Address>();
						addresses.add(address);
						long end = System.currentTimeMillis();
						long qTime = end - start;
						logger.info(query + " took " + (qTime) + " ms and returns a result");
						return new AddressResultsDto(addresses, qTime);
					}
				}
			} else {
				logger.debug("the street has no housenumbers");
				Address address = addressHelper.buildAddressFromOpenstreetMapAndPoint(openStreetMap,point);
				if (address!=null){
					List<Address> addresses = new ArrayList<Address>();
					addresses.add(address);
					long end = System.currentTimeMillis();
					long qTime = end - start;
					logger.info(query + " took " + (qTime) + " ms and returns a result");
					return new AddressResultsDto(addresses, qTime);
				} 
			}
		} 
		long end = System.currentTimeMillis();
		long qTime = end - start;
		logger.info(query + " took " + (qTime) + " ms and doeant returns no result");
		return new AddressResultsDto(NO_ADDRESS_LIST, qTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.service.IQueryProcessor#executeAndSerialize(com.gisgraphy.domain.geoloc.service.AbstractGisQuery,
	 *      java.io.OutputStream)
	 */
	public void executeAndSerialize(ReverseGeocodingQuery query, OutputStream outputStream)
			throws ServiceException {
		Assert.notNull(query, "Can not execute a null query");
		Assert.notNull(outputStream,
				"Can not serialize into a null outputStream");
	AddressResultsDto AddressResultDto = executeQuery(query);
	Map<String, Object> extraParameter = new HashMap<String, Object>();
	extraParameter.put(UniversalSerializerConstant.CALLBACK_METHOD_NAME, query.getCallback());
	addressResultsDtoSerializer.serialize(outputStream, query.getOutputFormat(), AddressResultDto, false,extraParameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.service.IQueryProcessor#executeQueryToString(com.gisgraphy.domain.geoloc.service.AbstractGisQuery)
	 */
	public String executeQueryToString(ReverseGeocodingQuery query)
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
