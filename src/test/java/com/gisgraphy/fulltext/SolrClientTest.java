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

import java.util.logging.Level;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.junit.Assert;
import org.junit.Test;

public class SolrClientTest extends AbstractIntegrationHttpSolrTestCase {
	
	

    @Test
    public void testConstructorShouldNotAcceptNullParameters() {
	try {
	    new SolrClient(null, new MultiThreadedHttpConnectionManager());
	    fail("solrClient constructor does not accept null URL");
	} catch (IllegalArgumentException e) {
	}
	try {
	    new SolrClient("", null);
	    fail("solrClient constructor does not accept null multiThreadedHttpConnectionManager");
	} catch (IllegalArgumentException e) {
	}
    }
    
    @Test
    public void testConstructorShouldAddEndingSlashIfNotPresent() {
	    IsolrClient client = new SolrClient("http://127.0.0.1/solr", new MultiThreadedHttpConnectionManager());
	    Assert.assertTrue("SolrClient should add a '/' if not present ", client.getURL().equals("http://127.0.0.1/solr/"));
    }
    
    @Test
    public void testBindToUrlShouldAddEndingSlashIfNotPresent() {
	    IsolrClient client = new SolrClient("http://127.0.0.2/solr", new MultiThreadedHttpConnectionManager());
	    client.bindToUrl("http://127.0.0.2/solr");
	    Assert.assertTrue("SolrClient should add a '/' if not present ", client.getURL().equals("http://127.0.0.2/solr/"));
    }

    @Test
    public void testIsALive() throws Exception {
		    IsolrClient clientAlive = new SolrClient("http://nowhere.tld/solr",
		    new MultiThreadedHttpConnectionManager());
	    assertFalse(clientAlive.isServerAlive());
	    clientAlive.bindToUrl(AbstractIntegrationHttpSolrTestCase.fulltextSearchUrlBinded);
	    assertTrue(clientAlive.isServerAlive());
    }
    
    @Test
    public void testSetSolRLogLevel() throws Exception {
	    IsolrClient client = new SolrClient(AbstractIntegrationHttpSolrTestCase.fulltextSearchUrlBinded,
		    new MultiThreadedHttpConnectionManager());
	    assertTrue(client.isServerAlive());
	    client.setSolRLogLevel(Level.CONFIG);
	    client.setSolRLogLevel(Level.FINE);
	    client.setSolRLogLevel(Level.FINER);
	    client.setSolRLogLevel(Level.FINEST);
	    client.setSolRLogLevel(Level.INFO);
	    client.setSolRLogLevel(Level.SEVERE);
	    client.setSolRLogLevel(Level.WARNING);
	    client.setSolRLogLevel(Level.ALL);
	    client.setSolRLogLevel(Level.OFF);
    }
    

    @Test
    public void testBindToURL() {
		    IsolrClient clientAlive = new SolrClient("http://nowhere.tld/solr",
		    new MultiThreadedHttpConnectionManager());
	    clientAlive.bindToUrl(AbstractIntegrationHttpSolrTestCase.fulltextSearchUrlBinded);
	    try {
		clientAlive.bindToUrl("http://nowhere.tld:4747solr");
		fail("BindToUrl should throw for malformedUrl");
	    } catch (Exception e) {
	    }


    }

}
