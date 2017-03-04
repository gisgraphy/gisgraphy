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

import static com.gisgraphy.domain.geoloc.entity.GisFeature.NAME_MAX_LENGTH;
import static com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICitySubdivisionDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.Constants;
import com.gisgraphy.fulltext.FullTextSearchEngine;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.util.StringUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the cities from an (pre-processed) openStreet map data file.
 * The goal of this importer is to cross information between geonames and Openstreetmap. 
 * Geonames has no concept of city but of populated place (That can be a city, suburb or other)
 * By cross the informations we can add shape and set a 'municipality' flag to identify city.
 * 
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenStreetMapCitiesSimpleImporter extends AbstractSimpleImporterProcessor {
	
	public static final int SCORE_LIMIT = 1;
	
	public final static int BATCH_UPDATE_SIZE = 100;

	protected static final Logger logger = LoggerFactory.getLogger(OpenStreetMapCitiesSimpleImporter.class);
	
    public static final Output MINIMUM_OUTPUT_STYLE = Output.withDefaultFormat().withStyle(OutputStyle.SHORT);
    
    protected IIdGenerator idGenerator;
    
    protected ICityDao cityDao;
        
    protected ICitySubdivisionDao citySubdivisionDao;
    
    protected IAdmDao admDao;
    
    protected IGisFeatureDao gisFeatureDao;
    
    protected ISolRSynchroniser solRSynchroniser;
    
    protected IFullTextSearchEngine fullTextSearchEngine;
    
    protected IMunicipalityDetector municipalityDetector;
    
    LabelGenerator generator = LabelGenerator.getInstance();
    
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear()
     */
    @Override
    protected void flushAndClear() {
    	cityDao.flushAndClear();
    }
    
    @Override
    protected void setup() {
        super.setup();
        //temporary disable logging when importing
        FullTextSearchEngine.disableLogging=true;
        logger.info("sync idgenerator");
        idGenerator.sync();
    }
    

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
	return ImporterHelper.listCountryFilesToImport(importerConfig.getOpenStreetMapCitiesDir());
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
	return 16;
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
     */
    @Override
    protected void processData(String line) throws ImporterException {
	String[] fields = line.split("\t");
	String countrycode=null;
	String name=null;
	Point location=null;
	Geometry shape=null;;
	Point adminCentreLocation=null;
	int  adminLevel =0;
	Long osmId = 0L;
	
	//
	// old Line table has the following fields :
	// --------------------------------------------------- 
	//0: N|W|R; 1 id; 2 name; 3 countrycode; 4 :postcode 
	//5:population 6:location; 7 : shape ;8: place tag; 9 : is_in;
	// 10 : alternatenames
	//
	// new Line table has the following fields :
	// --------------------------------------------------- 
	//0:	N|W|R ;1 : id;	2 :admin_centre_node_id; 3 : name;	4 : countrycode; 5 : postcode;	6 : postcode_subdivision; 7 : admin level;
	//	8 : population;	9 : location;	10 : admin_centre location 11 : shape;	12 : place tag	13 : is_in ; 14 : is_in_adm;
	//	15 : alternatenames;   

	
	checkNumberOfColumn(fields);
	
	
	// name
	if (!isEmptyField(fields, 3, false)) {
		name=fields[3].trim();
		if (name.length() > NAME_MAX_LENGTH){
			logger.warn(name + "is too long");
			name= name.substring(0, NAME_MAX_LENGTH-1);
		}
	}

	if (name==null){
		return;
	}
	
	//countrycode
	if (!isEmptyField(fields, 4, true)) {
	    countrycode=fields[4].trim().toUpperCase();
	}
	//location
	if (!isEmptyField(fields, 9, false)) {
	    try {
	    	location = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[9]);
	    } catch (RuntimeException e) {
	    	logger.warn("can not parse location for "+fields[9]+" : "+e);
	    	return;
	    }
	}
	//shape
		if(!isEmptyField(fields, 11, false)){
			try {
				shape = (Geometry) GeolocHelper.convertFromHEXEWKBToGeometry(fields[11]);
			    } catch (RuntimeException e) {
			    	logger.warn("can not parse shape for id "+fields[1]+" : "+e);
			    }
		}
	
	//admin_centre_location
		if (!isEmptyField(fields, 10, false)) {
		    try {
		    	adminCentreLocation = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[10]);
		    } catch (RuntimeException e) {
		    	logger.warn("can not parse admin centre location for "+fields[10]+" : "+e);
		    }
		}
	
	GisFeature place=null;
	if (isPoi(fields[12], fields[7])) {
		SolrResponseDto  poiToremove = getNearestByPlaceType(location, name, countrycode,Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE, shape);
		//find and delete the city or subdivision 
		if (poiToremove!=null){
			GisFeature cityToRemoveObj = null;
			if (poiToremove.getPlacetype().equalsIgnoreCase(City.class.getSimpleName())){
				cityToRemoveObj = cityDao.getByFeatureId(poiToremove.getFeature_id());
				
			}else if (poiToremove.getPlacetype().equalsIgnoreCase(CitySubdivision.class.getSimpleName())){
				 cityToRemoveObj = citySubdivisionDao.getByFeatureId(poiToremove.getFeature_id());
			}
			if (cityToRemoveObj!=null){
				logger.error("'"+name+"'/'"+fields[1]+"' is a poi we remove , "+cityToRemoveObj.getName()+","+cityToRemoveObj.getFeatureId());
				gisFeatureDao.remove(cityToRemoveObj);
			}
		}
		//recreate the poi
		place = createNewPoi(name, countrycode, location, adminCentreLocation);
	} else if (StringUtil.containsDigit(name) || isACitySubdivision(fields[12])){
		SolrResponseDto  nearestCity = getNearestByPlaceType(location, name, countrycode,Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE, shape);
		if (nearestCity != null ){
			if (nearestCity.getPlacetype().equalsIgnoreCase(CitySubdivision.class.getSimpleName())){
				place = citySubdivisionDao.getByFeatureId(nearestCity.getFeature_id());
				if (place==null){
					place = createNewCitySubdivision(name,countrycode,location,adminCentreLocation);
					
				} else{ 
					place.setSource(GISSource.GEONAMES_OSM);
					//generally osm data is better than geonames, we overide geonames values
					if (name!=null){
						if (place.getOpenstreetmapId()==null ){
							//if osmid is not null, the name has already been set by previous line,
							//and because relation are before node the relation one is probably better
							place.setName(name);
						}
					}
					place.setCountryCode(countrycode);
					if (adminCentreLocation!=null){
						place.setAdminCentreLocation(adminCentreLocation);
					}
					if (location!=null){
						place.setLocation(location);
					}
				}
				
			} else if (nearestCity.getPlacetype().equalsIgnoreCase(City.class.getSimpleName())){
				//osm consider the place as a suburb, we delete the city and create a citysubdivision
				City cityToRemove = cityDao.getByFeatureId(nearestCity.getFeature_id());
				if (cityToRemove!=null && !cityToRemove.isMunicipality()){
					logger.error("'"+name+"'/'"+fields[1]+"' is a subdivision we remove , "+nearestCity.getName()+","+nearestCity.getFeature_id());
					cityDao.remove(cityToRemove);
				}
				place = createNewCitySubdivision(name,countrycode,location,adminCentreLocation);
			}
			
			
			
			
		} else {
			logger.warn("'"+name+"'/'"+fields[1]+"' is not found");
			place = createNewCitySubdivision(name,countrycode,location,adminCentreLocation);
		}
		
	}  else {
		SolrResponseDto  nearestCity = getNearestByPlaceType(location, name, countrycode, Constants.ONLY_CITY_PLACETYPE, shape);
		if (nearestCity != null ){
			place = cityDao.getByFeatureId(nearestCity.getFeature_id());
			if (place==null){
				place = createNewCity(name,countrycode,location,adminCentreLocation);

			} else{ 
				place.setSource(GISSource.GEONAMES_OSM);
				//generally osm data is better than geonames, we overide geonames values
				if (name!=null && place.getOpenstreetmapId()==null){
					//if osmid is not null, the name has already been set by previous line,
					//and because relation are before node the relation one is probably better
					place.setName(name);
				}
				place.setCountryCode(countrycode);
				if (adminCentreLocation!=null){
					place.setAdminCentreLocation(adminCentreLocation);
				}
				if (location!=null && place.getOpenstreetmapId()==null){
						place.setLocation(location);
				}
			}
		} else {
			place = createNewCity(name,countrycode,location,adminCentreLocation);
		}
		//set municipality if needed
		if ( !((City)place).isMunicipality()){ 
			//only if not already a city, because, a node can be after a relation and then node set the municipality to false
			((City)place).setMunicipality(municipalityDetector.isMunicipality(countrycode, fields[12], fields[0], GISSource.OSM));
		}
		if ("locality".equalsIgnoreCase(fields[12])){
			((City)place).setMunicipality(false);
		}
	}
	//populate new fields
	//population
	if(!isEmptyField(fields, 8, false)){
		try {
			String populationStr = fields[8];
			int population = parsePopulation(populationStr);
			place.setPopulation(population);
		} catch (NumberFormatException e) {
			logger.error("can not parse population :"+fields[8]+" for "+fields[1]);
		}
	}
	//zip code
	if(!isEmptyField(fields, 5, false) && (place.getZipCodes()==null || !place.getZipCodes().contains(new ZipCode(fields[5])))){
			populateZip(fields[5], place);
	}
	//subdivision zip code
		if(!isEmptyField(fields, 6, false) && (place.getZipCodes()==null || !place.getZipCodes().contains(new ZipCode(fields[6])))){
				populateZip(fields[6], place);
		}
	if (place.getZipCodes()!=null && place.getZipCodes().size()>0){
		place.setZipCode(generator.getBestZip(place.getZipCodes()));
	}
	//place tag/amenity
	if(!isEmptyField(fields, 12, false)){
		place.setAmenity(fields[12]);
		
	}
	
	//set shape
	place.setShape(shape);
	
	//osmId
	if (place.getOpenstreetmapId()==null){
		//we do not override the osm ID because if it is filled, we are probably with a node and it 
		//has already been filled by a relation
		if (!isEmptyField(fields, 1, true)) {
			String osmIdAsString =fields[1].trim();

			try {
				osmId = Long.parseLong(osmIdAsString);
				place.setOpenstreetmapId(osmId);
			} catch (NumberFormatException e) {
				logger.error("can not parse openstreetmap id "+ osmIdAsString);
			}
		}
	}
	//adm level, we need it to populate adms
	if (!isEmptyField(fields, 7, true)) {
		String adminLevelStr =fields[7].trim();
		
		try {
			adminLevel = Integer.parseInt(adminLevelStr);
		} catch (NumberFormatException e) {
			logger.error("can not parse admin level "+adminLevelStr+" for "+osmId);
		}
	}
	
	//populate alternatenames
	if (!isEmptyField(fields, 15, false)) {
		String alternateNamesAsString=fields[15].trim();
		populateAlternateNames(place,alternateNamesAsString);
	}

	
	//isinadm
	if(!isEmptyField(fields, 14, false)){
		List<AdmDTO> adms = ImporterHelper.parseIsInAdm(fields[14]);
		populateAdmNames(place,adminLevel,adms);
		if (place.getAdm()==null){
			LinkAdm(place,adms);
		}
	} 
	else if(!isEmptyField(fields, 13, false)){
		if (place.getAdm()==null){
			String admname =fields[13];
			SolrResponseDto solrResponseDto= getAdm(admname,countrycode);
			if (solrResponseDto!=null){
				Adm adm = admDao.getByFeatureId(solrResponseDto.getFeature_id());
				if (adm!=null){
					place.setAdm(adm);
					populateAdmNamesFromAdm(place, adm);
				}
			}
		}
	}
	place.setAlternateLabels(generator.generateLabels(place));
	place.setLabel(generator.generateLabel(place));
	place.setFullyQualifiedName(generator.getFullyQualifiedName(place));
	//postal is not set because it is only for street
	
	try {
		savecity(place);
	} catch (ConstraintViolationException e) {
		logger.error("Can not save "+dumpFields(fields)+"(ConstraintViolationException) we continue anyway but you should consider this",e);
	}catch (Exception e) {
		logger.error("Can not save "+dumpFields(fields)+" we continue anyway but you should consider this",e);
	}

    }

	protected int parsePopulation(String populationStr) {
		int population = Integer.parseInt(populationStr.replaceAll("[\\s\\,]", ""));
		return population;
	}
    
	

	protected boolean isPoi(String placeType,String admlevel) {
		if ("locality".equalsIgnoreCase(placeType) && admlevel!=null && !"8".equals(admlevel)) {
			return true;
		}
		return false;
	}

	protected void LinkAdm(GisFeature city, List<AdmDTO> adms) {
		if (adms!=null){
			Collections.reverse(adms);
			for (AdmDTO admDTO:adms){
				if (admDTO.getAdmName()!=null){
					SolrResponseDto solrResponseDto= getAdm(admDTO.getAdmName(),city.getCountryCode());
					if (solrResponseDto!=null && solrResponseDto.getFeature_id()!=null){
						Adm adm = admDao.getByFeatureId(solrResponseDto.getFeature_id());
						if (adm != null){
							city.setAdm(adm);
							Collections.reverse(adms);
							return;
						}
					}
					
				}
			}
		}
		
		
	}

	protected boolean isACitySubdivision(String placeType) {
		if ("neighbourhood".equalsIgnoreCase(placeType)
				|| "quarter".equalsIgnoreCase(placeType)
				|| "isolated_dwelling".equalsIgnoreCase(placeType)
				|| "suburb".equalsIgnoreCase(placeType)
				|| "city_block".equalsIgnoreCase(placeType)
				|| "borough".equalsIgnoreCase(placeType)) {
			return true;
		}
		return false;
	}
	
	

	/**
     * @param fields
     *                The array to process
     * @return a string which represent a human readable string of the Array but without shape because it is useless in logs
     */
    protected static String dumpFields(String[] fields) {
	String result = "[";
	for (int i=0;i<fields.length;i++) {
		if (i==11){
			result= result+"THE_SHAPE;";
		}else {
	    result = result + fields[i] + ";";
		}
	}
	return result + "]";
    }

	protected void populateZip(String zipAsString, GisFeature city) {
			String[] zips = zipAsString.split(";|\\||,");
			for (int i = 0;i<zips.length;i++){
				if (!ImporterHelper.isUnwantedZipCode(zips[i]) 
						)
						{
					city.addZipCode(new ZipCode(zips[i]));
				}
			}
		
	}

	void savecity(GisFeature city) {
		if (city!=null){
			if (city instanceof City){
				cityDao.save((City)city);
			} else if (city instanceof CitySubdivision){
				citySubdivisionDao.save((CitySubdivision)city);
			} else {
				gisFeatureDao.save(city);
			}
		}
	}

	City createNewCity(String name,String countryCode,Point location,Point adminCentreLocation) {
		City city = new City();
		city.setFeatureId(idGenerator.getNextFeatureId());
		city.setSource(GISSource.OSM);
		city.setName(name);
		city.setLocation(location);
		city.setAdminCentreLocation(adminCentreLocation);
		city.setCountryCode(countryCode);
		return city;
	}
	
	GisFeature createNewPoi(String name,String countryCode,Point location,Point adminCentreLocation) {
		GisFeature city = new GisFeature();
		city.setFeatureId(idGenerator.getNextFeatureId());
		city.setSource(GISSource.OSM);
		city.setName(name);
		city.setLocation(location);
		city.setAdminCentreLocation(adminCentreLocation);
		city.setCountryCode(countryCode);
		return city;
	}
	

	CitySubdivision createNewCitySubdivision(String name,String countryCode,Point location,Point adminCentreLocation) {
		CitySubdivision city = new CitySubdivision();
		city.setFeatureId(idGenerator.getNextFeatureId());
		city.setSource(GISSource.OSM);
		city.setName(name);
		city.setLocation(location);
		city.setAdminCentreLocation(adminCentreLocation);
		city.setCountryCode(countryCode);
		return city;
	}
	
	GisFeature populateAlternateNames(GisFeature feature,
			String alternateNamesAsString) {
		return ImporterHelper.populateAlternateNames(feature,alternateNamesAsString);
		
	}
	
	
	


	protected SolrResponseDto getNearestByPlaceType(Point location, String name,String countryCode,Class[] placetypes, Geometry shape) {
		if (location ==null || name==null || "".equals(name.trim())){
			return null;
		}
		FulltextQuery query;
		try {
			if (placetypes==null){
				query = (FulltextQuery) new FulltextQuery(name).around(location).withoutSpellChecking().withPagination(Pagination.ONE_RESULT).withOutput(MINIMUM_OUTPUT_STYLE);
			} else {
				query = (FulltextQuery) new FulltextQuery(name).withPlaceTypes(placetypes).around(location).withoutSpellChecking().withPagination(Pagination.ONE_RESULT).withOutput(MINIMUM_OUTPUT_STYLE);
			}
			
		} catch (IllegalArgumentException e) {
			logger.error("can not create a fulltext query for "+name);
			return null;
		}
		if (countryCode != null){
			query.limitToCountryCode(countryCode);
		}
		FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
		if (results != null){
			for (SolrResponseDto solrResponseDto : results.getResults()) {
				if (solrResponseDto!=null 
						&& solrResponseDto.getOpenstreetmap_id()== null){
					//if fopenstreetmapid is not null it is because the shape has already been set
					//(R are before nodes), we ignore because we don't want the place if relation has been set
					if (solrResponseDto.getName()!=null && StringHelper.isSameName(name, solrResponseDto.getName())){
						if (shape!=null){
							//we should verify
							if (solrResponseDto.getLng()!=null && solrResponseDto.getLat()!=null){
								if (shape.contains(GeolocHelper.createPoint(solrResponseDto.getLng(),solrResponseDto.getLat()))){
									return solrResponseDto;
								}
							} else {
								logger.error("no GPS coordinate for "+solrResponseDto);
							} 
						} else {
							//if the name is the same, no need to verify shape
							return solrResponseDto;
						}
					}
				} else {
					return null;
				}
			}
		}
		return null;
	}
	
	
	protected GisFeature populateAdmNames(GisFeature gisFeature, int currentLevel, List<AdmDTO> admdtos){
		
		return ImporterHelper.populateAdmNames(gisFeature, currentLevel, admdtos);
		
	}
	
	protected GisFeature populateAdmNamesFromAdm(GisFeature gisFeature,Adm adm){
		if (gisFeature ==null || adm ==null){
			return gisFeature;
		}
		String lastName="";
		int gisLevel = 1;
		for (int admlevel=1;admlevel <=5;admlevel++){
			if (adm !=null){
			String nameToSet = adm.getAdmName(admlevel);
			if (!lastName.equalsIgnoreCase(nameToSet) ){
				//only if adm level < or not set
				gisFeature.setAdmName(gisLevel++,nameToSet );
				if (nameToSet!=null){
					lastName = nameToSet;
				}
			}
			}
		}
		
		return gisFeature;
		
	}
	
	protected SolrResponseDto getAdm(String name, String countryCode) {
		if (name==null){
			return null;
		}
		FulltextQuery query;
		try {
			query = (FulltextQuery)new FulltextQuery(name).withAllWordsRequired(false).withoutSpellChecking().
					withPlaceTypes(ONLY_ADM_PLACETYPE).withOutput(MINIMUM_OUTPUT_STYLE).withPagination(Pagination.ONE_RESULT);
		} catch (IllegalArgumentException e) {
			logger.error("can not create a fulltext query for "+name);
			return null;
		}
		if (countryCode != null){
			query.limitToCountryCode(countryCode);
		}
		FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
		if (results != null){
			for (SolrResponseDto solrResponseDto : results.getResults()) {
				return solrResponseDto;
			}
		}
		return null;
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
    	logger.info("reseting openstreetmap cities...");
    	//TODO only cities that have source openstreetmap
    	    deletedObjectInfo
    		    .add(new NameValueDTO<Integer>(City.class.getSimpleName(), 0));
    	resetStatus();
    	return deletedObjectInfo;
    }
    
    @Override
    //TODO test
    protected void tearDown() {
    	super.tearDown();
    	String savedMessage = this.statusMessage;
    	/*try {
    		this.statusMessage = internationalisationService.getString("import.updatecitysubdivision");
			int nbModify = citySubdivisionDao.linkCitySubdivisionToTheirCity();
			logger.warn(nbModify +" citySubdivision has been modify");
		} catch (Exception e){
			logger.error("error during link city subdivision to their city",e);
		}finally {
			 // we restore message in case of error
    	    this.statusMessage = savedMessage;
		}*/
    	try {
    		this.statusMessage = internationalisationService.getString("import.fixpolygon");
			logger.info("fixing polygons for city");
			int nbModify = cityDao.fixPolygons();
			logger.warn(nbModify +" polygons has been fixed");
		} catch (Exception e){
			logger.error("error durin fixing polygons",e);
		}
    	finally {
    	    this.statusMessage = savedMessage;
		}
    	FullTextSearchEngine.disableLogging=false;
    	try {
    		this.statusMessage = internationalisationService.getString("import.fulltext.optimize");
    		solRSynchroniser.optimize();
    		logger.warn("fulltext engine has been optimized");
    	}  catch (Exception e){
			logger.error("error durin fulltext optimization",e);
		}finally {
    	    // we restore message in case of error
    	    this.statusMessage = savedMessage;
    	}
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
    public void setCityDao(ICityDao cityDao) {
		this.cityDao = cityDao;
	}
    
    @Required
    public void setGisFeatureDao(IGisFeatureDao gisFeatureDao) {
		this.gisFeatureDao = gisFeatureDao;
	}
    

    @Required
	public void setFullTextSearchEngine(IFullTextSearchEngine fullTextSearchEngine) {
		this.fullTextSearchEngine = fullTextSearchEngine;
	}

    @Required
	public void setAdmDao(IAdmDao admDao) {
		this.admDao = admDao;
	}
    

    @Required
    public void setMunicipalityDetector(IMunicipalityDetector municipalityDetector) {
		this.municipalityDetector = municipalityDetector;
	}

    @Required
	public void setCitySubdivisionDao(ICitySubdivisionDao citySubdivisionDao) {
		this.citySubdivisionDao = citySubdivisionDao;
	}

    
}
