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
 * This describes the pagination view.
 */
%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="org.apache.olio.webapp.util.*" %>

<%
 out.flush();
 int lower=0, upper=WebappConstants.PAGINATION_NUM_PAGES_DISPLAY -1;
 int numPages = 1;
 if (request.getAttribute("numPages") != null)
    numPages = (Integer) request.getAttribute("numPages");
 int index = 0;
 if (request.getAttribute("index") != null)
     index = (Integer) request.getAttribute("index");
 
 if (numPages > 1) {
     if (numPages < WebappConstants.PAGINATION_NUM_PAGES_DISPLAY) {
        upper =  numPages-1;
     }
     else if (index > WebappConstants.PAGINATION_NUM_PAGES_DISPLAY) {
        lower = index - WebappConstants.PAGINATION_NUM_PAGES_DISPLAY + 1;
        upper = index;
     }
 
     String url = (String) request.getAttribute("pageUrl");
     if ( url != null) {
         if (url.indexOf("?") == -1)
             url += "?index=";
         else
             url += "&index=";
     }
     else {
         System.out.println ("pageUrl is not correctly set in paginate.jsp");
         url="?index=";
     }
     
     if (index > 0) {
         out.print ("<< <a href=\"" + url +
                 (index-1) + "\"><b>Previous</b></a>");
     }
     if (index > upper) {
         upper = index;
         lower = upper + 1 - WebappConstants.PAGINATION_NUM_PAGES_DISPLAY;
         lower = lower < 0 ? 0 : lower;
     }
     if (lower > 0)
         out.print ("&nbsp ..........");
     
     for (int i=lower; i<=upper; i++) {
         if (i == index) {
             out.print("&nbsp <b>" + (i+1) + "</b>");
         }
         else {
             out.print("&nbsp <a href=\"" + url +
                 i + "\">" + (i+1) + "</a>");      
         }
     }
     if (upper < numPages) {
         if (numPages > WebappConstants.PAGINATION_NUM_PAGES_DISPLAY)
            out.print ("&nbsp ..........");
         out.print("&nbsp <a href=\"" + url +
                 (index+1) + "\"> Next </a> >>");
     }
  }
  out.flush();  
%>
