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

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.gisgraphy.stats.StatsUsage;
import com.gisgraphy.stats.StatsUsageType;

/**
 * Dao for {@link StatsUsage}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
@Repository
public class StatsUsageDao extends GenericDao<StatsUsage, Long> implements
	IStatsUsageDao {

    /**
     * Default Constructor
     */
    public StatsUsageDao() {
	super(StatsUsage.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IStatUsageDao#getByUsageType(com.gisgraphy.stats.StatsUsageType)
     */
    public StatsUsage getByUsageType(final StatsUsageType statsUsageType) {
	return (StatsUsage) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "from StatsUsage as s where s.statsUsageType = :statstype";

			final Query qry = session.createQuery(queryString);
			qry.setParameter("statstype", statsUsageType);
			qry.setCacheable(true);

			return (StatsUsage) qry.uniqueResult();
		    }
		});
    }

}
