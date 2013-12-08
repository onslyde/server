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
import com.onslyde.model.SessionManager;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/{session:[0-9][0-9]*}")
@RequestScoped
public class RemoteService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupMemberById(@PathParam("session") int session,@Context HttpServletRequest req) {
//        Session session = repository.findById(id);

        if (session == 0) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return Response.status(Response.Status.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "https://www.onslyde.com/deck/remote.html?session=" + session)
                .build();

    }
}
