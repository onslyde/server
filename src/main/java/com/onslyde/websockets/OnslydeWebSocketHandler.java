package com.onslyde.websockets;

import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;
import com.onslyde.util.ClientEvent;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.server.WebSocketServerConnection;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

@WebSocket
public class OnslydeWebSocketHandler
{

    protected Session session;

    @Inject
    private static SessionManager sessionManager;

    @Inject
    private static Mediator mediator;

    @Inject
    private Logger log;

    //wss: http://amilamanoj.blogspot.com/2013/06/secure-websockets-with-jetty.html

    private Connection connection;
    private String ACTIVE_OPTIONS = "activeOptions:";
    private String REMOTE_MARKUP = "remoteMarkup";
    private String ROULETTE = "roulette";
    private String VOTE = "vote:";
    private String SEPARATOR = ":";

    String attendeeIP = "111.111.111.111";
    int sessionID = 0;
    private int pollCount = 0;

    public void observeItemEvent(@Observes Mediator mediator) {
        syncMediator(mediator);
        //this method serves as a sync between embedded Jetty and the CDI conttext (threads)
        //we fire this event from multiple locations in the CDI beans and this WebSocket is managed by that
        //...sad but that's the hack

        if (mediator.getJsEvent() != null) {
            try {
                for(Map<String,Session> sessions : mediator.getSessions().values()){
                    for(Session owsh : sessions.values()){
                        owsh.getRemote().sendStringByFuture(mediator.getJsEvent());
                    }
                }


            } catch (Exception x) {
                System.out.println("------need better solution for socket management!!!-");
                x.printStackTrace();

            }
        }
    }

