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
import org.apache.olio.webapp.model.ModelFacade;
import org.apache.olio.webapp.model.Person;
//import com.sun.javaee.blueprints.webapp.model.UserSignOn;
import org.apache.olio.webapp.util.WebappUtil;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.olio.webapp.controller.WebConstants.*;
import org.apache.olio.webapp.fileupload.FileUploadHandler;
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.security.SecurityHandler;
import java.io.PrintWriter;
import java.util.Hashtable;
import org.apache.olio.webapp.model.Invitation;
import org.apache.olio.webapp.util.UserBean;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * handles all request related to users
 * @author Mark Basler
 * @author Inderjeet Singh
 * @author Binu John
 * @author Kim Lichong
 */
public class PersonRestAction implements Action {

    private ServletContext context;
    private Logger logger = Logger.getLogger(PersonRestAction.class.getName());

    public PersonRestAction(ServletContext context) {
        this.context = context;
    }

    /**
     * You can test this action by putting a url in your browser such as
     * http://localhost:8080/webapp/api/person?user_name=bob123
     */
    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String actionType = request.getParameter(ACTION_TYPE_PARAM);
        String method = request.getMethod();
        logger.log(Level.FINER, "PERSON-REST-ACTION:process:: " + ACTION_TYPE_PARAM + "=" + actionType);

