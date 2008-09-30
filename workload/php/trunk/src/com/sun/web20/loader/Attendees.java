/* Copyright © 2008 Sun Microsystems, Inc. All rights reserved
 *
 * Use is subject to license terms.
 *
 * $Id: Attendees.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package com.sun.web20.loader;

import com.sun.faban.driver.util.Random;
import com.sun.web20.util.ScaleFactors;
import com.sun.web20.util.UserName;
import com.sun.web20.loader.framework.Loadable;
import com.sun.web20.loader.framework.ThreadConnection;
import com.sun.web20.loader.framework.ThreadResource;

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
            "PERSON_SOCIALEVENT " +
            "(username, socialeventid) values (?, ?)";

    static Logger logger = Logger.getLogger(Attendees.class.getName());

    int eventId;
    String[] userNames;

    public String getClearStatement() {
        return "truncate table PERSON_SOCIALEVENT";
    }

    public Attendees(int eventId) {
        this.eventId = ++eventId;
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        int attendees = r.random(10, 100);
        LinkedHashSet<Integer> userIdSet = 
                                    new LinkedHashSet<Integer>(attendees);
        for (int i = 0; i <attendees; i++)
            while(!userIdSet.add(r.random(1, ScaleFactors.users)));

        userNames = new String[userIdSet.size()];
        int idx = 0;
        for (int userId : userIdSet)
            userNames[idx++] = UserName.getUserName(userId);
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (String userName : userNames) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
                s.setString(1, userName);
                s.setInt(2, eventId);
                c.addBatch();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
