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
 * Author: Binu John. Sun Microsystems, Inc.
 * 
 * This describes the details of an event.
 * 
 */
%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="userBean" class="org.apache.olio.webapp.util.UserBean" scope="session"/>
<jsp:useBean id="mf" type="org.apache.olio.webapp.model.ModelFacade" scope="application"/>
<%@ taglib prefix="a" uri="http://jmaki/v1.0/jsp" %>

<script type="text/javascript">
    var base= "${pageContext.request.contextPath}/images/star_";
    var addAttendeeLink = "${pageContext.request.contextPath}/api/event/addAttendee?socialEventID=${requestScope['socialEvent'].socialEventID}";
    var deleteAttendeeLink = "${pageContext.request.contextPath}/api/event/deleteAttendee?socialEventID=${requestScope['socialEvent'].socialEventID}";
    var deleteCommentLink = "${pageContext.request.contextPath}/api/event/deleteCommentsRating?socialEventID=${requestScope['socialEvent'].socialEventID}&commentId=";
    var updateCommentLink = "${pageContext.request.contextPath}/api/event/updateCommentsRating?socialEventID=${requestScope['socialEvent'].socialEventID}&comment=";
    var context = "${pageContext.request.contextPath}";
    var attendeeWidget;
    var commentWidget;
    <c:if test="${not empty userBean.loggedInPerson}">
        var loggedInPerson="${userBean.loggedInPerson.userName}";
    </c:if>
</script>
<script src="${pageContext.request.contextPath}/js/starrating.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/httpobject.js" type="text/javascript"></script>
<c:choose>
    <c:when test="${userBean.JMakiUsed}">
      <script type="text/javascript">
        function updateComment() {
           var commentdiv = document.getElementById("comments");
           var comment = commentdiv.value;
           commentWidget.updateComment(escape(comment));
        }
      </script>
    </c:when>
    <c:otherwise>
      <script src="${pageContext.request.contextPath}/js/attendee.js" type="text/javascript"></script>
      <script src="${pageContext.request.contextPath}/js/comments.js" type="text/javascript"></script>
      <script type="text/javascript">
        function updateComment() {
           var commentdiv = document.getElementById("comments");
           var comment = commentdiv.value;
           updateCommentList(escape(comment));
        } 
      </script>
    </c:otherwise>
</c:choose>
<div id="event_header">
  <div id="event_thumbnail">
    <img src="${mf.artifactPath}/${requestScope['socialEvent'].imageURL}" height=150px width=150px /></div>

  <div id="main_event_details">
    <h1 class="inline">${requestScope['socialEvent'].title}</h1>
    <c:if test="${not empty userBean.loggedInPerson && 
        userBean.loggedInPerson.userName == requestScope['socialEvent'].submitterUserName}">
       <form method="post" action="${pageContext.servletContext.contextPath}/event/updateEvent?socialEventID=${requestScope['socialEvent'].socialEventID}" class="button-to">
        <div><input type="submit" value="Edit" /></div>
       </form> 
       <form method="post" action="${pageContext.servletContext.contextPath}/event/delete?socialEventID=${requestScope['socialEvent'].socialEventID}"class="button-to">
        <div><input onclick="return confirm('Are you sure?');" type="submit" value="Delete" /></div>
       </form>
    </c:if>
    <hr />
    <fmt:formatDate dateStyle="full" timeStyle="full" type="both" value="${requestScope['socialEvent'].eventTimestamp}"></fmt:formatDate><br/><br />
    <br />
    <c:if test="${not empty requestScope['socialEvent'].literatureURL}">
        <a href="${mf.artifactPath}/${requestScope['socialEvent'].literatureURL}">Event Literature</a>;
    </c:if>
    <br />
    <div id="event_address">
        <c:if test="${not empty requestScope['socialEvent'].address}">
            ${requestScope['socialEvent'].address.street1} &nbsp; ${requestScope['socialEvent'].address.street2}
            ${requestScope['socialEvent'].address.city}, ${requestScope['socialEvent'].address.state}, ${requestScope['socialEvent'].address.zip}
            ${requestScope['socialEvent'].address.country}
        </c:if>
        <c:if test="${empty requestScope['socialEvent'].address}">
            No address given
        </c:if>
    </div>
    Contact: <span id="event_contact">${socialEvent.telephone}</span><br />
  </div>
  <div class="clr"></div>
