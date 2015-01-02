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
package com.gisgraphy.domain.valueobject;

import org.dom4j.io.OutputFormat;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a> Some
 *         Constants
 */
public class Constants {

    /**
     * Default charset
     */
    public static final String CHARSET = "UTF-8";

    /**
     * Default Date pattern
     */
    public static final String GIS_DATE_PATTERN = "yyyy-MM-dd";

    /**
     * The field on which we want to search
     */
    public static final String QF_PARAMETER = "qf";

    /**
     * The output format (XML, json,...) parameter name
     * 
     * @see OutputFormat
     */
    public final static String OUTPUT_FORMAT_PARAMETER = "wt";

    /**
     * The XSLT stylesheet parameter name
     * 
     * @see OutputFormat
     */
    public final static String STYLESHEET_PARAMETER = "tr";

    /**
     * The name of the XSL to apply to output ATOM format
     * 
     * @see OutputFormat
     */
    public final static String ATOM_STYLESHEET = "atom.xsl";

    /**
     * The name of the XSL to apply to output Geo RSS format
     * 
     * @see OutputFormat
     */
    public final static String GEORSS_STYLESHEET = "georss.xsl";

    /**
     * The field list to return (Solr parameter name)
     */
    public static final String FL_PARAMETER = "fl";
    
    /**
     * The field list to return (Solr parameter name)
     */
    public static final String FQ_PARAMETER = "fq";

    /**
     * The parameter to specifie the text to search (Solr parameter name)
     */
    public static final String QUERY_PARAMETER = "q";

    /**
     * The query type parameter (dismax, standard, ...) (Solr parameter name)
     */
    public static final String QT_PARAMETER = "qt";
    
    /**
     * The boost query type parameter 
     */
    public static final String BQ_PARAMETER = "bq";
    
    /**
     * The boost field type parameter 
     */
    public static final String BF_PARAMETER = "bf";

    /**
     * The rows parameter (useful for paginate, Solr parameter name)
     */
    public static final String ROWS_PARAMETER = "rows";

    /**
     * The indent parameter (Solr parameter name)
     */
    public static final String INDENT_PARAMETER = "indent";

    /**
     * The echoparam parameter (if the query parameters should be print in the
     * response, Solr parameter name)
     */
    public static final String ECHOPARAMS_PARAMETER = "echoParams";

    /**
     * The start rows parameter (useful for paginate, Solr parameter name)
     */
    public static final String START_PARAMETER = "start";
    
    /**
     * The parameter name to active the spellchecker (Solr parameter name)
     */
    public static final String SPELLCHECKER_ENABLED_PARAMETER = "spellcheck";
    
    /**
     * The parameter name to Rebuild the spellchecker (Solr parameter name)
     */
    public static final String SPELLCHECKER_BUILD_PARAMETER = "spellcheck.build";
    
    /**
     * The parameter name to Rebuild the spellchecker (Solr parameter name)
     */
    public static final String SPELLCHECKER_RELOAD_PARAMETER = "spellcheck.reload";
    
    /**
     * The parameter name to define the number of suggestion for the spellchecker (Solr parameter name)
     */
    public static final String SPELLCHECKER_NUMBER_OF_SUGGESTION_PARAMETER = "spellcheck.count";
    
    /**
     * The parameter name to define the number of suggestion for the spellchecker (Solr parameter name)
     */
    public static final String SPELLCHECKER_COLLATE_RESULTS_PARAMETER = "spellcheck.collate";
    
    /**
     * The parameter name to define the name of the dictionary to use for the spellchecker (Solr parameter name)
     */
    public static final String SPELLCHECKER_DICTIONARY_NAME_PARAMETER = "spellcheck.dictionary";
    
    /**
     * The parameter name to define the query to use for the spellchecker (Solr parameter name)
     */
    public static final String SPELLCHECKER_QUERY_PARAMETER = "spellcheck.q";
    
    /**
     * The parameter name to define the filter query to use (Solr parameter name)
     */
    public static final String FILTER_QUERY_PARAMETER = "fq";
    
    /**
     * The parameter name to define the spatial fields (Solr parameter name)
     */
    public static final String SPATIAL_FIELD_PARAMETER = "sfield";
    
    /**
     * The parameter name to define the point to search around (Solr parameter name)
     */
    public static final String POINT_PARAMETER = "pt";
    
