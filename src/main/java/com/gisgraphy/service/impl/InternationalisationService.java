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
package com.gisgraphy.service.impl;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.gisgraphy.service.IInternationalisationService;

/**
 * Allow i18n and L10n When we are not in a web context
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class InternationalisationService implements IInternationalisationService {
    
    private Locale locale = LocaleContextHolder.getLocale();
    
    private ReloadableResourceBundleMessageSource resourceBundle;

    public static final Logger logger = LoggerFactory
    .getLogger(InternationalisationService.class);
   
    
    /* (non-Javadoc)
     * @see com.gisgraphy.service.IInternationalisationService#getString(java.lang.String)
     */
    public String getString(String key) {
       try {
     return this.resourceBundle.getMessage(key, null,locale);
    } catch (NoSuchMessageException e) {
	logger.warn("can not finfd message "+key+" in bundle");
	return key;
    }
       
    }

    
    /* (non-Javadoc)
     * @see com.gisgraphy.service.IInternationalisationService#getString(java.lang.String, java.lang.Object[])
     */
    public String getString(String key, Object[] params) {
	 try {
	     return this.resourceBundle.getMessage(key, params, locale);
	    } catch (NoSuchMessageException e) {
		logger.warn("can not finfd message "+key+" in bundle");
		return key;
	    }
        
    }
    
   
    /* (non-Javadoc)
     * @see com.gisgraphy.service.IInternationalisationService#getLocale()
     */
    public Locale getLocale() {
        return locale;
    }


    /* (non-Javadoc)
     * @see com.gisgraphy.service.IInternationalisationService#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    
    public void setResourceBundle(ReloadableResourceBundleMessageSource resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
    
}
