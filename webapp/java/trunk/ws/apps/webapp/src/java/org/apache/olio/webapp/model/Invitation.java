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

import java.io.Serializable;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.util.logging.Logger;

/**
 * Entity class Invitation
 * /

/**
 *
 * @author Kim LiChong
 */
@Entity
@Table(name = "INVITATION")
public class Invitation implements Serializable {

    private transient Logger logger = Logger.getLogger(Invitation.class.getName());
    /* EclipseLink 1.0 sometimes generated the same ID 
     * under heavy load leading to transaction failures during the insertion of
     * SocialEvents (PK violation). The problem seems to happen when the allocation size is exceeded.
     *
     * This is being investigated, temporary solution is to use a large allocationSize
     * to reduce the occurance of this issue.
     */
    @TableGenerator(name = "INVITATION_ID_GEN",
    table = "ID_GEN",
    pkColumnName = "GEN_KEY",
    valueColumnName = "GEN_VALUE",
    pkColumnValue = "INVITATION_ID",
    allocationSize = 50000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "INVITATION_ID_GEN")
    @Id
    private int invitationID;
    @ManyToOne
    private Person requestor;
    @ManyToOne
    private Person candidate;
    private boolean isAccepted;

    public int getInvitationID() {
        return invitationID;
    }

    public Person getRequestor() {
        return requestor;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public Person getCandidate() {
        return candidate;
    }

    public void setCandidate(Person candidate) {
        this.candidate = candidate;
    }

    public void setIsAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public void setRequestor(Person requestor) {
        this.requestor = requestor;
    }

    public void setInvitationID(int invitationID) {
        this.invitationID = invitationID;
    }

    public Invitation(Person requestor, Person candidate) {
        this.requestor = requestor;
        this.candidate = candidate;
        this.isAccepted = false;
    }

    public Invitation() {
    }

    /*
    public boolean equals(Invitation inv) {
    String requestorUsername = inv.getRequestor().getUserName();
    String candidateUsername = inv.getCandidate().getUserName();
    boolean returnValue = false;

    if ((requestorUsername != null && this.getRequestor().getUserName().equalsIgnoreCase(requestorUsername))
    && (candidateUsername != null && this.getCandidate().getUserName().equalsIgnoreCase(candidateUsername))) {
    returnValue = true;
    } else {
    returnValue = false;
    }

    return returnValue;

    }
     */
    @Override
    public boolean equals(Object o) {
        boolean returnValue = false;
        if (o != null && o instanceof Invitation) {
            Invitation inv = (Invitation) o;
            String requestorUsername = inv.getRequestor().getUserName();
            String candidateUsername = inv.getCandidate().getUserName();

            String o_candidateUsername = this.getCandidate().getUserName();
            String o_requestorUsername = this.getRequestor().getUserName();
            logger.finer("this: candidate:" + o_candidateUsername + " requestor: " + o_requestorUsername);
            logger.finer("arg:  candidate:" + candidateUsername + " requestor: " + requestorUsername);

            if ((o_requestorUsername.equalsIgnoreCase(requestorUsername) && o_candidateUsername.equalsIgnoreCase(candidateUsername))) {
                returnValue = true;
            } else {
                returnValue = false;
            }
        }
        logger.finer("****");
        logger.finer("the return value for equality check is " + returnValue);

        return returnValue;
    }
}
