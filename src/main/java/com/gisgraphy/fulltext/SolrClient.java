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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

/**
 * Default implementation for IsolrClient.it represent a client to connect to
 * solR server
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class SolrClient implements IsolrClient {


    protected static final Logger logger = LoggerFactory
	    .getLogger(SolrClient.class);

    private SolrServer server;
    
    private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;

    private String URL;

    /**
     * Default constructor needed by spring
     */
    public SolrClient() {
	super();
    }

    /**
     * @param solrUrl
     *                The solr URL of the server to connect
     */
    @Autowired
    public SolrClient(@Qualifier("fulltextSearchUrl")
    String solrUrl, @Qualifier("multiThreadedHttpConnectionManager")
    MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager) {
	try {
	    Assert.notNull(solrUrl, "solrClient does not accept null solrUrl");
	    Assert
		    .notNull(multiThreadedHttpConnectionManager,
			    "solrClient does not accept null multiThreadedHttpConnectionManager");
	    this.multiThreadedHttpConnectionManager = multiThreadedHttpConnectionManager;
	    this.server = new CommonsHttpSolrServer(new URL(solrUrl),
		    new HttpClient(multiThreadedHttpConnectionManager));
	    this.URL = !solrUrl.endsWith("/") ? solrUrl + "/" : solrUrl ;
	    logger.info("connecting to solr on " + this.URL + "...");
	} catch (MalformedURLException e) {
	    throw new RuntimeException("Error connecting to Solr! : "
		    + e.getMessage());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IsolrClient#bindToUrl(java.lang.String)
     */
    public void bindToUrl(String solrUrl) {
	try {
	    this.server = new CommonsHttpSolrServer(new URL(solrUrl));
	    this.URL = !solrUrl.endsWith("/") ? solrUrl + "/" : solrUrl ;
	    logger
		    .info("fulltextSearchUrl for FullTextSearchEngine is changed to "
			    + solrUrl);
	} catch (Exception e) {
	    throw new RuntimeException("Error connecting to Solr to "+solrUrl);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IsolrClient#getConnection()
     */
    public SolrServer getServer() {
	return this.server;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IsolrClient#getURL()
     */
    public String getURL() {
	return URL;
    }

    public boolean isServerAlive() {
	try {
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder();
	    XPath xpath = XPathFactory.newInstance().newXPath();
	    if (xpath == null || builder == null) {
		throw new RuntimeException(
			"Can not determine if fulltext engine is alive");
	    }
	    SolrPingResponse response = getServer().ping();
	    if (response == null) {
		return false;
	    }
	    return ((String) response.getResponse().get("status")).equals("OK");
	} catch (Exception e) {
	    logger.error("can not determine if fulltext engine is alive "
		    + e.getMessage(),e);
	    return false;
	}

    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.IsolrClient#setSolRLogLevel(java.util.logging.Level)
     */
    public void setSolRLogLevel(Level level) {
	Assert.notNull(level, "you can not specify a null level");
	Assert.notNull(multiThreadedHttpConnectionManager,"httpconnectionManager should not be null, can not set log level");
	Assert.notNull(URL,"Solr URL should not be null, can not set log level");
	HttpClient client = new HttpClient(multiThreadedHttpConnectionManager);
	PostMethod method = new PostMethod(this.URL+"admin/logging");
	method.setParameter("root",level.toString().toUpperCase());
	method.setParameter("submit","set");
	 try {
	            try {
			int responseCode = client.executeMethod(method);
			logger.info("Set solr log Level to "+level);
			String responseBody = method.getResponseBodyAsString();
			if (responseCode >= 500){
			    throw new RuntimeException("Can not set solr log level to "+level+" because response code is not OK ("+responseCode+"): "+responseBody);
			}
		    } catch (Exception e) {
			throw new RuntimeException("Can not set solr log level to "+level,e);
		    }
	        } finally {
	            method.releaseConnection();
	        }
    }

}
