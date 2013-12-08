package com.onslyde.service;


import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wesleyhales on 12/8/13.
 */

public class JettyServices {

  @Inject
  private SessionManager sessionManager;

  @Inject
  private Event<SessionManager> slidFastEventSrc;

  @Inject
  private Mediator mediator;

  @Inject Event<Mediator> mediatorEventSrc;

  @PostConstruct
  public void initialize() {
    slidFastEventSrc.fire(sessionManager);
    mediatorEventSrc.fire(mediator);
    System.out.println("_____________postconstruct slidfast and mediator in rest");
  }




}
