package com.onslyde.service;

import com.onslyde.domain.SessionHome;
import com.onslyde.model.SlidFast;

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
    private Event<SlidFast> slidFastEventSrc;

    @Inject
    private Validator validator;

    @Inject
    private SlidFast slidFast;

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



             slidFastEventSrc.fire(slidFast);
//        if(sessionID == null){
//            //send message that ID is invalid
//        }else{
//
//        }
        //need to check for hosted mode
        return addr;
    }

}
