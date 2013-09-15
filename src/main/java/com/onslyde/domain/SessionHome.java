package com.onslyde.domain;

// Generated Jun 15, 2012 8:41:06 AM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.logging.Logger;

/**
 * Home object for domain model class Session.
 * @see com.onslyde.domain.Session
 * @author Hibernate Tools
 */
@Stateless
public class SessionHome {

	@Inject
    private Logger log;//(SessionHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Session transientInstance) {
		log.fine("persisting Session instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed" + re);
			throw re;
		}
	}

	public void remove(Session persistentInstance) {
		log.fine("removing Session instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed" + re);
			throw re;
		}
	}

	public Session merge(Session detachedInstance) {
		log.fine("merging Session instance");
		try {
			Session result = entityManager.merge(detachedInstance);
			log.fine("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.severe("merge failed" + re);
			throw re;
		}
	}

    public Session findById(Integer id) {
        log.fine("getting Session instance with id: " + id);
        try {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Session> criteria = cb.createQuery(Session.class);
            Root<Session> session = criteria.from(Session.class);
            criteria.select(session).where(cb.equal(session.get("id"), id));
            log.fine("get successful");
            return entityManager.createQuery(criteria).getSingleResult();


        } catch (RuntimeException re) {
            log.severe("get failed" + re);
            throw re;
        }
    }

    public List findByUser(User user) {
        log.fine("getting Session instance by user: " + user);
        try {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Session> criteria = cb.createQuery(Session.class);
            Root<Session> session = criteria.from(Session.class);
            criteria.select(session).where(cb.equal(session.get("user"), user));
            log.fine("get successful");
            return entityManager.createQuery(criteria).getResultList();


        } catch (RuntimeException re) {
            log.severe("get failed" + re);
            throw re;
        }
    }
}
