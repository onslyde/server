package com.onslyde.domain;

// Generated Jun 15, 2012 8:41:06 AM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.inject.Inject;
import java.util.logging.Logger;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Home object for domain model class Attendee.
 * @see com.onslyde.domain.Attendee
 * @author Hibernate Tools
 */
@Stateless
public class AttendeeHome {

	@Inject
    private Logger log;//(AttendeeHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Attendee transientInstance) {
		log.fine("persisting Attendee instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed" + re);
			throw re;
		}
	}

	public void remove(Attendee persistentInstance) {
		log.fine("removing Attendee instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed" + re);
			throw re;
		}
	}

	public Attendee merge(Attendee detachedInstance) {
		log.fine("merging Attendee instance");
		try {
			Attendee result = entityManager.merge(detachedInstance);
			log.fine("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.severe("merge failed" + re);
			throw re;
		}
	}

	public Attendee findById(Integer id) {
		log.fine("getting Attendee instance with id: " + id);
		try {
			Attendee instance = entityManager.find(Attendee.class, id);
			log.fine("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.severe("get failed" + re);
			throw re;
		}
	}
}
