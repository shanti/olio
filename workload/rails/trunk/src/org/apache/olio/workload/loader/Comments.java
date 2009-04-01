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
    private static final String STATEMENT = "insert into comments " +
            "(user_id, event_id, comment, rating, id, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)";

    static Logger logger = Logger.getLogger(Comments.class.getName());

    int eventId, commentId;
    int[] users;
    String[] comments;
    int[] ratings;
    Date createdTimestamp;


    public String getClearStatement() {
        return "truncate table comments";
    }

    public void prepare() {
		eventId = getSequence() + 1;
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        int commentCount = r.random(0, 20);
        users = new int[commentCount];
        comments = new String[commentCount];
        ratings = new int[commentCount];
        for (int i = 0; i < users.length; i++) {
            users[i] = r.random(1, ScaleFactors.users);;
            comments[i] = r.makeCString(250, 1024);
            ratings[i] = r.random(2, 5);
        }
        createdTimestamp = r.makeDateInInterval( //createdtimestamp
                BASE_DATE, -540, 0);
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (int i = 0; i < users.length; i++) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setInt(1, users[i]);
                s.setInt(2, eventId);
                s.setString(3, comments[i]);
                s.setInt(4, ratings[i]);
                s.setInt(5, eventId * 20 + i);
                s.setDate(6, createdTimestamp);
                s.setDate(7, createdTimestamp);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
