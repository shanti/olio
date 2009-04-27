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
 * 
 * $Id: ThreadResource.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package org.apache.olio.workload.loader.framework;

import com.sun.faban.driver.util.Random;

import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class ThreadResource {

    Logger logger = Logger.getLogger(ThreadResource.class.getName());

    private static ThreadLocal<ThreadResource> resource =
            new ThreadLocal<ThreadResource>() {
        public ThreadResource initialValue() {
            return new ThreadResource();
        }
    };

    Random random;
    StringBuilder buffer;
    SimpleDateFormat dateFormat;

    private ThreadResource() {
        buffer = new StringBuilder(256);
        random = new Random();
    }

    public StringBuilder getBuffer() {
        buffer.setLength(0); // Make sure we clear it
        return buffer;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * DateFormat is not thread safe. We need to include it into the
     * ThreadResource.
     * @return The thread instance of DateFormat.
     */
    public DateFormat getDateFormat() {
        if (dateFormat == null)
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat;
    }

    public static ThreadResource getInstance() {
        return resource.get();
    }
}
