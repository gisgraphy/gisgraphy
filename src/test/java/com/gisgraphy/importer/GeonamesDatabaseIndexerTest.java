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

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.domain.repository.AdmDao;
import com.gisgraphy.domain.repository.CityDao;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.IGisDao;
import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.ImporterStatusDto;
import com.gisgraphy.service.IInternationalisationService;

public class GeonamesDatabaseIndexerTest extends AbstractTransactionalTestCase {
    
    public GeonamesDatabaseIndexer geonamesDatabaseIndexer;
    
    @Test
   public void testProcessPercentAndDTO(){
	geonamesDatabaseIndexer.process();
	assertEquals("statusMessage should be empty if the process is ok","", geonamesDatabaseIndexer.getStatusMessage());
	ImporterStatusDto status = new ImporterStatusDto(geonamesDatabaseIndexer);
	assertEquals(100, status.getPercent());
	Assert.assertEquals("curentFileName should be the default one at the end of the process",GeonamesDatabaseIndexer.DEFAULT_CURRENT_FILENAME, status.getCurrentFileName());
    }
    
    @Test
    public void testProcessShouldCallTheDao(){
	GeonamesDatabaseIndexer geonamesDatabaseIndexer = new GeonamesDatabaseIndexer();
 	ICityDao cityDao = EasyMock.createMock(CityDao.class);
 	cityDao.createGISTIndexForLocationColumn();
 	EasyMock.expect(cityDao.getPersistenceClass()).andReturn(City.class);
 	EasyMock.replay(cityDao);
 	
 	IAdmDao admDao = EasyMock.createMock(AdmDao.class);
 	admDao.createGISTIndexForLocationColumn();
 	EasyMock.expect(admDao.getPersistenceClass()).andReturn(Adm.class);
 	EasyMock.replay(admDao);
 	
 	IInternationalisationService internationalisationService = EasyMock.createMock(IInternationalisationService.class);
 	EasyMock.expect(internationalisationService.getString((String)EasyMock.anyObject(), ((Object[])EasyMock.anyObject()))).andStubReturn("");
 	geonamesDatabaseIndexer.internationalisationService= internationalisationService;
 	EasyMock.replay(internationalisationService);
 	
 	IGisDao[] daoArray = {cityDao,admDao};
 	geonamesDatabaseIndexer.daos= daoArray;
 	
 	ImporterConfig importerConfig = new ImporterConfig();
 	importerConfig.setGeonamesImporterEnabled(true);
 	
 	geonamesDatabaseIndexer.importerConfig =importerConfig;
 	
	geonamesDatabaseIndexer.process();
 	EasyMock.verify(cityDao);
 	EasyMock.verify(admDao);
     }
    
    @Test
    public void testShouldBeSkiped(){
	ImporterConfig importerConfig = new ImporterConfig();
	GeonamesDatabaseIndexer geonamesDatabaseIndexerTobeSkipped = new GeonamesDatabaseIndexer();
	geonamesDatabaseIndexerTobeSkipped.setImporterConfig(importerConfig);
	
	importerConfig.setGeonamesImporterEnabled(false);
	Assert.assertTrue(geonamesDatabaseIndexerTobeSkipped.shouldBeSkipped());
	
	importerConfig.setGeonamesImporterEnabled(true);
	Assert.assertFalse(geonamesDatabaseIndexerTobeSkipped.shouldBeSkipped());
    }

    
    public void setGeonamesDatabaseIndexer(GeonamesDatabaseIndexer geonamesDatabaseIndexer) {
        this.geonamesDatabaseIndexer = geonamesDatabaseIndexer;
    }
    
    @Test
    public void testResetStatusShouldReset() {
	GeonamesDatabaseIndexer indexer = new GeonamesDatabaseIndexer() {
			@Override
			protected void setup() {
				throw new RuntimeException();
			}
		};
		try {
			indexer.process();
		} catch (RuntimeException ignore) {
			fail("The GeonamesDatabaseIndexer should not throws");
		}
		Assert.assertTrue(indexer.getStatusMessage().length() > 0);
		Assert.assertEquals(ImporterStatus.ERROR, indexer.getStatus());
		Assert.assertNotNull("curentFileName should not be null if the process fail", indexer.getCurrentFileName());
		indexer.resetStatus();
		Assert.assertEquals(0, indexer.getNumberOfLinesToProcess());
		Assert.assertEquals(0, indexer.getTotalReadLine());
		Assert.assertEquals(0, indexer.getReadFileLine());
		Assert.assertEquals(ImporterStatus.WAITING, indexer.getStatus());
		Assert.assertEquals("", indexer.getStatusMessage());
		Assert.assertEquals("curentFileName should be null at the end of the reset",GeonamesDatabaseIndexer.DEFAULT_CURRENT_FILENAME, indexer.getCurrentFileName());
	}

}
