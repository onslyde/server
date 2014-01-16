package com.onslyde.domain;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

@Stateless
public class QuestionsHome {

  @Inject
  private Logger log;//(SlideGroupHome.class);

  @PersistenceContext
  private EntityManager entityManager;

  public int persist(Questions transientInstance) {
    try {
      entityManager.persist(transientInstance);
      log.fine("persist successful");
    } catch (RuntimeException re) {
      log.severe("persist failed" + re);
      throw re;
    }
    return transientInstance.getId();
  }

  public void remove(Questions persistentInstance) {
    log.fine("removing Questions instance");
    try {
      entityManager.remove(persistentInstance);
      log.fine("remove successful");
    } catch (RuntimeException re) {
      log.severe("remove failed" + re);
      throw re;
    }
  }

  public Questions merge(Questions detachedInstance) {
    log.fine("merging Questions instance");
    try {
      Questions result = entityManager.merge(detachedInstance);
      log.fine("merge successful");
      return result;
    } catch (RuntimeException re) {
      log.severe("merge failed" + re);
      throw re;
    }
  }

  public Questions findById(Integer id) {
    log.fine("getting Questions instance with id: " + id);
    try {
      Questions instance = entityManager.find(Questions.class, id);
      log.fine("get successful");
      return instance;
    } catch (RuntimeException re) {
      log.severe("get failed" + re);
      throw re;
    }
  }


}
