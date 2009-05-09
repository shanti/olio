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
        
/**
 * 
 * Author: Binu John. Sun Microsystems, Inc.
 * 
 */
%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="userBean" class="org.apache.olio.webapp.util.UserBean" scope="session"/>

<script src="${pageContext.servletContext.contextPath}/js/httpobject.js" type="text/javascript"></script>
<script type="text/javascript">
       var deleteSElink = "${pageContext.servletContext.contextPath}/event/delete?socialEventID=";
       function deleteSE(se) {
         http.open("GET", deleteSElink + escape(se), true);
         http.send(null);
       }
</script>
      
<link href="${pageContext.servletContext.contextPath}/css/scaffold.css" media="screen" rel="stylesheet" type="text/css" />
<link href="${pageContext.servletContext.contextPath}/css/site.css" media="screen" rel="stylesheet" type="text/css" />

<h2 class="tight_heading">Events Tagged with ${param['tag']}</h2><br/>
<div id="event_table">
 <ol id="event_list" style="list-style-type: none;">
  <c:forEach var="event" items="${requestScope['itemList']}">
  <li id="event_1_details" class="event_item even_event" style="padding: 7px;"
    onmouseover="Element.findChildren(this, 'extra_details', true, 'div').first().show();"
    onmouseout="Element.findChildren(this, 'extra_details', true, 'div').first().hide();">
   <div class="thumbnail_for_list">
    <div style="width: 90px; height: 90px; border:1px solid #CCC; color: #666;text-align: center; vertical-align: middle; display: table-cell;">
     <a href="${pageContext.servletContext.contextPath}/event/detail?socialEventID=${event.socialEventID}"><img src="${mf.artifactPath}/${event.imageThumbURL}" height=90px width=90px /></a>
    </div>
   </div>
   <div class="event_details_for_list">
    <h2 class="tight_heading">
     <a href="${pageContext.servletContext.contextPath}/event/detail?socialEventID=${event.socialEventID}">${event.title}</a>
    </h2>
    <fmt:formatDate dateStyle="full" timeStyle="full" type="both" value="${event.eventTimestamp}"></fmt:formatDate><br/>
     <div class="extra_details" style="display: none;">
     <c:if test="${not empty userBean.loggedInPerson && 
      event.submitterUserName == userBean.loggedInPerson.userName}">
      <form method="post" action="addEvent.jsp?socialEventID=${event.socialEventID}" class="button-to">  
       <div><input type="submit" value="Edit" /></div>
      </form> 
      <form method="post" action="${pageContext.servletContext.contextPath}/event/delete?socialEventID=${event.socialEventID}" class="button-to">
       <div><input onclick="return confirm('Are you sure?');" type="submit" value="Delete" /></div>
      </form>
     </c:if>
     <br />
     Created: <fmt:formatDate dateStyle="full" timeStyle="full" type="both" value="${event.eventTimestamp}"></fmt:formatDate><br/>
      <br/>${event.description}
     </div>
    </div>
    <div class="clr"></div>
   </li>
  </c:forEach>
 </ol>
 <div class="clr"></div><br/>
 <div>
  <jsp:include flush="true" page="paginate.jsp"/>
 </div>
 <div class="clr"></div><br/>
</div>

