/*
 * dfh.trie -- a library for generating trie regular expressions
 * 
 * Copyright (C) 2012 David F. Houghton
 * 
 * This software is licensed under the LGPL. Please see accompanying NOTICE file
 * and lgpl.txt.
 */
package com.gisgraphy.compound;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates TRIE regexes out of lists of words. Regexes so created will be fast
 * (optimal or near-optimal matching speed so long as non-backtracking) and
 * compact. This class is thread safe.
 * 
 * @author David Houghton
 */
public class Trie {

	/**
	 * Whether the pattern created should be case sensitive
	 */
	public static final int CASEINSENSITIVE = 1;

	/**
	 * If set, and whitespace is not being preserved, any whitespace found will
	 * be replaced with {@code [ \\u00a0]+} (perhaps with the possessive
	 * modifier {@code +}).
	 * <p>
	 * If no special whitespace option is set, whitespace becomes {@code \\s+}.
	 * See {@link #SPACEANDTAB}.
	 */
	public static final int SPACEONLY = 2;

	/**
	 * If set, and whitespace is not being preserved, any whitespace found will
	 * be replaced with {@code [ \\u00a0\\t]+} (perhaps with the possessive
	 * modifier {@code +}).
	 * <p>
	 * If no special whitespace option is set, whitespace becomes {@code \\s+}.
	 * See {@link #SPACEONLY}.
	 */
	public static final int SPACEANDTAB = 4;

	/**
	 * Treat any whitespace character found like any other character. See
	 * {@link #SPACEONLY} and {@link #SPACEANDTAB}.
	 */
	public static final int PRESERVE_WHITESPACE = 8;

	/**
	 * Turns off ordinary repetition behavior, which is to make them possessive.
	 */
	public static final int BACKTRACKING = 16;

	/**
	 * Whether to assume word boundaries at the edges of phrases.
	 */
	public static final int AUTO_BOUNDARY = 32;

	/**
	 * If set, regexes will not include the "possessive" modifier &mdash; e.g.,
	 * {@code *+}, {@code ++}, {@code ?+} &mdash; which is not understood by
	 * versions of Perl prior to 5.10.
	 */
	public static final int PERL_SAFE = 64;

	/**
	 * Whether to condense long repeating sequences; e.g.,
	 * {@code aaaaaaaaaaaa -> (?:aa) 6}
	 */
	public static final int CONDENSE = 128;

	/**
	 * Use {@code (?:a|b|c)} in lieu of {@code [a-c]}.
	 */
	public static final int NO_CHAR_CLASSES = 256;
	/**
	 * Reverse all strings before compiling the regex.
	 */
	public static final int REVERSE = 512;

	/**
	 * A character used to represent word boundaries. I use the null character
	 * since it's unlikely to be passed in as input.
	 */
	private static final char BOUNDARY = '\u0000';

	private static final String LONG_TEXT = "..";

	/**
	 * A pattern matching things that might be regex meta-characters. This could
	 * be more carefully written, but I figure it doesn't matter all that much.
	 */
	private static final Pattern needsQuotes = Pattern
			.compile("[\\p{Punct}&&[^!;:,'\"_-]]++");

	/**
	 * A pattern matching characters that need to be escaped in character
	 * classes.
	 */
	private static final Pattern classMetaCharacters = Pattern
			.compile("([\\[\\]\\^\\-\\&\\{\\}\\\\])");

