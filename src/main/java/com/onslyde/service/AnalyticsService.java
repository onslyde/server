package com.onslyde.service;

import com.onslyde.domain.Session;
import com.onslyde.domain.SessionHome;
import com.onslyde.domain.SlideGroupHome;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@RequestScoped
@Path("/analytics")
public class AnalyticsService {

    @Inject
    private SessionHome sessionHome;

    @Inject
    private SlideGroupHome sgHome;

    @GET
    @Produces("application/json")
    @Path("/{session:[0-9][0-9]*}")
    public Session getSessionData(@PathParam("session") int session){
        Session mySession = sessionHome.findById(session);
        return mySession;
    }

    @GET
    @Produces("application/json")
    @Path("/remove/{session:[0-9][0-9]*}")
    public String removeSessionVotes(@PathParam("session") int session){

        try {
            Session mySession = sessionHome.findById(session);
            sgHome.removeBySessionId(mySession);
            return "success!!!";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail!!";
        }
    }

}
