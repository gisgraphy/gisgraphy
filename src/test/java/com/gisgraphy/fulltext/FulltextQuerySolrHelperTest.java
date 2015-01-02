/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.fulltext;

import static com.gisgraphy.domain.valueobject.Pagination.paginate;
import static com.gisgraphy.fulltext.FulltextQuerySolrHelper.BF_NEAREST;
import static com.gisgraphy.fulltext.FulltextQuerySolrHelper.NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE;
import static com.gisgraphy.fulltext.FulltextQuerySolrHelper.NESTED_QUERY_TEMPLATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;

import net.sf.jstester.util.Assert;

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.spell.SpellCheckerConfig;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.test.GisgraphyTestHelper;

public class FulltextQuerySolrHelperTest {
	private OutputStyleHelper outputStyleHelper = new OutputStyleHelper();

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForSuggestQuery() {
		Country france = GisgraphyTestHelper.createCountryForFrance();
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.XML)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, null, null).withAllWordsRequired(true)
				.withSuggest(true).withSpellChecking();
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertNotNull(
				"field list parameter are by default, we use the one in the suggest request handler",
				parameters.get(Constants.FL_PARAMETER));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.suggest.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.equals(searchTerm));
		assertNull("spellchecker query should not be set",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}
	
	@Test
	public void testToQueryStringShouldreturnCorrectParamsForSuggestQuery_smartDetection() {
		Country france = GisgraphyTestHelper.createCountryForFrance();
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.XML)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Rue Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, null, null).withAllWordsRequired(true)
				.withSuggest(true).withSpellChecking();
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertNotNull(
				"field list parameter are by default, we use the one in the suggest request handler",
				parameters.get(Constants.FL_PARAMETER));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.suggest.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.equals(searchTerm));
		assertTrue(
				"wrong FilterQuery with smart street detection",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0)
						.equals("placetype:Street"));
		assertNull("spellchecker query should not be set",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForBasicQuery_spellcheckingEnabled() {
		Country france = GisgraphyTestHelper.createCountryForFrance();
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, null, null).withAllWordsRequired(true);
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found, we want to boost score even if it is a simple query",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.contains("dismax"));
		assertEquals("spellchecker query should be set", searchTerm, parameters
				.get(Constants.SPELLCHECKER_QUERY_PARAMETER).get(0));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForBasicQuery_spellcheckingDisabled() {
		Country france = GisgraphyTestHelper.createCountryForFrance();
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, null, null).withAllWordsRequired(true)
				.withoutSpellChecking();
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found, we want to boost score even if it is a simple query",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.contains("dismax"));
		assertNull("spellchecker query should be set",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForBasicNumericQuery() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "1001";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, null, null).withAllWordsRequired(true);
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong query parameter", String.format(
				FulltextQuerySolrHelper.NESTED_QUERY_NUMERIC_TEMPLATE, searchTerm),
				parameters.get(Constants.QUERY_PARAMETER).get(0));
		
		
		assertNull(
				"spellchecker query should not be set when basic id numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}
	
	@Test
	public void testToQueryStringShouldreturnCorrectParamsForFeatureIdNumericQuery() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerms = "feature_id:1001";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerms,
				pagination, output,
				com.gisgraphy.fulltext.Constants.ONLY_CITY_PLACETYPE, "FR");
		// split parameters
		String queryString = FulltextQuerySolrHelper
				.toQueryString(fulltextQuery);
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(queryString, "&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong query parameter", String.format(
				FulltextQuerySolrHelper.NESTED_QUERY_ID_TEMPLATE, "1001"),
				parameters.get(Constants.QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for placetype",
				"placetype:(City)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));

		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertNull(
				"spellchecker query should not be set when advanced numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForAdvancedNumericQuery() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerms = "1001";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerms,
				pagination, output,
				com.gisgraphy.fulltext.Constants.ONLY_CITY_PLACETYPE, "FR");
		// split parameters
		String queryString = FulltextQuerySolrHelper
				.toQueryString(fulltextQuery);
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(queryString, "&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong query parameter", String.format(
				FulltextQuerySolrHelper.NESTED_QUERY_NUMERIC_TEMPLATE, searchTerms),
				parameters.get(Constants.QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for placetype",
				"placetype:(City)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));

		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertNull(
				"spellchecker query should not be set when advanced numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsWhenAllRequiredIsFalse() {
		Pagination pagination = paginate().from(1).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		FulltextQuery fulltextQuery = new FulltextQuery("foo", pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_CITY_PLACETYPE,
				"FR").withAllWordsRequired(false);
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "0",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "10",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for placetype",
				"placetype:(City)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));

		assertEquals("wrong nested parameter found", String.format(
				NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE, "", "","", "foo"),
				parameters.get(Constants.QUERY_PARAMETER).get(0));

		assertNotNull("spellchecker query should be set ",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForAdvancedNonNumeric_allwordNotRequired() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr").withAllWordsRequired(false);
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong nested parameter found", String.format(
				NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE, "", "","",
				searchTerm), parameters.get(Constants.QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertNotNull("spellchecker query should be set when numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForAdvancedNonNumeric_allwordsRequired() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr").withAllWordsRequired(true);
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong nested parameter found ",
				String.format(NESTED_QUERY_TEMPLATE, "", "", "",searchTerm),
				parameters.get(Constants.QUERY_PARAMETER).get(0));

		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertNotNull("spellchecker query should be set when numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForGeoRSS() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.GEORSS)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr");
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals("wrong field list",
				outputStyleHelper.getFulltextFieldList(
						Output.OutputStyle.MEDIUM, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.GEORSS.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong stylesheet", Constants.GEORSS_STYLESHEET,
				parameters.get(Constants.STYLESHEET_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong query parameter found ", String.format(
				NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE, "", "","",
				searchTerm), parameters.get(Constants.QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForAtom() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.ATOM)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr");
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals("wrong field list",
				outputStyleHelper.getFulltextFieldList(
						Output.OutputStyle.MEDIUM, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.GEORSS.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong stylesheet", Constants.ATOM_STYLESHEET, parameters
				.get(Constants.STYLESHEET_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("wrong query parameter found ", String.format(
				NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE, "", "","",
				searchTerm), parameters.get(Constants.QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForSpellChecking() {
		boolean savedSpellCheckingActiveByDefaultValue = SpellCheckerConfig.activeByDefault;
		boolean savedSpellCheckerConfigEnabled = SpellCheckerConfig.enabled;
		try {
			SpellCheckerConfig.activeByDefault = true;
			SpellCheckerConfig.enabled = false;
			Pagination pagination = paginate().from(3).to(10);
			Output output = Output.withFormat(OutputFormat.ATOM)
					.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
					.withIndentation();
			FulltextQuery fulltextQuery = new FulltextQuery("Saint-André",
					pagination, output,
					com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE, "fr")
					.withSpellChecking();
			// split parameters
			HashMap<String, List<String>> parameters = GisgraphyTestHelper
					.splitURLParams(FulltextQuerySolrHelper
							.toQueryString(fulltextQuery), "&");
			// check parameters
			assertTrue(
					"the fulltextquery should have spellchecking enabled even if spellchecker is disabled",
					fulltextQuery.hasSpellChecking());
			assertTrue(
					"spellchecker should not be listed if spellchecker is disabled",
					!parameters
							.containsKey(Constants.SPELLCHECKER_ENABLED_PARAMETER));
			// active spellchecker and re test
			SpellCheckerConfig.enabled = true;
			fulltextQuery = new FulltextQuery("Saint-André", pagination,
					output,
					com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE, "fr")
					.withSpellChecking();
			parameters = GisgraphyTestHelper.splitURLParams(
					FulltextQuerySolrHelper.toQueryString(fulltextQuery), "&");
			assertTrue(
					"the fulltextquery should have spellchecking enabled when spellchecker is enabled",
					fulltextQuery.hasSpellChecking());
			assertEquals("spellchecker should be enabled", "true", parameters
					.get(Constants.SPELLCHECKER_ENABLED_PARAMETER).get(0));
			assertEquals(
					"spellchecker should be enabled",
					String.valueOf(SpellCheckerConfig.collateResults),
					parameters.get(
							Constants.SPELLCHECKER_COLLATE_RESULTS_PARAMETER)
							.get(0));
			assertEquals(
					"spellchecker should be enabled",
					String.valueOf(SpellCheckerConfig.numberOfSuggestion),
					parameters
							.get(Constants.SPELLCHECKER_NUMBER_OF_SUGGESTION_PARAMETER)
							.get(0));
			assertEquals(
					"spellchecker should be enabled",
					SpellCheckerConfig.spellcheckerDictionaryName.toString(),
					parameters.get(
							Constants.SPELLCHECKER_DICTIONARY_NAME_PARAMETER)
							.get(0));
		} catch (RuntimeException e) {
			fail(e.getMessage());
		} finally {
			SpellCheckerConfig.activeByDefault = savedSpellCheckingActiveByDefaultValue;
			SpellCheckerConfig.enabled = savedSpellCheckerConfigEnabled;
		}
	}

	@Test
	public void testIsStreetQuery() {
		FulltextQuery query = new FulltextQuery("foo");
		query.withPlaceTypes(new Class[] { Street.class });
		Assert.assertTrue(FulltextQuerySolrHelper.isStreetQuery(query));
		query.withPlaceTypes(null);
		Assert.assertFalse(FulltextQuerySolrHelper.isStreetQuery(query));
		query.withPlaceTypes(new Class[] { Street.class, City.class });
		Assert.assertTrue(FulltextQuerySolrHelper.isStreetQuery(query));
		query.withPlaceTypes(new Class[] { Street.class, null });
		Assert.assertTrue(FulltextQuerySolrHelper.isStreetQuery(query));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForAdvancedGeolocQuery_allwordRequired() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr").withAllWordsRequired(true);
		Float longitude = 20F;
		Float latitude = 30F;
		fulltextQuery.around(GeolocHelper.createPoint(longitude, latitude));
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found (no search term part) actual : "
						+ parameters.get(Constants.QUERY_PARAMETER).get(0),
				parameters
						.get(Constants.QUERY_PARAMETER)
						.get(0)
						.contains(
								String.format(NESTED_QUERY_TEMPLATE, "", "",BF_NEAREST,
										searchTerm)));

		assertEquals("wrong query parameter found ",
				String.format(NESTED_QUERY_TEMPLATE, "", "", BF_NEAREST,searchTerm),
				parameters.get(Constants.QUERY_PARAMETER).get(0));

		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(2));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));

		assertEquals("wrong filter query parameter for geoloc",
				"{!bbox sfield=location}",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, point",
				"30.0,20.0", parameters.get(Constants.POINT_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, distance",
				"10.0", parameters.get(Constants.DISTANCE_PARAMETER).get(0));

		// (String.format(Locale.US,FulltextQuerySolrHelper.GEOLOC_QUERY_TEMPLATE,
		// fulltextQuery.getPoint().getY(),fulltextQuery.getPoint().getX(),fulltextQuery.getRadius()/1000)));

		assertNotNull("spellchecker query should be set when numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldreturnCorrectParamsForAdvancedGeolocQuery_allwordNotRequired() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr").withAllWordsRequired(false);
		Float longitude = 20F;
		Float latitude = 30F;
		fulltextQuery.around(GeolocHelper.createPoint(longitude, latitude));
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found (no search term part) actual : "
						+ parameters.get(Constants.QUERY_PARAMETER).get(0),
				parameters
						.get(Constants.QUERY_PARAMETER)
						.get(0)
						.contains(
								String.format(
										NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE,
										"", "",BF_NEAREST, searchTerm)));

		assertEquals("wrong query parameter found, if location is given, the nearest will be boost", String.format(
				NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE, "", "",BF_NEAREST,
				searchTerm), parameters.get(Constants.QUERY_PARAMETER).get(0));

		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(2));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));

		assertEquals("wrong filter query parameter for geoloc",
				"{!bbox sfield=location}",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, point",
				"30.0,20.0", parameters.get(Constants.POINT_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, distance",
				"10.0", parameters.get(Constants.DISTANCE_PARAMETER).get(0));
		assertNotNull("spellchecker query should be set when numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	
	@Test
	public void testToQueryStringShouldBoostNearestPlaceForAdvancedGeolocQuery_allwordRequired() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr").withAllWordsRequired(true).withRadius(1000);
		Float longitude = 20F;
		Float latitude = 30F;
		fulltextQuery.around(GeolocHelper.createPoint(longitude, latitude));
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found (no search term part) actual : "
						+ parameters.get(Constants.QUERY_PARAMETER).get(0),
				parameters
						.get(Constants.QUERY_PARAMETER)
						.get(0)
						.contains(
								String.format(NESTED_QUERY_TEMPLATE, "", "",BF_NEAREST,
										searchTerm)));

		assertEquals("wrong query parameter found ",
				String.format(NESTED_QUERY_TEMPLATE, "", "", BF_NEAREST,searchTerm),
				parameters.get(Constants.QUERY_PARAMETER).get(0));

		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(2));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));

		assertEquals("wrong filter query parameter for geoloc",
				"{!bbox sfield=location}",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, point",
				"30.0,20.0", parameters.get(Constants.POINT_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, distance","1.0",
				parameters.get(Constants.DISTANCE_PARAMETER).get(0));

		// (String.format(Locale.US,FulltextQuerySolrHelper.GEOLOC_QUERY_TEMPLATE,
		// fulltextQuery.getPoint().getY(),fulltextQuery.getPoint().getX(),fulltextQuery.getRadius()/1000)));

		assertNotNull("spellchecker query should be set when numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	@Test
	public void testToQueryStringShouldboostNearestPlaceForAdvancedGeolocQuery_allwordNotRequired() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.ONLY_ADM_PLACETYPE,
				"fr").withAllWordsRequired(false).withRadius(0);
		Float longitude = 20F;
		Float latitude = 30F;
		fulltextQuery.around(GeolocHelper.createPoint(longitude, latitude));
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));
		assertEquals("if a location is porvided, sfield should be filled",
				GisFeature.LOCATION_COLUMN_NAME,
				parameters.get(Constants.SPATIAL_FIELD_PARAMETER).get(0));
		assertTrue(
				"wrong query parameter found (no search term part) actual : "
						+ parameters.get(Constants.QUERY_PARAMETER).get(0),
				parameters
						.get(Constants.QUERY_PARAMETER)
						.get(0)
						.contains(
								String.format(
										NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE,
										"", "",BF_NEAREST, searchTerm)));

		assertEquals("wrong query parameter found, if location is given, the nearest will be boost", String.format(
				NESTED_QUERY_NOT_ALL_WORDS_REQUIRED_TEMPLATE, "", "",BF_NEAREST,
				searchTerm), parameters.get(Constants.QUERY_PARAMETER).get(0));

		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Adm)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(2));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));

		assertEquals("wrong filter query parameter for geoloc",
				"{!bbox sfield=location}",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, point",
				"30.0,20.0", parameters.get(Constants.POINT_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, distance should not be set to max if radius = 0",
				FulltextQuerySolrHelper.MAX_RADIUS+"",
				parameters.get(Constants.DISTANCE_PARAMETER).get(0));
		assertNotNull("spellchecker query should be set when numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

	
	
	@Test
	public void testToQueryStringShouldreturnCorrectParamsForStreetQuery() {
		Pagination pagination = paginate().from(3).to(10);
		Output output = Output.withFormat(OutputFormat.JSON)
				.withLanguageCode("FR").withStyle(OutputStyle.SHORT)
				.withIndentation();
		String searchTerm = "Saint-André";
		FulltextQuery fulltextQuery = new FulltextQuery(searchTerm, pagination,
				output, com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, "fr");
		Float longitude = 20F;
		Float latitude = 30F;
		fulltextQuery.around(GeolocHelper.createPoint(longitude, latitude));
		// split parameters
		HashMap<String, List<String>> parameters = GisgraphyTestHelper
				.splitURLParams(
						FulltextQuerySolrHelper.toQueryString(fulltextQuery),
						"&");
		// check parameters
		assertEquals(outputStyleHelper.getFulltextFieldList(
				Output.OutputStyle.SHORT, "FR"),
				parameters.get(Constants.FL_PARAMETER).get(0));
		assertEquals("wrong indent parameter found", "on",
				parameters.get(Constants.INDENT_PARAMETER).get(0));
		assertEquals("wrong echoparams parameter found", "none", parameters
				.get(Constants.ECHOPARAMS_PARAMETER).get(0));
		assertEquals("wrong start parameter found", "2",
				parameters.get(Constants.START_PARAMETER).get(0));
		assertEquals("wrong rows parameter found", "8",
				parameters.get(Constants.ROWS_PARAMETER).get(0));
		assertEquals("wrong output format parameter found",
				OutputFormat.JSON.getParameterValue(),
				parameters.get(Constants.OUTPUT_FORMAT_PARAMETER).get(0));
		assertEquals("wrong query type parameter found",
				Constants.SolrQueryType.advanced.toString(),
				parameters.get(Constants.QT_PARAMETER).get(0));

		assertTrue(
				FullTextFields.IS_IN.getValue() + " field should be added : ",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.contains(FullTextFields.IS_IN.getValue()));

		assertTrue(
				FullTextFields.IS_IN_ADM.getValue()
						+ " field should be added : ",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.contains(FullTextFields.IS_IN_ADM.getValue()));

		assertTrue(
				FullTextFields.IS_IN_PLACE.getValue()
						+ " field should be added : ",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.contains(FullTextFields.IS_IN_PLACE.getValue()));

		assertTrue(
				FullTextFields.IS_IN_ZIP.getValue()
						+ " field should be added : ",
				parameters.get(Constants.QUERY_PARAMETER).get(0)
						.contains(FullTextFields.IS_IN_ZIP.getValue()));

		assertEquals("wrong filter query parameter for placetype",
				"placetype:(Street)",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(2));
		assertEquals("wrong filter query parameter for countrycode",
				"country_code:FR",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(1));

		assertEquals("wrong filter query parameter for geoloc",
				"{!bbox sfield=location}",
				parameters.get(Constants.FILTER_QUERY_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, point",
				"30.0,20.0", parameters.get(Constants.POINT_PARAMETER).get(0));
		assertEquals("wrong filter query parameter for geoloc, distance",
				"10.0", parameters.get(Constants.DISTANCE_PARAMETER).get(0));

		assertNotNull("spellchecker query should be set when numeric query",
				parameters.get(Constants.SPELLCHECKER_QUERY_PARAMETER));
	}

}
