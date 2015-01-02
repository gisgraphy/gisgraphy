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
package com.gisgraphy.helper;

import java.io.File;

/**
 * 
 * Some useful method to manage file
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class FileHelper {

    /**
     * @param path
     *            The name of the temporary directory to create. A timestamp will be
     *            added to make a unique directory, so you can use this method
     *            several times with the same path. A common use is to give the
     *            name of the class that needs to create the directory. 
     * @return the created directory
     * @throws RuntimeException
     *             if we can not write in temporary directory
     */
    public static File createTempDir(String path) {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tempDir.canWrite()) {
            throw new RuntimeException("can not write in temp Directory :"
        	    + tempDir.getAbsolutePath());
        }
    
        tempDir = new File(System.getProperty("java.io.tmpdir")
        	+ System.getProperty("file.separator") + path + "-"
        	+ System.currentTimeMillis());
    
        tempDir.mkdir();
        if (!tempDir.canWrite()) {
            throw new RuntimeException("can not write in temp Directory :"
        	    + tempDir.getAbsolutePath());
        }
        return tempDir;
    }

}
