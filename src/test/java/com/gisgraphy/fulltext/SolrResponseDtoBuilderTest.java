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

import static org.junit.Assert.assertNotNull;
import net.sf.jstester.util.Assert;

import org.apache.solr.common.SolrDocument;
import org.easymock.EasyMock;
import org.junit.Test;

public class SolrResponseDtoBuilderTest  {

    @Test
    public void constructorWithNullValueShouldNotThrows() {
    	solrResponseDtoBuilder builder = new solrResponseDtoBuilder();
	assertNotNull(builder.build(null));
    }
    
    //see solrunmarshallertest
    
    @Test
    public void equalsShouldBeBasedOnFeature_id(){
	
	SolrDocument document1 = EasyMock.createNiceMock(SolrDocument.class);
	EasyMock.expect(document1.get(FullTextFields.FEATUREID)).andStubReturn(1L);
	EasyMock.replay(document1);
	
	SolrDocument document2 = EasyMock.createNiceMock(SolrDocument.class);
	EasyMock.expect(document2.get(FullTextFields.FEATUREID)).andStubReturn(2L);
	EasyMock.replay(document2);
	
	SolrResponseDto solrResponseDto = new SolrResponseDto();
	solrResponseDto.setFeature_id(1L);
	SolrResponseDto solrResponseDtoNotEquals = new SolrResponseDto();	
	solrResponseDtoNotEquals.setFeature_id(2L);
	SolrResponseDto solrResponseDtoEquals = new SolrResponseDto();
	solrResponseDtoEquals.setFeature_id(1L);
	
	Assert.assertTrue("solrReqponseDto Without the same featureId should be equals",solrResponseDto.equals(solrResponseDtoEquals));
	Assert.assertFalse("solrReqponseDto With the same featureId should be equals",solrResponseDto.equals(solrResponseDtoNotEquals));
	
    }

}
