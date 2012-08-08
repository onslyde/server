package com.onslyde.service;

import com.onslyde.util.ClientEvent;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/performance")
@RequestScoped
@Stateful
public class PerformanceService {

    @GET
    @Path("/go")
    @Produces("text/html")
    public String go(@QueryParam("url") String url) {
        String param1AfterDecoding = "";
        UUID random = UUID.randomUUID();
//        try {
//            param1AfterDecoding = URLDecoder.decode(url, "UTF-8");
//            System.out.println(param1AfterDecoding);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
        try
        {

            for(int i = 0; i < 5; i++) {
                Process p=Runtime.getRuntime().exec("phantomjs --disk-cache=no confess-mod.js "+ url +" performance json " + random.toString() );
                //p.waitFor();
                BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line=reader.readLine();
                while(line!=null)
                {
                    System.out.println(line);
                    line=reader.readLine();
                }
            }
        }
        catch(IOException e1) {}
        //catch(InterruptedException e2) {}

            System.out.println("Done");
        }else{
            System.out.println("Bad URL");
            return "failed, sorry! check the url";
        }
        builder = Response.ok();

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
            BufferedReader in = new BufferedReader(new FileReader("/Users/wesleyhales/www/jboss-as-7.1.1.Final/confess-report-" + uuid + ".json"));
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
