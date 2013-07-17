package com.onslyde.websockets;

import com.onslyde.model.Mediator;
import com.onslyde.model.SlidFast;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: wesleyhales
 * Date: 7/16/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class EchoCreator implements WebSocketCreator
{



    
    
    private OnslydeWebSocketHandler logSocket = new OnslydeWebSocketHandler();

    @Override
    public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp)
    {

        return logSocket;

    }





//    public static synchronized SlidFast getSlidFast() {
//        if (slidFast == null) {
//            syncSlidFast(slidFast);
//        }
//        return slidFast;// == null ? new SlidFast() : slidFast;
//    }
//
//    public static synchronized Mediator getMediator() {
//        return mediator;
//    }
//
//    public static synchronized void syncMediator(Mediator mediator2) {
//        mediator = mediator2;
//    }
//
//    public void observeItemEvent(@Observes SlidFast slidFast) {
//        syncSlidFast(slidFast);
//    }
//
//    public static synchronized void syncSlidFast(SlidFast slidFast2) {
//        slidFast = slidFast2;
//    }

}
