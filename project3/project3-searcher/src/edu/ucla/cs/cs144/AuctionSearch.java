package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

    /* 
     * You will probably have to use JDBC to access MySQL data
     * Lucene IndexSearcher class to lookup Lucene index.
     * Read the corresponding tutorial to learn about how to use these.
     *
     * You may create helper functions or classes to simplify writing these
     * methods. Make sure that your helper functions are not public,
     * so that they are not exposed to outside of this class.
     *
     * Any new classes that you create should be part of
     * edu.ucla.cs.cs144 package and their source files should be
     * placed at src/edu/ucla/cs/cs144.
     *
     */
	
	private IndexSearcher searcher = null;
    private QueryParser parser = null;
	
    private String INDEX_DIR = "/var/lib/lucene/";
    
    public AuctionSearch() throws Exception{
    	this.searcher = new IndexSearcher(
    		DirectoryReader.open(
    			FSDirectory.open(
    					new File(INDEX_DIR + "index-1")
    			)
    		)
    	);
    	
    	this.parser = new QueryParser("content", new StandardAnalyzer());
	}

    public SearchResult[] basicSearch(String query, int numResultsToSkip, 
            int numResultsToReturn){
    	
    	SearchResult arr[] = new SearchResult[0];
    	
    	try{
            ScoreDoc[] results = searcher.search(
            	parser.parse(query),
            	numResultsToSkip + numResultsToReturn
            ).scoreDocs;
            
            int resultLength;
            
            if(results.length > numResultsToSkip){
            	resultLength = Math.min(
            		results.length - numResultsToSkip,
            		numResultsToReturn
            	);
            }
            else{
            	resultLength = 0;
            }
            
            arr = new SearchResult[resultLength];
            
            for(int i=0;i<resultLength;i++){
            	Document item = searcher.doc(results[i + numResultsToSkip].doc);
            	arr[i] = new SearchResult(
        			item.get("ItemID"),
        			item.get("Name")
    			);
            }
    	}
    	catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    	
        
        return arr;
    }

    public SearchResult[] spatialSearch(String query, SearchRegion region,
            int numResultsToSkip, int numResultsToReturn) {
        
    	Connection conn;
    	
    	// create a connection to the database to retrieve Items from MySQL
        try {
            conn = DbManager.getConnection(true);
        } catch (SQLException ex) {
            System.out.println("Couldn't establish connection to DB");
            ex.printStackTrace();
            return new SearchResult[0];
        }
        
    	SearchResult[] basicResults = basicSearch(query, 0, numResultsToReturn);
    	
    	// Prepapre the SQL statement to verify location
    	PreparedStatement stmt;
    	try{
    		// Points are:
        	// (region.lx region.ly, 
        	//  region.lx region.ry, 
        	//  region.rx region.ry, 
        	//  region.rx region.ly,
        	//  region.lx, region.ly)
    		Double lx = region.getLx();
    		String polygon = "GeomFromText('" + 
    			"POLYGON((" + 
    				region.getLx() + " " + region.getLy() + ", " + 
    				region.getLx() + " " + region.getRy() + ", " + 
    				region.getRx() + " " + region.getRy() + ", " + 
    				region.getRx() + " " + region.getLy() + ", " + 
    				region.getLx() + " " + region.getLy() + 
    			"))" + 
    		"')";
    		
    		stmt = conn.prepareStatement(
			    "SELECT ItemID, MBRContains(" + polygon + ", Location) as inRect " + 
			    "FROM ItemLocation " + 
			    " WHERE ItemID = ?"
			);
    	}
    	catch(Exception e){
    		System.out.println("Couldn't prepare statement");
    		e.printStackTrace();
    		return new SearchResult[0];
    	}
    	
    	int seen = 0;
    	int skipped = 0;
    	int filled = 0;
    	int i = 0;
    	
    	List<SearchResult> list = new ArrayList<SearchResult>();
    	
    	while(filled < numResultsToReturn && basicResults.length > 0){
    		SearchResult sr = basicResults[i++];
    		String ItemID = sr.getItemId();
    		String Name = sr.getName();
    		++seen;
    		 
    		// Execute query
    		try{  
    			stmt.setInt(1, Integer.parseInt(ItemID));
    			ResultSet rs = stmt.executeQuery();
    			if(rs.next() && rs.getBoolean("inRect")){
    				// Point lies inside SearchRegion
    				if(skipped == numResultsToSkip){
    					list.add(new SearchResult(ItemID, Name));
    					++filled;
    				}
    				else{
    					++skipped;
    				}
    			}
    			rs.close();
    		}
    		catch(Exception ex){
    			System.out.println("Couldn't execute \n" + stmt.toString());
    			ex.printStackTrace();
    			return new SearchResult[0];
    		}
    		
    		if(i == basicResults.length){
    			i = 0;
    			basicResults = basicSearch(query, seen, numResultsToReturn);
    		}
    	}
    	    	
    	return list.toArray(new SearchResult[list.size()]);
    }

    private String[] fetchCategories(String itemId, Connection conn){
    	List<String> categories = new ArrayList<String>();
    	
    	try{
    		Statement stmt = conn.createStatement();
        	ResultSet rs = stmt.executeQuery("SELECT * FROM ItemCategory WHERE ItemID = " + itemId);
        	
        	while(rs.next()){
        		categories.add(rs.getString("Category"));
        	}
        	
        	return categories.toArray(new String[categories.size()]);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return new String[0];
    	}
    }
    
    private String[] fetchBids(String itemId, Connection conn){
    	List<String> bids = new ArrayList<String>();
    	
    	try{
    		Statement stmt = conn.createStatement();
        	Statement ratingStmt = conn.createStatement();
        	
    		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
    		ResultSet rs = stmt.executeQuery("SELECT * FROM Bids WHERE ItemID = " + itemId);
    		
    		while(rs.next()){
    			String UserID = rs.getString("UserID");
    			
    			// Fetch User Rating
    			ResultSet user = ratingStmt.executeQuery(
    				"SELECT Location, Rating, Country FROM Users WHERE UserID = " + escape(UserID)
    			);
    			
    			user.next();
    			StringBuilder sb = new StringBuilder();
    			sb.append("<Bid>\n");
    			
    			sb.append(
    				"<Bidder Rating=" + 
    				escape(user.getString("Rating")) +  
    				" UserID=" + 
    				escape(processString(UserID)) + 
    				">\n"
    			);
    			
    			String Location = user.getString("Location");
    			if(!Location.equals("null")){
    				sb.append("<Location>" + processString(Location) + "</Location>\n");
    			}
    			
    			String Country = user.getString("Country");
    			if(!Country.equals("null")){
    				sb.append("<Country>" + processString(Country) + "</Country>\n");
    			}
    			sb.append("</Bidder>\n");
    			
    			Calendar time = Calendar.getInstance();
            	time.setTimeInMillis(rs.getTimestamp("Time").getTime());
    			sb.append("<Time>" + df.format(time.getTime()) + "</Time>\n");
    			sb.append("<Amount>$" + rs.getString("Amount") + "</Amount>\n");
    			
    			sb.append("</Bid>\n");
    			bids.add(sb.toString());
    		}
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		return new String[0];
    	}
    	
    	return bids.toArray(new String[bids.size()]);
    }
    
    private String fetchSeller(String UserID, Connection conn){
    	try{
    		ResultSet rs = conn.createStatement().executeQuery(
	    		"SELECT Rating FROM SellerRating WHERE UserID = " + escape(UserID)
	    	);
    			
    		if(rs.next()){
    			return "<Seller Rating=" + escape(rs.getString("Rating")) + " UserID=" + escape(UserID) + "/>\n";
    		}
    		else{
    			return "";
    		}
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    		return "";
    	}
    }
    
    public String getXMLDataForItemId(String itemId) {
    	// create a connection to the database to retrieve Items from MySQL
    	Connection conn;
    	StringBuilder sb = new StringBuilder();
    	SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
    	
        try {
            conn = DbManager.getConnection(true);
        } catch (SQLException ex) {
            System.out.println("Couldn't establish connection to DB");
            ex.printStackTrace();
            return "";
        }
        
        try{
        	Statement itemStmt = conn.createStatement();
        	ResultSet itemSet = itemStmt.executeQuery("SELECT * FROM Items WHERE ItemID = " + itemId);
        	
        	if(itemSet.next()){
            	// ItemID
            	sb.append("<Item ItemID=" + escape(itemId) + ">\n");
            	
            	// Name
            	sb.append("<Name>" + processString(itemSet.getString("Name")) + "</Name>\n");
            	
            	// Categories
            	for(String category : fetchCategories(itemId, conn)){
            		sb.append("<Category>" + processString(category) + "</Category>\n");
            	}
            	
            	// Currently
            	sb.append("<Currently>$" + itemSet.getString("Currently") + "</Currently>\n");
            	
            	// Buy_Price
            	String Buy_Price = itemSet.getString("Buy_Price");
            	if(!itemSet.wasNull()){
            		sb.append("<Buy_Price>$" + Buy_Price + "</Buy_Price>\n");
            	}
            	
            	// First_Bid
            	sb.append("<First_Bid>$" + itemSet.getString("First_Bid") + "</First_Bid>\n");
            	
            	//Bids
            	String[] bids = fetchBids(itemId, conn);
            	
            	sb.append("<Number_of_Bids>" + bids.length + "</Number_of_Bids>\n");
            	if(bids.length == 0){
            		sb.append("<Bids />\n");
            	}
            	else{
            		sb.append("<Bids>\n");
            		for(String bid : bids){
            			sb.append(bid);
            		}
            		sb.append("</Bids>\n");
            	}
            	
            	// Location
            	String Location = itemSet.getString("Location");
            	sb.append("<Location");
            	
            	String Latitude = itemSet.getString("Latitude");
            	if(!itemSet.wasNull()){
            		sb.append(" Latitude=" + escape(Latitude));
            	}
            	
            	String Longitude = itemSet.getString("Longitude");
            	if(!itemSet.wasNull()){
            		sb.append(" Longitude="+ escape(Longitude));
            	}
            	
            	
            	
            	sb.append(">" + Location +"</Location>\n");
            	
            	// Country
            	sb.append("<Country>" + itemSet.getString("Country") + "</Country>\n");
            	
            	// Started
            	Calendar started = Calendar.getInstance();
            	started.setTimeInMillis(itemSet.getTimestamp("Started").getTime());
            	sb.append("<Started>" + df.format(started.getTime()) + "</Started>\n");
            	
            	// Ends
            	Calendar ends = Calendar.getInstance();
            	ends.setTimeInMillis(itemSet.getTimestamp("Ends").getTime());
            	sb.append("<Ends>" + df.format(ends.getTime()) + "</Ends>\n");
            	
            	// Seller
            	sb.append(fetchSeller(itemSet.getString("SellerId"), conn));
            	
            	// Description
            	sb.append("<Description>" + processString(itemSet.getString("Description")) + "</Description>\n");
            	
            	sb.append("</Item>");
            	
            	return sb.toString(); 
            }
        	else{
        		return "";
        	}
        }
        catch(Exception e){
        	return "";
        }
    }

    public String echo(String message) {
        return message;
    }

    
    private String escape(String s){
    	return "\"" + s + "\"";
    }
    
    private String processString(String s){
    	s = s.replaceAll("\"", "&quot;");
    	s = s.replaceAll("\'", "&apos;");
    	s = s.replaceAll("<" , "&lt;");
    	s = s.replaceAll(">", "&gt");
    	s = s.replaceAll("&", "&amp;");
    	
    	return s;
    }
}
