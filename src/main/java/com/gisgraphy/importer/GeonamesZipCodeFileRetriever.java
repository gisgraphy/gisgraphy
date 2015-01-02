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
package com.gisgraphy.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieve The Geonames zip code files from a server
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeonamesZipCodeFileRetriever extends AbstractFileRetriever {
	
	protected static final Logger logger = LoggerFactory.getLogger(GeonamesZipCodeFileRetriever.class);

    /*
     * (non-Javadoc)
     * 
     * @seecom.gisgraphy.domain.geoloc.importer.AbstractFileRetriever#
     * getDownloadDirectory()
     */
    public String getDownloadDirectory() {
	return importerConfig.getGeonamesZipCodeDir();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gisgraphy.domain.geoloc.importer.AbstractFileRetriever#getDownloadBaseUrl
     * ()
     */
    public String getDownloadBaseUrl() {
	return importerConfig.getGeonamesZipCodeDownloadURL();
    }


    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gisgraphy.domain.geoloc.importer.AbstractFileRetriever#decompressFiles
     * ()
     */
    public void decompressFiles() throws IOException  {
	File[] filesToUnZip = getFilesToDecompress();
	for (int i = 0; i < filesToUnZip.length; i++) {
	    ImporterHelper.unzipFile(filesToUnZip[i]);
	}

	// for log purpose
	File[] filesToImport = ImporterHelper.listCountryFilesToImport(getDownloadDirectory());

	for (int i = 0; i < filesToImport.length; i++) {
	    logger.info("the files " + filesToImport[i].getName() + " will be imported");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gisgraphy.domain.geoloc.importer.AbstractFileRetriever#shouldBeSkipped
     * ()
     */
    @Override
    public boolean shouldBeSkipped() {
	return !importerConfig.isGeonamesImporterEnabled();
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractFileRetriever#getFilesToDownload()
     */
    @Override
    List<String> getFilesToDownload() {
	return importerConfig.getGeonamesDownloadFilesListFromOption();
    }

    @Override
    public File[] getFilesToDecompress() throws IOException {
	return ImporterHelper.listZipFiles(getDownloadDirectory());
    }

    @Override
    public boolean isFileNotFoundTolerant() {
	return true;
    }

}
