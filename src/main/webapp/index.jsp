<%@ page import="io.github.honeypot.servlet.HistoryServlet.HistoryEnum" %>
<%@ page import="io.github.honeypot.servlet.RankServlet.RankEnum" %>
<html>
<head>
    <title>honeypot</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/4.4.0/d3.min.js" integrity="sha256-zbE3mv7cXuSkW7mhK6Y5vnY6eXmPZPPRUYpUcNfVM/A=" crossorigin="anonymous"></script>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
          crossorigin="anonymous">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/jvectormap/2.0.4/jquery-jvectormap.min.css"
          integrity="sha256-sQoGnt24NPHEjR6SdRxBEeM8vq+ARYdTJCxm/XuQUvA=" crossorigin="anonymous"/>
    <link rel="stylesheet" href="style.css"/>

    <script
            src="https://code.jquery.com/jquery-3.1.1.min.js"
            integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
            crossorigin="anonymous"></script>

    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
            integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
            crossorigin="anonymous"></script>
    <script src="http://jvectormap.com/js/jquery-jvectormap-2.0.3.min.js"></script>
    <script src="http://jvectormap.com/js/jquery-jvectormap-world-mill.js"></script>
    <script src="index.js"></script>
</head>

<body>
<div class="title">
    <h3>Honeypot</h3>
    <h4><a href="https://cpe305.github.io/fall2016-project-JuicyPasta/">docs</a></h4>
</div>

<div class="container-fluid">

    <div class="row">
        <div class="col-sm-6">
            <div class="card map"></div>
        </div>
        <div class="col-sm-3">
            <div class="card info">
                <h4>Logs</h4>
                <!-- Nav tabs -->
                <ul class="nav nav-tabs logTabs" role="tablist">
                    <%for (HistoryEnum type : HistoryEnum.values()) { %>
                        <li role="presentation" id="<%=type.name()%>"><a href="#<%= type.name() %>" aria-controls="<%= type.name() %>" role="tab" data-toggle="tab"><%= type.name() %></a></li>
                    <%}%>
                </ul>

                <!-- Tab panes -->
                <div class="tab-content">
                    <%for (HistoryEnum type : HistoryEnum.values()) { %>
                        <div role="tabpanel" class="tab-pane" id="<%=type.name()%>">
                            <div class="log-area">
                                <ul class="log-list">
                                </ul>
                            </div>
                        </div>
                    <%}%>

                </div>

                <script>
                    $(".logTabs > #ALL").addClass("active")
                    $(".tab-content > #ALL").addClass("active")
                </script>

            </div>
        </div>

    </div>
    <div class="row">
        <%for (RankEnum type : RankEnum.values()) { %>
            <div class="col-md-3 col-sm-4 col-xs-6 rank" id="<%=type.name() %>">
                <div class="card info">
                    <h4><%=type.getName()%></h4>
                    <div class="chart bordered"></div>
                </div>
            </div>
        <%}%>
    </div>

</div>
</body>
</html>