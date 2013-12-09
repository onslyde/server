package com.onslyde.service;

import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;
import com.onslyde.util.ClientEvent;
import com.onslyde.websockets.OnslydeWebSocketHandler;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by wesleyhales on 12/8/13.
 */

@Path("/attendees")
public class JettyAttendeeService {

  @Inject
  private static Mediator mediator;

  @Inject
  private static SessionManager sessionManager;

  private List optionList = new ArrayList();

  @GET
  @Path("/json")
  @Produces(MediaType.APPLICATION_JSON)
  public Response listAllMembersJSON(@QueryParam("sessionID") int sessionID, @QueryParam("attendeeIP") String attendeeIP, @QueryParam("tracked") String tracked, @Context HttpServletRequest req) {

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

        if(tracked.equals("start") || (st.getActiveOptions().size() > 0 && !st.getActiveOptions().get(0).equals("null")) || (st.getLastActiveSlide() != st.getActiveSlide())){
          optionList.add(st.getActiveOptions().get(0));
          optionList.add(st.getActiveOptions().get(1));
          data = ClientEvent.createEvent("updateOptions", optionList, sessionID);
        }else{
          data = ClientEvent.remoteMarkup(st.getActiveMarkup(),st.getActiveData(),sessionID);
        }

        st.setLastActiveSlide(st.getActiveSlide());
      }

    } catch (Exception e) {
      System.out.println("problem with polling remote++++++");
      e.printStackTrace();
    }

    Response.ResponseBuilder response = Response.ok(data, MediaType.APPLICATION_JSON);

    response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    response.header("Access-Control-Allow-Origin", "*");
    //response.header("Access-Control-Allow-Origin", request.getHeader("Origin"));
    response.header("Access-Control-Allow-Headers", "accept, origin, ag-mobile-variant, content-type");
    response.header("Content-Type", "text/plain");

    return response.build();

  }

  private String randomIPRange(){
    int min = 256;
    int max = 555;
    return min + (int)(Math.random() * ((max - min) + 1)) + "";
  }


//  @GET
//  @Produces("text/html")
//  public Response  index() throws URISyntaxException {
//    System.out.println("-----"+mediator.getPsessions().size());
////    File f = new File(System.getProperty("user.dir")+"/index.html");
////    String mt = new MimetypesFileTypeMap().getContentType(f);
//    return Response.ok("yay", "text/html").build();
//  }
//  @GET
//  @Path("/hello")
//  public Response  helloGet() {
//    return Response.status(200).entity("HTTP GET method called").build();
//  }
//  @POST
//  @Path("/hello")
//  public Response  helloPost(){
//    return Response.status(200).entity("HTTP POST method called").build();
//  }
//

  public void observeItemEvent(@Observes Mediator mediator) {
    syncMediator(mediator);
  }

  public static synchronized void syncMediator(Mediator mediator2) {
    mediator = mediator2;
  }

  public static synchronized Mediator getMediator() {
    return mediator;
  }

  public static synchronized SessionManager getSessionManager() {
    if (sessionManager == null) {
      syncSlidFast(sessionManager);
    }
    return sessionManager;// == null ? new SlidFast() : slidFast;
  }

  public static synchronized void syncSlidFast(SessionManager sessionManager2) {
    sessionManager = sessionManager2;
  }

//  public void observeSessionManager(@Observes SessionManager sessionManager) {
//    syncSlidFast(sessionManager);
//  }

}
