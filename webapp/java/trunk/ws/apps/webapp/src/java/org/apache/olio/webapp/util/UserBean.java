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


package org.apache.olio.webapp.util;

import org.apache.olio.webapp.model.ModelFacade;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.model.SocialEvent;
import java.util.List;

/**
 *
 * @author Binu John
 */
public class UserBean {
    private ModelFacade mf;
    private Person loggedInPerson;
    private String displayMessage;
    private String forwardingUrl;
    
    public UserBean() {}

    public void setModelFacade (ModelFacade mf) {
       this.mf = mf;
    }
    
    public ModelFacade getModelFacade() {
        return mf;
    }
    
    public Person getLoggedInPerson() {
        return loggedInPerson;
    }

    public void setLoggedInPerson(Person loggedInPerson) {
        this.loggedInPerson = loggedInPerson;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }
    
    public void resetDisplayMessage() {
        displayMessage = "";
    }

    public String getForwardingUrl() {
        return forwardingUrl;
    }

    public void setForwardingUrl(String forwardingUrl) {
        this.forwardingUrl = forwardingUrl;
    }
    
    public Boolean getJMakiUsed() {
        //return false;
        return ModelFacade.getJMakiUsage();
    }
    
    private boolean upcomingEventsListDirty = true;
    
    private List<SocialEvent> upcomingEventsList;
    
    boolean isUpcomingEventsDirty() {
        return upcomingEventsListDirty;
    }

    public void setUpcomingEventsDirty(boolean upcomingEventsListdirty) {
        this.upcomingEventsListDirty = upcomingEventsListdirty;
    }
    
    public List<SocialEvent> getUpcomingEvents () {
        if (loggedInPerson != null) {
            if (!upcomingEventsListDirty)
                return upcomingEventsList;
            upcomingEventsList = mf.getUpcomingEvents (loggedInPerson, WebappConstants.UPCOMING_EVENTS_DISPLAY_MAX);
            upcomingEventsListDirty = false;
            return upcomingEventsList;
        }
        
        return null;
    }
    
    public String getUpcomingEventsAsJson() {
        StringBuilder strb = new StringBuilder ("{\"upcomingEvents\" : [");
        List<SocialEvent> list = getUpcomingEvents();
        if (list != null && list.size() > 0) {
            for (SocialEvent e: list) {
                strb.append ("{\"title\":\"");
                strb.append(e.getTitle());
                strb.append("\", \"");
                strb.append("id\":\"");
                strb.append(e.getSocialEventID());
                strb.append("\"}, ");
            }
            strb.deleteCharAt(strb.length()-1); // remove comma
            strb.deleteCharAt(strb.length()-1); // remove space
        }
        strb.append("]}");
        return strb.toString();
    }
    
}
