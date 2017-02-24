package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String query = null;
    	Integer numResultsToSkip, numResultsToReturn;
    	
    	try{
    		query = request.getParameter("query");
    		if(query != null){
    			numResultsToSkip = Integer.parseInt(request.getParameter("numResultsToSkip"));
        		numResultsToReturn = Integer.parseInt(request.getParameter("numResultsToReturn"));
        		
        		request.setAttribute("query", query);
        		request.setAttribute("numResultsToSkip", numResultsToSkip);
        		request.setAttribute("numResultsToReturn", numResultsToReturn);
        		
        		SearchResult[] results = AuctionSearch.basicSearch(
        			query, 
        			numResultsToSkip, 
        			numResultsToReturn
        		);
        		
        		String[] itemIds = new String[results.length];
        		String[] itemNames = new String[results.length];
        		
        		for(int i = 0;i<results.length;i++){
        			itemIds[i] = results[i].getItemId();
        			itemNames[i] = results[i].getName();
        		}
        		
        		request.setAttribute("itemIds", itemIds);
        		request.setAttribute("itemNames", itemNames);
        		
    		}
    		
    		
    		request.getRequestDispatcher("/search.jsp").forward(request, response);
    	}
    	catch(Exception e){
    		PrintWriter out = response.getWriter();
	        out.println("<html>");
	        out.println("<head><title>Error</title></head>");
	        out.println("<body><h1>500: Server Error</h1></body>");
	        e.printStackTrace(out);
	        out.println("</html>");
	        out.close();
    	}
    }
}
