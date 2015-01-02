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
package com.gisgraphy.importer;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import the Alternate names.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeonamesAlternateNamesAdmImporter extends GeonamesAlternateNamesSimpleImporter {

	protected static final Logger logger = LoggerFactory.getLogger(GeonamesAlternateNamesAdmImporter.class);
	
		 @Override
		    protected File[] getFiles() {
			if (importerConfig.isImportGisFeatureEmbededAlternateNames()) {
			    logger
				    .info("ImportGisFeatureEmbededAlternateNames = true, we do not import alternatenames from "
					    + importerConfig.getAlternateNamesFileName());
			    return new File[0];
			}
			File[] files = new File[3];
			files[0] = new File(importerConfig.getGeonamesDir()
				+ importerConfig.getAlternateNameCountryFileName());
			files[1] = new File(importerConfig.getGeonamesDir()
				+ importerConfig.getAlternateNameAdm1FileName());
			files[2] = new File(importerConfig.getGeonamesDir()
				+ importerConfig.getAlternateNameAdm2FileName());
			return files;
		    }

}
