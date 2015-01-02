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
package com.gisgraphy.geoloc;

import static com.gisgraphy.domain.valueobject.Pagination.paginate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.geocoloc.IGeolocSearchEngine;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsageType;
import com.gisgraphy.test.FeedChecker;
import com.gisgraphy.test.GisgraphyTestHelper;

public class GeolocSearchEngineTest extends AbstractIntegrationHttpSolrTestCase {

    @Resource
    IGeolocSearchEngine geolocSearchEngine;

    @Resource
    private GisgraphyTestHelper geolocTestHelper;

    @Resource
    ICityDao cityDao;
    
    @Resource
    IAdmDao admDao;

    @Resource
    IStatsUsageService statsUsageService;

    @Test
    public void testExecuteAndSerializeShouldSerialize() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	// N 48° 52' 0'' 2° 20' 0'' E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	// N 44 50 0 ; 0 34 0 W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	// N49° 1' 0'' E 2° 28' 0''

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);

	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	File file = new File(tempDir.getAbsolutePath()
		+ System.getProperty("file.separator") + "serializegeoloc.txt");

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000000,
		pagination, output, GisFeature.class);
	FileOutputStream outputStream = null;
	try {
	    outputStream = new FileOutputStream(file);
	} catch (FileNotFoundException e) {
	    fail("Error when instanciate OutputStream");
	}
	geolocSearchEngine.executeAndSerialize(query, outputStream);

	String content = "";
	try {
	    content = GisgraphyTestHelper.readFileAsString(file.getAbsolutePath());
	} catch (IOException e) {
	    fail("can not get content of file " + file.getAbsolutePath());
	}
	FeedChecker.assertQ("The query returns incorrect values", content, "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[1]/name[.='"
		+ p1.getName() + "']", "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[2]/name[.='"
		+ p3.getName() + "']", "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[3]/name[.='"
		+ p2.getName() + "']");

	// delete temp dir
	assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));

    }

    @Test
    public void testExecuteQueryToStringShouldReturnsAValidStringWithResults() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	// N 48° 52' 0'' 2° 20' 0'' E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	// N 44 50 0 ; 0 34 0 W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	// N49° 1' 0'' E 2° 28' 0''

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
		pagination, output, City.class);
	String results = geolocSearchEngine.executeQueryToString(query);
	FeedChecker.assertQ("The query returns incorrect values", results, "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[1]/name[.='"
		+ p1.getName() + "']", "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[2]/name[.='"
		+ p3.getName() + "']", "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[3]/name[.='"
		+ p2.getName() + "']");
    }

    @Test
    public void testExecuteQueryShouldReturnsAValidDTOOrderedByDistance() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	// N 48° 52' 0'' 2° 20' 0'' E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	// N 44 50 0 ; 0 34 0 W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	// N49° 1' 0'' E 2° 28' 0''

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
		pagination, output, City.class);
	GeolocResultsDto results = geolocSearchEngine.executeQuery(query);
	assertEquals(3, results.getResult().size());
	assertEquals(p1.getName(), results.getResult().get(0).getName());
	assertEquals(p3.getName(), results.getResult().get(1).getName());
	assertEquals(p2.getName(), results.getResult().get(2).getName());
    }
    
    @Test
    public void testExecuteQueryShouldNotReturnTheDistanceFieldIfDistanceFieldIsFalse() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	// N 48° 52' 0'' 2° 20' 0'' E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	// N 44 50 0 ; 0 34 0 W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	// N49° 1' 0'' E 2° 28' 0''

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
		pagination, output, City.class);
	query.withDistanceField(false);
	GeolocResultsDto results = geolocSearchEngine.executeQuery(query);
	assertEquals(3, results.getResult().size());
	assertNull("Distance should not be null if withDistance is false", results.getResult().get(0).getDistance());
	assertNull("Distance should not be null if withDistance is false", results.getResult().get(1).getDistance());
	assertNull("Distance should not be null if withDistance is false", results.getResult().get(2).getDistance());
    }
    
    @Test
    public void testExecuteQueryShouldfilterMunicipalityIfMunicipalityFilterIsTrue() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	// N 48° 52' 0'' 2° 20' 0'' E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	p2.setMunicipality(true);
	// N 44 50 0 ; 0 34 0 W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	
	
	// N49° 1' 0'' E 2° 28' 0''

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);
	Adm adm1 = GisgraphyTestHelper.createAdm("name", "FR", "A1", "B2", "C3", "D4", p2, 4);
	this.admDao.save(adm1);

	//true
	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
		pagination, output, City.class);
	query.withMunicipalityFilter(true);
	GeolocResultsDto results = geolocSearchEngine.executeQuery(query);
	assertEquals(1, results.getResult().size());
	assertEquals("bordeaux",results.getResult().get(0).getName());
	
	//false
	query = new GeolocQuery(p1.getLocation(), 1000001, pagination, output, City.class);
	query.withMunicipalityFilter(false);
	results = geolocSearchEngine.executeQuery(query);
	assertEquals(3, results.getResult().size());

	// with gisfeature placetype
	query = new GeolocQuery(p1.getLocation(), 1000001, pagination, output, GisFeature.class);
	query.withMunicipalityFilter(true);
	results = geolocSearchEngine.executeQuery(query);
	assertEquals(1, results.getResult().size());
	assertEquals("bordeaux", results.getResult().get(0).getName());

	
    }
   @Test
    public void testExecuteQueryShouldReturnsAnEmptyListIfThereIsNoResults() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
		pagination, output, City.class);
	GeolocResultsDto results = geolocSearchEngine.executeQuery(query);
	assertNotNull(
		"Geoloc search engine should never return null but an empty list",
		results);
	assertEquals(0, results.getResult().size());
    }

    @Test
    public void testExecuteQueryInJSONShouldReturnValidJson() {
	City p1 = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(10000000L);
	p1.setAdm4Code("D4");
	p1.setAdm4Name("adm");

	this.cityDao.save(p1);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.JSON).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 40000,
		pagination, output, City.class);
	String results = geolocSearchEngine.executeQueryToString(query);
	GeolocResultsDto geolocResultsDto = geolocSearchEngine.executeQuery(query);
