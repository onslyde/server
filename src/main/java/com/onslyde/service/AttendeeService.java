package com.onslyde.service;

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
import java.util.logging.Logger;

@Path("/attendees")
@RequestScoped
public class AttendeeService {
    @Inject
    private Logger log;//

    @Inject
    private EntityManager em;

    @Inject
    private Event<SlidFast> slidFastEventSrc;

    @Inject
    private Validator validator;

    @Inject
    private SlidFast slidFast;

    private String currentOptions;

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllMembersJSON(@QueryParam("sessionID") int sessionID) {
        //@SuppressWarnings("unchecked")
        //executing this every second on poll... nice :)
        String data = "";
        slidFastEventSrc.fire(slidFast);
        List optionList = new ArrayList();
        if(slidFast.getActiveOptions().containsKey(sessionID)){
        optionList.addAll(slidFast.getActiveOptions().get(sessionID));
        }
        //System.out.println("!!!!!!!!!!!!!!!!poll " + optionList.size());
        if(optionList.size() == 2){
            //System.out.println("!!!!!!!!!!!!!!!!options " + optionList.get(0).toString() + optionList.get(1).toString());
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
        slidFastEventSrc.fire(slidFast);

        //req.getRemoteAddr();
        //get ip and verify attendee
//        System.out.println("**************slidFast" + slidFast.getCurrentVotes().size() + "ip: " + ip + " vote:" + vote);
        if(vote != null){
            if(ip == null && req.getSession().getAttribute("onslydeIP") == null){
                //first subnet should be a user id for the presenter?
                ip = "777." + randomIPRange() + "." + randomIPRange() + "." + randomIPRange();
//                System.out.println("**************random" + ip);
                req.getSession().setAttribute("onslydeIP",ip);
                int pollcount = slidFast.getPollcount();
                pollcount++;
                System.out.println("connect pollcount" + pollcount);
                slidFast.setPollcount(pollcount);
            }else{
                ip = req.getSession().getAttribute("onslydeIP").toString();
//                System.out.println("**************else" + ip);
            }

            //System.out.println("**************slidFast" + ip);

            slidFast.updateGroupVote(vote,ip);

            if(vote.equals("wtf") || vote.equals("nice")){
                slidFast.setJsEvent(ClientEvent.clientProps(vote,sessionID));
            }else{
                slidFast.setJsEvent(ClientEvent.clientVote(vote,sessionID));
            }

            slidFastEventSrc.fire(slidFast);
            slidFast.setJsEvent(null);
        }
        Response.ResponseBuilder builder = null;

        builder = Response.ok();

        return builder.build();
    }
}
