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
 * $Id: UIDriver.java,v 1.14 2007/07/27 19:03:05 akara Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package org.apache.olio.workload.driver;

import com.sun.faban.driver.*;
import com.sun.faban.common.Utilities;
import com.sun.faban.common.NameValuePair;
import org.apache.olio.workload.util.RandomUtil;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
import java.util.logging.Level;
import org.apache.commons.httpclient.methods.MultipartPostMethod;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;

@BenchmarkDefinition (
    name    = "Java Web 2.0 Workload",
    version = "0.2",
    scaleName = "Concurrent Users"

)
@BenchmarkDriver (
    name           = "JavaUIDriver",
    threadPerScale    = 1
)
        // 90/10 Read/Write ratio
       
@MatrixMix (
//    operations = {"HomePage", "Login", , "TagSearch", "EventDetail", "PersonDetail",
//                  "Logout", "AddEvent",  "AddPerson"},    
/*
    operations = { "HomePage", "Login", "TagSearch", "EventDetail", "PersonDetail", "AddPerson", "AddEvent" },
    mix = { @Row({  0, 10, 50, 35,  0, 1,  0 }),
            @Row({  0,  0, 60, 20,  0, 0, 20 }),
            @Row({ 20,  5, 40, 30,  0, 1,  0 }),
            @Row({ 70, 20,  0,  0,  5, 1,  0 }),
            @Row({ 50,  5,  0, 30, 10, 0,  0 }),
            @Row({ 30, 65,  0,  0,  5, 0,  0 }),
            @Row({  0,  0, 25, 75,  0, 0,  0 })
          }
*/
   //updated matrix from Olio 
operations = { "HomePage", "Login", "TagSearch", "EventDetail", "PersonDetail", "AddPerson", "AddEvent" },

    mix = { @Row({  0, 11, 52, 36,  0, 1,  0 }),
            @Row({  0,  0, 60, 20,  0, 0, 20 }),
            @Row({ 21,  6, 41, 31,  0, 1,  0 }),
            @Row({ 72, 21,  0,  0,  6, 1,  0 }),
            @Row({ 52,  6,  0, 31, 11, 0,  0 }),
            @Row({ 0, 0,  0,  0,  100, 0,  0 }),
            @Row({  0,  0, 0, 100,  0, 0,  0 })
          }
    // Binu's mix for testing
    /*
    mix = { @Row({  0, 11, 52, 36,  0, 0,  0 }),
            @Row({  0,  0, 60, 20,  0, 0, 20 }),
            @Row({ 21,  6, 41, 31,  0, 0,  0 }),
            @Row({ 72, 21,  0,  0,  0, 0,  0 }),
            @Row({ 52,  6,  0, 31, 0, 0,  0 }),
            @Row({ 100, 0,  0,  0,  0, 0,  0 }),
            @Row({  0,  0, 0, 100,  0, 0,  0 })
          }   
     */
)
@NegativeExponential (
    cycleType = CycleType.CYCLETIME,
    cycleMean = 5000,
    cycleDeviation = 2
)

public class JavaUIDriver {
    // These are the common static files present in all pages.
    public static final String[] SITE_STATICS = {       
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
        "/resources/jmaki-min.js",
        "/glue.js",
        "/resources/system-glue.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/yahoo-dom-event/yahoo-dom-event.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/element/element-beta-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/container/container_core-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/menu/menu-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/button/button-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/datasource/datasource-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/calendar/calendar-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/menu/assets/skins/sam/menu.css",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/button/assets/skins/sam/button.css",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/calendar/assets/skins/sam/calendar.css",
        "/resources/yahoo/calendar/component.js",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_left.gif",
        "/images/php_reflec_right.gif",
        "/resources/config.json",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/assets/skins/sam/sprite.png"        
    };

