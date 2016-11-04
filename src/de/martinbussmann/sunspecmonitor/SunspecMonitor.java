package de.martinbussmann.sunspecmonitor;
import org.eclipse.jetty.server.Handler; 
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import de.martinbussmann.sunspecmonitor.helper.ReadProperties;
import de.martinbussmann.sunspecmonitor.servlet.SunspecReadServlet;

public class SunspecMonitor {
	
	private static ReadProperties props = new ReadProperties("config.properties");
	
    public static void main(String[] args) {
        //System.setProperty("org.eclipse.jetty.servlet.LEVEL","DEBUG");
    	//System.setProperty("net.wimpi.modbus.debug", "true");
        
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(props.getIntPropertie("webserver.port"));
        server.addConnector(connector);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("htdocs/");

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(SunspecReadServlet.class, "/modbus/*");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, handler, new DefaultHandler()});

        server.setHandler(handlers);

        try
        {
            server.start();
            //server.dump(System.err);
            server.join();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }
}
