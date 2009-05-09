package org.apache.olio.workload.loader;

import com.sun.faban.driver.util.Random;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.loader.framework.Loadable;
import org.apache.olio.workload.loader.framework.ThreadConnection;
import org.apache.olio.workload.loader.framework.ThreadResource;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.text.DateFormat;
import java.sql.Date;

/**
 * Comments Loader.
 */
public class Comments extends Loadable {
    // We use on average of 10 comments per event. Random 0..20 comments..

    private static final String STATEMENT = "insert into COMMENTS_RATING " +
            "(commentsid, username_username, socialevent_socialeventid, comments, rating, creationtime) values (?, ?, ?, ?, ?, ?)";

    static Logger logger = Logger.getLogger(Comments.class.getName());

    private static AtomicLong idGen = new AtomicLong(1);

    public static final Date BASE_DATE = new Date(System.currentTimeMillis());

    int eventId;
    String[] userNames;
    String[] comments;
    int[] ratings;
    String[] cDates;

    public Comments(int eventId) {
        this.eventId = ++eventId;
    }

    public static long getNextId() {
        return idGen.getAndIncrement();
    }

    public String getClearStatement() {
        return "truncate table COMMENTS_RATING";
    }

    public void prepare() {
        ThreadResource tr = ThreadResource.getInstance();
        Random r = tr.getRandom();
        int commentCount = r.random(0, 20);
        userNames = new String[commentCount];
        comments = new String[commentCount];
        ratings = new int[commentCount];
	cDates = new String[commentCount];

	DateFormat dateFormat = tr.getDateFormat();
	String cDate = dateFormat.format(
                    r.makeDateInInterval(BASE_DATE, 0, 540));
	int eventHr = r.random(7, 21);
	int eventMin = (int) r.random (0, 59);

        for (int i = 0; i < userNames.length; i++) {
            int userId = r.random(1, ScaleFactors.users);
            userNames[i] = UserName.getUserName(userId);
            comments[i] = r.makeCString(10, 1000);
            ratings[i] = r.random(2, 5);
            cDates[i] = String.format("%s %02d:%s:00",
                                            cDate, eventHr, eventMin);
        }
    }


    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            for (int i = 0; i < userNames.length; i++) {
                PreparedStatement s = c.prepareStatement(STATEMENT);
	        s.setLong (1, getNextId());
                s.setString(2, userNames[i]);
                s.setInt(3, eventId);
                s.setString(4, comments[i]);
                s.setInt(5, ratings[i]);
		s.setString(6, cDates[i]);
                c.addBatch();
                
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            //LoadController.increaseErrorCount();
        }
    }
    
       /**
     * For address, update ID after all the data is loaded.
     * So we update the ID_GEN table at postload and add 1 to count.
     */
    public void postLoad() {
        ThreadConnection c = ThreadConnection.getInstance();
        try {
            //update id
            
            //bug exists in JPA where we are using one ID generator (address)
            //for now, update to a ridiculous high number to avoid duplicate key 
            //exceptions
            
            
             logger.info("updating Comments_Rating ID");
             /*
             c.prepareStatement("update ID_GEN set GEN_VALUE = " +
                    "(select count(*) + 1 from COMMENTS_RATING) " +
                    "where GEN_KEY='COMMENTS_RATING_ID'");
             c.executeUpdate();
             */ 
            
             logger.info("Updating Comments_Rating ID");
             
             c.prepareStatement("INSERT INTO ID_GEN " +
                    "(GEN_KEY, GEN_VALUE) " +
                    "VALUES ('COMMENTS_RATING_ID', "+ ScaleFactors.events +1 + ")");
             c.executeUpdate();
              
            
            
             logger.info("After updating Comments_Rating ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
           // LoadController.increaseErrorCount();
        }


    }

}
