package com.onslyde.websockets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: wesleyhales
 * Date: 7/31/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */

@WebSocket
public class SessionLoadClientSocket {
    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;
    int index = 0;
    public SessionLoadClientSocket(Integer index)
    {
        this.closeLatch = new CountDownLatch(1);
        this.index = index;
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
    {
        return this.closeLatch.await(duration,unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.printf("Connection closed: %d - %s%n",statusCode,reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.printf("Got connect: %s%n",session);
        this.session = session;
        try
        {
            Future<Void> fut;
            session.getRemote().sendStringByFuture("::connect::").get(2,TimeUnit.SECONDS);
            session.getRemote().sendStringByFuture("activeOptions:test1" + index + ",test2" + index + ",1:0").get(2,TimeUnit.SECONDS);


//            fut = session.getRemote().sendStringByFuture("Thanks for the conversation.");
//            fut.get(2,TimeUnit.SECONDS); // wait for send to complete.
            System.out.println("----closing: " + index);
//            session.close(StatusCode.NORMAL,"I'm done");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        System.out.printf("Got msg: %s%n",msg);
    }
}
