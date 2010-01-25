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
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Mark Basler
 * @author Binu John
 * @author Kim Lichong
 */
  
@Entity
@Table (name="SOCIALEVENTTAG")
public class SocialEventTag implements java.io.Serializable, Comparable {
    
    private int socialEventTagID;
    private List<SocialEvent> socialEvents= new ArrayList<SocialEvent>();
    private String tag;
    private int refCount=1;
    
    public SocialEventTag() {
    }
    
    public SocialEventTag(String tag) {
        this.tag=tag;
    }
   
    // Very high allocationSize is set to work around a eclipseLink duplicate 
    // sequence generation issue faced under heavy load.
    @TableGenerator(name="SOCIAL_EVENT_TAG_ID_GEN",
        table="ID_GEN",
        pkColumnName="GEN_KEY",
        valueColumnName="GEN_VALUE",
        pkColumnValue="SOCIAL_EVENT_TAG_ID",
        allocationSize=20000)
    @GeneratedValue(strategy=GenerationType.TABLE,generator="SOCIAL_EVENT_TAG_ID_GEN")
    @Id
    public int getSocialEventTagID() {
        return socialEventTagID;
    }
    public void setSocialEventTagID(int socialEventTagID) {
        this.socialEventTagID=socialEventTagID;
    }    
    
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag=tag;
    }
    
    public int getRefCount() {
        return refCount;
    }
    public void setRefCount(int refCount) {
        this.refCount=refCount;
    }
    public synchronized void incrementRefCount() {
        refCount++;
    }
    
    public synchronized void decrementRefCount() {
        refCount--;
    }
    
    @ManyToMany
    @JoinTable(name = "SOCIALEVENTTAG_SOCIALEVENT", joinColumns = @JoinColumn(name = "SOCIALEVENTTAGID", referencedColumnName = "SOCIALEVENTTAGID")
    , inverseJoinColumns = @JoinColumn(name = "SOCIALEVENTID", referencedColumnName = "SOCIALEVENTID")
    )
    public List<SocialEvent> getSocialEvents() {
        return socialEvents;
    }
    
    public void setSocialEvents(List<SocialEvent> socialEvents) {
        this.socialEvents=socialEvents;
    }
    
    public boolean socialEventExists(SocialEvent socialEvent) {
        return this.getSocialEvents().contains(socialEvent);
    }

    public int compareTo(Object o) {
        if (o == null || !(o instanceof SocialEventTag))
            return 0;
        
        SocialEventTag t = (SocialEventTag)o;
        return this.tag.compareTo(t.getTag());
    }
}



