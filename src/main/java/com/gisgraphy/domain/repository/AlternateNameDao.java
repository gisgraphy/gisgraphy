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
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.gisgraphy.domain.geoloc.entity.AlternateName;

/**
 * A data access object for {@link AlternateName}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class AlternateNameDao extends GenericDao<AlternateName, Long> implements
	IAlternateNameDao {

    /**
     * Default constructor
     */
    public AlternateNameDao() {
	super(AlternateName.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAlternateNameDao#getUsedLanguages()
     */
    @SuppressWarnings("unchecked")
    public List<String> getUsedLanguagesCodes() {
	return (List<String>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "select distinct(a.language) from AlternateName a where a.language != null";

			final Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			List<String> result = (List<String>) qry.list();
			if (result == null) {
			    result = new ArrayList<String>();
			}

			return result;
		    }
		});

    }

}
