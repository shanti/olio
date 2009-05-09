package org.apache.olio.workload.loader;

import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.Loader;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.util.ScaleFactors;

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
    private static final String STATEMENT = "insert into SOCIALEVENTTAG " +
            "(socialeventtagid, tag, refcount) values (?, ?, ?)";

    static Logger logger = Logger.getLogger(Tag.class.getName());

    int id;
    String tag;

    public Tag(int id) {
        this.id = ++id;
    }

    public String getClearStatement() {
        return "truncate table SOCIALEVENTTAG ";
    }

    public void prepare() {
        tag = UserName.getUserName(id);
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setInt (1, id);
            s.setString(2, tag);
            s.setInt(3, 0); // Initialize it to 0 first, count and add later.
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
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            c.prepareStatement("update SOCIALEVENTTAG set refcount = " +
                    "(select count(*) from SOCIALEVENTTAG_SOCIALEVENT " +
                    "where socialeventtagid = " +
                    "SOCIALEVENTTAG.socialeventtagid)");
            c.executeUpdate();
            //update id
            
            logger.info("updating Tag ID_GEN ID");
             /* 
             c.prepareStatement("update ID_GEN set GEN_VALUE = " + 
                    "(select count(*) +1 from SOCIALEVENTTAG) " +
                    "where GEN_KEY='SOCIAL_EVENT_TAG_ID'"); 
             c.executeUpdate();
             */                        
             c.prepareStatement("INSERT INTO ID_GEN " +
                    "(GEN_KEY, GEN_VALUE) " +
                    "VALUES ('SOCIAL_EVENT_TAG_ID', "+ ScaleFactors.tagCount + ")");
             c.executeUpdate();
             
            logger.info("After updating Tag ID_GEN ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }


    }
}
