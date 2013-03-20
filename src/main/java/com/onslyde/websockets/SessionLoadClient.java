package com.onslyde.websockets;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class SessionLoadClient {

    public static void main(String... arg) throws Exception
    {

      WebSocketClientFactory factory = new WebSocketClientFactory();
        factory.setBufferSize(4096);
        factory.start();

      WebSocketClient client = factory.newWebSocketClient();
      //client.setBufferSize(4096);
      client.setMaxIdleTime(300000);
      client.setProtocol("chat");
      //client.start();

        final long startTime = System.currentTimeMillis();
        final long endTime;

        for (int i =33;i<43;i++){
        try {



      final int marker = i;
      WebSocket.Connection connection = client.open(new URI("ws://192.168.1.2:8081?session=1" + i), new WebSocket.OnTextMessage()
       {
         public void onOpen(Connection connection)
         {
           // open notification
             //System.out.println("-----------------------1");
         }

         public void onClose(int closeCode, String message)
         {
           // close notification
            System.out.println("-----------------------CLOSE Deck: " + marker + " : " + message);
         }

         public void onMessage(String data)
         {
             System.out.println("-----------------------3:" + marker + " : " + data);
           // handle incoming message
         }
       }).get(5, TimeUnit.SECONDS);

       connection.sendMessage("::connect::");
        connection.sendMessage("activeOptions:test1,test2");



        } finally {
//          endTime = System.currentTimeMillis();
        }
//        final long duration = endTime - startTime;
//        System.out.println("-----------------------That took: " + duration + "ms");

    }
    }


}
