package com.onslyde.websockets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Example server using WebSocket and core Jetty Handlers
 */
@WebListener
public class ExampleEchoServer implements ServletContextListener
{

    @Inject
    @Any
    private Event<ServletContext> servletContextEvent;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        servletContextEvent.select(new AnnotationLiteral<Initialized>() {}).fire(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        servletContextEvent.select(new AnnotationLiteral<Destroyed>() {}).fire(sce.getServletContext());
    }



    /**
     * Stop Embedding Jetty server when WEB Application is stopped.
     */

    private static final Logger LOG = Log.getLogger(ExampleEchoServer.class);

//    public static void main(String... args)
//    {
//        try
//        {
//            int port = 8080;
//            boolean verbose = false;
//            String docroot = "src/test/webapp";
//
//            for (int i = 0; i < args.length; i++)
//            {
//                String a = args[i];
//                if ("-p".equals(a) || "--port".equals(a))
//                {
//                    port = Integer.parseInt(args[++i]);
//                }
//                else if ("-v".equals(a) || "--verbose".equals(a))
//                {
//                    verbose = true;
//                }
//                else if ("-d".equals(a) || "--docroot".equals(a))
//                {
//                    docroot = args[++i];
//                }
//                else if (a.startsWith("-"))
//                {
//                    usage();
//                }
//            }
//
//            ExampleEchoServer server = new ExampleEchoServer(port);
//            server.setVerbose(verbose);
//            server.setResourceBase(docroot);
//            server.runForever();
//        }
//        catch (Exception e)
//        {
//            LOG.warn(e);
//        }
//    }

//    private static void usage()
//    {
//        System.err.println("java -cp{CLASSPATH} " + ExampleEchoServer.class + " [ OPTIONS ]");
//        System.err.println("  -p|--port PORT    (default 8080)");
//        System.err.println("  -v|--verbose ");
//        System.err.println("  -d|--docroot file (default 'src/test/webapp')");
//        System.exit(1);
//    }
//
//    private Server server;
//
//    private ServerConnector connector;
//    private boolean _verbose;
//    private WebSocketHandler wsHandler;
//    private ResourceHandler rHandler;

//    public ExampleEchoServer(int port)
//    {
//        server = new Server();
//        connector = new ServerConnector(server);
//        connector.setPort(port);
//
//        server.addConnector(connector);
//        wsHandler = new EchoSocketHandler();
//
//        server.setHandler(wsHandler);
//
//        rHandler = new ResourceHandler();
//        rHandler.setDirectoriesListed(true);
//        rHandler.setResourceBase("src/test/webapp");
//        wsHandler.setHandler(rHandler);
//    }

//    public String getResourceBase()
//    {
//        return rHandler.getResourceBase();
//    }
//
//    public boolean isVerbose()
//    {
//        return _verbose;
//    }

//    public void runForever() throws Exception
//    {
//        server.start();
//        String host = connector.getHost();
//        if (host == null)
//        {
//            host = "localhost";
//        }
//        int port = connector.getLocalPort();
//        System.err.printf("Echo Server started on ws://%s:%d/%n",host,port);
//        System.err.println(server.dump());
//        server.join();
//    }

//    public void setResourceBase(String dir)
//    {
//        rHandler.setResourceBase(dir);
//    }
//
//    public void setVerbose(boolean verbose)
//    {
//        _verbose = verbose;
//    }
}