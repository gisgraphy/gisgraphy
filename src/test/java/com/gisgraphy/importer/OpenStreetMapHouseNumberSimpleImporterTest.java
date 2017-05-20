package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.List;

import net.sf.jstester.util.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.HouseNumberType;
import com.gisgraphy.domain.valueobject.Pagination;
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
import com.vividsolutions.jts.geom.Point;

public class OpenStreetMapHouseNumberSimpleImporterTest {
	
		boolean findNearestStreetCalled=false;
		boolean buildHouseNumberFromAssociatedHouseNumberCalled=false;

	@Test
	public void parseAssociatedStreetHouseNumber() {
		String line = "A	" +
				"2069647	1661205474___0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540___24___housename1___Avenue de Fontvieille___city___zip___suburb___0102000020E6100000070000002CB583B641206340D4F36E2C28723BC0F07F91E142206340187FDB1324723BC0E4E8E04F43206340EBE74D452A723BC0A638FD8F42206340311812E62C723BC0F6FA496B42206340EB9F96D52A723BC070E01F004220634061E4654D2C723BC02CB583B641206340D4F36E2C28723BC0___N___house___" +
				"8035911___0101000020E61000002CDEB7197720634062A071EC8A753BC0______Kelvin Grove Road___Kelvin Grove Road___city___zip___suburb______W___street___" +
				"178308979___0101000020E610000084BCC39277206340114C7F40B1753BC0______Kelvin Grove Road___Kelvin Grove Road_______________W___street";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			AssociatedStreetHouseNumber actual = importer.parseAssociatedStreetHouseNumber(line);
			//TODO wrong number of fields, null
			Assert.assertEquals("2069647", actual.getRelationID());
			Assert.assertNotNull(actual.getAssociatedStreetMember());
			Assert.assertEquals(3,actual.getAssociatedStreetMember().size());
			AssociatedStreetMember m1 = new AssociatedStreetMember("1661205474", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540"),"24","Avenue de Fontvieille","N","house","housename1","city","zip","suburb", GeolocHelper.convertFromHEXEWKBToGeometry("0102000020E6100000070000002CB583B641206340D4F36E2C28723BC0F07F91E142206340187FDB1324723BC0E4E8E04F43206340EBE74D452A723BC0A638FD8F42206340311812E62C723BC0F6FA496B42206340EB9F96D52A723BC070E01F004220634061E4654D2C723BC02CB583B641206340D4F36E2C28723BC0"));
			AssociatedStreetMember m2 = new AssociatedStreetMember("8035911", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000002CDEB7197720634062A071EC8A753BC0"),null,"Kelvin Grove Road","W","street","Kelvin Grove Road","city","zip","suburb", null);
			AssociatedStreetMember m3 = new AssociatedStreetMember("178308979", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000084BCC39277206340114C7F40B1753BC0"),null,"Kelvin Grove Road","W","street","Kelvin Grove Road",null,null,null, null);
			
			Assert.assertEquals(actual.getAssociatedStreetMember().get(0),m1);
			Assert.assertEquals(actual.getAssociatedStreetMember().get(1),m2);
			Assert.assertEquals(actual.getAssociatedStreetMember().get(2),m3);
	}
	
	@Test
	public void parseAssociatedStreetHouseNumber_noMembers() {
		String line = "A	1021328	";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			AssociatedStreetHouseNumber actual = importer.parseAssociatedStreetHouseNumber(line);
			Assert.assertNull(actual);
	}
	
	@Test
	public void parseAssociatedStreetHouseNumberWrongType() {
		String line = "X	" +
				"2069647	1661205474___0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540___24___Avenue de Fontvieille___N___house___" +
				"158189815___0101000020E61000002AA4070C99A81D40227F492749DD4540___Avenue de Fontvieille___Avenue de Fontvieille___W___street___" +
				"176577460___0101000020E61000004522EE9504A81D4081BAA66957DD4540___Avenue de Fontvieille___Avenue de Fontvieille___W___street";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			Assert.assertNull(importer.parseAssociatedStreetHouseNumber(line));
	}
	
	@Test
	public void parseAssociatedStreetHouseNumberUnderscoreInName() {
		String line = "A	" +
				"2069647	1661205474___0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540___24___housename1___Avenue de Fontvieille___city___zip___suburb___0102000020E6100000070000002CB583B641206340D4F36E2C28723BC0F07F91E142206340187FDB1324723BC0E4E8E04F43206340EBE74D452A723BC0A638FD8F42206340311812E62C723BC0F6FA496B42206340EB9F96D52A723BC070E01F004220634061E4654D2C723BC02CB583B641206340D4F36E2C28723BC0___N___house___" +
				"8035911___0101000020E61000002CDEB7197720634062A071EC8A753BC0______Kelvin_Grove Road___Kelvin Grove_Road___ci_ty___z_ip___s_uburb______W___street___" +
				"178308979___0101000020E610000084BCC39277206340114C7F40B1753BC0______Kelvin_Grove Road___Kelvin Grove_Road_______________W___street";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			AssociatedStreetHouseNumber actual = importer.parseAssociatedStreetHouseNumber(line);
			//TODO wrong number of fields, null
			Assert.assertEquals("2069647", actual.getRelationID());
			Assert.assertNotNull(actual.getAssociatedStreetMember());
			Assert.assertEquals(3,actual.getAssociatedStreetMember().size());
			AssociatedStreetMember m1 = new AssociatedStreetMember("1661205474", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540"),"24","Avenue de Fontvieille","N","house","housename1","city","zip","suburb", GeolocHelper.convertFromHEXEWKBToGeometry("0102000020E6100000070000002CB583B641206340D4F36E2C28723BC0F07F91E142206340187FDB1324723BC0E4E8E04F43206340EBE74D452A723BC0A638FD8F42206340311812E62C723BC0F6FA496B42206340EB9F96D52A723BC070E01F004220634061E4654D2C723BC02CB583B641206340D4F36E2C28723BC0"));
			AssociatedStreetMember m2 = new AssociatedStreetMember("8035911", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000002CDEB7197720634062A071EC8A753BC0"),null,"Kelvin Grove_Road","W","street","Kelvin_Grove Road","ci_ty","z_ip","s_uburb", null);
			AssociatedStreetMember m3 = new AssociatedStreetMember("178308979", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000084BCC39277206340114C7F40B1753BC0"),null,"Kelvin Grove_Road","W","street","Kelvin_Grove Road",null,null,null, null);
			
			Assert.assertEquals(actual.getAssociatedStreetMember().get(0),m1);
			Assert.assertEquals(actual.getAssociatedStreetMember().get(1),m2);
			Assert.assertEquals(actual.getAssociatedStreetMember().get(2),m3);
	}
	
	
	@Test
	public void parseAssociatedStreetHouseNumberWrongHouseNumberType() {
		String line = "B	" +
				"2069647	1661205474___0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540___24___Avenue de Fontvieille___N___house___" +
				"158189815___0101000020E61000002AA4070C99A81D40227F492749DD4540___Avenue de Fontvieille___Avenue de Fontvieille___W___street___" +
				"176577460___0101000020E61000004522EE9504A81D4081BAA66957DD4540___Avenue de Fontvieille___Avenue de Fontvieille___W___street";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Assert.assertNull(importer.parseAssociatedStreetHouseNumber(line));
	}
	
	@Test
	public void parseAssociatedStreetHouseNumberWrongNumberOFields() {
		String line = "A	" +
				"2069647	";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Assert.assertNull(importer.parseAssociatedStreetHouseNumber(line));
	}
	
	@Test
	public void parseAssociatedStreetHouseNumberNullOrEmptyLine() {
		String line =null;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			Assert.assertNull(importer.parseAssociatedStreetHouseNumber(line));
			Assert.assertNull(importer.parseAssociatedStreetHouseNumber(""));
	}
	
	/*--------------------------------------------------interpolation-----------------------------------------------*/
	@Test
	public void parseInterpolationHouseNumberWrongNumberOFields() {
		String line = "I	" +
				"2069647	";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Assert.assertNull(importer.parseInterpolationHouseNumber(line));
	}
	
	@Test
	public void parseInterpolationHouseNumberNullOrEmptyLine() {
		String line =null;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			Assert.assertNull(importer.parseInterpolationHouseNumber(line));
			Assert.assertNull(importer.parseInterpolationHouseNumber(""));
	}
	
	@Test
	public void parseInterpolationHouseNumber() {
		String line = "I	168365171	1796478450___0___0101000020E61000009A023EE4525350C0959C137B682F38C0_________"
				+"1796453793___2___0101000020E610000038691A144D5350C023ADE75A6A2F38C0___600___ba_r___"
		  +"1366275082___1___0101000020E610000068661CD94B5350C0B055270C6F2F38C0______foo___"
		 +"1796453794___3___0101000020E6100000F38F6390605350C028A6666A6D2F38C0___698___	sname	even	actual";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			InterpolationHouseNumber interpolation = importer.parseInterpolationHouseNumber(line);
			InterpolationHouseNumber actual = interpolation;
			//TODO wrong number of fields, null
			Assert.assertEquals("168365171", actual.getWayId());
			Assert.assertEquals(InterpolationType.even,actual.getInterpolationType());
			Assert.assertEquals("sname",actual.getStreetName());
			Assert.assertEquals(AddressInclusion.actual,actual.getAddressInclusion());
			Assert.assertEquals(4,actual.getMembers().size());
			InterpolationMember m0 = new InterpolationMember("1796478450", 0, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000009A023EE4525350C0959C137B682F38C0"),null,null);
			InterpolationMember m1 = new InterpolationMember("1366275082", 1, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000068661CD94B5350C0B055270C6F2F38C0"),null,"foo");
			InterpolationMember m2 = new InterpolationMember("1796453793", 2, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000038691A144D5350C023ADE75A6A2F38C0"),"600","ba_r");
			InterpolationMember m3 = new InterpolationMember("1796453794", 3, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000F38F6390605350C028A6666A6D2F38C0"),"698",null);
			
			Assert.assertEquals("members should be sorted",m0,actual.getMembers().get(0));
			Assert.assertEquals(m1,actual.getMembers().get(1));
			Assert.assertEquals(m2,actual.getMembers().get(2));
			Assert.assertEquals(m3,actual.getMembers().get(3));
			//other common test
			line = "I	168365171	1796478450___0___0101000020E61000009A023EE4525350C0959C137B682F38C0_________"
					+"1796453793___2___0101000020E610000038691A144D5350C023ADE75A6A2F38C0___600___ba_r___"
			  +"1366275082___1___0101000020E610000068661CD94B5350C0B055270C6F2F38C0______foo___"
			 +"1796453794___3___0101000020E6100000F38F6390605350C028A6666A6D2F38C0___698___	sname	even	";
			interpolation = importer.parseInterpolationHouseNumber(line);
			Assert.assertNotNull("address inclusion is empty",interpolation);
			Assert.assertEquals(4,interpolation.getMembers().size());
				
			line ="I	169508709	1806691488___0___0101000020E610000045C3BD8D28404DC0E08211A04B4D41C0___3702___Tinogasta___1806691490___1___0101000020E6100000C3FF0C2549404DC0F924C1655F4D41C0___3800___Tinogasta		even	actual";
			Assert.assertNotNull(interpolation);
			Assert.assertEquals(4,interpolation.getMembers().size());
	}
	
	@Test
	public void parseInterpolationHouseNumber_NoInterpolationType() {
		String line = "I	168365171	1796478450___0___0101000020E61000009A023EE4525350C0959C137B682F38C0_________"
				+"1796453793___2___0101000020E610000038691A144D5350C023ADE75A6A2F38C0___600___ba_r___"
		  +"1366275082___1___0101000020E610000068661CD94B5350C0B055270C6F2F38C0______foo___"
		 +"1796453794___3___0101000020E6100000F38F6390605350C028A6666A6D2F38C0___698___	sname		actual";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			InterpolationHouseNumber interpolation = importer.parseInterpolationHouseNumber(line);
			InterpolationHouseNumber actual = interpolation;
			//TODO wrong number of fields, null
			Assert.assertEquals("168365171", actual.getWayId());
			Assert.assertEquals("By default interpolation is all",InterpolationType.all,actual.getInterpolationType());
			
	}
	
	//parseNodeHouseNumber
	@Test
	public void parseNodeHouseNumberWrongNumberOFields() {
		String line = "N	" +
				"2069647	";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Assert.assertNull(importer.parseNodeHouseNumber(line));
	}
	
	//parseNodeHouseNumber
		@Test
		public void parseNodeHouseNumberWrongNumberOFields_W() {
			String line = "W	" +
					"2069647	";
			OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
			Assert.assertNull(importer.parseNodeHouseNumber(line));
		}
	
	@Test
	public void parseNodeHouseNumberNullOrEmptyLine() {
		String line =null;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Assert.assertNull(importer.parseNodeHouseNumber(line));
		Assert.assertNull(importer.parseNodeHouseNumber(""));
	}
	
	
		
	@Test
	public void parseNodeHouseNumber(){
		String line = "N	247464344	0101000020E610000044BC1A457B304DC018737C597F4B41C0	405	Museo Ferroviario	Avenida Del Libertador	city	zip	suburb	";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		NodeHouseNumber actual = importer.parseNodeHouseNumber(line);
		NodeHouseNumber expected = new NodeHouseNumber("247464344",(Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000044BC1A457B304DC018737C597F4B41C0"),"405","Museo Ferroviario","Avenida Del Libertador","city","zip","suburb",null) ;
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void parseNodeHouseNumber_W(){
		String line = "W	247464344	0101000020E610000044BC1A457B304DC018737C597F4B41C0	405	Museo Ferroviario	Avenida Del Libertador	city	zip	suburb	0102000020E61000000700000048066FFE15216340CA79B5920C813BC04E0B5EF4152163403510CB660E813BC0348D81D0152163404D24873E0E813BC0AEE584BF1521634081A1A24511813BC0D1590B581721634020C1430713813BC036AC4E731721634082AD122C0E813BC048066FFE15216340CA79B5920C813BC0";
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		NodeHouseNumber actual = importer.parseNodeHouseNumber(line);
		NodeHouseNumber expected = new NodeHouseNumber("247464344",(Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000044BC1A457B304DC018737C597F4B41C0"),"405","Museo Ferroviario","Avenida Del Libertador","city","zip","suburb",null) ;
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void segmentize(){
		/*
		0     5           15
		|------|------|------|------|-----|
		0      6     12      18     24     30
		*/
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Point p1 = GeolocHelper.createPoint(1f, 1f);
		Point p2 = GeolocHelper.createPoint(1f, 1.045225f);
		Point p3 = GeolocHelper.createPoint(1f, 1.13567f);
		Point p4 = GeolocHelper.createPoint(1f, 1.27133f);
		/*
		System.out.println(GeolocHelper.distance(p1, p2));
		System.out.println(GeolocHelper.distance(p2, p3));
		System.out.println(GeolocHelper.distance(p3, p4));*/
		List<Point> points = new ArrayList<Point>();
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		
		List<Point> result = importer.segmentize(points, 4);
		Assert.assertEquals(6, result.size());
		for (int i =0; i<5;i++){
			double distance = GeolocHelper.distance(result.get(i), result.get(i+1));
			System.out.println(distance);
			Assert.assertTrue(Math.abs(distance-6000)<10);
		}
		
		
	}
	
	@Test
	public void segmentize_onePoint(){
		/*
		0     5           15			  30
		|------|------|------|------|-----|
		0      6     12      18     24     30
		*/
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Point p1 = GeolocHelper.createPoint(1f, 1f);
		Point p2 = GeolocHelper.createPoint(1f, 1.045225f);
		Point p3 = GeolocHelper.createPoint(1f, 1.13567f);
		Point p4 = GeolocHelper.createPoint(1f, 1.27133f);
		/*
		System.out.println(GeolocHelper.distance(p1, p2));
		System.out.println(GeolocHelper.distance(p2, p3));
		System.out.println(GeolocHelper.distance(p3, p4));*/
		List<Point> points = new ArrayList<Point>();
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		
		List<Point> result = importer.segmentize(points, 1);
		Assert.assertEquals(3, result.size());
		for (int i =0; i<2;i++){
			double distance = GeolocHelper.distance(result.get(i), result.get(i+1));
			System.out.println(distance);
			Assert.assertTrue(Math.abs(distance-15000)<10);
		}
		
		
	}
	
	@Test
	public void buildHouseNumberFromAssociatedHouseNumber(){
		AssociatedStreetMember houseMember = new AssociatedStreetMember();
		String number = "3";
		String id = "234";
		String type = "house";
		Point point = GeolocHelper.createPoint(3F, 2F);
		houseMember.setHouseNumber(number);
		houseMember.setLocation(point);
		houseMember.setId(id);
		houseMember.setType(type);
		
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		HouseNumber houseNumber = importer.buildHouseNumberFromAssociatedHouseNumber(houseMember);
		
		Assert.assertEquals(number,houseNumber.getNumber() );
		Assert.assertEquals(234, houseNumber.getOpenstreetmapId().intValue());
		Assert.assertEquals(HouseNumberType.ASSOCIATED, houseNumber.getType());
		
		
	}
	
	
	@Test
	public void findNearestStreet_oneResult(){
		List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
		SolrResponseDto solrResponseDto = EasyMock.createMock(SolrResponseDto.class);
		long openstreetmapId = 233L;
		EasyMock.expect(solrResponseDto.getOpenstreetmap_id()).andStubReturn(openstreetmapId);
		EasyMock.replay(solrResponseDto);
		results.add(solrResponseDto);
		FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
		EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
		EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
		EasyMock.replay(mockResultDTO);
		
		String streetName="streetname";
		FulltextQuery query = new FulltextQuery(streetName, Pagination.DEFAULT_PAGINATION, OpenStreetMapHouseNumberSimpleImporter.MEDIUM_OUTPUT, 
				com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, null);
		Point point = GeolocHelper.createPoint(2F,	3F);
		query.around(point);
		query.withRadius(OpenStreetMapHouseNumberSimpleImporter.DEFAULT_SEARCH_DISTANCE);
		query.withAllWordsRequired(false).withoutSpellChecking();
		
		OpenStreetMap osm = new OpenStreetMap();
		osm.setOpenstreetmapId(openstreetmapId);
		IOpenStreetMapDao osmDaoMock = EasyMock.createMock(IOpenStreetMapDao.class);
		EasyMock.expect(osmDaoMock.getByOpenStreetMapId(openstreetmapId)).andStubReturn(osm);
		EasyMock.expect(osmDaoMock.save(osm)).andReturn(osm);
		EasyMock.replay(osmDaoMock);
		

		IFullTextSearchEngine fulltextEngine = EasyMock.createMock(IFullTextSearchEngine.class);
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		EasyMock.expect(fulltextEngine.executeQuery(query)).andStubReturn(mockResultDTO);
		EasyMock.replay(fulltextEngine);
		importer.setFullTextSearchEngine(fulltextEngine);
		importer.setOpenStreetMapDao(osmDaoMock);
		OpenStreetMap result = importer.findNearestStreet(streetName, point);
		Assert.assertEquals(osm, result);
		EasyMock.verify(fulltextEngine);
	}
	
	@Test
	public void findNearestStreet_errorsShouldBeCatched(){
		
		String streetName="foo";
		FulltextQuery query = new FulltextQuery(streetName, Pagination.DEFAULT_PAGINATION, OpenStreetMapHouseNumberSimpleImporter.MEDIUM_OUTPUT, 
				com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, null);
		Point point = GeolocHelper.createPoint(2F,	3F);
		query.around(point);
		query.withRadius(OpenStreetMapHouseNumberSimpleImporter.DEFAULT_SEARCH_DISTANCE);
		query.withAllWordsRequired(false).withoutSpellChecking();
		
		

		IFullTextSearchEngine fulltextEngine = EasyMock.createMock(IFullTextSearchEngine.class);
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		EasyMock.expect(fulltextEngine.executeQuery(query)).andThrow(new RuntimeException());
		EasyMock.replay(fulltextEngine);
		importer.setFullTextSearchEngine(fulltextEngine);
		OpenStreetMap result = importer.findNearestStreet(streetName, point);
		EasyMock.verify(fulltextEngine);
	}
	
	
	@Test
	public void findNearestStreet_severalResults(){
		List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
		SolrResponseDto solrResponseDto = EasyMock.createMock(SolrResponseDto.class);
		long openstreetmapId = 233L;
		EasyMock.expect(solrResponseDto.getOpenstreetmap_id()).andStubReturn(openstreetmapId);
		EasyMock.replay(solrResponseDto);
		results.add(solrResponseDto);
		
		
		SolrResponseDto solrResponseDto2 = EasyMock.createMock(SolrResponseDto.class);
		long openstreetmapId2 = 344L;
		EasyMock.expect(solrResponseDto2.getOpenstreetmap_id()).andStubReturn(openstreetmapId2);
		EasyMock.replay(solrResponseDto2);
		results.add(solrResponseDto2);
		
		FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
		EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(2);
		EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
		EasyMock.replay(mockResultDTO);
		
		String streetName="streetname";
		FulltextQuery query = new FulltextQuery(streetName, Pagination.DEFAULT_PAGINATION, OpenStreetMapHouseNumberSimpleImporter.MEDIUM_OUTPUT, 
				com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, null);
		Point point = GeolocHelper.createPoint(2F,	3F);
		query.around(point);
		query.withRadius(OpenStreetMapHouseNumberSimpleImporter.DEFAULT_SEARCH_DISTANCE);
		query.withAllWordsRequired(false).withoutSpellChecking();
		
		OpenStreetMap osm = new OpenStreetMap();
		osm.setOpenstreetmapId(openstreetmapId);
		IOpenStreetMapDao osmDaoMock = EasyMock.createMock(IOpenStreetMapDao.class);
		List<Long> ids = new ArrayList<Long>();
		ids.add(openstreetmapId);
		ids.add(openstreetmapId2);
		EasyMock.expect(osmDaoMock.getNearestByosmIds(point, ids )).andStubReturn(osm);
		EasyMock.expect(osmDaoMock.save(osm)).andReturn(osm);
		EasyMock.replay(osmDaoMock);
		

		IFullTextSearchEngine fulltextEngine = EasyMock.createMock(IFullTextSearchEngine.class);
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		EasyMock.expect(fulltextEngine.executeQuery(query)).andStubReturn(mockResultDTO);
		EasyMock.replay(fulltextEngine);
		importer.setFullTextSearchEngine(fulltextEngine);
		importer.setOpenStreetMapDao(osmDaoMock);
		OpenStreetMap result = importer.findNearestStreet(streetName, point);
		Assert.assertEquals(osm, result);
		EasyMock.verify(fulltextEngine);
	}
	
	@Test
	public void findNearestStreet_nullPoint(){
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Point point = GeolocHelper.createPoint(2F, 3F);
		org.junit.Assert.assertNull(importer.findNearestStreet("foo", null));
	}
	
	@Test
	public void findNearestStreet_nullQuery(){
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Point point = GeolocHelper.createPoint(2F, 3F);
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap openStreetMap = new OpenStreetMap();
		EasyMock.expect(openStreetMapDao.getNearestFrom(point,OpenStreetMapHouseNumberSimpleImporter.DEFAULT_SEARCH_DISTANCE)).andReturn(openStreetMap);
		EasyMock.replay(openStreetMapDao);
		importer.openStreetMapDao=openStreetMapDao;
		org.junit.Assert.assertEquals(openStreetMap,importer.findNearestStreet(null, point));
		EasyMock.verify(openStreetMapDao);
	}
	
	@Test
	public void findNearestStreet_EmptyQuery(){
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Point point = GeolocHelper.createPoint(2F, 3F);
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap openStreetMap = new OpenStreetMap();
		EasyMock.expect(openStreetMapDao.getNearestFrom(point,OpenStreetMapHouseNumberSimpleImporter.DEFAULT_SEARCH_DISTANCE)).andReturn(openStreetMap);
		EasyMock.replay(openStreetMapDao);
		importer.openStreetMapDao=openStreetMapDao;
		org.junit.Assert.assertEquals(openStreetMap,importer.findNearestStreet("", point));
		EasyMock.verify(openStreetMapDao);
	}
	
	@Test
	public void findNearestStreet_doubleQuoteQuery(){
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		Point point = GeolocHelper.createPoint(2F, 3F);
		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		OpenStreetMap openStreetMap = new OpenStreetMap();
		EasyMock.expect(openStreetMapDao.getNearestFrom(point,OpenStreetMapHouseNumberSimpleImporter.DEFAULT_SEARCH_DISTANCE)).andReturn(openStreetMap);
		EasyMock.replay(openStreetMapDao);
		importer.openStreetMapDao=openStreetMapDao;
		org.junit.Assert.assertEquals(openStreetMap,importer.findNearestStreet("\"\"", point));
		EasyMock.verify(openStreetMapDao);
	}
	
	
	@Test
	public void processAssociatedStreet_nohouse(){
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		AssociatedStreetHouseNumber associatedStreetHouseNumber = new AssociatedStreetHouseNumber();
		importer.processAssociatedStreet(associatedStreetHouseNumber);
		
	}
	
	@Test
	public void processAssociatedStreet_noStreetMember(){
		//setup
		Long openstreetmapId =123L;
		OpenStreetMap osm = new OpenStreetMap();
		osm.setOpenstreetmapId(openstreetmapId);
		
		IOpenStreetMapDao osmDaoMock = EasyMock.createMock(IOpenStreetMapDao.class);
		EasyMock.expect(osmDaoMock.getByOpenStreetMapId(openstreetmapId )).andStubReturn(osm);
		EasyMock.expect(osmDaoMock.save(osm)).andReturn(osm);
		EasyMock.replay(osmDaoMock);
		
		final OpenStreetMap openStreetMap = new OpenStreetMap();
		
		final Point point = (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540");
		final AssociatedStreetHouseNumber associatedStreetHouseNumber = new AssociatedStreetHouseNumber();
		final AssociatedStreetMember number = new AssociatedStreetMember("1661205474", point,"24","Avenue de Fontvieille","N","house","housename1","city","zip","suburb", GeolocHelper.createLineString("LINESTRING (0 0, 10 10, 20 30)"));
		associatedStreetHouseNumber.addMember(number);
		
		findNearestStreetCalled=false;
		buildHouseNumberFromAssociatedHouseNumberCalled=false;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter(){
			@Override
			protected OpenStreetMap findNearestStreet(String streetName,
					Point location) {
				if ("Avenue de Fontvieille".equals(streetName) && location==point){
					findNearestStreetCalled=true;
					return openStreetMap;
				} else {
					Assert.fail("find nearest street is not called with the correct parameters");
					return null;
				}
			}
			
			@Override
			protected HouseNumber buildHouseNumberFromAssociatedHouseNumber(
					AssociatedStreetMember houseMember) {
				if (houseMember==number){
					buildHouseNumberFromAssociatedHouseNumberCalled=true;
					return new HouseNumber();
				}else {
					Assert.fail("buildHouseNumberFromAssociatedHouseNumber is not call with correct parameter");
					return null;
				}
			}
		};
		
		importer.setOpenStreetMapDao(osmDaoMock);
		
		//exercise
		importer.processAssociatedStreet(associatedStreetHouseNumber);
		
		//verify
		EasyMock.verify(osmDaoMock);
		Assert.assertTrue(findNearestStreetCalled);
		Assert.assertTrue(buildHouseNumberFromAssociatedHouseNumberCalled);
	}
	
	
	@Test
	public void processAssociatedStreet_noStreetMember_nostreetfound(){
		//setup
		IOpenStreetMapDao osmDaoMock = EasyMock.createMock(IOpenStreetMapDao.class);
		EasyMock.replay(osmDaoMock);
		
		final Point point = (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540");
		final AssociatedStreetHouseNumber associatedStreetHouseNumber = new AssociatedStreetHouseNumber();
		final AssociatedStreetMember number = new AssociatedStreetMember("1661205474", point,"24","Avenue de Fontvieille","N","house","housename1","city","zip","suburb", GeolocHelper.createLineString("LINESTRING (0 0, 10 10, 20 30)"));
		associatedStreetHouseNumber.addMember(number);
		
		findNearestStreetCalled=false;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter(){
			@Override
			protected OpenStreetMap findNearestStreet(String streetName,
					Point location) {
				if ("Avenue de Fontvieille".equals(streetName) && location==point){
					findNearestStreetCalled=true;
					return null;
				} else {
					Assert.fail("find nearest street is not called with the correct parameters");
					return null;
				}
			}
			
			
		};
		
		importer.setOpenStreetMapDao(osmDaoMock);
		
		//exercise
		importer.processAssociatedStreet(associatedStreetHouseNumber);
		
		//verify
		EasyMock.verify(osmDaoMock);
		Assert.assertTrue(findNearestStreetCalled);
	}
	
	@Test
	public void processAssociatedStreet_OneStreetMember(){
		//setup
		Long openstreetmapId =158189815L;
		OpenStreetMap osm = new OpenStreetMap();
		osm.setOpenstreetmapId(openstreetmapId);
		
		IOpenStreetMapDao osmDaoMock = EasyMock.createMock(IOpenStreetMapDao.class);
		EasyMock.expect(osmDaoMock.getByOpenStreetMapId(openstreetmapId )).andStubReturn(osm);
		EasyMock.expect(osmDaoMock.save(osm)).andReturn(osm);
		EasyMock.replay(osmDaoMock);
		
		
		final AssociatedStreetHouseNumber associatedStreetHouseNumber = new AssociatedStreetHouseNumber();
		final AssociatedStreetMember number = new AssociatedStreetMember("1661205474", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540"),"24","Avenue de Fontvieille","N","house","housename1","city","zip","suburb", GeolocHelper.createLineString("LINESTRING (0 0, 10 10, 20 20)"));
		final AssociatedStreetMember street = new AssociatedStreetMember("158189815", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000002AA4070C99A81D40227F492749DD4540"),"Avenue de Fontvieille","Avenue de Fontvieille","W","street","housename2","city","zip","suburb", GeolocHelper.createLineString("LINESTRING (0 0, 10 10, 20 30)"));
		associatedStreetHouseNumber.addMember(number);
		associatedStreetHouseNumber.addMember(street);
		
		buildHouseNumberFromAssociatedHouseNumberCalled=false;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter(){
			
			@Override
			protected HouseNumber buildHouseNumberFromAssociatedHouseNumber(
					AssociatedStreetMember houseMember) {
				if (houseMember==number){
					buildHouseNumberFromAssociatedHouseNumberCalled=true;
					return new HouseNumber();
				}else {
					Assert.fail("buildHouseNumberFromAssociatedHouseNumber is not call with correct parameter");
					return null;
				}
			}
		};
		
		importer.setOpenStreetMapDao(osmDaoMock);
		
		//exercise
		importer.processAssociatedStreet(associatedStreetHouseNumber);
		
		//verify
		EasyMock.verify(osmDaoMock);
		Assert.assertTrue(buildHouseNumberFromAssociatedHouseNumberCalled);
	}
	
	@Test
	public void processAssociatedStreet_SeveralStreetMember(){
		//setup
		Long openstreetmapId =158189815L;
		Long openstreetmapId2 =158189815L;
		List<Long> ids = new ArrayList<Long>();
		ids.add(openstreetmapId);
		ids.add(openstreetmapId2);
		
		OpenStreetMap osm = new OpenStreetMap();
		osm.setOpenstreetmapId(openstreetmapId);
		final AssociatedStreetHouseNumber associatedStreetHouseNumber = new AssociatedStreetHouseNumber();
		
		IOpenStreetMapDao osmDaoMock = EasyMock.createMock(IOpenStreetMapDao.class);
		EasyMock.expect(osmDaoMock.getNearestByosmIds(EasyMock.anyObject(Point.class),EasyMock.anyObject(List.class) )).andStubReturn(osm);
		EasyMock.expect(osmDaoMock.save(osm)).andReturn(osm);
		EasyMock.replay(osmDaoMock);
		
		
		Point point = (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000046DBC85BFA81D40DA7D22AA4BDD4540");
		final AssociatedStreetMember number = new AssociatedStreetMember("1661205474", point,"24","Avenue de Fontvieille","N","house","housename1","city","zip","suburb", GeolocHelper.createLineString("LINESTRING (0 0, 10 10, 20 30)"));
		final AssociatedStreetMember street = new AssociatedStreetMember("158189815", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000002AA4070C99A81D40227F492749DD4540"),"Avenue de Fontvieille","Avenue de Fontvieille","W","street","housename2","city","zip","suburb", GeolocHelper.createLineString("LINESTRING (0 0, 10 10, 20 30)"));
		AssociatedStreetMember street2 = new AssociatedStreetMember("176577460", (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000004522EE9504A81D4081BAA66957DD4540"),"Avenue de Fontvieille","Avenue de Fontvieille","W","street","housename3","city","zip","suburb", GeolocHelper.createLineString("LINESTRING (0 0, 10 10, 20 30)"));
		associatedStreetHouseNumber.addMember(number);
		associatedStreetHouseNumber.addMember(street);
		associatedStreetHouseNumber.addMember(street2);
		
		buildHouseNumberFromAssociatedHouseNumberCalled=false;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter(){
			
			@Override
			protected HouseNumber buildHouseNumberFromAssociatedHouseNumber(
					AssociatedStreetMember houseMember) {
				if (houseMember==number){
					buildHouseNumberFromAssociatedHouseNumberCalled=true;
					return new HouseNumber();
				}else {
					Assert.fail("buildHouseNumberFromAssociatedHouseNumber is not call with correct parameter");
					return null;
				}
			}
		};
		
		importer.setOpenStreetMapDao(osmDaoMock);
		
		//exercise
		importer.processAssociatedStreet(associatedStreetHouseNumber);
		
		//verify
		EasyMock.verify(osmDaoMock);
		Assert.assertTrue(buildHouseNumberFromAssociatedHouseNumberCalled);
	}
	
	@Test
	public void processNodeHouseNumber_populateOsmFields(){
		Long openstreetmapId =247464344L;
		final NodeHouseNumber house = new NodeHouseNumber(openstreetmapId+"",(Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000044BC1A457B304DC018737C597F4B41C0"),"405","Museo Ferroviario","Avenida Del Libertador","city","zip","suburb",null) ;
		house.setHouseNumber("12345");
		house.setName("name");
		house.setStreetName("streetName");
		final Point point = GeolocHelper.createPoint(2F, 1F);
		house.setLocation(point);
		house.setCity("city");
		house.setSuburb("suburb");
		house.setZipCode("zipCode");
		
		HouseNumber houseNumber = new HouseNumber();
		houseNumber.setNumber(house.getHouseNumber());
		houseNumber.setName(house.getName());
		houseNumber.setType(HouseNumberType.NODE);
		Point location = house.getLocation();
		houseNumber.setLocation(location);
		houseNumber.setOpenstreetmapId(openstreetmapId);
		
		final OpenStreetMap osm = new OpenStreetMap();
		osm.setCityConfident(false);
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter(){
		
			@Override
			protected OpenStreetMap findNearestStreet(String streetName,
					Point location) {
					return osm;
			}
			@Override
			protected void saveOsm(OpenStreetMap osm) {
				Assert.assertEquals("isInPlace should be filled",house.getSuburb(), osm.getIsInPlace());
				Assert.assertEquals("isIn  should be filled if it is not city confident",house.getCity(), osm.getIsIn());
				Assert.assertTrue("zip should be filled",osm.getIsInZip().contains(house.getZipCode()));
				super.saveOsm(osm);
			}
			
		};
		importer.processNodeHouseNumber(house);
		
		
	}
	
	
	@Test
	public void processNodeHouseNumber_populateOsmFields_cityconfident(){
		Long openstreetmapId =247464344L;
		final NodeHouseNumber house = new NodeHouseNumber(openstreetmapId+"",(Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000044BC1A457B304DC018737C597F4B41C0"),"405","Museo Ferroviario","Avenida Del Libertador","city","zip","suburb",null) ;
		house.setHouseNumber("12345");
		house.setName("name");
		house.setStreetName("streetName");
		final Point point = GeolocHelper.createPoint(2F, 1F);
		house.setLocation(point);
		house.setCity("city");
		house.setSuburb("suburb");
		house.setZipCode("zipCode");
		
		HouseNumber houseNumber = new HouseNumber();
		houseNumber.setNumber(house.getHouseNumber());
		houseNumber.setName(house.getName());
		houseNumber.setType(HouseNumberType.NODE);
		Point location = house.getLocation();
		houseNumber.setLocation(location);
		houseNumber.setOpenstreetmapId(openstreetmapId);
		
		final OpenStreetMap osm = new OpenStreetMap();
		osm.setIsIn("isIn");
		osm.setCityConfident(true);
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter(){
		
			@Override
			protected OpenStreetMap findNearestStreet(String streetName,
					Point location) {
					return osm;
			}
			@Override
			protected void saveOsm(OpenStreetMap osm) {
				Assert.assertEquals("isIn should not be filled if it is city confident","isIn", osm.getIsIn());
				super.saveOsm(osm);
			}
			
		};
		importer.processNodeHouseNumber(house);
		
		
	}
	

	@Test
	public void processNodeHouseNumber(){
		Long openstreetmapId =247464344L;
		NodeHouseNumber house = new NodeHouseNumber(openstreetmapId+"",(Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000044BC1A457B304DC018737C597F4B41C0"),"405","Museo Ferroviario","Avenida Del Libertador","city","zip","suburb",null) ;
		house.setHouseNumber("12345");
		house.setName("name");
		house.setStreetName("streetName");
		final Point point = GeolocHelper.createPoint(2F, 1F);
		house.setLocation(point);
		
		HouseNumber houseNumber = new HouseNumber();
		houseNumber.setNumber(house.getHouseNumber());
		houseNumber.setName(house.getName());
		houseNumber.setType(HouseNumberType.NODE);
		Point location = house.getLocation();
		houseNumber.setLocation(location);
		houseNumber.setOpenstreetmapId(openstreetmapId);
		
		
		final OpenStreetMap osm = EasyMock.createMock(OpenStreetMap.class);
		EasyMock.expect(osm.getOpenstreetmapId()).andStubReturn(openstreetmapId);
		EasyMock.expect(osm.getIsInZip()).andStubReturn(null);
		EasyMock.expect(osm.isCityConfident()).andStubReturn(true);
		osm.setIsInPlace(house.getSuburb());
		osm.addHouseNumber(houseNumber);
		EasyMock.replay(osm);
		
		final SolrResponseDto solrResponseDto = EasyMock.createNiceMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDto.getOpenstreetmap_id()).andStubReturn(openstreetmapId);
		EasyMock.replay(solrResponseDto);
		
		
		
		findNearestStreetCalled=false;
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter(){
			@Override
			protected OpenStreetMap findNearestStreet(String streetName,
					Point location) {
				if ("streetName".equals(streetName) && location==point){
					findNearestStreetCalled=true;
					return osm;
				} else {
					Assert.fail("find nearest street is not called with the correct parameters");
					return null;
				}
			}
			
			
		};;
		
		IOpenStreetMapDao osmDaoMock = EasyMock.createMock(IOpenStreetMapDao.class);
		EasyMock.expect(osmDaoMock.getByOpenStreetMapId(openstreetmapId )).andStubReturn(osm);
		EasyMock.expect(osmDaoMock.save(osm)).andReturn(osm);
		EasyMock.replay(osmDaoMock);
		importer.setOpenStreetMapDao(osmDaoMock);
		
		HouseNumber actual = importer.processNodeHouseNumber(house);
		
		Assert.assertEquals(point, actual.getLocation());
		Assert.assertEquals("name", actual.getName());
		Assert.assertEquals("12345", actual.getNumber());
		Assert.assertEquals(247464344L, actual.getOpenstreetmapId().longValue());
		Assert.assertEquals(HouseNumberType.NODE, actual.getType());
		
		EasyMock.verify(osmDaoMock);
		EasyMock.verify(solrResponseDto);
		EasyMock.verify(osm);
		Assert.assertTrue(findNearestStreetCalled);
	}
	
	@Test
	public void processInterpolationHouseNumber(){
		InterpolationHouseNumber interpolationHouseNumber = new InterpolationHouseNumber();
		InterpolationMember m0 = new InterpolationMember("1796478450", 0, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000009A023EE4525350C0959C137B682F38C0"),null,null);
		InterpolationMember m1 = new InterpolationMember("1366275082", 1, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000068661CD94B5350C0B055270C6F2F38C0"),null,"foo");
		InterpolationMember m2 = new InterpolationMember("1796453793", 2, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000038691A144D5350C023ADE75A6A2F38C0"),"600","ba_r");
		InterpolationMember m3 = new InterpolationMember("1796453794", 3, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000F38F6390605350C028A6666A6D2F38C0"),"607",null);
		interpolationHouseNumber.addMember(m0);
		interpolationHouseNumber.addMember(m1);
		interpolationHouseNumber.addMember(m2);
		interpolationHouseNumber.addMember(m3);
		interpolationHouseNumber.setInterpolationType(InterpolationType.even);
		interpolationHouseNumber.setStreetName("california street");
		
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		List<HouseNumber> houseNumbers = importer.processInterpolationHouseNumber(interpolationHouseNumber);
		Assert.assertEquals(5, houseNumbers.size());//600,602,604,606,608
		
		//check the first point
		Assert.assertEquals("600", houseNumbers.get(0).getNumber());
		Assert.assertEquals(1796453793L, houseNumbers.get(0).getOpenstreetmapId().longValue());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(0).getType());
		
		//check intermediary point
		Assert.assertEquals("602", houseNumbers.get(1).getNumber());
		Assert.assertEquals(null, houseNumbers.get(1).getOpenstreetmapId());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(1).getType());
		
		//check the last point
		Assert.assertEquals("the number should be round to the even value","608", houseNumbers.get(4).getNumber());
		Assert.assertEquals(1796453794L, houseNumbers.get(4).getOpenstreetmapId().longValue());
		Assert.assertEquals(null, houseNumbers.get(0).getName());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(0).getType());
		
	}
	
	@Test
	public void processInterpolationHouseNumber_multipleInterpolation(){
		InterpolationHouseNumber interpolationHouseNumber = new InterpolationHouseNumber();
		InterpolationMember m0 = new InterpolationMember("1796478450", 0, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000009A023EE4525350C0959C137B682F38C0"),null,null);
		InterpolationMember m1 = new InterpolationMember("1366275082", 1, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000068661CD94B5350C0B055270C6F2F38C0"),null,"foo");
		InterpolationMember m2 = new InterpolationMember("1796453793", 2, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000038691A144D5350C023ADE75A6A2F38C0"),"600","ba_r");
		InterpolationMember m3 = new InterpolationMember("1796453794", 3, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000F38F6390605350C028A6666A6D2F38C0"),"607",null);
		InterpolationMember m4 = new InterpolationMember("1796453795", 4, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000B85B9203765350C03A0664AF772F38C0"),"611",null);
		interpolationHouseNumber.addMember(m0);
		interpolationHouseNumber.addMember(m1);
		interpolationHouseNumber.addMember(m2);
		interpolationHouseNumber.addMember(m3);
		interpolationHouseNumber.addMember(m4);
		interpolationHouseNumber.setInterpolationType(InterpolationType.even);
		interpolationHouseNumber.setStreetName("california street");
		
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		List<HouseNumber> houseNumbers = importer.processInterpolationHouseNumber(interpolationHouseNumber);
		Assert.assertEquals(7, houseNumbers.size());//600,602,604,606,608,610,612
		
		//check the first point
		Assert.assertEquals("600", houseNumbers.get(0).getNumber());
		Assert.assertEquals(1796453793L, houseNumbers.get(0).getOpenstreetmapId().longValue());
		Assert.assertEquals(null, houseNumbers.get(0).getName());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(0).getType());
		
		//check intermediary point
		Assert.assertEquals("602", houseNumbers.get(1).getNumber());
		Assert.assertEquals(null, houseNumbers.get(1).getOpenstreetmapId());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(1).getType());
		
		//check the last point of the first interpolation
		Assert.assertEquals("the number should be round to the even value","608", houseNumbers.get(4).getNumber());
		Assert.assertEquals(1796453794L, houseNumbers.get(4).getOpenstreetmapId().longValue());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(4).getType());
		
		//check the interpolate point of the 2nd interpolation
		Assert.assertEquals("the number is not corrst","610", houseNumbers.get(5).getNumber());
		Assert.assertEquals(null, houseNumbers.get(5).getOpenstreetmapId());
		Assert.assertEquals(null, houseNumbers.get(5).getName());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(5).getType());

		//check the last point of the 2nd interpolation
		Assert.assertEquals("the number should be round to the even value","612", houseNumbers.get(6).getNumber());
		Assert.assertEquals(1796453795L, houseNumbers.get(6).getOpenstreetmapId().longValue());
		Assert.assertEquals(null, houseNumbers.get(6).getName());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(6).getType());
		
	}
	
	@Test
	public void processInterpolationHouseNumber_multipleInterpolation_no_even_correction(){
		InterpolationHouseNumber interpolationHouseNumber = new InterpolationHouseNumber();
		InterpolationMember m0 = new InterpolationMember("1796478450", 0, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E61000009A023EE4525350C0959C137B682F38C0"),null,null);
		InterpolationMember m1 = new InterpolationMember("1366275082", 1, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000068661CD94B5350C0B055270C6F2F38C0"),null,"foo");
		InterpolationMember m2 = new InterpolationMember("1796453793", 2, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E610000038691A144D5350C023ADE75A6A2F38C0"),"600","ba_r");
		InterpolationMember m3 = new InterpolationMember("1796453794", 3, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000F38F6390605350C028A6666A6D2F38C0"),"608",null);
		InterpolationMember m4 = new InterpolationMember("1796453795", 4, (Point)GeolocHelper.convertFromHEXEWKBToGeometry("0101000020E6100000B85B9203765350C03A0664AF772F38C0"),"612",null);
		interpolationHouseNumber.addMember(m0);
		interpolationHouseNumber.addMember(m1);
		interpolationHouseNumber.addMember(m2);
		interpolationHouseNumber.addMember(m3);
		interpolationHouseNumber.addMember(m4);
		interpolationHouseNumber.setInterpolationType(InterpolationType.even);
		interpolationHouseNumber.setStreetName("california street");
		
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		List<HouseNumber> houseNumbers = importer.processInterpolationHouseNumber(interpolationHouseNumber);
		Assert.assertEquals(7, houseNumbers.size());//600,602,604,606,608,610,612
		
		//check the first point
		Assert.assertEquals("600", houseNumbers.get(0).getNumber());
		Assert.assertEquals(1796453793L, houseNumbers.get(0).getOpenstreetmapId().longValue());
		Assert.assertEquals(null, houseNumbers.get(0).getName());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(0).getType());
		
		//check intermediary point
		Assert.assertEquals("602", houseNumbers.get(1).getNumber());
		Assert.assertEquals(null, houseNumbers.get(1).getOpenstreetmapId());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(1).getType());
		
		//check the last point of the first interpolation
		Assert.assertEquals("the number should be round to the even value","608", houseNumbers.get(4).getNumber());
		Assert.assertEquals(1796453794L, houseNumbers.get(4).getOpenstreetmapId().longValue());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(4).getType());
		
		//check the interpolate point of the 2nd interpolation
		Assert.assertEquals("the number is not corrst","610", houseNumbers.get(5).getNumber());
		Assert.assertEquals(null, houseNumbers.get(5).getOpenstreetmapId());
		Assert.assertEquals(null, houseNumbers.get(5).getName());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(5).getType());

		//check the last point of the 2nd interpolation
		Assert.assertEquals("the number should be round to the even value","612", houseNumbers.get(6).getNumber());
		Assert.assertEquals(1796453795L, houseNumbers.get(6).getOpenstreetmapId().longValue());
		Assert.assertEquals(null, houseNumbers.get(6).getName());
		Assert.assertEquals(HouseNumberType.INTERPOLATION, houseNumbers.get(6).getType());
		
	}
	
	@Test
	public void testAreTooClosedDistance(){
		
		OpenStreetMapHouseNumberSimpleImporter importer = new OpenStreetMapHouseNumberSimpleImporter();
		
		Assert.assertTrue(importer.areTooCloseDistance(250D, 250D));
		Assert.assertTrue(importer.areTooCloseDistance(250D, 260D));
		Assert.assertTrue(importer.areTooCloseDistance(250D, 374D));
		Assert.assertFalse(importer.areTooCloseDistance(250D, 375D));
		Assert.assertFalse(importer.areTooCloseDistance(250D, 376D));
		
		Assert.assertTrue(importer.areTooCloseDistance(null, null));
		Assert.assertTrue(importer.areTooCloseDistance(null, 1D));
		Assert.assertTrue(importer.areTooCloseDistance(1D, null));
		
	}
	
	

}