    /**
     * The parameter name to define the distance to search around (Solr parameter name)
     */
    public static final String DISTANCE_PARAMETER = "d";
    
    
    

    /**
     * A string that is used to load a class from a string with class.forName()
     */
    public static final String ENTITY_PACKAGE = "com.gisgraphy.domain.geoloc.entity.";

    /**
     * All the Query type in the Gisgraphy solr config file
     */
    public enum SolrQueryType {
	standard, numeric, simple, deep, advanced,spellcheck,suggest
    };

    /**
     * The radius of the earth
     */
    public static final double RADIUS_OF_EARTH_IN_METERS = 6378100;

    /**
     * An Array of the applicationContext names
     */
    public final static String[] APPLICATION_CONTEXT_NAMES = new String[] {

    "classpath:/applicationContext.xml",
	    "classpath:/applicationContext-resources.xml",
	    "classpath:/applicationContext-repository.xml",
	    "classpath:/applicationContext-geoloc.xml",
	    "classpath:/applicationContext-dao.xml",
	    "classpath:/applicationContext-service.xml",
	    "classpath:/WEB-INF/applicationContext-struts.xml",
	    "classpath:**/applicationContext*.xml" };
    
    /**
     * application context files to start classpath in a non 
     * web context, (note that : javax.servlet should be added in the classpath)
     * then you can start 
     * 
     *  new ClassPathXmlApplicationContext(Constants.APPLICATION_CONTEXT_NAMES_NON_WEB);
     */
    public final static String[] APPLICATION_CONTEXT_NAMES_NON_WEB= new String[] {

	    "classpath:/applicationContext.xml",
		    "classpath:/applicationContext-resources.xml",
		    "classpath:/applicationContext-repository.xml",
		    "classpath:/applicationContext-geoloc.xml",
		    "classpath:/applicationContext-dao.xml",
		    "classpath:/applicationContext-service.xml",
		    "classpath:**/applicationContext*.xml" };

    /**
     * An Array of the ApplicationContext names for tests
     */
    public final static String[] APPLICATION_CONTEXT_NAMES_FOR_TEST = new String[] {

    "classpath:/applicationContext.xml",
	    "classpath:/applicationContext-resources.xml",
	    "classpath:/applicationContext-repository.xml",
	    "classpath:/applicationContext-geoloc.xml",
	    "classpath:/applicationContext-dao.xml",
	    "classpath:/applicationContext-service.xml",
	    "classpath:/WEB-INF/applicationContext-struts.xml",
	    "classpath:**/applicationContext*.xml",
	    "classpath:/applicationContext-test.xml",
	    "classpath:/applicationContext-dao-test.xml" };

   
   
    

    /**
     * The node name for {@link StreetDistance} node in JAXB
     */
    public final static String STREETDISTANCE_JAXB_NAME = "result";

    

    /**
     * The node name for {@link StreetSearchResultsDto} node in JAXB
     */
    public final static String STREETSEARCHRESULTSDTO_JAXB_NAME = "results";

    /**
     * The name of the ResourceBundle used in this application
     */
    public static final String BUNDLE_KEY = "ApplicationResources";

    /**
     * The name of the ResourceBundle used in this application for errors
     */
    public static final String BUNDLE_ERROR_KEY = "ApplicationErrorResources";

    /**
     * The name of the ResourceBundle for Feature class / code used in this
     * application
     */
    public static final String FEATURECODE_BUNDLE_KEY = "featurecodes";
    
    /**
     * The name of the ResourceBundle for environement properties that hold 
     * gisgraphy configuration
     */
    public static final String ENVIRONEMENT_BUNDLE_KEY = "env";
    
    /**
     * The default title for the RSS/ATOM
     */
    public static final String FEED_TITLE = "Gisgraphy";
    
    /**
     * The default Link for the RSS/ATOM
     */
    public static final String FEED_LINK = "http://www.gisgraphy.com";
    
    /**
     * The default Description for the RSS/ATOM
     */
    public static final String FEED_DESCRIPTION = "Gisgraphy search";
    
    /**
     * The Base URL for building GISFeature URL
     */
    public static final String GISFEATURE_BASE_URL = "http://services.gisgraphy.com/displayfeature.html?featureId=";
    
    public static final String STREET_BASE_URL = "http://services.gisgraphy.com/displaystreet.html?gid=";

   

  
    
    

}
