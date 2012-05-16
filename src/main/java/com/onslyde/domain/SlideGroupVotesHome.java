package com.onslyde.domain;

// Generated May 15, 2012 3:25:29 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Home object for domain model class SlideGroupVotes.
 * @see com.onslyde.domain.SlideGroupVotes
 * @author Hibernate Tools
 */
@Stateless
public class SlideGroupVotesHome {

    @Inject
    private Logger log;

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(SlideGroupVotes transientInstance) {
		log.fine("persisting SlideGroupVotes instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed " + re);
			throw re;
		}
	}

	public void remove(SlideGroupVotes persistentInstance) {
		log.fine("removing SlideGroupVotes instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed " + re);
			throw re;
		}
	}

	public SlideGroupVotes merge(SlideGroupVotes detachedInstance) {
		log.fine("merging SlideGroupVotes instance");
		try {
			SlideGroupVotes result = entityManager.merge(detachedInstance);
			log.fine("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.severe("merge failed " + re);
			throw re;
		}
	}

	public SlideGroupVotes findById(int id) {
		log.fine("getting SlideGroupVotes instance with id: " + id);
		try {
			SlideGroupVotes instance = entityManager.find(
					SlideGroupVotes.class, id);
			log.fine("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.severe("get failed " + re);
			throw re;
		}
	}
}
