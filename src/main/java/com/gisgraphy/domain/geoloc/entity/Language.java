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
package com.gisgraphy.domain.geoloc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

/**
 * Represents a spoken language
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * @see <a href="http://en.wikipedia.org/wiki/List_of_languages">List Of
 *      Languages</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "languageSequence", sequenceName = "language_id_seq")
public class Language {

    /**
     * @see #getId()
     */
    private Long id;

    /**
     * @see #getIso639Alpha2LanguageCode()
     */
    private String iso639Alpha2LanguageCode;

    /**
     * @see #getIso639Alpha3LanguageCode()
     */
    private String iso639Alpha3LanguageCode;

    /**
     * @see #getIso639LanguageName()
     */
    private String iso639LanguageName;

    public Language() {
	super();
    }

    /**
     * @param iso639LanguageName
     *                The iso 639 Language name for the current language
     * @param iso639Alpha2LanguageCode
     *                The iso 639 alpha-2 code for the current language
     * @param iso639Alpha3LanguageCode
     *                The iso 639 alpha-3 code for the current language
     */
    public Language(String iso639LanguageName, String iso639Alpha2LanguageCode,
	    String iso639Alpha3LanguageCode) {
	super();

	this.iso639LanguageName = iso639LanguageName;
	this.iso639Alpha2LanguageCode = iso639Alpha2LanguageCode;
	this.iso639Alpha3LanguageCode = iso639Alpha3LanguageCode;
    }

    /**
     * @return the datastore ID, it is not a domain value, just a technical one
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "languageSequence")
    public Long getId() {
	return this.id;
    }

    /**
     * @return The iso 639 Alpha-2 letter code, if available
     */
    @Column(unique = true, nullable = true)
    @Index(name = "iso639Alpha2LanguageCode")
    public String getIso639Alpha2LanguageCode() {
	return this.iso639Alpha2LanguageCode;
    }

    /**
     * @return ISO-3 lettter code. Must be present
     */
    @Column(unique = true, nullable = false)
    @Index(name = "iso639Alpha3LanguageCode")
    public String getIso639Alpha3LanguageCode() {
	return this.iso639Alpha3LanguageCode;
    }

    /**
     * @return official ISO 639 name
     */
    @Column(unique = false, nullable = false)
    public String getIso639LanguageName() {
	return this.iso639LanguageName;
    }

    /**
     * @param id
     *                The datastore id. You should never call this method. It is
     *                the responsibility of the dataStore
     * @see #getId()
     */
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * @see #getIso639Alpha2LanguageCode()
     * @param iso639Alpha2LanguageCode
     */
    public void setIso639Alpha2LanguageCode(String iso639Alpha2LanguageCode) {
	this.iso639Alpha2LanguageCode = iso639Alpha2LanguageCode;
    }

    /**
     * @see #getIso639Alpha3LanguageCode()
     * @param iso639Alpha3LanguageCode
     */
    public void setIso639Alpha3LanguageCode(String iso639Alpha3LanguageCode) {
	this.iso639Alpha3LanguageCode = iso639Alpha3LanguageCode;
    }

    /**
     * @see #getIso639LanguageName()
     * @param iso639LanguageName
     */
    public void setIso639LanguageName(String iso639LanguageName) {
	this.iso639LanguageName = iso639LanguageName;
    }
}
