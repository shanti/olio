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
 * $Id: UIDriver.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */ 
package org.apache.olio.workload.driver;

import com.sun.faban.common.NameValuePair;
import com.sun.faban.common.Utilities;
import com.sun.faban.driver.*;
import org.apache.olio.workload.util.RandomUtil;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
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
    name    = "OlioWorkload",
    version = "0.1",
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

    mix = { @Row({  0, 11, 52, 36,  0, 1,  0 }),
            @Row({  0,  0, 60, 20,  0, 0, 20 }),
            @Row({ 21,  6, 41, 31,  0, 1,  0 }),
            @Row({ 72, 21,  0,  0,  6, 1,  0 }),
            @Row({ 52,  6,  0, 31, 11, 0,  0 }),
            @Row({ 0, 0,  0,  0,  100, 0,  0 }),
            @Row({  0,  0, 0, 100,  0, 0,  0 })
          }
)
@NegativeExponential (
    cycleType = CycleType.CYCLETIME,
    cycleMean = 5000,
    cycleDeviation = 2
)

public class UIDriver {

    public static final String[] HOME_STATICS = {
        "/js/tiny_mce/tiny_mce.js",
        "/js/prototype.js",
        "/js/effects.js",
        "/js/dragdrop.js",
        "/js/controls.js",
        "/js/application.js",
        "/css/scaffold.css",
        "/css/site.css",
        "/images/bg_main.png",
        "/images/RSS-icon-large.gif",
        "/images/php_bg_header.gif",
        "/images/php_main_nav_link_bg.gif",
        "/images/php_corner_top_right.gif",
        "/images/php_corner_top_left.gif",
        "/images/php_corner_bottom_right.gif",
        "/images/php_corner_bottom_left.gif",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_right.gif",
        "/images/php_reflec_left.gif",
        "/images/php_main_nav_hover_bg.gif"
    };

    public static final String[] EVENTDETAIL_STATICS = {
        "/images/php_main_nav_hover_bg.gif",
        "/js/tiny_mce/tiny_mce.js",
        "/js/prototype.js",
        "/js/effects.js",
        "/js/dragdrop.js",
        "/js/controls.js",
        "/js/application.js",
        "/css/scaffold.css",
        "/css/site.css",
        "/images/bg_main.png",
        "/images/RSS-icon-large.gif",
        "/images/php_bg_header.gif",
        "/images/php_main_nav_link_bg.gif",
        "/js/starrating.js",
        "/images/php_corner_top_right.gif",
        "/images/php_corner_top_left.gif",
        "/images/star_on.png",
        "/images/star_off.png",
        "/images/php_corner_bottom_right.gif",
        "/images/php_corner_bottom_left.gif",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_right.gif",
        "/images/php_reflec_left.gif"
    };

    public static final String[] ADDPERSON_STATICS = {
        "/js/tiny_mce/tiny_mce.js",
        "/js/prototype.js",
        "/js/effects.js",
        "/js/dragdrop.js",
        "/js/controls.js",
        "/js/application.js",
        "/css/scaffold.css",
        "/css/site.css",
        "/images/bg_main.png",
        "/images/RSS-icon-large.gif",
        "/images/php_bg_header.gif",
        "/images/php_main_nav_link_bg.gif",
        "/js/validateform.js",
        "/images/php_corner_top_right.gif",
        "/images/php_corner_top_left.gif",
        "/js/tiny_mce/themes/simple/editor_template.js",
        "/js/tiny_mce/langs/en.js",
        "/js/tiny_mce/themes/simple/css/editor_ui.css",
        "/images/php_corner_bottom_right.gif",
        "/images/php_corner_bottom_left.gif",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_right.gif",
        "/images/php_reflec_left.gif",
        "/js/tiny_mce/themes/simple/images/bold.gif",
        "/js/tiny_mce/themes/simple/images/italic.gif",
        "/js/tiny_mce/themes/simple/images/underline.gif",
        "/js/tiny_mce/themes/simple/images/strikethrough.gif",
        "/js/tiny_mce/themes/simple/images/separator.gif",
        "/js/tiny_mce/themes/simple/images/undo.gif",
        "/js/tiny_mce/themes/simple/images/redo.gif",
        "/js/tiny_mce/themes/simple/images/cleanup.gif",
        "/js/tiny_mce/themes/simple/images/bullist.gif",
        "/js/tiny_mce/themes/simple/images/numlist.gif"
    };

