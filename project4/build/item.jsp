<html>
    <head>
        <title>Search Items</title>
    </head>
    <style>
body {
    background-color: black;
    color: white;
}
.main {
    align: center;
    width: 50%;
    margin: 0 auto;
}

td.col-prop {
    text-align: left;
}

td.col-val {
    text-align: right;
}

    </style>
    <body>
        <div class="main">
            <h1>eBay Item Info</h1>
            <textarea><%= request.getAttribute("xmlData") %></textarea>
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
                    <!--  -->
                    <td class="col-prop">Current price</td>
                    <td class="col-val"><%= request.getAttribute("Currently") %></td>
                </tr>
                <tr>
                    <!-- NAME -->
                    <td class="col-prop">First bid</td>
                    <td class="col-val"><%= request.getAttribute("First_Bid") %></td>
                </tr>
            </table>

            <h2>Categories</h2>
            <ul>
                <% for(String s : (String[]) request.getAttribute("Categories")) { %>
                    <li><%= s %></li>
                <% } %>
            </ul>
        </div>
    </body>
</html>
