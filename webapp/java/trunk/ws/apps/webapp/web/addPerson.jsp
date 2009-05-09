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

<script type="text/javascript">
    //<![CDATA[
    tinyMCE.init({
        mode : 'textareas',
        theme : 'simple'
    });
    //]]>
</script>
<script type="text/javascript">
    var context = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.servletContext.contextPath}/js/validateform.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/js/httpobject.js" type="text/javascript"></script>
        
<!-- div class="inside" -->
 
    <div id="yield">
        <c:choose>
            <c:when test="${requestScope['isEditable']}">
                <h1>Update user</h1>
            </c:when>
            <c:otherwise>
                <h1>New user</h1>
            </c:otherwise>
        </c:choose>
        
        
        <form name="addperson" action="${pageContext.request.contextPath}/api/person/fileuploadPerson?isEditable=${requestScope['isEditable']}" method="POST" enctype="multipart/form-data" onsubmit="return checkUserFields()" >                    
    
            <fieldset id="user_form">
                <legend>User Details</legend>
                <label for="add_user_name">Username</label>                
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">                        
                        <input id="user_name" name="user_name" type="text" value="${userBean.loggedInPerson.userName}" size="30"/> <!-- onblur="checkUser();" --> <p id="usercheck"></p
                    </c:when>
                    <c:otherwise>
                        <input id="user_name" name="user_name" type="text" value="" size="30"/>  <p id="usercheck"></p>
                    </c:otherwise>
                </c:choose>

                <br />
                    
                <label for="password">Password</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">    
                        <input id="password" name="password" size="30" type="password"  value="${userBean.loggedInPerson.password}" /><br />
                    </c:when>
                    <c:otherwise>
                        <input id="password" name="password" size="30" type="password"  value="" /><br />
                    </c:otherwise>
                </c:choose>
                <label for="passwordx">Confirm Password</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="passwordx" name="passwordx" size="30" type="password" value="${userBean.loggedInPerson.password}" onblur="checkPwdMatch();" /><p id="checkpwdmatch"></p>
                    </c:when>
                    <c:otherwise>
                        <input id="passwordx" name="passwordx" size="30" type="password" onblur="checkPwdMatch();" /><p id="checkpwdmatch"></p>
                    </c:otherwise>
                </c:choose>
                <br />
                    
                <label for="first_name">Firstname</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="first_name" name="first_name" value="${userBean.loggedInPerson.firstName}" size="30" type="text"  />
                    </c:when>
                    <c:otherwise>
                        <input id="first_name" name="first_name" value="" size="30" type="text"  />
                    </c:otherwise>
                </c:choose>
                <br />
                    
                <label for="last_name">Lastname</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="last_name" name="last_name" value="${userBean.loggedInPerson.lastName}" size="30" type="text"  />
                    </c:when>
                    <c:otherwise>
                        <input id="last_name" name="last_name" value="" size="30" type="text"  />                          
                    </c:otherwise> 
                </c:choose>
                  
                <br />
                    
                <label for="email">Email</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="email" name="email" size="30" type="text" value="${userBean.loggedInPerson.email}" onblur="isValidEmail();" /><p id="isvalidemail"></p>
                    </c:when>
                    <c:otherwise>
                        <input id="email" name="email" size="30" type="text" value="" onblur="isValidEmail();" /><p id="isvalidemail"></p>
                    </c:otherwise>
                </c:choose>
                <br />
                    
                <label for="telephone">Telephone</label>
                 <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                <input id="telephone" name="telephone" size="30" type="text" value="${userBean.loggedInPerson.telephone}" onblur="isValidTelephone();" /><p id="isvalidtelephone"></p>
                 </c:when>
                 <c:otherwise>
                     <input id="telephone" name="telephone" size="30" type="text" value="" onblur="isValidTelephone();" /><p id="isvalidtelephone"></p>
                 </c:otherwise>
             </c:choose>
                 
                 
                <br />
                    
                <label for="user_image">Image</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        
                        <input id="upload_person_image" name="upload_person_image" value="${userBean.loggedInPerson.imageURL}" type="file" />
                    </c:when>
                    <c:otherwise>
                        <input id="upload_person_image" name="upload_person_image" type="file" />
                    </c:otherwise>
                </c:choose>
                <br />
                    
                <label for="summary">Summary</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <textarea cols="40" id="summary" name="summary" rows="20" >${userBean.loggedInPerson.summary}</textarea><br />
                    </c:when>
                    <c:otherwise>
                        <textarea cols="40" id="summary" name="summary" rows="20" ></textarea><br />
                    </c:otherwise>
                </c:choose>
                    
                <label for="timezone">Timezone</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <select id="timezone" name="timezone" >
                            <option selected>${userBean.loggedInPerson.timezone}</option>                            
                             <%@ include file="timezones.html" %> 
                        </select>
                    </c:when>
                    <c:otherwise>
                        <select id="timezone" name="timezone">
                             <%@ include file="timezones.html" %>                            
                        </select>
                    </c:otherwise>
                </c:choose>
                <!--[eoform:users]-->
            </fieldset>                
            <fieldset id="address_form">
                <legend>Address</legend>
                <!--[form:address]-->
                <label for="street1">Street 1</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="street1" name="street1" value="${userBean.loggedInPerson.address.street1}" size="30" type="text"  /> 
                    </c:when>
                    <c:otherwise>
                        <input id="street1" name="street1" value="" size="30" type="text"  /> 
                    </c:otherwise>
                </c:choose>
                <br />
                    
                <label for="street2">Street 2</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="street2" name="street2" value="${userBean.loggedInPerson.address.street2}" size="30" type="text"  /><br />
                    </c:when>
                    <c:otherwise>
                        <input id="street2" name="street2" value="" size="30" type="text"  /><br />
                    </c:otherwise>
                </c:choose>
                <label for="zip">Zip</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="zip" name="zip" size="30" type="text" value="${userBean.loggedInPerson.address.zip}" onblur="isValidZip();fillCityState();" /><p id="isvalidzip"></p>
                    </c:when>
                    <c:otherwise>
                        <input id="zip" name="zip" size="30" type="text" value="" onblur="isValidZip();fillCityState();" /><p id="isvalidzip"></p>
                    </c:otherwise>
                </c:choose>
                <br />
                    
                <label for="city">City</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="city" name="city" value="${userBean.loggedInPerson.address.city}" size="30" type="text"  /><br />
                    </c:when>
                    <c:otherwise>
                        <input id="city" name="city" value="" size="30" type="text"  /><br />
                    </c:otherwise>
                </c:choose>

                <label for="state">State</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <input id="state" name="state" value="${userBean.loggedInPerson.address.state}" size="30" type="text"  /><br />
                    </c:when>
                    <c:otherwise>
                        <input id="state" name="state" value="" size="30" type="text"  /><br />
                    </c:otherwise>
                </c:choose>
                <label for="country">Country</label>
                <c:choose>
                    <c:when test="${requestScope['isEditable']}">
                        <select id="country" name="country">
                            <%@ include file="countries.html" %>                             
                        </select>
                    </c:when>
                    <c:otherwise>
                        <select id="country" name="country">
                            <option selected>${userBean.loggedInPerson.address.country}<option>                            
                             <%@ include file="countries.html" %> 
                        </select>
                    </c:otherwise>
                </c:choose>
                <br />
                <!--[eoform:address]-->
                <!-- div id="progressBar"></div -->
            </fieldset>
            
            <c:choose>
                <c:when test="${requestScope['isEditable']}">
                    <input type="submit" value="Update" name="Submit" id="submit"  />
                    <input type="reset" value="Reset" name="addpersonreset" />
                </c:when>
                <c:otherwise>
                    <input type="submit" value="Create" name="Submit" id="submit"  />
                    <input type="reset" value="Reset" name="addpersonreset" />
                </c:otherwise>
            </c:choose>
        </form>
    </div>
<!-- /div -->
     
     
