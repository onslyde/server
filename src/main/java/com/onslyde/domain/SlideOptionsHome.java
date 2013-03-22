package com.onslyde.domain;

// Generated Jun 15, 2012 8:41:06 AM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Home object for domain model class SlideOptions.
 * @see SlideOptions
 * @author Hibernate Tools
 */
@Stateless
public class SlideOptionsHome {

    @Inject
    private Logger log;

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(SlideOptions transientInstance) {
		log.fine("persisting SlideOptions instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed" + re);
			throw re;
		}
	}

	public void remove(SlideOptions persistentInstance) {
		log.fine("removing SlideOptions instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed" + re);
			throw re;
		}
	}

	public SlideOptions merge(SlideOptions detachedInstance) {
		log.fine("merging SlideOptions instance");
		try {
			SlideOptions result = entityManager.merge(detachedInstance);
			log.fine("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.severe("merge failed" + re);
			throw re;
		}
	}

	public SlideOptions findById(Integer id) {
		log.fine("getting SlideOptions instance with id: " + id);
		try {
			SlideOptions instance = entityManager.find(
					SlideOptions.class, id);
			log.fine("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.severe("get failed" + re);
			throw re;
		}
	}
}
