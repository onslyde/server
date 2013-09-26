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

import com.onslyde.domain.Session;
import com.onslyde.domain.SessionHome;
import com.onslyde.domain.SlideGroupHome;
import com.onslyde.model.Mediator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestScoped
@Path("/analytics")
public class AnalyticsService {

    @Inject
    private SessionHome sessionHome;

    @Inject
    private SlideGroupHome sgHome;

    @Inject
    private Mediator mediator;

    @GET
    @Produces("application/json")
    @Path("/{session:[0-9][0-9]*}")
    public Session getSessionData(@PathParam("session") int session){
        Session mySession = sessionHome.findById(session);
        return mySession;
    }

//    @GET
//    @Produces("application/json")
//    @Path("/remove/{session:[0-9][0-9]*}")
//    public String removeSessionVotes(@PathParam("session") int session){
//
//        try {
//            Session mySession = sessionHome.findById(session);
//            sgHome.removeBySessionId(mySession);
//            return "success!!!";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "fail!!";
//        }
//    }

    private class AllSessions{
        List<SessionSummary> sessionSummaries;

        public List<SessionSummary> getSessionSummaries() {
            return sessionSummaries;
        }

        public void setSessionSummaries(List<SessionSummary> sessionSummaries) {
            this.sessionSummaries = sessionSummaries;
        }
    }
    private class SessionSummary{
        int pollingCount = 0;
        int wsCount = 0;
        int sessionID;

        public int getSessionID() {
            return sessionID;
        }

        public void setSessionID(int sessionID) {
            this.sessionID = sessionID;
        }

        public int getPollingCount() {
            return pollingCount;
        }

        public void setPollingCount(int pollingCount) {
            this.pollingCount = pollingCount;
        }

        public int getWsCount() {
            return wsCount;
        }

        public void setWsCount(int wsCount) {
            this.wsCount = wsCount;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/status")
    public Response allSessionStats(){
        AllSessions allSessions = new AllSessions();
        allSessions.setSessionSummaries(new ArrayList<SessionSummary>());
        try {

            for (int key : mediator.getSessions().keySet()){
                SessionSummary ss = new SessionSummary();
                ss.setSessionID(key);
                if(mediator.getPollCount().get(key) != null){
                    ss.setPollingCount(mediator.getPollCount().get(key).size());
                }
                if(mediator.getSessions().get(key) != null){
                    ss.setWsCount(mediator.getSessions().get(key).size());
                }
                allSessions.getSessionSummaries().add(ss);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok(allSessions, MediaType.APPLICATION_JSON).build();
    }



}
