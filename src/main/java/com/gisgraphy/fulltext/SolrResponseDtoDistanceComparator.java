package com.gisgraphy.fulltext;

import java.util.Comparator;


public class SolrResponseDtoDistanceComparator implements Comparator<SolrResponseDto> {

    public int compare(SolrResponseDto o1, SolrResponseDto o2) {
	if (o1==null){
	    if (o2==null){
		return 0;
	    } else {
		return -1;
	    }
	}
	if (o2==null){
	    //o1 is not null so o2<o1
	    return 1;
	}
	Double distance1 = o1.getDistance();
	Double distance2 = o2.getDistance();
	if (distance1==null){
	    if (distance2==null){
		return 0;
	    } else {
		return -1;
	    }
	}
	if (distance2==null){
	    return 1;
	}
	if (distance1 > distance2)
	    return 1;
	else if (distance1 < distance2)
	    return -1;
	else
	    return 0;
    }

}
