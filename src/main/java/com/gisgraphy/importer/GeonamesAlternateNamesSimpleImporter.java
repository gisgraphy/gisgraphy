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

import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.geoloc.entity.ZipCodesAware;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.IAlternateNameDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.fulltext.spell.ISpellCheckerIndexer;

/**
 * Import the Alternate names.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeonamesAlternateNamesSimpleImporter extends AbstractSimpleImporterProcessor {
	
	protected static final Logger logger = LoggerFactory.getLogger(GeonamesAlternateNamesSimpleImporter.class);

	protected IGisFeatureDao gisFeatureDao;

    protected ICityDao cityDao;
    
    protected IAdmDao admDao;

    protected IAlternateNameDao alternateNameDao;

    protected ISolRSynchroniser solRSynchroniser;

    private static final Long defaultFeatureId = Long.valueOf(-1);
    
    protected ISpellCheckerIndexer spellCheckerIndexer;


    /**
     * Default constructor
     */
    public GeonamesAlternateNamesSimpleImporter() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
     */
    @Override
    protected void processData(String line) {
	String[] fields = line.split("\t");

	/*
	 * line table has the following fields :
	 * ----------------------------------------- 0 : alternateNameId : 1 :
	 * geonameid : 2 : isolanguage : iso 639-2 or 3 or or 'post' 3 :
	 * alternate name 4 : isPreferredName 5 : isShortName
	 */

	AlternateName alternateName = null;
	// we don't check length because some fields like preferredname or
	// shortname are not mandatory
	// checkNumberOfColumn(fields);
	if (!isEmptyField(fields, 0, true)) {
	    // check if need to be inserted or updated
	    // alternateName = this.alternateNameDao.get(new Long(fields[0]));
	    // if (alternateName == null) {
	    alternateName = new AlternateName();
	    alternateName.setAlternateNameId(new Integer(fields[0]));
	    // }

	}

	GisFeature gisFeature = null;

	Long gisFeatureId = defaultFeatureId;
	if (!isEmptyField(fields, 1, true)) {
	    try {
		gisFeatureId = new Long(fields[1]);
	    } catch (NumberFormatException e) {
		logger.warn("The featureId " + fields[1] + " is not a number");
		return;
	    }
	    // get the features
	   // to improve performance we first search in cities then adm and finally in all features
	    gisFeature = cityDao.getByFeatureId(gisFeatureId);
	    if (gisFeature == null){
		gisFeature = this.admDao.getByFeatureId(gisFeatureId);
	    }
	    if (gisFeature == null){
		gisFeature = this.gisFeatureDao.getByFeatureId(gisFeatureId);
	    }
	    if (gisFeature == null) {
		return;
	    }

	}

	alternateName.setGisFeature(gisFeature);
	if (isAnUnWantedLanguageField(fields[2])) {
	    return;
	}

	// it is not an empty field =>will save the alternatename

	// set name and sources
	if (!isEmptyField(fields, 3, true)) {
	    alternateName.setName(fields[3].trim());
	}

	alternateName.setSource(AlternateNameSource.ALTERNATENAMES_FILE);

	// if it is a post=>update zipCode
	if (fields[2].equals("post")) {
	    // it is a zip code :set and save gisfeature
	    if (!isEmptyField(fields, 3, false)) {
		// if it is a city we update the city
		    if (!(gisFeature instanceof ZipCodesAware)) {
		    	logger.error("We've got a zipCode for a feature ("+gisFeature+") but "+gisFeature.getClass().getSimpleName()+
						" is not 'zipCodes aware'," +
						" that mean that zipcode is added but the field" +
						" will not be added in the feed of the web service");
		    } 
		    gisFeature.addZipCode(new ZipCode(fields[3]));
			// Hibernate will save the feature according to his
			// class even if it is cast in an other class
			this.gisFeatureDao.save(gisFeature);
			return;
	    } else {
		logger.warn("could not set ZipCode for GisFeature["
			+ gisFeatureId + "] because zipCode is null");
		return;
	    }
	} else {
	    // it is probably a language field;
	    alternateName.setLanguage(fields[2]);
	}

	// preferred name
	if (!isEmptyField(fields, 4, false)) {
	    if (fields[4].equals("1")) {
		alternateName.setPreferredName(true);
	    } else {
		alternateName.setPreferredName(false);
	    }

	}

	// short name
	if (!isEmptyField(fields, 5, false)) {
	    if (fields[5].equals("1")) {
		alternateName.setShortName(true);
	    } else {
		alternateName.setShortName(false);
	    }
	}

	/*List<AlternateName> alternateNames = gisFeature.getAlternateNames();
	if (alternateNames == null) {
	    alternateNames = new ArrayList<AlternateName>();
	}
	alternateNames.add(alternateName);*/
	gisFeature.addAlternateName(alternateName);

	this.gisFeatureDao.save(gisFeature);

    }

    private boolean isAnUnWantedLanguageField(String languageField) {
	boolean unWanted = false;
	// TODO v2 option : || languageField.equals("icao") ||
	// languageField.equals("iata")
	if (languageField.equals("fr-1793")) {
	    unWanted = true;
	}
	return unWanted;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreFirstLine()
     */
    @Override
    protected boolean shouldIgnoreFirstLine() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreComments()
     */
    @Override
    protected boolean shouldIgnoreComments() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#setCommitFlushMode()
     */
    @Override
    protected void setCommitFlushMode() {
	this.gisFeatureDao.setFlushMode(FlushMode.COMMIT);
	this.cityDao.setFlushMode(FlushMode.COMMIT);
	this.alternateNameDao.setFlushMode(FlushMode.COMMIT);
    }

    @Override
    protected void flushAndClear() {
	this.gisFeatureDao.flushAndClear();
	this.cityDao.flushAndClear();
	this.alternateNameDao.flushAndClear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
	return 6;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#tearDown()
     */
    @Override
    protected void tearDown() {
	super.tearDown();
	if (!solRSynchroniser.commit()){
	    logger.warn("The commit in tearDown of "+this.getClass().getSimpleName()+" has failed, the uncommitted changes will be commited with the auto commit of solr in few minuts");
	}
	spellCheckerIndexer.buildAllIndex();
	solRSynchroniser.optimize();
    }

    /**
     * @param alternateNameDao
     *                The alternateDao to set
     */
    @Required
    public void setAlternateNameDao(IAlternateNameDao alternateNameDao) {
	this.alternateNameDao = alternateNameDao;
    }

    /**
     * @param gisFeatureDao
     *                The GisFeatureDao to set
     */
    @Required
    public void setGisFeatureDao(IGisFeatureDao gisFeatureDao) {
	this.gisFeatureDao = gisFeatureDao;
    }

    /**
     * @param cityDao
     *                The cityDao to set
     */
    @Required
    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    
    /**
     * @param admDao the admDao to set
     */
    @Required
    public void setAdmDao(IAdmDao admDao) {
        this.admDao = admDao;
    }

    @Override
    public boolean shouldBeSkipped() {
        if (importerConfig.isImportGisFeatureEmbededAlternateNames() || !importerConfig.isGeonamesImporterEnabled()){
            return true ;
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
	if (importerConfig.isImportGisFeatureEmbededAlternateNames()) {
	    logger
		    .info("ImportGisFeatureEmbededAlternateNames = true, we do not import alternatenames from "
			    + importerConfig.getAlternateNamesFileName());
	    return new File[0];
	}
	File[] files = new File[1];
	files[0] = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getAlternateNameFeaturesFileName());
	return files;
    }

    /**
     * @param solRSynchroniser
     *                the solRSynchroniser to set
     */
    @Required
    public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
	this.solRSynchroniser = solRSynchroniser;
    }
    
    /**
     * @param spellCheckerIndexer the spellCheckerIndexer to set
     */
    public void setSpellCheckerIndexer(ISpellCheckerIndexer spellCheckerIndexer) {
        this.spellCheckerIndexer = spellCheckerIndexer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
	List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
	logger.info("deleting alternateNames...");
	int deletedAnames = alternateNameDao.deleteAll();
	if (deletedAnames != 0) {
	    deletedObjectInfo.add(new NameValueDTO<Integer>(GisFeature.class
		    .getSimpleName(), deletedAnames));
	}
	logger.info(deletedAnames + " alternateNames have been deleted");
	resetStatus();
	return deletedObjectInfo;
    }

}
