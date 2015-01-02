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
package com.gisgraphy.webapp.action;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.gisgraphy.importer.ImporterManager;


public class ResetImportActionTest {
	
	@Before
	public void setup(){
		BaseActionTestCase.setUpActionContext();
	}
	
	@Test
	public void executeShouldReturnImpossibleViewIfImportIsInProgress() throws Exception{
		ImporterManager importerManager = createMockImporterManager(true,null);
		
		ResetImportAction action = new ResetImportAction();
		action.setImporterManager(importerManager);
		
		String view = action.execute();
		Assert.assertEquals("when the import is in progress, the "+ResetImportAction.IMPORT_IN_PROGRESS+" view should be returned",ResetImportAction.IMPORT_IN_PROGRESS, view);
	}

	
	@Test
	public void executeShouldReturnAskViewIfImportIsNotInProgress() throws Exception{
		ImporterManager importerManager = createMockImporterManager(false, null);
		
		ResetImportAction action = new ResetImportAction();
		action.setImporterManager(importerManager);
		
		String view = action.execute();
		Assert.assertEquals("when the import is not in progress, the "+ResetImportAction.ASK+" view should be returned",ResetImportAction.ASK, view);
	}
	
	@Test
	public void resetShouldReturnImpossibleViewIfImportIsInProgress() throws Exception{
		ImporterManager importerManager = createMockImporterManager(true,null);
		
		ResetImportAction action = new ResetImportAction();
		action.setImporterManager(importerManager);
		
		String view = action.reset();
		Assert.assertEquals("when the import is in progress, the "+ResetImportAction.IMPORT_IN_PROGRESS+" view should be returned",ResetImportAction.IMPORT_IN_PROGRESS, view);
	}

	
	@Test
	public void resetShouldReturnAskViewIfImportIsNotInProgressAndResetIsNotConfirmed() throws Exception{
		ImporterManager importerManager = createMockImporterManager(false, null);
		
		ResetImportAction action = new ResetImportAction(){
			@Override
			public boolean isConfirmed() {
				return false;
			}
		};
		action.setImporterManager(importerManager);
		
		String view = action.reset();
		Assert.assertEquals("when the import is not in progress and reset is unconfirmed , the "+ResetImportAction.ASK+" view should be returned",ResetImportAction.ASK, view);
	}	
	
	
	@Test
	public void resetShouldResetIfImportIsNotInProgressAndResetIsConfirmed() throws Exception{
		List<String> errorsAndWarning = new ArrayList<String>();
		errorsAndWarning.add("fake error message");
		ImporterManager importerManager = createMockImporterManager(false,errorsAndWarning );
		
		ResetImportAction action = new ResetImportAction(){
			@Override
			public boolean isConfirmed() {
				return true;
			}
			
			@Override
			public void unconfirm() {
				//do nothing
			}
		};
		action.setImporterManager(importerManager);
		String view = action.reset();
		assertEquals("errors and warning should be populated in the action",errorsAndWarning, action.getErrorsAndWarningMessages());
		assertFalse("the reset fail status should be false if no exception is throws",action.isResetFailed());
		assertNull("the failedMessage should be null is no exception is throws", action.getFailedMessage());
		assertEquals("when the import is not in progress, the "+ResetImportAction.RESET+" view should be returned",ResetImportAction.RESET, view);
	}
	
	@Test
	public void resetWithExceptions() throws Exception{
		String exceptionMessage = "Error occured ! ";
		ImporterManager importerManager = createMock(ImporterManager.class);
		expect(importerManager.isInProgress()).andStubReturn(false);
		expect(importerManager.resetImport()).andStubThrow(new Exception(exceptionMessage));
		replay(importerManager);
		
		ResetImportAction action = new ResetImportAction(){
			@Override
			public boolean isConfirmed() {
				return true;
			}
			
			@Override
			public void unconfirm() {
				//do nothing
			}
		};
		action.setImporterManager(importerManager);
		String view = action.reset();
		Assert.assertTrue("the reset failed status should be true if no exception is throws",action.isResetFailed());
		Assert.assertEquals("the failedMessage should be set with the exception message",exceptionMessage, action.getFailedMessage());
		assertEquals("when the import is not in progress, the "+ResetImportAction.RESET+" view should be returned",ResetImportAction.RESET, view);
	}
	
	@Test
	public void confirmStuff() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(new MockHttpSession());
		
		ResetImportAction action = new ResetImportAction();
		ServletActionContext.setRequest(request);
		Assert.assertFalse("by default confirm should be false",action.isConfirmed());

		action.confirm();
		Assert.assertTrue("when confirm method is called, isConfirmed should return true",action.isConfirmed());
		
		action.unconfirm();
		Assert.assertFalse("When uncnfirmed is called, isConfirmed should return false",action.isConfirmed());
		
//		fail("change struts.xml+create view");
	}
	
	private ImporterManager createMockImporterManager(boolean isInProgress,List<String> errorAndWarningMessages) throws Exception {
		ImporterManager importerManager = createMock(ImporterManager.class);
		expect(importerManager.isInProgress()).andStubReturn(isInProgress);
		expect(importerManager.resetImport()).andStubReturn(errorAndWarningMessages);
		replay(importerManager);
		return importerManager;
	}
	
}
