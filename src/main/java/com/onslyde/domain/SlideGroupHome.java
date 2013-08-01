package com.onslyde.domain;

// Generated Jun 15, 2012 8:41:06 AM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.logging.Logger;

/**
 * Home object for domain model class SlideGroup.
 * @see com.onslyde.domain.SlideGroup
 * @author Hibernate Tools
 */
@Stateless
public class SlideGroupHome {

    @Inject
    private Logger log;//(SlideGroupHome.class);

    @PersistenceContext
    private EntityManager entityManager;

    public int persist(SlideGroup transientInstance) {
        log.fine("persisting SlideGroup instance");
        try {
            entityManager.persist(transientInstance);
            log.fine("persist successful");
        } catch (RuntimeException re) {
            log.severe("persist failed" + re);
            throw re;
        }
        //entityManager.refresh(transientInstance);
        return transientInstance.getId();
    }

    public void remove(SlideGroup persistentInstance) {
        log.fine("removing SlideGroup instance");
        try {
            entityManager.remove(persistentInstance);
            log.fine("remove successful");
        } catch (RuntimeException re) {
            log.severe("remove failed" + re);
            throw re;
        }
    }

    public SlideGroup merge(SlideGroup detachedInstance) {
        log.fine("merging SlideGroup instance");
        try {
            SlideGroup result = entityManager.merge(detachedInstance);
            log.fine("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.severe("merge failed" + re);
            throw re;
        }
    }

    public SlideGroup findById(Integer id) {
        log.fine("getting SlideGroup instance with id: " + id);
        try {
            SlideGroup instance = entityManager.find(SlideGroup.class, id);
            log.fine("get successful");
            return instance;
        } catch (RuntimeException re) {
            log.severe("get failed" + re);
            throw re;
        }
    }

    public void removeBySessionId(Session id) {
        log.fine("getting SlideGroup instance with session id: " + id);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SlideGroup> criteria = cb.createQuery(SlideGroup.class);
        Root<SlideGroup> slideGroup = criteria.from(SlideGroup.class);

        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));

        criteria.select(slideGroup).
                where(cb.equal(slideGroup.get("session"), id));
        List<SlideGroup> sgList = entityManager.createQuery(criteria).getResultList();

        for(SlideGroup sg : sgList){
            entityManager.remove(sg);
        }


//        try {
//            SlideGroup instance = entityManager.find(SlideGroup.class, id);
//            log.fine("get successful");
//            return instance;
//        } catch (RuntimeException re) {
//            log.severe("get failed" + re);
//            throw re;
//        }
    }
}
