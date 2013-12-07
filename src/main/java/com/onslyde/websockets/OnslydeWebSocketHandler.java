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

import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;
import com.onslyde.util.ClientEvent;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.server.WebSocketServerConnection;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Blob;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

//@WebSocket(maxMessageSize = 64 * 2048)
@WebSocket
public class OnslydeWebSocketHandler {

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
  private String PROPS = "props:";
  private String SEPARATOR = ":";

  String attendeeIP = "111.111.111.111";
  int sessionID = 0;
  private int pollCount = 0;
  private String name = "";
  private String email = "";
  private Long voteTime = 0L;

  public void observeItemEvent(@Observes Mediator mediator) {
    syncMediator(mediator);
    //this method serves as a sync between embedded Jetty and the CDI conttext (threads)
    //we fire this event from multiple locations in the CDI beans and this WebSocket is managed by that
    //...sad but that's the hack

    if (mediator.getJsEvent() != null && mediator.getCurrentSessionID() != 0) {
      try {
        //only send to presenter for session
        if (mediator.getPsessions().containsKey(mediator.getCurrentSessionID())) {
          for (Session owsh : mediator.getPsessions().get(mediator.getCurrentSessionID()).values()) {
            owsh.getRemote().sendStringByFuture(mediator.getJsEvent());
          }
        }

      } catch (Exception x) {
        System.out.println("------need better solution for socket management!!!-");
        x.printStackTrace();

      }
    }
  }

  private void getRequestParamData(Map requestMap){

    List requestItem;

    if (requestMap.get("attendeeIP") != null)     {
      requestItem = (List) requestMap.get("attendeeIP");
      attendeeIP = (String)requestItem.get(0);
    }

    if (requestMap.get("session") != null){
      requestItem = (List) requestMap.get("session");
      sessionID = Integer.parseInt((String)requestItem.get(0));
    }

  }

