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
package com.gisgraphy.domain.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.geoloc.ZipcodeNormalizer;

/**
 * A data access object for {@link ZipCode} Object
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class ZipCodeDao extends GenericDao<ZipCode, Long> implements IZipCodeDao
{
    
    //CREATE INDEX communes_idxr ON zipcode("code" text_pattern_ops);
	
   /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(ZipCodeDao.class);
	
    /**
     * Default constructor
     */
    public ZipCodeDao() {
	    super(ZipCode.class);
    }
    


	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IZipCodeDao#getByCodeAndCountry(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<ZipCode> getByCodeAndCountry(final String code, final String countryCode) {
		Assert.notNull(code);
		Assert.notNull(countryCode);
		return (List<ZipCode>) this.getHibernateTemplate().execute(
			new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				String queryString = "from "
					+ getPersistenceClass().getSimpleName()
					+ " as z where z.code= ? and z.gisFeature.countryCode= ?";
				

				Query qry = session.createQuery(queryString);
				qry.setCacheable(true);

				qry.setParameter(0, code.toUpperCase());
				qry.setParameter(1, countryCode.toUpperCase());

				List<ZipCode> result = (List<ZipCode>) qry.list();
				return result==null? new ArrayList<ZipCode>():result;
			    }
			});
	}
	
	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IZipCodeDao#getByCodeAndCountry(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<ZipCode> getByCodeAndCountrySmart(final String code, final String countryCode) {
		Assert.notNull(code);
		Assert.notNull(countryCode);
		String modifyCode = ZipcodeNormalizer.normalize(code, countryCode);
		 return getByCodeAndCountry(modifyCode, countryCode);
		 /*   return (List<ZipCode>) this.getHibernateTemplate().execute(
			    new HibernateCallback() {
				
				public Object doInHibernate(Session session)
				throws PersistenceException {
				    String queryString = "from "
					+ getPersistenceClass().getSimpleName()
					+ " as z where code like ?  and z.gisFeature.countryCode= ?";
				    
				    
				    Query qry = session.createQuery(queryString);
				    qry.setCacheable(true);
				    
				    qry.setParameter(0, code.trim().split(" ")[0].toUpperCase());
				    qry.setParameter(1, countryCode.toUpperCase());
				    
				    List<ZipCode> result = (List<ZipCode>) qry.list();
				    return result==null? new ArrayList<ZipCode>():result;
				}
			    });
		} else {
		    return getByCodeAndCountry(code, countryCode);
		}*/
	}



	/* (non-Javadoc)
	 * @see com.gisgraphy.domain.repository.IZipCodeDao#listByCode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<ZipCode> listByCode(final String code) {
		Assert.notNull(code);
		return (List<ZipCode>) this.getHibernateTemplate().execute(
			new HibernateCallback() {

			    public Object doInHibernate(Session session)
				    throws PersistenceException {
				String queryString = "from "
					+ getPersistenceClass().getSimpleName()
					+ " as z where z.code= ?";
				

				Query qry = session.createQuery(queryString);
				qry.setCacheable(true);

				qry.setParameter(0, code.toUpperCase());

				List<ZipCode> result = (List<ZipCode>) qry.list();
				return result==null? new ArrayList<ZipCode>():result;
			    }
			});
	}

    
  


	
}
