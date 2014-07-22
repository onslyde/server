package com.onslyde.domain;

// Generated Jun 15, 2012 8:41:06 AM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.logging.Logger;

/**
 * Home object for domain model class Attendee.
 *
 * @author Hibernate Tools
 * @see com.onslyde.domain.Attendee
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

  public Attendee findByUUID(String uuid) {
    log.fine("getting Attendee by uuid: " + uuid);
    try {

      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Attendee> criteria = cb.createQuery(Attendee.class);
      Root<Attendee> attendee = criteria.from(Attendee.class);
      criteria.select(attendee).where(cb.equal(attendee.get("ip"), uuid)).orderBy(cb.desc(attendee.get("created")));
      log.fine("get successful");
      return entityManager.createQuery(criteria).setMaxResults(1).getSingleResult();

    } catch (Exception nre){
      log.severe("get failed (normal attendee lookup)" + nre);
    }

    return null;
  }
}
