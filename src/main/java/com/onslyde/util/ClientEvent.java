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
