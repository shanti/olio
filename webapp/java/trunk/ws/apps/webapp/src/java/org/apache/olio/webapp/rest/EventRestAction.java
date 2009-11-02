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
package org.apache.olio.webapp.rest;

import org.apache.olio.webapp.controller.Action;
import org.apache.olio.webapp.controller.WebConstants;
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.model.ModelFacade;
import org.apache.olio.webapp.util.WebappUtil;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.olio.webapp.controller.WebConstants.*;
import org.apache.olio.webapp.fileupload.FileUploadHandler;
import org.apache.olio.webapp.fileupload.FileUploadUtil;
import org.apache.olio.webapp.model.CommentsRating;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.security.SecurityHandler;
import org.apache.olio.webapp.util.ServiceLocator;
import org.apache.olio.webapp.util.UserBean;
import org.apache.olio.webapp.util.fs.FileSystem;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * handles all request related to users
 * @author Mark Basler
 * @author Inderjeet Singh
 * @author Binu John
 */
public class EventRestAction implements Action {

    private static final String comma = ", ";
    private static final String dq_cm = "\", ";
    private ServletContext context;
    private static final int UPDATE_MODE_ADD_ATTENDEE = 1;
    private static final int UPDATE_MODE_DELETE_ATTENDEE = 2;
    private Logger logger = Logger.getLogger(EventRestAction.class.getName());

    public EventRestAction(ServletContext context) {
        this.context = context;
    }

    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        logger.finer("\n*** pathinfo = " + path);
        ModelFacade modelFacade = (ModelFacade) context.getAttribute(MF_KEY);
        if (path.equals("/event")) {
            // This is not really rest
        } else if (path.equals("/event/updateRating")) {
            updateSocialEventRating(request, response, modelFacade);
        } else if (path.equals("/event/getComments")) {
            getSocialEventComments(request, response, modelFacade);
        } else if (path.equals("/event/updateCommentsRating")) {
            updateSocialEventComment(request, response, modelFacade);
        } else if (path.equals("/event/deleteCommentsRating")) {
            deleteSocialEventComment(request, response, modelFacade);
        } else if (path.equals("/event/addEvent") || path.equals("updateEvent")) {
            // file upload
            logger.finer("AddEvent ... ");
            FileUploadHandler fuh = new FileUploadHandler();
            Hashtable<String, String> htUpload = fuh.getInitialParams(request, response);

            // file is upload check for error and then write to database
            if (htUpload != null) {
                StringBuilder sb = new StringBuilder();
                for (String key : htUpload.keySet()) {
                    sb.append(key);
                    sb.append(comma);
                }
                logger.finer("\n***elements  = " + sb.toString());
                SocialEvent event = null;
                /* Don't check for submit since we're doing upload in 2 phases
                 * and we may not have read it in. We don't do updates.
                String type = htUpload.get("submit");
                if (type == null) {
                    return "/site.jsp?page=error.jsp";
                }
                if (type.equals("Update")) {
                    event = getEvent(modelFacade, htUpload);
                } else {
                */
                event = createEvent(request, modelFacade, htUpload);
                    if (request.getSession(true).getAttribute("userBean") != null) {
                        UserBean uBean = (UserBean) request.getSession(true).getAttribute("userBean");
                        uBean.setDisplayMessage("Event added successfully");
                        logger.log(Level.FINER, "A new Event has been added and persisted");
                    }
                //}

                String id = String.valueOf(event.getSocialEventID());
                htUpload = fuh.handleFileUpload(id, request, response);

                // Update the event with the right stuff.
                updateEvent(event, request, modelFacade, htUpload);

                // clear the cache
                WebappUtil.clearCache("/event/list");
                response.sendRedirect(request.getContextPath() + "/event/detail?socialEventID=" + event.getSocialEventID());
                return null;
            }
        } else if (path.equals("/event/fileuploadEventFinal")) {
            // file upload status request
            FileUploadHandler.handleFileUploadFinal(request, response);
        } else if (path.equals("/event/getAttendees")) {
            getAttendees(request, response, modelFacade);
        } else if (path.equals("/event/addAttendee")) {
            updateAttendee(request, response, modelFacade, UPDATE_MODE_ADD_ATTENDEE);
        } else if (path.equals("/event/deleteAttendee")) {
            updateAttendee(request, response, modelFacade, UPDATE_MODE_DELETE_ATTENDEE);
        } else {
            // file upload status request
            FileUploadHandler.handleFileStatus(request, response);
        }

