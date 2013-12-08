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
package com.onslyde.rest;

import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;
import com.onslyde.service.JettyAttendeeService;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6
 * "no XML" approach to activating JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath}
 * annotation.
 * </p>
 */
public class JaxRsActivator extends Application {
   /* class body intentionally left blank */

  private Set<Object> singletons = new HashSet<Object>();
  private Set<Class<?>> classes = new HashSet<Class<?>>();

  public JaxRsActivator() {
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
