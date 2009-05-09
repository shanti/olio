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

    private static final String STATEMENT = "insert into PERSON_PERSON " +
            "(Person_username, friends_username) values (?, ?)";

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
            for (String friend : friends) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setString(1, userName);
                s.setString(2, friend);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            //LoadController.increaseErrorCount();
        }
    }
}
