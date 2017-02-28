<html>
    <head>
        <title>Search Items</title>
    </head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" /> 
    <style>
body {
    background-color: black;
    color: white;
}

div.container {
    align: center;
    margin-top: 5%;
    margin-left: 10%;
    margin-right: 10%;
}

div.main {
    float: left;
    width: 45%;
}

td.col-prop {
    text-align: left;
}

td.col-val {
    text-align: right;
}

#map_canvas {
    float: right;
    height: 100%;
    width: 45%;
    position: fixed;
}

    </style>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false">
    </script>

    <script type="text/javascript">
            function initialize() {
            var latitude = <%= request.getAttribute("Latitude") %>;
            var longitude = <%= request.getAttribute("Longitude") %>;

            if(latitude != null && longitude != null) {
            console.log(latitude);
            console.log(longitude);
            var latlng = new google.maps.LatLng(latitude, longitude);
            console.log(latlng);
            var myOptions = {
            zoom: 8, 
            center: latlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
            }
            }
    </script>


    <body onload="initialize()">
        <div class="container">
            <div class="main">
                <h1>eBay Item Info</h1>
                <h2>Information for ItemID: <%= request.getParameter("id") %></h2>
                <table>
                    <tr>
                        <th>Property</th>
                        <th>Value</th>
                    </tr>
                    <tr>
                        <!-- NAME -->
                        <td class="col-prop">Name</td>
                        <td class="col-val"><%= request.getAttribute("Name") %></td>
                    </tr>
                    <tr>
                        <!-- Started -->
                        <td class="col-prop">Bidding started on</td>
                        <td class="col-val"><%= request.getAttribute("Started") %></td>
                    </tr>
                    <tr>
                        <!-- Ends -->
                        <td class="col-prop">Bidding ends on</td>
                        <td class="col-val"><%= request.getAttribute("Ends") %></td>
                    </tr>
                    <tr>
                        <!-- Currently -->
                        <td class="col-prop">Current price</td>
                        <td class="col-val"><%= request.getAttribute("Currently") %></td>
                    </tr>
                    <tr>
                        <!-- First_Bid -->
                        <td class="col-prop">First bid</td>
                        <td class="col-val"><%= request.getAttribute("First_Bid") %></td>
                    </tr>
                    <% if(request.getAttribute("Buy_Price") != null) { %>
                    <tr>
                        <!-- BUY_PRICE -->
                        <td class="col-prop">Buy_Price</td>
                        <td class="col-val"><%= request.getAttribute("Buy_Price") %></td>
                    </tr>
                    <% } %>
                    <tr>
                        <!-- Location -->
                        <td class="col-prop">Location</td>
                        <td class="col-val"><%= request.getAttribute("Location") %></td>
                    </tr>
                    <tr>
                        <!-- Country -->
                        <td class="col-prop">Country</td>
                        <td class="col-val"><%= request.getAttribute("Country") %></td>
                    </tr>
                </table>

                <h3>Item sold by <%= request.getAttribute("SellerID") %> with rating <%= request.getAttribute("SellerRating") %></h3>

                <h2>Categories</h2>
                <ul>
                    <% for(String s : (String[]) request.getAttribute("Categories")) { %>
                    <li><%= s %></li>
                    <% } %>
                </ul>

                <h2>Bid History</h2>
                <% Integer numBids = Integer.parseInt((String) request.getAttribute("Number_of_Bids")); %>
                This item has <%= numBids %> bids.
                <% String[][] bidInfo = (String[][]) request.getAttribute("bidInfo"); %>
                <% if(numBids > 0 ) { %>
                <table>
                    <tr>
                        <th>Bidder ID</th>
                        <th>Time</th>
                        <th>Amount</th>
                    </tr>
                    <% for(int i=0;i<bidInfo.length;i++) { %>
                    <tr>
                        <td style="text-align: center"><%= bidInfo[i][0] %></td>
                        <td style="text-align: center"><%= bidInfo[i][1] %></td>
                        <td style="text-align: center"><%= bidInfo[i][2] %></td>
                    </tr>
                    <% } %>
                </table>
                <% } %>

            </div>

            <div id="map_canvas"></div>
        </div>
    </body>
</html>
