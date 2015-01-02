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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.helper.FeatureClassCodeHelper;

/**
 * Useful methods for importer
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a> 
 */
public class ImporterHelper {

    /**
     * The readme filename (it must not be processed)
     */
    public static final String EXCLUDED_README_FILENAME = "readme.txt";
    /**
     * the all country dump file name
     */
    public static final String ALLCOUTRY_FILENAME = "allCountries.txt";
    /**
     * The regexp that every country file dump matches
     */
    public static final String GEONAMES_COUNTRY_FILE_ACCEPT_REGEX_STRING = "[A-Z][A-Z](.txt)";

    public static final String OPENSTREETMAP_US_FILE_ACCEPT_REGEX_STRING = "(US.)[0-9]+(.txt)";
    
    public static final String QUATTROSHAPES_FILE_ACCEPT_REGEX_STRING = "(localities.txt)";

    public static final String SPLITED_FILE_ACCEPT_REGEX_STRING = "[A-Z][A-Z](.)[0-9]+(.txt)";
    
    //2 letter but not us, it is managed by SPLITED_OPENSTREETMAP_US_FILE_ACCEPT_REGEX_STRING
    public static final String SPLITED_OPENSTREETMAP_FILE_ACCEPT_REGEX_STRING = "((?!(?:US))[A-Z][A-Z])(.)[0-9]+(.txt)";
    
    
    public static final String SPLITED_GEONAMES_ALTERNATENAMES_FILE_ACCEPT_REGEX_STRING = "(US.)[0-9]+(.txt)";
    
    public static final String SPLITED_ALLCOUNTRIES_FILE_ACCEPT_REGEX_STRING = "(allCountries)(.)[0-9]+(.txt)";
    
    /**
     * The regexp that every zipped country file dump matches
     */
    public static final String ZIP_FILE_ACCEPT_REGEX_STRING = ".*(.zip)";

    public static final String TAR_BZ2_FILE_ACCEPT_REGEX_STRING = ".*(.tar.bz2)";

    protected static final Logger logger = LoggerFactory.getLogger(ImporterHelper.class);
    
    private static HttpClientParams params = new HttpClientParams(){{
  		setConnectionManagerTimeout(2000);
  		setSoTimeout(2000);
  	}
  	};
    private static MultiThreadedHttpConnectionManager connectionManager = 	new MultiThreadedHttpConnectionManager();
  	private static HttpClient client = new HttpClient(connectionManager){{
  		setParams(params);
  	}};

    public static FileFilter countryFileFilter = new FileFilter() {
	public boolean accept(File file) {
	    Pattern patternGeonames = Pattern.compile(GEONAMES_COUNTRY_FILE_ACCEPT_REGEX_STRING);
	    Pattern patternOpenStreetMapUS = Pattern.compile(OPENSTREETMAP_US_FILE_ACCEPT_REGEX_STRING);
	    Pattern patternQuattroshapes = Pattern.compile(QUATTROSHAPES_FILE_ACCEPT_REGEX_STRING);

	    return (file.isFile() && file.exists()) && !EXCLUDED_README_FILENAME.equals(file.getName())
		    && ( patternGeonames.matcher(file.getName()).matches() || ALLCOUTRY_FILENAME.equals(file.getName()) || patternOpenStreetMapUS.matcher(file.getName()).matches() || patternQuattroshapes.matcher(file.getName()).matches());
	}
    };
    

	public static FileFilter splitedFileFilter = new FileFilter() {
		public boolean accept(File file) {
			Pattern patternSplit = Pattern.compile(SPLITED_FILE_ACCEPT_REGEX_STRING);
			Pattern patternAllCountriesSplit = Pattern.compile(SPLITED_ALLCOUNTRIES_FILE_ACCEPT_REGEX_STRING);

			return (file.isFile() && file.exists()) && !EXCLUDED_README_FILENAME.equals(file.getName()) && (patternAllCountriesSplit.matcher(file.getName()).matches() || patternSplit.matcher(file.getName()).matches());
		}
	};
	
	
    private static FileFilter ZipFileFilter = new FileFilter() {
	public boolean accept(File file) {
	    Pattern pattern = Pattern.compile(ZIP_FILE_ACCEPT_REGEX_STRING);

	    return (file.isFile() && file.exists()) && pattern.matcher(file.getName()).matches();
	}
    };

    private static FileFilter tarBZ2FileFilter = new FileFilter() {
	public boolean accept(File file) {
	    Pattern pattern = Pattern.compile(TAR_BZ2_FILE_ACCEPT_REGEX_STRING);

	    return (file.isFile() && file.exists()) && pattern.matcher(file.getName()).matches();
	}
    };

