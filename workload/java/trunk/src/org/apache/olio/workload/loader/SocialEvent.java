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

    private static final String STATEMENT = "insert into SOCIALEVENT " +
            "(socialeventid, title, summary, description, submitterUserName, imageurl, " +
            "imagethumburl, literatureurl, telephone, " +
            "eventtimestamp, createdtimestamp, ADDRESS_addressid, " +
            "totalscore, numberofvotes, disabled) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String[] EVT_MINUTES = { "00", "15", "30", "45" };

    static Logger logger = Logger.getLogger(SocialEvent.class.getName());
    int id;

    String[] fields = new String[9];
    Date createdTimestamp;
    int[] ifields = new int[4];

    public SocialEvent(int id) {
        this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table SOCIALEVENT";
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        StringBuilder buffer = tr.getBuffer();
        fields[0] = RandomUtil.randomText(r, 15, 20); //title
        fields[1] = RandomUtil.randomText(r, 15, 50); //summary
        fields[2] = RandomUtil.randomText(r, 50, 495); // description
        fields[3] = UserName.getUserName(r.random(1, ScaleFactors.users));
        fields[4] = "e" + id + ".jpg"; // imageurl
        fields[5] = "e" + id + "t.jpg"; // imagethumburl
        fields[6] = "e" + id + "l.pdf"; //literatureurl
        fields[7] = RandomUtil.randomPhone(r, buffer); //phone
        DateFormat dateFormat = tr.getDateFormat(); // eventtimestamp
        String eventDate = dateFormat.format(
                    r.makeDateInInterval(BASE_DATE, 0, 540));
        int eventHr = r.random(7, 21);
        String eventMin = EVT_MINUTES[r.random(0, 3)];  // eventtimestamp
        fields[8] = String.format("%s %02d:%s:00",
                                            eventDate, eventHr, eventMin);
        createdTimestamp = r.makeDateInInterval( //createdtimestamp
                                        BASE_DATE, -540, 0);
        ifields[0] = r.random(1, ScaleFactors.users); // addressId
        // The rest is initialized to 0 anyway, leave it that way.
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setInt (1, id);
 	    int len=2;
            for (int i = 0; i < fields.length; i++,len++)
                if (fields[i] != null)
                    s.setString(i + 2, fields[i]);
                else
                    s.setNull(i + 2, Types.VARCHAR);
            s.setDate(len, createdTimestamp);
	    len++;
            for (int i = 0; i < ifields.length; i++,len++)
                s.setInt(len, ifields[i]);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            //LoadController.increaseErrorCount();
        }
    }

     /**
     * For tags, we won't know the refcount till all the data is loaded.
     * So we update the table at postload.
     */
    public void postLoad() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            //update id
             logger.info("before updating socialEventID");             
             
             c.prepareStatement("INSERT INTO ID_GEN " +
                    "(GEN_KEY, GEN_VALUE) " +
                    "VALUES ('SOCIAL_EVENT_ID', "+ ScaleFactors.events +1 + ")");
             c.executeUpdate();                          
             
            /* 
             c.prepareStatement("update ID_GEN set GEN_VALUE = " +
                    "(select count(*) +1 from SOCIALEVENT) " +
                    "where GEN_KEY='SOCIAL_EVENT_ID'");
             c.executeUpdate();
             */
             logger.info("after updating socialEventID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            //LoadController.increaseErrorCount();
        }


    }

}
