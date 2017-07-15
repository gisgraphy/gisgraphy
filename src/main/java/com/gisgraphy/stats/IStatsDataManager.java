package com.gisgraphy.stats;

import java.util.List;

public interface IStatsDataManager {
	
	public String getAllInJson(boolean refresh,boolean details);
	
	public String getOneInJson(String countryCode, boolean refresh) ;
	
	public List<StatsDataDTO> processCountryCode(String country,boolean withdetails) ;
	
	public List<StatsDataDTO> getAllSummary(boolean refresh);
	
	public void exportEachCountriesInJson();
	
	public void exportAllInJson();
	
	public void exportAllInJsonNoDetails();
	
	public void exportAllSummaryInJson();
	
	public void exportStats();

}
