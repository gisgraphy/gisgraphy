package com.gisgraphy.importer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.AdmBuilding;
import com.gisgraphy.domain.geoloc.entity.Airport;
import com.gisgraphy.domain.geoloc.entity.BusStation;
import com.gisgraphy.domain.geoloc.entity.Castle;
import com.gisgraphy.domain.geoloc.entity.Cemetery;
import com.gisgraphy.domain.geoloc.entity.Craft;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.Golf;
import com.gisgraphy.domain.geoloc.entity.Mill;
import com.gisgraphy.domain.geoloc.entity.Rail;
import com.gisgraphy.domain.geoloc.entity.RailRoadStation;
import com.gisgraphy.domain.geoloc.entity.Religious;
import com.gisgraphy.domain.geoloc.entity.Shop;
import com.gisgraphy.domain.geoloc.entity.Sport;
import com.gisgraphy.domain.geoloc.entity.TourismInfo;

public class OsmAmenityToPlacetypeTest {

	OsmAmenityToPlacetype osmAmenityToPlacetype = new OsmAmenityToPlacetype();
	
	
	@Test(expected=RuntimeException.class)
	public void getObjectsFromTags_wrongTagsArraySize(){
		//wrong size
		String[] tags = new String[12];
		osmAmenityToPlacetype.getObjectsFromTags(tags);
	}
	
	
	@Test
	public void isNonRealTag(){
		Assert.assertTrue(osmAmenityToPlacetype.isNonRealTag("yes"));
		Assert.assertTrue(osmAmenityToPlacetype.isNonRealTag("no"));
		Assert.assertTrue(osmAmenityToPlacetype.isNonRealTag("fixme"));
		
		Assert.assertTrue(osmAmenityToPlacetype.isNonRealTag("YES"));
		Assert.assertTrue(osmAmenityToPlacetype.isNonRealTag("NO"));
		Assert.assertTrue(osmAmenityToPlacetype.isNonRealTag("FIXME"));
		
		Assert.assertFalse(osmAmenityToPlacetype.isNonRealTag("foo"));
	}
	@Test
	public void getObjectsFromTags(){
		List<GisFeature> objects = osmAmenityToPlacetype.getObjectsFromTags(null);
		Assert.assertNotNull(objects);
		Assert.assertEquals(0, objects.size());
		
		//empty array
		String[] tags = new String[14];
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals(1, objects.size());
		
		//amenity only (one tag)
		 tags = new String[14];
		tags[0]="parking";//amenity
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals(1, objects.size());
		
		//railway
		tags = new String[14];
		tags[8]="funicular";
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals(1, objects.size());
		
		//aeroway
		tags = new String[14];
		tags[1]="heliport";
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals(1, objects.size());
		
		
		//two tags
		tags = new String[14];
		tags[0]="parking";//amenity
		tags[2]="hotel";//building
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals(2, objects.size());
		
		//two tags and one is a gisfeature
		tags = new String[14];
		tags[0]="bbq";//amenity
		tags[2]="hotel";//building
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals("we should have 2 object and one gisFeature cause even if hotel extends GisFeature, GisFeature is considered as a real placetype not a 'by default one'",2, objects.size());
		
		//simple leisure with some sport
		tags = new String[14];
		tags[5]="dance";//leisure
		tags[11]="football";//sport
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals(2, objects.size());
		
		//pitch leisure with some sport
		tags = new String[14];
		tags[5]="pitch";//leisure
		tags[11]="football";//sport
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals("When leisure is pitch and there is some sport, we don't take leisure",1, objects.size());
		
		//pitch leisure without some sport
		tags = new String[14];
		tags[5]="pitch";//leisure
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals("When leisure is pitch and there is no sport, we take leisure",1, objects.size());
		
		//sport_center leisure with some sport
		tags = new String[14];
		tags[5]="sport_center";//leisure
		tags[11]="football";//sport
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals("When leisure is sport_center and there is some sport, we don't take leisure",1, objects.size());
		
		//sport_center leisure without some sport
		tags = new String[14];
		tags[5]="sports_center";//leisure
		objects = osmAmenityToPlacetype.getObjectsFromTags(tags);
		Assert.assertNotNull(objects);
		Assert.assertEquals("When leisure is sport_center and there is no sport, we take leisure",1, objects.size());
		
	}
	
	
	@Test
	public void getAmenityObjectWithNull() {
		GisFeature o = osmAmenityToPlacetype.getAmenityObject(null);
		assertEquals(null, o);
	}
	
