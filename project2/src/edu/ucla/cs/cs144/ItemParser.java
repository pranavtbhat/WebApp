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
import java.util.*;

public class ItemParser {
	private class User{
		private String UserID;
		private String Location;
		private String Country;
		private int Rating;
		
		User(String UserID, String Location, String Country, int Rating){
			this.UserID = UserID;
			this.Location = Location;
			this.Country = Country;
			this.Rating = Rating;
		}
		
		public String toString(){
			return UserID + "," + Location + "," + Country + "," + Rating;
		}
	}
	
	private Map<String,User> hmap;
	
	
	
	
	
	
}
