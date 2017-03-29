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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.FlushMode;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICitySubdivisionDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.domain.valueobject.SpeedMode;
import com.gisgraphy.fulltext.FullTextSearchEngine;
import com.gisgraphy.geoloc.GeolocSearchEngine;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.street.StreetType;
import com.gisgraphy.util.StringUtil;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the street from an (pre-processed) openStreet map data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class DatastoreOptimizer extends AbstractSimpleImporterProcessor {
	
	protected static final Logger logger = LoggerFactory.getLogger(DatastoreOptimizer.class);
	
   
    @Autowired
    protected ISolRSynchroniser solRSynchroniser;

    @Autowired
	private IOpenStreetMapDao openStreetMapDao;
    

    
   
    
    @Override
    protected void setup() {
        super.setup();
        //temporary disable logging when importing
        FullTextSearchEngine.disableLogging=true;
        GeolocSearchEngine.disableLogging=true;
    }
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
    	return new File[0];
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
    	return 0;
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
     */
    @Override
    protected void processData(String line) throws ImporterException {
    }
    
   
		

    
	/* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped()
     */
    @Override
    public boolean shouldBeSkipped() {
    	return false;
    }
    
   


    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#setCommitFlushMode()
     */
    @Override
    protected void setCommitFlushMode() {
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreComments()
     */
    @Override
    protected boolean shouldIgnoreComments() {
    	return true;
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreFirstLine()
     */
    @Override
    protected boolean shouldIgnoreFirstLine() {
    	return true;
    }
   
    
   
    
    @Override
    //TODO test
    protected void tearDown() {
    	super.tearDown();
    	FullTextSearchEngine.disableLogging=false;
    	GeolocSearchEngine.disableLogging=false;
    	String savedMessage = this.statusMessage;
    	try {
    		this.statusMessage = internationalisationService.getString("import.fulltext.optimize");
    		solRSynchroniser.optimize();
    		this.statusMessage = internationalisationService.getString("import.message.createIndex");
    		openStreetMapDao.createSpatialIndexes();
    	} catch (Exception e) {
    		logger.error("an error occured during optimization, we ignore it but you have to manually run it to have good performances : "+e.getMessage(),e);
    	} finally{
        	this.statusMessage=savedMessage;
        }
    }
    
    /**
     * overidded because alternatenames can be null so number of fields can differ
     * 
     * @see #getNumberOfColumns()
     * @param fields
     *                The array to check
     */
    @Override
    protected void checkNumberOfColumn(String[] fields) {
    }
    
    
    @Required
    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }
   

    @Required
    public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
        this.solRSynchroniser = solRSynchroniser;
    }


	public List<NameValueDTO<Integer>> rollback() {
		return null;
	}


	@Override
	protected void flushAndClear() {
		
	}

   
    
    
}
