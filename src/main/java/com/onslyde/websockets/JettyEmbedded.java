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
package com.onslyde.websockets;


import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.spdy.api.SPDY;
import org.eclipse.jetty.spdy.server.http.HTTPSPDYServerConnector;
import org.eclipse.jetty.spdy.server.http.PushStrategy;
import org.eclipse.jetty.spdy.server.http.ReferrerPushStrategy;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class JettyEmbedded {


  Server server = new Server();
  WebSocketHandler wsHandler = new EchoSocketHandler();
  ServletContextHandler context = new ServletContextHandler();
  ServletContextHandler restEasyContext = new ServletContextHandler();
  ServletContextHandler wscontext = new ServletContextHandler();
  private ConstraintSecurityHandler _security;

  private static SessionHandler _session;

  public void onStartup(@Observes @Initialized ServletContext ctx) {

    {
      try {



        // SSL configurations
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath("onslyde_keystore");
        sslContextFactory.setTrustStorePath("onslyde_keystore");
        sslContextFactory.setKeyStorePassword("hello123");
        sslContextFactory.setKeyManagerPassword("hello123");
        sslContextFactory.setTrustStorePassword("hello123");
        sslContextFactory.setProtocol("TLSv1");
        sslContextFactory.setIncludeProtocols("TLSv1");
        sslContextFactory.setExcludeCipherSuites(
            "SSL_RSA_WITH_DES_CBC_SHA",
            "SSL_DHE_RSA_WITH_DES_CBC_SHA",
            "SSL_DHE_DSS_WITH_DES_CBC_SHA",
            "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
            "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
            "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
            "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");



        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(443);
        http_config.addCustomizer(new SecureRequestCustomizer());
        http_config.setSendServerVersion(true);

        ServerConnector http = new ServerConnector(server,new HttpConnectionFactory(http_config));
        http.setPort(80);
        http.setIdleTimeout(30000);
        server.addConnector(http);


        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        ServerConnector https = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory,"http/1.1"),
            new HttpConnectionFactory(https_config));
        https.setPort(443);
        https.setIdleTimeout(500000);
        https.setHost("0.0.0.0");

        // Spdy Connector - short version
        Map<Short,PushStrategy> pushMap = new HashMap<Short, PushStrategy>();
        pushMap.put(SPDY.V3,new ReferrerPushStrategy());
        HTTPSPDYServerConnector spdyConnector = new HTTPSPDYServerConnector(server,sslContextFactory,pushMap);
        spdyConnector.setPort(443);

        server.addConnector(spdyConnector);
        _security = new ConstraintSecurityHandler();

        RequestHandler _handler = new RequestHandler();
        _security.setHandler(_handler);

//        <security-constraint>
//        <web-resource-collection>
//          <url-pattern>/*</url-pattern>
//        </web-resource-collection>
//        <user-data-constraint>
//            <transport-guarantee>CONFIDENTIAL</transport-guarantee>
//        </user-data-constraint>
//    </security-constraint>


        Constraint constraint0 = new Constraint();
        constraint0.setAuthenticate(false);
        constraint0.setName("force https");
        constraint0.setDataConstraint(Constraint.DC_CONFIDENTIAL);
        constraint0.setRoles(new String[]{"*","user"});
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/*");
        mapping0.setConstraint(constraint0);

        Set<String> knownRoles=new HashSet<String>();
        knownRoles.add("*");
        knownRoles.add("user");

        _security.setConstraintMappings(Arrays.asList(mapping0),knownRoles);


        ContextHandlerCollection contexts = new ContextHandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        MovedContextHandler movedHandler = new MovedContextHandler();

        movedHandler.setNewContextURL("https://www.onslyde.com");
        movedHandler.setContextPath("/");
        movedHandler.setPermanent(true);
        movedHandler.setDiscardPathInfo(false);
        movedHandler.setDiscardQuery(false);
        movedHandler.setVirtualHosts(new String[]{"onslyde.com"});

        MovedContextHandler goHandler = new MovedContextHandler();
        goHandler.setNewContextURL("https://www.onslyde.com:8443/go");
        goHandler.setContextPath("/go");
        goHandler.setPermanent(true);
        goHandler.setDiscardPathInfo(false);
        goHandler.setDiscardQuery(false);
        goHandler.setVirtualHosts(new String[]{"onslyde.com","www.onslyde.com"});

//        _security.setHandler(context);
        context.setSecurityHandler(_security);
//        restEasyContext.setSecurityHandler(_security);
// no       context.setHandler(movedHandler);

        server.setHandler(requestLogHandler);
        requestLogHandler.setHandler(contexts);


        contexts.addHandler(goHandler);
        contexts.addHandler(movedHandler);

        contexts.addHandler(wscontext);
        contexts.addHandler(context);
        contexts.addHandler(restEasyContext);



        context.setContextPath("/");
        context.setResourceBase("standalone/deployments/onslyde-hosted.war");

        ServletHolder servletHolder = new ServletHolder(DefaultServlet.class);
        ServletHolder restHolder = new ServletHolder(new HttpServletDispatcher());

//        ServletMapping sm = new ServletMapping();
////
//        sm.setServletName("resteasy");
//        sm.setPathSpec("/go/*");
////
        servletHolder.setName("default");

        restHolder.setName("resteasy");
        restHolder.setDisplayName("resteasy");
        restHolder.setInitParameter("javax.ws.rs.Application", "com.onslyde.rest.JettyActivator");
//        servletHolder.setInitParameter("resteasy.scan","true");
//        servletHolder.setInitParameter("resteasy.servlet.mapping.prefix","/go");
        restHolder.setInitParameter( "resteasy.providers", "org.jboss.resteasy.plugins.providers.jackson.JacksonJsonpInterceptor" );


        context.addServlet(servletHolder,"/*");


        wscontext.setContextPath("/ws");
        wscontext.setHandler(wsHandler);

        restEasyContext.setContextPath("/poll");
        restEasyContext.addServlet(restHolder,"/*");

        server.start();
        server.dumpStdErr();

      } catch (Exception e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }


  }


  private class RequestHandler extends AbstractHandler
  {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException
    {
      baseRequest.setHandled(true);

      response.setStatus(200);
      response.setContentType("text/plain; charset=UTF-8");
      response.getWriter().println("URI="+request.getRequestURI());
      String user = request.getRemoteUser();
      response.getWriter().println("user="+user);
      if (request.getParameter("test_parameter")!=null)
        response.getWriter().println(request.getParameter("test_parameter"));
    }
  }

  public final class EchoSocketHandler extends WebSocketHandler {
    @Override
    public void configure(WebSocketServletFactory factory) {
      factory.setCreator(new OnslydeSocketCreator());
    }
  }

}
