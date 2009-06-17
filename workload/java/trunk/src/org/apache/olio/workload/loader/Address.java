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
import org.apache.olio.workload.util.RandomUtil;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.loader.framework.ThreadResource;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Address loader.
 */
public class Address extends Loadable {

    private static final String STATEMENT = "insert into ADDRESS (addressid, street1, " +
            "street2, city, state, zip, country, latitude, longitude)" +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String[] STREETEXTS = { "Blvd", "Ave", "St", "Ln", "" };
    static Logger logger = Logger.getLogger(Address.class.getName());

    String[] fields = new String[8];

    private int id;

    public Address(int id) {
	this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table ADDRESS";
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        StringBuilder buffer = tr.getBuffer();
        buffer.append(r.makeNString(1, 5)).append(' '); // number
        RandomUtil.randomName(r, buffer, 1, 11); // street
        String streetExt = STREETEXTS[r.random(0, STREETEXTS.length - 1)];
        if (streetExt.length() > 0)
            buffer.append(' ').append(streetExt);

        fields[0] = buffer.toString();

        int toggle = r.random(0, 1); // street2
        if (toggle > 0)
            fields[1] = r.makeCString(5, 20);

        fields[2] = r.makeCString(4, 14); // city
        fields[3] = r.makeCString(2, 2).toUpperCase(); // state
        fields[4] = r.makeNString(5, 5);  // zip

        toggle = r.random(0, 1);
        if (toggle == 0) {
            fields[5] = "USA";
        } else {
            buffer.setLength(0);
            fields[5] = RandomUtil.randomName(r, buffer, 6, 16).toString();
        }
        // Latitude, we do not get addresses in polar circles. So the limit
        fields[6] = String.format("%.6f", r.drandom(-66.560556d, 66.560556d));

        fields[7] = String.format("%.6f", r.drandom(-179.999999d, 180d));
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
	    s.setInt (1, id);
            for (int i = 0; i < fields.length; i++)
                if (fields[i] != null)
                    s.setString(i + 2, fields[i]);
                else
                    s.setNull(i + 2, Types.VARCHAR);
            c.addBatch();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            //LoadController.increaseErrorCount();
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
             logger.info("Updating Address ID");
              
             c.prepareStatement("INSERT INTO ID_GEN " +
                    "(GEN_KEY, GEN_VALUE) " +
                    "VALUES ('ADDRESS_ID', "+ ScaleFactors.users+1  + ")");
             c.executeUpdate();
             
            /* 
             c.prepareStatement("update ID_GEN set GEN_VALUE = " +
                    "(select count(*) + 1 from ADDRESS) " +
                    "where GEN_KEY='ADDRESS_ID'");
             c.executeUpdate();
             */ 
             logger.info("After updating Address ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            //LoadController.increaseErrorCount();
        }


    }
}
