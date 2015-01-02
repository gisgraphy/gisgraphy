/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

/**
 * The CommentedProperties class is an extension of java.util.Properties
 * to allow retention of comment lines and blank (whitespace only) lines
 * in the properties file.
 * 
 * Written for Java version 1.4
 */
public class CommentedProperties extends java.util.Properties {

    String filePath;
    
    /**
     * 
     */
    public CommentedProperties() {
	super();
    }

    /**
     * @param defaults
     */
    public CommentedProperties(Properties defaults) {
	super(defaults);
    }

    public static void editProperty(String filePath,String key,String value) {
	InputStream inputStream;
	try {
	     inputStream = new FileInputStream(filePath);
	} catch (FileNotFoundException e) {
	    throw new IllegalArgumentException(e);
	}
	CommentedProperties instance = new CommentedProperties();
	try {
	    instance.load(inputStream);
	    instance.setProperty(key, value);
	    OutputStream outputStream = new FileOutputStream(filePath);
	    instance.store(outputStream, "");
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
    
    public static void editPropertyFromClassPathRessource(String classPathRessource,String key,String value) {
	CommentedProperties instance = new CommentedProperties();
	InputStream inputStream;
	File file ;
	try {
	    URL resourceUrl = instance.getClass().getResource(classPathRessource);
	    if (resourceUrl==null){
		throw new IllegalArgumentException(classPathRessource+ " can not be found in classpath");
	    }
	    file = new File(resourceUrl.toURI());

	     inputStream =  new FileInputStream(file);

	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
	try {
	    instance.load(inputStream);
	    instance.setProperty(key, value);
	    OutputStream outputStream = new FileOutputStream(file);
	    instance.store(outputStream, "");
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
    
	/**
     * 
     */
    private static final long serialVersionUID = 4729607947460578219L;

	/**
	 * Use a Vector to keep a copy of lines that are a comment or 'blank'
	 */
	public Vector lineData = new Vector(0, 1);

	/**
	 * Use a Vector to keep a copy of lines containing a key, i.e. they are a property.
	 */
	public Vector keyData = new Vector(0, 1);

	/**
	 * Load properties from the specified InputStream. 
	 * Overload the load method in Properties so we can keep comment and blank lines.
	 * @param   inStream   The InputStream to read.
	 */
	public void load(InputStream inStream) throws IOException
	{
		// The spec says that the file must be encoded using ISO-8859-1.
		BufferedReader reader =
		new BufferedReader(new InputStreamReader(inStream, "ISO-8859-1"));
		String line;

		while ((line = reader.readLine()) != null) {
			char c = 0;
			int pos = 0;
			// Leading whitespaces must be deleted first.
			while ( pos < line.length()
					&& Character.isWhitespace(c = line.charAt(pos))) {
				pos++;
			}

			// If empty line or begins with a comment character, save this line
			// in lineData and save a "" in keyData.
			if (	(line.length() - pos) == 0
					|| line.charAt(pos) == '#' || line.charAt(pos) == '!') {
				lineData.add(line);
				keyData.add("");
				continue;
			}

			// The characters up to the next Whitespace, ':', or '='
			// describe the key.  But look for escape sequences.
			// Try to short-circuit when there is no escape char.
			int start = pos;
			boolean needsEscape = line.indexOf('\\', pos) != -1;
			StringBuffer key = needsEscape ? new StringBuffer() : null;

			while ( pos < line.length()
					&& ! Character.isWhitespace(c = line.charAt(pos++))
					&& c != '=' && c != ':') {
				if (needsEscape && c == '\\') {
					if (pos == line.length()) {
						// The line continues on the next line.  If there
						// is no next line, just treat it as a key with an
						// empty value.
						line = reader.readLine();
						if (line == null)
							line = "";
						pos = 0;
						while ( pos < line.length()
								&& Character.isWhitespace(c = line.charAt(pos)))
							pos++;
					} else {
						c = line.charAt(pos++);
						switch (c) {
							case 'n':
								key.append('\n');
								break;
							case 't':
								key.append('\t');
								break;
							case 'r':
								key.append('\r');
								break;
							case 'u':
								if (pos + 4 <= line.length()) {
									char uni = (char) Integer.parseInt
											   (line.substring(pos, pos + 4), 16);
									key.append(uni);
									pos += 4;
								}   // else throw exception?
								break;
							default:
								key.append(c);
								break;
						}
					}
				} else if (needsEscape)
					key.append(c);
			}

			boolean isDelim = (c == ':' || c == '=');

			String keyString;
			if (needsEscape)
				keyString = key.toString();
			else if (isDelim || Character.isWhitespace(c))
				keyString = line.substring(start, pos - 1);
			else
				keyString = line.substring(start, pos);

			while ( pos < line.length()
					&& Character.isWhitespace(c = line.charAt(pos)))
				pos++;

			if (! isDelim && (c == ':' || c == '=')) {
				pos++;
				while ( pos < line.length()
						&& Character.isWhitespace(c = line.charAt(pos)))
					pos++;
			}

			// Short-circuit if no escape chars found.
			if (!needsEscape) {
				put(keyString, line.substring(pos));
				// Save a "" in lineData and save this
				// keyString in keyData.
				lineData.add("");
				keyData.add(keyString);
				continue;
			}

			// Escape char found so iterate through the rest of the line.
			StringBuffer element = new StringBuffer(line.length() - pos);
			while (pos < line.length()) {
				c = line.charAt(pos++);
				if (c == '\\') {
					if (pos == line.length()) {
						// The line continues on the next line.
						line = reader.readLine();

						// We might have seen a backslash at the end of
						// the file.  The JDK ignores the backslash in
						// this case, so we follow for compatibility.
						if (line == null)
							break;

						pos = 0;
						while ( pos < line.length()
								&& Character.isWhitespace(c = line.charAt(pos)))
							pos++;
						element.ensureCapacity(line.length() - pos +
											   element.length());
					} else {
						c = line.charAt(pos++);
						switch (c) {
							case 'n':
								element.append('\n');
								break;
							case 't':
								element.append('\t');
								break;
							case 'r':
								element.append('\r');
								break;
							case 'u':
								if (pos + 4 <= line.length()) {
									char uni = (char) Integer.parseInt
											   (line.substring(pos, pos + 4), 16);
									element.append(uni);
									pos += 4;
								}   // else throw exception?
								break;
							default:
								element.append(c);
								break;
						}
					}
				} else
					element.append(c);
			}
			put(keyString, element.toString());
			// Save a "" in lineData and save this
			// keyString in keyData.
			lineData.add("");
			keyData.add(keyString);
		}
	}

	/**
	 * Write the properties to the specified OutputStream.
	 * 
	 * Overloads the store method in Properties so we can put back comment	
	 * and blank lines.													  
	 * 
	 * @param out	The OutputStream to write to.
	 * @param header Ignored, here for compatability w/ Properties.
	 * 
	 * @exception IOException
	 */
	public void store(OutputStream out, String header) throws IOException
	{
		// The spec says that the file must be encoded using ISO-8859-1.
		PrintWriter writer
		= new PrintWriter(new OutputStreamWriter(out, "ISO-8859-1"));

		// We ignore the header, because if we prepend a commented header
		// then read it back in it is now a comment, which will be saved
		// and then when we write again we would prepend Another header...

		String line;
		String key;
		StringBuffer s = new StringBuffer ();

		for (int i=0; i<lineData.size(); i++) {
			line = (String) lineData.get(i);
			key = (String) keyData.get(i);
			if (key.length() > 0) {  // This is a 'property' line, so rebuild it
				formatForOutput (key, s, true);
				s.append ('=');
				formatForOutput ((String) get(key), s, false);
				writer.println (s);
			} else {  // was a blank or comment line, so just restore it
				writer.println (line);
			}
		} 
		writer.flush ();
	}

	/**
	 * Need this method from Properties because original code has StringBuilder,
	 * which is an element of Java 1.5, used StringBuffer instead (because
	 * this code was written for Java 1.4)
	 * 
	 * @param str	- the string to format
	 * @param buffer - buffer to hold the string
	 * @param key	- true if str the key is formatted, false if the value is formatted
	 */
	private void formatForOutput(String str, StringBuffer buffer, boolean key)
	{
		if (key) {
			buffer.setLength(0);
			buffer.ensureCapacity(str.length());
		} else
			buffer.ensureCapacity(buffer.length() + str.length());
		boolean head = true;
		int size = str.length();
		for (int i = 0; i < size; i++) {
			char c = str.charAt(i);
			switch (c) {
				case '\n':
					buffer.append("\\n");
					break;
				case '\r':
					buffer.append("\\r");
					break;
				case '\t':
					buffer.append("\\t");
					break;
				case ' ':
					buffer.append(head ? "\\ " : " ");
					break;
				case '\\':
				case '!':
				case '#':
				case '=':
				case ':':
					buffer.append('\\').append(c);
					break;
				default:
					if (c < ' ' || c > '~') {
						String hex = Integer.toHexString(c);
						buffer.append("\\u0000".substring(0, 6 - hex.length()));
						buffer.append(hex);
					} else
						buffer.append(c);
			}
			if (c != ' ')
				head = key;
		}
	}

	/**
	 * Add a Property to the end of the CommentedProperties. 
	 * 
	 * @param   keyString	 The Property key.
	 * @param   value		 The value of this Property.
	 */
	public void add(String keyString, String value)
	{
		put(keyString, value);
		lineData.add("");
		keyData.add(keyString);
	}

	/**
	 * Add a comment or blank line or comment to the end of the CommentedProperties. 
	 * 
	 * @param   line The string to add to the end, make sure this is a comment
	 *			   or a 'whitespace' line.
	 */
	public void addLine(String line)
	{
		lineData.add(line);
		keyData.add("");
	}
}

