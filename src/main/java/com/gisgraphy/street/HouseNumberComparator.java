package com.gisgraphy.street;

import java.util.Comparator;

import com.gisgraphy.domain.geoloc.entity.HouseNumber;

/**
 * implement a house number comparator based on the house number base on alphanumcomparator
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class HouseNumberComparator implements Comparator<HouseNumber>{


	  private final boolean isDigit(char ch)
	    {
	        return ch >= 48 && ch <= 57;
	    }

	    /** Length of string is passed in for improved efficiency (only need to calculate it once) **/
	    private final String getChunk(String s, int slength, int marker)
	    {
	        StringBuilder chunk = new StringBuilder();
	        char c = s.charAt(marker);
	        chunk.append(c);
	        marker++;
	        if (isDigit(c))
	        {
	            while (marker < slength)
	            {
	                c = s.charAt(marker);
	                if (!isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        } else
	        {
	            while (marker < slength)
	            {
	                c = s.charAt(marker);
	                if (isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        }
	        return chunk.toString();
	    }

		public int compare(HouseNumber h1, HouseNumber h2) {
	    {
	    	if (h1==null){
	    		return 1;
	    	}
	    	if (h2==null){
	    		return -1;
	    	}
	       
	        String s1 = h1.getNumber();
	        String s2 = h2.getNumber();
	        if (s1==null){
	        	return 1;
	        }
	        if (s2==null){
	        	return -1;
	        }

	        int thisMarker = 0;
	        int thatMarker = 0;
	        int s1Length = s1.length();
	        int s2Length = s2.length();

	        while (thisMarker < s1Length && thatMarker < s2Length)
	        {
	            String thisChunk = getChunk(s1, s1Length, thisMarker);
	            thisMarker += thisChunk.length();

	            String thatChunk = getChunk(s2, s2Length, thatMarker);
	            thatMarker += thatChunk.length();

	            // If both chunks contain numeric characters, sort them numerically
	            int result = 0;
	            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0)))
	            {
	                // Simple chunk comparison by length.
	                int thisChunkLength = thisChunk.length();
	                result = thisChunkLength - thatChunk.length();
	                // If equal, the first different number counts
	                if (result == 0)
	                {
	                    for (int i = 0; i < thisChunkLength; i++)
	                    {
	                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
	                        if (result != 0)
	                        {
	                            return result;
	                        }
	                    }
	                }
	            } else
	            {
	                result = thisChunk.compareTo(thatChunk);
	            }

	            if (result != 0)
	                return result;
	        }

	        return s1Length - s2Length;
	    }
		}

}
