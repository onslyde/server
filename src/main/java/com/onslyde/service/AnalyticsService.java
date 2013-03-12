package com.onslyde.service;

import com.onslyde.domain.Session;
import com.onslyde.domain.SessionHome;

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


    @GET
    @Produces("application/json")
    @Path("/{session:[0-9][0-9]*}")
    public Session getSessionData(@PathParam("session") int session){
        Session mySession = sessionHome.findById(session);
        return mySession;
    }

}