    // This is in adition to SITE_STATICS
    public static final String[] HOME_STATICS = {
        "/js/httpobject.js",
        "/js/dragdrop.js",
        "/js/effects.js",
        "/js/prototype.js"
    };
    
    // This is in addition to SITE_STATICS.
    public static final String[] EVENTDETAIL_COMMON_STATICS = { 
        "/js/starrating.js",
        "/js/httpobject.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/yahoo-dom-event/yahoo-dom-event.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/dragdrop/dragdrop-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/animation/animation-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/connection/connection-min.js",
        "/resources/yahoo/map/component.js",
        "/images/star_off.png",
        "/images/star_on.png"
    };
    
    public static final String[] EVENTDETAIL_BASE_STATICS = {
        "/js/attendee.js",
        "/js/comments.js"
    };
    
    public static final String[] EVENTDETAIL_JMAKI_STATICS = {
        "/resources/blueprints/list/attendeeList/component.css", 
        "/resources/blueprints/list/attendeeList/component.js",
        "/resources/blueprints/list/commentList/component.css",
        "/resources/blueprints/list/commentList/component.js"
    };
    
    public static final String[] ADDPERSON_STATICS = {
        "/js/validateform.js",
        "/js/httpobject.js"
    };

    // In addition to SITE_STATIC
    public static final String[] ADDEVENT_STATICS = {
        "/js/validateform.js",
        "/js/httpobject.js"
    };


    public static final String[] PERSON_STATICS = {
        "/js/httpobject.js"
    };
    
    public static final String[] PERSON_GETS = {
        "/api/person?user_name=@USER_NAME@_&actionType=get_friends",
        "/api/person?user_name=@USER_NAME@&actionType=get_attend_events",
        "/api/person?user_name=@USER_NAME@&actionType=get_posted_events"
    };

    // In addition to SITE_STATICS
    public static final String[] TAGSEARCH_STATICS = { 
        "/js/httpobject.js"
    };

    // We just need today's date. java.sql.date does not have any time anyway.
    public static final java.sql.Date BASE_DATE =
                                new java.sql.Date(System.currentTimeMillis());

    private DriverContext ctx;
    private HttpTransport http;
    private String baseURL, hostURL;
    private String personDetailURL;
    private String homepageURL, logoutURL, loginURL;
    private String tagSearchURL;
    private String addEventURL, addPersonURL, eventDetailURL;
    private String addEventResultURL, fileUploadStatusURL,
            fileUploadPersonURL, fileUploadPersonFinalURL, fileUploadEventURL,
            fileUploadEventFinalURL;
    private String addAttendeeURL, fileServiceURL; //GET update.php?id=$eventid
    private String[] homepageStatics, personStatics, personGets,
            tagSearchStatics, eventDetailStatics, addPersonStatics, addEventStatics;
    File eventImg, eventThumb, eventPdf, personImg, personThumb;
    private List<NameValuePair<String>> personPosts;
    private boolean isLoggedOn = false;
    private int loginTime;
    private String username;
    private boolean jMakiComponentsUsed = true;
    private boolean requestSiteStatics = true;
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
    
    private boolean firstTime = true; // Work around for EclipseLink issue (under investigation)

