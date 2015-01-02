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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.IAlternateNameDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.domain.repository.IGisDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.FeatureCode;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.helper.FeatureClassCodeHelper;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.util.StringUtil;

/**
 * Import the Features from a Geonames dump file.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeonamesFeatureSimpleImporter extends AbstractSimpleImporterProcessor {

	protected static final Logger logger = LoggerFactory.getLogger(GeonamesFeatureSimpleImporter.class);
	
    protected ICityDao cityDao;

    protected IGisFeatureDao gisFeatureDao;

    protected IAlternateNameDao alternateNameDao;

    protected IAdmDao admDao;

  
    protected ICountryDao countryDao;

    protected Pattern acceptedPatterns ;
    

    protected ISolRSynchroniser solRSynchroniser;

    @Autowired
    protected IGisDao<? extends GisFeature>[] iDaos;
    
    @Autowired
    protected IMunicipalityDetector municipalityDetector;
    

    /**
     * Default constructor
     */
    public GeonamesFeatureSimpleImporter() {
	super();
    }

    private SimpleDateFormat dateFormatter = new SimpleDateFormat(
	    Constants.GIS_DATE_PATTERN);

    boolean isPlaceTypeAccepted(String classname) {
	if (acceptedPatterns.matcher(classname).find()){
		return true;
	}
	return false;
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
	 * --------------------------------------------------- 0 geonameid : 1
	 * name 2 asciiname 3 alternatenames 4 latitude 5 longitude 6 feature
	 * class 7 feature code 8 country code 9 cc2 10 admin1 code 11 admin2
	 * code 12 admin3 code 13 admin4 code 14 population 15 elevation 16
	 * gtopo30 17 timezone 18 modification date last modification in
	 * yyyy-MM-dd format
	 */

	// check that the csv file line is in a correct format
	checkNumberOfColumn(fields);
	String featureClass = null;
	String featureCode = null;

	// featureClass
	if (!isEmptyField(fields, 6, false)) {
	    featureClass = fields[6];
	} else {
	    featureClass = ImporterConfig.DEFAULT_FEATURE_CLASS;
	    logger.warn("[wrongFeatureClass] : set featureClass to "
		    + ImporterConfig.DEFAULT_FEATURE_CODE + " for gisFeature  "
		    + fields[0]);
	}

	// featureCode
	if (!isEmptyField(fields, 7, false)) {
	    featureCode = fields[7];
	} else {
	    featureCode = ImporterConfig.DEFAULT_FEATURE_CODE;
	    logger.warn("[wrongFeatureCode] set featureCode to "
		    + ImporterConfig.DEFAULT_FEATURE_CODE + " for gisFeature  "
		    + fields[0]);
	}

	if (FeatureClassCodeHelper.is_Adm(featureClass,featureCode) && !isAdmMode()){
	    return;
	}
	// TODO v2 virtualizeADMD
	// fields = ImporterHelper.virtualizeADMD(fields);
	fields = ImporterHelper.correctLastAdmCodeIfPossible(fields);

	FeatureCode featureCode_ = null;

	try {
	    featureCode_ = FeatureCode
		    .valueOf(featureClass + "_" + featureCode);
	} catch (RuntimeException e) {
	}
	GisFeature gisFeature = null;
	String name = fields[1];
	if (name.length() > GisFeature.NAME_MAX_LENGTH){
		logger.warn(name + "is too long");
		return;
	}
	if (featureCode_ != null ) {
		gisFeature = (GisFeature) featureCode_.getObject();
		gisFeature = correctPlaceType(gisFeature, name);
	    if (gisFeature!=null && !isPlaceTypeAccepted(gisFeature.getClass().getSimpleName())){
	    	return;
	    }
	} else {
		gisFeature = new GisFeature();
	}
	

	
	// create GisFeature and set featureId
	if (!isEmptyField(fields, 0, true)) {
	   // gisFeature = new GisFeature();
	    gisFeature.setFeatureId(new Long(fields[0]));
	}

	// set names
	if (!isEmptyField(fields, 1, true)) {
	    gisFeature.setName(name.trim());
	}

	gisFeature.setAsciiName(fields[2].trim());

	// Location
	if (!isEmptyField(fields, 4, true) && !isEmptyField(fields, 5, true)) {
	    gisFeature.setLocation(GeolocHelper.createPoint(
		    new Float(fields[5]), new Float(fields[4])));
	}

	// featureClass
	gisFeature.setFeatureClass(featureClass);

	// featureCode
	gisFeature.setFeatureCode(featureCode);
	

	// countrycode
	if (!isEmptyField(fields, 8, true)) {
	    gisFeature.setCountryCode(fields[8].toUpperCase());
	}

	// ignore cc2

	// population
	if (!isEmptyField(fields, 14, false)) {
	    gisFeature.setPopulation(new Integer(fields[14]));
	}

	// elevation
	if (!isEmptyField(fields, 15, false)) {
	    gisFeature.setElevation(new Integer(fields[15]));
	} else {
	    gisFeature.setElevation(null);
	}

	// gtopo30
	if (!isEmptyField(fields, 16, false)) {
	    gisFeature.setGtopo30(new Integer(fields[16]));
	}

	// timeZone
	gisFeature.setTimezone(fields[17]);

	// source
	gisFeature.setSource(GISSource.GEONAMES);

	// modificationDate
	if (!isEmptyField(fields, 18, false)) {
	    try {
		gisFeature.setModificationDate(dateFormatter.parse(fields[18]));
	    } catch (ParseException e) {
		gisFeature.setModificationDate(null);
		logger
			.info("[wrongModificationDate] Modificationdate is not properly set for featureId "
				+ fields[0]);
	    }
	}

	// add alternatenames
	// not necessary because alternatenames will be added by its own
	// importer
	if (!isEmptyField(fields, 3, false)
		&& importerConfig.isImportGisFeatureEmbededAlternateNames()) {
	    List<AlternateName> splitedAlternateNames = splitAlternateNames(fields[3],
		    gisFeature);
		gisFeature.addAlternateNames(splitedAlternateNames);
	}

	//TODO  //NAI list countryid
	Country country = this.countryDao.getByFeatureId(new Long(fields[0]));

	if (country != null) {
	    String countryName = country.getName();
	    country.populate(gisFeature);
	    // we preffer keep the original name (example : we prefer
	    // France,
	    // instead of Republic Of France
	    country.setName(countryName);
	    this.countryDao.save(country);
	    return;
	}

	
	if (featureCode_ != null) {
	    if (featureCode_.getObject() instanceof Country) {
		logger.warn("[wrongCountryCode] Country " + fields[8]
			+ " have no entry in "
			+ importerConfig.getCountriesFileName()
			+ " or has not been imported. It will be ignored");
		return;

	    }
	}

	// Rem :if we don't set the code they will be null for object that
	// extends gisfeature when populate() will be called
	// Rem : country don't have their admXcodes and AdmXnames
	// set admcodes
	setAdmCodesWithCSVOnes(fields, gisFeature);

	// if gis Feature is an ADM need to update ADM with this GisFeature
		if (gisFeature.isAdm()) {
			if (isAdmMode()) {
				Adm adm = processAdm(fields, gisFeature);
				if (adm != null) {
					this.admDao.save(adm);
				}
				return;
			} else {
				// it is an adm that must be treated in adm importer
				return;
			}
		}

	// it is not an adm, not a country =>try to set Adm
	Adm adm = null;
	if (importerConfig.isTryToDetectAdmIfNotFound()) {
	    adm = this.admDao.suggestMostAccurateAdm(fields[8], fields[10],
		    fields[11], fields[12], fields[13], gisFeature);
	    logger.debug("suggestAdm=" + adm);
	} else {
	    adm = this.admDao.getAdm(fields[8], fields[10], fields[11],
		    fields[12], fields[13]);
	}

	// log
	if (adm == null) {
	    logger.warn("[noAdm] " + fields[8] + "." + fields[10] + "."
		    + fields[11] + "." + fields[12] + "." + fields[13]
		    + " for " + gisFeature);
	} else {
	    if ("00".equals(fields[10]) && !featureCode.startsWith("ADM")) {
		logger
			.info("[adm1autoDetected];" + gisFeature.getFeatureId()
				+ ";" + gisFeature.getName() + ";"
				+ gisFeature.getFeatureClass() + ";"
				+ gisFeature.getFeatureCode() + ";"
				+ adm.getAdm1Code());
		// see http://forum.geonames.org/gforum/posts/list/699.page
	    }

	}
	gisFeature.setAdm(adm);
	setAdmCodesWithLinkedAdmOnes(adm, gisFeature, importerConfig
		.isSyncAdmCodesWithLinkedAdmOnes());
	setAdmNames(adm, gisFeature);

	if (featureCode_ != null) {
	   if (gisFeature instanceof City){
	    	((City)gisFeature).setMunicipality(municipalityDetector.isMunicipality(fields[8].toUpperCase(),null,null,GISSource.GEONAMES));
	    }
	    
		// zipcode
	   
		String foundZipCode = findZipCode(fields);
		if (foundZipCode != null){
			gisFeature.addZipCode(new ZipCode(foundZipCode));//TODO tests zip we should take embeded option into account
		}
	    this.gisFeatureDao.save(gisFeature);
	} else {
	    logger.debug(featureClass + "_" + featureCode
		    + " have no entry in " + FeatureCode.class.getSimpleName()
		    + " and will be considered as a GisFeature");
	    this.gisFeatureDao.save(gisFeature);
	}
	// }

    }

	protected GisFeature correctPlaceType(GisFeature featureObject, String name) {
		if (StringUtil.containsDigit(name) && featureObject!=null && featureObject!=null && featureObject.getClass()==City.class){
	    	featureObject= new CitySubdivision();
	    }
		return featureObject;
	}
    
    protected boolean isAdmMode() {
        return false;
    }

    protected Adm processAdm(String[] fields, GisFeature gisFeature) {
	int levelFromCode = Adm.getProcessedLevelFromCodes(fields[10],
	    fields[11], fields[12], fields[13]);
	int levelFromClassCode = Adm.getProcessedLevelFromFeatureClassCode(
	    fields[6], fields[7]);
	// check if data are consistant
	if (levelFromCode != levelFromClassCode) {
	logger.warn("[unprocessed Adm] : The Adm " + fields[8] + "."
		+ fields[10] + "." + fields[11] + "." + fields[12]
		+ "." + fields[13] + " is not consistant for "
		+ fields[6] + "." + fields[7] + " adm" + "["
		+ fields[0] + "] will be ignored");
	return null;
	}
	Adm adm = this.admDao.getAdm(fields[8], fields[10], fields[11],
	    fields[12], fields[13]);
	if (adm != null) {
		logger
		.warn("[unprocessed Adm] : "+
			gisFeature+ " will not be saved because it is duplicate (same codes) with "
			+ adm);	
		return null;
	}

	if (levelFromCode != 0) {
	    adm = new Adm(levelFromCode);
	    //adm.setName(fields[1].trim());
	    adm.setAdm1Name(fields[10]);
	    adm.setAdm2Name(fields[11]);
	    adm.setAdm3Name(fields[12]);
	    adm.setAdm4Name(fields[13]);
	    // the only goal to do this code is to get the adm codes in
	    // the
	    // logs bellow when toString will be called (in other way it
	    // will be done by the populate)
	    setAdmCodesWithCSVOnes(fields, adm);
	    // try to link to his parent
	    if (levelFromCode>1){
	    Adm admParent = this.admDao
		    .getAdmOrFirstValidParentIfNotFound(fields[8],
			    fields[10], fields[11], fields[12],
			    fields[13]);
	    if (admParent != null) {
	    	adm.setParent(admParent);
	    } else {
		logger
			.warn("[unprocessed Adm] : won't save an adm"
				+ levelFromCode
				+ " : "
				+ adm
				+ " that have not been import when AdmXCodes and without parent");
		return null;
	    }
	    }
	} else {
	    // we can do anything with an Adm with Wrong code/level
	    logger
		    .warn("[unprocessedAdm] : Could not detect level of Adm "
			    + adm + ". it will be ignored");
	    return null;
	}
	if (isAlreadyUpdated(adm)) {// needed for duplicate
	// we only keep the first adm
	return null;
	}
	setAdmNames(adm, gisFeature);
	adm.populate(gisFeature);
	return adm;
    }
    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped()
     */
    @Override
    public boolean shouldBeSkipped() {
	return !importerConfig.isGeonamesImporterEnabled();
    }

    private boolean isAlreadyUpdated(GisFeature feature) {
	if (feature.getModificationDate() != null) {
	    logger
		    .info(feature
			    + " has already been updated, it is probably a duplicate entry");
	    return true;
	}
	return false;
    }

    private void setAdmNames(Adm adm, GisFeature gisFeature) {
	if (adm == null) {
	    return;
	}
	Adm admTemp = adm;
	do {
	    if (admTemp.getLevel() == 1) {
		gisFeature.setAdm1Name(admTemp.getName());
	    } else if (admTemp.getLevel() == 2) {
		gisFeature.setAdm2Name(admTemp.getName());
	    } else if (admTemp.getLevel() == 3) {
		gisFeature.setAdm3Name(admTemp.getName());
	    } else if (admTemp.getLevel() == 4) {
		gisFeature.setAdm4Name(admTemp.getName());
	    }
	    admTemp = admTemp.getParent();
	} while (admTemp != null);

    }

    private void setAdmCodesWithLinkedAdmOnes(Adm adm, GisFeature gisFeature,
	    boolean syncAdmCodesWithLinkedAdmOnes) {

	if (syncAdmCodesWithLinkedAdmOnes) {
	    // reset adm code because we might link to an adm3 and adm4 code
	    // have
	    // been set
	    setAdmCodesToNull(gisFeature);
	    if (adm != null) {
		if (adm.getAdm1Code() != null) {
		    gisFeature.setAdm1Code(adm.getAdm1Code());
		}
		if (adm.getAdm2Code() != null) {
		    gisFeature.setAdm2Code(adm.getAdm2Code());
		}
		if (adm.getAdm3Code() != null) {
		    gisFeature.setAdm3Code(adm.getAdm3Code());
		}
		if (adm.getAdm4Code() != null) {
		    gisFeature.setAdm4Code(adm.getAdm4Code());
		}
	    }

	}
    }

    private void setAdmCodesToNull(GisFeature gisFeature) {
	gisFeature.setAdm1Code(null);
	gisFeature.setAdm2Code(null);
	gisFeature.setAdm3Code(null);
	gisFeature.setAdm4Code(null);
    }

    private void setAdmCodesWithCSVOnes(String[] fields, GisFeature gisFeature) {
	logger.debug("in setAdmCodesWithCSVOnes");
	if (!isEmptyField(fields, 10, false)) {
	    gisFeature.setAdm1Code(fields[10]);
	}
	if (!isEmptyField(fields, 11, false)) {
	    gisFeature.setAdm2Code(fields[11]);
	}
	if (!isEmptyField(fields, 12, false)) {
	    gisFeature.setAdm3Code(fields[12]);
	}
	if (!isEmptyField(fields, 13, false)) {
	    gisFeature.setAdm4Code(fields[13]);
	}
    }

    
    
    private List<AlternateName> splitAlternateNames(String alternateNamesString,
	    GisFeature gisFeature) {
	String[] alternateNames = alternateNamesString.split(",");
	List<AlternateName> alternateNamesList = new ArrayList<AlternateName>();
	for (String name : alternateNames) {
	    AlternateName alternateName = new AlternateName();
	    alternateName.setName(name.trim());
	    alternateName.setSource(AlternateNameSource.EMBEDED);
	    alternateName.setGisFeature(gisFeature);
	    alternateNamesList.add(alternateName);
	}
	return alternateNamesList;
    }

    private String findZipCode(String[] fields) {
	logger.debug("try to detect zipCode for " + fields[1] + "[" + fields[0]
		+ "]");
	String zipCode = null;
	String[] alternateNames = fields[3].split(",");
	boolean found = false;
	Pattern patterncountry = null;
	Matcher matcherCountry = null;
	if (!isEmptyField(fields, 8, false)) {
	    Country country = countryDao.getByIso3166Alpha2Code(fields[8]);
	    if (country != null) {
		String regex = country.getPostalCodeRegex();
		if (regex != null) {
		    patterncountry = Pattern.compile(regex);
		    if (patterncountry == null) {
			logger.info("can not compile regexp" + regex);
			return null;
		    }
		} else {
		    logger.debug("regex=null for country " + country);
		    return null;
		}
	    } else {
		logger
			.warn("can not proces ZipCode because can not find country for "
				+ fields[8]);
		return null;
	    }

	} else {
	    logger.warn("can not proces ZipCode because can not find country ");
	}
	for (String element : alternateNames) {
	    matcherCountry = patterncountry.matcher(element);
	    if (matcherCountry.matches()) {
		if (found) {
		    logger
			    .info("There is more than one possible ZipCode for feature with featureid="
				    + fields[0] + ". it will be ignore");
		    return null;
		}
		try {
		    zipCode = element;
		    found = true;
		} catch (NumberFormatException e) {
		}

	    }
	}
	logger.debug("found " + zipCode + " for " + fields[1] + "[" + fields[0]
		+ "]");
	return zipCode;
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
    	this.cityDao.setFlushMode(FlushMode.COMMIT);
    	this.gisFeatureDao.setFlushMode(FlushMode.COMMIT);
    	this.alternateNameDao.setFlushMode(FlushMode.COMMIT);
    	this.admDao.setFlushMode(FlushMode.COMMIT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear()
     */
    @Override
    protected void flushAndClear() {
    	this.cityDao.flushAndClear();
    	this.gisFeatureDao.flushAndClear();
    	this.alternateNameDao.flushAndClear();
    	this.admDao.flushAndClear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
	return 19;
    }

    /**
     * @param cityDao
     *                The CityDao to set
     */
    @Required
    public void setCityDao(ICityDao cityDao) {
	this.cityDao = cityDao;
    }

    /**
     * @param alternateNameDao
     *                The alternateNameDao to set
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
     * @param admDao
     *                the admDao to set
     */
    @Required
    public void setAdmDao(IAdmDao admDao) {
	this.admDao = admDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#setup()
     */
    @Override
    public void setup() {
	super.setup();
	acceptedPatterns = ImporterHelper.compileRegex(importerConfig
		.getAcceptRegExString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#tearDown()
     */
    @Override
    protected void tearDown() {
    String savedMessage =this.statusMessage;
    this.statusMessage=internationalisationService.getString("import.teardown");
    try {
	super.tearDown();
	if (!solRSynchroniser.commit()){
	    logger.warn("The commit in tearDown of "+this.getClass().getSimpleName()+" has failed, the uncommitted changes will be commited with the auto commit of solr in few minuts");
	}
	solRSynchroniser.optimize();
    } finally{
    	this.statusMessage=savedMessage;
    }
    }

    /**
     * @param countryDao
     *                The countryDao to set
     */
    @Required
    public void setCountryDao(ICountryDao countryDao) {
	this.countryDao = countryDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
	return ImporterHelper.listCountryFilesToImport(importerConfig
		.getGeonamesDir());
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
     * @param daos
     *                the iDaos to set
     */
    public void setIDaos(IGisDao<? extends GisFeature>[] daos) {
	iDaos = daos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
	List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
	// we first reset subClass
	for (IGisDao<? extends GisFeature> gisDao : iDaos) {
	    if (gisDao.getPersistenceClass() != GisFeature.class
		    && gisDao.getPersistenceClass() != Adm.class
		    && gisDao.getPersistenceClass() != Country.class) {
		logger.warn("deleting "
			+ gisDao.getPersistenceClass().getSimpleName() + "...");
		// we don't want to remove adm because some feature can be
		// linked again
		int deletedgis = gisDao.deleteAll();
		logger.warn(deletedgis+" "
			+ gisDao.getPersistenceClass().getSimpleName()
			+ " have been deleted");
		if (deletedgis != 0) {
		    deletedObjectInfo.add(new NameValueDTO<Integer>(
			    GisFeature.class.getSimpleName(), deletedgis));
		}
	    }
	}
	logger.warn("deleting gisFeature...");
	// we don't want to remove adm because some feature can be linked again
	int deletedgis = gisFeatureDao.deleteAllExceptAdmsAndCountries();
	logger.warn(deletedgis + " gisFeature have been deleted");
	if (deletedgis != 0) {
	    deletedObjectInfo.add(new NameValueDTO<Integer>(GisFeature.class
		    .getSimpleName(), deletedgis));
	}
	resetStatus();
	return deletedObjectInfo;
    }


}
