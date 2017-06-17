package com.gisgraphy.stats;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;

@Ignore
public class StatsDataManagerTest extends AbstractIntegrationHttpSolrTestCase{

	@Autowired
	public IStatsDataManager statsDataManager;
	
	
	@Test
	public void test() {
		statsDataManager.exportAllInJson();
	}

}
