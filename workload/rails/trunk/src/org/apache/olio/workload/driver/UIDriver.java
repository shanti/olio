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
 * 
 * $Id: UIDriver.java,v 1.26 2008/02/12 03:24:20 akara Exp $
 *
 */
package org.apache.olio.workload.driver;

import com.sun.faban.driver.*;
import com.sun.faban.common.Utilities;
import com.sun.faban.common.NameValuePair;
import org.apache.olio.workload.util.RandomUtil;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.MultipartPostMethod;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

@BenchmarkDefinition (
    name    = "Web20Bench",
    version = "0.3",
    scaleName = "Concurrent Users"

)
@BenchmarkDriver (
    name           = "UIDriver",
    threadPerScale    = 1
)
        // 90/10 Read/Write ratio
       
@MatrixMix (
//    operations = {"HomePage", "Login", , "TagSearch", "EventDetail", "PersonDetail",
//                  "Logout", "AddEvent",  "AddPerson"},
    operations = { "HomePage", "Login", "TagSearch", "EventDetail", "PersonDetail", "AddPerson", "AddEvent" },

    mix = { @Row({  0, 11, 52, 36,  0, 1,  0 }), // Home Page
            @Row({  0,  0, 60, 20,  0, 0, 20 }), // Login
            @Row({ 21,  6, 41, 31,  0, 1,  0 }), // Tag Search
            @Row({ 72, 21,  0,  0,  6, 1,  0 }), // Event Detail
            @Row({ 52,  6,  0, 31, 11, 0,  0 }), // Person Detail
            @Row({  0,  0,  0,  0,100, 0,  0 }), // Add Person
            @Row({  0,  0,  0,100,  0, 0,  0 })  // Add Event
          }
)

@NegativeExponential (
    cycleType = CycleType.CYCLETIME,
    cycleMean = 5000,
    cycleDeviation = 2
)

public class UIDriver {

    public static final String[] HOME_STATICS = {
        "/javascripts/prototype.js",
        "/javascripts/effects.js",
        "/javascripts/dragdrop.js",
        "/javascripts/controls.js",
        "/javascripts/application.js",
        "/stylesheets/scaffold.css",
        "/stylesheets/site.css",
        "/images/bg_main.png",
        "/images/bg_header.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/RSS-icon-large.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/corner_top_right.png",
        "/images/corner_top_left.png",
        "/images/corner_bottom_right.png",
        "/images/corner_bottom_left.png",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/images/main_nav_hover_bg.gif"
   };

    public static final String[] EVENTDETAIL_STATICS = {
        "/javascripts/prototype.js",
        "/javascripts/effects.js",
        "/javascripts/dragdrop.js",
        "/javascripts/controls.js",
        "/javascripts/application.js",
        "/stylesheets/scaffold.css",
        "/stylesheets/site.css",
        "/images/bg_main.png",
        "/images/bg_header.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/RSS-icon-large.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/corner_top_right.png",
        "/images/corner_top_left.png",
        "/images/corner_bottom_right.png",
        "/images/corner_bottom_left.png",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/images/main_nav_hover_bg.gif",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png"
    };

    public static final String[] ADDPERSON_STATICS = {
        "/javascripts/prototype.js",
        "/javascripts/effects.js",
        "/javascripts/dragdrop.js",
        "/javascripts/controls.js",
        "/javascripts/application.js",
        "/stylesheets/scaffold.css",
        "/stylesheets/site.css",
        "/images/bg_main.png",
        "/images/bg_header.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/RSS-icon-large.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/corner_top_right.png",
        "/images/corner_top_left.png",
        "/images/corner_bottom_right.png",
        "/images/corner_bottom_left.png",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/images/main_nav_hover_bg.gif",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png"
    };

    public static final String[] ADDEVENT_STATICS = {
        "/javascripts/prototype.js",
        "/javascripts/effects.js",
        "/javascripts/dragdrop.js",
        "/javascripts/controls.js",
        "/javascripts/application.js",
        "/stylesheets/scaffold.css",
        "/stylesheets/site.css",
        "/images/bg_main.png",
        "/images/bg_header.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/RSS-icon-large.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/corner_top_right.png",
        "/images/corner_top_left.png",
        "/images/corner_bottom_right.png",
        "/images/corner_bottom_left.png",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/images/main_nav_hover_bg.gif",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png"
    };


