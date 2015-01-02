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
package com.gisgraphy.street;

import com.gisgraphy.serializer.common.IoutputFormatVisitor;
import com.gisgraphy.serializer.common.OutputFormat;

/**
 * Visitor (visitor pattern) to return error message according to the format for
 * the geoloc service
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class StreetSearchErrorVisitor implements IoutputFormatVisitor {

   
    
    private String errorMessage = IoutputFormatVisitor.DEFAULT_ERROR_MESSAGE;

    public StreetSearchErrorVisitor() {
	super();
    }

    /**
     * @param errorMessage
     *                The error Message
     */
    public StreetSearchErrorVisitor(String errorMessage) {
	super();
	this.errorMessage = errorMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitXML(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitXML(OutputFormat format) {
	return String
		.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><results xmlns=\"http://gisgraphy.com\"><error>%s</error><numFound>0</numFound></results>",
			errorMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitJSON(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitJSON(OutputFormat format) {
	return String.format("{\"numFound\":0,\"error\":\"%s\",\"result\":[]}",
		errorMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitPYTHON(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitPYTHON(OutputFormat format) {
	return visitXML(format);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitRUBY(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitRUBY(OutputFormat format) {
	return visitXML(format);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitPHP(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitPHP(OutputFormat format) {
	return visitXML(format);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitATOM(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitATOM(OutputFormat outputFormat) {
	return visitXML(outputFormat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitGEORSS(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitGEORSS(OutputFormat outputFormat) {
	return visitXML(outputFormat);
    }
    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#visitUNSUPPORTED(com.gisgraphy.domain.valueobject.Output.OutputFormat)
     */
    public String visitUNSUPPORTED(OutputFormat format) {
		return "";
	}
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.service.errors.IoutputFormatVisitor#getErrorMessage()
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public String visitYAML(OutputFormat format) {
	return "error";
    }

}
