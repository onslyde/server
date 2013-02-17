package com.onslyde.websockets;

import com.onslyde.domain.*;
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
import java.util.logging.Logger;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

public class ChatWebSocketHandler extends WebSocketHandler {

    private static Set<ChatWebSocket> websockets = new ConcurrentHashSet<ChatWebSocket>();
    @Inject
    private Logger log;

    private String randomIPRange(){
        int min = 256;
        int max = 555;
        return min + (int)(Math.random() * ((max - min) + 1)) + "";
    }

    private static SlidFast slidFast;

    public WebSocket doWebSocketConnect(HttpServletRequest request,
            String protocol) {
        String attendeeIP = "777." + randomIPRange() + "." + randomIPRange() + "." + randomIPRange();
//        String attendeeIP = request.getRemoteAddr();
        return new ChatWebSocket(attendeeIP);
    }

    public void observeItemEvent(@Observes SlidFast slidFast) {

        syncSlidFast(slidFast);
        //System.out.println("-observer event... votes#--------" + getSlidFast().getCurrentVotes());
        if(slidFast.getJsEvent() != null){
            try {
                for (ChatWebSocket webSocket : getWebsockets()) {
                    //send out to all connected websockets
                    //System.out.println("-slidFast.getJsEvent()#1--------" + slidFast.getJsEvent());
                    if(slidFast.getJsEvent() != null){
                        //System.out.println("-slidFast.getJsEvent()#2--------" + slidFast.getJsEvent());
                        webSocket.connection.sendMessage(slidFast.getJsEvent());
                        //System.out.println("send message--------" + slidFast.getJsEvent());
                    }
                }
            } catch (Exception x) {
                //todo - do something
                x.printStackTrace();
            }
        }
    }

    private int getAttendeeID(){
        return 0;
    }

    private int wscount = 0;



    public class ChatWebSocket implements WebSocket.OnTextMessage {

        private Connection connection;
        private String ACTIVE_OPTIONS = "activeOptions:";
        private String VOTE = "vote:";
        private String SEPARATOR = ":";

        private String attendeeIP;

        public ChatWebSocket(String attendeeIP) {
            this.attendeeIP = attendeeIP;
        }

        public void onOpen(Connection connection) {
            // Client (Browser) WebSockets has opened a connection.
            // 1) Store the opened connection
            this.connection = connection;
            // 2) Add ChatWebSocket in the global list of ChatWebSocket
            // instances
            // instance.
            this.connection.setMaxIdleTime(1000000);

            getWebsockets().add(this);

            //send current state to remotes
            //syncSlidFast(slidFast);
            if(slidFast != null && slidFast.getActiveOptions().size() == 2) {
                try {
                    //only send options to this connection
                    this.connection.sendMessage(ClientEvent.createEvent("updateOptions",slidFast.getActiveOptions()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //update count on deck
            try {
                assert slidFast != null;
                int wscount = slidFast.getWscount();
                wscount++;
                System.out.println("connect" + wscount);
                slidFast.setWscount(wscount);
                //todo - very inefficient... this only needs to go to presenter/slide deck
                for (ChatWebSocket webSocket : getWebsockets()) {
                    webSocket.connection.sendMessage(ClientEvent.updateCount(wscount, slidFast.getPollcount()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void onMessage(String data) {
            // Loop for each instance of ChatWebSocket to send message server to
            // each client WebSockets.
             //System.out.println("------data-" + data);
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
            }else if (data.equals("vote:wtf")){
                data = ("{\"cdievent\":{\"fire\":function(){" +
                                        "window.eventObj3 = document.createEvent('Event');" +
                                        "eventObj3.initEvent(\'wtf\', true, true);" +
                                        "document.dispatchEvent(eventObj3);" +
                                        "}}}");
                try {
                   getSlidFast().updateGroupVote("wtf",attendeeIP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (data.equals("vote:nice")){
                data = ("{\"cdievent\":{\"fire\":function(){" +
                        "window.eventObj4 = document.createEvent('Event');" +
                        "eventObj4.initEvent(\'nice\', true, true);" +
                        "document.dispatchEvent(eventObj4);" +
                        "}}}");
                try {
                   getSlidFast().updateGroupVote("nice",attendeeIP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (data.contains(ACTIVE_OPTIONS)){
                String options = data.substring(ACTIVE_OPTIONS.length(), data.length());
                List<String> optionList = Arrays.asList(options.split("\\s*,\\s*"));

                try {
                    if(!getSlidFast().startSession()){
//                        System.out.println("-getSlidFast().startSession()-------false");
                        getSlidFast().startSession();
                    }
                    getSlidFast().addGroupOptions(optionList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                try {
////                    System.out.println("-slidFast.getCurrentVotes()-count-------" + getSlidFast().getCurrentVotes());
//                    getSlidFast().setActiveOptions(optionList);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                data = ClientEvent.createEvent("updateOptions", optionList);

            }else if (data.contains(VOTE)){

                String vote = data.substring(VOTE.length(), data.length());

                try {
                    getSlidFast().updateGroupVote(vote,attendeeIP);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                try {
////                    getSlidFast().getCurrentVotes().add(vote);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                data = ClientEvent.clientVote(vote);

            }else if (data.contains("connect")){
                try {
                    getSlidFast().setPollcount(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            System.out.println("-----------" + data);
            //fan out
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
            int wscount = slidFast.getWscount();
            wscount--;
            System.out.println("disconnect" + wscount);
            slidFast.setWscount(wscount);
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
        return slidFast;// == null ? new SlidFast() : slidFast;
    }
}
