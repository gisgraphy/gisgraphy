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
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
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
import com.gisgraphy.domain.valueobject.AlternateNameSource;
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
public class OpenStreetMapSimpleImporter extends AbstractSimpleImporterProcessor {
	
	protected static final Logger logger = LoggerFactory.getLogger(OpenStreetMapSimpleImporter.class);
	
    public static final int DISTANCE = 40000;
    
    BasicAddressFormater formater = BasicAddressFormater.getInstance();
    
    LabelGenerator labelGenerator = LabelGenerator.getInstance();

	@Autowired
    protected IIdGenerator idGenerator;
    
    @Autowired
    protected IOpenStreetMapDao openStreetMapDao;
    
    @Autowired
    protected ISolRSynchroniser solRSynchroniser;
    
    @Autowired
    protected IMunicipalityDetector municipalityDetector;
    
    @Autowired
    protected ICityDao cityDao;
    
    @Autowired
    protected ICitySubdivisionDao citySubdivisionDao;
    
    private static final Pattern pattern = Pattern.compile("(\\w+)\\s\\d+.*",Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    

	public static final Float SUBURB_MAX_DISTANCE = 5000f;
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear()
     */
    @Override
    protected void flushAndClear() {
	openStreetMapDao.flushAndClear();

    }
    
    @Override
    protected void setup() {
        super.setup();
        //temporary disable logging when importing
        FullTextSearchEngine.disableLogging=true;
        GeolocSearchEngine.disableLogging=true;
        logger.info("reseting Openstreetmap generatedId");
        idGenerator.sync();
    }
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
    	return ImporterHelper.listCountryFilesToImport(importerConfig.getOpenStreetMapDir());
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
    	return 10;
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
     */
    @Override
    protected void processData(String line) throws ImporterException {
	String[] fields = line.split("\t");

	//
	// old Line table has the following fields :
	// --------------------------------------------------- 
	//0: id; 1 name; 2 location; 3 length ;4 countrycode; 5 : gid ;
	//6 type; 7 oneway; 8 : shape; 9 : Alternate names
	//
	// new table has the following fields :
	// --------------------------------------------------- 
	//0: id; 	1: name;	2: location; 	3: length ;	4: countrycode; 	5 : is_in; 	6: postcode; 	7: is_in_adm;
	//	8: type;	9: oneway;10: shape;  11: max_speed;	12: lanes; 	13: toll; 	14: surface; 15 azimuth start ; 16 azimut end; 17: alternatenames; 
	//
	checkNumberOfColumn(fields);
	OpenStreetMap street = new OpenStreetMap();
	
	// set id
	if (!isEmptyField(fields, 0, false)) {
	    Long openstreetmapId= null;
	    try {
		openstreetmapId = new Long(fields[0].trim());
	    } catch (NumberFormatException e) {
		logger.warn("can not get openstreetmap id for "+fields[0]);
	    }
	    street.setOpenstreetmapId(openstreetmapId);
	}
	
	// set name
	if (!isEmptyField(fields, 1, false)) {
	    street.setName(fields[1].trim());
	    StringHelper.updateOpenStreetMapEntityForIndexation(street);
	}
	
	//location
	if (!isEmptyField(fields, 2, false)) {
	    try {
		Point location = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[2]);
		street.setLocation(location);
	    } catch (RuntimeException e) {
	    	logger.warn("can not parse location for "+fields[1]+" : "+e);
	    	return;
	    }
	}
	
	if (!isEmptyField(fields, 3, false)) {
	    Double length;
		try {
			length = new Double(fields[3].trim());
			street.setLength(length);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	if (!isEmptyField(fields, 4, false)) {
	    street.setCountryCode(fields[4].trim());
	}
	
	//5 is_in see behind
	
	//6 zip
	if (!isEmptyField(fields, 6, false)) {
		 populateZip(fields[6].trim(),street);
	}
	
	//7 is_in_adm for future use
	//bypass
	
	//8 streettype
	if (!isEmptyField(fields, 8, false)) {
	    StreetType type;
	    try {
		type = StreetType.valueOf(fields[8].toUpperCase());
		street.setStreetType(type);
	    } catch (Exception e) {
		logger.warn("can not determine streetType for "+fields[0]+"/"+fields[8]+" : "+e);
		street.setStreetType(StreetType.UNCLASSIFIED);
	    }
	    
	}
	
	//9 one way
	if (!isEmptyField(fields, 9, false)) {
	    boolean oneWay = false;
	    try {
		oneWay  = Boolean.valueOf(fields[9]);
		street.setOneWay(oneWay);
	    } catch (Exception e) {
		logger.warn("can not determine oneway for "+fields[1]+"/"+fields[9]+" : "+e);
	    }
	    
	}
	//10 shape
	if (!isEmptyField(fields, 10, true)) {
	    try {
	    	street.setShape((LineString)GeolocHelper.convertFromHEXEWKBToGeometry(fields[10]));
	    } catch (RuntimeException e) {
		logger.warn("can not parse shape for "+fields[0]+"/"+fields[10] +" : "+e);
		return;
	    }
	}
	
	//11 max speed
	if (!isEmptyField(fields,11 , false)) {
		PopulateMaxSpeed(street,fields[11]);  
	}
	
	//12 lanes
	if (!isEmptyField(fields,12 , false)) {
		try {
			Integer lanes = Integer.parseInt(fields[12]);
			street.setLanes(lanes);
		} catch (NumberFormatException e) {
			logger.warn("can not parse lanes for "+fields[0]+"/"+fields[12] +" : "+e);
		}
  
	}
	
	//13 toll
	if (!isEmptyField(fields, 13, false)) {
	    	if (fields[13].equalsIgnoreCase("yes")){
	    		street.setToll(true);
	    	}
	}
	
	
	//14 surface
	if (!isEmptyField(fields, 14, false)) {
		street.setSurface(fields[14].trim());
	}
	
	//5 is_in	
	if (!isEmptyField(fields, 5, false)) {
		street.setIsIn(fields[5].trim());
	}
	if (shouldFillIsInField()) {
		//we try to process is_in fields, because we want to fill adm and zip too
		setIsInFields(street);
	}
	
	
	long generatedId= idGenerator.getNextGId();
	street.setGid(new Long(generatedId));

	
	//azimuth *2
	if (!isEmptyField(fields, 15, false)){
		street.setAzimuthStart(parseAzimuth(fields[15]));
	}
	if (!isEmptyField(fields, 16, false)){
		street.setAzimuthEnd(parseAzimuth(fields[16]));
	}
	//alternate names
	if (fields.length == 18 && !isEmptyField(fields, 17, false)){
		populateAlternateNames(street,fields[17]);
	}
	
	//labels
	street.setAlternateLabels(labelGenerator.generateLabels(street));
	street.setLabel(labelGenerator.generateLabel(street));
	street.setFullyQualifiedName(labelGenerator.getFullyQualifiedName(street, false));
	street.setLabelPostal(labelGenerator.generatePostal(street));
	
	
		
	try {
		openStreetMapDao.save(street);
	} catch (ConstraintViolationException e) {
		logger.error("Can not save "+dumpFields(fields)+"(ConstraintViolationException) we continue anyway but you should consider this",e);
	}catch (Exception e) {
		logger.error("Can not save "+dumpFields(fields)+" we continue anyway but you should consider this",e);
	}

    }
    
    protected void populateZip(String zipAsString, OpenStreetMap osm) {
  		String[] zips = zipAsString.split(";|\\||,");
  		for (int i = 0;i<zips.length;i++){
  				osm.addIsInZip(zips[i]);
  		}
  		if (osm.getIsInZip()!=null && osm.getIsInZip().size() >0){
  			osm.setZipCode(labelGenerator.getBestZipString(osm.getIsInZip()));
  		}
  	
  }

	private void setBestZip(OpenStreetMap street) {
		//we set the zipcode as the best one (when necessary)
		if (street.getIsInZip()!=null && street.getIsInZip().size() >0 && street.getZipCode()==null){
			street.setZipCode(labelGenerator.getBestZipString(street.getIsInZip()));
		}
		
	}

	protected Integer parseAzimuth(String azimutStr) {
		if (azimutStr==null){
			return null;
		}
		Float azimuth = null;
		try {
			azimuth = Float.parseFloat(azimutStr);
			if (azimuth == null || azimuth.intValue()<0 || azimuth >360){
				return null;
			}
			return azimuth.intValue();
			
		} catch (NumberFormatException e) {
			logger.warn("can not parse azimuth "+azimutStr +" : "+e);
			return null;
		}
	}
    
    protected void PopulateMaxSpeed(OpenStreetMap street, String string) {
		if (string!=null && string.trim()!=""){
			String[] fields= string.split("___");
			String trimField= "";
			if (fields.length>=1){
				 trimField = fields[0].trim();
				if (!"".equals(trimField) && StringUtil.containsDigit(trimField)){
					street.setMaxSpeed(trimField);
					street.setSpeedMode(SpeedMode.OSM);
				}
			}
			if (fields.length>=2){
				trimField = fields[1].trim();
				if (!"".equals(trimField)  && StringUtil.containsDigit(trimField)){
					street.setMaxSpeedBackward(trimField);
					street.setSpeedMode(SpeedMode.OSM);
				}
			}
			if (fields.length==3){
				trimField = fields[2].trim();
				if (!"".equals(trimField) && street.getMaxSpeed()==null && StringUtil.containsDigit(trimField)){
					street.setMaxSpeed(trimField);
					street.setSpeedMode(SpeedMode.OSM);
				}
			}
		}
			
		}
		

	OpenStreetMap populateAlternateNames(OpenStreetMap street,
			String alternateNamesAsString) {
		return ImporterHelper.populateAlternateNames(street, alternateNamesAsString);
	}
    
  

    protected void setIsInFields(OpenStreetMap street) {
    	if (street != null && street.getLocation() != null) {
    		//first search By Shape because it is the more reliable :
    		City cityByShape = cityDao.getByShape(street.getLocation(),street.getCountryCode(),true);
    		if (cityByShape != null){
    			street.setIsIn(cityByShape.getName());
    			street.setCityId(cityByShape.getId());
    			street.setCityConfident(true);
    			street.setPopulation(cityByShape.getPopulation());
    			if (street.getZipCode()== null && cityByShape.getZipCodes() != null) {//only if the zipcode is not previously set with the value from CSV
    				for (ZipCode zip:cityByShape.getZipCodes()){
    					street.addIsInZip(zip.getCode());
    				}
    			}
    			if (cityByShape.getAlternateNames()!=null){
    				for (AlternateName name : cityByShape.getAlternateNames() ){
    					if (name!=null && name.getName()!=null){
    						street.addIsInCitiesAlternateName(name.getName());
    					}
    				}
    			}
    			//we add the name of the city as well as the alternatename, so we can search in one field (only is_in_city)
    			if (cityByShape.getName()!=null & !"".equals(cityByShape.getName().trim())){
    				street.addIsInCitiesAlternateName(cityByShape.getName());
    			}
    				setAdmNames(street, cityByShape);
    				//AFTER setting admnames, we took the best one
    				street.setIsInAdm(getBestAdmName(cityByShape));//cityByShape.getAdm().getName()
    				//set the is_in_place
    				CitySubdivision subdivision = citySubdivisionDao.getByShape(street.getLocation(),cityByShape.getCountryCode());
    				if (subdivision !=null){
    					street.setIsInPlace(subdivision.getName());
    				}
    				setBestZip(street);
    				return;
    		}
    		//
    		City city = getNearestCity(street.getLocation(),street.getCountryCode(), true);
    		if (city != null) {
    			street.setPopulation(city.getPopulation());
    			setAdmNames(street, city);
    			//AFTER setting admnames, we took the best one
    			if (street.getIsInAdm()==null){
					street.setIsInAdm(getBestAdmName(city));
				}
    			if (street.getZipCode()== null && city.getZipCodes() != null) {//only if the zipcode is not previously set with the value from CSV
    				for (ZipCode zip:city.getZipCodes()){
    					if (zip != null && zip.getCode()!=null){
    						street.addIsInZip(zip.getCode());
    					}
    				}
    			}
    			if (city.getName() != null && street.getIsIn()==null) {//only if it has not be set by the openstreetmap is_in field
    				//we can here have some concordance problem if the city found is not the one populate in the osm is_in fields.
    				street.setIsIn(pplxToPPL(city.getName()));
    				street.setCityId(city.getId());
    			}
    			if (city.getAlternateNames()!=null){
    				for (AlternateName name : city.getAlternateNames() ){
    					if (name!=null && name.getName()!=null){
    						street.addIsInCitiesAlternateName(name.getName());
    					}
    				}
    			}
    		}
    		//
    		City city2 = getNearestCity(street.getLocation(),street.getCountryCode(), false);
    		if (city2 != null) {
    			if (city != null){
    					if (city.getFeatureId() == city2.getFeatureId()) {
    						setBestZip(street);
    						return;
    					}
    					if (city2.getLocation()!=null && city.getLocation()!=null && GeolocHelper.distance(street.getLocation(),city2.getLocation())>GeolocHelper.distance(street.getLocation(),city.getLocation())){
    						setBestZip(street);
    						return;
    					}
    			}
    				//we got a non municipality that is nearest, we set isinPlace tag and update is_in if needed
    				if (city2.getPopulation() != null && city2.getPopulation() != 0 && (street.getPopulation() == null || street.getPopulation() == 0)) {
    					street.setPopulation(city2.getPopulation());
    				}

    				if (street.getIsIn() == null) {
    					street.setIsIn(pplxToPPL(city2.getName()));
    					street.setCityId(city2.getId());
    				} else {
    					street.setIsInPlace(pplxToPPL(city2.getName()));
    				}
    				setAdmNames(street, city2);
    				//AFTER setting admnames, we took the best one
    				if (street.getIsInAdm()==null){
    					street.setIsInAdm(getBestAdmName(city2));
    				}
    				if (street.getZipCode()== null && city2.getZipCodes() != null ) {//we merge the zipcodes for is_in and is_in_place, so we don't check
    					//only if the zipcode is not previously set with the value from CSV
    					//if zipcodes are already filled
    					for (ZipCode zip:city2.getZipCodes()){
    						if (zip!=null && zip.getCode()!=null){
    							street.addIsInZip(zip.getCode());
    						}
        				}
    					/*if (street.getIsInZip()!=null && street.getIsInZip().size() >0){
        					street.setZipCode(labelGenerator.getBestZipString(street.getIsInZip()));
        				}*/
    				}
    				if (city==null && city2!=null){//add AN only if there are not added yet
	        			if (city2.getAlternateNames()!=null){
	        				for (AlternateName name : city2.getAlternateNames() ){
	        					if (name!=null && name.getName()!=null){
	        						street.addIsInCitiesAlternateName(name.getName());
	        					}
	        				}
	        			}
    				}
    		}
    		setBestZip(street);
    	}
    }
    

	protected void setAdmNames(OpenStreetMap street,City city) {
		if (city != null && street !=null) {
			//we only set admnames if it is not already filled
			if (city.getAdm5Name() != null && street.getAdm5Name()==null) {
				street.setAdm5Name(city.getAdm5Name());
			}
			if (city.getAdm4Name() != null && street.getAdm4Name()==null) {
				street.setAdm4Name(city.getAdm4Name());
			} 
			if (city.getAdm3Name() != null && street.getAdm3Name()==null) {
				street.setAdm3Name(city.getAdm3Name());
			} 
			if (city.getAdm2Name() != null && street.getAdm2Name()==null) {
				street.setAdm2Name(city.getAdm2Name());
			} 
			if (city.getAdm1Name() != null && street.getAdm1Name()==null) {
				street.setAdm1Name(city.getAdm1Name());
			}
		}
	}

	protected String getBestAdmName(GisFeature gisFeature) {
		if (gisFeature != null) {
			if (gisFeature.getCountryCode()!= null  && formater.getAdmLevelByContryCode(gisFeature.getCountryCode())!=0){
				int level = formater.getAdmLevelByContryCode(gisFeature.getCountryCode());
				if (level == 1) {
					return gisFeature.getAdm1Name();
				} else if (level == 2) {
					if (gisFeature.getAdm2Name()!=null){
					return gisFeature.getAdm2Name();
					} else {
						return gisFeature.getAdm1Name();
					}
				} else if (level == 3) {
					if (gisFeature.getAdm3Name()!=null){
						return gisFeature.getAdm3Name();
						} else {
							return gisFeature.getAdm1Name();
						}
				} else if (level == 4) {
					if (gisFeature.getAdm4Name()!=null){
						return gisFeature.getAdm4Name();
						} else {
							return gisFeature.getAdm1Name();
						}
				}else if (level == 5) {
					if (gisFeature.getAdm5Name()!=null){
						return gisFeature.getAdm5Name();
						} else {
							return gisFeature.getAdm1Name();
						}
				} else {
					return null;
				}
			}
			if (gisFeature.getAdm1Name() != null) {
				return gisFeature.getAdm1Name();
			} else if (gisFeature.getAdm2Name() != null) {
				return gisFeature.getAdm2Name();
			} else if (gisFeature.getAdm3Name() != null) {
				return gisFeature.getAdm3Name();
			} else if (gisFeature.getAdm4Name() != null) {
				return gisFeature.getAdm4Name();
			}else if (gisFeature.getAdm5Name() != null) {
				return gisFeature.getAdm5Name();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
    
   
	protected City getNearestCity(Point location, String countryCode, boolean filterMunicipality) {
		if (location ==null){
			return null;
		}
		return cityDao.getNearest(location, countryCode, filterMunicipality, DISTANCE);
	}
    
    /**
     *  tests if city is a paris district, if so it is
		probably a pplx that is newly considered as ppl
		http://forum.geonames.org/gforum/posts/list/2063.page
     */
    protected String pplxToPPL(String cityName){
    	if (cityName!=null){
    		Matcher matcher = pattern.matcher(cityName);
    		if (matcher.find()) {
    			return matcher.group(1);
    		} else {
    			return cityName;
    		}
    	} else {
    		return cityName;
    	}
    }

	/* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped()
     */
    @Override
    public boolean shouldBeSkipped() {
	return !importerConfig.isOpenstreetmapImporterEnabled();
    }
    
   


    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#setCommitFlushMode()
     */
    @Override
    protected void setCommitFlushMode() {
    	this.openStreetMapDao.setFlushMode(FlushMode.COMMIT);
    	this.cityDao.setFlushMode(FlushMode.COMMIT);
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
	return false;
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
    	List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
    	logger.info("deleting openstreetmap entities...");
    	int deleted = openStreetMapDao.deleteAll();
    	if (deleted != 0) {
    	    deletedObjectInfo
    		    .add(new NameValueDTO<Integer>(openStreetMapDao.getPersistenceClass().getSimpleName(), deleted));
    	}
    	logger.info(deleted + " openstreetmap entities have been deleted");
    	resetStatus();
    	return deletedObjectInfo;
    }
    
    
   
    
    @Override
    //TODO test
    protected void tearDown() {
    	super.tearDown();
    	FullTextSearchEngine.disableLogging=false;
    	GeolocSearchEngine.disableLogging=false;
    	String savedMessage = this.statusMessage;
    	try {
    		this.statusMessage = internationalisationService.getString("import.message.createIndex");
    		openStreetMapDao.createSpatialIndexes();
    		this.statusMessage = internationalisationService.getString("import.fulltext.optimize");
    		solRSynchroniser.optimize();
    	} catch (Exception e) {
    		logger.error("an error occured during spatial index creation, we ignore it but you have to manually run it to have good performances : "+e.getMessage(),e);
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
	if (fields.length != 18 && fields.length != 17) {

	    throw new WrongNumberOfFieldsException(
		    "The number of fields is not correct. expected : "
			    + getNumberOfColumns() + ", founds :  "
			    + fields.length+ ". details :"+dumpFields(fields));
	}
    }
    
    protected boolean shouldFillIsInField(){
    	return importerConfig.isGeonamesImporterEnabled() && importerConfig.isOpenStreetMapFillIsIn(); 
    }
    
    @Required
    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }
   

    @Required
    public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
        this.solRSynchroniser = solRSynchroniser;
    }

    @Required
    public void setIdGenerator(IIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }


    @Required
    public void setMunicipalityDetector(IMunicipalityDetector municipalityDetector) {
		this.municipalityDetector = municipalityDetector;
	}

    @Required
	public void setCityDao(ICityDao cityDao) {
		this.cityDao = cityDao;
	}

    @Required
	public void setCitySubdivisionDao(ICitySubdivisionDao citySubdivisionDao) {
		this.citySubdivisionDao = citySubdivisionDao;
	}
    
    
}
