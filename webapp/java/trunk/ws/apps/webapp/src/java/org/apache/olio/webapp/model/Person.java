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

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import java.text.SimpleDateFormat;
import org.apache.olio.webapp.util.WebappUtil;
import java.util.List;
import static org.apache.olio.webapp.controller.WebConstants.*;
import javax.persistence.CascadeType;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Sean Brydon
 * @author Binu John
 * @author Kim LiChong
 */
@NamedQueries(
{
    @NamedQuery(name = "getPostedEvents",
    query = "SELECT s FROM SocialEvent s WHERE s.submitterUserName = :submitter"),
    @NamedQuery(name = "getIncomingInvitations",
    query = "SELECT i FROM Invitation i WHERE i.candidate.userName = :candidate"),
    @NamedQuery(name = "getOutgoingInvitations",
    query = "SELECT i FROM Invitation i WHERE i.requestor.userName = :requestor")
}
)
        
@Entity
@Table (name="PERSON")
public class Person implements java.io.Serializable {
    
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String summary;
    private String telephone;
    private String email;
    private String imageURL;
    private String imageThumbURL;
    private String timezone;
    private Collection<Person> friends=new ArrayList<Person>();
    private Collection<SocialEvent> socialEvents=new ArrayList<SocialEvent>();
    private Collection<Invitation> incomingInvitations=new ArrayList<Invitation>();
    private Collection<Invitation> outgoingInvitations = new ArrayList<Invitation>();
    private Address address;
    //used for UI display purposes
    private boolean hasReceivedInvitation;
    private Collection<Person> nonFriendList = new ArrayList<Person>();
    private int friendshipRequests;
    
    
    
    //private Collection<SocialEvent> planToAttendEvents=new Vector<SocialEvent>();
    //private Address location; //do I need some location info for viewing events listing or is it stored as cookie?
    //private String imageThumbURL; //Need a list of images?
    
    public Person() { }
    public Person(String userName, String password, String firstName, String lastName, String summary, String email,
            String telephone, String imageURL, String imageThumbURL, String timezone, Address address){
        
        this.userName = userName;
        this.password=password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.summary = summary;
        this.email = email;
        this.telephone = telephone;
        this.imageURL = imageURL;
        this.imageThumbURL = imageThumbURL;
        this.timezone = timezone;
        this.address = address;
    }
    
