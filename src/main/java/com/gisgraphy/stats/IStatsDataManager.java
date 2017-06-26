package com.gisgraphy.stats;

import java.util.List;

public interface IStatsDataManager {
	
	public String getAllInJson(boolean refresh);
	
	public String getOneInJson(String countryCode, boolean refresh) ;
	
	public List<StatsDataDTO> processCountryCode(String country) ;
	
	public void exportEachCountriesInJson();
	
	public void exportAllInJson();
	
	public void exportStats();

}
