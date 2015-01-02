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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.AccessDeniedException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.apache.struts2.ServletActionContext;

import com.gisgraphy.Constants;
import com.gisgraphy.model.User;
import com.gisgraphy.service.UserExistsException;
import com.gisgraphy.util.StringUtil;
import com.gisgraphy.webapp.util.RequestUtil;

/**
 * Action to allow new users to sign up.
 */
public class SignupAction extends BaseAction {
    private static final long serialVersionUID = 6558317334878272308L;

    private User user;

    private String cancel;

    public void setCancel(String cancel) {
	this.cancel = cancel;
    }

    public void setUser(User user) {
	this.user = user;
    }

    /**
     * Return an instance of the user - to display when validation errors occur
     * 
     * @return a populated user
     */
    public User getUser() {
	return user;
    }

    /**
     * When method=GET, "input" is returned. Otherwise, "success" is returned.
     * 
     * @return cancel, input or success
     */
    @Override
    public String execute() {
	if (cancel != null) {
	    return CANCEL;
	}
	if (ServletActionContext.getRequest().getMethod().equals("GET")) {
	    return INPUT;
	}
	return SUCCESS;
    }

    /**
     * Returns "input"
     * 
     * @return "input" by default
     */
    @Override
    public String doDefault() {
	return INPUT;
    }

    /**
     * Save the user, encrypting their passwords if necessary
     * 
     * @return success when good things happen
     * @throws Exception
     *                 when bad things happen
     */
    public String save() throws Exception {
	Boolean encrypt = (Boolean) getConfiguration().get(
		Constants.ENCRYPT_PASSWORD);

	if (encrypt != null && encrypt) {
	    String algorithm = (String) getConfiguration().get(
		    Constants.ENC_ALGORITHM);

	    if (algorithm == null) { // should only happen for test case
		if (log.isDebugEnabled()) {
		    log.debug("assuming testcase, setting algorithm to 'SHA'");
		}
		algorithm = "SHA";
	    }

	    user.setPassword(StringUtil.encodePassword(user.getPassword(),
		    algorithm));
	}

	user.setEnabled(true);

	// Set the default user role on this new user
	user.addRole(roleManager.getRole(Constants.USER_ROLE));

	try {
	    user = userManager.saveUser(user);
	} catch (AccessDeniedException ade) {
	    // thrown by UserSecurityAdvice configured in aop:advisor
	    // userManagerSecurity
	    log.warn(ade.getMessage());
	    getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
	    return null;
	} catch (UserExistsException e) {
	    log.warn(e.getMessage());
	    List<String> args = new ArrayList<String>();
	    args.add(user.getUsername());
	    args.add(user.getEmail());
	    addActionError(getText("errors.existing.user", args.toArray(new String[]{})));

	    // redisplay the unencrypted passwords
	    user.setPassword(user.getConfirmPassword());
	    return INPUT;
	}

	saveMessage(getText("user.registered"));
	getSession().setAttribute(Constants.REGISTERED, Boolean.TRUE);

	// log user in automatically
	UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
		user.getUsername(), user.getConfirmPassword(), user
			.getAuthorities());
	auth.setDetails(user);
	SecurityContextHolder.getContext().setAuthentication(auth);

	// Send an account information e-mail
	mailMessage.setSubject(getText("signup.email.subject"));
	sendUserMessage(user, getText("signup.email.message"), RequestUtil
		.getAppURL(getRequest()));

	return SUCCESS;
    }
}
