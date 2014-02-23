package com.onslyde.service;

import com.onslyde.domain.QuestionsStaged;
import com.onslyde.domain.QuestionsStagedHome;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wesleyhales
 * Date: 2/21/14
 * Time: 8:21 AM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@Path("/questions")
public class QuestionService {


  @Inject
  private EntityManager em;

  @Inject
  private QuestionsStagedHome qsh;

  @Inject
  private QuestionsStaged qs;


  @GET
  @Path("/check")
  @Produces(MediaType.APPLICATION_JSON)
  public Response checkQuestions(@QueryParam("id") String id) {


//    Long tempPollTime = null;
    QuestionsStaged currentQuestion = null;
    //convert the onclick timestamp... might be a bit overprotective with the try/catch
//    try {
//      tempPollTime = Long.valueOf(currentTime);
//    } catch (NumberFormatException e) {
//      e.printStackTrace();
//    }

//    Date pollTime = new Date(tempPollTime);

    List<QuestionsStaged> questions = qsh.findByIdentifier(id);

    for (QuestionsStaged question : questions) {
      System.out.println("--------question loop" + question.getTimeStart() + " end:" + question.getTimeEnd());
//      System.out.println("--------question loop" + pollTime.after(question.getTimeStart()) + " " + pollTime.before(question.getTimeEnd()));
//      if(pollTime.after(question.getTimeStart()) && pollTime.before(question.getTimeEnd())){
//        System.out.println("--------question is ready... start:" + question.getTimeStart() + " end:" + question.getTimeEnd() + " pollTime:" + pollTime);
//        currentQuestion = question;
//      }
    }

    Response.ResponseBuilder response = Response.ok(questions, MediaType.APPLICATION_JSON);

    response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    response.header("Access-Control-Allow-Origin", "*");
    //response.header("Access-Control-Allow-Origin", request.getHeader("Origin"));
    response.header("Access-Control-Allow-Headers", "accept, origin, ag-mobile-variant, content-type");
    response.header("Content-Type", "text/plain");

    return response.build();

  }


}