</div>

<div id="event_attendees">
 <c:choose>
  <c:when test="${userBean.JMakiUsed}">
   <a:widget name="blueprints.list.attendeeList" 
        service="/api/event/getAttendees?socialEventID=${requestScope['socialEvent'].socialEventID}&userName=${userBean.loggedInPerson.userName}"/>
  </c:when>       
  <c:otherwise>
   <h2 id="attendees_h2" class="smaller_heading">${requestScope['socialEvent'].numAttendees} Attendees:</h2><br/>
   <c:if test="${not empty userBean.loggedInPerson}">
    <div id="attending_div">
     <c:choose>
      <c:when test="${requestScope['isAttending']}">
       <input name="unattend" type="button" value="Unattend" onclick="deleteAttendee();"/>  
      </c:when>
      <c:otherwise>
       <input name="attend" type="button" value="Attend" onclick="addAttendee();"/>   
      </c:otherwise>
     </c:choose>
    </div>
  </c:if>  
  <br/>
   <ol id="attending_list">
    <c:forEach var="attendee" items="${requestScope['socialEvent'].attendees}">
       <li><a href="${pageContext.servletContext.contextPath}/person/display?user_name=${attendee.userName}&actionType=display_person">${attendee.userName}</a></li>
    </c:forEach>
   </ol>
  </c:otherwise> 
 </c:choose>
</div>

<div id="event_description">
 <p><strong>Summary</strong></p>
 <p>${requestScope['socialEvent'].summary}</p>
 <p><strong>Description</strong></p>
 <p>${requestScope['socialEvent'].description}</p>
</div>
<div class="clr"></div>
<div id="event_map">
 <div id="map" style="width: 100%;"></div>
 <a:widget name="yahoo.map" args="{centerLat : 37.4041960114344,
		 centerLon : -122.008194923401,
                 mapType: 'REGULAR',
                 height: 480,
                 address: '${requestScope['socialEvent'].address.street1}, ${requestScope['socialEvent'].address.street2}, ${requestScope['socialEvent'].address.city}, ${requestScope['socialEvent'].address.state}, ${requestScope['socialEvent'].address.country}'
                 }" />

 <%
 /*
 <script type="text/javascript">
 // Create a map object
        var map = new YMap(document.getElementById('map'));
        // Add map type control
        map.addTypeControl();
        // Add map zoom (long) control
        map.addZoomLong();
        // Add the Pan Control
        map.addPanControl();
        // Set map type to either of: YAHOO_MAP_SAT, YAHOO_MAP_HYB, YAHOO_MAP_REG
        map.setMapType(YAHOO_MAP_REG);
        // Display the map centered on a geocoded location
        map.drawZoomAndCenter ("${requestScope['socialEvent'].address.street1}, ${requestScope['socialEvent'].address.street2}, ${requestScope['socialEvent'].address.city}, ${requestScope['socialEvent'].address.state}, ${requestScope['socialEvent'].address.country}", 5);
        YEvent.Capture(map, EventsList.onEndGeoCode, setMarker);

        function setMarker() {
            var currentGeoPoint = map.getCenterLatLon();
            var marker = new YMarker(currentGeoPoint);
            marker.addLabel("A");
            map.addOverlay(marker);
        }
 </script>
 * */
 %>
</div>

<div id="event_tags">
  <h1 class="inline">Tags::</h1>
  <div id="tag_list">${requestScope['socialEvent'].tagCloud}</div>
  <div class="clr"></div>
</div>

