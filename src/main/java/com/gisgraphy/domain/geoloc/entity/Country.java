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
package com.gisgraphy.domain.geoloc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

/**
 * A Country (as defined by ISO 3166). A country always has an ISO 3166 alpha-2,
 * alpha-3 and numeric code, but may, or may not have other names (FIPS, etc).
 * The list of countries has been imported from Geonames Country List. Codes are
 * written in upper case !
 * 
 * @see <a
 *      href="http://www.iso.org/iso/en/prods-services/popstds/countrynamecodes.html">Country
 *      Name Codes</a>
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Country extends GisFeature implements Serializable,
	Comparable<Country> {

    /**
     * Constructor that populate the {@link Country} with the gisFeature fields<br>
     * 
     * @param gisFeature
     *                The gisFeature from which we want to populate the
     *                {@linkplain Country}
     */
    public Country(GisFeature gisFeature) {
	super(gisFeature);
    }

    /**
     * Default constructor (Needed by CGLib)
     */
    public Country() {
	super();
    }

    /**
     * Construct a country with the iso 3166 alpha-2 code, iso 3166 alpha-3 code, and
     * iso 3166 numeric code
     * 
     * @param iso3166Alpha2Code
     *                The iso 3166 alpha 2 code for this Country
     * @param iso3166Alpha3Code
     *                The iso 3166 alpha3 code for this Country
     * @param iso3166NumericCode
     *                The iso 3166 numeric code for this Country <u>NOTE</u> : The
     *                iso3166AlphaX codes will be automatically uppercased
     */
    public Country(String iso3166Alpha2Code, String iso3166Alpha3Code,
	    int iso3166NumericCode) {
	super();
	this.setIso3166Alpha2Code(iso3166Alpha2Code);
	this.setIso3166Alpha3Code(iso3166Alpha3Code);
	this.setIso3166NumericCode(iso3166NumericCode);
    }

    private static final long serialVersionUID = 1L;

    private Double area;

    private String tld;

    private String capitalName;

    private String continent;

    private String postalCodeRegex;

    /**
     * @see #getCurrencyCode()
     * @see #getCurrency()
     */
    private String currencyCode;

    private String currencyName;

    /**
     * @see #getEquivalentFipsCode()
     */
    private String equivalentFipsCode;

    /**
     * @see #getFipsCode()
     */
    private String fipsCode;

    /**
     * @see #getIso3166Alpha2Code()
     */
    private String iso3166Alpha2Code;

    /**
     * @see #getIso3166Alpha3Code()
     */
    private String iso3166Alpha3Code;

    /**
     * @see #getIso3166NumericCode()
     */
    private int iso3166NumericCode;

    // TODO v3
    // private List<Country> neighbourCountries = new ArrayList<Country>();
    /**
     * @see #getPhonePrefix()
     */
    private String phonePrefix;

    /**
     * @see #getPostalCodeMask()
     */
    private String postalCodeMask;

    /**
     * The spoken languages
     */
    private List<Language> spokenLanguages = new ArrayList<Language>();

    /*
     * public void addNeighbourCountry(Country c) {
     * this.neighbourCountries.add(c); }
     */

    /**
     * Add a spoken language to the country. The language is described by its
     * Alpha2 Code.
     * 
     * @param lang
     */
    public void addSpokenLanguage(Language lang) {
	this.spokenLanguages.add(lang);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.entity.GisFeature#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!super.equals(obj)) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Country other = (Country) obj;
	if (iso3166Alpha2Code == null) {
	    if (other.iso3166Alpha2Code != null) {
		return false;
	    }
	} else if (!iso3166Alpha2Code.equals(other.iso3166Alpha2Code)) {
	    return false;
	}
	return true;
    }

    /**
     * Country area in square Km
     * 
     * @return The country area in square Km
     */
    public Double getArea() {
	return this.area;
    }

    /**
     * Returns the ISO 4217 Currency from the currency code. The code is not
     * imported but processed :
     * {@link Currency#getInstance(java.util.Locale)} Important : some
     * currencies are NOT recognized by
     * {@link Currency#getInstance(java.util.Locale)}.
     * 
     * @return The ISO 4217 Currency or null if the currencyCode is null or
     *         incorrect
     */
    @Transient
    public Currency getCurrency() {
	Currency currency = null;
	if (this.currencyCode != null) {
	    try {
		currency = Currency.getInstance(this.currencyCode);
	    } catch (RuntimeException e) {
	    	logger.warn("Got a wrong currencycode" + getCountryCode());
	    }
	}
	return currency;
    }

    /**
     * ISO 4217 currency code when possible. However, for some countries, there
     * is no official ISO 4217 code, like Guernsey (GGP), and the information
     * can be null sometimes when we're not sure of the currency (United States
     * Minor Outlying Islands). This field is not unique, because some countries
     * have the same currency. (Euro for instance..)
     * 
     * @see <a
     *      href="http://www.iso.org/iso/en/prods-services/popstds/currencycodeslist.html">
     *      ISO 4217 Currency names </a>
     * @see <a href="http://en.wikipedia.org/wiki/Guernsey_pound">Guernsey Pound</a>
     * @see <a
     *      href="http://en.wikipedia.org/wiki/United_States_Minor_Outlying_Islands">
     *      United States Minor Outlying Islands</a>
     * @return The ISO 4217 currency code
     */
    @Column(unique = false, nullable = true)
    public String getCurrencyCode() {
	return this.currencyCode;
    }

    /**
     * This field is not supported yet. The equivalent fips Code. This is
     * the same as the FIPS code, except that sometimes, there is no FIPS code
     * for some entity (Aaland Islands), even if it is the same country than
     * another one (finland). So in that case, Aaland island equivalent fips
     * code will be set to FI
     * 
     * @return The equivalent fips Code
     */
    public String getEquivalentFipsCode() {
	return this.equivalentFipsCode;
    }

    /**
     * The FIPS 10.4 country code. This field can be null in some cases
     * (when there is an ISO Code and no FIPS code, for instance). Note that
     * these code is not the same as the ISO 3166 codes used by the U.N. and
     * for Internet top-level country code domains.
     * 
     * @see <a href="http://en.wikipedia.org/wiki/List_of_FIPS_country_codes">
     *      List Of FIPS Country Codes </a>
     * @return Returns the fipsCode.
     */
    @Column(unique = true, nullable = true)
    @Index(name = "countryFipsCode")
    public String getFipsCode() {
	return this.fipsCode;
    }

    /**
     * The ISO 3166 alpha-2 letter code(should be in upper case). We don't use the country code of the
     * GisFeature because we want that fields as mandatory and the GisFeature
     * one is not 
     * 
     * @see <a href="http://en.wikipedia.org/wiki/ISO_3166"> ISO 3166 </a>
     * @return Returns the iso3166Alpha2Code.
     */
    @Column(unique = true, nullable = false)
    @Index(name = "countryIso3166Alpha2CodeIndex")
    public String getIso3166Alpha2Code() {
	return this.iso3166Alpha2Code;
    }

    /**
     * The ISO 3166 alpha-3 letter code (should be in upper case)
     * 
     * @see <a href="http://en.wikipedia.org/wiki/ISO_3166"> ISO 3166 </a>
     */
    @Column(unique = true, nullable = false)
    @Index(name = "countryIso3166Alpha3CodeIndex")
    public String getIso3166Alpha3Code() {
	return this.iso3166Alpha3Code;
    }

    /**
     * @return The ISO 3166 numeric code
     */
    @Column(unique = true, nullable = false)
    @Index(name = "countryIso3166NumericCodeIndex")
    public int getIso3166NumericCode() {
	return this.iso3166NumericCode;
    }

    /*
     * Neighbour Countries @return @ManyToMany public List<Country>
     * getNeighbourCountries() { return this.neighbourCountries; }
     */

    /**
     * The Phone Prefix (e.g : 33..) without '+'
     * 
     * @return The phone prefix
     */
    public String getPhonePrefix() {
	return this.phonePrefix;
    }

    /**
     * @return The postal Code Mask (ex : #####)
     */
    public String getPostalCodeMask() {
	return this.postalCodeMask;
    }

    /**
     * All language spoken in this Country.
     * 
     * @see Language
     * @return the {@link Language} spoken in this Country
     */
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public List<Language> getSpokenLanguages() {
	return this.spokenLanguages;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.entity.GisFeature#hashCode()
     */
    @Override
    public int hashCode() {
	final int PRIME = 31;
	int result = super.hashCode();
	result = PRIME
		* result
		+ ((iso3166Alpha2Code == null) ? 0 : iso3166Alpha2Code
			.hashCode());
	return result;
    }

    /**
     * @see #getArea()
     * @param area
     */
    public void setArea(Double area) {
	this.area = area;
    }

    /**
     * Set the currency code for this country
     * 
     * @see #getCurrencyCode()
     * @param currencyCode
     *                The currencyCode to set
     */
    public void setCurrencyCode(String currencyCode) {
	this.currencyCode = currencyCode;
    }

    /**
     * @see #getEquivalentFipsCode()
     * @param equivalentFipsCode
     */
    public void setEquivalentFipsCode(String equivalentFipsCode) {
	this.equivalentFipsCode = equivalentFipsCode;
    }

    /**
     * @see #getFipsCode()
     * @param fipsCode
     *                The fipsCode to set.
     */
    public void setFipsCode(String fipsCode) {
	this.fipsCode = fipsCode;
    }

    /**
     * Set the iso3166 alpha-2 code. <u>NOTE</u> : The Code will be automaticaly uppercased, and
     * the gisfeature code will be set automatically
     * 
     * @see #getIso3166Alpha2Code()
     * @param isoCode
     *                The iso3166 alpha-2 code to set in upper case.
     */
    public void setIso3166Alpha2Code(String isoCode) {
	if (isoCode != null) {
	    this.iso3166Alpha2Code = isoCode.toUpperCase();
	} else {
	    this.iso3166Alpha2Code = isoCode;
	}
	setCountryCode(isoCode);
    }

    /**
     * <u>Note</u> : The code will be automaticaly uppercased
     * 
     * @see #getIso3166Alpha3Code()
     * @param iso3166Alpha3Code
     *                The iso3166Alpha3Code to set (in upper case).
     */
    public void setIso3166Alpha3Code(String iso3166Alpha3Code) {
	// check if we can apply touppercase method
	if (iso3166Alpha3Code != null) {
	    this.iso3166Alpha3Code = iso3166Alpha3Code.toUpperCase();
	} else {
	    this.iso3166Alpha3Code = iso3166Alpha3Code;
	}
    }

    /**
     * @see #getIso3166NumericCode()
     * @param iso3166NumericCode
     *                The iso 3166 numeric code to set.
     */
    public void setIso3166NumericCode(int iso3166NumericCode) {
	this.iso3166NumericCode = iso3166NumericCode;
    }

    /*
     * /**
     * 
     * @see #getNeighbourCountries() @param neighbourCountries public void
     *      setNeighbourCountries(List<Country> neighbourCountries) {
     *      this.neighbourCountries = neighbourCountries; }
     */

    /**
     * @see #getPhonePrefix()
     * @param phonePrefix
     */
    public void setPhonePrefix(String phonePrefix) {
	this.phonePrefix = phonePrefix;
    }

    /**
     * @see #getPostalCodeMask()
     * @param postalCodeMask
     */
    public void setPostalCodeMask(String postalCodeMask) {
	this.postalCodeMask = postalCodeMask;
    }

    /**
     * @see #getSpokenLanguages()
     * @param languages
     */
    public void setSpokenLanguages(List<Language> languages) {
	this.spokenLanguages = languages;
    }

    /**
     * @see #getSpokenLanguages()
     * @param languages
     */
    public void addSpokenLanguages(List<Language> languages) {
	this.spokenLanguages.addAll(languages);
    }

    /**
     * Get the capital for this country
     * 
     * @return the Capital for this country
     */
    public String getCapitalName() {
	return capitalName;
    }

    /**
     * @see #getCapitalName()
     */
    public void setCapitalName(String capitalName) {
	this.capitalName = capitalName;
    }

    /**
     * Returns The name of the continent this country belongs to
     * 
     * @return The name of the continent this country belongs to
     */
    public String getContinent() {
	return continent;
    }

    /**
     * @see #getContinent()
     */
    public void setContinent(String continent) {
	this.continent = continent;
    }

    /**
     * Returns The name of the currency for this country. The name is not
     * processed but imported from file.
     * 
     * @return The name of the currency
     */
    public String getCurrencyName() {
	return currencyName;
    }

    /**
     * @see #getCurrencyName()
     */
    public void setCurrencyName(String currencyName) {
	this.currencyName = currencyName;
    }

    /**
     * Returns The regexp that every Zipcode for this country matches. it is
     * useful to test if a zipcode is valid. this field is imported from file
     * and is not tested. Please report any problems.
     * 
     * @see #getPostalCodeMask()
     * @return The regexp that every zipcode for this country matches
     */
    public String getPostalCodeRegex() {
	return postalCodeRegex;
    }

    /**
     * @see #getPostalCodeRegex()
     */
    public void setPostalCodeRegex(String postalCodeRegex) {
	this.postalCodeRegex = postalCodeRegex;
    }

    /**
     * Returns the top level domain for this country with the starting point.
     * (ex : .fr)
     * 
     * @see <a href="http://en.wikipedia.org/wiki/Top-level_domain">TLD</a>
     * @return the TLD for this Country
     */
    public String getTld() {
	return tld;
    }

    /**
     * @see #getTld()
     */
    public void setTld(String tld) {
	this.tld = tld;
    }

    /**
     * compare the name of the country
     */
    public int compareTo(Country country) {
	if (getName() == null) {
	    if (country.getName() == null) {
	    	return 0;
	    } else {
	    	return -1;
	    }
	}
	return getName().compareTo(country.getName());
    }

}
