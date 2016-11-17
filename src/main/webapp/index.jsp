<%@ page import="io.github.honeypot.logger.ServiceLogType" %>

<html>
<head>
    <title>honeypot</title>
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
<div class="container-fluid">

    <h1>Honeypot</h1>

    <div class="row">
        <div class="col-sm-8">
            <div class="card map"></div>
        </div>
        <div class="col-sm-3">
            <div class="card info">
                <!-- Nav tabs -->
                <ul class="nav nav-tabs logTabs" role="tablist">
                    <li role="presentation" class="active"><a href="#ALL" aria-controls="ALL" role="tab" data-toggle="tab">ALL</a></li>
                    <%for (ServiceLogType type : ServiceLogType.values()) { %>
                        <li role="presentation" class=""><a href="#<%= type.type() %>" aria-controls="<%= type.type() %>" role="tab" data-toggle="tab"><%= type.type() %></a></li>
                    <%}%>
                </ul>

                <!-- Tab panes -->
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="ALL">
                        <div class="metadata-area">
                            <h4>metadata</h4>
                            <div class="metadata well">
                            </div>
                        </div>

                        <div class="log-area">
                            <ul class="log-list">
                            </ul>
                        </div>
                    </div>

                    <%for (ServiceLogType type : ServiceLogType.values()) { %>

                        <div role="tabpanel" class="tab-pane" id="<%=type.type() %>">
                            <div class="metadata-area">
                                <h4>metadata</h4>
                                <div class="metadata well">
                                </div>
                            </div>

                            <div class="log-area">
                                <ul class="log-list">
                                </ul>
                            </div>

                        </div>
                    <%}%>
                </div>


            </div>
        </div>

    </div>

</div>
</body>
</html>