        if (actionType == null) { // treat it as a read
            // need to check for fileupload.  Will not have an action because it is in multi-part mime format
            String path = request.getPathInfo();
            logger.finer("\n*** pathinfo = " + path);

            ModelFacade modelFacade = (ModelFacade) context.getAttribute(MF_KEY);
            if (path.equals("/person/fileuploadPerson")) {
                // file upload
                FileUploadHandler fuh = new FileUploadHandler();
                Hashtable<String, String> htUpload = fuh.handleFileUpload(request, response);
                // file is upload check for error and then write to database
                if (htUpload != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String key : htUpload.keySet()) {
                        sb.append(key);
                        sb.append(",");
                    }
                    //logger.finer("\n***elements  = " + sb.toString());

                    //createUser(request, htUpload);
                    // just for now comment out the above line
                    // need to check if fileupload is complete
                    Person newEditPerson = null;
                    boolean isEditable = Boolean.parseBoolean(request.getParameter("isEditable"));

                    if (isEditable) {
                        newEditPerson = updateUser(request, htUpload, fuh);
                    } else {
                        newEditPerson = createUser(request, htUpload, fuh);
                    }

                    logger.log(Level.FINER, "A new Person has been added and persisted");
                    request.setAttribute("displayPerson", newEditPerson);

                }
                return "/site.jsp?page=personContent.jsp";


            } else if (path.equals("/person/fileuploadPersonFinal")) {
                // file upload status request
                FileUploadHandler.handleFileUploadFinal(request, response);
                return null;
            } else if (path.equals("/person/fileuploadStatusPerson")) {
                // file upload status request
                FileUploadHandler.handleFileStatus(request, response);
                return null;
            } else {
                if (method.equals("GET")) // default to a read action
                {
                    actionType = AT_READ_PARAMVALUE;
                }
            }
        } else {
            ModelFacade modelFacade = (ModelFacade) context.getAttribute(MF_KEY);
            UserBean userBean = (UserBean) request.getSession().getAttribute("userBean");
            Person loggedInPerson = userBean.getLoggedInPerson();
            if (loggedInPerson == null) {
                userBean.setDisplayMessage("Log in to manage invitations");
                return null;
            }
            String requestorUsername = request.getParameter(USER_NAME_PARAM);
            String friendUsername = request.getParameter(FRIEND_PARAM);

            if (actionType.equals(GET_FRIENDS)) {
                Person p = getPerson(request);
                if (p != null) {
                    out.write(p.getFriendsAsJson());
                }
                out.close();
                return null;
            } else if (actionType.equals(GET_POSTED_EVENTS)) {
                Person p = getPerson(request);
                if (p != null) {
                    out.write(modelFacade.getPostedEventsAsJson(p));
                }
                out.close();
                return null;
            } else if (actionType.equals(GET_ATTEND_EVENTS)) {
                Person p = getPerson(request);
                if (p != null) {
                    out.write(p.getAttendEventsAsJson());
                }
                out.close();
                return null;
            } else if (actionType.equals(REVOKE_INVITE)) {
                //Person p = getPerson(request);
                //Person friend = getFriend(request);
                //String requestorUsername = request.getParameter(USER_NAME_PARAM);
                //String friendUsername = request.getParameter(FRIEND_PARAM);
                Invitation inv = modelFacade.findInvitation(loggedInPerson.getUserName(), friendUsername);
                if (inv != null) {
                    modelFacade.deleteInvitation(loggedInPerson, inv);
                }
                //update outgoing invitation list
                //out.write(loggedInPerson.getOutgoingInvitationsAsJson());
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write(this.OutgoingInvitationsAsJson(loggedInPerson, "revokeInvite"));
                response.flushBuffer();
                out.close();


            } else if (actionType.equals(APPROVE_FRIEND)) {
                Invitation acceptedInv = modelFacade.findInvitation(friendUsername, loggedInPerson.getUserName());
                modelFacade.addFriend(loggedInPerson.getUserName(), friendUsername);
                modelFacade.deleteInvitation(loggedInPerson, acceptedInv);

            } else if (actionType.equals(REJECT_INVITE)) {
                //this is an incoming friendship request so the friend is the requestor
                //Invitation revokedInv = modelFacade.findInvitation(friendUsername, loggedInPerson.getUserName());                
                //let do this in memory

                // Invitation foundInv = null;
                Collection<Invitation> invs = loggedInPerson.getIncomingInvitations();
                Iterator<Invitation> invsIter = invs.iterator();
                Invitation revokedInv = null;
                while (invsIter.hasNext()) {
                    revokedInv = invsIter.next();
                    //incoming
                    if ((revokedInv.getRequestor().getUserName().equalsIgnoreCase(friendUsername)) && (revokedInv.getCandidate().getUserName().equalsIgnoreCase(requestorUsername))) {
                        break;
                    }
                }
                modelFacade.deleteInvitation(loggedInPerson, revokedInv);

            }
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        Person p = null;
        if (AT_CREATE_PARAMVALUE.equals(actionType)) {
            // this shouldn't be hit anymore because the person will be created by a fileupload
            p = createUser(request);
        } else if (AT_READ_PARAMVALUE.equals(actionType)) {
            p = getPerson(request);
        } else if (AT_UPDATE_PARAMVALUE.equals(actionType)) {
            p = updateUser(request);
        } else if (AT_DELETE_PARAMVALUE.equals(actionType)) {
            deleteUser(request);
        } else if (AT_ADD_FRIEND.equals(actionType)) {
            p = addFriend(request);
        }

        if (p != null) {
            out.write(p.toJson());
        }
        out.close();
        return null;
    }

    /*
     * example url = http://localhost:8080/webapp/api/person?user_name=bob123
     **/
    private Person getPerson(HttpServletRequest request) {
        String userName = request.getParameter(USER_NAME_PARAM);
        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        Person person = mf.getPerson(userName);
        return person;
    }

    /*
     * example url = http://localhost:8080/webapp/api/person?user_name=bob123
     **/
    private Person getFriend(HttpServletRequest request) {
        String userName = request.getParameter(FRIEND_PARAM);
        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        Person person = mf.getPerson(userName);
        logger.finer("inside getFriend - the friend's username is " + person.getUserName());
        return person;
    }

