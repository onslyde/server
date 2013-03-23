/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.onslyde.util;

import java.util.List;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */
public class ClientEvent {

    public static String createEvent(String eventName, List options, int sessionID){
        String eventOptions = "";
        if(options.size() >= 0){
                 eventOptions = ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                  "\"fire\":function(){" +
                  "window.eventObjb = document.createEvent('Event');" +
                  "eventObjb.initEvent(\'updateOptions\', true, true);" +
                  "eventObjb.option1 = '" + options.get(0) + "';\n" +
                  "eventObjb.option2 = '" + options.get(1) + "';\n" +
                  "document.dispatchEvent(eventObjb);" +
                  "}}}");
        }
        return eventOptions;
    }

    public static String clientVote(String vote, int sessionID){
       return ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
               "\"fire\":function(){" +
                    "window.eventObjc = document.createEvent('Event');" +
                    "eventObjc.initEvent(\'clientVote\', true, true);" +
                    "eventObjc.vote = '" + vote + "';\n" +
                    "document.dispatchEvent(eventObjc);" +
                    "}}}");
    }

    public static String clientProps(String prop, int sessionID){
        return ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                "\"fire\":function(){" +
                "window.eventObje = document.createEvent('Event');" +
                "eventObje.initEvent(\'" + prop + "\', true, true);" +
                "document.dispatchEvent(eventObje);" +
                "}}}");
    }

    public static String updateCount(int wscount, int pollcount, int sessionID){
        return ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                "\"fire\":function(){" +
                "window.eventObjf = document.createEvent('Event');" +
                "eventObjf.initEvent(\'updateCount\', true, true);" +
                "eventObjf.wsCount = '" + wscount + "';\n" +
                "eventObjf.pollCount = '" + pollcount + "';\n" +
                "document.dispatchEvent(eventObjf);" +
                "}}}");
    }

    public static String remoteMarkup(String markup, int sessionID){
        return ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                "\"fire\":function(){" +
                "window.eventObjg = document.createEvent('Event');" +
                "eventObjg.initEvent(\'remoteMarkup\', true, true);" +
                "eventObjg.markup = '" + markup + "';\n" +
                "document.dispatchEvent(eventObjg);" +
                "}}}");
    }

    public static String roulette(int sessionID, boolean winner){
        return ("{\"onslydeEvent\":{\"sessionID\":\"" + sessionID + "\"," +
                "\"fire\":function(){" +
                "window.eventObjh = document.createEvent('Event');" +
                "eventObjh.initEvent(\'roulette\', true, true);" +
                "eventObjh.roulette = true;\n" +
                "eventObjh.winner = " + winner + ";\n" +
                "document.dispatchEvent(eventObjh);" +
                "}}}");
    }

}
