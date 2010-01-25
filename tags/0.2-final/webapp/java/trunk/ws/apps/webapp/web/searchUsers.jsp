                                
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
 * Author: Kim Lichong. Sun Microsystems, Inc.
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
    
    
<div class="inside">
    <div id="messages">
    </div>
    <div id="yield">
        <h1 id="search_header">Search Users</h1>
            
        <form action="${pageContext.servletContext.contextPath}/person" method="get" >
            <input id="query" name="query" type="text" />  <input name="actionType" type="submit" value="Search" />
        </form>
        <hr id="search_results_separator" />
        <div id="user_search_results">
            <ol style="list-style-type: none;">
                <c:set var="mySearchResults" scope="page" value="${requestScope['searchResults']}"/>
                    
                <c:if test="${not empty mySearchResults}">                               
                    <c:forEach var="myFriend" items="${mySearchResults}">
                        <div class="thumbnail_for_list">
                            
                                                                                    
                            <a href="${pageContext.servletContext.contextPath}/person?&user_name=${myFriend.userName}&actionType=display_person">
                                <img src="${mf.artifactPath}/${myFriend.imageThumbURL}" height=90px width=90px />
                            </a>
                        </div>
                        <div id="user_details_for_list">
                            
                            <h2 class="inline">
                                <a href="${pageContext.servletContext.contextPath}/person?&user_name=${myFriend.userName}&actionType=display_person">${myFriend.userName}</a>
                                    
                            </h2>//
                            <h3 class="inline" style="padding-bottom: 5px;">${myFriend.firstName}&nbsp;${myFriend.lastName}</h3>
                            <br/>
                            
                            
                            <c:choose>
                                
                            <c:when test="${myFriend.hasReceivedInvitation}">
                               <form method="post" action="${pageContext.servletContext.contextPath}/person?actionType=addDeleteFriend&friend=${myFriend.userName}&query=document.query.value&flag=delete" class="button-to">
                                <div>                                    
                                    <input type="submit" value="Revoke Friend" />
                                </div>
                                </form>
                            </c:when>
                            <c:otherwise>
                                  <form method="post" action="${pageContext.servletContext.contextPath}/person?actionType=addDeleteFriend&friend=${myFriend.userName}&query=document.query.value&flag=add" class="button-to">
                                <div>                                    
                                    <input type="submit" value="Add Friend" />
                                </div>
                                </form>
                                
                            </c:otherwise>
                                
                            </c:choose>
                        </div>
                        <div class="clr"></div>
                    </c:forEach>
                </c:if>
            </ol>
        </div>
            
    </div>
</div>
        
   
        
                                                         


