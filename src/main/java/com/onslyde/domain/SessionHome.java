package com.onslyde.domain;

// Generated May 15, 2012 3:25:29 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

	public int persist(Session transientInstance) {
		log.fine("persisting Session instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed " + re);
			throw re;
		}
        return transientInstance.getId();
	}

	public void remove(Session persistentInstance) {
		log.fine("removing Session instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed " + re);
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
			log.severe("merge failed " + re);
			throw re;
		}
	}

	public Session findById(Integer id) {
		log.fine("getting Session instance with id: " + id);
		try {
			Session instance = entityManager.find(Session.class, id);
			log.fine("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.severe("get failed " + re);
			throw re;
		}
	}
}
