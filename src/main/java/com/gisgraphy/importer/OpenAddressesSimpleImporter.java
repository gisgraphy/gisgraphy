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
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the street from an (pre-processed) openaddresses.io data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenAddressesSimpleImporter extends AbstractSimpleImporterProcessor {

	@Autowired
	protected OpenStreetMapSimpleImporter openStreetMapImporterHelper;

	protected IOpenStreetMapDao openStreetMapDao;

	protected IhouseNumberDao houseNumberDao;

	protected ISolRSynchroniser solRSynchroniser;

	protected IFullTextSearchEngine fullTextSearchEngine;

	@Autowired
	protected IIdGenerator idGenerator;

	BasicAddressFormater formater = BasicAddressFormater.getInstance();

	LabelGenerator labelGenerator = LabelGenerator.getInstance();
	String[] currentfields = null;

	private static final String HASH_RESTART = null;

	private static final Pattern CORRECT_LINE_PATTERN = Pattern.compile(",\"([^\"]+)\",");

	//the fulltext has to be greater than the db one since the fulltext use boundingbox nd midle point (db use cross and can be lower)
	public static final long DEFAULT_FULLTEXT_SEARCH_DISTANCE = 5000L;

	protected static final long DEFAULT_SEARCH_DISTANCE = 1000L;

	protected static final Logger logger = LoggerFactory.getLogger(OpenAddressesSimpleImporter.class);


	protected final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);

	private static final Pattern ALL_ZERO = Pattern.compile("^0+$");

	private static final Pattern NOT_VALID_LABEL = Pattern.compile("\\b(NULL|UNDEFINED|UNAVAILABLE)\\b",Pattern.CASE_INSENSITIVE);

	public static final int MAX_NAME_SIZE = 250;


	protected boolean isZeroHouseNumber(String houseNumber){
		if (houseNumber!=null){
			return ALL_ZERO.matcher(houseNumber).matches();
		}
		return false;
	}

	protected boolean isUnWantedHouseNumber(String houseNumber){
		if (houseNumber!=null){
			if (isUnWantedStreetName(houseNumber) || isZeroHouseNumber(houseNumber) || houseNumber.startsWith("-") || !houseNumber.matches(".*[0-9]+.*")){
				return true;
			}
		}
		return false;

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

	protected String lastStreetName=null;
	protected Point lastPoint = null;

	protected String lasthash="?";
	protected String currentHash="?";

	protected boolean notYetThere = true;


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
		line=correctLine(line);
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
			logger.warn("invalid house number '"+fields[2]+"'for line "+line);
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
		if (fields.length>=10 && !isEmptyField(fields, 10, false)){
			countryCode= extractCountrycode(fields[10]);
			lasthash = currentHash;
			currentHash = fields[10];
		}
		//by specifying hash restart we allow to re run this importer from the line where the hash is equals to hash restart
		if (HASH_RESTART==null){
			notYetThere=false;
		} else {

			if (currentHash.equals(HASH_RESTART)){
				notYetThere = false;
			}

			if (HASH_RESTART!=null && notYetThere){
				//logger.error("notyetthere");
				return;
			}
		}

		String streetName = null;
		if (!isUnWantedStreetName(fields[3])){
			streetName = cleanupStreetName(fields[3]);
			streetName = StringHelper.expandStreetType(streetName, countryCode);
		}


		OpenStreetMap street =findNearestStreet(streetName, location,cleanedNumber,fields);
		//null=>need to create
		//empty street =>do nothing
		//a street populate and save
		if (street!=null){
			if (street.getId()==null){
				//ignore this street
				return;
			}
		} else {
			street= createStreet(fields);
			openStreetMapDao.save(street);
		}
		if (!isEmptyField(fields, 8, false)){
			street.setZipCode(fields[8]);
		} 

		HouseNumber hn = new HouseNumber(cleanedNumber,location);
		hn.setSource(GISSource.OPENADDRESSES);
		street.addHouseNumber(hn);
		houseNumberDao.save(hn);

		lastPoint= location;
		lastStreetName = streetName;
	}

	protected String correctStreetName(String streetName,String countryCode){
		if (streetName!=null){
			streetName = cleanupStreetName(streetName);
			streetName = StringHelper.expandStreetType(streetName, countryCode);
			streetName = WordUtils.capitalize(streetName.toLowerCase());
			return streetName;
		}
		return null;
	}


	protected OpenStreetMap createStreet(String[] fields) {
		if (fields!=null && fields.length==getNumberOfColumns() ){


			logger.error("will create street for "+dumpFields(fields));
			OpenStreetMap street = new OpenStreetMap();
			Point location;
			try {
				location = GeolocHelper.createPoint(new Float(fields[0]), new Float(fields[1]));
			} catch (NumberFormatException e) {
				logger.error("can not get location for "+fields);
				return null;
			}
			String countryCode =null;
			if (fields.length>=10 && !isEmptyField(fields, 10, false)){
				countryCode= extractCountrycode(fields[10]);
				lasthash = currentHash;
				currentHash = fields[10];
			}
			street.setGid(idGenerator.getNextGId());
			street.setSource(GISSource.OPENADDRESSES);
			String streetName = null;
			if (!isUnWantedStreetName(fields[3])){
				streetName = correctStreetName(fields[3],countryCode);
				street.setName(streetName);
			}
			if (streetName!=null){
				StringHelper.updateOpenStreetMapEntityForIndexation(street);
			}
			street.setLocation(location);
			/*if (!isEmptyField(fields,5, false)){
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
		}*/
			openStreetMapImporterHelper.setIsInFields(street);
			if (street.getName() !=null){
				street.setAlternateLabels(labelGenerator.generateLabels(street));
				street.setLabel(labelGenerator.generateLabel(street));
				street.setFullyQualifiedName(labelGenerator.getFullyQualifiedName(street, false));
				street.setLabelPostal(labelGenerator.generatePostal(street));
			}
			return street;
		} else {
			return null;
		}
	}

	protected  String correctLine(String line){
		if (line==null){
			return null;
		}else {

			Matcher m = CORRECT_LINE_PATTERN.matcher(line);
			StringBuffer sb = new StringBuffer();
			while(m.find()){
				String corrected=m.group(1).replaceAll(",", " ");
				m.appendReplacement(sb, ","+corrected+",");
			}
			m.appendTail(sb);
			return sb.toString();
		}
	}

	protected String cleanNumber(String string) {
		if (string!=null){
			String cleaned = string.trim().replaceFirst("#", "").replaceFirst("^0+", "");
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
			return streetName.trim().replaceAll("[\\s]+", " ").replaceFirst("^0+(?!$)", "").trim();
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


	/**
	 * 
	 * Simply returns a string representation x y instead of the y x one of the toString of jts Point
	 */
	protected String ts(Point point){
		if (point!=null){
			return point.getY()+" "+point.getX();
		}
		return null;
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





	protected OpenStreetMap findNearestStreet(String streetName, Point location, String cleanedNumber, String[] fields) {
		//Openstreetmap has sometimes, for a  same street, several segment, so we do a fulltext search and then search for the nearest based on shape,not nearest point
		logger.error("findNearestStreet :streetname="+streetName+" and location = "+location +" for house number "+cleanedNumber );
		if (location == null){
			logger.warn("findNearestStreet :location is null");
			return null;
		}
		if (streetName==null || "".equals(streetName.trim()) || "\"\"".equals(streetName.trim()) || "-".equals(streetName.trim()) || "---".equals(streetName.trim()) || "--".equals(streetName.trim())){
			logger.warn("findNearestStreet : no streetname, we search by location "+location);
			OpenStreetMap osm =	openStreetMapDao.getNearestFrom(location,DEFAULT_SEARCH_DISTANCE);
			logger.warn("findNearestStreet :getNearestFrom return "+osm);

			return osm;
		}


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
		query.withRadius(DEFAULT_FULLTEXT_SEARCH_DISTANCE);
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
		if (resultsSize==0){
			return createStreet(fields);
		}

		return osm;
	}

	protected OpenStreetMap getNearestByGIds(List<SolrResponseDto> results,Point point,String streetname) {
		List<Long> ids = new ArrayList<Long>();
		OpenStreetMap result = null;
		if (results!=null){
			for (SolrResponseDto dto:results){
				if (dto!=null && dto.getFeature_id()!=null){
					ids.add(dto.getFeature_id());
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
		return importerConfig.isOpenaddressesImporterEnabled();
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
		return false;
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

	@Required
	public void setOpenStreetMapImporterHelper(
			OpenStreetMapSimpleImporter openStreetMapImporterHelper) {
		this.openStreetMapImporterHelper = openStreetMapImporterHelper;
	}




}
