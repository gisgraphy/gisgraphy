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
package com.gisgraphy.domain.geoloc.valueobject;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.serializer.common.OutputFormat;

public class OutputTest  {

    @Test
    public void outputTestWithNullValuesSetTheDefaultFormat() {
	assertEquals(OutputFormat.getDefault(), Output.withFormat(null)
		.getFormat());
    }

    @Test
    public void withFormatShouldSetTheFormat() {
	assertEquals(OutputFormat.JSON, Output.withFormat(OutputFormat.JSON)
		.getFormat());
    }

    @Test
    public void withIndentationShouldSetTheidentation() {
	Assert.assertTrue(Output.withDefaultFormat().withIndentation().isIndented());
    }

    @Test
    public void withDefaultFormatShouldBeSetToFormat() {
	assertEquals(OutputFormat.getDefault(), Output.withDefaultFormat()
		.getFormat());
    }

    @Test
    public void withLanguageCodeWithNullOrEmptyShouldBeSetToDefault() {
	assertEquals(Output.DEFAULT_LANGUAGE_CODE, Output.withDefaultFormat()
		.withLanguageCode(null).getLanguageCode());
	assertEquals(Output.DEFAULT_LANGUAGE_CODE, Output.withDefaultFormat()
		.withLanguageCode(" ").getLanguageCode());
	assertEquals(Output.DEFAULT_LANGUAGE_CODE, Output.withDefaultFormat()
		.withLanguageCode("").getLanguageCode());
    }

    @Test
    public void withLanguageCodeShouldBeUpperCased() {
	assertEquals("FR", Output.withDefaultFormat().withLanguageCode("fr")
		.getLanguageCode());
    }

    @Test
    public void withStyleWithNullShouldBeSetToDefault() {
	assertEquals(OutputStyle.getDefault(), Output.withDefaultFormat()
		.withStyle(null).getStyle());
    }

    @Test
    public void defaultOutputShouldHaveDefaultParameters() {
	assertEquals(Output.DEFAULT_LANGUAGE_CODE, Output.DEFAULT_OUTPUT
		.getLanguageCode());
	assertEquals(OutputFormat.getDefault(), Output.DEFAULT_OUTPUT
		.getFormat());
	assertEquals(OutputStyle.getDefault(), Output.DEFAULT_OUTPUT
		.getStyle());
    }

    @Test
    public void withIndentationShouldSetTheIndentationToTrue() {
	assertEquals(true, Output.withDefaultFormat().withIndentation()
		.isIndented());
	assertEquals(false, Output.DEFAULT_OUTPUT.isIndented());
    }

   

}
