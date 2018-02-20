package com.gisgraphy.fulltext.suggest;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author David Masclet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GisgraphySearchResponse {


	    private Integer numFound = 0;
	    private List<GisgraphySearchEntry> docs = new ArrayList<GisgraphySearchEntry>();
	    private float maxScore = 0f;
		private int start=0;

		public Integer getNumFound() {
			return numFound;
		}

		public void setNumFound(Integer numFound) {
			this.numFound = numFound;
		}

		public List<GisgraphySearchEntry> getDocs() {
			return docs;
		}

		public void setDocs(List<GisgraphySearchEntry> docs) {
			this.docs = docs;
		}

		public float getMaxScore() {
			return maxScore;
		}

		public void setMaxScore(float maxScore) {
			this.maxScore = maxScore;
		}
		
		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}
}
