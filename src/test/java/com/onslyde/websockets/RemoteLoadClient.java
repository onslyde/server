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
public class RemoteLoadClient {

    public static void main(String[] args)
    {

        String destUri = "wss://www.onslyde.com/ws/?session=175";

    final long startTime = System.currentTimeMillis();
    final long endTime;
    for (int i =33;i<44;i++){


        WebSocketClient client = new WebSocketClient();
        RemoteLoadClientSocket socket = new RemoteLoadClientSocket(i);
    try
    {
        client.start();

        URI echoUri = new URI("wss://www.onslyde.com/ws/?session=1" + i + "&attendeeIP=111.111.111.111");
        ClientUpgradeRequest request = new ClientUpgradeRequest();

        client.connect(socket,echoUri,request);
        System.out.printf("Connecting to : %s%n",echoUri);

        // wait for closed socket connection.
        socket.awaitClose(60, TimeUnit.SECONDS);
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
            client.stop();
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
