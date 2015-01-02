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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.ImporterStatusDto;
import com.gisgraphy.importer.ImporterConfig;

/**
 * A data access object for {@link ImporterStatus}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class ImporterStatusListDao implements IImporterStatusListDao {

    public static final String IMPORTER_STATUS_LIST_FILENAME = "importerStatusList";

    ImporterConfig importerConfig;

    private Logger logger = LoggerFactory
	    .getLogger(ImporterStatusListDao.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IImporterStatusListDao#getFilePath()
     */
    public String getSavedFilePath() {
	String dirpath = importerConfig.createImporterMetadataDirIfItDoesnTExist();
	return dirpath + IMPORTER_STATUS_LIST_FILENAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IImporterStatusListDao#saveOrUpdate(java.util.List)
     */
    public List<ImporterStatusDto> saveOrUpdate(
	    List<ImporterStatusDto> importerStatusDtoList) {
	OutputStreamWriter writer = getWriter();
	try {
	    for (ImporterStatusDto dto : importerStatusDtoList) {
		writer.append(dto.toCSV());
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	} finally {
	    if (writer != null) {
		try {
		    writer.flush();
		    writer.close();
		} catch (IOException e) {
		    logger.error("error during flush or close");
		}
	    }
	}

	return importerStatusDtoList;
    }

    private OutputStreamWriter getWriter() {
	OutputStreamWriter writer = null;
	OutputStream outputStream = null;
	try {

	    File file = new File(getSavedFilePath());
	    if (file.exists()) {
		file.delete();
	    }
	    if (!file.createNewFile()) {
		throw new RuntimeException("can not save to file "
			+ getSavedFilePath());
	    }
	    outputStream = new BufferedOutputStream(new FileOutputStream(file));
	    writer = new OutputStreamWriter(outputStream, Constants.CHARSET);

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return writer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IImporterStatusListDao#get()
     */
    public List<ImporterStatusDto> get() {
	List<ImporterStatusDto> result = new ArrayList<ImporterStatusDto>();
	FileReader fileReader = null;
	BufferedReader bufferReader = null;
	try {
	    File file = new File(getSavedFilePath());
	    if (!file.exists()){
		logger.warn("can not find file for importer metadata : "+file.getAbsolutePath());
		return new ArrayList<ImporterStatusDto>();
	    }
	    fileReader = new FileReader(file);
	    bufferReader = new BufferedReader(fileReader);
	    String line = bufferReader.readLine();
	    ImporterStatusDto importerStatusDto = null;
	    while (line != null) {
		try {
			importerStatusDto = new ImporterStatusDto(line);
			result.add(importerStatusDto);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		line = bufferReader.readLine();
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(),e);
	    return new ArrayList<ImporterStatusDto>();
	} finally {
	    if (bufferReader != null) {
		try {
		    bufferReader.close();
		} catch (IOException e) {
		    logger.error("error during flush or close : "
			    + e.getMessage());
		}
	    }
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IImporterStatusListDao#delete()
     */
    public boolean delete() {
	File file = new File(getSavedFilePath());
	if (file.exists()) {
	    return file.delete();
	}
	return true;
    }

    /**
     * @param importerConfig
     *                the importerConfig to set
     */
    @Required
    public void setImporterConfig(ImporterConfig importerConfig) {
	this.importerConfig = importerConfig;
    }
}
