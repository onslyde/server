package com.onslyde.service;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.ws.Response;
import java.io.IOException;

@RequestScoped
@Path("/template")
public class PresentationCreator {

    @GET
    @Path("/create")
    @Produces("text/html")
    public void getEvent(@QueryParam("eventid") int eventid) throws ServletException, IOException {

        HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
        HttpServletResponse response = ResteasyProviderFactory.getContextData(HttpServletResponse.class);

        request.setAttribute("event", eventid);
        request.getRequestDispatcher("/prestemplate.jsp").forward(request, response);
//        response.sendRedirect("prestemplate.jsp");
    }

}
