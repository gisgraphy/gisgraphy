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
package com.gisgraphy.webapp.action;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;

import org.apache.struts2.ServletActionContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.opensymphony.xwork2.Preparable;

/**
 * Action for facilitating User Management feature.
 */
public class LoginAction extends BaseAction implements Preparable {
    private static final long serialVersionUID = 6776558938712115191L;



    private String username;
    private String password;
    
    /**
     * Grab the entity from the database before populating with request
     * parameters
     */
    public void prepare() {
    }
    @Override
    public String execute() throws Exception {
    	super.execute();
 System.out.println("YEAHH"+SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
 SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
 SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        Principal principal = ServletActionContext.getRequest().getUserPrincipal();
        /*ServletActionContext.getRequest().getParameterMap();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("username: " + userDetails.getUsername());
        System.out.println("password: " + userDetails.getPassword());
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) userDetails.getAuthorities();
        for (Iterator it = authorities.iterator(); it.hasNext();) {
            SimpleGrantedAuthority authority = (SimpleGrantedAuthority) it.next();
            System.out.println("Role: " + authority.getAuthority());
        }
 */
        return "success";
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public String getPassword() {
        return password;
    }

   
}
