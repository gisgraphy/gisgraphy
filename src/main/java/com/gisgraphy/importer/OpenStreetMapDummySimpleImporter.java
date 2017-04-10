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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.NameValueDTO;

/**
 * Import the street from an (pre-processed) openStreet map data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenStreetMapDummySimpleImporter extends AbstractSimpleImporterProcessor {
	
	protected static final Logger logger = LoggerFactory.getLogger(OpenStreetMapDummySimpleImporter.class);
	

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
    	return ImporterHelper.listCountryFilesToImport(importerConfig.getOpenStreetMapDir());
    }


    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
     */
    @Override
    protected void processData(String line) throws ImporterException {
	logger.error(line);

    }


	@Override
	public List<NameValueDTO<Integer>> rollback() {
		return null;
	}


	@Override
	protected boolean shouldIgnoreFirstLine() {
		return false;
	}


	@Override
	protected void flushAndClear() {
	}


	@Override
	protected void setCommitFlushMode() {
	}


	@Override
	protected int getNumberOfColumns() {
		return 0;
	}


	@Override
	protected boolean shouldIgnoreComments() {
		return true;
	}
    
   
    
    
}
