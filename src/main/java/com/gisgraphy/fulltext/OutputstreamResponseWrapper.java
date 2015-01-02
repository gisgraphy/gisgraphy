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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.ResponseParser;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.gisgraphy.domain.valueobject.Constants;

/**
 * Wrapper that must be used when we want the fulltext query to be serialize
 * into an output stream
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * @since solr 1.3
 */
public class OutputstreamResponseWrapper extends ResponseParser {

    protected static final Logger logger = LoggerFactory
    .getLogger(OutputstreamResponseWrapper.class);
    
    private final OutputStream outputStream;
    private final String writerType;

    /**
     * The encoding of the response Default to {@link Constants#CHARSET}
     */
    private String encoding = Constants.CHARSET;

    /**
     * @param outputStream
     *                The OutpuStream to serialize the fulltext response in
     * @param writerType
     *                The writerType (aka : the wt parameter)
     */
    public OutputstreamResponseWrapper(OutputStream outputStream,
	    String writerType) {
	super();
	Assert.notNull(outputStream, "outputstream can not be null");
	this.outputStream = outputStream;
	this.writerType = writerType == null ? "XML" : writerType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.solr.client.solrj.ResponseParser#getWriterType()
     */
    @Override
    public String getWriterType() {
	return this.writerType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.solr.client.solrj.ResponseParser#processResponse(java.io.Reader)
     */
    @Override
    public NamedList<Object> processResponse(Reader reader) {

	try {
	    IOUtils.copy(reader, outputStream, Constants.CHARSET);
	} catch (IOException e) {
	   logger.error("error when writing fulltext respone : "+e,e);
	} finally {

	    try {
		outputStream.flush();
		outputStream.close();
	    } catch (IOException e) {

	    }
	}
	return new NamedList<Object>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.solr.client.solrj.ResponseParser#processResponse(java.io.InputStream,
     *      java.lang.String)
     */
    @Override
    public NamedList<Object> processResponse(InputStream inputStream,
	    String encoding) {
	try {
	    this.encoding = encoding;
	    IOUtils.copy(inputStream, outputStream);
	} catch (IOException e1) {
	} finally {

	    try {
		outputStream.flush();
		outputStream.close();
	    } catch (IOException e) {

	    }
	}
	return new NamedList<Object>();
    }

    /**
     * @return The encoding of the response
     */
    public String getEncoding() {
	return encoding;
    }

}
