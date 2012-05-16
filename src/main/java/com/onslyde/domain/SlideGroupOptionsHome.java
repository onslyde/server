package com.onslyde.domain;

// Generated May 15, 2012 3:25:29 PM by Hibernate Tools 3.4.0.CR1


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Home object for domain model class SlideGroupOptions.
 * @see com.onslyde.domain.SlideGroupOptions
 * @author Hibernate Tools
 */
@Stateless
public class SlideGroupOptionsHome {

    @Inject
    private Logger log;

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(SlideGroupOptions transientInstance) {
		log.fine("persisting SlideGroupOptions instance");
		try {
			entityManager.persist(transientInstance);
			log.fine("persist successful");
		} catch (RuntimeException re) {
			log.severe("persist failed " + re);
			throw re;
		}
	}

	public void remove(SlideGroupOptions persistentInstance) {
		log.fine("removing SlideGroupOptions instance");
		try {
			entityManager.remove(persistentInstance);
			log.fine("remove successful");
		} catch (RuntimeException re) {
			log.severe("remove failed " + re);
			throw re;
		}
	}

	public SlideGroupOptions merge(SlideGroupOptions detachedInstance) {
		log.fine("merging SlideGroupOptions instance");
		try {
			SlideGroupOptions result = entityManager.merge(detachedInstance);
			log.fine("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.severe("merge failed " + re);
			throw re;
		}
	}

	public SlideGroupOptions findById(int id) {
		log.fine("getting SlideGroupOptions instance with id: " + id);
		try {
			SlideGroupOptions instance = entityManager.find(
					SlideGroupOptions.class, id);
			log.fine("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.severe("get failed " + re);
			throw re;
		}
	}
}
