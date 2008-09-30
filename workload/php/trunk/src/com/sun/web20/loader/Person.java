/* Copyright © 2008 Sun Microsystems, Inc. All rights reserved
 *
 * Use is subject to license terms.
 *
 * $Id: Person.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package com.sun.web20.loader;

import com.sun.faban.driver.util.Random;
import com.sun.web20.util.UserName;
import com.sun.web20.util.RandomUtil;
import com.sun.web20.util.ScaleFactors;
import com.sun.web20.loader.framework.Loadable;
import com.sun.web20.loader.framework.ThreadConnection;
import com.sun.web20.loader.framework.ThreadResource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Person loader
 */
public class Person extends Loadable {

    private static final String STATEMENT = "insert into PERSON (username, " +
            "password, firstname, lastname, email, telephone, imageurl, " +
            "imagethumburl, summary, timezone, ADDRESS_addressid)" +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    static Logger logger = Logger.getLogger(Person.class.getName());

    int id;
    String[] fields = new String[10];
    int addressId;

    public Person(int id) {
        this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table PERSON";
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        StringBuilder b = tr.getBuffer();
        fields[0] = UserName.getUserName(id);
        fields[1] = String.valueOf(id);
        fields[2] = RandomUtil.randomName(r, b, 2, 12).toString();
        b.setLength(0);
        fields[3] = RandomUtil.randomName(r, b, 5, 15).toString();
        fields[4] = r.makeCString(3, 10);
        fields[4] = fields[2] + '_' + fields[3] + '@' + fields[4] + ".com";
        b.setLength(0);
        fields[5] = RandomUtil.randomPhone(r, b);
        fields[6] = "p" + id + ".jpg";
        fields[7] = "p" + id + "t.jpg";
        fields[8] = RandomUtil.randomText(r, 250, 2500);
        fields[9] = "PST";
        addressId = r.random(1, ScaleFactors.users);
    }

    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            for (int i = 0; i < fields.length; i++)
                s.setString(i + 1, fields[i]);
            s.setInt(11, addressId);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
