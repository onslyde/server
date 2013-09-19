/*
Copyright (c) 2012-2013 Wesley Hales and contributors (see git log)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to
deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.onslyde.service;

import com.onslyde.data.MemberRepository;
import com.onslyde.domain.Session;
import com.onslyde.domain.User;

import javax.ejb.Stateless;
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

    @Inject
    private MemberRepository repository;

    public int register(User user) throws Exception {
        log.info("Registering " + user.getFullName());
        user.setCreated(new Date());
        em.persist(user);
        Session currentSession = new Session();
        currentSession.setSessionCode("beta");
        currentSession.setSessionName("beta");
        currentSession.setUser(user);
        currentSession.setCreated(new Date());
        em.persist(currentSession);
        return currentSession.getId();
    }

    public int createPresentation(String email, String token, String presName) throws Exception {
        try {
            log.info("Create presentation for: " + email);
            User user = repository.findByEmail(email);
            if(token.equals(String.valueOf(user.getCreated().getTime()))){
                Session currentSession = new Session();
                currentSession.setSessionCode("beta");
                currentSession.setSessionName(presName);
                currentSession.setUser(user);
                currentSession.setCreated(new Date());
                em.persist(currentSession);
                return currentSession.getId();
            }else{
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
