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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.gisgraphy.domain.valueobject.AlternateNameSource;

/**
 * Represents an Alternate Name of a feature
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "Alternateosmname", sequenceName = "Alternateosmname_sequence")
public class AlternateOsmName {

    private Long id;

    private Integer alternateNameId;

    private String name;

    private OpenStreetMap street;

    private AlternateNameSource source;

    private String language;
    
    private String countryCode;
    


    /**
     * @param name
     *                The name of the alternate name
     * @param source
     *                what is the source the alternate name 
     * @see AlternateNameSource
     * 
     **/                
    public AlternateOsmName(String name, AlternateNameSource source,String countryCode) {
	super();
	this.name = name;
	this.source = source;
	if (countryCode==null){
		throw new RuntimeException("countrycode should not be null");
	}
	this.countryCode=countryCode.toUpperCase();
    }
    
    /**
     * @param name
     *                The name of the alternate name
     * @param source
     *                what is the source the alternate name 
     * @param language the language of the alternatename
     * @see AlternateNameSource
     * 
     **/                
    public AlternateOsmName(String name, String language,AlternateNameSource source,String countryCode) {
	super();
	this.name = name;
	if(language!=null){
		this.language=language.toUpperCase();
	}
	this.source = source;
	if (countryCode==null){
		throw new RuntimeException("countrycode should not be null");
	}
	this.countryCode=countryCode.toUpperCase();
    }

    /**
     * Default constructor (Needed by CGLib)
     */
    public AlternateOsmName() {
	super();
    }

    /**
     * The datastore id
     * 
     * @return The datastore id, it is not a domain value, just a technical One
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Alternateosmname")
    public Long getId() {
	return this.id;
    }

    /**
     * Set the datastore id. You should never call this method. It is the
     * responsability of the dataStore
     * 
     * @param id
     *                The datastore id
     */
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * @return The name of the current alternate name
     */
    //@Index(name = "Alternatenameindex")
    @Column(nullable = false, length = 200)
    public String getName() {
	return name;
    }

    /**
     * Set the name of the current alternate name
     * 
     * @param name
     *                The name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * The GisFeature, the Alternate name refers to
     * 
     * @return the GisFeature, the AlternateName refers to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "street")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Index(name = "Alternatenameosmnameindex")
    public OpenStreetMap getStreet() {
	return street;
    }

    /**
     * Set the gisFeature, the alternate name refers to
     * 
     * @param street
     *                The street, the alternate name refers to
     */
    public void setStreet(OpenStreetMap street) {
	this.street = street;
    }

    /**
     * It tells from which files / gazetteers it has been imported
     * 
     * @return The source from which the Alternate name come From (from the
     *         alternate name file, from the dump of the country file, a personal
     *         add,...)
     * @see AlternateNameSource
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AlternateNameSource getSource() {
	return source;
    }

    /**
     * Set the source from which the alternate name come from
     */
    public void setSource(AlternateNameSource source) {
	this.source = source;
    }

    /**
     * Returns the alternate name id. The id could be null if the
     * alternate name come from the country dump file.
     * 
     * @return The alternateNameId
     */
    public Integer getAlternateNameId() {
	return alternateNameId;
    }

    /**
     * Set the alternateName id
     * 
     * @param alternateNameId
     *                The id to set
     */
    public void setAlternateNameId(Integer alternateNameId) {
	this.alternateNameId = alternateNameId;
    }

   
    /**
     * @return The iso639 Alpha2 or alpha 3 LanguageCode of the
     *         {@link AlternateOsmName}
     */
    @Column(length = 30)
    public String getLanguage() {
	return language;
    }

    /**
     * Set the the iso 639 alpha2 or alpha 3 languageCode for the current
     * alternate name
     * 
     * @param language
     *                The language to set, it will be automaticaly upercased
     */
    public void setLanguage(String language) {
	this.language = language == null ? language : language.toUpperCase();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int PRIME = 31;
	int result = super.hashCode();
	result = PRIME * result
		+ ((street == null) ? 0 : street.hashCode());
	result = PRIME * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }
    
    /**
     * @return The ISO 3166 alpha-2 letter code.
     */
    @Index(name = "alternateosmnamecountryindex")
    @Column(length = 3,nullable=false)
    public String getCountryCode() {
    	return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
    	if (countryCode!=null){
    		this.countryCode = countryCode.toUpperCase();
    	}  else {
			 throw new RuntimeException("alternate osm name should have a not null countrycode");
		 }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	
	final AlternateOsmName other = (AlternateOsmName) obj;
	if (id != null && other.id!=null){
		if (other.id.equals(id)) {
		return true;
		} else {
		return false;	
		}
	}
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equalsIgnoreCase(other.name)) {
	    return false;
	}
	if (language == null) {
	    if (other.language != null) {
		return false;
	    }
	} else if (!language.equalsIgnoreCase(other.language)) {
	    return false;
	}
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return name + "[" + alternateNameId + "][linked to "
		+ street.getId() + "[lang=" + language + "][countrycode="+countryCode+"]";
    }
}
