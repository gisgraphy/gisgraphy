package com.gisgraphy.helper;



import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class OrthogonalProjectionTest {
	
	private final static GeometryFactory geomFact = new GeometryFactory();

	@Test
	public void testProjects() {
		OrthogonalProjection orth = new OrthogonalProjection();
		Point p1 = geomFact.createPoint(new Coordinate(170, 150));
		Point p2 = geomFact.createPoint(new Coordinate(210, 170));
		Point p = geomFact.createPoint(new Coordinate(150, 250));
		Point actual = orth.projects(p1, p2, p);
		Assert.assertEquals(194, actual.getX(),0.0001);
		Assert.assertEquals(162, actual.getY(),0.0001);
		//[50,50],[170,150],[210,170],[250,170],[290,160],[330,140],[410,100],[550,50],[750,30]
		
	}
	
	@Test
	public void testGetPointOnLine() {
		OrthogonalProjection orth = new OrthogonalProjection();
		//with coordinate
		Point p = geomFact.createPoint(new Coordinate(150, 250));
		Coordinate[] coordinates = new Coordinate[]{new Coordinate(50,50),new Coordinate(170,150),new Coordinate(210,170),new Coordinate(250,170),new Coordinate(290,160)};
		LineString line = geomFact.createLineString(coordinates);
		DistancePointDto orthpoPoint = orth.getPointOnLine(line, p);
		System.out.println(orthpoPoint);
		Assert.assertEquals(194, orthpoPoint.getPoint().getX(),0.0001);
		Assert.assertEquals(162, orthpoPoint.getPoint().getY(),0.0001);
		Assert.assertEquals(98.3, orthpoPoint.getDistance(),0.1);
		
		//with Real GPS point
		coordinates = new Coordinate[]{new Coordinate(-0.5800364, 44.841225),new Coordinate(2.3514992, 48.8566101)};
		 line = geomFact.createLineString(coordinates);
		 p= geomFact.createPoint(new Coordinate(0.340196, 46.5802596));
		 orthpoPoint = orth.getPointOnLine(line, p);
		 Assert.assertEquals(0.56811007, orthpoPoint.getPoint().getX(),0.0001);
			Assert.assertEquals(46.41386504, orthpoPoint.getPoint().getY(),0.0001);
			Assert.assertEquals(25460.5, orthpoPoint.getDistance(),0.1);
	}

}
