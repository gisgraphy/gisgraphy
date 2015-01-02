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
package com.gisgraphy.webapp.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gisgraphy.domain.valueobject.FeatureCode;
import com.gisgraphy.domain.valueobject.Pagination;
import com.opensymphony.xwork2.ActionSupport;

/**
 * base class for Gisgraphy search Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class SearchAction extends ActionSupport {

    protected final static String POPUP_VIEW = "popup";

    /**
     * 
     */
    private static final long serialVersionUID = -9018894533914543310L;

    private boolean advancedSearch;

    private int from = Pagination.DEFAULT_FROM;

    private int to = Pagination.DEFAULT_MAX_RESULTS;

    private String format;

    private boolean indent;

    public static final int DEFAULT_NUMBER_OF_RESULTS_PER_PAGE = 10;

    /**
     * The error message in case of exception
     */
    protected String errorMessage = "";

    /**
     * @param from
     *                the from to set
     */
    public void setFrom(int from) {
	this.from = from;
    }

    /**
     * @param to
     *                the to to set
     */
    public void setTo(int to) {
	this.to = to;
    }

    /**
     * @param format
     *                the format to set
     */
    public void setFormat(String format) {
	this.format = format;
    }

    /**
     * @param indent
     *                the indent to set
     */
    public void setIndent(boolean indent) {
	this.indent = indent;
    }

    /**
     * @return the available placetypes
     */
    public List<String> getPlacetypes() {
	List<String> sort = new ArrayList<String>(FeatureCode.entityClass
		.keySet());
	Collections.sort(sort);
	return sort;
    }

    /**
     * @return the advancedSearch
     */
    public boolean isAdvancedSearch() {
	return advancedSearch;
    }

    /**
     * @param advancedSearch
     *                the advancedSearch to set
     */
    public void setAdvancedSearch(boolean advancedSearch) {
	this.advancedSearch = advancedSearch;
    }

    /**
     * @return the from
     */
    public int getFrom() {
	return from;
    }

    /**
     * @return the to
     */
    public int getTo() {
	return to;
    }

    /**
     * @return the format
     */
    public String getFormat() {
	return format;
    }

    /**
     * @return the indent
     */
    public boolean isIndent() {
	return indent;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * @return the dEFAULT_NUMBER_OF_RESULTS_PER_PAGE
     */
    public int getDefaultNumberOfResultsPerPage() {
	return DEFAULT_NUMBER_OF_RESULTS_PER_PAGE;
    }

}
