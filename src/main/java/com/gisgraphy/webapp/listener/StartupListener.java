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
package com.gisgraphy.webapp.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.rememberme.RememberMeAuthenticationProvider;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gisgraphy.Constants;
import com.gisgraphy.service.LookupManager;

/**
 * <p>
 * StartupListener class used to initialize and database settings and populate
 * any application-wide drop-downs. <p/>
 * <p>
 * Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the GenericDao or service level to
 * avoid LazyInitializationException. Hibernate.initialize() works well for
 * doing this.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListener implements ServletContextListener {
    private static final Log log = LogFactory.getLog(StartupListener.class);

    @SuppressWarnings( { "unchecked" })
    public void contextInitialized(ServletContextEvent event) {
	log.debug("initializing context...");

	ServletContext context = event.getServletContext();

	// Orion starts Servlets before Listeners, so check if the config
	// object already exists
	Map<String, Object> config = (HashMap<String, Object>) context
		.getAttribute(Constants.CONFIG);

	if (config == null) {
	    config = new HashMap<String, Object>();
	}

	if (context.getInitParameter(Constants.CSS_THEME) != null) {
	    config.put(Constants.CSS_THEME, context
		    .getInitParameter(Constants.CSS_THEME));
	}

	ApplicationContext ctx = WebApplicationContextUtils
		.getRequiredWebApplicationContext(context);

	boolean encryptPassword = true;
	
    try {
        ProviderManager provider = (ProviderManager) ctx.getBean(ctx.getBeanNamesForType(ProviderManager.class)[0]);
        for (Object o : provider.getProviders()) {
            AuthenticationProvider p = (AuthenticationProvider) o;
            if (p instanceof RememberMeAuthenticationProvider) {
                config.put("rememberMeEnabled", Boolean.TRUE);
            } 
            config.put(Constants.ENCRYPT_PASSWORD, Boolean.TRUE);
            config.put(Constants.ENC_ALGORITHM, "SHA");
        }
    } catch (NoSuchBeanDefinitionException n) {
        log.debug("authenticationManager bean not found, assuming test and ignoring...");
        // ignore, should only happen when testing
    }
	
	
	context.setAttribute(Constants.CONFIG, config);

	// output the retrieved values for the Init and Context Parameters
	if (log.isDebugEnabled()) {
	    log
		    .debug("Remember Me Enabled? "
			    + config.get("rememberMeEnabled"));
	    log.debug("Encrypt Passwords? " + encryptPassword);
	    if (encryptPassword) {
		log.debug("Encryption Algorithm: "
			+ config.get(Constants.ENC_ALGORITHM));
	    }
	    log.debug("Populating drop-downs...");
	}

	setupContext(context);
    }

    /**
     * This method uses the LookupManager to lookup available roles from the
     * data layer.
     * 
     * @param context
     *                The servlet context
     */
    public static void setupContext(ServletContext context) {
	ApplicationContext ctx = WebApplicationContextUtils
		.getRequiredWebApplicationContext(context);
	LookupManager mgr = (LookupManager) ctx.getBean("lookupManager");

	// get list of possible roles
	context.setAttribute(Constants.AVAILABLE_ROLES, mgr.getAllRoles());
	log.debug("Drop-down initialization complete [OK]");
    }

    /**
     * Shutdown servlet context (currently a no-op method).
     * 
     * @param servletContextEvent
     *                The servlet context event
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
	// LogFactory.release(Thread.currentThread().getContextClassLoader());
	// Commented out the above call to avoid warning when SLF4J in
	// classpath.
	// WARN: The method class
	// org.apache.commons.logging.impl.SLF4JLogFactory#release() was
	// invoked.
	// WARN: Please see http://www.slf4j.org/codes.html for an explanation.
    }
}
