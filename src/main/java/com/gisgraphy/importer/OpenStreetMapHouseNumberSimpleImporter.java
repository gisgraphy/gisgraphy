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
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.importer.dto.AddressInclusion;
import com.gisgraphy.importer.dto.AssociatedStreetHouseNumber;
import com.gisgraphy.importer.dto.AssociatedStreetMember;
import com.gisgraphy.importer.dto.InterpolationHouseNumber;
import com.gisgraphy.importer.dto.InterpolationMember;
import com.gisgraphy.importer.dto.InterpolationType;
import com.gisgraphy.importer.dto.NodeHouseNumber;
import com.gisgraphy.service.ServiceException;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the street from an (pre-processed) openStreet map data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OpenStreetMapHouseNumberSimpleImporter extends AbstractSimpleImporterProcessor {


	protected static final Logger logger = LoggerFactory.getLogger(OpenStreetMapHouseNumberSimpleImporter.class);

	protected IOpenStreetMapDao openStreetMapDao;

	protected IhouseNumberDao houseNumberDao;

	protected ISolRSynchroniser solRSynchroniser;

	protected IFullTextSearchEngine fullTextSearchEngine;

	private static final String ASSOCIATED_HOUSE_NUMBER_REGEXP = "([0-9]+)___([^_]*)___((?:(?!___).)*)___((?:(?!___).)*)___([NW])___([^_]*)(?:___)?";

	private static final String INTERPOLATION_HOUSE_NUMBER_REGEXP = "([0-9]+)___([0-9])___((?:(?!___).)+)*___((?:(?!___).)+)*___((?:(?!___).)+)*(?:___)?";

	private static final Pattern ASSOCIATED_HOUSE_NUMBER_PATTERN = Pattern.compile(ASSOCIATED_HOUSE_NUMBER_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern INTERPOLATION_HOUSE_NUMBER_PATTERN = Pattern.compile(INTERPOLATION_HOUSE_NUMBER_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	
	protected final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);

	protected static final double SEARCH_DISTANCE = 6000;

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear
	 * ()
	 */
	@Override
	protected void flushAndClear() {
		openStreetMapDao.flushAndClear();
		houseNumberDao.flushAndClear();

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
		 * "47129758___0101000020E61000005CCBD3C231E76240AA6514FE5BF440C0___Bowral Street___Bowral Street___W___street"
		 * ", ""84623507
		 * ___0101000020E6100000546690CC36E76240A417D5545AF440C0___71___Bowral
		 * Street___W___house""}"
		 */
		if (line == null || "".equals(line.trim())) {
			return null;
		}
		String[] fields = line.split("\t");
		if (fields.length != 3) {
			logger.warn("wrong number of fields for line " + line + " expected 3 but was " + fields.length);
			return null;
		}
		if (!"A".equals(fields[0])) {
			logger.warn("wrong house Number Type for line " + line + " expected 'A' but was " + fields[0]);
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
				if (matcher.groupCount() != 6) {
					logger.warn("wrong number of fields for AssociatedStreetMember no " + i + "for line " + line);
					continue;
				}
				member.setId(matcher.group(1));
				Point point;
				try {
					point = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(matcher.group(2));
				} catch (Exception e) {
					logger.warn(e.getMessage());
					return null;
				}
				if (point == null) {
					logger.warn("wrong location for AssociatedStreetMember for point n" + i + "for line " + line);
					continue;
				}
				member.setLocation(point);
				String role = matcher.group(6);
				member.setRole(role);
				member.setHouseNumber(matcher.group(3));
				member.setStreetName(matcher.group(4));
				member.setType(matcher.group(5));

				houseNumber.addMember(member);
				i++;
			}

		} else {
			return null;
		}
		return houseNumber;
	}

	protected InterpolationHouseNumber parseInterpolationHouseNumber(String line) {
		/*
		 * I	168365171	1796478450___0___0101000020E61000009A023EE4525350C0959C137B682F38C0______;
		 * 1366275082___1___0101000020E610000068661CD94B5350C0B055270C6F2F38C0______;
		 * 1796453793___2___0101000020E610000038691A144D5350C023ADE75A6A2F38C0___600___;
		 * 1796453794___3___0101000020E6100000F38F6390605350C028A6666A6D2F38C0___698___		even
		 */
		if (line == null || "".equals(line.trim())) {
			return null;
		}
		String[] fields = line.split("\t");
		if (fields.length < 5 || fields.length > 6) {
			logger.warn("wrong number of fields for line " + line + " expected 5/6 but was " + fields.length);
			return null;
		}
		if (!"I".equals(fields[0])) {
			logger.warn("wrong house Number Type for line " + line + " expected 'A' but was " + fields[0]);
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
				//ignore
			}
		}

		if (!isEmptyField(fields, 2, false)) {
			Matcher matcher = INTERPOLATION_HOUSE_NUMBER_PATTERN.matcher(fields[2].trim());
			int i = 0;
			while (matcher.find()) {
				InterpolationMember member = new InterpolationMember();
				if (matcher.groupCount() != 5) {
					logger.warn("wrong number of fields for InterpolationMember n" + i + "for line " + line);
					continue;
				}
				member.setId(matcher.group(1));
				// seqId
				String seqIdAsString = matcher.group(2);
				int seqId = 0;
				try {
					seqId = Integer.parseInt(seqIdAsString);
				} catch (NumberFormatException e) {
					logger.warn("can not convert sequence id "+seqIdAsString+" to integer");
					continue;
				}
				member.setSequenceId(seqId);
				// location
				Point point;
				try {
					point = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(matcher.group(3));
				} catch (Exception e) {
					logger.warn(e.getMessage());
					return null;
				}
				if (point == null) {
					logger.warn("wrong location for InterpolationMember point n" + i + "for line " + line);
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
			return null;
		}
		
		return houseNumber;
	}

	protected NodeHouseNumber parseNodeHouseNumber(String line) {
		//N	1053493828	0101000020E610000060910486D17250C05D4B6D4ECA753CC0	75	Sandwichs La Estrellita	Estanislao Maldones
		if (line == null || "".equals(line.trim())) {
			return null;
		}
		String[] fields = line.split("\t");
		if (fields.length < 4 ) {
			logger.warn("wrong number of fields for line " + line + " expected 4 but was " + fields.length);
			return null;
		}
		if (!"N".equals(fields[0]) && !"W".equals(fields[0])) {
			logger.warn("wrong house Number Type for line " + line + " expected 'N' or 'w' but was " + fields[0]);
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
				logger.warn(e.getMessage());
				return null;
			}
			if (point == null) {
				logger.warn("wrong location for NodeHouseNumber for point for line " + line);
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
			}
		} else if (line.startsWith("N")){
			NodeHouseNumber house = parseNodeHouseNumber(line);
			if (house!=null){
				processNodeHouseNumber(house);
			}
		}else if (line.startsWith("W")){
			NodeHouseNumber house = parseNodeHouseNumber(line);
			if (house!=null){
				processNodeHouseNumber(house);
			}
		}  else if (line.startsWith("I")) {
			InterpolationHouseNumber house = parseInterpolationHouseNumber(line);
			if(house==null){
				return;
			}
			List<InterpolationMember> members = house.getMembers();
			if (members.size() <= 1) {
				//we can not interpolate if there is less than 2 points
				logger.warn("can not interpolate if there is less than two points for " + line);
				return;
			}
			OpenStreetMap osm = null;
			if (house.getStreetName() != null && !"".equals(house.getStreetName().trim()) && !"\"\"".equals(house.getStreetName().trim())) {
				osm = findNearestStreet(house.getStreetName(), members.get(0).getLocation());
				if (osm == null) {
					logger.warn("can not find street for name "+house.getStreetName()+", position :"+ members.get(0).getLocation());
					return;// we don't know which street to add the numbers
				}
			} else {
				return;
			}
			List<HouseNumber> houseNumbers = processInterpolationHouseNumber(house);
			if (houseNumbers.size()!=0){
				osm.addHouseNumbers(houseNumbers);
				openStreetMapDao.save(osm);
			}
		} else {
			logger.warn("unknow node type for line " + line);
		}

	}

	protected void processAssociatedStreet(AssociatedStreetHouseNumber house) {
		if (house==null){
			return;
		}
		List<AssociatedStreetMember> streetMembers = house.getStreetMembers();
		List<AssociatedStreetMember> houseMembers = house.getHouseMembers();
		if (houseMembers.size()==0 ){
			//no streets or no house
			logger.warn("there is no member for associated street "+house);
			return;
		} 
		if (streetMembers.size()==0){
			//treet as node, it is often the case when associated with a relation and our script don't manage it so we link it here
			if (houseMembers!=null){
				String streetname = null;
				boolean allHouseHaveTheSameStreetName = true;
				OpenStreetMap street=null;
				for (AssociatedStreetMember houseMember : houseMembers){
					if (streetname==null && houseMember.getStreetName()!=null){
						streetname=houseMember.getStreetName();
						street = findNearestStreet(houseMember.getStreetName(),houseMember.getLocation());
						continue;
					}else {
						if (!houseMember.getStreetName().equals(streetname)){
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
						//Long openstreetmapId = street.getOpenstreetmap_id();
						//OpenStreetMap osm = openStreetMapDao.getByOpenStreetMapId(openstreetmapId);
						HouseNumber houseNumber = buildHouseNumberFromAssociatedHouseNumber(houseMember);
						if (houseNumber!=null){
							street.addHouseNumber(houseNumber);
							openStreetMapDao.save(street);
						}
					} else {
						logger.warn("can not find associated street for name "+houseMember.getStreetName()+", position :"+ houseMember.getLocation());
					}
				}
			}
			}
		}
		else if (streetMembers.size()==1){
			AssociatedStreetMember associatedStreetMember = streetMembers.get(0);
			if (associatedStreetMember.getId()==null){
				logger.warn("associated street "+associatedStreetMember+" has no id");
				return;
			}
			Long idAsLong = null;
			try {
				idAsLong = Long.valueOf(associatedStreetMember.getId());
			} catch (NumberFormatException e) {
				logger.warn(idAsLong+" is not a valid id for associated street");
				return;
			}
			OpenStreetMap associatedStreet = openStreetMapDao.getByOpenStreetMapId(idAsLong);
			if (associatedStreet==null){
				logger.warn("no street can be found for associated street for id "+idAsLong);
				return;
			}
			for (AssociatedStreetMember houseMember : houseMembers){
				HouseNumber houseNumber = buildHouseNumberFromAssociatedHouseNumber(houseMember);
				if (houseNumber!=null){
					associatedStreet.addHouseNumber(houseNumber);
				}
			}
			openStreetMapDao.save(associatedStreet);
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
					logger.warn(street+" has no id");
				}
			}
			for (AssociatedStreetMember houseMember : houseMembers){
				if (houseMember!=null && houseMember.getLocation()!=null){
					HouseNumber houseNumber = buildHouseNumberFromAssociatedHouseNumber(houseMember);
				OpenStreetMap associatedStreet = openStreetMapDao.getNearestByosmIds(houseMember.getLocation(), streetIds);
				if (associatedStreet!=null && houseNumber!=null){
					associatedStreet.addHouseNumber(houseNumber);
					openStreetMapDao.save(associatedStreet);
				} else {
					
					logger.warn("associated street "+associatedStreet+", or house numer "+houseNumber+" is null");
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
		houseNumber.setType(HouseNumberType.NODE);
		try {
			Long id = Long.valueOf(house.getNodeId());
			houseNumber.setOpenstreetmapId(id);
		} catch (NumberFormatException e) {
			//ignore
		}
		Point location = house.getLocation();
		houseNumber.setLocation(location);
		OpenStreetMap osm = findNearestStreet(house.getStreetName(),location);
		if (osm!=null){
					try {
						osm.addHouseNumber(houseNumber);
						openStreetMapDao.save(osm);
					} catch (Exception e) {
						logger.error("error processing node housenumber, we ignore it but you should consider it : "+ e.getMessage(),e);
					}
					return houseNumber;
		} else {
			logger.warn("can not find node street for name "+house.getStreetName()+", position :"+ location+ " for "+house);
		}
		return null;
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
									logger.warn("interpolation house number "+firstNumberAsString+" and/or "+ lastNumberAsString+"are not numbers");
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
		if (location == null){
			return null;
		}
		if (streetName==null || "".equals(streetName.trim()) || "\"\"".equals(streetName.trim()) ){
			return openStreetMapDao.getNearestFrom(location);
		}
		FulltextQuery query;
		try {
			query = new FulltextQuery(streetName, Pagination.DEFAULT_PAGINATION, MEDIUM_OUTPUT, 
					com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, null);
		} catch (IllegalArgumentException e) {
			logger.error("can not create a fulltext query for "+streetName+", will return the nearest");
			return openStreetMapDao.getNearestFrom(location);
		}
		query.withAllWordsRequired(false).withoutSpellChecking();
		query.around(location);
			query.withRadius(SEARCH_DISTANCE);
		FulltextResultsDto results;
		try {
			results = fullTextSearchEngine.executeQuery(query);
		} catch (RuntimeException e) {
			logger.error("error during fulltext search : "+e.getMessage(),e);
			return null;
		}
		int resultsSize = results.getResultsSize();
		List<SolrResponseDto> resultsList = results.getResults();
		if (resultsSize == 1) {
			SolrResponseDto street = resultsList.get(0);
			if (street!=null){
				Long openstreetmapId = street.getOpenstreetmap_id();
				if (openstreetmapId!=null){
					OpenStreetMap osm = openStreetMapDao.getByOpenStreetMapId(openstreetmapId);
					if (osm == null) {
						logger.warn("can not find street for id "+openstreetmapId);
					}
					return osm;
				}
			}
		} if (resultsSize > 1) {
			return getNearestByIds(resultsList,location);
		} else {
			return null;
		}
	}

	protected OpenStreetMap getNearestByIds(List<SolrResponseDto> results,Point point) {
		List<Long> ids = new ArrayList<Long>();
		OpenStreetMap result = null;
		if (results!=null){
			for (SolrResponseDto dto:results){
				if (dto!=null && dto.getOpenstreetmap_id()!=null){
					ids.add(dto.getOpenstreetmap_id());
				}
			}
			result = openStreetMapDao.getNearestByosmIds(point, ids);
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
		if (houseMember.getLocation()!=null){//it is a mandatory field
			houseNumber.setLocation(houseMember.getLocation());
		} else {
			return null;
		}
		houseNumber.setNumber(normalizeNumber(houseMember.getHouseNumber()));//todo normalize 11 d
		Long osmId = null;
		try {
			osmId = Long.valueOf(houseMember.getId());
			houseNumber.setOpenstreetmapId(osmId);
		} catch (NumberFormatException e) {
			logger.warn(osmId+" is not a valid openstreetmapId");
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
		this.statusMessage = internationalisationService.getString("import.fulltext.optimize");
		solRSynchroniser.optimize();
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
