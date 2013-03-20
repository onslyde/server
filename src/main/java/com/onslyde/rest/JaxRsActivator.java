/**
 * onslyde, Audience engagement
 * Copyright 2012, Hales Consulting, Inc., and individual contributors
*/
package com.onslyde.rest;

import com.onslyde.model.Mediator;
import com.onslyde.model.SlidFast;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6
 * "no XML" approach to activating JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath}
 * annotation.
 * </p>
 */
@ApplicationPath("/go")
public class JaxRsActivator extends Application {
   /* class body intentionally left blank */

    @Inject
    private SlidFast slidFast;

    @Inject
    private Event<SlidFast> slidFastEventSrc;

    @Inject
    private Mediator mediator;

    @Inject Event<Mediator> mediatorEventSrc;

   @PostConstruct
   public void initialize() {
       slidFastEventSrc.fire(slidFast);
       mediatorEventSrc.fire(mediator);

       System.out.println("_____________postconstruct slidfast and mediator in rest");
   }
}