    public static final String[] PERSON_STATICS = {
        "/javascripts/prototype.js",
        "/javascripts/effects.js",
        "/javascripts/dragdrop.js",
        "/javascripts/controls.js",
        "/javascripts/application.js",
        "/stylesheets/scaffold.css",
        "/stylesheets/site.css",
        "/images/bg_main.png",
        "/images/bg_header.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/RSS-icon-large.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/corner_top_right.png",
        "/images/corner_top_left.png",
        "/images/corner_bottom_right.png",
        "/images/corner_bottom_left.png",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/images/main_nav_hover_bg.gif"
    };

    public static final String[] PERSON_GETS = {
        "/javascripts/prototype.js",
        "/javascripts/effects.js",
        "/javascripts/dragdrop.js",
        "/javascripts/controls.js",
        "/javascripts/application.js",
        "/stylesheets/scaffold.css",
        "/stylesheets/site.css",
        "/images/bg_main.png",
        "/images/bg_header.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/RSS-icon-large.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/corner_top_right.png",
        "/images/corner_top_left.png",
        "/images/corner_bottom_right.png",
        "/images/corner_bottom_left.png",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/images/main_nav_hover_bg.gif"
    };

    public static final String[] TAGSEARCH_STATICS = {
        "/javascripts/prototype.js",
        "/javascripts/effects.js",
        "/javascripts/dragdrop.js",
        "/javascripts/controls.js",
        "/javascripts/application.js",
        "/stylesheets/scaffold.css",
        "/stylesheets/site.css",
        "/images/bg_main.png",
        "/images/bg_header.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/RSS-icon-large.gif",
        "/images/main_nav_link_bg.gif",
        "/images/main_nav_link_bg.gif",
        "/images/corner_top_right.png",
        "/images/corner_top_left.png",
        "/images/corner_bottom_right.png",
        "/images/corner_bottom_left.png",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/images/main_nav_hover_bg.gif"
    };

    /* Custom metric targets */
    public static final double EVENT_VIEWS_WHERE_ATTENDEE_ATTENDED_TARGET = 8d;
    public static final double AVG_IMAGE_REFS_PER_HOMEPAGE_TARGET = 10d;
    public static final double AVG_IMAGES_LOADED_PER_HOMEPAGE_TARGET = 3d;
    public static final double AVG_IMAGE_BYTES_PER_HOMEPAGE_TARGET = 20000d;
    public static final double AVG_IMAGE_PER_TAG_SEARCH_TARGET = 10;
    public static final double AVG_IMGS_PER_EVENT_DETAIL_TARGET = 0d;


    // We just need today's date. java.sql.date does not have any time anyway.
    public static final java.sql.Date BASE_DATE =
                                new java.sql.Date(System.currentTimeMillis());

    private DriverContext ctx;
    private HttpTransport http;
    private String baseURL;
    private String personDetailURL;
    private String tagSearchURL;
    private String homepageURL, loginURL, logoutURL;
    private String addEventURL, addPersonURL, eventDetailURL;
    private String addEventResultURL, addPersonResultURL;
    private String addAttendeeURL; //GET update.php?id=$eventid
    private String checkNameURL;
    // private String updatePageURL; //POST gettextafterinsert.php list=attendees
    private String fileServiceURL;
    private String[] homepageStatics, personStatics, personGets,
    tagSearchStatics, eventDetailStatics, addPersonStatics, addEventStatics;
    File eventImg, eventThumb, eventPdf, personImg, personThumb;
    private boolean isLoggedOn = false;
    private String username;
    Logger logger;
    private com.sun.faban.driver.util.Random random;
    private DateFormat df;
    private String selectedEvent;
    private int personsAdded = 0;
    private int loadedUsers;
    private boolean isCached;
    private HashSet<String> cachedURLs = new HashSet<String>();
    private LinkedHashMap<String, String> loginHeaders =
            new LinkedHashMap<String, String>();
    private UIDriverMetrics driverMetrics;
    private long imgBytes = 0;
    private int imagesLoaded = 0;
    private String tagCloudURL;

    private HttpClient httpClient;

