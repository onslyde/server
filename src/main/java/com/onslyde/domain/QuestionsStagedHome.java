package com.onslyde.domain;

import com.onslyde.service.QuestionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class QuestionsStagedHome {

  @Inject
  private Logger log;//(SlideGroupHome.class);

  @PersistenceContext
  private EntityManager entityManager;

  public int persist(QuestionsStaged transientInstance) {
    try {
      entityManager.persist(transientInstance);
      log.fine("persist successful");
    } catch (RuntimeException re) {
      log.severe("persist failed" + re);
      throw re;
    }
    return transientInstance.getId();
  }

  public void remove(QuestionsStaged persistentInstance) {
    log.fine("removing QuestionsStaged instance");
    try {
      entityManager.remove(persistentInstance);
      log.fine("remove successful");
    } catch (RuntimeException re) {
      log.severe("remove failed" + re);
      throw re;
    }
  }

  public QuestionsStaged merge(QuestionsStaged detachedInstance) {
    log.fine("merging QuestionsStaged instance");
    try {
      QuestionsStaged result = entityManager.merge(detachedInstance);
      log.fine("merge successful");
      return result;
    } catch (RuntimeException re) {
      log.severe("merge failed" + re);
      throw re;
    }
  }

  public QuestionsStaged findByIdentifier(Integer id) {
    log.fine("getting QuestionsStaged instance with id: " + id);
    try {
      QuestionsStaged instance = entityManager.find(QuestionsStaged.class, id);
      log.fine("get successful");
      return instance;
    } catch (RuntimeException re) {
      log.severe("get failed" + re);
      throw re;
    }
  }

  public List<QuestionsStaged> findByIdentifier(String identifier) {
    log.fine("getting Session instance by identifier: " + identifier);
    try {

      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<QuestionsStaged> criteria = cb.createQuery(QuestionsStaged.class);
      Root<QuestionsStaged> qs = criteria.from(QuestionsStaged.class);
      criteria.select(qs).where(cb.equal(qs.get("identifier"), identifier));
      log.fine("get identifier successful");
      return entityManager.createQuery(criteria).getResultList();

    } catch (RuntimeException re) {
      log.severe("get identifier failed" + re);
      throw re;
    }
  }


}
