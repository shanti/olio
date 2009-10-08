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
package org.apache.olio.workload.loader;

import com.sun.faban.driver.util.Random;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.loader.framework.ThreadResource;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.text.DateFormat;
import java.sql.Date;

/**
 * Comments Loader.
 */
public class Comments extends Loadable {
    // We use on average of 10 comments per event. Random 0..20 comments..

    private static final String STATEMENT = "insert into COMMENTS_RATING " +
            "(commentsid, username_username, socialevent_socialeventid, comments, rating, creationtime) values (?, ?, ?, ?, ?, ?)";
    static Logger logger = Logger.getLogger(Comments.class.getName());
    private static AtomicLong idGen = new AtomicLong(1);
    public static final Date BASE_DATE = new Date(System.currentTimeMillis());
    int eventId;
    String[] userNames;
    String[] comments;
    int[] ratings;
    String[] cDates;

    public static long getNextId() {
        return idGen.getAndIncrement();
    }

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
        cDates = new String[commentCount];

        DateFormat dateFormat = tr.getDateFormat();
        String cDate = dateFormat.format(
                r.makeDateInInterval(BASE_DATE, 0, 540));
        int eventHr = r.random(7, 21);
        int eventMin = (int) r.random(0, 59);

        for (int i = 0; i < userNames.length; i++) {
            int userId = r.random(1, ScaleFactors.users);
            userNames[i] = UserName.getUserName(userId);
            comments[i] = r.makeCString(10, 1000);
            ratings[i] = r.random(2, 5);
            cDates[i] = String.format("%s %02d:%s:00",
                    cDate, eventHr, eventMin);
        }
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (int i = 0; i < userNames.length; i++) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setLong(1, getNextId());
                s.setString(2, userNames[i]);
                s.setInt(3, eventId);
                s.setString(4, comments[i]);
                s.setInt(5, ratings[i]);
                s.setString(6, cDates[i]);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * For address, update ID after all the data is loaded.
     * So we update the ID_GEN table at postload and add 1 to count.
     */
    public void postLoad() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            //update id

            //bug exists in JPA where we are using one ID generator (address)
            //for now, update to a ridiculous high number to avoid duplicate key 
            //exceptions

            logger.fine("Updating Comments_Rating ID");
            c.prepareStatement("INSERT INTO ID_GEN " +
                    "(GEN_KEY, GEN_VALUE) " +
                    "VALUES ('COMMENTS_RATING_ID', " + ScaleFactors.events + 1 + ")");
            c.executeUpdate();
            logger.fine("After updating Comments_Rating ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        // LoadController.increaseErrorCount();
        }
    }
}
