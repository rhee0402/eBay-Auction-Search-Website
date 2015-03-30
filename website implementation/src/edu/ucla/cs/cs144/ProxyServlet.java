package edu.ucla.cs.cs144;

import java.net.URL;
import java.net.URLEncoder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	  response.setContentType("text/xml");
	  BufferedReader br = null;
	  PrintWriter pw = response.getWriter();
	  String outputString = "";  
	  try{
		String q = request.getParameter("q");
		String qUTF8 = URLEncoder.encode(q, "UTF-8");
		String search_url = "http://google.com/complete/search?output=toolbar&q=";
		search_url = search_url + qUTF8;
  
		URL url = new URL(search_url);
		StringBuilder sb = new StringBuilder();
		String lne = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setReadTimeout(3000); // 3000ms timeout value
		conn.connect();
		br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		lne = br.readLine();
		while(!(lne == null)){
		  sb.append(lne + "\n");
		  lne = br.readLine();
		}
		outputString = sb.toString();
		pw.println(outputString);
	  }catch(Exception e){e.printStackTrace();}
	  finally{
		if(!(br == null)){
		  try{ br.close();}
		  catch(Exception e){}
		}
	  }//end of finally
    }//end of doGet method
}
