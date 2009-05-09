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
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="a" uri="http://jmaki/v1.0/jsp" %>
<jsp:useBean id="mf" type="org.apache.olio.webapp.model.ModelFacade" scope="application"/>
<jsp:useBean id="userBean" class="org.apache.olio.webapp.util.UserBean" scope="session"/>
<% userBean.setModelFacade(mf); %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
      <meta http-equiv="content-type" content="text/html;charset=UTF-8" />
      <title>Web2.0 Java EE Application: index</title>    
      <link href="${pageContext.request.contextPath}/css/scaffold.css" media="screen" rel="stylesheet" type="text/css" />
      <link href="${pageContext.request.contextPath}/css/site.css" media="screen" rel="stylesheet" type="text/css" />
    </head>
    
    <body>
    <div id="outer_wrapper">
      <div id="header">
        <h1>Java EE Performance Application</h1>    
        <div id="nav_wrapper">
          <c:if test="${empty userBean.loggedInPerson}">
           <div id="inline_login">
            <a name="login"></a>
            <form method="post" action="${pageContext.request.contextPath}/person/login">
                 <label for="user_name">Username</label>
                 <input id="user_name" name="user_name" size="12" type="text" />
                 <label for="password">Password</label>
                 <input id="password" name="password" size="12" type="password" />
                 <input name="submit" type="submit" value="Login" />  
            </form>
           </div>
          </c:if>
          <ul id="main_nav">
              <li ><a href="${pageContext.servletContext.contextPath}/event/list" title="Home"><span>Home</span></a></li>  
              <c:if test="${not empty userBean.loggedInPerson}">
               <li><a href="${pageContext.servletContext.contextPath}/event/addEvent" title="Add Event"><span>Add Event</span></a></li>
               <li><a href="${pageContext.servletContext.contextPath}/site.jsp?page=searchUsers.jsp" title="Find Users"><span>Users</span></a></li>
               <li><a href="${pageContext.servletContext.contextPath}/person?isEditable=true&actionType=edit_person" title="Edit Profile"><span>Edit Profile</span></a></li>
              </c:if>
              <c:if test="${empty userBean.loggedInPerson}">
               <li><a href="${pageContext.servletContext.contextPath}/site.jsp?page=addPerson.jsp" title="Register"><span>Register</span></a></li>
              </c:if>
          </ul>
        </div>
        <div class="clr"></div>
      </div>

      <div id="inner_wrapper">
        <div id="content">
          <div class="rounded_corner top_right"><span></span></div>
          <div class="rounded_corner top_left"><span></span></div>
          <div class="inside">
            <div id="messages">
              ${userBean.displayMessage}
            </div>
            <div id="yield">
             <c:choose>
                 <c:when test="${not empty param['page']}">
                  <jsp:include flush="true" page="${param['page']}">
                   <jsp:param name="*" value="*"/>   
                  </jsp:include>
                 </c:when>
                 <c:otherwise>
                  <c:choose>
                   <c:when test="${param['cachedContent']}">
                       <% out.flush();
                          out.print(request.getAttribute("content")); 
                          out.flush();
                       %>
                   </c:when>      
                   <c:otherwise>
                    <jsp:include flush="true" page="eventList.jsp">
                     <jsp:param name="*" value="*"/>
                    </jsp:include>
                   </c:otherwise>
                  </c:choose>
                 </c:otherwise>
                </c:choose>
            </div>
          </div>
          <div class="rounded_corner bottom_right"><span></span></div>
          <div class="rounded_corner bottom_left"><span></span></div>
        </div>

        <div id="sidebar">
          <div class="rounded_corner top_right"><span></span></div>
          <div class="rounded_corner top_left"><span></span></div>
          <div class="inside">
            <c:if test="${not empty userBean.loggedInPerson}">
             Hello, <strong><a href="${pageContext.servletContext.contextPath}/person?user_name=${userBean.loggedInPerson.userName}&actionType=display_person">${userBean.loggedInPerson.userName}</a></strong>
             <a href="${pageContext.request.contextPath}/logout"> (Logout)</a><br/>
            </c:if>
            <c:if test="${empty userBean.loggedInPerson}">Not logged in.</c:if>
            <hr />
              <div id="calendar"><a:widget name="yahoo.calendar"/></div>
              <script type="text/javascript">
                    jmaki.subscribe("/yahoo/calendar/onSelect", calendarListener);
                    var baseCalLink = "${pageContext.servletContext.contextPath}/event/list?order=${requestScope['order']}&orderBy=${requestScope['orderBy']}&displayType=partial";
                    function calendarListener(item) {
                          var tokstr = item.value.toString();
                          var toks = tokstr.split(" ");
                          var date = toks[2];
                          var month = toks[1];
                          var year = toks[3];
                          var clink = baseCalLink + "&day=" + date + "&month=" + month + "&year=" + year;
                          jmaki.doAjax({url: clink, callback: function(req) {
                                if (req.readyState == 4) {
                                    if (req.status == 200) {
                                        var yielddiv = document.getElementById("yield");
                                        yielddiv.innerHTML=req.responseText;
                                    }
                                }
                            }});
                    }
                </script>
            <hr />
            <div id="upcoming_subset">
              <c:if test="${not empty userBean.loggedInPerson}">
                <h2 style="font-size: small;">Your Upcoming Events</h2>
                <ol id="top_upcoming_events">
                <c:forEach var="se" items="${userBean.upcomingEvents}">
                    <li><a href="${pageContext.servletContext.contextPath}/event/detail?socialEventID=${se.socialEventID}">${se.title}</a></li>    
                </c:forEach>
               </ol>
               <div style="text-align: right; padding-right: 25px;">
		 <a href="${pageContext.servletContext.contextPath}/event/upcoming?userName=${userBean.loggedInPerson.userName}">more...</a>
              </div>
              </c:if>
            </div>
            <c:if test="${not empty userBean.loggedInPerson}">
             <div id="requests_link">
              <a href="${pageContext.servletContext.contextPath}/person?actionType=display_person&user_name=${userBean.loggedInPerson.userName}#incoming_requests"><div id="rq">friendship requests (${userBean.loggedInPerson.friendshipRequests})</div></a>
             </div>
            </c:if><hr />
              <form id="tagSearchForm" method="get" action="${pageContext.servletContext.contextPath}/tag/display" >
           	<input type="text"  size="20" name="tag" />
           	<input type="submit" value="Search Tags" name="tagsearchsubmit" />
     	      </form>
          </div>
          <div class="rounded_corner bottom_right"><span></span></div>
          <div class="rounded_corner bottom_left"><span></span></div>
        </div>
        <div id="footer">
            <%@ include file="footer.html" %>
        </div>
      </div>

      <div id="outer_reflection_wrapper">
        <div id="inner_reflection_wrapper">
          <div id="reflection"></div>
          <div id="reflec_right">&nbsp;</div>

        </div>
        <div id="reflec_left">&nbsp;</div>
      </div>

    </div>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/jmaki-min.js" ></script>
    </body>
</html>
