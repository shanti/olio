/* Copyright ?? 2008 Sun Microsystems, Inc. All rights reserved
 *
 * Use is subject to license terms.
 *
 * $Id: Friends.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package com.sun.web20.loader;

import com.sun.faban.driver.util.Random;
import com.sun.web20.util.ScaleFactors;
import com.sun.web20.util.UserName;
import com.sun.web20.loader.framework.Loadable;
import com.sun.web20.loader.framework.ThreadConnection;
import com.sun.web20.loader.framework.ThreadResource;

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

    private static final String STATEMENT = "insert into PERSON_PERSON " +
            "(Person_username, friends_username,is_accepted) values (?, ?, ?)";

    static Logger logger = Logger.getLogger(Friends.class.getName());

    int id;
    String userName;
    String[] friends;

    public Friends(int id) {
        this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table PERSON_PERSON";
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        userName = UserName.getUserName(id);
        int count = r.random(2, 28);

        LinkedHashSet<Integer> friendSet = new LinkedHashSet<Integer>(count);
        for (int i = 0; i < count; i++) {
            int friendId;
            do { // Prevent friend to be the same user.
                friendId = r.random(1, ScaleFactors.users);
            } while (friendId == id || !friendSet.add(friendId));
        }

        friends = new String[friendSet.size()];
        int idx = 0;
        for (int friendId : friendSet)
            friends[idx++] = UserName.getUserName(friendId);
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            int alternate = 0;
            for (String friend : friends) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setString(1, userName);
                s.setString(2, friend);
                if (alternate%2 == 0){
                    s.setInt(3, 0);
                }else{
                    s.setInt(3, 1);
                }
                alternate++;
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
