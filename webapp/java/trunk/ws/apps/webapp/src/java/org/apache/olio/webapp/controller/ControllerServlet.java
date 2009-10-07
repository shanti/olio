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

import org.apache.olio.webapp.rest.EventRestAction;
import org.apache.olio.webapp.rest.PersonRestAction;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.sun.javaee.blueprints.webapp.model.Item;
//import com.sun.javaee.blueprints.webapp.model.Tag;

/**
 * This servlet is responsible for interacting with a client
 * based controller and will fetch resources including content
 * and relevant script.
 *
 * This servlet also will process requests for client observers
 * @author Inderjeet Singh
 */
public class ControllerServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ControllerServlet.class.getName());
    private ActionMap actionMap = new ActionMap();
    
    @Override
    public void destroy() {
        actionMap = null;
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Action action = actionMap.get(request);
        if (action != null) {
            String responseUrl = action.process(request, response);
            if (responseUrl != null) {
                getServletContext().getRequestDispatcher(responseUrl).forward(request, response);
            }
        } else {
            logger.log(Level.SEVERE, "Action for '" + request.getRequestURI()  + "' not registered in ControllerServlet!!");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();
        actionMap.put("/api/person", new PersonRestAction(context));
        actionMap.put("/api/event", new EventRestAction(context));
        actionMap.put("/person", new PersonAction(context));
        actionMap.put("/access-artifacts", new ArtifactAction(context));
        actionMap.put("/logout", new LogoutAction(context));
        actionMap.put("/event", new EventAction(context));
        actionMap.put("/tag", new TagAction(context));
        actionMap.put("/util", new UtilAction(context));
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        process(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        process(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
    
}
