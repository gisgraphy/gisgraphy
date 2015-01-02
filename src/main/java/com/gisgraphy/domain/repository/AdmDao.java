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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.GisFeature;

/**
 * A data access object for {@link Adm}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Repository
public class AdmDao extends GenericGisDao<Adm> implements IAdmDao {

    public AdmDao() {
	super(Adm.class);
    }
    
    @Override
    public Adm get(final Long id) {
	Assert.notNull(id, "Can not retrieve an Ogject with a null id");
	Adm returnValue = null;
	try {
	    return (Adm) this.getHibernateTemplate().execute(
		    new HibernateCallback() {

			public Object doInHibernate(Session session)
				throws PersistenceException {
			    String queryString = "from "
				    + persistentClass.getSimpleName()
				    + " o where o.id=" + id;

			    Query qry = session.createQuery(queryString);
			    qry.setCacheable(true);
			    return (Adm) qry.uniqueResult();

			}
		    });
	} catch (Exception e) {
	    logger.info("could not retrieve object of type "
		    + persistentClass.getSimpleName() + " with id " + id, e);
	}
	return returnValue;
    }

    
    
    /**
     * Check that codes are consistent according the level (see
     * {@link Adm#isConsistentForLevel() } and save it in the datastore
     * 
     * @param adm
     *                The Adm to save
     * @return the saved instance
     * @see Adm#isConsistentForLevel()
     */
    @Override
    public Adm save(Adm adm) {
	if (!adm.isConsistentForLevel()) {
	    throw new RuntimeException(
		    "Level and AdmXCode are inconsistant for " + adm.toString());
	}
	return super.save(adm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAdm1(java.lang.String,
     *      java.lang.String)
     */
    public Adm getAdm1(final String countryCode, final String adm1Code) {
	Assert.notNull(countryCode);
	Assert.notNull(adm1Code);
	return (Adm) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where a.countryCode = ? and a.adm1Code= ? and a.level=1";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, countryCode.toUpperCase());
			qry.setParameter(1, adm1Code);

			Adm result = (Adm) qry.uniqueResult();

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAdm2(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Adm getAdm2(final String countryCode, final String adm1Code,
	    final String adm2Code) {
	Assert.notNull(countryCode);
	Assert.notNull(adm1Code);
	Assert.notNull(adm2Code);

	return (Adm) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where a.countryCode=? and a.adm2Code=?";
			if (!"00".equals(adm1Code)) {
			    queryString += "and a.adm1Code= ?";
			}
			queryString += " and a.level=2";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, countryCode.toUpperCase());
			qry.setParameter(1, adm2Code);
			if (!"00".equals(adm1Code)) {
			    qry.setParameter(2, adm1Code);
			}

			Adm result;
			try {
			    result = (Adm) qry.uniqueResult();
			} catch (HibernateException e) {
			    if (!"00".equals(adm1Code)) {
				throw e;
			    } else {
				logger
					.error("Can not retrieve Adm for countrycode="
						+ countryCode
						+ " and adm2code="
						+ adm2Code
						+ " in flex mode : result is ambiguous");
				return null;
			    }
			}

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAdm3(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Adm getAdm3(final String countryCode, final String adm1Code,
	    final String adm2Code, final String adm3Code) {
	Assert.notNull(countryCode);
	Assert.notNull(adm1Code);
	Assert.notNull(adm2Code);
	Assert.notNull(adm3Code);
	return (Adm) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where a.countryCode=? and a.adm2Code=? and a.adm3Code=?";
			if (!"00".equals(adm1Code)) {
			    queryString += "and a.adm1Code= ?";
			}
			queryString += " and a.level=3";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, countryCode.toUpperCase());
			qry.setParameter(1, adm2Code);
			qry.setParameter(2, adm3Code);

			if (!"00".equals(adm1Code)) {
			    qry.setParameter(3, adm1Code);
			}

			Adm result;
			try {
			    result = (Adm) qry.uniqueResult();
			} catch (HibernateException e) {
			    if (!"00".equals(adm1Code)) {
				throw e;
			    } else {
				logger
					.error("Can not retrieve Adm for countrycode="
						+ countryCode
						+ " and adm2code="
						+ adm2Code
						+ " and adm3code="
						+ adm3Code
						+ " in flex mode : result is ambiguous");
				return null;
			    }
			}

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAdm4(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Adm getAdm4(final String countryCode, final String adm1Code,
	    final String adm2Code, final String adm3Code, final String adm4Code) {
	Assert.notNull(countryCode);
	Assert.notNull(adm1Code);
	Assert.notNull(adm2Code);
	Assert.notNull(adm3Code);
	Assert.notNull(adm4Code);

	return (Adm) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where a.countryCode=? and a.adm2Code=? and a.adm3Code=? and a.adm4Code=?";
			if (!"00".equals(adm1Code)) {
			    queryString += "and a.adm1Code= ?";
			}
			queryString += " and a.level=4";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, countryCode.toUpperCase());
			qry.setParameter(1, adm2Code);
			qry.setParameter(2, adm3Code);
			qry.setParameter(3, adm4Code);

			if (!"00".equals(adm1Code)) {
			    qry.setParameter(4, adm1Code);
			}

			Adm result;
			try {
			    result = (Adm) qry.uniqueResult();
			} catch (HibernateException e) {
			    if (!"00".equals(adm1Code)) {
				throw e;
			    } else {
				logger
					.error("Can not retrieve Adm for countrycode="
						+ countryCode
						+ " and adm2code="
						+ adm2Code
						+ " and adm3code="
						+ adm3Code
						+ " and adm4code="
						+ adm4Code
						+ " in flex mode : result is ambiguous");
				return null;
			    }
			}

			return result;
		    }
		});
    }

    private boolean isAdmCodeEmpty(String admCode) {
	if (admCode == null || admCode.trim().equals("")) {
	    return true;
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAdm(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Adm getAdm(final String countryCode, final String adm1Code,
	    final String adm2Code, final String adm3Code, final String adm4Code) {
	Assert.notNull(countryCode);
	if (!isAdmCodeEmpty(countryCode)) {
	    if (!isAdmCodeEmpty(adm1Code)) {
		if (!isAdmCodeEmpty(adm2Code)) {
		    if (!isAdmCodeEmpty(adm3Code)) {
			if (!isAdmCodeEmpty(adm4Code)) {
			    // adm1,adm2,adm3,adm4
			    return getAdm4(countryCode, adm1Code, adm2Code,
				    adm3Code, adm4Code);
			} else {
			    // adm1,adm2,adm3,null
			    return getAdm3(countryCode, adm1Code, adm2Code,
				    adm3Code);
			}
		    } else {
			// adm1,Adm2, null..
			return getAdm2(countryCode, adm1Code, adm2Code);
		    }
		} else {
		    // adm1,null...
		    return getAdm1(countryCode, adm1Code);
		}
	    } else {
		logger.info("Can not retrieve an adm if Adm1code is null");
		// if adm1 is empty can not retrieve any Adm
		return null;
	    }
	} else {
	    return null;
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getUnused()
     */
    @SuppressWarnings("unchecked")
    public List<Adm> getUnused() {
	return (List<Adm>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where a not in (select distinct(g.adm) from GisFeature g)";

			final Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			List<Adm> result = qry.list();
			if (result == null) {
			    result = new ArrayList<Adm>();
			}

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.GenericGisDao#getDirty()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Adm> getDirties() {
	return (List<Adm>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where a.featureId < 0";

			final Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			List<Adm> result = qry.list();
			if (result == null) {
			    result = new ArrayList<Adm>();
			}

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAllbyLevel(java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public List<Adm> getAllbyLevel(final int level) {
	return (List<Adm>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where a.level=?";

			final Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, level);

			List<Adm> result = qry.list();
			if (result == null) {
			    result = new ArrayList<Adm>();
			}

			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#countByLevel(int)
     */
    public long countByLevel(final int level) {
	// level is of type int =>could not be null
	return ((Long) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "select count(*) from "
				+ Adm.class.getSimpleName()
				+ " a where a.level=? ";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);
			qry.setParameter(0, level);

			Long result = (Long) qry.uniqueResult();
			return result;
		    }
		})).longValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAdmByCountryAndCodeAndLevel(java.lang.String,
     *      java.lang.String, int)
     */
    @SuppressWarnings("unchecked")
    public List<Adm> getAdmByCountryAndCodeAndLevel(final String countryCode,
	    final String admCode, final int level) {
	Assert.notNull(countryCode);
	Assert.notNull(admCode);
	// level is a int =>can not be null
	return (List<Adm>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "from "
				+ Adm.class.getSimpleName()
				+ " as a where  a.countryCode= ? and a.adm"
				+ level + "Code = ?  and a.level=?";

			Query qry = session.createQuery(queryString);
			qry.setCacheable(true);

			qry.setParameter(0, countryCode.toUpperCase());
			qry.setParameter(1, admCode);
			qry.setParameter(2, level);

			List<Adm> result = (List<Adm>) qry.list();
			if (result == null) {
			    return new ArrayList<Adm>();
			}
			return result;
		    }
		});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#getAdmOrFirstValidParentIfNotFound(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Adm getAdmOrFirstValidParentIfNotFound(final String countryCode,
	    final String adm1Code, final String adm2Code,
	    final String adm3Code, final String adm4Code) {
	Assert.notNull(countryCode);
	Adm adm = null;
	String adm1CodeTemp = adm1Code;
	String adm2CodeTemp = adm2Code;
	String adm3CodeTemp = adm3Code;
	String adm4CodeTemp = adm4Code;
	do {
	    adm = getAdm(countryCode, adm1CodeTemp, adm2CodeTemp, adm3CodeTemp,
		    adm4CodeTemp);
	    if (adm == null) {
		// downgrade the admvalue to search a lower level
		if (adm4CodeTemp != null) {
		    adm4CodeTemp = null;
		    continue;
		} else if (adm3CodeTemp != null) {
		    adm3CodeTemp = null;
		    continue;
		} else if (adm2CodeTemp != null) {
		    adm2CodeTemp = null;
		    continue;
		} else {
		    return getAdm1(countryCode, adm1Code);
		}
	    } else {
		return adm;
	    }
	} while (adm == null);
	return adm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.IAdmDao#suggestMostAccurateAdm(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, com.gisgraphy.domain.geoloc.entity.GisFeature)
     */
    public Adm suggestMostAccurateAdm(String countryCode, String adm1Code,
	    String adm2Code, String adm3Code, String adm4Code,
	    GisFeature gisfeature) {
	Assert.notNull(countryCode);
	logger.debug("try to find ADM for " + adm1Code + "." + adm2Code + "."
		+ adm3Code + "." + adm4Code);
	Adm adm = getAdm(countryCode.toUpperCase(), adm1Code, adm2Code,
		adm3Code, adm4Code);
	if (adm == null) {
	    // must get the most accurate adm
	    String lowestNotNullAdmCode = getLowestNotNullAdmCode(adm1Code,
		    adm2Code, adm3Code, adm4Code);
	    int level = Adm.getProcessedLevelFromCodes(adm1Code, adm2Code,
		    adm3Code, adm4Code);

	    if (lowestNotNullAdmCode != null && level != 0) {
		List<Adm> levelAdms = getAdmByCountryAndCodeAndLevel(
			countryCode.toUpperCase(), lowestNotNullAdmCode, level);
		Adm levelAdm = null;
		if (levelAdms.size() == 1) {
		    levelAdm = levelAdms.get(0);
		}
		Adm nearestParentAdm = getAdmOrFirstValidParentIfNotFound(
			countryCode.toUpperCase(), adm1Code, adm2Code,
			adm3Code, adm4Code);
		if (levelAdm != null) {
		    if (nearestParentAdm != null) {
			if (((level - nearestParentAdm.getLevel()) <= 2)) {
			    // the admcode of the lowestlevel is probably not
			    // well set, return the specified level Adm
			    logger.warn("[wrong adm" + (level - 1)
				    + "code] : The adm" + (level - 1)
				    + "Code for " + gisfeature + "[" + adm1Code
				    + "." + adm2Code + "." + adm3Code + "."
				    + adm4Code
				    + "] is wrong. Please correct it to "
				    + levelAdm);
			    return levelAdm;
			} else {
			    logger
				    .warn("[wrong adm codes] : "
					    + ((level - nearestParentAdm
						    .getLevel()) - 1)
					    + " admCodes are wrong for "
					    + gisfeature
					    + "["
					    + adm1Code
					    + "."
					    + adm2Code
					    + "."
					    + adm3Code
					    + "."
					    + adm4Code
					    + "] but the adm"
					    + level
					    + "code is corect. The suggested Adm is "
					    + nearestParentAdm);
			    return nearestParentAdm;
			}

		    } else {
			logger.error("[Wrong adm codes] an Adm" + level
				+ " exists but no parent for " + adm1Code + "."
				+ adm2Code + "." + adm3Code + "." + adm4Code
				+ " : all the code before adm" + level
				+ " are wrong");
			return levelAdm;
		    }
		} else {
		    // there is no adm with this code for this level, we return
		    // the nearest parent
		    // split logs
		    if ((nearestParentAdm != null)
			    && ((level - nearestParentAdm.getLevel()) == 1)) {
			logger
				.warn("[wrong adm"
					+ level
					+ "Code] The adm"
					+ (level)
					+ "Code for "
					+ gisfeature
					+ "["
					+ adm1Code
					+ "."
					+ adm2Code
					+ "."
					+ adm3Code
					+ "."
					+ adm4Code
					+ "] is not well set. The nearest parent found is "
					+ nearestParentAdm);
		    } else {
			logger
				.warn("[wrong adm codes] The adm"
					+ (level)
					+ "Code for "
					+ gisfeature
					+ "["
					+ adm1Code
					+ "."
					+ adm2Code
					+ "."
					+ adm3Code
					+ "."
					+ adm4Code
					+ "] is not well set and some others. The nearest parent found is "
					+ nearestParentAdm);
		    }
		    return nearestParentAdm;

		}
	    } else {
		// The specified adm code doesn't allow a search
		logger.warn("No adm can be found for [" + adm1Code + "."
			+ adm2Code + "." + adm3Code + "." + adm4Code + "]");
		return null;
	    }
	} else {
	    return adm;
	}
    }

    public int deleteAllByLevel(final int level) {
	return ((Integer) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(Session session)
			    throws PersistenceException {
			String queryString = "delete from "
				+ persistentClass.getSimpleName()
				+ " as a where a.level=?";

			Query qry = session.createQuery(queryString);
			qry.setParameter(0, level);
			qry.setCacheable(false);

			return Integer.valueOf(qry.executeUpdate());

		    }
		})).intValue();
    }

    private String getLowestNotNullAdmCode(String adm1Code, String adm2Code,
	    String adm3Code, String adm4Code) {
	if (!isAdmCodeEmpty(adm1Code)) {
	    if (!isAdmCodeEmpty(adm2Code)) {
		if (!isAdmCodeEmpty(adm3Code)) {
		    if (!isAdmCodeEmpty(adm4Code)) {
			// adm1,adm2,adm3,adm4
			return adm4Code;
		    } else {
			// adm1,adm2,adm3,null
			return adm3Code;
		    }
		} else {
		    // adm1,Adm2, null..
		    return adm2Code;
		}
	    } else {
		// adm1,null...
		return adm1Code;
	    }
	} else {
	    // if adm1 is empty can not retrieve any Adm
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public List<Long> listFeatureIdByLevel(final int level) {
	return ((List<Long>) this.getHibernateTemplate().execute(
		new HibernateCallback() {

		    public Object doInHibernate(final Session session)
			    throws PersistenceException {
			final String queryString = "select featureId from "
				+ persistentClass.getSimpleName()
				+ " as a where a.level=?";

			final Query qry = session.createQuery(queryString);
			qry.setParameter(0, level);
			qry.setCacheable(false);
			return qry.list();

		    }
		}));
    }
    

}
