/**
 * 
 */

package de.martinbussmann.sunspecmonitor;
import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import de.martinbussmann.sunspecmonitor.helper.ReadProperties;

public class SunspecMonitor {
	
	private static ReadProperties props = new ReadProperties("config.properties");
	static ServletContext context = null;
	
    public static void main(String[] args) {
    	
    	int unitID = 247;
    	String host = "192.168.178.38";
    	int port = 502;
    	
	    Server server = new Server();
	    ServerConnector connector = new ServerConnector(server);
	    connector.setPort(props.getIntPropertie("webserver.port"));
	    server.addConnector(connector);
	
	    ResourceHandler resourceHandler = new ResourceHandler();
	    resourceHandler.setDirectoriesListed(true);
	    resourceHandler.setResourceBase("htdocs/");
	
	    ServletContextHandler handler = new ServletContextHandler();
	    context = handler.getServletContext();
	    context.setAttribute("sunspec.result", "[]");
	    handler.addServlet(SunspecReadServlet.class, "/modbus/*");
	    
	    HandlerList handlers = new HandlerList();
	    handlers.setHandlers(new Handler[]{resourceHandler, handler, new DefaultHandler()});
	
	    server.setHandler(handlers);
	    
    	ModbusReaderThread modbus = new ModbusReaderThread();
    	Thread thread = new Thread(modbus);
    	modbus.setContext(context);
		modbus.setUnitID(unitID);
    	modbus.setHost(host);
    	modbus.setPort(port);
    	thread.start();
	    
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
