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
package com.onslyde.model;

import com.onslyde.websockets.OnslydeWebSocketHandler;
import org.eclipse.jetty.websocket.api.Session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class Mediator {

    //this class is used to store in memory session/presentation information
    //keeps track of sessionID and all audience members, etc..

    //active options use the sessionID as the key and store currently activated remote options
    private static Map<Integer,SessionTracker> activeOptions;
    private String jsEvent;
    private int currentSessionID;
    private List<Integer> sessionID;
    private Map<Integer,Integer> pollCount;

    //each session IS is the key to a List of IP addresses and their respective connection
    private Map<Integer, Map<String,Session>> sessions;
    //presenter sessions across the app
    private Map<Integer, Map<String,Session>> psessions;


    private static ConcurrentLinkedQueue<OnslydeWebSocketHandler> websockets = new ConcurrentLinkedQueue<OnslydeWebSocketHandler>();

    public static class SessionTracker {

        public SessionTracker(List<String> activeOptions, int activeSlideGroupID, int activeSlide) {
            this.activeOptions = activeOptions;
            this.activeSlideGroupID = activeSlideGroupID;
            this.activeSlide = activeSlide;
        }

        private List<String> activeOptions;
        private int activeSlideGroupID;
        private int activeSlide;
        private String activeMarkup = "";
        private Map<String,Session> queuedParticipants;

        public int getActiveSlideGroupID() {
            return activeSlideGroupID;
        }

        public void setActiveSlideGroupID(int activeSlideGroupID) {
            this.activeSlideGroupID = activeSlideGroupID;
        }

        public List<String> getActiveOptions() {
            return activeOptions;
        }

        public void setActiveOptions(List<String> activeOptions) {
            this.activeOptions = activeOptions;
        }

        public int getActiveSlide() {
            return activeSlide;
        }

        public void setActiveSlide(int activeSlide) {
            this.activeSlide = activeSlide;
        }

        public String getActiveMarkup() {
            return activeMarkup;
        }

        public void setActiveMarkup(String activeMarkup) {
            this.activeMarkup = activeMarkup;
        }

        public Map<String, Session> getQueuedParticipants() {
            return queuedParticipants;
        }

        public void setQueuedParticipants(Map<String, Session> queuedParticipants) {
            this.queuedParticipants = queuedParticipants;
        }
    }

    @PostConstruct
    public void initialize() {
        System.out.println("_____________postconstruct mediator");

        this.activeOptions = new HashMap<Integer, SessionTracker>();
    }

    public static synchronized Map<Integer, SessionTracker> getActiveOptions() {
        return activeOptions;
    }

    public static synchronized void setActiveOptions(Map<Integer, SessionTracker> activeOptions) {
        Mediator.activeOptions = activeOptions;
    }

    public String getJsEvent() {
        return jsEvent;
    }

    public void setJsEvent(String jsEvent) {
        this.jsEvent = jsEvent;
    }

    public int getCurrentSessionID() {
        return currentSessionID;
    }

    public void setCurrentSessionID(int currentSessionID) {
        this.currentSessionID = currentSessionID;
    }

    public List<Integer> getSessionID() {
        if(sessionID == null){
            sessionID = new ArrayList<Integer>();
        }
        return sessionID;
    }

    public void setSessionID(List<Integer> sessionID) {
        this.sessionID = sessionID;
    }

    public Map<Integer, Integer> getPollCount() {
        if(pollCount == null){
            pollCount = new HashMap<Integer, Integer>();
        }
        return pollCount;
    }

    public void setPollCount(Map<Integer, Integer> pollCount) {
        this.pollCount = pollCount;
    }

    public Map<Integer, Map<String,Session>> getSessions() {
        if (sessions == null) {
            sessions = new HashMap<Integer, Map<String,Session>>();
        }
        return sessions;
    }

    public void setSessions(Map<Integer, Map<String,Session>> sessions) {
        this.sessions = sessions;
    }

    public Map<Integer, Map<String,Session>> getPsessions() {
        if (psessions == null) {
            psessions = new HashMap<Integer, Map<String,Session>>();
        }
        return psessions;
    }

    public void setPsessions(Map<Integer, Map<String,Session>> psessions) {
        this.psessions = psessions;
    }

    public ConcurrentLinkedQueue<OnslydeWebSocketHandler> getWebsockets() {
        return websockets;
    }

    public void setWebsockets(ConcurrentLinkedQueue<OnslydeWebSocketHandler> websockets) {
        Mediator.websockets = websockets;
    }
}
