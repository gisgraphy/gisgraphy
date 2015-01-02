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

import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.valueobject.NameValueDTO;

public class GeonamesAdm1ImporterTest {

    @Test
    public void rollbackShouldRollback() {
	GeonamesAdm1Importer geonamesAdm1Importer = new GeonamesAdm1Importer();
	IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
	EasyMock.expect(admDao.deleteAllByLevel(1)).andReturn(4);
	EasyMock.replay(admDao);
	geonamesAdm1Importer.setAdmDao(admDao);
	List<NameValueDTO<Integer>> deleted = geonamesAdm1Importer.rollback();
	assertEquals(1, deleted.size());
	assertEquals(4, deleted.get(0).getValue().intValue());
    }
    
    @Test
    public void shouldBeSkipShouldReturnCorrectValue(){
	ImporterConfig importerConfig = new ImporterConfig();
	GeonamesAdm1Importer geonamesAdm1Importer = new GeonamesAdm1Importer();
	geonamesAdm1Importer.setImporterConfig(importerConfig);
	
	importerConfig.setGeonamesImporterEnabled(false);
	Assert.assertTrue(geonamesAdm1Importer.shouldBeSkipped());
	
	importerConfig.setGeonamesImporterEnabled(true);
	Assert.assertFalse(geonamesAdm1Importer.shouldBeSkipped());
		
    }
    
    @Test
    public void isAdmModeShouldBeTrue(){
    	GeonamesAdm1Importer importer = new GeonamesAdm1Importer();
    	Assert.assertTrue(importer.isAdmMode());
    }

}
