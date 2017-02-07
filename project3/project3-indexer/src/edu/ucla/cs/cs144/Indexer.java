package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
	private String INDEX_DIR = "/var/lib/lucene/";
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }

    public void rebuildIndexes() throws SQLException, IOException {

        Connection conn = null;
        
        // Setup indexWriter
        Directory indexDir = FSDirectory.open(new File(INDEX_DIR + "index-1"));
    	IndexWriterConfig config = new IndexWriterConfig(
    		Version.LUCENE_4_10_2, 
    		new StandardAnalyzer()
    	);
    	
    	IndexWriter indexWriter = new IndexWriter(indexDir, config);
    	
        // create a connection to the database to retrieve Items from MySQL
        try {
            conn = DbManager.getConnection(true);
        } catch (SQLException ex) {
            System.out.println(ex);
        }


        /*
         * Add your code here to retrieve Items using the connection
         * and add corresponding entries to your Lucene inverted indexes.
         *
         * You will have to use JDBC API to retrieve MySQL data from Java.
         * Read our tutorial on JDBC if you do not know how to use JDBC.
         *
         * You will also have to use Lucene IndexWriter and Document
         * classes to create an index and populate it with Items data.
         * Read our tutorial on Lucene as well if you don't know how.
         *
         * As part of this development, you may want to add 
         * new methods and create additional Java classes. 
         * If you create new classes, make sure that
         * the classes become part of "edu.ucla.cs.cs144" package
         * and place your class source files at src/edu/ucla/cs/cs144/.
         * 
         */
        Statement stmt = conn.createStatement();
        PreparedStatement getCategories = conn.prepareStatement(
		    "SELECT Category " +
		    "FROM ItemCategory " + 
		    "WHERE ItemID = ?"
    	);
        
        ResultSet items = stmt.executeQuery(
        	"SELECT ItemID, Name, Description " +
        	"FROM Items"
        );
        
        while(items.next()){
        	Document doc = new Document();
        	
        	int ItemID = items.getInt(1);
        	String Name = items.getString(2);
        	String Description = items.getString(3);
        	
        	getCategories.setInt(1, ItemID);
        	ResultSet categories = getCategories.executeQuery();
        	StringBuilder sb = new StringBuilder();
        	
        	while(categories.next()){
        		sb.append(categories.getString(1));
        		sb.append(" ");
        	}
        	String Categories = sb.toString();
        
            String fullSearchable = ItemID + " " + Name + " " + Description + " " + Categories;
        	
        	doc.add(new StringField("ItemID", Integer.toString(ItemID),Field.Store.YES));
        	doc.add(new StringField("Name", items.getString(2), Field.Store.YES));
        	doc.add(new TextField( "Description", items.getString(3), Field.Store.NO));
        	doc.add(new TextField("Categories", Categories, Field.Store.NO)); 
        	doc.add(new TextField("content", fullSearchable, Field.Store.NO)); 
        	
        	indexWriter.addDocument(doc);
        }

        stmt.close();
        
        // close the database connection
        try {
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        
        // close indexWriter
        indexWriter.close();
    }    

    public static void main(String args[]) throws Exception {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
