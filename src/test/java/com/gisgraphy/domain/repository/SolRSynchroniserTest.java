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
package com.gisgraphy.domain.repository;

import static com.gisgraphy.domain.valueobject.Pagination.paginate;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.Language;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeleteAllEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeletedEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureStoredEvent;
import com.gisgraphy.domain.geoloc.entity.event.PlaceTypeDeleteAllEvent;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.fulltext.FullTextFields;
import com.gisgraphy.fulltext.FullTextSearchException;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextQuerySolrHelper;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IsolrClient;
import com.gisgraphy.fulltext.spell.ISpellCheckerIndexer;
import com.gisgraphy.geoloc.GisgraphyCommunicationException;
import com.gisgraphy.helper.FileHelper;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.street.HouseNumberSerializer;
import com.gisgraphy.street.StreetType;
import com.gisgraphy.test.FeedChecker;
import com.gisgraphy.test.GisgraphyTestHelper;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class SolRSynchroniserTest extends AbstractIntegrationHttpSolrTestCase {

    @Resource
    private ICityDao cityDao;
    
    @Resource
    private OpenStreetMapDao openStreetMapDao;

    @Resource
    private ICountryDao countryDao;
    
    @Resource
    private IAdmDao admDao;

    @Resource
    private GisgraphyTestHelper geolocTestHelper;
    
    @Resource
    private ILanguageDao languageDao;
    
    @Resource
    private ISpellCheckerIndexer spellCheckerIndexer;

    @Test
    public void testDeleteAllShouldResetTheIndex() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());

	this.solRSynchroniser.commit();
	QueryResponse resultsAfterRemove = searchInFulltextSearchEngine("my city");

	int resultsSize = resultsAfterRemove.getResults().size();
	assertEquals("The city hasn't been saved", 1, resultsSize);
	assertEquals("The city hasn't been saved", saved.getFeatureId(),
		resultsAfterRemove.getResults().get(0).getFieldValue(
			FullTextFields.FEATUREID.getValue()));

	this.solRSynchroniser.deleteAll();
	resultsAfterRemove = searchInFulltextSearchEngine("my city");
	assertTrue("the index is not reset ", resultsAfterRemove.getResults()
		.isEmpty());

    }

    @Test
    public void testSolrSynchroniserConstructorCanNotHaveNullParam() {
	try {
	    new SolRSynchroniser(null);
	    fail("SolrSynchroniser can not have null parameters");
	} catch (IllegalArgumentException e) {
	}
    }

    @Test
    public void testSavingAgisFeatureShouldSynchronize() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(1L);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());
	this.solRSynchroniser.commit();
	QueryResponse resultsAfterRemove = searchInFulltextSearchEngine("my city");

	int resultsSize = resultsAfterRemove.getResults().size();
	assertEquals("The city hasn't been saved", 1, resultsSize);
	assertEquals("The city hasn't been saved", saved.getFeatureId(),
		resultsAfterRemove.getResults().get(0).getFieldValue(
			FullTextFields.FEATUREID.getValue()));
    }

    /*
     * @Test public void
     * testSavingAgisFeatureShouldNotSynchronizeNonFullTextSearchable(){ City
     * city = new City(){ @Transient public boolean isFullTextSearchable() {
     * return false; } }; city.setFeatureId(1L); city.setName("name");
     * city.setLocation(GeolocTestHelper.createPoint(1.4F, 2.4F)); City saved =
     * this.cityDao.save(city); assertNotNull(saved);
     * assertNotNull(saved.getId()); this.solRSynchroniser.commit(); for (int
     * i=0;i<10000;i++){ } List<City> results =this.cityDao.listFromText("my
     * city", true); assertTrue("A non fulltextsearchable gisFeature should not
     * be synchronised ",results.isEmpty()); }
     */

    @Test
    public void testCommitShouldReallyCommit() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(1L);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());

	QueryResponse searchResults = searchInFulltextSearchEngine("my city");

	assertTrue(
		"The fulltextSearchEngine should return an empty List before commit",
		searchResults.getResults().isEmpty());

	this.solRSynchroniser.commit();

	searchResults = searchInFulltextSearchEngine("my city");

	int resultsSize = searchResults.getResults().size();
	assertEquals("The city hasn't been saved", 1, resultsSize);
	assertEquals("The city hasn't been saved", saved.getFeatureId(),
		searchResults.getResults().get(0).getFieldValue(
			FullTextFields.FEATUREID.getValue()));

    }

    @Test
    public void testSavingAGisFeatureWithNullFeatureIdShouldNotSynchronize() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(null);
	try {
	    this.cityDao.save(city);
	    fail("A gisFeature with null gisFeature can not be saved");
	} catch (RuntimeException e) {

	}
    }

    @Test
    public void testSavingAGisFeatureWithNegativeFeatureIdShouldNotSynchronize() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(-1L);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());
	this.solRSynchroniser.commit();
	// for (int i = 0; i < 10000; i++) {
	// }
	List<City> results = this.cityDao.listFromText("my city", true);
	assertNotNull(results);
	assertTrue("a GisFeatureWith null featureId should not synchronize",
		results.isEmpty());
    }

    @Test
    public void testSavingAGisFeatureWithPointEqualsZeroShouldNotSynchronize() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 0F,
		0F);
	city.setFeatureId(1L);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());
	this.solRSynchroniser.commit();
	List<City> results = this.cityDao.listFromText("my city", true);
	assertNotNull(results);
	assertTrue("a GisFeatureWith null featureId should not synchronize",
		results.isEmpty());
    }
    
    @Test
    public void testDeleteAllGisFeaturesOfASpecificPlaceTypeShouldRetryOnFailure() throws SolrServerException, IOException {
	
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.deleteByQuery(((String)EasyMock.anyObject()))).andThrow(new SolrServerException("exception"));
	expect(mockSolrServer.deleteByQuery(((String)EasyMock.anyObject()))).andReturn(null);
	expect(mockSolrServer.commit(true, true)).andReturn(null);
	expect(mockSolrServer.optimize(true,true)).andReturn(null);
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	fakeSolrsynchroniser.handleEvent(new PlaceTypeDeleteAllEvent(City.class));
	EasyMock.verify(mockSolrServer);
    }
    
    @Test
    public void testDeleteAllGisFeaturesOfASpecificPlaceTypeShouldFailWhenMaxNumberOfRetryIsReached() throws SolrServerException, IOException {
	
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.deleteByQuery(((String)EasyMock.anyObject()))).andStubThrow(new SolrServerException("exception"));
	expect(mockSolrServer.commit(true, true)).andReturn(null);
	expect(mockSolrServer.optimize(true,true)).andReturn(null);
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	try {
	    fakeSolrsynchroniser.handleEvent(new PlaceTypeDeleteAllEvent(City.class));
	    fail("The solrSynchroniser should have throw");
	} catch (GisgraphyCommunicationException ignore) {
	}
    }
    
    @Test
    public void testDeleteAFeatureShouldRetryOnFailure() throws SolrServerException, IOException {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(2L);
	
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.deleteById("2")).andThrow(new SolrServerException("exception"));
	expect(mockSolrServer.deleteById("2")).andReturn(null);
	expect(mockSolrServer.commit(true, true)).andReturn(null);
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	fakeSolrsynchroniser.handleEvent(new GisFeatureDeletedEvent(city));
	EasyMock.verify(mockSolrServer);
    }
    
    @Test
    public void testDeleteAFeatureShouldFailWhenMaxNumberOfRetryIsReached() throws SolrServerException, IOException {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(2L);
	
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.deleteById("2")).andStubThrow(new SolrServerException("exception"));
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakesolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	try {
	    fakesolrsynchroniser.handleEvent(new GisFeatureDeletedEvent(city));
	    fail("The solrSynchroniser should have throw");
	} catch (GisgraphyCommunicationException ignore) {
	}

    }
    
    
    @Test
    public void testSaveAFeatureShouldRetryOnFailure() throws SolrServerException, IOException {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(2L);
	
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.add(((SolrInputDocument)EasyMock.anyObject()))).andThrow(new SolrServerException("exception"));
	expect(mockSolrServer.add(((SolrInputDocument)EasyMock.anyObject()))).andReturn(null);
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	fakeSolrsynchroniser.handleEvent(new GisFeatureStoredEvent(city));
	EasyMock.verify(mockSolrServer);
    }
    
    @Test
    public void testSaveAFeatureShouldFailWhenMaxNumberOfRetryIsReached() throws SolrServerException, IOException {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(2L);
	
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.add(((SolrInputDocument)EasyMock.anyObject()))).andStubThrow(new SolrServerException("exception"));
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakesolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	try {
	    fakesolrsynchroniser.handleEvent(new GisFeatureStoredEvent(city));
	    fail("The solrSynchroniser should have throw");
	} catch (GisgraphyCommunicationException ignore) {
	}

    }
    
    
    @Test
    public void testDeleteAListOfFeatureShouldRetryOnFailure() throws SolrServerException, IOException {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(2L);
	
	SolrServer mockSolrServer = EasyMock.createMock(SolrServer.class);
	expect(mockSolrServer.deleteById("2")).andThrow(new SolrServerException("exception"));
	expect(mockSolrServer.deleteById("2")).andReturn(null);
	expect(mockSolrServer.commit(true, true)).andReturn(null);
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	List<City> listOfFeature = new ArrayList<City>();
	listOfFeature.add(city);
	fakeSolrsynchroniser.handleEvent(new GisFeatureDeleteAllEvent(listOfFeature));
	EasyMock.verify(mockSolrServer);
    }
    
    @Test
    public void testCommitShouldRetryOnFailure() throws SolrServerException, IOException {
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.commit(true,true)).andThrow(new SolrServerException("exception"));
	expect(mockSolrServer.commit(true, true)).andReturn(null);
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	Assert.assertTrue("When a commit is success it must return true",fakeSolrsynchroniser.commit());
	EasyMock.verify(mockSolrServer);
    }
    
    @Test
    public void testOptimizeShouldRetryOnFailure() throws SolrServerException, IOException {
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.optimize(true,true)).andThrow(new SolrServerException("exception"));
	expect(mockSolrServer.optimize(true, true)).andReturn(null);
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	fakeSolrsynchroniser.optimize();
	EasyMock.verify(mockSolrServer);
    }
    
    @Test
    public void testOptimizeShouldFailWhenMaxNumberOfRetryIsReached() throws SolrServerException, IOException {
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.optimize(true,true)).andStubThrow(new SolrServerException("exception"));
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	try {
	    fakeSolrsynchroniser.optimize();
	} catch (GisgraphyCommunicationException ignore) {
	}
    }
    
    @Test
    public void testCommitShouldReturnFalseWhenMaxNumberOfRetryIsReached() throws SolrServerException, IOException {
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.commit(true,true)).andStubThrow(new SolrServerException("exception"));
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	assertFalse("When a commit fail it must return false",fakeSolrsynchroniser.commit());
	   
    }
    
    
    @Test
    public void testDeleteAListOfFeatureShouldFailWhenMaxNumberOfRetryIsReached() throws SolrServerException, IOException {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(2L);
	
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.deleteById("2")).andStubThrow(new SolrServerException("exception"));
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	List<City> listOfFeature = new ArrayList<City>();
	listOfFeature.add(city);
	try {
	    fakeSolrsynchroniser.handleEvent(new GisFeatureDeleteAllEvent(listOfFeature));
	    fail("The solrSynchroniser should have throw");
	} catch (GisgraphyCommunicationException e) {
	}
    }
    
    
    
    @Test
    public void testDeleteAllShouldRetryOnFailure() throws SolrServerException, IOException {
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.deleteByQuery("*:*")).andThrow(new SolrServerException("exception"));
	expect(mockSolrServer.deleteByQuery("*:*")).andReturn(null);
	expect(mockSolrServer.commit(true, true)).andReturn(null);
	expect(mockSolrServer.optimize(true,true)).andReturn(null);
	EasyMock.replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	fakeSolrsynchroniser.deleteAll();
	EasyMock.verify(mockSolrServer);
    }
    
    @Test
    public void testDeleteAllShouldFailWhenMaxNumberOfRetryIsReached() throws SolrServerException, IOException {
	SolrServer mockSolrServer = createMock(SolrServer.class);
	expect(mockSolrServer.deleteByQuery("*:*")).andStubThrow((new SolrServerException("exception")));
	replay(mockSolrServer);
	
	IsolrClient mockSolrClient = createMock(IsolrClient.class);
	expect(mockSolrClient.getServer()).andStubReturn(mockSolrServer);
	replay(mockSolrClient);
	
	ISolRSynchroniser fakeSolrsynchroniser = new SolRSynchroniser(mockSolrClient);
	
	try {
	    fakeSolrsynchroniser.deleteAll();
	    fail("The solrSynchroniser should have throw");
	} catch (GisgraphyCommunicationException ignore) {
	}
    }
    
    
  

    @Test
    public void testDeleteAgisFeatureShouldSynchronize() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(1L);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());
	this.solRSynchroniser.commit();
	QueryResponse searchResults = searchInFulltextSearchEngine("my city");

	int resultsSize = searchResults.getResults().size();
	assertEquals("The city hasn't been saved", 1, resultsSize);
	assertEquals("The city hasn't been saved", saved.getFeatureId(),
		searchResults.getResults().get(0).getFieldValue(
			FullTextFields.FEATUREID.getValue()));

	this.cityDao.remove(saved);

	searchResults = searchInFulltextSearchEngine("my city");

	assertTrue(
		"The city hasn't been removed from the full text search engine",
		searchResults.getResults().isEmpty());
    }

    @Test
    public void testDeleteAlistOfGisFeaturesShouldSynchronize() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(1L);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());
	this.solRSynchroniser.commit();
	// for (int i = 0; i < 10000; i++) {
	// }
	QueryResponse searchResults = searchInFulltextSearchEngine("my city");

	int resultsSize = searchResults.getResults().size();
	assertEquals("The city hasn't been saved", 1, resultsSize);
	assertEquals("The city hasn't been saved", saved.getFeatureId(),
		searchResults.getResults().get(0).getFieldValue(
			FullTextFields.FEATUREID.getValue()));

	List<City> listToRemove = new ArrayList<City>();
	listToRemove.add(saved);
	this.cityDao.deleteAll(listToRemove);

	QueryResponse resultsAfterRemove = searchInFulltextSearchEngine("my city");

	resultsAfterRemove = searchInFulltextSearchEngine("my city");

	assertTrue(
		"The city hasn't been removed from the full text search engine",
		resultsAfterRemove.getResults().isEmpty());
    }

    @Test
    public void testDeleteAllGisFeaturesOfASpecificPlaceTypeShouldSynchronize() {
	City city = GisgraphyTestHelper.createCityAtSpecificPoint("my city", 1.5F,
		1.6F);
	city.setFeatureId(1L);
	City saved = this.cityDao.save(city);
	assertNotNull(saved);
	assertNotNull(saved.getId());
	this.solRSynchroniser.commit();
	QueryResponse searchResults = searchInFulltextSearchEngine("my city");

	int resultsSize = searchResults.getResults().size();
	assertEquals("The city hasn't been saved", 1, resultsSize);
	assertEquals("The city hasn't been saved", saved.getFeatureId(),
		searchResults.getResults().get(0).getFieldValue(
			FullTextFields.FEATUREID.getValue()));

	Country country = GisgraphyTestHelper.createCountryForFrance();
	Country savedCountry = this.countryDao.save(country);
	assertNotNull(savedCountry);
	assertNotNull(savedCountry.getId());
	this.solRSynchroniser.commit();

	searchResults = searchInFulltextSearchEngine("france");

	resultsSize = searchResults.getResults().size();
	assertEquals("The country hasn't been saved", 1, resultsSize);
	assertEquals("The country hasn't been saved", savedCountry
		.getFeatureId(), searchResults.getResults().get(0)
		.getFieldValue(FullTextFields.FEATUREID.getValue()));

	this.cityDao.deleteAll();

	QueryResponse resultsAfterRemove = searchInFulltextSearchEngine("my city");

	assertTrue(
		"The city hasn't been removed from the full text search engine",
		resultsAfterRemove.getResults().isEmpty());

	searchResults = searchInFulltextSearchEngine("france");

	resultsSize = searchResults.getResults().size();
	assertEquals("The country should still be in the datastore", 1,
		resultsSize);
	assertEquals("The country should still be in the datastore",
		savedCountry.getFeatureId(), searchResults.getResults().get(0)
			.getFieldValue(FullTextFields.FEATUREID.getValue()));
    }
    
   
    @Test
    public void testSynchronize() {
	Long featureId = 1001L;
	City paris = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(featureId);
	// commit changes
	this.solRSynchroniser.commit();
	//buildIndex
	Map<String,Boolean> spellChekerResultMap = spellCheckerIndexer.buildAllIndex();
	for (String key : spellChekerResultMap.keySet()){
	    assertTrue(spellChekerResultMap.get(key).booleanValue());
	}
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	File file = new File(tempDir.getAbsolutePath()
		+ System.getProperty("file.separator") + "serialize.txt");

	OutputStream outputStream = null;
	try {
	    outputStream = new FileOutputStream(file);
	} catch (FileNotFoundException e1) {
	    fail();
	}

	try {
	    Pagination pagination = paginate().from(1).to(10);
	    Output output = Output.withFormat(OutputFormat.XML)
		    .withLanguageCode("FR").withStyle(OutputStyle.FULL)
		    .withIndentation();
	    FulltextQuery fulltextQuery = new FulltextQuery("Saint-Andre",
		    pagination, output, com.gisgraphy.fulltext.Constants.ONLY_CITY_PLACETYPE, "fr").withSpellChecking();
	    fullTextSearchEngine.executeAndSerialize(fulltextQuery,
		    outputStream);
	} catch (FullTextSearchException e) {
	    fail("error during search : " + e.getMessage());
	}

	String content = "";
	try {
	    content = GisgraphyTestHelper.readFileAsString(file.getAbsolutePath());
	} catch (IOException e) {
	    fail("can not get content of file " + file.getAbsolutePath());
	}

	Iterator<ZipCode> ZipIterator = paris.getZipCodes().iterator();
	FeedChecker.assertQ(
		"The query return incorrect values",
		content,
		"//*[@numFound='1']",
		"//*[@name='status'][.='0']"
		// name
		,
		"//*[@name='" + FullTextFields.NAME.getValue()
			+ "'][.='Saint-André']",
		"//*[@name='" + FullTextFields.NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
			+ "'][./str[1]][.='cityalternate']",
		"//*[@name='" + FullTextFields.NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue()
			+ "FR'][./str[1]][.='cityalternateFR']"

		,
		"//*[@name='" + FullTextFields.ADM3NAME.getValue()
			+ "'][.='admParent']"
		// adm1
		,
		"//*[@name='" + FullTextFields.ADM1CODE.getValue()
			+ "'][.='A1']",
		"//*[@name='" + FullTextFields.ADM1NAME.getValue()
			+ "'][.='admGrandGrandParent']",
		"//*[@name='" + FullTextFields.ADM1NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
			+ "'][./str[1]='admGGPalternate']",
		"//*[@name='" + FullTextFields.ADM1NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
			+ "'][./str[2]='admGGPalternate2']",
		"//*[@name='" + FullTextFields.ADM1NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue()
			+ "FR'][./str[1]][.='admGGPalternateFR']"
		// adm2
		,
		"//*[@name='" + FullTextFields.ADM2CODE.getValue()
			+ "'][.='B2']",
		"//*[@name='" + FullTextFields.ADM2NAME.getValue()
			+ "'][.='admGrandParent']",
		"//*[@name='" + FullTextFields.ADM2NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
			+ "'][./str[1]][.='admGPalternate']",
		"//*[@name='" + FullTextFields.ADM2NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue()
			+ "FR'][./str[1]][.='admGPalternateFR']"
		// adm3
		,
		"//*[@name='" + FullTextFields.ADM3CODE.getValue()
			+ "'][.='C3']"
		// country
		,
		"//*[@name='" + FullTextFields.COUNTRYCODE.getValue()
			+ "'][.='FR']",
		"//*[@name='" + FullTextFields.COUNTRYNAME.getValue()
			+ "'][.='France']",
		"//*[@name='" + FullTextFields.COUNTRYNAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
			+ "'][./str[1]][.='francia']",
		"//*[@name='" + FullTextFields.COUNTRYNAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue()
			+ "FR'][./str[1]][.='franciaFR']"

		// property
		, "//*[@name='" + FullTextFields.FEATURECLASS.getValue()
			+ "'][.='P']",
		"//*[@name='" + FullTextFields.FEATURECODE.getValue()
			+ "'][.='PPL']", "//*[@name='"
			+ FullTextFields.FEATUREID.getValue() + "'][.='1001']",
			//since V 4.0 we have removed preprocessed field for performance reasons
		/*"//*[@name='" + FullTextFields.FULLY_QUALIFIED_NAME.getValue()
			+ "'][.='" + paris.getFullyQualifiedName(false) + "']",*/
		"//*[@name='" + FullTextFields.LAT.getValue() + "'][.='2.5']",
		"//*[@name='" + FullTextFields.LONG.getValue() + "'][.='1.5']",
		"//*[@name='" + FullTextFields.PLACETYPE.getValue()
			+ "'][.='City']", "//*[@name='"
			+ FullTextFields.POPULATION.getValue()
			+ "'][.='10000000']",
			"//*[@name='" + FullTextFields.ZIPCODE.getValue()
			+ "'][./str[1][.='"+ZipIterator.next().getCode()+"']]"
		, 
		"//*[@name='" + FullTextFields.ZIPCODE.getValue()
		+ "'][./str[2][.='"+ZipIterator.next().getCode()+"']]"
		, 
		"//*[@name='" + FullTextFields.NAMEASCII.getValue()
			+ "'][.='ascii']",
		"//*[@name='" + FullTextFields.AMENITY.getValue()
			+ "'][.='amenity']",
		"//*[@name='" + FullTextFields.MUNICIPALITY.getValue()
			+ "'][.='true']",
		"//*[@name='" + FullTextFields.ELEVATION.getValue()
			+ "'][.='13456']"
		, "//*[@name='"
			+ FullTextFields.GTOPO30.getValue() + "'][.='7654']",
		"//*[@name='" + FullTextFields.TIMEZONE.getValue()
			+ "'][.='Europe/Paris']"

			//since V 4.0 we have removed preprocessed field for performance reasons
		/*, "//*[@name='" + FullTextFields.COUNTRY_FLAG_URL.getValue()
			+ "'][.='"
			+ URLUtils.createCountryFlagUrl(paris.getCountryCode())
			+ "']"
		, "//*[@name='"
			+ FullTextFields.GOOGLE_MAP_URL.getValue()
			+ "'][.='"
			+ URLUtils.createGoogleMapUrl(paris.getLocation(),
				paris.getName()) + "']", "//*[@name='"
			+ FullTextFields.YAHOO_MAP_URL.getValue() + "'][.='"
			+ URLUtils.createYahooMapUrl(paris.getLocation())
			+ "']"
		, "//*[@name='"
			+ FullTextFields.OPENSTREETMAP_MAP_URL.getValue()
			+ "'][.='"
			+ URLUtils.createOpenstreetmapMapUrl(paris.getLocation()) + "']"*/
		,//spellchecker fields
		"//*[@name='" + FullTextFields.SPELLCHECK.getValue()
			+ "']"
		,"//*[@name='" + FullTextFields.SPELLCHECK_SUGGESTIONS.getValue()
			+ "']"
		,"//*[@name='" + FullTextFields.SPELLCHECK_SUGGESTIONS.getValue()
			+ "'][./lst[1][@name='andre'][./arr[1]/str[1]/.='andré']]"
		
	
	);

	// delete temp dir
	assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));

    }
    
    @Test
    public void testRemoveAnAlternateNameShouldUpdate() {
	Country country = GisgraphyTestHelper
		.createFullFilledCountry();
	
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);
	
	Language lang2 = new Language("english", "EN", "ENG");
	Language savedLang2 = languageDao.save(lang2);
	Language retrievedLang2 = languageDao.get(savedLang2.getId());
	assertEquals(savedLang2, retrievedLang2);

	country.addSpokenLanguage(lang2);
	country.addSpokenLanguage(lang);
	
	AlternateName alternateName = new AlternateName("alternate",AlternateNameSource.ALTERNATENAMES_FILE);
	country.addAlternateName(alternateName);
	AlternateName alternateName2 = new AlternateName("alternate 2",AlternateNameSource.ALTERNATENAMES_FILE);
	country.addAlternateName(alternateName);
	country.addAlternateName(alternateName2);
	
	String CountryName = "France";
	country.setName(CountryName);
	countryDao.save(country);
	// commit changes
	this.solRSynchroniser.commit();
	
	

	
	FulltextResultsDto results ;
	    Pagination pagination = paginate().from(1).to(10);
	    Output output = Output.withFormat(OutputFormat.XML)
		    .withLanguageCode("FR").withStyle(OutputStyle.FULL)
		    .withIndentation();
	    FulltextQuery fulltextQuery = new FulltextQuery(CountryName,
		    pagination, output, new Class[]{Country.class},null).withoutSpellChecking();
	    results = fullTextSearchEngine.executeQuery(fulltextQuery);
	    Assert.assertEquals(1, results.getResults().size());
		Assert.assertEquals(country.getAlternateNames().size(),results.getResults().get(0).getName_alternates().size());

	country.getAlternateNames().remove(0);
	country.getAlternateNames().size();
	
	countryDao.save(country);
	// commit changes
	this.solRSynchroniser.commit();
	
	    fulltextQuery = new FulltextQuery(CountryName,
		    pagination, output, new Class[]{Country.class},null).withoutSpellChecking();
	    results = fullTextSearchEngine.executeQuery(fulltextQuery);
	    Assert.assertEquals(1, results.getResults().size());
		Assert.assertEquals(country.getAlternateNames().size(),results.getResults().get(0).getName_alternates().size());
	
	
	
	
    }
	
    
    @Test
    public void testSynchronizeAcountryShouldSynchronizeCountrySpecificFields() {
	Country country = GisgraphyTestHelper
		.createFullFilledCountry();
	
	Language lang = new Language("french", "FR", "FRA");
	Language savedLang = languageDao.save(lang);
	Language retrievedLang = languageDao.get(savedLang.getId());
	assertEquals(savedLang, retrievedLang);
	
	Language lang2 = new Language("english", "EN", "ENG");
	Language savedLang2 = languageDao.save(lang2);
	Language retrievedLang2 = languageDao.get(savedLang2.getId());
	assertEquals(savedLang2, retrievedLang2);

	country.addSpokenLanguage(lang2);
	country.addSpokenLanguage(lang);
	
	AlternateName alternateNameLocalized = new AlternateName("alternateFR",AlternateNameSource.ALTERNATENAMES_FILE);
	alternateNameLocalized.setLanguage("FR");
	AlternateName alternateName = new AlternateName("alternate",AlternateNameSource.ALTERNATENAMES_FILE);
	country.addAlternateName(alternateName);
	country.addAlternateName(alternateNameLocalized);
	
	String CountryName = "France";
	country.setName(CountryName);
	countryDao.save(country);
	// commit changes
	this.solRSynchroniser.commit();
	File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	File file = new File(tempDir.getAbsolutePath()
		+ System.getProperty("file.separator") + "serialize.txt");

	OutputStream outputStream = null;
	try {
	    outputStream = new FileOutputStream(file);
	} catch (FileNotFoundException e1) {
	    fail();
	}

	try {
	    Pagination pagination = paginate().from(1).to(10);
	    Output output = Output.withFormat(OutputFormat.XML)
		    .withLanguageCode("FR").withStyle(OutputStyle.FULL)
		    .withIndentation();
	    FulltextQuery fulltextQuery = new FulltextQuery(CountryName,
		    pagination, output, new Class[]{Country.class},null).withoutSpellChecking();
	    fullTextSearchEngine.executeAndSerialize(fulltextQuery,
		    outputStream);
	} catch (FullTextSearchException e) {
	    fail("error during search : " + e.getMessage());
	}

	String content = "";
	try {
	    content = GisgraphyTestHelper.readFileAsString(file.getAbsolutePath());
	} catch (IOException e) {
	    fail("can not get content of file " + file.getAbsolutePath());
	}

	FeedChecker.assertQ(
		"The query return incorrect values",
		content,
		"//*[@numFound='1']",
		"//*[@name='status'][.='0']",
		"//*[@name='" + FullTextFields.CONTINENT.getValue()
			+ "'][.='"+country.getContinent()+"']",
		"//*[@name='" + FullTextFields.CURRENCY_CODE.getValue()
			+ "'][.='"+country.getCurrencyCode()+"']",
		"//*[@name='" + FullTextFields.CURRENCY_NAME.getValue()
			+ "'][.='"+country.getCurrencyName()+"']",
		"//*[@name='" + FullTextFields.CURRENCY_CODE.getValue()
			+ "'][.='"+country.getCurrencyCode()+"']",
		"//*[@name='" + FullTextFields.FIPS_CODE.getValue()
			+ "'][.='"+country.getFipsCode()+"']",
		"//*[@name='" + FullTextFields.ISOALPHA2_COUNTRY_CODE.getValue()
			+ "'][.='"+country.getIso3166Alpha2Code()+"']",
		"//*[@name='" + FullTextFields.ISOALPHA3_COUNTRY_CODE.getValue()
			+ "'][.='"+country.getIso3166Alpha3Code()+"']",
		"//*[@name='" + FullTextFields.POSTAL_CODE_MASK.getValue()
			+ "'][.='"+country.getPostalCodeMask()+"']",
		"//*[@name='" + FullTextFields.POSTAL_CODE_REGEX.getValue()
			+ "'][.='"+country.getPostalCodeRegex()+"']",
		"//*[@name='" + FullTextFields.PHONE_PREFIX.getValue()
			+ "'][.='"+country.getPhonePrefix()+"']",
		"//*[@name='" + FullTextFields.SPOKEN_LANGUAGES.getValue()
			+ "'][./str[1][.='"+country.getSpokenLanguages().get(0).getIso639LanguageName()+"']]"
		, "//*[@name='" + FullTextFields.SPOKEN_LANGUAGES.getValue()
		+ "'][./str[2][.='"+country.getSpokenLanguages().get(1).getIso639LanguageName()+"']]"
		, 
		"//*[@name='" + FullTextFields.TLD.getValue()
			+ "'][.='"+country.getTld()+"']",
		"//*[@name='" + FullTextFields.AREA.getValue()
			+ "'][.='"+country.getArea()+"']",
		"//*[@name='" + FullTextFields.NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
			+ "'][./str[1]][.='"+alternateName.getName()+"']",
		"//*[@name='" + FullTextFields.NAME.getValue()
			+ FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue()
			+ "FR'][./str[1]][.='"+alternateNameLocalized.getName()+"']"
			
	
	);

	// delete temp dir
	assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));

    }
    
    @Test
    public void testSynchronizeAcountryShouldSynchronizeAdmSpecificFields() {
    
    Adm adm = GisgraphyTestHelper
	.createAdm("AdmName", "FR", "A1", "B2", null, null, null, 2);

        admDao.save(adm);

        this.solRSynchroniser.commit();
        File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	File file = new File(tempDir.getAbsolutePath()
		+ System.getProperty("file.separator") + "serialize.txt");

	OutputStream outputStream = null;
	try {
	    outputStream = new FileOutputStream(file);
	} catch (FileNotFoundException e1) {
	    fail();
	}

	try {
	    Pagination pagination = paginate().from(1).to(10);
	    Output output = Output.withFormat(OutputFormat.XML)
		    .withLanguageCode("FR").withStyle(OutputStyle.FULL)
		    .withIndentation();
	    FulltextQuery fulltextQuery = new FulltextQuery(adm.getName(),
		    pagination, output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,null).withoutSpellChecking();
	    fullTextSearchEngine.executeAndSerialize(fulltextQuery,
		    outputStream);
	} catch (FullTextSearchException e) {
	    fail("error during search : " + e.getMessage());
	}

	String content = "";
	try {
	    content = GisgraphyTestHelper.readFileAsString(file.getAbsolutePath());
	} catch (IOException e) {
	    fail("can not get content of file " + file.getAbsolutePath());
	}

	FeedChecker.assertQ("The query return incorrect values",
		content,
		"//*[@numFound='1']",
		"//*[@name='status'][.='0']"
		// name
		,
		"//*[@name='" + FullTextFields.LEVEL.getValue()
			+ "'][.='"+adm.getLevel()+"']",
		"//*[@name='" + FullTextFields.ADM1CODE.getValue()
			+ "'][.='"+adm.getAdm1Code()+"']",
		"//*[@name='" + FullTextFields.ADM2CODE.getValue()
			+ "'][.='"+adm.getAdm2Code()+"']"
		
	);

	// delete temp dir
	assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));
    }
    
    
    public void testSynchronizeAStreetWithNullCountryCodeShouldNotThrows() {
    	Double length = 3.5D;
    	boolean oneWay = true;
    	StreetType streetType = StreetType.PATH;
    	String countryCode= null;
    	String name= "peter martin";
    	long featureId =12345l;
    	Float latitude = 4.5F;
		Float longitude=3.9F;
		Point location = GeolocHelper.createPoint(longitude, latitude);
		LineString shape = GeolocHelper.createLineString("LINESTRING (30.001 30.001, 40 40)");
    	
		OpenStreetMap street = new OpenStreetMap();
    	street.setName(name);
    	street.setLength(length);
    	street.setOneWay(oneWay);
    	street.setStreetType(streetType);
    	street.setCountryCode(countryCode);
    	street.setGid(featureId);
    	street.setLocation(location);
    	street.setOpenstreetmapId(1234L);
    	street.setShape(shape);
   

    	openStreetMapDao.save(street);
    }
    
    public void testSynchronizeAStreetWithNullNameCodeShouldNotThrows() {
    	Double length = 3.5D;
    	boolean oneWay = true;
    	StreetType streetType = StreetType.PATH;
    	String countryCode= "FR";
    	String name= null;
    	Long featureId =12345l;
    	Float latitude = 4.5F;
		Float longitude=3.9F;
		Point location = GeolocHelper.createPoint(longitude, latitude);
		LineString shape = GeolocHelper.createLineString("LINESTRING (30.001 30.001, 40 40)");
    	
		OpenStreetMap street = new OpenStreetMap();
    	street.setName(name);
    	street.setLength(length);
    	street.setOneWay(oneWay);
    	street.setStreetType(streetType);
    	street.setCountryCode(countryCode);
    	street.setGid(featureId);
    	street.setOpenstreetmapId(1234L);
    	street.setLocation(location);
    	street.setShape(shape);

    	openStreetMapDao.save(street);
    	
    }
    
    public void testSynchronizeStreetShouldNotSendNameIfItIsEmpty() {
    	Double length = 3.5D;
    	boolean oneWay = true;
    	StreetType streetType = StreetType.PATH;
    	String countryCode= "FR";
    	String name= "";
    	Long featureId =12345l;
    	Float latitude = 4.5F;
		Float longitude=3.9F;
		Point location = GeolocHelper.createPoint(longitude, latitude);
		LineString shape = GeolocHelper.createLineString("LINESTRING (30.001 30.001, 40 40)");
    	
		OpenStreetMap street = new OpenStreetMap();
    	street.setName(name);
    	street.setLength(length);
    	street.setOneWay(oneWay);
    	street.setStreetType(streetType);
    	street.setCountryCode(countryCode);
    	street.setGid(featureId);
    	street.setOpenstreetmapId(1234L);
    	street.setLocation(location);
    	street.setShape(shape);

    	openStreetMapDao.save(street);
    	solRSynchroniser.commit();
    	
    	FulltextQuery query =new FulltextQuery(FulltextQuerySolrHelper.FEATUREID_PREFIX+featureId.toString());
    	FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
    	Assert.assertEquals(1, results.getResultsSize());
    	Assert.assertNull(results.getResults().get(0).getName());
    }
    
    public void testSynchronizeStreetShouldNotSendIsInIfItIsEmpty() {
    	Double length = 3.5D;
    	boolean oneWay = true;
    	StreetType streetType = StreetType.PATH;
    	String countryCode= "FR";
    	String name= "foo";
    	Long featureId =12345l;
    	Float latitude = 4.5F;
		Float longitude=3.9F;
		Point location = GeolocHelper.createPoint(longitude, latitude);
		LineString shape = GeolocHelper.createLineString("LINESTRING (30.001 30.001, 40 40)");
    	
		OpenStreetMap street = new OpenStreetMap();
    	street.setName(name);
    	street.setLength(length);
    	street.setOneWay(oneWay);
    	street.setStreetType(streetType);
    	street.setCountryCode(countryCode);
    	street.setGid(featureId);
    	street.setOpenstreetmapId(1234L);
    	street.setLocation(location);
    	street.setShape(shape);
    	street.setIsIn("");

    	openStreetMapDao.save(street);
    	solRSynchroniser.commit();
    	
    	FulltextQuery query =new FulltextQuery(name);
    	FulltextResultsDto results = fullTextSearchEngine.executeQuery(query);
    	Assert.assertEquals(1, results.getResultsSize());
    	Assert.assertNull(results.getResults().get(0).getIs_in());
    }
    
    @Test
    public void testSynchronizeAStreetShouldSynchronizeStreetSpecificFields() {
    	Double length = 3.5D;
    	boolean oneWay = true;
    	StreetType streetType = StreetType.PATH;
    	String countryCode= "FR";
    	String name= "peter martin";
    	long featureId =12345l;
    	long openstreetmapId =56789l;
    	Float latitude = 4.5F;
		Float longitude=3.9F;
		Point location = GeolocHelper.createPoint(longitude, latitude);
		LineString shape = GeolocHelper.createLineString("LINESTRING (30.001 30.001, 40 40)");
		String isIn = "los angeles";
		String isInPlace = "french quarter";
		String isInAdm = "california";
		Set<String> isInZip = new HashSet<String>();
		isInZip.add("90001");
		isInZip.add("90002");
		String fullyQualifiedAddress = "fullyQualifiedAddress";
		String altname1 = "alt name 1";
		String altname2 = "alt name 2";
		HouseNumber houseNumber1 = new HouseNumber();
		houseNumber1.setNumber("4");
		houseNumber1.setLocation(GeolocHelper.createPoint(4F, 3F));
		
		HouseNumber houseNumber2 = new HouseNumber();
		houseNumber2.setNumber("3");
		houseNumber2.setLocation(GeolocHelper.createPoint(6F, 5F));
		
		List<HouseNumber> houseNumbers = new ArrayList<HouseNumber>();
		//we put the number in a wrong order to see if the are sorted
		houseNumbers.add(houseNumber1);
		houseNumbers.add(houseNumber2);
		
		List<AlternateOsmName> alternateNames = new ArrayList<AlternateOsmName>();
		alternateNames.add(new AlternateOsmName(altname1,AlternateNameSource.OPENSTREETMAP));
		alternateNames.add(new AlternateOsmName(altname2,AlternateNameSource.OPENSTREETMAP));
		
    	
		OpenStreetMap street = new OpenStreetMap();
    	street.setName(name);
    	street.setOpenstreetmapId(openstreetmapId);
    	street.setLength(length);
    	street.setOneWay(oneWay);
    	street.setStreetType(streetType);
    	street.setCountryCode(countryCode);
    	street.setGid(featureId);
    	street.setOpenstreetmapId(1234L);
    	street.setLocation(location);
    	street.setShape(shape);
    	street.setIsIn(isIn);
    	street.setIsInAdm(isInAdm);
    	street.setIsInZip(isInZip);
    	street.setIsInPlace(isInPlace);
		street.setFullyQualifiedAddress(fullyQualifiedAddress);
		street.addHouseNumbers(houseNumbers);
		street.addAlternateNames(alternateNames);
   
		HouseNumberSerializer houseNumberSerializer = new HouseNumberSerializer();
    	openStreetMapDao.save(street);

        this.solRSynchroniser.commit();
        File tempDir = FileHelper.createTempDir(this.getClass()
		.getSimpleName());
	File file = new File(tempDir.getAbsolutePath()
		+ System.getProperty("file.separator") + "serialize.txt");

	OutputStream outputStream = null;
	try {
	    outputStream = new FileOutputStream(file);
	} catch (FileNotFoundException e1) {
	    fail();
	}

	try {
	    Pagination pagination = paginate().from(1).to(10);
	    Output output = Output.withFormat(OutputFormat.XML)
		    .withLanguageCode("FR").withStyle(OutputStyle.FULL)
		    .withIndentation();
	    FulltextQuery fulltextQuery = new FulltextQuery(name,
		    pagination, output, new Class[]{Street.class},null).withoutSpellChecking();
	    fullTextSearchEngine.executeAndSerialize(fulltextQuery,
		    outputStream);
	} catch (FullTextSearchException e) {
	    fail("error during search : " + e.getMessage());
	}

	String content = "";
	try {
	    content = GisgraphyTestHelper.readFileAsString(file.getAbsolutePath());
	} catch (IOException e) {
	    fail("can not get content of file " + file.getAbsolutePath());
	}

	Iterator<String> zipIterator = street.getIsInZip().iterator();
	FeedChecker.assertQ("The query return incorrect values",
		content,
		"//*[@numFound='1']",
		"//*[@name='status'][.='0']"
		// name
		,
		"//*[@name='" + FullTextFields.ONE_WAY.getValue()
			+ "'][.='"+street.isOneWay()+"']",
		"//*[@name='" + FullTextFields.LENGTH.getValue()
			+ "'][.='"+street.getLength()+"']",
		"//*[@name='" + FullTextFields.LAT.getValue()
			+ "'][.='"+street.getLatitude()+"']",
			"//*[@name='" + FullTextFields.LONG.getValue()
			+ "'][.='"+street.getLongitude()+"']",
			"//*[@name='" + FullTextFields.NAME.getValue()
			+ "'][.='"+street.getName()+"']",
			"//*[@name='" + FullTextFields.STREET_TYPE.getValue()
			+ "'][.='"+street.getStreetType()+"']",
			"//*[@name='" + FullTextFields.OPENSTREETMAP_ID.getValue()
			+ "'][.='"+street.getOpenstreetmapId()+"']",
			"//*[@name='" + FullTextFields.COUNTRYCODE.getValue()
			+ "'][.='"+street.getCountryCode()+"']",
			/*"//*[@name='" + FullTextFields.COUNTRY_FLAG_URL.getValue()
			+ "'][.='"+URLUtils.createCountryFlagUrl(street.getCountryCode())+"']",*/
			"//*[@name='" + FullTextFields.PLACETYPE.getValue()
			+ "'][.='"+Street.class.getSimpleName()+"']",
			"//*[@name='" + FullTextFields.IS_IN.getValue()
			+ "'][.='"+street.getIsIn()+"']",
			"//*[@name='" + FullTextFields.IS_IN_ADM.getValue()
			+ "'][.='"+street.getIsInAdm()+"']",
			"//*[@name='" + FullTextFields.IS_IN_PLACE.getValue()
			+ "'][.='"+street.getIsInPlace()+"']",
			"//*[@name='" + FullTextFields.IS_IN_ZIP.getValue()
			+ "'][./str[1]/.='"+zipIterator.next()+"']",
			"//*[@name='" + FullTextFields.IS_IN_ZIP.getValue()
			+ "'][./str[2]/.='"+zipIterator.next()+"']",
			/*"//*[@name='" + FullTextFields.FULLY_QUALIFIED_ADDRESS.getValue()
			+ "'][.='"+street.getFullyQualifiedAddress()+"']",*/
			//we check the order too
			"//*[@name='" + FullTextFields.HOUSE_NUMBERS.getValue()
			+ "'][./str[1][.='"+houseNumberSerializer.serialize(houseNumber2)+"']]"
		, "//*[@name='" + FullTextFields.HOUSE_NUMBERS.getValue()
		+ "'][./str[2][.='"+houseNumberSerializer.serialize(houseNumber1)+"']]",
		//altnames
		"//*[@name='" + FullTextFields.NAME.getValue()
		+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
		+ "'][./str[1]]['"+altname1+"']"
		,"//*[@name='" + FullTextFields.NAME.getValue()
		+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue()
		+ "'][./str[2]]['"+altname2+"']"
		
	);

	// delete temp dir
	assertTrue("the tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));
    }

    private QueryResponse searchInFulltextSearchEngine(String searchWords) {
	SolrQuery query = new SolrQuery();
	String namefield = FullTextFields.ALL_NAME.getValue();

	String queryString = "(" + namefield + ":\"" + searchWords + "\")";
	query.setQuery(queryString);
	query.setQueryType(Constants.SolrQueryType.advanced.toString());
	query.setFields(FullTextFields.FEATUREID.getValue());

	QueryResponse resultsAfterRemove = null;
	try {
	    resultsAfterRemove = solrClient.getServer().query(query);
	} catch (SolrServerException e) {
	    fail();
	}
	return resultsAfterRemove;
    }

}