    public static final String[] ADDEVENT_STATICS = {
        "/js/tiny_mce/tiny_mce.js",
        "/js/prototype.js",
        "/js/effects.js",
        "/js/dragdrop.js",
        "/js/controls.js",
        "/js/application.js",
        "/css/scaffold.css",
        "/css/site.css",
        "/images/bg_main.png",
        "/images/RSS-icon-large.gif",
        "/images/php_bg_header.gif",
        "/images/php_main_nav_link_bg.gif",
        "/images/php_corner_top_right.gif",
        "/images/php_corner_top_left.gif",
        "/js/tiny_mce/themes/simple/editor_template.js",
        "/js/tiny_mce/langs/en.js",
        "/js/tiny_mce/themes/simple/css/editor_ui.css",
        "/js/validateform.js",
        "/images/php_corner_bottom_right.gif",
        "/images/php_corner_bottom_left.gif",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_right.gif",
        "/images/php_reflec_left.gif",
        "/js/tiny_mce/themes/simple/images/bold.gif",
        "/js/tiny_mce/themes/simple/images/italic.gif",
        "/js/tiny_mce/themes/simple/images/underline.gif",
        "/js/tiny_mce/themes/simple/images/strikethrough.gif",
        "/js/tiny_mce/themes/simple/images/separator.gif",
        "/js/tiny_mce/themes/simple/images/undo.gif",
        "/js/tiny_mce/themes/simple/images/redo.gif",
        "/js/tiny_mce/themes/simple/images/cleanup.gif",
        "/js/tiny_mce/themes/simple/images/bullist.gif",
        "/js/tiny_mce/themes/simple/images/numlist.gif"
    };


    public static final String[] PERSON_STATICS = {
        "/js/tiny_mce/tiny_mce.js",
        "/js/prototype.js",
        "/js/effects.js",
        "/js/dragdrop.js",
        "/js/controls.js",
        "/js/application.js",
        "/css/scaffold.css",
        "/css/site.css",
        "/images/bg_main.png",
        "/images/RSS-icon-large.gif",
        "/images/php_bg_header.gif",
        "/images/php_main_nav_link_bg.gif",
        "/images/php_corner_top_right.gif",
        "/images/php_corner_top_left.gif",
        "/images/php_corner_bottom_right.gif",
        "/images/php_corner_bottom_left.gif",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_right.gif",
        "/images/php_reflec_left.gif"
    };

    

