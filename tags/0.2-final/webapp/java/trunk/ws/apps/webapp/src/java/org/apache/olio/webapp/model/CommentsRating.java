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

/*
 *
 * Created on August 27, 2007, 3:04 PM
 *
 */

package org.apache.olio.webapp.model;

import org.apache.olio.webapp.util.WebappConstants;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.JoinColumn;

/**
 * Entity class CommentsRating
 * 
 * 
 * @author Binu John
 * @author Kim Lichong
 */
@Entity
@Table (name="COMMENTS_RATING")
public class CommentsRating implements Serializable {

    private SocialEvent socialEvent;
    private int commentsRatingId;
    private Person userName;
    private String comments;
    private int rating;
    private Timestamp creationTime;
    
    /**
     * Creates a new instance of CommentsRating
     */
    public CommentsRating() {
    }

    public CommentsRating (SocialEvent event, Person userName, String comments, int rating) {
        this.socialEvent = event;
        this.userName = userName;
        this.comments = comments;
        this.rating = rating;
        this.creationTime = new Timestamp(System.currentTimeMillis());
    }
    /**
     * Gets the id of this CommentsRating.
     * 
     * @return the id
     */
    // Very high allocationSize is set to work around a eclipseLink duplicate 
    // sequence generation issue faced under heavy load.
    @TableGenerator(name="COMMENTS_RATING_ID_GEN",
            table="ID_GEN",
            pkColumnName="GEN_KEY",
            valueColumnName="GEN_VALUE",
            pkColumnValue="COMMENTS_RATING_ID",
            allocationSize=20000)
    @GeneratedValue(strategy=GenerationType.TABLE,generator="COMMENTS_RATING_ID_GEN")
            
    @Id
    @Column (name="commentsid")
    public int getCommentsRatingId() {
        return this.commentsRatingId;
    }

    /**
     * Sets the id of this CommentsRating to the specified value.
     * 
     * @param id the new id
     */
    public void setCommentsRatingId(int id) {
        this.commentsRatingId = id;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return commentsRatingId;
    }

    /**
     * Determines whether another object is equal to this CommentsRating.  The result is 
     * <code>true</code> if and only if the argument is not null and is a CommentsRating object that 
     * has the same id field values as this object.
     * 
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CommentsRating)) {
            return false;
        }
        CommentsRating other = (CommentsRating)object;
        return commentsRatingId == other.getCommentsRatingId();
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "org.apache.olio.webapp.model.CommentRating[id=" + commentsRatingId + "]";
    }

    @Lob
    @Basic(fetch=FetchType.LAZY)
    public String getComments() {
        return comments;
    }
    
    public void setComments (String c) {
        comments = c;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @ManyToOne
    public SocialEvent getSocialEvent() {
        return socialEvent;
    }

    public void setSocialEvent(SocialEvent socialEvent) {
        this.socialEvent = socialEvent;
    }

    @OneToOne
    @JoinColumn(
         name = "USERNAME_USERNAME",
         referencedColumnName = "USERNAME",
         unique = true
    )
    public Person getUserName() {
        return userName;
    }

    public void setUserName(Person user) {
        this.userName = user;
    }
    
    @Transient
    public String getCommentString() {
        if (comments != null)
            return new String (comments);
        
        return null;
    }
    
    public void updateComments (String str) {
        comments = str;
    }
    
    /*
     *jsonobject.commentsratings[i].username) + "\">
     <img style=border-style:ridge; height=40px width=50px  
     src=\"/results/FileService.php?file="+escape(jsonobject.commentsratings[i].userimage)+"\">
     <br/></a></td><td style=padding:15px 0px 15px 0px;> <table> <tr> 
     <td>"+jsonobject.commentsratings[i].username + " says,  " + 
     jsonobject.commentsratings[i].comments + "<br/> 
     Rating: "+ jsonobject.commentsratings[i].ratings + 
     **/
    
    public String toJSON () {
        StringBuilder strb = new StringBuilder();
        strb.append ("{\"username\":\"");
        if (userName != null)
            strb.append(userName.getUserName());
        else
            strb.append("");
        strb.append("\",\"commentsRatingId\":\"");
        strb.append(commentsRatingId);
        strb.append("\",\"userimage\":\"");
        if (userName != null)
            strb.append(userName.getImageThumbURL());
        
        else
            strb.append("");
        strb.append("\",\"comments\":\"");
        if (comments != null)
            strb.append(comments);
        else
            strb.append("");
        strb.append("\",\"rating\":\"");
        strb.append(rating);
        strb.append("\",\"creationTime\":\"");
        strb.append(creationTime.toString());
        strb.append("\"}");
        
        return strb.toString();
    }
    
    @Transient
    public List<String> getRatingGraphic() {
        List<String> list = new ArrayList<String>(WebappConstants.MAX_RATING);
        
        for (int i=0; i<rating; i++) {
            list.add("images/star_on.png\" alt=\"16-star-hot\"");
        }
        for (int i=rating; i<WebappConstants.MAX_RATING; i++) {
            list.add("images/star_off.png\" alt=\"16-star-cold\"");
        }
        return list;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }
}
