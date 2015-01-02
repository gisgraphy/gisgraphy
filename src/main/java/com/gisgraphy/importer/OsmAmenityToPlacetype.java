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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gisgraphy.domain.geoloc.entity.ATM;
import com.gisgraphy.domain.geoloc.entity.AdmBuilding;
import com.gisgraphy.domain.geoloc.entity.Airport;
import com.gisgraphy.domain.geoloc.entity.AmusePark;
import com.gisgraphy.domain.geoloc.entity.Bank;
import com.gisgraphy.domain.geoloc.entity.Bar;
import com.gisgraphy.domain.geoloc.entity.Bench;
import com.gisgraphy.domain.geoloc.entity.BusStation;
import com.gisgraphy.domain.geoloc.entity.Camping;
import com.gisgraphy.domain.geoloc.entity.Casino;
import com.gisgraphy.domain.geoloc.entity.Castle;
import com.gisgraphy.domain.geoloc.entity.Cemetery;
import com.gisgraphy.domain.geoloc.entity.Cinema;
import com.gisgraphy.domain.geoloc.entity.CityHall;
import com.gisgraphy.domain.geoloc.entity.CourtHouse;
import com.gisgraphy.domain.geoloc.entity.Craft;
import com.gisgraphy.domain.geoloc.entity.Dentist;
import com.gisgraphy.domain.geoloc.entity.Doctor;
import com.gisgraphy.domain.geoloc.entity.EmergencyPhone;
import com.gisgraphy.domain.geoloc.entity.Farm;
import com.gisgraphy.domain.geoloc.entity.FerryTerminal;
import com.gisgraphy.domain.geoloc.entity.FireStation;
import com.gisgraphy.domain.geoloc.entity.FishingArea;
import com.gisgraphy.domain.geoloc.entity.Fountain;
import com.gisgraphy.domain.geoloc.entity.Fuel;
import com.gisgraphy.domain.geoloc.entity.Garden;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.Golf;
import com.gisgraphy.domain.geoloc.entity.Hospital;
import com.gisgraphy.domain.geoloc.entity.Hotel;
import com.gisgraphy.domain.geoloc.entity.House;
import com.gisgraphy.domain.geoloc.entity.Ice;
import com.gisgraphy.domain.geoloc.entity.Library;
import com.gisgraphy.domain.geoloc.entity.Mill;
import com.gisgraphy.domain.geoloc.entity.Museum;
import com.gisgraphy.domain.geoloc.entity.NightClub;
import com.gisgraphy.domain.geoloc.entity.ObservatoryPoint;
import com.gisgraphy.domain.geoloc.entity.Park;
import com.gisgraphy.domain.geoloc.entity.Parking;
import com.gisgraphy.domain.geoloc.entity.Pharmacy;
import com.gisgraphy.domain.geoloc.entity.Picnic;
import com.gisgraphy.domain.geoloc.entity.PolicePost;
import com.gisgraphy.domain.geoloc.entity.Port;
import com.gisgraphy.domain.geoloc.entity.PostOffice;
import com.gisgraphy.domain.geoloc.entity.Prison;
import com.gisgraphy.domain.geoloc.entity.Rail;
import com.gisgraphy.domain.geoloc.entity.RailRoadStation;
import com.gisgraphy.domain.geoloc.entity.Religious;
import com.gisgraphy.domain.geoloc.entity.Rental;
import com.gisgraphy.domain.geoloc.entity.Reserve;
import com.gisgraphy.domain.geoloc.entity.Restaurant;
import com.gisgraphy.domain.geoloc.entity.School;
import com.gisgraphy.domain.geoloc.entity.Shop;
import com.gisgraphy.domain.geoloc.entity.Sport;
import com.gisgraphy.domain.geoloc.entity.Stadium;
import com.gisgraphy.domain.geoloc.entity.SwimmingPool;
import com.gisgraphy.domain.geoloc.entity.Taxi;
import com.gisgraphy.domain.geoloc.entity.Telephone;
import com.gisgraphy.domain.geoloc.entity.Theater;
import com.gisgraphy.domain.geoloc.entity.Toilet;
import com.gisgraphy.domain.geoloc.entity.Tourism;
import com.gisgraphy.domain.geoloc.entity.TourismInfo;
import com.gisgraphy.domain.geoloc.entity.VendingMachine;
import com.gisgraphy.domain.geoloc.entity.Veterinary;
import com.gisgraphy.domain.geoloc.entity.Zoo;


