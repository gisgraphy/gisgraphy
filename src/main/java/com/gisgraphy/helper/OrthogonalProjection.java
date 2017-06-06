package com.gisgraphy.helper;

import com.gisgraphy.domain.valueobject.SRID;
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
		Point result =  geomFact.createPoint(new Coordinate(bx, by));
		if (result!=null){
			result.setSRID(SRID.WGS84_SRID.getSRID());
		}
		return result;
				
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
		Point result = geomFact.createPoint(new Coordinate(bx, by));
		if (result!=null){
			result.setSRID(SRID.WGS84_SRID.getSRID());
		}
		return result;
				
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
	
	

}
