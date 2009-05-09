
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


      

    <!--
    
          <div class="rounded_corner top_right"><span></span></div>
          <div class="rounded_corner top_left"><span></span></div>
          
          -->

          <div class="inside">
             
            <div id="messages">            </div>
                         
              <h1 id="friendslist">Friends of ${requestScope['displayPerson'].userName}</h1>
              <ol id="friends_list" >

                <c:forEach var="myFriend" items="${requestScope['displayPerson'].friends}">   
               
                    <li style="padding: 7px;" class="my_friend" id="friend_${myFriend.userName}">

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
                        </div>                            
                        <div class="clr"></div>           
                      </li>
        
               </c:forEach>
            </ol>

                <div class="clr"></div>
                <br />          
            </div>
  
<!--
          <div class="rounded_corner bottom_right"><span></span></div>
          <div class="rounded_corner bottom_left"><span></span></div>
          
          

        </div>
        -->
        
                                                         


