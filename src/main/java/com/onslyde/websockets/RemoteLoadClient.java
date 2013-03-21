package com.onslyde.websockets;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class RemoteLoadClient {

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
    for (int i =1;i<500;i++){
          try {



          final int marker = i;
          final WebSocket.Connection connection = client.open(new URI("ws://192.168.1.2:8081?session=139"), new WebSocket.OnTextMessage()
           {
             public void onOpen(Connection connection)
             {
               // open notification
                 //System.out.println("-----------------------1");
             }

             public void onClose(int closeCode, String message)
             {
               // close notification
                System.out.println("-----------------------CLOSE: " + marker + " : " + message);
             }

             public void onMessage(String data)
             {
                 System.out.println("-----------------------3:" + marker + " : " + data);
               // handle incoming message
             }
           }).get(5, TimeUnit.SECONDS);
//              for (int j =0;j<1;j++){
//                     Timer timer = new Timer();
//                  timer.schedule(new TimerTask() {
//                      public void run()  {
//                          // do stuff
//                          //System.out.println("TimerTask running poll1: " + incomingMsgs);
                          try {
                              connection.sendMessage("vote:test1");
                          } catch (IOException e) {
                              e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                          }
//
//                      }
//                  }, 2000, 120000);


//              }



            } finally {
//              endTime = System.currentTimeMillis();
            }
//            final long duration = endTime - startTime;
//            System.out.println("-----------------------That took: " + duration + "ms");

    }

    }


    }


