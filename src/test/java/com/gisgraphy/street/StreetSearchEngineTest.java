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
package com.gisgraphy.street;

import static com.gisgraphy.domain.valueobject.Pagination.paginate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Resource;

import net.sf.jstester.util.Assert;

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.domain.valueobject.StreetSearchResultsDto;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.geoloc.GeolocResultsDto;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsageType;
import com.gisgraphy.test.FeedChecker;
import com.gisgraphy.test.GisgraphyTestHelper;

public class StreetSearchEngineTest extends AbstractIntegrationHttpSolrTestCase {

    @Resource
    IStreetSearchEngine streetSearchEngine;

   

    @Resource
    IOpenStreetMapDao openStreetMapDao;

    @Resource
    IStatsUsageService statsUsageService;
    
    @Test
    public void testExecuteAndSerializeShouldSerialize() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
	

	this.openStreetMapDao.save(street);

	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	File file = new File(tempDir.getAbsolutePath()
		+ System.getProperty("file.separator") + "serializegeoloc.txt");

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),10000,pagination,output,StreetType.MOTORWAY,street.isOneWay(),null,null);
	FileOutputStream outputStream = null;
	try {
	    outputStream = new FileOutputStream(file);
	} catch (FileNotFoundException e) {
	    fail("Error when instanciate OutputStream");
	}
	streetSearchEngine.executeAndSerialize(query, outputStream);

	String content = "";
	try {
	    content = GisgraphyTestHelper.readFileAsString(file.getAbsolutePath());
	} catch (IOException e) {
	    fail("can not get content of file " + file.getAbsolutePath());
	}
	FeedChecker.assertQ("The query returns incorrect values", content, "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[1]/name[.='"
		+ street.getName() + "']");

	// delete temp dir
	assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));

    }
    
    @Test
    public void testExecuteWithContainsMode() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
	

	this.openStreetMapDao.save(street);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),10000,pagination,output,StreetType.MOTORWAY,street.isOneWay(),"hn ken",StreetSearchMode.CONTAINS);
	
	StreetSearchResultsDto results = streetSearchEngine.executeQuery(query);
	assertEquals("Contains mode should be case insensitive and accent insensitive ",1, results.getResult().size());
	assertEquals(street.getName(), results.getResult().get(0).getName());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPointIsMandatoryWithContainsMode() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
	

	this.openStreetMapDao.save(street);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery("hn ken");
	query.withOutput(output);
	query.withPagination(pagination);
	query.withStreetSearchMode(StreetSearchMode.CONTAINS);
	
	try {
	    StreetSearchResultsDto results = streetSearchEngine.executeQuery(query);
	    fail("Point is required when searchmode= " + StreetSearchMode.CONTAINS+". An exception should have been thrown");
	} catch (IllegalArgumentException e) {
	    // OK
	}
	
    }
    
    
    
    @Test
    public void testExecuteWithFulltextMode() {
    	if (GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE){
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();
	

	this.openStreetMapDao.save(street);
	int numberOfLineUpdated =openStreetMapDao.updateTS_vectorColumnForStreetNameSearch();
	
	assertEquals("It should have 1 lines updated for fulltext",1, numberOfLineUpdated);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),10000,pagination,output,StreetType.MOTORWAY,street.isOneWay(),"Kenedy",StreetSearchMode.FULLTEXT);
	
	StreetSearchResultsDto results = streetSearchEngine.executeQuery(query);
	assertEquals(1, results.getResult().size());
	assertEquals(street.getName(), results.getResult().get(0).getName());
    	} 
	

    }
    

    @Test
    public void testExecuteQueryToStringShouldReturnsAValidStringWithResults() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();

	this.openStreetMapDao.save(street);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),10000,pagination,output,street.getStreetType(),street.isOneWay(),null,null);
	
	String content = streetSearchEngine.executeQueryToString(query);

	FeedChecker.assertQ("The query returns incorrect values", content, "/"
		+ GeolocResultsDto.GEOLOCRESULTSDTO_JAXB_NAME + "/"
		+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME + "[1]/name[.='"
		+ street.getName() + "']");
    }
    

    @Test
    public void testExecuteQueryShouldTakeCallbackParameterIntoAccountForScriptLanguage() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();

	this.openStreetMapDao.save(street);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.PHP).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),10000,pagination,output,street.getStreetType(),street.isOneWay(),null,null);
	query.withCallback("doit");
	
	String content = streetSearchEngine.executeQueryToString(query);
	Assert.assertTrue(content.startsWith("doit("));
	Assert.assertTrue(content.endsWith(");"));
	
    }
    
    @Test
    public void testExecuteQueryShouldTakeCallbackParameterIntoAccountForScriptLanguageWhenNoResult() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();

	this.openStreetMapDao.save(street);

	Pagination pagination = paginate().from(20).to(25);
	Output output = Output.withFormat(OutputFormat.PHP).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),1,pagination,output,street.getStreetType(),street.isOneWay(),null,null);
	query.withCallback("doit");
	
	String content = streetSearchEngine.executeQueryToString(query);
	Assert.assertTrue(content.startsWith("doit("));
	Assert.assertTrue(content.endsWith(");"));
	
    }
    
    @Test
    public void testExecuteQueryShouldNotTakeCallbackParameterIntoAccountForXML() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();

	this.openStreetMapDao.save(street);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),10000,pagination,output,street.getStreetType(),street.isOneWay(),null,null);
	query.withCallback("doit");
	
	String content = streetSearchEngine.executeQueryToString(query);
	Assert.assertFalse(content.startsWith("doit("));
	Assert.assertFalse(content.endsWith(");"));
	
    }


    @Test
    public void testExecuteQueryShouldReturnsAValidDTOOrderedByDistance() {
	OpenStreetMap street = GisgraphyTestHelper.createOpenStreetMapForJohnKenedyStreet();

	this.openStreetMapDao.save(street);

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(street.getLocation(),10000,pagination,output,street.getStreetType(),street.isOneWay(),null,null);
	
	StreetSearchResultsDto results = streetSearchEngine.executeQuery(query);
	assertEquals(1, results.getResult().size());
	assertEquals(street.getName(), results.getResult().get(0).getName());
	assertEquals(street.getCountryCode(), results.getResult().get(0).getCountryCode());
	assertEquals(street.getGid(), results.getResult().get(0).getGid());
	assertEquals(street.getIsIn(), results.getResult().get(0).getIsIn());
	assertEquals(street.getLatitude(), results.getResult().get(0).getLat());
	assertEquals(street.getLongitude(), results.getResult().get(0).getLng());
	assertEquals(street.getLength(), results.getResult().get(0).getLength());
	assertEquals(street.getOpenstreetmapId(), results.getResult().get(0).getOpenstreetmapId());
	assertEquals(street.isOneWay(), results.getResult().get(0).getOneWay().booleanValue());
	assertEquals(street.getStreetType(), results.getResult().get(0).getStreetType());
	assertNotNull(results.getResult().get(0).getDistance());
	
	
	
    }

    @Test
    public void testExecuteQueryShouldReturnsAnEmptyListIfThereIsNoResults() {

	Pagination pagination = paginate().from(1).to(15);
	Output output = Output.withFormat(OutputFormat.XML).withIndentation();
	StreetSearchQuery query = new StreetSearchQuery(GeolocHelper.createPoint(2F, 3F),10000,pagination,output,null,null,null,null);
	
	StreetSearchResultsDto results = streetSearchEngine.executeQuery(query);
	assertNotNull(
		"street search engine should never return null but an empty list",
		results);
	assertEquals(0, results.getResult().size());
    }

    

    public void testStatsShouldBeIncreaseForAllCall() {
	statsUsageService.resetUsage(StatsUsageType.STREET);
	StreetSearchQuery query = new StreetSearchQuery(GeolocHelper
		    .createPoint(1F, 1F),StreetType.BRIDLEWAY);
	streetSearchEngine.executeQueryToString(query);
	assertEquals(new Long(1), statsUsageService
		.getUsage(StatsUsageType.STREET));
	streetSearchEngine.executeQuery(query);
	assertEquals(new Long(2), statsUsageService
		.getUsage(StatsUsageType.STREET));
	streetSearchEngine.executeAndSerialize(query,
		new ByteArrayOutputStream());
	assertEquals(new Long(3), statsUsageService
		.getUsage(StatsUsageType.STREET));
    }

    public void testExecuteQueryWithNullQueryShouldThrows() {
	try {
	    streetSearchEngine.executeQuery(null);
	    fail("executeQuery does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (StreetSearchException e) {
	    fail("executequery does not accept null query and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }

    public void testExecuteQueryToStringWithNullQueryShouldThrows() {
	try {
	    streetSearchEngine.executeQueryToString(null);
	    fail("executeQueryToString does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (StreetSearchException e) {
	    fail("executequeryToString does not accept null query and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }

    public void testExecuteAndSerializeWithNullQueryShouldThrows() {
	try {
	    streetSearchEngine.executeAndSerialize(null,
		    new ByteArrayOutputStream());
	    fail("executeAndSerialize does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (StreetSearchException e) {
	    fail("executeAndSerialize does not accept null query and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }

    public void testExecuteAndSerializeWithNullOutputStreamShouldThrows() {
	try {
	    streetSearchEngine.executeAndSerialize(new StreetSearchQuery(GeolocHelper
		    .createPoint(1F, 1F),StreetType.BRIDLEWAY), null);
	    fail("executeAndSerialize does not accept null query");
	} catch (IllegalArgumentException e) {

	} catch (StreetSearchException e) {
	    fail("executeAndSerialize does not accept null outputStream and must throws an IllegalArgumentException, not GeolocServiceException");
	}
    }

}
