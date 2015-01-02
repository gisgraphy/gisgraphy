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

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.gisgraphy.domain.geoloc.entity.Language;

/**
 * Interface of data access object for {@link Language}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class LanguageDao extends GenericDao<Language, Long> implements
	ILanguageDao {

    /**
     * Default constructore
     */
    public LanguageDao() {
	super(Language.class);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ILanguageDao#findByIso639Alpha2Code(java.lang.String)
     */
    public Language getByIso639Alpha2Code(final String iso639Alpha2LanguageCode) {
	return (Language) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Language.class.getSimpleName()
				+ " as l where Iso639Alpha2LanguageCode= ?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, iso639Alpha2LanguageCode
				.toUpperCase());
			Language result = (Language) qry.uniqueResult();

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ILanguageDao#findByIso639Alpha3Code(java.lang.String)
     */
    public Language getByIso639Alpha3Code(final String iso639Alpha3Code) {
	return (Language) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Language.class.getSimpleName()
				+ " as l where Iso639Alpha3LanguageCode= ?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, iso639Alpha3Code.toUpperCase());
			Language result = (Language) qry.uniqueResult();

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ILanguageDao#getByIso639Code(java.lang.String)
     */
    public Language getByIso639Code(String iso639LanguageCode) {
	if (iso639LanguageCode == null) {
	    throw new IllegalArgumentException(
		    "can not retrieve language with iso639Code="
			    + iso639LanguageCode);
	} else if (iso639LanguageCode.length() == 2) {
	    return getByIso639Alpha2Code(iso639LanguageCode);
	} else if (iso639LanguageCode.length() == 3) {
	    return getByIso639Alpha3Code(iso639LanguageCode);
	} else {
	    throw new IllegalArgumentException(
		    "can not retrieve language with iso639Code="
			    + iso639LanguageCode
			    + " : iso639LanguageCode must have a lenght of 2 or 3");
	}
    }

}
