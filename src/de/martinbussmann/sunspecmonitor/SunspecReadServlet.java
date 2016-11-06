package de.martinbussmann.sunspecmonitor;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SunspecReadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
    	String json = null;
    	json = (String) this.getServletContext().getAttribute("sunspec.result");
    	
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
