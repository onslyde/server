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

import com.onslyde.data.MemberRepository;
import com.onslyde.domain.SessionHome;
import com.onslyde.domain.User;
import com.onslyde.domain.UserHome;
import com.onslyde.model.Mediator;
import com.onslyde.util.ClientEvent;
import org.eclipse.jetty.websocket.api.Session;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

@Path("/members")
@RequestScoped
@Stateful
public class MemberService {
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    UserHome userHome;

    @Inject
    SessionHome sessionHome;

    @Inject
    private MemberRepository repository;

    @Inject
    MemberRegistration registration;

    private class UserSummary {
        String name;
        Date created;
        List Sessions;
        String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public List getSessions() {
            return Sessions;
        }

        public void setSessions(List sessions) {
            Sessions = sessions;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMember(User user) {

        Response.ResponseBuilder builder = null;
        int sessionId;
        try {
            //Validates member using bean validation
            validateMember(user);

            sessionId = registration.register(user);
            try {
                sendMessage(user.getEmail(),sessionId);
            } catch (Exception e) {
                log.severe("Problem sending email");
                e.printStackTrace();
            }
            //Create an "ok" response
            builder = Response.ok().entity("{\"sessionId\":\"" + sessionId + "\"}");
        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            //Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }


    /**
     * <p>Validates the given Member variable and throws validation exceptions based on the type of error.
     * If the error is standard bean validation errors then it will throw a ConstraintValidationException
     * with the set of the constraints violated.</p>
     * <p>If the error is caused because an existing member with the same email is registered it throws a regular
     * validation exception so that it can be interpreted separately.</p>
     *
     * @param user Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException          If member with the same email already exists
     */
    private void validateMember(User user) throws ConstraintViolationException, ValidationException {
        //Create a bean validator and check for issues.
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        //Check the uniqueness of the email address
        if (emailAlreadyExists(user.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message.
     * This can then be used by clients to show violations.
     *
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    /**
     * Checks if a member with the same email address is already registered.  This is the only way to
     * easily capture the "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    public boolean emailAlreadyExists(String email) {
        User user = null;
        try {
            user = repository.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }
        return user != null;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegister(@FormParam("email") String name, @FormParam("email") String email, @FormParam("password") String password) {
        User user = null;
        UserSummary userSummary = new UserSummary();
        log.info("User login attempt: " + email);
        try {
            user = repository.findByEmail(email);
            if(password.equals(user.getPassword())){
                userSummary.setName(user.getFullName());
                userSummary.setCreated(user.getCreated());
                userSummary.setEmail(user.getEmail());
                userSummary.setSessions(sessionHome.findByUser(user));
                System.out.println(user.getFullName() + " logged in.");
            }
        } catch (NoResultException e) {
            log.severe("Bad login: User does not exist");
            userSummary.setName("Not Found");
        }
        return Response.ok(userSummary, MediaType.APPLICATION_JSON).build();
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(@FormParam("email") String email, @FormParam("password") String password) {
        User user = null;
        UserSummary userSummary = new UserSummary();
        log.info("User login attempt: " + email);
        try {
            user = repository.findByEmail(email);
            if(password.equals(user.getPassword())){
                userSummary.setName(user.getFullName());
                userSummary.setCreated(user.getCreated());
                userSummary.setEmail(user.getEmail());
                userSummary.setSessions(sessionHome.findByUser(user));
                System.out.println(user.getFullName() + " logged in.");
            }
        } catch (NoResultException e) {
            log.severe("Bad login: User does not exist");
            userSummary.setName("Not Found");
        }
        return Response.ok(userSummary, MediaType.APPLICATION_JSON).build();
    }


    private boolean sendMessage(String email, int sessionID){
        String host = "smtpout.secureserver.net";
        final String from = "help@onslyde.com";
        final String pass = "*****";
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true"); // added this line
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");

        String[] to = {email}; // added this line
        String bcc = "wesleyhales@gmail.com";
//        String bcc2 = "darren@curiomusic.com";

        javax.mail.Authenticator authenticator = new javax.mail.Authenticator()
        {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication()
            {
                return new javax.mail.PasswordAuthentication(from, pass);
            }
        };
        javax.mail.Session session = javax.mail.Session.getInstance(props, authenticator);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("help@onslyde.com"));

            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for( int i=0; i < to.length; i++ ) { // changed from a while loop
                toAddress[i] = new InternetAddress(to[i]);
            }
            InternetAddress bccAddress = new InternetAddress(bcc);
//            InternetAddress bccAddress2 = new InternetAddress(bcc2);
            System.out.println("send email to:" + email);

            for( int i=0; i < toAddress.length; i++) { // changed from a while loop
                message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress[i]);
                message.addRecipient(Message.RecipientType.BCC, bccAddress);
//                message.addRecipient(Message.RecipientType.BCC, bccAddress2);
            }
            message.setSubject("Welcome to onslyde!");
            message.setText("Check it out. Here's your first sessionID:" + sessionID + "\nYou'll need it to create a private presentation. Go here for setup info: https://github.com/wesleyhales/onslyde/blob/master/README.md");
            Transport transport = session.getTransport("smtps");
            transport.connect(host,from,pass);
            System.out.println("-------send mail");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;

    }
}
