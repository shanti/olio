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

/**
 * 
 * @author Binu John
 */
public class WebappConstants {
    
    public static final String WEBAPP_INDEX_DIRECTORY=System.getProperty("com.sun.aas.instanceRoot") + 
                                                            "/lib/webapp/searchindex";
    public static String WEBAPP_IMAGE_DIRECTORY=System.getProperty("com.sun.aas.instanceRoot") +
                                                            "/lib/webapp_lib/artifacts";
    public static final String DEFAULT_IMAGE_DIRECTORY=System.getProperty("com.sun.aas.instanceRoot") +
                                                            "/docroot/artifacts";
    public static final String WEBAPP_BASE_LOGGER="org.apache.olio.webapp";
    public static final String WEBAPP_BASE_LOG_STRINGS="org.apache.olio.webapp.util.LogStrings";

    public static final String DEFAULT_CACHE="org.apache.olio.webapp.cache.SimpleMapCacheFactory";

    public static final String LEGAL_NOTICE="Copyright ? 2008 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara, California 95054, U.S.A. All rights reserved.\n" 
            + "U.S. Government Rights - Commercial software. Government users are subject to the Sun Microsystems, Inc. standard license agreement and applicable provisions of the FAR and its supplements.\n"
            + "Use is subject to license terms.\n"
            + "This distribution may include materials developed by third parties.\n"
            + "Sun, Sun Microsystems, the Sun logo, Java and MySQL are trademarks or registered trademarks of Sun Microsystems, Inc. in the U.S. and other countries.";
    
    public static final String ORDER_CREATED = "created_at";
    public static final String ORDER_EVENT_DATE = "event_date";
    
    public static int ORDER_BY_ASCENDING = 0;
    public static int ORDER_BY_DESCENDING = 1;
    
    public static final int PAGINATION_NUM_PAGES_DISPLAY = 10;
    public static final int ITEMS_PER_PAGE = 10;
    
    public static final int NUM_TAGS = 50;
    public static final int MAX_RATING = 5;
    
    public static final int CACHE_TIME_TO_LIVE_IN_SECS = 1800; // half hour
    
    public static final int UPCOMING_EVENTS_DISPLAY_MAX = 3;
   
    /** private constructor to enforce only constants class */
    private WebappConstants() {}
    
}
