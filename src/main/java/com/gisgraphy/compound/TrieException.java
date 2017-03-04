/*
 * dfh.trie -- a library for generating trie regular expressions
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package com.gisgraphy.compound;

/**
 * Marks predictable exceptions in Trie package.
 * 
 * @author David Houghton
 * 
 */
public class TrieException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TrieException(String message) {
		super(message);
	}
}