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

import org.apache.commons.lang.WordUtils;
import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.repository.IhouseNumberDao;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.FullTextSearchEngine;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.helper.DistancePointDto;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.LevenshteinAlgorithm;
import com.gisgraphy.helper.OrthogonalProjection;
import com.gisgraphy.helper.StringHelper;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the street from an (pre-processed) openaddresses.io data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenAddressesSimpleImporter extends AbstractSimpleImporterProcessor {


	
	private static final int ACCEPTABLE_DISTANCE_HOUSE_TO_STREET = 250;

	public static final long DEFAULT_SEARCH_DISTANCE = 1000L;
	
	public static final long SHORT_SEARCH_DISTANCE = 250L;
	
	//the fulltext has to be greater than the db one since the fulltext use boundingbox nd midle point (db use cross and can be lower)
	public static final long DEFAULT_FULLTEXT_SEARCH_DISTANCE = 5000L;

	protected static final Logger logger = LoggerFactory.getLogger(OpenAddressesSimpleImporter.class);

	protected IOpenStreetMapDao openStreetMapDao;

	protected IhouseNumberDao houseNumberDao;

	protected ISolRSynchroniser solRSynchroniser;

	protected IFullTextSearchEngine fullTextSearchEngine;
	
	//protected OpenStreetMapAdvancedImporter openStreetMapImporter;
	
	private OrthogonalProjection orthogonalProjection = new OrthogonalProjection();
	 
    BasicAddressFormater formater = BasicAddressFormater.getInstance();
    
    LabelGenerator labelGenerator = LabelGenerator.getInstance();
	
	@Autowired
    protected IIdGenerator idGenerator;
	
	protected final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);
	

	private static final Pattern ALL_ZERO = Pattern.compile("^0+$");
	
	private static final Pattern NOT_VALID_LABEL = Pattern.compile("\\b(NULL|UNDEFINED|UNAVAILABLE)\\b",Pattern.CASE_INSENSITIVE);

	public static final int MAX_NAME_SIZE = 250;
	
	LevenshteinAlgorithm leven = new LevenshteinAlgorithm();
	
	protected boolean isZeroHouseNumber(String houseNumber){
		if (houseNumber!=null){
			return ALL_ZERO.matcher(houseNumber).matches();
		}
		return false;
	}
	
	protected boolean isUnWantedHouseNumber(String houseNumber){
		if (houseNumber!=null){
			return isUnWantedStreetName(houseNumber) || isZeroHouseNumber(houseNumber) || houseNumber.startsWith("-") || !houseNumber.matches("[0-9]+");
		}
		return true;
		
	}
	protected boolean isUnWantedStreetName(String streetname){
		if (streetname!=null){
			return NOT_VALID_LABEL.matcher(streetname).find() || streetname.trim().equals("");
		}
		return true;
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear
	 * ()
	 */
	@Override
	protected void flushAndClear() {
	}

	@Override
	protected void setup() {
		//temporary disable logging when importing
		FullTextSearchEngine.disableLogging=true;
		idGenerator.sync();
		super.setup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
	 */
	  @Override
	    protected File[] getFiles() {
	    	return ImporterHelper.listCountryFilesToImport(importerConfig.getOpenAddressesDir());
	    }


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
	 * getNumberOfColumns()
	 */
	@Override
	protected int getNumberOfColumns() {
		return 11;
	}

	private String lastStreetName=null;
	private OpenStreetMap lastStreet=null;
	
	private int null_name_street_counter=0;
	private OpenStreetMap null_name_street=null;
	
	
	
	private Pattern COUNTRY_EXTRACTION_PATTERN=Pattern.compile("(..):");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData
	 * (java.lang.String)
	 */
	@Override
	protected void processData(String line) throws ImporterException {
		if (line==null || "".equals(line.trim())){
			return;
		}
		//logger.error("process line "+line);
		//0:LON 1:LAT 2: NUMBER 3:STREET 4:UNIT 5:CITY 6:DISTRICT 7:REGION 8:POSTCODE 9:ID 10:HASH
		String[] fields = line.split(",");
		if (fields.length != getNumberOfColumns()) {
			logger.error("wrong number of column ("+fields.length+") for "+line);
			return;
		}
		if (!isAllRequiredFieldspresent(fields)){
			logger.error("some fields are not present for line "+line);
			return;
		};
		
		if (isUnWantedHouseNumber(fields[2])){
			logger.warn("invalid house number '"+fields[3]+"'for line "+line);
			return;
		}
		String cleanedNumber= cleanNumber(fields[2]);
		if (cleanedNumber==null){
			return;
		}
		
		Point location;
		try {
			location = GeolocHelper.createPoint(new Float(fields[0]), new Float(fields[1]));
		} catch (NumberFormatException e) {
			logger.error("can not get location for "+line);
			return;
		}
		String countryCode =null;
		if (fields.length>=10 && isEmptyField(fields, 10, false)){
			countryCode= extractCountrycode(fields[10]);
		}
		
		String streetName = null;
		if (!isUnWantedStreetName(fields[3])){
			streetName = cleanupStreetName(fields[3]);
			streetName = StringHelper.expandStreetType(streetName, countryCode);
		}
		
		boolean newstreet = false;
		OpenStreetMap street =findNearestStreet(streetName, location);
		if (street==null){
			logger.error("can not find street for name "+streetName+", position :"+ location);
			logger.error("laststreetname="+lastStreetName+", streetname="+streetName+", lastStreet="+lastStreet+",hash="+fields[10]);
			if (lastStreetName!=null && lastStreetName.equals(streetName) && lastStreet!=null && lastStreet.getOpenstreetmapId()==null){
				logger.error("already added for "+streetName+ " : "+lastStreet);
				street=lastStreet;
				//lastStreetName = streetName;//useless
			} else {
				if (lastStreet==null){
					logger.error("will create street for "+streetName);
				} else {
					logger.error("will create street for "+streetName+ " : "+lastStreet+"/"+lastStreet.getId()+"/"+lastStreet.getOpenstreetmapId());
				}
				street = new OpenStreetMap();
				street.setGid(idGenerator.getNextGId());
				street.setSource(GISSource.OPENADDRESSES);
				if (streetName!=null){
					street.setName(streetName);
					StringHelper.updateOpenStreetMapEntityForIndexation(street);
				}
				street.setLocation(location);
				if (!isEmptyField(fields,5, false)){
					street.setIsIn(fields[5]);
				}
				if (!isEmptyField(fields,6, false)){
					street.setIsInPlace(fields[6]);
				}
				if (!isEmptyField(fields,7, false)){
					street.setIsInAdm(fields[7]);
				}
				street.setCountryCode(countryCode);
				if (!isEmptyField(fields,8, false)){
					street.addIsInZip(fields[8]);
					street.setZipCode(fields[8]);
				}
				//openStreetMapImporter.setIsInFields(street);
				if (street.getName() !=null){
					street.setAlternateLabels(labelGenerator.generateLabels(street));
					street.setLabel(labelGenerator.generateLabel(street));
					street.setFullyQualifiedName(labelGenerator.getFullyQualifiedName(street, false));
					street.setLabelPostal(labelGenerator.generatePostal(street));
				}
				newstreet=true;
			}
		} 
		if (!isEmptyField(fields, 8, false)){
			street.setZipCode(fields[8]);
		}
		
		HouseNumber hn = new HouseNumber(cleanedNumber,location);
		hn.setSource(GISSource.OPENADDRESSES);
		if (street!=null){
			if (newstreet){
				openStreetMapDao.save(street);
				street.addHouseNumber(hn);
				openStreetMapDao.save(street);
			} else {
				street.addHouseNumber(hn);
				houseNumberDao.save(hn);
			}
				lastStreet = street;
				lastStreetName = streetName;
		} else {
			logger.error("OpenAddressesSimpleImporter : no street are found");
		}
		
		

	}

	protected String cleanNumber(String string) {
		if (string!=null){
			String cleaned = string.trim().replaceFirst("^0+", "");
			if (cleaned.trim().length()==0){
				return null;
			} else {
				return cleaned;
			}
		}
		return null;
	}

	protected String extractCountrycode(String string) {
		if (string !=null){
			Matcher m = COUNTRY_EXTRACTION_PATTERN.matcher(string);
			if (m.find()){
				return m.group(1).toUpperCase();
			}
		}
		return null;
	}

	protected String cleanupStreetName(String streetName){
		if (streetName!=null){
			if (streetName.length()>MAX_NAME_SIZE){
				streetName = streetName.substring(0, MAX_NAME_SIZE);
			}
			return streetName.trim().replaceAll("[\\s']+", " ").replaceFirst("^0+(?!$)", "").trim();
		}
		return streetName;
	}

	protected boolean isAllRequiredFieldspresent(String[] fields) {
		if (isEmptyField(fields, 0, false)
			||	isEmptyField(fields, 1, false)
			||	isEmptyField(fields, 2, false)
				){
			return false;
		}
		return true;
	}
	
	
	protected OpenStreetMap findNearestStreet(String streetName, Point location) {
		//Openstreetmap has sometimes, for a  same street, several segment, so we do a fulltext search and then search for the nearest based on shape,not nearest point
		//logger.error("findNearestStreet :streetname="+streetName+" and location = "+location);
		if (location == null){
			logger.error("findNearestStreet :location is null");
			return null;
		}
		List<OpenStreetMap> osmsFromDb = openStreetMapDao.getNearestsFrom(location, true, false, DEFAULT_SEARCH_DISTANCE);;
		if (streetName==null || "".equals(streetName.trim()) || "\"\"".equals(streetName.trim()) || "-".equals(streetName.trim()) || "---".equals(streetName.trim()) || "--".equals(streetName.trim())){
				//logger.error("findNearestStreet : no streetname, we search by location "+location);
			if (osmsFromDb!=null && osmsFromDb.size()>0){
				return osmsFromDb.get(0);
			} else {
				//name is null and no street found=>return null;
				return null;
			}
				//OpenStreetMap osm =	openStreetMapDao.getNearestFrom(location,DEFAULT_SEARCH_DISTANCE);
				//logger.error("findNearestStreet :getNearestFrom return "+osm);
				
				//return osm;
		}
		//name is not null=> first iterate to find a street with the same name 
		if (osmsFromDb!=null && osmsFromDb.size()>0){
			for (OpenStreetMap openstreetmap:osmsFromDb){
				if (openstreetmap !=null && StringHelper.isSameStreetName(streetName, openstreetmap)){
					return openstreetmap;
				}

			}
		}	
		//name is not null but iterate on name fail=>try by fulltext
		FulltextQuery query;
		try {
			query = new FulltextQuery(streetName, Pagination.paginateWithMaxResults(50).from(1).to(50), MEDIUM_OUTPUT, 
					com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, null);
		} catch (IllegalArgumentException e) {
			logger.error("findNearestStreet : can not create a fulltext query for "+streetName+", will return the nearest");
			return openStreetMapDao.getNearestFrom(location,DEFAULT_SEARCH_DISTANCE);
		}
		query.withAllWordsRequired(false).withoutSpellChecking();
		query.around(location);
			query.withRadius(DEFAULT_FULLTEXT_SEARCH_DISTANCE).withFuzzy(false);
		FulltextResultsDto results;
		try {
			results = fullTextSearchEngine.executeQuery(query);
		} catch (RuntimeException e) {
			logger.error("findNearestStreet : error during fulltext search : "+e.getMessage(),e);
			return null;
		}
		int resultsSize = results.getResultsSize();
	//	logger.error(query + "returns "+resultsSize +" results");
		OpenStreetMap osm =null;
		float score= -1;
		List<SolrResponseDto> resultsList = results.getResults();
		if (resultsSize == 1) {
			score=results.getMaxScore();
		//	logger.error("only one result for streetname="+streetName+" and location="+location);
			SolrResponseDto street = resultsList.get(0);
			if (street!=null){
				Long gid = street.getFeature_id();
				//logger.error("findNearestStreet : find a street with osmId "+openstreetmapId);
				if (gid!=null){
					 osm = openStreetMapDao.getByGid(gid);
					if (osm == null) {
						logger.error("findNearestStreet : can not find street for id "+gid);
					}
					/*if (!StringHelper.isSameStreetName(streetName, osm)){
						osm =null;
					}*/
				}
			}
		} else if (resultsSize > 1) {
					//logger.error("max score for "+streetName+"="+results.getMaxScore());
					score=results.getMaxScore();
					 osm = getNearestByGIds(resultsList,location,streetName);
					//logger.error("findNearestStreet : getNearestByIds returns "+osm+" for "+streetName);
		} else {
			osm=null;
		}
		/*if ((osmDB!=null && osm==null) || (osmDB==null && osm!=null) || (osmDB!=null && osm!=null && osmDB.getId()!= osm.getId())){
			logger.error("notsame street "+score+" : "+streetName+"/"+location+" returns "+osmDB+" and "+osm);
			if (osmDB!=null && osmDB.getName()!=null && osmDB.getName().matches(".*\\d+.*")){
				 osmDB =	openStreetMapDao.getNearestFromByName(location, DEFAULT_SEARCH_DISTANCE, streetName);
			}
		}*/
		if (osm == null){
			//the fulltext search fails too, we search the nearests street in a short radius
			//accept street that have the same name without streettype in a shortest radius

			//if not found return the nearest one if the second is not too far (is so,it is ambiguous) or name without strettype is the same
			if (osmsFromDb!=null){
				if (osmsFromDb.size()==0){
					return null;
				}
				else if (osmsFromDb.size()>=1){
					OpenStreetMap nearest = osmsFromDb.get(0);
					if (nearest != null && nearest.getShape()!=null){
						DistancePointDto pointOnNearest = orthogonalProjection.getPointOnLine(nearest.getShape(), location);
						if (pointOnNearest != null  && pointOnNearest.getDistance()!=null && pointOnNearest.getDistance()!=0 && pointOnNearest.getDistance()<ACCEPTABLE_DISTANCE_HOUSE_TO_STREET){
							if (osmsFromDb.size()==1){// only one result that is close to street
								logger.error("neareststreet orthogonal for "+location+" at "+pointOnNearest+" : "+nearest);
								doMapMatching(streetName, nearest,
										pointOnNearest);
							
							
								//then return the street
								if (nearest.getHouseNumbers()!=null){
									logger.error("map matching nbhouses after "+nearest.getHouseNumbers().size());
								}
								return nearest;
							} else { //more than one result
								OpenStreetMap second = osmsFromDb.get(1);
								if (second != null && second.getShape()!=null){
									DistancePointDto secondPointOnNearest = orthogonalProjection.getPointOnLine(second.getShape(), location);
									//start debug
									/*String streetsAsString = "";
									for (OpenStreetMap openstreetmap:osmsFromDb){
										if (openstreetmap!=null){
											if (openstreetmap.getName()==null){
												streetsAsString=streetsAsString+openstreetmap.getGid()+" / ";
											} else {
												streetsAsString=streetsAsString+openstreetmap.getName()+" / ";
											}
										}
									}
									
									logger.error("ambiguous orthogonal list = "+streetsAsString);*/
									//end debug
									if (secondPointOnNearest != null && secondPointOnNearest.getDistance()!=null && secondPointOnNearest.getDistance()!=0 && !areTooCloseDistance(pointOnNearest.getDistance(),secondPointOnNearest.getDistance())){
										int percent = 0;	
										if (nearest.getName()!=null){
											int l = leven.execute(streetName, nearest.getName());
											int max = Math.max(streetName.length(), nearest.getName().length());
											percent  = (max-l)/max*100;
											/*=>normalize avant 
											 *  /RUA ANTONIO DIAS ADORNO : 189.17897022196354/19.175648558177603/Rua Antônio Dias Adomo  55.451943033920784/Rua Doutor Armando Cunha


											 */
											} 
											logger.error("ambiguous orthogonal leven="+percent+"% for "+location.getY()+" "+location.getX()+" /"+streetName+" : " +(Math.abs(pointOnNearest.getDistance()-secondPointOnNearest.getDistance())/ Math.min(pointOnNearest.getDistance(), secondPointOnNearest.getDistance() ))*100+"/"+pointOnNearest.getDistance()+"/"+nearest.getName()+"  "+secondPointOnNearest.getDistance()+"/"+second.getName());
											doMapMatching(streetName, nearest, pointOnNearest);
									    //then return the street
											//TODO check percent of similarity=>if 0 (null) or >XX
										return nearest;
									} else {
										Double percent = Math.abs(pointOnNearest.getDistance()-secondPointOnNearest.getDistance())/ Math.min(pointOnNearest.getDistance(), secondPointOnNearest.getDistance() )*100;
										logger.error("ambiguous orthogonal tooclose "+percent.intValue()+"% for "+streetName+"/"+location.getY()+" "+location.getX()+" : "+pointOnNearest.getDistance()+"/"+nearest.getName()+"  "+secondPointOnNearest.getDistance()+"/"+second.getName());
									}
								}
							}
						}
					} 
				} 
			}
			return null;
		}
		return osm;
	}

	protected void doMapMatching(String streetName, OpenStreetMap nearest,
			DistancePointDto pointOnNearest) {
		if (nearest!=null && nearest.getName()==null && pointOnNearest.getDistance() <=100){//if found street name is null and distance <100 =>remember it, 
			if(null_name_street!=null){
				if (null_name_street.getGid() == nearest.getGid()){//if the last street was that street and the name is still null
					//cnt++
					logger.error("map matching counter increment for "+streetName+"/"+null_name_street+" to "+(null_name_street_counter+1));
					null_name_street_counter++;
				} else {//it was not that street
					//reset the counter, clear the last_null_street
					logger.error("map matching reset increment for "+streetName+"/"+null_name_street);
					null_name_street_counter = 0;
					null_name_street=null;
				}
			} else {
				null_name_street_counter = 0;
				null_name_street = nearest;
			}
		}
			
		if (null_name_street_counter >=3){
			//set the street name
			nearest.setName(WordUtils.capitalize(streetName.toLowerCase()));
			nearest.setSource(GISSource.OSM_OPENADDRESSES);
			logger.error("map matching "+nearest);
			if (nearest.getHouseNumbers()!=null){
				logger.error("map matching nbhouses before "+nearest.getHouseNumbers().size());
			}
			openStreetMapDao.save(nearest);
			//reset the counter, clear the last_null_street
			null_name_street_counter = 0;
			null_name_street=null;
		}
	}
	

	protected boolean areTooCloseDistance(Double distance, Double distance2) {
		if (distance!=null && distance2!=null ){
			double min = Math.min(distance, distance2);
			if( min!=0 &&  Math.abs(distance-distance2)/min >= 0.5){
				return false;
			}
		}
		return true;
	}
	
	
	


	protected OpenStreetMap findNearestStreet_old(String streetName, Point location) {
		//Openstreetmap has sometimes, for a  same street, several segment, so we do a fulltext search and then search for the nearest based on shape,not nearest point
		logger.error("findNearestStreet :streetname="+streetName+" and location = "+location);
		if (location == null){
			logger.warn("findNearestStreet :location is null");
			return null;
		}
		if (streetName==null || "".equals(streetName.trim()) || "\"\"".equals(streetName.trim()) || "-".equals(streetName.trim()) || "---".equals(streetName.trim()) || "--".equals(streetName.trim())){
				logger.warn("findNearestStreet : no streetname, we search by location "+location);
				OpenStreetMap osm =	openStreetMapDao.getNearestFrom(location,DEFAULT_SEARCH_DISTANCE);
				logger.error("findNearestStreet :getNearestFrom return "+osm);
				
				return osm;
		}
		long start = System.currentTimeMillis();
		
		OpenStreetMap osmDB =	openStreetMapDao.getNearestFromByName(location, DEFAULT_SEARCH_DISTANCE, streetName);
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		
		
		start = System.currentTimeMillis();
		FulltextQuery query;
		try {
			query = new FulltextQuery(streetName, Pagination.DEFAULT_PAGINATION, MEDIUM_OUTPUT, 
					com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, null);
		} catch (IllegalArgumentException e) {
			logger.error("can not create a fulltext query for "+streetName+", will return the nearest");
			return openStreetMapDao.getNearestFrom(location,2000L);
		}
		query.withAllWordsRequired(false).withoutSpellChecking();
		query.around(location);
			query.withRadius(DEFAULT_SEARCH_DISTANCE);
		FulltextResultsDto results;
		try {
			results = fullTextSearchEngine.executeQuery(query);
		} catch (RuntimeException e) {
			logger.error("error during fulltext search : "+e.getMessage(),e);
			return null;
		}
		int resultsSize = results.getResultsSize();
	//	logger.warn(query + "returns "+resultsSize +" results");
		OpenStreetMap osm =null;
		List<SolrResponseDto> resultsList = results.getResults();
		if (resultsSize == 1) {
			SolrResponseDto street = resultsList.get(0);
			if (street!=null){
				Long openstreetmapId = street.getOpenstreetmap_id();
				//logger.warn("findNearestStreet : find a street with osmId "+openstreetmapId);
				if (openstreetmapId!=null){
					 osm = openStreetMapDao.getByOpenStreetMapId(openstreetmapId);
					if (osm == null) {
						logger.warn("can not find street for id "+openstreetmapId);
					}
				}
			}
		} if (resultsSize > 1) {
					 osm = getNearestByGIds(resultsList,location,streetName);
					//logger.warn("findNearestStreet : getNearestByIds returns "+osm+" for "+streetName);
		}
		 end = System.currentTimeMillis();
		 duration = end - start;
		
		if (osmDB!=null && osm!=null && osmDB.getId()!= osm.getId()){
			logger.error("notsame street : "+streetName+"/"+location+" returns "+osmDB+" and "+osm);
		}
		return osm;
	}

	protected OpenStreetMap getNearestByGIds(List<SolrResponseDto> results,Point point,String streetname) {
		List<Long> ids = new ArrayList<Long>();
		OpenStreetMap result = null;
		if (results!=null){
			for (SolrResponseDto dto:results){
				if (dto!=null && dto.getOpenstreetmap_id()!=null){
					ids.add(dto.getOpenstreetmap_id());
				}
			}
			String idsAsSTring="";
			for (Long id:ids){
				idsAsSTring = idsAsSTring+","+id;
			}
			//logger.warn("getNearestByIds : "+idsAsSTring);
			result = openStreetMapDao.getNearestByGIds(point, ids);
			if (result==null){
			logger.warn("getNearestByGIds for"+streetname+" and  ids "+idsAsSTring+" and point" +point+" return  "+result);
			}
		}
		return result;
	}

	
	
		
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped
	 * ()
	 */
	@Override
	public boolean shouldBeSkipped() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
	 * setCommitFlushMode()
	 */
	@Override
	protected void setCommitFlushMode() {
		this.openStreetMapDao.setFlushMode(FlushMode.COMMIT);
		this.houseNumberDao.setFlushMode(FlushMode.COMMIT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
	 * shouldIgnoreComments()
	 */
	@Override
	protected boolean shouldIgnoreComments() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
	 * shouldIgnoreFirstLine()
	 */
	@Override
	protected boolean shouldIgnoreFirstLine() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
	 */
	public List<NameValueDTO<Integer>> rollback() {
		List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
		return deletedObjectInfo;
	}

	

	@Override
	// TODO test
	protected void tearDown() {
		super.tearDown();
		FullTextSearchEngine.disableLogging=false;
	}


	@Required
	public void setHouseNumberDao(IhouseNumberDao houseNumberDao) {
		this.houseNumberDao = houseNumberDao;
	}
	
	@Required
	public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
		this.openStreetMapDao = openStreetMapDao;
	}
	
	@Required
	public void setFullTextSearchEngine(IFullTextSearchEngine fullTextSearchEngine) {
		this.fullTextSearchEngine = fullTextSearchEngine;
	}

	@Required
	public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
		this.solRSynchroniser = solRSynchroniser;
	}

	/*@Required
	public void setOpenStreetMapImporter(
			OpenStreetMapSimpleImporter openStreetMapImporter) {
		this.openStreetMapImporter = openStreetMapImporter;
	}*/
	
	


}
