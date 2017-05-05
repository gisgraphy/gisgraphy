package com.gisgraphy.importer;


import java.io.File;
import java.nio.charset.Charset;
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
	
	@Test
	public void testGetInput(){
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
			
			@Override
			Integer getImportKey() {
				return 10;
			}
		};
		byte[] newb2 = new byte[2];
		newb2[0]= ((byte)195);//c3
		newb2[1]=((byte)159);//ef
		System.out.println(new String(newb2,Charset.forName("utf-8")));
		
		
		byte[] chineseencoded = new byte[4];//c6ee 0aa4
		chineseencoded[0]= ((byte)238);//ee
		chineseencoded[1]=((byte)198);//c6
		chineseencoded[2]= ((byte)164);//a4
		chineseencoded[3]=((byte)10);//0a
		
		byte[] chinesnoteencoded = new byte[4];//bce4 0a9a
		chinesnoteencoded[0]= ((byte)228);//e4
		chinesnoteencoded[1]=((byte)188);//bc 
		chinesnoteencoded[2]= ((byte)154);//9a
		chinesnoteencoded[3]=((byte)10);//0a 
		String chinese = new String(chineseencoded,Charset.forName("UTF-8"));
		Assert.assertEquals("new", importerProcessor.getInput("xo").trim());
		//System.out.println("--------------");
			Assert.assertEquals("Straße", importerProcessor.getInput("]~|kÍ©o"));
		System.out.println("--------------");
		/*System.out.println(chinese);*/
		Assert.assertEquals("会", importerProcessor.getInput(chinese).trim());
		//9a0a=>39434
		//Assert.assertEquals("é会意字", importerProcessor.getInput("Í³îÆ€ðï·¡").trim());
		
		
		//Assert.assertEquals("Alte Hellersdorfer Straße", importerProcessor.getInput("Kv~o*Rovvo|}ny|po|*]~|kͩo"));//Kv~o*Rovvo|}ny|po|*]~|kͩo
		
	}
	

}
