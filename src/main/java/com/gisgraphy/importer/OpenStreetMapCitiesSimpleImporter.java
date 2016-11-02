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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.FlushMode;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICitySubdivisionDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
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
    
    public static final String ISINADM_EXTRACTION_REGEXP = "((?:(?!___).)+)(?=(?:___|$))(?:___|$)"
    		+ "((?:(?!___)\\d)*)(?=(?:___|$))(?:___|$)"
    		+ "(\\d+)(?:___|$)?";    
    public static final Pattern ISINADM_EXTRACTION_PATTERN = Pattern.compile(ISINADM_EXTRACTION_REGEXP);
    
    public static final String UNWANTED_ZIPCODE_REGEXP = ".*(CEDEX).*";
    public static final Pattern UNWANTED_ZIPCODE_PATTERN = Pattern.compile(UNWANTED_ZIPCODE_REGEXP,Pattern.CASE_INSENSITIVE);

	protected IIdGenerator idGenerator;
    
    protected ICityDao cityDao;
    
    
    protected ICitySubdivisionDao citySubdivisionDao;
    
    protected IAdmDao admDao;
    
    protected ISolRSynchroniser solRSynchroniser;
    
    protected IFullTextSearchEngine fullTextSearchEngine;
    
    protected IMunicipalityDetector municipalityDetector;
    
    

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
        logger.info("reseting Openstreetmap generatedId");
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
	Point adminCentrelocation=null;
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
	
	//admin_centre_location
		if (!isEmptyField(fields, 10, false)) {
		    try {
		    	adminCentrelocation = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[10]);
		    } catch (RuntimeException e) {
		    	logger.warn("can not parse admin centre location for "+fields[10]+" : "+e);
		    }
		}
	
	GisFeature city=null;
	if (StringUtil.containsDigit(name) || isACitySubdivision(fields[12])){
		SolrResponseDto  nearestCity = getNearestCity(location, name, countrycode,Constants.ONLY_CITYSUBDIVISION_PLACETYPE);
		if (nearestCity != null ){
			city = citySubdivisionDao.getByFeatureId(nearestCity.getFeature_id());
			if (city==null){
				city = createNewCitySubdivision(name,countrycode,location,adminCentrelocation);

			} else{ 
				city.setSource(GISSource.GEONAMES_OSM);
				//generally osm data is better than geonames, we overide geonames values
				city.setName(name);
				city.setCountryCode(countrycode);
				city.setAdminCentreLocation(adminCentrelocation);
				city.setLocation(location);
			}
		} else {
			logger.warn("'"+name+"'/'"+fields[1]+"' is not found");
			city = createNewCitySubdivision(name,countrycode,location,adminCentrelocation);
		}
		
	} else {
		SolrResponseDto  nearestCity = getNearestCity(location, name, countrycode, Constants.ONLY_CITY_PLACETYPE);
		if (nearestCity != null ){
			city = cityDao.getByFeatureId(nearestCity.getFeature_id());
			if (city==null){
				city = createNewCity(name,countrycode,location,adminCentrelocation);

			} else{ 
				city.setSource(GISSource.GEONAMES_OSM);
				city.setSource(GISSource.GEONAMES_OSM);
				//generally osm data is better than geonames, we overide geonames values
				city.setName(name);
				city.setCountryCode(countrycode);
				city.setAdminCentreLocation(adminCentrelocation);
				city.setLocation(location);
			}
		} else {
			city = createNewCity(name,countrycode,location,adminCentrelocation);
		}
		//set municipality if needed
		if ( !((City)city).isMunicipality()){ 
			//only if not already a city, because, a node can be after a relation and then node set the municipality to false
			((City)city).setMunicipality(municipalityDetector.isMunicipality(countrycode, fields[12], fields[0], GISSource.OSM));
		}
	}
	//populate new fields
	//population
	if(city.getPopulation()==null && !isEmptyField(fields, 8, false)){
		try {
			int population = Integer.parseInt(fields[8].replaceAll("\\s+", ""));
			city.setPopulation(population);
		} catch (NumberFormatException e) {
			logger.error("can not parse population :"+fields[8]);
		}
	}
	//zip code
	if(!isEmptyField(fields, 5, false) && (city.getZipCodes()==null || !city.getZipCodes().contains(new ZipCode(fields[5])))){
			populateZip(fields[5], city);
	}
	//subdivision zip code
		if(!isEmptyField(fields, 6, false) && (city.getZipCodes()==null || !city.getZipCodes().contains(new ZipCode(fields[6])))){
				populateZip(fields[6], city);
		}
	//place tag/amenity
	if(!isEmptyField(fields, 12, false)){
		city.setAmenity(fields[12]);
}
	//shape
	if(!isEmptyField(fields, 11, false)){
		try {
			Geometry shape = (Geometry) GeolocHelper.convertFromHEXEWKBToGeometry(fields[11]);
			city.setShape(shape);
		    } catch (RuntimeException e) {
		    	logger.warn("can not parse shape for id "+fields[1]+" : "+e);
		    }
	}
	//osmId
	if (!isEmptyField(fields, 1, true)) {
		String osmIdAsString =fields[1].trim();
		
		try {
			osmId = Long.parseLong(osmIdAsString);
			city.setOpenstreetmapId(osmId);
		} catch (NumberFormatException e) {
			logger.error("can not parse openstreetmap id "+ osmId);
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
		populateAlternateNames(city,alternateNamesAsString);
	}

	
	//isinadm
	if(!isEmptyField(fields, 14, false)){
		List<AdmDTO> adms = parseIsInAdm(fields[14]);
		populateAdmNames(city,adminLevel,adms);
		if (city.getAdm()==null){
			LinkAdm(city,adms);
		}
	} 
	else if(!isEmptyField(fields, 13, false)){
		if (city.getAdm()==null){
			String admname =fields[13];
			SolrResponseDto solrResponseDto= getAdm(admname,countrycode);
			if (solrResponseDto!=null){
				Adm adm = admDao.getByFeatureId(solrResponseDto.getFeature_id());
				if (adm!=null){
					populateAdmNamesFromAdm(city, adm);
				}
			}
		}
	}
	try {
		savecity(city);
	} catch (ConstraintViolationException e) {
		logger.error("Can not save "+dumpFields(fields)+"(ConstraintViolationException) we continue anyway but you should consider this",e);
	}catch (Exception e) {
		logger.error("Can not save "+dumpFields(fields)+" we continue anyway but you should consider this",e);
	}

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
	
	protected boolean isUnwantedZipCode(String zipcode){
		if (zipcode == null || "".equals(zipcode.trim()) || UNWANTED_ZIPCODE_PATTERN.matcher(zipcode).matches()){
			return true ; 
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
		if (i==7){
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
				if (!isUnwantedZipCode(zips[i])){
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
	
	
	


	protected SolrResponseDto getNearestCity(Point location, String name,String countryCode,Class[] placetypes) {
		if (location ==null || name==null || "".equals(name.trim())){
			return null;
		}
		FulltextQuery query;
		try {
			query = (FulltextQuery) new FulltextQuery(name).withPlaceTypes(placetypes).around(location).withoutSpellChecking().withPagination(Pagination.ONE_RESULT).withOutput(MINIMUM_OUTPUT_STYLE);
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
				if (solrResponseDto!=null && solrResponseDto.getScore() >= SCORE_LIMIT 
						&& solrResponseDto.getOpenstreetmap_id()== null){
					//if fopenstreetmapid is not null it is because the shape has already been set 
					//(R are before nodes)
					return solrResponseDto;
				} else {
					return null;
				}
			}
		}
		return null;
	}
	
	protected List<AdmDTO> parseIsInAdm(String isInAdm){
		List<AdmDTO> adms = new ArrayList<AdmDTO>();
		if (isInAdm ==null ){
			return adms;
		}
		Matcher matcher = ISINADM_EXTRACTION_PATTERN.matcher(isInAdm);
		int i = 0;
		while (matcher.find()){
			if (matcher.groupCount() != 3) {
				logger.warn("wrong number of fields for isInAdm no " + i + "for line " + isInAdm);
				continue;
			}
			String alternateName = matcher.group(1);
			int level;
			try {
				level = Integer.valueOf(matcher.group(2));
			} catch (NumberFormatException e) {
				logger.warn("wrong adm level for isInAdm no " + i + "for line " + isInAdm);
				continue;
			}
			int openstreetmapId=0;
			try {
				openstreetmapId = Integer.valueOf(matcher.group(3));
			} catch (NumberFormatException e) {
				logger.warn("wrong openstreetmapId for isInAdm no " + i + "for line " + isInAdm);
			}
			adms.add(new AdmDTO(alternateName, level, openstreetmapId));
		}
		Collections.sort(adms);
		return adms;
		
	}
	protected GisFeature populateAdmNames(GisFeature gisFeature, int currentLevel, List<AdmDTO> admdtos){
		if (gisFeature ==null || admdtos ==null || admdtos.size() == 0){
			return gisFeature;
		}
		int level = 1;
		String lastName="";
		for (AdmDTO dto: admdtos){
			if ((dto.getLevel() < currentLevel || currentLevel == 0) && !lastName.equalsIgnoreCase(dto.getAdmName()) ){
				//only if adm level < or not set
				gisFeature.setAdmName(level++,dto.getAdmName() );
				lastName = dto.getAdmName();
			}
		}
		return gisFeature;
		
	}
	
	protected GisFeature populateAdmNamesFromAdm(GisFeature gisFeature,Adm adm){
		if (gisFeature ==null || adm ==null){
			return gisFeature;
		}
		String lastName="";
		int gisLevel = 1;
		for (int admlevel=1;admlevel <=5;admlevel++){
			String nameToSet = adm.getAdmName(admlevel);
			if (!lastName.equalsIgnoreCase(nameToSet) ){
				//only if adm level < or not set
				gisFeature.setAdmName(gisLevel++,nameToSet );
				lastName = nameToSet;
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
    	try {
    		this.statusMessage = internationalisationService.getString("import.updatecitysubdivision");
			int nbModify = citySubdivisionDao.linkCitySubdivisionToTheirCity();
			logger.warn(nbModify +" citySubdivision has been modify");
		} catch (Exception e){
			logger.error("error during link city subdivision to their city",e);
		}finally {
			 // we restore message in case of error
    	    this.statusMessage = savedMessage;
		}
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
