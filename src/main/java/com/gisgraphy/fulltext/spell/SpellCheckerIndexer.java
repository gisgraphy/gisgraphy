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
package com.gisgraphy.fulltext.spell;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.fulltext.IsolrClient;

/**
 * 
 * Solr implementation of {@link ISpellCheckerIndexer}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class SpellCheckerIndexer implements ISpellCheckerIndexer {

    private IsolrClient solrClient;

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(SpellCheckerIndexer.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.spell.ISpellCheckerIndexer#buildAllIndex()
     */
    public Map<String, Boolean> buildAllIndex() {
	Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
	for (SpellCheckerDictionaryNames dictionary : SpellCheckerDictionaryNames
		.values()) {
	    Boolean result = buildIndex(dictionary);
	    resultMap.put(dictionary.name(), result);
	}
	return resultMap;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.fulltextsearch.spell.ISpellCheckerIndexer#buildIndex(com.gisgraphy.domain.geoloc.service.fulltextsearch.spell.SpellCheckerDictionaryNames)
     */
    public boolean buildIndex(
	    SpellCheckerDictionaryNames spellCheckerDictionaryName) {
	if (!SpellCheckerConfig.isEnabled()) {
	    return false;
	}
	
	SolrQuery solrQuery = new SolrQuery();
	solrQuery.setQueryType(Constants.SolrQueryType.spellcheck.toString());
	solrQuery.add(Constants.SPELLCHECKER_DICTIONARY_NAME_PARAMETER,
		spellCheckerDictionaryName.toString());
	solrQuery.add(Constants.SPELLCHECKER_BUILD_PARAMETER, "true");
	solrQuery.add(Constants.SPELLCHECKER_ENABLED_PARAMETER, "true");
	solrQuery.setQuery("spell");
	try {
	    QueryResponse response = solrClient.getServer().query(solrQuery);
	    if (response.getStatus() != 0) {
		 logger.error("Indexing dictionary "
			    + spellCheckerDictionaryName.name()+" fails");
		return false;
	    }
	    logger.info("Successfully indexing dictionary "
		    + spellCheckerDictionaryName.name());
	    return true;

	} catch (Exception e) {
	    logger.error("An error has occured when indexing spellchecker "
		    + spellCheckerDictionaryName + " : " + e);
	    return false;
	}

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