    public JavaUIDriver() throws XPathExpressionException {
        ctx = DriverContext.getContext();
        int scale = ctx.getScale();
        ScaleFactors.setActiveUsers(scale);
        http = new HttpTransport();
        logger = ctx.getLogger();
        random = ctx.getRandom();
        driverMetrics = new UIDriverMetrics();
        ctx.attachMetrics(driverMetrics);        
        String hostPorts = ctx.getXPathValue(
                                "/web20/webServer/fa:hostConfig/fa:hostPorts");
        List<NameValuePair<Integer>> hostPortList =
                                            Utilities.parseHostPorts(hostPorts);
        /*
        String host = ctx.getXPathValue(
                                    "/web20/webServer/fa:hostConfig/fa:host");
        String port = ctx.getXPathValue("/web20/webServer/port");
        */
        int loadedScale = Integer.parseInt(
                                    ctx.getXPathValue("/web20/dbServer/scale"));
        loadedUsers = ScaleFactors.USERS_RATIO * loadedScale;
        if (scale > loadedScale)
            throw new FatalException("Data loaded only for " + loadedScale +
                    " concurrent users. Run is set for " + scale +
                    " concurrent users. Please load for enough concurrent " +
                    "users. Run terminating!");

        String type = ctx.getProperty("serverType");
        String resourcePath = ctx.getResourceDir();
        // TEMP START -- DELETE
        if (resourcePath.startsWith("null"))
            resourcePath="/export/web20_repository/olio/javaee/web2.0driver/resources";
        //System.out.println ("resourcePath = " + resourcePath);
        // DELETE END
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

        // Check whether jMaki component usage is disabled. default: true
        jMakiComponentsUsed = Boolean.parseBoolean(ctx.getProperty("useJMakiComponents"));
        // Check whether site statics should be requested for each page. Default: true.
        requestSiteStatics = !Boolean.parseBoolean(ctx.getProperty("noSiteStaticRequests"));
        // TEMP work around for a division by zero error
        try {
            int bucket = Utilities.selectBucket(ctx.getThreadId(),
                            ctx.getClientsInDriver(), hostPortList.size());
            NameValuePair<Integer> hostPort = hostPortList.get(bucket);
            hostURL = "http://" + hostPort.name + ':' + hostPort.value;
            loginHeaders.put("Host", hostPort.name + ':' + hostPort.value);
        }
        catch (Exception e) {
            hostURL = "http://jes-x4600-1.sfbay:8080";
            loginHeaders.put("Host", hostURL);
        }
        
        /*adding context root */
        String contextRoot = "/webapp";
        
        
        // hostURL = "http://" + hostPort.name + ":" + port;
        baseURL = hostURL + contextRoot;
        personDetailURL = baseURL + "/person?actionType=display_person&user_name=";
        tagSearchURL = baseURL + "/tag/display";
        tagCloudURL = baseURL + "/tag/display";
        addEventURL = baseURL + "/event/addEvent";
        //should this go to Event Detail?
        addEventResultURL = baseURL + "/event/detail";
        addPersonURL = baseURL + "/site.jsp?page=addPerson.jsp";        
        fileUploadPersonURL = baseURL + "/api/person/fileuploadPerson";        
        fileUploadEventURL = baseURL + "/api/event/addEvent";
        
        homepageURL = baseURL + "/index.jsp";
        //change here for Action model
        logoutURL = baseURL + "/logout";
        
        loginURL = baseURL + "/person/login";
        //GET /webapp/api/event/addAttendee?id=4
        addAttendeeURL = baseURL + "/api/event/addAttendee" + "?socialEventID=";
        //updatePageURL = baseURL + "/gettextafterinsert." + type;
        eventDetailURL = baseURL + "/event/detail?socialEventID=";
        fileServiceURL = baseURL + "/access-artifacts";

        List<String[]> sList = new ArrayList<String[]>();
        sList.add(HOME_STATICS);
        homepageStatics = populateList(sList);
        sList.clear();
        sList.add(PERSON_STATICS);
        personStatics = populateList(sList);
        //personGets = populateList(PERSON_GETS);
        sList.clear();
        sList.add(TAGSEARCH_STATICS);
        tagSearchStatics = populateList(sList);
        
        if (jMakiComponentsUsed) {
            sList.clear();
            sList.add(EVENTDETAIL_COMMON_STATICS);
            sList.add(EVENTDETAIL_JMAKI_STATICS);
            eventDetailStatics = populateList(sList);
        }
        else {
            sList.clear();
            sList.add(EVENTDETAIL_COMMON_STATICS);
            sList.add(EVENTDETAIL_BASE_STATICS);
            eventDetailStatics = populateList(sList);
        }
        sList.clear();
        sList.add(ADDPERSON_STATICS);
        addPersonStatics = populateList(sList);
        sList.clear();
        sList.add(ADDEVENT_STATICS);
        addEventStatics = populateList(sList);

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
        
        // If all the client accessEclipsElink at the same time, this cause
        // throws the Exception - invalide operator: 
        //This is a work around while the problem is ebing investigated
        synchronized (JavaUIDriver.class) {
            if (firstTime) {
                try {
                    http.fetchURL(homepageURL);
                } catch (IOException ex) {
                    Logger.getLogger(JavaUIDriver.class.getName()).log(Level.SEVERE, null, ex);
                }
                firstTime = false;
            }
        }
    }

