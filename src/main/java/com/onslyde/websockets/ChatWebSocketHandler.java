package com.onslyde.websockets;

import com.onslyde.domain.*;
import com.onslyde.model.SlidFast;
import com.onslyde.service.MemberService;
import com.onslyde.util.ClientEvent;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
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



    private Map<Integer,List<ChatWebSocket>> sessions;

    private Map<Integer,List<ChatWebSocket>> getSessions() {
        if(sessions == null){
          sessions = new HashMap<Integer,List<ChatWebSocket>>();
        }
        return sessions;
    }

    public void setSessions(Map<Integer,List<ChatWebSocket>> sessions) {
        this.sessions = sessions;
    }

    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
        String attendeeIP;
        int sessionID = 0;
        //todo - DONT TRY TO ACCESS getSlidFast() in this method!

        if(request.getParameter("session") != null){
            sessionID = Integer.parseInt(request.getParameter("session"));
        }

        //System.out.println("doWebSocketConnect----------" + sessionID);
        if(request.getParameter("attendeeIP") != null){
            attendeeIP = request.getParameter("attendeeIP");
        }else{
            attendeeIP = "777" + "." + sessionID + "." + randomIPRange() + "." + randomIPRange();
        }
//        String attendeeIP = request.getRemoteAddr();
        ChatWebSocket cws = new ChatWebSocket(attendeeIP,sessionID);
//        System.out.println("---attendeeIP,sessionID----" + attendeeIP + " " + sessionID);
        if(getSessions().containsKey(sessionID)){
//            System.out.println("-------etSessions().containsKey" );
            getSessions().get(sessionID).add(cws);
        }else{
            List newList = new ArrayList();
//            System.out.println("------else-");
            newList.add(cws);
            getSessions().put(sessionID,newList);
        }
        return cws;
    }

    public void observeItemEvent(@Observes SlidFast slidFast) {

        syncSlidFast(slidFast);
        //System.out.println("-slidFast.getJsEvent()-------" + slidFast.getJsEvent());
        if(slidFast.getJsEvent() != null){
            try {
                for (ChatWebSocket webSocket : getWebsockets()) {
                    //send out to all connected websockets
                    ////System.out.println("-slidFast.getJsEvent()#1--------" + slidFast.getJsEvent());
//                    if(slidFast.getJsEvent() != null){

                        webSocket.connection.sendMessage(slidFast.getJsEvent());

//                    }
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
        private String REMOTE_MARKUP = "remoteMarkup";
        private String VOTE = "vote:";
        private String SEPARATOR = ":";

        private String attendeeIP;
        private int sessionID;

        public ChatWebSocket(String attendeeIP, int sessionID) {
//            syncSlidFast(slidFast);
            //System.out.println("attendeeIP----------" + attendeeIP);
            this.attendeeIP = attendeeIP;
            this.sessionID = sessionID;
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
//            syncSlidFast(slidFast);
            try {
                System.out.println("slidFast: " + slidFast);
                if(slidFast != null){
                    System.out.println("sessions in map: " + slidFast.getSessionID() + " users session:" + sessionID);
                    if(slidFast.getActiveOptions().containsKey(sessionID)){
                        List options = slidFast.getActiveOptions().get(sessionID);
                        if(options.size() == 2) {

                            System.out.println("options sent: " + options + " users session:" + sessionID);
                                //only send options to this connection
                                    this.connection.sendMessage(ClientEvent.createEvent("updateOptions",options,sessionID));


                        }
                    }
                }
            } catch (IOException e) {
                    System.out.println("error1=========");
                    e.printStackTrace();
            }
            //update count on deck
            try {
                if(slidFast != null){

//                    wscount++;
    //                //System.out.println("connect" + wscount);
//                    slidFast.setWscount(wscount);
                    //todo - very inefficient... this only needs to go to presenter/slide deck
                    if(getSessions().containsKey(sessionID)){
                        List<ChatWebSocket> channelSessions = getSessions().get(sessionID);
                        int wscount = channelSessions.size();
                        this.connection.sendMessage(ClientEvent.updateCount(wscount, 0,sessionID));

                    }

                }

            } catch (IOException e) {
                //System.out.println("error2=========");
                e.printStackTrace();
            }


        }

        public void onMessage(String data) {
            // Loop for each instance of ChatWebSocket to send message server to
            // each client WebSockets.
             ////System.out.println("------data-" + data);
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
                data = ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                        "\"fire\":function(){" +
                                        "window.eventObj3 = document.createEvent('Event');" +
                                        "eventObj3.initEvent(\'wtf\', true, true);" +
                                        "document.dispatchEvent(eventObj3);" +
                                        "}}}");
                try {
                   //System.out.println("wtf))))) " + attendeeIP);
                   getSlidFast().updateGroupVote("wtf",attendeeIP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (data.equals("vote:nice")){
                data = ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                        "\"fire\":function(){" +
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
//                    if(!getSlidFast().startSession()){
////                        //System.out.println("-getSlidFast().startSession()-------false");
//                        getSlidFast().startSession();
//                    }
                    getSlidFast().addGroupOptions(optionList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                try {
////                    //System.out.println("-slidFast.getCurrentVotes()-count-------" + getSlidFast().getCurrentVotes());
//                    getSlidFast().setActiveOptions(optionList);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                data = ClientEvent.createEvent("updateOptions", optionList,sessionID);

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
                data = ClientEvent.clientVote(vote,sessionID);

            }else if (data.contains(REMOTE_MARKUP)){

                data = ClientEvent.remoteMarkup(data,sessionID);
//                //System.out.println("-----------" + data);
            }else if (data.contains("::connect::")){
                try {
//
                    //System.out.println("-start session----------" + sessionID);
//                    syncSlidFast(slidFast);
                    getSlidFast().startSession(sessionID);
                    getSlidFast().setPollcount(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (data.contains("::disconnect::")){
                try {
//                    syncSlidFast(slidFast);
                    //System.out.println("-disconnect session----------" + sessionID);
                    for(int i : getSlidFast().getSessionID()){
                        if(i == sessionID){
                            //System.out.println("-remove----------" + sessionID);
                        getSlidFast().getSessionID().remove(i);
                        }
                    }
                    if(sessions.containsKey(sessionID)){
                        //System.out.println("-sessions.remove----------" + sessions.get(sessionID));
                        sessions.remove(sessionID);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            //System.out.println("-----------" + data);
            //fan out
            sendToAll(data,this.connection,sessionID);

        }

        public void onClose(int closeCode, String message) {
            // Remove ChatWebSocket in the global list of ChatWebSocket
            // instance.
            if(getSessions().containsKey(sessionID)){
                //System.out.println("-remove attendee socket----------" + sessionID);
                sessions.get(sessionID).remove(this);
            }
            getWebsockets().remove(this);
        }
    }

    private void sendToAll(String data, WebSocket.Connection connection,int sessionID){
        try {

            List<ChatWebSocket> channelSessions = getSessions().get(sessionID);
            for (ChatWebSocket webSocket : channelSessions) {
//                if(data.contains(sessionID + "") && getSlidFast().getSessionID().contains(sessionID)){
                    webSocket.connection.sendMessage(data);
//                }
            }
        } catch (IOException x) {
            // Error was detected, close the ChatWebSocket client side
            connection.disconnect();
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
