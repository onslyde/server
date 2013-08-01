package com.onslyde.domain;

// Generated Jun 15, 2012 8:41:06 AM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Home object for domain model class Slide.
 * @see com.onslyde.domain.Slide
 * @author Hibernate Tools
 */
@Stateless
public class SlideHome {

	@Inject
    private Logger log;//(SlideHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public int persist(Slide transientInstance) {
		log.fine("persisting Slide instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed" + re);
			throw re;
		}
        return transientInstance.getId();
	}

	public void remove(Slide persistentInstance) {
		log.fine("removing Slide instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed" + re);
			throw re;
		}
	}

	public Slide merge(Slide detachedInstance) {
		log.fine("merging Slide instance");
		try {
			Slide result = entityManager.merge(detachedInstance);
			log.fine("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.severe("merge failed" + re);
			throw re;
		}
	}

	public Slide findById(Integer id) {
		log.fine("getting Slide instance with id: " + id);
		try {
			Slide instance = entityManager.find(Slide.class, id);
			log.fine("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.severe("get failed" + re);
			throw re;
		}
	}
}