  @OnWebSocketConnect
  public void onWebSocketConnect(Session session) {
    this.session = session;
    //33 minutes
    this.session.setIdleTimeout(2000000);

    //1 hour
//        this.session.setIdleTimeout(3600000);

    getRequestParamData(session.getUpgradeRequest().getParameterMap());

    //when a user connects, we add his ws connection to a sessionID map
    if (mediator.getSessions().containsKey(sessionID)) {
      //session in progress
      //get the map and put new value (dropping the old)
      mediator.getSessions().get(sessionID).put(attendeeIP, this.session);

    } else {
      Map<String, Session> sessionMap = new HashMap<String, Session>();
      sessionMap.put(attendeeIP, this.session);
      mediator.getSessions().put(sessionID, sessionMap);
    }

    //todo - remove this
    // I'm adding all sockets for broadcast from polling clients
    // so at this point we are managing 2 lists of the same thing
    mediator.getWebsockets().add(this);


    //we must do 2 things on each client connect, send active options for vote, and send active markup
    //this also updates how many connections we have for UI
    try {
      if (mediator != null) {
        if (mediator.getPollCount().containsKey(sessionID)) {
          pollCount = mediator.getPollCount().get(sessionID).size();
        }

        if (getSessionTracker(sessionID) != null) {
          List options = getSessionTracker(sessionID).getActiveOptions();
          if (options.size() == 3) {
            //only send options to this connection
            this.session.getRemote().sendStringByFuture(ClientEvent.createEvent("updateOptions", options, sessionID));
          }
          Mediator.SessionTracker st = getSessionTracker(sessionID);
          if (!st.getActiveMarkup().isEmpty()) {
            //send active markup
            this.session.getRemote().sendStringByFuture(ClientEvent.remoteMarkup(st.getActiveMarkup(), "", sessionID));
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error updating client on connect");
      e.printStackTrace();
    }
    //update count on deck
    try {
      if (getSessionManager() != null) {
        if (mediator.getPsessions().containsKey(sessionID)) {
          int wscount = mediator.getSessions().get(sessionID).size();
          sendToPresenter(ClientEvent.updateCount(wscount, pollCount, sessionID), null, sessionID);
        }

      }

    } catch (Exception e) {
      System.out.println("Problem sending connection count to presenter");
      e.printStackTrace();
    }
  }

  private byte[] screenshot;

  @OnWebSocketMessage
  public void onWebSocketBinary(byte[] payload, int offset, int len) {
    screenshot = payload;
  }

  @OnWebSocketMessage
  public void onWebSocketText(Session session, String data) {

    Map request = this.session.getUpgradeRequest().getParameterMap();

    getRequestParamData(session.getUpgradeRequest().getParameterMap());

    if (data.contains(PROPS) || data.contains(VOTE)) {

      int substringLength = 0;
      boolean isProps = false;

      if (data.contains(PROPS)) {
        substringLength = PROPS.length();
        isProps = true;
      } else {
        substringLength = VOTE.length();
      }

      String vote = data.substring(substringLength, data.length());
      List<String> optionList = null;

      //check to see if we're appending name and email
      try {
        optionList = Arrays.asList(vote.split("\\s*,\\s*"));
      } catch (Exception e) {
        e.printStackTrace();
      }

      vote = optionList.get(0);

      if (optionList != null && optionList.size() > 1) {
        //parse name and email
        try {
          name = optionList.get(1);
          email = optionList.get(2);
          voteTime = Long.valueOf(optionList.get(3));
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      }

      //just determining whether this is sentiment or actual vote
      if (isProps) {
        data = ClientEvent.clientProps(vote, sessionID);
      } else {
        data = ClientEvent.clientVote(vote, sessionID);
      }

      try {
        getSessionManager().updateGroupVote(vote, attendeeIP, name, email, sessionID, voteTime);
      } catch (Exception e) {
        e.printStackTrace();
      }
      sendToPresenter(data, this.session, sessionID);

    } else if (data.contains(ACTIVE_OPTIONS)) {
      String options = data.substring(ACTIVE_OPTIONS.length(), data.length());
      List<String> optionList = Arrays.asList(options.split("\\s*,\\s*"));
      String liveAttendee = "";
      if (optionList.size() == 4) {
        //hack for the panels, notify those that want to speak their position, etc...
        // :P  get the attendee IP who is going live
        liveAttendee = optionList.get(3);
        //save the list as orginal 3 items
        optionList = optionList.subList(0, 3);
        if (getSessionTracker(sessionID).getQueuedParticipants().containsKey(liveAttendee)) {
          Session thanks = getSessionTracker(sessionID).getQueuedParticipants().get(liveAttendee);
          //polling clients get a null session, so must check
          //using 777 as a stopgap for start/end of talk
          if (thanks != null) {

            try {
              thanks.getRemote().sendStringByFuture(ClientEvent.speak(sessionID, attendeeIP, "", 777));
            } catch (Exception e) {
              e.printStackTrace();
            }
          }

          //populate for polling clients to let them know who is speaking
          getSessionTracker(sessionID).setActiveData("{\"attendeeIP\":\"" + liveAttendee + "\",\"position\":\"777\"}");

          //todo make this part of the api so remotes can call it when cancelling speak event
          getSessionTracker(sessionID).getQueuedParticipants().remove(liveAttendee);
          Collection<Session> participantSessions = getSessionTracker(sessionID).getQueuedParticipants().values();
          int count = 0;
          for (Session sessions : participantSessions) {
            if (sessions != null) {
              try {
                sessions.getRemote().sendStringByFuture(ClientEvent.speak(sessionID, attendeeIP, "", count++));
              } catch (Exception e) {
                System.err.println("-- Problem sending speak complete confirm to remote.");
              }
            }
          }
        } else {
          //he's already been removed and this is just reset for polling clients
          getSessionTracker(sessionID).setActiveData("{\"attendeeIP\":\"\",\"position\":\"\"}");
        }
      }
      //basic continue with normal 3 options
      if (optionList.size() == 3) {

        data = ClientEvent.createEvent("updateOptions", optionList, sessionID);
        try {
          sendToAll(data, this.session, sessionID);
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          getSessionManager().addGroupOptions(optionList, sessionID, screenshot);
        } catch (Exception e) {
          System.out.println("----- couldn't find session: " + sessionID + " here's the option list: " + optionList);
          e.printStackTrace();
        }


      }
    } else if (data.contains("speak:")) {
      System.out.println("Speak:" + data);
      String name = data.substring("speak:".length(), data.length());

      data = ClientEvent.speak(sessionID, attendeeIP, name, 0);

      sendToPresenter(data, this.session, sessionID);
      //send notification to remote
      Mediator.SessionTracker st = getSessionTracker(sessionID);
      if (st != null) {
        st.getQueuedParticipants().put(attendeeIP, this.session);
        //use the same speak event and send back to remote... handle as confirm
        //you are queued as #xx
        this.session.getRemote().sendStringByFuture(ClientEvent.speak(sessionID, attendeeIP, name, getSessionTracker(sessionID).getQueuedParticipants().size()));
      }


    } else if (data.contains(REMOTE_MARKUP)) {

      try {
        //not formatting data here for polling clients
        //need to add one more thing for status in AttendeeService
        getSessionManager().broadcastMarkup(data, sessionID);
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        sendToAll(ClientEvent.remoteMarkup(data, "", sessionID), this.session, sessionID);
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
        System.out.println("=====cws" + key + "=====" + mediator.getPsessions().get(sessionID).containsKey(key));
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

        getSessionManager().startSession(sessionID);
        getSessionManager().setPollcount(0);
        Map<String, Session> presenterData = new HashMap<String, Session>();
        presenterData.put(attendeeIP, this.session);
        //todo - prevent session takeover
        //needs auth
        if (mediator.getPsessions().containsKey(sessionID)) {
          //simple fix to allow mirroring
          mediator.getPsessions().get(sessionID).put(attendeeIP, this.session);
        } else {
          mediator.getPsessions().put(sessionID, presenterData);
        }

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
    //        if (data.equals("nextSlide")) {
//            data = ("{\"cdievent\":{\"fire\":function(){" +
//                    "window.eventObja = document.createEvent('Event');" +
//                    "eventObja.initEvent(\'slideEvent\', true, true);" +
//                    "eventObja.action = 'next';\n" +
//                    "document.dispatchEvent(eventObja);" +
//                    "}}}");
//        } else if (data.equals("previousSlide")) {
//            data = ("{\"cdievent\":{\"fire\":function(){" +
//                    "window.eventObj1 = document.createEvent('Event');" +
//                    "eventObj1.initEvent(\'slideEvent\', true, true);" +
//                    "eventObj1.action = 'previous';\n" +
//                    "document.dispatchEvent(eventObj1);" +
//                    "}}}");
//        } else if (data.equals("clearRoute")) {
//            data = ("{\"cdievent\":{\"fire\":function(){" +
//                    "window.eventObj2 = document.createEvent('Event');" +
//                    "eventObj2.initEvent(\'clearRoute\', true, true);" +
//                    "document.dispatchEvent(eventObj2);" +
//                    "}}}");
//        } else

  }


  private void sendToAll(String data, Session connection, int sessionID) throws IOException {
    try {
      //only send data to specified sessionID
      Collection<Session> channelSessions = mediator.getSessions().get(sessionID).values();
      for (Session owsh : channelSessions) {
        owsh.getRemote().sendStringByFuture(data);
      }
    } catch (Exception x) {
      System.out.println("--- problem sending to all");
      x.printStackTrace();
      //not sure if we want to diconnect yet
//            try {
//                connection.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

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
  public void onWebSocketClose(int statusCode, String reason) {

    if (mediator.getSessions().containsKey(sessionID)) {
      System.out.println("-remove attendee socket--" + attendeeIP + "--------" + sessionID);
      Map session = mediator.getSessions().get(sessionID);

      if (session.containsKey(attendeeIP)) {
        session.remove(attendeeIP);
      }

      mediator.getWebsockets().remove(this);

      if (mediator.getPsessions().containsKey(this.sessionID)) {
        if (mediator.getPsessions().get(this.sessionID).containsKey(this.attendeeIP)) {
          System.out.println("-remove presenter socket--" + this.attendeeIP + "--------" + sessionID + " sesseion " + this.session);
          mediator.getPsessions().get(this.sessionID).remove(this.attendeeIP);
        }

      }

    }
  }

  @OnWebSocketError
  public void onWebSocketError(Throwable cause) {
    System.out.println("---WEBSOCKET error: " + cause);
  }


  public static synchronized void syncMediator(Mediator mediator2) {
    mediator = mediator2;
  }

  public static synchronized Mediator getMediator() {
    return mediator;
  }

  public static synchronized SessionManager getSessionManager() {
    if (sessionManager == null) {
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

  private Mediator.SessionTracker getSessionTracker(int sessionID) {
    Mediator.SessionTracker st = null;
    if (mediator.getActiveOptions().containsKey(sessionID)) {
      st = mediator.getActiveOptions().get(sessionID);
    }
    return st;
  }


}
