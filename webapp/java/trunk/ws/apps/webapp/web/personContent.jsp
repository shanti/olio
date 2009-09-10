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
 * Author: Kim LiChong. Sun Microsystems, Inc.
 * 
 */
        
%>


<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="mf" type="org.apache.olio.webapp.model.ModelFacade" scope="application"/>
<jsp:useBean id="userBean" class="org.apache.olio.webapp.util.UserBean" scope="session"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

 <script src="${pageContext.request.contextPath}/js/httpobject.js" type="text/javascript"></script>
  <script src="${pageContext.request.contextPath}/js/invitations.js" type="text/javascript"></script>
 
<script type="text/javascript">
    var revokeLink = "${pageContext.servletContext.contextPath}/api/person?actionType=revoke_invite&user_name=";
    var rejectLink = "${pageContext.servletContext.contextPath}/api/person?actionType=reject_invite&user_name=";
    var approveLink= "${pageContext.servletContext.contextPath}/api/person?actionType=approve_friend&user_name=";
    function handleApproveFriendship() {
             if (http.readyState == 4) {
                 results = http.responseText.split("\n");
                 document.getElementById("messages").innerHTML="<font color=green>Friendship approved.</font>";
                 document.getElementById("rq").innerHTML=results[0];
                 document.getElementById("friendCloud").innerHTML=results[1];
                 for(i=2; i < results.length - 1; i++) {
                    var ilist = ilist + results[i];
                 }
                 document.getElementById("incoming_list").innerHTML=ilist;
             }
    }

    function approveFriendship(person,friend) {        
             alert ("I, " + person + " accept the offer of friendship from " + friend);
             http.open("GET", approveLink + escape(person) + "&friend=" + escape(friend) , true);
             http.onreadystatechange =  handleApproveFriendship;
             http.send(null);
    }
/*
    function handleRevokeInvite() {
             if (http.readyState == 4) {
                 //results = http.responseText.split("\n");
                 //document.getElementById("messages").innerHTML=results[0];
                 for(i=1; i < results.length - 1; i++) {
                    var ilist = ilist + results[i];
                 }
                 document.getElementById("outgoing_list").innerHTML=ilist;
             }
    }
*/
    function revokeInvite(person,friend) {
        alert("person is " + person);
             http.open("GET", revokeLink + escape(person) + "&friend=" + escape(friend) , true);
             http.onreadystatechange =  handleRevokeInvite;
             http.send(null);
    }

    function handleRejectInvite() {
             if (http.readyState == 4) {
                 results = http.responseText.split("\n");
                 document.getElementById("messages").innerHTML=results[0];
                 document.getElementById("rq").innerHTML=results[1];
                 for(i=2; i < results.length - 1; i++) {
                    var ilist = ilist + results[i];
                 }
                 document.getElementById("incoming_list").innerHTML=ilist;
             }
    }

    function rejectInvite(person,friend) {
             alert ("I, " + person + " reject the offer of friendship from " + friend);
             
             http.open("GET", rejectLink + escape(person) + "&friend=" + escape(friend) , true);
             http.onreadystatechange =  handleRejectInvite;
             http.send(null);
    }
