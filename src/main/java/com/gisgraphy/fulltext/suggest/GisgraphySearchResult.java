package com.gisgraphy.fulltext.suggest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author David Masclet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GisgraphySearchResult {


		private GisgraphySearchResponse response;
	    private GisgraphySearchResponseHeader responseHeader;
		
	    public GisgraphySearchResponse getResponse() {
			return response;
		}

	    public void setResponse(GisgraphySearchResponse response) {
			this.response = response;
		}

	    public GisgraphySearchResponseHeader getResponseHeader() {
			return responseHeader;
		}

	    public void setResponseHeader(GisgraphySearchResponseHeader responseHeader) {
			this.responseHeader = responseHeader;
		}
	    @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((response == null) ? 0 : response.hashCode());
			result = prime
					* result
					+ ((responseHeader == null) ? 0 : responseHeader.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GisgraphySearchResult other = (GisgraphySearchResult) obj;
			if (response == null) {
				if (other.response != null)
					return false;
			} else if (!response.equals(other.response))
				return false;
			if (responseHeader == null) {
				if (other.responseHeader != null)
					return false;
			} else if (!responseHeader.equals(other.responseHeader))
				return false;
			return true;
		}

}
