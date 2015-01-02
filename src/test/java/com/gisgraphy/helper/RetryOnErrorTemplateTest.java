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
package com.gisgraphy.helper;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.geoloc.GisgraphyCommunicationException;


public class RetryOnErrorTemplateTest {

    
    private static final String THE_SENTENCE_TO_IDENTIFY_RETRY_IN_LOGS = "the sentence to identify retry in logs";
    private static final String THE_RETURNED_VALUE = "The returned value";

    @Test
    public void testRetryWhenCodeThrows() throws Exception {
	RetryOnErrorTemplate<String> retryOnError = new RetryOnErrorTemplate<String>() {
	    @Override
	    public String tryThat() throws Exception {
		    throw new GisgraphyCommunicationException();
	    }
	};
	try {
	    retryOnError.times(3);
	    Assert.fail("the code should have throws");
	} catch (GisgraphyCommunicationException ignore) {
	}
	Assert.assertEquals("The number of retry is not the expected one",3, retryOnError.getAlreadyTry());
    }

    @Test
    public void testRetryWhenCodeDoesnTThrows() throws Exception {
	RetryOnErrorTemplate<String> retryOnError = new RetryOnErrorTemplate<String>() {
	    @Override
	    public String tryThat() throws Exception {
		return THE_RETURNED_VALUE;
	    }
	};
	Assert.assertEquals("The returned value is not the expected one",THE_RETURNED_VALUE, retryOnError.times(3));
	Assert.assertEquals("Only one try should have been done",1, retryOnError.getAlreadyTry());
    }
    
    @Test
    public void testLoggingSentence() throws Exception {
	RetryOnErrorTemplate<String> retryOnError = new RetryOnErrorTemplate<String>() {
	    @Override
	    public String tryThat() throws Exception {
		return THE_RETURNED_VALUE;
	    }
	};
	retryOnError.setLoggingSentence(THE_SENTENCE_TO_IDENTIFY_RETRY_IN_LOGS);
	Assert.assertEquals("The logging sentence value is not the expected one",THE_SENTENCE_TO_IDENTIFY_RETRY_IN_LOGS, retryOnError.getLoggingSentence());
    }

}