	@Test
	public void getAmenityObjectWithEmptyString() {
		GisFeature o = osmAmenityToPlacetype.getAmenityObject("");
		assertEquals(null, o);
	}
	
	@Test
	public void getAmenityObjectWithUnknowAmenity() {
		GisFeature o = osmAmenityToPlacetype.getAmenityObject("foo");
		Assert.assertEquals(null, o);
	}
	
	@Test
	public void getAmenityObjectWithknowAmenity_caseandTrim() {
		GisFeature o = osmAmenityToPlacetype.getAmenityObject("ShOp ");
		Assert.assertEquals(Shop.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("shop", o.getAmenity());
	}
	@Test
	public void getAmenityObjectWithKnowAmenity() {
		GisFeature o = osmAmenityToPlacetype.getAmenityObject("shop");
		Assert.assertEquals(Shop.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("shop", o.getAmenity());
	}
	
	@Test
	public void getAmenityObjectBuildingAmenity() {
		Assert.assertNull("building is a tag we want to ignore",osmAmenityToPlacetype.getAmenityObject("building"));
		osmAmenityToPlacetype = new OsmAmenityToPlacetype();
		Assert.assertNull("public_building is a tag we want to ignore",osmAmenityToPlacetype.getAmenityObject("public_building"));
	}
	
	
	@Test
	public void getAerowayObject(){
		GisFeature o = osmAmenityToPlacetype.getAerowayObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getAerowayObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getAerowayObject("foo");
		Assert.assertEquals(Airport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("foo", o.getAmenity());
		
		o = osmAmenityToPlacetype.getAerowayObject("aerodrome");
		Assert.assertEquals(Airport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("aerodrome", o.getAmenity());
		
		o = osmAmenityToPlacetype.getAerowayObject("aerODRome");
		Assert.assertEquals(Airport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("aerodrome", o.getAmenity());
		
		o = osmAmenityToPlacetype.getAerowayObject("terminal");
		Assert.assertEquals(Airport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("terminal", o.getAmenity());
		
		o = osmAmenityToPlacetype.getAerowayObject("heLIpdad ");
		Assert.assertEquals(Airport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("helipdad", o.getAmenity());
		
	}

	
	@Test
	public void getRailwayObject(){
		GisFeature o = osmAmenityToPlacetype.getRailwayObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getRailwayObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getRailwayObject("foo");
		Assert.assertEquals(Rail.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("foo", o.getAmenity());
		
		o = osmAmenityToPlacetype.getRailwayObject("funicular");
		Assert.assertEquals(Rail.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("funicular", o.getAmenity());
		
		o = osmAmenityToPlacetype.getRailwayObject("funICUlar ");
		Assert.assertEquals(Rail.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("funicular", o.getAmenity());
		
		o = osmAmenityToPlacetype.getRailwayObject("station");
		Assert.assertEquals(RailRoadStation.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("station", o.getAmenity());
		
	}

	@Test
	public void getBuildingObject(){
		GisFeature o = osmAmenityToPlacetype.getBuildingObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getBuildingObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getBuildingObject("foo");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getBuildingObject("supermarket");
		Assert.assertEquals(Shop.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("supermarket", o.getAmenity());
		
		o = osmAmenityToPlacetype.getBuildingObject("suPErmarket ");
		Assert.assertEquals(Shop.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("supermarket", o.getAmenity());
	}
	
	@Test
	public void getLeisureObject(){
		GisFeature o = osmAmenityToPlacetype.getLeisureObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getLeisureObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getLeisureObject("foo");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getLeisureObject("common");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getLeisureObject("golf");
		Assert.assertEquals(Golf.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("golf", o.getAmenity());
		
		o = osmAmenityToPlacetype.getLeisureObject("gOLf ");
		Assert.assertEquals(Golf.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("golf", o.getAmenity());
		
		o = osmAmenityToPlacetype.getLeisureObject("pitch");
		Assert.assertEquals(Sport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("pitch", o.getAmenity());
		
		o = osmAmenityToPlacetype.getLeisureObject("sports_center");
		Assert.assertEquals(Sport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("sports_center", o.getAmenity());
	}
	
	@Test
	public void getHistoricObject(){
		GisFeature o = osmAmenityToPlacetype.getHistoricObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getHistoricObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getHistoricObject("foo");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getHistoricObject("manor");
		Assert.assertEquals(Castle.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("manor", o.getAmenity());
		
		o = osmAmenityToPlacetype.getHistoricObject("chAPel ");
		Assert.assertEquals(Religious.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("chapel", o.getAmenity());
		
		
	}
	
	@Test
	public void getManMadeObject(){
		GisFeature o = osmAmenityToPlacetype.getManMadeObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getManMadeObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getManMadeObject("foo");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getManMadeObject("watermill");
		Assert.assertEquals(Mill.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("watermill", o.getAmenity());
		
		o = osmAmenityToPlacetype.getManMadeObject("windMIll ");
		Assert.assertEquals(Mill.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("windmill", o.getAmenity());
	}
	
	@Test
	public void getOfficeObject(){
		GisFeature o = osmAmenityToPlacetype.getOfficeObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getOfficeObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getOfficeObject("foo");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getOfficeObject("notary");
		Assert.assertEquals(AdmBuilding.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("notary", o.getAmenity());
		
		o = osmAmenityToPlacetype.getOfficeObject("notaRY ");
		Assert.assertEquals(AdmBuilding.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("notary", o.getAmenity());
		
	}
	
	@Test
	public void getTourismObject(){
		GisFeature o = osmAmenityToPlacetype.getTourismObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getTourismObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getTourismObject("foo");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getTourismObject("information");
		Assert.assertEquals(TourismInfo.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("information", o.getAmenity());
		
		o = osmAmenityToPlacetype.getTourismObject("informatiON ");
		Assert.assertEquals(TourismInfo.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("information", o.getAmenity());
		
		
	}
	
	@Test
	public void getCraftObject(){
		GisFeature o = osmAmenityToPlacetype.getCraftObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getCraftObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getCraftObject("pottery");
		Assert.assertEquals(Craft.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("pottery", o.getAmenity());
		
		o = osmAmenityToPlacetype.getCraftObject("poTTery");
		Assert.assertEquals(Craft.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("pottery", o.getAmenity());
	}
	
	@Test
	public void getShopObject(){
		GisFeature o = osmAmenityToPlacetype.getShopObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getShopObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getShopObject("foo");
		Assert.assertEquals(Shop.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("foo", o.getAmenity());
		
		o = osmAmenityToPlacetype.getShopObject("fOO ");
		Assert.assertEquals(Shop.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("foo", o.getAmenity());
	}
	
	@Test
	public void getSportObject(){
		GisFeature o = osmAmenityToPlacetype.getSportObject(null);
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getSportObject("");
		Assert.assertEquals(null, o);
		
		o = osmAmenityToPlacetype.getSportObject("foo");
		Assert.assertEquals(Sport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("foo", o.getAmenity());
		
		o = osmAmenityToPlacetype.getSportObject("fOO ");
		Assert.assertEquals(Sport.class, o.getClass());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
		Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
		Assert.assertEquals("foo", o.getAmenity());
	}
	
	@Test
	public void getLanduseObject(){
			GisFeature o = osmAmenityToPlacetype.getLanduseObject(null);
			Assert.assertEquals(null, o);
			
			o = osmAmenityToPlacetype.getLanduseObject("");
			Assert.assertEquals(null, o);
			
			o = osmAmenityToPlacetype.getLanduseObject("foo");
			Assert.assertEquals(null, o);
			
			o = osmAmenityToPlacetype.getLanduseObject("cemetery");
			Assert.assertEquals(Cemetery.class, o.getClass());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
			Assert.assertEquals("cemetery", o.getAmenity());
			
			o = osmAmenityToPlacetype.getLanduseObject("Cemetery ");
			Assert.assertEquals(Cemetery.class, o.getClass());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
			Assert.assertEquals("cemetery", o.getAmenity());
			
		}
	
	@Test
	public void getHighwayObject(){
			GisFeature o = osmAmenityToPlacetype.getHighwayObject(null);
			Assert.assertEquals(null, o);
			
			o = osmAmenityToPlacetype.getHighwayObject("");
			Assert.assertEquals(null, o);
			
			o = osmAmenityToPlacetype.getHighwayObject("foo");
			Assert.assertEquals(null, o);
			
			o = osmAmenityToPlacetype.getHighwayObject("bus_stop");
			Assert.assertEquals(BusStation.class, o.getClass());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
			Assert.assertEquals("bus_stop", o.getAmenity());
			
			o = osmAmenityToPlacetype.getHighwayObject("buS_Stop ");
			Assert.assertEquals(BusStation.class, o.getClass());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CODE, o.getFeatureCode());
			Assert.assertEquals(OsmAmenityToPlacetype.DEFAULT_OSM_FEATURE_CLASS, o.getFeatureClass());
			Assert.assertEquals("bus_stop", o.getAmenity());
			
		}
	
}