    @OnWebSocketConnect
    public void onWebSocketConnect(Session session)
    {
        this.session = session;
        this.session.setIdleTimeout(1000000);
        Map request = session.getUpgradeRequest().getParameterMap();

        if (request.get("attendeeIP") != null)
            attendeeIP = ((String[]) request.get("attendeeIP"))[0];

        if (request.get("session") != null)
            sessionID = Integer.parseInt(((String[]) request.get("session"))[0]);

        //when a user connects, we add his ws connection to a sessionID map
        if (mediator.getSessions().containsKey(sessionID)) {

            //try to disconnect stale session on browser refresh before replacing with new
//            if(mediator.getSessions().get(sessionID).get(attendeeIP) != null){
//                try {
//                    mediator.getSessions().get(sessionID).get(attendeeIP).disconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            //get the map and put new value (dropping the old)
            mediator.getSessions().get(sessionID).put(attendeeIP, this.session);


        } else {
            Map<String,Session> sessionMap = new HashMap<String,Session>();
            sessionMap.put(attendeeIP, this.session);
            mediator.getSessions().put(sessionID, sessionMap);
        }

        //todo - remove this
        // I'm adding all sockets for broadcast from polling clients
        // so at this point we are managing 2 lists of the same thing
        mediator.getWebsockets().add(this);

//        System.out.println("----attendeeIP 1: " + attendeeIP);



        try {
            if (mediator != null) {
                if (mediator.getPollCount().containsKey(sessionID)) {
                    pollCount = mediator.getPollCount().get(sessionID);
                }
                System.out.println("_____sessions in map: " + mediator.getSessionID() + " users session: " + sessionID + " size for session: " + mediator.getSessions().get(sessionID).size() + "---" + mediator.getSessions().get(sessionID).get(attendeeIP));

                if (mediator.getActiveOptions().containsKey(sessionID)) {

                    Mediator.SessionTracker st = mediator.getActiveOptions().get(sessionID);
                    List options = st.getActiveOptions();
                    if (options.size() == 3) {
                        //only send options to this connection
                        this.session.getRemote().sendStringByFuture(ClientEvent.createEvent("updateOptions", options, sessionID));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error1=========");
            e.printStackTrace();
        }
        //update count on deck
        try {
            if (getSessionManager() != null) {
                if (mediator.getPsessions().containsKey(sessionID)) {
                    int wscount  = mediator.getSessions().get(sessionID).size();
                    sendToPresenter(ClientEvent.updateCount(wscount, pollCount, sessionID),null,sessionID);
                }

            }

        } catch (Exception e) {
            System.out.println("error2=========");
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onWebSocketText(Session session, String data)
    {

        Map request = this.session.getUpgradeRequest().getParameterMap();

        if (request.get("attendeeIP") != null)
            attendeeIP = ((String[]) request.get("attendeeIP"))[0];

        if (request.get("session") != null)
            sessionID = Integer.parseInt(((String[]) request.get("session"))[0]);

//        System.out.println("----attendeeIP: " + attendeeIP);
        System.out.println("=====sessionID: " + sessionID);


        if (data.equals("nextSlide")) {
            data = ("{\"cdievent\":{\"fire\":function(){" +
                    "window.eventObja = document.createEvent('Event');" +
                    "eventObja.initEvent(\'slideEvent\', true, true);" +
                    "eventObja.action = 'next';\n" +
                    "document.dispatchEvent(eventObja);" +
                    "}}}");
        } else if (data.equals("previousSlide")) {
            data = ("{\"cdievent\":{\"fire\":function(){" +
                    "window.eventObj1 = document.createEvent('Event');" +
                    "eventObj1.initEvent(\'slideEvent\', true, true);" +
                    "eventObj1.action = 'previous';\n" +
                    "document.dispatchEvent(eventObj1);" +
                    "}}}");
        } else if (data.equals("clearRoute")) {
            data = ("{\"cdievent\":{\"fire\":function(){" +
                    "window.eventObj2 = document.createEvent('Event');" +
                    "eventObj2.initEvent(\'clearRoute\', true, true);" +
                    "document.dispatchEvent(eventObj2);" +
                    "}}}");
        } else if (data.equals("vote:wtf")) {
            data = ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                    "\"fire\":function(){" +
                    "window.eventObj3 = document.createEvent('Event');" +
                    "eventObj3.initEvent(\'wtf\', true, true);" +
                    "document.dispatchEvent(eventObj3);" +
                    "}}}");
            try {
                //System.out.println("wtf))))) " + attendeeIP);
                getSessionManager().updateGroupVote("wtf", attendeeIP, sessionID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendToPresenter(data, this.session, sessionID);

        } else if (data.equals("vote:nice")) {
            data = ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                    "\"fire\":function(){" +
                    "window.eventObj4 = document.createEvent('Event');" +
                    "eventObj4.initEvent(\'nice\', true, true);" +
                    "document.dispatchEvent(eventObj4);" +
                    "}}}");
            try {
                getSessionManager().updateGroupVote("nice", attendeeIP, sessionID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendToPresenter(data, this.session, sessionID);

        } else if (data.contains(ACTIVE_OPTIONS)) {
            String options = data.substring(ACTIVE_OPTIONS.length(), data.length());
            List<String> optionList = Arrays.asList(options.split("\\s*,\\s*"));
//                System.out.println("=======optionList.size()=" + optionList.size() + " " + optionList.get(2));
            if (optionList.size() == 3) {
                try {
                    getSessionManager().addGroupOptions(optionList, sessionID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                data = ClientEvent.createEvent("updateOptions", optionList, sessionID);
                try {
                    sendToAll(data, this.session, sessionID);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } else if (data.contains(VOTE)) {

            String vote = data.substring(VOTE.length(), data.length());

            try {
                getSessionManager().updateGroupVote(vote, attendeeIP, sessionID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            data = ClientEvent.clientVote(vote, sessionID);
            sendToPresenter(data, this.session, sessionID);
        } else if (data.contains("speak:")) {

            String name = data.substring("speak:".length(), data.length());

            data = ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                    "\"fire\":function(){" +
                    "window.eventObji = document.createEvent('Event');" +
                    "eventObji.initEvent(\'speak\', true, true);" +
                    "eventObji.name = '" + name + "';\n" +
                    "document.dispatchEvent(eventObji);" +
                    "}}}");

            sendToPresenter(data, this.session, sessionID);

        } else if (data.contains(REMOTE_MARKUP)) {

            data = ClientEvent.remoteMarkup(data, sessionID);
            try {
                getSessionManager().broadcastMarkup(data, sessionID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sendToAll(data, this.session, sessionID);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
//                //System.out.println("-----------" + data);
        } else if (data.contains(ROULETTE)) {

            data = ClientEvent.roulette(sessionID, false);

            try {
                sendToAll(data, this.session, sessionID);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            List<Session> channelSessions = new ArrayList<Session>();
            //we don't want the presenter socket
            for (String key : mediator.getSessions().get(sessionID).keySet()) {
                System.out.println("=====cws" + key + "=====" +  mediator.getPsessions().get(sessionID).containsKey(key));
                if (!key.equals(attendeeIP)) {
                    channelSessions.add(mediator.getSessions().get(sessionID).get(key));
                }
            }

            Random random = new Random();
            System.out.println("=====random" + channelSessions.size() + " " + sessionID + " " + random.nextInt());
            Session winner = channelSessions.get(random.nextInt(channelSessions.size()));

            try {
                winner.getRemote().sendStringByFuture(ClientEvent.roulette(sessionID, true));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (data.contains("::connect::")) {
            try {

//                if(mediator.getPsessions().containsKey(sessionID) &&
//                        mediator.getPsessions().get(sessionID).containsKey(attendeeIP)) {

                    //Insert presenter into existing sessionID
//                    replace with new ws session
//                    mediator.getPsessions().get(sessionID).put(attendeeIP,this);

//                } else {
                    //start a new session for presenter and purge all existing ws connections

                    getSessionManager().startSession(sessionID);
                    getSessionManager().setPollcount(0);
                    Map<String,Session> presenterData = new HashMap<String,Session>();
                    presenterData.put(attendeeIP,this.session);
                    //todo - prevent session takeover
                    //needs auth
                    mediator.getPsessions().put(sessionID, presenterData);

                    //this is fugly....disconnect any existing sessions
                    //todo - doing this because I can't get the websocket disconnect handler to pickup who the
                    //disconnect is actually coming from :()
                    //clear out the session management map

//                    Iterator<String> it = mediator.getSessions().get(sessionID).keySet().iterator();
//                    while (it.hasNext()) {
//                        String ips = it.next();
//                        if(!ips.equals(attendeeIP)) {
//                            it.remove();
//                        }
//                    }
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (data.contains("::disconnect::")) {
            try {

//                for (int i : mediator.getSessionID()) {
//                    if (i == sessionID) {
//                        mediator.getSessionID().remove(i);
//                    }
//                }
//                if (mediator.getSessions().containsKey(sessionID)) {
//                    mediator.getSessions().remove(sessionID);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void sendToAll(String data, Session connection, int sessionID) throws IOException {
        try {
            //only send data to specified sessionID
            Collection<Session> channelSessions = mediator.getSessions().get(sessionID).values();
            for (Session owsh : channelSessions) {
                owsh.getRemote().sendStringByFuture(data);
            }
        } catch (Exception x) {
            // Error was detected, close the ChatWebSocket client side
            try {
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    private void sendToPresenter(String data, Session connection, int sessionID) {
        try {
            if (mediator.getPsessions().containsKey(sessionID)) {
                Collection<Session> presenterSessions = mediator.getPsessions().get(sessionID).values();
                for (Session webSocket : presenterSessions) {
                    webSocket.getRemote().sendStringByFuture(data);
                }
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason)
    {
//        System.out.println("-this.sessionID----------" + this.sessionID + " sesseion " + this.session);
        if(mediator.getSessions().containsKey(sessionID)){
            System.out.println("-remove attendee socket--" + attendeeIP + "--------" + sessionID + " sesseion " + this.session);
            Map session = mediator.getSessions().get(sessionID);

            if(session.containsKey(attendeeIP)){
                session.remove(attendeeIP);
            }

//            if(mediator.getSessions().get(sessionID).size() == 0){
//                System.out.println("-remove session ID from memory----------" + sessionID);
//                mediator.getSessions().remove(sessionID);
//                Iterator<Integer> i = mediator.getSessionID().iterator();
//                while (i.hasNext()) {
//                    Integer s = i.next();
//                    i.remove();
//                }
//            }
        }
//        System.out.println("---onclose" + mediator.getWebsockets().size());
//        System.out.println("---onclose" + mediator.getWebsockets().contains(this));

        mediator.getWebsockets().remove(this);

        if(mediator.getPsessions().containsKey(this.sessionID)){
            if(mediator.getPsessions().get(this.sessionID).containsKey(this.attendeeIP)){
                System.out.println("-remove presenter socket--" + this.attendeeIP + "--------" + sessionID + " sesseion " + this.session);
                mediator.getPsessions().get(this.sessionID).remove(this.attendeeIP);
            }

        }
//        System.out.println("---onclose" + mediator.getWebsockets().size());

    }

    @OnWebSocketError
    public void onWebSocketError(Throwable cause)
    {
       System.out.println("---WEBSOCKET error: " + cause);
    }

    public static synchronized void syncMediator(Mediator mediator2) {
        mediator = mediator2;
    }

    public static synchronized Mediator getMediator() {
        return mediator;
    }

    public static synchronized SessionManager getSessionManager() {
        if(sessionManager == null){
            syncSlidFast(sessionManager);
        }
        return sessionManager;// == null ? new SlidFast() : slidFast;
    }

    public static synchronized void syncSlidFast(SessionManager sessionManager2) {
        sessionManager = sessionManager2;
    }

    public void observeItemEvent(@Observes SessionManager sessionManager) {
        syncSlidFast(sessionManager);
    }

    
}
