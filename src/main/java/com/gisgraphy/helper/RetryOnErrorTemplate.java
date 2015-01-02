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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Give the ability to retry a function multiple times if it fails.
 * <br/><br/>
 * usage : 
 * 
 * <code>
 * <pre>
 * RetryOnErrorTemplate&lt;String&gt; retryOnError = new RetryOnErrorTemplate&lt;String&gt;() {
 *	    public String tryThat() throws Exception {
 *		return THE_RETURNED_VALUE;
 *	    }
 *	};
 * retryOnError.times(3)
 *</pre>
 * </code>
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public abstract class RetryOnErrorTemplate<T> {

    private int alreadyTry = 0;

    private int numberOfTimesToRetry = 1;
    private T returnvalue = null;
    
    protected static final Logger logger = LoggerFactory
    .getLogger(RetryOnErrorTemplate.class);
    
    private String loggingSentence = "?";

    private RetryOnErrorTemplate<T> retry() throws Exception {
	try {
	    returnvalue = tryThat();
	    alreadyTry++;
	    return this;
	} catch (Exception e) {
	    logger.error("try no "+alreadyTry+" for "+loggingSentence+" fails : " + e.getMessage(),e);
	    numberOfTimesToRetry--;
	    alreadyTry++;
	    if (numberOfTimesToRetry == 0) {
		throw e;
	    } else {
		return retry();
	    }
	}
    }

   
    public abstract T tryThat() throws Exception;

    public T times(int numberOfTry) throws Exception {
	this.numberOfTimesToRetry = numberOfTry;
	retry();
	return returnvalue;

    }


    /**
     * @return The number of times the code has already been try
     */
    public int getAlreadyTry() {
        return alreadyTry;
    }


    /**
     * @return The number of times the code should be try  
     */
    public int getNumberOfTimesToRetry() {
        return numberOfTimesToRetry;
    }


    /**
     * @return the loggingSentence
     */
    public String getLoggingSentence() {
        return loggingSentence;
    }


    /**
     * @param loggingSentence the loggingSentence to set
     */
    public void setLoggingSentence(String loggingSentence) {
        this.loggingSentence = loggingSentence;
    }


  


}
