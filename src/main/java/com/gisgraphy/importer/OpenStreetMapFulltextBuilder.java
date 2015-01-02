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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.IdGenerator;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.service.IInternationalisationService;

/**
 * build the fulltext engine in order to use the street fulltext search
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenStreetMapFulltextBuilder implements IImporterProcessor {

   /**
    * The logger
    */
   	protected static final Logger logger = LoggerFactory.getLogger(OpenStreetMapFulltextBuilder.class);
    	
	private ImporterConfig importerConfig;

	private IInternationalisationService internationalisationService;

	private IOpenStreetMapDao openStreetMapDao;

	/**
	 * The paginate step
	 */
	protected int increment = 10000;
	
	protected ImporterStatus status = ImporterStatus.WAITING;

	private long lineProcessed = 0;

	private long numberOfLinesToProcess = 0;


	private String statusMessage;

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear()
	 */
	protected void flushAndClear() {
		openStreetMapDao.flushAndClear();

	}

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
	 */
	public List<NameValueDTO<Integer>> rollback() {
		List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
		logger.info("rollback OpenStreetMapFulltextBuilder ");
		resetStatus();
		return deletedObjectInfo;
	}

	/**
	 * @param openStreetMapDao the openStreetMapDao to set
	 */
	@Required
	public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
		this.openStreetMapDao = openStreetMapDao;
	}



	public String getCurrentFileName() {
		return this.getClass().getSimpleName();
	}

	public long getNumberOfLinesToProcess() {
		return numberOfLinesToProcess;
	}

	public long getReadFileLine() {
		return lineProcessed;
	}

	public ImporterStatus getStatus() {
		return status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public long getTotalReadLine() {
		return lineProcessed;
	}

	public void process() {
		status = ImporterStatus.PROCESSING;
		try {
			if (!shouldBeSkipped()) {
				setup();
			    statusMessage = internationalisationService.getString("import.build.openstreetmap.fulltext.searchEngine.count");
				numberOfLinesToProcess = new Long(openStreetMapDao.countEstimate()).intValue();
				logger.info(numberOfLinesToProcess+" streets will be build for the fulltext engine");
				statusMessage = internationalisationService.getString("import.build.openstreetmap.fulltext.searchEngine");
				for (long start=IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT;start<=(numberOfLinesToProcess+IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT);start = start+increment){
				    long from = start;
				    long to = start+increment-1;
				    logger.info("paginate build of openstreetmap fulltext engine for streets from "+start+" to "+to);
					openStreetMapDao.updateTS_vectorColumnForStreetNameSearchPaginate(from, to);
					lineProcessed = Math.min(to-IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT, numberOfLinesToProcess);
				}
				
				this.status = ImporterStatus.PROCESSED;
			} else {
				logger.info("OpenStreetMapFulltextBuilder will be skiped");
				this.status = ImporterStatus.SKIPPED;
			}
			statusMessage = "";
		} catch (Exception e) {
			this.statusMessage = "Error during the construction of the openstreetmap fulltext engine : " + e.getMessage();
			logger.error(statusMessage,e);
			status = ImporterStatus.ERROR;
			throw new ImporterException(statusMessage, e);
		} 

	}

	protected void setup() {
		statusMessage = internationalisationService.getString("import.message.createIndex");
		openStreetMapDao.createFulltextIndexes();
	}

	public void resetStatus() {
		this.lineProcessed = 0;
		this.numberOfLinesToProcess = 0;
		this.status = ImporterStatus.WAITING;
		this.statusMessage = "";
	}

	public boolean shouldBeSkipped() {
		return !importerConfig.isOpenstreetmapImporterEnabled() || !GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE;
	}

	public ImporterConfig getImporterConfig() {
	    return importerConfig;
	}

	@Required
	public void setImporterConfig(ImporterConfig importerConfig) {
	    this.importerConfig = importerConfig;
	}

	@Required
	public void setInternationalisationService(IInternationalisationService internationalisationService) {
	    this.internationalisationService = internationalisationService;
	}

	public IOpenStreetMapDao getOpenStreetMapDao() {
	    return openStreetMapDao;
	}
	
}
