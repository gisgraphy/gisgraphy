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
/**
 *
 */
package com.gisgraphy.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.service.IInternationalisationService;

/**
 *  Base class to download files from a server
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public abstract class AbstractFileRetriever implements IImporterProcessor {
    
   @Autowired
    private IInternationalisationService internationalisationService;

    protected ImporterConfig importerConfig;

    protected String currentFileName = null;

    protected ImporterStatus status = ImporterStatus.WAITING;

    protected int fileIndex = 0;

    protected int numberOfFileToDownload = 0;

    protected String statusMessage = "";
    
    private long fileSizeToDownloadCached =0;
    
    
    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(AbstractFileRetriever.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#process()
     */
    public void process() throws ImporterException {
	statusMessage = internationalisationService.getString("import.download.info");
	status = ImporterStatus.PROCESSING;
	try {
	    if (!shouldBeSkipped()) {
		if (importerConfig.isRetrieveFiles()){
		logger
			.info("DownloadFiles option is set to true, we will download and decompress files");
		downloadFiles();
		} else {
		    logger
			.info("DownloadFiles option is set to false, we won't download but decompress files");
		}
		statusMessage = internationalisationService.getString("import.extract.info");
		decompressFiles();
		this.status = ImporterStatus.PROCESSED ;
	    } else {
		this.status = ImporterStatus.SKIPPED;
		logger
			.info("DownloadFiles option is set to false, we will not download and decompress files");
	    }
	    statusMessage = "";
	} catch (Exception e) {
	    this.statusMessage = "error retrieving or decompres file : " + e.getMessage();
	    logger.error(statusMessage,e);
	    status = ImporterStatus.ERROR;
	    throw new ImporterException(statusMessage, e);
	} 

    }

    protected void downloadFiles() {
	List<String> downloadFileList = getFilesToDownload();
	this.numberOfFileToDownload = downloadFileList.size();
	for (String file : downloadFileList) {
	    this.fileIndex++;
	    this.currentFileName = file;
	    try {
		downloadFile(file);
	    } catch (FileNotFoundException e) {
		if (isFileNotFoundTolerant()){
		    logger.error(getDownloadBaseUrl()
		    + file+" can not be downloaded" );
		} else {
		 throw new RuntimeException(e);    
		}
		}
	}
    }

    protected void downloadFile(String file) throws FileNotFoundException {
	ImporterHelper.download(getDownloadBaseUrl()
	    + file, getDownloadDirectory() + file);
    }
    
    /**
     * @return false if download files that doesn't exists on the remote server
     *         should throw
     */
    public abstract boolean isFileNotFoundTolerant();

    /**
     * @return A list of file to be download
     */
    abstract List<String> getFilesToDownload() ;

    /**
     * @return true if the processor should Not be executed
     */
    public boolean shouldBeSkipped() {
	return importerConfig.isRetrieveFiles();
    }

    /**
     * Method to call if files must be decompress (untar or unzip)
     * @throws IOException
     */
    public abstract void decompressFiles() throws IOException ;
    
    /**
     * return an array of file that are to be decompressed
     * @throws IOException
     */
    public abstract File[] getFilesToDecompress() throws IOException ;

    /** 
     * @return The directory where the file should be downloaded
     */
    public abstract String getDownloadDirectory();

    /**
     * @return the base URL from wich the file should be downloaded
     */
    public abstract String getDownloadBaseUrl() ;

   

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getReadFileLine()
     */
    public long getReadFileLine() {
	File file = new File( getDownloadDirectory() + this.currentFileName);
	if (!file.exists() || !file.isFile()){
	    return 0;
	}
	return new Long(file.length());
    }

    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.IImporterProcessor#getTotalReadLine()
     */
    public long getTotalReadLine() {
	if (ImporterStatus.SKIPPED.equals(status) || ImporterStatus.WAITING.equals(status)){
	    return 0;
	}
	List<String> downloadedFiles;
	try {
	    downloadedFiles = getFilesToDownload();
	} catch (Exception e) {
	    logger.error("Can not retrieve files to decompress to calculate alredy downloaded file "+ e.getMessage(),e);
	   return -1;
	}
	long cumulatedSize = 0;
	for (String filename : downloadedFiles) {
		File file =new File(getDownloadDirectory() + filename);
	    if (file.exists() && file.isFile()){
		cumulatedSize = cumulatedSize+file.length();
	    }
	}
	return cumulatedSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#getCurrentFile()
     */
    public String getCurrentFileName() {
	if (this.currentFileName != null) {
	    return this.currentFileName;
	}
	return "?";
    }

    public long getNumberOfLinesToProcess() {
	if (fileSizeToDownloadCached==0 && importerConfig.isRetrieveFiles()){
	List<String> downloadFileList = getFilesToDownload();
	long cumulatedSize = 0;
	for (String file : downloadFileList) {
	    long size = ImporterHelper.getHttpFileSize((getDownloadBaseUrl()
		    + file));
	    if (size !=-1){
		cumulatedSize = cumulatedSize +size;
	    }
	}
	fileSizeToDownloadCached = cumulatedSize;
	}
	return fileSizeToDownloadCached;
	
    }

    public ImporterStatus getStatus() {
	return this.status;
    }

    public String getStatusMessage() {
	return this.statusMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
	List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
	List<String> filesToImport = getFilesToDownload();
	int deleted = 0;
	for (String fileName : filesToImport) {
	    File file = new File(getDownloadDirectory() + fileName);
	    if (file.delete()) {
		logger
			.info("the files " + file.getName()
				+ " has been deleted");
		deleted++;
	    } else {
		logger.info("the files " + file.getName()
			+ " hasn't been deleted");
	    }
	}
	deletedObjectInfo.add(new NameValueDTO<Integer>("Downloaded files",
		deleted));
	resetStatus();
	return deletedObjectInfo;
    }

    public void resetStatus() {
	currentFileName = null;
	status = ImporterStatus.WAITING;
	fileIndex = 0;
	numberOfFileToDownload = 0;
	statusMessage = "";
    }
    
    /**
     * @param importerConfig
     *                The importerConfig to set
     */
    @Required
    public void setImporterConfig(ImporterConfig importerConfig) {
	this.importerConfig = importerConfig;
    }
    
    /**
     * @param internationalisationService the internationalisationService to set
     */
    @Required
    public void setInternationalisationService(IInternationalisationService internationalisationService) {
        this.internationalisationService = internationalisationService;
    }


}
