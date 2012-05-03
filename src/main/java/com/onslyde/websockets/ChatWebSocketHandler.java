package com.onslyde.websockets;

import com.onslyde.model.SlidFast;
import com.onslyde.service.MemberService;
import com.onslyde.util.ClientEvent;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

public class ChatWebSocketHandler extends WebSocketHandler {

    private static Set<ChatWebSocket> websockets = new ConcurrentHashSet<ChatWebSocket>();

    @Inject
    private MemberService ms;

    private static SlidFast slidFast;

    public WebSocket doWebSocketConnect(HttpServletRequest request,
            String protocol) {
        return new ChatWebSocket();
    }

    public void observeItemEvent(@Observes SlidFast slidFast) {

        syncSlidFast(slidFast);
        System.out.println("-observer event... votes#--------" + getSlidFast().getCurrentVotes());
        if(slidFast.getJsEvent() != null){
            try {
                for (ChatWebSocket webSocket : getWebsockets()) {
                    //send out to all connected websockets
                    webSocket.connection.sendMessage(slidFast.getJsEvent());
                    slidFast.setJsEvent(null);
                }
            } catch (Exception x) {
                //todo - do something
            }
        }
    }


    public class ChatWebSocket implements WebSocket.OnTextMessage {

        private Connection connection;
        private String ACTIVE_OPTIONS = "activeOptions:";
        private String VOTE = "vote:";
        private String SEPARATOR = ":";

        public void onOpen(Connection connection) {
            // Client (Browser) WebSockets has opened a connection.
            // 1) Store the opened connection
            this.connection = connection;
            // 2) Add ChatWebSocket in the global list of ChatWebSocket
            // instances
            // instance.
            getWebsockets().add(this);

            //send current state to remotes
            //syncSlidFast(slidFast);
            if(slidFast != null && slidFast.getActiveOptions().size() == 2) {
                try {
                    this.connection.sendMessage(ClientEvent.createEvent("updateOptions",slidFast.getActiveOptions()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void onMessage(String data) {
            // Loop for each instance of ChatWebSocket to send message server to
            // each client WebSockets.

            //btw, switch on string coming in JDK 7...quicker to use if/else if for now
            if(data.equals("nextSlide")) {
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "window.eventObja = document.createEvent('Event');" +
                                        "eventObja.initEvent(\'slideEvent\', true, true);" +
                                        "eventObja.action = 'next';\n" +
                                        "document.dispatchEvent(eventObja);" +
                                        "}}}");
            }else if (data.equals("previousSlide")){
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "window.eventObj1 = document.createEvent('Event');" +
                                        "eventObj1.initEvent(\'slideEvent\', true, true);" +
                                        "eventObj1.action = 'previous';\n" +
                                        "document.dispatchEvent(eventObj1);" +
                                        "}}}");
            }else if (data.equals("clearRoute")){
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "window.eventObj2 = document.createEvent('Event');" +
                                        "eventObj2.initEvent(\'clearRoute\', true, true);" +
                                        "document.dispatchEvent(eventObj2);" +
                                        "}}}");
            }else if (data.equals("wtf")){
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "window.eventObj3 = document.createEvent('Event');" +
                                        "eventObj3.initEvent(\'wtf\', true, true);" +
                                        "document.dispatchEvent(eventObj3);" +
                                        "}}}");
            }else if (data.contains(ACTIVE_OPTIONS)){
                String options = data.substring(ACTIVE_OPTIONS.length(), data.length());
                List<String> optionList = Arrays.asList(options.split("\\s*,\\s*"));
                try {
                    System.out.println("-slidFast.getCurrentVotes()-22-------" + getSlidFast().getCurrentVotes());
                    getSlidFast().setActiveOptions(optionList);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                data = ClientEvent.createEvent("updateOptions", optionList);

//                        ("{\"cdievent\":{\"fire\":function(){" +
//                                        "window.eventObj = document.createEvent('Event');" +
//                                        "eventObj.initEvent(\'updateOptions\', true, true);" +
//                                        "eventObj.option1 = '" + optionList.get(0) + "';\n" +
//                                        "eventObj.option2 = '" + optionList.get(1) + "';\n" +
//                                        "document.dispatchEvent(eventObj);" +
//                                        "}}}");

            }else if (data.contains(VOTE)){

                String vote = data.substring(VOTE.length(), data.length());
                try {
                    getSlidFast().getCurrentVotes().add(vote);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                data = ClientEvent.clientVote(vote);
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

    public static synchronized void syncSlidFast(SlidFast slidFast2) {
        slidFast = slidFast2;
    }

    public static synchronized SlidFast getSlidFast() {
        return slidFast;
    }
}
