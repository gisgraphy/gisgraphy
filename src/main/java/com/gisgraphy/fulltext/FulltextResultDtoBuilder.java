package com.gisgraphy.fulltext;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;

public class FulltextResultDtoBuilder {

	/**
	 * @param response
	 *            The {@link QueryResponse} to build the DTO
	 */
	public FulltextResultsDto build(QueryResponse response) {
		FulltextResultsDto fulltextResultDto = new FulltextResultsDto();
		fulltextResultDto.results = SolrUnmarshaller.unmarshall(response);
		fulltextResultDto.QTime = response.getQTime();
		fulltextResultDto.numFound = response.getResults().getNumFound();
		fulltextResultDto.maxScore = response.getResults().getMaxScore();
		fulltextResultDto.resultsSize = fulltextResultDto.results == null ? 0 : fulltextResultDto.results.size();
		SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
		if (spellCheckResponse != null) {
			Map<String, Suggestion> suggestionMapInternal = spellCheckResponse.getSuggestionMap();
			if (suggestionMapInternal != null) {
				for (String key: suggestionMapInternal.keySet()){
					Suggestion suggestion = suggestionMapInternal.get(key);
					fulltextResultDto.suggestionMap.put(key, suggestion.getAlternatives());
					
				}
			}
			if (spellCheckResponse.getCollatedResult() != null) {
				fulltextResultDto.collatedResult = spellCheckResponse.getCollatedResult().trim();
			}
			List<Suggestion> suggestions = spellCheckResponse.getSuggestions();
			if (suggestions.size() != 0) {
				StringBuffer sb = new StringBuffer();
				for (Suggestion suggestion : suggestions) {
					sb.append(suggestion.getSuggestions().get(0)).append(" ");
				}
				fulltextResultDto.spellCheckProposal = sb.toString().trim();
			}
		}
		return fulltextResultDto;

	}

}
