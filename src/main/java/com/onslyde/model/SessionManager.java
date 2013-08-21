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

import com.onslyde.domain.*;
import com.onslyde.service.MemberService;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

//@Singleton
@Stateless
public class SessionManager implements Serializable {

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
    private SlideVotesHome slideVotesHome;

    @Inject
    private SlideGroupHome sgHome;

    @Inject
    private SlideHome sHome;

    @Inject
    private SlideGroupOptionsHome sgoHome;

    @Inject
    private SlideOptionsHome soHome;

    @Inject
    private Event<SessionManager> slidFastEventSrc;

//    private Session currentSession;
//    private SlideGroup currentSlideGroup;
    private boolean sessionStarted = false;

    @PostConstruct
    public void initialize() {
        System.out.println("_____________postconstruct sessionManager");
    }


    public boolean startSession(int sessionID){
        Session currentSession;
        if(!sessionStarted){
            System.out.println("session started!!!!! sessionID:" + sessionID);
            //todo - catch session not found
            currentSession = sessionHome.findById(sessionID);

            currentSession.setStart(new Date());
            sessionHome.merge(currentSession);
            //todo hack to sync objects across threads for now
//            sessionStarted = true;
            setPresenterID(currentSession.getUser().getId());
            if(!mediator.getSessionID().contains(currentSession.getId())){
                mediator.getSessionID().add(currentSession.getId());
            }
//            }else{
//                //clear out any existing session tracker
//                List<String> options = new ArrayList<String>();
//                options.add("null");
//                options.add("null");
//                mediator.getActiveOptions().put(currentSession.getId(), new Mediator.SessionTracker(options,));
//
//            }
            slidFastEventSrc.fire(this);
            return true;
        }else{
            //todo - we need a session end trigger
            //for now always start session
            return true;
        }

    }

    public void broadcastMarkup(String markup, int sessionID){
        if(mediator.getActiveOptions().containsKey(sessionID)){
            Mediator.SessionTracker st = mediator.getActiveOptions().get(sessionID);
            st.setActiveMarkup(markup);
        }
    }

    public void addGroupOptions(List<String> options, int sessionID){
        Session currentSession;
        SlideGroup currentSlideGroup;
        Slide currentSlide;
        currentSession = sessionHome.findById(sessionID);
        int sgid = 0;
        int sid = 0;
        if(options != null){


                String groupName = "";
                currentSlideGroup = new SlideGroup();
                currentSlideGroup.setSession(currentSession);
                currentSlideGroup.setGroupName(groupName);

                currentSlideGroup.setCreated(new Date());
                //currentSlideGroup.set
                sgid = sgHome.persist(currentSlideGroup);


                currentSlide = new Slide();
                currentSlide.setSlideIndex(options.get(2));
                currentSlide.setSlideGroup(currentSlideGroup);
                sid = sHome.persist(currentSlide);
                //SlideGroupOptions sgos = new SlideGroupOptions();

                SlideGroupOptions sgOption = null;
                SlideOptions sOption = null;

                List<String> alloptions = new ArrayList<String>();
                alloptions.add(options.get(0));
                alloptions.add(options.get(1));
                alloptions.add("disagree");
                alloptions.add("agree");
                if(!options.get(0).equals("null")){
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
                }else{
                    //not a voting option but persist the thumbs up and down
                    sOption = new SlideOptions();
                    sOption.setName("disagree");
                    currentSlide.getSlideOptionses().add(sOption);
                    sOption.setSlide(sHome.findById(sid));
                    soHome.persist(sOption);

                    sOption = new SlideOptions();
                    sOption.setName("agree");
                    currentSlide.getSlideOptionses().add(sOption);
                    sOption.setSlide(sHome.findById(sid));
                    soHome.persist(sOption);

                    groupName += "slide";
                }

                    currentSlideGroup.setGroupName(groupName);
                    //if(sgOption != null){
                    sgHome.merge(currentSlideGroup);
                    //}


                }
            if(mediator.getActiveOptions().containsKey(currentSession.getId())){
                Mediator.SessionTracker st = mediator.getActiveOptions().get(currentSession.getId());
                st.setActiveOptions(options);
                st.setActiveSlideGroupID(sgid);
                st.setActiveSlide(sid);
            }else{
                mediator.getActiveOptions().put(currentSession.getId(),new Mediator.SessionTracker(options,sgid,sid));
            }
//        }
        //sessionHome.persist(currentSession);
    }

    public void updateGroupVote(String vote, String attendeeIP, String name, String email, int sessionID){
        Session currentSession;
        currentSession = sessionHome.findById(sessionID);
        SlideGroup currentSlideGroup = null;
        Slide currentSlide = null;
        if(vote != null && !vote.isEmpty()){
            SlideGroupVotes sgv = new SlideGroupVotes();
            SlideVotes sv = new SlideVotes();
            Attendee attendee;
            boolean merge = false;
            //manage the attendee object :(
//            System.out.println("'attendeeIP''''''''''''''''" + attendeeIP);
//            System.out.println("'ips.containsKey(attendeeIP)''''''''''''''''" + ips.containsKey(attendeeIP));

            if(!ips.containsKey(attendeeIP)){
                attendee = new Attendee();
                attendee.setName(name);
                attendee.setEmail(email);
                attendee.setIp(attendeeIP);
                attendee.setCreated(new Date());
                attendeeHome.persist(attendee);
                ips.put(attendeeIP, attendee);
            }else{
                attendee = ips.get(attendeeIP);
                merge = true;
            }
//            System.out.println("'attendee.getId()''''''''''''''''" + attendee.getId());

            //get the currentSlideGroupID from the sessionTracker
            try {
                if(mediator.getActiveOptions().containsKey(currentSession.getId())){
                    Mediator.SessionTracker st = mediator.getActiveOptions().get(currentSession.getId());
                    currentSlideGroup = sgHome.findById(st.getActiveSlideGroupID());
                    currentSlide = sHome.findById(st.getActiveSlide());
                }
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if(currentSlideGroup != null) {
                if(currentSlideGroup.getSlideGroupOptionses().isEmpty()){

                    for(SlideOptions option : currentSlide.getSlideOptionses()){
                        if(option != null){
                            if(option.getName().equals(vote)){
                                sv.setAttendee(attendee);
                                sv.setSlide(currentSlide);
                                sv.setSlideOptions(option);
                                sv.setVoteTime(new Date());
                                slideVotesHome.persist(sv);
                            }
                        }
                    }
                }else{
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
