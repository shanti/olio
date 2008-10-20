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

    private static final String STATEMENT = "insert into invites " +
            "(user_id, user_id_target, is_accepted) values (?, ?, 1)";

    static Logger logger = Logger.getLogger(Friends.class.getName());

    int id;
    int[] friends;

    public Friends(int id) {
        this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table invites";
    }

    public void prepare() {
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
