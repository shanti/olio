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

/*
 *
 * Created on March 26, 2007, 2:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.apache.olio.webapp.security;

import org.apache.olio.webapp.controller.WebConstants;
import org.apache.olio.webapp.model.ModelFacade;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.util.UserBean;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The SecurityHandler singleton handles all the 
 * generic security of the live application
 * 
 * @author basler
 */
public class SecurityHandler {
    
    public static final String LOGGED_IN_PERSON="LoggedInPerson";
    private static final SecurityHandler securityHandler=new SecurityHandler();
    
    /** Creates a new instance of SecurityHandler */
    private SecurityHandler() {}
    
    
    public static SecurityHandler getInstance() {
        return securityHandler;
    }
    
    
    public Person login(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, String userName, String password) {
         ModelFacade mf=(ModelFacade)servletContext.getAttribute(WebConstants.MF_KEY);
         Person person=mf.getPerson(userName);
         // person will be null if there is an error
         person=login(person, userName, password);
         if(person != null) {
            setLoggedInPerson(request, person);
         }
         return person;
    }
    
    
    
    /** login the user by create a person object in the session
     */
    public Person login(Person person, String userName, String password) {

        // check to make sure password equals
        if(person == null || !person.getPassword().equals(encryptPassword(password))) {
           // passwords aren't equal, so error
           person=null;
        }
        
        return person;
    }
    
    
    public String encryptPassword(String password) {
        // later, mask clear text password
        
        return password;
    }
    
    
    
    public boolean isProtectedResource(String name) {
        boolean bRet=false;
        
        return bRet;
    }
    
    

    public void setLoggedInPerson(HttpServletRequest request, Person person) {
        if(person != null) {
            HttpSession session=request.getSession();
            session.setAttribute(LOGGED_IN_PERSON, person);
        }
    }
    
    public Person getLoggedInPerson(HttpServletRequest request) {
        HttpSession session=request.getSession(true);
        UserBean uBean = (UserBean) session.getAttribute ("userBean");
        if (uBean != null)
            return uBean.getLoggedInPerson();
        return null;
    }

    public boolean isPersonLoggedIn(HttpServletRequest request) {
        boolean bRet=false;
        HttpSession session=request.getSession();
        Person person=(Person)session.getAttribute(LOGGED_IN_PERSON);
        if(person != null) {
            bRet=true;
        }
        return bRet;
    }
    
    
}
