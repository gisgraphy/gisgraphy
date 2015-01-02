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
package com.gisgraphy;


/**
 * Mother class of all gisgraphy Exception
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
//FIXME all gisgraphy exceptions should extends this
public class GisgraphyException extends RuntimeException {

    /**
     * Default generated serial ID
     */
    private static final long serialVersionUID = 5086776885839921918L;

    /**
     * Default constructor
     */
    public GisgraphyException() {
	super();
    }

    /**
     * @param message The message
     * @param cause The cause
     */
    public GisgraphyException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * @param message The message
     */
    public GisgraphyException(String message) {
	super(message);
    }

    /**
     * @param cause The cause
     */
    public GisgraphyException(Throwable cause) {
	super(cause);
    }

}
