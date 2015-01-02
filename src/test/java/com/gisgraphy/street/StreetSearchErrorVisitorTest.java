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
package com.gisgraphy.street;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sf.jstester.JsTester;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.serializer.common.IoutputFormatVisitor;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.test.FeedChecker;


public class StreetSearchErrorVisitorTest {
    
    private String errorMessage = "My Message";

    @Test
    public void streetSearchErrorVisitorString() {
	IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	Assert.assertEquals("The error message is not well set ",errorMessage, StreetSearchErrorVisitor.getErrorMessage());
    }
    
    @Test
    public void streetSearchErrorVisitor() {
	IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor();
	Assert.assertEquals("An error message should be provided when no message is specified ",IoutputFormatVisitor.DEFAULT_ERROR_MESSAGE, StreetSearchErrorVisitor.getErrorMessage());
    }

    @Test
    public void visitXML() {

	    IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	    String result = StreetSearchErrorVisitor.visitXML(OutputFormat.XML);
	    
	    FeedChecker.assertQ("The XML error is not correct", result, "//error[.='" + errorMessage + "']");
    }


    @Test
    public void visitJSON() {
	JsTester jsTester = null;
	try {
	    jsTester = new JsTester();
	    jsTester.onSetUp();
	    IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	    String result = StreetSearchErrorVisitor.visitJSON(OutputFormat.JSON);

	    // JsTester
	    jsTester.eval("evalresult= eval(" + result + ");");
	    jsTester.assertNotNull("evalresult");
	    String error = jsTester.eval("evalresult.error").toString();

	    assertEquals(errorMessage, error);

	} catch (Exception e) {
	    fail("An exception has occured " + e.getMessage());
	} finally {
	    if (jsTester != null) {
		jsTester.onTearDown();
	    }

	}

    }

    @Test
    public void visitPYTHON() {
	IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	    String result = StreetSearchErrorVisitor.visitPYTHON(OutputFormat.PYTHON);
	    
	    FeedChecker.assertQ("The XML error is not correct", result, "//error[.='" + errorMessage + "']");
    }

    @Test
    public void visitRUBY() {
	IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	    String result = StreetSearchErrorVisitor.visitRUBY(OutputFormat.RUBY);
	    
	    FeedChecker.assertQ("The XML error is not correct", result, "//error[.='" + errorMessage + "']");
    }

    @Test
    public void visitPHP() {
	IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	    String result = StreetSearchErrorVisitor.visitPHP(OutputFormat.PHP);
	    
	    FeedChecker.assertQ("The XML error is not correct", result, "//error[.='" + errorMessage + "']");
    }

    @Test
    public void visitATOM() {
	IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	    String result = StreetSearchErrorVisitor.visitATOM(OutputFormat.ATOM);
	    
	    FeedChecker.assertQ("The XML error is not correct", result, "//error[.='" + errorMessage + "']");
    }

    @Test
    public void visitGEORSS() {
	IoutputFormatVisitor StreetSearchErrorVisitor = new StreetSearchErrorVisitor(errorMessage);
	    String result = StreetSearchErrorVisitor.visitGEORSS(OutputFormat.GEORSS);
	    
	    FeedChecker.assertQ("The XML error is not correct", result, "//error[.='" + errorMessage + "']");
    }


}
