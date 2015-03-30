<html>
  <head>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>

	<script type="text/javascript">
		function initialize() {
			var geocoder = new google.maps.Geocoder(); 
			var latlng = new google.maps.LatLng(34.063509,-118.44541); 
			var myOptions = { 
				zoom: 14, 
				center: latlng, 
				mapTypeId: google.maps.MapTypeId.ROADMAP 
			}; 
			var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions); 
		}
	</script>

	<script type="text/javascript">      
      var center_location;
      function CustomGeocoder() {   
        var geocoder = new google.maps.Geocoder();
        geocoder.geocode( {'address': document.getElementById("addressInText").value },
          	function(results, status) {
	            if (status == google.maps.GeocoderStatus.OK) {
	              	center_location = results[0].geometry.location;
	              	var myOptions = { 
						zoom: 14,  
						center: center_location, 
						mapTypeId: google.maps.MapTypeId.ROADMAP 
					}; 
					var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);    
	            } 
	            else {
		            document.getElementById("map_canvas").style.display = "none"; 	            		
	            }
          	}
        ); }
	</script> 

  </head>
  <body onload="CustomGeocoder()">
	<style type="text/css"> 
		html { height: 100% } 
		body { height: 100%; margin: 0px; padding: 0px } 
		#map_canvas { height: 100% } 
	</style> 

	<form action="/eBay/item">
	<%
	    String qid = request.getParameter("itemID");
		out.println("Enter Item Id: <input type=\"text\" name=\"itemID\" value=\"" + qid +"\">");
		out.println("<input type=\"hidden\" id=\"addressInText\" value=\"\">");

	%>
	<input type="submit" value="Submit"><br>
	</form>
	<%
		String totalNumber = request.getAttribute("totalNumber").toString();
		String ItemId = request.getParameter("itemID").toString();

		if (totalNumber.equals("1"))
		{
			out.println("<p><b>Name</b>: " + request.getAttribute("Name") + "</p>");
			out.println("<p><b>Categories</b>: " + request.getAttribute("Categories") + "</p>");
			out.println("<p><b>Currently</b>: " + request.getAttribute("Currently") + "</p>");
			if (request.getAttribute("Buy_Price_Valid").equals("True"))
			{
				out.println("<p><b>Buy_Price</b>: " + request.getAttribute("Buy_Price") + "<br>");
			}
			out.println("<p><b>FirstBid</b>: " + request.getAttribute("First_Bid") + "</p>");
			out.println("<p><b>Number of Bids</b>: " + request.getAttribute("Number_of_Bids") + "</p>");
			out.println("<p><b>Location</b>: " + request.getAttribute("Location") + "</p>");
			out.println("<p><b>Country</b>: " + request.getAttribute("Country") + "</p>");
			out.println("<p><b>Latitude</b>: " + request.getAttribute("Latitude") + "</p>");
			out.println("<p><b>Longitude</b>: " + request.getAttribute("Longitude") + "</p>");
			out.println("<p><b>Started</b>: " + request.getAttribute("Started") + "</p>");
			out.println("<p><b>Ends</b>: " + request.getAttribute("Ends") + "</p>");
			out.println("<p><b>SellerID</b>: " + request.getAttribute("SellerID") + "</p>");
			out.println("<p><b>Rating of Seller</b>: " + request.getAttribute("SellerRating") + "</p>");
			out.println("<p><b>Bids</b>: " + request.getAttribute("Bids") + "</p>");
			out.println("<p><b>Description</b>: " + request.getAttribute("Description") + "</p>");

			String addressInText = request.getAttribute("Location").toString() + ", " + request.getAttribute("Country").toString();
			out.println("<script> document.getElementById(\"addressInText\").value=\"" + addressInText + "\"; </script>");
		}
		else 
			out.println("<p>Item with given ItemId was not found.</p>");
	
	%>

	<div id="map_canvas" style="width:500px; height:500px;"></div> 

	<p><a href="/eBay/search">Keyword Search</a></p>
  </body>
</html>
