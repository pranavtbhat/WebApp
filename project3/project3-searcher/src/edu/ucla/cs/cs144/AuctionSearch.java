package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
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
        // TODO: Your code here!
        return new SearchResult[0];
    }

    public String getXMLDataForItemId(String itemId) {
        // TODO: Your code here!
        return "";
    }

    public String echo(String message) {
        return message;
    }

}
