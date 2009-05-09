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
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.model.Invitation;
import org.apache.olio.webapp.security.SecurityHandler;
import org.apache.olio.webapp.util.UserBean;
import java.util.Collection;
import java.util.Iterator;

/**
 * handles all request related to users
 * @author Mark Basler
 * @author Inderjeet Singh
 * @author Kim LiChong
 */
public class PersonAction implements Action {
    
    private static final boolean bDebug=false;
    
    public PersonAction(ServletContext context) {
        this.context = context;
    }
    /**
     * DO NOT UPDATE THIS FILE
     * INSTEAD use rest.PersonRestAction
     * display person details used to be AJAX requests
     * changing this to displaying JSP page 
     *
     */
    
    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter(USER_NAME_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);
        String firstName = request.getParameter(FIRST_NAME_PARAM);
        String lastName = request.getParameter(LAST_NAME_PARAM);
        String summary = request.getParameter(SUMMARY_PARAM);
        
        
        String actionType = request.getParameter(ACTION_TYPE_PARAM);
        ModelFacade mf= (ModelFacade) context.getAttribute(MF_KEY);
        String returnURL=null;
        String path = request.getPathInfo();
        if (path!=null && path.equals("/login")) {
            String fUrl = "/site.jsp";
            String user_name = request.getParameter("user_name");
            UserBean userBean = (UserBean) request.getSession(true).getAttribute("userBean");
            if (userBean == null) {
                userBean = new UserBean();
                userBean.setModelFacade(mf);
                request.getSession().setAttribute("userBean", userBean);
            }
            if (user_name != null && password != null) {
                Person p = mf.login(user_name, password);
                if (p != null) {
                    userBean.setDisplayMessage("Successfully logged in");
                    userBean.setLoggedInPerson(p);
                }
            } else {
                userBean.setDisplayMessage("Log in failed for user_name = " + user_name +
                        " password = " + password);
            }
            fUrl = userBean.getForwardingUrl();
            if (fUrl == null) {
                fUrl = request.getContextPath() + "/event/list";
            } else {
                fUrl = request.getContextPath() + fUrl;
            // We will do a redirect rather than a forward so that refreshing the browser
            // does not cause issues
            }
            response.sendRedirect(fUrl);
            return null;
        }
        
