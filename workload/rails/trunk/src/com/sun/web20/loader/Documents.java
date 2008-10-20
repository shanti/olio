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
public class Documents extends Loadable {
    // We use on average of 10 comments per event. Random 0..20 comments..

    private static final String STATEMENT = "insert into documents " +
            "(size, content_type, filename, id) " + 
            "values (?, ?, ?, ?)";

    static Logger logger = Logger.getLogger(Comments.class.getName());

    int eventId;

    public Documents(int eventId) {
        this.eventId = ++eventId;
    }

    public String getClearStatement() {
        return "truncate table documents";
    }

    public void prepare() {
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setInt(1, 129585);
            s.setString(2, "application/pdf");
            s.setString(3, "e" + eventId + "l.pdf");
            s.setInt(4, this.eventId);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}