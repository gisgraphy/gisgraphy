/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.gisgraphy.fulltext;

import java.io.File;
import java.util.Random;
import java.util.logging.Level;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.domain.repository.ISolRSynchroniser;

/**
 * An Abstract base class that makes writing Solr JUnit tests "easier"
 * 
 * @see #setUp
 * @see #tearDown
 */
public abstract class AbstractIntegrationHttpSolrTestCase extends
	AbstractTransactionalTestCase {

    private static boolean serverStarted = false;

   

   
    protected boolean isMustStartServlet() {
	return true;
    }

    /**
     * the URL of the fulltextSearchUrl
     */
    public static String fulltextSearchUrlBinded;

    protected ISolRSynchroniser solRSynchroniser;

    protected IsolrClient solrClient;

    protected IFullTextSearchEngine fullTextSearchEngine;

    protected String FULLTEXT_SEARCH_ENGINE_CONTEXT = "/solr";

    /**
     * The directory used to story the index managed by the TestHarness h
     */
    protected File dataDir;
    
    /**
     * @return a port beetween 49152 and 65535
     */
    private int generateRandomPort(){
    	return 49152 + new Random().nextInt(65535-49152);
    }

    /**
     * Initializes things your test might need
     * <ul>
     * <li>Creates a dataDir in the "java.io.tmpdir"</li>
     * <li>initializes the TestHarness h using this data directory, and
     * getSchemaPath()</li>
     * <li>initializes the LocalRequestFactory lrf using sensible defaults.</li>
     * </ul>
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onSetUp() throws Exception {
	super.onSetUp();


	if (!serverStarted && isMustStartServlet()) {
	    String separator = System.getProperty("file.separator");
	    String solrDataDirPropertyName = "solr.data.dir";
	    String solrDataDirValue = "./target" + separator + "classes" + separator
		    + "data";
	    if (System.getProperty(solrDataDirPropertyName) == null
		    || !System.getProperty(solrDataDirPropertyName).equals(solrDataDirValue)) {
		logger.info("change system property from "
			+ System.getProperty(solrDataDirPropertyName) + " to " + solrDataDirValue);
		System.setProperty(solrDataDirPropertyName, solrDataDirValue);

		logger.info("System property " + solrDataDirPropertyName + " is now : "
			+ System.getProperty(solrDataDirPropertyName));
	    } else {
		logger.info(solrDataDirPropertyName + "=" + System.getProperty("file.encoding"));
	    }
	    
	    String jetty_default=new java.io.File("./start.jar").exists()?".":"./src/dist/";;
	    String jetty_home = System.getProperty("jetty.home",jetty_default);

	    Server server = new Server();
	    int port = generateRandomPort();
	    Connector connector=new SelectChannelConnector();
	    connector.setPort(Integer.getInteger("jetty.port",port).intValue());
	    server.setConnectors(new Connector[]{connector});
	    
	    WebAppContext webapp = new WebAppContext();
	    webapp.setContextPath(FULLTEXT_SEARCH_ENGINE_CONTEXT);
	    webapp.setWar(jetty_home+"webapps/solr.war");
	    webapp.setDefaultsDescriptor(jetty_home+"etc/webdefault.xml");
	    
	    server.setHandler(webapp);
	    server.setStopAtShutdown(true);
	    logger.info("will start jetty on "+port);
	    serverStarted = true;
	    server.start();
	    fulltextSearchUrlBinded=("http://localhost:"+port
	    + FULLTEXT_SEARCH_ENGINE_CONTEXT);

	    this.solrClient.bindToUrl(fulltextSearchUrlBinded);
	    // set log to off
	    // comment this line to see solr logs
	    this.solrClient.setSolRLogLevel(Level.OFF);
	}
	if (isMustStartServlet()) {
	    this.solRSynchroniser.deleteAll();
	}

    }

   

    /**
     * Shuts down the test harness, and makes the best attempt possible to
     * delete dataDir, unless the system property "solr.test.leavedatadir" is
     * set.
     */
    @Override
    public void onTearDown() throws Exception {
	super.onTearDown();
	// server.stop();
	solRSynchroniser.deleteAll();
	// tester.stop();
	// TODO v2 remove solrdir after all test
    }

    @Required
    public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
	this.solRSynchroniser = solRSynchroniser;
    }

    @Required
    public void setFullTextSearchEngine(
	    IFullTextSearchEngine fullTextSearchEngine) {
	this.fullTextSearchEngine = fullTextSearchEngine;
    }

    /**
     * @param solrClient
     *                the solrClient to set
     */
    @Required
    public void setSolrClient(IsolrClient solrClient) {
	this.solrClient = solrClient;
    }


	

}
