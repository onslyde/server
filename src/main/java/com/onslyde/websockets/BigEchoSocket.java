package com.onslyde.websockets;

import java.nio.ByteBuffer;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Example Socket for echoing back Big data using the Annotation techniques along with stateless techniques.
 */
@WebSocket(maxMessageSize = 64 * 1024)
public class BigEchoSocket
{
    private static final Logger LOG = Log.getLogger(BigEchoSocket.class);

    @OnWebSocketMessage
    public void onBinary(Session session, byte buf[], int offset, int length)
    {
        if (!session.isOpen())
        {
            LOG.warn("Session is closed");
            return;
        }
        session.getRemote().sendBytesByFuture(ByteBuffer.wrap(buf,offset,length));
    }

    @OnWebSocketMessage
    public void onText(Session session, String message)
    {
        if (!session.isOpen())
        {
            LOG.warn("Session is closed");
            return;
        }
        session.getRemote().sendStringByFuture(message);
    }
}