    /**
     * @param directoryPath
     *            The directory where files are
     * @see #GEONAMES_COUNTRY_FILE_ACCEPT_REGEX_STRING
     * @return the allcountries.txt (@see {@linkplain #ALLCOUTRY_FILENAME} file
     *         if present or the list of country file to Import or an empty
     *         array if there is no file
     */
    public static File[] listCountryFilesToImport(String directoryPath) {

	File dir = new File(directoryPath);

	File[] files = dir.listFiles(countryFileFilter);

	if (files == null) {
	    return new File[0];
	}

	for (File file : files) {
	    if (ALLCOUTRY_FILENAME.equals(file.getName())) {
		files = new File[1];
		files[0] = file;
		logger.info(ALLCOUTRY_FILENAME + " is present. Only this file will be imported. all other country files will be ignore");
		break;
	    }
	}
	
	if (files.length==0){
	    logger.warn("there is no file to import in "+directoryPath);
	}

	// for Log purpose
	for (int i = 0; i < files.length; i++) {
	    logger.info(files[i].getName() + " is an importable File");
	}
	logger.info(files.length +" files are importable files");

	return files;
    }

    
    
    /**
     * @param directoryPath
     *            The directory where splited files are
     * 
     */
    public static File[] listSplitedFilesToImport(String directoryPath) {

	File dir = new File(directoryPath);

	File[] files = dir.listFiles(splitedFileFilter);

	if (files == null) {
	    return new File[0];
	}

		
	if (files.length==0){
	    logger.warn("there is no file to import in "+directoryPath);
	}

	// for Log purpose
	for (int i = 0; i < files.length; i++) {
	    logger.info(files[i].getName() + " is a Geonames splited importable File");
	}
	logger.info(files.length +" files are Geonames importable files");

	return files;
    }
    
   

    /**
     * @param directoryPath
     *            The directory where Geonames files are to be downloaded in
     *            order to be processed
     * @see #ZIP_FILE_ACCEPT_REGEX_STRING
     * @return all the zip files present in the specified directory or an empty
     *         array if there is no file
     */
    public static File[] listZipFiles(String directoryPath) {

	File dir = new File(directoryPath);

	File[] files = dir.listFiles(ZipFileFilter);
	return files == null ? new File[0] : files;
    }

    /**
     * @param directoryPath
     *            The directory where openstreetmap files are to be downloaded
     *            in order to be processed
     * @see #TAR_BZ2_FILE_ACCEPT_REGEX_STRING
     * @return all the zip files present in the specified directory or an empty
     *         array if there is no file
     */
    public static File[] listTarFiles(String directoryPath) {

	File dir = new File(directoryPath);

	File[] files = dir.listFiles(tarBZ2FileFilter);
	return files == null ? new File[0] : files;
    }

    
    /**
     * @param URL the HTTP URL
     * @return The size of the HTTP file using HTTP head method 
     * or -1 if error or the file doesn't exists
     */
    public static long getHttpFileSize(String URL){
	HeadMethod headMethod = new HeadMethod(URL);
	//we can not follow redirect because Geonames send a 302 found HTTP status code when a file doen't exists
	headMethod.setFollowRedirects(false);
    try {
    	int code = client.executeMethod(headMethod);
    	int firstDigitOfCode = code/100;
    	switch (firstDigitOfCode) {
	case 4 :
	    logger.error("Can not determine HTTP file size of "+URL+" because it does not exists ("+code+")");
	    return -1;
	//needed to catch 3XX code because Geonames send a 302 found HTTP status code when a file doen't exists
	case 3 :
	    logger.error("Can not determine HTTP file size of "+URL+" because it does not exists ("+code+")");
	    return -1;
	case 5:
	    logger.error("Can not determine HTTP file size of "+URL+" because the server send an error "+code);
	    return -1;

	default:
	    break;
	}
	Header[] contentLengthHeaders = headMethod.getResponseHeaders("Content-Length");
	if (contentLengthHeaders.length ==1){
	    logger.info("HTTP file size of "+URL+" = "+contentLengthHeaders[0].getValue());
	    return new Long(contentLengthHeaders[0].getValue());
	} else if (contentLengthHeaders.length <= 0){
	    return -1L;
	}
    } catch (HttpException e) {
	logger.error("can not execute head method for "+URL+" : "+e.getMessage(),e);
    } catch (IOException e) {
    	logger.error("can not execute head method for "+URL+" : "+e.getMessage(),e);
    } finally {
        headMethod.releaseConnection();
    }
    return -1;
    }
    
