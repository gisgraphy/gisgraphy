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
package com.gisgraphy.fulltext;

import static com.gisgraphy.helper.StringHelper.isEmptyString;
import static com.gisgraphy.helper.StringHelper.isNotEmptyString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.compound.Decompounder;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.fulltext.spell.SpellCheckerConfig;
import com.gisgraphy.geocoding.GeocodingService;
import com.gisgraphy.serializer.common.OutputFormat;

/**
 * 
 * usefullmethod to process fulltext query by solr
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class FulltextQuerySolrHelper {
	


	protected static String ALL_ADM1_NAME_ALL_ADM2_NAME = " all_adm1_name^0.2 all_adm2_name^0.2 ";

	public static final Float MIN_SCORE = 10F;
	
	protected static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);
	
	public static final int NUMBER_OF_STREET_TO_RETRIEVE = 40;
	public static final String FUZZY_FACTOR = "0.7";
	public static final String BOOST_EXACT_WORD_FACTOR = "";
	
	
	public static final String FEATUREID_PREFIX = FullTextFields.FEATUREID.getValue()+":";
	
	public static final String OPENSTREETMAPID_PREFIX = FullTextFields.OPENSTREETMAP_ID.getValue()+":";
	
	public static final int MAX_RADIUS_IN_METER = 100000;

	private static SmartStreetDetection smartStreetDetection = new SmartStreetDetection();

	private static OutputStyleHelper outputStyleHelper = new OutputStyleHelper();

	
	public static String MM_NOT_ALL_WORD_REQUIRED ="2<2";//"3<-1 4<3";
	public static String MM_ALL_WORD_REQUIRED ="100%%";
	
	//_query_:"{!edismax qf='label^18 name^25 all_name^10 fully_qualified_name %s' pf='all_label' ps=0 tie='0.1' bq=' %s'   mm='%s'  bf='%s'}%s"=>better for rue de lille bailleul=>label do the job
	protected static  String NESTED_QUERY_TEMPLATE = "_query_:\"{!edismax qf='label^15 name^25 all_name^10 fully_qualified_name %s' pf='all_label' ps=0 tie='0.1' bq=' %s'   mm='%s'  bf='%s'}%s\"";
	protected static  String NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE = NESTED_QUERY_TEMPLATE;
	
	//good result :"_query_:\"{!edismax qf='suggest_in^0.5 suggest_name^0.55 suggest_fqdn_name^5 suggest_all_label^5 zipcode^0.2 name^0.01' bq='population^1.5 placetype:city^1.2'   mm='1<100%% 2<-2 5<-3' bf='%s' }%s\""
	//initial : "_query_:\"{!edismax qf='suggest_in^0.5 suggest_name^0.55 zipcode^0.2 name^0.01' bq='population^1.5 placetype:city^1.2'   mm='1<100%% 2<-2 5<-3' bf='%s' }%s\"";
	//short : "_query_:\"{!edismax qf='suggest_name^0.55 suggest_fqdn_name^0.7' bq='population^1.5 placetype:city^1.2'   mm='1<100%% 2<-2 5<-3' bf='%s' }%s\"";
	protected static  String SUGGEST_QUERY_TEMPLATE = "_query_:\"{!edismax qf='suggest_name^0.55 suggest_fqdn_name^0.7' bq='population^1.5 placetype:city^1.2'   mm='1<100%% 2<-2 5<-3' bf='%s' }%s\"";
	protected static final String SUGGEST_FQ = "placetype:city placetype:adm placetype:street";
	protected static final String SUGGEST_FL = "name,zipcode,country_code,adm1_name,is_in,feature_id,lat,lng,score,house_numbers";
	
	protected static String CITY_ADM_BOOST_QUERY="placetype:city^800 placetype:adm^600";
	protected static String CITY_BOOST_QUERY="placetype:city^200";
	protected static String STREET_BOOST_QUERY="placetype:street^150";
	// we need to consider adm1name for andora and brooklin
	protected static final String NESTED_QUERY_NUMERIC_TEMPLATE =          "_query_:\"{!edismax qf='zipcode^1.2 pf=name^1.1'  bq='placetype:City^2 population^2' bf='pow(map(population,0,0,0.0001),0.3)     pow(map(city_population,0,0,0.0000001),0.3)' }%s\"";
	
	protected static final String NESTED_QUERY_ID_TEMPLATE =          "_query_:\"{!edismax qf='feature_id^1.1 '}%s\"";//openstreetmap_id^1.1
	
	protected static final String NESTED_QUERY_OPENSTREETMAP_ID_TEMPLATE =          "_query_:\"{!edismax qf='openstreetmap_id^1.1 '}%s\"";//openstreetmap_id^1.1
    
	protected static final String FQ_COUNTRYCODE = FullTextFields.COUNTRYCODE.getValue()+":%s";
	protected static final String FQ_PLACETYPE = FullTextFields.PLACETYPE.getValue()+":";
	protected static final String FQ_LOCATION = "{!bbox "+Constants.SPATIAL_FIELD_PARAMETER+"="+GisFeature.LOCATION_COLUMN_NAME+"}";

	//http://rechneronline.de/function-graphs/
	protected static String BF_NEAREST = "recip(geodist(),0.01,3000,1)";//first number impact  the nearest (the more, the nearest got importance), two other the farest. 2/3 =>the highest score
	protected static String BF_POPULATION="pow(map(population,0,0,0.009),0.7) pow(map(city_population,0,0,0.0001),0.20)";
	//http://wiki.apache.org/solr/FunctionQuery#recip
	
	//{!geofilt sfield=store}&pt=45.15,-93.85&d=5
	
	protected static final String GEOLOC_QUERY_TEMPLATE = "_query_:\"{!bbox "
			+ Constants.SPATIAL_FIELD_PARAMETER + "="
			+ GisFeature.LOCATION_COLUMN_NAME + " " + Constants.POINT_PARAMETER
			+ "=%f,%f " + Constants.DISTANCE_PARAMETER + "=%f}\"";

	
	private static Decompounder decompounder = new Decompounder();
	
	/**
	 * @return A Representation of all the needed parameters
	 */
	public static ModifiableSolrParams parameterize(FulltextQuery query) {
		
		//getConfigInFile();
		//logger.error("NESTED_QUERY_TEMPLATE : "+NESTED_QUERY_TEMPLATE);
		//logger.error("not all words : "+NESTED_QUERY_TEMPLATE);
		boolean spellchecker = true;
		ModifiableSolrParams parameters = new ModifiableSolrParams();


		parameters.set(Constants.INDENT_PARAMETER, query.isOutputIndented() ? "on"
				: "off");
		parameters.set(Constants.ECHOPARAMS_PARAMETER, "none");
		
		//pagination
		parameters.set(Constants.START_PARAMETER, String
				.valueOf(query.getFirstPaginationIndex() - 1));// sub 1 because solr start at 0
		parameters.set(Constants.ROWS_PARAMETER, String.valueOf(query.getPagination()
				.getMaxNumberOfResults()));
		
		//xslt?
		if (query.getOutputFormat() == OutputFormat.ATOM) {
			parameters.set(Constants.STYLESHEET_PARAMETER,
					Constants.ATOM_STYLESHEET);
		} else if (query.getOutputFormat() == OutputFormat.GEORSS) {
			parameters.set(Constants.STYLESHEET_PARAMETER,
					Constants.GEORSS_STYLESHEET);
		}

		//set outputformat
		if (query.isSuggest()){
			parameters.set(Constants.OUTPUT_FORMAT_PARAMETER, OutputFormat.JSON
					.getParameterValue());
		} else {
			parameters.set(Constants.OUTPUT_FORMAT_PARAMETER, query.getOutputFormat()
					.getParameterValue());
		}

		//set field list
		if (query.isSuggest()){
			 parameters.set(Constants.FL_PARAMETER,SUGGEST_FL);
		} else
			if (query.getOutputFormat() == OutputFormat.ATOM
				|| query.getOutputFormat() == OutputFormat.GEORSS) {
			// force Medium style if ATOM or Geo RSS
			parameters.set(Constants.FL_PARAMETER,outputStyleHelper.getFulltextFieldList(OutputStyle.MEDIUM, query.getOutput().getLanguageCode()));
		} else {
			parameters.set(Constants.FL_PARAMETER, outputStyleHelper.getFulltextFieldList(query.getOutput()));
		}

		//radius / point filter query
		if (query.getPoint() != null) {
			    parameters.set(Constants.SPATIAL_FIELD_PARAMETER, GisFeature.LOCATION_COLUMN_NAME);
				parameters.add(Constants.POINT_PARAMETER,query.getPoint().getY()+","+query.getPoint().getX());
				if(query.getRadius() != 0){
					double radius = query.getRadius();
					if (query.getRadius()> MAX_RADIUS_IN_METER){
						radius=MAX_RADIUS_IN_METER;
					} 
					//we do a bounding box
					parameters.add(Constants.FQ_PARAMETER, FQ_LOCATION);
					parameters.add(Constants.DISTANCE_PARAMETER,radius/1000+"");
					logger.debug("restrict to bbox with radius="+radius/1000);
				} /*else if(query.getRadius() == 0){
					parameters.add(Constants.DISTANCE_PARAMETER,MAX_RADIUS+"");
				}  */
		}
		
		//countrycode fq
		if (query.getCountryCode()!=null && !"".equals(query.getCountryCode().trim())){
			parameters.add(Constants.FQ_PARAMETER, String.format(FQ_COUNTRYCODE,query.getCountryCode().toUpperCase()));
		}
		
		//placetype fq
		setFQPlacetype(query, parameters);
		
		//boost field
		String bfField = "";
		if (query.getPoint() != null ) {//promote nearest if  point is specified
			bfField = BF_NEAREST;
			logger.debug("boost nearest on");
		} else {
			bfField=BF_POPULATION;
		}
		
		String querystr = query.getQuery();
		boolean isNumericQuery = isNumericQuery(querystr);
		StringBuffer querybuffer ;
		
		if (querystr.startsWith(FEATUREID_PREFIX)){
			spellchecker=false;
			String id = querystr.substring(FEATUREID_PREFIX.length());
			String queryString = String.format(NESTED_QUERY_ID_TEMPLATE,id);
			parameters.set(Constants.QUERY_PARAMETER, queryString);
			parameters.set(Constants.QT_PARAMETER, Constants.SolrQueryType.advanced
					.toString());
			/*if (query.getPoint() != null ){
			parameters.set(Constants.BF_PARAMETER, BF_NEAREST);
			}*/
		} else if (querystr.startsWith(OPENSTREETMAPID_PREFIX)){
			spellchecker=false;
			String id = querystr.substring(OPENSTREETMAPID_PREFIX.length());
			String queryString = String.format(NESTED_QUERY_OPENSTREETMAP_ID_TEMPLATE,id);
			parameters.set(Constants.QUERY_PARAMETER, queryString);
			parameters.set(Constants.QT_PARAMETER, Constants.SolrQueryType.advanced
					.toString());
		} else if (query.isSuggest()){
			
			parameters.set(Constants.QT_PARAMETER, Constants.SolrQueryType.advanced
					.toString());
			if (query.getPoint() != null ) {//promote nearest if  point is specified
				bfField = BF_NEAREST;
				logger.debug("boost nearest on");
			} else {
				bfField=""; //population is already boost by bq
			}
			
		 if (
				 (query.getCountryCode()!=null && !"".equals(query.getCountryCode().trim()) && Decompounder.isDecompoudCountryCode(query.getCountryCode()))
				 || decompounder.isDecompoundName(querystr)
				 || isStreetQuery(query)
				 ){
			 querystr = decompounder.addOtherFormat(querystr);
		 }
			String querySolr = String.format(SUGGEST_QUERY_TEMPLATE,bfField,querystr);
			parameters.set(Constants.QUERY_PARAMETER, querySolr);
			logger.debug("querysolr="+querySolr);
			/*if(query.getPoint()!=null){
				parameters.set(Constants.BF_PARAMETER, BF_NEAREST);
			}*/
		} else if (isNumericQuery(querystr)) {
			parameters.set(Constants.QT_PARAMETER, Constants.SolrQueryType.advanced
					.toString());
			String queryString = String.format(NESTED_QUERY_NUMERIC_TEMPLATE,querystr);
			parameters.set(Constants.QUERY_PARAMETER, queryString);
		} else {
			// we overide the query type
			/*parameters.set(Constants.QT_PARAMETER,
		    Constants.SolrQueryType.standard.toString());
	    parameters.set(Constants.QUERY_PARAMETER, query.getQuery());*/
			String bqField="";
			List<String> streetTypes = smartStreetDetection.getStreetTypes(querystr);
			if ((!isStreetQuery(query) && streetTypes.size()>=1)){
				bqField=STREET_BOOST_QUERY;
			} else if (query.getPlaceTypes()==null || query.getPlaceTypes().length==0){
					bqField=CITY_ADM_BOOST_QUERY;//we force boost to city because it is not a 'Typed' query
				}
			 else if (isAdministrative(query.getPlaceTypes())){
				bqField=CITY_BOOST_QUERY;//we force nothin  because it is admin query
			} 
			
			
			String queryString;
			if (query.isFuzzy()){
				if (streetTypes!=null && streetTypes.size()==1){
					queryString = buildFuzzyWords(querystr,streetTypes.get(0));
				} else {
					queryString = buildFuzzyWords(querystr,null);
				}
			} else {
				queryString = querystr;
			}
			/*if (query.isExactName()){
				querybuffer = new StringBuffer(String.format(EXACT_NAME_QUERY_TEMPLATE,"",boost,boostNearest,queryString));
			} else {*/
			String mm ;
				if (!query.isAllwordsRequired()){
					mm= MM_NOT_ALL_WORD_REQUIRED;
					//is_in = isStreetQuery(query)?IS_IN_SENTENCE:"";
					//querybuffer = new StringBuffer(String.format(NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE,is_in,boost,MM_NOT_ALL_WORD_REQUIRED,boostNearest,queryString));
				} else {
					//with all word required we don't search in is_in
					//is_in= "";
					mm=MM_ALL_WORD_REQUIRED;

				}
				String is_in="";
				if (isAdministrative(query.getPlaceTypes())){
					is_in=ALL_ADM1_NAME_ALL_ADM2_NAME;
				}
				querybuffer = new StringBuffer(String.format(NESTED_QUERY_TEMPLATE,is_in,bqField,mm,bfField,queryString));
			//}
			parameters.set(Constants.QT_PARAMETER, Constants.SolrQueryType.advanced
					.toString());
			String querySolr = querybuffer.toString();
			logger.debug("querysolr="+querySolr);
			String queryAsStr = querySolr;
			
			parameters.set(Constants.QUERY_PARAMETER, queryAsStr);
		}




		if (SpellCheckerConfig.enabled && query.hasSpellChecking() && !isNumericQuery && !query.isSuggest() && spellchecker){
			parameters.set(Constants.SPELLCHECKER_ENABLED_PARAMETER,"true");
			parameters.set(Constants.SPELLCHECKER_QUERY_PARAMETER, querystr);
			parameters.set(Constants.SPELLCHECKER_COLLATE_RESULTS_PARAMETER,SpellCheckerConfig.collateResults);
			parameters.set(Constants.SPELLCHECKER_NUMBER_OF_SUGGESTION_PARAMETER,SpellCheckerConfig.numberOfSuggestion);
			parameters.set(Constants.SPELLCHECKER_DICTIONARY_NAME_PARAMETER,SpellCheckerConfig.spellcheckerDictionaryName.toString());
		}

		return parameters;
	}

	public static void setFQPlacetype(FulltextQuery query,
			ModifiableSolrParams parameters) {
		if (query.isSuggest() && (query.getPlaceTypes() == null || query.getPlaceTypes().length==0)){
			logger.debug("fq placetype default = "+SUGGEST_FQ);
			parameters.add(Constants.FQ_PARAMETER, SUGGEST_FQ);
		}
		else if (query.getPlaceTypes() != null && containsOtherThingsThanNull(query.getPlaceTypes())) {
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			boolean firstAppend=false;
			for (int i=0;i< query.getPlaceTypes().length;i++){
				if (query.getPlaceTypes()[i] != null){
					if (firstAppend){
						sb.append(" OR ");
					}
					sb.append(query.getPlaceTypes()[i].getSimpleName());
					firstAppend=true;
				}
			}
			sb.append(")");
			parameters.add(Constants.FQ_PARAMETER, FQ_PLACETYPE+sb.toString());
			logger.debug("fq placetype = "+FQ_PLACETYPE+sb.toString());
		}
	}
	
	protected static boolean isAdministrative(Class[] array){
		boolean containsAdministrative = false;
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == Adm.class || array[i] == CitySubdivision.class || array[i] == City.class) {
					containsAdministrative = true;
				} else {
					//there is other class that are not Administrative
					return false;
				}
			}
			
		}
		return containsAdministrative;
	}


	private static void getConfigInFile() {
		try {
			//File fileDir = new File("/home/gisgraphy/workspace/gisgraphy/etc/solrtemplates.txt");
			
			File fileDir = new File("/usr/local/gisgraphy/solrtemplates.txt");

			BufferedReader in = new BufferedReader(
			   new InputStreamReader(
	                      new FileInputStream(fileDir), "UTF8"));


			
			NESTED_QUERY_TEMPLATE = in.readLine();
			//NESTED_QUERY_TEMPLATE=in.readLine();
				//ALL_ADM1_NAME_ALL_ADM2_NAME= in.readLine();
				

	                in.close();
		    }
		    catch (UnsupportedEncodingException e)
		    {
				System.out.println(e.getMessage());
		    }
		    catch (IOException e)
		    {
				System.out.println(e.getMessage());
		    }
		    catch (Exception e)
		    {
				System.out.println(e.getMessage());
		    }
		}
		


	private static boolean containsOtherThingsThanNull(Class[] array) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isNumericQuery(String queryString) {
		try {
			Integer.parseInt(queryString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected static boolean isStreetQuery(FulltextQuery query) {
		if (query.getPlaceTypes() != null
				&& containsOtherThingsThanNull(query.getPlaceTypes())) {
			for (int i = 0; i < query.getPlaceTypes().length; i++) {
				if (query.getPlaceTypes()[i] != null
						&& query.getPlaceTypes()[i] == Street.class) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected static boolean isStreetQueryOnly(FulltextQuery query) {
		if (query.getPlaceTypes() != null && query.getPlaceTypes().length == 1 && 
			 query.getPlaceTypes()[0] == Street.class) {
					return true;
			}
		return false;
	}

	/**
	 * @return A query string for the specified parameter (starting with '?')
	 *         the name of the parameters are defined in {@link Constants}
	 */
	public static String toQueryString(FulltextQuery fulltextQuery) {
		return ClientUtils.toQueryString(parameterize(fulltextQuery), false);
	}
	
	/**
	 * @return A query string for the specified parameter (starting with '?')
	 *         the name of the parameters are defined in {@link Constants}
	 */
	public static String toQueryString(Address address, boolean fuzzy) {
		ModifiableSolrParams addressQuery = buildAddressQuery(address, fuzzy);
		if (addressQuery == null){
			return null;
		}
		return ClientUtils.toQueryString(addressQuery, false);
	}
	
	public static ModifiableSolrParams toRawQuery(String q) {
		ModifiableSolrParams parameters = new ModifiableSolrParams();
		parameters.set(Constants.QUERY_PARAMETER, q);
		parameters.set(Constants.QT_PARAMETER, Constants.SolrQueryType.advanced
				.toString());
		parameters.add(Constants.FQ_PARAMETER, FQ_PLACETYPE+"(Street)");
		return parameters;
	}
	
/*	protected static String buildFuzzyQuery(FulltextQuery fulltextQuery){
		String query=fulltextQuery.getQuery();
		StringBuffer sbq= new StringBuffer();
		String fuzzyWords = buildFuzzyWords(query);
		sbq.append(FullTextFields.ALL_NAME.getValue()).append(":").append(fuzzyWords);
		//name
		if (!fulltextQuery.isAllwordsRequired()){
			
		if (isStreetQuery(fulltextQuery)){
			//sbq.append(FullTextFields.IS_IN.getValue()).append(":").append(fuzzyWords);
			sbq.append(FullTextFields.IS_IN_CITIES.getValue()).append(":").append(fuzzyWords);
			//sbq.append(FullTextFields.IS_IN_PLACE.getValue()).append(":").append(fuzzyWords);
			//sbq.append(FullTextFields.IS_IN_ADM.getValue()).append(":").append(fuzzyWords);
			
		}
		if (!isStreetQueryOnly(fulltextQuery)){
			sbq.append(FullTextFields.ALL_ADM1_NAME.getValue()).append(":").append(fuzzyWords);
			sbq.append(FullTextFields.ALL_ADM2_NAME.getValue()).append(":").append(fuzzyWords);
			
		} 
		}
		return sbq.toString();
	}*/
	
	public static String clean(String s){
		if (s!=null){
			s=  s.replaceAll("\\s+"," ").trim();
					s=s.replaceAll("[\\-\\s]$", "");
					s=s.replaceAll("\\s+"," ");
		}
		return s;
	}
	
	
	
	public static ModifiableSolrParams buildAddressQuery(Address address, boolean fuzzy){
		ModifiableSolrParams parameters = new ModifiableSolrParams();
		StringBuffer sbq= new StringBuffer();
		if (address.getCountryCode()!=null && address.getCountryCode().length()==2){
		parameters.add(Constants.FQ_PARAMETER, String.format(FQ_COUNTRYCODE,address.getCountryCode().toUpperCase()));
		}
		//pagination
		parameters.set(Constants.START_PARAMETER, 0);// sub 1 because solr start at 0
		
		parameters.set(Constants.ROWS_PARAMETER, NUMBER_OF_STREET_TO_RETRIEVE);
		
		if (address.getStreetName() != null) {
			String streetSentenceToSearch = address.getStreetName();
			if (address.getStreetType()!=null){
				streetSentenceToSearch = address.getStreetName()+ " "+address.getStreetType();
			}
			if (fuzzy){
				sbq.append(FullTextFields.ALL_NAME.getValue()).append(":").append(buildFuzzyWords(streetSentenceToSearch,null)).append(" ");
				sbq.append(FullTextFields.ALL_NAME.getValue()).append(":").append(buildExactWords(streetSentenceToSearch)).append(" ");
			} else {
				sbq.append(FullTextFields.ALL_NAME.getValue()).append(":(").append(clean(streetSentenceToSearch)).append(") ");
			}
			//Set placetype to street
			parameters.add(Constants.FQ_PARAMETER, FullTextFields.PLACETYPE.getValue()+":"+Street.class.getSimpleName());
		}
		
			//set city
			String city="";
			if (isNotEmptyString(address.getCity())) {
				city += " " + address.getCity();
			} else if (isNotEmptyString(address.getPostTown())){
				city += " " + address.getPostTown();
			}
			if (!"".equals(city)){
				//we got a city
				String field ;
				if (address.getStreetName() != null){
					//it is a city for a streetname
					field = FullTextFields.IS_IN_CITIES.getValue();
				} else {
					//it is only a city
					field = FullTextFields.ALL_NAME.getValue();
					parameters.add(Constants.FQ_PARAMETER, FullTextFields.PLACETYPE.getValue()+":"+City.class.getSimpleName());
					
				}
				if (fuzzy){
					sbq.append(field).append(":").append((clean(city))).append(" ");
					sbq.append(FullTextFields.IS_IN.getValue()).append(":(").append(clean(city)).append(") ");
				} else {
					sbq.append(field).append(":(").append(clean(city)).append(") ");
					sbq.append(FullTextFields.IS_IN.getValue()).append(":(").append(clean(city)).append(") ");
				}
			}
			
			//set state
			String choiceState = "";
			String dependentLocality = address.getDependentLocality();
			String state = address.getState();
			if (isEmptyString(state) && isNotEmptyString(dependentLocality)) {
				choiceState = " " + dependentLocality;
			} else if (isNotEmptyString(state) && isEmptyString(dependentLocality)) {
				choiceState = " " + state;
			} else if (isNotEmptyString(state) && isNotEmptyString(dependentLocality)) {
				choiceState = " " + state + " " + dependentLocality;
			}
			choiceState = clean(choiceState);
			if (!"".equals(choiceState)){
				//we got a state
				if (address.getStreetName()!=null){
					//it is an adm for a street
					if (fuzzy){
						sbq.append(" ").append(FullTextFields.IS_IN_ADM.getValue()).append(":").append(buildFuzzyWords(choiceState,null)).append(" ");
					} else {
						sbq.append(" ").append(FullTextFields.IS_IN_ADM.getValue()).append(":(").append(choiceState).append(") ");
					}
				} 
					if (address.getCity()!=null || address.getZipCode() !=null){
						//adm for a city or zip
						if (fuzzy){
							sbq.append(" ").append(FullTextFields.ALL_ADM1_NAME.getValue()).append(":").append(buildFuzzyWords(choiceState,null)).append(" ");
						} else {
							sbq.append(" ").append(FullTextFields.ALL_ADM1_NAME.getValue()).append(":(").append(choiceState).append(") ");
						}
					} else {
						//we got only a state
						if (fuzzy){
							sbq.append(" ").append(FullTextFields.ALL_NAME.getValue()).append(":").append(buildFuzzyWords(choiceState,null)).append(" ");
						} else {
							sbq.append(" ").append(FullTextFields.ALL_NAME.getValue()).append(":").append(":(").append(choiceState).append(") ");
						}
						parameters.add(Constants.FQ_PARAMETER, FullTextFields.PLACETYPE.getValue()+":"+Adm.class.getSimpleName());
					}
						
			}	
			
			//if (address.getZipCode()!=null){
			//	String field =FullTextFields.ZIPCODE.getValue();
				//if (address.getStreetName()!=null){
					//field =FullTextFields.IS_IN_ZIP.getValue();
				//}
				//if (address.getCity()==null){
					//it has already been added by city step
				//	parameters.add(Constants.FQ_PARAMETER, FullTextFields.PLACETYPE.getValue()+":"+City.class.getSimpleName());
				//}
				//	sbq.append(" ").append(field).append(":").append(clean(address.getZipCode())).append(" ");//no fuzzy in zipcode
			//}
			
			
		parameters.set(Constants.QUERY_PARAMETER,  sbq.toString());
		
		parameters.set(Constants.QT_PARAMETER, Constants.SolrQueryType.advanced
				.toString());
		
		if ("".equals(sbq.toString())){
			return null;
		}
		return parameters;
	}
	
	protected static String buildFuzzyWords(String query,String stopWord){
		if (query ==null){
			return "";
		}
		String[] words = query.split("[,\\s\\-\\–\\一]");//not slash
		StringBuffer sb = new StringBuffer("");
		for (int i = 0;i<words.length ;i++){
			String word = words[i].trim();
			if (words!=null && !"".equals(word)){
				if (stopWord!=null){
					if(word.equalsIgnoreCase(stopWord) || word.length()<=3 || StringUtils.isNumeric(word)){
						sb.append(words[i]+BOOST_EXACT_WORD_FACTOR+" ");
					} else {
						//if there is street type, we put search exact and fuzzy
						sb.append(" ").append(words[i]).append("~"+FUZZY_FACTOR+" ").append(words[i]+BOOST_EXACT_WORD_FACTOR+" ");
					}
				} else {
					//if there is no street type only search in fuzzy
						sb.append(" ").append(words[i]).append("~"+FUZZY_FACTOR+" ");
				}
			}
		}
		sb.append("");
		return sb.toString().trim();
	}
	
	
	protected static String buildExactWords(String query){
		if (query ==null){
			return "";
		}
		String[] words = query.split("[,\\s\\-\\–\\一]");//not slash
		StringBuffer sb = new StringBuffer(" (");
		for (int i = 0;i<words.length ;i++){
			String word = words[i].trim();
			if (words!=null && !"".equals(word) && !StringUtils.isNumericSpace(word) ){
				sb.append(" ").append(words[i]).append("^20 ");
			}
		}
		sb.append(") ");
		return sb.toString();
	}
}
