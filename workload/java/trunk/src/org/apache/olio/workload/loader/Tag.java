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

import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.Loader;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.util.ScaleFactors;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The tag loader.
 */
public class Tag extends Loadable {

    // Note that the tag id in the database is autoincrement and may
    // not coincide with this tag id/name when using multi-thread loading.
    private static final String STATEMENT = "insert into SOCIALEVENTTAG " +
            "(socialeventtagid, tag, refcount) values (?, ?, ?)";

    static Logger logger = Logger.getLogger(Tag.class.getName());

    int id;
    String tag;

    public String getClearStatement() {
        return "truncate table SOCIALEVENTTAG";
    }

    public void prepare() {
        id = getSequence();
        ++id;
        tag = UserName.getUserName(id);
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setInt (1, id);
            s.setString(2, tag);
            s.setInt(3, 0); // Initialize it to 0 first, count and add later.
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Loader.increaseErrorCount();
        }
    }

    /**
     * For tags, we won't know the refcount till all the data is loaded.
     * So we update the table at postload.
     */
    public void postLoad() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            c.prepareStatement("update SOCIALEVENTTAG set refcount = " +
                    "(select count(*) from SOCIALEVENTTAG_SOCIALEVENT " +
                    "where socialeventtagid = " +
                    "SOCIALEVENTTAG.socialeventtagid)");
            c.executeUpdate();
            //update id
            
            logger.fine("updating Tag ID_GEN ID");
            c.prepareStatement("INSERT INTO ID_GEN " +
                    "(GEN_KEY, GEN_VALUE) " +
                    "VALUES ('SOCIAL_EVENT_TAG_ID', "+ ScaleFactors.tagCount + ")");
             c.executeUpdate();
             
            logger.fine("After updating Tag ID_GEN ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
