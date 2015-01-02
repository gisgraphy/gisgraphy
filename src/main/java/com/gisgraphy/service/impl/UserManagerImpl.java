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
package com.gisgraphy.service.impl;

import java.util.List;

import javax.jws.WebService;
import javax.persistence.EntityExistsException;

import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import com.gisgraphy.dao.UserDao;
import com.gisgraphy.model.User;
import com.gisgraphy.service.UserExistsException;
import com.gisgraphy.service.UserManager;
import com.gisgraphy.service.UserService;

/**
 * Implementation of UserManager interface.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@WebService(serviceName = "UserService", endpointInterface = "com.gisgraphy.service.UserService")
public class UserManagerImpl extends UniversalManagerImpl implements
	UserManager, UserService {
    private UserDao dao;

    /**
     * Set the Dao for communication with the data layer.
     * 
     * @param dao
     *                the UserDao that communicates with the database
     */
    public void setUserDao(UserDao dao) {
	this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    public User getUser(String userId) {
	return dao.get(new Long(userId));
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getUsers(User user) {
	return dao.getUsers();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public User saveUser(User user) throws UserExistsException {
	// if new user, lowercase userId
	if (user.getVersion() == null) {
	    user.setUsername(user.getUsername().toLowerCase());
	}

	try {
	    return dao.saveUser(user);
	} catch (DataIntegrityViolationException e) {
	    log.warn(e.getMessage());
	    throw new UserExistsException("User '" + user.getUsername()
		    + "' already exists!");
	} catch (EntityExistsException e) { // needed for JPA
	    log.warn(e.getMessage());
	    throw new UserExistsException("User '" + user.getUsername()
		    + "' already exists!");
	}
    }

    /**
     * {@inheritDoc}
     */
    public void removeUser(String userId) {
	log.debug("removing user: " + userId);
	dao.remove(new Long(userId));
    }

    /**
     * {@inheritDoc}
     * 
     * @param username
     *                the login name of the human
     * @return User the populated user object
     * @throws UsernameNotFoundException
     *                 thrown when username not found
     */
    public User getUserByUsername(String username)
	    throws UsernameNotFoundException {
	return (User) dao.loadUserByUsername(username);
    }
}
