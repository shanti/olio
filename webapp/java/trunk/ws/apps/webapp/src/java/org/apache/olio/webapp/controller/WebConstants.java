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

package org.apache.olio.webapp.controller;

/**
 * Constants used by the package
 * @author Inderjeet Singh
 * @author Binu John
 */
public class WebConstants {
    
    private WebConstants() {
    }
    
    /** REST action type */
    /** represents action parameter in a request */
    public static final String ACTION_TYPE_PARAM = "actionType";
    /** represents the action value add parameter in a request */
    public static final String AT_CREATE_PARAMVALUE = "create";
    public static final String AT_READ_PARAMVALUE = "read";
    /** represents the action value edit parameter in a request */
    public static final String AT_UPDATE_PARAMVALUE = "update";
    /** represents the action value delete parameter in a request */
    public static final String AT_DELETE_PARAMVALUE = "delete";
    
    //for person action to add a friend
    public static final String AT_ADD_FRIEND = "add_friend";
    
    public static final String GET_FRIENDS = "get_friends";
    public static final String GET_POSTED_EVENTS = "get_posted_events";
    public static final String GET_ATTEND_EVENTS = "get_attend_events";
    public static final String DISPLAY_PERSON = "display_person";
    
     //for person action to handle invitations
    public static final String REVOKE_INVITE = "revoke_invite";
    public static final String APPROVE_FRIEND = "approve_friend";
    public static final String REJECT_INVITE = "reject_invite";
    public static final String FRIEND_PARAM = "friend";

    /** represents id parameter in a request */
    public static final String ID_PARAM = "id";

    /** Key used to represent event in the request scope */
    public static final String EVENT_KEY = "event";
    
    /** Key used to represent person in the request scope */
    public static final String PERSON_KEY = "person";
    
    /** Key used to represent event facade in the application scope */
    public static final String MF_KEY = "mf";
    
    /** Key used to represent event date in the request scope */
    public static final String DATE_PARAM = "date";
    
    /** Key used to represent title parameter in a request */
    public static final String TITLE_PARAM = "title";
    
    /** Key used to represent tags parameter in a request */
    public static final String TAGS_PARAM = "tags";
    
    /** Key used to represent event description in the request scope */
    public static final String DESCRIPTION_PARAM = "description";
    
    /** Key used to represent event description in the request scope */
    public static final String SUMMARY_PARAM = "summary";
    
    /** Key used to represent submitted user name parameter in a request */
    public static final String SUBMITTER_USER_NAME_PARAM = "submitter_user_name";
    
    /** Key used to represent street1 parameter in the request scope */
    public static final String STREET1_PARAM = "street1";

    /** Key used to represent street2 parameter in the request scope */
    public static final String STREET2_PARAM = "street2";
    
    /** Key used to represent city parameter in the request scope */
    public static final String CITY_PARAM = "city";
    
    /** Key used to represent state parameter in the request scope */
    public static final String STATE_PARAM = "state";
    
    /** Key used to represent zip parameter in the request scope */
    public static final String ZIP_PARAM = "zip";

    /** Key used to represent telephone parameter in the request scope */
    public static final String TELEPHONE_PARAM = "telephone";
    
    /** Key used to represent country parameter in the request scope */
    public static final String COUNTRY_PARAM = "country";
    
    /** Key used to represent timezone parameter in the request scope */
    public static final String TIMEZONE_PARAM = "timezone";

    /** Key used to represent email parameter in the request scope */
    public static final String EMAIL_PARAM = "email";
    
    /** Key used to represent upload_literature parameter in the request scope */
    public static final String UPLOAD_LITERATURE_PARAM = "upload_event_literature";

    //adding 2 different constants to differentiate between event and person upload images
    public static final String UPLOAD_PERSON_IMAGE_PARAM = "upload_person_image";
    public static final String UPLOAD_EVENT_IMAGE_PARAM = "upload_event_image";

    public static final String UPLOAD_EVENT_IMAGE_THUMBNAIL_PARAM = "upload_event_thumbnail_image";
    public static final String UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM = "upload_user_thumbnail_image";

    /** Key used to represent time parameter in the request scope */
    public static final String TIME_PARAM = "time";
    
    /** Keys used for person requests */  
    public static final String USER_NAME_PARAM = "user_name";
    public static final String PASSWORD_PARAM = "password";
    public static final String FIRST_NAME_PARAM = "first_name";
    public static final String LAST_NAME_PARAM = "last_name";
    public static final String FRIEND_USER_NAME_PARAM = "friend_user_name";
    
    public static final String PROXY_HOST_INIT_PARAM = "proxyHost";
    public static final String PROXY_PORT_INIT_PARAM = "proxyPort";         
    public static final String COMMA = ", ";
    public static final String DOUBLE_QUOTE = "\"";
}
