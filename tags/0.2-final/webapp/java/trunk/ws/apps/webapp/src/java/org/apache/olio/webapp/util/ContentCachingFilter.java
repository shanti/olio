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

import org.apache.olio.webapp.cache.Cache;
import org.apache.olio.webapp.cache.CacheFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This filter allows caching of JSP and Servlet contents.
 * It only caches content from specified JSPs. Currenlty these paths are hard coded
 * TO DO: Make the content caching configurable.
 * 
 * @author Binu John
 */
public class ContentCachingFilter implements Filter {

    private Cache cache; // For caching static content
    private FilterConfig fConfig;
    private long nextRefresh;
    private static final int CACHE_LOOP_TIME = 10 * 1000; // 10 seconds
    private static final int CACHE_CHECK_INTERVAL = 100; // in millisecs
    private Logger logger = Logger.getLogger(ContentCachingFilter.class.getName());

    /** Creates a new instance of ContentCachingFilter */
    public ContentCachingFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        cache = CacheFactory.getCache("CachedPages");
        fConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // first check whether this page exists in teh cache
        HttpServletRequest hReq = (HttpServletRequest) request;
        String path = hReq.getRequestURI();
        String cPath = hReq.getContextPath();

        // We will pass along the content to the Filter chain if other filter nood to do
        // something about it. The flush is carried out here since JSPs can only
        // ouput char[] or String. Writing as byte[] allows us to eliminate the 
        // char decoding expense.

        // The following pages are going to be cached - index.jsp, home_content.jsp. Teh rest are ignored.
        if (path.equals(cPath + "/index.jsp") || path.equals(cPath + "/")) {
            HttpSession session = hReq.getSession(false);
            if (session == null || session.getAttribute("LoggedInPerson") == null) {
                String cacheKey = "home";
                Object obj = getContent(cacheKey, path, request, response, chain);

                if (obj != null) {
                    // Write the content and return. 
                    // Important to note that this will eliminate calling
                    // other filters if any are present.
                    // Ok since we don't have any other filters.

                    if (obj instanceof byte[]) {
                        byte[] content = (byte[]) obj;
                        OutputStream out = response.getOutputStream();
                        out.write(content);
                        out.flush();
                    } else {
                        char[] content = (char[]) obj;
                        PrintWriter out = response.getWriter();
                        out.print(content);
                        out.flush();
                    }

                    return;
                } else {
                    chain.doFilter(request, response);
                }
            } else { // USer is logged in
                // Try to get the home_content from the cache
                String cacheKey = "home_partial";
                //logger.finer ("User logged in - using the home_content.jsp cache ");
                path = "/home_content.jsp";
                Object content = getContent(cacheKey, path, request, response, chain);
                HashMap<String, Object> contentMap = new HashMap<String, Object>();
                contentMap.put(path, content);
                request.setAttribute("contentMap", contentMap);
                chain.doFilter(request, response);
                request.removeAttribute("contentMap");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
    The caching works as follows -
    1. Try to get the content from the cache
    2. If the content is null, no one has populated the cache.
    We will try to acquire the lock to update the cache.
    3a. If we are successful in acquiring the lock, we will populate the cache and release the lock
    3b. If we are not successful in populating the cache,
    that means that someone else has acquired the lock. We will wait in a loop
    for a while to get the content or acquire the lock
    4. If we are unable to get a repsonse from the cache within a certain threashold, regenrate the data.
     **/
    /**
     * 
     * @param key
     * @param path
     * @param request
     * @param response
     * @param chain
     * @return return the char buffer represnting the string. Saving as char[] eliminates the char encoding cost 
     *         
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    private Object getContent(String key, String path, ServletRequest request,
            ServletResponse response, FilterChain chain) throws IOException, ServletException {
        return getContent(key, path, request, response, chain, true);
    }

    private Object getContent(String key, String path, ServletRequest request,
            ServletResponse response, FilterChain chain, boolean asBytes) throws IOException, ServletException {
        // First get the page from cache
        Object obj = cache.get(key);

        char[] content = null;
        Object retObj = null;

        if (obj != null) {
            // The page is cached. But does it need a refresh?
            if (cache.needsRefresh(true, key)) {
                // We own the lock, so we need to refresh it
                String str = acquirePageContent(path, request, response, chain);
                if (str != null) {
                    if (!asBytes) {
                        content = new char[str.length()];
                        str.getChars(0, str.length(), content, 0);
                        retObj = content;
                    } else {
                        retObj = str.getBytes();
                    }

                    cache.put(key, retObj, CacheFactory.getInstance().getCacheExpireInSeconds());
                    // Release the lock
                    cache.doneRefresh(key, CacheFactory.getInstance().getCacheExpireInSeconds());
                    return retObj;
                }
            }
            return obj;
        } else {
            // No one has populated the cache.
            // Do the following in a loop
            // 1. Try to acquire the lock
            // 2a. If we have the lock. acuire the page content, put it in cache & reset the lock
            // 2b. If we did not acquire the lock, spin for a period of time
            //     to get the content from the cache or the lock. If not achieved within a certain
            //     time, generate the output and return.
            long start = System.currentTimeMillis();
            int attemptCount = 1;
            while (true) {
                logger.finer("needsRefreshAttempt = " + attemptCount++);
                if (cache.needsRefresh(false, key)) {
                    // We have the lock, refresh the content
                    String str = acquirePageContent(path, request, response, chain);
                    if (str != null) {
                        if (!asBytes) {
                            content = new char[str.length()];
                            str.getChars(0, str.length(), content, 0);
                            retObj = content;
                        } else {
                            retObj = str.getBytes();
                        }

                        cache.put(key, retObj);

                        // Release the lock
                        cache.doneRefresh(key, CacheFactory.getInstance().getCacheExpireInSeconds());
                    //logger.finer ("Acquired lock and put Content in cache - path = " + path);
                    }
                    return retObj;
                } else {
                    // Someone else has the lock. Sleep for a while and then try again
                    try {
                        Thread.sleep(CACHE_CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                    }
                    retObj = cache.get(key);
                    if (retObj != null) {
                        return retObj;
                    }

                    if ((System.currentTimeMillis() - start) > CACHE_LOOP_TIME) {
                        logger.info("Tries too many times to refresh - giving up");
                        break;
                    }
                }
            }
            // We could not get the content from the cache within the specified time.
            // Regenerate
            //logger.finer ("Timed out in getting content from cache - path = " + path);
            String str = acquirePageContent(path, request, response, chain);
            if (str != null) {
                if (!asBytes) {
                    content = new char[str.length()];
                    str.getChars(0, str.length(), content, 0);
                    retObj = content;
                } else {
                    retObj = str.getBytes();
                }
            }
            return content;
        }
    }

    private String acquirePageContent(String path, ServletRequest request,
            ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCacheResponseWrapper wrapper = new ContentCacheResponseWrapper(
                (HttpServletResponse) response);
        if (path.equals("/home_content.jsp")) {
            // This is a special case of inclusion
            fConfig.getServletContext().getRequestDispatcher(path).include(request, wrapper);
        } else {
            chain.doFilter(request, wrapper);
        }
        //logger.finer ("acquirePageContent called - path = " + path);
        return new String(wrapper.getData());
    }

    public void destroy() {
    }
}


