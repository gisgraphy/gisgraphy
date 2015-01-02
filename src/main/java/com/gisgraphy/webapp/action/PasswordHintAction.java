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

import com.gisgraphy.model.User;
import com.gisgraphy.webapp.util.RequestUtil;

/**
 * Action class to send password hints to registered users.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class PasswordHintAction extends BaseAction {
    private static final long serialVersionUID = -4037514607101222025L;

    private String username;

    /**
     * @param username
     *                The username to set.
     */
    public void setUsername(String username) {
	this.username = username;
    }

    /**
     * Execute sending the password hint via e-mail.
     * 
     * @return success if username works, input if not
     */
    @Override
    public String execute() {
	List<String> args = new ArrayList<String>();

	// ensure that the username has been sent
	if (username == null) {
	    log
		    .warn("Username not specified, notifying user that it's a required field.");

	    args.add(getText("user.username"));
	    addActionError(getText("errors.requiredField",args.toArray(new String[]{}) ));
	    return INPUT;
	}

	if (log.isDebugEnabled()) {
	    log.debug("Processing Password Hint...");
	}

	// look up the user's information
	try {
	    User user = userManager.getUserByUsername(username);
	    String hint = user.getPasswordHint();

	    if (hint == null || hint.trim().equals("")) {
		log.warn("User '" + username
			+ "' found, but no password hint exists.");
		addActionError(getText("login.passwordHint.missing"));
		return INPUT;
	    }

	    StringBuffer msg = new StringBuffer();
	    msg.append("Your password hint is: ").append(hint);
	    msg.append("\n\nLogin at: ").append(
		    RequestUtil.getAppURL(getRequest()));

	    mailMessage.setTo(user.getEmail());
	    String subject = '[' + getText("webapp.name") + "] "
		    + getText("user.passwordHint");
	    mailMessage.setSubject(subject);
	    mailMessage.setText(msg.toString());
	    mailEngine.send(mailMessage);

	    args.add(username);
	    args.add(user.getEmail());

	    saveMessage(getText("login.passwordHint.sent", args.toArray(new String[]{})));

	} catch (Exception e) {
	    log.warn("Username '" + username + "' not found in database.");
	    args.add(username);
	    addActionError(getText("login.passwordHint.error", args.toArray(new String[]{})));
	    return INPUT;
	}

	return SUCCESS;
    }
}
