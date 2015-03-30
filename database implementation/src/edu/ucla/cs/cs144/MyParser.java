/* 
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 */

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


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
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
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    public static String replaceQuote(String s){
//      return s.replaceAll("\""", "");
      return s;
    }

    public static String getFirstFourThousand(final String description){
      if(description.length() > 4000)  
        return description.substring(0,4000);
      else
        return description;
    }

    public static String formatTime(String time){
      try{
        SimpleDateFormat prev_format = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = new_format.format(prev_format.parse(time));
      }catch(Exception e){System.out.println("Failed to convert time");}
      return time;
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        int count=0;
        try{
          ArrayList<String> bidderAList = new ArrayList<String>();
          ArrayList<String> sellerAList = new ArrayList<String>();
   
   		  // create FileWriter for each csv files
          FileWriter bidderFW = new FileWriter("bidders.csv", true);
          FileWriter sellerFW = new FileWriter("sellers.csv", true);
          FileWriter bidsFW = new FileWriter("bids.csv",true);
          FileWriter categoryFW = new FileWriter("categories.csv",true);
          FileWriter itemListFW = new FileWriter("itemList.csv",true);

		  // create BufferedWriter for each FileWriter
          BufferedWriter bidderBW = new BufferedWriter(bidderFW);
          BufferedWriter sellerBW = new BufferedWriter(sellerFW);
          BufferedWriter bidsBW = new BufferedWriter(bidsFW);
          BufferedWriter categoryBW = new BufferedWriter(categoryFW);
          BufferedWriter itemListBW = new BufferedWriter(itemListFW);
   
   		  // obtain all items in list
          Element items[] = getElementsByTagNameNR(doc.getDocumentElement(), "Item");       
          for(int index=0; index < items.length; index++){  
            Element item = items[index];
			// for each item, obtain name, current price, first bid price, buy price, number of bids,
			// country and location of item including latitude and longitude, started time of auction,
			// end time of auction, and description of item with maximum 4000 characters
            int itemId = Integer.parseInt(item.getAttributes().item(0).getNodeValue());
            String name = replaceQuote(getElementTextByTagNameNR(item,"Name"));
            String currently = strip(getElementTextByTagNameNR(item, "currently"));
            String first_bid = strip(getElementTextByTagNameNR(item, "First_Bid"));
            if(currently.equals(""))
              currently = first_bid;
            String buy_price = strip(getElementTextByTagNameNR(item, "Buy_Price"));
            //here buy price can be = ""
	        if(buy_price.equals(""))
              buy_price = "0.00";
            String number_of_bids = getElementTextByTagNameNR(item, "Number_of_Bids");
            Element location_element = getElementByTagNameNR(item, "Location");
            double latitude = 0.0;
            double longitude = 0.0;
            if(location_element.getAttributes().item(0) != null){
             String latitude_str = location_element.getAttributes().item(0).getNodeValue();
             if(!(latitude_str.equals(""))){
               latitude = Double.parseDouble(latitude_str);
             }
            }
            if(location_element.getAttributes().item(1) != null){
              String longitude_str =location_element.getAttributes().item(1).getNodeValue();
              if(!(longitude_str.equals(""))){
                longitude = Double.parseDouble(longitude_str);
              }
            }
            String location = replaceQuote(getElementTextByTagNameNR(item, "Location"));
            String country = replaceQuote(getElementTextByTagNameNR(item, "Country"));
            String started = formatTime(getElementTextByTagNameNR(item, "Started"));
            String ends = formatTime(getElementTextByTagNameNR(item,"Ends"));
            String description = replaceQuote(getFirstFourThousand(getElementTextByTagNameNR(item,"Description")));
			// get item selle's information: seller's UserId and seller rating
            Element seller = getElementByTagNameNR(item,"Seller");
            String seller_userId = replaceQuote(seller.getAttributes().item(1).getNodeValue());
            String seller_rating = seller.getAttributes().item(0).getNodeValue();

			// get list of category item belongs to  
            Element[] categoryList = getElementsByTagNameNR(item, "Category");

			// get bids information 
			// for each bid, obtain Bidder's information, Time of making bid, and Bid Amount  
            Element bidList = getElementByTagNameNR(item, "Bids");
            Element bids[] = getElementsByTagNameNR(bidList, "Bid");
            for(int bid_index = 0; bid_index < bids.length; bid_index++){
              Element bid = bids[bid_index];
              Element bidder = getElementByTagNameNR(bid, "Bidder");
              String bidder_userId = replaceQuote(bidder.getAttributes().item(1).getNodeValue());
              String bidder_rating = bidder.getAttributes().item(0).getNodeValue();
              String bidder_location = replaceQuote(getElementTextByTagNameNR(bidder,"Location"));
              String bidder_country = replaceQuote(getElementTextByTagNameNR(bidder, "Country"));
              String bid_time = formatTime(getElementTextByTagNameNR(bid,"Time"));
              String bid_amount = strip(getElementTextByTagNameNR(bid,"Amount"));

             if(!(bidderAList.contains(bidder_userId))){
                 if(bidder_location.equals(""))
                   bidder_location = "NotAvailable";
                 if(bidder_country.equals(""))
                   bidder_country = "NotAvailable";
                 bidderBW.append("\""+bidder_userId+"\""+columnSeparator+bidder_rating+columnSeparator+"\""+bidder_location+"\""+columnSeparator+"\""+bidder_country +"\""+"\n");

               bidderAList.add(bidder_userId);
             }
             bidsBW.append(itemId+columnSeparator+"\""+bidder_userId+"\""+columnSeparator+bid_time+columnSeparator+bid_amount+"\n");
            }

            //Now let's start to insert data
            //fill in seller relation
            if(!(sellerAList.contains(seller_userId))){
              sellerBW.append("\""+seller_userId+"\""+columnSeparator+seller_rating+"\n");
              sellerAList.add(seller_userId);
            }
            //fill in category relation
            for(int i=0; i<categoryList.length; i++){
              categoryBW.append(itemId + columnSeparator + "\"" + getElementText(categoryList[i]) + "\"" + "\n"); 
            }

           //fill in item relation
           itemListBW.append(itemId+columnSeparator+"\""+name+"\""+
columnSeparator+"\""+seller_userId+"\""+columnSeparator+currently+
columnSeparator+buy_price+columnSeparator+first_bid+columnSeparator+
number_of_bids+columnSeparator+"\""+location+"\""+columnSeparator+
"\""+country+"\""+columnSeparator+latitude+columnSeparator+longitude+
columnSeparator+started+columnSeparator+ends+columnSeparator+"\""+
description+"\""+"\n");
          } 
           bidderBW.close();
           sellerBW.close();
           bidsBW.close();
           categoryBW.close();
           itemListBW.close();
          

        }catch(Exception e){System.out.println("count: "+count);e.printStackTrace();}        
        
        
        /**************************************************************/
        
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
    }
}
