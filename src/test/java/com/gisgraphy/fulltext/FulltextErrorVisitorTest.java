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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sf.jstester.JsTester;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.serializer.common.IoutputFormatVisitor;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.test.FeedChecker;


public class FulltextErrorVisitorTest  {
    
    private String errorMessage = "My Message";
    IoutputFormatVisitor FulltextErrorVisitor;
    

    @Before
    public void onSetUp() throws Exception {
	FulltextErrorVisitor = new FulltextErrorVisitor(errorMessage);
    }
    
    
    @Test
    public void fulltextErrorVisitorString() {
	IoutputFormatVisitor FulltextErrorVisitor = new FulltextErrorVisitor(errorMessage);
	Assert.assertEquals("The error message is not well set ",errorMessage, FulltextErrorVisitor.getErrorMessage());
    }
    
    @Test
    public void fulltextErrorVisitor() {
	IoutputFormatVisitor FulltextErrorVisitor = new FulltextErrorVisitor();
	Assert.assertEquals("An error message should be provided when no message is specified ",IoutputFormatVisitor.DEFAULT_ERROR_MESSAGE, FulltextErrorVisitor.getErrorMessage());
    }

    @Test
    public void visitXML() {
	    String result = FulltextErrorVisitor.visitXML(OutputFormat.XML);
	    FeedChecker.checkFulltextErrorXML(result,errorMessage);
    }


    @Test
    public void visitJSON() {
	JsTester jsTester = null;
	IoutputFormatVisitor FulltextErrorVisitor = new FulltextErrorVisitor(errorMessage);
	String result = FulltextErrorVisitor.visitJSON(OutputFormat.JSON);
	try {
	    jsTester = new JsTester();
	    jsTester.onSetUp();

	    // JsTester
	    jsTester.eval("evalresult= eval(" + result + ");");
	    String error = jsTester.eval("evalresult.responseHeader.error")
	    .toString();
   
	    assertEquals(errorMessage, error);

	    error = jsTester.eval("evalresult.responseHeader.status")
	    .toString();
	    Assert.assertEquals("-1.0", error);// -1.0 because it is considered as a
	    // float

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
	    String result = FulltextErrorVisitor.visitPYTHON(OutputFormat.PYTHON);
	    checkErrorMessageIsPresentInOutputStream(result);
    }




    @Test
    public void visitRUBY() {
	    String result = FulltextErrorVisitor.visitRUBY(OutputFormat.RUBY);
	    checkErrorMessageIsPresentInOutputStream(result);
    }

    @Test
    public void visitPHP() {
	    String result = FulltextErrorVisitor.visitPHP(OutputFormat.PHP);
	    checkErrorMessageIsPresentInOutputStream(result);
	    
    }

    @Test
    public void visitATOM() {
	    String result = FulltextErrorVisitor.visitATOM(OutputFormat.ATOM);
	    
	    FeedChecker.checkFulltextErrorXML(result,errorMessage);
    }

    @Test
    public void visitGEORSS() {
	    String result = FulltextErrorVisitor.visitGEORSS(OutputFormat.GEORSS);
	    
	    FeedChecker.checkFulltextErrorXML(result,errorMessage);
    }

    private void checkErrorMessageIsPresentInOutputStream(String result) {
	Assert.assertTrue("the error Message should contains the error Message", result.contains(errorMessage));
    }


   


}
