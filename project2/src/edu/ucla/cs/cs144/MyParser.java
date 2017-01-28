/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    // Datastructures for User Table
    static Map<String,String> LocationMap;
    static Map<String,String> CountryMap;
    static Map<String,String> RatingMap;
    
    // Date Formatting
    static SimpleDateFormat dtf;
    static SimpleDateFormat sqldtf;
    
    // Files
    static PrintWriter Users, Bids, Items, ItemCategory, SellerRating;
    
    // Separator
    static private String SEP = "<?>";
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    static void processBidsNode(Integer ItemID, Element bids) throws ParseException{
    	Element[] bidList = getElementsByTagNameNR(bids, "Bid");
    	String s;
    	
    	for(Element bid : bidList){
    		Element bidder = getElementByTagNameNR(bid, "Bidder");
    		String UserID = bidder.getAttribute("UserID");
    		Integer Rating = Integer.parseInt(bidder.getAttribute("Rating"));
    		
    		if(!CountryMap.containsKey(UserID)){
    			s = getElementTextByTagNameNR(bidder, "Country");
    			CountryMap.put(UserID, s == "" ? null : s);
    		}
    		
    		s = getElementTextByTagNameNR(bid, "Time");
    		Date Time = dtf.parse(s);
    		
    		Float Amount = Float.parseFloat(
	        	strip(getElementTextByTagNameNR(bid, "Amount"))
	        );
    		
    		
    		// Insert into User Table
    		if(!LocationMap.containsKey(UserID)){
    			s = getElementTextByTagNameNR(bidder, "Location");
    			LocationMap.put(UserID, s == "" ? null : s);
    			
    			s = getElementTextByTagNameNR(bidder, "Country");
    			CountryMap.put(UserID, s == "" ? null : s);
    
    	        RatingMap.put(UserID, Rating.toString());
    		}
    		
    		// Insert into Bid table
    		Bids.println(
    			ItemID + SEP +
    			UserID + SEP +
    			sqldtf.format(Time) + SEP +
    			Amount
    		);
    	}
    }
    
    
    static void processItemNode(Element item) throws ParseException{
    	String s;
    	Element e;
    	
        Integer ItemID = Integer.parseInt(item.getAttribute("ItemID"));
        
        String Name = getElementTextByTagNameNR(item, "Name");
        
        // Insert into Categories table
        for(Element Category : getElementsByTagNameNR(item, "Category")){
        	ItemCategory.println(
        		ItemID + SEP +
        		getElementText(Category)
        	);
        	
        }
        
        Float Currently = Float.parseFloat(
        	strip(getElementTextByTagNameNR(item, "Currently"))
        );
        
        s = getElementTextByTagNameNR(item, "Buy_Price");
        Float Buy_Price = s == "" ? null : Float.parseFloat(strip(s));
        
        Float First_Bid = Float.parseFloat(
        	strip(getElementTextByTagNameNR(item, "First_Bid"))
        );
        
        Integer Number_of_Bids = Integer.parseInt(
        	getElementTextByTagNameNR(item, "Number_of_Bids")
        );
        
        processBidsNode(ItemID, getElementByTagNameNR(item, "Bids"));
        
        e = getElementByTagNameNR(item, "Location");
        String Location = getElementText(e);
        s = e.getAttribute("Latitude");
        Float Latitude = s == "" ? null : Float.parseFloat(s);
        s = e.getAttribute("Longitude");
        Float Longitude = s == "" ? null : Float.parseFloat(s);
        
        String Country = getElementTextByTagNameNR(item, "Country");
        
        Date Started = dtf.parse(getElementTextByTagNameNR(item, "Started"));
        
        Date Ends = dtf.parse(getElementTextByTagNameNR(item, "Ends"));
        
        e = getElementByTagNameNR(item, "Seller");
        String SellerUserID = e.getAttribute("UserID");
        Integer Rating = Integer.parseInt(e.getAttribute("Rating"));
        
        String Description = getElementTextByTagNameNR(item, "Description");
        
        // Insert data into Users table
        
        // Insert data into SellerRating table
        SellerRating.println(
        	SellerUserID + SEP + 
			Rating
        );
        
        // Insert data into Items Table
        Description = Description.substring(0, Math.min(4000 + 1, Description.length()));
    	
        Items.println(
        	ItemID + SEP +
        	SellerUserID + SEP +
        	Name + SEP +
        	Currently + SEP +
        	Buy_Price + SEP + 
        	First_Bid + SEP + 
        	Location + SEP +
        	Longitude + SEP +
        	Latitude + SEP + 
        	Country + SEP +
        	sqldtf.format(Started) + SEP +
        	sqldtf.format(Ends) + SEP + 
        	Description
        );
	}
	
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) throws ParseException {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        Element[] itemList = getElementsByTagNameNR(
        	(Element) doc.getDocumentElement(), 
        	"Item"
        );
        
        for(Element e : itemList){
        	processItemNode(e);
        }
        
        
        /**************************************************************/
        
    }
    
    public static void main (String[] args) throws Exception {
    	// Datastructures for User Table
        LocationMap = new HashMap<String,String>();
        CountryMap = new HashMap<String,String>();
        RatingMap = new HashMap<String,String>();
        
        // DateTime Formatter
        dtf = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        sqldtf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        
        // Directory and file structures
        File parsed = new File("parsed");
        if(!parsed.exists()){
        	parsed.mkdir();
        }
        
        Users = new PrintWriter("parsed/Users.dat", "UTF-8");
        Bids = new PrintWriter("parsed/Bids.dat", "UTF-8");
        Items = new PrintWriter("parsed/Items.dat", "UTF-8");
        ItemCategory = new PrintWriter("parsed/ItemCategory.dat", "UTF-8");
        SellerRating = new PrintWriter("parsed/SellerRating.dat", "UTF-8");
        
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
        
        /* Write to Users table */
        for(String UserID : RatingMap.keySet()){
        	Users.println(
        		UserID + SEP +
        		LocationMap.get(UserID) + SEP +
        		CountryMap.get(UserID) + SEP +
        		RatingMap.get(UserID)
        	);
        }
        
        Users.close();
        Bids.close();
        Items.close();
        ItemCategory.close();
        SellerRating.close();
        
        
    }
}
