package com.onslyde.rest;

import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;
import com.onslyde.service.AttendeeService;
import com.onslyde.service.JettyAttendeeService;
import com.onslyde.websockets.OnslydeWebSocketHandler;
import org.eclipse.jetty.websocket.api.Session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wesleyhales on 12/8/13.
 */

public class JettyActivator extends Application {
   /* class body intentionally left blank */

  @Inject
  private AttendeeService attendeeService;

  @javax.ws.rs.core.Context
  ServletContext context;

  private Set<Object> singletons = new HashSet<Object>();
  private Set<Class<?>> classes = new HashSet<Class<?>>();

  public JettyActivator() {
    System.out.println("--------------context " + context);
    singletons.add(new JettyAttendeeService());
  }

  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }

  @PostConstruct
  public void initialize() {
    System.out.println("_____________jetty post construct");
  }






}