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
/**
 *
 */
package com.gisgraphy.domain.repository;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.helper.EncodingHelper;
import com.gisgraphy.helper.PropertiesHelper;

/**
 * Abstract Test Case that takes care of beginning and rolling back a
 * transaction between each test. It also Configures spring to dependency inject
 * setters of the Unit tests. It can be inherited by integration tests, that
 * need a database and spring wiring.
 * 
 * @see AbstractDependencyInjectionSpringContextTests for more information
 */
public abstract class AbstractTransactionalTestCase extends
	AbstractDependencyInjectionSpringContextTests {

    protected boolean isTransactionNeeded() {
	return true;
    }

 

    // private final static String[] resources = { "classpath:/spring/*.xml",
    // "classpath:/springtest/*.xml" };

    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass())
     * from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    protected PlatformTransactionManager transactionManager;

    protected TransactionStatus txStatus = null;

    /**
     * ResourceBundle loaded from
     * src/test/resources/${package.name}/ClassName.properties (if exists)
     */
    protected ResourceBundle rb;

    /**
     * Default constructor - populates "rb" variable if properties file exists
     * for the class in src/test/resources. set autoWire=AUTOWIRE_BY_NAME;
     */
    public AbstractTransactionalTestCase() {
	super();
	this
		.setAutowireMode(AbstractDependencyInjectionSpringContextTests.AUTOWIRE_BY_NAME);
	// Since a ResourceBundle is not required for each class, just
	// do a simple check to see if one exists
	String className = this.getClass().getName();

	try {
	    rb = ResourceBundle.getBundle(className);
	} catch (MissingResourceException mre) {
	    // logger.info("No resource bundle found for: " + className);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {
	setAutowireMode(AUTOWIRE_BY_NAME);
	return Constants.APPLICATION_CONTEXT_NAMES_FOR_TEST;
    }

    /**
     * Begins a new transaction
     */
    @Override
    protected void onSetUp() throws Exception {
	super.onSetUp();
	EncodingHelper.setJVMEncodingToUTF8();
	DefaultTransactionDefinition def = new DefaultTransactionDefinition();
	def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

	if (isTransactionNeeded()) {
	    this.txStatus = this.transactionManager.getTransaction(def);
	}
	this.onSetUpInTransaction();

    }

    protected void onSetUpInTransaction() {

    }

    /**
     * Rolls back the current transaction
     */
    @Override
    protected void onTearDown() throws Exception {
	endTransaction();
    }

    /**
     * @throws Exception
     */
    public void endTransaction() throws Exception {
	super.onTearDown();
	this.onTearDownInTransaction();
	if (isTransactionNeeded()) {
	    this.transactionManager.rollback(this.txStatus);
	    // this.transactionManager.commit(this.txStatus);
	}
	this.txStatus = null;
    }

    protected void onTearDownInTransaction() {

    }

    @Required
    public void setTransactionManager(
	    PlatformTransactionManager transactionManager) {
	this.transactionManager = transactionManager;
    }

    /**
     * Utility method to populate a javabean-style object with values from a
     * Properties file
     * 
     * @param obj
     *                the model object to populate
     * @return Object populated object
     * @throws Exception
     *                 if BeanUtils fails to copy properly
     */
    @SuppressWarnings("unchecked")
    protected Object populate(Object obj) throws Exception {
	// loop through all the beans methods and set its properties from
	// its .properties file
	Map map = PropertiesHelper.convertBundleToMap(rb);
	BeanUtils.copyProperties(obj, map);

	return obj;
    }

    /**
     * Create a HibernateTemplate from the SessionFactory and call flush() and
     * clear() on it. Designed to be used after "save" methods in tests:
     * http://issues.appfuse.org/browse/APF-178.
     */
    protected void flush() {
	HibernateTemplate hibernateTemplate = new HibernateTemplate(
		(SessionFactory) applicationContext.getBean("sessionFactory"));
	hibernateTemplate.flush();
	hibernateTemplate.clear();
    }

}
