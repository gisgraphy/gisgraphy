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
package com.gisgraphy.fulltext;

import static com.gisgraphy.helper.StringHelper.isEmptyString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.GisFeatureDao;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.HouseNumberAddressDto;
import com.gisgraphy.fulltext.suggest.GisgraphySearchEntry;
import com.gisgraphy.fulltext.suggest.GisgraphySearchResponse;
import com.gisgraphy.fulltext.suggest.GisgraphySearchResponseHeader;
import com.gisgraphy.fulltext.suggest.GisgraphySearchResult;
import com.gisgraphy.geocoding.GeocodingHelper;
import com.gisgraphy.geoloc.ZipcodeNormalizer;
import com.gisgraphy.helper.CountryInfo;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.service.ServiceException;
import com.gisgraphy.stats.StatsUsageType;
import com.gisgraphy.street.HouseNumberDeserializer;
import com.gisgraphy.street.HouseNumberDto;
import com.vividsolutions.jts.geom.Point;

/**
 * Default (threadsafe) implementation of {@link IFullTextSearchEngine}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class FullTextSearchEngine implements IFullTextSearchEngine {


	/**
	 * very usefull when import is running
	 */
	public static boolean disableLogging=false;


	protected static final Logger logger = LoggerFactory
			.getLogger(FullTextSearchEngine.class);

	private HttpClient httpClient;

	private IsolrClient solrClient;

	FulltextResultDtoBuilder builder = new FulltextResultDtoBuilder();
	HouseNumberDeserializer houseNumberDeserializer = new HouseNumberDeserializer();

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	@Qualifier("gisFeatureDao")
	private GisFeatureDao gisFeatureDao;

	@Autowired
	IStatsUsageService statsUsageService;

	/**
	 * Default constructor needed by cglib and spring
	 */
	@SuppressWarnings("unused")
	private FullTextSearchEngine() {
		super();
	}

	/**
	 * @param multiThreadedHttpConnectionManager
	 *                The
	 * @link {@link MultiThreadedHttpConnectionManager} that the fulltext search
	 *       engine will use
	 * @throws FullTextSearchException
	 *                 If an error occured
	 */
	@Autowired
	public FullTextSearchEngine(
			@Qualifier("multiThreadedHttpConnectionManager")
			MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager)
					throws FullTextSearchException {
		Assert.notNull(multiThreadedHttpConnectionManager,
				"multiThreadedHttpConnectionManager can not be null");
		HttpConnectionManagerParams p = new HttpConnectionManagerParams();
		p.setSoTimeout(0);
		p.setConnectionTimeout(0);
		multiThreadedHttpConnectionManager.setParams(p);
		this.httpClient = new HttpClient(multiThreadedHttpConnectionManager);
		if (this.httpClient == null) {
			throw new FullTextSearchException(
					"Can not instanciate http client with multiThreadedHttpConnectionManager : "
							+ multiThreadedHttpConnectionManager);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IFullTextSearchEngine#executeAndSerialize(com.gisgraphy.domain.geoloc.service.fulltextsearch.FulltextQuery,
	 *      java.io.OutputStream)
	 */
	public void executeAndSerialize(FulltextQuery query,
			OutputStream outputStream) throws FullTextSearchException {
		statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
		Assert.notNull(query, "Can not execute a null query");
		Assert.notNull(outputStream,
				"Can not serialize into a null outputStream");
		String queryString = ZipcodeNormalizer.normalize(query.getQuery(), query.getCountryCode());
		query.withQuery(queryString);
		if (query.isSuggest()){
			HouseNumberAddressDto dto = GeocodingHelper.findHouseNumber(queryString,query.getCountryCode());
			if (dto !=null && dto.getHouseNumber()!=null){
				if (!dto.getAddressWithoutHouseNumber().trim().isEmpty()){
					query.withQuery(dto.getAddressWithoutHouseNumber());
					try {
						ByteArrayOutputStream outputStreamString = new ByteArrayOutputStream();
						doExecuteAndSerialize(query, outputStreamString);
						String feed  = outputStreamString.toString(Constants.CHARSET);
						//	String feed = executeQueryToString(query);
						GisgraphySearchResult feedAsObj = mapper.readValue(feed, GisgraphySearchResult.class);
						/*for (GisgraphySearchEntry entry:feedAsObj.getResponse().getDocs()){
							logger.error(entry.toString());
						}*/
						feedAsObj = updateFeed(feedAsObj,dto.getHouseNumber());
						//logger.debug("----------------------------------------------------------------------------------------");
						/*for (GisgraphySearchEntry entry:feedAsObj.getResponse().getDocs()){
							logger.error(entry.toString());
						}*/
						mapper.writeValue(outputStream, feedAsObj);
						return;
					} catch (Exception e) {
						String message = e.getCause()!=null?e.getCause().getMessage():e.getMessage();
						logger
						.error("An error has occurred during suggest search of query "
								+ query + " : " + message,e);
						throw new FullTextSearchException(message,e);
					}
				} else {
					//query is empty after HN removal
					GisgraphySearchResult result = new GisgraphySearchResult();
					result.setResponse(new GisgraphySearchResponse());
					result.setResponseHeader(new GisgraphySearchResponseHeader());
					try {
						mapper.writeValue(outputStream, result);
					} catch (Exception e) {
						String message = e.getCause()!=null?e.getCause().getMessage():e.getMessage();
						logger
						.error("An error has occurred during suggest search of query "
								+ query + " : " + message,e);
						throw new FullTextSearchException(message,e);
					}
				}
			} /*else {
			    try {
                    ByteArrayOutputStream outputStreamString = new ByteArrayOutputStream();
                    doExecuteAndSerialize(query, outputStreamString);
                    String feed  = outputStreamString.toString(Constants.CHARSET);
                    //  String feed = executeQueryToString(query);
                    GisgraphySearchResult feedAsObj = mapper.readValue(feed, GisgraphySearchResult.class);
                    if (feedAsObj !=null && feedAsObj.getResponse() != null || feedAsObj.getResponse().getDocs() !=null){
                        List<GisgraphySearchEntry> docs = feedAsObj.getResponse().getDocs();
                        for (GisgraphySearchEntry entry : docs){
                            if (entry.getCountryCode()!=null){
                                entry.setCountry(CountryInfo.countryLookupMap.get(entry.getCountryCode().toUpperCase()));
                         } 
                        }
                    }
                    mapper.writeValue(outputStream, feedAsObj);
                    return;
                } catch (Exception e) {
                    String message = e.getCause()!=null?e.getCause().getMessage():e.getMessage();
                    logger
                    .error("An error has occurred during suggest search of query "
                            + query + " : " + message,e);
                    throw new FullTextSearchException(message,e);
                }
			    
			}*/
			//if no HN found, we do the common process
		}
		//not a suggest query, do a classical search
		doExecuteAndSerialize(query, outputStream);

	}


	protected GisgraphySearchResult updateFeed(GisgraphySearchResult feedAsObj,String number) {
		if (number == null || number.isEmpty() || feedAsObj ==null || feedAsObj.getResponse() == null || feedAsObj.getResponse().getDocs() ==null){
			return feedAsObj;	
		}
		
		String lastName=null;
		String lastIsin=null;
		Point lastLocation=null;
		boolean housenumberFound =false;
		GisgraphySearchEntry candidate =null; 
		
		
		List<GisgraphySearchEntry> docs = feedAsObj.getResponse().getDocs();
		List<GisgraphySearchEntry> filtereddocs = new ArrayList<GisgraphySearchEntry>();
		for (GisgraphySearchEntry entry : docs){
			Point curLoc = GeolocHelper.createPoint(entry.getLng(),entry.getLat());
			if (!isEmptyString(entry.getName())){ 
				double distance;
				try {
					distance = GeolocHelper.distance(lastLocation, curLoc);
				} catch (Exception e) {
					distance=-1;
				}
				if(entry.getName().equalsIgnoreCase(lastName) && ((entry.getIsIn()!=null && entry.getIsIn().equalsIgnoreCase(lastIsin) && (lastLocation!=null && !(distance>12000))) || (lastLocation!=null && !(distance>12000)))){
					//logger.debug("same street : "+entry.getFeatureId()+" - "+entry.getName()+","+entry.getIsIn()+"(dist="+distance+")");
					if (housenumberFound){
						continue;
						//do nothing it has already been found in the street
					}else {
						
						housenumberFound = updateHouseNumber(number, entry);
						if (housenumberFound){
							//logger.debug("find hn for same name "+number+" for "+entry.getFeatureId()+" - "+entry.getLabel());
							mergeZip(candidate, entry);
							filtereddocs.add(entry);
							candidate = null;
						} else {
							//logger.debug("don't find hn for same name "+number+" for "+entry.getFeatureId()+" - "+entry.getLabel());
							mergeZip(candidate, entry);
							candidate = entry;
						}
					}
				} else { //the streetName is different,
					//logger.debug("not same street : "+entry.getFeatureId()+" - "+entry.getName()+","+entry.getIsIn()+"(dist="+distance+")");
					if (candidate!=null){
						//logger.debug("adding (not same street) candidate "+candidate.getFeatureId()+" - "+candidate.getLabel());
						filtereddocs.add(candidate);
					}
					housenumberFound = updateHouseNumber(number, entry);
					if (housenumberFound){
						//logger.debug("find hn for different name "+number+" for "+entry.getFeatureId()+" - "+entry.getLabel());
						filtereddocs.add(entry);
						candidate = null;
					} else {
						//logger.debug("don't find hn for different name "+number+" for "+entry.getFeatureId()+" - "+entry.getLabel());
						candidate = entry;
					}
				}
			} else {
				//logger.debug("no name for "+entry.getLabel());
				if (candidate!=null){
				//	logger.debug ("adding candidate without name "+entry.getFeatureId()+" - "+entry.getLabel());
					filtereddocs.add(candidate);
					candidate = null;
				}
				housenumberFound = updateHouseNumber(number, entry);
				if (housenumberFound){
					//logger.debug("find hn for null name "+number+" for "+entry.getFeatureId()+" - "+entry.getLabel());
					filtereddocs.add(entry);
					candidate = null;
				} else {
					//logger.debug("don't find hn for null name "+number+" for "+entry.getFeatureId()+" - "+entry.getLabel());
					candidate = entry;
				}
				
				
			}
			lastName=entry.getName();
			lastIsin = entry.getIsIn();
			lastLocation=curLoc;	
			 if (entry.getCountryCode()!=null){
	                entry.setCountry(CountryInfo.countryLookupMap.get(entry.getCountryCode().toUpperCase()));
	         }
			entry.setLabel(GeocodingHelper.processLabel(entry));
		}
		if (candidate!=null){
			//logger.debug("adding last candidate"+candidate.getFeatureId()+" - "+candidate.getLabel());
			filtereddocs.add(candidate);
		}
		feedAsObj.getResponse().setDocs(filtereddocs);
		return feedAsObj;
	}

	/**
	 * sometimes zip are present in some segment and not in others
	 * @param candidate
	 * @param entry
	 */
	protected void mergeZip(GisgraphySearchEntry candidate,
			GisgraphySearchEntry entry) {
		if (candidate!=null && candidate.getZipCodes()!=null && candidate.getZipCodes().size() >0 && entry.getZipCodes()==null){
			entry.setZipCodes(candidate.getZipCodes());
			logger.error("adding zip to "+entry);
		}
		if (candidate!=null && candidate.getIsInZip()!=null && candidate.getIsInZip().size() >0 && entry.getIsInZip()==null){
			entry.setIsInZip(candidate.getIsInZip());
			logger.error("adding isinzip to "+entry);
		}
	}

	protected boolean updateHouseNumber(String number, GisgraphySearchEntry entry) {
		List<String> house_numbers = entry.getHouseNumbers();
		if (house_numbers !=null ) {
			/*String hnstring = "will check ";
			for (String hn : house_numbers){
				hnstring= hnstring+hn.split(":")[0]+",";
			}
			logger.debug(hnstring+" for "+entry.getFeatureId()+" - "+entry.getName());	*/
			for (String houseNumber: house_numbers){
				HouseNumberDto dto = houseNumberDeserializer
						.deserialize(houseNumber);
				if (dto.getNumber()!=null &&  dto.getNumber().equals(number) ){
					//logger.debug("find hn "+dto.getNumber()+ " for "+entry.getFeatureId()+"/"+entry.getName()+"/"+entry.getIsIn());
					entry.setHouseNumber(dto.getNumber());
					entry.setLat(dto.getLatitude());
					entry.setLng(dto.getLongitude());
					return true;
				}
			}
		}
		else {
			logger.debug(" no hn to check for "+entry.getFeatureId()+" - "+entry.getName());	
		}
		return false;
	}




	@SuppressWarnings("deprecation")
	protected void doExecuteAndSerialize(FulltextQuery query,
			OutputStream outputStream) {
		try {
			if (!disableLogging){
				logger.info(query.toString());
			}

			ModifiableSolrParams params = FulltextQuerySolrHelper.parameterize(query);
			CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrClient
					.getURL(), this.httpClient,
					new OutputstreamResponseWrapper(outputStream, params
							.get(Constants.OUTPUT_FORMAT_PARAMETER)));
			server.query(params);
		} catch (SolrServerException e) {
			logger.error("Can not execute query " + FulltextQuerySolrHelper.toQueryString(query)
					+ "for URL : " + solrClient.getURL() + " : "
					+ e.getCause().getMessage(),e);
			throw new FullTextSearchException(e.getCause().getMessage());
		} catch (MalformedURLException e1) {
			logger.error("The URL " + solrClient.getURL() + " is incorrect",e1);
			throw new FullTextSearchException(e1);
		} catch (RuntimeException e2) {
			String message = e2.getCause()!=null?e2.getCause().getMessage():e2.getMessage();
			logger
			.error("An error has occurred during fulltext search of query "
					+ query + " : " + message,e2);
			throw new FullTextSearchException(message,e2);
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IFullTextSearchEngine#executeQueryToString(com.gisgraphy.domain.geoloc.service.fulltextsearch.FulltextQuery)
	 */
	public String executeQueryToString(FulltextQuery query) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			executeAndSerialize(query, outputStream);
			return outputStream.toString(Constants.CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new FullTextSearchException("Encoding error during search : "
					+ e.getCause().getMessage(),e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IFullTextSearchEngine#executeQuery(com.gisgraphy.domain.geoloc.service.fulltextsearch.FulltextQuery)
	 */
	public List<? extends GisFeature> executeQueryToDatabaseObjects(
			FulltextQuery query) throws ServiceException {
		statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
		Assert.notNull(query, "Can not execute a null query");
		String queryString = ZipcodeNormalizer.normalize(query.getQuery(), query.getCountryCode());
		query.withQuery(queryString);
		ModifiableSolrParams params = FulltextQuerySolrHelper.parameterize(query);
		List<GisFeature> gisFeatureList = new ArrayList<GisFeature>();
		QueryResponse results = null;
		try {
			results = solrClient.getServer().query(params);
			if (!disableLogging){
				logger.info(query + " took " + (results.getQTime())
						+ " ms and returns " + results.getResults().getNumFound()
						+ " results");
			}

			List<Long> ids = new ArrayList<Long>();
			for (SolrDocument doc : results.getResults()) {
				ids.add((Long) doc.getFieldValue(FullTextFields.FEATUREID
						.getValue()));
			}

			gisFeatureList = gisFeatureDao.listByFeatureIds(ids);
		} catch (Exception e) {
			logger
			.error("An error has occurred during fulltext search to database object for query "
					+ query + " : " + e.getCause().getMessage(),e);
			throw new FullTextSearchException(e);
		}

		return gisFeatureList;
	}

	public FulltextResultsDto executeQuery(FulltextQuery query)
			throws ServiceException {
		statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
		Assert.notNull(query, "Can not execute a null query");
		String queryString = ZipcodeNormalizer.normalize(query.getQuery(), query.getCountryCode());
		query.withQuery(queryString);
		ModifiableSolrParams params = FulltextQuerySolrHelper.parameterize(query);
		QueryResponse response = null;
		try {
			response = solrClient.getServer().query(params);
		} catch (SolrServerException e) {
			throw new FullTextSearchException(e.getMessage(), e);
		} catch (RuntimeException e) {
			throw new FullTextSearchException(e.getMessage(), e);
		}
		if (response != null) {
			long numberOfResults = response.getResults() != null ? response
					.getResults().getNumFound() : 0;
					if (!disableLogging){
						logger.info(query + " took " + response.getQTime()
								+ " ms and returns " + numberOfResults + " results");
					}
					return builder.build(response);
		} else {
			return new FulltextResultsDto();
		}
	}

	/*public FulltextResultsDto executeAddressQuery(Address address, boolean fuzzy)
    	    throws ServiceException {
    	statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
    	Assert.notNull(address, "Can not execute a null address query");
    	if (address.getZipCode()!=null && address.getCountryCode()!=null){
    		address.setZipCode(ZipcodeNormalizer.normalize(address.getZipCode(), address.getCountryCode()));
    	}
    	ModifiableSolrParams params = FulltextQuerySolrHelper.buildAddressQuery(address, fuzzy);
    	QueryResponse response = null;
    	try {
    	    response = solrClient.getServer().query(params);
    	} catch (SolrServerException e) {
    	    throw new FullTextSearchException(e.getMessage(), e);
    	} catch (RuntimeException e) {
    	    throw new FullTextSearchException(e.getMessage(), e);
    	}
    	if (response != null) {
    	    long numberOfResults = response.getResults() != null ? response
    		    .getResults().getNumFound() : 0;
    		    if (!disableLogging){
    		    	logger.info("addressQuery" + address + " took " + response.getQTime()
    		    	+ " ms and returns " + numberOfResults + " results");
    		    }
    	    return builder.build(response);
    	} else {
    	    return new FulltextResultsDto();
    	}
        }

	 */
	public FulltextResultsDto executeRawQuery(String q)
			throws ServiceException {
		Assert.notNull(q, "Can not execute a null raw query");
		ModifiableSolrParams params = FulltextQuerySolrHelper.toRawQuery(q);
		QueryResponse response = null;
		try {
			response = solrClient.getServer().query(params);
		} catch (SolrServerException e) {
			throw new FullTextSearchException(e.getMessage(), e);
		} catch (RuntimeException e) {
			throw new FullTextSearchException(e.getMessage(), e);
		}
		if (response != null) {
			long numberOfResults = response.getResults() != null ? response
					.getResults().getNumFound() : 0;
					/*  if (!disableLogging){
    		    	logger.info("rawQuery" + q + " took " + response.getQTime()
    		    	+ " ms and returns " + numberOfResults + " results");
    		    }*/
					return builder.build(response);
		} else {
			return new FulltextResultsDto();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IFullTextSearchEngine#isAlive()
	 */
	public boolean isAlive() {
		return solrClient == null ? false : solrClient.isServerAlive();
	}

	/**
	 * @param solrClient
	 *                the solrClient to set
	 */
	@Required
	public void setSolrClient(IsolrClient solrClient) {
		this.solrClient = solrClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IFullTextSearchEngine#getURL()
	 */
	public String getURL() {
		return solrClient.getURL();
	}

}
