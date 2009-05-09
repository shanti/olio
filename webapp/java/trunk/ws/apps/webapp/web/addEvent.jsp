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
 * This page is to collect the details of the event. 
 * The uploaded literature file and the image are stored in LocalFS storage. 
 * All the file types are handled in a file called fileService.php. 
 * If you want to open any file, you just need to call this and pass the file name. 
 * Once you submit this page, the event gets added and will be directed to the home page. 
 * 
 */
%>
<jsp:useBean id="mf" type="org.apache.olio.webapp.model.ModelFacade" scope="application"/>
<jsp:useBean id="userBean" class="org.apache.olio.webapp.util.UserBean" scope="session"/>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
//<![CDATA[
tinyMCE.init({
mode : 'textareas',
theme : 'simple'
});
//]]>
</script>
<script type="text/javascript">
    var context = "${pageContext.servletContext.contextPath}";
</script>
<script src="${pageContext.servletContext.contextPath}/js/validateform.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/js/httpobject.js" type="text/javascript"></script>
<c:if test="${not empty requestScope['socialEvent']}">
    <h1>Update event</h1>
</c:if>
<c:if test="${empty requestScope['socialEvent']}">
    <h1>New event</h1>
</c:if>
<form name="addEvent" action="${pageContext.servletContext.contextPath}/api/event/addEvent" method="POST" enctype="multipart/form-data" onsubmit="return checkEventFields()">
<fieldset id="event_form">
    <legend>Event Details</legend>
    <div id="basic_event_form">
      <p><label for="title">Title</label><br/>
      <input id="title" name="title" size="30" type="text" value="${requestScope['title']}" /></p>
      <p><label for="summary">Summary</label><br />
      <textarea cols="37" id="summary" name="summary" rows="20" >${requestScope['summary']}</textarea></p>
      <p><label for="description">Description</label><br/>
      <textarea cols="37" id="description" name="description" rows="20" >${requestScope['description']}</textarea></p>
    </div>

    <div id="misc_event_form">
      <p>
      <label for="event_timestamp">Event date and time</label><br/>
      <c:choose>
       <c:when test="${not empty requestScope['socialEvent']}">
        ${requestScope['socialEvent'].yearDropDown} ${requestScope['socialEvent'].monthDropDown} ${requestScope['socialEvent'].dayDropDown}
        &mdash;<br/> 
        ${requestScope['socialEvent'].hourDropDown}:${requestScope['socialEvent'].minuteDropDown}
       </c:when>
       <c:otherwise>
        ${mf.yearDropDown} ${mf.monthDropDown} ${mf.dayDropDown}
        &mdash;<br/> 
        ${mf.hourDropDown}:${mf.minuteDropDown}
       </c:otherwise>
      </c:choose>
      </p><p>
      <label for="telephone">Telephone</label><br/>
      <input id="telephone" name="telephone" size="30" type="text" value="${requestScope['telephone']}" onblur="isValidTelephone();" />
      <p id="isvalidtelephone"></p>
      </p>
      <br /><hr /><br />
      <p><label for="upload_event_image">Image</label><br/>
        <input name="upload_event_image" id="upload_event_image" type="file" />
      </p>
      <p>
       <label for="upload_literature">
  	Document <sup><em>(only PDF, Word, and plain text documents)</em></sup>
       </label><br/>
       <input name="upload_event_literature" id="upload_event_literature" type="file" />
      </p>
      <p><label for="tags">Tags</label><br/>
       <input id="tags" name="tags" size="40" type="text" value="${requestScope['tags']}"/>
      </p>
    </div>
</fieldset>

<fieldset id="address_form">
<legend>Address</legend>
    <label for="street1">Street 1</label>
    <input id="street1" name="street1" size="30" type="text" value="${requestScope['street1']}" /><br />
    <label for="street2">Street 2</label>
    <input id="street2" name="street2" size="30" type="text" value="${requestScope['street2']}" /><br />
    <label for="zip">Zip</label>
     <input id="zip" name="zip" size="30" type="text" value="${requestScope['zip']}" onblur="isValidZip();fillCityState();" /><br />
    <p id="isvalidzip"></p>
    <label for="city">City</label>
     <input id="city" name="city" size="30" type="text" value="${requestScope['city']}" /><br />
    <label for="state">State</label>
    <input id="state" name="state" size="30" type="text" value="${requestScope['state']}" /><br />
    <label for="country">Country</label>
    <select id="country" name="country">
     <%@ include file="countries.html" %>
     <c:if test="${not empty requestScope['country']}">
      <option selected="selected" value="${requestScope['country']}">${requestScope['country']}</option>
     </c:if>
    </select><br />
</fieldset>
<div class="clr"></div>
<c:choose>
 <c:when test="${not empty requestScope['socialEvent']}">
  <input type="submit" name="submit" value="Update"/>
  <input type="hidden" name="socialEventID" value="${requestScope['socialEvent'].socialEventID}"/>
  <jsp:setProperty name="userBean" property="forwardingUrl" 
    value="${pageContext.servletContext.contextPath}/event/updateEvent?socialEventID=${requestScope['socialEvent'].socialEventID}"/> 
 </c:when>
 <c:otherwise>
  <input type="submit" name="submit" value="Create"/>
  <jsp:setProperty name="userBean" property="forwardingUrl" 
    value="${pageContext.servletContext.contextPath}/event/addEvent"/> 
 </c:otherwise>
</c:choose>
<input type='reset' value='Reset' name="addeventreset" />
</form>
