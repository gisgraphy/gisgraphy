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
package com.gisgraphy.domain.geoloc.entity;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;

public class EntityTest  {

    @Test
    public void CheckAllEntitiesHasTheirPlacetype() {
    	Reflections reflections = new Reflections("com.gisgraphy.domain.geoloc.entity");

    	 Set<Class<? extends GisFeature>> allClasses = 
    	     reflections.getSubTypesOf(GisFeature.class);
    	 
    	 System.out.println("Found "+allClasses.size()+" entities that extends GisFeature");
    	 for (Class c: allClasses){
    		 System.out.println(c.getSimpleName());
    		 try {
				Class placetype = Class.forName("com.gisgraphy.domain.placetype."+c.getSimpleName());
			} catch (ClassNotFoundException e) {
				Assert.fail("com.gisgraphy.domain.placetype."+c.getSimpleName()+" doesn't exists, each entities should have their placetype");
			}
    	 }
    }
	
	@Test
    public void CheckAllEntitiesHasTheirDao() {
    	Reflections reflections = new Reflections("com.gisgraphy.domain.geoloc.entity");

    	 Set<Class<? extends GisFeature>> allClasses = 
    	     reflections.getSubTypesOf(GisFeature.class);
    	 
    	 System.out.println("Found "+allClasses.size()+" entities that extends GisFeature");
    	 for (Class c: allClasses){
    		 System.out.println(c);
    		 try {
				Class placetype = Class.forName("com.gisgraphy.domain.repository."+c.getSimpleName()+"Dao");
			} catch (ClassNotFoundException e) {
				Assert.fail("com.gisgraphy.domain.repository."+c.getSimpleName()+"Dao doesn't exists, each entities should have their Dao");
			}
    	 }
    }

}
