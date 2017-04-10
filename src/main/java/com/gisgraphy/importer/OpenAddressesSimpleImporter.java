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
import java.util.regex.Pattern;

import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.repository.IdGenerator;
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
import com.gisgraphy.helper.GeolocHelper;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the street from an (pre-processed) openStreet map data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenAddressesSimpleImporter extends AbstractSimpleImporterProcessor {


	public static final long DEFAULT_SEARCH_DISTANCE = 1000L;

	protected static final Logger logger = LoggerFactory.getLogger(OpenAddressesSimpleImporter.class);

	protected IOpenStreetMapDao openStreetMapDao;

	protected IhouseNumberDao houseNumberDao;

	protected ISolRSynchroniser solRSynchroniser;

	protected IFullTextSearchEngine fullTextSearchEngine;
	
	@Autowired
    protected IIdGenerator idGenerator;
	
	protected final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);
	
	long cummulative_db_time = 0;
	long cummulative_fulltext_time = 0;
	
	long cummulative_db_nb_request = 0;
	long cummulative_fulltext_nb_request = 0;
	

	private static final Pattern ALL_ZERO = Pattern.compile("^0+$");
	private static final Pattern NOT_VALID_LABEL = Pattern.compile("\\b(NULL|UNDEFINED|UNAVAILABLE)\\b",Pattern.CASE_INSENSITIVE);

	private static final int MAX_NAME_SIZE = 250;
	
	
	
	
	
	
	protected boolean isZeroHouseNumber(String houseNumber){
		if (houseNumber!=null){
			return ALL_ZERO.matcher(houseNumber).matches();
		}
		return false;
		
	}
	
	protected boolean isUnWantedHouseNumber(String houseNumber){
		if (houseNumber!=null){
			return NOT_VALID_LABEL.matcher(houseNumber).matches() || isZeroHouseNumber(houseNumber);
		}
		return true;
		
	}
	protected boolean isUnWantedStreetName(String houseNumber){
		if (houseNumber!=null){
			return NOT_VALID_LABEL.matcher(houseNumber).find();
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
		//openStreetMapDao.flushAndClear();
		//houseNumberDao.flushAndClear();

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
		return new File[0];
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
	
	private Point lastPoint=null;

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
		//0:LON 1:LAT 2: NUMBER 3:STREET 4:UNIT 5:CITY 6:DISTRICT 7:REGION 8:POSTCODE 9:ID 10:HASH
		String[] fields = line.split(",");
		checkNumberOfColumn(fields);
		if (!isAllRequiredFieldspresent(fields)){
			logger.warn("some fields are not present for line "+line);
		};
		
		if (isUnWantedHouseNumber(fields[2])){
			logger.warn("invalid house number '"+fields[3]+"'for line "+line);
		}
		
		Point location;
		try {
			location = GeolocHelper.createPoint(new Float(fields[0]), new Float(fields[1]));
		} catch (NumberFormatException e) {
			logger.error("can not get location for "+line);
			return;
		}
		
		String streetname = null;
		if (!isUnWantedStreetName(fields[3])){
			streetname = cleanupStreetName(fields[3]);
		}
		
		
		OpenStreetMap street =null;
		boolean newStreet = false;
		if (lastStreetName != null & lastStreetName.equals(streetname) && lastPoint!=null && GeolocHelper.distance(lastPoint, location)<1000 && lastStreet !=null){
			street = lastStreet;
		} else {
			//save the last street
			openStreetMapDao.save(lastStreet);
			//search for the new One
			street = findNearestStreet(streetname, location);
			lastStreetName = streetname;
			lastStreet = street;
			lastPoint = location;
			newStreet = true;
		}
		
		if (street==null){
			street = new OpenStreetMap();
			street.setGid(idGenerator.getNextGId());
			street.setSource(GISSource.OPENADDRESSES);
			street.setName(streetname);
			street.setLocation(lastPoint);
			if (!isEmptyField(fields,5, false)){
				street.setIsIn(fields[5]);
			}
			if (!isEmptyField(fields,6, false)){
				street.setIsInPlace(fields[6]);
			}
			if (!isEmptyField(fields,7, false)){
				street.setIsInAdm(fields[7]);
			}
			lastStreet = street;
			lastStreetName = streetname;
			lastPoint = location;
		}
		if (!isEmptyField(fields, 8, false)){
			street.setZipCode(fields[8]);
		}
		
		HouseNumber hn = new HouseNumber(fields[2],location);
		hn.setSource(GISSource.OPENADDRESSES);
		street.addHouseNumber(hn);
		
		if (newStreet){
			openStreetMapDao.save(street);
		}
		
		
		

	}

	protected String cleanupStreetName(String streetName){
		if (streetName!=null){
			streetName.replaceFirst("^0+(?!$)", "");
			if (streetName.length()>MAX_NAME_SIZE){
				streetName = streetName.substring(0, MAX_NAME_SIZE);
			}
		}
		return streetName;
	}

	private boolean isAllRequiredFieldspresent(String[] fields) {
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
		cummulative_db_nb_request++;
		cummulative_db_time+=duration;
		
		
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
					 osm = getNearestByIds(resultsList,location,streetName);
					//logger.warn("findNearestStreet : getNearestByIds returns "+osm+" for "+streetName);
		}
		 end = System.currentTimeMillis();
		 duration = end - start;
		cummulative_fulltext_nb_request++;
		cummulative_fulltext_time+=duration;
		
		if (osmDB!=null && osm!=null && osmDB.getId()!= osm.getId()){
			logger.error("notsame street : "+streetName+"/"+location+" returns "+osmDB+" and "+osm);
		}
		return osm;
	}

	protected OpenStreetMap getNearestByIds(List<SolrResponseDto> results,Point point,String streetname) {
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
			result = openStreetMapDao.getNearestByosmIds(point, ids);
			if (result==null){
			logger.warn("getNearestByIds for"+streetname+" and  ids "+idsAsSTring+" and point" +point+" return  "+result);
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
		return false;
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


}
