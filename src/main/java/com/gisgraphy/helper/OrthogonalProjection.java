package com.gisgraphy.helper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class OrthogonalProjection {
	
	private final static GeometryFactory geomFact = new GeometryFactory();
	
	public DistancePointDto getPointOnLine(LineString line,Point point){
		double distance = 99999999;
		Point res = point;
		if(line!=null && point !=null){
			Coordinate[] coordinates = line.getCoordinates();
			for (int i=0;i< coordinates.length-1;i++){
				Coordinate coordinate = coordinates[i];
				if (coordinate==null){
					continue;
				}else {
					Point proj = projects(coordinates[i], coordinates[i+1], point.getCoordinate());
					double px = point.getX();
					double py = point.getY();
					double dist = Math.sqrt( Math.pow(px-proj.getX(), 2) + Math.pow(py-proj.getY(), 2) );
					if (dist < distance){
						distance =dist;//approx
						res=proj;
					}
				}
			}
			 try {
				Point pd1 = GeolocHelper.createPoint(res.getX(), res.getY());
				 distance = GeolocHelper.distance(pd1, point);//more precise
			} catch (IllegalArgumentException e) {
				//ignore in case lat and long are not in range
			}
			return new DistancePointDto(res, distance);
		}
		return new DistancePointDto(res, 0D);
	}
	
	public Point projects(Point LinePoint1, Point LinePoint2, Point point){
		if (LinePoint1==null || LinePoint2==null || point ==null){
			return point;
		}
		Coordinate p1 = LinePoint1.getCoordinate();
		Coordinate p2 = LinePoint2.getCoordinate();
		Coordinate p = point.getCoordinate();
		
		double bx = p1.x-p2.x;
		double by=p1.y-p2.y;
		double sq = bx*bx+by*by;
		
		double scale = 0;
				if (sq >0){
					scale = ((p.x - p2.x)*bx + (p.y - p2.y)*by ) / sq;
				}
				
				if (scale <=0){
					bx=p2.x;
					by=p2.y;
				} else if (scale >=1){
					bx=p1.x;
					by=p1.y;
				} else {
					 bx = bx*scale + p2.x;
			         by = by*scale + p2.y;
				}
		System.out.println("bx="+bx+" by="+by);
		return geomFact.createPoint(new Coordinate(bx, by));
				
		/*function algo( v, u, p ){

	        var bx = v[0] - u[0];
	        var by = v[1] - u[1];
	        var sq = bx*bx + by*by;

	        var scale = ( sq > 0 ) ? (( (p[0] - u[0])*bx + (p[1] - u[1])*by ) / sq) : 0.0;

	        if( scale <= 0.0 ){
	          bx = u[0];
	          by = u[1];
	        }
	        else if( scale >= 1.0 ){
	          bx = v[0];
	          by = v[1];
	        }
	        else {
	          bx = bx*scale + u[0];
	          by = by*scale + u[1];
	        }

	        return [bx, by];
	      }*/
		
		
		
	}
	
	public Point projects(Coordinate p1, Coordinate p2, Coordinate p){
		if (p1==null || p2==null || p ==null){
			return null;
		}
		
		double bx = p1.x-p2.x;
		double by=p1.y-p2.y;
		double sq = bx*bx+by*by;
		
		double scale = 0;
				if (sq >0){
					scale = ((p.x - p2.x)*bx + (p.y - p2.y)*by ) / sq;
				}
				
				if (scale <=0){
					bx=p2.x;
					by=p2.y;
				} else if (scale >=1){
					bx=p1.x;
					by=p1.y;
				} else {
					 bx = bx*scale + p2.x;
			         by = by*scale + p2.y;
				}
		System.out.println("bx="+bx+" by="+by);
		return geomFact.createPoint(new Coordinate(bx, by));
				
		/*function algo( v, u, p ){

	        var bx = v[0] - u[0];
	        var by = v[1] - u[1];
	        var sq = bx*bx + by*by;

	        var scale = ( sq > 0 ) ? (( (p[0] - u[0])*bx + (p[1] - u[1])*by ) / sq) : 0.0;

	        if( scale <= 0.0 ){
	          bx = u[0];
	          by = u[1];
	        }
	        else if( scale >= 1.0 ){
	          bx = v[0];
	          by = v[1];
	        }
	        else {
	          bx = bx*scale + u[0];
	          by = by*scale + u[1];
	        }

	        return [bx, by];
	      }*/
		
		
		
	}
	
	public static void main(String[] args) {
		OrthogonalProjection orth = new OrthogonalProjection();
		Point p1 = geomFact.createPoint(new Coordinate(170, 150));
		Point p2 = geomFact.createPoint(new Coordinate(210, 170));
		Point p = geomFact.createPoint(new Coordinate(150, 250));
	//	orth.projects(p1, p2, p);
		//[50,50],[170,150],[210,170],[250,170],[290,160],[330,140],[410,100],[550,50],[750,30]
		Coordinate[] coordinates = new Coordinate[]{new Coordinate(50,50),new Coordinate(170,150),new Coordinate(210,170),new Coordinate(250,170),new Coordinate(290,160)};
		LineString line = geomFact.createLineString(coordinates);
		DistancePointDto orthpoPoint = orth.getPointOnLine(line, p);
		System.out.println(orthpoPoint);
		
		coordinates = new Coordinate[]{new Coordinate(-0.5800364, 44.841225),new Coordinate(2.3514992, 48.8566101)};
		 line = geomFact.createLineString(coordinates);
		 p= geomFact.createPoint(new Coordinate(0.340196, 46.5802596));
		 orthpoPoint = orth.getPointOnLine(line, p);
		 System.out.println(orthpoPoint);
		 Point pd1 = GeolocHelper.createPoint(0.5681100788863813, 46.413865040236196);
		 Point pd2 = GeolocHelper.createPoint(46.5802596,0.340196 );
		 System.out.println(GeolocHelper.distance(pd1, p));
		 
	}

}
