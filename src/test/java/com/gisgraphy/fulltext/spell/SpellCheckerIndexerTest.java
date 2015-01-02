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

import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;

public class SpellCheckerIndexerTest extends
	AbstractIntegrationHttpSolrTestCase {

    private ISpellCheckerIndexer spellCheckerIndexer;

    @Test
    public void testBuildAllIndex() {
	Map<String,Boolean> results = spellCheckerIndexer.buildAllIndex();
	assertEquals(SpellCheckerDictionaryNames.values().length, results.size());
	for (String key : results.keySet()){
	    assertTrue(results.get(key).booleanValue());
	}
    }

    @Test
    public void testBuildIndexShouldReturnFalseIfSpellCheckIsNotEnabled() {
	boolean savedSpellCheckingValue = SpellCheckerConfig.enabled;
	try {
	    SpellCheckerConfig.enabled = false;
	    assertFalse(spellCheckerIndexer
		    .buildIndex(SpellCheckerDictionaryNames.getDefault()));

	} finally {
	    SpellCheckerConfig.enabled = savedSpellCheckingValue;
	}
    }

    @Test
    public void testBuildIndexShouldReturnFalseIfAnErrorOccured() {
	SpellCheckerIndexer wrongSpellCheckerIndexer = new SpellCheckerIndexer();
	wrongSpellCheckerIndexer.setSolrClient(null);
	boolean savedSpellCheckingValue = SpellCheckerConfig.enabled;
	try {
	    assertFalse(wrongSpellCheckerIndexer
		    .buildIndex(SpellCheckerDictionaryNames.getDefault()));
	} finally {
	    SpellCheckerConfig.enabled = savedSpellCheckingValue;
	}
    }

    @Test
    public void testBuildIndexShouldReturnTrueIfOK() {
	boolean savedSpellCheckingValue = SpellCheckerConfig.enabled;
	try {
	    SpellCheckerConfig.enabled = true;
	    assertTrue(spellCheckerIndexer
		    .buildIndex(SpellCheckerDictionaryNames.getDefault()));

	} finally {
	    SpellCheckerConfig.enabled = savedSpellCheckingValue;
	}
    }

    @Test
    public void testThatSpellCheckerShouldNotAcceptAnInexistingSpellCheckerDictionaryName() {
	SolrQuery solrQuery = new SolrQuery();
	solrQuery.setQueryType(Constants.SolrQueryType.spellcheck.toString());
	solrQuery.add(Constants.SPELLCHECKER_DICTIONARY_NAME_PARAMETER,
		"notExistingInSolrConfig.xml");
	solrQuery.add(Constants.SPELLCHECKER_BUILD_PARAMETER, "true");
	solrQuery.add(Constants.SPELLCHECKER_ENABLED_PARAMETER, "true");
	solrQuery.setQuery("spell");
	try {
	    QueryResponse response = solrClient.getServer().query(solrQuery);
	    if (response.getStatus() != 0) {
		fail("Status should not be 0 when the name of the dictionnary name is not defined in solrConfig.xml");
	    }
	    fail("dictionnary name that are not defined in solrConfig.xml should not be accepted");
	} catch (Exception e) {
	  logger.error(e);
	}

    }

    @Required
    public void setSpellCheckerIndexer(ISpellCheckerIndexer spellCheckerIndexer) {
	this.spellCheckerIndexer = spellCheckerIndexer;
    }

}
