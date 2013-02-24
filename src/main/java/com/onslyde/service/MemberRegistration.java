package com.onslyde.service;

import com.onslyde.domain.Session;
import com.onslyde.domain.User;
import com.onslyde.model.Member;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class MemberRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    private Session currentSession;

    public int register(User user) throws Exception {
        log.info("Registering " + user.getFullName());
        user.setCreated(new Date());
        em.persist(user);
        currentSession = new Session();
        currentSession.setSessionCode("beta");
        currentSession.setSessionName("beta");
        currentSession.setUser(user);
        currentSession.setCreated(new Date());
        em.persist(currentSession);
        return currentSession.getId();
    }
}
