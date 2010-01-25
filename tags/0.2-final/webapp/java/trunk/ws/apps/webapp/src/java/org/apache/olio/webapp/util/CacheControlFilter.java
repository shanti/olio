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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Binu John
 * 
 * Filter to add caching attributes to various files. 
 * By default, the filter is not enabled. 
 */
public class CacheControlFilter implements Filter {

    private FilterConfig config;
    private Map<String, String> mimeMap = new HashMap<String, String>();
    boolean isWildCardSpecified = false;
    private boolean cacheEnabled = true; // Provide a System property based over ride mechanism
    Logger logger = Logger.getLogger(CacheControlFilter.class.getName());

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        // Populate the map. 
        configureCache();

        if (!cacheEnabled) {
            logger.info("static content caching is disabled");
            return;
        } else {
            logger.info("static content caching is enabled");
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (cacheEnabled) {
            HttpServletRequest hreq = (HttpServletRequest) request;
            String path = hreq.getRequestURI();
            String mimeType = config.getServletContext().getMimeType(path);

            HttpServletResponse hres = (HttpServletResponse) response;

            boolean found = false;
            if (mimeType != null) {
                if (isWildCardSpecified) {
                    int index = mimeType.indexOf("/");
                    if (index != -1) {
                        String mime = mimeType.substring(0, index + 1);
                        if (mimeMap.containsKey(mime)) {
                            hres.addHeader("Cache-Control", mimeMap.get(mime));
                            found = true;
                        }
                    }
                }
                if (!found && mimeMap.containsKey(mimeType)) {
                    hres.addHeader("Cache-Control", mimeMap.get(mimeType));
                }
            }
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
    }

    private void configureCache() {
        // Over ride defaults through a properties file
        // 1. Check if the System property is set to disable it.
        // 2. If caching is enabled, check the file specified by the System property first
        //    This is an over ride mechanism that allows the deployer to modify the behavior
        // 3. Default behavior is taken from the Filter init params (web.xml)

        cacheEnabled = !Boolean.getBoolean("disableStaticContentCaching");
        if (!cacheEnabled) {
            return;
        }

        // Overwrite the default if specified
        String str = System.getProperty("staticContentCachingConfigurationFile");
        if (str != null && new File(str).exists()) {
            try {
                InputStream is = new FileInputStream(str);
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    is.close();

                    // Parse the properties
                    Set<String> keys = props.stringPropertyNames();
                    Iterator<String> iter = keys.iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        processMimeType(key, props.getProperty(key));
                    }
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(CacheControlFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Enumeration<String> emimes = config.getInitParameterNames();
            while (emimes.hasMoreElements()) {
                String mime = emimes.nextElement();
                String value = config.getInitParameter(mime);
                processMimeType(mime, value);
            }
        }
    }

    private void processMimeType(String mime, String value) {
        if (value == null) {
            return;
        }

        int index = mime.indexOf("/");
        if (index == -1 || mime.length() < 3) // Incorrect mime specification
        {
            return;
        }

        // Check for wild cards
        if (mime.charAt(mime.length() - 1) == '*') {
            String wcard = mime.substring(0, index + 1);
            mimeMap.put(wcard, value);
            isWildCardSpecified = true;
        } else {
            mimeMap.put(mime, value);
        }
    }
}
