package de.martinbussmann.sunspecmonitor.servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.martinbussmann.sunspecmonitor.modbus.ModbusReader;

public class SunspecReadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
    	String json = null;
    	ModbusReader modbus = new ModbusReader();

    	int unitID = Integer.parseInt(request.getParameter("unitID"));
    	int port   = Integer.parseInt(request.getParameter("port"));
    	
    	ArrayList<Integer> modbusResult = modbus.getModbusData(unitID, 100, 110, port, request.getParameter("host"));
    	
    	if(modbusResult != null) {
    	json = "{\"I_DC_Power\": " + modbusResult.get(0) + 
    			", \"M_AC_Power\": " + modbusResult.get(106) + 
    			", \"M_AC_Power1\": " + modbusResult.get(107) + 
    			", \"M_AC_Power2\": " + modbusResult.get(108) + 
    			", \"M_AC_Power3\": " + modbusResult.get(109) + 
    			"}";
    	} else {
        	json = "{\"error\": \"true\" }";    		
    	}
    	
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);        
        response.getWriter().println(json);    
    }

	private void setParameters(HttpServletRequest request, ServletContext context) {
		Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
        	String paramName = parameterNames.nextElement();
        	context.setAttribute(paramName, request.getParameter(paramName));
        }	
	}
}
