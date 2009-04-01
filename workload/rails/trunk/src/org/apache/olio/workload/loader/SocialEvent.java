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
import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.util.RandomUtil;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.loader.framework.ThreadResource;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SocialEvent loader.
 */
public class SocialEvent extends Loadable {

    public static final Date BASE_DATE = new Date(System.currentTimeMillis());

    private static final String STATEMENT = "insert into events " +
            "(title, description, telephone, " +
            "event_timestamp, event_date, summary, created_at, address_id, " +
            "total_score, num_votes, disabled, user_id, image_id, document_id) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String[] EVT_MINUTES = { "00", "15", "30", "45" };

    static Logger logger = Logger.getLogger(SocialEvent.class.getName());
    int id;

    String[] fields = new String[6];
    Date createdTimestamp;
    int[] ifields = new int[7];


    public String getClearStatement() {
        return "truncate table events";
    }

    public void prepare() {
		id = getSequence() + 1;
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        StringBuilder buffer = tr.getBuffer();
        fields[0] = RandomUtil.randomText(r, 20, 100); //title
        fields[1] = RandomUtil.randomText(r, 1024, 4096); // description
        fields[2] = RandomUtil.randomPhone(r, buffer); //phone
        DateFormat dateFormat = tr.getDateFormat(); // eventtimestamp
        String eventDate = dateFormat.format(
                    r.makeDateInInterval(BASE_DATE, 0, 540));
                    
        int eventHr = r.random(7, 21);
        String eventMin = EVT_MINUTES[r.random(0, 3)];  // eventtimestamp
        fields[3] = String.format("%s %02d:%s:00",
                                            eventDate, eventHr, eventMin);
        fields[4] = eventDate; // eventdate
        fields[5] = RandomUtil.randomText(r, 500, 1024); // summary
        
        createdTimestamp = r.makeDateInInterval( //createdtimestamp
                BASE_DATE, -540, 0);
        
        ifields[0] = r.random(1, ScaleFactors.users);
        ifields[1] = 0;
        ifields[2] = 0;
        ifields[3] = 1;
        ifields[4] = r.random(1, ScaleFactors.users);
        ifields[5] = id;
        ifields[6] = id;

        // The rest is initialized to 0 anyway, leave it that way.
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            for (int i = 0; i < fields.length; i++)
                if (fields[i] != null)
                    s.setString(i + 1, fields[i]);
                else
                    s.setNull(i + 1, Types.VARCHAR);
            s.setDate(7, createdTimestamp);
            for (int i = 0; i < ifields.length; i++)
                s.setInt(8 + i, ifields[i]);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