    @BenchmarkOperation (
        name    = "HomePage",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doHomePage() throws IOException {
        logger.finer("HomePage: Accessing " + homepageURL);
        http.fetchURL(homepageURL);
        //logger.info("doHomePage - cachedURLs size is " + cachedURLs.size() );
        imgBytes = 0;
        imagesLoaded = 0;

        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");

        Set<String> images = parseImages(responseBuffer);
        //logger.info("The size of the set of images loaded is " + images.size() );
        if (!isCached) {
            // Fetch the CSS/JS files
            loadStatics(homepageStatics);
        }
        loadImages(images);
        selectedEvent = RandomUtil.randomEvent(random, responseBuffer);
        validateEvent ("doHomePage", selectedEvent);
        logger.finest("Images loaded: " + imagesLoaded);
        logger.finest("Image bytes loaded: " + imgBytes);
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
    public void doLogin() throws IOException {
        int randomId = 0; //use as password
        username = null;

        if (!isLoggedOn) {
            randomId = selectUserID();
            username = UserName.getUserName(randomId);
            // TEMP
            //String lurl = loginURL + "?user_name=" + username + "&password=" +
             //       randomId;
            try {
            http.readURL(loginURL, constructLoginPost(randomId), loginHeaders);
                //http.readURL(url);
                http.fetchURL(homepageURL);
            }
            catch (Exception e) {
               logger.severe("logging in: url = " + constructLoginPost(randomId));
               e.printStackTrace();
            }
            //loginTime = ctx.getTime();
            // This redirects to home.
            // TEMP
            //????http.fetchURL(homepageURL);           
            //http.fetchURL(loginURL, constructLoginPost(randomId),
            //        loginHeaders);            
            /*******
            if (http.getResponseBuffer().indexOf("Not logged in") != -1) {                                
                //logger.warning(http.getResponseBuffer().toString());
                throw new RuntimeException("Login as " + username + ", " +
                                                        randomId + " failed.");
            } else if (http.getResponseBuffer().indexOf("Logout") == -1) {
                logger.severe("Neither logged in or not logged in.");
                logger.severe ("loginURL = " + loginURL + " post msg = " + constructLoginPost(randomId));
                System.exit(1);
                //logger.warning(http.getResponseBuffer().toString());
            }
            ********/
            //logger.info("response buffer is " + http.getResponseBuffer());
            int loginIdx = http.getResponseBuffer().indexOf("Username");
            //logger.info("loginIdx is " + loginIdx);
            if (loginIdx != -1){
                throw new RuntimeException("Found login prompt at index " + loginIdx + ", Login as " + username + ", " +
                                                        randomId + " failed.");
                //throw new Exception("Found login prompt at index " + loginIdx);
            }

            //logger.info("Login successful as " + username + ", " + randomId);
            // TEMP
            isLoggedOn=true;
        //if (ctx.isTxSteadyState()) ++driverMetrics.loginTotal;
        ++driverMetrics.loginTotal;
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
            logger.finer("Logging off: " + username);
            http.fetchURL(logoutURL);
            cachedURLs.clear();
            isCached = cached();
            isLoggedOn=false;
            http = new HttpTransport(); // clear all state
        }
              //if (ctx.isTxSteadyState()) ++driverMetrics.logoutTotal;
              ++driverMetrics.logoutTotal;
    }


    @BenchmarkOperation (
        name    = "TagSearch",
        max90th = 2,
        timing  = Timing.AUTO
    )
    public void doTagSearch() throws IOException {
        String tag = RandomUtil.randomTagName(random);
        String post = "tag=" + tag + "&tagsearchsubmit=Submit";
        logger.finer("TagSearch: " + tagSearchURL + " Post: " + post);
        
        //logger.info("TagSearch: " + tagSearchURL + "Post: " + post);
        http.readURL(tagSearchURL, post);
        
        //no recirection for Java
        //if (http.getResponseCode() != 302)
        //    logger.warning("Tag search response not redirecting.");
        //for java, tagCloudURL and tagSearchURL are the same
        http.fetchURL(tagCloudURL + "?tag=" + tag);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        Set<String> images = parseImages(responseBuffer);
        loadStatics(tagSearchStatics);
        loadImages(images);
        String event = RandomUtil.randomEvent(random, responseBuffer);
        if (event != null) {
            selectedEvent = event;
            validateEvent("doTagSearch", selectedEvent);
        }
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

        StringBuilder buffer = new StringBuilder(256);
        MultipartPostMethod post = new MultipartPostMethod(fileUploadEventURL);
        if(isLoggedOn) {
            post.addParameter("title", RandomUtil.randomText(random, 15, 20)); //title
            post.addParameter("summary", RandomUtil.randomText(random, 20, 100)); // summary
            post.addParameter("description", RandomUtil.randomText(random, 50, 495)); // description
            post.addParameter("submitter_user_name", UserName.getUserName(random.random(1, ScaleFactors.users)));
            post.addParameter("telephone", RandomUtil.randomPhone(random, buffer)); //phone
            DateFormat dateFormat = getDateFormat(); // eventtimestamp
            String strDate = dateFormat.format( 
                random.makeDateInInterval(BASE_DATE, 0, 540));                
            StringTokenizer tk = new StringTokenizer(strDate,"-");
            // The tokens are in order: year, month, day, hour, minute
            post.addParameter("year", tk.nextToken());
            post.addParameter("month", tk.nextToken());
            post.addParameter("day", tk.nextToken());
            post.addParameter("hour", tk.nextToken());
            post.addParameter("minute", tk.nextToken());
            post.addParameter("upload_event_image", eventImg);
            post.addParameter("upload_event_literature",eventPdf);
            post.addParameter("submit", "Create");
            
            int numTags = random.random(1, 7); // Avg is 4 tags per event
            for (int i = 0; i < numTags; i++)
                while (!tagSet.add(RandomUtil.randomTagId(random, 0.1d)));

            for (int tagId : tagSet)
                tags.append(UserName.getUserName(tagId)).append(' ');
            tags.setLength(tags.length() - 1);
            post.addParameter("tags", tags.toString());
            tags.setLength(0);
            tagSet.clear();

            String[] addressArr = prepareAddress();
            post.addParameter("street1",addressArr[0]);
            post.addParameter("street2",addressArr[1]);
            post.addParameter("city", addressArr[2]);
            post.addParameter("state", addressArr[3]);
            post.addParameter("zip", addressArr[4]);
            post.addParameter("country", addressArr[5]);
            doMultiPartPost(post);            
        }
        else {
            System.out.println ("doAddEvent ==> ERROR. Not logged in. Did not amke the post call");
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
       // http.readURL(fileUploadStatusURL);        
        loadStatics(addPersonStatics);        
        String[] parameters = preparePerson();
        MultipartPostMethod post = new MultipartPostMethod(fileUploadPersonURL+"?user_name="+parameters[0]);
        
        // Debug
        if (parameters[0] == null || parameters[0].length() == 0)
            logger.warning("Username is null!");
        
        //logger.info("Username being added is " + parameters[0]);
        post.addParameter("user_name", parameters[0]);
        post.addParameter("password", parameters[1]);
        post.addParameter("passwordx", parameters[1]);
        post.addParameter("first_name", parameters[2]);
        post.addParameter("last_name", parameters[3]);
        post.addParameter("email",parameters[4]);
        post.addParameter("telephone",parameters[5]);
        String[] addressArr = prepareAddress();
        post.addParameter("street1",addressArr[0]);
        post.addParameter("street2",addressArr[1]);
        post.addParameter("city", addressArr[2]);
        post.addParameter("state", addressArr[3]);
        post.addParameter("zip", addressArr[4]);
        post.addParameter("country", addressArr[5]);
        post.addParameter("summary", parameters[6]);
        post.addParameter("timezone", parameters[7]);
        post.addParameter("upload_person_image", personImg);
        //post.addParameter("user_thumbnail",personThumb);
       
        doMultiPartPost(post);        
        //http.readURL(fileUploadPersonFinalURL);
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
        if (!validateEvent("doEventDetail", selectedEvent))
            return;
        
        http.fetchURL(eventDetailURL + selectedEvent);
        
        StringBuilder responseBuffer = http.getResponseBuffer();
        
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        boolean canAddAttendee = false;
        if (!jMakiComponentsUsed) {
            canAddAttendee = isLoggedOn &&
                                responseBuffer.indexOf("Attend") != -1;
            if (!canAddAttendee && isLoggedOn) {
                if (responseBuffer.indexOf("Login:") != -1) {
                    isLoggedOn = false;
                    logger.info("Logged on as " + username + ' ' +
                        (ctx.getTime() - loginTime) +
                        " ms ago, but page shows not logged on.");
                    // logger.info(responseBuffer.toString());
                }
            }
        }
        
        Set<String> images = parseImages(responseBuffer);
	loadStatics(eventDetailStatics);
        loadImages(images);
        // If using jmaki components, we need to make two additional calls to polulate these
        // components
        
        if (jMakiComponentsUsed) {
            //http.readURL(baseURL+"/api/event/getComments?socialEventID="+selectedEvent);
            http.fetchURL(baseURL+"/api/event/getComments?socialEventID="+selectedEvent);
            responseBuffer = http.getResponseBuffer();
            if (isLoggedOn) {
                http.fetchURL(baseURL+"/api/event/getAttendees?socialEventID="+ selectedEvent +
                        "&userName=" + username);
                responseBuffer = http.getResponseBuffer();
                // Creating the JSON object and checking the status is quite expensive
                // So using a simpler approach.
                if (responseBuffer.indexOf("not_attending") != -1 || responseBuffer.indexOf("deleted") != -1)
                    canAddAttendee = true;
            }
            else {
                http.readURL(baseURL+"/api/event/getAttendees?socialEventID="+selectedEvent +
                        "&userName=" + username);
            }
        }
        
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
                if (card == 0) {
                    ++driverMetrics.addAttendeeCount;
                }
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
        buffer.append(fileServiceURL).append("/p");
        int id = random.random(1, ScaleFactors.users);
        String userName = UserName.getUserName(id);
        //logger.info("Accessing "+personDetailURL + UserName.getUserName(id) );
        http.fetchURL(personDetailURL + userName);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        Set<String> images = parseImages(responseBuffer);
        loadStatics(personStatics);
        //logger.info("The buffer is before appending is " + buffer.toString() );
        //logger.info("readURL is: " + buffer.toString() + id +".jpg"); 
		/**
        http.readURL(buffer.append(id).append(".jpg").toString());
		*/
		loadImages(images);
    }

    public void doAddAttendee() throws IOException {
        //can only add yourself (one attendee) to party
        
        //http.readURL(addAttendeeURL + selectedEvent);        original line before hack for session fix
        validateEvent("doAddAttendee", selectedEvent);
        http.readURL(addAttendeeURL + selectedEvent+"&userName="+ username);
        //http.readURL(updatePageURL, "list=attendees"); no need to do this - PersonRestAction sends back JSON
    }

    public Set<String> parseImages(StringBuilder buffer) {
        LinkedHashSet<String> urlSet = new LinkedHashSet<String>();
        //String elementStart = "<img src=\"";
        //int elementStartLen = elementStart.length();
        String elStart = "<img ";
        String attrStart = " src=\"";
        int elStartLen = elStart.length() - 1; // Don't include the trailing space
        int attrStartLen = attrStart.length();
        int idx = 0;            
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
            //logger.info("Analyzing link: " + link);

            if (link.startsWith("/webapp/access-artifacts") && link.contains("jpg")) {
                String url =  hostURL + link;
                //logger.info("url in parseImages is"+ url);
                logger.finest("Adding " + url + " from idx " + idx);
                urlSet.add(url);
            }
        }
        return urlSet;
    }

    private void loadImages(Set<String> images) throws IOException {
        if (images != null)
            for (String image : images) {
                // Loads image only if not cached, means we can add to cache.
                //logger.info("does cache contain this image " + image + ": " + cachedURLs.contains(image) );
                if (cachedURLs.add(image)) {
                    //debug statement                    
                    //logger.info("Loading image " + image);
                    if(!image.contains("jpg"))
                        logger.info("can't find image " + image);
                    int size = http.readURL(image);
                    if (size == 0)
                        logger.warning("Image: " + image + " size 0");
                    imgBytes += size;
                    ++imagesLoaded;
                } else {
                    logger.finer("Image already cached: Not loading " + image);
                    
                }
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
            df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        return df;
    }

    public int selectUserID() {
        return random.random(0, ScaleFactors.USERS_RATIO - 1) *
                            ScaleFactors.activeUsers + ctx.getThreadId() + 1;
        //return random.random(0, ScaleFactors.USERS_RATIO - 1) *
        //                    ScaleFactors.Users + ctx.getThreadId() + 1;
    }


    private String[] populateList(List<String[]> stats) {
        String[] sa = new String[1];
        List<String> list = new ArrayList<String>();
        // If site statics are required, this should be added to all pages
        if (requestSiteStatics) {
            for (int i=0; i<SITE_STATICS.length; i++) {
                list.add(baseURL + SITE_STATICS[i].trim());
            }
        }
        if (stats != null) {
            for (int j=0; j<stats.size(); j++) {
                String[] sarray = stats.get(j);
                for (int i=0; i<sarray.length; i++)
                    list.add(baseURL +sarray[i].trim());
            }
            
        }
        return list.toArray(sa);
    }

    private List<NameValuePair<String>> populatePosts(String[][] posts) {
        List<NameValuePair<String>> returnList =
                                    new ArrayList<NameValuePair<String>>();
        for (String[] post : posts) {
            NameValuePair<String> nvPair = new NameValuePair<String>();
            nvPair.name = baseURL + post[0];
            nvPair.value = post[1];
            returnList.add(nvPair);
        }
        return returnList;
    }

    
    private String constructLoginPost(int randomId) {
        return "user_name=" + username + "&password=" +
                String.valueOf(randomId);
    }
    
      
    
    public void doMultiPartPost(MultipartPostMethod post) throws IOException {

        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        // Olio uses redirect on success of add Event/Person
  	post.setFollowRedirects (true);
        int status = client.executeMethod(post);
        if(status != HttpStatus.SC_OK) {
            throw new IOException("Multipart Post did not work - status = " + status);
        }
    }
    public String[] prepareEvent()  {
        String fields[]  = new String[12];
        StringBuilder buffer = new StringBuilder(256);
        
        int counter=0;
        fields[counter++] = RandomUtil.randomText(random, 15, 20); //title
        fields[counter++] = RandomUtil.randomText(random, 50, 495); // summary
        fields[counter++] = RandomUtil.randomText(random, 50, 495); // description
        fields[counter++]= UserName.getUserName(random.random(1, ScaleFactors.users));
        fields[counter++]= RandomUtil.randomPhone(random, buffer); //phone
        fields[counter++]= RandomUtil.randomTimeZone(random); // timezone
        DateFormat dateFormat = getDateFormat(); // eventtimestamp
        String strDate = dateFormat.format( 
                random.makeDateInInterval(BASE_DATE, 0, 540));                
        StringTokenizer tk = new StringTokenizer(strDate,"-");
        while (tk.hasMoreTokens()) {
            fields[counter++]=tk.nextToken();                        
        }
        //based on DateFormat,
        //fields[5]=year
        //fields[6]=month
        //fields[7]=day
        //fields[8]=hours
        //fields[9]=minutes        
          int numTags = random.random(1, 7); // Avg is 4 tags per event
        for (int i = 0; i < numTags; i++)
            while (!tagSet.add(RandomUtil.randomTagId(random, 0.1d)));

        for (int tagId : tagSet)
            tags.append(UserName.getUserName(tagId)).append(' ');
        tags.setLength(tags.length() - 1);

        fields[counter]= tags.toString();
        tags.setLength(0);
        tagSet.clear();

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
        String[] fields = new String[8];
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
        // Latitude, we do not get addresses in polar circles. So the limit
       // fields[6] = String.format("%.6f", random.drandom(-66.560556d, 66.560556d));

       // fields[7] = String.format("%.6f", random.drandom(-179.999999d, 180d));

        return fields;
    }

    public String[] preparePerson() {
        String fields[]  = new String[8];
        StringBuilder b = new StringBuilder(256);
        int id = loadedUsers + personsAdded++ * ScaleFactors.activeUsers +
                                                        ctx.getThreadId() + 1;
        fields[0] = UserName.getUserName(id);
        //logger.info("in prepare person - the id is "+ id + " for the username " + fields[0]);
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
        //additions here
        int loginTotal = 0;
        int logoutTotal = 0;

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
            //additions here
            loginTotal += o.loginTotal;
            logoutTotal += o.logoutTotal;
            
        }

               public Element[] getResults() {
            Result r = Result.getInstance();
            int total = r.getOpsCountSteady("EventDetail");
            Element[] el = new Element[15];
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
             // additions here
            //home page counts
            el[10] = new Element();
            el[10].description = "Total Home Page counts";
            el[10].target = "&gt;100";
            el[10].result = Integer.toString(r.getOpsCountSteady("HomePage"));
            el[10].passed = Boolean.TRUE;
            //home page Images Loaded
            el[11] = new Element();
            el[11].description = "Total Home Image Loaded counts";
            el[11].target = "&gt;3";
            el[11].result = Double.toString(homePageImagesLoaded);
            //home page Images Count
            el[12] = new Element();
            el[12].description = "Total Home Image counts";
            el[12].target = "&gt;3";
            el[12].result = Integer.toString(homePageImages);
            el[13] = new Element();
            el[13].description = "Total Login total";
            el[13].target = "&gt;3";
            el[13].result = Integer.toString(loginTotal);
            el[14] = new Element();
            el[14].description = "Total Logout totol";
            el[14].target = "&gt;3";
            el[14].result = Integer.toString(logoutTotal);
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
            clone.loginTotal = loginTotal;
            clone.logoutTotal = logoutTotal;
            return clone;
        }
    }

    private boolean validateEvent(String method, String selectedEvent) {
        if (selectedEvent == null || selectedEvent.length() == 0 || selectedEvent.equals("-1")) {
            logger.warning("method: " + method + " selectedEvent is incorrect. eventid = " + selectedEvent);
            return false;
        }
        return true;
    }
}
