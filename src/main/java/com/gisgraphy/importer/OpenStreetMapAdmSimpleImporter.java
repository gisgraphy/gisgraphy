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
import com.gisgraphy.domain.repository.IGisFeatureDao;
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
import com.gisgraphy.helper.AdmStateLevelInfo;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.util.StringUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the administrative from an (pre-processed) openStreet map data file.
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenStreetMapAdmSimpleImporter extends AbstractSimpleImporterProcessor {

	public static final int SCORE_LIMIT = 1;

	public final static int BATCH_UPDATE_SIZE = 100;

	protected static final Logger logger = LoggerFactory.getLogger(OpenStreetMapAdmSimpleImporter.class);

	public static final Output MINIMUM_OUTPUT_STYLE = Output.withDefaultFormat().withStyle(OutputStyle.SHORT);

	protected IIdGenerator idGenerator;

	protected ICityDao cityDao;

	protected ICitySubdivisionDao citySubdivisionDao;

	protected IAdmDao admDao;


	protected ISolRSynchroniser solRSynchroniser;

	protected IFullTextSearchEngine fullTextSearchEngine;

	LabelGenerator generator = LabelGenerator.getInstance();

	
	protected int currentOsmlevel =0; 
	protected int calculatedLevel =1;
	
	protected String currentCountryCode = null;

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear()
	 */
	@Override
	protected void flushAndClear() {
		admDao.flushAndClear();
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
		return ImporterHelper.listCountryFilesToImport(importerConfig.getOpenStreetMapAdmDir());
	}

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
	 */
	@Override
	protected int getNumberOfColumns() {
		return 9;
	}

	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
	 */
	@Override
	protected void processData(String line) throws ImporterException {
		String[] fields = line.split("\t");
		String name=null;
		int adminLevelOsm=1;
		Point location=null;
		Adm place=null;
		String countrycode=null;
		

	


		// new Line table has the following fields :
		// --------------------------------------------------- 
		//0: id;	1 : name;	2: shape; 3: location;	4: countrycode;5: the administrative level 
		//6: type;7: alternatenames;8 is_in_adm


		checkNumberOfColumn(fields);


		// name
		if (!isEmptyField(fields, 1, false)) {
			name=fields[1].trim();
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

		//admin level
		if (!isEmptyField(fields, 5, false)) {
			String adminLevelAsString =fields[5].trim();

			try {
				adminLevelOsm = Integer.parseInt(adminLevelAsString);
				int level = calculateAdmLevel(countrycode,adminLevelOsm);
				if (!shouldBeImported(countrycode,adminLevelOsm)){
					return;
				}
				place = new Adm(level);
				place.setCountryCode(countrycode);
				place.setName(name);
			} catch (NumberFormatException e) {
				logger.error("can not parse admin level id "+adminLevelAsString);
				return;
			}
		}

		//osmId
		if (!isEmptyField(fields, 0, true)) {
			String osmIdAsString =fields[0].trim();
			
			try {
				Long osmId =  Long.parseLong(osmIdAsString);
				place.setOpenstreetmapId(osmId);
			} catch (NumberFormatException e) {
				logger.error("can not parse openstreetmap id "+ osmIdAsString);
			}
		}

		//shape
		if(!isEmptyField(fields, 2, false)){
			try {
				Geometry shape = (Geometry) GeolocHelper.convertFromHEXEWKBToGeometry(fields[2]);
				place.setShape(shape);
			} catch (RuntimeException e) {
				logger.warn("can not parse shape for id "+fields[1]+" : "+e);
			}
		}

		
		//location
		if (!isEmptyField(fields, 3, false)) {
			try {
				location = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[3]);
				place.setLocation(location);
			} catch (RuntimeException e) {
				logger.warn("can not parse location for "+fields[3]+" : "+e);
				return;
			}
		}
		
		if(!isEmptyField(fields, 6, false)){
			place.setAmenity(fields[6]);
		}

		
		//populate alternatenames
		if (!isEmptyField(fields, 7, false)) {
			String alternateNamesAsString=fields[7].trim();
			populateAlternateNames(place,alternateNamesAsString);
			
		
		}

		//isinadm
		if(!isEmptyField(fields, 8, false)){
			List<AdmDTO> admDTOs = ImporterHelper.parseIsInAdm(fields[8]);
			//current level is the osm one!
			populateAdmNames(place,adminLevelOsm,admDTOs);
			setParent(place,admDTOs);
		} 
		

		place.setAlternateLabels(generator.generateLabels(place));
		place.setLabel(generator.generateLabel(place));
		place.setFullyQualifiedName(generator.getFullyQualifiedName(place));
		//postal is not set because it is only for street
		
		place.setFeatureId(idGenerator.getNextFeatureId());
		place.setSource(GISSource.OSM);

		try {
			save(place);
		} catch (ConstraintViolationException e) {
			logger.error("Can not save "+dumpFields(fields)+"(ConstraintViolationException) we continue anyway but you should consider this",e);
		}catch (Exception e) {
			logger.error("Can not save "+dumpFields(fields)+" we continue anyway but you should consider this",e);
		}

	}
	
	protected boolean shouldBeImported(String countryCode,int osmLevel) {
		return AdmStateLevelInfo.shouldBeImported(countryCode, osmLevel);
	}

	@Override
	 protected int getMaxInsertsBeforeFlush() {
	    	return 1;
	    }


	protected int calculateAdmLevel(String countryCode,int adminLevel) {
		boolean countryHasChanged =false;
		if (currentOsmlevel==0){
			currentOsmlevel=adminLevel;
			calculatedLevel = 1;
		}
		if (currentCountryCode == null){
			currentCountryCode = countryCode;
			calculatedLevel = 1;
		}
		if (!countryCode.equals(currentCountryCode)){
			currentCountryCode=countryCode;
			calculatedLevel = 1;
			countryHasChanged=true;
		}
		if (adminLevel>currentOsmlevel && !countryHasChanged){
			calculatedLevel++;
			currentOsmlevel = adminLevel;
		}
		if (calculatedLevel>5){
			return 5;
		}
		return calculatedLevel;
	}

	protected GisFeature populateAdmNames(Adm adm, int currentLevel, List<AdmDTO> admdtos) {
		return ImporterHelper.populateAdmNames(adm, currentLevel, admdtos);


	}
	
	protected Adm setParent(Adm adm, List<AdmDTO> adms){
		if (adms!=null && adms!=null && adms.size()>=1){
			AdmDTO last = adms.get(adms.size()-1);
			if (last!=null && last.getAdmOpenstreetMapId()!=0){
				Adm parent = admDao.getByOpenStreetMapId(last.getAdmOpenstreetMapId());
				if (parent!=null && parent.getLevel()< adm.getLevel()){
						adm.setParent(parent);
				}
			}
		}
		return adm;
		
	}

	

	/**
	 * @param fields
	 *                The array to process
	 * @return a string which represent a human readable string of the Array but without shape because it is useless in logs
	 */
	protected static String dumpFields(String[] fields) {
		String result = "[";
		for (int i=0;i<fields.length;i++) {
			if (i==2){
				result= result+"THE_SHAPE;";
			}else {
				result = result + fields[i] + ";";
			}
		}
		return result + "]";
	}

	

	void save(GisFeature feature) {
		if (feature!=null){
			if (feature instanceof Adm){
				admDao.save((Adm)feature);
			} else if (feature instanceof CitySubdivision){
				citySubdivisionDao.save((CitySubdivision)feature);
			}
		}
	}

	

	GisFeature populateAlternateNames(GisFeature feature,
			String alternateNamesAsString) {
		return ImporterHelper.populateAlternateNames(feature,alternateNamesAsString);

	}




	protected GisFeature populateAdmNames(GisFeature gisFeature, int currentLevel, List<AdmDTO> admdtos){
		return ImporterHelper.populateAdmNames(gisFeature, currentLevel, admdtos);

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
	public void setCitySubdivisionDao(ICitySubdivisionDao citySubdivisionDao) {
		this.citySubdivisionDao = citySubdivisionDao;
	}


}
