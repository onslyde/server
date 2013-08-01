package com.onslyde.service;

import com.onslyde.data.MemberRepository;
import com.onslyde.model.SessionManager;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/{session:[0-9][0-9]*}")
@RequestScoped
public class RemoteService {
    @Inject
    private Logger log;//

    @Inject
    private EntityManager em;

    @Inject
    private Event<SessionManager> slidFastEventSrc;

    @Inject
    private Validator validator;

    @Inject
    private SessionManager sessionManager;

    private String currentOptions;

    @Inject
    private MemberRepository repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupMemberById(@PathParam("session") int session) {
//        Session session = repository.findById(id);
        if (session == 0) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return Response.status(Response.Status.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/remote.html?session=" + session)
                .build();

    }
}
