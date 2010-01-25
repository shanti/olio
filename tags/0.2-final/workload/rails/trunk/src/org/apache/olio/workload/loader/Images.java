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

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Comments Loader.
 */
public abstract class Images extends Loadable {
    // We use on average of 10 comments per event. Random 0..20 comments..

    private static final String STATEMENT = "insert into images " +
            "(size, content_type, filename, height, width, thumbnail, id) " + 
            "values (?, ?, ?, ?, ?, ?, ?)";

    static Logger logger = Logger.getLogger(Comments.class.getName());

    int imageKey, imageId;
    String prefix;

    public String getClearStatement() {
        return "truncate table images";
    }

    public abstract void prepare();


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setInt(1, 671614);
            s.setString(2, "application/jpg");
            s.setString(3, prefix + imageId + ".jpg");
            s.setInt(4, 1280);
            s.setInt(5, 960);
            s.setString(6, prefix + imageId + "t.jpg");
            s.setInt(7, this.imageKey);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
