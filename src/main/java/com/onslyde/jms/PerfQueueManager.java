package com.onslyde.jms;

import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: wesleyhales
 * Date: 8/11/12
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
@ApplicationScoped
public class PerfQueueManager {

    private static final String DEFAULT_USERNAME = "quickstartUser";
    private static final String DEFAULT_PASSWORD = "quickstartPassword";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    Context context;
    ConnectionFactory connectionFactory;
    Destination destination;
    Connection connection;
    Session session;
    MessageProducer messageProducer;
    MessageConsumer consumer;

    private int incomingMsgs = 1;

    private Timer timer = null;

    private void startTimer(final int number){
        if(timer == null){
            if(number >= 0){
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run()  {
                        // do stuff
                        System.out.println("poll" + number);
                        //if(number >= 0){
                            runTest();
                            System.out.println("poll");
                        //}

                    }
                }, 10000, 10000);
            }
        }
    }

    private void setupJMS() throws NamingException, JMSException {
        if(context == null){
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
            context = new InitialContext(env);
            //if(connectionFactory == null){
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            connectionFactory = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
            //}
            destination = (Destination) context.lookup("jms/queue/test");
            context.close();
            connection = connectionFactory.createConnection("quickstartUser","quickstartPassword");
            session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            messageProducer = session.createProducer(destination);
            consumer = session.createConsumer(destination);
            connection.start();
        }
        //System.out.println("connection " + connection.toString());
    }

    public int storeMessage(String url, String taskName, String uuid){
        startTimer(incomingMsgs);

        try {
            setupJMS();

            MapMessage message = session.createMapMessage();
            message.setString("url",url);
            message.setString("taskName", taskName);
            message.setString("uuid",uuid);

            System.out.println("===========" + incomingMsgs);
            messageProducer.send(message);
            incomingMsgs++;

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return incomingMsgs;
    }

    public String runTest(){
        //Map<String,String> tempMap = null;
        String url = "";
        String cached = "false";
        String random = "";
        HashMap<String,String> tempMap = new HashMap<String, String>();
        String taskName = "performance";
        MapMessage message = null;
        if(incomingMsgs == 0){
            System.out.println("----------closing connection");
            timer.cancel();
            timer.purge();
            timer = null;
            try {
                if (context != null) {
                    context.close();
                    context = null;
                }
                // closing the connection takes care of the session, producer, and consumer
                if (connection != null) {
                    connection.close();
                }
            } catch (NamingException e) {
                e.printStackTrace();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }else{

            try {
                setupJMS();
                message = (MapMessage)consumer.receive(1000);
                url = message.getString("url");
                taskName = message.getString("taskName");
                random = message.getString("uuid");
                System.out.println("Received request for: " + url + "--" + taskName + "--" + random + "--" + incomingMsgs);
                //tempMap = ((HashMap)message.getObject("tempMap"));
                incomingMsgs--;

                //running = true;

            } catch (NamingException e) {
                e.printStackTrace();
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                timer = null;
            }


            //System.out.println("---" + url);


            try
            {

                for(int i = 0; i <= 5; i++) {
                    Process p=Runtime.getRuntime().exec("phantomjs --disk-cache=no loadreport.js "+ url +" "+ taskName +" json " + random );
                    p.waitFor();
                    Thread.sleep(2000);
//                    BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
//                    String line=reader.readLine();
//                    while(line!=null)
//                    {
//                        line=reader.readLine();
//                    }
                }
                Process p=Runtime.getRuntime().exec("phantomjs --disk-cache=no speedreport.js "+ url +" "+ random );
                p.waitFor();
            }
            catch(IOException e1) {
                e1.printStackTrace();
                return "#fail";
            }
            catch(InterruptedException e2) {}

            System.out.println("Done : " + random);

            //if(incomingMsgs == 0){
            //runTest();
            //break;
            //}

            //running = false;

        }



        //return builder.build();
        return random;


    }
}