        return null;
    }

    public void updateAttendee(HttpServletRequest request, HttpServletResponse response,
            ModelFacade modelFacade, int mode) {
        Person person = SecurityHandler.getInstance().getLoggedInPerson(request);
        if (person == null) {
            // This is an error condition -- ignore
            return;
        }
        String sxEventId = request.getParameter("socialEventID");
        if (sxEventId == null) {
            throw new RuntimeException("Couldnot find event with socialEventID = " +
                    sxEventId);
        }
        try {
            int eventId = Integer.parseInt(sxEventId);
            SocialEvent event = modelFacade.getSocialEvent(eventId);
            if (event == null) {
                throw new RuntimeException("Couldnot find event with socialEventID = " +
                        eventId);
            }

            boolean attending = event.isAttending(person);

            boolean already = false;
            String status = "success";
            if (mode == UPDATE_MODE_ADD_ATTENDEE && attending) {
                already = true;
                status = "attending";
            }
            if (!attending && mode == UPDATE_MODE_DELETE_ATTENDEE) {
                already = true;
                status = "not_attending";
            }

            if (!already) {
                if (mode == UPDATE_MODE_ADD_ATTENDEE) {
                    event.getAttendees().add(person);
                    person.getSocialEvents().add(event);
                    status = "added";
                } else {
                    event.getAttendees().remove(person);
                    person.getSocialEvents().remove(event);
                    status = "deleted";
                }

                modelFacade.updateSocialEvent(event);
            }
            String s = getAttendeesAsJson(event.getAttendees(), status);

            logger.finer("\n*** people = " + s);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write(s);
            response.flushBuffer();

            // Before we return, set the userBean's upcomingEvents as dirty since 
            // the event add/deleted may be in the list
            UserBean uBean = (UserBean) request.getSession(true).getAttribute("userBean");
            if (uBean != null) {
                uBean.setUpcomingEventsDirty(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void getAttendees(HttpServletRequest request, HttpServletResponse response,
            ModelFacade modelFacade) throws IOException {
        Person person = SecurityHandler.getInstance().getLoggedInPerson(request);
        String status = null;
        String uname = null;
        if (person == null) {
            // Check wehther the userName parameter is passed in
            uname = request.getParameter("userName");
            if (uname == null || uname.length() == 0);
            status = "not_logged_in";
        } else {
            uname = person.getUserName();
        }

        String sxEventId = request.getParameter("socialEventID");
        if (sxEventId == null) {
            throw new RuntimeException("Couldnot find event with socialEventID = " +
                    sxEventId);
        }
        SocialEvent event = null;
        try {
            int eventId = Integer.parseInt(sxEventId);
            event = modelFacade.getSocialEvent(eventId);
            if (event == null) {
                throw new RuntimeException("Couldnot find event with socialEventID = " +
                        eventId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Couldnot find event with socialEventID = " +
                    sxEventId);
        }
        if (uname != null && uname.length() > 0) {
            if (event.isAttending(uname)) {
                status = "attending";
            } else {
                status = "not_attending";
            }
        }

        String s = getAttendeesAsJson(event.getAttendees(), status);
        // Faban does not understand the content type application/json
        //response.setContentType("application/json;charset=UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(s);
        response.flushBuffer();
    }

    private static String getAttendeesAsJson(Collection<Person> attendees, String status) {
        StringBuilder sb = new StringBuilder("{ \"result\": {\"status\":");
        sb.append("\"" + status + "\"");
        sb.append(", \"attendees\":[");
        if (attendees != null) {
            for (Person psx : attendees) {
                sb.append("{\"userName\":\"");
                sb.append(WebappUtil.encodeJSONString(psx.getUserName()));
                sb.append("\"}, ");
            }
            if (attendees.size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        sb.append("] } }");
        return sb.toString();
    }

    public SocialEvent createEvent(HttpServletRequest request, ModelFacade modelFacade, Hashtable<String, String> htUpload) {
        /* We don't worry about the address at creation time. Only do at update.
        String street1 = htUpload.get(STREET1_PARAM);
        String street2 = htUpload.get(STREET2_PARAM);
        String city = htUpload.get(CITY_PARAM);
        String state = htUpload.get(STATE_PARAM);
        String country = htUpload.get(COUNTRY_PARAM);
        String zip = htUpload.get(ZIP_PARAM);
        Address address = WebappUtil.handleAddress(context, street1, street2, city, state, zip, country);
        */

        String title = htUpload.get(TITLE_PARAM);
        String description = htUpload.get(DESCRIPTION_PARAM);
        String summary = htUpload.get(SUMMARY_PARAM);
        String tags = htUpload.get(TAGS_PARAM);
        String telephone = htUpload.get(TELEPHONE_PARAM);
        String monthx = htUpload.get("month");
        String dayx = htUpload.get("day");
        String yearx = htUpload.get("year");
        String hourx = htUpload.get("hour");
        String minutex = htUpload.get("minute");
        String timezonex = htUpload.get(TIMEZONE_PARAM);

        // SECURITY get submitter from session
        String submitterUserName = null;
        Person person = SecurityHandler.getInstance().getLoggedInPerson(request);
        if (person != null) {
            submitterUserName = person.getUserName();
        } else {
            // error, submitter must be there
            // you shouldn't be able to get here unless there is a hole in the security mechanism

            // ADDENDUM - need to ommenting out the security check for now
            // because of the Faban issues with two HTTP Sessions
            //(one from Java HTTP Client, the other from Apache HTTP client
            // workaround - if user is not in session, then get Person from username in JSP
            //modification for getting using username in JSP
            //submitterUserName = htUpload.get("submitter_user_name");
            submitterUserName = htUpload.get("submitter_user_name");
            request.setAttribute("submitter_user_name", submitterUserName);
            logger.finer("EventRestAction:  other session--> username is " + submitterUserName);
            person = modelFacade.getPerson(submitterUserName);
        //throw new RuntimeException(WebappUtil.getMessage("person_not_logged_in"));
        }

        //end modification

        String literaturex = htUpload.get(UPLOAD_LITERATURE_PARAM);
        if (literaturex == null) {
            literaturex = "";
        }
        String imagex = htUpload.get(UPLOAD_EVENT_IMAGE_PARAM);
        String thumbImage;
        thumbImage = htUpload.get(UPLOAD_EVENT_IMAGE_THUMBNAIL_PARAM);
        if (thumbImage == null) {
            thumbImage = "";
        }
        // get upload location from map
        String imageLocation = htUpload.get(UPLOAD_EVENT_IMAGE_PARAM + FileUploadUtil.FILE_LOCATION_KEY);
        logger.finer("\n image path = " + imageLocation);
        // This is done during upload for efficiency.
        // thumbImage=WebappUtil.constructThumbnail(imageLocation);
        logger.finer("\n thumb path = " + thumbImage);


        // gat time in utc milliseconds...
        //adding timezone specific info for social event
        Calendar localCal = GregorianCalendar.getInstance(TimeZone.getTimeZone(timezonex));
        localCal.set(Calendar.MONTH, Integer.parseInt(monthx) - 1);
        localCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayx));
        localCal.set(Calendar.YEAR, Integer.parseInt(yearx));
        localCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourx));
        localCal.set(Calendar.MINUTE, Integer.parseInt(minutex));
        localCal.set(Calendar.SECOND, 0);
        logger.finer("\n***local = " + localCal + "\n Millis = " + localCal.getTimeInMillis());
        Timestamp eventTimestamp = new Timestamp(localCal.getTimeInMillis());

        SocialEvent socialEvent = new SocialEvent(title, summary, description, submitterUserName, null, telephone, 0, 0,
                imagex, thumbImage, literaturex, eventTimestamp);
        logger.finer("Event title = " + socialEvent.getTitle());


        // Submitter is not necessarily an attendde

        socialEvent = modelFacade.addSocialEvent(socialEvent, tags);
        logger.log(Level.FINER, "SocialEvent " + socialEvent.getSocialEventID() + " has been persisted");

        return socialEvent;
    }

    public SocialEvent getEvent(ModelFacade modelFacade, Hashtable<String, String> htUpload) {
        String sids = htUpload.get("socialEventID");
        if (sids == null) {
            throw new RuntimeException("socialEventID is not set for updateEVent");
        }
        try {
            int id = Integer.parseInt(sids);
            return modelFacade.getSocialEvent(id);
        } catch (Exception e) {
            throw new RuntimeException("updateSocialEvent: SocialEvent could not be retrieved - id = " +
                    sids);
        }
    }

    public SocialEvent updateEvent(SocialEvent event, HttpServletRequest request, ModelFacade modelFacade, Hashtable<String, String> htUpload) throws IOException {

        // Update requires an id
        String street1 = htUpload.get(STREET1_PARAM);
        String street2 = htUpload.get(STREET2_PARAM);
        String city = htUpload.get(CITY_PARAM);
        String state = htUpload.get(STATE_PARAM);
        String country = htUpload.get(COUNTRY_PARAM);
        String zip = htUpload.get(ZIP_PARAM);
        Address address = WebappUtil.handleAddress(context, street1, street2, city, state, zip, country);
        event.setAddress(address);

        event.setTitle(htUpload.get(TITLE_PARAM));
        event.setDescription(htUpload.get(DESCRIPTION_PARAM));
        event.setSummary(htUpload.get(SUMMARY_PARAM));
        event.setTelephone(htUpload.get(TELEPHONE_PARAM));

        String tags = htUpload.get(TAGS_PARAM);

        Calendar localCal = GregorianCalendar.getInstance();
        String monthx = htUpload.get("month");
        String dayx = htUpload.get("day");
        String yearx = htUpload.get("year");
        String hourx = htUpload.get("hour");
        String minutex = htUpload.get("minute");
        localCal.set(Calendar.MONTH, Integer.parseInt(monthx) - 1);
        localCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayx));
        localCal.set(Calendar.YEAR, Integer.parseInt(yearx));
        localCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourx));
        localCal.set(Calendar.MINUTE, Integer.parseInt(minutex));
        localCal.set(Calendar.SECOND, 0);
        logger.finer("\n***local = " + localCal + "\n Millis = " + localCal.getTimeInMillis());
        Timestamp eventTimestamp = new Timestamp(localCal.getTimeInMillis());
        event.setEventTimestamp(eventTimestamp);

        // SECURITY get submitter from session
        String submitterUserName = null;
        Person person = SecurityHandler.getInstance().getLoggedInPerson(request);
        if (person != null) {
            submitterUserName = person.getUserName();
        } else {
            throw new RuntimeException("Unknown user");
        }

        String literaturex = htUpload.get(UPLOAD_LITERATURE_PARAM);
        String imagex = htUpload.get(UPLOAD_EVENT_IMAGE_PARAM);
        String thumbImage;
        thumbImage = htUpload.get(UPLOAD_EVENT_IMAGE_THUMBNAIL_PARAM);
        // get upload location from map
        //String imageLocation = htUpload.get(UPLOAD_EVENT_IMAGE_PARAM + FileUploadUtil.FILE_LOCATION_KEY);
        logger.finer("\n image path = " + imagex);
        // This is done during upload for efficiency.
        // thumbImage=WebappUtil.constructThumbnail(imageLocation);
        logger.finer("\n thumb path = " + thumbImage);

        // Set the image and thumbnail location if the image location is not empty.
        // If empty, leave the old one alone
        // Delete the old images since it is being replaced
        // Do the same for literature
        // The following doesn't work for AddEvent. Since we don't support an
        // updateEvent currently, commenting for now
        /****
        ServiceLocator locator = ServiceLocator.getInstance();
        FileSystem fs = (FileSystem) locator.getFileSystem();
        if (imagex != null) {
            if (event.getImageURL() != null) {
                fs.delete(event.getImageURL());
            }
            event.setImageURL(imagex);
            if (event.getImageThumbURL() != null) {
                fs.delete(event.getImageThumbURL());
            }
            event.setImageThumbURL(thumbImage);
        }
        if (literaturex != null) {
            if (event.getLiteratureURL() != null) {
                fs.delete(event.getLiteratureURL());
            }
            event.setLiteratureURL(literaturex);
        }
        ****/
        event.setImageURL(imagex);
        event.setImageThumbURL(thumbImage);
        event.setLiteratureURL(literaturex);
        
        event = modelFacade.updateSocialEvent(event, tags);
        logger.log(Level.FINER, "SocialEvent " + event.getSocialEventID() + " has been updated");

        return event;
    }

    private void updateSocialEventRating(HttpServletRequest request, HttpServletResponse response, ModelFacade modelFacade) throws IOException {
        String eventID = request.getParameter("socialEventID");
        int eid;
        try {
            eid = Integer.parseInt(eventID);
        } catch (Exception e) {
            throw new RuntimeException("Incorrect eventID = " + eventID);
        }

        Person person = SecurityHandler.getInstance().getLoggedInPerson(request);

        int rating = 0;

        try {
            rating = Integer.parseInt(request.getParameter("rating"));
        } catch (Exception e) {
        }

        SocialEvent event = modelFacade.updateSocialEventRating(person, eid, rating);

        /*
        response.setContentType("text/json");
        PrintWriter out = response.getWriter();
        out.print("{\"commentsratings\":[");
        
        boolean first=true;
        for (CommentsRating cr: event.getComments()) {
        if (!first)
        out.print(",");
        else
        first = false;
        out.print(cr.toJSON());
        }
         * */
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print("Your rating is " + rating);
        out.flush();
    }

    public void getSocialEventComments(HttpServletRequest request, HttpServletResponse response, ModelFacade modelFacade) throws IOException {
        String eventID = request.getParameter("socialEventID");
        SocialEvent event = null;
        try {
            int eid = Integer.parseInt(eventID);
            event = modelFacade.getSocialEvent(eid);
        } catch (Exception e) {
            throw new RuntimeException("coulnot locate event -- eventID = " + eventID);
        }
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        out.print(getCommentsRatingAsJson(event));
        out.flush();
    }

    private void updateSocialEventComment(HttpServletRequest request, HttpServletResponse response, ModelFacade modelFacade) throws IOException {
        String eventID = request.getParameter("socialEventID");
        int eid;
        try {
            eid = Integer.parseInt(eventID);
        } catch (Exception e) {
            throw new RuntimeException("Incorrect eventID");
        }

        Person person = SecurityHandler.getInstance().getLoggedInPerson(request);

        String comment = request.getParameter("comment");

        SocialEvent event = modelFacade.updateSocialEventComment(person, eid, comment);

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        out.print(getCommentsRatingAsJson(event));
        out.flush();
    }

    private void deleteSocialEventComment(HttpServletRequest request, HttpServletResponse response, ModelFacade modelFacade) throws IOException {
        int eid, commentId;
        try {
            eid = Integer.parseInt(request.getParameter("socialEventID"));
            commentId = Integer.parseInt(request.getParameter("commentId"));
        } catch (Exception e) {
            throw new RuntimeException("Incorrect eventID or commentId. eventId = " +
                    request.getParameter("socialEventID") + " commentId = " +
                    request.getParameter("commentId"));
        }

        Person person = SecurityHandler.getInstance().getLoggedInPerson(request);
        SocialEvent event = modelFacade.getSocialEvent(eid);

        if (event == null) {
            throw new RuntimeException("Incorrect eventID or commentId. eventId = " +
                    request.getParameter("socialEventID") + " commentId = " +
                    request.getParameter("commentId"));
        }

        modelFacade.deleteCommentFromSocialEvent(event, commentId);

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        out.print(getCommentsRatingAsJson(event));
        out.flush();
    }

    private String getCommentsRatingAsJson(SocialEvent event) {
        StringBuilder strb = new StringBuilder();
        strb.append("{\"commentsratings\":[");
        boolean first = true;
        for (CommentsRating cr : event.getComments()) {
            if (!first) {
                strb.append(",");
            } else {
                first = false;
            }
            strb.append(cr.toJSON());
        }
        strb.append("]}");
        return strb.toString();
    }
}
