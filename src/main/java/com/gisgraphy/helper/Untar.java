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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to untar files, files can be zipped in multi format (extension
 * tar, tar.gzip,tar.gz, tar.bz2, tar.bzip2 are supported).
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class Untar {
    private String tarFileName;
    private File dest;
    private String currentFileNameIntoArchiveExtracted;

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Untar.class);

    /**
     * @param tarFileName
     *            the path to the file we want to untar
     * @param dest
     *            the path where the file should be untar
     */
    public Untar(String tarFileName, File dest) {
	this.tarFileName = tarFileName;
	this.dest = dest;
    }

    private InputStream getDecompressedInputStream(final String name, final InputStream istream) throws IOException {
	logger.info("untar: decompress " + name + " to " + dest);
	if (name == null) {
	    throw new RuntimeException("fileName to decompress can not be null");
	}
	if (name.toLowerCase().endsWith("gzip") || name.toLowerCase().endsWith("gz")) {
	    return new BufferedInputStream(new GZIPInputStream(istream));
	} else if (name.toLowerCase().endsWith("bz2") || name.toLowerCase().endsWith("bzip2")) {
	    final char[] magic = new char[] { 'B', 'Z' };
	    for (int i = 0; i < magic.length; i++) {
		if (istream.read() != magic[i]) {
		    throw new RuntimeException("Invalid bz2 file." + name);
		}
	    }
	    return new BufferedInputStream(new CBZip2InputStream(istream));
	} else if (name.toLowerCase().endsWith("tar")) {
	    return istream;
	}
	throw new RuntimeException("can only detect compression for extension tar, gzip, gz, bz2, or bzip2");
    }

    /**
     * process the untar operation
     * 
     * @throws IOException
     */
    public void untar() throws IOException {
	logger.info("untar: untar " + tarFileName + " to " + dest);
	TarInputStream tin = null;
	try {
	    if (!dest.exists()) {
		dest.mkdir();
	    }

	    tin = new TarInputStream(getDecompressedInputStream(tarFileName, new FileInputStream(new File(tarFileName))));

	    TarEntry tarEntry = tin.getNextEntry();

	    while (tarEntry != null) {
		File destPath = new File(dest.toString() + File.separatorChar + tarEntry.getName());

		if (tarEntry.isDirectory()) {
		    destPath.mkdir();
		} else {
		    if (!destPath.getParentFile().exists()) {
			destPath.getParentFile().mkdirs();
		    }
		    currentFileNameIntoArchiveExtracted = tarEntry.getName();
		    logger.info("untar: untar " + tarEntry.getName() + " to " + destPath);
		    FileOutputStream fout = new FileOutputStream(destPath);
		    try {
			tin.copyEntryContents(fout);
		    } finally {
			fout.flush();
			fout.close();
		    }
		}
		tarEntry = tin.getNextEntry();
	    }
	} finally {
	    currentFileNameIntoArchiveExtracted = null;
	    if (tin != null) {
		tin.close();
	    }
	}

    }

    public String getCurrentFileNameIntoArchiveExtracted() {
	return currentFileNameIntoArchiveExtracted;
    }
}