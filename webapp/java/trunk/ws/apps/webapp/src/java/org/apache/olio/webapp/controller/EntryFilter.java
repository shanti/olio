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

import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.security.SecurityHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.olio.webapp.util.WebappUtil;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

/**
 * Main security filter
 */

public class EntryFilter implements Filter {
    
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;
    private HashSet<String> securePages=new HashSet<String>();
    //private static final boolean bDebug=false;
    private static final Logger logger = Logger.getLogger(EntryFilter.class.getName());

    public EntryFilter() {
    }
        
    /**
     *
     * @param request The servlet request we are processing
     * @param result The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        Throwable problem = null;
        try {
            
            // Implement a simple security model for now.
            // Programtic security is needed to match submitters with their events.
            HttpServletRequest httpRequest=(HttpServletRequest)request;
            HttpServletResponse httpResponse=(HttpServletResponse)response;
            SecurityHandler securityHandler=SecurityHandler.getInstance();
            String resource=httpRequest.getServletPath();
            logger.finer("\nEntity Filter - Have servletpath = " + resource);
            
            if(resource.equals("/login.jsp")) {
                // login page either being accessed or submitted
                logger.finer("Entity Filter - have login page request or submission");
                
                // see if parameters are submitted
                String userName=request.getParameter(WebConstants.USER_NAME_PARAM);
                String password=request.getParameter(WebConstants.PASSWORD_PARAM);
                String accessingURL=request.getParameter("accessingURL");
                if(userName != null) {
                    // login action
                    Person person=securityHandler.login(getFilterConfig().getServletContext(), httpRequest, httpResponse, userName, password);
                    if(person != null) {
                        // login successful, return originally requested resource
                        // don't like showing login in url because can't bookmark so redirect to it
                        httpResponse.sendRedirect(httpRequest.getContextPath() + accessingURL);
                    } else {
                        // error on login, populate error and go to login page again
                        // make sure to set hidden accessingURL again
                        
                        // set response header to alert ajax calls of a login error.
                        httpResponse.addHeader("LogginError", WebappUtil.getMessage("login_error"));
                        
                        RequestDispatcher requestDispatcher=httpRequest.getRequestDispatcher("/login.jsp?accessingURL=" + 
                                httpResponse.encodeURL(accessingURL) + "&loginError=" + 
                                httpResponse.encodeURL(WebappUtil.getMessage("login_error")));
                        requestDispatcher.forward(httpRequest, httpResponse);
                        return;
                    }
                }
                
            } else if(!securityHandler.isPersonLoggedIn(httpRequest)) {
                // the person isn't logged in, see if they are accessing a protected resource

                // since we have a rest type of url, need to get path info to decide if path protected
                String pathInfo=httpRequest.getPathInfo();
                if(pathInfo != null) {
                    resource += pathInfo;
                    // using the rest type urls go only 3 '/' deep for security check (e.g. /xxx/xxxx/xxx)
                    resource=getSecurityURI(resource);
                    logger.finer("\nEntity Filter - checking if protect path = " + resource);
                }
                
                
                logger.finer("Checking resource to see if login required " + resource);
                logger.finer("Entity Filter - Checking resource to see if login required - " + resource);
                //resource=resource.substring(resource.lastIndexOf("/") + 1);
                // if null page then using default welcome mechanism, assume it is an accessable page.
                if(resource != null) {
                    if(securePages.contains(resource)) {
                        logger.finer("Entity Filter - have secure resource - " + resource);
                        
                        // set response header to alert ajax calls of a login error.
                        httpResponse.addHeader("NeedLogin", "The user needs to be logged in");
                        
                        // need to login to get to these page
                        //??? todo, need pathinfo and querystring
                        // what about post payload if an event is being submitted for creation
                        RequestDispatcher requestDispatcher=httpRequest.getRequestDispatcher("/login.jsp?accessingURL=" + 
                                httpResponse.encodeURL(getAccessURL(httpRequest)));
                        requestDispatcher.forward(httpRequest, httpResponse);
                        return;
                    }
                }
            }
            
            chain.doFilter(request, response);
        } catch(Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            //
            problem = t;
            t.printStackTrace();
        }
        
        //
        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        //
        if (problem != null) {
            if (problem instanceof ServletException) throw (ServletException)problem;
            if (problem instanceof IOException) throw (IOException)problem;
            sendProcessingError(problem, response);
        }
    }

    
    private static String getSecurityURI(String path) {
        StringBuilder sb=new StringBuilder();
        int cnt=0;
        char[] array=path.toCharArray();
        for(char chx : array) {
            if(chx == '/') cnt++;
            if(cnt >= 4) break;
            sb.append(chx);
        }
        
        return sb.toString();
    }
    
    private static String getAccessURL(HttpServletRequest request) {
        // need to create a valid string that can be used be the RequestDispatcher
        StringBuilder sb=new StringBuilder(request.getServletPath());
        if(request.getPathInfo() != null) {
            sb.append(request.getPathInfo());
        }
        if(request.getQueryString() != null) {
            sb.append("?");
            sb.append(request.getQueryString());
        }
        
        return sb.toString();
    }
    
    
    
    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }
    
    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
    
    /** Destroy method for this filter */
    public void destroy() {
    }
    
    
    /** Init method for this filter */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        
        // read in allowed access points
        String securePagesParam=filterConfig.getServletContext().getInitParameter("securePages");
        logger.finer("\n*** entry String = " + securePagesParam);
        // loop through pages to see if
        StringTokenizer stPages=new StringTokenizer(securePagesParam, "|");
        int countx=stPages.countTokens();
        for(int ii=0; ii < countx; ii++) {
            securePages.add(stPages.nextToken());
        }
    }
    
    
    public String toString() {
        if (filterConfig == null) return ("EntryFilter()");
        StringBuffer sb = new StringBuffer("EntryFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        try {
            String stackTrace = getStackTrace(t);
            PrintWriter pw = response.getWriter();
            if(stackTrace != null && !stackTrace.equals("")) {
                response.setContentType("text/html");
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N
                
                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
            } else {
                t.printStackTrace(pw);
            }
            WebappUtil.closeIgnoringException(pw);
        } catch(IOException ex){
        }
    }
    
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        WebappUtil.closeIgnoringException(pw);
        WebappUtil.closeIgnoringException(sw);
        return sw.getBuffer().toString();
    }
}
