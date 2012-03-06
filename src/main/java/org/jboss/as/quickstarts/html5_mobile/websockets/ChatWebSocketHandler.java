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

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.jboss.as.quickstarts.html5_mobile.model.Member;
import org.jboss.as.quickstarts.html5_mobile.rest.MemberService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */
public class ChatWebSocketHandler extends WebSocketHandler {

    private static Set<ChatWebSocket> websockets = new ConcurrentHashSet<ChatWebSocket>();

    public WebSocket doWebSocketConnect(HttpServletRequest request,
            String protocol) {
        return new ChatWebSocket();
    }

    public void observeItemEvent(@Observes Member member) {
        try {
            for (ChatWebSocket webSocket : getWebsockets()) {
                webSocket.connection.sendMessage("{\"cdievent\":{\"fire\":function(){" +
                                                        "eventObj.initEvent(\'memberEvent\', true, true);" +
                                                        "eventObj.name = '" +  member.getName() + "';\n" +
                                                        "document.dispatchEvent(eventObj);" +
                                                        "}}}");
            }
        } catch (IOException x) {
            //todo - do something
        }
    }


    public class ChatWebSocket implements WebSocket.OnTextMessage {

        private Connection connection;

        public void onOpen(Connection connection) {
            // Client (Browser) WebSockets has opened a connection.
            // 1) Store the opened connection
            this.connection = connection;
            // 2) Add ChatWebSocket in the global list of ChatWebSocket
            // instances
            // instance.
            getWebsockets().add(this);
        }

        public void onMessage(String data) {
            // Loop for each instance of ChatWebSocket to send message server to
            // each client WebSockets.
            //btw, switch on string coming in JDK 7
            if(data.equals("nextSlide")) {
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "eventObj.initEvent(\'slideEvent\', true, true);" +
                                        "eventObj.action = 'next';\n" +
                                        "document.dispatchEvent(eventObj);" +
                                        "}}}");
            }else if (data.equals("previousSlide")){
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "eventObj.initEvent(\'slideEvent\', true, true);" +
                                        "eventObj.action = 'previous';\n" +
                                        "document.dispatchEvent(eventObj);" +
                                        "}}}");
            }else if (data.equals("user:anonymous")){
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "eventObj.initEvent(\'slideGroup\', true, true);" +
                                        "eventObj.group = 'default';\n" +
                                        "document.dispatchEvent(eventObj);" +
                                        "}}}");
            }
            System.out.println("-----------" + data);
            try {
                for (ChatWebSocket webSocket : getWebsockets()) {
                    // send a message to the current client WebSocket.
                    webSocket.connection.sendMessage(data);
                }
            } catch (IOException x) {
                // Error was detected, close the ChatWebSocket client side
                this.connection.disconnect();
            }

        }

        public void onClose(int closeCode, String message) {
            // Remove ChatWebSocket in the global list of ChatWebSocket
            // instance.
            getWebsockets().remove(this);
        }
    }

    public static synchronized Set<ChatWebSocket> getWebsockets() {
        return websockets;
    }


}
