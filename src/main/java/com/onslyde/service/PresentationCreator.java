package com.onslyde.service;

import com.onslyde.data.MemberRepository;
import com.onslyde.domain.Session;
import com.onslyde.domain.User;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.ws.Response;
import java.io.IOException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequestScoped
@Path("/template")
public class PresentationCreator {

    @Inject
    private EntityManager em;

    private Session currentSession;

    @Inject
    private MemberRegistration memberReg;

    @GET
    @Path("/create")
    @Produces("text/html")
    public void createPres(@QueryParam("email") String email,
                         @QueryParam("token") String token,
                         @QueryParam("presName") String presName,
                         @QueryParam("poll1") String poll1,
                         @QueryParam("option1") String option1,
                         @QueryParam("option2") String option2) throws ServletException, IOException {

        int presID = 0;
        try {
            presID = memberReg.createPresentation(email,token,presName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
        HttpServletResponse response = ResteasyProviderFactory.getContextData(HttpServletResponse.class);

        request.setAttribute("eventid", presID);
        request.setAttribute("presName", presName);
        request.setAttribute("poll1", poll1);
        request.setAttribute("option1", option1);
        request.setAttribute("option2", option2);

        request.getRequestDispatcher("/prestemplate.jsp").forward(request, response);
//        response.sendRedirect("prestemplate.jsp");
    }

}
