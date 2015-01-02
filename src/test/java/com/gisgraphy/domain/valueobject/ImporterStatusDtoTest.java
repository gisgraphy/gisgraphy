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
package com.gisgraphy.domain.valueobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.importer.GeonamesFeatureSimpleImporter;
import com.gisgraphy.importer.IImporterProcessor;

public class ImporterStatusDtoTest {

    private static final String STATUS_MESSAGE = "message";

    private static final long TOTAL_READ_LINE = 3L;

    private static final long TOTAL_LINE_TO_PROCESS = 10L;

    private static final String CURRENT_FILE_NAME = "currentFileName";

    private static final String PROCESSOR_NAME = "processorName";

    private static final long CURRENT_LINE = 2L;

    private final static String EXPECTEDCSV = PROCESSOR_NAME
	    + ImporterStatusDto.CSV_FIELD_SEPARATOR + CURRENT_FILE_NAME
	    + ImporterStatusDto.CSV_FIELD_SEPARATOR + CURRENT_LINE
	    + ImporterStatusDto.CSV_FIELD_SEPARATOR + TOTAL_LINE_TO_PROCESS
	    + ImporterStatusDto.CSV_FIELD_SEPARATOR + TOTAL_READ_LINE
	    + ImporterStatusDto.CSV_FIELD_SEPARATOR + STATUS_MESSAGE
	    + ImporterStatusDto.CSV_FIELD_SEPARATOR
	    + ImporterStatus.PROCESSING.name()
	    + ImporterStatusDto.CSV_LINE_SEPARATOR;

