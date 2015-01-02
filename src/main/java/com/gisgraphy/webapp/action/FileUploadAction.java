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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.struts2.ServletActionContext;

import com.gisgraphy.Constants;

/**
 * Sample action that shows how to do file upload with Struts 2.
 */
public class FileUploadAction extends BaseAction {
    private static final long serialVersionUID = -9208910183310010569L;

    private File file;

    private String fileContentType;

    private String fileFileName;

    private String name;

    /**
     * Upload the file
     * 
     * @return String with result (cancel, input or sucess)
     * @throws Exception
     *                 if something goes wrong
     */
    public String upload() throws Exception {
	if (this.cancel != null) {
	    return "cancel";
	}

	// the directory to upload to
	String uploadDir = ServletActionContext.getServletContext()
		.getRealPath("/resources")
		+ "/" + getRequest().getRemoteUser() + "/";

	// write the file to the file specified
	File dirPath = new File(uploadDir);

	if (!dirPath.exists()) {
	    dirPath.mkdirs();
	}

	// retrieve the file data
	InputStream stream = new FileInputStream(file);

	// write the file to the file specified
	OutputStream bos = new FileOutputStream(uploadDir + fileFileName);
	int bytesRead;
	byte[] buffer = new byte[8192];

	while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
	    bos.write(buffer, 0, bytesRead);
	}

	bos.close();
	stream.close();

	// place the data into the request for retrieval on next page
	getRequest().setAttribute("location",
		dirPath.getAbsolutePath() + Constants.FILE_SEP + fileFileName);

	String link = getRequest().getContextPath() + "/resources" + "/"
		+ getRequest().getRemoteUser() + "/";

	getRequest().setAttribute("link", link + fileFileName);

	return SUCCESS;
    }

    /**
     * Default method - returns "input"
     * 
     * @return "input"
     */
    @Override
    public String execute() {
	return INPUT;
    }

    public void setFile(File file) {
	this.file = file;
    }

    public void setFileContentType(String fileContentType) {
	this.fileContentType = fileContentType;
    }

    public void setFileFileName(String fileFileName) {
	this.fileFileName = fileFileName;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public File getFile() {
	return file;
    }

    public String getFileContentType() {
	return fileContentType;
    }

    public String getFileFileName() {
	return fileFileName;
    }

    @Override
    public void validate() {
	if (getRequest().getMethod().equalsIgnoreCase("post")) {
	    getFieldErrors().clear();
	    if ("".equals(fileFileName) || file == null) {
		super.addFieldError("file", getText("errors.requiredField",
			new String[] { getText("uploadForm.file") }));
	    } else if (file.length() > 2097152) {
		addActionError(getText("maxLengthExceeded"));
	    }
	}
    }
}
