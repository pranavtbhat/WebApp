package edu.ucla.cs.cs144;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.*;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Scanner;


public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    final String URL = "http://google.com/complete/search?output=toolbar&q=%s";
    final String charset = "UTF-8";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String query = request.getParameter("query");
    	
    	
    	if(query != null){
    		String URLString = String.format(
	    		URL, 
	    		URLEncoder.encode(
	    			request.getParameter("query"),
	    			charset
	    		)
	    	);
	    	
	    	HttpURLConnection conn = (HttpURLConnection) new URL(URLString).openConnection();
	    	conn.setRequestProperty("Accept-Charset", charset);
	    	conn.setRequestMethod("GET");
	    	InputStream rstream = conn.getInputStream();
	    	
	    	Scanner s = new Scanner(rstream).useDelimiter("\\A");
	    	String result = s.hasNext() ? s.next() : null;
	    	
	    	PrintWriter out = response.getWriter();
	    	out.print(result);
    	}
    	else{
    		PrintWriter out = response.getWriter();
	    	out.print("<?xml version=\"1.0\"?><toplevel></toplevel>");
    	}
    	
    }
}