    public static final String[] TAGSEARCH_STATICS = {
        "/js/tiny_mce/tiny_mce.js",
        "/js/prototype.js",
        "/js/effects.js",
        "/js/dragdrop.js",
        "/js/controls.js",
        "/js/application.js",
        "/css/scaffold.css",
        "/css/site.css",
        "/images/bg_main.png",
        "/images/RSS-icon-large.gif",
        "/images/php_bg_header.gif",
        "/images/php_main_nav_link_bg.gif",
        "/images/php_corner_top_right.gif",
        "/images/php_corner_top_left.gif",
        "/images/php_corner_bottom_right.gif",
        "/images/php_corner_bottom_left.gif",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_right.gif",
        "/images/php_reflec_left.gif",
        "/images/php_main_nav_hover_bg.gif"
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
    private StringBuilder tags = new StringBuilder();
    private LinkedHashSet<Integer> tagSet = new LinkedHashSet<Integer>(7);

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

        String type = ctx.getProperty("serverType");
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

        personDetailURL = baseURL + "/users." + type + "?username=";
        tagSearchURL = baseURL + "/taggedEvents." + type;
        tagCloudURL = baseURL + "/taggedEvents." + type;
        addEventURL = baseURL + "/addEvent." + type;
        addEventResultURL = baseURL + "/addEventResult." + type;
        addPersonURL = baseURL + "/addPerson." + type;
        addPersonResultURL = baseURL + "/addPersonResult." + type;
        homepageURL = baseURL + "/index." + type;
        loginURL = baseURL + "/login." + type;
        logoutURL = baseURL + "/logout." + type;
        addAttendeeURL = baseURL + "/addAttendee." + type + "?id=";
        // updatePageURL = baseURL + "/gettextafterinsert." + type;
        eventDetailURL = baseURL + "/events." + type + "?socialEventID=";
        fileServiceURL = baseURL + "/fileService." + type + '?';

        homepageStatics = populateList(HOME_STATICS);
        personStatics = populateList(PERSON_STATICS);
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
          
            int loginIdx = http.getResponseBuffer().indexOf("Username");
            if (loginIdx != -1){
                throw new RuntimeException("Found login prompt at index " + loginIdx + ", Login as " + username + ", " +
                                                        randomId + " failed.");
                //throw new Exception("Found login prompt at index " + loginIdx); 
            }
            
            /*
            if (http.getResponseBuffer().indexOf("Login:") != 1290) {
                logger.finest(http.getResponseBuffer().toString());
                //throw new RuntimeException("Index of LOGIN = " +http.getResponseBuffer().indexOf("Login:") );
                //logger.info(http.getResponseBuffer().toString());
                logger.info("Index of LOGIN = " +http.getResponseBuffer().indexOf("Login:") );
                throw new RuntimeException("Login as " + username + ", " +
                                                        randomId + " failed.");
            }
            */
            isLoggedOn=true;
            logger.fine("Login successful as " + username + ", " + randomId);
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
        int count = random.random(100, 150);
        String post = "tag=" + tag + "&tagsearchsubmit=Search+Tags";
        logger.finer("TagSearch: " + tagSearchURL + " Post: " + post);
        http.readURL(tagSearchURL, post);
        //if (http.getResponseCode() != 302)
        //    logger.warning("Tag search response not redirecting.");
      
        http.fetchURL(tagCloudURL + "?tag=" + tag + "&count=" + count);
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
    public void doAddEvent() throws IOException {
        logger.finer("doAddEvent");
        ctx.recordTime();
        http.readURL(addEventURL);
        loadStatics(addEventStatics);

        MultipartPostMethod post = new MultipartPostMethod(addEventResultURL);
        if(isLoggedOn) {
            String[] parameters = prepareEvent();
            if (parameters[0] == null || parameters[0].length() == 0)
                logger.warning("Socialevent title is null!");
            else
                logger.finer("addEvent adding event title: " + parameters[0]);
            
            post.addParameter("title", parameters[0]);
            post.addParameter("description", parameters[1]);
            post.addParameter("telephone", parameters[3]);
            post.addParameter("timezone", parameters[4]);
            //add the address
            String[] addressArr = prepareAddress();
            post.addParameter("street1", addressArr[0]);
            post.addParameter("street2", addressArr[1]);
            post.addParameter("city", addressArr[2]);
            post.addParameter("state", addressArr[3]);
            post.addParameter("zip", addressArr[4]);
            post.addParameter("country", addressArr[5]);
            post.addParameter("year",parameters[5]);
            post.addParameter("month", parameters[6]);
            post.addParameter("day", parameters[7]);
            post.addParameter("hour", parameters[8]);
            post.addParameter("minute", parameters[9]);
            post.addParameter("tags", parameters[2]);
            post.addParameter("submitter_user_name", username);
            // We do the images last, not to split the fields into parts
            post.addParameter("upload_image", eventImg);
//            post.addParameter("eventThumbnail", eventThumb);
            post.addParameter("upload_literature",eventPdf);
            post.addParameter("addeventsubmit", "Create");

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
        String[] parameters = preparePerson();
        
        // Debug
        if (parameters[0] == null || parameters[0].length() == 0)
            logger.warning("Username is null!");
        else
            logger.finer("addPerson adding user: " + parameters[0]);
        
        post.addParameter("add_user_name", parameters[0]);
        post.addParameter("psword", parameters[1]);
        post.addParameter("passwordx", parameters[1]);
        post.addParameter("first_name", parameters[2]);
        post.addParameter("last_name", parameters[3]);
        post.addParameter("email",parameters[4]);
        String[] addressArr = prepareAddress();
        post.addParameter("street1",addressArr[0]);
        post.addParameter("street2",addressArr[1]);
        post.addParameter("zip", addressArr[4]);
        post.addParameter("city", addressArr[2]);
        post.addParameter("state", addressArr[3]);
        post.addParameter("country", addressArr[5]);
        post.addParameter("telephone",parameters[5]);
        post.addParameter("timezone", parameters[7]);
        post.addParameter("user_image", personImg);
//        post.addParameter("user_thumbnail",personThumb);
        post.addParameter("summary", parameters[6]);
        post.addParameter("addpersonsubmit", "Create");
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
            http.fetchURL(eventDetailURL + selectedEvent);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        boolean canAddAttendee = isLoggedOn &&
                                responseBuffer.indexOf("Attend") != -1;
//        if (isLoggedOn && !canAddAttendee)
//            logger.warning(responseBuffer.toString());
        Set<String> images = parseImages(responseBuffer);
        loadStatics(eventDetailStatics);
        loadImages(images);
        int card = -1;
        if (canAddAttendee) {
            // 10% of the time we can add ourselves, we will.
            card = random.random(0, 9);
            if (card == 0)
                doAddAttendee();
        }

        if (ctx.isTxSteadyState()) {
            driverMetrics.eventDetailImages += images.size();
            if (canAddAttendee) {
                ++driverMetrics.addAttendeeReadyCount;
                if (card == 0)
                    ++driverMetrics.addAttendeeCount;
            }
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
        
        // TODO: account for new users when loading images, too.
        buffer.append(fileServiceURL).append("file=p");
        int id = random.random(1, ScaleFactors.users);
        http.fetchURL(personDetailURL + UserName.getUserName(id));
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");

        loadStatics(personStatics);       
        http.readURL(buffer.append(id).append(".jpg").toString());
    }

    public void doAddAttendee() throws IOException {
        //can only add yourself (one attendee) to party
        http.readURL(addAttendeeURL + selectedEvent);
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
                    int imgSize = http.readURL(image);
                    if (imgSize < 1024) {
                        logger.warning("Image at " + image + " size of " +
                                        imgSize + " bytes is too small. " +
                                        "Image may not exist");
                    }
                    imgBytes += imgSize;
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
            df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
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

    private String constructLoginPost(int randomId) {
        return "user_name=" + username + "&password=" +
                String.valueOf(randomId) + "&submit=Login";
    }

    public void doMultiPartPost(MultipartPostMethod post) throws IOException {

        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        int status = client.executeMethod(post);
        if(status != HttpStatus.SC_OK)
            throw new IOException("Multipart Post did not work");
    }

    public String[] prepareEvent()  {

        String fields[]  = new String[10];
        StringBuilder buffer = new StringBuilder(256);
        fields[0] = RandomUtil.randomText(random, 15, 20); //title
        fields[1] = RandomUtil.randomText(random, 50, 495); // description

        int numTags = random.random(1, 7); // Avg is 4 tags per event
        for (int i = 0; i < numTags; i++)
            while (!tagSet.add(RandomUtil.randomTagId(random, 0.1d)));

        for (int tagId : tagSet)
            tags.append(UserName.getUserName(tagId)).append(' ');
        tags.setLength(tags.length() - 1);

        fields[2]= tags.toString();
        tags.setLength(0);
        tagSet.clear();

        fields[3]= RandomUtil.randomPhone(random, buffer); //phone
        fields[4]= RandomUtil.randomTimeZone(random); // timezone
        DateFormat dateFormat = getDateFormat(); // eventtimestamp
        String dateTime = dateFormat.format( //eventtimestamp
                random.makeDateInInterval(BASE_DATE, 0, 540));
        StringTokenizer t = new StringTokenizer(dateTime, "-");
        int i = 5;
        while (t.hasMoreTokens()) {
            fields[i++] = t.nextToken();
        }
        return fields;
    }

    public String[] prepareAddress() {

        String[] STREETEXTS = { "Blvd", "Ave", "St", "Ln", "" };
        StringBuilder buffer = new StringBuilder(255);
        buffer.append(random.makeNString(1, 5)).append(' '); // number
        RandomUtil.randomName(random, buffer, 1, 11); // street
        String streetExt = STREETEXTS[random.random(0, STREETEXTS.length - 1)];
        if (streetExt.length() > 0)
            buffer.append(' ').append(streetExt);
        String[] fields = new String[6];
        fields[0] = buffer.toString();

        int toggle = random.random(0, 1); // street2
        if (toggle > 0)
            fields[1] = random.makeCString(5, 20);
        else
            fields[1] = "";

        fields[2] = random.makeCString(4, 14); // city
        fields[3] = random.makeCString(2, 2).toUpperCase(); // state
        fields[4] = random.makeNString(5, 5);  // zip

        toggle = random.random(0, 1);
        if (toggle == 0) {
            fields[5] = "USA";
        } else {
            buffer.setLength(0);
            fields[5] = RandomUtil.randomName(random, buffer, 6, 16).toString();
        }
        return fields;
    }

    public String[] preparePerson() {
        String fields[]  = new String[8];
        StringBuilder b = new StringBuilder(256);
        int id = loadedUsers + personsAdded++ * ScaleFactors.activeUsers +
                                                        ctx.getThreadId() + 1;
        fields[0] = UserName.getUserName(id);
        //use the same field for repeating the password field.
        fields[1] = String.valueOf(id);
        fields[2] = RandomUtil.randomName(random, b, 2, 12).toString();
        b.setLength(0);
        fields[3] = RandomUtil.randomName(random, b, 5, 15).toString();
        fields[4] = random.makeCString(3, 10);
        fields[4] = fields[2] + '_' + fields[3] + '@' + fields[4] + ".com";
        b.setLength(0);
        fields[5] = RandomUtil.randomPhone(random, b);
        fields[6] = random.makeAString(250, 2500);
        fields[7] = RandomUtil.randomTimeZone(random);
        return fields;
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
