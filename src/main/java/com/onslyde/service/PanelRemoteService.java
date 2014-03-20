package com.onslyde.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/{session:[a-zA-Z][a-zA-Z]*}")
@RequestScoped
public class PanelRemoteService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupMemberById(@PathParam("session") String session) {
        if (session.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        String[] lookup = { "x", "b", "z", "d", "y", "f", "r", "h", "s", "j"};

        System.out.println("=====" + Arrays.asList(lookup).get(3));

        String[] key = session.split("(?!^)");
        String sessionID = "";
        for(int i=0;i<key.length;i++){
            sessionID+=Arrays.asList(lookup).indexOf(key[i]);
        }

        return Response.status(Response.Status.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "https://www.onslyde.com/panel/panel-remote.html?session=" + sessionID)
                .build();

    }

}
