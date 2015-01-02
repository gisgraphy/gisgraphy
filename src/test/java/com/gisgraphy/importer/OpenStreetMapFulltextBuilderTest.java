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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.IdGenerator;
import com.gisgraphy.domain.repository.OpenStreetMapDao;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.service.IInternationalisationService;

public class OpenStreetMapFulltextBuilderTest {

	@Test
	public void testShouldBeSkiped() {
		boolean savedValue = GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE;
		try {
			GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE=true;
			ImporterConfig importerConfig = new ImporterConfig();
			OpenStreetMapFulltextBuilder openStreetMapFulltextBuilderTobeSkipped = new OpenStreetMapFulltextBuilder();
			openStreetMapFulltextBuilderTobeSkipped.setImporterConfig(importerConfig);

			//test with features enabled
			GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE=false;
			importerConfig.setOpenstreetmapImporterEnabled(false);
			assertTrue(openStreetMapFulltextBuilderTobeSkipped.shouldBeSkipped());

			importerConfig.setOpenstreetmapImporterEnabled(true);
			assertTrue(openStreetMapFulltextBuilderTobeSkipped.shouldBeSkipped());
			
			//test with features enabled
			GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE=true;
			importerConfig.setOpenstreetmapImporterEnabled(false);
			assertTrue(openStreetMapFulltextBuilderTobeSkipped.shouldBeSkipped());

			importerConfig.setOpenstreetmapImporterEnabled(true);
			assertFalse(openStreetMapFulltextBuilderTobeSkipped.shouldBeSkipped());
		} finally {
			GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE=savedValue;
		}
	}

	@Test
	public void testSetupShouldCreateTheIndex() {
		OpenStreetMapFulltextBuilder builder = new OpenStreetMapFulltextBuilder();
		IOpenStreetMapDao openStreetMapDao = createMock(IOpenStreetMapDao.class);
		openStreetMapDao.createFulltextIndexes();
		replay(openStreetMapDao);

		IInternationalisationService internationalisationService = createMock(IInternationalisationService.class);
		String localizedString = "localizedString";
		expect(internationalisationService.getString((String) anyObject())).andStubReturn(localizedString);
		replay(internationalisationService);

		builder.setOpenStreetMapDao(openStreetMapDao);
		builder.setInternationalisationService(internationalisationService);

		builder.setup();

		assertEquals(localizedString, builder.getStatusMessage());
		verify(openStreetMapDao);
	}

	@Test
	public void testProcessWhenShouldBeSkipped() {
		OpenStreetMapFulltextBuilder builder = new OpenStreetMapFulltextBuilder() {
			@Override
			public boolean shouldBeSkipped() {
				return true;
			}

			@Override
			protected void setup() {
			}
		};

		builder.process();

		assertEquals(ImporterStatus.SKIPPED, builder.getStatus());
		assertEquals("", builder.getStatusMessage());
	}

	@Test
	public void testProcess() {
		boolean savedValue= GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE;
		try {
			GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE=true;
			OpenStreetMapFulltextBuilder builder = new OpenStreetMapFulltextBuilder() {

				@Override
				public boolean shouldBeSkipped() {
					return false;
				}
			};
			IOpenStreetMapDao openStreetMapDao = createMock(IOpenStreetMapDao.class);
			openStreetMapDao.createFulltextIndexes();
			expect(openStreetMapDao.countEstimate()).andReturn(19999L);
			expect(openStreetMapDao.updateTS_vectorColumnForStreetNameSearchPaginate(IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT, IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT+builder.increment - 1)).andReturn(250);
			expect(openStreetMapDao.updateTS_vectorColumnForStreetNameSearchPaginate(IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT+builder.increment, IdGenerator.OPENSTREETMAP_GID_NO_CONFLICT+(builder.increment * 2) - 1)).andReturn(200);
			replay(openStreetMapDao);

			IInternationalisationService internationalisationService = createMock(IInternationalisationService.class);
			String localizedString = "localizedString";
			expect(internationalisationService.getString((String) anyObject())).andStubReturn(localizedString);
			replay(internationalisationService);

			builder.setOpenStreetMapDao(openStreetMapDao);
			builder.setInternationalisationService(internationalisationService);

			builder.process();

			assertEquals("", builder.getStatusMessage());
			assertEquals(ImporterStatus.PROCESSED, builder.getStatus());
			assertEquals(builder.getNumberOfLinesToProcess(), builder.getTotalReadLine());
			verify(openStreetMapDao);
		} finally {
			GisgraphyConfig.STREET_SEARCH_FULLTEXT_MODE=savedValue;
		}
	}

	@Test
	public void testRollback() throws Exception {
		OpenStreetMapFulltextBuilder builder = new OpenStreetMapFulltextBuilder();
		List<NameValueDTO<Integer>> dtoList = builder.rollback();
		Assert.assertNotNull(dtoList);
		Assert.assertEquals(0, dtoList.size());
		Assert.assertEquals(0, builder.getNumberOfLinesToProcess());
		Assert.assertEquals(0, builder.getTotalReadLine());
		Assert.assertEquals(0, builder.getReadFileLine());
		Assert.assertEquals(ImporterStatus.WAITING, builder.getStatus());
		Assert.assertEquals("", builder.getStatusMessage());
	}

	@Test
	public void testGetCurrentFileNameShouldReturnTheClassName() {
		OpenStreetMapFulltextBuilder builder = new OpenStreetMapFulltextBuilder();
		builder.setOpenStreetMapDao(new OpenStreetMapDao());
		assertEquals(OpenStreetMapFulltextBuilder.class.getSimpleName(), builder.getCurrentFileName());
	}

	@Test
	public void testResetStatusShouldReset() {
		OpenStreetMapFulltextBuilder builder = new OpenStreetMapFulltextBuilder() {
			@Override
			protected void setup() {
				throw new RuntimeException();
			}
		};
		try {
			builder.process();
			fail("The fulltextbuilder should have throws");
		} catch (RuntimeException ignore) {
		}
		Assert.assertTrue(builder.getStatusMessage().length() > 0);
		Assert.assertEquals(ImporterStatus.ERROR, builder.getStatus());
		builder.resetStatus();
		Assert.assertEquals(0, builder.getNumberOfLinesToProcess());
		Assert.assertEquals(0, builder.getTotalReadLine());
		Assert.assertEquals(0, builder.getReadFileLine());
		Assert.assertEquals(ImporterStatus.WAITING, builder.getStatus());
		Assert.assertEquals("", builder.getStatusMessage());
	}

}
