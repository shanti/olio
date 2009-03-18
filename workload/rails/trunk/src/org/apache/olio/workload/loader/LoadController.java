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
 * $Id: LoadController.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package org.apache.olio.workload.loader;

import org.apache.olio.workload.util.ScaleFactors;

import java.util.logging.Logger;

import static org.apache.olio.workload.loader.framework.Loader.*;

public class LoadController {

    static Logger logger = Logger.getLogger(LoadController.class.getName());

    public static void main(String[] args) throws Exception {
        setJDBCDriverClassName(args[0]);
        setConnectionURL(args[1]);
        ScaleFactors.setActiveUsers(Integer.parseInt(args[2]));

        // Clear the database
        clear(Person.class);
        clear(Friends.class);
        clear(Address.class);
        clear(Tag.class);
        clear(SocialEvent.class);
        clear(EventTag.class);
        clear(Attendees.class);
        clear(Comments.class);
        clear(Documents.class);
        clear(PersonImages.class);
        clear(EventImages.class);
        logger.info("Done clearing database tables.");

        // load person, friends, and addresses
        load(Person.class, ScaleFactors.users);
        load(Friends.class, ScaleFactors.users);
        load(Address.class, ScaleFactors.users);
		// load(new Images(imageId, "p"));
        load(PersonImages.class, ScaleFactors.users);

        // load tags
        load(Tag.class, ScaleFactors.tagCount);

        // load events and all relationships to events
        load(SocialEvent.class, ScaleFactors.events);
        load(EventTag.class, ScaleFactors.events);
        load(Attendees.class, ScaleFactors.events);
        load(Comments.class, ScaleFactors.events);
        load(EventImages.class, ScaleFactors.events);
        load(Documents.class, ScaleFactors.events);

        waitProcessing();
        logger.info("Done data creation.");

        // Now we need to check that all loading is done.
        shutdown();
        logger.info("Done data loading.");

        // We use a new set of connections and thread pools for postLoad.
        // This is to ensure all load tasks are done before this one starts.
        postLoad(Tag.class);
        shutdown();
        logger.info("Done post-load.");
        System.exit(0); // Signal successful loading.
    }
}
