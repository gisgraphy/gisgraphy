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

import org.apache.commons.logging.Log;
import org.apache.struts2.ServletActionContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.domain.valueobject.Constants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Base class for running Struts 2 Action tests.
 * 
 * @author mraible
 */
public abstract class BaseActionTestCase extends AbstractTransactionalTestCase {
    protected transient final Log log = logger;

    JavaMailSenderImpl mailSender;

    @SuppressWarnings("unchecked")
    @Override
    protected void onSetUpInTransaction() {
	LocalizedTextUtil.addDefaultResourceBundle(Constants.BUNDLE_KEY);
	// ActionContext.getContext().setSession(new HashMap<Object, Object>());

	// change the port on the mailSender so it doesn't conflict with an
	// existing SMTP server on localhost
	// JavaMailSenderImpl mailSender = (JavaMailSenderImpl)
	// applicationContext.getBean("mailSender");
	mailSender.setPort(2525);
	mailSender.setHost("localhost");

	// populate the request so getRequest().getSession() doesn't fail in
	// BaseAction.java

	setUpActionContext();
    }

	public static void setUpActionContext() {
		ConfigurationManager configurationManager = new ConfigurationManager();
		configurationManager
			.addContainerProvider(new XWorkConfigurationProvider());
		Configuration config = configurationManager.getConfiguration();
		Container container = config.getContainer();

		ValueStack stack = container.getInstance(ValueStackFactory.class)
			.createValueStack();
		stack.getContext().put(ActionContext.CONTAINER, container);
		ActionContext.setContext(new ActionContext(stack.getContext()));
		ServletActionContext.setRequest(new MockHttpServletRequest());
	}

    @Override
    protected void onTearDownInTransaction() {
	ActionContext.getContext().setSession(null);
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
	this.mailSender = mailSender;
    }
}
