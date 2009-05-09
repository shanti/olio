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

import org.apache.olio.webapp.cache.Cache;
import org.apache.olio.webapp.model.CommentsRating;
import org.apache.olio.webapp.model.ModelFacade;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.olio.webapp.controller.WebConstants.*;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.security.SecurityHandler;
import org.apache.olio.webapp.util.WebappConstants;
import org.apache.olio.webapp.util.WebappUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles action for the event - update of comments and ratings.
 * 
 * @author Binu John
 */
public class EventAction implements Action {
    
    private ServletContext context;
    
    /** Creates a new instance of EventAction */
    public EventAction(ServletContext con) {
        this.context = con;
    }

    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ModelFacade mf= (ModelFacade) context.getAttribute(MF_KEY);
        String path = request.getPathInfo();
    if (path.equals("/list")) {
            return listEvents (request, response, mf);
        }
        if (path.equals("/addEvent") || path.equals("/updateEvent")) {
            return addEvent (request, response);
        }
        if (path.equals("/upcoming")) {
            return listUpcomingEvents (request, response, mf);
        }
        
        String eventID = request.getParameter("socialEventID");
        int eid = this.getSocialEventID(eventID);
        
        if (path.equals("/detail")) {
            SocialEvent se = mf.getSocialEvent(eid);
            if (se == null)
               throw new RuntimeException("Could not find event. eventID = " + eid);
            
            request.setAttribute("socialEvent", se);
            boolean attending = false;
            Person user=SecurityHandler.getInstance().getLoggedInPerson(request);
            
            if (user != null)
                attending = se.isAttending(user);
            
            request.setAttribute("isAttending", attending);
            
            // If the user is looged in get the comment if there is any
            if (user != null) {
                CommentsRating cr = mf.getCommentRating(user, se);
                if (cr != null && cr.getCommentString() != null) {
                    request.setAttribute("comment", cr.getCommentString());
                }
            }
            return "/site.jsp?page=event.jsp";
        }
        if (path.equals("/delete"))
            return deleteEvent(eid, request, response);
        
        Person person=SecurityHandler.getInstance().getLoggedInPerson(request);
        String comments = request.getParameter("comments");
        
        SocialEvent event = mf.updateSocialEventComment(person, eid, comments, 0);
        
