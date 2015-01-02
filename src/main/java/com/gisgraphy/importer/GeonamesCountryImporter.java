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
package com.gisgraphy.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.Language;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.domain.repository.ILanguageDao;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.helper.GeolocHelper;

/**
 * Import the Countries.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeonamesCountryImporter extends AbstractSimpleImporterProcessor {
	
	protected static final Logger logger = LoggerFactory.getLogger(GeonamesCountryImporter.class);

    private ICountryDao countryDao;

    private ILanguageDao languageDao;

    /**
     * Default constructor
     */
    public GeonamesCountryImporter() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData(java.lang.String)
     */
    @Override
    protected void processData(String line) {
	String[] fields = line.split("\t");

	/*
	 * line table has the following fields :
	 * --------------------------------------------------- 0 ISO 1 ISO3 2
	 * ISO-Numeric 3 fips 4 Country name 5 Capital 6 Area(in sq km) 7
	 * Population 8 Continent 9 tld 10 CurrencyCode 11 CurrencyName 12 Phone
	 * 13 Postal Code Format 14 Postal Code Regex 15 Languages 16 geonameid
	 * 17 neighbours 18 EquivalentFipsCode
	 */
	// checkNumberOfColumn(fields);
	Country country = null;
	if (!isEmptyField(fields, 0, true) && !isEmptyField(fields, 1, true)
		&& !isEmptyField(fields, 2, true)) {
	    country = new Country(fields[0], fields[1], new Integer(fields[2])
		    .intValue());
	    country.setLocation(GeolocHelper.createPoint(0F, 0F));
	    country.setSource(GISSource.GEONAMES);
	} else {
	    logger.warn("country " + fields[4] + " requires iso3166 values");
	}
	
	country.setCountryCode(fields[0]);

	if (!isEmptyField(fields, 16, false) && !fields[16].equals("0")) {
	    country.setFeatureId(new Long(fields[16]));
	} else {
	    logger.warn("country " + fields[4] + " has no featureid");
	    country.setFeatureId((++nbGisInserted) * -1);
	}

	if (!isEmptyField(fields, 4, true)) {
	    country.setName(fields[4].trim());
	}

	if (!isEmptyField(fields, 3, false)) {
	    country.setFipsCode(fields[3]);
	}
	if (!isEmptyField(fields, 5, false)) {
	    country.setCapitalName(fields[5].trim());
	}
	if (!isEmptyField(fields, 6, false)) {
	    country.setArea(Double.parseDouble(fields[6].replace(",", "")
		    .trim()));
	}
	if (!isEmptyField(fields, 7, false)) {
	    country
		    .setPopulation(new Integer(fields[7].replace(",", "")
			    .trim()));
	}
	if (!isEmptyField(fields, 8, false)) {
	    country.setContinent(fields[8]);
	}
	if (!isEmptyField(fields, 9, false)) {
	    country.setTld(fields[9].toLowerCase());
	}

	if (!isEmptyField(fields, 10, false)) {
	    country.setCurrencyCode(fields[10]);
	}

	if (!isEmptyField(fields, 11, false)) {
	    country.setCurrencyName(fields[11]);
	}
	if (!isEmptyField(fields, 12, false)) {
	    country.setPhonePrefix(fields[12]);
	}
	if (!isEmptyField(fields, 13, false)) {
	    country.setPostalCodeMask(fields[13]);
	}
	if (!isEmptyField(fields, 14, false)) {
	    country.setPostalCodeRegex(fields[14]);
	}
	if (!isEmptyField(fields, 15, false)) {
	    String[] languages = fields[15].split(",");
	    for (String element : languages) {
		String lang = element.toUpperCase();
		if (lang.indexOf("-") != -1) {
		    lang = lang.substring(0, lang.indexOf("-"));
		}
		Language language = this.languageDao.getByIso639Code(lang);
		if (language == null) {
		    throw new RuntimeException(
			    "could not retrieve language with code " + lang);
		}
		country.addSpokenLanguage(language);
	    }
	}

	if (!isEmptyField(fields, 18, false)) {
	    country.setEquivalentFipsCode(fields[18]);
	}

	this.countryDao.save(country);

    }
    
    @Override
    protected void onFileProcessed(File file) {
    	//we don't want to rename files so we overide the method
    }
    
    /* (non-Javadoc)
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped()
     */
    @Override
    public boolean shouldBeSkipped() {
    	//should never be skiped
    	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreFirstLine()
     */
    @Override
    protected boolean shouldIgnoreFirstLine() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldIgnoreComments()
     */
    @Override
    protected boolean shouldIgnoreComments() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#setCommitFlushMode()
     */
    @Override
    protected void setCommitFlushMode() {
	this.languageDao.setFlushMode(FlushMode.COMMIT);
	this.countryDao.setFlushMode(FlushMode.COMMIT);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear()
     */
    @Override
    protected void flushAndClear() {
	this.languageDao.flushAndClear();
	this.countryDao.flushAndClear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
	return 4;
    }

    /**
     * @param countryDao
     *                The countryDao to set
     */
    @Required
    public void setCountryDao(ICountryDao countryDao) {
	this.countryDao = countryDao;
    }

    /**
     * @param languageDao
     *                The languageDao to set
     */
    @Required
    public void setLanguageDao(ILanguageDao languageDao) {
	this.languageDao = languageDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
	File[] files = new File[1];
	files[0] = new File(importerConfig.getGeonamesDir()
		+ importerConfig.getCountriesFileName());
	return files;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
	List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
	logger.info("deleting countries...");
	int deletedCountries = countryDao.deleteAll();
	if (deletedCountries != 0) {
	    deletedObjectInfo.add(new NameValueDTO<Integer>(Country.class
		    .getSimpleName(), deletedCountries));
	}
	logger.info(deletedCountries + " countries have been deleted");
	resetStatus();
	return deletedObjectInfo;
    }

}
