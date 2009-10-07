 /*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olio.webapp.model;

import static org.apache.olio.webapp.controller.WebConstants.*;
import org.apache.olio.webapp.controller.WebConstants;
import org.apache.olio.webapp.util.ServiceLocator;
import org.apache.olio.webapp.util.WebappConstants;
import org.apache.olio.webapp.util.WebappUtil;
import org.apache.olio.webapp.util.fs.FileSystem;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.transaction.UserTransaction;

//import for mysql constraint Exception
//import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
/**
 * Facade for all model manipulations.
 * 
 * @author Mark Basler
 * @author Binu John
 * @author Kim Lichong
 * 
 */
public class ModelFacade implements ServletContextListener {

    @PersistenceUnit(unitName = "BPWebappPu")
    private EntityManagerFactory emf;
    @Resource
    private UserTransaction utx;
    private static final boolean bDebug = true;
    private ServletContext context;
    private static Calendar calendar;
    private static boolean jmakiUsage = true;
    private String tagCloudStr = null;
    private boolean useCache = true;
    private String artifactPath = null;
    private Logger logger = Logger.getLogger(ModelFacade.class.getName());

    /** Creates a new instance of ModelFacade */
    public ModelFacade() {
        calendar = GregorianCalendar.getInstance();

    /*
    try {
    FileSystem fs = ServiceLocator.getInstance().getFileSystem();
    if (fs.isLocal())
    artifactPath = "/artifacts";
    else
    artifactPath = this.getContext().getContextPath() + "/access-artifacts";
    } catch (IOException ex) {
    Logger.getLogger(ModelFacade.class.getName()).log(Level.SEVERE, null, ex);
    artifactPath = this.getContext().getContextPath() + "/access-artifacts";
    }
     */
    }

    public void contextDestroyed(ServletContextEvent sce) {
        //close the factory and all entity managers associated with it
        if (emf.isOpen()) {
            emf.close();
        }
    }

    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        getContext().setAttribute(WebConstants.MF_KEY, this);
        WebappUtil.setContext(getContext().getContextPath());

