package com.sun.web20.loader;

import com.sun.faban.driver.util.Random;
import com.sun.web20.util.ScaleFactors;
import com.sun.web20.util.UserName;
import com.sun.web20.loader.framework.Loadable;
import com.sun.web20.loader.framework.ThreadConnection;
import com.sun.web20.loader.framework.ThreadResource;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Comments Loader.
 */
public class Images extends Loadable {
    // We use on average of 10 comments per event. Random 0..20 comments..

    private static final String STATEMENT = "insert into images " +
            "(size, content_type, filename, height, width, thumbnail, id) " + 
            "values (?, ?, ?, ?, ?, ?, ?)";

    static Logger logger = Logger.getLogger(Comments.class.getName());

    int eventId;
    String prefix;

    public Images(int eventId, String prefix) {
        this.eventId = ++eventId;
        this.prefix = prefix;
    }

    public String getClearStatement() {
        return "truncate table images";
    }

    public void prepare() {
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setInt(1, 671614);
            s.setString(2, "application/jpg");
            s.setString(3, prefix + eventId + ".jpg");
            s.setInt(4, 1280);
            s.setInt(5, 960);
            s.setString(6, prefix + eventId + "t.jpg");
            s.setInt(7, this.eventId);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
