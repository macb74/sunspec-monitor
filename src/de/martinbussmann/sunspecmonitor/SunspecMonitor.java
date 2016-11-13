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
import org.json.JSONArray;
import org.json.JSONObject;

import de.martinbussmann.sunspecmonitor.helper.ReadProperties;

public class SunspecMonitor {
	
	private static ReadProperties props = new ReadProperties("config.properties");
	static ServletContext context = null;
	
    public static void main(String[] args) {
    	
    	int unitID = props.getIntPropertie("modbus.id");
    	String host = props.getPropertie("modbus.host");
    	int port = props.getIntPropertie("modbus.port");
    	
	    Server server = new Server();
	    ServerConnector connector = new ServerConnector(server);
	    connector.setPort(props.getIntPropertie("webserver.port"));
	    server.addConnector(connector);
	
	    ResourceHandler resourceHandler = new ResourceHandler();
	    resourceHandler.setDirectoriesListed(true);
	    resourceHandler.setResourceBase("htdocs/");
	
	    ServletContextHandler handler = new ServletContextHandler();
	    context = handler.getServletContext();
	    
	    JSONObject json = initJSONObject();
	    context.setAttribute("sunspec.result", json.toString());

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
    	modbus.setGraphLength(props.getIntPropertie("graph.length"));    	
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

	private static JSONObject initJSONObject() {
	    JSONArray j = new JSONArray();
	    JSONObject json = new JSONObject();
	    json.put("I_DC_Power", j);
	    json.put("M_AC_Power", j);
	    json.put("M_AC_Power1", j);
	    json.put("M_AC_Power2", j);
	    json.put("M_AC_Power3", j);
	    json.put("Date", j);
	    json.put("My_Power", j);
	    return json;
	}
     
}
