package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import java.util.*;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}
    
    private static Document toXML(String xml) throws Exception {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	factory.setNamespaceAware(true);
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	return builder.parse(new ByteArrayInputStream(xml.getBytes()));
    }
    
    private static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    private static String[] getCategories(Document dom){
    	NodeList categoryNodes = dom.getElementsByTagName("Category");
    	String[] categories = new String[categoryNodes.getLength()];
    	
    	for(int i=0;i<categoryNodes.getLength();i++){
    		categories[i] = getElementText((Element)categoryNodes.item(i));
    	}
    	
    	return categories;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String itemId = (String) request.getParameter("id");
        String xmlData = AuctionSearch.getXMLDataForItemId(itemId);
        
        try{
        	Document dom = toXML(xmlData);
        	
        	request.setAttribute("Name", getElementText((Element) dom.getElementsByTagName("Name").item(0)));
        	request.setAttribute("Started", getElementText((Element) dom.getElementsByTagName("Started").item(0)));
        	request.setAttribute("Ends", getElementText((Element) dom.getElementsByTagName("Ends").item(0)));
        	request.setAttribute("Currently", getElementText((Element) dom.getElementsByTagName("Currently").item(0)));
        	request.setAttribute("First_Bid", getElementText((Element) dom.getElementsByTagName("First_Bid").item(0)));
        	
        	if(dom.getElementsByTagName("Buy_Price").item(0) != null){
        		request.setAttribute("Buy_Price", getElementText((Element) dom.getElementsByTagName("Buy_Price").item(0)));
        	}
        	
        	request.setAttribute("Location", getElementText((Element) dom.getElementsByTagName("Location").item(0)));
        	request.setAttribute("Country", getElementText((Element) dom.getElementsByTagName("Country").item(0)));
        	
        	Element seller = (Element) dom.getElementsByTagName("Seller").item(0);
        	request.setAttribute("SellerID", seller.getAttribute("UserID"));
        	request.setAttribute("SellerRating", seller.getAttribute("Rating"));
        	
        	
        	request.setAttribute("Categories", getCategories(dom));
        	
        	request.setAttribute("Number_of_Bids", getElementText((Element) dom.getElementsByTagName("Number_of_Bids").item(0)));
        	NodeList bidList = dom.getElementsByTagName("Bid");
        	String[][] bidInfo = new String[bidList.getLength()][3];
        	
        	for(int i=0;i<bidList.getLength();i++){
        		Element bid = (Element) bidList.item(i);
        		Element bidder = (Element) ((Element) bid).getElementsByTagName("Bidder").item(0);
        		bidInfo[i][0] = bidder.getAttribute("UserID");
        		bidInfo[i][1] = getElementText((Element) bid.getElementsByTagName("Time").item(0));
        		bidInfo[i][2] = getElementText((Element) bid.getElementsByTagName("Amount").item(0));
        	}
        	
        	request.setAttribute("bidInfo", bidInfo);
        	
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
        
        request.getRequestDispatcher("/item.jsp").forward(request, response);
    }
}