///	new GisFeatureDistance(((GisFeature)p1),0D);
	FeedChecker.checkGeolocResultsDtoJSON(geolocResultsDto, results);
    }

   
    
    @Test
    public void testExecuteQueryInGEORSShouldReturnValidFeed() {
	GisFeature city = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(1L);
	city.setAdm4Code("D4");
	city.setAdm4Name("adm");

	this.cityDao.save((City)city);

	Pagination pagination = Pagination.DEFAULT_PAGINATION;
	Output output = Output.withFormat(OutputFormat.GEORSS).withIndentation();
	GeolocQuery query = new GeolocQuery(city.getLocation(), 40000,
		pagination, output, City.class);
	GeolocResultsDto geolocResultsDto = geolocSearchEngine.executeQuery(query);
	String results = geolocSearchEngine.executeQueryToString(query);
	FeedChecker.checkGeolocResultsDtoGEORSS(geolocResultsDto, results);
    }
    
    @Test
    public void testExecuteQueryWithStreetPlaceTypeShouldIgnoreAllTrantientField() {
    	//is_in field are only for sync openstreetmap entity to gisfeature
    	//so the is_in fields are transcient and the property should be mark with introspectionignore field
	GisFeature city = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(1L);
	city.setAdm4Code("D4");
	city.setAdm4Name("adm");

	this.cityDao.save((City)city);

	Pagination pagination = Pagination.DEFAULT_PAGINATION;
	Output output = Output.withFormat(OutputFormat.GEORSS).withIndentation();
	GeolocQuery query = new GeolocQuery(city.getLocation(), 40000,
		pagination, output, Street.class);
	 geolocSearchEngine.executeQuery(query);
	 //should not fails
    }

	
    
    @Test
    public void testExecuteQueryInATOMShouldReturnValidFeed() {
	City p1 = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(1L);
	p1.setAdm4Code("D4");
	p1.setAdm4Name("adm");

	this.cityDao.save(p1);

	Pagination pagination = Pagination.DEFAULT_PAGINATION;
	Output output = Output.withFormat(OutputFormat.ATOM).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 40000,
		pagination, output, City.class);
	GeolocResultsDto geolocResultsDto = geolocSearchEngine.executeQuery(query);
	String results = geolocSearchEngine.executeQueryToString(query);
	FeedChecker.checkGeolocResultsDtoATOM(geolocResultsDto, results);
    }

   

    public void testStatsShouldBeIncreaseForAllCall() {
	statsUsageService.resetUsage(StatsUsageType.GEOLOC);
	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.JSON).withIndentation();
	GeolocQuery query = new GeolocQuery(GeolocHelper.createPoint(2F, 3F),
		40000, pagination, output, City.class);
	geolocSearchEngine.executeQueryToString(query);
	assertEquals(new Long(1), statsUsageService
		.getUsage(StatsUsageType.GEOLOC));
	geolocSearchEngine.executeQuery(query);
	assertEquals(new Long(2), statsUsageService
		.getUsage(StatsUsageType.GEOLOC));
	geolocSearchEngine.executeAndSerialize(query,
		new ByteArrayOutputStream());
	assertEquals(new Long(3), statsUsageService
		.getUsage(StatsUsageType.GEOLOC));
    }

    public void testExecuteQueryWithNullQueryShouldThrows() {
	try {
	    geolocSearchEngine.executeQuery(null);
	    fail("executeQuery does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (GeolocSearchException e) {
	    fail("executequery does not accept null query and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }

    public void testExecuteQueryToStringWithNullQueryShouldThrows() {
	try {
	    geolocSearchEngine.executeQueryToString(null);
	    fail("executeQueryToString does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (GeolocSearchException e) {
	    fail("executequeryToString does not accept null query and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }

    public void testExecuteAndSerializeWithNullQueryShouldThrows() {
	try {
	    geolocSearchEngine.executeAndSerialize(null,
		    new ByteArrayOutputStream());
	    fail("executeAndSerialize does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (GeolocSearchException e) {
	    fail("executeAndSerialize does not accept null query and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }

    public void testExecuteAndSerializeWithNullOutputStreamShouldThrows() {
	try {
	    geolocSearchEngine.executeAndSerialize(new GeolocQuery(GeolocHelper
		    .createPoint(1F, 1F)), null);
	    fail("executeAndSerialize does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (GeolocSearchException e) {
	    fail("executeAndSerialize does not accept null outputStream and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }
    
    @Test
    public void testExecuteQueryShouldTakeCallbackParameterIntoAccountForScriptLanguage() {
		City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
		// N 48° 52' 0'' 2° 20' 0'' E
		City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
			3L);
		// N 44 50 0 ; 0 34 0 W
		City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
			2.46667F, 2L);
		// N49° 1' 0'' E 2° 28' 0''

		this.cityDao.save(p1);
		this.cityDao.save(p2);
		this.cityDao.save(p3);

		Pagination pagination = paginate().from(1).to(15);
		Output output = Output.withFormat(OutputFormat.JSON).withIndentation();
		GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
			pagination, output, City.class);
		query.withCallback("doit");
		

	String content = geolocSearchEngine.executeQueryToString(query);
	Assert.assertTrue(content.startsWith("doit("));
	Assert.assertTrue(content.endsWith(");"));
	
    }
    
    @Test
    public void testExecuteQueryShouldTakeCallbackParameterIntoAccountForScriptLanguageWhenNoResult() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	// N 48° 52' 0'' 2° 20' 0'' E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	// N 44 50 0 ; 0 34 0 W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	// N49° 1' 0'' E 2° 28' 0''

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);

	Pagination pagination = paginate().from(15).to(20);
	Output output = Output.withFormat(OutputFormat.JSON).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
		pagination, output, City.class);
	query.withCallback("doit");
	

       String content = geolocSearchEngine.executeQueryToString(query);
       Assert.assertTrue(content.startsWith("doit("));
       Assert.assertTrue(content.endsWith(");"));

	
    }
    
    @Test
    public void testExecuteQueryShouldNotTakeCallbackParameterIntoAccountForXML() {
	City p1 = GisgraphyTestHelper.createCity("paris", 48.86667F, 2.3333F, 1L);
	// N 48° 52' 0'' 2° 20' 0'' E
	City p2 = GisgraphyTestHelper.createCity("bordeaux", 44.83333F, -0.56667F,
		3L);
	// N 44 50 0 ; 0 34 0 W
	City p3 = GisgraphyTestHelper.createCity("goussainville", 49.01667F,
		2.46667F, 2L);
	// N49° 1' 0'' E 2° 28' 0''

	this.cityDao.save(p1);
	this.cityDao.save(p2);
	this.cityDao.save(p3);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	GeolocQuery query = new GeolocQuery(p1.getLocation(), 1000001,
		pagination, output, City.class);
	query.withCallback("doit");
	

	String content = geolocSearchEngine.executeQueryToString(query);
	Assert.assertFalse(content.startsWith("doit("));
	Assert.assertFalse(content.endsWith(");"));

	
    }


}
