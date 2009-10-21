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
 */
package org.apache.olio.workload.driver;

import com.sun.faban.driver.*;
import com.sun.faban.common.Utilities;
import com.sun.faban.common.NameValuePair;
import com.sun.faban.driver.transport.hc3.ApacheHC3Transport;
import org.apache.olio.workload.util.RandomUtil;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

@BenchmarkDefinition(
	name = "OlioRails",
	version = "0.2",
    scaleName = "Concurrent Users")
@BenchmarkDriver(name = "UIDriver",
threadPerScale = 1)
// 90/10 Read/Write ratio
@MatrixMix( //    operations = {"HomePage", "Login", , "TagSearch", "EventDetail", "PersonDetail",
//                  "Logout", "AddEvent",  "AddPerson"},
operations = {"HomePage", "Login", "TagSearch", "EventDetail", "PersonDetail", "AddPerson", "AddEvent"},
mix = {@Row({0, 11, 52, 36, 0, 1, 0}), // Home Page
    @Row({0, 0, 60, 20, 0, 0, 20}), // Login
    @Row({21, 6, 41, 31, 0, 1, 0}), // Tag Search
    @Row({72, 21, 0, 0, 6, 1, 0}), // Event Detail
    @Row({52, 6, 0, 31, 11, 0, 0}), // Person Detail
    @Row({0, 0, 0, 0, 100, 0, 0}), // Add Person
    @Row({0, 0, 0, 100, 0, 0, 0}) // Add Event
})
@NegativeExponential(cycleType = CycleType.CYCLETIME,
cycleMean = 5000,
cycleDeviation = 2)
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
    private String homepageURL,  loginURL,  logoutURL;
    private String addEventURL,  addPersonURL,  eventDetailURL;
    private String addEventResultURL,  addPersonResultURL;
    private String addAttendeeURL; //GET update.php?id=$eventid
    private String checkNameURL;
    // private String updatePageURL; //POST gettextafterinsert.php list=attendees
    private String fileServiceURL;
    private String[] homepageStatics, tagSearchStatics, personStatics, personGets,
            eventDetailStatics, addPersonStatics, addEventStatics;
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
    private LinkedHashMap<String, String> cachedHeaders;
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
        HttpTransport.setProvider("com.sun.faban.driver.transport.hc3.ApacheHC3Transport");
        http = HttpTransport.newInstance();

        logger = ctx.getLogger();
        random = ctx.getRandom();
        driverMetrics = new UIDriverMetrics();
        ctx.attachMetrics(driverMetrics);

		// We use the proxy hostPort if it is set. 
		// If not, then fall back to webserver.
        String hostPorts = ctx.getXPathValue(
                "/olio/proxyServer/fa:hostConfig/fa:hostPorts");
        List<NameValuePair<Integer>> hostPortList =
                Utilities.parseHostPorts(hostPorts);
        if (hostPortList.get(0).name == null) {
            hostPorts = ctx.getXPathValue(
                "/olio/webServer/fa:hostConfig/fa:hostPorts");
            hostPortList = Utilities.parseHostPorts(hostPorts);
        }

        int loadedScale = Integer.parseInt(
                ctx.getXPathValue("/olio/dbServer/scale"));
        loadedUsers = ScaleFactors.USERS_RATIO * loadedScale;
        if (scale > loadedScale) {
            throw new FatalException("Data loaded only for " + loadedScale +
                    " concurrent users. Run is set for " + scale +
                    " concurrent users. Please load for enough concurrent " +
                    "users. Run terminating!");
        }

        //String type = ctx.getProperty("serverType");
        String type = "html";
        String resourcePath = ctx.getResourceDir();
        if (!resourcePath.endsWith(File.separator)) {
            resourcePath += File.separator;
        }
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

        if (hostPort.value == null) {
            baseURL = "http://" + hostPort.name;
        } else {
            baseURL = "http://" + hostPort.name + ':' + hostPort.value;
        }
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

        if (hostPort.value == null) {
            loginHeaders.put("Host", hostPort.name);
        } else {
            loginHeaders.put("Host", hostPort.name + ':' + hostPort.value);
        }
        loginHeaders.put("User-Agent", "Mozilla/5.0");
        loginHeaders.put("Accept", "text/xml.application/xml,application/" +
                "xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;" +
                "q=0.5");

		// We don't want the rest of the loginheaders in cachedHeaders
		cachedHeaders = (LinkedHashMap)(loginHeaders.clone());

        loginHeaders.put("Accept-Language", "en-us,en;q=0.5");
        loginHeaders.put("Accept-Encoding", "gzip,deflate");
        loginHeaders.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        loginHeaders.put("Keep-Alive", "300");
        loginHeaders.put("Connection", "keep-alive");
        loginHeaders.put("Referer", homepageURL);

        isLoggedOn = false;
  
		// Create headers for if-modified-since
		String ifmod = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(BASE_DATE);
		cachedHeaders.put("If-Modified-Since", ifmod);
        isCached = cached();
    }

    @BenchmarkOperation(name = "HomePage",
    max90th = 1,
    timing = Timing.AUTO)
    public void doHomePage() throws IOException {
        logger.finer("HomePage: Accessing " + homepageURL);

        http.fetchURL(homepageURL);
        imgBytes = 0;
        imagesLoaded = 0;

        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0) {
            throw new IOException("Received empty response");
        }

        loadStatics(homepageStatics);
        Set<String> images = parseImages(responseBuffer);
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

    @BenchmarkOperation(name = "Login",
                        max90th = 1,
                        timing = Timing.AUTO)
    public void doLogin() throws IOException, Exception {
        logger.finer("In doLogin");
        int randomId = 0; //use as password
        username = null;

        if (isLoggedOn) {
            doLogout();
        }

        randomId = selectUserID();
        username = UserName.getUserName(randomId);
        logger.finer("Logging in as " + username + ", " + randomId);
        String loginPost = constructLoginPost(randomId);

        StringBuilder response = http.fetchURL(loginURL, loginPost);

        int loginIdx = response.indexOf("Login:");
        if (loginIdx != -1) {
            throw new Exception(" Found login prompt at index " + loginIdx);
        }

        logger.finer("Login successful as " + username + ", " + randomId);
        isLoggedOn = true;
    }

    @BenchmarkOperation(name = "Logout",
                        max90th = 1,
                        timing = Timing.AUTO)
    public void doLogout() throws IOException {
        if (isLoggedOn) {
            logger.finer("Logging off = " + isLoggedOn);
            http.readURL(logoutURL);
            cachedURLs.clear();
            isCached = cached();
            isLoggedOn = false;
        }
    }

    @BenchmarkOperation(name = "TagSearch",
    max90th = 2,
    timing = Timing.AUTO)
    public void doTagSearch() throws IOException {
        String tag = RandomUtil.randomTagName(random);
        String search = tagSearchURL + "?tag=" + tag + "&submit=Search+Tags";
        logger.finer("TagSearch: " + search);
        http.fetchURL(search);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0) {
            throw new IOException("Received empty response");
        }
        Set<String> images = parseImages(responseBuffer);
        loadStatics(tagSearchStatics);
        loadImages(images);
        String event = RandomUtil.randomEvent(random, responseBuffer);
        if (event != null) {
            selectedEvent = event;
        }
        if (ctx.isTxSteadyState()) {
            driverMetrics.tagSearchImages += images.size();
        }
    }

    @BenchmarkOperation(
        name = "AddEvent",
        max90th = 4,
        timing = Timing.AUTO)
    @NegativeExponential(
        cycleType = CycleType.CYCLETIME,
        cycleMean = 5000,
        cycleMin = 3000,
        truncateAtMin = false,
        cycleDeviation = 2)
    public void doAddEvent() throws Exception {
        logger.finer("entering doAddEvent()");
        if (!isLoggedOn) {
            throw new IOException("User not logged when trying to add an event");
        }

        ArrayList<Part> params = new ArrayList<Part>();
        String[] parameters = prepareEvent();
        if (parameters[0] == null || parameters[0].length() == 0) {
            logger.warning("Socialevent title is null!");
        } else {
            logger.finer("addEvent adding event title: " + parameters[0]);
        }

        params.add(new NullContentTypePart("event[title]", parameters[0]));
        params.add(new NullContentTypePart("event[summary]", parameters[1]));
        params.add(new NullContentTypePart("event[description]", parameters[2]));
        params.add(new NullContentTypePart("event[telephone]", parameters[3]));
// What about timezone ?
        params.add(new NullContentTypePart("event[event_timestamp(1i)]", parameters[5]));
        params.add(new NullContentTypePart("event[event_timestamp(2i)]", parameters[6]));
        params.add(new NullContentTypePart("event[event_timestamp(3i)]", parameters[7]));
        params.add(new NullContentTypePart("event[event_timestamp(4i)]", parameters[8]));
        params.add(new NullContentTypePart("event[event_timestamp(5i)]", parameters[9]));

        params.add(new NullContentTypePart("tag_list", parameters[10]));
// No submitter_user_name ?
        //add the address
        String[] addressArr = prepareAddress();
        params.add(new NullContentTypePart("address[street1]", addressArr[0]));
        params.add(new NullContentTypePart("address[street2]", addressArr[1]));
        params.add(new NullContentTypePart("address[city]", addressArr[2]));
        params.add(new NullContentTypePart("address[state]", addressArr[3]));
        params.add(new NullContentTypePart("address[zip]", addressArr[4]));
        params.add(new NullContentTypePart("address[country]", addressArr[5]));

        params.add(new FilePart("event_image", eventImg, "image/jpeg", null));
        params.add(new FilePart("event_document", eventPdf, "application/pdf",
                   null));
        params.add(new NullContentTypePart("commit", "Create"));

        // GET the new event form within a user session
        StringBuilder responseBuffer = http.fetchURL(addEventURL);
        loadStatics(addEventStatics);
        if (responseBuffer.length() == 0) {
            throw new IOException("Received empty response");
        }

        // Parse the authenticity_token from the response
        String token = parseAuthToken(responseBuffer);
        if (token != null) {
            params.add(new NullContentTypePart("authenticity_token", token));
        }

        StringBuilder response = ((ApacheHC3Transport) http).
                                 fetchURL(addEventResultURL, params);
        
        int status = http.getResponseCode();
        String[] locationHeader = http.getResponseHeader("location");

		if (locationHeader != null) {
			logger.fine("redirectLocation is " + locationHeader[0]);
			response = http.fetchURL(locationHeader[0]);
        } else if (status != HttpStatus.SC_OK) {
            throw new IOException("Multipart post did not work, returned " +
                                  "status code: " + status);
        }
        String message = "Event was successfully created.";
        if (response.indexOf(message) == -1) {
            throw new Exception("Could not find success message '" + message +
                                " in result body");
        }
        ++driverMetrics.addEventTotal;
    }

    @BenchmarkOperation(name = "AddPerson",
        max90th = 3,
        timing = Timing.AUTO)
    @NegativeExponential(
        cycleType = CycleType.CYCLETIME,
        cycleMean = 5000,
        cycleMin = 2000,
        truncateAtMin = false,
        cycleDeviation = 2)
    public void doAddPerson() throws Exception {
        logger.finer("doAddPerson");
        if (isLoggedOn) {
            doLogout();
        }

        String[] parameters = preparePerson();
        ArrayList<Part> params = new ArrayList();
        // Debug
        if (parameters[0] == null || parameters[0].length() == 0) {
            logger.warning("Username is null!");
        } else {
            logger.finer("addPerson adding user: " + parameters[0]);
        }
        params.add(new NullContentTypePart("user[username]", parameters[0]));
        http.readURL(checkNameURL, "name=" + parameters[0]);

        params.add(new NullContentTypePart("user[password]", parameters[1]));
        params.add(new NullContentTypePart("user[password_confirmation]",
                                                            parameters[1]));
        params.add(new NullContentTypePart("user[firstname]", parameters[2]));
        params.add(new NullContentTypePart("user[lastname]", parameters[3]));
        params.add(new NullContentTypePart("user[email]", parameters[4]));
        String[] addressArr = prepareAddress();
        params.add(new NullContentTypePart("address[street1]", addressArr[0]));
        params.add(new NullContentTypePart("address[street2]", addressArr[1]));
        params.add(new NullContentTypePart("address[city]", addressArr[2]));
        params.add(new NullContentTypePart("address[state]", addressArr[3]));
        params.add(new NullContentTypePart("address[zip]", addressArr[4]));
        params.add(new NullContentTypePart("address[country]", addressArr[5]));
        params.add(new NullContentTypePart("user[telephone]", parameters[5]));
        params.add(new NullContentTypePart("user[timezone]", parameters[7]));
        params.add(new NullContentTypePart("user[summary]", parameters[6]));
        params.add(new FilePart("user_image", personImg, "image/jpeg", null));

        http.readURL(addPersonURL);
        loadStatics(addPersonStatics);
        StringBuilder response = ((ApacheHC3Transport)http).
                                 fetchURL(addPersonResultURL, params);
        
        int status = http.getResponseCode();
        String[] locationHeader = http.getResponseHeader("location");

		if (locationHeader != null) {
			logger.fine("redirectLocation is " + locationHeader[0]);
			response = http.fetchURL(locationHeader[0]);
        } else if (status != HttpStatus.SC_OK) {
            throw new IOException("Multipart post did not work, returned " +
                                  "status code: " + status);
        }
        String message = "Succeeded in creating user.";
        if (response.indexOf(message) == -1) {
            throw new Exception("Could not find success message '" + message +
                    " in result body");
        }
        ++driverMetrics.addPersonTotal;
    }


    @BenchmarkOperation(name = "EventDetail",
    max90th = 2,
    timing = Timing.AUTO)
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
        if (responseBuffer.length() == 0) {
            throw new IOException("Received empty response");
        }
        boolean canAddAttendee = isLoggedOn &&
                responseBuffer.indexOf("Unattend") == -1;

        Set<String> images = parseImages(responseBuffer);
        loadStatics(eventDetailStatics);
        loadImages(images);
        if (ctx.isTxSteadyState()) {
            driverMetrics.eventDetailImages += images.size();
        }
        if (canAddAttendee) {
            // 10% of the time we can add ourselves, we will.
            int card = random.random(0, 9);
            if (card == 0) {
                doAddAttendee();
                if (ctx.isTxSteadyState()) {
                    ++driverMetrics.addAttendeeCount;
                }
            }
            if (ctx.isTxSteadyState()) {
                ++driverMetrics.addAttendeeReadyCount;
            }
        }
    }

    @BenchmarkOperation(name = "PersonDetail",
    max90th = 2,
    timing = Timing.AUTO)
    public void doPersonDetail() throws IOException {
        logger.finer("doPersonDetail");
        StringBuilder buffer = new StringBuilder(fileServiceURL.length() + 20);
        //buffer.append(fileServiceURL).append("file=p");

        // Shanti: No need to be logged on to see user
        /**
        if (isLoggedOn) {
         **/
        int id = random.random(1, ScaleFactors.users);
        StringBuilder response = http.fetchURL(personDetailURL + id);
        if (response.length() == 0) {
            throw new IOException("Received empty response");
        }
        Set<String> images = parseImages(response);
        loadImages(images);
        String event = RandomUtil.randomEvent(random, response);
        if (event != null) {
            selectedEvent = event;
        }
        /***
        }
        else
        {
        // Need to handle this better. User profile is only for logged in users.
        logger.warning("Trying to view user, but not logged in");
        http.fetchURL(homepageURL);
        }
         ***/
    }

    public void doAddAttendee() throws Exception {
        //can only add yourself (one attendee) to party
        // Need to add header that will request js instead of
        // html. This will prevent the redirect.
        HashMap<String, String> header = new HashMap<String, String>(1);
        header.put("Accept", "text/javascript");
		// Need to send only post request, get doesn't work. So send null post.
        StringBuilder response = http.fetchURL(addAttendeeURL +
                selectedEvent + "/attend", "", header);
        int status = http.getResponseCode();
        if (status != HttpStatus.SC_OK) {
            throw new Exception("Add attendee returned: " + status);
        }
        if (response.indexOf("You are attending") == -1) {
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
            if (idx == -1) {
                break;
            }
            idx += elStartLen;
            int endIdx = buffer.indexOf(")", idx) + 1; // +1 to include the '('
            if (endIdx == -1) {
                break;
            }
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

    private String parseAuthToken(StringBuilder responseBuffer)
            throws IOException {

        int idx = responseBuffer.indexOf("authenticity_token");

        if (idx == -1) {
            logger.info("Trying to add event but authenticity token not found");
            return null;
        }

        int endIdx = responseBuffer.indexOf("\" />", idx);

        if (endIdx == -1) {
            throw new IOException("Invalid authenticity_token element. Buffer(100 chars) = " +
                    responseBuffer.substring(idx, idx + 100));
        }

        String tmpString = responseBuffer.substring(idx, endIdx);
        String[] splitStr = tmpString.split("value=\"");

        if (splitStr.length < 2) {
            throw new IOException("Invalid authenticity_token element. Buffer(100 chars) = " +
                    responseBuffer.substring(idx, idx + 100));
        }

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
        // Comment out next line to test if-modified-since headers
        // if (!isCached) {
            for (String url : urls) {
            // If we are simulating browser caching, send if-modified-since
           // hdr. We won't check return code.
                if (isCached)
                    http.readURL(url, cachedHeaders);
                else {
                    if (cachedURLs.add(url)) {
                        logger.finer("Loading URL " + url);
                        http.readURL(url);
                    } else {
                        logger.finer("URL already cached: Not loading " + url);
                    }
                }
            }
        // }
    }

    public DateFormat getDateFormat() {
        if (df == null) {
            df = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        }
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
                System.out.println("Exception - " + e);
                e.printStackTrace();
            }
        }
        return returnList;
    }

    private String constructLoginPost(int randomId) {
        StringBuilder postString = new StringBuilder();
        postString.append("users[username]=").append(username);
        postString.append("&users[password]=").append(randomId);
        postString.append("&submit=").append("Login");

        return postString.toString();

    }

    public String[] prepareEvent() {

        String fields[] = new String[11];
        StringBuilder buffer = new StringBuilder(256);
        fields[0] = RandomUtil.randomText(random, 15, 20); //title
        fields[1] = RandomUtil.randomText(random, 50, 200); //summary
        fields[2] = RandomUtil.randomText(random, 100, 495); // description

        int numTags = random.random(1, 7); // Avg is 4 tags per event
        for (int i = 0; i < numTags; i++) {
            tagSet.add(RandomUtil.randomTagId(random, 0.1d));
        }

        for (int tagId : tagSet) {
            tags.append(UserName.getUserName(tagId)).append(' ');
        }
        tags.setLength(tags.length() - 1);

        fields[10] = tags.toString();
        tags.setLength(0);
        tagSet.clear();

        fields[3] = RandomUtil.randomPhone(random, buffer); //phone
        fields[4] = RandomUtil.randomTimeZone(random); // timezone
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

        String[] STREETEXTS = {"Blvd", "Ave", "St", "Ln", ""};
        StringBuilder buffer = new StringBuilder(255);
        buffer.append(random.makeNString(1, 5)).append(' '); // number
        RandomUtil.randomName(random, buffer, 1, 11); // street
        String streetExt = STREETEXTS[random.random(0, STREETEXTS.length - 1)];
        if (streetExt.length() > 0) {
            buffer.append(' ').append(streetExt);
        }
        String[] fields = new String[6];
        fields[0] = buffer.toString();

        int toggle = random.random(0, 1); // street2
        if (toggle > 0) {
            fields[1] = random.makeCString(5, 20);
        } else {
            fields[1] = "";
        }

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
        String fields[] = new String[8];
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
                if (pctAdd >= 6d) {
                    el[0].passed = Boolean.TRUE;
                } else {
                    el[0].passed = Boolean.FALSE;
                }
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
                if (imagesPerPage >= 9.5d && imagesPerPage <= 10.5d) {
                    el[2].passed = Boolean.TRUE;
                } else {
                    el[2].passed = Boolean.FALSE;
                }
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
                if (avgImgs >= 2.5) {
                    el[3].passed = Boolean.TRUE;
                } else {
                    el[3].passed = Boolean.FALSE;
                }
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
                if (avgBytes >= 15000) {
                    el[4].passed = Boolean.TRUE;
                } else {
                    el[4].passed = Boolean.FALSE;
                }
            } else {
                el[4].result = "";
                el[4].passed = Boolean.FALSE;
            }
            cnt = r.getOpsCountSteady("TagSearch");
            el[5] = new Element();
            el[5].description = "Average images on Tag Search Results";
            //el[5].target = "&gt;= 3.6";
            el[5].target = "&gt;= 0";
            if (cnt > 0) {
                double avgImgs = tagSearchImages / (double) cnt;
                el[5].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 0) {
                    el[5].passed = Boolean.TRUE;
                } else {
                    el[5].passed = Boolean.FALSE;
                }
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
                if (avgImgs >= 1d) {
                    el[6].passed = Boolean.TRUE;
                } else {
                    el[6].passed = Boolean.FALSE;
                }
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
            if (ratio <= 5.25d) {
                el[9].passed = true;
            } else {
                el[9].passed = false;
            }
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
    
    /*
     * StringPart doesn't work for Rack 1.0.0 unless it's contentType
     * is set to null
     */
    class NullContentTypePart extends StringPart {
      public NullContentTypePart(String name, String value) {
          super(name, value);
          this.setContentType(null);
      }
    }
}