<div id="event_comments">
<h2 class="event_detail_heading">Comments</h2>
<c:choose>
   <c:when test="${userBean.JMakiUsed}">
    <a:widget name="blueprints.list.commentList" 
        service="/api/event/getComments?socialEventID=${requestScope['socialEvent'].socialEventID}"/>
  </c:when>       
  <c:otherwise>
   <ol id="comment_list">
    <c:forEach var="comment" items="${requestScope['socialEvent'].comments}">
    <li class="event_comment">
        ${comment.userName.userName} (<fmt:formatDate dateStyle="full" timeStyle="full" type="both" value="${comment.creationTime}"></fmt:formatDate>)<span id="rating"> &nbsp; 
            <c:forEach var="img" items="${comment.ratingGraphic}">
                <img src="${pageContext.servletContext.contextPath}/${img}"/>
            </c:forEach>
        </span>
        <p>${comment.commentString}</p>
        <c:if test="${not empty userBean.loggedInPerson && userBean.loggedInPerson.userName == comment.userName.userName}">
         <a href="#edit" id="commentsRatingToggle" class='edit_comment' style='color:#999;' onclick="return ShowHideLayer('commentsRatingBox');">Edit</a>
         or
         <a href="#delete" id="delete${comment.commentsRatingId}>" onclick="commentWidget.deleteComment(${comment.commentsRatingId});" class='edit_comment' style='color:#999;' >Delete</a>

        </c:if>
     </li> 
    </c:forEach>
   </ol>
  </c:otherwise>
</c:choose>
</div>
<div id="comment_add_link">
  <c:choose>
   <c:when test="${not empty userBean.loggedInPerson}">
    <a href="#comment" id="commentsRatingToggle" onclick="return ShowHideLayer('commentsRatingBox');">Add a comment</a>      
   </c:when>
   <c:otherwise>
    Please login to leave a comment.  
   </c:otherwise>
  </c:choose>
  <br />
</div>
<div id="commentsRatingBox" style="display: none;">
<div id="comment_form">
  <strong>Comment</strong><br/>
  <textarea cols="40" id="comments" name="comments" rows="20"><c:if test="${not empty requestScope['comment']}">${requestScope['comment']}</c:if></textarea>
  <br/>
  <strong>Rating</strong><br />
  <div id="rating" class="simple_comment_rating">
   <img border="0" src="${pageContext.request.contextPath}/images/star_off.png" 
            onmouseout="outStars(1, 0.0);" name="star_1" id="star_1" 
            onclick='rateEvent("${pageContext.request.contextPath}/api/event/updateRating?socialEventID=${requestScope['socialEvent'].socialEventID}&rating=", 1);' 
            onmouseover="overStars(1, 0.0);" >
   <img border="0" src="${pageContext.request.contextPath}/images/star_off.png" 
            onmouseout="outStars(2, 0.0);" name="star_2" id="star_2" 
            onclick='rateEvent("${pageContext.request.contextPath}/api/event/updateRating?socialEventID=${requestScope['socialEvent'].socialEventID}&rating=", 2);' 
            onmouseover="overStars(2, 0.0);" >
   <img border="0" src="${pageContext.request.contextPath}/images/star_off.png" 
            onmouseout="outStars(3, 0.0);" name="star_3" id="star_3" 
            onclick='rateEvent("${pageContext.request.contextPath}/api/event/updateRating?socialEventID=${requestScope['socialEvent'].socialEventID}&rating=", 3);' 
            onmouseover="overStars(3, 0.0);" >
   <img border="0" src="${pageContext.request.contextPath}/images/star_off.png" 
            onmouseout="outStars(4, 0.0);" name="star_4" id="star_4" 
            onclick='rateEvent("${pageContext.request.contextPath}/api/event/updateRating?socialEventID=${requestScope['socialEvent'].socialEventID}&rating=", 4);' 
            onmouseover="overStars(4, 0.0);" >
   <img border="0" src="${pageContext.request.contextPath}/images/star_off.png" 
            onmouseout="outStars(5, 0.0);" name="star_5" id="star_5" 
            onclick='rateEvent("${pageContext.request.contextPath}/api/event/updateRating?socialEventID=${requestScope['socialEvent'].socialEventID}&rating=", 5);' 
            onmouseover="overStars(5, 0.0);" >
  </div>
  <div id="ratingText"></div>
   <input type="submit" value="Comment" name="commentsratingsubmit" onclick="updateComment();">
  </div>
</div>
<hr class="clr" />
<jsp:setProperty name="userBean" property="forwardingUrl" 
    value="/event/detail?socialEventID=${requestScope['socialEvent'].socialEventID}"/> 
