package com.gisgraphy.geocoding;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class GeocodingHelperTest {

	 @Test
	    public void findHouseNumber(){
	    	
	     Assert.assertEquals(null, GeocodingHelper.findHouseNumber("route 66",null));
	     
	     
	     Assert.assertEquals(null, GeocodingHelper.findHouseNumber("autoroute 1",null));
	     
	     Assert.assertEquals(null, GeocodingHelper.findHouseNumber("autoroute A1",null));
	     
	     Assert.assertEquals(null, GeocodingHelper.findHouseNumber("route nationale 43",null));
	     Assert.assertEquals(null, GeocodingHelper.findHouseNumber("D 951",null));
	     Assert.assertEquals(null, GeocodingHelper.findHouseNumber("D951",null));
	     Assert.assertEquals(null, GeocodingHelper.findHouseNumber("rn 43",null));
	     
	     Assert.assertEquals("43", GeocodingHelper.findHouseNumber("43 route 66",null).getHouseNumber());
	    	
	    	Assert.assertEquals("9", GeocodingHelper.findHouseNumber("9 avenue de l'opera paris",null).getHouseNumber());
	    	Assert.assertEquals("avenue de l'opera paris", GeocodingHelper.findHouseNumber("9 avenue de l'opera paris",null).getAddressWithoutHouseNumber());
	    	
	    	//ordinal without suffix
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("Straße des 17 Juni",null));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("Straße des 17. Juni","DE"));
	    	
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("rue du 01 septembre",null));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("rue du 1 septembre","FR"));


	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("EAST 236 STREET",null));
	    	
	    	
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("236 STREET",null));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("11 DE SEPTIEMBRE DE 1888",null));
	    	
	    	
	    	
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("EAST 236 avenue",null));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("236 avenue",null));
	    	
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("2ème Avenue, Gilly, Charleroi, Hainaut, Wallonia, 6060",null));
	    	
	    	
	    	Assert.assertEquals("13", GeocodingHelper.findHouseNumber("Lottumstraße, 13 berlin",null).getHouseNumber());
	    	Assert.assertEquals("13", GeocodingHelper.findHouseNumber("Lottumstraße, 13, berlin",null).getHouseNumber());
	    	Assert.assertEquals("13", GeocodingHelper.findHouseNumber("Lottumstraße, 13 ,berlin",null).getHouseNumber());
	    	
	    	Assert.assertEquals("13", GeocodingHelper.findHouseNumber("Lottumstraße, 13a berlin",null).getHouseNumber());
	    	Assert.assertEquals("Lottumstraße, berlin", GeocodingHelper.findHouseNumber("Lottumstraße, 13a berlin",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("13", GeocodingHelper.findHouseNumber("Lottumstraße, 13a, berlin",null).getHouseNumber());
	    	Assert.assertEquals("Lottumstraße, berlin", GeocodingHelper.findHouseNumber("Lottumstraße, 13a, berlin",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("13", GeocodingHelper.findHouseNumber("Lottumstraße, 13a, berlin",null).getHouseNumber());
	    	Assert.assertEquals("Lottumstraße, berlin", GeocodingHelper.findHouseNumber("Lottumstraße, 13a, berlin",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("82", GeocodingHelper.findHouseNumber("Reichenhallerstr. 82 A 83395 Freilassing",null).getHouseNumber());
	    	Assert.assertEquals("Reichenhallerstr. 83395 Freilassing", GeocodingHelper.findHouseNumber("Reichenhallerstr. 82 A 83395 Freilassing","DE").getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("82", GeocodingHelper.findHouseNumber("83395 Freilassing, Reichenhallerstr. 82 A",null).getHouseNumber());
	    	Assert.assertEquals("83395 Freilassing, Reichenhallerstr.", GeocodingHelper.findHouseNumber("83395 Freilassing, Reichenhallerstr. 82 A","DE").getAddressWithoutHouseNumber());
	    	
	      	Assert.assertEquals("82", GeocodingHelper.findHouseNumber("83395 Freilassing, Reichenhallerstr. 82A",null).getHouseNumber());
	    	Assert.assertEquals("83395 Freilassing, Reichenhallerstr.", GeocodingHelper.findHouseNumber("83395 Freilassing, Reichenhallerstr. 82A","DE").getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("95190","FR"));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("95190","FR"));
	    	
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("95190 paris","FR"));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("95190 paris","FR"));
	    	
	    	Assert.assertEquals("25", GeocodingHelper.findHouseNumber("Bleibtreustraße 25",null).getHouseNumber());
	    	Assert.assertEquals("Bleibtreustraße", GeocodingHelper.findHouseNumber("Bleibtreustraße 25a",null).getAddressWithoutHouseNumber());
	    	Assert.assertEquals("Bleibtreustraße", GeocodingHelper.findHouseNumber("Bleibtreustraße 25a","DE").getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("4", GeocodingHelper.findHouseNumber("4-6 rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("4-6 rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165, rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165, rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 ter rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165 ter rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 ter, rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165 ter, rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	

	    
	    	
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 bis rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165 bis rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	//ordinal french
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("5ieme avenue 59000 lille",null));
	    	//Assert.assertEquals(null, GeocodingHelper.findHouseNumber("2ème Avenue, Gilly, Charleroi, Hainaut, Wallonia, 6060",null));
	    	
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165a, rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165a, rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165a rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165a rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	//for country that zip is 3 digit we don't remove the housenumber from the address
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille","IS").getHouseNumber());
	    	Assert.assertEquals("165 rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille","IS").getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille","XX").getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille","XX").getAddressWithoutHouseNumber());
	    	
	    	
	    	//for country that housenumber is 4 digit we don't remove the housenumber from the address
	    	//with house number that can't be zip => remove from address 
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 rue de la gare 5900 lille","LU").getHouseNumber());
	    	Assert.assertEquals("rue de la gare 5900 lille", GeocodingHelper.findHouseNumber("165 rue de la gare 5900 lille","LU").getAddressWithoutHouseNumber());
	    	//with house that can be zip => don't remove from address 
	    	Assert.assertEquals("5900", GeocodingHelper.findHouseNumber("rue de la gare 5900 lille","LU").getHouseNumber());
	    	Assert.assertEquals("rue de la gare 5900 lille", GeocodingHelper.findHouseNumber("165 rue de la gare 5900 lille","LU").getAddressWithoutHouseNumber());
	    	//countrycode is null but zip is 4 length=>don't remove
	    	Assert.assertEquals("5900", GeocodingHelper.findHouseNumber("rue de la gare 5900 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 5900 lille", GeocodingHelper.findHouseNumber("165 rue de la gare 5900 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("25", GeocodingHelper.findHouseNumber("Bleibtreustraße 25",null).getHouseNumber());
	    	Assert.assertEquals("Bleibtreustraße", GeocodingHelper.findHouseNumber("Bleibtreustraße 25",null).getAddressWithoutHouseNumber());
	    	Assert.assertEquals("Bleibtreustraße", GeocodingHelper.findHouseNumber("Bleibtreustraße 25","DE").getAddressWithoutHouseNumber());
	    	
	       	
	    	Assert.assertEquals("165", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille","XX").getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("165 rue de la gare 59000 lille","XX").getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("3", GeocodingHelper.findHouseNumber("3 rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("3 rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("3rd rue de la gare 59000 lille",null));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("2nd rue de la gare 59000 lille",null));
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("1st rue de la gare 59000 lille",null));
	    	
	    	Assert.assertEquals("36", GeocodingHelper.findHouseNumber("36 rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("36 rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("367", GeocodingHelper.findHouseNumber("367 rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("36 rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("3677", GeocodingHelper.findHouseNumber("3677 rue de la gare 59000 lille",null).getHouseNumber());
	    	Assert.assertEquals("3677 rue de la gare 59000 lille", GeocodingHelper.findHouseNumber("3677 rue de la gare 59000 lille",null).getAddressWithoutHouseNumber());
	    	
	    	Assert.assertEquals("5900", GeocodingHelper.findHouseNumber("rue de la gare 5900 lille",null).getHouseNumber());
	    	Assert.assertEquals("de la gare 5900 lille", GeocodingHelper.findHouseNumber("de la gare 5900 lille",null).getAddressWithoutHouseNumber());
	    
	    	Assert.assertEquals(null, GeocodingHelper.findHouseNumber("rue de la gare 59000 lille",null));
	    	
	    	Assert.assertEquals("140", GeocodingHelper.findHouseNumber("räukerweg 140 Menden 58808",null).getHouseNumber());
	    	Assert.assertEquals("räukerweg Menden 58808", GeocodingHelper.findHouseNumber("räukerweg 140 Menden 58808",null).getAddressWithoutHouseNumber());
	    }

}
