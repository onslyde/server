package com.onslyde.model;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class Mediator {

    //active options use the sessionID as the key and store currently activated remote options
    private static Map<Integer,SessionTracker> activeOptions;
    private String jsEvent;
    private List<Integer> sessionID;
    private Map<Integer,Integer> pollCount;

    public static class SessionTracker {

        public SessionTracker(List<String> activeOptions, int activeSlideGroupID, int activeSlide) {
            this.activeOptions = activeOptions;
            this.activeSlideGroupID = activeSlideGroupID;
            this.activeSlide = activeSlide;
        }

        private List<String> activeOptions;
        private int activeSlideGroupID;
        private int activeSlide;

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
}
