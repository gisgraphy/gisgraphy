package com.gisgraphy.importer;


import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.NameValueDTO;

public class AbstractSimpleImporterProcessorTest {

	@Test
	public void isEmptyFields() {
		AbstractSimpleImporterProcessor importerProcessor = new AbstractSimpleImporterProcessor() {
			
			public List<NameValueDTO<Integer>> rollback() {
				return null;
			}
			
			@Override
			protected boolean shouldIgnoreFirstLine() {
				return false;
			}
			
			@Override
			protected boolean shouldIgnoreComments() {
				return false;
			}
			
			@Override
			protected void setCommitFlushMode() {
				
			}
			
			@Override
			protected void processData(String line) throws ImporterException {
				
			}
			
			@Override
			protected int getNumberOfColumns() {
				return 0;
			}
			
			@Override
			protected File[] getFiles() {
				return null;
			}
			
			@Override
			protected void flushAndClear() {
				
			}
		};
		String[] fields = {""};
		Assert.assertTrue(importerProcessor.isEmptyField(fields, 0, false));
		
		fields = null;
		Assert.assertTrue(importerProcessor.isEmptyField(fields, 0, false));
		
		String[] fields2 = {"\"\""};
		Assert.assertTrue(importerProcessor.isEmptyField(fields2, 0, false));
		
		String[] fields3 = {" "};
		Assert.assertTrue(importerProcessor.isEmptyField(fields3, 0, false));
		
		String[] fields4 = {"foo"};
		Assert.assertFalse(importerProcessor.isEmptyField(fields4, 0, false));
		
		String[] fields5 = {" "};
		Assert.assertTrue(importerProcessor.isEmptyField(fields5, 0, false));
	}

}