    public UIDriver() throws XPathExpressionException {
        ctx = DriverContext.getContext();
        int scale = ctx.getScale();
        ScaleFactors.setActiveUsers(scale);
        http = new HttpTransport();

        logger = ctx.getLogger();
        random = ctx.getRandom();
        driverMetrics = new UIDriverMetrics();
        ctx.attachMetrics(driverMetrics);
        String hostPorts = ctx.getXPathValue(
                                "/olio/webServer/fa:hostConfig/fa:hostPorts");
        List<NameValuePair<Integer>> hostPortList =
                                            Utilities.parseHostPorts(hostPorts);
        int loadedScale = Integer.parseInt(
                                    ctx.getXPathValue("/olio/dbServer/scale"));
        loadedUsers = ScaleFactors.USERS_RATIO * loadedScale;
        if (scale > loadedScale)
            throw new FatalException("Data loaded only for " + loadedScale +
                    " concurrent users. Run is set for " + scale +
                    " concurrent users. Please load for enough concurrent " +
                    "users. Run terminating!");

        //String type = ctx.getProperty("serverType");
        String type = "html";
        String resourcePath = ctx.getResourceDir();
        if (!resourcePath.endsWith(File.separator))
            resourcePath += File.separator;
        eventImg = new File(resourcePath + "event.jpg");
        // logger.info("eventImg: " + eventImg);
        eventThumb = new File(resourcePath + "event_thumb.jpg");
        // logger.info("eventThumb: " + eventThumb);
        eventPdf = new File(resourcePath + "event.pdf");
        // logger.info("eventPdf: " + eventPdf);
        personImg = new File(resourcePath + "person.jpg");
        // logger.info("personImg: " + personImg);
        personThumb = new File(resourcePath + "person_thumb.jpg");
        // logger.info("personThumb: " + personThumb);

        int bucket = Utilities.selectBucket(ctx.getThreadId(),
                            ctx.getClientsInDriver(), hostPortList.size());
        NameValuePair<Integer> hostPort = hostPortList.get(bucket);

        if (hostPort.value == null)
            baseURL = "http://" + hostPort.name;
        else
            baseURL = "http://" + hostPort.name + ':' + hostPort.value;
        personDetailURL = baseURL + "/users/";
        tagSearchURL = baseURL + "/events/tag_search/";
        tagCloudURL = baseURL + "/tagCloud." + type;
        addEventURL = baseURL + "/events/new";
        addEventResultURL = baseURL + "/events/";
        addPersonURL = baseURL + "/users/new";
        addPersonResultURL = baseURL + "/users";
        homepageURL = baseURL + "/";
        loginURL = baseURL + "/users/login";
        logoutURL = baseURL + "/users/logout";
        addAttendeeURL = baseURL + "/events/";
        // updatePageURL = baseURL + "/gettextafterinsert." + type;
        eventDetailURL = baseURL + "/events/";
        fileServiceURL = baseURL + "/fileService." + type + '?';
        checkNameURL = baseURL + "/users/check_name";

        homepageStatics = populateList(HOME_STATICS);
        personStatics = populateList(PERSON_STATICS);
        personGets = populateList(PERSON_GETS);
        tagSearchStatics = populateList(TAGSEARCH_STATICS);
        eventDetailStatics = populateList(EVENTDETAIL_STATICS);
        addPersonStatics = populateList(ADDPERSON_STATICS);
        addEventStatics = populateList(ADDEVENT_STATICS);

       if (hostPort.value == null)
            loginHeaders.put("Host", hostPort.name);
        else
            loginHeaders.put("Host", hostPort.name + ':' + hostPort.value);
        loginHeaders.put("User-Agent", "Mozilla/5.0");
        loginHeaders.put("Accept", "text/xml.application/xml,application/" +
                "xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;" +
                "q=0.5");
        loginHeaders.put("Accept-Language", "en-us,en;q=0.5");
        loginHeaders.put("Accept-Encoding", "gzip,deflate");
        loginHeaders.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        loginHeaders.put("Keep-Alive", "300");
        loginHeaders.put("Connection", "keep-alive");
        loginHeaders.put("Referer", homepageURL);

        isLoggedOn = false;
        isCached = cached();
// Shanti: Why are we creating httpClient here ?
        httpClient = new HttpClient();
        httpClient.setConnectionTimeout(5000);
    }

