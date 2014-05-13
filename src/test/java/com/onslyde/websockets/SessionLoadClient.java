package com.onslyde.websockets;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: wesleyhales
 * Date: 7/31/13
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class SessionLoadClient {

    public static void main(String[] args)
    {

//        String destUri = "ws://192.168.1.8:8081/?session=175";

    final long startTime = System.currentTimeMillis();
    final long endTime;
    for (int i =33;i<44;i++){


        WebSocketClient client = new WebSocketClient();
        SessionLoadClientSocket socket = new SessionLoadClientSocket(i);
    try
    {
        client.start();

        URI echoUri = new URI("wss://www.onslyde.com/ws?session=1" + i);
        ClientUpgradeRequest request = new ClientUpgradeRequest();

        client.connect(socket,echoUri,request);
        System.out.printf("Connecting to : %s%n",echoUri);

        // wait for closed socket connection.
        socket.awaitClose(20, TimeUnit.SECONDS);
    }
    catch (Throwable t)
    {
        t.printStackTrace();
    }
    finally
    {
        //              endTime = System.currentTimeMillis();
        try

        {
//            client.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//            final long duration = endTime - startTime;
//            System.out.println("-----------------------That took: " + duration + "ms");

    }
    }


}
