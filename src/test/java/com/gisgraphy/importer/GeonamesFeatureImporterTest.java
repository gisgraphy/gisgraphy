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
package com.gisgraphy.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.Lake;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IGisDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.valueobject.NameValueDTO;

public class GeonamesFeatureImporterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void rollbackShouldRollback() {
	GeonamesFeatureSimpleImporter featureImporter = new GeonamesFeatureSimpleImporter();
	ICityDao mockCityDao = EasyMock.createMock(ICityDao.class);
	EasyMock.expect(mockCityDao.deleteAll()).andReturn(2);
	EasyMock.expect(mockCityDao.getPersistenceClass()).andStubReturn(
		City.class);
	EasyMock.replay(mockCityDao);
	IGisDao<Lake> mockLakeDao = EasyMock.createMock(IGisDao.class);
	EasyMock.expect(mockLakeDao.deleteAll()).andReturn(0);
	EasyMock.expect(mockLakeDao.getPersistenceClass()).andStubReturn(
		Lake.class);
	EasyMock.replay(mockLakeDao);
	IGisFeatureDao mockGisDao = EasyMock.createMock(IGisFeatureDao.class);
	EasyMock.expect(mockGisDao.deleteAllExceptAdmsAndCountries())
		.andReturn(65);
	EasyMock.expect(mockGisDao.getPersistenceClass()).andStubReturn(
		GisFeature.class);
	EasyMock.replay(mockGisDao);
	IGisDao<? extends GisFeature>[] daoList = new IGisDao[2];
	daoList[0] = mockCityDao;
	daoList[1] = mockLakeDao;
	featureImporter.setGisFeatureDao(mockGisDao);
	featureImporter.setIDaos(daoList);
	List<NameValueDTO<Integer>> deleted = featureImporter.rollback();
	assertEquals(
		"if zero elements are deleted(lake), it should not have an entry",
		2, deleted.size());
    }
    
    @Test
    public void shouldBeSkipShouldReturnCorrectValue(){
	ImporterConfig importerConfig = new ImporterConfig();
	GeonamesFeatureSimpleImporter geonamesFeatureImporter = new GeonamesFeatureSimpleImporter();
	geonamesFeatureImporter.setImporterConfig(importerConfig);
	
	importerConfig.setGeonamesImporterEnabled(false);
	Assert.assertTrue(geonamesFeatureImporter.shouldBeSkipped());
	
	importerConfig.setGeonamesImporterEnabled(true);
	Assert.assertFalse(geonamesFeatureImporter.shouldBeSkipped());
		
    }
    
    @Test
    public void isAdmModeShouldBeFalse(){
    	GeonamesFeatureSimpleImporter importer = new GeonamesFeatureSimpleImporter();
    	Assert.assertFalse(importer.isAdmMode());
    }
    
    @Test
    public void isPlacetypeAccepted() {
	assertEquals(
			"ADM|COUNTRY|CITY$",
	ImporterConfig.DEFAULT_ACCEPT_REGEX_CITY);
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setAcceptRegExString(ImporterConfig.DEFAULT_ACCEPT_REGEX_CITY);
	GeonamesFeatureSimpleImporter importer = new GeonamesFeatureSimpleImporter();
	importer.setImporterConfig(importerConfig);
	importer.setup();
	
	
	// must be false
	assertFalse(importer.isPlaceTypeAccepted("foo"));
	assertFalse(importer.isPlaceTypeAccepted("Ci"));
	// must be true
	assertTrue(importer.isPlaceTypeAccepted("city"));
	assertTrue(importer.isPlaceTypeAccepted("ADM"));
	assertTrue(importer.isPlaceTypeAccepted("CITY"));

    }
    
    @Test
    public void isPlacetypeAccepted_Accept_all() {
	assertEquals(
			"ADM|COUNTRY|CITY$",
	ImporterConfig.DEFAULT_ACCEPT_REGEX_CITY);
	ImporterConfig importerConfig = new ImporterConfig();
	importerConfig.setAcceptRegExString(ImporterConfig.ACCEPT_ALL_REGEX_OPTION);
	GeonamesFeatureSimpleImporter importer = new GeonamesFeatureSimpleImporter();
	importer.setImporterConfig(importerConfig);
	importer.setup();
	
	
	// must be false
	assertTrue(importer.isPlaceTypeAccepted("foo"));
	assertTrue(importer.isPlaceTypeAccepted("Ci"));
	// must be true
	assertTrue(importer.isPlaceTypeAccepted("city"));
	assertTrue(importer.isPlaceTypeAccepted("ADM"));
	assertTrue(importer.isPlaceTypeAccepted("CITY"));

    }
    
    @Test
    public void correctPlaceType(){
    	GeonamesFeatureSimpleImporter importer = new GeonamesFeatureSimpleImporter();
    	Assert.assertTrue(importer.correctPlaceType(new City(), "a1").getClass() == CitySubdivision.class);
    	Assert.assertTrue(importer.correctPlaceType(new City(), null).getClass() == City.class);
    	Assert.assertTrue(importer.correctPlaceType(new City(), "a").getClass() == City.class);
    	Assert.assertTrue(importer.correctPlaceType(new GisFeature(), "a").getClass() == GisFeature.class);
    	
    	Assert.assertNull(importer.correctPlaceType(null, "a"));
    	Assert.assertTrue(importer.correctPlaceType(new GisFeature(), "a").getClass()== GisFeature.class);
    	Assert.assertNull(importer.correctPlaceType(null, null));
    }

}
