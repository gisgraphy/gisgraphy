package com.gisgraphy.importer;

/**
 * A simple DTO to handle the isInAdm items for the importer
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class AdmDTO implements Comparable<AdmDTO>{
	
	private String admName;
	private int level;
	private long admOpenstreetMapId;
	
	
	
	public AdmDTO(String admName, int level, long admOpenstreetMapId) {
		super();
		this.admName = admName;
		this.level = level;
		this.admOpenstreetMapId = admOpenstreetMapId;
	}
	/**
	 * @return the admName
	 */
	public String getAdmName() {
		return admName;
	}
	/**
	 * @param admName the admName to set
	 */
	public void setAdmName(String admName) {
		this.admName = admName;
	}
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return the admOpenstreetMapId
	 */
	public long getAdmOpenstreetMapId() {
		return admOpenstreetMapId;
	}
	/**
	 * @param admOpenstreetMapId the admOpenstreetMapId to set
	 */
	public void setAdmOpenstreetMapId(long admOpenstreetMapId) {
		this.admOpenstreetMapId = admOpenstreetMapId;
	}
	
	public int compareTo(AdmDTO o) {
		int a = this.level;
		int b = o.getLevel();
		int cmp;
		if (a > b)
		   cmp = +1;
		else if (a < b)
		   cmp = -1;
		else
		   cmp = 0;
		return cmp;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdmDTO [");
		if (admName != null) {
			builder.append("admName=");
			builder.append(admName);
			builder.append(", ");
		}
		builder.append("level=");
		builder.append(level);
		builder.append(", admOpenstreetMapId=");
		builder.append(admOpenstreetMapId);
		builder.append("]");
		return builder.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((admName == null) ? 0 : admName.hashCode());
		result = prime * result
				+ (int) (admOpenstreetMapId ^ (admOpenstreetMapId >>> 32));
		result = prime * result + level;
		return result;
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
		AdmDTO other = (AdmDTO) obj;
		if (admName == null) {
			if (other.admName != null)
				return false;
		} else if (!admName.equals(other.admName))
			return false;
		if (admOpenstreetMapId != other.admOpenstreetMapId)
			return false;
		if (level != other.level)
			return false;
		return true;
	}
	
	

}
