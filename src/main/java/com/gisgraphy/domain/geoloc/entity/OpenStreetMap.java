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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.SRID;
import com.gisgraphy.domain.valueobject.SpeedMode;
import com.gisgraphy.helper.IntrospectionIgnoredField;
import com.gisgraphy.street.HouseNumberComparator;
import com.gisgraphy.street.StreetSearchMode;
import com.gisgraphy.street.StreetType;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Represents a street in OpenStreetMap. it is different from {@link Street} that represent a street in Geonames.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "streetosmsequence", sequenceName = "street_osm_sequence")
public class OpenStreetMap {
	
	 protected static final Logger logger = LoggerFactory
			    .getLogger(OpenStreetMap.class);
	
	@Transient
	private static final HouseNumberComparator houseNumberComparator = new HouseNumberComparator();
	

    public static final String SHAPE_COLUMN_NAME = "shape";
    
    public static final int MAX_ALTERNATENAME_SIZE = 200;

   

    
    /**
     * Name of the field property in hibernate. This is a string that is used
     * for fulltext and contains search without postgres fulltext engine. this
     * fields will have the name without accent and special char This value
     * should be changed if the getter and the setter of the
     * {@link #getTextsearchVector()} change
     * 
     * @see StreetSearchMode#FULLTEXT
     */
    public static final String FULLTEXTSEARCH_PROPERTY_NAME = "textSearchName";

    /**
     * Name of the column that is equals to store a string that is used for
     * fulltext search. it deffer form the @{@link #FULLTEXTSEARCH_COLUMN_NAME}
     * because Hibernate, by default, lowercase the property to get the column
     * name This value should be change if the getter and the setter of the
     * {@link #getTextsearchVector()} change
     * 
     * @see StreetSearchMode#FULLTEXT
     */
    public static final String FULLTEXTSEARCH_COLUMN_NAME = FULLTEXTSEARCH_PROPERTY_NAME.toLowerCase();


    public static final String LOCATION_COLUMN_NAME = "location";

    /**
     * Needed by CGLib
     */
    public OpenStreetMap() {
    }

    @IntrospectionIgnoredField
    private Long id;

    private Long gid;
    
    private Long openstreetmapId;

    private String name;

    private StreetType streetType;

    private boolean oneWay = false;

    private Point location;

    @IntrospectionIgnoredField
    private LineString shape;
    
    @IntrospectionIgnoredField
    private Integer population;
    
    private String isIn;
    
    private String isInPlace;
    
    private String isInAdm;
    
    private String streetRef;
    
private GISSource source;

    
   
    
    /**
	 * @return the streetRef
	 */
	public String getStreetRef() {
		return streetRef;
	}

	/**
	 * @param streetRef the streetRef to set
	 */
	public void setStreetRef(String streetRef) {
		this.streetRef = streetRef;
	}

	/**
     * This field is only for relevance and allow to search for street<->cities in 
     * many alternateNames. It is not in stored
     */
    @IntrospectionIgnoredField
    private Set<String> isInCityAlternateNames;
    
   
    
    private String fullyQualifiedName;

    private String countryCode;

    private Double length;
    
    //@Sort(comparator=HouseNumberComparator.class,type=SortType.COMPARATOR)
    private SortedSet<HouseNumber> houseNumbers;

    @IntrospectionIgnoredField
    private String partialSearchName;

    @IntrospectionIgnoredField
    private String textSearchName;
    
    private List<AlternateOsmName> alternateNames;
    
    @IntrospectionIgnoredField
    private Long cityId;
    /**
     * if the associated city has been found by shape 
     * (only for sttistics and relevance purpose
     */
    @IntrospectionIgnoredField
    private boolean cityConfident = false;
    
    private String adm1Name;
    
    private String adm2Name;
    
    private String adm3Name;
    
    private String adm4Name;
    
    private String adm5Name;
    
    private Integer lanes;
    
    private Boolean toll;
    
    private String surface;
    
    private SpeedMode speedMode;
    
    private String maxSpeed;
    
