/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html or
 * install_dir/legal/LICENSE
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at install_dir/legal/LICENSE.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: UIDriver.java,v 1.26 2008/02/12 03:24:20 akara Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.web20.driver;

import com.sun.faban.driver.*;
import com.sun.faban.common.Utilities;
import com.sun.faban.common.NameValuePair;
import com.sun.web20.util.RandomUtil;
import com.sun.web20.util.ScaleFactors;
import com.sun.web20.util.UserName;
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

    mix = { @Row({  0, 10, 50, 35,  0, 5,  0 }), // Home Page
            @Row({  0,  0, 60, 20,  0, 0, 20 }), // Login
            @Row({ 20,  5, 40, 30,  0, 5,  0 }), // Tag Search
            @Row({ 70, 20,  0,  0,  0, 5,  0 }), // Event Detail
            @Row({  0,  0,  0, 30, 10, 0, 30 }), // Person Detail
            @Row({ 30, 65,  0,  0, 15, 0,  0 }), // Add Person
            @Row({  0,  0, 25, 75, 15, 0,  0 })  // Add Event
          }
)
@NegativeExponential (
    cycleType = CycleType.THINKTIME,
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
        "/javascripts/tiny_mce/tiny_mce.js",
        "/javascripts/tiny_mce/themes/simple/editor_template.js",
        "/javascripts/tiny_mce/langs/en.js",
        "/javascripts/tiny_mce/themes/simple/css/editor_ui.css",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/javascripts/tiny_mce/themes/simple/images/italic.gif",
        "/javascripts/tiny_mce/themes/simple/images/underline.gif",
        "/javascripts/tiny_mce/themes/simple/images/strikethrough.gif",
        "/javascripts/tiny_mce/themes/simple/images/undo.gif",
        "/javascripts/tiny_mce/themes/simple/images/separator.gif",
        "/javascripts/tiny_mce/themes/simple/images/redo.gif",
        "/javascripts/tiny_mce/themes/simple/images/cleanup.gif",
        "/javascripts/tiny_mce/themes/simple/images/bullist.gif",
        "/javascripts/tiny_mce/themes/simple/images/numlist.gif",
        "/javascripts/tiny_mce/themes/simple/css/editor_content.css"
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
        "/javascripts/tiny_mce/tiny_mce.js",
        "/javascripts/tiny_mce/themes/simple/editor_template.js",
        "/javascripts/tiny_mce/langs/en.js",
        "/javascripts/tiny_mce/themes/simple/css/editor_ui.css",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/javascripts/tiny_mce/themes/simple/images/italic.gif",
        "/javascripts/tiny_mce/themes/simple/images/underline.gif",
        "/javascripts/tiny_mce/themes/simple/images/strikethrough.gif",
        "/javascripts/tiny_mce/themes/simple/images/undo.gif",
        "/javascripts/tiny_mce/themes/simple/images/separator.gif",
        "/javascripts/tiny_mce/themes/simple/images/redo.gif",
        "/javascripts/tiny_mce/themes/simple/images/cleanup.gif",
        "/javascripts/tiny_mce/themes/simple/images/bullist.gif",
        "/javascripts/tiny_mce/themes/simple/images/numlist.gif",
        "/javascripts/tiny_mce/themes/simple/css/editor_content.css"
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
        "/javascripts/tiny_mce/tiny_mce.js",
        "/javascripts/tiny_mce/themes/simple/editor_template.js",
        "/javascripts/tiny_mce/langs/en.js",
        "/javascripts/tiny_mce/themes/simple/css/editor_ui.css",
        "/images/reflec_tile.png",
        "/images/reflec_right.png",
        "/images/reflec_left.png",
        "/javascripts/tiny_mce/themes/simple/images/italic.gif",
        "/javascripts/tiny_mce/themes/simple/images/underline.gif",
        "/javascripts/tiny_mce/themes/simple/images/strikethrough.gif",
        "/javascripts/tiny_mce/themes/simple/images/undo.gif",
        "/javascripts/tiny_mce/themes/simple/images/separator.gif",
        "/javascripts/tiny_mce/themes/simple/images/redo.gif",
        "/javascripts/tiny_mce/themes/simple/images/cleanup.gif",
        "/javascripts/tiny_mce/themes/simple/images/bullist.gif",
        "/javascripts/tiny_mce/themes/simple/images/numlist.gif",
        "/javascripts/tiny_mce/themes/simple/css/editor_content.css"
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

    public UIDriver() throws XPathExpressionException {
        ctx = DriverContext.getContext();
        int scale = ctx.getScale();
        ScaleFactors.setActiveUsers(scale);
        http = new HttpTransport();
        // http.setFollowRedirects(true);
        logger = ctx.getLogger();
        random = ctx.getRandom();
        driverMetrics = new UIDriverMetrics();
        ctx.attachMetrics(driverMetrics);
        String hostPorts = ctx.getXPathValue(
                                "/web20/webServer/fa:hostConfig/fa:hostPorts");
        List<NameValuePair<Integer>> hostPortList =
                                            Utilities.parseHostPorts(hostPorts);
        int loadedScale = Integer.parseInt(
                                    ctx.getXPathValue("/web20/dbServer/scale"));
        loadedUsers = 4 * loadedScale;
        if (scale > loadedScale)
            throw new FatalException("Data loaded only for " + loadedScale +
                    " concurrent users. Run is set for " + scale +
                    " concurrent users. Please load for enough concurrent " +
                    "users. Run terminating!");

        //String type = ctx.getProperty("serverType");
        String type = "html";
        String resourcePath = ctx.getProperty("resourcePath");
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

            // Fetch the CSS/JS files

            loadStatics(homepageStatics);
        }
        loadImages(images);
        selectedEvent = RandomUtil.randomEvent(random, responseBuffer);
        logger.finer("Images loaded: " + imagesLoaded);
        logger.finer("Image bytes loaded: " + imgBytes);
        if (ctx.isTxSteadyState())
            driverMetrics.homePageImages += images.size();
            driverMetrics.homePageImagesLoaded += imagesLoaded;
            driverMetrics.homePageImageBytes += imgBytes;
    }

    @BenchmarkOperation (
        name    = "Login",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doLogin() throws IOException, Exception {
        logger.finer("In doLogin");
        int randomId = 0; //use as password
        username = null;

        if (!isLoggedOn) {
            randomId = selectUserID();
            username = UserName.getUserName(randomId);
            logger.fine("Logging in as " + username + ", " + randomId);
            http.readURL(loginURL, constructLoginPost(randomId), loginHeaders);
            // This redirects to home.
            http.fetchURL(homepageURL);
            int loginIdx = http.getResponseBuffer().indexOf("Login:");
            if (loginIdx != -1)
                throw new Exception(" Found login prompt at index " + loginIdx); 
            
            /*if (http.getResponseBuffer().indexOf("Login:") != 1216) {
                logger.finest(http.getResponseBuffer().toString());
                //throw new RuntimeException("Index of LOGIN = " +http.getResponseBuffer().indexOf("Login:") );
                //logger.info(http.getResponseBuffer().toString());
                throw new RuntimeException("Login as " + username + ", " +
                                                        randomId + " failed.");
            }*/
            //logger.fine("Login successful as " + username + ", " + randomId);
            isLoggedOn=true;
        } else {
            //already logged in --> logout,then log in again
            doLogout();
            doLogin();
        }
    }


    @BenchmarkOperation (
        name    = "Logout",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doLogout() throws IOException {
        if (isLoggedOn){
            logger.finer("Logging off = " + isLoggedOn);
            http.fetchURL(logoutURL);
            cachedURLs.clear();
            isCached = cached();
            isLoggedOn=false;
            http = new HttpTransport(); // clear all state
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
        max90th = 3,
        timing  = Timing.MANUAL
    )
    public void doAddEvent() throws IOException {
        logger.finer("doAddEvent");
        ctx.recordTime();
        http.readURL(addEventURL);
        loadStatics(addEventStatics);

        MultipartPostMethod post = new MultipartPostMethod(addEventResultURL);
        if(isLoggedOn) {
		    StringBuilder buffer = new StringBuilder(256);
		    post.addParameter("event[title]", RandomUtil.randomText(random, 15, 20));
		    post.addParameter("event[summary]", RandomUtil.randomText(random, 50, 200));
            post.addParameter("event[description]", RandomUtil.randomText(random, 100, 495));
            post.addParameter("event[telephone]", RandomUtil.randomPhone(random, buffer));
            post.addParameter("event[event_timestamp(1i)]", "2008");
            post.addParameter("event[event_timestamp(2i)]", "10");
            post.addParameter("event[event_timestamp(3i)]", "20");
            post.addParameter("event[event_timestamp(4i)]", "20");
            post.addParameter("event[event_timestamp(5i)]", "10");

            // We do the images last, not to split the fields into parts
            post.addParameter("event[image]", eventImg);
            post.addParameter("event[document]", eventPdf);
            post.addParameter("tag_list", random.makeCString(4, 14));

            addAddress(post);
            
            doMultiPartPost(post);
        }
        
        ctx.recordTime();
        ++driverMetrics.addEventTotal;
    }

    @BenchmarkOperation (
        name    = "AddPerson",
        max90th = 3,
        timing  = Timing.MANUAL
    )
    public void doAddPerson() throws IOException {
        logger.finer("doAddPerson");
        if (isLoggedOn)
            doLogout();

        ctx.recordTime();
        http.readURL(addPersonURL);
        loadStatics(addPersonStatics);

        MultipartPostMethod post = new MultipartPostMethod(addPersonResultURL);
        
        String fields[]  = new String[8];
        StringBuilder b = new StringBuilder(256);
        int id = loadedUsers + personsAdded++ * ScaleFactors.activeUsers +
                                                        ctx.getThreadId() + 1;
        String username = UserName.getUserName(id);
        if (username == null || username.length() == 0)
            logger.warning("Username is null!");
                            
        post.addParameter("user[username]", username);
        http.readURL(checkNameURL, "name=" + username);
        
        post.addParameter("user[password]", "" + id);
        post.addParameter("user[password_confirmation]", "" + id);
        post.addParameter("user[firstname]",  RandomUtil.randomName(random, b, 2, 12).toString());
        b.setLength(0);
        post.addParameter("user[lastname]",  RandomUtil.randomName(random, b, 5, 12).toString());
        post.addParameter("user[email]", username + "@" + random.makeCString(3, 10) + ".com");
        b.setLength(0);
        post.addParameter("user[telephone]", RandomUtil.randomPhone(random, b).toString());
        post.addParameter("user[summary]", RandomUtil.randomText(random, 50, 200));
        post.addParameter("user[timezone]", RandomUtil.randomTimeZone(random));
        
        // Images
        post.addParameter("user_image", personImg);        
        
        addAddress(post);
        doMultiPartPost(post);
        ctx.recordTime();
        ++driverMetrics.addPersonTotal;
    }

    @BenchmarkOperation (
        name    = "EventDetail",
        max90th = 2,
        timing  = Timing.AUTO
    )
    public void doEventDetail() throws IOException {
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
                                responseBuffer.indexOf("Attend") != -1;
        if (isLoggedOn && !canAddAttendee)
           logger.warning("Logged on and can't attend");

        Set<String> images = parseImages(responseBuffer);
        loadStatics(eventDetailStatics);
        loadImages(images);
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
                driverMetrics.eventDetailImages += images.size();

        }
    }

    @BenchmarkOperation (
        name = "PersonDetail",
        max90th = 2,
        timing = Timing.AUTO
    )
    public void doPersonDetail() throws IOException {
        logger.finer("doPersonDetail");
        StringBuilder buffer = new StringBuilder(fileServiceURL.length() + 20);
        //buffer.append(fileServiceURL).append("file=p");
        
        if (isLoggedOn) {
            int id = random.random(1, ScaleFactors.users);
            http.fetchURL(personDetailURL + id);
            StringBuilder responseBuffer = http.getResponseBuffer();
            if (responseBuffer.length() == 0)
                throw new IOException("Received empty response");

           // loadStatics(personStatics);
           // for (String url : personGets)
           //    http.readURL(url);

            // http.readURL(buffer.append(id).append(".jpg").toString());

            String event = RandomUtil.randomEvent(random, responseBuffer);
            if (event != null)
                selectedEvent = event;
        }
        else
        {
            // Need to handle this better. User profile is only for logged in users.
            logger.warning("Trying to view user, but not logged in");
            http.fetchURL(homepageURL);
        }
    }

    public void doAddAttendee() throws IOException {
        //can only add yourself (one attendee) to party
        http.readURL(addAttendeeURL + selectedEvent + "/attend", "");
        // http.readURL(updatePageURL, "list=attendees");
    }

    public Set<String> parseImages(StringBuilder buffer) {
        LinkedHashSet<String> urlSet = new LinkedHashSet<String>();
        String elStart = "<img ";
        String attrStart = " src=\"";
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
            int endIdx = buffer.indexOf("/>", idx);
            if (endIdx == -1)
                break;
            String elText = buffer.substring(idx, endIdx);
            idx = endIdx + 1;

            // Find the attribute
            int idx2 = elText.indexOf(attrStart);
            if (idx2 == -1) {
                logger.finer("No img src attribute. Weird! " + elText);
                continue;
            }
            endIdx = elText.indexOf("\"", idx2 + attrStartLen);
            if (endIdx == -1) {
                logger.warning("No img src attribute ending. Weird! " + elText);
                continue;
            }

            String link = elText.substring(idx2 + attrStartLen, endIdx);
            if (link.startsWith("fileService.")) {
                String url = baseURL + '/' + link;

                logger.finest("Adding " + url + " from idx " + idx);
                urlSet.add(url);
            }
        }
        return urlSet;
    }

    private void loadImages(Set<String> images) throws IOException {
        if (images != null)
            for (String image : images)
                // Loads image only if not cached, means we can add to cache.
                if (cachedURLs.add(image)) {
                    logger.finer("Loading image " + image);
                    imgBytes += http.readURL(image);
                    ++imagesLoaded;
                } else {
                    logger.finer("Image already cached: Not loading " + image);
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

        if (!isCached)
            for (String url : urls)
                if (cachedURLs.add(url)) {
                    logger.finer("Loading URL " + url);
                    http.readURL(url);
                } else {
                    logger.finer("URL already cached: Not loading " + url);
                }
    }

    public DateFormat getDateFormat() {
        if (df == null)
            df = new SimpleDateFormat("yyyy-MM-dd");
        return df;
    }

    public int selectUserID() {
        return random.random(0,3) * ScaleFactors.activeUsers +
                                                        ctx.getThreadId() + 1;
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

    private String constructLoginPost(int randomId) {
        return "users[username]=" + username + "&users[password]=" +
                String.valueOf(randomId) + "&submit=Login";
    }

    public void doMultiPartPost(MultipartPostMethod post) throws IOException {

        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        int status = client.executeMethod(post);
        if(status != HttpStatus.SC_OK)
            throw new IOException("Multipart Post did not work");
    }

    public void addAddress(MultipartPostMethod post)
    {
        //add the address
        post.addParameter("address[street1]", street1());
        post.addParameter("address[street2]",street2());
        post.addParameter("address[city]", random.makeCString(4, 14));
        post.addParameter("address[state]", random.makeCString(2, 2).toUpperCase());
        post.addParameter("address[zip]", random.makeNString(5, 5));
        post.addParameter("address[country]", country());
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

            el[3] = new Element();
            el[3].description = "Average images loaded per Home Page";
            el[3].target = "&gt;= 3";
            if (cnt > 0) {
                double avgImgs = homePageImagesLoaded / (double) cnt;
                el[3].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 3d)
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
                if (avgImgs >= 9d)
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
