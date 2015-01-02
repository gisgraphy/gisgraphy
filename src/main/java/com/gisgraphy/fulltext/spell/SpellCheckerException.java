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
package com.gisgraphy.fulltext.spell;

/**
 * 
 * Generic SpellCheckerException
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class SpellCheckerException extends RuntimeException {

     /**
     * Generated serial ID 
     */
    private static final long serialVersionUID = -8534626865010822443L;

	/**
	 * Default Constructor
	 */
	public SpellCheckerException() {
		super();
	}

	/**
	 * @param message The message 
	 * @param cause Thecause
	 */
	public SpellCheckerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message The message
	 */
	public SpellCheckerException(String message) {
		super(message);
	}

	/**
	 * @param cause The cause
	 */
	public SpellCheckerException(Throwable cause) {
		super(cause);
	}

}