    private String maxSpeedBackward;
    
    private Integer azimuthStart;
    
    private Integer azimuthEnd;
    
    private String label;
    
    private String labelPostal;
    
    //the zip often one (often from the city), we don't want to have an associated database so we stock this here as transcient field
    // and populate the fulltext engine with that.
    @IntrospectionIgnoredField
    private Set<String> isInZip;
    //the zipcode, it is the best choice between all isInZip
    private String zipCode;
    
   
	@IntrospectionIgnoredField
    private Set<String> alternateLabels;
    
    /**
	 * @return the label that represent the Postal address
	 */
	 @Column(length = 500)
	public String getLabelPostal() {
		return labelPostal;
	}

	/**
	 * @param labelPostal the labelPostal to set
	 */
	public void setLabelPostal(String labelPostal) {
		this.labelPostal = labelPostal;
	}

    
    
    
    /**
	 * @return the label that represent the street
	 */
	 @Column(length = 500)
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the alternate Labels of the streets
	 */
	@Transient
	public Set<String> getAlternateLabels() {
		return alternateLabels;
	}

	/**
	 * @param alternateLabels the alternate Labels to set
	 */
	public void setAlternateLabels(Set<String> alternateLabels) {
		this.alternateLabels = alternateLabels;
	}

	/**
	 * @return the number of lanes
	 */
	public Integer getLanes() {
		return lanes;
	}

	/**
	 * @param lanes the number of lanes to set
	 */
	public void setLanes(Integer lanes) {
		this.lanes = lanes;
	}

	/**
	 * @return whether the street is toll or free
	 */
	public Boolean isToll() {
		return toll;
	}

	/**
	 * @param toll whether the street is toll or free
	 */
	public void setToll(Boolean toll) {
		this.toll = toll;
	}

	/**
	 * @return the physical surface of the street
	 */
	public String getSurface() {
		return surface;
	}

	/**
	 * @param surface the surface to set
	 */
	public void setSurface(String surface) {
		this.surface = surface;
	}

	/**
	 * @return the maxSpeed of the street
	 */
	public String getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * @param maxSpeed the max speed to set
	 */
	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * @return the max Speed in the backward direction
	 */
	public String getMaxSpeedBackward() {
		return maxSpeedBackward;
	}

	/**
	 * @param maxSpeedBackward the max Speed in the Backward to set
	 */
	public void setMaxSpeedBackward(String maxSpeedBackward) {
		this.maxSpeedBackward = maxSpeedBackward;
	}

	/**
	 * @return the azimuth at the start of the street
	 */
	public Integer getAzimuthStart() {
		return azimuthStart;
	}

	/**
	 * @param azimuthStart the azimuth to set
	 */
	public void setAzimuthStart(Integer azimuthStart) {
		this.azimuthStart = azimuthStart;
	}

	/**
	 * @return the azimuth at the end of the street
	 */
	public Integer getAzimuthEnd() {
		return azimuthEnd;
	}

	/**
	 * @param azimuthEnd the azimuth to set
	 */
	public void setAzimuthEnd(Integer azimuthEnd) {
		this.azimuthEnd = azimuthEnd;
	}

	/**
     * @return the id of the city, we don't use the object because we don't want to link objects
     * for performance reasons
     */
    public Long getCityId() {
    	return cityId;
    }
    
    /**
     * @param set the id of the city of this street
     */
    public void setCityId(Long cityId) {
    	this.cityId = cityId;
    }
    
    
    
