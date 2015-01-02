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

import org.junit.Test;
import org.subethamail.wiser.Wiser;

public class PasswordHintActionTest extends BaseActionTestCase {
    private PasswordHintAction passwordHintAction;

    public void setPasswordHintAction(PasswordHintAction action) {
	this.passwordHintAction = action;
    }

    @Test
    public void testExecute() throws Exception {
	// start SMTP Server
	Wiser wiser = new Wiser();
	wiser.setPort(2525);
	wiser.start();

	passwordHintAction.setUsername("user");
	assertEquals("success", passwordHintAction.execute());
	assertFalse(passwordHintAction.hasActionErrors());

	// verify an account information e-mail was sent
	wiser.stop();
	assertTrue(wiser.getMessages().size() == 1);

	// verify that success messages are in the request
	assertNotNull(passwordHintAction.getSession().getAttribute("messages"));
    }
}