    /**
     * @param urlsAsString
     * @return true if ALL the url doesn't retrun 200 or 3XX code 
     * and are valids
     */
    public static boolean checkUrls(List<String> urlsAsString){
    	if (urlsAsString==null){
    		return false;
    	}
    	for (String url:urlsAsString){
    		if (!checkUrl(url)){
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * check if an url doesn't return 200 or 3XX code
     * @param urlAsString the url to check
     * @return true if the url exists and is valid
     */
    public static boolean checkUrl(String urlAsString){
    	if (urlAsString==null){
    		logger.error("can not check null URL");
    		return false;
    	}
    	URL url;
		try {
			url = new URL(urlAsString);
		} catch (MalformedURLException e) {
			logger.error(urlAsString+" is not a valid url, can not check.");
			return false;
		}
    	int responseCode;
    	String responseMessage = "NO RESPONSE MESSAGE";
    	Object content = "NO CONTENT";
    	HttpURLConnection huc;
		try {
			huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("HEAD");
			responseCode = huc.getResponseCode();
			content = huc.getContent();
			responseMessage = huc.getResponseMessage();
		} catch (ProtocolException e) {
			logger.error("can not check url "+e.getMessage(),e);
			return false;
		} catch (IOException e) {
			logger.error("can not check url "+e.getMessage(),e);
			return false;
		}

    	if (responseCode == 200 || (responseCode >300 &&  responseCode < 400)) {
    		logger.info("URL "+urlAsString+ " exists");
    		return true;
    	} else {
    		logger.error(urlAsString+" return a "+responseCode+" : "+content+"/"+responseMessage);
    	return false;
    	}
    }
    
    /**
     * @param address
     *            the address of the file to be downloaded
     * @param localFileName
     *            the local file name (with absolute path)
     */
    public static void download(String address, String localFileName) throws  FileNotFoundException{
	logger.info("download file " + address + " to " + localFileName);
	OutputStream out = null;
	HttpURLConnection conn = null;
	InputStream in = null;
	try {
	    URL url = new URL(address);
	    conn = (HttpURLConnection) url.openConnection();
	    if (conn instanceof HttpURLConnection) {
    	((HttpURLConnection) conn).setInstanceFollowRedirects(false);
	    	
		int responseCode = ((HttpURLConnection) conn).getResponseCode();
		//manage most frequent error code and Gisgraphy specific one
		switch (responseCode) {
		case 509:
		    throw new RuntimeException("Sorry, there is too many users connected for "+address+", this site has limmited resources, please try again later");
		case 500:
		    throw new RuntimeException("Sorry, the server return an 500 status code for "+address+", an internal error has occured");
		case 404:
		    throw new FileNotFoundException("Sorry, the server return an 404 status code for "+address+", the file probably not exists or the URL is not correct");
		case 302:
		    throw new FileNotFoundException("Sorry, the server return an 302 status code for "+address+", the file is not at the correct URL");
		default:
		    break;
		}
		
	    }
	    in = conn.getInputStream();
	    out = new BufferedOutputStream(new FileOutputStream(localFileName));
	    byte[] buffer = new byte[1024];
	    int numRead;
	    long numWritten = 0;
	    while ((numRead = in.read(buffer)) != -1) {
		out.write(buffer, 0, numRead);
		numWritten += numRead;
	    }
	    logger.info(localFileName + "\t" + numWritten);
	} catch (UnknownHostException e) {
	    String errorMessage = "can not download " + address + " to " + localFileName + " : " + e.getMessage() + ". if the host exists and is reachable," + " maybe this links can help : http://www.gisgraphy.com/forum/viewtopic.php?f=3&t=64 ";
	    logger.warn(errorMessage);
	    throw new ImporterException(errorMessage, e);
	} catch (FileNotFoundException e) {
	    throw e;
	} catch (Exception e) {
	    logger.warn("can not download " + address + " to " + localFileName + " : " + e.getMessage());
	    throw new ImporterException(e);
	} finally {
	    try {
		if (in != null) {
		    in.close();
		}
		if (out != null) {
		    out.flush();
		    out.close();
		}
	    } catch (IOException ioe) {
		logger.error("cannot close streams");
	    }
	}
    }

    /**
     * unzip a file in the same directory as the zipped file
     * 
     * @param file
     *            The file to unzip
     */
    public static void unzipFile(File file) {
	logger.info("will Extracting file: " + file.getName());
	Enumeration<? extends ZipEntry> entries;
	ZipFile zipFile;

	try {
	    zipFile = new ZipFile(file);

	    entries = zipFile.entries();

	    while (entries.hasMoreElements()) {
		ZipEntry entry = (ZipEntry) entries.nextElement();

		if (entry.isDirectory()) {
		    // Assume directories are stored parents first then
		    // children.
		    (new File(entry.getName())).mkdir();
		    continue;
		}

		logger.info("Extracting file: " + entry.getName() + " to " + file.getParent() + File.separator + entry.getName());
		copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file.getParent() + File.separator + entry.getName())));
	    }

	    zipFile.close();
	} catch (IOException e) {
	    logger.error("can not unzip " + file.getName() + " : " + e.getMessage(),e);
	    throw new ImporterException(e);
	}
    }

