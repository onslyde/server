package com.onslyde.service;

import com.onslyde.model.Mediator;
import com.onslyde.model.SlidFast;
import com.onslyde.util.ClientEvent;
import org.eclipse.jetty.server.Request;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private Event<SlidFast> slidFastEventSrc;

    @Inject
    private Validator validator;

    @Inject
    private SlidFast slidFast;

    @Inject
    private Mediator mediator;

    private String currentOptions;

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllMembersJSON(@QueryParam("sessionID") int sessionID) {
        //@SuppressWarnings("unchecked")
        //executing this every second on poll... nice :)
        String data = "";
        mediatorEventSrc.fire(mediator);
        List optionList = new ArrayList();
        if(mediator.getActiveOptions().containsKey(sessionID)){
            Mediator.SessionTracker st = mediator.getActiveOptions().get(sessionID);
            optionList.addAll(st.getActiveOptions());
        }

        if(optionList.size() == 2){
            data = ClientEvent.createEvent("updateOptions", optionList, sessionID);
        }
        return data;
    }

    private String randomIPRange(){
        int min = 256;
        int max = 555;
        return min + (int)(Math.random() * ((max - min) + 1)) + "";
    }

    private String ip = null;

    @POST
    @Path("/vote")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response optionVote(@FormParam("user") String user, @FormParam("sessionID") int sessionID, @FormParam("vote") String vote, @Context HttpServletRequest req) {
        mediatorEventSrc.fire(mediator);

        //req.getRemoteAddr();
        //get ip and verify attendee
//        System.out.println("**************slidFast" + slidFast.getCurrentVotes().size() + "ip: " + ip + " vote:" + vote + " sessionID:" + sessionID);
        if(vote != null){
            if(ip == null && req.getSession().getAttribute("onslydeIP") == null){
                //first subnet should be a user id for the presenter?
                ip = "777." + randomIPRange() + "." + randomIPRange() + "." + randomIPRange();
//                System.out.println("**************random" + ip);
                req.getSession().setAttribute("onslydeIP",ip);
                Map pollcount = mediator.getPollCount();
                if(!mediator.getPollCount().containsKey(sessionID)){
                    mediator.getPollCount().put(sessionID,1);
                }else{
                    int pc = mediator.getPollCount().get(sessionID);
                    pc++;
                    mediator.getPollCount().put(sessionID,pc);
                }
//                System.out.println("connect pollcount" + pollcount);

            }else{
                ip = req.getSession().getAttribute("onslydeIP").toString();
//                System.out.println("**************else" + ip);
            }

            //System.out.println("**************slidFast" + ip);

            slidFast.updateGroupVote(vote,ip,sessionID);

            if(vote.equals("wtf") || vote.equals("nice")){
                mediator.setJsEvent(ClientEvent.clientProps(vote,sessionID));
            }else{
                mediator.setJsEvent(ClientEvent.clientVote(vote,sessionID));
            }

            mediatorEventSrc.fire(mediator);
            mediator.setJsEvent(null);
        }
        Response.ResponseBuilder builder = null;

        builder = Response.ok();

        return builder.build();
    }
}
