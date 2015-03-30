package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
     
    String queryString = request.getQueryString();
    if(queryString != null){
		String keyword = request.getParameter("q");
		String output = "";
	 	int numResultsToReturn;
	 	int numResultsToSkip;
	 	try{
	  		if(request.getParameter("numResultsToReturn") == null)
	    		numResultsToReturn = 20;
	  		else
	    		numResultsToReturn = Integer.parseInt(request.getParameter("numResultsToReturn"));
    	}catch(Exception e){numResultsToReturn = 20;}

		try{
	  	if(request.getParameter("numResultsToSkip") == null)
	    	numResultsToSkip = 0;
	  	else
	    	numResultsToSkip = Integer.parseInt(request.getParameter("numResultsToSkip"));
		}catch(Exception e){numResultsToSkip = 0;}

		if(keyword.equals("")){
			request.setAttribute("output", "");
	 	 	request.setAttribute("totalNumber", 0);
		}
		else{
			AuctionSearchClient asclient = new AuctionSearchClient();
		  	SearchResult[] sr = asclient.basicSearch(keyword, numResultsToSkip, numResultsToReturn);
		  	int totalNumber = sr.length;
			int size = sr.length;
			int starting = numResultsToSkip;
	  		if(size > numResultsToReturn)
	    		size = numResultsToReturn;
	  		for(int i=0; i<size; i++){
	    		if(!(i != 0)){
	      			if(sr[i].getItemId().equals("-1"))
	        			totalNumber = 0;
	    	}
	    	starting++;
	    	output += starting + ". " + "<a href=\"/eBay/item?itemID=" +
	        sr[i].getItemId() + "\">" + sr[i].getName() + "</a><br>";
	  	}
	  request.setAttribute("output", output);
	  request.setAttribute("totalNumber", totalNumber);
	  request.setAttribute("keyword", keyword);
	  request.setAttribute("numResultsToReturn",numResultsToReturn);
	  request.setAttribute("numResultsToSkip", numResultsToSkip);
	} //end of else
	request.getRequestDispatcher("/keySearchResult.jsp").forward(request,response);
    }//end of if queryString != null
      else
        request.getRequestDispatcher("/keywordSearch.html").forward(request,response);
    
    }//end of doGet method;
}