        return "/site.jsp?page=event.jsp&socialEventID=" + eventID;
    }
    
    private String deleteEvent (int eid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelFacade mf= (ModelFacade) context.getAttribute(MF_KEY);
        mf.deleteEvent(eid);
        // Since this affects the cache, clear the cache
        WebappUtil.clearCache("/event/list");
        response.sendRedirect(request.getContextPath() + "/event/list");
        return null;
    }
    
    private String addEvent (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String eventID = request.getParameter("socialEventID");
        Person person=SecurityHandler.getInstance().getLoggedInPerson(request);
        if (person == null) {
            request.setAttribute("errorMessage", "You need to log in to edit an event.");
            return "/site.jsp?page=error.jsp";
        }
        if (eventID == null)
            return "/site.jsp?page=addEvent.jsp";
        
        int eid = getSocialEventID (eventID);
        ModelFacade mf= (ModelFacade) context.getAttribute(MF_KEY);
        SocialEvent event = mf.getSocialEvent(eid);
        if (event == null)
            return "/site.jsp?page=addEvent.jsp";
        
        // Only the original submitter can edit an event
        
        if (!person.getUserName().equals(event.getSubmitterUserName())) {
            request.setAttribute("errorMessage", "Only the user that submitted the event can edit an event.");
            return "/site.jsp?page=error.jsp";
        }
        // Set the necessary fields
        //request.setAttribute("title", event.getTitle());
        request.setAttribute("title", event.getTitle());
        request.setAttribute("summary", event.getSummary());
        request.setAttribute("description", event.getDescription());
        request.setAttribute("telephone", event.getTelephone());
        request.setAttribute("tags", event.getTagsAsString());
        if (event.getAddress() != null) {
            request.setAttribute("street1", event.getAddress().getStreet1());
            request.setAttribute("street2", event.getAddress().getStreet2());
            request.setAttribute("zip", event.getAddress().getZip());
            request.setAttribute("city", event.getAddress().getCity());
            request.setAttribute("state", event.getAddress().getState());
            request.setAttribute("country", event.getAddress().getCountry());
        }
        request.setAttribute("socialEvent", event);
        return "/site.jsp?page=addEvent.jsp";
    }
    
    private int getSocialEventID (String eventID) {
        try {
            return Integer.parseInt(eventID); 
        } catch (Exception e) {
            throw new RuntimeException("Incorrect eventID = " + eventID);
        }
    }

    private String listEvents(HttpServletRequest request, HttpServletResponse response, ModelFacade mf) throws IOException, ServletException {
        int index = WebappUtil.getIntProperty(request.getParameter("index"));
        String zip = request.getParameter("zipcode");
        String order = request.getParameter("order");
        String pageType = request.getParameter("displayType");
        int day = WebappUtil.getIntProperty(request.getParameter("day"));
        // Check if month is in integer form
        int month = WebappUtil.getIntProperty(request.getParameter("month"));
        if (month == 0)
            month = WebappUtil.getMonthNumber(request.getParameter("month"));
        int year = WebappUtil.getIntProperty(request.getParameter("year"));
        int orderBy = WebappUtil.getIntProperty(request.getParameter("orderBy"));

        request.setAttribute("pageUrl", request.getContextPath() + "/event/list?" +
                "zip=" + zip + "&order=" + order +
                "&day=" + day + "&month=" + month + "&year=" + year +
                "&orderBy=" + orderBy);
        request.setAttribute("zip", zip);
        request.setAttribute("order", order);
        request.setAttribute("index", index);
        request.setAttribute("day", day);
        request.setAttribute("month", month);
        request.setAttribute("year", year);
        request.setAttribute("orderBy", orderBy);

        // There are a few alternatives in how to maintain these lists.
        //1. Rerun the query everytime and return the appropriate page list.
        //2. Cache the individual rendered pages.
        // Currently, we use option #2. 
        // Caching can be disabled by setting the following System property
        // cacheOlio=false

        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("zip", zip);
        queryMap.put("orderType", order);
        int eventsPerPage = WebappConstants.ITEMS_PER_PAGE;
        queryMap.put("startIndex", index*eventsPerPage);
        queryMap.put("eventsPerPage", eventsPerPage);
        
        queryMap.put("day", day);
        queryMap.put("month", month);
        queryMap.put("year", year);
        queryMap.put("orderBy", orderBy);

        Cache cache = WebappUtil.getCache();
        if (cache != null) {
            String cacheKey = WebappUtil.getCacheKey("/event/list", queryMap);
            if (cacheKey != null) {
                if (cache.isLocal()) {
                    Object cobj = null;
                    CachedList cl = (CachedList) cache.get(cacheKey);
                    if (cl == null) {
                        cl = new CachedList(cacheKey);
                        cache.put(cacheKey, cl, WebappUtil.getCacheTimeToLiveInSecs());
                    }
                    cobj = cl.get(index);
                    if (cobj== null) {
                        // Fill the cache
                        List<SocialEvent> list = mf.getSocialEvents(queryMap);
                        int numPages = 1;
                        if (list != null) {
                            Object o = queryMap.get("listSize");
                            if (o != null) {
                                Long l = (Long) o;
                                numPages = WebappUtil.getNumPages(l);
                            }
                        }
                            
                        request.setAttribute("itemList", list);
                        request.setAttribute("numPages", numPages);
                        cobj = WebappUtil.acquirePageContent("/eventList.jsp", request, response);
                        cl.put(index, cobj);
                        //System.out.println ("Put content in cache - key = " + cacheKey +
                          //      " index = " + index);
                    }
                    else {
                        //System.out.println ("Got content from cache - key = " + cacheKey +
                          //      " index = " + index);
                    }
                    if (cobj != null) {
                        request.setAttribute("content", cobj);
                        if (pageType != null && pageType.equalsIgnoreCase("partial")) {
                            response.getWriter().print((String) cobj);
                            return null;
                        }
                        else {
                            return "/site.jsp?cachedContent=true";
                        }
                    }
                }
                else {
                    // TO DO -- Needs different logic for distributed caches since we
                    // don't intent to cache heirarchical objects in that mode.
                }
            }
        }

        // This is not cacheable or cache is not enabled.
        List<SocialEvent> list = mf.getSocialEvents(queryMap);

        int numPages = 1;
        if (list != null) {
            Object o = queryMap.get("listSize");
            if (o != null) {
                Long l = (Long) o;
                numPages = WebappUtil.getNumPages(l);
            }
        }
        request.setAttribute("itemList", list);
        request.setAttribute("numPages", numPages);
        if (pageType != null && pageType.equalsIgnoreCase("partial"))
            return "/eventList.jsp?index=" + index;
        else
            return "/site.jsp?page=eventList.jsp&index=" + index;
    }

    private String listUpcomingEvents(HttpServletRequest request, HttpServletResponse response, ModelFacade mf) {
        String userName = request.getParameter("userName");
        Person user = null;
        if (userName != null) {
            user = mf.findPerson(userName);
        }
        else {
            // Get the loggedin user
            user = SecurityHandler.getInstance().getLoggedInPerson(request);
        }
        if (user == null) {
            request.setAttribute("errorMessage", "Could not find user for user = " + userName);
            return "/site.jsp?page=error.jsp";
        }
        Map<String, Object> qMap = new HashMap<String, Object>();
        int index = WebappUtil.getIntProperty(request.getParameter("index"));
        
        int eventsPerPage = WebappConstants.ITEMS_PER_PAGE;
        qMap.put("startIndex", index*eventsPerPage);
        qMap.put("eventsPerPage", eventsPerPage);
        List<SocialEvent> list = mf.getUpcomingEvents(user, qMap);
        Long l = (Long) qMap.get("listSize");
        int numPages = WebappUtil.getNumPages(l);
        request.setAttribute("itemList", list);
        request.setAttribute("numPages", numPages);
        request.setAttribute("index", index);
        request.setAttribute("title", "Upcoming Events for User: " + userName);
        request.setAttribute("pageUrl", request.getContextPath() + "/event/upcoming?userName=" + userName);
        return "/site.jsp?page=eventList.jsp&index="+index;
    }

    static class CachedList {
        public String cacheKey;
        public HashMap<Integer, Object> valueMap;
        
        public CachedList (String key) {
            this.cacheKey = key;
            valueMap = new HashMap<Integer, Object>();
        }
        
        public void put(int index, Object value) {
            valueMap.put(index, value);
        }
        
        public Object get(int index) {
            return valueMap.get(index);
        }
    }

}
