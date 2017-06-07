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

import static com.gisgraphy.street.HouseNumberUtil.normalizeNumber;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.repository.IhouseNumberDao;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.HouseNumberType;
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
import com.gisgraphy.helper.OrthogonalProjection;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.importer.dto.AddressInclusion;
import com.gisgraphy.importer.dto.AssociatedStreetHouseNumber;
import com.gisgraphy.importer.dto.AssociatedStreetMember;
import com.gisgraphy.importer.dto.InterpolationHouseNumber;
import com.gisgraphy.importer.dto.InterpolationMember;
import com.gisgraphy.importer.dto.InterpolationType;
import com.gisgraphy.importer.dto.NodeHouseNumber;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the street from an (pre-processed) openStreet map data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenStreetMapHouseNumberSimpleImporter extends AbstractSimpleImporterProcessor {


	protected static final int ACCEPTABLE_DISTANCE_HOUSE_TO_STREET = 250;

	public static final long DEFAULT_SEARCH_DISTANCE = 500L;
	
	public static final long SHORT_SEARCH_DISTANCE = 250L;
	
	//the fulltext has to be greater than the db one since the fulltext use boundingbox nd midle point (db use cross and can be lower)
	public static final long DEFAULT_FULLTEXT_SEARCH_DISTANCE = 5000L;

	protected static final Logger logger = LoggerFactory.getLogger(OpenStreetMapHouseNumberSimpleImporter.class);

	protected IOpenStreetMapDao openStreetMapDao;

	protected IhouseNumberDao houseNumberDao;

	protected ISolRSynchroniser solRSynchroniser;

	protected IFullTextSearchEngine fullTextSearchEngine;
																//id		location	number			 name				streetname		city                zip				  suburb           shape             tpe		role	
	private static final String ASSOCIATED_HOUSE_NUMBER_REGEXP = "([0-9]+)___([^_]*)___((?:(?!___).)*)___((?:(?!___).)*)___((?:(?!___).)*)___((?:(?!___).)*)___((?:(?!___).)*)___((?:(?!___).)*)___((?:(?!___).)*)___([NW])___([^_]*)(?:___)?";

	private static final String INTERPOLATION_HOUSE_NUMBER_REGEXP = "([0-9]+)___([0-9])___((?:(?!___).)+)*___((?:(?!___).)+)*___((?:(?!___).)+)*(?:___)?";

	private static final Pattern ASSOCIATED_HOUSE_NUMBER_PATTERN = Pattern.compile(ASSOCIATED_HOUSE_NUMBER_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern INTERPOLATION_HOUSE_NUMBER_PATTERN = Pattern.compile(INTERPOLATION_HOUSE_NUMBER_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	
	protected final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);
	
	protected OrthogonalProjection orthogonalProjection = new OrthogonalProjection();
	
	long cummulative_db_time = 0;
	long cummulative_fulltext_time = 0;
	
	long cummulative_db_nb_request = 1;
	long cummulative_fulltext_nb_request = 1;
	


	
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
		return ImporterHelper.listCountryFilesToImport(importerConfig.getOpenStreetMapHouseNumberDir());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
	 * getNumberOfColumns()
	 */
	@Override
	protected int getNumberOfColumns() {
		return 9;
	}

	protected AssociatedStreetHouseNumber parseAssociatedStreetHouseNumber(String line) {
		/*
		 * A 1264114 "{"
		 * "30296860___0101000020E61000000AC435854620634009464AF440723BC0___288______Kelvin Grove Road___Kelvin Grove_________0102000020E610000005000000B3BA302D4520634069BFFFA03F723BC089940B3A462063408EE
9094B3C723BC0F73E5585462063404A16E6F340723BC021657A78452063404F6B894B44723BC0B3BA302D4520634069BFFFA03F723BC0___W___house___30296861___0101000020E6100000F45C72F545206340E75472EF3A723BC0___290______Kelvin Grove Ro
ad___Kelvin Grove_________0102000020E6100000050000001F5A1AAE44206340662E15C039723BC09E1095A1452063409762FD5536723BC0C2E677F545206340DC16C0EF3A723BC027E0320245206340ABE2D7593E723BC01F5A1AAE44206340662E15C039723BC0
___W___house"} SHAPE"
		 */
		if (line == null || "".equals(line.trim())) {
			logger.error("parseAssociatedStreetHouseNumber : null line "+line);
			return null;
		}
		String[] fields = line.split("\t");
		if (fields.length != 3) {
			logger.error("parseAssociatedStreetHouseNumber : wrong number of fields for line " + line + " expected 3 but was " + fields.length);
			return null;
		}
		if (!"A".equals(fields[0])) {
			logger.error("parseAssociatedStreetHouseNumber : wrong house Number Type for line " + line + " expected 'A' but was " + fields[0]);
			return null;
		}
		AssociatedStreetHouseNumber houseNumber = new AssociatedStreetHouseNumber();
		if (!isEmptyField(fields, 1, false)) {
			houseNumber.setRelationID(fields[1].trim());
		}
		if (!isEmptyField(fields, 2, false)) {
			Matcher matcher = ASSOCIATED_HOUSE_NUMBER_PATTERN.matcher(fields[2].trim());
			int i = 0;
			while (matcher.find()) {
				AssociatedStreetMember member = new AssociatedStreetMember();
				if (matcher.groupCount() != 11) {
					logger.error("parseAssociatedStreetHouseNumber : wrong number of fields for AssociatedStreetMember no " + i + "for line " + line);
					continue;
				}
				member.setId(matcher.group(1));
				Point point;
				try {
					point = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(matcher.group(2));
				} catch (Exception e) {
					logger.error("parseAssociatedStreetHouseNumber : "+e.getMessage());
					return null;
				}
				if (point == null) {
					logger.error("parseAssociatedStreetHouseNumber : wrong location for AssociatedStreetMember for point n" + i + "for line " + line);
					continue;
				}
				member.setLocation(point);
				if (isNotEmpty(matcher.group(3))){
					member.setHouseNumber(matcher.group(3));
				}
				if (isNotEmpty(matcher.group(4))){
					member.setHouseName(matcher.group(4));
				}
				if (isNotEmpty(matcher.group(5))){
					member.setStreetName(matcher.group(5));
				}
				if (isNotEmpty(matcher.group(6))){
					member.setCity(matcher.group(6));
				}
				if (isNotEmpty(matcher.group(7))){
					member.setZipCode(matcher.group(7));
				}
				if (isNotEmpty(matcher.group(8))){
					member.setSuburb(matcher.group(8));
				}
				
				//we ignore shape 9
				
				member.setType(matcher.group(10));
				member.setRole(matcher.group(11));

				houseNumber.addMember(member);
				i++;
			}

		} else {
			logger.error("associated : null number : "+line);
			return null;
		}
		return houseNumber;
	}

	protected boolean isNotEmpty(String number) {
		return number!=null && !number.trim().equals("");
	}

	protected InterpolationHouseNumber parseInterpolationHouseNumber(String line) {
		/*
		 * I	168365171	1796478450___0___0101000020E61000009A023EE4525350C0959C137B682F38C0______;
		 * 1366275082___1___0101000020E610000068661CD94B5350C0B055270C6F2F38C0______;
		 * 1796453793___2___0101000020E610000038691A144D5350C023ADE75A6A2F38C0___600___;
		 * 1796453794___3___0101000020E6100000F38F6390605350C028A6666A6D2F38C0___698___		even    shape
		 */
		if (line == null || "".equals(line.trim())) {
			logger.error("parseInterpolationHouseNumber : null number : "+line);
			return null;
		}
		String[] fields = line.split("\t");
		if (fields.length < 6 || fields.length > 7) {
			logger.error("parseInterpolationHouseNumber : wrong number of fields for line " + line + " expected 5/6 but was " + fields.length);
			return null;
		}
		if (!"I".equals(fields[0])) {
			logger.error("parseInterpolationHouseNumber : wrong house Number Type for line " + line + " expected 'I' but was " + fields[0]);
			return null;
		}
		InterpolationHouseNumber houseNumber = new InterpolationHouseNumber();
		if (!isEmptyField(fields, 1, false)) {
			houseNumber.setWayId(fields[1].trim());
		}
		if (!isEmptyField(fields, 4, false)) {
			
			try {
				houseNumber.setInterpolationType(InterpolationType.valueOf(fields[4].trim().toLowerCase()));
			} catch (Exception e) {
				logger.error("parseInterpolationHouseNumber : wrong interpolation type "+fields[4]+" : "+line);
				//ignore
			}
		}
		if (!isEmptyField(fields, 3, false)) {
			houseNumber.setStreetName(fields[3].trim());
		}
		if (!isEmptyField(fields, 5, false)) {
			
			try {
				houseNumber.setAddressInclusion(AddressInclusion.valueOf(fields[5].trim().toLowerCase()));
			} catch (Exception e) {
				logger.error("parseInterpolationHouseNumber : wrong address inclusion type "+fields[5]+" : "+line);
				//ignore
			}
		}

		if (!isEmptyField(fields, 2, false)) {
			Matcher matcher = INTERPOLATION_HOUSE_NUMBER_PATTERN.matcher(fields[2].trim());
			int i = 0;
			while (matcher.find()) {
				InterpolationMember member = new InterpolationMember();
				if (matcher.groupCount() != 5) {
					logger.error("parseInterpolationHouseNumber : wrong number of fields for InterpolationMember n" + i + "for line " + line);
					continue;
				}
				member.setId(matcher.group(1));
				// seqId
				String seqIdAsString = matcher.group(2);
				int seqId = 0;
				try {
					seqId = Integer.parseInt(seqIdAsString);
				} catch (NumberFormatException e) {
					logger.error("parseInterpolationHouseNumber : can not convert sequence id "+seqIdAsString+" to integer");
					continue;
				}
				member.setSequenceId(seqId);
				// location
				Point point;
				try {
					point = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(matcher.group(3));
				} catch (Exception e) {
					logger.error(e.getMessage());
					return null;
				}
				if (point == null) {
					logger.error("parseInterpolationHouseNumber : wrong location for InterpolationMember point n" + i + "for line " + line);
					continue;
				}
				member.setLocation(point);

				member.setHouseNumber(matcher.group(4));
				member.setStreetname(matcher.group(5));
				houseNumber.addMember(member);
				i++;
			}
			Collections.sort(houseNumber.getMembers());
		} else {
			logger.error("parseInterpolationHouseNumber : wrong housenumber "+fields[2]+" : "+line);
			return null;
		}
		
		return houseNumber;
	}

	protected NodeHouseNumber parseNodeHouseNumber(String line) {
		//N	598495945	0101000020E61000002D1DBD2BCC2462401E37FC6EBAF042C0	46		Dunscombe Avenue	Glen Waverley	3150	Glen Waverley
		//N	1053493828	0101000020E610000060910486D17250C05D4B6D4ECA753CC0	75	Sandwichs La Estrellita	Estanislao Maldones 6:CITY 7:POSTCODE 8:SUBURB 9:SHAPE
		if (line == null || "".equals(line.trim())) {
			logger.error("parseNodeHouseNumber : null line "+line);
			return null;
		}
		String[] fields = line.split("\t");
		if (fields.length < 7 ) {
			logger.error("parseNodeHouseNumber : wrong number of fields for line " + line + " expected 7 but was " + fields.length);
			return null;
		}
		if (!"N".equals(fields[0]) && !"W".equals(fields[0])) {
			logger.error("parseNodeHouseNumber : wrong house Number Type for line " + line + " expected 'N' or 'w' but was " + fields[0]);
			return null;
		}
		NodeHouseNumber node = new NodeHouseNumber();
		if (!isEmptyField(fields, 1, false)) {
			node.setNodeId(fields[1].trim());
		}
		if (!isEmptyField(fields, 2, false)) {
			Point point;
			try {
				point = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[2].trim());
			} catch (Exception e) {
				logger.error("parseNodeHouseNumber : "+ e.getMessage());
				return null;
			}
			if (point == null) {
				logger.error("parseNodeHouseNumber : wrong location for NodeHouseNumber for point for line " + line);
				return null;
			} else {
				node.setLocation(point);
			}
		}
		if (!isEmptyField(fields, 3, false)) {
			node.setHouseNumber(fields[3].trim());
		}
		if (!isEmptyField(fields, 4, false)) {
			node.setName(fields[4].trim());
		}
		if (!isEmptyField(fields, 5, false)) {
			node.setStreetName(fields[5].trim());
		}
		if (!isEmptyField(fields, 6, false)) {
			node.setCity(fields[6].trim());
		}
		if (!isEmptyField(fields, 7, false)) {
			node.setZipCode(fields[7].trim());
		}
		if (!isEmptyField(fields, 8, false)) {
			node.setSuburb(fields[8].trim());
		}
		//we ignore shape for the moment
		/*if (!isEmptyField(fields, 9, false)) {
			node.setshape(fields[5].trim());
		}*/
		return node;
	}
	

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
		
		if (line.startsWith("A")){
			AssociatedStreetHouseNumber house = parseAssociatedStreetHouseNumber(line);
			if (house!=null){
				processAssociatedStreet(house);
			} else {
				logger.error("can not parse associated for "+line);
			}
		} else if (line.startsWith("N")){
			NodeHouseNumber house = parseNodeHouseNumber(line);
			if (house!=null){
				processNodeHouseNumber(house);
			}else {
				logger.error("can not parse node for "+line);
			}
		}else if (line.startsWith("W")){
			NodeHouseNumber house = parseNodeHouseNumber(line);
			if (house!=null){
				processNodeHouseNumber(house);
			}else {
				logger.error("can not parse way for "+line);
			}
		}  else if (line.startsWith("I")) {
			InterpolationHouseNumber house = parseInterpolationHouseNumber(line);
			if(house==null){
				logger.error("can not parse interpolation for "+line);
				return;
			}
			List<InterpolationMember> members = house.getMembers();
			if (members.size() <= 1) {
				//we can not interpolate if there is less than 2 points
				logger.error("parseInterpolationHouseNumber : can not interpolate if there is less than two points for " + line);
				return;
			}
			OpenStreetMap osm = null;
			if (house.getStreetName() != null && !"".equals(house.getStreetName().trim()) && !"\"\"".equals(house.getStreetName().trim())) {
				osm = findNearestStreet(house.getStreetName(), members.get(0).getLocation());
				if (osm == null) {
					logger.error("parseInterpolationHouseNumber : can not find street for name "+house.getStreetName()+", position :"+ members.get(0).getLocation());
					return;// we don't know which street to add the numbers
				}
			} else {
				logger.error("parseInterpolationHouseNumber : streetname is null for "+line);
				return;
			}
			List<HouseNumber> houseNumbers = processInterpolationHouseNumber(house);
			if (houseNumbers.size()!=0){
				osm.addHouseNumbers(houseNumbers);
				saveOsm(osm);
			} else {
				logger.error("parseInterpolationHouseNumber : no housenumberFound");
			}
		} else {
			logger.error("unknow node type for line " + line);
		}

	}

	protected void processAssociatedStreet(AssociatedStreetHouseNumber house) {
		if (house==null){
			logger.error("processAssociatedStreet : AssociatedStreetHouseNumber is null");
			return;
		}
		List<AssociatedStreetMember> streetMembers = house.getStreetMembers();
		List<AssociatedStreetMember> houseMembers = house.getHouseMembers();
		if (houseMembers.size()==0 ){
			//no streets or no house
			logger.error("processAssociatedStreet : there is no member for associated street "+house);
			return;
		} 
		if (streetMembers.size()==0){
			//street as node, it is often the case when associated with a relation and our script don't manage it so we link it here
			if (houseMembers!=null){
				String streetname = null;
				boolean allHouseHaveTheSameStreetName = true;
				OpenStreetMap street=null;
				for (AssociatedStreetMember houseMember : houseMembers){
					if (streetname==null && houseMember!=null && houseMember.getStreetName()!=null){
						streetname=houseMember.getStreetName();
						street = findNearestStreet(houseMember.getStreetName(),houseMember.getLocation());
						continue;
					}else {
						if (houseMember != null && houseMember.getStreetName()!=null && !houseMember.getStreetName().equals(streetname)){
							allHouseHaveTheSameStreetName=false;
							street=null;
							break;
						}
					}
				}
				
			for (AssociatedStreetMember houseMember : houseMembers){
				if (houseMember.getStreetName()!=null && !"".equals(houseMember.getStreetName().trim()) && houseMember.getLocation()!=null){
					if (!allHouseHaveTheSameStreetName) {//we have to find for each one
						street = findNearestStreet(houseMember.getStreetName(),houseMember.getLocation());
					}
					if (street!=null){
						if (street!=null && houseMember!=null && houseMember.getZipCode()!=null){
							street.setZipCode(houseMember.getZipCode());
						}
						//Long openstreetmapId = street.getOpenstreetmap_id();
						//OpenStreetMap osm = openStreetMapDao.getByOpenStreetMapId(openstreetmapId);
						HouseNumber houseNumber = buildHouseNumberFromAssociatedHouseNumber(houseMember);
						if (houseNumber!=null){
							street.addHouseNumber(houseNumber);
							saveOsm(street);
						}
					} else {
						logger.error("processAssociatedStreet : can not find associated street for name "+houseMember.getStreetName()+", position :"+ houseMember.getLocation());
					}
				}
			}
			}
		}
		else if (streetMembers.size()==1){
			AssociatedStreetMember associatedStreetMember = streetMembers.get(0);
			if (associatedStreetMember.getId()==null){
				logger.error("processAssociatedStreet : associated street "+associatedStreetMember+" has no id");
				return;
			}
			Long idAsLong = null;
			try {
				idAsLong = Long.valueOf(associatedStreetMember.getId());
			} catch (NumberFormatException e) {
				logger.error("processAssociatedStreet  : "+idAsLong+" is not a valid id for associated street");
				return;
			}
			OpenStreetMap associatedStreet = openStreetMapDao.getByOpenStreetMapId(idAsLong);
			if (associatedStreet==null){
				logger.error("processAssociatedStreet  : no street can be found for associated street for id "+idAsLong);
				return;
			}
			for (AssociatedStreetMember houseMember : houseMembers){
				if (houseMember!=null && houseMember.getZipCode()!=null){
					associatedStreet.setZipCode(houseMember.getZipCode());
				}
				HouseNumber houseNumber = buildHouseNumberFromAssociatedHouseNumber(houseMember);
				if (houseNumber!=null){
					associatedStreet.addHouseNumber(houseNumber);
				}
			}
			saveOsm(associatedStreet);
		}
		else if (streetMembers.size()>1){
			//for each house, search the nearest street
			//getStreetIds
			List<Long>  streetIds = new ArrayList<Long>();
			for (AssociatedStreetMember street : streetMembers){
				Long id;
				try {
					id = Long.valueOf(street.getId());
					streetIds.add(id);
				} catch (NumberFormatException e) {
					logger.error("processAssociatedStreet : "+street+" has no id");
				}
			}
			for (AssociatedStreetMember houseMember : houseMembers){
				
				if (houseMember!=null && houseMember.getLocation()!=null){
					HouseNumber houseNumber = buildHouseNumberFromAssociatedHouseNumber(houseMember);
				OpenStreetMap associatedStreet = openStreetMapDao.getNearestByosmIds(houseMember.getLocation(), streetIds);
				if (associatedStreet!=null && houseMember!=null && houseMember.getZipCode()!=null){
					associatedStreet.setZipCode(houseMember.getZipCode());
				}
				if (associatedStreet!=null && houseNumber!=null){
					associatedStreet.addHouseNumber(houseNumber);
					saveOsm(associatedStreet);
				} else {
					
					logger.error("processAssociatedStreet : associated street "+associatedStreet+", or house numer "+houseNumber+" is null");
				}
				}
			}
			
		}
	}

	protected HouseNumber processNodeHouseNumber(NodeHouseNumber house) {
		if(house==null || house.getLocation()==null){
			return null;
		}
		HouseNumber houseNumber = new HouseNumber();
		houseNumber.setNumber(normalizeNumber(house.getHouseNumber()));
		houseNumber.setName(house.getName());
		houseNumber.setSource(GISSource.OSM);
		houseNumber.setType(HouseNumberType.NODE);
		try {
			Long id = Long.valueOf(house.getNodeId());
			houseNumber.setOpenstreetmapId(id);
		} catch (NumberFormatException e) {
			logger.error("processNodeHouseNumber : can not parse openstreetmapid for house : "+house);
			//ignore
		}
		Point location = house.getLocation();
		houseNumber.setLocation(location);
		OpenStreetMap osm = findNearestStreet(house.getStreetName(),location);
		if (osm!=null){
					try {
						osm.addHouseNumber(houseNumber);
						if (house.getZipCode()!=null && osm.getIsInZip()!=null){
							//we override even if it is already present because it is a set
							osm.addIsInZip(house.getZipCode());
							osm.setZipCode(house.getZipCode());
						}
						if (house.getCity()!= null && !osm.isCityConfident()){//we override if it not cityConfident 
							osm.setIsIn(house.getCity());
						}
						if (house.getSuburb()!= null){//we override if it not cityConfident 
							osm.setIsInPlace(house.getSuburb());
						}
						saveOsm(osm);
					} catch (Exception e) {
						logger.error("processNodeHouseNumber : error processing node housenumber, we ignore it but you should consider it : "+ e.getMessage(),e);
					}
					return houseNumber;
		} else {
			logger.error("processNodeHouseNumber : can not find node street for name "+house.getStreetName()+", position :"+ location+ " for "+house);
		}
		return null;
	}

	protected void saveOsm(OpenStreetMap osm) {
		openStreetMapDao.save(osm);
	}

	protected List<HouseNumber> processInterpolationHouseNumber(InterpolationHouseNumber house) {
			//the results
			List<HouseNumber> houseNumbers = new ArrayList<HouseNumber>();
			boolean multipleInterpolation = false;//boolean to indicate that we 
			//interpolate several segment, so we should not add the N2 point
			//cause it has already been added by last interpolation
			//N1--------N2-------N3
		
			List<InterpolationMember> membersForSegmentation = new ArrayList<InterpolationMember>();
			List<InterpolationMember> members = house.getMembers();
			if (members != null) {
				for (InterpolationMember member : members) {
					if (member.getHouseNumber() != null
							&& !"".equals(member.getHouseNumber().trim())) {
						// got HN in the member
						membersForSegmentation.add(member);
						if (membersForSegmentation.size() == 1) {
							continue;// we only have one point and need at
										// least one other
						} else {
							int nbInnerPoint = 0;
							int increment = 1;
							if (house.getInterpolationType() == InterpolationType.alphabetic) {
								//WE choose to ignore Alphabetic interpolation due to poor interest
							} else {
								// odd,even,all=>should be numeric
								String firstNumberAsString = membersForSegmentation
										.get(0).getHouseNumber();
								String lastNumberAsString = membersForSegmentation
										.get(membersForSegmentation.size()-1).getHouseNumber();
								int firstNumberAsInt = 0;
								int lastNumberAsInt = 0;
								try {
									firstNumberAsInt = Integer
											.parseInt(normalizeNumber(firstNumberAsString));
									lastNumberAsInt = Integer
											.parseInt(normalizeNumber(lastNumberAsString));
								} catch (NumberFormatException e) {
									logger.error("processInterpolationHouseNumber : interpolation house number "+firstNumberAsString+" and/or "+ lastNumberAsString +"are not numbers");
									return houseNumbers;
								}
								if (house.getInterpolationType() == InterpolationType.even) {// pair
									if (firstNumberAsInt % 2 == 1) {
										firstNumberAsInt++;
										membersForSegmentation.get(0).setHouseNumber(firstNumberAsInt+"");
									}
									if (lastNumberAsInt % 2 == 1) {
										lastNumberAsInt++;
										membersForSegmentation.get(membersForSegmentation.size()-1).setHouseNumber(lastNumberAsInt+"");
									}
									// two even number substracts always give an odd one
									nbInnerPoint = Math
											.abs((firstNumberAsInt - lastNumberAsInt) / 2) - 1;
									if (firstNumberAsInt < lastNumberAsInt){
										increment = 2;
									} else {
										increment = -2;
									}

								} else if (house.getInterpolationType() == InterpolationType.odd) {// impair
									if (firstNumberAsInt % 2 == 0) {
										firstNumberAsInt++;
										membersForSegmentation.get(0).setHouseNumber(firstNumberAsInt+"");
									}
									if (lastNumberAsInt % 2 == 0) {
										lastNumberAsInt++;
										membersForSegmentation.get(membersForSegmentation.size()-1).setHouseNumber(lastNumberAsInt+"");
									}
									nbInnerPoint = Math
											.abs((firstNumberAsInt - lastNumberAsInt) / 2) - 1;
									if (firstNumberAsInt < lastNumberAsInt){
										increment = 2;
									} else {
										increment = -2;
									}
									

								} else if (house.getInterpolationType() == InterpolationType.all) {
									nbInnerPoint = Math
											.abs((firstNumberAsInt - lastNumberAsInt)) - 1;
									if (firstNumberAsInt < lastNumberAsInt){
										increment = 1;
									} else {
										increment = -1;
									}

								}
								List<Point> points = new ArrayList<Point>(membersForSegmentation.size());
								for (InterpolationMember memberForSegmentation : membersForSegmentation) {
									points.add(memberForSegmentation
											.getLocation());
								}
								List<Point> segmentizedPoint = segmentize(
										points, nbInnerPoint);
								
								for (int i =0;i<segmentizedPoint.size();i++){
									if (i==0 && multipleInterpolation){
										continue;//this point has already been added by previous interpolation
									}
									HouseNumber houseNumberToAdd = new HouseNumber();
									//set the openstretmapid of the first point
									if ((i==0 && membersForSegmentation.get(0)!= null && membersForSegmentation.get(0).getId()!=null)){
										try {
											Long id = Long.valueOf(membersForSegmentation.get(i).getId());
											houseNumberToAdd.setOpenstreetmapId(id);
										} catch (NumberFormatException e) {
											//ignore
										}
									}
									//set the openstretmapid of the last point
									if ((i==(segmentizedPoint.size()-1) && membersForSegmentation.get(1)!= null && membersForSegmentation.get(1).getId()!=null)){
										try {
											Long id = Long.valueOf(membersForSegmentation.get(1).getId());
											houseNumberToAdd.setOpenstreetmapId(id);
										} catch (NumberFormatException e) {
											//ignore
										}
									}
									Point p = segmentizedPoint.get(i);
									houseNumberToAdd.setType(HouseNumberType.INTERPOLATION);
									houseNumberToAdd.setLocation(p);
									houseNumberToAdd.setNumber(firstNumberAsInt+(increment*i)+"");
									houseNumberToAdd.setSource(GISSource.OSM);
									houseNumbers.add(houseNumberToAdd);
								}
								//return houseNumbers;
								
							}
							

							membersForSegmentation = new ArrayList<InterpolationMember>();
							multipleInterpolation=true;
							// restart the process with the last point;
							membersForSegmentation.add(member);
						}
					} else {
						// no housenumber in the members, it is a point to draw a street
						if (membersForSegmentation.size() == 0) {
							// we go to the next point to search for a point with HN
							continue;
						} else {
							// we add the member and continue to search for a point with HN;
							membersForSegmentation.add(member);
						}
					}

				}
			}
			return houseNumbers;
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
		return osm;
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

	protected OpenStreetMap getNearestByGIds(List<SolrResponseDto> results,Point point,String streetname) {
		if (results == null || results.size()==0){
			return null;
		}
		List<Long> ids = new ArrayList<Long>();
		/*List<SolrResponseDto> filteredlist = new ArrayList<SolrResponseDto>();
		if (streetname != null){
			for (SolrResponseDto dto:results){
				if(dto!=null && dto.getName()!=null && StringHelper.isSameStreetName(streetname,dto.getName(),dto.getCountry_code())){
					filteredlist.add(dto);
				} else if (dto!= null && dto.getName_alternates()!=null){
					for (String altName:dto.getName_alternates()){
						if (altName!=null && StringHelper.isSameStreetName(streetname, altName, dto.getCountry_code())){
							filteredlist.add(dto);
						}
					}
				}
			}
		} else {
			filteredlist = results;
		}*/
		List<SolrResponseDto> filteredlist = results;
		String idsAsSTring="{";
		for (SolrResponseDto dto:results){
			if (dto!=null){
				idsAsSTring = idsAsSTring+","+dto.getFeature_id();
			}
		}
		idsAsSTring+="}";
		//logger.error("getNearestByIds for "+streetname+" have "+idsAsSTring+" ids and filtered has "+filteredlist.size() +"id");
		OpenStreetMap result = null;
		if (filteredlist !=null && !filteredlist.isEmpty()){
			for (SolrResponseDto dto:filteredlist){
				if (dto!=null && dto.getFeature_id()!=null){
					ids.add(dto.getFeature_id());
				}
			}
			result = openStreetMapDao.getNearestByGIds(point, ids);
			 idsAsSTring="{";
			for (Long id:ids){
				idsAsSTring = idsAsSTring+","+id;
			}
			idsAsSTring+="}";
			if (result==null){
				
				//logger.error("getNearestByIds for "+streetname+" and  ids "+idsAsSTring+" and point" +point+" return  "+result);
			}
			/*float score = -1;
			for (SolrResponseDto dto:results){
				if (dto!=null && result!=null && dto.getFeature_id()==result.getGid()){
					score=dto.getScore();
					break;
				}
			}*/
			//logger.error("getNearestByIds for "+streetname+" and  ids "+idsAsSTring+" and point" +point+" return  score="+score+" and "+result);
		}
		return result;
		/*SolrResponseDto candidate=null;
		if (results!=null ){
			float score =0;
			Double smallestDistance = 0D;
			for (SolrResponseDto dto: results){
				if (score==0){//initialize with first element
					score= dto.getScore();
					smallestDistance = GeolocHelper.distance(GeolocHelper.createPoint(dto.getLng(), dto.getLat()), point);
					candidate=dto;
					continue;
				}
				if ((Math.abs(dto.getScore()-score)*100/score) < SCORE_THRESOLD){//the score is very close => < X%
					if (GeolocHelper.distance(GeolocHelper.createPoint(dto.getLng(), dto.getLat()), point) < smallestDistance){//if the dto is closer, we keep it
						candidate = dto;
					}
				} else {//score is too far, and results are sorted by score, we return the candidate
					return candidate;
				}
			}
		}
		return candidate;
		*/
	}

	protected HouseNumber buildHouseNumberFromAssociatedHouseNumber(
			AssociatedStreetMember houseMember) {
		HouseNumber houseNumber = new HouseNumber();
		houseNumber.setSource(GISSource.OSM);
		if (houseMember.getLocation()!=null){//it is a mandatory field
			houseNumber.setLocation(houseMember.getLocation());
		} else {
			logger.error("buildHouseNumberFromAssociatedHouseNumber : no location found for "+houseMember);
			return null;
		}
		houseNumber.setNumber(normalizeNumber(houseMember.getHouseNumber()));//todo normalize 11 d
		Long osmId = null;
		try {
			osmId = Long.valueOf(houseMember.getId());
			houseNumber.setOpenstreetmapId(osmId);
		} catch (NumberFormatException e) {
			logger.error("buildHouseNumberFromAssociatedHouseNumber" +osmId+" is not a valid openstreetmapId");
		}
		houseNumber.setType(HouseNumberType.ASSOCIATED);
		return houseNumber;
	}
	
		
	/**
	 * @param points a list of point, typically a list of point that represent a street
	 * @param nbInnerPoint the number of point to add beetween the startpoint and the endpoint
	 * @return the intermediate points that represents segments of same size, if nbInnerPoint=4, 
	 * we will get 6 points back
	 */
	protected List<Point> segmentize(List<Point> points,int nbInnerPoint){
		List<Point> result = new ArrayList<Point>();
		if (points==null || nbInnerPoint<=0 || points.size()==0){
			return result;
		}
		if (points.size()>=1){
			result.add(points.get(0));
			
		}
		if (points.size()==1){
			return result;
		}
		List<Double> distances = new ArrayList<Double>();//5/10/15
		double totalDistance = 0;//30
		for (int i=0;i<points.size()-1;i++){
			double distance = GeolocHelper.distance(points.get(i), points.get(i+1));
			distances.add(distance);
			totalDistance+=distance;
		}
		int nbSegment = nbInnerPoint+1;//4+1
		int nbpoints = nbInnerPoint+1;//4+1
		double segmentLength = totalDistance/nbSegment;//30/5=6
		int currentSubSegment =1;
		double currentSubLength=0;
		for (int i=1;i<=nbpoints;i++){
			while(currentSubLength+distances.get(currentSubSegment-1) <i*segmentLength){
				currentSubLength+=distances.get(currentSubSegment-1);
				currentSubSegment++;
				if (currentSubSegment>distances.size()){
					return result;
				}
			}
			double distanceLeft = (i*segmentLength)-currentSubLength;
			double ratio = distanceLeft/(distances.get(currentSubSegment-1));
			if (ratio <= 0){
				//it is the last point
				result.add(points.get(points.size()-1));
			} else {
				//we put it at the correct ratio
				Point p1=points.get(currentSubSegment-1);
				Point p2=points.get(currentSubSegment);
				Double lng = (p2.getX()-p1.getX())*ratio+p1.getX();
				Double lat = (p2.getY()-p1.getY())*ratio+p1.getY();
				Point p = GeolocHelper.createPoint(lng.floatValue(), lat.floatValue());
				result.add(p);
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
		return !importerConfig.isOpenstreetmapHouseNumberImporterEnabled();
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
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
	 */
	public List<NameValueDTO<Integer>> rollback() {
		List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
		logger.info("reseting  house number importer...");
		int deleted = houseNumberDao.deleteAll();
		if (deleted != 0) {
			deletedObjectInfo.add(new NameValueDTO<Integer>(houseNumberDao.getPersistenceClass().getSimpleName(), deleted));
		}
		logger.info(deleted + " house number entities have been deleted");
		resetStatus();
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
