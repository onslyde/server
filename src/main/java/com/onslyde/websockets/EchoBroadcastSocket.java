package com.onslyde.websockets;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class EchoBroadcastSocket
{
    private static final ConcurrentLinkedQueue<EchoBroadcastSocket> BROADCAST = new ConcurrentLinkedQueue<EchoBroadcastSocket>();

    protected Session session;

    @OnWebSocketMessage
    public void onBinary(byte buf[], int offset, int len)
    {
        ByteBuffer data = ByteBuffer.wrap(buf,offset,len);
        for (EchoBroadcastSocket sock : BROADCAST)
        {
            sock.session.getRemote().sendBytesByFuture(data.slice());
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        BROADCAST.remove(this);
    }

    @OnWebSocketConnect
    public void onOpen(Session session)
    {
        this.session = session;
        BROADCAST.add(this);
    }

    @OnWebSocketMessage
    public void onText(String text)
    {
        for (EchoBroadcastSocket sock : BROADCAST)
        {
            sock.session.getRemote().sendStringByFuture(text);
        }
    }
}
