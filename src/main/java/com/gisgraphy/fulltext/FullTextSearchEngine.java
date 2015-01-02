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

import java.io.ByteArrayOutputStream;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.GisFeatureDao;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.geoloc.ZipcodeNormalizer;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.service.ServiceException;
import com.gisgraphy.stats.StatsUsageType;

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
