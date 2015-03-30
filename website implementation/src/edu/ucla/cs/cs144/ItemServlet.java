package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.*;
import java.util.*;

import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.FactoryConfigurationError;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}
//========================================================================
// helper functions for accessing elements/attributes in DOM tree

     static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
       static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
       static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    static Attr getAttributeByTagNameNR(Element e, String tagName) {
        Attr att = e.getAttributeNode(tagName);
        if (att != null) {
             return att;
        }
        else {
            return null;
        }
    }

    static String getAttributeTextByTagNameNR(Element e, String tagName) {
        Attr att = getAttributeByTagNameNR(e, tagName);
        if (att != null) {
            return att.getValue();
        }
        else {
            return "";
        }
    }
//=====================================================================


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	String queryString = request.getQueryString();
	if(queryString != null){
	  String itemId = request.getParameter("itemID");
	  if(!(itemId != null))
	    itemId = "";
	  request.setAttribute("itemID", itemId);
	  AuctionSearchClient asc = new AuctionSearchClient();
	  String xmlData = asc.getXMLDataForItemId(itemId);
	  Document doc=null;
	  Element rootElem;
	  DocumentBuilder db=null;

	  if(xmlData.isEmpty())
	    request.setAttribute("totalNumber",0);
	  else{
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    request.setAttribute("totalNumber",1);
	    try{
	      db = dbf.newDocumentBuilder();
	    }catch(Exception ex){ex.printStackTrace();}
	    InputSource inputSource = new InputSource(new StringReader(xmlData));
	    try{
	      doc = db.parse(inputSource);
	    }catch(Exception ex){ex.printStackTrace();}
	    rootElem = doc.getDocumentElement();
	    request.setAttribute("Name", getElementTextByTagNameNR(rootElem, "Name"));
	    request.setAttribute("Currently", getElementTextByTagNameNR(rootElem, "Currently"));
	    //insert categories
	    String categs = "";
	    Element[] categories_arr = getElementsByTagNameNR(rootElem, "Category");
	    for(int i=0; i<categories_arr.length; i++){
	      categs += getElementText(categories_arr[i]);
	      if(i!= categories_arr.length-1 && i!=0)
	        categs += ", ";
	    }
	    request.setAttribute("Categories", categs);
	    //insert buy_price (and buy_price_valid attribute)
	    Element[] buy_Price_arr = getElementsByTagNameNR(rootElem,"Buy_Price");
	    if(!(buy_Price_arr.length != 0)){
	      request.setAttribute("Buy_Price_Valid", "False");
	      request.setAttribute("Buy_Price", "0.0");
	    }
	    else{
	      request.setAttribute("Buy_Price_Valid", "True");
	      request.setAttribute("Buy_Price", getElementTextByTagNameNR(rootElem, "Buy_Price"));
	    }
	    request.setAttribute("First_Bid", getElementTextByTagNameNR(rootElem, "First_Bid"));
	    request.setAttribute("Number_of_Bids", getElementTextByTagNameNR(rootElem,"Number_of_Bids"));
	    request.setAttribute("Location", getElementTextByTagNameNR(rootElem,"Location"));
	    Element locationElem = getElementByTagNameNR(rootElem, "Location");
	    String longitude = getAttributeTextByTagNameNR(locationElem, "Longitude");
	    if(longitude.equals(""))
	      longitude = "0.0";
	    request.setAttribute("Longitude", longitude); 
	    String latitude = getAttributeTextByTagNameNR(locationElem, "Latitude");
	    if(latitude.equals(""))
	      latitude = "0.0";
	    request.setAttribute("Latitude", latitude);
	    request.setAttribute("Country", getElementTextByTagNameNR(rootElem, "Country"));
	    request.setAttribute("Started", getElementTextByTagNameNR(rootElem, "Started"));
	    request.setAttribute("Ends", getElementTextByTagNameNR(rootElem, "Ends"));
	    request.setAttribute("Description", getElementTextByTagNameNR(rootElem, "Description"));
           //add seller's information
	   Element sellerElem = getElementByTagNameNR(rootElem, "Seller");
	   request.setAttribute("SellerID", getAttributeTextByTagNameNR(sellerElem, "UserID"));
	   request.setAttribute("SellerRating", getAttributeTextByTagNameNR(sellerElem, "Rating"));
	   //add bids' information
	   String bids = "";
	   Element bidsElem = getElementByTagNameNR(rootElem, "Bids");
	   Element[] bidList = getElementsByTagNameNR(bidsElem, "Bid");
	   for(int i=0; i<bidList.length; i++){
		Element bidderElem = getElementByTagNameNR(bidList[i], "Bidder");
		String bidderID = getAttributeTextByTagNameNR(bidderElem, "UserID");
		String bidderRating = getAttributeTextByTagNameNR(bidderElem, "Rating");
		String bidderLocation = getElementTextByTagNameNR(bidderElem, "Location");
		String bidderCountry = getElementTextByTagNameNR(bidderElem, "Country");
		String bidTime = getElementTextByTagNameNR(bidList[i], "Time");
		String bidAmount = getElementTextByTagNameNR(bidList[i], "Amount");
		bids += "<br>BidderId: " + bidderID + "<br>Rating of Bidder: "+
		bidderRating + "<br>Location of Bidder: " + bidderLocation + 
		"<br>Country of Bidder: " + bidderCountry + "<br>Bid Time: " + 
		bidTime + "<br>Bid Amount: " + bidAmount + "<br><br>"; 
	   }
	   request.setAttribute("Bids", bids);
	    
	  }//end of else
	  request.getRequestDispatcher("/itemSearchResult.jsp").forward(request, response);
	}//end of "if(queryString!= null)"
	else
	  request.getRequestDispatcher("/getItem.html").forward(request, response);
    }
}
