package com.onslyde.data;

import com.onslyde.domain.User;
import com.onslyde.util.PasswordHash;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@ApplicationScoped
public class MemberRepository {

  @Inject
  private EntityManager em;

  public User findById(Long id) {
    return em.find(User.class, id);
  }

  public User findByEmail(String email) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<User> criteria = cb.createQuery(User.class);
    Root<User> member = criteria.from(User.class);
    // Swap criteria statements if you would like to try out type-safe criteria queries, a new
    // feature in JPA 2.0
    // criteria.select(member).where(cb.equal(member.get(Member_.name), email));
    criteria.select(member).where(cb.equal(member.get("email"), email));
    return em.createQuery(criteria).getSingleResult();
  }


  public User findById(int id) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<User> criteria = cb.createQuery(User.class);
    Root<User> member = criteria.from(User.class);
    criteria.select(member).where(cb.equal(member.get("id"), id));
    return em.createQuery(criteria).getSingleResult();
  }

  public List<User> findAll() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<User> criteria = cb.createQuery(User.class);
    Root<User> member = criteria.from(User.class);
    // Swap criteria statements if you would like to try out type-safe criteria queries, a new
    // feature in JPA 2.0
    // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
    criteria.select(member).orderBy(cb.asc(member.get("created")));
    return em.createQuery(criteria).getResultList();
  }


}
