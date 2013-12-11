package com.onslyde.service;

import com.onslyde.model.Mediator;
import com.onslyde.util.ClientEvent;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by wesleyhales on 12/8/13.
 */

@Path("/attendees")
public class JettyAttendeeService {

  @javax.ws.rs.core.Context
  ServletContext context;

  @Inject
  private Mediator mediator;

  @Inject
  private Event<Mediator> mediatorEventSrc;

  private String currentOptions;

  private List optionList = new ArrayList();

  @GET
  @Path("/json")
  @Produces(MediaType.APPLICATION_JSON)
  public Response listAllMembersJSON(@QueryParam("sessionID") int sessionID, @QueryParam("attendeeIP") String attendeeIP, @QueryParam("tracked") String tracked, @Context HttpServletRequest req) {
    optionList = new ArrayList();

    if(mediator == null){
      mediator = (Mediator)req.getServletContext().getAttribute("mediator");
      mediatorEventSrc = (Event)req.getServletContext().getAttribute("mediatorEvent");
    }

    String data = "";

    String activeMarkup = "";

    try {

      //increment poll count
      //todo - remove duped code
      if(tracked.equals("start")){
        Map<Integer,HashSet<String>> pollcount = mediator.getPollCount();
        if(!pollcount.containsKey(sessionID)){
          HashSet<String> ips = new HashSet<String>();
          ips.add(attendeeIP);
          pollcount.put(sessionID,ips);
        }else{
          HashSet<String> ips = pollcount.get(sessionID);
          ips.add(attendeeIP);
          pollcount.put(sessionID,ips);
        }
      }

      //send active options or markup
      if(mediator.getActiveOptions().containsKey(sessionID)){
        Mediator.SessionTracker st = mediator.getActiveOptions().get(sessionID);
        //for presentations and panels
        //every new poll presents 2 new options, but the next slide will show null options
        //the second OR option below is just keeping track of option IDs

        //handle fresh/new presentation
        if(st.getActiveOptions().size() > 0){
          if(tracked.equals("start") || (!st.getActiveOptions().get(0).equals("null")) || (st.getLastActiveSlide() != st.getActiveSlide())){
            optionList.add(st.getActiveOptions().get(0));
            optionList.add(st.getActiveOptions().get(1));
            data = ClientEvent.createEvent("updateOptions", optionList, sessionID);
          }else{
            data = ClientEvent.remoteMarkup(st.getActiveMarkup(),st.getActiveData(),sessionID);
          }
        }

        st.setLastActiveSlide(st.getActiveSlide());
      }

    } catch (Exception e) {
//      log.severe("problem with polling remote++++++");
      e.printStackTrace();
    }

    Response.ResponseBuilder response = Response.ok(data, MediaType.APPLICATION_JSON);

//    response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//    response.header("Access-Control-Allow-Origin", "*");
//    //response.header("Access-Control-Allow-Origin", request.getHeader("Origin"));
//    response.header("Access-Control-Allow-Headers", "accept, origin, ag-mobile-variant, content-type");
//    response.header("Content-Type", "text/plain");

    return response.build();
  }


  @POST
  @Path("/remove")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeAttendee(@FormParam("attendeeIP") String attendeeIP, @FormParam("sessionID") int sessionID) {
    mediatorEventSrc.fire(mediator);
    Map<Integer,HashSet<String>> pollcount = mediator.getPollCount();
    if(pollcount.containsKey(sessionID)){
      HashSet<String> ips = pollcount.get(sessionID);
      if(ips.contains(attendeeIP)){
        ips.remove(attendeeIP);
      }
      pollcount.put(sessionID,ips);
    }

    Response.ResponseBuilder response = Response.ok();

    return response.build();
  }


}