    @Test
    public void constructorWithAllFields() {
	ImporterStatusDto importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, null, CURRENT_LINE, TOTAL_LINE_TO_PROCESS,
		TOTAL_READ_LINE, STATUS_MESSAGE, ImporterStatus.PROCESSING);
	assertEquals(
		"currentFileName should be equals to the default value if null",
		ImporterStatusDto.DEFAULT_CURRENT_FILE, importerStatusDto
			.getCurrentFileName());
	importerStatusDto = new ImporterStatusDto(PROCESSOR_NAME,
		CURRENT_FILE_NAME, CURRENT_LINE, TOTAL_LINE_TO_PROCESS,
		TOTAL_READ_LINE, STATUS_MESSAGE, ImporterStatus.PROCESSING);
	assertEquals(PROCESSOR_NAME, importerStatusDto.getProcessorName());
	assertEquals(CURRENT_FILE_NAME, importerStatusDto.getCurrentFileName());
	assertEquals(CURRENT_LINE, importerStatusDto.getCurrentLine());
	assertEquals(TOTAL_LINE_TO_PROCESS, importerStatusDto
		.getNumberOfLineToProcess());
	assertEquals(TOTAL_LINE_TO_PROCESS - TOTAL_READ_LINE, importerStatusDto
		.getNumberOfLinelefts());
	assertEquals(STATUS_MESSAGE, importerStatusDto.getStatusMessage());
	assertEquals(ImporterStatus.PROCESSING, importerStatusDto.getStatus());
	assertEquals(30, importerStatusDto.getPercent());
    }

    @Test
    public void constructorFromCSV() {
	ImporterStatusDto importerStatusDto = new ImporterStatusDto(EXPECTEDCSV);
	assertEquals(PROCESSOR_NAME, importerStatusDto.getProcessorName());
	assertEquals(CURRENT_FILE_NAME, importerStatusDto.getCurrentFileName());
	assertEquals(CURRENT_LINE, importerStatusDto.getCurrentLine());
	assertEquals(TOTAL_LINE_TO_PROCESS, importerStatusDto
		.getNumberOfLineToProcess());
	assertEquals(TOTAL_LINE_TO_PROCESS - TOTAL_READ_LINE, importerStatusDto
		.getNumberOfLinelefts());
	assertEquals(STATUS_MESSAGE, importerStatusDto.getStatusMessage());
	assertEquals(ImporterStatus.PROCESSING, importerStatusDto.getStatus());
	assertEquals(30, importerStatusDto.getPercent());
    }

    @Test
    public void constructorFromCSVShouldThrowIfWrongNumberOfFields() {
	try {
	    new ImporterStatusDto(" ; ");
	    fail("Wrong number of CSV should throw");
	} catch (IllegalArgumentException e) {
	}

    }

    @Test
    public void constructorFromImporter() {
	IImporterProcessor processor = EasyMock
		.createMock(GeonamesFeatureSimpleImporter.class);
	EasyMock.expect(processor.getCurrentFileName()).andReturn(
		CURRENT_FILE_NAME);
	EasyMock.expect(processor.getNumberOfLinesToProcess()).andReturn(
		TOTAL_LINE_TO_PROCESS);
	EasyMock.expect(processor.getReadFileLine()).andReturn(CURRENT_LINE);
	EasyMock.expect(processor.getTotalReadLine()).andReturn(3L);
	EasyMock.expect(processor.getStatusMessage()).andReturn(STATUS_MESSAGE);
	EasyMock.expect(processor.getStatus()).andReturn(
		ImporterStatus.PROCESSING);
	EasyMock.replay(processor);
	ImporterStatusDto importerStatusDto = new ImporterStatusDto(processor);
	assertEquals(processor.getClass().getSimpleName(), importerStatusDto
		.getProcessorName());
	assertEquals(CURRENT_FILE_NAME, importerStatusDto.getCurrentFileName());
	assertEquals(CURRENT_LINE, importerStatusDto.getCurrentLine());
	assertEquals(TOTAL_LINE_TO_PROCESS, importerStatusDto
		.getNumberOfLineToProcess());
	assertEquals(TOTAL_LINE_TO_PROCESS - TOTAL_READ_LINE, importerStatusDto
		.getNumberOfLinelefts());
	assertEquals(STATUS_MESSAGE, importerStatusDto.getStatusMessage());
	assertEquals(ImporterStatus.PROCESSING, importerStatusDto.getStatus());
	assertEquals(30, importerStatusDto.getPercent());

	IImporterProcessor processor2 = EasyMock
		.createMock(GeonamesFeatureSimpleImporter.class);
	EasyMock.expect(processor2.getCurrentFileName()).andReturn(null);
	EasyMock.expect(processor2.getNumberOfLinesToProcess()).andReturn(
		TOTAL_LINE_TO_PROCESS);
	EasyMock.expect(processor2.getReadFileLine()).andReturn(CURRENT_LINE);
	EasyMock.expect(processor2.getTotalReadLine()).andReturn(
		TOTAL_READ_LINE);
	EasyMock.expect(processor2.getStatusMessage())
		.andReturn(STATUS_MESSAGE);
	EasyMock.expect(processor2.getStatus()).andReturn(
		ImporterStatus.PROCESSING);
	EasyMock.replay(processor2);
	importerStatusDto = new ImporterStatusDto(processor2);
	assertEquals(ImporterStatusDto.DEFAULT_CURRENT_FILE, importerStatusDto
		.getCurrentFileName());
    }

    @Test
    public void toCSV() {
	ImporterStatusDto importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		TOTAL_LINE_TO_PROCESS, TOTAL_READ_LINE, STATUS_MESSAGE,
		ImporterStatus.PROCESSING);
	String CSV = importerStatusDto.toCSV();
	assertEquals(EXPECTEDCSV, CSV);
    }
    
    @Test
    public void percentShouldBe100IfThereIsNoThingToDo(){
	ImporterStatusDto importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		0, 0, STATUS_MESSAGE,
		ImporterStatus.PROCESSING);
	assertEquals(100, importerStatusDto.getPercent());
	
	importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		0, 0, STATUS_MESSAGE,
		ImporterStatus.PROCESSED);
	assertEquals(100, importerStatusDto.getPercent());
	
	importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		0, 0, STATUS_MESSAGE,
		ImporterStatus.SKIPPED);
	assertEquals(0, importerStatusDto.getPercent());
	
	importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		0, 0, STATUS_MESSAGE,
		ImporterStatus.ERROR);
	assertEquals(0, importerStatusDto.getPercent());
    
	importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		0, 0, STATUS_MESSAGE,
		ImporterStatus.WAITING);
	assertEquals(0, importerStatusDto.getPercent());
    
    }
    
    @Test
    public void percentShouldUnknowIfNumberOfLineToProcessedEquals0(){
	ImporterStatusDto importerStatusDto = new ImporterStatusDto(
		PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
		-1, 0, STATUS_MESSAGE,
		ImporterStatus.PROCESSING);
	assertEquals(ImporterStatus.UNKNOW, importerStatusDto.getStatus());
	assertEquals(0, importerStatusDto.getPercent());
	assertEquals(0, importerStatusDto.getNumberOfLinelefts());
	
	importerStatusDto = new ImporterStatusDto(
			PROCESSOR_NAME, CURRENT_FILE_NAME, CURRENT_LINE,
			0, -1, STATUS_MESSAGE,
			ImporterStatus.PROCESSING);
		assertEquals(ImporterStatus.UNKNOW, importerStatusDto.getStatus());
		assertEquals(0, importerStatusDto.getPercent());
		assertEquals(0, importerStatusDto.getNumberOfLinelefts());
    
    }
    
   

}
