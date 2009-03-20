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
 * $Id: Comments.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package org.apache.olio.workload.loader;

import com.sun.faban.driver.util.Random;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.loader.framework.ThreadResource;

import java.sql.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Comments Loader.
 */
public class Comments extends Loadable {
    // We use on average of 10 comments per event. Random 0..20 comments..
    public static final Date BASE_DATE = new Date(System.currentTimeMillis());
    private static final String STATEMENT = "insert into COMMENTS_RATING " +
            "(username, socialeventid, comments, ratings, created_at) values (?, ?, ?, ?, ?)";

    static Logger logger = Logger.getLogger(Comments.class.getName());

    int eventId;
    String[] userNames;
    String[] comments;
    int[] ratings;
    Date created_at;
    
    public String getClearStatement() {
        return "truncate table COMMENTS_RATING";
    }

    public void prepare() {
        eventId = getSequence();
        ++eventId;
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        int commentCount = r.random(0, 20);
        userNames = new String[commentCount];
        comments = new String[commentCount];
        ratings = new int[commentCount];
        for (int i = 0; i < userNames.length; i++) {
            int userId = r.random(1, ScaleFactors.users);
            userNames[i] = UserName.getUserName(userId);
            comments[i] = r.makeCString(10, 1000);
            ratings[i] = r.random(2, 5);
        }
        created_at = r.makeDateInInterval( BASE_DATE, -540, 0);
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (int i = 0; i < userNames.length; i++) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setString(1, userNames[i]);
                s.setInt(2, eventId);
                s.setString(3, comments[i]);
                s.setInt(4, ratings[i]);                
                s.setDate(5, created_at);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