    @BenchmarkOperation (
        name    = "HomePage",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doHomePage() throws IOException {
        logger.finer("HomePage: Accessing " + homepageURL);

        http.fetchURL(homepageURL);
        imgBytes = 0;
        imagesLoaded = 0;

        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");

        Set<String> images = parseImages(responseBuffer);
        if (!isCached) {
            loadStatics(homepageStatics);
        }
        loadImages(images);
        selectedEvent = RandomUtil.randomEvent(random, responseBuffer);
        logger.finer("Images loaded: " + imagesLoaded);
        logger.finer("Image bytes loaded: " + imgBytes);
        if (ctx.isTxSteadyState()) {
            driverMetrics.homePageImages += images.size();
            driverMetrics.homePageImagesLoaded += imagesLoaded;
            driverMetrics.homePageImageBytes += imgBytes;
        }
    }

    @BenchmarkOperation (
        name    = "Login",
        max90th = 1,
        timing  = Timing.MANUAL
    )
    public void doLogin() throws IOException, Exception {
        httpClient = new HttpClient();
        logger.finer("In doLogin");
        int randomId = 0; //use as password
        username = null;

        if (isLoggedOn) {
            doLogout();
        }

        randomId = selectUserID();
        username = UserName.getUserName(randomId);
        logger.fine("Logging in as " + username + ", " + randomId);
        PostMethod loginPost = constructLoginPost(randomId);
        loginPost.setFollowRedirects(true);

        ctx.recordTime();

        httpClient.executeMethod(loginPost);

        ctx.recordTime();

        int loginIdx = loginPost.getResponseBodyAsString().indexOf("Login:");
        if (loginIdx != -1)
            throw new Exception(" Found login prompt at index " + loginIdx); 

        logger.fine("Login successful as " + username + ", " + randomId);
        isLoggedOn=true;
    }


    @BenchmarkOperation (
        name    = "Logout",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doLogout() throws IOException {
        if (isLoggedOn){
            logger.finer("Logging off = " + isLoggedOn);
	    GetMethod logout = new GetMethod(logoutURL);
            httpClient.executeMethod(logout);
            cachedURLs.clear();
            isCached = cached();
            isLoggedOn=false;
        }
    }


    @BenchmarkOperation (
        name    = "TagSearch",
        max90th = 2,
        timing  = Timing.AUTO
    )
    public void doTagSearch() throws IOException {
        String tag = RandomUtil.randomTagName(random);
        String search = tagSearchURL + "?tag=" + tag + "&submit=Search+Tags";
        logger.finer("TagSearch: " + search);
        http.fetchURL(search);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        Set<String> images = parseImages(responseBuffer);
        loadStatics(tagSearchStatics);
        loadImages(images);
        String event = RandomUtil.randomEvent(random, responseBuffer);
        if (event != null)
            selectedEvent = event;
        if (ctx.isTxSteadyState())
            driverMetrics.tagSearchImages += images.size();
    }

    @BenchmarkOperation (
        name    = "AddEvent",
        max90th = 4,
        timing  = Timing.MANUAL
    )
    public void doAddEvent() throws Exception {
        logger.finer("entering doAddEvent()");
        if(!isLoggedOn)
            throw new IOException("User not logged when trying to add an event");

        ctx.recordTime();
        loadStatics(addEventStatics);

        MultipartPostMethod post = new MultipartPostMethod(addEventResultURL);
        GetMethod eventForm = new GetMethod(addEventURL);

        StringPart tmpPart = null;
        // TODO: Implement prepareEvent() for Rails form data

        String nvp[][] = {{"commit", "Create"},
                          {"event[title]", RandomUtil.randomText(random, 15, 20)},
                          {"event[summary]", RandomUtil.randomText(random, 50, 200)},
                          {"event[description]", RandomUtil.randomText(random, 100, 495)},
                          {"event[telephone]", RandomUtil.randomPhone(random, new StringBuilder(256))},
                          {"event[event_timestamp(1i)]", "2008"},
                          {"event[event_timestamp(2i)]", "10"},
                          {"event[event_timestamp(3i)]", "20"},
                          {"event[event_timestamp(4i)]", "20"},
                          {"event[event_timestamp(5i)]", "10"}};

        for (int i = 0; i < nvp.length; i++) {            
            tmpPart = new StringPart(nvp[i][0], nvp[i][1]);
            tmpPart.setContentType(null);
            post.addPart(tmpPart);
        }

        Part imagePart = new FilePart("event_image", eventImg, "image/jpeg", null);
        Part docPart = new FilePart("event_document", eventPdf, "application/pdf", null);

        post.addPart(imagePart);
        post.addPart(docPart);

        tmpPart = new StringPart("tag_list", "tag1");
        tmpPart.setContentType(null);

        post.addPart(tmpPart);

        addAddress(post);

        // GET the new event form within a user session
        httpClient.executeMethod(eventForm);
        String responseBuffer = eventForm.getResponseBodyAsString();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");

        // Parse the authenticity_token from the response
        String token = parseAuthToken(responseBuffer);
        if (token != null) {
            tmpPart = new StringPart("authenticity_token", token);
            tmpPart.setContentType(null);
            post.addPart(tmpPart);
        }

        doMultiPartPost(post, "Event was successfully created.");

        ctx.recordTime();
        ++driverMetrics.addEventTotal;
    }

    @BenchmarkOperation (
        name    = "AddPerson",
        max90th = 3,
        timing  = Timing.MANUAL
    )
    public void doAddPerson() throws Exception {
        logger.finer("doAddPerson");
        if (isLoggedOn)
            doLogout();

        ctx.recordTime(); // Start critical section
        http.readURL(addPersonURL);
        MultipartPostMethod post = new MultipartPostMethod(addPersonResultURL);
        
        // TODO: Implement preparePerson() for Rails form data
        // String fields[]  = new String[8];
        StringBuilder b = new StringBuilder(256);
        int id = loadedUsers + personsAdded++ * ScaleFactors.activeUsers +
                                                        ctx.getThreadId() + 1;
        String username = UserName.getUserName(id);
        if (username == null || username.length() == 0)
            logger.warning("Username is null!");

        http.readURL(checkNameURL, "name=" + username);

        StringPart tmpPart = null;

        String nvp[][] = {{"user[username]", username},
                          {"user[password]", "" + id},
                          {"user[password_confirmation]", "" + id},
                          {"user[firstname]",  RandomUtil.randomName(random, new StringBuilder(256), 2, 12).toString()},
                          {"user[lastname]",  RandomUtil.randomName(random, new StringBuilder(256), 5, 12).toString()},
                          {"user[email]", username + "@" + random.makeCString(3, 10) + ".com"},
                          {"user[telephone]", RandomUtil.randomPhone(random, new StringBuilder(256)).toString()},
                          {"user[summary]", RandomUtil.randomText(random, 50, 200)},
                          {"user[timezone]", RandomUtil.randomTimeZone(random)}};

        for (int i = 0; i < nvp.length; i++) {
            tmpPart = new StringPart(nvp[i][0], nvp[i][1]);
            tmpPart.setContentType(null);
            post.addPart(tmpPart);
        }
        
        Part imagePart = new FilePart("user_image", personImg, "image/jpeg", null);
        post.addPart(imagePart);
        
        addAddress(post);

        loadStatics(addPersonStatics);
        doMultiPartPost(post, "Succeeded in creating user.");

        ctx.recordTime();
        ++driverMetrics.addPersonTotal;
    }

    @BenchmarkOperation (
        name    = "EventDetail",
        max90th = 2,
        timing  = Timing.AUTO
    )
    public void doEventDetail() throws Exception {
        //select random event
        logger.finer("doEventDetail");
        if (selectedEvent == null) {
            logger.warning("In event detail and select event is null");
            http.fetchURL(homepageURL);
            StringBuilder responseBuffer = http.getResponseBuffer();
            selectedEvent = RandomUtil.randomEvent(random, responseBuffer);
            if (selectedEvent == null) {
                throw new IOException("In event detail and select event is null");
            }    
        }

        http.fetchURL(eventDetailURL + selectedEvent);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        boolean canAddAttendee = isLoggedOn &&
            responseBuffer.indexOf("Unattend") == -1;

        Set<String> images = parseImages(responseBuffer);
        loadStatics(eventDetailStatics);
        loadImages(images);
        if (ctx.isTxSteadyState())
            driverMetrics.eventDetailImages += images.size();
        if (canAddAttendee) {
            // 10% of the time we can add ourselves, we will.
            int card = random.random(0, 9);
            if (card == 0) {
                doAddAttendee();
                if (ctx.isTxSteadyState())
                    ++driverMetrics.addAttendeeCount;
            }
            if (ctx.isTxSteadyState())
                ++driverMetrics.addAttendeeReadyCount;
        }
    }

    @BenchmarkOperation (
        name = "PersonDetail",
        max90th = 2,
        timing = Timing.MANUAL
    )
    public void doPersonDetail() throws IOException {
        logger.finer("doPersonDetail");
        StringBuilder buffer = new StringBuilder(fileServiceURL.length() + 20);
        //buffer.append(fileServiceURL).append("file=p");
        
        ctx.recordTime();
		// Shanti: No need to be logged on to see user
		/**
        if (isLoggedOn) {
		**/
            int id = random.random(1, ScaleFactors.users);
            GetMethod personDetailGet = new GetMethod(personDetailURL + id);
            httpClient.executeMethod(personDetailGet);
            StringBuilder responseBuffer = new StringBuilder(personDetailGet.getResponseBodyAsString());
            if (responseBuffer.length() == 0)
                throw new IOException("Received empty response");
            Set<String> images = parseImages(responseBuffer);
			loadImages(images);
            String event = RandomUtil.randomEvent(random, responseBuffer);
            if (event != null)
                selectedEvent = event;
		/***
        }
        else
        {
            // Need to handle this better. User profile is only for logged in users.
            logger.warning("Trying to view user, but not logged in");
            http.fetchURL(homepageURL);
        }
		***/
        ctx.recordTime();
    }

    public void doAddAttendee() throws Exception {
        //can only add yourself (one attendee) to party
        // Need to add header that will request js instead of
        // html. This will prevent the redirect.
        PostMethod attendeePost = new PostMethod(addAttendeeURL + selectedEvent + "/attend");
        Header header = new Header("Accept", "text/javascript");
        attendeePost.setRequestHeader(header);
        int status = httpClient.executeMethod(attendeePost);
        if (status != HttpStatus.SC_OK) {
            throw new Exception("Add attendee returned: " + status);
        }
        String buffer = attendeePost.getResponseBodyAsString();
        if (buffer.indexOf("You are attending") == -1) {
            logger.warning("Add attendee failed, possible race condition");
            // throw new Exception("Add attendee failed, could not find: You are attending");
        }
    }

    public Set<String> parseImages(StringBuilder buffer) {
        LinkedHashSet<String> urlSet = new LinkedHashSet<String>();
        //String elStart = "<img ";
        String elStart = "background: ";
        String attrStart = " url(";
        int elStartLen = elStart.length() - 1; // Don't include the trailing space
        int attrStartLen = attrStart.length();
        int idx = 0;
        logger.finest("Parsing images from buffer");
        for (;;) {

            // Find and copy out the element.
            idx = buffer.indexOf(elStart, idx);
            if (idx == -1)
                break;
            idx += elStartLen;
            int endIdx = buffer.indexOf(")", idx) + 1; // +1 to include the '('
            if (endIdx == -1)
                break;
            String elText = buffer.substring(idx, endIdx);
            logger.finest(elText);
            idx = endIdx + 1;

            // Find the attribute
            int idx2 = elText.indexOf(attrStart);
            if (idx2 == -1) {
                logger.finer("No img src attribute. Weird! " + elText);
                continue;
            }
            endIdx = elText.indexOf(")", idx2 + attrStartLen);
            if (endIdx == -1) {
                logger.warning("No ) attribute ending. Weird! " + elText);
                continue;
            }

            String link = elText.substring(idx2 + attrStartLen, endIdx);
            if (link.startsWith("/uploaded_files")) {
                String url = baseURL + link;

                logger.finer("Adding " + url + " from idx " + idx);
                urlSet.add(url);
            }
        }
        return urlSet;
    }

    private String parseAuthToken(String responseBuffer) throws IOException {

        int idx = responseBuffer.indexOf("authenticity_token");

        if (idx == -1) {
            logger.info("Trying to add event but authenticity token not found");
            return null;
        }

        int endIdx = responseBuffer.indexOf("\" />", idx);

        if (endIdx == -1)
            throw new IOException("Invalid authenticity_token element. Buffer(100 chars) = " +
                    responseBuffer.substring(idx, idx + 100));

        String tmpString = responseBuffer.substring(idx, endIdx);
        String[] splitStr = tmpString.split("value=\"");

        if (splitStr.length < 2)
            throw new IOException("Invalid authenticity_token element. Buffer(100 chars) = " +
                    responseBuffer.substring(idx, idx + 100));

        String token = splitStr[1];

        logger.finer("authenticity_token = " + token);

        return token;

    }

    private void loadImages(Set<String> images) throws IOException {
        logger.finer("loadImages()");
        logger.finest("No. images = " + images.size());

        if (images != null) {
            for (String image : images) {
                // Loads image only if not cached, means we can add to cache.
                if (cachedURLs.add(image)) {
                    logger.finest("Loading image " + image);
                    imgBytes += http.readURL(image);
                    ++imagesLoaded;
                } else {
                    logger.finest("Image already cached: Not loading " + image);
                }
            }
        } else {
            logger.finest("images == null");
        }
    }

    private boolean cached() {
        // We have to decide whether the cache is empty or not.
        // 40% of the time, it is empty.
        boolean cached = true;
        int selector = random.random(0, 9);
        if (selector < 4) {
            cached = false;
        }
        return cached;
    }

    private void loadStatics(String[] urls) throws IOException {
        if (!isCached) {
            for (String url : urls) {
                if (cachedURLs.add(url)) {
                    logger.finer("Loading URL " + url);
                    http.readURL(url);
                } else {
                    logger.finer("URL already cached: Not loading " + url);
                }
            }
        }
    }

    public DateFormat getDateFormat() {
        if (df == null)
            df = new SimpleDateFormat("yyyy-MM-dd");
        return df;
    }

    public int selectUserID() {
        return random.random(0, ScaleFactors.USERS_RATIO - 1) *
                     ScaleFactors.activeUsers + ctx.getThreadId() + 1;
    }


    private String[] populateList(String[] arrList) {

        String[] returnList = new String[arrList.length];
        if (arrList != null) {

            try {
                for (int i = 0; i < arrList.length; i++) {
                    returnList[i] = baseURL + arrList[i].trim();

                }
            } catch (Exception e) {
                System.out.println ("Exception - " + e);
                e.printStackTrace();
            }
        }
        return returnList;
    }

    private PostMethod constructLoginPost(int randomId) {
        PostMethod loginPost = new PostMethod(loginURL);

        loginPost.setFollowRedirects(true);
        loginPost.addParameter("users[username]", username);
        loginPost.addParameter("users[password]", String.valueOf(randomId));
        loginPost.addParameter("submit", "Login");
        
        return loginPost;
    }

    public void doMultiPartPost(MultipartPostMethod post, String message) throws Exception {
        logger.finer("In doMultiPartPost()");

        if (httpClient == null) {
            logger.warning("HttpClient is null, this shouldn't happen");
            httpClient = new HttpClient();
            httpClient.setConnectionTimeout(5000);
        }

        post.setFollowRedirects(false);
        int status = httpClient.executeMethod(post);
        if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
            // Manually follow redirect
            GetMethod get = new GetMethod(post.getResponseHeader("Location").getValue());
            httpClient.executeMethod(get);
            String buffer = get.getResponseBodyAsString();
            int idx = buffer.indexOf(message);
            if (idx == -1 )
                throw new Exception("Could not find success message '" + message + "' in result body");
        } else if (status != HttpStatus.SC_OK) {
            throw new Exception("Multipart post did not redirect");
        }
    }

    // TODO: implement prepareAddress() for Rails form data
    public void addAddress(MultipartPostMethod post)
    {

        StringPart tmpPart = null;
        //add the address
        String nvp[][] = {{"address[street1]", street1()},
                          {"address[street2]",street2()},
                          {"address[city]", random.makeCString(4, 14)},
                          {"address[state]", random.makeCString(2, 2).toUpperCase()},
                          {"address[zip]", random.makeNString(5, 5)},
                          {"address[country]", country()}};

        for (int i = 0; i < nvp.length; i++) {
            tmpPart = new StringPart(nvp[i][0], nvp[i][1]);
            tmpPart.setContentType(null);
            post.addPart(tmpPart);
        }
    }

    public String street1() {
        String[] STREETEXTS = { "Blvd", "Ave", "St", "Ln", "" };
        StringBuilder buffer = new StringBuilder(255);
        buffer.append(random.makeNString(1, 5)).append(' '); // number
        RandomUtil.randomName(random, buffer, 1, 11); // street
        String streetExt = STREETEXTS[random.random(0, STREETEXTS.length - 1)];
        if (streetExt.length() > 0)
            buffer.append(' ').append(streetExt);
    	return buffer.toString();
    }

    public String street2() {
        int toggle = random.random(0, 1); // street2
        String street;
        if (toggle > 0)
          street = random.makeCString(5, 20);
        else
          street = "";
        return street;
    }

    public String country() {
        int toggle = random.random(0, 1);
        StringBuilder buffer = new StringBuilder(255);
        String cntry;
        if (toggle == 0) {
          cntry = "USA";
        } else {
          cntry = RandomUtil.randomName(random, buffer, 6, 16).toString();
        }

    return cntry;
    }
    
    static class UIDriverMetrics implements CustomMetrics {

        int addAttendeeCount = 0;
        int addAttendeeReadyCount = 0;
        int homePageImages = 0;
        int tagSearchImages = 0;
        int eventDetailImages = 0;
        int homePageImagesLoaded = 0;
        long homePageImageBytes = 0;
        int addEventTotal = 0;
        int addPersonTotal = 0;

        public void add(CustomMetrics other) {
            UIDriverMetrics o = (UIDriverMetrics) other;
            addAttendeeCount += o.addAttendeeCount;
            addAttendeeReadyCount += o.addAttendeeReadyCount;
            homePageImages += o.homePageImages;
            tagSearchImages += o.tagSearchImages;
            eventDetailImages += o.eventDetailImages;
            homePageImageBytes += o.homePageImageBytes;
            homePageImagesLoaded += o.homePageImagesLoaded;
            addEventTotal += o.addEventTotal;
            addPersonTotal += o.addPersonTotal;
        }

        public Element[] getResults() {
            Result r = Result.getInstance();
            int total = r.getOpsCountSteady("EventDetail");
            Element[] el = new Element[10];
            el[0] = new Element();
            el[0].description = "% EventDetail views where attendee added";
            el[0].target = "&gt;= 6";
            if (total > 0) {
                double pctAdd = 100d * addAttendeeCount / (double) total;
                el[0].result = String.format("%.2f", pctAdd);
                if (pctAdd >= 6d)
                    el[0].passed = Boolean.TRUE;
                else
                    el[0].passed = Boolean.FALSE;
            } else {
                el[0].result = "";
                el[0].passed = Boolean.FALSE;
            }

            el[1] = new Element();
            el[1].description = "EventDetail count where attendee can be added";
            el[1].result = String.valueOf(addAttendeeReadyCount);

            int cnt = r.getOpsCountSteady("HomePage");
            el[2] = new Element();
            el[2].description = "Average images references on Home Page";
            el[2].target = "10";
            el[2].allowedDeviation = "0.5";
            if (cnt > 0) {
                double imagesPerPage = homePageImages / (double) cnt;
                el[2].result = String.format("%.2f", imagesPerPage);
                if (imagesPerPage >= 9.5d && imagesPerPage <= 10.5d)
                    el[2].passed = Boolean.TRUE;
                else
                    el[2].passed = Boolean.FALSE;
            } else {
                el[2].result = "";
                el[2].passed = Boolean.FALSE;
            }

// Shanti: This should be 3. Changed to 2.5 temporarily
            el[3] = new Element();
            el[3].description = "Average images loaded per Home Page";
            el[3].target = "&gt;= 2.5";
            if (cnt > 0) {
                double avgImgs = homePageImagesLoaded / (double) cnt;
                el[3].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 2.5)
                    el[3].passed = Boolean.TRUE;
                else
                    el[3].passed = Boolean.FALSE;
            } else {
                el[3].result = "";
                el[3].passed = Boolean.FALSE;
            }

            el[4] = new Element();
            el[4].description = "Average image bytes received per Home Page";
            el[4].target = "&gt;= 15000";
            if (cnt > 0) {
                double avgBytes = homePageImageBytes / (double) cnt;
                el[4].result = String.format("%.2f", avgBytes);
                if (avgBytes >= 15000)
                    el[4].passed = Boolean.TRUE;
                else
                    el[4].passed = Boolean.FALSE;
            } else {
                el[4].result = "";
                el[4].passed = Boolean.FALSE;
            }
            cnt = r.getOpsCountSteady("TagSearch");
            el[5] = new Element();
            el[5].description = "Average images on Tag Search Results";
            el[5].target = "&gt;= 3.6";
            if (cnt > 0) {
                double avgImgs = tagSearchImages / (double) cnt;
                el[5].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 3.6d)
                    el[5].passed = Boolean.TRUE;
                else
                    el[5].passed = Boolean.FALSE;
            } else {
                el[5].result = "";
                el[5].passed = Boolean.FALSE;
            }
            el[6] = new Element();
            el[6].description = "Average images on Event Detail";
            el[6].target = "&gt;= 1";
            if (total > 0) {
                double avgImgs = eventDetailImages / (double) total;
                el[6].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 1d)
                    el[6].passed = Boolean.TRUE;
                else
                    el[6].passed = Boolean.FALSE;
            } else {
                el[6].result = "";
                el[6].passed = Boolean.FALSE;
            }
            el[7] = new Element();
            el[7].description = "Total successful AddEvent calls";
            el[7].result = String.valueOf(addEventTotal);
            el[8] = new Element();
            el[8].description = "Total successful AddPerson calls";
            el[8].result = String.valueOf(addPersonTotal);
            el[9] = new Element();
            el[9].description = "Concurrent user to ops/sec ratio";
            el[9].target = "&lt;= 5.25";
            double ratio = r.getScale() / r.getMetric();
            el[9].result = String.format("%.2f", ratio);
            if (ratio <= 5.25d)
                el[9].passed = true;
            else
                el[9].passed = false;
            return el;
        }

        public Object clone() {
            UIDriverMetrics clone = new UIDriverMetrics();
            clone.addAttendeeCount = addAttendeeCount;
            clone.addAttendeeReadyCount = addAttendeeReadyCount;
            clone.homePageImages = homePageImages;
            clone.tagSearchImages = tagSearchImages;
            clone.eventDetailImages = eventDetailImages;
            clone.homePageImageBytes = homePageImageBytes;
            clone.homePageImagesLoaded = homePageImagesLoaded;
            clone.addEventTotal = addEventTotal;
            clone.addPersonTotal = addPersonTotal;
            return clone;
        }
    }
}
