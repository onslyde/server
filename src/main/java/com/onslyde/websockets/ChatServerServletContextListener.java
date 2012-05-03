package com.onslyde.websockets;


import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */
public class ChatServerServletContextListener implements ServletContextListener {

    private Server server = null;
    /**
     * Start Embedding Jetty server when WEB Application is started.
     *
     */
    public void contextInitialized(ServletContextEvent event) {
        try {
            // 1) Create a Jetty server with the 8081 port.
            InetAddress addr = InetAddress.getLocalHost();
            this.server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort(8081);
            //dev
            //connector.setHost(addr.getHostAddress());
            //prod
            connector.setHost("0.0.0.0");
            server.addConnector(connector);

            // 2) Register ChatWebSocketHandler in the Jetty server instance.
            ChatWebSocketHandler chatWebSocketHandler = new ChatWebSocketHandler();
            chatWebSocketHandler.setHandler(new DefaultHandler());

            server.setHandler(chatWebSocketHandler);

            // 2) Start the Jetty server.
            server.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop Embedding Jetty server when WEB Application is stopped.
     */
    public void contextDestroyed(ServletContextEvent event) {
        if (server != null) {
            try {
                // stop the Jetty server.
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
