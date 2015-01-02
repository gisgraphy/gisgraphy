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
package com.gisgraphy.service;

import java.util.Locale;

/**
 * allow L10N and I18N
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IInternationalisationService {

        /**
         * set the locale, if not return the default JVM one.
         * @param locale the locale to set
         */
    	public void setLocale(Locale locale) ;
    	
    	
    	/**
    	 * @return the current locale
    	 */
    	public Locale getLocale() ;
    	
    	/**
        * @param key the key in the bundle 
        * @return the localized string or the key if not found
        */
        public String getString(String key);
        
        /**
         * @param key the key in the bundle 
         * @param params the param to inject in localised message
         * @return the localized string or the key if not found
         */
        public String getString(String key, Object[] params);
    
}
