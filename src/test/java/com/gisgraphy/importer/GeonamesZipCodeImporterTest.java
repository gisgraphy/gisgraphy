/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICitySubdivisionDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextQuerySolrHelper;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.helper.GeolocHelper;
import com.vividsolutions.jts.geom.Point;


public class GeonamesZipCodeImporterTest {
  
    FulltextResultsDto dtoWithTwoResults;
    FulltextResultsDto dtoWithOneResult;
    FulltextResultsDto dtoWithTwoResultsLowScore;
    FulltextResultsDto dtoWithOneResultLowScore;
    SolrResponseDto dtoTwo ;
    SolrResponseDto dtoOne ;
    SolrResponseDto dtoThreeLowScore ;
    SolrResponseDto dtoFoorLowScore ;
    boolean called = false;
    
    @Before
    public void setup(){
    	
	dtoOne = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(dtoOne.getFeature_id()).andStubReturn(123L);
	EasyMock.expect(dtoOne.getLat()).andStubReturn(20D);
	EasyMock.expect(dtoOne.getName()).andStubReturn("name");
	EasyMock.expect(dtoOne.getLng()).andStubReturn(2D);
	EasyMock.expect(dtoOne.getScore()).andStubReturn(FulltextQuerySolrHelper.MIN_SCORE+5);
	EasyMock.replay(dtoOne);
	
	dtoTwo = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(dtoTwo.getFeature_id()).andStubReturn(456L);
	EasyMock.expect(dtoTwo.getLat()).andStubReturn(34D);
	EasyMock.expect(dtoTwo.getLng()).andStubReturn(5D);
	EasyMock.expect(dtoTwo.getName()).andStubReturn("name");
	EasyMock.expect(dtoTwo.getScore()).andStubReturn(FulltextQuerySolrHelper.MIN_SCORE+5);
	EasyMock.replay(dtoTwo);
	
	dtoThreeLowScore = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(dtoThreeLowScore.getFeature_id()).andStubReturn(456L);
	EasyMock.expect(dtoThreeLowScore.getLat()).andStubReturn(34D);
	EasyMock.expect(dtoThreeLowScore.getLng()).andStubReturn(5D);
	EasyMock.expect(dtoThreeLowScore.getName()).andStubReturn("name");
	EasyMock.expect(dtoThreeLowScore.getScore()).andStubReturn(FulltextQuerySolrHelper.MIN_SCORE-5);
	EasyMock.replay(dtoThreeLowScore);
	
	dtoFoorLowScore = EasyMock.createMock(SolrResponseDto.class);
	EasyMock.expect(dtoFoorLowScore.getFeature_id()).andStubReturn(456L);
	EasyMock.expect(dtoFoorLowScore.getLat()).andStubReturn(34D);
	EasyMock.expect(dtoFoorLowScore.getLng()).andStubReturn(5D);
	EasyMock.expect(dtoFoorLowScore.getName()).andStubReturn("name");
	EasyMock.expect(dtoFoorLowScore.getScore()).andStubReturn(FulltextQuerySolrHelper.MIN_SCORE-5);
	EasyMock.replay(dtoFoorLowScore);
	
	
	
	
	List<SolrResponseDto> oneResult =new ArrayList<SolrResponseDto>();
	oneResult.add(dtoOne);
	
	List<SolrResponseDto> oneResultLowScore =new ArrayList<SolrResponseDto>();
	oneResultLowScore.add(dtoThreeLowScore);
	
	List<SolrResponseDto> twoResult =new ArrayList<SolrResponseDto>();
	twoResult.add(dtoOne);
	twoResult.add(dtoTwo);
	
	List<SolrResponseDto> twoResultLowScore =new ArrayList<SolrResponseDto>();
	twoResultLowScore.add(dtoThreeLowScore);
	twoResultLowScore.add(dtoFoorLowScore);
	
	
	
	dtoWithOneResult = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(dtoWithOneResult.getNumFound()).andStubReturn(1L);
	EasyMock.expect(dtoWithOneResult.getResultsSize()).andStubReturn(1);
	EasyMock.expect(dtoWithOneResult.getResults()).andStubReturn(oneResult);
	EasyMock.replay(dtoWithOneResult);
	
	dtoWithTwoResults = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(dtoWithTwoResults.getNumFound()).andStubReturn(2L);
	EasyMock.expect(dtoWithTwoResults.getResultsSize()).andStubReturn(2);
	EasyMock.expect(dtoWithTwoResults.getResults()).andStubReturn(twoResult);
	EasyMock.replay(dtoWithTwoResults);
	
	dtoWithOneResultLowScore = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(dtoWithOneResultLowScore.getNumFound()).andStubReturn(1L);
	EasyMock.expect(dtoWithOneResultLowScore.getResultsSize()).andStubReturn(1);
	EasyMock.expect(dtoWithOneResultLowScore.getResults()).andStubReturn(oneResultLowScore);
	EasyMock.replay(dtoWithOneResultLowScore);
	
	dtoWithTwoResultsLowScore = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(dtoWithTwoResultsLowScore.getNumFound()).andStubReturn(2L);
	EasyMock.expect(dtoWithTwoResultsLowScore.getResultsSize()).andStubReturn(2);
	EasyMock.expect(dtoWithTwoResultsLowScore.getResults()).andStubReturn(twoResultLowScore);
	EasyMock.replay(dtoWithTwoResultsLowScore);
	
    }
    