	/**
	 * Comparator to generate a reversed alphabetic list. Minor optimization
	 * achieved (in recursively looped code) by ignoring any language specific
	 * {@link Collator} and making the Comparator non-generic.
	 */
	private static final Comparator<Object> comparator = new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			String s1 = o1.toString(), s2 = o2.toString();
			int l1 = s1.length(), l2 = s2.length(), min = l1 < l2 ? l1 : l2;
			for (int i = 0; i < min; i++) {
				int comparison = s2.charAt(i) - s1.charAt(i);
				if (comparison == 0)
					continue;
				return comparison;
			}
			return l2 - l1;
		}
	};

	/**
	 * Convenience method. See {@link #trie(String[], int)}. {@link #DEFAULTS}
	 * are used.
	 * 
	 * @param ar
	 *            array of {@link String} to match
	 * @return {@link String} parsable as a regular expression
	 * @throws TrieException
	 */
	public static String trie(String[] ar) throws TrieException {
		return trie(ar, 0);
	}

	/**
	 * Delegates to {@link #trie(String[])}.
	 * 
	 * @param c
	 *            {@link Collection} of phrases to match
	 * @return {@link String} parsable as a regular expression
	 * @throws TrieException
	 */
	public static String trie(Collection<String> c) throws TrieException {
		String[] ar = c.toArray(new String[c.size()]);
		return trie(ar);
	}

	/**
	 * Delegates to {@link #trie(String[], int)}.
	 * 
	 * @param c
	 *            {@link Collection} of phrases to match
	 * @param flags
	 *            modifiers for regular expression
	 * @return {@link String} parsable as a regular expression
	 * @throws TrieException
	 */
	public static String trie(Collection<String> c, int flags)
			throws TrieException {
		String[] ar = c.toArray(new String[c.size()]);
		return trie(ar, flags);
	}

	/**
	 * Takes an array of {@link String} and makes a compact regular expression
	 * which will match only the expressions in the list. The regex so created
	 * is deterministic: there is at most one possible choice at each branch
	 * point. This means no backtracking should be necessary. This allows for
	 * much more rapid matching. It is possible to cause the regex to be
	 * backtracking, however.
	 * 
	 * @param ar
	 *            the array of {@code Strings} to match.
	 * @param flags
	 *            modifiers for regular expression; e.g.,
	 *            {@code CONDENSE | AUTOBOUNDARY}
	 * @return {@link String} parsable as a regular expression
	 * @throws TrieException
	 *             for inconsistent parameters
	 */
	public static String trie(String[] ar, int flags) throws TrieException {
		State state = new State();
		state.caseInsensitive = (flags & CASEINSENSITIVE) == CASEINSENSITIVE;
		state.backtrack = (flags & BACKTRACKING) == BACKTRACKING;
		state.perlSafe = (flags & PERL_SAFE) == PERL_SAFE;
		state.groupMod = state.backtrack ? ':' : '>';
		state.whitespace = null;
		if ((flags & PRESERVE_WHITESPACE) != PRESERVE_WHITESPACE) {
			if (((flags & SPACEANDTAB) == SPACEANDTAB)
					&& ((flags & SPACEONLY) == SPACEONLY))
				throw new TrieException(
						"SPACEANDTAB | SPACEONLY is inconsistent");
			StringBuilder b = new StringBuilder();
			if (state.backtrack && state.perlSafe)
				b.append("(?").append(state.groupMod);
			if ((flags & SPACEONLY) == SPACEONLY)
				b.append("[ \u00a0]");
			else if ((flags & SPACEANDTAB) == SPACEANDTAB)
				b.append("[ \u00a0\\t]");
			else
				b.append("\\s");
			b.append('+');
			if (!state.backtrack) {
				if (state.perlSafe)
					b.append(')');
				else
					b.append('+');
			}
			state.whitespace = b.toString();
		}
		state.autoBoundary = (flags & AUTO_BOUNDARY) == AUTO_BOUNDARY;
		state.condense = (flags & CONDENSE) == CONDENSE;
		state.charclasses = (flags & NO_CHAR_CLASSES) != NO_CHAR_CLASSES;
		boolean reverse = (flags & REVERSE) == REVERSE;

		// clean out duplicates and estimate a maximum length for the regex
		int charCount = 5;
		{
			// inside block to localize data structures
			Set<String> types = new HashSet<String>(ar.length);
			for (int i = 0; i < ar.length; i++) {
				String s = ar[i];
				if (state.whitespace != null)
					s = s.trim().replaceAll("\\s++", " ");
				if (s.equals(""))
					continue;
				if (state.caseInsensitive)
					s = s.toLowerCase();
				if (reverse)
					s = reverse(s);
				// add boundary characters
				if (state.autoBoundary) {
					if (Character.isLetterOrDigit(s.charAt(0)))
						s = BOUNDARY + s;
					if (Character.isLetterOrDigit(s.charAt(s.length() - 1)))
						s += BOUNDARY;
				}
				if (!types.contains(s)) {
					types.add(s);
					charCount += s.length() + 1;
				}
			}
			ar = (String[]) types.toArray(new String[types.size()]);
		}

		// put in reversed alphabetical order -- this puts longer words first
		// and groups words with similar prefixes together
		Arrays.sort(ar, comparator);

		StringBuilder buffer = new StringBuilder(charCount);
		if (state.caseInsensitive)
			buffer.append("(?i:");
		// create the lookup list
		PatternAndEncapsulation pe = pattern(ar, state);
		if (!(state.caseInsensitive || pe.encapsulated)) {
			buffer.append("(?").append(state.groupMod);
			buffer.append(pe.pattern);
			buffer.append(')');
		} else
			buffer.append(pe.pattern);
		if (state.caseInsensitive)
			buffer.append(')');

		// free up memory
		ar = null;

		if (state.condense) {
			buffer = simpleRepeatingPatternCondenser(state, buffer);
		}

		// remove boundary characters
		if (state.autoBoundary) {
			// grab enough space in one go
			StringBuilder b2 = new StringBuilder((int) (buffer.length() * 1.5));
			int start = 0, ptr = 0;
			for (int lim = buffer.length(); ptr < lim; ptr++) {
				if (buffer.charAt(ptr) == BOUNDARY) {
					if (start < ptr)
						b2.append(buffer.subSequence(start, ptr));
					b2.append("\\b");
					start = ptr + 1;
				}
			}
			if (start < ptr)
				b2.append(buffer.subSequence(start, ptr));
			buffer = b2;
		}

		return buffer.toString();
	}

	/**
	 * Reverses characters in string.
	 * 
	 * @param s
	 * @return s reversed
	 */
	private static String reverse(String s) {
		char[] car = new char[s.length()];
		for (int i = 0, j = s.length() - 1; i < s.length(); i++, j--)
			car[j] = s.charAt(i);
		return new String(car);
	}

	// some regexes used by simpleRepeatingPatternCondenser

	/**
	 * Just looks for repeating sequences
	 */
	private static final Pattern repetionPattern = Pattern
			.compile("(.{1,16}?)\\1++");
	private static final Pattern precedingGroupPattern = Pattern
			.compile("\\{\\d*$");
	private static final Pattern trailingGroupPattern = Pattern
			.compile("\\d*\\}");
	private static final Pattern digitPattern = Pattern.compile("\\d+");

	private static StringBuilder simpleRepeatingPatternCondenser(State state,
			StringBuilder buffer) {
		StringBuilder s = new StringBuilder(buffer);
		Matcher m = repetionPattern.matcher(s);
		while (m.find()) {
			String smallestMatch = m.group(1);
			int pos = m.start();

			// do some math to see whether this is worth our time
			int smallLength = smallestMatch.length();
			int allLength = m.group().length();
			int iterations = allLength / smallLength;
			if (smallLength == 1) {
				if (allLength <= 4)
					continue;
			} else {
				if (smallLength + 7 >= allLength)
					continue;
			}

			// now check to make sure we aren't in an iteration counter (highly
			// unlikely given previous test)
			if (digitPattern.matcher(smallestMatch).matches()) {
				String substring = s.substring(m.end());
				Matcher trailingMatcher = trailingGroupPattern
						.matcher(substring);
				if (trailingMatcher.lookingAt()) {
					substring = s.substring(0, pos);
					Matcher precedingMatcher = precedingGroupPattern
							.matcher(substring);
					if (precedingMatcher.find()) {
						continue;
					}
				}
			}

			try {
				// see whether the substring compiles as a regex
				@SuppressWarnings("unused")
				Pattern testPattern = Pattern.compile(smallestMatch);

				// seems to be good, so we condense it
				StringBuilder b = new StringBuilder(s.substring(0, pos));
				if (smallLength == 1) {
					b.append(smallestMatch);
				} else {
					b.append("(?:").append(smallestMatch).append(")");
				}
				b.append('{').append(iterations).append('}');
				b.append(s.substring(m.end()));
				s = b;
				m = repetionPattern.matcher(s);

				// set the new matcher to skip the subsequence already examined
				m.region(pos, s.length());
			} catch (Exception e) {
			}
		}
		return s;
	}

	/**
	 * A data structure useful as a return value.
	 * 
	 * @author David Houghton
	 */
	private static class PatternAndEncapsulation {
		String pattern;

		boolean encapsulated = false;

		PatternAndEncapsulation(String pattern, boolean encapsulated) {
			this.pattern = pattern;
			this.encapsulated = encapsulated;
		}
	}

	/**
	 * Another little data structure useful as a return value.
	 * 
	 * @author David Houghton
	 */
	private static class OffsetAndPattern {
		/**
		 * offset into array to begin looking for strings with a common prefix
		 */
		int offset = 0;

		/**
		 * sub-pattern
		 */
		PatternAndEncapsulation segment = null;

		/**
		 * Record of text before quotemeta
		 */
		String originalText = null;
	}

	/**
	 * A data structure to ensure thread safety and to facilitate temporarily
	 * modifying processing to ensure the validity of non-backtracking
	 * expressions.
	 */
	private static class State {

		boolean condense, autoBoundary, backtrack, caseInsensitive, perlSafe,
				charclasses;

		char groupMod;

		String whitespace;

		boolean makeSuffix = true;

		State noSuffixCopy() {
			State s = new State();
			s.condense = condense;
			s.autoBoundary = autoBoundary;
			s.groupMod = groupMod;
			s.whitespace = whitespace;
			s.backtrack = backtrack;
			s.caseInsensitive = caseInsensitive;
			s.perlSafe = perlSafe;
			s.charclasses = charclasses;
			s.makeSuffix = false;

			return s;
		}
	}

	/**
	 * Reduces a sorted array of {@code Strings} to a regular expression
	 * matching any {@code String} in that array.
	 * 
	 * @param ar
	 *            the set of {@code Strings} that should be converted into a
	 *            pattern
	 * @param state
	 *            various fields particular to current thread
	 * @return matching regex
	 */
	private static PatternAndEncapsulation pattern(String[] ar, State state) {
		// check for degenerate case
		if (ar.length == 1)
			return quotemeta(ar[0], state);
		if (ar.length == 0)
			return quotemeta("", state);

		if (state.makeSuffix) {
			// find common suffix
			int numChars = 1;
			int firstLength = ar[0].length();
			FIND_SUFFIX_LOOP: while (true) {
				int length = firstLength;
				if (length <= numChars) {
					numChars--;
					break;
				}
				char c = ar[0].charAt(length - numChars);
				for (int i = 1; i < ar.length; i++) {
					String s = ar[i];
					length = s.length();
					if (length <= numChars || s.charAt(length - numChars) != c) {
						numChars--;
						break FIND_SUFFIX_LOOP;
					}
				}
				numChars++;
			}
			// if a common suffix was found, subtract it from all strings and
			// make regexes for whatever strings remain
			if (numChars > 0) {
				String suffix = ar[0].substring(ar[0].length() - numChars);

				// non-backtracking patterns can have problems with suffixes; we
				// shrink the suffix until we have one such that for no string
				// in the array is a leading substring of the suffix equal to a
				// trailing substring of the remainder of the word
				if (!state.backtrack) {
					while (true) {
						int sublength = 1;
						boolean foundMatch = false;
						SHRINK_SUFFIX_LOOP: do {
							String subSuffix = suffix.substring(0, sublength);
							for (String s : ar) {
								String substring = s.substring(0, s.length()
										- numChars);
								if (substring.endsWith(subSuffix)) {
									foundMatch = true;
									break SHRINK_SUFFIX_LOOP;
								}
							}
						} while (++sublength <= numChars);
						if (foundMatch) {
							numChars--;
							if (numChars == 0)
								return patternNoExtractableSuffix(ar, state);
							suffix = suffix.substring(1);
							continue;
						}
						break;
					}
				}

				suffix = quotemeta(suffix, state).pattern;
				for (int i = 0; i < ar.length; i++) {
					int wordLength = ar[i].length();
					ar[i] = ar[i].substring(0, wordLength - numChars);
				}

				// resort members of array put in reversed alphabetical order --
				// this puts longer words first and groups words with similar
				// prefixes together
				Arrays.sort(ar, comparator);

				// look for common prefixes
				PatternAndEncapsulation pe = patternNoExtractableSuffix(ar,
						state.backtrack ? state : state.noSuffixCopy());
				StringBuilder buffer = new StringBuilder(pe.pattern.length()
						+ suffix.length());
				buffer.append(pe.pattern);
				buffer.append(suffix);
				return new PatternAndEncapsulation(buffer.toString(), false);
			} else
				// look for common prefixes
				return patternNoExtractableSuffix(ar, state);
		} else {
			// resume making suffixes in subpatterns
			state.makeSuffix = true;
			return patternNoExtractableSuffix(ar, state);
		}
	}

	/**
	 * Takes a sorted array of {@code Strings} and returns a matching regex.
	 * This code assumes the {@code Strings} in the array have no common suffix
	 * that can be extracted. (It is possible that the array contains a
	 * one-letter {@code String} that is also a common suffix, but such a suffix
	 * is not extractable. If it were extracted, the set of Strings matched by
	 * the regex would not be the same as the set that produced it.)
	 * 
	 * @param ar
	 *            {@code Strings} to match
	 * @param state
	 *            various fields particular to current thread
	 * @return matching regex
	 */
	private static PatternAndEncapsulation patternNoExtractableSuffix(
			String[] ar, State state) {
		OffsetAndPattern op = new OffsetAndPattern();
		List<OffsetAndPattern> subsegments = new ArrayList<OffsetAndPattern>(
				Math.min(ar.length, 52));
		boolean compressible = state.charclasses;
		// collect different prefixes and the associated remaining strings
		do {
			op = patternCommonPrefix(ar, op.offset, state.makeSuffix ? state
					: state.noSuffixCopy());
			if (op == null)
				break;
			compressible &= op.originalText.length() == 1;
			subsegments.add(op);
		} while (op.offset < ar.length);

		// return appropriate string
		switch (subsegments.size()) {
		case 0:
			return new PatternAndEncapsulation("", false);
		case 1:
			return subsegments.get(0).segment;
		default:
			StringBuilder segment = null;
			// check to see if the segments can be converted to a character
			// class
			if (compressible) {
				// reduce segments to a character class
				segment = new StringBuilder(subsegments.size());
				boolean containsBoundary = false;
				// make sure dash isn't misinterpreted as part of a range
				// expression
				boolean containsDash = false;
				// look for character ranges of 3 or more
				char headOfRange = 0, lastChar = 0, charInRange = 0;
				for (int i = subsegments.size() - 1; i >= 0; i--) {
					char c = subsegments.get(i).originalText.charAt(0);
					if (lastChar > 0) {
						if (c == lastChar + 1) {
							lastChar = c;
							charInRange++;
						} else {
							while (true) {
								switch (charInRange) {
								case 1:
									if (headOfRange == '-')
										containsDash = true;
									else
										segment.append(headOfRange);
									break;
								case 2:
									if (headOfRange == '-')
										containsDash = true;
									else
										segment.append(headOfRange);
									if (lastChar == '-')
										containsDash = true;
									else
										segment.append(lastChar);
									break;
								default:
									if (headOfRange == '-') {
										containsDash = true;
										headOfRange++;
										charInRange--;
									} else if (lastChar == '-') {
										containsDash = true;
										lastChar--;
										charInRange--;
									}
									if (charInRange == 2)
										continue;
									segment.append(headOfRange).append('-')
											.append(lastChar);
								}
								break;
							}
							headOfRange = lastChar = c;
							charInRange = 1;
						}

					} else if (state.autoBoundary && c == BOUNDARY)
						containsBoundary = true;
					else {
						lastChar = headOfRange = c;
						charInRange = 1;
					}
				}
				if (charInRange > 0) {
					while (true) {
						switch (charInRange) {
						case 1:
							if (headOfRange == '-')
								containsDash = true;
							else
								segment.append(headOfRange);
							break;
						case 2:
							if (headOfRange == '-')
								containsDash = true;
							else
								segment.append(headOfRange);
							if (lastChar == '-')
								containsDash = true;
							else
								segment.append(lastChar);
							break;
						default:
							if (headOfRange == '-') {
								containsDash = true;
								headOfRange++;
								charInRange--;
							} else if (lastChar == '-') {
								containsDash = true;
								lastChar--;
								charInRange--;
							}
							if (charInRange == 2)
								continue;
							segment.append(headOfRange).append('-')
									.append(lastChar);
						}
						break;
					}
				}
				if (containsDash)
					segment.append('-');
				String characters = classMetaCharacters.matcher(
						segment.toString()).replaceAll("$1");
				segment = new StringBuilder(characters.length()
						+ (containsBoundary ? 8 : 2));
				if (containsBoundary)
					segment.append("(?").append(state.groupMod);
				segment.append('[');
				segment.append(characters);
				segment.append(']');
				if (containsBoundary)
					segment.append('|').append(BOUNDARY).append(')');
			} else {
				if (subsegments.size() == 1)
					return subsegments.get(0).segment;
				Iterator<OffsetAndPattern> i = subsegments.iterator();
				segment = new StringBuilder("(?");
				segment.append(state.groupMod);
				segment.append(i.next().segment.pattern);
				while (i.hasNext())
					segment.append('|').append(i.next().segment.pattern);
				segment.append(')');
			}
			return new PatternAndEncapsulation(segment.toString(), true);
		}
	}

	/**
	 * Takes in a sorted array of {@code Strings} and an offset into that array.
	 * Finds the subsequence of the array that has the same prefix as the string
	 * at the offset. Produces a pattern matching this subsequence. Returns this
	 * pattern plus the offset for the beginning of the next pattern.
	 * 
	 * Offset is useful for beginning search for next subsequence with a common
	 * prefix.
	 * 
	 * @param ar
	 *            a set of {@code Strings} to match.
	 * @param offset
	 *            offset into array where set of strings matching subpattern
	 *            begins
	 * @param state
	 *            various fields particular to current thread
	 * @return {@code OffsetAndPattern} containing current pattern and beginning
	 *         of next set of {@code Strings} for which a pattern must be found
	 */
	private static OffsetAndPattern patternCommonPrefix(String[] ar,
			int offset, State state) {
		if (ar[offset].length() == 0)
			return null;
		OffsetAndPattern op = new OffsetAndPattern();
		op.offset = offset;

		// look for the first string in the array that does not begin with the
		// initial character
		char c = ar[offset].charAt(0);
		do {
			op.offset++;
		} while (op.offset < ar.length && ar[op.offset].length() > 0
				&& ar[op.offset].charAt(0) == c);

		if (op.offset == offset + 1) {
			// the initial character was unique
			op.originalText = ar[offset];
			op.segment = quotemeta(ar[offset], state);
		} else {

			// figure out how long the prefix is
			int length = 1;
			SEARCH: do {
				char endchar = ar[offset].charAt(length);
				for (int i = offset + 1; i < op.offset; i++) {
					if (ar[i].length() == length
							|| ar[i].charAt(length) != endchar)
						break SEARCH;
				}
				length++;
			} while (true);

			// figure out whether any member of the set equals the prefix
			boolean lastEmpty = ar[op.offset - 1].length() == length;

			StringBuilder buffer = new StringBuilder();
			// quote this prefix
			PatternAndEncapsulation prefix = quotemeta(
					ar[offset].substring(0, length), state);
			buffer.append(prefix.pattern);
			// collect the non-prefixes and construct a pattern for them
			int endIndex = lastEmpty ? op.offset - 1 : op.offset;
			String[] subsegments = new String[endIndex - offset];
			for (int i = offset; i < endIndex; i++)
				subsegments[i - offset] = ar[i].substring(length);
			PatternAndEncapsulation rest = pattern(subsegments, state);
			if (lastEmpty && !rest.encapsulated) {
				buffer.append("(?").append(state.groupMod);
				buffer.append(rest.pattern);
				buffer.append(')');
			} else
				buffer.append(rest.pattern);
			if (lastEmpty) {
				buffer.append('?');
				if (!(state.backtrack || state.perlSafe))
					buffer.append('+');
			}
			op.segment = new PatternAndEncapsulation(buffer.toString(), false);
			op.originalText = LONG_TEXT; // only one-char strings are of
			// interest
		}
		return op;
	}

	/**
	 * Escapes meta characters and replaces blank spaces with
	 * whitespace-matching pattern.
	 * 
	 * @param s
	 *            {@code String} to quote
	 * @param whitespace
	 *            nature of whitespace for regex; null means preserver
	 *            whitespace
	 * @return quoted {@code String}
	 */
	private static PatternAndEncapsulation quotemeta(String s, State state) {
		boolean singleton = s.length() == 1;
		if (state.whitespace == null)
			return new PatternAndEncapsulation(subquote(s, state), singleton);
		StringBuilder b = new StringBuilder();
		if (s.startsWith(" ")) {
			b.append(state.whitespace);
			singleton = false;
			s = s.substring(1);
		}
		boolean finalSpace = false;
		if (s.endsWith(" ")) {
			finalSpace = true;
			s = s.substring(0, s.length() - 1);
		}
		String[] subsegments = s.split(" ");
		b.append(subquote(subsegments[0], state));
		for (int i = 1; i < subsegments.length; i++)
			b.append(state.whitespace).append(subquote(subsegments[i], state));
		if (finalSpace)
			b.append(state.whitespace);
		return new PatternAndEncapsulation(b.toString(), singleton);
	}

	/**
	 * Simple test adding in meta-character quoting when it might be necessary.
	 * 
	 * @param s
	 *            {@code String} to quote
	 * @return quoted {@code String}
	 */
	private static String subquote(String s, State state) {
		Matcher m = needsQuotes.matcher(s);
		StringBuilder b = new StringBuilder(s.length() * 2);
		int start = 0;
		while (m.find()) {
			b.append(s.substring(start, m.start()));
			start = m.end();
			String match = m.group();
			if (state.condense || match.length() < 4) {
				for (int i = 0, lim = match.length(); i < lim; i++) {
					char c = match.charAt(i);
					// all this rigamarole is to sidestep a Java regex parsing
					// bug: you can't simply escape an escape if the following
					// character is 'Q', or perhaps 'E'
					if (c == '\\' && start < s.length()) {
						char c2 = s.charAt(m.start() + i + 1);
						if (c2 == 'Q' || c2 == 'E') {
							b.append("[\\\\]");
							continue;
						}
					}
					b.append('\\').append(c);
				}
			} else {
				b.append("\\Q").append(match).append("\\E");
			}
		}
		if (start > 0) {
			b.append(s.substring(start));
			return b.toString();
		} else
			return s;
	}
}