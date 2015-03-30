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

import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import java.sql.Timestamp;

public class AuctionSearch implements IAuctionSearch {

	/* 
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	private Connection connection;
	private IndexSearcher index_searcher;
	
 	//default constructor for AuctionSearch Class
	public AuctionSearch(){
	  try{
		//create Lucene Index at directory /var/lib/lucene/index-directory
	    index_searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/var/lib/lucene/index-directory"))));
	    connection = DbManager.getConnection(true);
	  }catch(Exception e){ System.out.println(e);}
	}

	// basicSearch(String query, int numResultsToSkip, int numResultsToReturn):
	// search for the items containing given query(keyword)
	// return search results in array
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		int n = numResultsToSkip + numResultsToReturn;
		return getSearchResults(getDocsFromLucene(query,n),numResultsToSkip, numResultsToReturn);
	}
	
    // spatialSearch(String query, SearchRegion region, int numResultsToSkip, int numResultsToReturn):
	// search for the items containing query(keyword) and longitude and latitude 
	// fell inside the given search region
	// return search results in array
	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// first search for the items containing keyword
		SearchResult[] keyword_results = basicSearch(query, numResultsToSkip, numResultsToReturn);
		ArrayList<SearchResult> intersect_result_array = new ArrayList<SearchResult>();
		int bigInteger = 99999999;
		TopDocs keyword_docs = getDocsFromLucene(query, bigInteger);
		SearchResult[] keyword_result = getSearchResults(keyword_docs,0,0);
		// search for the items fell inside the region
		ArrayList<String> spatial_results = getIdsFromSpatial(region);
		System.out.println("keyword searched: " + keyword_result.length + ", and spatial searched: " + spatial_results.size());
		
		// find item that contain keyword and fell inside the region
		// this way of finding result can be faster since we already have index on (longtide,latitude)
		for(int i=0; i<keyword_results.length; i++){
		  for(int j=0; j<spatial_results.size(); j++){
		    if(keyword_results[i].getItemId().equals(spatial_results.get(j)))
		      intersect_result_array.add(keyword_results[i]);
		  }
		}
		SearchResult[] sr = new SearchResult[intersect_result_array.size()];
		sr = intersect_result_array.toArray(sr);
		SearchResult[] cutted_sr = cutSearchResult(sr, numResultsToSkip, numResultsToReturn);
		return cutted_sr;
	}

    // getXMLDataForItemId(String itemId):
	// generate string in xml format that represent item with given itemId
	public String getXMLDataForItemId(String itemId) {
	  String xmlstr = "";
	  try{
	    Statement statement = connection.createStatement();
	    String getItem_query = "SELECT * FROM Items WHERE itemId = '" + itemId + "'";
	    ResultSet rs = statement.executeQuery(getItem_query);
	    if(!(rs.next())){
		return "Fail to Find itemId: "+ itemId;
	    }
	    String name = rs.getString("name");
	    String sellerId = rs.getString("sellerId");
	    String currently = String.format("%.2f", rs.getFloat("currently"));
	    String buy_price = String.format("%.2f", rs.getFloat("buy_price"));
	    String first_bid = String.format("%.2f", rs.getFloat("first_bid"));
	    String number_of_bids = Integer.toString(rs.getInt("number_of_bids"));
	    String location = rs.getString("location");
	    String country = rs.getString("country");
	    String latitude = Integer.toString(rs.getInt("latitude"));
	    String longitude = Integer.toString(rs.getInt("longitude"));
	    String started = rs.getTimestamp("started").toString();
	    String ends = rs.getTimestamp("ends").toString();
	    String description = rs.getString("description");
	    xmlstr += "<Item ItemId=\"" + itemId + "\">" + '\n';
	    xmlstr += "  <Name>" + toXMLFormat(name) + "</Name>" + '\n';	

	    String getCategs_query = "SELECT * FROM Categories WHERE itemId = '" + itemId + "'";
	    ResultSet rs_categ = statement.executeQuery(getCategs_query);
	    while(rs_categ.next()){
	      xmlstr += "  <Category>" + toXMLFormat(rs_categ.getString("category")) + "</Category>" + '\n';
	    }	    
	    xmlstr += "  <Currently>$" + toXMLFormat(currently) + "</Currently" + '\n';
	    if(!(buy_price.equals("0.00")))
	      xmlstr += "  <Buy_Price>$" + toXMLFormat(buy_price) + "</Buy_Price>" + '\n';
	    xmlstr += "  <First_Bid>$" + toXMLFormat(first_bid) + "</First_Bid>" + '\n';
	    xmlstr += "  <Number_of_Bids>" + toXMLFormat(number_of_bids) + "</Number_of_Bids>" + '\n';
	    xmlstr += "<Bids>" + '\n';
            String bids_query = "SELECT * FROM Bids WHERE itemId = '" + itemId + "'";
	    ResultSet rs_bids = statement.executeQuery(bids_query);
	    while(rs_bids.next()){
	      xmlstr += getBidXMLData(rs_bids.getString("bidderId"), rs_bids.getTimestamp("time").toString(), String.format("%.2f", rs_bids.getFloat("amount")));
	    }
	    xmlstr += "  </Bids>" + '\n';
	    if(!(location.equals("NotAvailable")))
	      xmlstr += "  <Location>" + toXMLFormat(location) + "</Location>" + '\n';
	    if(!(country.equals("NotAvailable")))
	      xmlstr += "  <Country>" + toXMLFormat(country) + "</Country>" + '\n';
	    xmlstr += "  <Started>" + toXMLFormat(toXMLTimeFormat(started)) + "</Started>" + '\n';
	    xmlstr += "  <Ends>" + toXMLFormat(toXMLTimeFormat(ends)) + "</Ends>" + '\n';
	    String seller_query = "SELECT * FROM Sellers WHERE userId = '" + sellerId + "'";
	    ResultSet rs_seller = statement.executeQuery(seller_query);
	    if(!(rs_seller.next()))
	      return "Cannot Find Seller with Id: "+ sellerId;
	    String rating = Integer.toString(rs_seller.getInt("rating"));
	    xmlstr += "  <Seller " + "UserId=\"" + toXMLFormat(sellerId) + 
	    "\""+ " " + "Rating=\"" + toXMLFormat(rating) + "\"/>" + '\n';
	    xmlstr += "  <Description>" + toXMLFormat(description) + "</Description>" + '\n';
	    xmlstr += "</Item>";
	  }catch(Exception e){System.out.println(e);}
	  return xmlstr;
	}
	
	public String echo(String message) {
		return message;
	}

	// getIdsFromSpatial(SearchRegion reg):
	// return array containing itemIds for the items where (longitude,latitude) fell inside the given region
	private ArrayList<String> getIdsFromSpatial(SearchRegion reg){
	  ArrayList<String> result = new ArrayList<String>();
	  Statement stmt = null;
	  ResultSet rs = null;
	  try{
	    stmt = connection.createStatement();
	    String spatial_query = "SELECT itemId FROM ItemPosition WHERE MBRContains(GeomFromText('Polygon((" +
		reg.getLx() + " " + reg.getLy() + ","+
		reg.getLx() + " " + reg.getRy() + "," +
		reg.getRx() + " " + reg.getRy() + "," +
		reg.getRx() + " " + reg.getLy() + "," +
		reg.getLx() + " " + reg.getLy() + "))'),position)";
	    rs = stmt.executeQuery(spatial_query);
	    while(rs.next()){
	      result.add(Integer.toString(rs.getInt("itemId")));
	    }
	  }catch(Exception e){System.out.println(e);} 
	  return result;	
	}

	// cutSearchResult(SearchResult[] orig, int numResultsToSkip, int numResultsToReturn)
	// return appropriate array of search result based on the number of results to skip and 
	// number of result to return
	private SearchResult[] cutSearchResult(SearchResult[] orig, int numResultsToSkip, int numResultsToReturn){
	  if(numResultsToSkip > orig.length)
	    return new SearchResult[0];
	  else if(numResultsToReturn == 0)
	    numResultsToReturn = orig.length;
	  ArrayList<SearchResult> sr_array = new ArrayList<SearchResult>();
	  for(int i = numResultsToSkip; i<orig.length; i++){
	    if(!(numResultsToReturn >0))
	      break;
	      sr_array.add(orig[i]);
	      numResultsToReturn --;
	    }
	  SearchResult[] sr = new SearchResult[sr_array.size()];
	  sr = sr_array.toArray(sr);
	  return sr;
	}


    // getSearchResults(TopDocs docs, int numResultsToSkip, int numResultsToReturn)
    // convert search result from TopDocs class to SearchResult class and generate appropriate number of result
	// based on the number of results to skip and number of result to return
    // return search results after converting appropriately
	private SearchResult[] getSearchResults(TopDocs docs, int numResultsToSkip, int numResultsToReturn){
	  ScoreDoc[] hits = docs.scoreDocs;
	  if(numResultsToSkip > hits.length)
	    return new SearchResult[0];
	  else if(numResultsToReturn == 0)
	    numResultsToReturn = hits.length;
	  ArrayList<SearchResult> sr_array = new ArrayList<SearchResult>();
	  for(int i = numResultsToSkip; i<hits.length;i++){
	    if(!(numResultsToReturn > 0))
	      break;
	    Document doc = null;
	    try{
	      doc = index_searcher.doc(hits[i].doc);
	    }catch(Exception e){System.out.println(e);}
	    sr_array.add(new SearchResult(doc.get("itemId"), doc.get("name")));
	    numResultsToReturn --; 
	  }
	  SearchResult[] sr = new SearchResult[sr_array.size()];
	  sr = sr_array.toArray(sr);
	  return sr;
	}

	// getDocsFromLucene(String query, int n):
	// obtain search result by using Lucene Index 
	private TopDocs getDocsFromLucene(String query, int n){
	  if(query == null)
	    return null;
	  QueryParser query_parser = new QueryParser("searchText", new StandardAnalyzer());
	  Query parsed_query = null;
	  TopDocs searched_docs = null;
	  try{
	    parsed_query = query_parser.parse(query);
	    searched_docs = index_searcher.search(parsed_query, n);
	  }catch(Exception e){System.out.println(e);}
	  return searched_docs;
	}

	//toXMLFormat(String target):
	// modify string to xml format	
    public String toXMLFormat(String target){
	  StringBuilder strBuilder = new StringBuilder();
	  StringCharacterIterator strIterator = new StringCharacterIterator(target);
	  char current_char = strIterator.current();
	  while(!(current_char == CharacterIterator.DONE)){
	    if(current_char == '\'')
	      strBuilder.append("&apos;");
	    else if(current_char == '\"')
	      strBuilder.append("&quot;");
	    else if(current_char == '\\')
	      strBuilder.append("\\");
	    else if(current_char == '&')
	      strBuilder.append("&amp;");
	    else if(current_char == '>')
	      strBuilder.append("&gt;");
	    else if(current_char == '<')
	      strBuilder.append("&lt;");
	    else
	      strBuilder.append(current_char);
	  
	    current_char = strIterator.next();
	  }
	  return strBuilder.toString();
	}

	// getBidXMLData(String bidderId, String time, String amount):
	// get string in xml format that represent single bid's information
	public String getBidXMLData(String bidderId, String time, String amount)
	{
	  String bidXML_str = "";
	  String fs = "    "; //four spaces
          String ss = "      "; //six spaces
	  String es = "        "; //eight spaces
	  try{
	    Statement statement = connection.createStatement();
	    String bidder_query = "SELECT * FROM Bidders WHERE userId = '" + bidderId + "'";
	    ResultSet rs_bidder = statement.executeQuery(bidder_query);
	    if(!(rs_bidder.next()))
	      return "FAIL TO FIND Bidder with Id: " + bidderId;
	    String rating = Integer.toString(rs_bidder.getInt("rating"));
	    String location = rs_bidder.getString("location");
	    String country = rs_bidder.getString("country");
	    bidXML_str += fs + "<Bid>" + '\n';
	    bidXML_str += ss + "<Bidder UserId=\"" + toXMLFormat(bidderId) + "\" " + "Rating=\"" + toXMLFormat(rating) + "\">" + '\n'; 
	    bidXML_str += es + "<Location>" + toXMLFormat(location) + "</Location>" + '\n';
	    bidXML_str += es + "<Country>" + toXMLFormat(country) + "</Country>"+ '\n';
	    bidXML_str += ss + "</Bidder>" + '\n';
	    bidXML_str += ss + "<Time>" + toXMLFormat(toXMLTimeFormat(time)) + "</Time>" + '\n';
	    bidXML_str += ss + "<Amount>$" + toXMLFormat(amount) + "</Amount>" + "\n";
	    bidXML_str += fs + "</Bid>" + "\n";
          }catch(Exception e){System.out.println(e);}
	  return bidXML_str;
	}

	// toXMLTimeFormat(String time_str):
	// change time format to XML time format
	public String toXMLTimeFormat(String time_str){
	  String xmlTime_str = time_str;
	  try{
	    SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    SimpleDateFormat xmlDateFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
	    xmlTime_str = xmlDateFormat.format(databaseFormat.parse(time_str));
  	  }catch(Exception e){System.out.println(e);}
	  return xmlTime_str;
	}








}
