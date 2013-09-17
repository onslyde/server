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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.BufferedWriter;
import java.io.IOException;

import java.io.OutputStreamWriter;
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

    static class PresTemplate {
        private String email;
        private String token;
        private String sessionId;
        private String presName;
        private String poll1;
        private  String option1;
        private  String option2;

        private PresTemplate() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getPresName() {
            return presName;
        }

        public void setPresName(String presName) {
            this.presName = presName;
        }

        public String getPoll1() {
            return poll1;
        }

        public void setPoll1(String poll1) {
            this.poll1 = poll1;
        }

        public String getOption1() {
            return option1;
        }

        public void setOption1(String option1) {
            this.option1 = option1;
        }

        public String getOption2() {
            return option2;
        }

        public void setOption2(String option2) {
            this.option2 = option2;
        }
    }

    @GET
    @Path("/download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/html")
    public void downloadPres(@QueryParam("email") String email,
                         @QueryParam("token") String token,
                         @QueryParam("twitter") String twitter,
                         @QueryParam("sessionId") String sessionId,
                         @QueryParam("presName") String presName,
                         @QueryParam("poll1") String poll1,
                         @QueryParam("option1") String option1,
                         @QueryParam("option2") String option2) throws ServletException, IOException {
        int presID = 0;
        if(sessionId == null || sessionId.isEmpty()){

            try {
                presID = memberReg.createPresentation(email,token,presName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            presID = Integer.parseInt(sessionId);
        }

        HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
        HttpServletResponse response = ResteasyProviderFactory.getContextData(HttpServletResponse.class);

        request.setAttribute("eventid", presID);
        request.setAttribute("twitter", twitter);
        request.setAttribute("presName", presName);
        request.setAttribute("poll1", poll1);
        request.setAttribute("option1", option1);
        request.setAttribute("option2", option2);


//        response.setHeader("Access-Control-Allow-Origin","http://localhost:8001");
//        response.sendRedirect("/prestemplate.jsp");
        request.getRequestDispatcher("/prestemplate.jsp").forward(request, response);

//        System.out.println("-----" + response.toString());
//        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(response.getOutputStream() ) );

//        response.setHeader( "Content-Disposition", "attachment;filename=YourOnslydePresentation.html" );
//        bw.write( );
//        bw.flush();
//        bw.close();
//         responsey.
//        Response.ResponseBuilder response = Response.ok((Object) file);
//        response.header("Content-Disposition", "attachment; filename=\"howtodoinjava.txt\"");
//        return response.build();
//        return Response.ok("{}", MediaType.APPLICATION_JSON).build();

    }


    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/html")
    public void createPres(PresTemplate presTemplate) throws ServletException, IOException {
        int presID = 0;
        if(presTemplate.getSessionId() == null || presTemplate.getSessionId().isEmpty()){

            try {
                presID = memberReg.createPresentation(presTemplate.getEmail(),presTemplate.getToken(),presTemplate.getPresName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            presID = Integer.parseInt(presTemplate.getSessionId());
        }

        HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
        HttpServletResponse response = ResteasyProviderFactory.getContextData(HttpServletResponse.class);

        request.setAttribute("eventid", presID);
        request.setAttribute("presName", presTemplate.getPresName());
        request.setAttribute("poll1", presTemplate.getPoll1());
        request.setAttribute("option1", presTemplate.getOption1());
        request.setAttribute("option2", presTemplate.getOption2());


//        response.setHeader("Access-Control-Allow-Origin","http://localhost:8001");
//        response.sendRedirect("/prestemplate.jsp");
        request.getRequestDispatcher("/prestemplate.jsp").forward(request, response);
//         responsey.
//        Response.ResponseBuilder response = Response.ok((Object) file);
//        response.header("Content-Disposition", "attachment; filename=\"howtodoinjava.txt\"");
//        return response.build();
//        return Response.ok("{}", MediaType.APPLICATION_JSON).build();

    }

}
