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
    private Map<Integer,List<String>> activeOptions;
    private String jsEvent;
    private List<Integer> sessionID;
    private Map<Integer,Integer> pollCount;


    @PostConstruct
    public void initialize() {
        System.out.println("_____________postconstruct mediator");
        this.activeOptions = new HashMap<Integer, List<String>>();
    }

    public Map<Integer, List<String>> getActiveOptions() {
        if(activeOptions == null){
            activeOptions = new HashMap<Integer, List<String>>();
        }
        return activeOptions;
    }

    public void setActiveOptions(Map<Integer, List<String>> activeOptions) {
        this.activeOptions = activeOptions;
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
