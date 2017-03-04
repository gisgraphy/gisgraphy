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

import static com.gisgraphy.compound.Trie.CONDENSE;
import static com.gisgraphy.compound.Trie.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author david Masclet
 * 
 * decompounder are often based on wordlist and doesn't return unknow words (as lucene one).
 * It is not very useful when you got street names (e.g : fooStrasse).<br/><br>
 * This decompounder aim is to split a word based on words list but keep the unknow words: 
 * e.g : if words are {weg,wald} then foowegwald will return [foowegwald foo weg wald].
 * lucene one would have returned [weg wald].
 * 
 */
public class Decompounder {
	private Pattern p;
	private Pattern concatenatePattern;
	public enum state {CONCATENATE, SEPARATE, NOT_APPLICABLE};
	Pattern ENDING_POINT = Pattern.compile("\\.$");
	
	public static List<String> DEFAULT_WORD = new ArrayList<String>(){
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
	
	private static List<String> DECOMPOUND_COUNTRIES = new ArrayList<String>(){
		{
			add("DE");
			add("CH");
			add("LI");
			add("AT");
		}
	};
	
	/**
	 * create a basic decompounder with default ending word
	 */
	public Decompounder(){
		this(DEFAULT_WORD);
	}
	
		

	public Decompounder(List<String> words) {
		if (words==null){
			throw new RuntimeException("words list is mandatory for a decompounder");
		}
		List<String> inWords = new ArrayList<String>();
		List<String> endWords = new ArrayList<String>();
		for (String word: words){
			if (word.endsWith(".")){
				String endWord = word.substring(0, word.length()-1);
				endWords.add(endWord);
			} else {
				inWords.add(word);
			}
			
		}
		String re = trie(inWords, CONDENSE);
		String re2 = trie(endWords, CONDENSE);
		concatenatePattern = Pattern
				.compile("((\\S|\\s)("+re2+"\\b[\\.]?))",Pattern.CASE_INSENSITIVE);
		
		re= "((?:("+re2+"\\b[\\.]?))|(?:"+re+"))";
		//System.out.println(re);
		p = Pattern
				.compile(re, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}

	public String[] decompound(String str) {
		//Simple but probably not optimized
		Matcher m = p.matcher(str);
		StringBuffer s = new StringBuffer();
		boolean found=false;
		while (m.find()) {
			found=true;
			m.appendReplacement(s, " " + m.group(0) + " ");
		}
		m.appendTail(s);
		if(found){
		return s.toString().replaceAll("\\s+", " ").trim().split(" ");
		} else {
			return new String[]{str};
		}
	}
	
	public String getOtherFormat(String str){
		state state = getSate(str);
		if (state==state.CONCATENATE){
			return separate(str);
		} else if (state == state.SEPARATE){
			return concatenate(str);
		} else {
			return str;
		}
	}
	
	public String getOtherFormatForText(String text){
		if (text==null){
			return text;
		}
		Matcher m = concatenatePattern.matcher(text);
				StringBuffer s = new StringBuffer();
				while (m.find()) {
					if (" ".equals(m.group(2))|| "-".equals(m.group(2))){
					m.appendReplacement(s,  m.group(3) );
					} else {
						m.appendReplacement(s,  m.group(2)+" " +m.group(3) + " ");
					}
		}
				m.appendTail(s);
				return s.toString().replaceAll("\\s+", " ").trim();
	}
	
	/*public String separate(String str) {
		//Simple but probably not optimized
		Matcher m = p.matcher(str);
		StringBuffer s = new StringBuffer();
		boolean found=false;
		while (m.find()) {
			found=true;
			m.appendReplacement(s, " " + m.group(0) + " ");
		}
		m.appendTail(s);
		if(found){
		return s.toString().replaceAll("\\s+", " ").trim();
		} else {
			return str;
		}
	}*/
	
	public String concatenate(String text){
		return separate(text);
	}
	
	public String separate(String text){
		if (text==null){
			return text;
		}
		Matcher m = concatenatePattern.matcher(text);
				StringBuffer s = new StringBuffer();
				while (m.find()) {
					if (" ".equals(m.group(2))){
					m.appendReplacement(s,  m.group(3) );
					} else {
						m.appendReplacement(s,  m.group(2)+" " +m.group(3) + " ");
					}
				m.appendTail(s);
				return s.toString().replaceAll("\\s+", " ").trim();
		}
		return text;
	}
	
	public state getSate(String text){
		if (text==null){
			return state.NOT_APPLICABLE;
		}
		Matcher m = concatenatePattern.matcher(text);
		if (m.find()){
			if (" ".equals(m.group(2))){
				return state.SEPARATE;
			} else {
				return state.CONCATENATE;
			}
		}
		return state.NOT_APPLICABLE;
	}
	
	public static boolean isDecompoudCountryCode(String countryCode){
		if (countryCode!=null){
		return DECOMPOUND_COUNTRIES.contains(countryCode.toUpperCase());
		} else {
			return false;
		}
	}
	
	

}


