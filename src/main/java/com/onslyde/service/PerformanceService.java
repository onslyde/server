package com.onslyde.service;


import com.onslyde.jms.PerfJMSClient;
import com.onslyde.jms.PerfQueueManager;
import com.onslyde.util.ClientEvent;
import com.onslyde.util.SimpleQueue;
import org.hornetq.jms.client.HornetQMapMessage;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.*;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Cacheable;
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
@RequestScoped
public class PerformanceService implements Serializable{

    @Inject
    PerfQueueManager perfQueueManager;

//    @PostConstruct
//    public void init(){
////        try {
////            //perfJMSClient.init();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }





//    @Resource(mappedName = "java:/ConnectionFactory")
//    private ConnectionFactory connectionFactory;
//    @Resource(mappedName = "java:jms/queue/test")
//    pr
// ivate Queue queue;



    @GET
    @Path("/go")
    @Produces("text/html")
    public String go(@QueryParam("url") String url, @QueryParam("cached") String cached) {
        String retVal = "";
        String taskName = "performance";
        int position = 0;
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

        if(keepgoing){

//            HashMap<String,String> tempMap = new HashMap<String,String>();
//            tempMap.put("url",url);
//            tempMap.put("uuid",random.toString());

            if(cached.equals("true")){
                taskName = "performancecache";
                //tempMap.put("taskName",taskName);
            }

            position = perfQueueManager.storeMessage(url,taskName,random.toString());
        }else{
            System.out.println("Bad URL");
            return "#fail";
        }

        return "{\"position\":" + position + ",\"uuid\":\"" + random.toString() + "\"}";


    }




    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public String report(@QueryParam("uuid") String uuid) {
        //todo - check to see what this uuid position is and multiply timeout
        Response.ResponseBuilder builder = null;
        //the following location string is dependent on where you start the server (from the actual directory the command is ran from).
        String location = "/Users/wesleyhales/www/jboss-as-7.1.1.Final/reports/confess-report-";
        //String location = "/home/onslyde/dev/onslyde/reports/confess-report-";
        builder = Response.ok();
        String all = "";
        try {
            BufferedReader in;
            File locatedFile = new File(location + uuid + ".json");
            if(locatedFile.exists()) {
                in = new BufferedReader(new FileReader(location + uuid + ".json"));
            }else{
                return "#fail";
            }

            String ln;

            while ((ln = in.readLine()) != null)
                all += ln;
            in.close();
            //System.out.println(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return all;
    }

    @GET
    @Path("/speedreport")
    @Produces(MediaType.TEXT_HTML)
    public String speedreport(@QueryParam("uuid") String uuid) {
        //todo - check to see what this uuid position is and multiply timeout
        Response.ResponseBuilder builder = null;
        //the following location string is dependent on where you start the server (from the actual directory the command is ran from).
        String location = "/Users/wesleyhales/www/jboss-as-7.1.1.Final/speedreports/";
        //String location = "/home/onslyde/dev/onslyde/reports/confess-report-";
        builder = Response.ok();
        String all = "";
        try {
            BufferedReader in;
            File locatedFile = new File(location + uuid + ".html");
            if(locatedFile.exists()) {
                in = new BufferedReader(new FileReader(location + uuid + ".html"));
            }else{
                return "#fail";
            }

            String ln;

            while ((ln = in.readLine()) != null)
                all += ln + "\n";
            in.close();
            //System.out.println(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return all;
    }


}
