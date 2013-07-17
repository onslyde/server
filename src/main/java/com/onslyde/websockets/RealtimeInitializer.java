package com.onslyde.websockets;


import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;

@ApplicationScoped
public class RealtimeInitializer {


    Server server = new Server(8081);
    WebSocketHandler wsHandler = new EchoSocketHandler();
    ContextHandler context = new ContextHandler();

    public void onStartup(@Observes @Initialized ServletContext ctx)
    {
        System.out.println("Initialized web application at context path " + ctx.getContextPath());


//

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

                System.out.println("-----------jetty start");
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
            factory.setCreator(new EchoCreator());
        }
    }

}
