package com.sun.web20.loader;

import com.sun.web20.util.UserName;
import com.sun.web20.loader.framework.Loadable;
import com.sun.web20.loader.framework.Loader;
import com.sun.web20.loader.framework.ThreadConnection;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The tag loader.
 */
public class Tag extends Loadable {

    // Note that the tag id in the database is autoincrement and may
    // not coincide with this tag id/name when using multi-thread loading.
    private static final String STATEMENT = "insert into tags " +
            "(name, id) values (?, ?)";

    static Logger logger = Logger.getLogger(Tag.class.getName());

    int id;
    String tag;

    public Tag(int id) {
        this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table tags";
    }

    public void prepare() {
        tag = UserName.getUserName(id);
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setString(1, tag);
            s.setInt(2, id);
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Loader.increaseErrorCount();
        }
    }

    /**
     * For tags, we won't know the refcount till all the data is loaded.
     * So we update the table at postload.
     */
    public void postLoad() {
//        ThreadConnection c = ThreadConnection.getInstance();
//       try {
//            c.prepareStatement("update SOCIALEVENTTAG set refcount = " +
//                    "(select count(*) from SOCIALEVENTTAG_SOCIALEVENT " +
//                   "where socialeventtagid = " +
//                    "SOCIALEVENTTAG.socialeventtagid)");
//            c.executeUpdate();
//        } catch (SQLException e) {
//            logger.log(Level.SEVERE, e.getMessage(), e);
//        }


    }
}
