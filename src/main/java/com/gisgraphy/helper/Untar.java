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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
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
	    //return new BufferedInputStream(new CBZip2InputStream(istream));//CBZip2InputStream
	    return unBzip(new File(tarFileName),this.dest);
	} else if (name.toLowerCase().endsWith("tar")) {
	    return istream;
	}
	throw new RuntimeException("can only detect compression for extension tar, gzip, gz, bz2, or bzip2");
    }
    
    private List<File> processUnTar(InputStream is, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

        logger.info(String.format("Untaring file to dir %s.", outputDir.getAbsolutePath()));

        final List<File> untaredFiles = new LinkedList<File>();
       // final InputStream is = new FileInputStream(inputFile); 
        final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
        TarArchiveEntry entry = null; 
        while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
            	logger.info(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
                if (!outputFile.exists()) {
                	logger.info(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                }
            } else {
            	logger.info(String.format("Creating output file %s.", outputFile.getAbsolutePath()));
                final OutputStream outputFileStream = new FileOutputStream(outputFile); 
                IOUtils.copy(debInputStream, outputFileStream);
                outputFileStream.close();
            }
            untaredFiles.add(outputFile);
        }
        debInputStream.close(); 

        return untaredFiles;
    }

    
    
    /**
     * Ungzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.gz' extension. 
     * 
     * @param inputFile     the input .gz file
     * @param outputDir     the output directory file. 
     * @throws IOException 
     * @throws FileNotFoundException
     *  
     * @return  The {@File} with the ungzipped content.
     */
    private  File unGzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

    	logger.info(String.format("Ungzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);

        in.close();
        out.close();

        return outputFile;
    }
    
    private  InputStream unBzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

    	logger.info(String.format("UnBzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

       // final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final InputStream in = new BZip2CompressorInputStream(new FileInputStream(inputFile));
       /* final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);

        in.close();
        out.close();
        */

        return in;
    }
    
    public void untar() throws IOException {
    	
    	try {
    		InputStream bz2 = getDecompressedInputStream(tarFileName, new FileInputStream(new File(tarFileName)));
			processUnTar( bz2 ,this.dest);
		} catch (ArchiveException e) {
			logger.error("can not decompress "+tarFileName+" in "+this.dest+" : "+e);
		}
    }

    /**
     * process the untar operation
     * 
     * @throws IOException
     */
    public void processUnTar_v1() throws IOException {
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