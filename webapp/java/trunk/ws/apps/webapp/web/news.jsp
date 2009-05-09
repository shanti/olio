<%
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>BluePrints News Page</title>
        
        <style>
            p {
            width : 70%;
            background-color : #FFEFD5;
            font-size : 80%
            }
        </style>
        
    </head>
    <body>
        <jsp:include page="./resources/banner.jsp" />
        <h2><a href="http://blueprints.dev.java.net">BluePrints News</a></h2>
        
        <script type="text/javascript" src="resources/news.js"></script>
        <script type="text/javascript">
        var news = new bpuinews.RSS();
        dojo.addOnLoad(function(){news.getRssInJson('getRssFeed', 'https://blueprints.dev.java.net/servlets/ProjectRSS?type=news');});
        </script>
        
        <center>
            <table border="0" width="95%">
                <tr>
                    <td>
                        <button id="previous" type="button">&lt;&lt; Previous</button>
                    </td>
                    <td>
                        <button id="next" type="button">Next &gt&gt</button>
                    </td>
                </tr>
            </table>
        </center>
        <div id="news"></div>
        
        <jsp:include page="./resources/footer.jsp" />
    </body>
</html>
