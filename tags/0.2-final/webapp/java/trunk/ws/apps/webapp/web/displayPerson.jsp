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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="mf" type="org.apache.olio.webapp.model.ModelFacade" scope="application"/>
<html>
    <head>
        <title>Display Person Page</title>
    </head>
    <body>
        <jsp:include page="./resources/banner.jsp" />
        
        <h1>Display Person Page</h1>
        
        <table>
            <c:forEach var="person" items="${mf.allPersons}">                
                <tr>
                    <td>
                        <hr/>
                        <h3>UserName : ${person.userName}</h3>
                        First Name : ${person.firstName} <br/>
                        Last Name : ${person.lastName} <br/>
                        Summary : ${person.summary} <br/>
                    </td>
                </tr>
            </c:forEach>
        </table>
        
        <a:ajax name="blueprints.list.paging" />

        
        <jsp:include page="./resources/footer.jsp" />
    </body>
</html>
