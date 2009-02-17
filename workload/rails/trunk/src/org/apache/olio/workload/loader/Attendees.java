package org.apache.olio.workload.loader;

import com.sun.faban.driver.util.Random;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.loader.framework.ThreadResource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Attendees Loader
 */
public class Attendees extends Loadable {
    // We use on average of 55 attendees per event. Random 10..100 Attendees.

    private static final String STATEMENT = "insert into " +
            "events_users" +
            "(user_id, event_id) values (?, ?)";

    static Logger logger = Logger.getLogger(Attendees.class.getName());

    int eventId;
    LinkedHashSet<Integer> userIdSet;

    public String getClearStatement() {
        return "truncate table events_users";
    }

    public Attendees(int eventId) {
        this.eventId = ++eventId;
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        int attendees = r.random(10, 100);
        userIdSet = new LinkedHashSet<Integer>(attendees);
        for (int i = 0; i <attendees; i++)
            while(!userIdSet.add(r.random(1, ScaleFactors.users)));
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (int userId : userIdSet) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setInt(1, userId);
                s.setInt(2, eventId);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
