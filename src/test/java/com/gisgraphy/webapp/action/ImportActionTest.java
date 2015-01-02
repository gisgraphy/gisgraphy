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
package com.gisgraphy.webapp.action;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.importer.IImporterManager;
import com.gisgraphy.importer.ImporterManager;
import com.gisgraphy.importer.ImporterMetaDataException;

public class ImportActionTest {

    private IImporterManager createImporterManagerThatThrowsWhenIsAlreadyDoneIsCalled(String ErrorMessage) throws ImporterMetaDataException {
	IImporterManager mockImporterManager = createMock(ImporterManager.class);
	expect(mockImporterManager.isAlreadyDone()).andStubThrow(new ImporterMetaDataException(ErrorMessage));
	EasyMock.replay(mockImporterManager);
	return mockImporterManager;
    }

    @Test
    public void getImportFormatedTimeElapsed() {
	ImportAction action = new ImportAction();
	ImporterManager mockImporterManager = EasyMock.createMock(ImporterManager.class);
	String timeElapsed = "timeElapsed";
	EasyMock.expect(mockImporterManager.getFormatedTimeElapsed()).andReturn(timeElapsed);
	EasyMock.replay(mockImporterManager);
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(timeElapsed, action.getImportFormatedTimeElapsed());
	EasyMock.verify(mockImporterManager);

    }

    @Test
    public void isImportInProgress() {
	ImportAction action = new ImportAction();
	ImporterManager mockImporterManager = EasyMock.createMock(ImporterManager.class);
	boolean isInProgressState = true;
	EasyMock.expect(mockImporterManager.isInProgress()).andReturn(isInProgressState);
	EasyMock.replay(mockImporterManager);
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(isInProgressState, action.isImportInProgress());
	EasyMock.verify(mockImporterManager);
    }

    @Test
    public void isImportAlreadyDone() throws ImporterMetaDataException {
	ImportAction action = new ImportAction();
	ImporterManager mockImporterManager = EasyMock.createMock(ImporterManager.class);
	boolean alreadyDoneState = true;
	EasyMock.expect(mockImporterManager.isAlreadyDone()).andReturn(alreadyDoneState);
	EasyMock.replay(mockImporterManager);
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(alreadyDoneState, action.isImportAlreadyDone());
	EasyMock.verify(mockImporterManager);
    }

    @Test
    public void executeShouldReturnErrorViewIfIsALreadyDoneThrows() throws Exception {
	String ErrorMessage = "MyMessageToCheck";
	IImporterManager mockImporterManager = createImporterManagerThatThrowsWhenIsAlreadyDoneIsCalled(ErrorMessage);
	ImportAction action = new ImportAction();
	action.setImporterManager(mockImporterManager);
	assertEquals(ImportConfirmAction.ERROR, action.execute());
	assertEquals("incorect eror message ", ErrorMessage, action.getErrorMessage());
    }

    @Test
    public void statusShouldReturnErrorViewIfIsALreadyDoneThrows() throws Exception {
	String ErrorMessage = "MyMessageToCheck";
	IImporterManager mockImporterManager = createImporterManagerThatThrowsWhenIsAlreadyDoneIsCalled(ErrorMessage);
	ImportAction action = new ImportAction();
	action.setImporterManager(mockImporterManager);
	assertEquals(ImportConfirmAction.ERROR, action.status());
	assertEquals("incorect eror message ", ErrorMessage, action.getErrorMessage());
    }

    @Test
    public void statusShouldReturnWaitView() throws Exception {
	ImportAction action = new ImportAction();
	ImporterManager mockImporterManager = createMock(ImporterManager.class);
	expect(mockImporterManager.isAlreadyDone()).andStubReturn(false);
	EasyMock.replay(mockImporterManager);
	action.setImporterManager(mockImporterManager);
	assertEquals(ImportAction.WAIT, action.status());
    }

    @Test
    public void executeShouldReturnWaitViewIfInProgress() throws Exception {
	IImporterManager mockImporterManager = EasyMock.createMock(IImporterManager.class);
	EasyMock.expect(mockImporterManager.isInProgress()).andStubReturn(true);
	EasyMock.expect(mockImporterManager.isAlreadyDone()).andStubReturn(false);
	EasyMock.replay(mockImporterManager);
	ImportAction action = new ImportAction();
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(ImportAction.WAIT, action.execute());
    }

    @Test
    public void executeShouldImportIfNotInProgress() throws Exception {
	IImporterManager mockImporterManager = EasyMock.createMock(IImporterManager.class);
	EasyMock.expect(mockImporterManager.isInProgress()).andStubReturn(false);
	EasyMock.expect(mockImporterManager.isAlreadyDone()).andStubReturn(false);
	mockImporterManager.importAll();
	EasyMock.expectLastCall();
	EasyMock.replay(mockImporterManager);
	ImportAction action = new ImportAction();
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(ImportAction.SUCCESS, action.execute());
    }

    @Test
    public void executeShouldImportIfNotAlreadyDone() throws Exception {
	IImporterManager mockImporterManager = EasyMock.createMock(IImporterManager.class);
	EasyMock.expect(mockImporterManager.isInProgress()).andStubReturn(false);
	EasyMock.expect(mockImporterManager.isAlreadyDone()).andStubReturn(false);
	mockImporterManager.importAll();
	EasyMock.expectLastCall();
	EasyMock.replay(mockImporterManager);
	ImportAction action = new ImportAction();
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(ImportAction.SUCCESS, action.execute());
    }

    @Test
    public void executeShouldReturnWaitViewIfAlreadyDone() throws Exception {
	IImporterManager mockImporterManager = EasyMock.createMock(IImporterManager.class);
	EasyMock.expect(mockImporterManager.isInProgress()).andStubReturn(false);
	EasyMock.expect(mockImporterManager.isAlreadyDone()).andStubReturn(true);
	EasyMock.replay(mockImporterManager);
	ImportAction action = new ImportAction();
	action.setImporterManager(mockImporterManager);
	Assert.assertEquals(ImportAction.WAIT, action.execute());
    }

}