/**
 * associates the amenity tag of osm to a palcetype. 
 * list of values based on http://taginfo.openstreetmap.org/keys/?key=amenity#values
 * 
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class OsmAmenityToPlacetype {

	public static int TAGS_ARRAY_SIZE = 14;

	public final static String  DEFAULT_OSM_FEATURE_CODE= "UNK";
	public final static String  DEFAULT_OSM_FEATURE_CLASS= "UNK";
	
	boolean isNonRealTag(String tag) {
		if (tag == null || "".equals(tag.trim()) || "yes".equalsIgnoreCase(tag) ||  "no".equalsIgnoreCase(tag)  ||  "fixme".equalsIgnoreCase(tag) ){
			return true;
		} 
		return false;
	}

	public List<GisFeature> getObjectsFromTags(String[] tags){
		List<GisFeature> objects = new ArrayList<GisFeature>();
		if (tags == null){
			return objects;
		}
		if (tags.length!= TAGS_ARRAY_SIZE){
			throw new RuntimeException("tags array has not the correct size expected "+TAGS_ARRAY_SIZE+" but was "+tags.length+" :" +Arrays.toString(tags));//pqp
		}
		//0: amenity, 1: aeroway,2 : building,3: craft, 4 : historic, 5: leisure,6 : man_made,7 : office, 8 : railway,9 :tourism,10 : shop,11 : sport,12 :landuse,13 : highway
		GisFeature o = getRailwayObject(tags[8]);
		if (o!=null){
			objects.add(o);
			return objects;//railway is only a railway pqp
		}
		o = getAerowayObject(tags[1]);
		if (o!=null){
			objects.add(o);
			return objects;//aeroway is only a railway pqp
		}
		Set<String> placeTypes = new HashSet<String>();
		o = getAmenityObject(tags[0]);
		if (o!=null){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getBuildingObject(tags[2]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getCraftObject(tags[3]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getHistoricObject(tags[4]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		//if leisure and !sport or !sport[pitch|sports_center]
		if (tags[5]!=null && 
				(tags[12]==null ||
				(tags[12]!=null && !("pitch".equals(tags[5].trim().toLowerCase()) || "sports_center".equals(tags[5].trim().toLowerCase())))
				)
			){
			o = getLeisureObject(tags[5]);
			if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
				objects.add(o);
				placeTypes.add(o.getClass().getSimpleName());
			}
		}
		o = getManMadeObject(tags[6]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getOfficeObject(tags[7]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getTourismObject(tags[9]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getShopObject(tags[10]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getSportObject(tags[11]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getLanduseObject(tags[12]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}
		o = getHighwayObject(tags[13]);
		if (o!=null && !placeTypes.contains(o.getClass().getSimpleName())){
			objects.add(o);
			placeTypes.add(o.getClass().getSimpleName());
		}

        if (objects.size()==0){
        	objects.add(new GisFeature());
        }
		return objects;
	}



	GisFeature getAmenityObject(String amenity){
		if (isNonRealTag(amenity)){
			return null;
		} 
		GisFeature gisfeature = null;
		//take care of case, always put in lower case
		String a = amenity.trim().toLowerCase();
		if ("parking".equals(a)){
			gisfeature =  new Parking();
		} else if ("school".equals(a)){
			gisfeature =  new School();
		} else  if ("place_of_worship".equals(a)){
			gisfeature =  new Religious();
		} else  if ("restaurant".equals(a)){
			gisfeature =  new Restaurant();
		} else  if ("fuel".equals(a)){
			gisfeature =  new Fuel();
		} else  if ("bench".equals(a)){
			gisfeature =  new Bench();
		} else  if ("grave_yard".equals(a)){
			gisfeature =  new Cemetery();
		} else  if ("post_box".equals(a)){
			gisfeature =  new PostOffice();
		} else  if ("bank".equals(a)){
			gisfeature =  new Bank();
		} else  if ("fast_food".equals(a)){
			gisfeature =  new Restaurant();
		} else  if ("cafe".equals(a)){
			gisfeature =  new Restaurant();
		} else  if ("kindergarten".equals(a)){
			gisfeature =  new School();
		} else  if ("hospital".equals(a)){
			gisfeature =  new Hospital();
		} else  if ("pharmacy".equals(a)){
			gisfeature =  new Pharmacy();
		} else  if ("post_office".equals(a)){
			gisfeature =  new PostOffice();
		} else  if ("pub".equals(a)){
			gisfeature =  new Bar();
		} else  if ("bicycle_parking".equals(a)){
			gisfeature =  new Parking();
		} else  if ("telephone".equals(a)){
			gisfeature =  new Telephone();
		} else  if ("toilets".equals(a)){
			gisfeature =  new Toilet();
		}  else  if ("atm".equals(a)){
			gisfeature =  new ATM();
		} else  if ("drinking_water".equals(a)){
			gisfeature =  new GisFeature();
		} else  if ("fire_station".equals(a)){
			gisfeature =  new FireStation();
		} else  if ("police".equals(a)){
			gisfeature =  new PolicePost();
		} else  if ("bar".equals(a)){
			gisfeature =  new Bar();
		} else  if ("swimming_pool".equals(a)){
			gisfeature =  new SwimmingPool();
		} else  if ("townhall".equals(a)){
			gisfeature =  new CityHall();
		} else  if ("parking_space".equals(a)){
			gisfeature =  new Parking();
		} else  if ("library".equals(a)){
			gisfeature =  new Library();
		} else  if ("fountain".equals(a)){
			gisfeature =  new Fountain();
		} else  if ("vending_machine".equals(a)){
			gisfeature =  new VendingMachine();
		} else  if ("university".equals(a)){
			gisfeature =  new School();
		} else  if ("doctors".equals(a)){
			gisfeature =  new Doctor();
		} else  if ("social_facility".equals(a)){
			gisfeature =  new AdmBuilding();
		} else  if ("bus_station".equals(a)){
			gisfeature =  new BusStation();
		} else  if ("college".equals(a)){
			gisfeature =  new School();
		} else  if ("car_wash".equals(a)){
			gisfeature =  new GisFeature();
		} else  if ("marketplace".equals(a)){
			gisfeature =  new Shop();
		} else  if ("emergency_phone".equals(a)){
			gisfeature =  new EmergencyPhone();
		} else  if ("dentist".equals(a)){
			gisfeature =  new Dentist();
		} else  if ("theatre".equals(a)){
			gisfeature =  new Theater();
		} else  if ("taxi".equals(a)){
			gisfeature =  new Taxi();
		} else  if ("community_centre".equals(a)){
			gisfeature =  new GisFeature();
		} else  if ("cinema".equals(a)){
			gisfeature =  new Cinema();
		} else  if ("fire_hydrant".equals(a)){
			gisfeature =  new FireStation();
		} else  if ("bicycle_rental".equals(a)){
			gisfeature =  new Rental();
		} else  if ("veterinary".equals(a)){
			gisfeature =  new Veterinary();
		} else  if ("residential".equals(a)){
			gisfeature =  new House();
		} else  if ("nursing_home".equals(a)){
			gisfeature =  new House();
		} else  if ("courthouse".equals(a)){
			gisfeature =  new CourtHouse();
		} else  if ("ferry_terminal".equals(a)){
			gisfeature =  new FerryTerminal();
		} else  if ("nightclub".equals(a)){
			gisfeature =  new NightClub();
		}  else  if ("arts_centre".equals(a)){
			gisfeature =  new Museum();
		} else  if ("bbq".equals(a)){
			gisfeature =  new GisFeature();
		} else  if ("parking_entrance".equals(a)){
			gisfeature =  new Parking();
		}  else  if ("biergarten".equals(a)){
			gisfeature =  new Bar();
		} else  if ("car_rental".equals(a)){
			gisfeature =  new Rental();
		}  else  if ("clinic".equals(a)){
			gisfeature =  new Hospital();
		}  else  if ("prison".equals(a)){
			gisfeature =  new Prison();
		}  else  if ("car_sharing".equals(a)){
			gisfeature =  new Rental();
		}  else  if ("embassy".equals(a)){
			gisfeature =  new AdmBuilding();
		}  else  if ("driving_school".equals(a)){
			gisfeature =  new Shop();
		}  else  if ("ice_cream".equals(a)){
			gisfeature =  new Restaurant();
		}  else  if ("clock".equals(a)){
			gisfeature =  new GisFeature();
		}  else  if ("charging_station".equals(a)){
			gisfeature =  new GisFeature();
		}  else  if ("bureau_de_change".equals(a)){
			gisfeature =  new ATM();
		} else  if ("parking;fuel".equals(a)){
			gisfeature =  new Parking();
		} else  if ("shop".equals(a)){
			gisfeature =  new Shop();
		} else  if ("motorcycle_parking".equals(a)){
			gisfeature =  new Parking();
		} else  if ("casino".equals(a)){
			gisfeature =  new Casino();
		} else  if ("bus_stop".equals(a)){
			gisfeature =  new BusStation();
		}else  if ("monastery".equals(a)){
			gisfeature =  new Religious();
		}
		//tag we want to ignore
		else  if ("public_building".equals(a)){
			return null;
		}  else  if ("building".equals(a)){
			return null;
		} else  if ("no".equals(a)){
			return null;
		} else  if ("yes".equals(a)){
			return null;
		} else  if ("fixme".equals(a)){
			return null;
		}else  if ("recycling".equals(a)){
			return null;
		} else  if ("waste_basket".equals(a)){//bin
			return null;
		} else  if ("shelter".equals(a)){//abris
			return null;
		} else  if ("grit_bin".equals(a)){//composteur
			return null;
		} else  if ("waste_disposal".equals(a)){
			return null;
		}
		//default if we don't know (should we kepp the poi and it let us the ability to search by amenity,
		//or should we consider that it is something exotic
		else {
			/*gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
			gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
			gisfeature =  new GisFeature();*/
			return null;
		}
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;
	}


	GisFeature getAerowayObject(String aeroway){
		//default to airport
		if (isNonRealTag(aeroway)){
			return null;
		} 
		GisFeature gisfeature =  new Airport();
		String a  = aeroway.trim().toLowerCase();
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}


	GisFeature getRailwayObject(String railway){
		//default to rail
		String a = null;
		if (isNonRealTag(railway)){
			return null;
		} 
		GisFeature gisfeature = new Rail();
		a = railway.trim().toLowerCase();
		if ("funicular".equals(a)||"light_rail".equals(a)||"monorail".equals(a)){
			gisfeature = new Rail();
		} else if ("station".equals(a)||"tram_stop".equals(a)||"subway_entrance".equals(a)||"halt".equals(a) ){
			gisfeature = new RailRoadStation();
		} 
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getBuildingObject(String building){
		GisFeature gisfeature = null;
		String a = null;
		if (building != null && !"".equals(building.trim()) ){
			a = building.trim().toLowerCase();
		}
		if ("hotel".equals(a)||"dormitory".equals(a)){
			gisfeature = new Hotel();
		} else if ("school".equals(a)||"university".equals(a)||"faculty".equals(a)){
			gisfeature = new School();
		}  else if ("supermarket".equals(a)){
			gisfeature = new Shop();
		} else if ("train_station".equals(a)){
			gisfeature = new RailRoadStation();
		} 
		else if ("cathedral".equals(a)||"chapel".equals(a)||"church".equals(a)||"mortuary".equals(a)){
			gisfeature = new Religious();
		} 
		else if ("city_hall".equals(a)){
			gisfeature = new CityHall();
		}
		else if ("farm".equals(a)){
			gisfeature = new Farm();
		} 
		else if ("hospital".equals(a)){
			gisfeature = new Hospital();
		} else {
			return null;
		}

		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getHistoricObject(String historic){
		GisFeature gisfeature = null;
		String a = null;
		if (historic != null && !"".equals(historic.trim()) ){
			a = historic.trim().toLowerCase();
		}
		if ("castle".equals(a)||"manor".equals(a)){
			gisfeature = new Castle();
		} else if ("archeological_site".equals(a)||"city_gate".equals(a)||"fort".equals(a)||"memorial".equals(a)||"ruins".equals(a)||"rune_stone".equals(a)||"wreck".equals(a)||"monument".equals(a)){
			gisfeature = new Tourism();
		}  else if ("chapel".equals(a)||"monastery".equals(a)||"TOMB".equals(a)||"wayside_chapel".equals(a)){
			gisfeature = new Religious();
		}  else {
			return null;
		}

		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getManMadeObject(String manMade){
		GisFeature gisfeature = null;
		String a = null;
		if (manMade != null && !"".equals(manMade.trim()) ){
			a = manMade.trim().toLowerCase();
		}
		if ("watermill".equals(a)||"windmill".equals(a)){
			gisfeature = new Mill();
		} else {
			return null;
		}

		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getOfficeObject(String office){
		GisFeature gisfeature = null;
		String a = null;
		if (office != null && !"".equals(office.trim()) ){
			a = office.trim().toLowerCase();
		}
		if ("notary".equals(a)){
			gisfeature = new AdmBuilding();
		} else {
			return null;
		}
		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getCraftObject(String craft){
		GisFeature gisfeature = new Craft();
		String a = null;
		if (craft != null && !"".equals(craft.trim()) ){
			a = craft.trim().toLowerCase();
		} else {
			return null;//if no craft, we return null
		}
		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getShopObject(String shop){
		String a = null;
		if (isNonRealTag(shop)){
			return null;
		} 
		a = shop.trim().toLowerCase();
		GisFeature gisfeature = new Shop();

		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;
	}

	GisFeature getSportObject(String sport){
		String a = null;
		if (isNonRealTag(sport) ){
			return null;
		} 
		a = sport.trim().toLowerCase();
		GisFeature gisfeature = new Sport();

		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	



	GisFeature getLanduseObject(String landuse){
		GisFeature gisfeature = null;
		String a = null;
		if (landuse != null && !"".equals(landuse.trim()) ){
			a = landuse.trim().toLowerCase();
		}
		if ("cemetery".equals(a)){
			gisfeature = new Cemetery();
		} else {
			return null;
		}
		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getHighwayObject(String highway){
		GisFeature gisfeature = null;
		String a = null;
		if (highway != null && !"".equals(highway.trim()) ){
			a = highway.trim().toLowerCase();
		}
		if ("bus_stop".equals(a)){
			gisfeature = new BusStation();
		} else {
			return null;
		}
		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	//'common','dance','fishing','garden','golf','golf_course','ice_rink','marina','miniature_golf','nature_reserve','park' , 'pitch','playground','sports_center','stadium','swimming_pool','water_park')
	GisFeature getLeisureObject(String tourism){
		GisFeature gisfeature = null;
		String a = null;
		if (tourism != null && !"".equals(tourism.trim()) ){
			a = tourism.trim().toLowerCase();
		} if ("common".equals(a)){
			return null;
		} else if ("dance".equals(a)){
			gisfeature = new NightClub();
		} else if ("fishing".equals(a)||"hostel".equals(a)||"hotel".equals(a)||"motel".equals(a)){
			gisfeature = new FishingArea();
		}  else if ("garden".equals(a)){
			gisfeature = new Garden();
		} else if ("golf".equals(a) || "golf_course".equals(a)){
			gisfeature = new Golf();
		}  else if ("ice_rink".equals(a)){
			gisfeature = new Ice();
		} else if ("marina".equals(a)){
			gisfeature = new Port();
		} else if ("nature_reserve".equals(a)){
			gisfeature = new Reserve();
		} else if ("park".equals(a)){
			gisfeature = new Park();
		}  else if ("pitch".equals(a)||"sports_center".equals(a)){
			gisfeature = new Sport();
		}  else if ("playground".equals(a)){
			gisfeature = new AmusePark();
		} else if ("stadium".equals(a)){
			gisfeature = new Stadium();
		}  else if ("swimming_pool".equals(a)){
			gisfeature = new SwimmingPool();
		} else if ("water_park".equals(a)){
			gisfeature = new AmusePark();
		}  else {
			return null;
		}

		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}

	GisFeature getTourismObject(String tourism){
		GisFeature gisfeature = null;
		String a = null;
		if (tourism != null && !"".equals(tourism.trim()) ){
			a = tourism.trim().toLowerCase();
		}
		if ("camp_site".equals(a)||"caravan_site".equals(a)){
			gisfeature = new Camping();
		} else if ("guest_house".equals(a)||"hostel".equals(a)||"hotel".equals(a)||"motel".equals(a)){
			gisfeature = new Hotel();
		}  else if ("information".equals(a)){
			gisfeature = new TourismInfo();
		} else if ("museum".equals(a)){
			gisfeature = new Museum();
		}  else if ("picnic_site".equals(a)){
			gisfeature = new Picnic();
		} else if ("theme_park".equals(a)){
			gisfeature = new AmusePark();
		}  else if ("viewpoint".equals(a)){
			gisfeature = new ObservatoryPoint();
		} else if ("zoo".equals(a)){
			gisfeature = new Zoo();
		} else if ("artwork".equals(a)){
			gisfeature = new Tourism();
		}  else {
			return null;
		}

		gisfeature.setFeatureClass(gisfeature.getClass().getSimpleName().toUpperCase());
		gisfeature.setFeatureCode(DEFAULT_OSM_FEATURE_CODE);
		gisfeature.setFeatureClass(DEFAULT_OSM_FEATURE_CLASS);
		gisfeature.setAmenity(a);
		return gisfeature;

	}





}
