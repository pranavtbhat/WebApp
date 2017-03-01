<html>
    <head>
        <title>Search Items</title>
    </head>
    <link rel="stylesheet" href="search.css"></style>
    <script src="search.js" type="text/javascript"></script>
    <body>
        <div class="main">
            <h1>eBay Item Search</h1>
            <form action="" method="GET" autocomplete="off">
                Please enter your query: <input id="input" name="query" type="text" onKeyUp="sendAjaxRequest(this.value);">
                <input type="hidden" name="numResultsToSkip" value=0>
                <input type="hidden" name="numResultsToReturn" value=20>
                <input type="submit">
            </form>

            <% 
            String query = (String) request.getAttribute("query");
            String[] itemIds = (String []) request.getAttribute("itemIds");
            String[] itemNames = (String []) request.getAttribute("itemNames");


            Integer numResultsToSkip = (Integer)request.getAttribute("numResultsToSkip");
            Integer numResultsToReturn = (Integer) request.getAttribute("numResultsToReturn");
            %>

            <% if(itemIds != null){ %>
            <h2>Showing results for <%= query %></h2>
            <table>
                <tr>
                    <th>ItemID</th>
                    <th>Name</th>
                </tr>
                <% for(int i=0;i<itemIds.length;i++) { %>
                <tr>
                    <% String id = (String) itemIds[i]; %>
                    <td class="col-id"><a href="/eBay/item?id=<%= id %>"><%= itemIds[i] %></a></td> 
                    <td class="col-name"><%= itemNames[i] %></td>
                </tr>
                <% } %>
                </ol>
            </table>
            Results <%= numResultsToSkip %> - <%= numResultsToSkip + itemIds.length %> 
            <% if(itemIds.length == numResultsToReturn) { %>
            <div class="buttons">
                <form action="" method="GET">
                    <input type="hidden" name="query" value="${query}">
                    <input type="hidden" name="numResultsToSkip" value="${numResultsToSkip + numResultsToReturn}">
                    <input type="hidden" name="numResultsToReturn" value="${numResultsToReturn}">
                    <input type="submit" value="Next 20">
                </form>
            </div>
            <% } %>
            <% } %>
    </body>
</html>
