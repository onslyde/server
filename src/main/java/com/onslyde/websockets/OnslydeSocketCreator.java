package com.onslyde.websockets;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

/**
 * Created with IntelliJ IDEA.
 * User: wesleyhales
 * Date: 7/16/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class OnslydeSocketCreator implements WebSocketCreator
{

    private OnslydeWebSocketHandler onslydeSocket = new OnslydeWebSocketHandler();

    @Override
    public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp)
    {
        return onslydeSocket;
    }

}
