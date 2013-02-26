/**
 * onslyde, Audience engagement
 * Copyright 2012, Hales Consulting, Inc., and individual contributors
*/
package com.onslyde.service;

import com.onslyde.data.MemberRepository;
import com.onslyde.domain.User;
import com.onslyde.domain.UserHome;
import com.onslyde.model.Member;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 */
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
    private MemberRepository repository;

    @Inject
    MemberRegistration registration;

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Member> listAllMembers() {
//        return repository.findAllOrderedByName();
//    }

//    @GET
//    @Path("/{id:[0-9][0-9]*}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Member lookupMemberById(@PathParam("id") long id) {
//        Member member = repository.findById(id);
//        if (member == null) {
//            throw new WebApplicationException(Response.Status.NOT_FOUND);
//        }
//        return member;
//    }

    /**
     * Creates a new member from the values provided.  Performs validation, and will return a JAX-RS response with either
     * 200 ok, or with a map of fields, and related errors.
     */
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


    private boolean sendMessage(String email, int sessionID){
        String host = "smtp.gmail.com";
        final String from = "onslyde@gmail.com";
        final String pass = "onslyde123";
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true"); // added this line
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        String[] to = {email}; // added this line
        String bcc = "wesleyhales@gmail.com";
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
            message.setFrom(new InternetAddress("admin@onslyde.com"));

            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for( int i=0; i < to.length; i++ ) { // changed from a while loop
                toAddress[i] = new InternetAddress(to[i]);
            }
            InternetAddress bccAddress = new InternetAddress(bcc);
            System.out.println("send email to:" + email);

            for( int i=0; i < toAddress.length; i++) { // changed from a while loop
                message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress[i]);
                message.addRecipient(Message.RecipientType.BCC, bccAddress);
            }
            message.setSubject("Welcome to onslyde!");
            message.setText("Check it out. Here's your first sessionID:" + sessionID + "\n You'll need it to create your first presentation. Go here for setup info: https://github.com/wesleyhales/onslyde/blob/master/README.md");
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
