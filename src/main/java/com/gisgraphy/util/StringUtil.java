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
package com.gisgraphy.util;

import java.security.MessageDigest;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * String Utility Class This is used to encode passwords programmatically
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public final class StringUtil {
	
	private static Pattern  DIGIT_PATTERN = Pattern.compile("[0-9]+");
	
    private static final Log log = LogFactory.getLog(StringUtil.class);

    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    private StringUtil() {
    }

    // ~ Methods
    // ================================================================

    /**
     * Encode a string using algorithm specified in web.xml and return the
     * resulting encrypted password. If exception, the plain credentials string
     * is returned
     * 
     * @param password
     *                Password or other credentials to use in authenticating
     *                this username
     * @param algorithm
     *                Algorithm used to do the digest
     * @return encypted password based on the algorithm.
     */
    public static String encodePassword(String password, String algorithm) {
	byte[] unencodedPassword = password.getBytes();

	MessageDigest md = null;

	try {
	    // first create an instance, given the provider
	    md = MessageDigest.getInstance(algorithm);
	} catch (Exception e) {
	    log.error("Exception: " + e);

	    return password;
	}

	md.reset();

	// call the update method one or more times
	// (useful when you don't know the size of your data, eg. stream)
	md.update(unencodedPassword);

	// now calculate the hash
	byte[] encodedPassword = md.digest();

	StringBuffer buf = new StringBuffer();

	for (byte anEncodedPassword : encodedPassword) {
	    if ((anEncodedPassword & 0xff) < 0x10) {
		buf.append("0");
	    }

	    buf.append(Long.toString(anEncodedPassword & 0xff, 16));
	}

	return buf.toString();
    }

    /**
     * Encode a string using Base64 encoding. Used when storing passwords as
     * cookies. This is weak encoding in that anyone can use the decodeString
     * routine to reverse the encoding.
     * 
     * @param str
     *                the string to encode
     * @return the encoded string
     */
    public static String encodeString(String str) {
    	Base64 encoder = new Base64();
    	return String.valueOf(encoder.encode(str.getBytes())).trim();
    }

    
    public static boolean containsDigit(String text){
    	if (text!=null && DIGIT_PATTERN.matcher(text).find()){
    		return true;
    	} 
    	return false;
    	
    }
}