    @Id
    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    
    @Lob
    public String getSummary(){
        return summary;
    }
    public String getEmail(){
        return email;
    }
    public String getTelephone(){
        return telephone;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getImageThumbURL() {
        return imageThumbURL;
    }
    public String getTimezone() {
        return timezone;
    }
    
    @Transient
    public int getFriendshipRequests() {
        return incomingInvitations.size();
    }

    
    
    /* The default fetch type for OneToOne is EAGER. However, for person, the
     * address is not required for listing attendees in a social event.
     * So we will set it to LAZY and only load it when required (in ModelFacade).
     * */
    @OneToOne(cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY)
    public Address getAddress() {
        return address;
    }
    
    @ManyToMany(fetch=FetchType.LAZY)
    public Collection<Person> getFriends() {
        return friends;
    }
    
    @ManyToMany(mappedBy = "attendees")
    public Collection<SocialEvent> getSocialEvents() {
        return socialEvents;
    }

    @OneToMany(mappedBy = "candidate", cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY)
    //@OneToMany(mappedBy = "candidate")
    public Collection<Invitation> getIncomingInvitations() {
        return incomingInvitations;
    }

    public void setIncomingInvitations(Collection<Invitation> invitations) {
        this.incomingInvitations = invitations;
    }

    @Transient
    public boolean isHasReceivedInvitation() {
        return hasReceivedInvitation;
    }
       

    public void setHasReceivedInvitation(boolean hasReceivedInvitation) {
        this.hasReceivedInvitation = hasReceivedInvitation;
    }

    
    @Transient
    public Collection<Person> getNonFriendList() {
        return nonFriendList;
    }

    public void setNonFriendList(Collection<Person> nonFriendList) {
        this.nonFriendList = nonFriendList;
    }
         
     @OneToMany(mappedBy="requestor", cascade={CascadeType.PERSIST})
    public Collection<Invitation> getOutgoingInvitations() {
        return outgoingInvitations;
    }

    public void setOutgoingInvitations(Collection<Invitation> outgoingInvitations) {
        this.outgoingInvitations = outgoingInvitations;
    }
    
    public void setSocialEvents(Collection<SocialEvent> socialEvents) {
        this.socialEvents=socialEvents;
    }
    public void setFriends(Collection<Person> friends) {
        this.friends=friends;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public void setTelephone(String telephone){
        this.telephone=telephone;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public void setImageThumbURL(String imageThumbURL) {
        this.imageThumbURL = imageThumbURL;
    }
    public void setTimezone(String timezone) {
        this.timezone=timezone;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    
    
    
    /**
     * This method checks to make sure the class values are valid
     *
     * @return Message(s) of validation errors or and empty array (zero length) if class is valid
     */
    public String[] validateWithMessage() {
        ArrayList<String> valMess=new ArrayList<String>();
        if(firstName == null || firstName.equals("")) {
            valMess.add(WebappUtil.getMessage("invalid_first_name"));
        }
        return valMess.toArray(new String[valMess.size()]);
    }
    
    /**
     * @return String  contains JSON representation of person data,
     *                 inlcuding a list of friends' user NAMES but not
     *                 all the other person info of a friend.
     *                 This is NOT all the person data, it leaves off some data
     *                 such as details of friends and posted events and objects
     *                 it has relationships with, else whole object graph too big.
     *                 It is NOT all data, since cleint view only uses some
     *   for example { "person": { "userName": "sean", "firstName": "sean",
     *                "lastName": "brydon", "summary": "%26nbsp%3Bbio",
     *                "friends": ["inder", "mark", "yuta", "greg"]
     *                "postedEvents": [{"socialEventId": "1001", "title": "BluePrints Party", "date": "04-25-2007"},
     *                                 {"socialEventId": "1002", "title": "JavaOne", "date": "05-08-2007"}]
     *                 }}
     */
    public String toJson() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\"person\":{ ");
        sb.append("\"userName\":\"");
        sb.append(WebappUtil.encodeJSONString(userName));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"firstName\":\"");
        sb.append(WebappUtil.encodeJSONString(firstName));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"lastName\":\"");
        sb.append(WebappUtil.encodeJSONString(lastName));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"summary\":\"");
        sb.append(WebappUtil.encodeJSONString(summary));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"email\":\"");
        sb.append(WebappUtil.encodeJSONString(email));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"telephone\":\"");
        sb.append(WebappUtil.encodeJSONString(telephone));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"imageURL\":\"");
        sb.append(WebappUtil.encodeJSONString(imageURL));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"imageThumbURL\":\"");
        sb.append(WebappUtil.encodeJSONString(imageThumbURL));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"address\":\"");
        sb.append(WebappUtil.encodeJSONString(address.toString()));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        sb.append("\"timezone\":\"");
        sb.append(WebappUtil.encodeJSONString(timezone));
        sb.append(DOUBLE_QUOTE);
        sb.append(COMMA);
        
        //REMOVE soon, just support old page
        //get username of all this person's friends
        sb.append("\"friendNames\":[");
        if (getFriends().size() > 0) {
            for (Person p : getFriends()) {
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(p.getUserName()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        sb.append(COMMA);
        //end old junk to remove
        
        //get username of all this person's friends
        sb.append("\"friends\":[");
        if (getFriends().size() > 0) {
            for (Person p : getFriends()) {
                sb.append("{ ");
                sb.append("\"name\":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(p.getUserName()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                sb.append("\"imageThumbURL\":\"");
                sb.append(WebappUtil.encodeJSONString(p.getImageThumbURL()));
                sb.append(DOUBLE_QUOTE);
                sb.append("}");
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        
        //get list of all events posted by person, just titles and dates, not all event info
        boolean addedItems=false;
        sb.append(COMMA);
        sb.append( "\"postedEvents\":[");
        if (getSocialEvents().size() > 0) {
            for (SocialEvent event : getSocialEvents()) {
                if(event.getSubmitterUserName().equals(userName)) {
                    addedItems=true;
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
            }
            if(addedItems) {
                sb.deleteCharAt(sb.length()-1);//remove last space
                sb.deleteCharAt(sb.length()-1);//remove last comma
            }
        }
        sb.append("]");
        
        //get list of all events the person is attending, just titles and dates, not all event info
        sb.append(COMMA);
        sb.append( "\"attendEvents\":[");
        if (getSocialEvents().size() > 0) {
            for (SocialEvent event : getSocialEvents()) {
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
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        
        sb.append("}}");
        return sb.toString();
    }
    
    @Transient
    public String getPersonAsJson() {
        return toJson();
    }
    
    // Copied from toJson. TODO - cleanup
    @Transient
    public String getFriendsAsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"friendsList\":[");
        if (getFriends().size() > 0) {
            for (Person p : getFriends()) {
                sb.append("{");
                sb.append(DOUBLE_QUOTE);
                sb.append("name");
                sb.append(DOUBLE_QUOTE);
                sb.append(":");
                sb.append(DOUBLE_QUOTE);
                sb.append(WebappUtil.encodeJSONString(p.getUserName()));
                sb.append(DOUBLE_QUOTE);
                sb.append(COMMA);
                sb.append(DOUBLE_QUOTE);
                sb.append("imageURL");
                sb.append(DOUBLE_QUOTE);
                sb.append(":");
                sb.append(DOUBLE_QUOTE);
                if (p.getImageThumbURL() != null)
                    sb.append(WebappUtil.encodeJSONString(p.getImageThumbURL()));
                sb.append(DOUBLE_QUOTE);
                sb.append("}");
                sb.append(COMMA);
            }
            sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        sb.append("}");
        
        return sb.toString();
    }
    
    // Copied from toJson. TODO - fix it
    @Transient
    public String getAttendEventsAsJson() {
        return ModelFacade.eventsToJson(getSocialEvents());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person)object;
        return this.userName.equals(other.getUserName());
    }

   

    public String getOutgoingInvitationsAsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"outgoingInvitations\":[");
        if (getOutgoingInvitations().size() > 0) {
           for (Invitation inv : getOutgoingInvitations()){
               sb.append("{");
               sb.append(DOUBLE_QUOTE);
               sb.append("name");
               sb.append(DOUBLE_QUOTE);
               sb.append(":");
               sb.append(DOUBLE_QUOTE);
               sb.append(WebappUtil.encodeJSONString(inv.getCandidate().getUserName()));
               sb.append(DOUBLE_QUOTE);
               sb.append(COMMA);
               sb.append(DOUBLE_QUOTE);
               sb.append("fullname");
               sb.append(DOUBLE_QUOTE);
               sb.append(":");
               sb.append(DOUBLE_QUOTE);
               sb.append(WebappUtil.encodeJSONString(inv.getCandidate().getFirstName() + " " + inv.getCandidate().getLastName()));
               sb.append(DOUBLE_QUOTE);
               sb.append("}");
           }
           sb.deleteCharAt(sb.length()-1);//remove last space
            sb.deleteCharAt(sb.length()-1);//remove last comma
        }
        sb.append("]");
        sb.append("}");

        return sb.toString();
    }

}
