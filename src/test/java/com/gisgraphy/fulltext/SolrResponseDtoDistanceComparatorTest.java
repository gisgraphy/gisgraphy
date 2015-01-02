package com.gisgraphy.fulltext;

import net.sf.jstester.util.Assert;

import org.junit.Before;
import org.junit.Test;



public class SolrResponseDtoDistanceComparatorTest {
    private SolrResponseDto o1;
    private SolrResponseDto o2; 
    private SolrResponseDto o1WithNullDist;
    private SolrResponseDto o2WithNullDist; 
    private SolrResponseDtoDistanceComparator comparator = new SolrResponseDtoDistanceComparator();
   
    @Before
    public void setup(){
	o1 = new SolrResponseDto();
	o1.setDistance(1D);
	o2 = new SolrResponseDto();
	o2.setDistance(2D);
	o2WithNullDist = new SolrResponseDto();
	o1WithNullDist = new SolrResponseDto();
    }
    
    @Test
    public void compareWithNullObject(){
	Assert.assertEquals(0, comparator.compare(null, null));
	Assert.assertEquals(-1, comparator.compare(null, o1));
	Assert.assertEquals(1, comparator.compare(o1, null));
    }
    
    @Test
    public void compareWithNullDistance(){
	Assert.assertEquals(0, comparator.compare(o1WithNullDist, o2WithNullDist));
	Assert.assertEquals(-1, comparator.compare(null, o1WithNullDist));
	Assert.assertEquals(1, comparator.compare(o1WithNullDist, null));
    }
    
    @Test
    public void compareWithNotNullDistance(){
	Assert.assertEquals(0, comparator.compare(o1, o1));
	Assert.assertEquals(-1, comparator.compare(o1, o2));
	Assert.assertEquals(1, comparator.compare(o2, o1));
    }
}