        if (actionType!=null){
            if (actionType.equalsIgnoreCase("display_person")){
                String username = request.getParameter("user_name");
                Person displayUser = mf.findPerson(username);
               if (displayUser != null) {
                   //get posted events
                   Collection<SocialEvent> userEvents = mf.getPostedEvents(displayUser);
                   request.setAttribute("userEvents", userEvents);
                   //System.out.println("the size of the incoming friendships is "+ displayUser.getIncomingInvitations().size());
                 request.setAttribute("displayPerson", displayUser);                      
                 returnURL="/site.jsp?page=personContent.jsp";
               }
           } else if(actionType.equalsIgnoreCase("display_friends")) {
               String username = request.getParameter("user_name");
                Person displayUser = mf.findPerson(username);
                request.setAttribute("displayPerson", displayUser); 
                returnURL="/site.jsp?page=friends.jsp";           
                
           } else if(actionType.equalsIgnoreCase("add_person")) {
               //should not go here - we are doing this via PersonRestAction
               //this.addPerson(request, response);               
               returnURL = "/site.jsp?page=addPerson.jsp";
               
           } else if(actionType.equalsIgnoreCase("edit_person")) {
               request.setAttribute("isEditable", Boolean.TRUE);               
               returnURL="/site.jsp?page=addPerson.jsp";
           } else if(actionType.equalsIgnoreCase("display_myPostedEvents")) {
               String username = request.getParameter("user_name");
                Person displayUser = mf.findPerson(username);
                if (displayUser != null) {
                   //get posted events
                   Collection<SocialEvent> myPostedEvents = mf.getPostedEvents(displayUser);                   
                   System.out.println("the size of myPostedEvents is " +  myPostedEvents.size() );
                   request.setAttribute("myPostedEvents", myPostedEvents);
                }
                returnURL="/site.jsp?page=eventsPostedByUser.jsp";
           }    else if(actionType.equalsIgnoreCase("Search")) {
               String query = request.getParameter("query");
                //need to specify max results
                Collection <Person> searchResults = mf.searchUsers(query, 0);
                //have all of the users - now get the friends
                Person requestor = mf.findPerson(userName);
                UserBean userBean = (UserBean)request.getSession().getAttribute("userBean");
                Person loggedInPerson = userBean.getLoggedInPerson();
                String loggedInPersonUsername = loggedInPerson.getUserName();
                Collection<Person> friends = loggedInPerson.getFriends();
                System.out.println("the size of the loggedInPerson's friends  "+ loggedInPerson.getUserName() +" is " + friends.size() );               
                //invitations
                Collection<Invitation> invites = loggedInPerson.getIncomingInvitations();
                System.out.println("the size of the invitations list is "+ invites.size());
                
                //iterate through and remove the friends from the list
                //need to remove loggedInPerson too since you cannot have yourself as a friend
                Iterator<Person> it = searchResults.iterator();
                while (it.hasNext()) {
                    Person person = it.next();
                    
                    Iterator<Person> friendIterator = friends.iterator();
                    Iterator<Invitation> invitationIter = invites.iterator();
                    
                    while (friendIterator.hasNext()) {
                        String fUsername = friendIterator.next().getUserName();
                        if (fUsername.equalsIgnoreCase(person.getUserName()) || fUsername.equalsIgnoreCase(loggedInPerson.getUserName())) {
                            it.remove();
                        }
                    }
                    //determine whether they are received their invitation already
                    while(invitationIter.hasNext()){
                        Invitation inv = invitationIter.next();
                        if(inv.getCandidate().getUserName().equalsIgnoreCase(person.getUserName())){                        
                            person.setHasReceivedInvitation(true);
                        }
                    }
                }
                //System.out.println("after sorting, the size of the collection is " + searchResults.size());
                                                                
                //not in session request.getSession().setAttribute("searchResults", searchResults);
                loggedInPerson.setNonFriendList(searchResults);
                

                if (searchResults != null) {
                    request.setAttribute("searchResults", searchResults);
                }
                returnURL = "/site.jsp?page=searchUsers.jsp";
            } else if (actionType.equalsIgnoreCase("addDeleteFriend")) {
                String query = request.getParameter("query");
                String friendUsername = request.getParameter("friend");
                //this is returning null - SecurityHandler.getInstance().getLoggedInPerson(request);
                UserBean userBean = (UserBean)request.getSession().getAttribute("userBean");
                Person loggedInPerson = userBean.getLoggedInPerson();
                Collection <Person> previousSearchResults = loggedInPerson.getNonFriendList();
                ///add or delete
                String flag = request.getParameter("flag");
                System.out.println("*** flag is " + flag);
                if (flag.equals("add")) {
                    Person friend = mf.findPerson(friendUsername);
                    Invitation invitation = new Invitation(loggedInPerson, friend);
                    mf.addInvitation(loggedInPerson, invitation);
                    //iterate through and set new added friend's hasReceivedInvitation to true
                    Iterator<Person> searchIter = previousSearchResults.iterator();
                    while (searchIter.hasNext()) {
                        Person eachPerson = searchIter.next();
                        if (eachPerson.getUserName().equalsIgnoreCase(friendUsername)) {
                            eachPerson.setHasReceivedInvitation(true);
                            System.out.println("user " + eachPerson.getUserName() + " status is " + eachPerson.isHasReceivedInvitation());
                        }
                    }

                } else if (flag.equals("delete")) {  
                        Invitation inv = mf.findInvitation(loggedInPerson.getUserName(), friendUsername);                       
                        mf.deleteInvitation(loggedInPerson, inv);
                    //iterate through and set new added friend's hasReceivedInvitation to false 
                    
                    Iterator<Person> searchIter = previousSearchResults.iterator();
                    while (searchIter.hasNext()) {
                        Person eachPerson = searchIter.next();
                        if (eachPerson.getUserName().equalsIgnoreCase(friendUsername)) {
                            eachPerson.setHasReceivedInvitation(false);
                            System.out.println("user " + eachPerson.getUserName() + " status is " + eachPerson.isHasReceivedInvitation());
                        }

                    }
                }

                //reset the old list in memory with new list
                loggedInPerson.setNonFriendList(previousSearchResults);

                //no need to do query again - just use previousSearch results
                // Collection searchResults = mf.searchUsers(query, 0);

                if (previousSearchResults != null) {
                    request.setAttribute("searchResults", previousSearchResults);
                }
                returnURL = "/site.jsp?page=searchUsers.jsp";
            }

        }
        return returnURL;
}

    
    private ServletContext context;

    private Person addPerson(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter(USER_NAME_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);
        String firstName = request.getParameter(FIRST_NAME_PARAM);
        String lastName = request.getParameter(LAST_NAME_PARAM);
        String summary = request.getParameter(SUMMARY_PARAM);
        String street1 = request.getParameter(STREET1_PARAM);
        String street2 = request.getParameter(STREET2_PARAM);
        String city = request.getParameter(CITY_PARAM);
        String state = request.getParameter(STATE_PARAM);
        String country = request.getParameter(COUNTRY_PARAM);
        String zip = request.getParameter(ZIP_PARAM);
        String timezone = request.getParameter(TIMEZONE_PARAM);
        String telephone = request.getParameter(TELEPHONE_PARAM);
        String email = request.getParameter(EMAIL_PARAM);
        Address address = WebappUtil.handleAddress(context, street1, street2, city, state, zip, country);

        // get image from fileupload
        String imageURL = request.getParameter(UPLOAD_PERSON_IMAGE_PARAM);
        String thumbImage;
        thumbImage = request.getParameter(UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM);
        if (thumbImage == null) {
            thumbImage = "";
        }
        if (bDebug) {
            System.out.println("************** data entered is*** " + "user_name*" + userName +
                    " password=" + password +
                    " first_name=*" + firstName +
                    " last_name" + lastName +
                    " summary" + summary);
        }
        Person person = new Person(userName, password, firstName, lastName, summary, email, telephone, imageURL, thumbImage, timezone, address);
        ModelFacade mf = (ModelFacade) context.getAttribute(MF_KEY);
        //do not really need username since you set this value, not sure why it is returned
        //String userName = mf.addPerson(person, userSignOn);
        //changed above line to this since username already a variable name
        //userName = mf.addPerson(person, userSignOn);

        userName = mf.addPerson(person);
        WebappUtil.getLogger().log(Level.FINE, "Person " + userName + " has been persisted");
        // retrieve again ???
        //person=mf.getPerson(userName);
        // login person
        SecurityHandler.getInstance().setLoggedInPerson(request, person);
        return person;

    }
}
