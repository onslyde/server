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

import com.onslyde.domain.SessionHome;
import com.onslyde.model.SessionManager;

import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

@Path("/presenters")
@Singleton
@Stateful
public class PresenterService {
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<SessionManager> slidFastEventSrc;

    @Inject
    private Validator validator;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private SessionHome sessionHome;

    String addr = null;

    @GET
    @Path("/ip")
    @Produces(MediaType.APPLICATION_JSON)
    public String ip(@QueryParam("session") int sessionID) {

        //user signs up
        //we generate session code and email it
        //they use session code to init
//        System.out.println("sessionID-------sessionID" + sessionID);
        if(addr == null){

//            if(slidFast.startSession(sessionID)){
//                System.out.println("-getSlidFast().startSession()-------false");
                //todo hack to sync objects across threads for now

                //System.out.println("hello-----");
                try {
                    addr = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    log.severe("can't get IP address, falling back to local");
                    addr = "127.0.0.1";
                }
//            }else{
                //todo - in startSession() this session has already been started
//            }
        }



         slidFastEventSrc.fire(sessionManager);
//        if(sessionID == null){
//            //send message that ID is invalid
//        }else{
//
//        }
        //need to check for hosted mode
        return "127.0.0.1";
    }

}
