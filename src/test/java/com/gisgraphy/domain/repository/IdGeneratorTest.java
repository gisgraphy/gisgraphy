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
package com.gisgraphy.domain.repository;

import net.sf.jstester.util.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

public class IdGeneratorTest {

	
	
	 @Test
	    public void syncWhenActualValuesAreInferiorToIncrement(){
		IdGenerator idgenerator = new IdGenerator() ;

		IGisFeatureDao gisFeatureDao = EasyMock.createMock(IGisFeatureDao.class);
		long maxFeatureId = 123456L;
		EasyMock.expect(gisFeatureDao.getMaxFeatureId()).andReturn(maxFeatureId);
		EasyMock.replay(gisFeatureDao);
		idgenerator.setGisFeatureDao(gisFeatureDao);

		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		long maxGid = 7890L;
		EasyMock.expect(openStreetMapDao.getMaxGid()).andReturn(maxGid);
		EasyMock.replay(openStreetMapDao);
		idgenerator.setOpenStreetMapDao(openStreetMapDao);
		
		idgenerator.sync();
		
		//check initial values
		Assert.assertEquals(IdGenerator.FEATUREID_INCREMENT_NO_CONFLICT, idgenerator.getFeatureId());
		Assert.assertEquals(Math.max(idgenerator.getFeatureId(), idgenerator.getGid()), idgenerator.getGid());
		
		//check increment values
		Assert.assertEquals(IdGenerator.FEATUREID_INCREMENT_NO_CONFLICT+1, idgenerator.getNextFeatureId());
		Assert.assertEquals(Math.max(idgenerator.getFeatureId(), idgenerator.getGid())+1, idgenerator.getNextGId());
		
		EasyMock.verify(gisFeatureDao);
		EasyMock.verify(openStreetMapDao);
		
		//Assert.assertEquals(maxFeatureId+importer.FEATUREID_INCREMENT, importer.generatedFeatureId);
		
	    }
	 
	 @Test
	    public void syncWhenActualValuesAreSuperiorToIncrement(){
		IdGenerator idgenerator = new IdGenerator() ;

		IGisFeatureDao gisFeatureDao = EasyMock.createMock(IGisFeatureDao.class);
		long maxFeatureId = IdGenerator.FEATUREID_INCREMENT_NO_CONFLICT+1000;
		EasyMock.expect(gisFeatureDao.getMaxFeatureId()).andReturn(maxFeatureId);
		EasyMock.replay(gisFeatureDao);
		idgenerator.setGisFeatureDao(gisFeatureDao);

		IOpenStreetMapDao openStreetMapDao = EasyMock.createMock(IOpenStreetMapDao.class);
		long maxGid = IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT+1000;
		EasyMock.expect(openStreetMapDao.getMaxGid()).andReturn(maxGid);
		EasyMock.replay(openStreetMapDao);
		idgenerator.setOpenStreetMapDao(openStreetMapDao);
		
		idgenerator.sync();
		
		//check initial values
		Assert.assertEquals(maxFeatureId, idgenerator.getFeatureId());
		Assert.assertEquals(Math.max(maxGid, maxFeatureId), idgenerator.getGid());
		
		//check increment values
		Assert.assertEquals(maxFeatureId+1, idgenerator.getNextFeatureId());
		Assert.assertEquals(Math.max(maxGid, maxFeatureId)+1, idgenerator.getNextGId());
		
		EasyMock.verify(gisFeatureDao);
		EasyMock.verify(openStreetMapDao);
		
		//Assert.assertEquals(maxFeatureId+importer.FEATUREID_INCREMENT, importer.generatedFeatureId);
		
	    }

}
