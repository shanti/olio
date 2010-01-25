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

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Binu John
 */
public class UtilAction implements Action {
    private ServletContext context;
    
    UtilAction(ServletContext context) {
        this.context = context;
    }

    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path.equalsIgnoreCase("/zipSearch")) {
            zipSearch(request, response);
            return null;
        }
        return null;
    }

    private void zipSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("param");
        PrintWriter out = response.getWriter();
        if (param == null)
            out.println (",");
        else {    
            if(param.equals("95051"))
                out.println("Santa Clara,CA");
            else if(param.equals("95054"))
                out.println("Santa Clara,CA");
            else if(param.equals("94040"))
                out.println("Mountain View,CA");
            else if(param.equals("94025"))
                out.println("Menlo Park,CA");
            else if(param.equals("94086"))
                out.println("Sunnyvale,CA");
            else 
                out.println (",");
        }
    }
    
    

}
