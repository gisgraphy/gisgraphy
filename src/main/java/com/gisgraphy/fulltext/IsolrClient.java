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

import org.apache.solr.client.solrj.SolrServer;

/**
 * Wrap a SolrServer
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IsolrClient {

    /**
     * @param solrUrl
     *                the solr URL
     */
    public void bindToUrl(String solrUrl);

    /**
     * return a handler to the server
     * 
     * @return The server
     */
    public SolrServer getServer();

    /**
     * @return The current url of the server
     */
    public String getURL();

    /**
     * @return true if the fulltextsearchengine is alive, otherwise return
     *         false;
     */
    public boolean isServerAlive();
    
    /**
     * set the log level of Solr
     * @param level the level to set
     */
    public void setSolRLogLevel(Level level);
    

}