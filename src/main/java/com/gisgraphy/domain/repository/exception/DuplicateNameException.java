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
package com.gisgraphy.domain.repository.exception;

/**
 * Exception is throws when a GisFeature of the same name already exists The
 * constraint is not in the database because, it depends of the Class of the
 * gisFeature. e.g : Country can not have duplicate name
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class DuplicateNameException extends RuntimeException {

    /**
     * Generated serial Id
     */
    private static final long serialVersionUID = 1919599807530344500L;

    /**
     * Default constructor
     */
    public DuplicateNameException() {
	super();
    }

    /**
     * Constructor
     * 
     * @param message
     *                The message
     * @param cause
     *                The cause
     */
    public DuplicateNameException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructor
     * 
     * @param message
     *                The message
     */
    public DuplicateNameException(String message) {
	super(message);
    }

    /**
     * Constructor
     * 
     * @param cause
     *                The cause
     */
    public DuplicateNameException(Throwable cause) {
	super(cause);
    }

}
