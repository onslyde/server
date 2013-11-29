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


import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;

@ApplicationScoped
public class JettyEmbedded {


    Server server = new Server(8081);
    WebSocketHandler wsHandler = new EchoSocketHandler();
    ContextHandler context = new ContextHandler();

    public void onStartup(@Observes @Initialized ServletContext ctx)
    {

        {
            try {

                // HTTP Configuration
                HttpConfiguration http_config = new HttpConfiguration();
//                http_config.setSecureScheme("https");
//                http_config.setSecurePort(8443);
                http_config.setOutputBufferSize(32768);

                // HTTP connector
                ServerConnector http = new ServerConnector(server,new HttpConnectionFactory(http_config));
                http.setPort(8081);
                http.setIdleTimeout(30000);
                http.setHost("0.0.0.0");

                // HTTPS Configuration
//                HttpConfiguration https_config = new HttpConfiguration(http_config);
//                https_config.addCustomizer(new SecureRequestCustomizer());
//
//                // HTTPS connector
//                ServerConnector https = new ServerConnector(server,
//                        new SslConnectionFactory(sslContextFactory,"http/1.1"),
//                        new HttpConnectionFactory(https_config));
//                https.setPort(8443);
//                https.setIdleTimeout(500000);

                // Set the connectors
                server.setConnectors(new Connector[] { http });

//                System.out.println("-----------jetty start");
//                context.setContextPath("/echo");
                context.setHandler(wsHandler);
                server.setHandler(context);


                server.start();
//                server.join();


            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


    }

    public final class EchoSocketHandler extends WebSocketHandler
    {
        @Override
        public void configure(WebSocketServletFactory factory)
        {
            factory.setCreator(new OnslydeSocketCreator());
        }
    }

}
