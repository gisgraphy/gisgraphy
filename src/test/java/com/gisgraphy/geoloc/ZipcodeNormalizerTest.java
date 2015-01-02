/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.geoloc;

import org.junit.Assert;
import org.junit.Test;

public class ZipcodeNormalizerTest {

    @Test
    public void normalize_ca_with_null() {
	Assert.assertNull(ZipcodeNormalizer.normalize_ca(null));

    }

    @Test
    public void normalize_GB_with_null() {
	Assert.assertNull(ZipcodeNormalizer.normalize_gb(null));

    }

    @Test
    public void normalizeWithNullString() {
	Assert.assertNull(ZipcodeNormalizer.normalize(null, "GB"));

    }

    @Test
    public void normalize_ca() {

	// only text
	Assert.assertEquals("foo", ZipcodeNormalizer.normalize_ca("foo"));

	// only zip
	Assert.assertEquals("H3Z", ZipcodeNormalizer.normalize_ca("H3Z 2Y7"));
	Assert.assertEquals("H3Z", ZipcodeNormalizer.normalize_ca("H3Z2Y7"));
	Assert.assertEquals("H3Z", ZipcodeNormalizer.normalize_ca("H3Z–2Y7"));

	// zip +text
	Assert.assertEquals("foo H3Z bar", ZipcodeNormalizer.normalize_ca("foo H3Z 2Y7 bar "));
	Assert.assertEquals("foo H3Z bar", ZipcodeNormalizer.normalize_ca("foo H3Z 2Y7 bar "));
	Assert.assertEquals("foo H3Z bar", ZipcodeNormalizer.normalize_ca("foo H3Z2Y7 bar"));
	Assert.assertEquals("foo H3Z bar", ZipcodeNormalizer.normalize_ca("foo H3Z–2Y7 bar"));

	// double
	Assert.assertEquals("foo H3Z bar H3Z", ZipcodeNormalizer.normalize_ca("foo H3Z 2Y7 bar H3Z 2Y7"));
	Assert.assertEquals("foo H3Z bar H3Z", ZipcodeNormalizer.normalize_ca("foo H3Z 2Y7 bar H3Z 2Y7"));
	Assert.assertEquals("foo H3Z bar H3Z", ZipcodeNormalizer.normalize_ca("foo H3Z2Y7 bar H3Z 2Y7"));
	Assert.assertEquals("foo H3Z bar", ZipcodeNormalizer.normalize_ca("foo H3Z–2Y7 bar"));
    }

    @Test
    public void normalize_gb() {


	// only text
	Assert.assertEquals("foo", ZipcodeNormalizer.normalize_gb("foo"));

	// only zip
	Assert.assertEquals("EC1A", ZipcodeNormalizer.normalize_gb("EC1A 1HQ"));
	Assert.assertEquals("EC1A", ZipcodeNormalizer.normalize_gb("EC1A1HQ"));
	Assert.assertEquals("DN16", ZipcodeNormalizer.normalize_gb("DN16 9AA"));
	Assert.assertEquals("DN16", ZipcodeNormalizer.normalize_gb("DN169AA"));
	Assert.assertEquals("M2", ZipcodeNormalizer.normalize_gb("M2 5BQ"));
	Assert.assertEquals("M2", ZipcodeNormalizer.normalize_gb("M25BQ"));
	Assert.assertEquals("CR0", ZipcodeNormalizer.normalize_gb("CR0 2YR"));
	Assert.assertEquals("CR0", ZipcodeNormalizer.normalize_gb("CR02YR"));
	Assert.assertEquals("W1A", ZipcodeNormalizer.normalize_gb("W1A 4ZZ"));
	Assert.assertEquals("W1A", ZipcodeNormalizer.normalize_gb("W1A4ZZ"));
	Assert.assertEquals("M34", ZipcodeNormalizer.normalize_gb("M34 4AB"));
	Assert.assertEquals("M34", ZipcodeNormalizer.normalize_gb("M344AB"));
	Assert.assertEquals("GIR", ZipcodeNormalizer.normalize_gb("GIR 0AA"));
	Assert.assertEquals("GIR", ZipcodeNormalizer.normalize_gb("GIR0AA"));
	Assert.assertEquals("ASCN", ZipcodeNormalizer.normalize_gb("ASCN 1ZZ"));
	Assert.assertEquals("ASCN", ZipcodeNormalizer.normalize_gb("ASCN1ZZ"));
	Assert.assertEquals("STHL", ZipcodeNormalizer.normalize_gb("STHL 1ZZ"));
	Assert.assertEquals("STHL", ZipcodeNormalizer.normalize_gb("STHL1ZZ"));
	Assert.assertEquals("SIQQ", ZipcodeNormalizer.normalize_gb("SIQQ 1ZZ"));
	Assert.assertEquals("SIQQ", ZipcodeNormalizer.normalize_gb("SIQQ1ZZ"));

	// zip +text
	Assert.assertEquals("foo DN16 bar", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar "));
	Assert.assertEquals("foo DN16 bar", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar "));
	Assert.assertEquals("foo DN16 bar", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar"));
	Assert.assertEquals("foo DN16 bar", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar"));

	// double
	Assert.assertEquals("foo DN16 bar DN16", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar DN16 9AA"));
	Assert.assertEquals("foo DN16 bar DN16", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar DN16 9AA"));
	Assert.assertEquals("foo DN16 bar DN16", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar DN16 9AA"));
	Assert.assertEquals("foo DN16 bar", ZipcodeNormalizer.normalize_gb("foo DN16 9AA bar"));

    }

    @Test
    public void noramlizeShouldHandleCountryCorrectly() {
	Assert.assertNull(ZipcodeNormalizer.normalize(null, null));
	//with code
	Assert.assertEquals("H3Z", ZipcodeNormalizer.normalize("H3Z 2Y7", "CA"));
	Assert.assertEquals("DN16", ZipcodeNormalizer.normalize("DN16 9AA", "GB"));
	//with empy code
	Assert.assertEquals("H3Z", ZipcodeNormalizer.normalize("H3Z 2Y7", ""));
	Assert.assertEquals("DN16", ZipcodeNormalizer.normalize("DN16 9AA", ""));
	Assert.assertEquals("DN16 H3Z", ZipcodeNormalizer.normalize("DN16 9AA H3Z 2Y7", ""));
	
	//with null code
	Assert.assertEquals("H3Z", ZipcodeNormalizer.normalize("H3Z 2Y7", null));
	Assert.assertEquals("DN16", ZipcodeNormalizer.normalize("DN16 9AA", null));
	Assert.assertEquals("DN16 H3Z", ZipcodeNormalizer.normalize("DN16 9AA H3Z 2Y7", null));
	//with unknow code
	Assert.assertEquals("foo", ZipcodeNormalizer.normalize("foo", "XX"));
    }

}
