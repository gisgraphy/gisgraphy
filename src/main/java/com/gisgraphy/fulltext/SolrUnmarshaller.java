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
package com.gisgraphy.fulltext;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * SolrUnmarshaller for solr Query response to a DTO
 * 
 * @see SolrResponseDto
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class SolrUnmarshaller {

    /**
     * The Logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(FullTextSearchEngine.class);
    
    private static solrResponseDtoBuilder builder = new solrResponseDtoBuilder();

    /**
     * @param solrDocument
     *                the solrDocument To Marshall into a SolrResponseDto
     * @return a SolrResponseDto
     */
    private static SolrResponseDto unmarshall(SolrDocument solrDocument) {
	SolrResponseDto dto =builder.build(solrDocument);
	return dto;

    }

    /**
     * @param queryResponse
     *                the Solr QueryResponse we want to transform
     * @return a list of SolrResponseDto or an empty list. never return null.
     */
    public static List<SolrResponseDto> unmarshall(QueryResponse queryResponse) {
	List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
	try {
	    for (SolrDocument doc : queryResponse.getResults()) {
		results.add(unmarshall(doc));
	    }
	} catch (RuntimeException e) {
	    logger
		    .warn("an error has occured during unmarshaling of SolrResponseDto : "
			    + e.getMessage());
	    throw e;
	}
	return results;

    }

}