        ServiceLocator sloc = ServiceLocator.getInstance();
        jmakiUsage = Boolean.parseBoolean(sloc.getString("webapp.jmakiUsage", "false"));
        useCache = Boolean.parseBoolean(sloc.getString("useLocalCache", "true"));
    }

    //public String addPerson(Person person, UserSignOn userSignOn){
    public String addPerson(Person person) {
        EntityManager em = emf.createEntityManager();
        try {
            utx.begin();
            em.joinTransaction();
            em.persist(person);
            utx.commit();
        } catch (Exception exe) {
            try {
                utx.rollback();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Rollbak Exception: ", exe);
                throw new RuntimeException("Error persisting person : ", exe);
            } finally {
                em.close();
            }

        }
        return person.getUserName();
    }

    public int addInvitation(Person loggedInUser, Invitation invitation) {
        EntityManager em = emf.createEntityManager();
        Person friend = em.find(Person.class, invitation.getCandidate().getUserName());
        try {
            utx.begin();
            logger.finer("Before: size of outgoingInvitations for loggedInUser " + loggedInUser.getOutgoingInvitations().size());

            loggedInUser.getOutgoingInvitations().add(invitation);
            logger.finer("After:  size of outgoingInvitations for loggedInUser " + loggedInUser.getOutgoingInvitations().size());

            //need to sync for candidate.incomingInvitations?
            logger.finer("Before: size of incomingInvitations for friend " + friend.getIncomingInvitations().size());

            friend.getIncomingInvitations().add(invitation);
            logger.finer("After: size of incomingInvitations for friend " + friend.getIncomingInvitations().size());

            logger.finer("**** ModelFacade::addFriend about to merge, person username=" + loggedInUser.getUserName() +
                    " and friend username=" + friend.getUserName());
            em.joinTransaction();
            loggedInUser = em.merge(loggedInUser);
            em.merge(friend);
            //Do we need to persist invite too?
            em.persist(invitation);
            utx.commit();
            logger.finer("after committing - the size of the outgoing is " + loggedInUser.getOutgoingInvitations().size());
        } catch (Exception exe) {
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            logger.severe("Rollbak Exception: " + exe);
            throw new RuntimeException("Error persisting invitation: ", exe);
        } finally {
            em.close();
        }
        return invitation.getInvitationID();
    }

    public Person findPerson(String userName) {
        EntityManager em = emf.createEntityManager();
        Person p = null;
        try {
            p = em.find(Person.class, userName);
            return p;
        } catch (Exception e) {
        } finally {
            em.close();
        }
        return p;
    }

    /**
     * This method is used when you search for users
     */
    @SuppressWarnings("unchecked")
    public List<Person> searchUsers(String query, int maxResults) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT i FROM Person i WHERE i.userName LIKE " + "\'" + query + "%\'" + " ORDER BY i.userName DESC");
        //logger.finer("the query performed is "+ "SELECT i FROM Person i WHERE i.userName LIKE " + "\'" + query +"%\'" +  " ORDER BY i.userName DESC");
        if (maxResults > 0) {
            q.setMaxResults(maxResults);
        }
        List<Person> foundUsers = q.getResultList();
        em.close();

        return foundUsers;
    }

    @SuppressWarnings("unchecked")
    public List<String> getFriendsUsername(String username) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT i.friends_USERNAME FROM PERSON_PERSON i where i.PERSON_USERNAME=\'" + username + "\'");
        List<String> friendsUsernames = q.getResultList();
        em.close();

        return friendsUsernames;
    }

    public Invitation findInvitation(String requestorUsername, String candidateUsername) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("Select i FROM Invitation i where i.requestor.userName=\'" + requestorUsername + "\'" +
                " AND i.candidate.userName=\'" + candidateUsername + "\'");
        Invitation invite = (Invitation) q.getSingleResult();
        return invite;
    }

    @SuppressWarnings("unchecked")
    public List<Person> getAllPersons() {
        EntityManager em = emf.createEntityManager();
        List<Person> persons = em.createQuery("SELECT i FROM Person i").getResultList();
        em.close();
        return persons;
    }

    @SuppressWarnings("unchecked")
    public Person getPerson(String userName) {
        if (userName == null) {
            return null;
        }
        Person person = null;

        if (person == null) {
            EntityManager em = emf.createEntityManager();
            person = em.find(Person.class, userName);
            //Even though EclipseLink allows access of of Lazy fetched realtionships from detached
            // entities, other JPA implementations may not.
            // Manual loading of these relationships are done to support other implementations.
            // Making it eager fetch will cause cascade fetching.
            if (person != null) {
                person.getAddress();
                person.getFriends();
                person.getSocialEvents();
                person.getIncomingInvitations();
            }
            em.close();
        }

        return person;
    }

    @SuppressWarnings("unchecked")
    public Person updatePerson(Person person) {
        EntityManager em = emf.createEntityManager();
        try {
            utx.begin();
            em.merge(person);
            utx.commit();

        // Update the cache
        //Cache cache = CacheFactory.getCache("Person");
        //cache.put(person.getUserName(), person);
        } catch (Exception exe) {
            exe.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error updating rating", exe);
        } finally {
            em.close();
        }
        return person;
    }

    @SuppressWarnings("unchecked")
    public void deletePerson(String userName) {
        EntityManager em = emf.createEntityManager();

        try {
            //try with em.remove instead

            utx.begin();
            Person person = em.find(Person.class, userName);
            if (person == null) // Not a valid event
            {
                return;
            }
            em.remove(person);
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
            throw new RuntimeException("Error deleting person", e);
        } finally {
            em.close();
        }

    /*
    //Query q = em.createQuery("DELETE FROM Person i WHERE i.userName = :uname");
    q.setParameter("uname", userName);
    q.executeUpdate();
    utx.commit();
    } catch (Exception e) {
    try {
    utx.rollback();
    } catch (Exception ex) {
    }
    throw new RuntimeException("Error deleting person", e);
    } finally {
    em.close();
    }
    try {
    utx.begin();
    Query q = em.createQuery("DELETE FROM SocialEvent i WHERE i.socialEventID = :sid");
    // q.setParameter("sid", id);
    q.executeUpdate();
    utx.commit();
    } catch (Exception e) {
    try {
    utx.rollback();
    } catch (Exception ex) {
    }
    throw new RuntimeException("Error deleting person", e);
    } finally {
    em.close();
    }
     */
    }

    public void deleteEvent(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            utx.begin();
            SocialEvent event = em.find(SocialEvent.class, id);
            if (event == null) // Not a valid event
            {
                return;
            }
            List<SocialEventTag> tags = event.getTags();
            if (tags != null) {
                for (SocialEventTag tag : tags) {
                    tag.decrementRefCount();
                    if (tag.getRefCount() == 0) {
                        em.remove(tag);
                    }
                }
            }
            em.remove(event);
            utx.commit();
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
            throw new RuntimeException("Error deleting person", e);
        } finally {
            em.close();
        }

        // Reset tag cloud since it has been modified
        resetTagCloud();
    }

    public Person addFriend(String userName, String friendUserName) {
        Person person;
        EntityManager em = emf.createEntityManager();
        try {
            utx.begin();
            //need to add error checks if friend exists etc
            Person friend = em.find(Person.class, friendUserName);
            person = em.find(Person.class, userName);
            person.getFriends().add(friend);
            //logger.finer("**** ModelFacade::addFriend about to merge, person username=" + person.getUserName() +
            //              " and friend username=" + friend.getUserName());  
            person = em.merge(person);
            utx.commit();
        // Update the cache
        //Cache cache = CacheFactory.getCache("Person");
        //cache.put(person.getUserName(), person);

        /*
        for (Person f : person.getFriends()) {
        logger.finer("**** ModelFacade::addFriend AFTER MERGE, friend =" + f.getUserName());
        }
         **/
        } catch (Exception exe) {
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error updating person friend list", exe);
        } finally {
            em.close();
        }
        return person;
    }

    public SocialEvent addSocialEvent(SocialEvent socialEvent, String tags) {
        // Use the set to avoid duplicates
        HashSet hSet = new HashSet<String>();
        if (tags != null) {
            StringTokenizer stTags = new StringTokenizer(tags, " ");
            while (stTags.hasMoreTokens()) {
                String tagx = stTags.nextToken().trim().toLowerCase();
                if (tagx.length() == 0 || hSet.contains(tagx)) {
                    // The tag is a duplicate, ignore
                    logger.finer("Duplicate tag or empty tag -- " + tagx);
                    continue;
                }
                hSet.add(tagx);
            }
        }
        EntityManager em = emf.createEntityManager();

        // Create a single query that returns all the existing tags
        int size = hSet.size();
        Iterator<String> iter;
        Query q = null;
        if (size > 0) {
            StringBuilder strb = new StringBuilder("SELECT t FROM SocialEventTag t WHERE t.tag IN (");
            iter = hSet.iterator();
            int i = 0;
            while (iter.hasNext()) {
                String tag = iter.next();
                strb.append("'");
                strb.append(tag);
                strb.append("'");
                if (++i < size) {
                    strb.append(", ");
                }
            }
            strb.append(")");
            q = em.createQuery(strb.toString());
        }
        try {
            utx.begin();
            em.joinTransaction();
            em.persist(socialEvent);
            boolean needsMerge = false;

            List<SocialEventTag> ltags = null;
            if (q != null) {
                ltags = q.getResultList();
            }


            if (ltags != null) {
                for (SocialEventTag ptag : ltags) {
                    ptag.incrementRefCount();
                    socialEvent.addTag(ptag);
                    ptag.getSocialEvents().add(socialEvent);
                    em.merge(ptag);
                    needsMerge = true;
                    hSet.remove(ptag.getTag());
                }
            }

            iter = hSet.iterator();
            while (iter.hasNext()) {
                String tag = iter.next();
                SocialEventTag set = new SocialEventTag(tag);
                set.getSocialEvents().add(socialEvent);
                socialEvent.getTags().add(set);
                em.persist(set);
                needsMerge = true;
            }
            if (needsMerge) {
                em.merge(socialEvent);
            }
            utx.commit();
        } catch (Exception exe) {
            try {
                Collection<SocialEventTag> tgs = socialEvent.getTags();
                String tagstr = null;
                if (tgs != null) {
                    for (SocialEventTag set : tgs) {
                        tagstr = tagstr + set.getTag();
                    }
                    System.out.println("");
                }
                logger.log(Level.SEVERE, "Exception in addSE: " + exe.getMessage() +
                        "SE.id = " + socialEvent.getSocialEventID() +
                        "SE.tags = " + tagstr, exe);
                utx.rollback();
            //exe.printStackTrace();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error persisting Social Event: ", exe);
        } finally {
            em.close();
        }
        // Reset the tag cloud since it has been modified.
        resetTagCloud();
        return socialEvent;
    }

    public void deleteCommentFromSocialEvent(SocialEvent event, int commentId) {
        if (event.getComments() == null) {
            return;
        }
        // Find the comment first
        CommentsRating comment = null;
        for (CommentsRating cr : event.getComments()) {
            if (cr.getCommentsRatingId() == commentId) {
                comment = cr;
                break;
            }
        }
        if (comment == null) {
            logger.warning("Could not find comment with commentId = " + commentId);
            return;
        }
        event.getComments().remove(comment);
        comment.setSocialEvent(null);

        EntityManager em = emf.createEntityManager();
        try {
            utx.begin();
            em.joinTransaction();
            comment = em.merge(comment);
            em.merge(event);
            em.remove(comment);
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception ex) {
                logger.severe(ex.toString());
            }
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public SocialEvent getSocialEvent(int socialEventID) {
        EntityManager em = emf.createEntityManager();

        SocialEvent socialEvent = em.find(SocialEvent.class, socialEventID);

        // Since we have listed Adress, attendees, tags and comments as lazy, fetch them
        // within the same PersistenceContext.
        // These fetches are not required for EclipseLink. However, othe JPA 
        // implementations may not support the feature of accessing lazy fetched
        // items from detached objects.

        // Iterating through the list is a work around for a EclipseLink bug
        // Remove it when the bug has been fixed.

        if (socialEvent != null) {
            socialEvent.getAddress();
            Iterator<Person> iter = socialEvent.getAttendees().iterator();
            while (iter.hasNext()) {
                iter.next().getUserName();
            }
            Iterator<SocialEventTag> titer = socialEvent.getTags().iterator();
            while (titer.hasNext()) {
                titer.next().getTag();
            }
            Iterator<CommentsRating> citer = socialEvent.getComments().iterator();
            while (citer.hasNext()) {
                citer.next().getCommentString();
            }
        }
        em.close();



        return socialEvent;
    }

    private SocialEventTag getSocialEventTag(String sxTag) {
        EntityManager em = emf.createEntityManager();
        try {
            return getSocialEventTag(sxTag, em);
        } finally {
            em.close();
        }
    }

    public SocialEventTag getSocialEventTag(String sxTag, EntityManager em) {
        SocialEventTag tag = null;

        List<SocialEventTag> tags = em.createQuery("SELECT t FROM SocialEventTag t WHERE t.tag = :tag").setParameter("tag", sxTag).getResultList();
        if (tags != null && !tags.isEmpty()) {
            tag = tags.get(0);
        }
        return tag;
    }

    /* Marked for DELETE -- UNUSED
    @SuppressWarnings("unchecked")
    public SocialEventTag addSocialEventTag(String sxTag) {
    EntityManager em = emf.createEntityManager();
    SocialEventTag tag = null;
    try {
    List<SocialEventTag> tags = em.createQuery("SELECT t FROM SocialEventTag t WHERE t.tag = :tag").setParameter("tag", sxTag).getResultList();
    if (tags.isEmpty()) {
    // need to create tag and set flag to add reference item
    tag = new SocialEventTag(sxTag);
    // persist data
    utx.begin();
    em.joinTransaction();
    em.persist(tag);
    utx.commit();
    } else {
    // see if item already exists in tag
    tag = tags.get(0);
    }

    } catch (Exception exe) {
    try {
    utx.rollback();
    } catch (Exception e) {
    }
    System.out.println("Exception - " + exe);
    exe.printStackTrace();
    throw new RuntimeException("Error persisting tag", exe);
    } finally {
    em.close();
    }
    return tag;
    }
     * */

    // Manage the tags
    public SocialEvent updateSocialEvent(SocialEvent event, String tags) {
        String oldTags = event.getTagsAsString();
        if ((tags == null && oldTags == null) ||
                (tags != null && oldTags != null && tags.equalsIgnoreCase(oldTags))) {
            return updateSocialEvent(event);
        // The tags can be either new or overlapping.
        // New tags need to be added and overlapping tags maintained.
        // Exisitng tags that is not in the new list need to be deleted.
        }
        List<SocialEventTag> existingTags = event.getTags();
        List<SocialEventTag> deleteTags = new ArrayList<SocialEventTag>();
        List<SocialEventTag> updateTags = new ArrayList<SocialEventTag>();
        List<String> tagStrings = new ArrayList<String>();

        if (tags != null) {
            tags = tags.trim();
        }
        if (tags == null || tags.length() == 0) {
            for (SocialEventTag stag : existingTags) {
                if (stag.getRefCount() <= 1) {
                    deleteTags.add(stag);
                } else {
                    updateTags.add(stag);
                }
            }
            existingTags.clear();
        } else {
            // Iterate over the tags
            StringTokenizer strtok = new StringTokenizer(tags);
            while (strtok.hasMoreTokens()) {
                String tok = strtok.nextToken();
                tagStrings.add(tok);
            }

            Iterator<SocialEventTag> iter = existingTags.iterator();
            while (iter.hasNext()) {
                SocialEventTag setag = iter.next();
                boolean tagExists = false;
                for (int i = 0; i < tagStrings.size(); i++) {
                    String tagS = tagStrings.get(i);
                    if (setag.getTag().equalsIgnoreCase(tagS)) {
                        tagExists = true;
                        tagStrings.remove(i);
                        break;
                    }
                }
                if (!tagExists) {
                    iter.remove();
                    if (setag.getRefCount() > 1) {
                        updateTags.add(setag);
                    } else {
                        deleteTags.add(setag);
                    }
                }
            }
        }
        EntityManager em = emf.createEntityManager();
        // TO DO -- Look into bulk updates to improve performance
        try {
            utx.begin();
            em.joinTransaction();
            // Add the new tags
            SocialEventTag sTag;
            for (String tagStr : tagStrings) {
                sTag = new SocialEventTag(tagStr);
                sTag.getSocialEvents().add(event);
                em.persist(sTag);
                event.addTag(sTag);
            }
            // Update the tags
            for (SocialEventTag eTag : updateTags) {
                eTag.decrementRefCount();
                em.merge(eTag);
            }
            event = em.merge(event);

            //Delete the tags
            for (SocialEventTag eTag : deleteTags) {
                eTag.setSocialEvents(null);
                eTag = em.merge(eTag);
                em.remove(eTag);
            }
            utx.commit();
        } catch (Exception exe) {
            logger.severe("Error updating social event: " + exe.toString());
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error updating social event: ", exe);
        } finally {
            em.close();
        }

        // Reset the tag cloud since it may have changed
        resetTagCloud();
        return event;

    }

    public SocialEvent updateSocialEvent(SocialEvent event) {
        EntityManager em = emf.createEntityManager();
        try {
            utx.begin();
            em.joinTransaction();
            event = em.merge(event);
            utx.commit();
        } catch (Exception exe) {

            logger.severe("updateSocialEvent failed: " + exe.toString());
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error updating social event", exe);
        } finally {
            em.close();
        }

        return event;
    }

    /*This method will retrieve social events by tag name     
     * The events will be returned in the order by event date, ascending, so the ones happening soonest are first
     */
    @SuppressWarnings("unchecked")
    public List<SocialEvent> getSocialEventsByTag(String tagName) {
        if (tagName == null) {
            return null;
        }
        SocialEventTag tag = getSocialEventTag(tagName);
        if (tag != null) {
            return tag.getSocialEvents();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<SocialEventTag> getSocialEventTags() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT t FROM SocialEventTag t ORDER BY t.refCount DESC");
        query.setMaxResults(WebappConstants.NUM_TAGS);

        List<SocialEventTag> tags = query.getResultList();
        return tags;
    }

    public String getTagCloud() {
        if (useCache && tagCloudStr != null) {
            return tagCloudStr;
        }
        tagCloudStr = WebappUtil.createTagCloud(getContext().getContextPath(), getSocialEventTags());
        return tagCloudStr;
    }

    public SocialEvent updateSocialEventRating(Person user, int eventId, int rating) {
        SocialEvent event = getSocialEvent(eventId);

        CommentsRating crating = getCommentRating(user, event);

        if (crating == null) {
            crating = new CommentsRating(event, user, null, rating);
            event.addComments(crating);
            event = updateSocialEvent(event);
        } else {
            //update the comment
            updateRating(crating, rating);
        }

        return event;
    }

    public SocialEvent updateSocialEventComment(Person user, int eventId, String comments) {
        return updateSocialEventComment(user, eventId, comments, 0);
    }

    public SocialEvent deleteSocialEventComment(SocialEvent event, CommentsRating cr) {
        Collection<CommentsRating> crs = event.getComments();
        if (crs == null) {
            return event;
        }
        crs.remove(cr);

        return updateSocialEvent(event);

    }

    public SocialEvent updateSocialEventComment(Person user, int eventId, String comments, int rating) {
        SocialEvent event = getSocialEvent(eventId);

        CommentsRating crating = getCommentRating(user, event);

        if (crating == null) {
            crating = new CommentsRating(event, user, comments, rating);
            event.addComments(crating);
            event = updateSocialEvent(event);
        } else {
            //update the comment
            updateComments(crating, comments);
        }

        return event;
    }

    public CommentsRating updateRating(CommentsRating cr, int rating) {
        cr.setRating(rating);

        EntityManager em = emf.createEntityManager();
        try {
            // persist data
            utx.begin();
            em.joinTransaction();
            cr = em.merge(cr);
            utx.commit();

        } catch (Exception exe) {
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error persisting tag: ", exe);
        } finally {
            em.close();
        }

        return cr;
    }

    public CommentsRating updateComments(CommentsRating cr, String comment) {
        cr.updateComments(comment);

        EntityManager em = emf.createEntityManager();
        try {
            // persist data
            utx.begin();
            em.joinTransaction();
            cr = em.merge(cr);
            utx.commit();

        } catch (Exception exe) {
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error persisting tag: ", exe);
        } finally {
            em.close();
        }

        return cr;
    }

    public CommentsRating getCommentRating(Person user, SocialEvent event) {
        if (event == null || user == null) {
            return null;
        // First check whether a comment is already present
        }
        CommentsRating crating = null;
        Collection<CommentsRating> existingComments = event.getComments();
        for (CommentsRating cr : existingComments) {
            Person p = cr.getUserName();
            if (p != null && p.getUserName().equals(user.getUserName())) {
                crating = cr;
                break;
            }
        }

        return crating;
    }

    public List<SocialEvent> getUpcomingEvents(Person p, int max) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("eventsPerPage", max);
        return getUpcomingEvents(p, map);
    }

    public List<SocialEvent> getUpcomingEvents(Person p, Map<String, Object> qMap) {
        int startIndex = 0, eventsPerPage = WebappConstants.ITEMS_PER_PAGE;
        Integer value = (Integer) qMap.get("startIndex");
        if (value != null) {
            startIndex = value;
        }
        value = (Integer) qMap.get("eventsPerPage");
        if (value != null) {
            eventsPerPage = value;
        }

        EntityManager em = emf.createEntityManager();
        try {
            //String qstr = "SELECT s FROM SocialEvent s, IN (s.attendees) a WHERE a.userName = :uname AND s.eventTimestamp >= CURRENT_TIMESTAMP ORDER BY s.eventTimestamp ASC";
            String qstr = "SELECT e FROM SocialEvent e JOIN e.attendees p WHERE p.userName= :uname AND e.eventTimestamp >= CURRENT_TIMESTAMP ORDER BY e.eventTimestamp ASC";
            Query q = em.createQuery(qstr);
            q.setParameter("uname", p.getUserName());
            q.setFirstResult(startIndex);
            if (eventsPerPage > 0) {
                q.setMaxResults(eventsPerPage);
            }
            q.setParameter("uname", p.getUserName());
            List<SocialEvent> events = q.getResultList();

            Long l = 0l;
            // We need a count of the events for paging.
            // However, this is not required if eventsPerPage is less than the ITEMS_PER_PAGE
            if (eventsPerPage >= WebappConstants.ITEMS_PER_PAGE) {
                if (events != null && events.size() == eventsPerPage) {
                    // Get the count for these events
                    qstr = "SELECT COUNT(e) FROM SocialEvent e JOIN e.attendees p WHERE p.userName= :uname";
                    q = em.createQuery(qstr);
                    q.setParameter("uname", p.getUserName());
                    l = (Long) q.getSingleResult();
                } else if (events != null) {
                    l = (long) events.size();
                }
            } else if (events != null) {
                l = (long) events.size();
            }
            qMap.put("listSize", l);
            return events;
        } finally {
            em.close();
        }
    }

    public List<SocialEvent> getPostedEvents(Person p) {
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createNamedQuery("getPostedEvents");
            q.setParameter("submitter", p.getUserName());
            List<SocialEvent> events = q.getResultList();
            return events;
        } finally {
            em.close();
        }
    }

    public String getPostedEventsAsJson(Person p) {
        Collection<SocialEvent> events = getPostedEvents(p);
        return eventsToJson(events);
    }

    public static String eventsToJson(Collection<SocialEvent> events) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"eventsList\":[");
        if (events != null && events.size() > 0) {
            for (SocialEvent event : events) {
                sb.append("{\"socialEventId\":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(String.valueOf(event.getSocialEventID())));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                sb.append("\"title\":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(event.getTitle()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                sb.append("\"date\":");
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm aa zzz");
                String formattedDate = formatter.format(event.getEventTimestamp());
                sb.append(DOUBLE_QUOTE);
                sb.append(formattedDate);
                sb.append(DOUBLE_QUOTE);
                sb.append("}");
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length() - 1);//remove last space

            sb.deleteCharAt(sb.length() - 1);//remove last comma

        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    /**
     * 
     * @param qMap - map holding the required query parameters
     * @return the result of the query. The numbers of items in the list is
     *         set in the qMap with the key "listSize"
     */
    public List<SocialEvent> getSocialEvents(Map<String, Object> qMap) {
        int day = 0, month = 0, year = 0, startIndex = 0, eventsPerPage = WebappConstants.ITEMS_PER_PAGE;
        int orderBy = WebappConstants.ORDER_BY_ASCENDING;
        String zip = null, orderType = null;

        orderType = (String) qMap.get("orderType");
        zip = (String) qMap.get("zip");

        Object value = qMap.get("day");
        if (value != null) {
            day = (Integer) value;
        }
        value = qMap.get("month");
        if (value != null) {
            month = (Integer) value;
        }
        value = qMap.get("year");
        if (value != null) {
            year = (Integer) value;
        }
        value = qMap.get("startIndex");
        if (value != null) {
            startIndex = (Integer) value;
        }
        value = qMap.get("eventsPerPage");
        if (value != null) {
            eventsPerPage = (Integer) value;
        }
        value = qMap.get("orderBy");
        if (value != null) {
            orderBy = (Integer) value;
        }
        StringBuilder qstrb = new StringBuilder();
        qstrb.append("SELECT i ");

        // strb is used to hold the qhere clause so that it can reused for the Count query
        StringBuilder strb = new StringBuilder();
        strb.append("FROM SocialEvent i WHERE ");
        boolean zipSet = false;
        if (zip != null && zip.trim().length() != 0) {
            strb.append("i.address.zip = :zip AND ");
            zipSet = true;
        }
        boolean dateSet = false;
        Timestamp lts = null;
        Timestamp uts = null;
        if (day > 0 && month > 0 && year > 0) {
            GregorianCalendar cal = new GregorianCalendar(year, month - 1, day, 0, 0);
            lts = new Timestamp(cal.getTimeInMillis());
            cal.add(Calendar.DATE, 1);
            uts = new Timestamp(cal.getTimeInMillis());
            strb.append("i.eventTimestamp >= :lts AND i.eventTimestamp < :uts ");
            dateSet = true;
        } else {
            strb.append("i.eventTimestamp >= CURRENT_TIMESTAMP ");
        }

        qstrb.append(strb);
        if (orderType != null && orderType.equals(WebappConstants.ORDER_CREATED)) {
            qstrb.append("ORDER BY i.createdTimestamp ");
        } else {
            qstrb.append("ORDER BY i.eventTimestamp ");
        }
        if (orderBy == WebappConstants.ORDER_BY_ASCENDING) {
            qstrb.append("ASC");
        } else {
            qstrb.append("DESC");
        }
        EntityManager em = emf.createEntityManager();
        try {
            // Create the select query

            Query q = em.createQuery(qstrb.toString());

            if (zipSet) {
                q.setParameter("zip", zip);
            }
            if (dateSet) {
                q.setParameter("lts", lts);
                q.setParameter("uts", uts);
            }

            q.setFirstResult(startIndex);
            if (eventsPerPage > 0) {
                q.setMaxResults(eventsPerPage);
            }
            List<SocialEvent> socialEvents = q.getResultList();

            // We need to get the number of items in the list if there are more items 
            // than thespecified eventsPerPage
            Long size = 0l;
            if (socialEvents != null && socialEvents.size() == eventsPerPage) {
                qstrb = new StringBuilder("SELECT COUNT(i) ");
                qstrb.append(strb);
                q = em.createQuery(qstrb.toString());

                if (zipSet) {
                    q.setParameter("zip", zip);
                }
                if (dateSet) {
                    q.setParameter("lts", lts);
                    q.setParameter("uts", uts);
                }
                size = (Long) q.getSingleResult();
            } else if (socialEvents != null) {
                size = (long) socialEvents.size();
            }

            qMap.put("listSize", size);
            return socialEvents;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception for query - " + qstrb.toString(), e);
        } finally {
            em.close();
        }
        return null;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public Person login(String user_name, String password) {
        if (user_name == null || password == null) {
            return null;
        }
        Person p = getPerson(user_name);

        if (p != null && password.equals(p.getPassword())) {
            return p;
        }
        return null;
    }

    public String getYearDropDown() {
        int year = calendar.get(Calendar.YEAR);
        return getCalendarDropDown("year", year - 5, year + 10, year);
    }

    public static String getYearDropDown(int year) {
        return getCalendarDropDown("year", year - 5, year + 10, year);
    }

    public String getMonthDropDown() {
        return getCalendarDropDown("month", 1, 12, calendar.get(Calendar.MONTH) + 1);
    }

    public static String getMonthDropDown(int value) {
        return getCalendarDropDown("month", 1, 12, value);
    }

    public String getDayDropDown() {
        return getCalendarDropDown("day", 1, 31, calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getDayDropDown(int value) {
        return getCalendarDropDown("day", 1, 31, value);
    }

    public String getHourDropDown() {
        return getCalendarDropDown("hour", 0, 23, calendar.get(Calendar.HOUR_OF_DAY));
    }

    public static String getHourDropDown(int value) {
        return getCalendarDropDown("hour", 0, 23, value);
    }

    public String getMinuteDropDown() {
        return getCalendarDropDown("minute", 0, 59, calendar.get(Calendar.MINUTE));
    }

    public static String getMinuteDropDown(int value) {
        return getCalendarDropDown("minute", 0, 59, value);
    }

    /*
     * min amd max inclusive
     * */
    public static String getCalendarDropDown(String id, int min, int max, int value) {
        StringBuilder strb = new StringBuilder("<select id=\"");
        strb.append(id);
        strb.append("\" name=\"");
        strb.append(id);
        strb.append("\">");
        for (int i = min; i <= max; i++) {
            strb.append("<option value=\"");
            strb.append(i);
            strb.append("\"");
            if (i == value) {
                strb.append(" selected=\"selected\"");
            }
            strb.append(">");
            strb.append(i);
            strb.append("</option>");
        }
        strb.append("</select>");
        return strb.toString();
    }

    public static boolean getJMakiUsage() {
        return jmakiUsage;
    }

    public ServletContext getContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    public void deleteInvitation(Invitation inv) {
        EntityManager em = emf.createEntityManager();
        String username = inv.getRequestor().getUserName();
        String candidateUsername = inv.getCandidate().getUserName();
        Person person = em.find(Person.class, username);
        person.getIncomingInvitations().remove(inv);
        try {
            utx.begin();
            em.joinTransaction();
            Invitation inv0 = em.merge(inv);
            em.remove(inv0);
            utx.commit();
        } catch (Exception exe) {
            //exe.printStackTrace();
            logger.log(Level.SEVERE, "Error deleting invitation: ", exe);
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error deleting invitation", exe);
        } finally {
            em.close();
        }
        logger.finer("after deleting invitation");

    }

    public Collection<Invitation> getIncomingInvitations(Person p) {
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createNamedQuery("getIncomingInvitations");
            q.setParameter("candidate", p.getUserName());
            Collection<Invitation> invites = q.getResultList();
            return invites;
        } finally {
            em.close();
        }
    }

    public Collection<Invitation> getOutgoingInvitations(Person p) {
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createNamedQuery("getOutgoingInvitations");
            q.setParameter("requestor", p.getUserName());
            Collection<Invitation> invites = q.getResultList();
            return invites;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteInvitation(Person loggedInUser, Invitation inv) {
        EntityManager em = emf.createEntityManager();
        Person requestor = null, candidate = null;
        boolean isIncoming = false;
        //check to see if outgoing or incoming invitation
        if (loggedInUser.getUserName().equalsIgnoreCase(inv.getRequestor().getUserName())) {
            //outgoing
            isIncoming = false;
            requestor = loggedInUser;
            candidate = em.find(Person.class, inv.getCandidate().getUserName());
        } else if (loggedInUser.getUserName().equalsIgnoreCase(inv.getCandidate().getUserName())) {
            //incoming
            isIncoming = true;
            requestor = em.find(Person.class, inv.getRequestor().getUserName());
            candidate = loggedInUser;
        }
        //if incoming, then requestor is the friend, and the candidate is the loggedInUser


        try {
            utx.begin();

            if (isIncoming) {
                logger.finer("before:  size of the requestor IncomingInv is " + candidate.getIncomingInvitations().size());
                logger.finer("before:  size of the candidate OutgoingInv is " + requestor.getOutgoingInvitations().size());
                candidate.getIncomingInvitations().remove(inv);
                requestor.getOutgoingInvitations().remove(inv);
                logger.finer("After:  size of the requestor IncomingInv is " + requestor.getIncomingInvitations().size());
                logger.finer("after:  size of the candidate OutgoingInv is " + candidate.getOutgoingInvitations().size());
            } else {
                requestor.getOutgoingInvitations().remove(inv);
                candidate.getIncomingInvitations().remove(inv);
            }

            //inv.setCandidate(null);
            //inv.setRequestor(null);
            em.joinTransaction();

            Invitation inv0 = em.merge(inv);
            //merge the two people
            em.merge(requestor);
            em.merge(candidate);
            em.remove(inv0);
            utx.commit();
        } catch (Exception exe) {
            logger.log(Level.SEVERE, "Error deleting invitation: ", exe);
            try {
                utx.rollback();
            } catch (Exception e) {
            }
            throw new RuntimeException("Error deleting invitation", exe);
        } finally {
            em.close();
        }
        logger.finer("after deleting invitation");
    }

    public void resetTagCloud() {
        tagCloudStr = null;
    }

    public String getArtifactPath() {
        return WebappUtil.getArtifactPath();

    }
}
