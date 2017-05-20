package com.gisgraphy.helper.synonyms;

public class SynonymsDto {
	
	private String word1;
	private String word2;
	private String lang;
	
	public SynonymsDto(String word1, String word2,String lang) {
		super();
		if (word1!=null){
			this.word1 = word1.trim().toLowerCase();
		}
		if (word2!=null){
			this.word2 = word2.trim().toLowerCase();
			
		}
		if (lang != null){
			this.lang=lang.trim().toLowerCase();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SynonymsDto ["+word1+";"+word2+";"+lang;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word1 == null) ? 0 : word1.hashCode());
		result = prime * result + ((word2 == null) ? 0 : word2.hashCode());
		return result;
	}

	/**
	 * @return the word1
	 */
	public String getWord1() {
		return word1;
	}

	/**
	 * @param word1 the word1 to set
	 */
	public void setWord1(String word1) {
		this.word1 = word1;
	}

	/**
	 * @return the word2
	 */
	public String getWord2() {
		return word2;
	}

	/**
	 * @param word2 the word2 to set
	 */
	public void setWord2(String word2) {
		this.word2 = word2;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SynonymsDto other = (SynonymsDto) obj;
		if (word1 == null) {
			if (other.word1 != null)
				return false;
		} else if (!word1.equals(other.word1) && !word1.equals(other.word2))
			return false;
		if (word2 == null) {
			if (other.word2 != null)
				return false;
		} else if (!word2.equals(other.word2) && !word2.equals(other.word1))
			return false;
		return true;
	}

	
	
	

}