//    private Person createUser(HttpServletRequest request, Hashtable<String, String> htUpload) {
    private Person createUser(HttpServletRequest request, Hashtable<String, String> htUpload, FileUploadHandler fuh) {
        String userName = htUpload.get(USER_NAME_PARAM);
        String password = htUpload.get(PASSWORD_PARAM);
        String firstName = htUpload.get(FIRST_NAME_PARAM);
        String lastName = htUpload.get(LAST_NAME_PARAM);
        String summary = htUpload.get(SUMMARY_PARAM);
        String street1 = htUpload.get(STREET1_PARAM);
        String street2 = htUpload.get(STREET2_PARAM);
        String city = htUpload.get(CITY_PARAM);
        String state = htUpload.get(STATE_PARAM);
        String country = htUpload.get(COUNTRY_PARAM);
        String zip = htUpload.get(ZIP_PARAM);
        String timezone = htUpload.get(TIMEZONE_PARAM);
        String telephone = htUpload.get(TELEPHONE_PARAM);
        String email = htUpload.get(EMAIL_PARAM);
        Address address = WebappUtil.handleAddress(context, street1, street2, city, state, zip, country);

        // get image from fileupload
        String imageURL = htUpload.get(UPLOAD_PERSON_IMAGE_PARAM);
        String thumbImage;
        thumbImage = htUpload.get(UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM);
        if (thumbImage == null) {
            thumbImage = "";
        }

        logger.finer("************** data entered is*** " + "user_name*" + userName +
                " password=" + password +
                " first_name=*" + firstName +
                " last_name" + lastName +
                " summary" + summary);

        Person person = new Person(userName, password, firstName, lastName, summary, email, telephone, imageURL, thumbImage, timezone, address);
        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        //do not really need username since you set this value, not sure why it is returned
        //String userName = mf.addPerson(person, userSignOn);
        //changed above line to this since username already a variable name
        //userName = mf.addPerson(person, userSignOn);

        userName = mf.addPerson(person);
        logger.log(Level.FINER, "Person " + userName + " has been persisted");
        // retrieve again ???
        //person=mf.getPerson(userName);
        // login person
        SecurityHandler.getInstance().setLoggedInPerson(request, person);
        return person;
    }

    private Person updateUser(HttpServletRequest request, Hashtable<String, String> htUpload, FileUploadHandler fuh) {
        String userName = htUpload.get(USER_NAME_PARAM);
        String password = htUpload.get(PASSWORD_PARAM);
        String firstName = htUpload.get(FIRST_NAME_PARAM);
        String lastName = htUpload.get(LAST_NAME_PARAM);
        String summary = htUpload.get(SUMMARY_PARAM);
        String street1 = htUpload.get(STREET1_PARAM);
        String street2 = htUpload.get(STREET2_PARAM);
        String city = htUpload.get(CITY_PARAM);
        String state = htUpload.get(STATE_PARAM);
        String country = htUpload.get(COUNTRY_PARAM);
        String zip = htUpload.get(ZIP_PARAM);
        String timezone = htUpload.get(TIMEZONE_PARAM);
        String telephone = htUpload.get(TELEPHONE_PARAM);
        String email = htUpload.get(EMAIL_PARAM);
        Address address = WebappUtil.handleAddress(context, street1, street2, city, state, zip, country);

        // get image from fileupload
        String imageURL = htUpload.get(UPLOAD_PERSON_IMAGE_PARAM);
        String thumbImage;
        thumbImage = htUpload.get(UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM);

        //if these fields are null, then reuse the old file information stored

        //Person loggedInPerson = this.getPerson(request);
        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        Person loggedInPerson = mf.getPerson(userName);

        if (thumbImage == null) {
            thumbImage = loggedInPerson.getImageThumbURL();
        }
        if (imageURL == null) {
            imageURL = loggedInPerson.getImageURL();
        }


        logger.finer("************** data entered is*** " + "user_name*" + userName +
                " password=" + password +
                " first_name=*" + firstName +
                " last_name" + lastName +
                " summary" + summary);

        Person person = new Person(userName, password, firstName, lastName, summary, email, telephone, imageURL, thumbImage, timezone, address);
        //ModelFacade mf= (ModelFacade) context.getAttribute(MF_KEY);
        //do not really need username since you set this value, not sure why it is returned
        //String userName = mf.addPerson(person, userSignOn);
        //changed above line to this since username already a variable name
        //userName = mf.addPerson(person, userSignOn);

        person = mf.updatePerson(person);
        logger.log(Level.FINER, "Person " + userName + " has been updated");

        return person;
    }

    // should need this method anymore, we should only create person from multi-part mime form now
    private Person createUser(HttpServletRequest request) {
        String userName = request.getParameter(USER_NAME_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);
        String firstName = request.getParameter(FIRST_NAME_PARAM);
        String lastName = request.getParameter(LAST_NAME_PARAM);
        String summary = request.getParameter(SUMMARY_PARAM);
        String email = "";
        String telephone = "";
        String imageURL = "";
        String imageThumbURL = "";
        String timezone = "";
        Address address = new Address();

        logger.finer("************** data entered is*** " + "user_name*" + userName +
                " password=" + password +
                " first_name=*" + firstName +
                " last_name" + lastName +
                " summary" + summary);

        Person person = new Person(userName, password, firstName, lastName, summary, email, telephone, imageURL, imageThumbURL, timezone, address);

        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);

        //do not really need username since you set this value, not sure why it is returned
        //String userName = mf.addPerson(person, userSignOn);
        //changed above line to this since username already a variable name
        //userName = mf.addPerson(person, userSignOn);
        userName = mf.addPerson(person);
        logger.log(Level.FINER, "Person " + userName + " has been persisted");
        return mf.getPerson(userName);
    }

    private Person updateUser(HttpServletRequest request) {
        //String personId = request.getParameter(PERSON_ID_PARAM);
        String userName = request.getParameter(USER_NAME_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);
        String firstName = request.getParameter(FIRST_NAME_PARAM);
        String lastName = request.getParameter(LAST_NAME_PARAM);
        String summary = request.getParameter(SUMMARY_PARAM);
        String email = "";
        String telephone = "";
        String imageURL = "";
        String imageThumbURL = "";
        String timezone = "";
        Address address = new Address();

        Person person = new Person(userName, password, firstName, lastName, summary, email, telephone, imageURL, imageThumbURL, timezone, address);

        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        person = mf.updatePerson(person);

        logger.log(Level.FINER, "Person " + userName + " has been updated");
        return person;
    }

    private void deleteUser(HttpServletRequest request) {
        //String personId = request.getParameter(PERSON_ID_PARAM);
        String userName = request.getParameter(USER_NAME_PARAM);
        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        mf.deletePerson(userName);
        logger.log(Level.FINER, "Person " + userName + " has been deleted");
    }

    /*
     * example url = http://localhost:8080/webapp/api/person?actionType=add_friend&user_name=bob123&friend_user_name=sue
     */
    private Person addFriend(HttpServletRequest request) {
        String userName = request.getParameter(USER_NAME_PARAM);
        String friendUserName = request.getParameter(FRIEND_USER_NAME_PARAM);

        logger.finer("***** PERSON-REST-ACTION:addFriend: " + USER_NAME_PARAM + "=" + userName +
                " and " + FRIEND_USER_NAME_PARAM + "=" + friendUserName);

        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        Person person = mf.addFriend(userName, friendUserName);

        logger.log(Level.FINER, "Person " + userName + " has been updated to add friend=" + friendUserName);
        return person;
    }

    private static String OutgoingInvitationsAsJson(Person loggedInPerson, String status) {
        StringBuilder sb = new StringBuilder("{ \"result\": {\"status\":");
        sb.append("\"" + status + "\"");
        sb.append(", \"outgoingInvitations\":[");
        if (loggedInPerson != null) {
            for (Invitation inv : loggedInPerson.getOutgoingInvitations()) {
                sb.append("{\"username\":\"");
                sb.append(WebappUtil.encodeJSONString(inv.getCandidate().getUserName()));
                sb.append(COMMA);
                sb.append(DOUBLE_QUOTE);
                sb.append("fullname");
                sb.append(DOUBLE_QUOTE);
                sb.append(":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(inv.getCandidate().getFirstName() + " " + inv.getCandidate().getLastName()));
                sb.append(DOUBLE_QUOTE);
                sb.append("\"}, ");
            }
            if (loggedInPerson.getOutgoingInvitations().size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        sb.append("] } }");
        return sb.toString();
    }
}
