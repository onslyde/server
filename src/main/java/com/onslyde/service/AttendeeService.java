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
package com.onslyde.service;

import com.onslyde.model.Mediator;
import com.onslyde.model.SessionManager;
import com.onslyde.util.ClientEvent;
import org.eclipse.jetty.websocket.api.Session;
import org.jboss.resteasy.annotations.GZIP;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

@Path("/attendees")
@RequestScoped
public class AttendeeService {
    @Inject
    private Logger log;//

    @Inject
    private EntityManager em;

    @Inject
    private Event<Mediator> mediatorEventSrc;

    @Inject
    private Event<SessionManager> slidFastEventSrc;

    @Inject
    private Validator validator;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private Mediator mediator;

    private String currentOptions;

    private List optionList = new ArrayList();

    private String ip = null;

//    @GZIP
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllMembersJSON(@QueryParam("sessionID") int sessionID, @QueryParam("attendeeIP") String attendeeIP, @QueryParam("tracked") String tracked, @Context HttpServletRequest req) {
        //@SuppressWarnings("unchecked")
        //executing this every second on poll... nice :)
        String data = "";
        mediatorEventSrc.fire(mediator);

        String activeMarkup = "";

        try {

            //increment poll count
            //todo - remove duped code
            if(tracked.equals("start")){
                Map<Integer,HashSet<String>> pollcount = mediator.getPollCount();
                if(!pollcount.containsKey(sessionID)){
                    HashSet<String> ips = new HashSet<String>();
                    ips.add(attendeeIP);
                    pollcount.put(sessionID,ips);
                }else{
                    HashSet<String> ips = pollcount.get(sessionID);
                    ips.add(attendeeIP);
                    pollcount.put(sessionID,ips);
                }
            }

            //send active options or markup
            if(mediator.getActiveOptions().containsKey(sessionID)){
                Mediator.SessionTracker st = mediator.getActiveOptions().get(sessionID);
                //for presentations and panels
                //every new poll presents 2 new options, but the next slide will show null options
                //the second OR option below is just keeping track of option IDs

                if(tracked.equals("start") || (st.getActiveOptions().size() > 0 && !st.getActiveOptions().get(0).equals("null")) || (st.getLastActiveSlide() != st.getActiveSlide())){
                    optionList.add(st.getActiveOptions().get(0));
                    optionList.add(st.getActiveOptions().get(1));
                    data = ClientEvent.createEvent("updateOptions", optionList, sessionID);
                }else{
                    data = ClientEvent.remoteMarkup(st.getActiveMarkup(),st.getActiveData(),sessionID);
                }

                st.setLastActiveSlide(st.getActiveSlide());
            }

        } catch (Exception e) {
            log.severe("problem with polling remote++++++");
            e.printStackTrace();
        }


        return data;
    }

    private String randomIPRange(){
        int min = 256;
        int max = 555;
        return min + (int)(Math.random() * ((max - min) + 1)) + "";
    }



    @POST
    @Path("/vote")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response optionVote(@FormParam("attendeeIP") String attendeeIP, @FormParam("username") String username, @FormParam("email") String email, @FormParam("sessionID") int sessionID, @FormParam("vote") String vote, @FormParam("voteTime") String voteTime) {
        mediatorEventSrc.fire(mediator);
        System.out.println("------" + voteTime);
        if(vote != null){
            Long tempVoteTime = null;
            //convert the onclick timestamp... might be a bit overprotective with the try/catch
            try {
                tempVoteTime = Long.valueOf(voteTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            sessionManager.updateGroupVote(vote,attendeeIP,username,email,sessionID,tempVoteTime);

            if(vote.equals("disagree") || vote.equals("agree")){
                mediator.setJsEvent(ClientEvent.clientProps(vote,sessionID));
            }else{
                mediator.setJsEvent(ClientEvent.clientVote(vote,sessionID));
            }
            mediator.setCurrentSessionID(sessionID);
            mediatorEventSrc.fire(mediator);
            mediator.setJsEvent(null);
            mediator.setCurrentSessionID(0);
        }
        Response.ResponseBuilder builder = null;

        builder = Response.ok();

        return builder.build();
    }


    @POST
    @Path("/speak")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response optionSpeak(@FormParam("attendeeIP") String attendeeIP, @FormParam("sessionID") int sessionID, @FormParam("speak") String speak, @Context HttpServletRequest req) {
        mediatorEventSrc.fire(mediator);

        String data = "";
        if(speak != null){

            mediator.setJsEvent(ClientEvent.speak(sessionID, attendeeIP, speak, 0));
            mediator.setCurrentSessionID(sessionID);
            mediatorEventSrc.fire(mediator);

            Mediator.SessionTracker st = mediator.getActiveOptions().get(sessionID);
            if (st != null) {
                if(st.getQueuedParticipants() == null){
                    st.setQueuedParticipants(new HashMap<String,Session>());
                }
                st.getQueuedParticipants().put(attendeeIP,null);
                //use the same speak event and send back to remote... handle as confirm
                st.setActiveData("{\"attendeeIP\":\"" + attendeeIP + "\",\"position\":\"" + mediator.getActiveOptions().get(sessionID).getQueuedParticipants().size() + "\"}");

            }

            mediator.setJsEvent(null);
            mediator.setCurrentSessionID(0);
        }

        return Response.ok("", MediaType.APPLICATION_JSON).build();
    }


    @POST
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAttendee(@FormParam("attendeeIP") String attendeeIP, @FormParam("sessionID") int sessionID) {
        mediatorEventSrc.fire(mediator);
        Map<Integer,HashSet<String>> pollcount = mediator.getPollCount();
        if(pollcount.containsKey(sessionID)){
            HashSet<String> ips = pollcount.get(sessionID);
            if(ips.contains(attendeeIP)){
                ips.remove(attendeeIP);
            }
            pollcount.put(sessionID,ips);
        }

        Response.ResponseBuilder builder = null;

        builder = Response.ok();

        return builder.build();
    }
}
