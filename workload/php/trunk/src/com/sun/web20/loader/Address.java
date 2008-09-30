/* Copyright © 2008 Sun Microsystems, Inc. All rights reserved
 *
 * Use is subject to license terms.
 *
 * $Id: Address.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package com.sun.web20.loader;

import com.sun.faban.driver.util.Random;
import com.sun.web20.util.RandomUtil;
import com.sun.web20.loader.framework.Loadable;
import com.sun.web20.loader.framework.ThreadConnection;
import com.sun.web20.loader.framework.ThreadResource;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Address loader.
 */
public class Address extends Loadable {

    private static final String STATEMENT = "insert into ADDRESS (street1, " +
            "street2, city, state, zip, country, latitude, longitude)" +
            "values (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String[] STREETEXTS = { "Blvd", "Ave", "St", "Ln", "" };
    static Logger logger = Logger.getLogger(Address.class.getName());

    String[] fields = new String[8];

    public String getClearStatement() {
        return "truncate table ADDRESS";
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        StringBuilder buffer = tr.getBuffer();
        buffer.append(r.makeNString(1, 5)).append(' '); // number
        RandomUtil.randomName(r, buffer, 1, 11); // street
        String streetExt = STREETEXTS[r.random(0, STREETEXTS.length - 1)];
        if (streetExt.length() > 0)
            buffer.append(' ').append(streetExt);

        fields[0] = buffer.toString();

        int toggle = r.random(0, 1); // street2
        if (toggle > 0)
            fields[1] = r.makeCString(5, 20);

        fields[2] = r.makeCString(4, 14); // city
        fields[3] = r.makeCString(2, 2).toUpperCase(); // state
        fields[4] = r.makeNString(5, 5);  // zip

        toggle = r.random(0, 1);
        if (toggle == 0) {
            fields[5] = "USA";
        } else {
            buffer.setLength(0);
            fields[5] = RandomUtil.randomName(r, buffer, 6, 16).toString();
        }
        // Latitude, we do not get addresses in polar circles. So the limit
        fields[6] = String.format("%.6f", r.drandom(-66.560556d, 66.560556d));

        fields[7] = String.format("%.6f", r.drandom(-179.999999d, 180d));
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            for (int i = 0; i < fields.length; i++)
                if (fields[i] != null)
                    s.setString(i + 1, fields[i]);
                else
                    s.setNull(i + 1, Types.VARCHAR);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
