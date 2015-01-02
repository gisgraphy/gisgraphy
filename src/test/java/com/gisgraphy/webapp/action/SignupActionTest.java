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

import org.springframework.security.context.SecurityContextHolder;
import org.apache.struts2.ServletActionContext;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.subethamail.wiser.Wiser;

import com.gisgraphy.Constants;
import com.gisgraphy.model.Address;
import com.gisgraphy.model.User;

public class SignupActionTest extends BaseActionTestCase {
    private SignupAction signupAction;

    public void setSignupAction(SignupAction action) {
	this.signupAction = action;
    }

    @Test
    public void testDisplayForm() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest(null,
		"GET", "/signup.html");
	ServletActionContext.setRequest(request);
	assertEquals("input", signupAction.execute());
    }

    @Test
    public void testExecute() throws Exception {
	User user = new User();
	user.setUsername("self-registered");
	user.setPassword("Password1");
	user.setConfirmPassword("Password1");
	user.setFirstName("First");
	user.setLastName("Last");

	Address address = new Address();
	address.setCity("Denver");
	address.setProvince("CO");
	address.setCountry("USA");
	address.setPostalCode("80210");
	user.setAddress(address);

	user.setEmail("self-registered@raibledesigns.com");
	user.setWebsite("http://raibledesigns.com");
	user.setPasswordHint("Password is one with you.");
	signupAction.setUser(user);

	// set mock response so setting cookies doesn't fail
	ServletActionContext.setResponse(new MockHttpServletResponse());

	// start SMTP Server
	Wiser wiser = new Wiser();
	wiser.setPort(2525);
	wiser.start();

	assertEquals("success", signupAction.save());
	assertFalse(signupAction.hasActionErrors());

	// verify an account information e-mail was sent
	wiser.stop();
	assertTrue(wiser.getMessages().size() == 1);

	// verify that success messages are in the session
	assertNotNull(signupAction.getSession().getAttribute(
		Constants.REGISTERED));

	SecurityContextHolder.getContext().setAuthentication(null);
    }
}
