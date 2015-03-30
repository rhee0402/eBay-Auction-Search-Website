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
import java.util.ArrayList;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    //rebuildIndexes():
 	// create/rebuild indexes for the Name, Category, and Description concavinated together.
    public void rebuildIndexes() {
        Connection conn = null;
    	// create a connection to the database to retrieve Items from MySQL
		try {
	 		conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
		    System.out.println(ex);
		}

     	 IndexWriter indexWriter = null;
   	    try{
			Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index-directory"));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
			indexWriter = new IndexWriter(indexDir, config);
			ArrayList<String> testarr = new ArrayList<String>();
			ResultSet rs = getItems(conn);
			if(!(rs==null)){
				while(rs.next()){
					Document doc = new Document();
					doc.add(new StringField("itemId", Integer.toString(rs.getInt("itemId")), Field.Store.YES));
					if(testarr.contains(Integer.toString(rs.getInt("itemId")))){
						System.out.println("Duplicate ItemId: " + rs.getInt("itemId"));	       	 	
						return;
					}
				testarr.add(Integer.toString(rs.getInt("itemId")));
				String name_str = rs.getString("name");
				doc.add(new TextField("name", name_str, Field.Store.YES));
				String description_str = rs.getString("description");
				doc.add(new TextField("description", description_str, Field.Store.YES));
				String categories_str = getItemCategs(conn,rs.getInt("itemId"));
				doc.add(new TextField("category", categories_str, Field.Store.YES));
				String search_text = name_str + " " + description_str + " " + categories_str;
				doc.add(new TextField("searchText", search_text, Field.Store.NO));
				indexWriter.addDocument(doc);
				}//end of while(rs.next)
			}//end of if (!(rs==null))
    	}catch(Exception e){ System.out.println(e);}

		try {
			indexWriter.close();
		}catch(Exception e) { System.out.println(e);}
		
			// close the database connection
		try { 
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	}    

	// getItems(Connection conn):
	// from Items table in SQL database, obtain all items
    private static ResultSet getItems(Connection conn){
      Statement stmt = null;
      ResultSet rs = null;
      try{
	stmt = conn.createStatement();
	String query_str = "SELECT itemId, name, description FROM Items";
	rs = stmt.executeQuery(query_str);
      }catch(Exception e){System.err.println(e.getMessage());}
      return rs;
    }

	// getItemCategs(Connection conn, int id):
	// from Categories table in SQL database, obtain matching item's categories
    private static String getItemCategs(Connection conn, int id){
      Statement stmt = null;
      ResultSet rs = null;
      String categs = "";
      try{
	stmt = conn.createStatement();
	String query_str = "SELECT group_concat(category separator ' ') as AllCategs FROM Categories WHERE ItemId = '" + id + "'";
        rs = stmt.executeQuery(query_str);
        if(rs.next())
	  categs = rs.getString("AllCategs");
      }catch(Exception e){System.err.println(e.getMessage());}
      return categs;
    }


    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
