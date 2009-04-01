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

import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Friends loader
 */
public class Friends extends Loadable {
    // We use on average of 15 friends. Random 2..28 Friends.

    private static final String STATEMENT = "insert into invites " +
            "(user_id, user_id_target, is_accepted) values (?, ?, 1)";

    static Logger logger = Logger.getLogger(Friends.class.getName());

    int id;
    int[] friends;

    public String getClearStatement() {
        return "truncate table invites";
    }

    public void prepare() {
		id = getSequence() + 1;
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        int count = r.random(2, 28);

        LinkedHashSet<Integer> friendSet = new LinkedHashSet<Integer>(count);
        for (int i = 0; i < count; i++) {
            int friendId;
            do { // Prevent friend to be the same user.
                friendId = r.random(1, ScaleFactors.users);
            } while (friendId == id || !friendSet.add(friendId));
        }

        friends = new int[friendSet.size()];
        int idx = 0;
        for (int friendId : friendSet)
            friends[idx++] = friendId;
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (int friend : friends) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setInt(1, id);
                s.setInt(2, friend);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
