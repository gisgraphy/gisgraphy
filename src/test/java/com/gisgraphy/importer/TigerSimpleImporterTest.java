package com.gisgraphy.importer;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.helper.DistancePointDto;
import com.gisgraphy.helper.GeolocHelper;

public class TigerSimpleImporterTest {
    
    TigerSimpleImporter importer = new TigerSimpleImporter();

    @Test
    public void testParseHouseNumbers() {
        
        List<HouseNumber> actual = importer.parseHouseNumbers(null);
        Assert.assertEquals(0, actual.size());
        
        
        actual = importer.parseHouseNumbers("");
        Assert.assertEquals(0, actual.size());
        
        actual = importer.parseHouseNumbers("\"\"");
        Assert.assertEquals(0, actual.size());
        
        
         actual = importer.parseHouseNumbers("133_POINT(-87.261606 30.9977117685408);134_POINT(-87.2631298638806 -30.9977102601629);135_POINT(-87.2646523593788 30.997714768957);");
        Assert.assertEquals(3, actual.size());
        HouseNumber house = actual.get(0);
        Assert.assertEquals(-87.261606d, house.getLongitude().doubleValue(),0.00001);
        Assert.assertEquals(30.9977117685408d, house.getLatitude().doubleValue(),0.00001);
        Assert.assertEquals("133", house.getNumber());
        
        house = actual.get(1);
        Assert.assertEquals(-87.2631298638806d, house.getLongitude().doubleValue(),0.00001);
        Assert.assertEquals(-30.9977102601629d, house.getLatitude().doubleValue(),0.00001);
        Assert.assertEquals("134", house.getNumber());
        
        
        //with error in point
        actual = importer.parseHouseNumbers("133_POINT(a 30.9977117685408);134_POINT(-87.2631298638806 -30.9977102601629);135_POINT(-87.2646523593788 30.997714768957);");
        Assert.assertEquals(2, actual.size());
    }
    
    
    
   

}
