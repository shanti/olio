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

package org.apache.olio.webapp.controller;

import org.apache.olio.webapp.model.ModelFacade;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles all the REST events related to calendars
 * @author Inderjeet Singh
 */
public class RestAction implements Action {
    
    private ModelFacade mf;
    
    public RestAction() {
    }
    
    public String process(HttpServletRequest request, HttpServletResponse response) {
/*        String id = null;
        if (WebConstants.ADD_ACTION_PARAMVALUE.equals(request.getParameter(WebConstants.ACTION_PARAM))) {
            id = addEvent(request);
        } else if (WebConstants.DELETE_ACTION_PARAMVALUE.equals(request.getParameter(WebConstants.ACTION_PARAM))) {
            deleteEvent(request);
        } else {
            id = (String) request.getParameter(WebConstants.ID_PARAM);
            if (WebConstants.EDIT_ACTION_PARAMVALUE.equals(request.getParameter(WebConstants.ACTION_PARAM))) {
                editEvent(request);
            }
        }
        if (id != null) {
            request.setAttribute(WebConstants.EVENT_KEY, mf.getEvent(id));
        }
 */
        String servletPath = request.getServletPath();
        String httpMethod = request.getMethod();
        String pathInfo = request.getPathInfo();
        //int paramIndex = pathInfo.indexOf('?');
        //pathInfo = paramIndex == -1 ? pathInfo : pathInfo.substring(0, paramIndex);
        String targetJsp = "/rest" + pathInfo + ".jsp";
        return targetJsp;
    }
    
    private void deleteEvent(HttpServletRequest request) {
//        mf.delete(request.getParameter(WebConstants.ID_PARAM));
    }
/*
    private SocialEvent createSocialEvent(HttpServletRequest request) {
        String title = request.getParameter(WebConstants.TITLE_PARAM);
        String description = request.getParameter(WebConstants.DESCRIPTION_PARAM);
        String submitterUserName = request.getParameter(WebConstants.SUBMITTER_USER_NAME_PARAM);
        String street1 = request.getParameter(WebConstants.STREET1_PARAM);
        String city = request.getParameter(WebConstants.CITY_PARAM);
        String state = request.getParameter(WebConstants.STATE_PARAM);
        String zip = request.getParameter(WebConstants.ZIP_PARAM);
        Address address = handleAddress(street1, city, state, zip, proxyHost, proxyPort);                  
        SocialEvent socialEvent = new SocialEvent(title, description, submitterUserName, address, 0, 0);
        System.out.println("Event title = " + socialEvent.getTitle());
        int socialEventID = modelFacade.addSocialEvent(socialEvent);
        WebappUtil.getLogger().log(Level.FINE, "SocialEvent " + socialEventID + " has been persisted");       
    }
*/
/*    private String addEvent(HttpServletRequest request) {
        SocialEvent event = createSocialEvent(request);
        return event.getId();
    }
*/    
    private void editEvent(HttpServletRequest request) {
/*        SocialEvent event = mf.getEvent(request.getParameter(WebConstants.ID_PARAM));
        String date = request.getParameter(WebConstants.DATE_PARAM);
        String description = request.getParameter(WebConstants.DESCRIPTION_PARAM);
        event.setDateString(date);
        event.setDescription(description);
 */
    }
}