    /**
     * @return A list of the {@link AlternateName}s for this street
     */
    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "street")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Fetch(FetchMode.SELECT)
    public List<AlternateOsmName> getAlternateNames() {
    	return alternateNames;
    }
    
    /**
     * @param alternateNames
     *                The {@link AlternateName}s for this street
     */
    public void setAlternateNames(List<AlternateOsmName> alternateNames) {
    	this.alternateNames = alternateNames;
    }

    /**
     * Do a double set : add the alternate name to the current GisFeature and set
     * this GisFeature as the GisFeature of the specified AlternateName
     * 
     * @param alternateName
     *                the alternateName to add
     */
    public void addAlternateName(AlternateOsmName alternateName) {
    	if (alternateName!=null){
    		if (alternateName.getName() != null && alternateName.getName().length() > MAX_ALTERNATENAME_SIZE){
    			logger.warn("alternate name "+ alternateName.getName()+" is too long");
    		} else {
    		List<AlternateOsmName> currentAlternateNames = getAlternateNames();
    		if (currentAlternateNames == null) {
    			currentAlternateNames = new ArrayList<AlternateOsmName>();
    		}
    		currentAlternateNames.add(alternateName);
    		this.setAlternateNames(currentAlternateNames);
    		alternateName.setStreet(this);
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
    public void addAlternateNames(List<AlternateOsmName> alternateNames) {
	if (alternateNames != null) {
	    for (AlternateOsmName alternateName : alternateNames) {
		addAlternateName(alternateName);
	    }
	}
    }
    
    
    
    public void addAlternateLabel(String alternateLabel) {
		if (alternateLabel!=null){
			Set<String> currentLabels = getAlternateLabels();
			if (currentLabels == null) {
				currentLabels = new HashSet<String>();
			}
			currentLabels.add(alternateLabel);
			this.setAlternateLabels(currentLabels);
		}
	}

    public void addAlternateLabels(Collection<String> alternateLabels) {
	if (alternateLabels != null) {
	    for (String label : alternateLabels) {
	    	addAlternateLabel(label);
	    }
	}
    }
    
    

    /**
     * (Experimental) This String is used to search for a part of a street name
     * 
     * @see StreetSearchMode#CONTAINS
     * @return the partialSearchName
     */
    @Column(unique = false, nullable = true, columnDefinition = "text")
    public String getPartialSearchName() {
	return partialSearchName;
    }

    /**
     * @param partialSearchName
     *            the partialSearchName to set
     */
    public void setPartialSearchName(String partialSearchName) {
	this.partialSearchName = partialSearchName;
    }

    /**
     * This value is use to do a Fulltext search for a street name with index
     * 
     * @return the textSearchName
     */
    @Column(unique = false, nullable = true, columnDefinition = "text")
    public String getTextSearchName() {
	return textSearchName;
    }

    /**
     * @param textSearchName
     *            the textSearchName to set
     */
    public void setTextSearchName(String textSearchName) {
	this.textSearchName = textSearchName;
    }

    /**
     * IT DOES NOTHING. ONLY USE BY HIBERNATE This field is only use for the
     * text search to improve performance, you should not set / get a value, it
     * is declared here, to create the column
     * 
     * @return null ALWAYS
     */
    @Column(unique = false, nullable = true, insertable = false, updatable = true, columnDefinition = "tsvector")
    @Type(type = "com.gisgraphy.hibernate.type.TsVectorStringType")
    public String getTextsearchVector() {
	return null;
    }

    /**
     * IT DOES NOTHING. ONLY USE BY HIBERNATE
     * 
     * @param textsearchVector
     *            the textsearchVector to set
     * 
     */
    public void setTextsearchVector(String textsearchVector) {
    }

    /**
     * IT DOES NOTHING. ONLY USE BY HIBERNATE This field is only use for the
     * autocomplete search to improve performance, you should not set / get a
     * value, it is declared here, to create the column
     * 
     * @return null ALWAYS
     */
    @Column(unique = false, nullable = true, insertable = false, updatable = true, columnDefinition = "tsvector")
    @Type(type = "com.gisgraphy.hibernate.type.TsVectorStringType")
    public String getPartialsearchVector() {
	return null;
    }

    /**
     * IT DOES NOTHING. ONLY USE BY HIBERNATE
     * 
     * @param partialsearchVector
     *            the ilikesearch to set
     */
    public void setPartialsearchVector(String partialsearchVector) {

    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "streetosmsequence")
    public Long getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * @return an uniqueid that identify the street, it differs from {@link OpenStreetMap#openstreetmapId}
     *  because the value can not be in conflict between geonames and openstreetmap
     */
    @Index(name = "streetosmgidindex")
    @Column(unique = true, nullable = false)
    public Long getGid() {
	return gid;
    }

    /**
     * @param gid
     *            the gid to set
     */
    public void setGid(Long gid) {
	this.gid = gid;
    }
    
    /**
     * @return the openstreetmap internal id
     */
    @Index(name = "streetosmopenstreetmapidindex")
    @Column(unique = false, nullable = true)
    public Long getOpenstreetmapId() {
        return openstreetmapId;
    }

    /**
     * @param openstreetmapId the openstreetmap internal id
     */
    public void setOpenstreetmapId(Long openstreetmapId) {
        this.openstreetmapId = openstreetmapId;
    }

    /**
     * @return the name
     */
    @Column(length = 255)
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the type of the street
     */
    @Index(name = "streetosmtypeIndex")
    @Enumerated(EnumType.STRING)
    public StreetType getStreetType() {
	return streetType;
    }

    /**
     * @param streetType
     *            the streetType to set
     */
    public void setStreetType(StreetType streetType) {
	this.streetType = streetType;
    }
    
    

    /**
	 * @return the speedMode
	 */
    @Enumerated(EnumType.STRING)
	public SpeedMode getSpeedMode() {
		return speedMode;
	}

	/**
	 * @param speedMode the speedMode to set
	 */
	public void setSpeedMode(SpeedMode speedMode) {
		this.speedMode = speedMode;
	}

	/**
     * @return the oneway
     */
    @Index(name = "streetosmonewayIndex")
    @Column(length = 9)
    public boolean isOneWay() {
	return oneWay;
    }

    /**
     * @param oneWay
     *            the oneWay to set
     */
    public void setOneWay(boolean oneWay) {
	this.oneWay = oneWay;
    }

    /**
     * Returns The JTS location point of the current street : The Geometry
     * representation for the latitude, longitude. The Return type is a JTS
     * point. The Location is calculate from the 4326 {@link SRID}
     * 
     * @see SRID
     * @return The JTS Point
     */
    @Type(type = "org.hibernatespatial.GeometryUserType")
    @Column(name = OpenStreetMap.LOCATION_COLUMN_NAME)
    public Point getLocation() {
	return location;
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
     * @param location
     *            the location to set
     */
    public void setLocation(Point location) {
	this.location = location;
    }

    /**
     * @return the shape
     */
    @Type(type = "org.hibernatespatial.GeometryUserType")
    @Column(nullable = false)
    public LineString getShape() {
	return shape;
    }

    /**
     * @param shape
     *            the shape to set
     */
    public void setShape(LineString shape) {
	this.shape = shape;
    }

    /**
     * @return The ISO 3166 alpha-2 letter code.
     */
    @Index(name = "openstreetmapcountryindex")
    @Column(length = 3)
    public String getCountryCode() {
	return countryCode;
    }

    /**
     * @param countryCode
     *            the countryCode to set
     */
    public void setCountryCode(String countryCode) {
	this.countryCode = countryCode;
    }

    /**
     * @return the length of the street in meters
     */
    public Double getLength() {
	return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(Double length) {
	this.length = length;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    //TODO use business key
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	OpenStreetMap other = (OpenStreetMap) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
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

	/**
	 * @return the zipcode where the street is located
	 */
	//don't want to store it, just for fulltext purpose
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
    public void addIsInZip(String zip) {
	Set<String> currentZips = getIsInZip();
	if (currentZips == null) {
		currentZips = new HashSet<String>();
	}
	if (zip != null && zip.length()>=3){
		//there is some error in zip code and we avoid sip if it is less than 2 character
		currentZips.add(zip);
	}
	this.setIsInZip(currentZips);
    }

    /**
     * add zips
     */
    public void addIsInZips(Collection<String> zips) {
	if (zips != null) {
	    for (String zip : zips) {
	    	addIsInZip(zip);
	    }
	}
    }

    @Column(length = 500)
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public Integer getPopulation() {
		return population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}
	
	public String getAdm1Name() {
		return adm1Name;
	}

	public void setAdm1Name(String adm1Name) {
		this.adm1Name = adm1Name;
	}

	public String getAdm2Name() {
		return adm2Name;
	}

	public void setAdm2Name(String adm2Name) {
		this.adm2Name = adm2Name;
	}

	public String getAdm3Name() {
		return adm3Name;
	}

	public void setAdm3Name(String adm3Name) {
		this.adm3Name = adm3Name;
	}

	public String getAdm4Name() {
		return adm4Name;
	}

	public void setAdm4Name(String adm4Name) {
		this.adm4Name = adm4Name;
	}

	public String getAdm5Name() {
		return adm5Name;
	}

	public void setAdm5Name(String adm5Name) {
		this.adm5Name = adm5Name;
	}
	
 	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}


	/**
	 * @return the houseNumbers associated to that street
	 */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "street")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Fetch(FetchMode.SELECT)
	@Sort(comparator=HouseNumberComparator.class,type=SortType.COMPARATOR)
	public SortedSet<HouseNumber> getHouseNumbers() {
		return houseNumbers;
	}

	/**
	 * @param houseNumbers the houseNumbers to set
	 */
	public void setHouseNumbers(SortedSet<HouseNumber> houseNumbers) {
		this.houseNumbers = houseNumbers;
	}
	
	/**
     * Do a double set : add the house number to the current street and set
     * this street as the street of the specified AlternateName
     * 
     * @param houseNumber
     *                the houseNumber to add
     */
	public void addHouseNumber(HouseNumber houseNumber) {
		if (houseNumber!=null){
			SortedSet<HouseNumber> currentHouseNumbers = getHouseNumbers();
			if (currentHouseNumbers == null) {
				currentHouseNumbers = new TreeSet<HouseNumber>(houseNumberComparator);
			}
			currentHouseNumbers.add(houseNumber);
			this.setHouseNumbers(currentHouseNumbers);
			houseNumber.setStreet(this);
		}
	}

    /**
     * Do a double set : add (not replace !) the House Numbers to the current
     * street and for each House numbers : set the current street as
     * the street of the House Numbers
     * 
     * @param HouseNumbers
     *                The House Numbers list to add
     */
    public void addHouseNumbers(List<HouseNumber> HouseNumbers) {
	if (HouseNumbers != null) {
	    for (HouseNumber houseNumber : HouseNumbers) {
	    	addHouseNumber(houseNumber);
	    }
	}
    }
    /**
   	 * @return the source
   	 */

        @Enumerated(EnumType.STRING)
   	public GISSource getSource() {
   		return source;
   	}

   	/**
   	 * @param source the source to set
   	 */
   	public void setSource(GISSource source) {
   		this.source = source;
   	}

	public boolean isCityConfident() {
		return cityConfident;
	}

	public void setCityConfident(boolean cityConfident) {
		this.cityConfident = cityConfident;
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
		if (isInCityAlternateName!=null){
			Set<String> currentCitiesAlternateNames = getIsInCityAlternateNames();
			if (currentCitiesAlternateNames == null) {
				currentCitiesAlternateNames = new HashSet<String>();
			}
			currentCitiesAlternateNames.add(isInCityAlternateName);
			this.setIsInCityAlternateNames(currentCitiesAlternateNames);
		}
	}

    public void addIsInCitiesAlternateNames(Collection<String> isInCityAlternateNames) {
	if (isInCityAlternateNames != null) {
	    for (String isInCityAlternateName : isInCityAlternateNames) {
	    	addIsInCitiesAlternateName(isInCityAlternateName);
	    }
	}
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OpenStreetMap [");
		if (openstreetmapId != null)
			builder.append("openstreetmapId=").append(openstreetmapId)
					.append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (location != null)
			builder.append("location=").append(location);
		builder.append("]");
		return builder.toString();
	}

}