</script>
 

      
  
                 
   <div id="user_header">
      <div id="user_thumbnail">
                    <div style='width: 150px; height: 150px; border: 1px solid #CCC; color: #666;
                        text-align: center; vertical-align: middle; display: table-cell;'>

                        <img src="${mf.artifactPath}/${requestScope['displayPerson'].imageThumbURL}" height=90px width=90px />
                    </div>
      </div>
   
    <div id="main_user_details">
        <h1 class="inline">${requestScope['displayPerson'].userName}</h1>  
        <c:if test="${requestScope['displayPerson'].userName == userBean.loggedInPerson.userName}">            
              <a href="${pageContext.servletContext.contextPath}/person?isEditable=true&actionType=edit_person">Edit</a>
          </c:if>
        <br/>        
        <h2 class="inline">${requestScope['displayPerson'].firstName} &nbsp; ${requestScope['displayPerson'].lastName} </h2>
        <hr />
        ${requestScope['displayPerson'].timezone}  <br />
        <div id="user_address">
            
             ${requestScope['displayPerson'].address.street1} ${requestScope['displayPerson'].address.street2} <br />
            ${requestScope['displayPerson'].address.city} ${requestScope['displayPerson'].address.state}  ${requestScope['displayPerson'].address.zip} <br />
            ${requestScope['displayPerson'].address.country}
        </div>
        <br />
        <span id="user_telephone"> ${requestScope['displayPerson'].telephone}</span> <br />
        <span id="user_email"> ${requestScope['displayPerson'].email}</span> <br />
    </div>
    
    <div id="profile_friendship_link"></div>
    <div class="clr"></div>

        
        <div id="user_summary">
            ${requestScope['displayPerson'].summary}
            
        </div>
        <hr class="clr" />
        
        <div id="posted">
            <h1>Your Recently Posted Events</h1>
            <ol>
                <c:forEach var="event" items="${requestScope['userEvents']}">                
                    <li><a href="${pageContext.servletContext.contextPath}/event/detail?socialEventID=${event.socialEventID}">${event.title}</a></li>                           
                </c:forEach>
            </ol>
            <a href="${pageContext.servletContext.contextPath}/person?&user_name=${requestScope['displayPerson'].userName}&actionType=display_myPostedEvents">more...</a>            
        </div>
        
        <div id="friend_cloud">
            <h2>Friend Cloud</h2>
            <p id="friend_cloud"> 
            <c:forEach var="friend" items="${requestScope['displayPerson'].friends}">                 
                <a href="${pageContext.servletContext.contextPath}/person?&user_name=${friend.userName}&actionType=display_person">${friend.userName}</a>
            </c:forEach>
            <p class="clr" />
            
            <a href="${pageContext.servletContext.contextPath}/person?user_name=${displayPerson.userName}&actionType=display_friends">more...</a>

        </div>
            
            <div id="incoming">
                <fieldset id="incoming_requests">
                    <legend>Incoming friendship invitations</legend>
                    <ol id="incoming_list">
                        <c:forEach var="incoming" items="${requestScope['displayPerson'].incomingInvitations}">  
                            
                            <li id="incoming_friend_request">
                                <a href="${pageContext.servletContext.contextPath}/person?user_name=${incoming.requestor.userName}&actionType=display_person">
                                ${incoming.requestor.firstName}&nbsp;${incoming.requestor.lastName}</a>
                                <div id="approve_friend"><input type="button" value="Approve Friendship" 
                                    onclick="approveFriendship('${userBean.loggedInPerson.userName}','${incoming.requestor.userName}')" /></div>
                                <div id="reject_invite_friend"><input type="button" value="Reject invite" 
                                onclick="rejectInvite('${userBean.loggedInPerson.userName}','${incoming.requestor.userName}')" /></div>
                            </li>                       
                        </c:forEach>
                    </ol>
                </fieldset>
            </div>
            <a name="outgoing_requests"></a>
            <div id="outgoing">
                <fieldset id="outgoing_requests">
                    <legend>Outgoing friendship invitations</legend>
                    <ol id="outgoing_list">
                        
                        <c:forEach var="outgoing" items="${requestScope['displayPerson'].outgoingInvitations}">  
                        
                        <li id="outgoing_friend_request">
                                          <a href="${pageContext.servletContext.contextPath}/person?user_name=${outgoing.candidate.userName}&actionType=display_person">
                                ${outgoing.candidate.firstName}&nbsp;${outgoing.candidate.lastName}</a>                                          
                            
                           
                            <div id="revoke_invite_friend">
                                <input type="button" onclick="revokeInvite('${userBean.loggedInPerson.userName}','${outgoing.candidate.userName}')" class="friend_link" value="Revoke invite" />
                            </div>
                        </li>
                         </c:forEach>   
                            
                    </ol>
                </fieldset>
            </div>
<hr />
        </div> <!-- user_header -->
        
                                                         


    
