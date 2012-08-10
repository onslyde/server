package com.onslyde.service;


import com.onslyde.jms.PerfJMSClient;
import com.onslyde.util.ClientEvent;
import com.onslyde.util.SimpleQueue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.*;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;

@Path("/performance")
@Singleton
@Stateful
public class PerformanceService {

    //@Inject
    //PerfJMSClient perfJMSClient;

//    @PostConstruct
//    public void init(){
////        try {
////            //perfJMSClient.init();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }

    public int incomingMsgs = 0;
    private Timer timer = null;

    private void startTimer(final int number){
        if(timer == null){
        if(number > 1){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run()  {
                    // do stuff
                    System.out.println("poll" + number);
                    if(number >= 1){
                        runTest();
                        System.out.println("poll");
                    }

                }
            }, 5000, 5000);
            }
        }
    }

    Map<String,String> tempMap = new HashMap<String,String>();

//    @Resource(mappedName = "java:/ConnectionFactory")
//    private ConnectionFactory connectionFactory;
//    @Resource(mappedName = "java:jms/queue/test")
//    pr
// ivate Queue queue;

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
        System.out.println("connection " + connection.toString());
    }

    @GET
    @Path("/go")
    @Produces("text/html")
    public String go(@QueryParam("url") String url, @QueryParam("cached") String cached) {
        String retVal = "";
        String taskName = "";

        tempMap.put("url",url);
        tempMap.put("cached",cached);
        startTimer(incomingMsgs);
        UUID random = UUID.randomUUID();
        //if(incomingMsgs > 0){
        boolean keepgoing = false;
        try {
            URL urltemp = new URL(url);
            URLConnection conn = urltemp.openConnection();
            conn.connect();
            keepgoing = true;
        } catch (MalformedURLException e) {
            // the URL is not in a valid form
            keepgoing = false;
        } catch (IOException e) {
            // the connection couldn't be established
            keepgoing = false;
        }
        Response.ResponseBuilder builder = null;

        if(keepgoing){
            if(cached.equals("true")){
                taskName = "performancecache";
            }

            try {
                setupJMS();
                TextMessage message = session.createTextMessage();
                //for (int i = 0; i < 5; i++) {
                    message.setText(url);
                    System.out.println("===========" + incomingMsgs);
                    messageProducer.send(message);
                    incomingMsgs++;
                    retVal = random.toString();
                //}
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Bad URL");
            return "#fail";
        }

        return retVal;


    }
    public String runTest(){


        //Map<String,String> tempMap = null;
        String url = "";
        String cached = "false";
        String random = "";

        String taskName = "performance";

        if(incomingMsgs == 0){
            timer.cancel();
            timer.purge();
            timer = null;
        }else{

    //        if(incomingMsgs > 1){
    //            try {
    //                Thread.sleep(10000 * incomingMsgs);
    //            } catch (InterruptedException e) {
    //                e.printStackTrace();
    //            }
    //            //runTest(url);
    //        }

            try {
                setupJMS();
                TextMessage message = (TextMessage) consumer.receive(1000);
                System.out.println("Received message with content " + message + "---" + incomingMsgs);
                url = message.getText();
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
                    Process p=Runtime.getRuntime().exec("phantomjs --disk-cache=no confess-mod.js "+ url +" "+taskName+" json " + random.toString() );
                    //p.waitFor();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line=reader.readLine();
                    while(line!=null)
                    {
                    //    System.out.println(line);
                        line=reader.readLine();
                    }
                }
            }
            catch(IOException e1) {return "#fail";}
    //        catch(InterruptedException e2) {}

            System.out.println("Done : " + incomingMsgs);

                //if(incomingMsgs == 0){
                    //runTest();
                    //break;
                //}

                //running = false;

        }



        //return builder.build();
        return "/rest/performance/report?uuid=" + random.toString();


    }


    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public String report(@QueryParam("uuid") String uuid) {

        Response.ResponseBuilder builder = null;

        builder = Response.ok();
        String all = "";
        try {
            BufferedReader in;
            File locatedFile = new File("/Users/wesleyhales/www/jboss-as-7.1.1.Final/reports/confess-report-" + uuid + ".json");
            if(locatedFile.exists()) {
                in = new BufferedReader(new FileReader("/Users/wesleyhales/www/jboss-as-7.1.1.Final/reports/confess-report-" + uuid + ".json"));
            }else{
                return "#fail";
            }

            String ln;

            while ((ln = in.readLine()) != null)
                all += ln;
            in.close();
            //System.out.println(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return all;
    }


}
