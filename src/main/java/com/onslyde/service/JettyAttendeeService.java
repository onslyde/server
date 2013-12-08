package com.onslyde.service;

import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;

import java.io.File;
import java.net.URISyntaxException;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by wesleyhales on 12/8/13.
 */

@Path("/attendees")
public class JettyAttendeeService {


  @GET
  @Produces("text/html")
  public Response  index() throws URISyntaxException {
    System.out.println("-----"+System.getProperty("user.dir"));
//    File f = new File(System.getProperty("user.dir")+"/index.html");
//    String mt = new MimetypesFileTypeMap().getContentType(f);
    return Response.ok("yay", "text/html").build();
  }
  @GET
  @Path("/hello")
  public Response  helloGet() {
    return Response.status(200).entity("HTTP GET method called").build();
  }
  @POST
  @Path("/hello")
  public Response  helloPost(){
    return Response.status(200).entity("HTTP POST method called").build();
  }

}
