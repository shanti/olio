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

import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Friends loader
 */
public class Invitation extends Loadable {
    // follow same as friends.  Average of 15 invitations. Random 2..28 Friends.

    private static final String STATEMENT = "insert into INVITATION " +
            "(invitationid, isaccepted, requestor_username, candidate_username) values (?, ?, ?, ?)";
    

    static Logger logger = Logger.getLogger(Friends.class.getName());

    int id;
    String requestorUsername;    
    String[] friends;
    boolean isAccepted = false;
    static AtomicInteger aint=new AtomicInteger(1);

    public Invitation(int id) {
        this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table INVITATION";
    }
      

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        requestorUsername = UserName.getUserName(id);        
        int count = r.random(2, 10);

        LinkedHashSet<Integer> inviteeSet = new LinkedHashSet<Integer>(count);
        for (int i = 0; i < count; i++) {
            int friendId;
            do { // Prevent friend to be the same user.
                friendId = r.random(1, ScaleFactors.users);
            } while (friendId == id || !inviteeSet.add(friendId));
        }

        friends = new String[inviteeSet.size()];
        int idx = 0;
        for (int friendId : inviteeSet)
            friends[idx++] = UserName.getUserName(friendId);
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (String friend : friends) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setInt(1, aint.incrementAndGet());
                 s.setInt(2, 0);
                s.setString(3, requestorUsername);
                s.setString(4, friend);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            System.out.println("the exception is "+ e.getMessage());
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
            
            //bug exists in JPA where we are using one ID generator (address)
            //for now, update to a ridiculous high number to avoid duplicate key 
            //exceptions
            
            
             logger.info("Updating Invitation ID");
             c.prepareStatement("INSERT INTO ID_GEN " +
                    "(GEN_KEY, GEN_VALUE) " +
                    "VALUES ('INVITATION_ID', "+ ScaleFactors.users +1 + ")");
             c.executeUpdate();
            
            
            
             logger.info("After updating Invitation ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
           // LoadController.increaseErrorCount();
        }
    }
        
}
