<html>
  <head>
    <script type="text/javascript" src="./autosuggest.js"></script>
    <script type="text/javascript" src="./suggestions.js"></script>
    <link rel="stylesheet" type="text/css" href="./autosuggest.css" />
  </head>
  <body>
    <h1>Search by Keyword</h1>
    <form action="/eBay/search">
    <%
      String val = request.getParameter("q");
      out.println("Enter Keyword to Search: <input type=\"text\" name=\"q\" id=\"keywordTextBox\" value=\"" + val + "\"autocomplete = \"off\" style = \"width:400px;\"/><br />");
    %>
      <input type="hidden" name="numResultsToSkip" value="0"/>
      <input type="hidden" name="numResultsToReturn" value="20"/><br />
      <input type="submit" />
    </form>
    <div id="suggestion" class="suggestions"></div>
      <script type="text/javascript">
	window.onload = function(){
	  var oTextbox= new AutoSuggestControl(document.getElementById("keywordTextBox"), new StateSuggestions());
	}
      </script>
<%
	String totalNumber = request.getAttribute("totalNumber").toString();
	if(totalNumber.equals("0")){
	  out.println("There was no result found.");
	}
        else{
%>
	  <%= request.getAttribute("output") %><br><br>
<%
	}
	Integer numSkip = Integer.parseInt(request.getParameter("numResultsToSkip"));
	Integer numReturn = Integer.parseInt(request.getParameter("numResultsToReturn"));
	Integer numNext = numSkip + numReturn;
	Integer numPrev = numSkip - numReturn;
	if(!(numSkip<=0)){
	  out.println("<a href=\"/eBay/search?q=" + val + "&numResultsToSkip=" +
	    numPrev + "&numResultsToReturn=" + numReturn + 
	    "\">Goto Previous Page</a>");
	}
	if(totalNumber.equals("20")){
	  out.println("<a href=\"/eBay/search?q=" + val + "&numResultsToSkip=" +
	    numNext + "&numResultsToReturn=" + numReturn +
	    "\">Goto Next Page</a>");
	}
%>
      <p><a href="/eBay/item">Search by ItemId</a></p>
  </body>
</html>
