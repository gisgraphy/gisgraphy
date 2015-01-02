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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.repository.exception.DuplicateNameException;

/**
 * A data access object for {@link Country}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class CountryDao extends GenericGisDao<Country> implements ICountryDao {

    /**
     * Default Constructor
     */
    public CountryDao() {
	super(Country.class);
    }

    /**
     * @param country
     *                the country to save
     * @return The saved instance
     * @throws DuplicateNameException
     *                 If a country with the same name already exists in the
     *                 datastore
     */
    @Override
    public Country save(Country country) {
	if (country != null && country.getId() == null
		&& getByName(country.getName()) != null) {
	    throw new DuplicateNameException(
		    "A country with the specified name already exists : "
			    + country.getName());
	}
	return super.save(country);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ICountryDao#getByIso3166Alpha2Code(java.lang.String)
     */
    public Country getByIso3166Alpha2Code(final String iso3166Alpha2Code) {
	if (iso3166Alpha2Code == null) {
	    return null;
	}
	if (iso3166Alpha2Code.length() != 2) {
	    logger
		    .info("can not retrieve country with iso639Alpha2LanguageCode="
			    + iso3166Alpha2Code
			    + " : iso639Alpha2LanguageCode must have a length of 2 ");
	    return null;
	}
	return (Country) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Country.class.getSimpleName()
				+ " as c where c.iso3166Alpha2Code= ?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, iso3166Alpha2Code.toUpperCase());
			Country result = (Country) qry.uniqueResult();

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ICountryDao#getByIso3166Alpha3Code(java.lang.String)
     */
    public Country getByIso3166Alpha3Code(final String iso3166Alpha3Code) {
	if (iso3166Alpha3Code == null) {
	    return null;
	}
	if (iso3166Alpha3Code.length() != 3) {
	    logger.info("can not retrieve country with iso3166Alpha3Code="
		    + iso3166Alpha3Code
		    + " : iso3166Alpha3Code must have a length of 3 ");
	    return null;
	}
	return (Country) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Country.class.getSimpleName()
				+ " as c where c.iso3166Alpha3Code= ?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, iso3166Alpha3Code.toUpperCase());
			Country result = (Country) qry.uniqueResult();

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ICountryDao#getByIso3166Code(java.lang.String)
     */
    public Country getByIso3166Code(String iso3166Code) {
	if (iso3166Code == null) {
	    return null;
	}
	if (iso3166Code.length() == 2) {
	    return getByIso3166Alpha2Code(iso3166Code);
	} else if (iso3166Code.length() == 3) {
	    return getByIso3166Alpha3Code(iso3166Code);
	} else {
	    logger.info("can not retrieve country with iso3166Code="
		    + iso3166Code
		    + " : iso3166Code must have a length of 2 or 3");
	    return null;
	}
    }

    // TODO v2 getneighboors and set neighboors en post import des polygon de
    // country
    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ICountryDao#getByName(java.lang.String)
     */
    public Country getByName(final String name) {
	if (name == null) {
	    return null;
	}
	return (Country) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Country.class.getSimpleName()
				+ " as c where c.name= ?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, name);
			Country result = (Country) qry.uniqueResult();

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericDao#deleteAll()
     */
    @Override
    public int deleteAll() {
	List<Country> all = getAll();
	int deleted = all.size();
	if (deleted != 0) {
	    super.deleteAll(all);
	    super.flushAndClear();
	}
	flushAndClear();
	return deleted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ICountryDao#getAllSortedByName()
     */
    @SuppressWarnings("unchecked")
    public List<Country> getAllSortedByName() {
	return (List<Country>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ persistentClass.getSimpleName()
				+ " order by name";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);
			List<Country> results = (List<Country>) qry.list();
			if (results == null) {
			    results = new ArrayList<Country>();
			}
			return results;
		    }
		});

    }

    @SuppressWarnings("unchecked")
	public List<Long> listFeatureIds() {
		return ((List<Long>) this.getHibernateTemplate().execute(
			new HibernateCallback() {

			    public Object doInHibernate(final Session session)
				    throws PersistenceException {
				final String queryString = "select featureId from "
					+ persistentClass.getSimpleName();

				final Query qry = session.createQuery(queryString);
				qry.setCacheable(false);
				return qry.list();

			    }
			}));
	}

}