    @Test
    public void getByShape(){
    	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	Point location = GeolocHelper.createPoint(3D, 4D);
		City city = new City();
		city.setFeatureId(123L);
		String countryCode = "FR";
		EasyMock.expect(cityDao.getByShape(location, countryCode, true)).andReturn(city);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		
		ICitySubdivisionDao citySubdivisionDao = EasyMock.createMock(ICitySubdivisionDao.class);
		CitySubdivision citySubdivision = new CitySubdivision();
		citySubdivision.setFeatureId(456L);
		countryCode = "FR";
		EasyMock.expect(citySubdivisionDao.getByShape(location, countryCode)).andReturn(citySubdivision);
		EasyMock.expect(citySubdivisionDao.save(citySubdivision)).andReturn(citySubdivision);
		EasyMock.replay(citySubdivisionDao);
		
		
		
		
		importer.setCityDao(cityDao);
		importer.setCitySubdivisionDao(citySubdivisionDao);
		
		boolean actual = importer.getByShape(countryCode, "code", location);
		Assert.assertEquals(true, actual);
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("code")));
		EasyMock.verify(cityDao);
    }
    @Test
    public void getByShape_first_null(){
    	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
    	ICityDao cityDao = EasyMock.createMock(ICityDao.class);
    	Point location = GeolocHelper.createPoint(3D, 4D);
		City city = new City();
		city.setFeatureId(123L);
		String countryCode = "FR";
		EasyMock.expect(cityDao.getByShape(location, countryCode, true)).andReturn(null);
		EasyMock.expect(cityDao.getByShape(location, countryCode, false)).andReturn(city);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		
		ICitySubdivisionDao citySubdivisionDao = EasyMock.createMock(ICitySubdivisionDao.class);
		CitySubdivision citySubdivision = new CitySubdivision();
		citySubdivision.setFeatureId(456L);
		countryCode = "FR";
		EasyMock.expect(citySubdivisionDao.getByShape(location, countryCode)).andReturn(citySubdivision);
		EasyMock.expect(citySubdivisionDao.save(citySubdivision)).andReturn(citySubdivision);
		EasyMock.replay(citySubdivisionDao);
		
		
		importer.setCityDao(cityDao);
		importer.setCitySubdivisionDao(citySubdivisionDao);
		
		
		boolean actual = importer.getByShape(countryCode, "code", location);
		Assert.assertEquals(true, actual);
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("code")));
		EasyMock.verify(cityDao);
    }
    
    @Test
    public void WhenACityIsFoundByShapeWeShouldNotFindByLocation(){
    	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
    		@Override
    		protected boolean getByShape(String countryCode, String code,
    				Point zipPoint) {
    			return true;
    		}
    		@Override
    		protected Long findFeature(String[] fields, Point zipPoint,
    				int maxDistance) {
    			Assert.fail("when A city is found by shape we should not find by location");
    			return 1L;
    		}
    	};
    	
    	importer.processData("AD\tAD100\tCanillo\t\t\t\t\t\t\t42.5833\t1.6667\t6");
    }
    
    @Test
    public void WhenACityIsNotFoundByShapeWeShouldFindByLocation(){
    	
    	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
    		@Override
    		protected boolean getByShape(String countryCode, String code,
    				Point zipPoint) {
    			return false;
    		}
    		@Override
    		protected Long findFeature(String[] fields, Point zipPoint,
    				int maxDistance) {
    			called = true;
    			return 1L;
    		}
    		@Override
    		protected GisFeature addAndSaveZipCodeToFeature(String code,
    				Long featureId) {
    			return new City();
    		}
    	};
    	
    	importer.processData("AD\tAD100\tCanillo\t\t\t\t\t\t\t42.5833\t1.6667\t6");
    	Assert.assertTrue(called);
    }
    
    
    @Test
    public void doAFulltextSearch(){
	String queryString = "query";
	Point point = GeolocHelper.createPoint(3D, 4D);
	FulltextQuery fulltextQuery = new FulltextQuery(queryString);
	String countryCode="cc";
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	
	IFullTextSearchEngine fullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	EasyMock.expect(fullTextSearchEngine.executeQuery(fulltextQuery)).andReturn(new FulltextResultsDto());
	EasyMock.replay(fullTextSearchEngine);
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
	importer.setFullTextSearchEngine(fullTextSearchEngine);
	
	importer.doAFulltextSearch(queryString, countryCode,point);
	EasyMock.verify(fullTextSearchEngine);
    }
    
    @Test
    public void importerShouldNotImportUnwantedZipsAsCEDEX(){
    	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
    		@Override
    		protected GisFeature addAndSaveZipCodeToFeature(String code,
    				Long featureId) {
    			Assert.fail("unwanted zip should not be saved");
    			return super.addAndSaveZipCodeToFeature(code, featureId);
    		}
    		
    		
    		@Override
    		protected GisFeature addNewEntityAndZip(String[] fields) {
    			Assert.fail("unwanted zip should not be saved");
    			return super.addNewEntityAndZip(fields);
    		}
    	};
    	importer.processData("AD\t75021 CEDEX 01\tCanillo\t\t\t\t\t\t\t42.5833\t1.6667\t6");
    	//with space
    	importer.processData("AD\t 75021 CEDEX 01\tCanillo\t\t\t\t\t\t\t42.5833\t1.6667\t6");
    	//case insensitive
    	importer.processData("AD\t75021 CedEx 01\tCanillo\t\t\t\t\t\t\t42.5833\t1.6667\t6");
    	
    }
   
    
   
    
    final StringBuffer count = new StringBuffer();
    
    @Test
    public void findFeatureExtendedThenBasicWithOutResult(){
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = "place name";
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		count.append("_");
		return new FulltextResultsDto();
	    }
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};
	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Assert.assertNull(importer.findFeature(fields,point,maxDistance));
	Assert.assertEquals(1,count.toString().length());
	
    }
    
    @Test
    public void getAccurateDistance(){
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
	Assert.assertEquals(importer.accuracyToDistance[0],importer.getAccurateDistance(0));
	Assert.assertEquals(importer.accuracyToDistance[0],importer.getAccurateDistance(-1));
	Assert.assertEquals(importer.accuracyToDistance[importer.accuracyToDistance.length-1],importer.getAccurateDistance(importer.accuracyToDistance.length+1));
	Assert.assertEquals(importer.accuracyToDistance[importer.accuracyToDistance.length-1],importer.getAccurateDistance(importer.accuracyToDistance.length));
	Assert.assertEquals(importer.accuracyToDistance[2],importer.getAccurateDistance(2));
	
    }
    
    @Test
    public void importerShouldBeTolerantToTheFactThatAccuracyIsOptionnal(){
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
	ImporterConfig config = new ImporterConfig();
	config.setWrongNumberOfFieldsThrows(true);
	String line = StringUtils.join(new String[]{"fr","zip","placename","adm1code","adm1name","adm2code","adm2name","adm3code","adm3name","3","4"},"\t");
	try {
	    importer.processData(line);
	} catch (WrongNumberOfFieldsException e) {
	    Assert.fail("if accuraty is missing importer should not throw");
	}
	 catch (Exception e) {
	     //other exceptions are ignored
	}
    }
    
    @Test
    public void findFeatureBasicWithOneResult_sameName(){
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = dtoOne.getName();
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		return dtoWithOneResult;
	    }
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};

	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Long actualFeatureId = importer.findFeature(fields,point,maxDistance);
	Assert.assertEquals(dtoWithOneResult.getResults().get(0).getFeature_id(),actualFeatureId);
	
    }
    
    @Test
    public void findFeatureBasicWithOneResult_NotSameName_butScoreOK(){
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = "place name";
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		return dtoWithOneResult;
	    }
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};

	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Long actualFeatureId = importer.findFeature(fields,point,maxDistance);
	Assert.assertEquals(123L,actualFeatureId.longValue());
	
    }
    
    @Test
    public void findFeatureBasicWithOneResult_NotSameName_butScoreKO(){
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = "place name";
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		return dtoWithOneResultLowScore;
	    }
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};

	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Long actualFeatureId = importer.findFeature(fields,point,maxDistance);
	Assert.assertEquals(null,actualFeatureId);
	
    }
    
    @Test
    public void findFeatureBasicWithSeveralResult_notSameName_butscoreOK(){
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = "place name";
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	final long featureId = 456L;
	
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		return dtoWithTwoResults;
	    }
	    
	    @Override
	    protected Long findNearest(Point zipPoint, int maxDistance, FulltextResultsDto results) {
	        return featureId;
	    }
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};
	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Long actualFeatureId = importer.findFeature(fields,point,maxDistance);
	Assert.assertEquals(123L,actualFeatureId.longValue());
	
    }
    
    @Test
    public void findFeatureBasicWithSeveralResult_notSameName_butscoreKO(){
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = "place name";
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	final long featureId = 456L;
	
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		return dtoWithTwoResultsLowScore;
	    }
	    
	    @Override
	    protected Long findNearest(Point zipPoint, int maxDistance, FulltextResultsDto results) {
	        return featureId;
	    }
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};
	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Long actualFeatureId = importer.findFeature(fields,point,maxDistance);
	Assert.assertEquals(null,actualFeatureId);
	
    }
    
    
    
    @Test
    public void findFeatureBasicWithSeveralResult_SameName(){
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = dtoOne.getName();
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	final long featureId = 456L;
	
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		return dtoWithTwoResults;
	    }
	    
	    @Override
	    protected Long findNearest(Point zipPoint, int maxDistance, FulltextResultsDto results) {
	        return featureId;
	    }
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};
	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Long actualFeatureId = importer.findFeature(fields,point,maxDistance);
	Assert.assertEquals(dtoOne.getFeature_id(),actualFeatureId);
	
    }
    
    
    
    
    
    @Test
    public void findFeatureNoResultThenNoResults(){
	
	String lat = "3.5";
	String lng = "44";
	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
	String accuracy = "5";
	String placeName = "place name";
	String countryCode = "FR";
	String adm1Name = "adm1name";
	String adm1Code = "adm1code";
	String adm2Name = "adm2name";
	String adm2Code = "adm2code";
	String adm3Name = "adm3name";
	String adm3COde = "adm3code";
	FulltextQuery fulltextQuery = new FulltextQuery(placeName +" "+adm1Name);
	fulltextQuery.limitToCountryCode(countryCode);
	fulltextQuery.withPlaceTypes(com.gisgraphy.fulltext.Constants.CITY_AND_CITYSUBDIVISION_PLACETYPE).around(point);
	
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter(){
	    int count = 0;
	    
	    @Override
	    protected FulltextResultsDto doAFulltextSearch(String query, String countryCode,Point point) {
		count = count+1;
		if (count == 1){
		    return new FulltextResultsDto();
		} else if (count==2){
		    return new FulltextResultsDto();
		}
		else return null;
	    }
	    
	};
	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3COde,lat,lng,accuracy};
	int maxDistance = importer.getAccurateDistance(new Integer(accuracy));
	Long actualFeatureId = importer.findFeature(fields,point,maxDistance);
	Assert.assertNull(actualFeatureId);
	
    }
    
    @Test
    public void findNearest(){
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
	Long FeatureId = importer.findNearest(GeolocHelper.createPoint(5F, 34F), 5, dtoWithTwoResults);
	Assert.assertEquals(dtoTwo.getFeature_id(), FeatureId);
    }
    
    @Test
    public void addAndSaveZipCodeToFeatureWithUnknowFeature(){
	Long featureId = 123456L; 
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
	IGisFeatureDao gisFeatureDaoMock = EasyMock.createMock(IGisFeatureDao.class);
	EasyMock.expect(gisFeatureDaoMock.getByFeatureId(featureId)).andReturn(null);
	EasyMock.replay(gisFeatureDaoMock);
	importer.setGisFeatureDao(gisFeatureDaoMock);
	Assert.assertNull(importer.addAndSaveZipCodeToFeature("code", featureId));
	EasyMock.verify(gisFeatureDaoMock);
    }
    
    @Test
    public void addAndSaveZipCodeToFeatureWithAlreadyExistingCode(){
	Long featureId = 123456L; 
	GisFeature gisFeature = new GisFeature();
	gisFeature.setFeatureId(123456L);
	gisFeature.addZipCode(new ZipCode("code"));
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
	IGisFeatureDao gisFeatureDaoMock = EasyMock.createMock(IGisFeatureDao.class);
	EasyMock.expect(gisFeatureDaoMock.getByFeatureId(featureId)).andReturn(gisFeature);
	EasyMock.expect(gisFeatureDaoMock.save(gisFeature)).andReturn(gisFeature);
	EasyMock.replay(gisFeatureDaoMock);
	importer.setGisFeatureDao(gisFeatureDaoMock);
	GisFeature actual = importer.addAndSaveZipCodeToFeature("code", featureId);
	Assert.assertTrue(actual.getZipCodes().contains(new ZipCode("code")));
	Assert.assertEquals(featureId,actual.getFeatureId());
	EasyMock.verify(gisFeatureDaoMock);
    }
    
    @Test
    public void addAndSaveZipCodeToFeatureShouldAdd(){
	Long featureId = 123456L; 
	GisFeature gisFeature = new GisFeature();
	gisFeature.setFeatureId(123456L);
	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
	IGisFeatureDao gisFeatureDaoMock = EasyMock.createMock(IGisFeatureDao.class);
	EasyMock.expect(gisFeatureDaoMock.getByFeatureId(featureId)).andReturn(gisFeature);
	EasyMock.expect(gisFeatureDaoMock.save(gisFeature)).andReturn(gisFeature);
	EasyMock.replay(gisFeatureDaoMock);
	importer.setGisFeatureDao(gisFeatureDaoMock);
	GisFeature actual = importer.addAndSaveZipCodeToFeature("code", featureId);
	Assert.assertTrue(actual.getZipCodes().contains(new ZipCode("code")));
	Assert.assertEquals(featureId,actual.getFeatureId());
	EasyMock.verify(gisFeatureDaoMock);
    }
    
    @Test
    public void addNewEntityAndZip(){
    	String lat = "3.5";
    	String lng = "44";
    	String accuracy = "5";
    	String placeName = "place name";
    	String countryCode = "FR";
    	String adm1Name = "adm1name";
    	String adm1Code = "adm1code";
    	String adm2Name = "adm2name";
    	String adm2Code = "adm2code";
    	String adm3Name = "adm3name";
    	String adm3Code = "adm3code";
    	String[] fields = {countryCode,"post",placeName,adm1Name,adm1Code,adm2Name,adm2Code,adm3Name,adm3Code,lat,lng,accuracy};
    	
    	GeonamesZipCodeSimpleImporter importer = new GeonamesZipCodeSimpleImporter();
    	long generatedId = 1234L;
	IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(generatedId);
	EasyMock.replay(idGenerator);
	importer.setIdGenerator(idGenerator);
    	
    	
    	ICityDao cityDaoMock = EasyMock.createMock(ICityDao.class);
    	EasyMock.expect(cityDaoMock.save((City) EasyMock.anyObject())).andReturn(new City());
    	EasyMock.replay(cityDaoMock);
    	importer.setCityDao(cityDaoMock);
    	
    	IAdmDao admDaoMock = EasyMock.createMock(IAdmDao.class);
    	City mockCity = new City();
    	mockCity.setFeatureClass("P");
    	mockCity.setFeatureCode("PPL");
    	mockCity.setSource(GISSource.GEONAMES_ZIP);
    	mockCity.setName(placeName);
    	mockCity.setFeatureId(generatedId);
    	mockCity.setAdm1Code(adm1Code);
    	mockCity.setAdm2Code(adm2Code);
    	mockCity.setAdm3Code(adm3Code);
    	Point point = GeolocHelper.createPoint(new Float(lng), new Float(lat));
    	mockCity.setLocation(point);
    	List<Adm> adms = new ArrayList<Adm>();
    	adms.add(new Adm(3));
    	//we set any abj because point doesn't implement equals 
    	EasyMock.expect(admDaoMock.ListByShape(EasyMock.anyObject(Point.class), EasyMock.anyObject(String.class))).andReturn(adms);
    	EasyMock.replay(admDaoMock);
    	importer.setAdmDao(admDaoMock);
    	
    	
    	ImporterConfig importerConfig = new ImporterConfig();
    	importerConfig.setTryToDetectAdmIfNotFound(true);
    	importer.setImporterConfig(importerConfig);
    	
    	GisFeature city = importer.addNewEntityAndZip(fields);
    	
    	
    	
    	
    	Assert.assertEquals(new Long(generatedId), city.getFeatureId());
    	Assert.assertEquals(placeName, city.getName());
    	Assert.assertEquals(new Double(lng) , new Double(city.getLocation().getX()));
    	Assert.assertEquals(new Double(lat) , new Double(city.getLocation().getY()));
    	Assert.assertEquals("P", city.getFeatureClass());
    	Assert.assertEquals("PPL", city.getFeatureCode());
    	Assert.assertEquals(GISSource.GEONAMES_ZIP, city.getSource());
    	Assert.assertEquals(countryCode, city.getCountryCode());
    	Assert.assertNotNull(city.getZipCodes());
    	Assert.assertEquals(1, city.getZipCodes().size());
    	Assert.assertEquals(new ZipCode("post"), city.getZipCodes().iterator().next());
    	EasyMock.verify(cityDaoMock);
    	EasyMock.verify(admDaoMock);
    	EasyMock.verify(idGenerator);
    	
    	
    }

}