    private static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
	byte[] buffer = new byte[1024];
	int len;

	while ((len = in.read(buffer)) >= 0) {
	    out.write(buffer, 0, len);
	}

	in.close();
	out.close();
    }

    /**
     * @param fields
     *            the fields corresponding to a split line of the csv geonames file
     * @return the modified fields whith the feature code change to
     *         ADM1,ADM2,ADM3,ADM4 according to the ADMcodes. e.g id adm1code
     *         and Adm2 code are not null : the feature code will be change to
     *         ADM2.
     */
    public static String[] virtualizeADMD(String[] fields) {
	if (fields[7] != null && "ADMD".equals(fields[7]) && fields[6] != null && "A".equals(fields[6])) {
	    // it is an ADMD, will try to detect level
	    int level = Adm.getProcessedLevelFromCodes(fields[10], fields[11], fields[12], fields[13]);
	    if (level != 0) {
		fields[7] = "ADM" + level;
	    }
	}
	return fields;

    }

    public static String[] correctLastAdmCodeIfPossible(String[] fields) {
	if (FeatureClassCodeHelper.is_Adm(fields[6], fields[7]) && !AbstractSimpleImporterProcessor.isEmptyField(fields, 0, false)) {
	    int level = Adm.getProcessedLevelFromFeatureClassCode(fields[6], fields[7]);
	    switch (level) {
	    case 0:
		return fields;
	    case 1:
		if (AbstractSimpleImporterProcessor.isEmptyField(fields, 10, false)) {
		    fields[10] = fields[0];// asign adm1code with featureid
		}
		return fields;
	    case 2:
		if (!AbstractSimpleImporterProcessor.isEmptyField(fields, 10, false) && AbstractSimpleImporterProcessor.isEmptyField(fields, 11, false)) {
		    fields[11] = fields[0];// asign adm2code with featureid
		}
		return fields;
	    case 3:
		if (!AbstractSimpleImporterProcessor.isEmptyField(fields, 10, false) && !AbstractSimpleImporterProcessor.isEmptyField(fields, 11, false) && AbstractSimpleImporterProcessor.isEmptyField(fields, 12, false)) {
		    fields[12] = fields[0];// asign adm3code with featureid
		}
		return fields;
	    case 4:
		if (!AbstractSimpleImporterProcessor.isEmptyField(fields, 10, false) && !AbstractSimpleImporterProcessor.isEmptyField(fields, 11, false) && !AbstractSimpleImporterProcessor.isEmptyField(fields, 12, false)
			&& AbstractSimpleImporterProcessor.isEmptyField(fields, 13, false)) {
		    fields[13] = fields[0];// asign adm4code with featureid
		}
		return fields;

	    default:
		return fields;
	    }

	}
	return fields;
    }

    /**
     * @param regexp
     *            a regexp
     * @return A {@link Pattern} or null if the regexp are not corrects
     */
    public static Pattern compileRegex(String regexp) {
	try {
	   	if (regexp != null && !regexp.trim().equals("")) {
		    return Pattern.compile(regexp,Pattern.CASE_INSENSITIVE);
		} else {
			return null;
		}
	} catch (RuntimeException e) {
	    return null;
	}
    }

    /**
     * @param secsIn
     *            the number of seconds
     * @return a human reading strings. example :1 hour 6 minuts 40 seconds.
     */
    public static String formatSeconds(long secsIn) {

	long hours = secsIn / 3600,

	remainder = secsIn % 3600, minutes = remainder / 60, seconds = remainder % 60;
	String displayhours = hours == 0 ? "" : hours + " hour" + getPlural(hours);
	String displayMin = minutes == 0 ? "" : minutes + " minut" + getPlural(minutes);
	String displaySec = seconds == 0 ? "" : seconds + " second" + getPlural(seconds);
	return displayhours + displayMin + displaySec;
    }

    private static String getPlural(long count) {
	return count > 1 ? "s " : " ";
    }

}
