package com.gisgraphy.stats;

public class StatsDataDTO {
	
	private String Label;
	private Long stat;
	
	public StatsDataDTO(String label, Long stat) {
		super();
		Label = label;
		this.stat = stat;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return Label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		Label = label;
	}
	/**
	 * @return the stat
	 */
	public Long getStat() {
		return stat;
	}
	/**
	 * @param stat the stat to set
	 */
	public void setStat(Long stat) {
		this.stat = stat;
	}
	
	
	

}
