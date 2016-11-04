package de.martinbussmann.sunspecmonitor.servlet;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.martinbussmann.sunspecmonitor.modbus.ModbusReader;

public class SunspecReadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
    	ModbusReader modbus = new ModbusReader();
    	ArrayList<Integer> modbusResult = modbus.getModbusData(1, 0, 10);
    	
    	String json = "{\"I_DC_Power\": " + modbusResult.get(0) + 
    			", \"M_AC_Power\": " + modbusResult.get(1) + 
    			", \"M_AC_Power1\": " + modbusResult.get(1) + 
    			", \"M_AC_Power2\": " + modbusResult.get(1) + 
    			", \"M_AC_Power3\": " + modbusResult.get(1) + 
    			"}";
    	
        response.setContentType("text/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(json);       
    }
}
