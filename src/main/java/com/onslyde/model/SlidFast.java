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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

//@Singleton
@ApplicationScoped
public class SlidFast {

    private List<String> activeOptions;
    private String activeOption;
    private List<String> currentVotes;
    private String jsEvent;

    private Map<String,Attendee> ips = new HashMap<String, Attendee>();

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

    @PostConstruct
    public void initialize() {
//        System.out.println("_____________postconstruct");
        this.activeOption = "";
        this.activeOptions = new ArrayList<String>();
        this.currentVotes = new ArrayList<String>();
    }

    private Session currentSession;
    private SlideGroup currentSlideGroup;
    private boolean sessionStarted = false;

    public boolean startSession(){
        //should be based off presenters socket id or something
        if(!sessionStarted){
        sessionStarted = true;
        int id;
        currentSession = new Session();
        currentSession.setSessionCode("html5");
        currentSession.setSessionName("atlhtml5");
        currentSession.setUser(userHome.findById(3));
        currentSession.setCreated(new Date());
        currentSession.setStart(new Date());
        sessionHome.persist(currentSession);
        //todo hack to sync objects across threads for now
        slidFastEventSrc.fire(this);
        //currentSession.setId(id);
        }
        return true;
    }

    public void addGroupOptions(List<String> options){
        if(options != null){
            String groupName = "";
            currentSlideGroup = new SlideGroup();
            currentSlideGroup.setSession(currentSession);
            currentSlideGroup.setGroupName(groupName);
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
                setActiveOptions(options);
        }
        //sessionHome.persist(currentSession);
    }

    public void updateGroupVote(String vote, String attendeeIP){

        if(vote != null){
            SlideGroupVotes sgv = new SlideGroupVotes();
            Attendee attendee;
            boolean merge = false;
            //manage the attendee object :(
            if(!ips.containsKey(attendeeIP)){
                attendee = new Attendee();
                attendee.setName("unknown");
                attendee.setIp(attendeeIP);
                attendee.setCreated(new Date());
                ips.put(attendeeIP, attendee);
            }else{
                attendee = ips.get(attendeeIP);
                merge = true;
            }


            for(SlideGroupOptions option : currentSlideGroup.getSlideGroupOptionses()){
               if(option != null){
                if(option.getName().equals(vote)){
                    sgv.setAttendee(attendee);
                    sgv.setSlideGroup(currentSlideGroup);
                    sgv.setSlideGroupOptions(option);
                    sgv.setVoteTime(new Date());
                    //currentSlideGroup.getSlideGroupVoteses().add(sgv);
                    if(merge){
                        attendeeHome.merge(attendee);
                    }else{
                        attendeeHome.persist(attendee);
                    }

                    slideGroupVotesHome.persist(sgv);
                }
               }
            }

            //currentSession.getSlideGroups().add(currentSlideGroup);
            //sgHome.persist(currentSlideGroup);
            sessionHome.merge(currentSession);
        }
    }


    public List<String> getActiveOptions() {
        return activeOptions;
    }

    public void setActiveOptions(List<String> activeOptions) {
        this.activeOptions = activeOptions;
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

    public String getJsEvent() {
        return jsEvent;
    }

    public void setJsEvent(String jsEvent) {
        this.jsEvent = jsEvent;
    }
}
