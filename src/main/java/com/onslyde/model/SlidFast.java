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
package com.onslyde.model;

import com.onslyde.domain.*;
import com.onslyde.service.MemberService;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

//@Singleton
@Stateless
public class SlidFast implements Serializable {

//    private Map<Integer,List<String>> activeOptions;
    private String activeOption;
    private List<String> currentVotes;
//    private String jsEvent;
    private int presenterID;
//    private List<Integer> sessionID;

    private int wscount = 0;
    private int pollcount = 0;

    private Map<String,Attendee> ips = new HashMap<String, Attendee>();

    @Inject
    private Mediator mediator;

    @Inject
    private MemberService ms;

    @Inject
    private UserHome userHome;

    @Inject
    private AttendeeHome attendeeHome;

    @Inject
    private SessionHome sessionHome;

    @Inject
    private SlideGroupVotesHome slideGroupVotesHome;

    @Inject
    private SlideGroupHome sgHome;

    @Inject
    private SlideGroupOptionsHome sgoHome;

    @Inject
    private Event<SlidFast> slidFastEventSrc;

    private Session currentSession;
    private SlideGroup currentSlideGroup;
    private boolean sessionStarted = false;

    @PostConstruct
    public void initialize() {
        System.out.println("_____________postconstruct slidfast");
    }


    public boolean startSession(int sessionID){

        if(!sessionStarted){
            System.out.println("session started!!!!! sessionID:" + sessionID);
            currentSession = sessionHome.findById(sessionID);
            currentSession.setStart(new Date());
            sessionHome.merge(currentSession);
            //todo hack to sync objects across threads for now
//            sessionStarted = true;
            setPresenterID(currentSession.getUser().getId());
            if(!mediator.getSessionID().contains(currentSession.getId())){
                mediator.getSessionID().add(currentSession.getId());
            }
            slidFastEventSrc.fire(this);
            return true;
        }else{
            //todo - we need a session end trigger
            //for now always start session
            return true;
        }

    }

    public void addGroupOptions(List<String> options, int sessionID){
        if(options != null){
            if(currentSession == null){
                currentSession = sessionHome.findById(sessionID);
            }

//            System.out.println("----options compare : " + options.size() + " and " + mediator.getActiveOptions().get(currentSession.getId()) + " and " + currentSession.getId());
            String groupName = "";
            currentSlideGroup = new SlideGroup();
            currentSlideGroup.setSession(currentSession);
            currentSlideGroup.setGroupName(groupName);
//            System.out.println("slide group created:" + new Date());
            currentSlideGroup.setCreated(new Date());
            //currentSlideGroup.set
            int sgid = sgHome.persist(currentSlideGroup);

            //SlideGroupOptions sgos = new SlideGroupOptions();

            SlideGroupOptions sgOption = null;
            List<String> alloptions = new ArrayList<String>();
            alloptions.addAll(options);
            alloptions.add("wtf");
            alloptions.add("nice");
                for(String option: alloptions){
                    sgOption = new SlideGroupOptions();
                    sgOption.setName(option);
                    currentSlideGroup.getSlideGroupOptionses().add(sgOption);
                    sgOption.setSlideGroup(sgHome.findById(sgid));
                    //sgOption.setSlideGroupVoteses();
                    sgoHome.persist(sgOption);
                    //on the fly creation, need to set this up in the code
                    groupName += option + ":";
                }

                currentSlideGroup.setGroupName(groupName);
                //if(sgOption != null){
                sgHome.merge(currentSlideGroup);
                //}
                mediator.getActiveOptions().put(currentSession.getId(),options);
        }
        //sessionHome.persist(currentSession);
    }

    public void updateGroupVote(String vote, String attendeeIP){

        if(vote != null && !vote.isEmpty()){
            SlideGroupVotes sgv = new SlideGroupVotes();
            Attendee attendee;
            boolean merge = false;
            //manage the attendee object :(
//            System.out.println("'attendeeIP''''''''''''''''" + attendeeIP);
//            System.out.println("'ips.containsKey(attendeeIP)''''''''''''''''" + ips.containsKey(attendeeIP));

            if(!ips.containsKey(attendeeIP)){
                attendee = new Attendee();
                attendee.setName("unknown");
                attendee.setIp(attendeeIP);
                attendee.setCreated(new Date());
                attendeeHome.persist(attendee);
                ips.put(attendeeIP, attendee);
            }else{
                attendee = ips.get(attendeeIP);
                merge = true;
            }
//            System.out.println("'attendee.getId()''''''''''''''''" + attendee.getId());
            if(currentSlideGroup != null) {
                for(SlideGroupOptions option : currentSlideGroup.getSlideGroupOptionses()){
                   if(option != null){
                    if(option.getName().equals(vote)){
                        sgv.setAttendee(attendee);
                        sgv.setSlideGroup(currentSlideGroup);
                        sgv.setSlideGroupOptions(option);
//                        System.out.println("set vote time:" + new Date());
                        sgv.setVoteTime(new Date());
                        //currentSlideGroup.getSlideGroupVoteses().add(sgv);
//                        if(merge){
//                            attendeeHome.merge(attendee);
//                        }else{
//                            attendeeHome.persist(attendee);
//                        }
//                        System.out.println("'sgv.getAttendee().getId()''''''''''''''''" + sgv.getAttendee().getId());
                        slideGroupVotesHome.persist(sgv);
                    }
                   }
                }

                //currentSession.getSlideGroups().add(currentSlideGroup);
                //sgHome.persist(currentSlideGroup);
                sessionHome.merge(currentSession);
            }


        }
    }




    public String getActiveOption() {
        return activeOption;
    }

    public void setActiveOption(String activeOption) {
        this.activeOption = activeOption;
    }

    public List<String> getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(List<String> currentVotes) {
        this.currentVotes = currentVotes;
    }

//    public String getJsEvent() {
//        return jsEvent;
//    }
//
//    public void setJsEvent(String jsEvent) {
//        this.jsEvent = jsEvent;
//    }

    public int getWscount() {
        return wscount;
    }

    public void setWscount(int wscount) {
        this.wscount = wscount;
    }

    public int getPollcount() {
        return pollcount;
    }

    public void setPollcount(int pollcount) {
        this.pollcount = pollcount;
    }

    public int getPresenterID() {
        return presenterID;
    }

    public void setPresenterID(int presenterID) {
        this.presenterID = presenterID;
    }


}
