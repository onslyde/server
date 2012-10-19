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

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllMembersJSON() {
        //@SuppressWarnings("unchecked")
        //executing this every second on poll... nice :)
        String data = "";
        slidFastEventSrc.fire(slidFast);
        List optionList = slidFast.getActiveOptions();
        log.fine("!!!!!!!!!!!!!!!!poll " + optionList.size());
        if(optionList.size() == 2){
            data = ClientEvent.createEvent("updateOptions", optionList);
        }
        //final List<Member> results = em.createQuery("select m from Member m order by m.name").getResultList();
        return data;
    }

    @POST
    @Path("/vote")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response optionVote(@FormParam("user") String user, @FormParam("vote") String vote, @Context HttpServletRequest req) {

        String ip = req.getRemoteAddr();
        //get ip and verify attendee
        slidFast.updateGroupVote(vote,ip);
        Response.ResponseBuilder builder = null;
        //slidFast.getCurrentVotes().add(vote);
        //System.out.println("**************slidFast.getCurrentVotes()*" + slidFast.getCurrentVotes().size());
        builder = Response.ok();
        //send new vote out to all conencted clients... should really only go to slide deck
        // could try to also validate that the vote matches one of the options
        if(vote != null){
            slidFast.setJsEvent(ClientEvent.clientVote(vote));
        }

        slidFastEventSrc.fire(slidFast);

        return builder.build();
    }
}
