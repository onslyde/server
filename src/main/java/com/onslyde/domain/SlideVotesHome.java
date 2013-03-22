package com.onslyde.domain;

// Generated Jun 15, 2012 8:41:06 AM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Home object for domain model class SlideVotes.
 * @see SlideVotes
 * @author Hibernate Tools
 */
@Stateless
public class SlideVotesHome {

	@Inject
    private Logger log;//(SlideVotesHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(SlideVotes transientInstance) {
		log.fine("persisting SlideVotes instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed" + re);
			throw re;
		}
	}

	public void remove(SlideVotes persistentInstance) {
		log.fine("removing SlideVotes instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed" + re);
			throw re;
		}
	}

	public SlideVotes merge(SlideVotes detachedInstance) {
		log.fine("merging SlideVotes instance");
		try {
			SlideVotes result = entityManager.merge(detachedInstance);
			log.fine("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.severe("merge failed" + re);
			throw re;
		}
	}

	public SlideVotes findById(Integer id) {
		log.fine("getting SlideVotes instance with id: " + id);
		try {
			SlideVotes instance = entityManager.find(
					SlideVotes.class, id);
			log.fine("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.severe("get failed" + re);
			throw re;
		}
	}
}
