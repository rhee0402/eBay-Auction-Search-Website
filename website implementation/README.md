# eBay Auction Search Website: Website Implementation
Website Implementation: make eBay auction data accessible by the end users through Web browser by implmenting on top of the eBay Auction Search Web service.

website integrated three Web services:
1. SOAP-based Web service to search for the eBay data.
2  Google Maps to display location of item.
3. Google suggest to help users enter the keyword.

Basic Flow:
index.html 
  -> keywordSearch.html
  -> getItem.html

keywordSearch.html -> SearchServlet.java -> keySearchResult.jsp

getItem.html -> ItemServlet.java -> itemSearchResult.jsp

Overview of important files:
keywordSearch.html: search by the keyword with auto suggestion feature implemented. 

SearchServlet.java: use AuctionSearchClient to obtain     results of search.

keySearchResult.jsp: display up to 20 found items per page.

getItem.html: search by the ItemId.

ItemServlet.java: use AuctionSearchClient to obtain information of item with given ItemId.

itemSearchResult.jsp: display information of item and location of item on the Google Maps.

