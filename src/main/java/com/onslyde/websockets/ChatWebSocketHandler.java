package com.onslyde.websockets;

import com.onslyde.domain.*;
import com.onslyde.model.Mediator;
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
    @Inject
    private static SlidFast slidFast;

    private static Mediator mediator;

    private Map<Integer,List<ChatWebSocket>> sessions;
    private Map<Integer,ChatWebSocket> psessions;

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

//        System.out.println("doWebSocketConnect----------" + sessionID);
        if(request.getParameter("attendeeIP") != null){
            attendeeIP = request.getParameter("attendeeIP");
        }else{
            attendeeIP = "777" + "." + sessionID + "." + randomIPRange() + "." + randomIPRange();
        }
        //todo - define a presenter websocket so we know when to fanout
//        String attendeeIP = request.getRemoteAddr();
        ChatWebSocket cws = new ChatWebSocket(attendeeIP,sessionID);

//        System.out.println("---attendeeIP,sessionID----" + attendeeIP + " " + sessionID);

        //when a user connects, we add his ws connection to a sessionID map
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

    public void observeItemEvent(@Observes Mediator mediator) {

        syncMediator(mediator);
//        System.out.println("-slidFast.getJsEvent()-------" + mediator.getJsEvent());
        if(mediator.getJsEvent() != null){
            try {
                for (ChatWebSocket webSocket : getWebsockets()) {
                    //send out to all connected websockets
//                    System.out.println("-slidFast.getJsEvent()#1--------" + mediator.getJsEvent());
//                    if(slidFast.getJsEvent() != null){

                        webSocket.connection.sendMessage(mediator.getJsEvent());

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
            int pollCount = 0;
            //send current state to remotes
//            syncSlidFast(slidFast);
//            syncMediator(mediator);
            try {
//                System.out.println("mediator: " + mediator);
                if(mediator != null){
                    if(mediator.getPollCount().containsKey(sessionID)){
                        pollCount = mediator.getPollCount().get(sessionID);
                    }
                    System.out.println("_____sessions in map: " + mediator.getSessionID() + " users session: " + sessionID + " size for session: " + sessions.get(sessionID).size());
                    if(mediator.getActiveOptions().containsKey(sessionID)){
                        Mediator.SessionTracker st = mediator.getActiveOptions().get(sessionID);

                        List options = st.getActiveOptions();
                        if(options.size() == 2) {

//                            System.out.println("options sent to new remote: " + options + " users session:" + sessionID);
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
                        this.connection.sendMessage(ClientEvent.updateCount(wscount, pollCount,sessionID));

                    }

                }

            } catch (IOException e) {
                System.out.println("error2=========");
                e.printStackTrace();
            }


        }

        public void onMessage(String data) {

            //dont do anything if the presenter has not started the session
            //todo inform the remote
//            if(sessions.containsKey(sessionID)){

//            System.out.println("------data-" + data + " sessionID" + sessionID);
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
                   getSlidFast().updateGroupVote("wtf",attendeeIP,sessionID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendToPresenter(data,this.connection,sessionID);

            }else if (data.equals("vote:nice")){
                data = ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                        "\"fire\":function(){" +
                        "window.eventObj4 = document.createEvent('Event');" +
                        "eventObj4.initEvent(\'nice\', true, true);" +
                        "document.dispatchEvent(eventObj4);" +
                        "}}}");
                try {
                   getSlidFast().updateGroupVote("nice",attendeeIP,sessionID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendToPresenter(data,this.connection,sessionID);

            }else if (data.contains(ACTIVE_OPTIONS)){
                String options = data.substring(ACTIVE_OPTIONS.length(), data.length());
                List<String> optionList = Arrays.asList(options.split("\\s*,\\s*"));
                System.out.println("=======optionList.size()=" + optionList.size());
                if(optionList.size() == 2){

                }

                try {
//                    System.out.println("-options " + options + " data " + data);
//                    if(getSlidFast() == null){
//                        syncMediator(mediator);
//                    }
                    getSlidFast().addGroupOptions(optionList,sessionID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                data = ClientEvent.createEvent("updateOptions", optionList,sessionID);
                sendToAll(data, this.connection, sessionID);

            }else if (data.contains(VOTE)){

                String vote = data.substring(VOTE.length(), data.length());

                try {
                    getSlidFast().updateGroupVote(vote,attendeeIP,sessionID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                data = ClientEvent.clientVote(vote,sessionID);
                sendToPresenter(data, this.connection, sessionID);

            }else if (data.contains(REMOTE_MARKUP)){

                data = ClientEvent.remoteMarkup(data,sessionID);
                sendToAll(data,this.connection,sessionID);
//                //System.out.println("-----------" + data);
            }else if (data.contains("::connect::")){
                try {
//
                    //System.out.println("-start session----------" + sessionID);
//                    syncSlidFast(slidFast);
//                    if(getSlidFast() == null){
//                        syncMediator(mediator);
//                    }
                    getSlidFast().startSession(sessionID);
                    getSlidFast().setPollcount(0);
                    getPsessions().put(sessionID,this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (data.contains("::disconnect::")){
                try {
//                    syncSlidFast(slidFast);
                    System.out.println("-disconnect session----------" + sessionID);
                    for(int i : getMediator().getSessionID()){
                        if(i == sessionID){
                            System.out.println("-remove----------" + sessionID);
                            getMediator().getSessionID().remove(i);
                        }
                    }
                    if(sessions.containsKey(sessionID)){
                        System.out.println("-sessions.remove----------" + sessions.get(sessionID));
                        sessions.remove(sessionID);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            //System.out.println("-----------" + data);
            //fan out

//            }

        }

        public void onClose(int closeCode, String message) {
            // Remove ChatWebSocket in the global list of ChatWebSocket
            // instance.
            if(getSessions().containsKey(sessionID)){
                System.out.println("-remove attendee socket----------" + sessionID);
                sessions.get(sessionID).remove(this);
                if(sessions.get(sessionID).size() == 0){
                    System.out.println("-remove session ID from memory----------" + sessionID);
                    sessions.remove(sessionID);
                    Iterator<Integer> i = mediator.getSessionID().iterator();
                    while (i.hasNext()) {
                        Integer s = i.next();
                        i.remove();
                    }
                }
            }
            getWebsockets().remove(this);
        }
    }

    private void sendToAll(String data, WebSocket.Connection connection,int sessionID){
        try {

            //only send data to specified sessionID
            List<ChatWebSocket> channelSessions = getSessions().get(sessionID);
            for (ChatWebSocket webSocket : channelSessions) {
                    webSocket.connection.sendMessage(data);
            }
        } catch (IOException x) {
            // Error was detected, close the ChatWebSocket client side
            connection.disconnect();
        }
    }

    private void sendToPresenter(String data, WebSocket.Connection connection,int sessionID){
        try {

            if(psessions.containsKey(sessionID)){
                psessions.get(sessionID).connection.sendMessage(data);
            }

        } catch (IOException x) {
            // Error was detected, close the ChatWebSocket client side
            connection.disconnect();
        }
    }

    public static synchronized Set<ChatWebSocket> getWebsockets() {
        return websockets;
    }

    public static synchronized void syncMediator(Mediator mediator2) {
        mediator = mediator2;
    }

    public static synchronized SlidFast getSlidFast() {
        if(slidFast == null){
            syncSlidFast(slidFast);
        }
        return slidFast;// == null ? new SlidFast() : slidFast;
    }

    public static synchronized Mediator getMediator() {
        return mediator;
    }

    public void observeItemEvent(@Observes SlidFast slidFast) {
        syncSlidFast(slidFast);
    }

    public static synchronized void syncSlidFast(SlidFast slidFast2) {
        slidFast = slidFast2;
    }

    public Map<Integer, ChatWebSocket> getPsessions() {
        if(psessions == null){
            psessions = new HashMap<Integer, ChatWebSocket>();
        }
        return psessions;
    }

    public void setPsessions(Map<Integer, ChatWebSocket> psessions) {
        this.psessions = psessions;
    }
}
