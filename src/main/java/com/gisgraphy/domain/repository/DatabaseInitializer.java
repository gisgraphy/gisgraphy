/**
 * 
 */
package com.gisgraphy.domain.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Do some initializing stuff on the database (create normalize_text function)
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Component
public class DatabaseInitializer implements InitializingBean {

	protected static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
	
	@Autowired
	private IDatabaseHelper databaseHelper;
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IDatabaseInitializer#init()
	 */
	public void afterPropertiesSet() throws Exception{
		if (!databaseHelper.isNormalize_textFunctionCreated()){
    		logger.error("The "+DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" does not exists, try to create it");
    		try {
				databaseHelper.createNormalize_textFunction();
			} catch (RuntimeException e) {
				throw new RuntimeException("The "+DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" is not created. This function is required for the streetsearch engine and We can not create it (maybe the SQL user haven't the rights to create the function) : "+e.getCause().getMessage(),e);
			}
			logger.info("The "+DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME+" has been created");
    	}
	}
	
}
