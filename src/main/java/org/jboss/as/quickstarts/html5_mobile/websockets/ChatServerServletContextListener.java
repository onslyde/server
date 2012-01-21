/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.quickstarts.html5_mobile.websockets;


import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.ConcurrentHashSet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;
import java.util.Set;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */
public class ChatServerServletContextListener implements ServletContextListener {

    //maybe use infinispan
    //https://code.google.com/p/infinispan-http-session-manager/wiki/Home

	private Server server = null;
    private static Set<ChatWebSocketHandler.ChatWebSocket> webSockets = new ConcurrentHashSet<ChatWebSocketHandler.ChatWebSocket>();
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
            connector.setHost(addr.getHostAddress());

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

    public static synchronized Set<ChatWebSocketHandler.ChatWebSocket> getWebSockets() {
        return webSockets;
    }

    public static synchronized void setWebSockets(Set<ChatWebSocketHandler.ChatWebSocket> webSockets) {
        ChatServerServletContextListener.webSockets = webSockets;
    }
}
