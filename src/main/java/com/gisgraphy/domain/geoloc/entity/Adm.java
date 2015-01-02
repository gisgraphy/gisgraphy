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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;

import com.gisgraphy.helper.FeatureClassCodeHelper;
import com.gisgraphy.helper.IntrospectionIgnoredField;

/**
 * Represents a (sub) division of a {@link Country} (Region, Province, state,
 * Department, and so on)<br>
 * {@linkplain Adm} are in tree structure. An Adm can have some children and MUST
 * have a parent if the Level is > 1 (an Adm with level 1 is to be a 'ROOT' Adm)
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Adm extends GisFeature {

    /**
     * Constructor that populate the Adm with the gisFeature fields and set the
     * level<br>
     * <u>note</u> The feature class will be set to 'A' and The feature code
     * will be set according to the Level (ADM + level)
     * 
     * @param gisFeature
     *                The gisFeature we want to populate the
     *                {@linkplain Adm}
     * @param level
     *                The level of the Adm
     */
    public Adm(GisFeature gisFeature, Integer level) {
	super(gisFeature);
	setFeatureClass("A");
	setLevel(level);
	setFeatureCode("ADM" + getLevel());
    }

    /**
     * Constructor that create an Adm for the specified level<br>
     * <u>note</u> the feature class will be set to 'A' and The feature code will
     * be set according to the Level (ADM + level)
     * 
     * @param level
     *                The level of the Adm
     */
    public Adm(Integer level) {
	setLevel(level);
	setFeatureClass("A");
	setFeatureCode("ADM" + getLevel());
    }

    /**
     * Default constructor (Needed by CGLib)
     */
    protected Adm() {
	super();
    }

    /**
     * Check that the country code is filled and the admXcode are correctly filled according
     * to the level
     */
    @Transient
    public boolean isConsistentForLevel() {
	if (getCountryCode() == null) {
	    return false;
	}
	if (this.getLevel() == 1 && this.getAdm1Code() == null) {
	    return false;
	} else if (this.getLevel() == 2
		&& (this.getAdm1Code() == null || this.getAdm2Code() == null)) {
	    return false;
	} else if (this.getLevel() == 3
		&& (this.getAdm1Code() == null || this.getAdm2Code() == null || this
			.getAdm3Code() == null)) {
	    return false;
	} else if (this.getLevel() == 4
		&& (this.getAdm1Code() == null || this.getAdm2Code() == null
			|| this.getAdm3Code() == null || this.getAdm4Code() == null)) {
	    return false;
	}
	return true;
    }

    private Integer level;

    @IntrospectionIgnoredField
    private Adm parent;

    private List<Adm> children;

    /**
     * @return The Level Of The Adm
     */
    @Column(nullable = false)
    @Index(name = "admLevel")
    public Integer getLevel() {
	return level;
    }

    /**
     * Set the level and Check that 1<= level<= 4. If it is not the case, throw an
     * {@link IllegalArgumentException}
     * 
     * @param level
     *                The Level to set
     * @throws IllegalArgumentException
     *                 If level is not correct
     */
    public void setLevel(Integer level) {
	if (level < 1 || level > 4) {
	    throw new IllegalArgumentException(
		    "The level of an Adm can not be " + level
			    + ". it must be beetween 1 and 4");
	}
	this.level = level;
    }

    /**
     * Do a double set : Add (not replace ! ) The child to the current Adm and
     * set the current Adm as the Parent of the specified Child.
     * 
     * @param child
     *                The child to add 
     * @throws IllegalArgumentException if the level of the child 
     * is not equals to the level of this Adm +1
     */
    public void addChild(Adm child) {
	if (child == null) {
	    throw new IllegalArgumentException("Could not add a null child to "
		    + this);
	}
	if (child.getLevel() != getLevel() + 1) {
	    throw new IllegalArgumentException("a child of level "
		    + child.getLevel() + " (" + child
		    + ") should not be added to an Adm with level " + getLevel()
		    + " : " + child + " but will be added");
	}
	List<Adm> currentChilds = getChildren();
	if (currentChilds == null) {
	    currentChilds = new ArrayList<Adm>();
	}
	currentChilds.add(child);
	this.setChildren(currentChilds);
	child.setParent(this);

    }

    /**
     * Do a double set : Add (not replace !) the children to the current Adm and
     * set the current Adm as the parent of the specified Children
     * 
     * @see #addChild(Adm)
     * @param children
     *                The children to add
     */
    public void addChildren(List<Adm> children) {
	if (children != null) {
	    for (Adm child : children) {
		addChild(child);
	    }
	}
	;
    }

    /**
     * Return the Adms of a directly higher Level in the adm the tree structure
     * @return The Adms of a directly higher Level <br>
     * <b>Example</b> Returns the Adm(s) with level 2 if the current
     *         Adm has a level equals to 1
     */
    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Fetch(FetchMode.SELECT)
    public List<Adm> getChildren() {
	return children;
    }

    /**
     * Set the Adms of a directly higher level
     * 
     * @param children
     *                the children for the current Adm
     */
    // @throws IllegalArgumentException If the children are are not equals to
    // the level of this Adm+1
    public void setChildren(List<Adm> children) {
	// TODO v2 DSL
	/*
	 * try { if (children != null ){ for (Adm child : children){ if
	 * (child.getLevel()!= getLevel()+1){ throw new
	 * IllegalArgumentException("Could not add a child of level
	 * "+child.getLevel()+" to an Adm of level "+getLevel()); } } }
	 */
	this.children = children;

    }

    /**
     * Returns The parent Adm in the Adm tree structure
     * 
     * @return The parent Adm (with lower Level)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "parent")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Index(name = "admadmindex")
    public Adm getParent() {
	return parent;
    }

    /**
     * Set the parent Adm in the tree structure
     * 
     * @param parent
     *                the Parent Adm to set
     */
    // @throws IllegalArgumentException if the parent is not equals to the level
    // of this Adm-1
    public void setParent(Adm parent) {
	// TODO v2 dsl
	/*
	 * if (parent != null && parent.getLevel()!=getLevel()-1 ){ throw new
	 * IllegalArgumentException("Could not add a null child"); }
	 */
	this.parent = parent;
    }

    private static boolean isAdmCodeEmpty(String admCode) {
	if (admCode == null || admCode.trim().length() == 0) {
	    return true;
	}
	return false;
    }

    /**
     * Determine what should be the level of 
     * an Adm which have the provided codes
     * 
     * @param adm1Code
     *                The Adm1Code of the Adm to test
     * @param adm2Code
     *                The Adm2Code of the Adm to test
     * @param adm3Code
     *                The Adm3Code of the Adm to test
     * @param adm4Code
     *                The Adm4Code of the Adm to test
     * @return the processed Level or 0 if the level can not be determine or all
     *         the code are null
     */
    public static int getProcessedLevelFromCodes(String adm1Code,
	    String adm2Code, String adm3Code, String adm4Code) {
	if (!isAdmCodeEmpty(adm1Code)) {
	    if (!isAdmCodeEmpty(adm2Code)) {
		if (!isAdmCodeEmpty(adm3Code)) {
		    if (!isAdmCodeEmpty(adm4Code)) {
			// adm1,adm2,adm3,adm4
			return 4;
		    } else {
			// adm1,adm2,adm3,null
			return 3;
		    }
		} else {
		    // adm1,Adm2, null..
		    return 2;
		}
	    } else {
		// adm1,null...
		return 1;
	    }
	} else {
	    // if adm1 is empty can not retrieve any Adm
	    return 0;
	}

    }

    /**
     * Determine what should be the level of an adm which have a the 
     * specified featureClass and a featureCode.<br/> e.g :
     * featureClass=A and featureCode=ADM3 will return 3 .<br/> featureClass=P
     * and featureCode=ADM4 will return 0 because P_ADM4 is not of ADM type. This
     * method is case sensitive.
     * 
     * @param featureClass
     *                The featureClass of the Adm to test
     * @param featureCode
     *                The featureCode of the Adm to Test
     * @return the Level of the Adm or 0 if the level can not be determine
     */
    public static int getProcessedLevelFromFeatureClassCode(
	    String featureClass, String featureCode) {
	if (FeatureClassCodeHelper.is_Adm(featureClass, featureCode)) {
	    int level = 0;
	    try {
		level = new Integer(featureCode.substring(3)).intValue();
	    } catch (NumberFormatException e) {
	    }
	    return level;

	} else {
	    return 0;
	}

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
	result = PRIME * result
		+ ((getAdm1Code() == null) ? 0 : getAdm1Code().hashCode());
	result = PRIME * result
		+ ((getAdm2Code() == null) ? 0 : getAdm2Code().hashCode());
	result = PRIME * result
		+ ((getAdm3Code() == null) ? 0 : getAdm3Code().hashCode());
	result = PRIME * result
		+ ((getAdm4Code() == null) ? 0 : getAdm4Code().hashCode());
	result = PRIME * result + ((level == null) ? 0 : level.hashCode());
	return result;
    }

    /**
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
	final Adm other = (Adm) obj;
	if (level == null) {
	    if (other.level != null) {
		return false;
	    }
	} else if (!level.equals(other.level)) {
	    return false;
	}
	if (getCountryCode() == null) {
	    if (other.getCountryCode() != null) {
		return false;
	    }
	} else if (!getCountryCode().equals(other.getCountryCode())) {
	    return false;
	}
	if (getAdm1Code() == null) {
	    if (other.getAdm1Code() != null) {
		return false;
	    }
	} else if (!getAdm1Code().equals(other.getAdm1Code())) {
	    return false;
	}
	if (getAdm2Code() == null) {
	    if (other.getAdm2Code() != null) {
		return false;
	    }
	} else if (!getAdm2Code().equals(other.getAdm2Code())) {
	    return false;
	}
	if (getAdm3Code() == null) {
	    if (other.getAdm3Code() != null) {
		return false;
	    }
	} else if (!getAdm3Code().equals(other.getAdm3Code())) {
	    return false;
	}
	if (getAdm4Code() == null) {
	    if (other.getAdm4Code() != null) {
		return false;
	    }
	} else if (!getAdm4Code().equals(other.getAdm4Code())) {
	    return false;
	}
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.entity.GisFeature#toString()
     */
    @Override
    public String toString() {
	return "Adm[" + getCountryCode() + "." + getAdm1Code() + "."
		+ getAdm2Code() + "." + getAdm3Code() + "." + getAdm4Code()
		+ "][level=" + getLevel() + "][" + getFeatureId() + "]["
		+ getName() + "]";
    }

}
