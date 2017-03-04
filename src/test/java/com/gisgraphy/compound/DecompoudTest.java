/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/
package com.gisgraphy.compound;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.compound.Decompounder.state;

public class DecompoudTest {
	@SuppressWarnings("serial")
	private static List<String> words = new ArrayList<String>(){ 
		{
		add("weg");
		add("strasse"); 
		add("wald");
		add("str.");
		add("kg.");
		}
	};
	
	@Test
	public void testDecompound_only_ending_words(){
		List<String> words = new ArrayList<String>(){ 
			{
			add("weg.");
			add("strasse."); 
			}
		};
		Decompounder d = new Decompounder(words);
		
	}

	@Test
	public void testDecompound2(){
		Decompounder d = new Decompounder(words);
		
		String[] stringsToTest = {"foo","fooweg","fOOWeg","wegfoo","wegwaldfoo","wegfoowald","BleibtreuStr","BleibtreuStrnotend","Bleibtreukg","Bleibtreukgnotend"};
		String[][] results= {{"foo"},{"foo","weg"},{"fOO","Weg"},{"weg","foo"},{"weg","wald","foo"},{"weg","foo","wald"},{"Bleibtreu","Str"},{"BleibtreuStrnotend"},{"Bleibtreu","kg"},{"Bleibtreukgnotend"}};
		for(int i=0;i<stringsToTest.length;i++){
			String str = stringsToTest[i];
			String[] actual = d.decompound(str);
			System.out.println(str+":"+Arrays.toString(actual));
			Assert.assertArrayEquals("expected "+Arrays.toString(results[i]) + "but was "+Arrays.toString(actual), results[i], actual);
		}
	}
	
	@Test
	public void testSeparate(){
	List<String> words = new ArrayList<String>(){ 
			{
			add("weg.");
			add("strasse."); 
			add("foo");
			}
		};
		Decompounder d = new Decompounder(words);
		
		String[] stringsToTest = {"foo","fooweg","fOOWeg"};
		String[] results= {"foo","foo weg","fOO Weg"};
		for(int i=0;i<stringsToTest.length;i++){
			String str = stringsToTest[i];
			String actual = d.separate(str);
			Assert.assertEquals("expected "+results[i]+ "but was "+str, results[i], actual);
		}
	}
	
	@Test
	public void testGetOtherFormat(){
		 List<String> words = new ArrayList<String>(){ 
				{
				add("weg");
				add("straße."); 
				add("strasse."); 
				add("wald");
				add("str.");
				}
			};
		Decompounder d = new Decompounder(words);
		Assert.assertEquals("foostrasse", d.getOtherFormat("foo strasse"));
		Assert.assertEquals("foo strasse", d.getOtherFormat("foostrasse"));
	}
	
	@Test
	public void testGetOtherFormatForText(){
		 List<String> words = new ArrayList<String>(){ 
				{
					add("weg.");
					add("str.");
					add("straße.");
					add("strasse.");
					add("plätze.");
					add("plätz.");
					add("platze.");
					add("platz.");
					add("wald.");
				}
			};
		Decompounder d = new Decompounder(words);
		/*Assert.assertEquals("foostrasse", d.concatenate("foo strasse"));
		Assert.assertEquals("foo-strasse", d.getOtherFormatForText("foo-strasse"));
		Assert.assertEquals("foo strasse", d.getOtherFormatForText("foostrasse"));
		
		Assert.assertEquals("foostrasse truc", d.getOtherFormatForText("foo strasse truc"));
		Assert.assertEquals("foo strasse truc", d.getOtherFormatForText("foostrasse truc"));
		
		Assert.assertEquals("truc foostrasse", d.getOtherFormatForText("truc foo strasse"));
		Assert.assertEquals("truc foo strasse", d.getOtherFormatForText("truc foostrasse"));
		*/
		//both 
		Assert.assertEquals("Park platz Eau-Le", d.getOtherFormatForText("Parkplatz Eau-Le"));
		Assert.assertEquals("foostrasse truc straße", d.getOtherFormatForText("foo strasse trucstraße"));
		Assert.assertEquals("foostrasse truc straße", d.getOtherFormatForText("foo-strasse trucstraße"));
		Assert.assertEquals("foo str truc straße", d.getOtherFormatForText("foostr trucstraße"));

		Assert.assertEquals("foo str. truc straße", d.getOtherFormatForText("foostr. trucstraße"));
		
		
	}
	
	

	@Test
	public void testConcatenate(){
		 List<String> words = new ArrayList<String>(){ 
				{
				add("weg");
				add("straße."); 
				add("strasse."); 
				add("wald");
				add("str.");
				add("str..");
				}
			};
		Decompounder d = new Decompounder(words);
		
		String[] stringsToTest= {"foo","foo strasse","fOO straße"};
		String[] results = {"foo","foostrasse","fOOstraße"};
		for(int i=0;i<stringsToTest.length;i++){
			String str = stringsToTest[i];
			String actual = d.separate(str);
			Assert.assertEquals("["+i+"] expected "+results[i]+ "but was "+str, results[i], actual);
		}
	}
	
	@Test(expected=RuntimeException.class)
	public void NullWordsList(){
		new Decompounder(null);
	}
	
	@Test
	public void testGetState(){
		
		 List<String> words = new ArrayList<String>(){ 
				{
				add("weg");
				add("straße."); 
				add("strasse."); 
				add("wald");
				add("str.");
				}
			};
		Decompounder d = new Decompounder(words);
		Assert.assertEquals(state.CONCATENATE,d.getSate("trucStrasse"));
		Assert.assertEquals(state.CONCATENATE,d.getSate("trucstraße"));
		
		
		Assert.assertEquals(state.NOT_APPLICABLE,d.getSate("trucStrassefoo"));
		Assert.assertEquals(state.NOT_APPLICABLE,d.getSate("Strasse"));
		Assert.assertEquals(state.NOT_APPLICABLE,d.getSate("foo"));
		Assert.assertEquals(state.NOT_APPLICABLE,d.getSate(""));
		Assert.assertEquals(state.NOT_APPLICABLE,d.getSate(null));
		
		Assert.assertEquals(state.SEPARATE,d.getSate("truc Strasse"));
		Assert.assertEquals(state.SEPARATE,d.getSate("truc Strasse foo"));
	}
	
	@Test
	public void isDecompoudCountryCode(){
		Assert.assertFalse(Decompounder.isDecompoudCountryCode(null));
		Assert.assertTrue(Decompounder.isDecompoudCountryCode("de"));
		Assert.assertTrue(Decompounder.isDecompoudCountryCode("DE"));
	}

}
