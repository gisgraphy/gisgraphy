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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.FeatureCode;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.SRID;
import com.gisgraphy.helper.FeatureClassCodeHelper;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.GisFeatureHelper;
import com.gisgraphy.helper.IntrospectionIgnoredField;
import com.gisgraphy.importer.ImporterConfig;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * GisFeature is the 'MotherClass of all Features. <b><u>IMPORTANT Note about
 * admXCodes</u></b> : <br>
 * The AdmCode can have the value from the Geonames CSV file or the value from
 * the {@link #getAdm()}.getAdm1Code It depends on the option
 * {@link ImporterConfig#isSyncAdmCodesWithLinkedAdmOnes()} in the
 * env.properties file : Gisgraphy try to detect and correct errors in the CSV
 * files. If an error is detected or wrong Adm code are set, the Adm for this
 * GisFeature may not be the one that will be found from the Code in the CSV
 * file. If syncAdmCodesWithLinkedAdmOnes is set to false, the Adm1Code will be
 * set with the value of the CSV file (even if the no {@linkplain Adm} are
 * found).<br>
 * If syncAdmCodesWithLinkedAdmOnes is set to true then the Adm1Code will always
 * be the same as the {@link #getAdm()}.getAdm1Code<br>
 * It depends on what you expect for Adm1Code : ADM values
 * (syncAdmCodesWithLinkedAdmOnes=true) or the CSV one
 * (syncAdmCodesWithLinkedAdmOnes=false)
 * 
 * @see ImporterConfig
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "gisFeatureSequence", sequenceName = "gisfeature_sequence")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class GisFeature{
	
	public static final int NAME_MAX_LENGTH= 200;
	
	public static final String SHAPE_COLUMN_NAME = "shape";

    public static final String LOCATION_COLUMN_NAME = "location";

    protected static final Logger logger = LoggerFactory
	    .getLogger(GisFeature.class);

	public static final int MAX_ALTERNATENAME_SIZE = 200;

    /**
     * Default Constructor, needed by cgLib
     */
    public GisFeature() {
	super();
    }

    /**
     * Copy Constructor that populate the current {@link GisFeature} with the
     * specified gisFeature fields<br>
     * 
     * @param gisFeature
     *                The gisFeature from which we want to populate the
     *                {@linkplain GisFeature}
     */
    public GisFeature(GisFeature gisFeature) {
	super();
	populate(gisFeature);
    }

   // @IntrospectionIgnoredField
    private Long id;

    private Long featureId;

    private String name;

    private String asciiName;

    private Set<AlternateName> alternateNames;

    private Point location;
    
    private String adm1Code;

    private String adm2Code;

    private String adm3Code;

    private String adm4Code;
    
    private String adm5Code;

    private String adm1Name;

    private String adm2Name;

    private String adm3Name;

    private String adm4Name;
    
    private String adm5Name;

    private String featureClass;

    private String featureCode;

    private String countryCode;

    @IntrospectionIgnoredField
    private Adm adm;

    private Integer population;

    private Integer elevation;

    private Integer gtopo30;

    private String timezone;

    @IntrospectionIgnoredField
    private Date modificationDate;

    @IntrospectionIgnoredField
    private GISSource source;
    
    @IntrospectionIgnoredField
    private Geometry shape;
    
    private Set<ZipCode> zipCodes;
    
    private String amenity;
    
    private Long openstreetmapId;
        
    private String isIn;
    
    private String isInPlace;
    
    private String isInAdm;
    
    @IntrospectionIgnoredField
    @Transient
    private Set<String> isInZip;
    
    /**
     * This field is only for relevance and allow to search for street<->cities in 
     * many alternateNames. It is not in stored
     */
    @IntrospectionIgnoredField
    private Set<String> isInCityAlternateNames;
   

	


	/**
     * The datastore id
     * 
     * @return The datastoreId, it is not a domain value, just a technical One
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gisFeatureSequence")
    public Long getId() {
	return this.id;
    }

    /**
     * @return the country from the country code. Return null if the country Code
     *         is null or if no country is found
     * @see #getCountryCode()
     */
    @Transient
    public Country getCountry() {
	return GisFeatureHelper.getInstance().getCountry(getCountryCode());
    }

    /**
     * @return Returns the latitude (north-south) from the Location
     *         {@link #getLocation()}.
     * @see #getLongitude()
     * @see #getLocation()
     */
    @Transient
    public Double getLatitude() {
	Double latitude = null;
	if (this.location != null) {
	    latitude = this.location.getY();
	}
	return latitude;
    }

    /**
     * Calculate the distance from the current GisFeature to the specified
     * point.
     * 
     * @param point
     *                the JTS point we want to calculate the distance from
     * @return the calculated distance
     * @see GeolocHelper#distance(Point, Point)
     */
    @Transient
    public double distanceTo(Point point) {
	return GeolocHelper.distance(this.location, point);

    }

    /**
     * Returns The JTS location point of the current GisFeature : The Geometry
     * representation of the latitude, longitude. The Return type is a JTS
     * point. The Location is calculate from the 4326 {@link SRID}
     * 
     * @see SRID
     * @see #getLongitude()
     * @see #getLocation()
     * @return The JTS Point
     */
    @Column(nullable = false, name = LOCATION_COLUMN_NAME)
    @Type(type = "org.hibernatespatial.GeometryUserType")
    public Point getLocation() {
	return location;
    }
    
    /**
     * Returns The JTS shape of the feature : The Return type is a JTS
     * geometry.
     * 
     * @see SRID
     * @see #getLongitude()
     * @see #getLocation()
     * @return The JTS Point
     */
    @Column(nullable = true, name = SHAPE_COLUMN_NAME)
    @Type(type = "org.hibernatespatial.GeometryUserType")
    public Geometry getShape() {
	return shape;
    }
    
    

    /**
     * @return Returns the longitude (east-west) from the Location
     *         {@link #getLocation()}.
     * @see #getLongitude()
     * @see #getLocation()
     */
    @Transient
    public Double getLongitude() {
	Double longitude = null;
	if (this.location != null) {
	    longitude = this.location.getX();
	}
	return longitude;
    }

    /**
     * The modification date of the feature. The date must match the
     * {@link Constants#GIS_DATE_PATTERN} This fields is not updated when saving
     * or updating a GisFeature. This fields is to track changes in the
     * gazetteers, not in the Datastore.
     * 
     * @return The modification date of the feature
     */
    public Date getModificationDate() {
	return this.modificationDate;
    }

    /**
     * Returns the Adm1Code for this feature. The only goal to have the Adm1Code
     * directly in the GisFeature is for performance reasons :<br>
     * It allows to have the adm1Code without loading all the Adm tree. See
     * Important Notes for admXcode for {@link GisFeature}
     * 
     * @return The Adm1Code for this Feature
     */
    @Index(name = "adm1codeIndex")
    @Column(length = 20)
    public String getAdm1Code() {
	return adm1Code;
    }

    /**
     * @see #getAdm1Code()
     * @param adm1Code
     *                The adm1code to set
     */
    public void setAdm1Code(String adm1Code) {
	this.adm1Code = adm1Code;
    }

    /**
     * Returns the Adm2Code for this feature. The only goal to have the Adm2Code
     * directly in the GisFeature is for performance reasons :<br>
     * It allows to have the adm2Code without loading all the Adm tree. See
     * Important Notes for admXcode for {@link GisFeature}
     * 
     * @return The Adm2Code for this Feature
     */
   // @Index(name = "adm2codeIndex")
    @Column(length = 80)
    public String getAdm2Code() {
	return adm2Code;
    }

    /**
     * @see #getAdm2Code()
     * @param adm2Code
     *                the adm2code to set
     */
    public void setAdm2Code(String adm2Code) {
	this.adm2Code = adm2Code;
    }

    /**
     * Returns the Adm3Code for this feature. The only goal to have the Adm3Code
     * directly in the GisFeature is for performance reasons :<br>
     * It allows to have the adm3Code without loading all the Adm tree. See
     * Important Notes for admXcode for {@link GisFeature}
     * 
     * @return The Adm3Code for this Feature
     */
    //@Index(name = "adm3codeIndex")
    @Column(length = 20)
    public String getAdm3Code() {
	return adm3Code;
    }

    /**
     * @see #getAdm3Code()
     * @param adm3Code
     *                the adm3code to set
     */
    public void setAdm3Code(String adm3Code) {
	this.adm3Code = adm3Code;
    }

    /**
     * Returns the Adm4Code for this feature. The only goal to have the Adm4Code
     * directly in the GisFeature is for performance reasons :<br>
     * It allows to have the adm4Code without loading the all Adm tree. See
     * Important Notes for admXcode for {@link GisFeature}
     * 
     * @return The Adm4Code for this Feature
     */
    //@Index(name = "adm4codeIndex")
    @Column(length = 20)
    public String getAdm4Code() {
	return adm4Code;
    }

    /**
     * @see #getAdm4Code()
     * @param adm4Code
     *                the adm4code to set
     */
    public void setAdm4Code(String adm4Code) {
	this.adm4Code = adm4Code;
    }
    
    /**
     * Returns the Adm4Code for this feature. The only goal to have the Adm4Code
     * directly in the GisFeature is for performance reasons :<br>
     * It allows to have the adm4Code without loading the all Adm tree. See
     * Important Notes for admXcode for {@link GisFeature}
     * 
     * @return The Adm4Code for this Feature
     */
    //@Index(name = "adm4codeIndex")
    @Column(length = 20)
    public String getAdm5Code() {
	return adm5Code;
    }

    /**
     * @see #getAdm4Code()
     * @param adm5Code
     *                the adm5Code to set
     */
    public void setAdm5Code(String adm5Code) {
	this.adm5Code = adm5Code;
    }

    /**
     * Returns the name of the Adm of level 1 that this GisFeature is linked to.
     * The only goal to have it directly in the gisFeature is for performance
     * reasons :<br>
     * It allows to have it without loading all the Adm tree
     * 
     * @return The name of the Adm of level 1 that this GisFeature is linked to
     */
   // @Index(name = "adm1NameIndex")
    @Column(length = 200)
    public String getAdm1Name() {
	return adm1Name;
    }

    /**
     * Set the name of the Adm of level 1 that this GisFeature is linked to
     * 
     * @param adm1Name
     *                The name of the Adm of level 1 that this GisFeature is
     *                linked to
     * @see #getAdm1Name()
     */
    public void setAdm1Name(String adm1Name) {
	this.adm1Name = adm1Name;
    }

    /**
     * Returns the name of the Adm of level 2 that this GisFeature is linked to.
     * The only goal to have it directly in the gisFeature is for performance
     * reasons :<br>
     * it allow to retrieve it without loading all the Adm tree
     * 
     * @return The name of the Adm of level 2 that this GisFeature is linked to
     */
   // @Index(name = "adm2NameIndex")
    @Column(length = 200)
    public String getAdm2Name() {
	return adm2Name;
    }

    /**
     * Set the name of the Adm of level 2 that this GisFeature is linked to
     * 
     * @param adm2Name
     *                The name of the Adm of level 2 that this GisFeature is
     *                linked to
     * @see #getAdm2Name()
     */
    public void setAdm2Name(String adm2Name) {
	this.adm2Name = adm2Name;
    }

    /**
     * Returns the name of the Adm of level 3 that this GisFeature is linked to.
     * The only goal to have it directly in the gisFeature is for performance
     * reasons :<br>
     * It allows to have it without loading all the Adm tree
     * 
     * @return The name of the Adm of level 3 that this GisFeature is linked to
     */
   // @Index(name = "adm3NameIndex")
    @Column(length = 200)
    public String getAdm3Name() {
	return adm3Name;
    }

    /**
     * Set the name of the Adm of level 3 that this GisFeature is linked to
     * 
     * @param adm3Name
     *                The name of the Adm of level 3 that this GisFeature is
     *                linked to
     * @see #getAdm3Name()
     */
    public void setAdm3Name(String adm3Name) {
	this.adm3Name = adm3Name;
    }

    /**
     * Returns the name of the Adm of level 4 that this GisFeature is linked to.
     * The only goal to have it directly in the gisFeature is for performance
     * reasons :<br>
     * It allows to have it without loading all the Adm tree
     * 
     * @return The name of the Adm of level 4 that this GisFeature is linked to
     */
  //  @Index(name = "adm4NameIndex")
    @Column(length = 200)
    public String getAdm4Name() {
	return adm4Name;
    }

    /**
     * Set The name of the adm of level 4 that the GisFeature is linked to
     * 
     * @param adm4Name
     *                The name of the adm of level 4 that the GisFeature is
     *                linked to
     * @see #getAdm4Name()
     */
    public void setAdm4Name(String adm4Name) {
	this.adm4Name = adm4Name;
    }
    
    /**
     * Returns the name of the Adm of level 5 that this GisFeature is linked to.
     * The only goal to have it directly in the gisFeature is for performance
     * reasons :<br>
     * It allows to have it without loading all the Adm tree
     * 
     * @return The name of the Adm of level 5 that this GisFeature is linked to
     */
  //  @Index(name = "adm4NameIndex")
    @Column(length = 200)
    public String getAdm5Name() {
	return adm5Name;
    }

    /**
     * Set The name of the adm of level 5 that the GisFeature is linked to
     * 
     * @param adm5Name
     *                The name of the adm of level 5 that the GisFeature is
     *                linked to
     * @see #getAdm5Name()
     */
    public void setAdm5Name(String adm5Name) {
	this.adm5Name = adm5Name;
    }

    /**
     * @return The Adm with the higher Level that this GisFeature is linked to
     *         (the deeper in the Adm tree). See Important Notes for admXcode
     *         for {@link GisFeature}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adm", unique = false, referencedColumnName = "id", nullable = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Index(name = "gisfeatureadmindex")
    public Adm getAdm() {
	return adm;
    }

    /**
     * @see #getAdm()
     * @param adm
     *                The Adm with the higher Level that this GisFeature is
     *                linked to (the deeper in the Adm tree).
     */
    public void setAdm(Adm adm) {
	this.adm = adm;
    }

    /**
     * @return A list of the {@link AlternateName}s for this GisFeature
     */
    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "gisFeature")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Fetch(FetchMode.SELECT)
    public Set<AlternateName> getAlternateNames() {
	return alternateNames;
    }

    /**
     * @param alternateNames
     *                The {@link AlternateName}s for this GisFeature
     */
    public void setAlternateNames(Set<AlternateName> alternateNames) {
	this.alternateNames = alternateNames;
    }

    /**
     * Do a double set : add the alternate name to the current GisFeature and set
     * this GisFeature as the GisFeature of the specified AlternateName
     * 
     * @param alternateName
     *                the alternateName to add
     */
    public void addAlternateName(AlternateName alternateName) {
    	if (alternateName!=null){
    		if (alternateName.getName() != null && alternateName.getName().length() > MAX_ALTERNATENAME_SIZE){
    			logger.warn("alternate name "+ alternateName.getName()+" is too long");
    		} else {
	    		alternateName.setGisFeature(this);
	    		Set<AlternateName> currentAlternateNames = getAlternateNames();
	    		if (currentAlternateNames == null) {
	    			currentAlternateNames = new HashSet<AlternateName>();
	    		}
	    		currentAlternateNames.add(alternateName);
	    		this.setAlternateNames(currentAlternateNames);
    		}
    	}
    }

    /**
     * Do a double set : add (not replace !) the AlternateNames to the current
     * GisFeature and for each alternatenames : set the current GisFeature as
     * the GisFeature of the Alternate Names
     * 
     * @param alternateNames
     *                The alternateNames list to add
     */
    public void addAlternateNames(List<AlternateName> alternateNames) {
	if (alternateNames != null) {
	    for (AlternateName alternateName : alternateNames) {
		addAlternateName(alternateName);
	    }
	}
    }

    /**
     * @return The ASCII name of the current GisFeature
     */
    @Column(nullable = true, length = 200)
    //@Index(name = "gisFeatureAsciiNameIndex")
    public String getAsciiName() {
	return this.asciiName;
    }

    /**
     * @see #getAsciiName()
     * @param asciiname
     *                The ASCII name of the current GisFeature
     */
    public void setAsciiName(String asciiname) {
	this.asciiName = asciiname;
    }

    /**
     * The country code is not mandatory because gisfeature like undersea does
     * not belongs to a country
     * 
     * @return The ISO 3166 alpha-2 letter code.
     */
    @Index(name = "gisFeatureCountryindex")
    @Column(length = 3)
    public String getCountryCode() {
	return countryCode;
    }

    /**
     * @param countryCode
     *                The ISO 3166 alpha-2 letter code in upper Case (it will be
     *                automatically uppercased)
     * @see #getCountryCode()
     */
    public void setCountryCode(String countryCode) {
	if (countryCode != null) {
	    this.countryCode = countryCode.toUpperCase();
	} else {
	    this.countryCode = countryCode;
	}
    }

    /**
     * @return The elevation of this gisFeature (in meters)
     */
    public Integer getElevation() {
	return elevation;
    }

    /**
     * @param elevation
     *                The elevation of this gisFeature (in meters)
     * @see #getElevation()
     */
    public void setElevation(Integer elevation) {
	this.elevation = elevation;
    }

    /**
     * @return The average elevation of 30'x30' (900mx900m) area in meters
     */
    public Integer getGtopo30() {
	return gtopo30;
    }

    /**
     * @param gtopo30
     *                The average elevation of 30'x30' (900mx900m) area to
     *                set in meters
     */
    public void setGtopo30(Integer gtopo30) {
	this.gtopo30 = gtopo30;
    }

    /**
     * @return The UTF-8 name for the current GisFeature
     */
    @Column(nullable = false, length = 200)
    //@Index(name = "gisFeatureNameIndex")
    public String getName() {
	return name;
    }

    /**
     * @param name
     *                The UTF-8 name for the current GisFeature
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return The population (how many people are in) for this GisFeature
     */
    public Integer getPopulation() {
	return population;
    }

    /**
     * @param population
     *                The population (how many people are in) of this GisFeature
     */
    public void setPopulation(Integer population) {
	this.population = population;
    }

    /**
     * @return The source for the gisFeature. it tells from which files /
     *         gazetteers it has been imported
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public GISSource getSource() {
	return source;
    }

    /**
     * @param source
     *                The source for the gisFeature to be set
     */
    public void setSource(GISSource source) {
	this.source = source;
    }

    /**
     * @return The timeZone for This GisFeature
     * @see <a
     *      href="http://download.geonames.org/export/dump/timeZones.txt">Time
     *      zone</a>
     */
    @Column(length = 40)
    public String getTimezone() {
	return timezone;
    }

    /**
     * @param timezone
     *                The timeZone for This GisFeature
     * @see #getTimezone()
     */
    public void setTimezone(String timezone) {
	this.timezone = timezone;

    }

    /**
     * @param location
     *                The location of the GisFeature (JTS point)
     * @see #getLocation()
     * @see #getLatitude()
     * @see #getLongitude()
     */
    public void setLocation(Point location) {
	this.location = location;
    }
    
    /**
     * @param shape
     *                The shape of the GisFeature (JTS)
     * @see #getShape()
     */
    public void setShape(Geometry shape) {
	this.shape = shape;
    }

    /**
     * @param modificationDate
     *                The Date of the Last Modification. This fields is not
     *                updated when saving or updating a GisFeature : This fields
     *                is to track changes in the gazetteers, not in the
     *                datastore. The date should match the
     *                {@link Constants#GIS_DATE_PATTERN}
     * @see #getModificationDate()
     */
    public void setModificationDate(Date modificationDate) {
	this.modificationDate = modificationDate;
    }

    /**
     * @param id
     *                The Id in the datastore. You should never call this
     *                method. It is the responsability of the dataStore
     * @see #getId()
     */
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * @return The featureId for this GisFeature. it is a 'Domain value' not a
     *         datastore one. A featureId is unique and mandatory
     */
    @Index(name = "gisFeatureIdIndex")
    @Column(unique = true, nullable = false)
    public Long getFeatureId() {
	return featureId;
    }

    /**
     * A featureId is unique and mandatory
     * 
     * @param featureId
     *                The featureId for this GisFeature
     */
    public void setFeatureId(Long featureId) {
	this.featureId = featureId;
    }

    /**
     * Whether the feature is a city, in the sense we define it in the
     * {@link FeatureClassCodeHelper} class. It does not check the Class but the
     * feature class and the feature code
     * 
     * @return true if it is a city
     */
    @Transient
    public boolean isCity() {
	return FeatureClassCodeHelper.isCity(this.featureClass,
		this.featureCode);
    }

    /**
     * Whether the feature is a coutry, in the sense we define it in the
     * {@link FeatureClassCodeHelper} class. It does not check the Class but the
     * feature class and the feature code
     * 
     * @return true if it is a country
     */
    @Transient
    public boolean isCountry() {
	return FeatureClassCodeHelper.isCountry(this.featureClass,
		this.featureCode);
    }

    /**
     * Whether the feature is an Adm, in the sense we define it in the
     * {@link FeatureClassCodeHelper} class. It does not check the Class but the
     * feature class and the feature code
     * 
     * @return true if it is an ADM
     */
    @Transient
    public boolean isAdm() {
	return FeatureClassCodeHelper.is_Adm(this.featureClass,
		this.featureCode);
    }

    /**
     * @return The feature class for this gisFeature (should always be in
     *         uppercase because setter automatically convert the feature class
     *         in upperCase). A feature class regroup some feature code
     * @see <a href="http://www.geonames.org/export/codes.html">codes</a>
     */
   // @Index(name = "gisFeatureFeatureClassindex")
    @Column(length = 4)
    public String getFeatureClass() {
	return featureClass;
    }

    /**
     * @param featureClass
     *                The feature class to set. <u>Note</u> The featureClass
     *                will automaticaly be uppercased.
     * @see #getFeatureClass()
     */
    public void setFeatureClass(String featureClass) {
	if (featureClass != null) {
	    this.featureClass = featureClass.toUpperCase();
	} else {
	    this.featureClass = featureClass;
	}
    }

    /**
     * @return The featureCode for the current GisFeature. A feature code
     *         represents a specific feature type. GisGraphy regroup some
     *         feature codes in an abstract Level called 'place type'. A place type
     *         regroup several featurecode.
     * @see FeatureCode
     */
    //@Index(name = "gisFeatureFeatureCodeindex")
    @Column(length = 10)
    public String getFeatureCode() {
	return featureCode;
    }

    /**
     * Populate all the field / association of the current gisFeature with The
     * Value of The specified One.
     * 
     * @param gisFeature
     *                the gisFeature to populate with
     */
    public void populate(GisFeature gisFeature) {
	if (gisFeature != null) {
	    this.setAdm(gisFeature.getAdm());

	    this.setAdm1Code(gisFeature.getAdm1Code());
	    this.setAdm2Code(gisFeature.getAdm2Code());
	    this.setAdm3Code(gisFeature.getAdm3Code());
	    this.setAdm4Code(gisFeature.getAdm4Code());

	    this.setAdm1Name(gisFeature.getAdm1Name());
	    this.setAdm2Name(gisFeature.getAdm2Name());
	    this.setAdm3Name(gisFeature.getAdm3Name());
	    this.setAdm4Name(gisFeature.getAdm4Name());

	    this.setAlternateNames(gisFeature.getAlternateNames());
	    if (getAlternateNames() != null) {
		for (AlternateName alternateName : getAlternateNames()) {
		    alternateName.setGisFeature(this);
		}
	    }
	    if (gisFeature.getAsciiName() != null) {
	    	this.setAsciiName(gisFeature.getAsciiName().trim());
	    }
	    if (gisFeature.getCountryCode() != null) {
	    	this.setCountryCode(gisFeature.getCountryCode().toUpperCase());
	    }
	    this.setElevation(gisFeature.getElevation());
	    this.setFeatureClass(gisFeature.getFeatureClass());
	    this.setFeatureCode(gisFeature.getFeatureCode());
	    this.setFeatureId(gisFeature.getFeatureId());
	    this.setGtopo30(gisFeature.getGtopo30());
	    this.setLocation(gisFeature.getLocation());
	    this.setModificationDate(gisFeature.getModificationDate());
	    this.setName(gisFeature.getName().trim());
	    this.setPopulation(gisFeature.getPopulation());
	    this.setSource(gisFeature.getSource());
	    this.setTimezone(gisFeature.getTimezone());
	    Set<ZipCode> zipCodes = gisFeature.getZipCodes();
		if (zipCodes!= null){
			for (ZipCode zipCode : zipCodes){
				this.addZipCode(zipCode);
			}
		}
		this.amenity=gisFeature.getAmenity();
		this.openstreetmapId = gisFeature.getOpenstreetmapId();
	}
    }

    /**
     * @param featureCode
     *                The feature Code for this GisFeature 
     * <u>Note</u> The
     *                featureCode will automaticaly be uppercased
     * @see #getFeatureCode()
     */
    public void setFeatureCode(String featureCode) {
	if (featureCode != null) {
	    this.featureCode = featureCode.toUpperCase();
	} else {
	    this.featureCode = featureCode;
	}
    }

    /**
     * @return true If the gisFeature must be sync with the fullText search
     *         engine
     */
    @Transient
    public boolean isFullTextSearchable() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int PRIME = 31;
	int result = 1;
	result = PRIME * result
		+ ((featureId == null) ? 0 : featureId.hashCode());
	return result;
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
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final GisFeature other = (GisFeature) obj;
	if (featureId == null) {
	    if (other.featureId != null) {
		return false;
	    }
	} else if (!featureId.equals(other.featureId)) {
	    return false;
	}
	return true;
    }

    /**
     * Returns a name of the form : (adm1Name et adm2Name are printed) Paris,
     * Département de Ville-De-Paris, Ile-De-France, (FR)
     * 
     * @param withCountry
     *                Whether the country information should be added
     * @return a name with the Administrative division and Country
     */
    @Transient
    public String getFullyQualifiedName(boolean withCountry) {
	return GisFeatureHelper.getInstance().getFullyQualifiedName(this, withCountry);
    }
    
    /**
     * @return a name with the Administrative division (but without Country)
     * wrap {@link #getFullyQualifiedName(boolean)}
     * @see #getFullyQualifiedName(boolean)
     */
    @Transient
    public String getFullyQualifiedName() {
	return getFullyQualifiedName(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return this.getClass().getSimpleName() + "[" + getFeatureId() + "]["
		+ getFeatureClass() + "." + getFeatureCode() + "][" + getName()
		+ "]";
    }

  //TODO tests zip
    /**
     * Do a double set : add the zip code to the current GisFeature and set
     * this GisFeature as the GisFeature of the zipcode
     * @param zipCode the zip code to add
     */
	public void addZipCode(ZipCode zipCode) {
		if (zipCode!=null){
			Set<ZipCode> actualZipCodes = getZipCodes();
			if (actualZipCodes == null) {
				actualZipCodes = new HashSet<ZipCode>();
			}
			actualZipCodes.add(zipCode);
			this.setZipCodes(actualZipCodes);
			zipCode.setGisFeature(this);
		}
	}

	/**
     * Do a double set : add the zip codes to the current GisFeature and set
     * this GisFeature as the GisFeature of the zipcodes
     *  * @param zipCodes the zip codes to add
     */
	//TODO tests zip with null, and so on
	public void addZipCodes(Collection<ZipCode> zipCodes) {
		if (zipCodes != null) {
		    for (ZipCode zipCode : zipCodes) {
			addZipCode(zipCode);
		    }
		}
	}

	 /**
     * @return the zip codes for the city
     */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "gisFeature")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@Fetch(FetchMode.SELECT)
	//TODO tests zip
	public Set<ZipCode> getZipCodes() {
		return zipCodes;
	}

	 /**
     * Set The zipCodes for the city. IMPORTANT : if you set the zipCodes, you should do a double set 
     * : that means that you should set the gisfeature property for all the zip codes, if you don't 
     * you will get problems when saving entity in the datastore. Please use this method
     * you should prefer the methods {@link #addZipCode(ZipCode)} and {@link #addZipCodes(Collection)}
     *  that do it automatically.
     * 
     * @param zipCodes
     *                The zip codes for the City
     */
	//TODO tests zip
	public void setZipCodes(Set<ZipCode> zipCodes) {
		this.zipCodes = zipCodes;
	}
	

	

    /**
     * @return the amenity (typically the osm tag)
     */
    public String getAmenity() {
		return amenity;
	}

    
	/**
	 * @param amenity the amenity tag 
	 * (typically the osm tag)
	 */
	public void setAmenity(String amenity) {
		this.amenity = amenity;
	}

	/**
	 * @return the openstreetmap id. 
	 * GisFeature has the openstreetmap field when it
	 * is a OSM POI
	 */
	public Long getOpenstreetmapId() {
		return openstreetmapId;
	}

	/**
	 * @param openstreetmapId
	 * The openstreetmap id of the POI
	 */
	public void setOpenstreetmapId(Long openstreetmapId) {
		this.openstreetmapId = openstreetmapId;
	}
	
	 /**
     * @return The city or state or any information where the street is located
     */
    public String getIsIn() {
	return isIn;
    }

    /**
     * @param isIn
     *            The city or state or any information where the street is
     *            located
     */
    public void setIsIn(String isIn) {
	this.isIn = isIn;
    }
    
    /**
	 * @return the place where the street is located, 
	 * this field is filled when {@link OpenStreetMap#isIn}
	 *  is filled and we got more specific details (generally quarter, neighborhood)
	 */
	public String getIsInPlace() {
		return isInPlace;
	}

	/**
	 * @param isInPlace the most precise information on where the street is located,
	 * generally quarter neighborhood
	 */
	public void setIsInPlace(String isInPlace) {
		this.isInPlace = isInPlace;
	}

	/**
	 * @return the adm (aka administrative division) where the street is located.
	 */
	public String getIsInAdm() {
		return isInAdm;
	}

	/**
	 * @param isInAdm  the adm (aka administrative division) where the street is located
	 */
	public void setIsInAdm(String isInAdm) {
		this.isInAdm = isInAdm;
	}

	//we don't sync it, because we don't want join table, for the moment
	@Transient
	public Set<String> getIsInZip() {
		return isInZip;
	}

	/**
	 * @param isInZip the zipcode where the street is located.
	 */
	public void setIsInZip(Set<String> isInZip) {
		this.isInZip = isInZip;
	}

	 /**
     * add a zip
     */
    public void addZip(String zip) {
	Set<String> currentZips = getIsInZip();
	if (currentZips == null) {
		currentZips = new HashSet<String>();
	}
	currentZips.add(zip);
	this.setIsInZip(currentZips);
    }

    /**
     * add zips
     */
    public void addZips(Collection<String> zips) {
	if (zips != null) {
	    for (String zip : zips) {
	    	addZip(zip);
	    }
	}
    }
	
	/**
	 * This field is only for relevance and allow to search for street<->cities in 
     * many alternateNames. It is not in stored
	 * 
	 */
	@Transient
	public Set<String> getIsInCityAlternateNames() {
		return isInCityAlternateNames;
	}

	public void setIsInCityAlternateNames(Set<String> isInCityAlternateNames) {
		this.isInCityAlternateNames = isInCityAlternateNames;
	}
	

    public void addIsInCitiesAlternateName(String isInCityAlternateName) {
	Set<String> currentCitiesAlternateNames = getIsInCityAlternateNames();
	if (currentCitiesAlternateNames == null) {
		currentCitiesAlternateNames = new HashSet<String>();
	}
	currentCitiesAlternateNames.add(isInCityAlternateName);
	this.setIsInCityAlternateNames(currentCitiesAlternateNames);
    }

    public void addIsInCitiesAlternateNames(Collection<String> isInCityAlternateNames) {
	if (isInCityAlternateNames != null) {
	    for (String isInCityAlternateName : isInCityAlternateNames) {
	    	addIsInCitiesAlternateName(isInCityAlternateName);
	    }
	}
    }
	
}
