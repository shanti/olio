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
package org.apache.olio.webapp.util;

import org.apache.olio.webapp.cache.Cache;
import org.apache.olio.webapp.cache.CacheFactory;
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.model.SocialEventTag;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.olio.webapp.controller.WebConstants.*;
import javax.servlet.ServletContext;

import java.net.URLEncoder;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author basler
 * @author binu
 * @author kim
 */

public class WebappUtil {
    
    //private static final Logger logger=getBaseLogger();
    private static Logger logger = Logger.getLogger(WebappUtil.class.getName());
    private static final PropertyResourceBundle _resBundle=getBaseBundle();
    public static String context = "/";
    private static HashMap<String, Integer> monthMap;
    private static Cache cache = null;
    private static String artifactPath;
    private static String artifactLocationDir;
    
    static {
         monthMap = new HashMap<String, Integer>();
         monthMap.put("january", 1);
         monthMap.put("february", 2);
         monthMap.put("march", 3);
         monthMap.put("april", 4);
         monthMap.put("may", 5);
         monthMap.put("june", 6);
         monthMap.put("july", 7);
         monthMap.put("august", 8);
         monthMap.put("september", 9);
         monthMap.put("october", 10);
         monthMap.put("november", 11);
         monthMap.put("december", 12);
         monthMap.put("jan", 1);
         monthMap.put("feb", 2);
         monthMap.put("mar", 3);
         monthMap.put("apr", 4);
         monthMap.put("may", 5);
         monthMap.put("jun", 6);
         monthMap.put("jul", 7);
         monthMap.put("aug", 8);
         monthMap.put("sep", 9);
         monthMap.put("oct", 10);
         monthMap.put("nov", 11);
         monthMap.put("dec", 12);
         
         cache = CacheFactory.getCache("Olio");
    }

    /** Creates a new instance of WebappUtil */
    public WebappUtil() {
    }

    public static String getArtifactLocalionDir() {
	return artifactLocationDir;
    }

    public static String getArtifactPath() {
	return artifactPath;
    }

    public static void setArtifactPath() {
        boolean isLocal = false;
        try {
            ServiceLocator sloc = ServiceLocator.getInstance();
            // Check if the app server is set up to use the DefaultServlet for
            // static content delivery.
            // Currently it is better to use our own Servlet rather than the default Servlet
            // This may change in later releases. The property is set up for future use
            isLocal = Boolean.parseBoolean(sloc.getString("webapp.useContainerArtifactDelivery", "false"));
            if (isLocal) {
                org.apache.olio.webapp.util.fs.FileSystem fs = sloc.getFileSystem();
                isLocal = fs.isLocal();
            }
            String imgDir = sloc.getString("webapp.image.directory");
            if (imgDir != null) {
                WebappConstants.WEBAPP_IMAGE_DIRECTORY = imgDir;
            }


        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if (isLocal) {
            artifactLocationDir = WebappConstants.DEFAULT_IMAGE_DIRECTORY;
            artifactPath = "/artifacts";

        } else {
            artifactLocationDir = WebappConstants.WEBAPP_IMAGE_DIRECTORY;
            artifactPath = getContext() + "/access-artifacts";
        }

    }

    public static String getContext() {
	return context;
    }

    public static void setContext(String aContext) {
	context = aContext;
	setArtifactPath();
    }


    /* Load Image, create thumbnail and save images. 
       This does not close the Input Stream */
    public static boolean saveImageWithThumbnail (InputStream is, String imagePath, 
                            String thumbnailPath) throws IOException {
        ImageScaler scaler = new ImageScaler ();
    
        FileOutputStream fos = new FileOutputStream (imagePath);
        WriteThroughInputStream wis = new WriteThroughInputStream (is, fos);
        // logger.finer(" the imagePath in saveImageWithThumbnail is "+ imagePath);
        try {
            scaler.customLoad(wis);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            //ex.printStackTrace();
        }
        if (thumbnailPath != null) {
            scaler.resizeWithGraphics(thumbnailPath);
        }
        // logger.finer(" the thumbNailPath in saveImageWithThumbnail is "+ thumbnailPath);
        wis.closeOutputStream();
        return true;
    } 
    
    public static String constructThumbnail(String path) {
        String thumbPath = null;
        
        int idx = path.lastIndexOf(".");
        if (idx > 0) {
            thumbPath = path.substring(0, idx)+"_thumb"+path.substring(idx, path.length());
        }
        
        try {
            ImageScaler imgScaler = new ImageScaler(path);
            imgScaler.keepAspectWithWidth();
            imgScaler.resizeWithGraphics(thumbPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR in generating thumbnail", e);
        }
        // remove path info and just return the thumb file name
        thumbPath=thumbPath.substring(thumbPath.lastIndexOf("/") + 1);
        
        return thumbPath;
    }
    
    
    /**
     * This method returns the default logger for the Webapp application
     *
     * @return Default Logger for Webapp application
     */
    private static Logger getBaseLogger() {
        return Logger.getLogger(WebappConstants.WEBAPP_BASE_LOGGER, WebappConstants.WEBAPP_BASE_LOG_STRINGS);
    }
    
    private static PropertyResourceBundle getBaseBundle() {
        try {
            return new PropertyResourceBundle(WebappUtil.class.getResourceAsStream("MessageStrings.properties"));
        } catch(IOException io) {
            logger.log(Level.WARNING, "resource_bundle_does_not_exist", io);
            return null;
        }
        
    }
    
    
    public static String getMessage(String key) {
        return getMessage(key, (Object[])null);
    }
    
    
    /**
     * This method uses the default message strings property file to resolve
     * resultant string to show to an end user
     * @param Key to use in MessageString.properties file
     *
     * @return Formated message for external display
     */
    public static String getMessage(String key, Object... arguments) {
        String sxRet=null;
        // get resource bundle and retrive message
        sxRet=_resBundle.getString(key);
        
        // see if the message needs to be formatted
        if(arguments != null) {
            // format message
            sxRet=MessageFormat.format(sxRet, arguments);
        }
        return sxRet;
    }
    
    
    public static void closeIgnoringException(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                // There is nothing we can do if close fails
            }
        }
    }
    
    public static String encodeJSONString(String json) {
        if (json == null)
            return "";
        json=json.replaceAll("'", "\\\\'");
        json=json.replaceAll("\"", "\\\\\\\"");
        return json;
    }
    
    public static Address handleAddress(ServletContext context, String street1, String street2, String city, String state, String zip, String country){
        StringBuffer addressx=new StringBuffer();
        String proxyHost=null, proxyPort=null;
        if(context!=null) {
            proxyHost = context.getInitParameter(PROXY_HOST_INIT_PARAM);
            proxyPort = context.getInitParameter(PROXY_PORT_INIT_PARAM);
        }
        // change the addressx to conform to geocoder emulator (emulator yahoo service)
        // as opposed to google's'
        try {
         if(street1 != null && street1.length() > 0) {
            addressx.append("&street=");
            addressx.append(URLEncoder.encode(street1,"ISO-8859-1"));
        }
        if(street2 != null && street2.length() > 0) {
            //addressx.append(COMMA);            
            addressx.append(URLEncoder.encode(street2, "ISO-8859-1"));
        }
        if(city != null && city.length() > 0) {
            //addressx.append(COMMA);
            addressx.append("&city=");
            addressx.append(URLEncoder.encode(city, "ISO-8859-1"));
        }
        if(state != null && state.length() > 0) {
            //addressx.append(COMMA);
            addressx.append("&state=");
            addressx.append(URLEncoder.encode(state, "ISO-8859-1"));
        }
        if(zip != null && zip.length() > 0) {
            //addressx.append(COMMA);
            addressx.append("&zip=");
            addressx.append(URLEncoder.encode(zip, "ISO-8859-1"));
        }
        /* remove USA since current emulator only accepts values for zip code
        if(country != null && country.length() > 0) {
            addressx.append(COMMA);
            addressx.append(country);
        }
        */
       } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING, "geoCoder.encodeLocation ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
        if(street2 == null) street2="";
        logger.finer("Ready to set proxyHost:proxyPort to " + proxyHost + ":" + proxyPort + ". ");
        // get latitude & longitude
        GeoCoder geoCoder=new GeoCoder();
        //if(proxyHost != null && proxyPort != null)
        if(proxyHost != null && !proxyHost.equals("") && proxyPort != null && proxyPort.equals("")) {
            // set proxy host and port if it exists
            logger.finer("Setting proxyHost:proxyPort to " + proxyHost + ":" + proxyPort + 
                ".  Make sure server.policy is updated to allow setting System Properties");
            geoCoder.setProxyHost(proxyHost);
            try {
                geoCoder.setProxyPort(Integer.parseInt(proxyPort));
            } catch (NumberFormatException ee) {
                ee.printStackTrace();
            }
        } else {
            logger.finer("A \"proxyHost\" and \"proxyPort\" isn't set as a web.xml context-param. A proxy server may be necessary to reach the open internet.");
        }
        
        // use component to get points based on location (this uses Yahoo's map service
        String totAddr=addressx.toString();
        double latitude=0;
        double longitude=0;
        if(totAddr.length() > 0) {
            try {
                GeoPoint points[]=geoCoder.geoCode(totAddr);
                if ((points == null) || (points.length < 1)) {
                    logger.fine("No addresses for location - " + totAddr);
                } else if(points.length > 1) {
                    logger.fine("Matched " + points.length + " locations, taking the first one");
                }
                
                // grab first address in more that one came back
                if(points != null && points.length > 0) {
                    // set values to used for map location
                    latitude = points[0].getLatitude();
                    longitude = points[0].getLongitude();
                }
            } catch (Exception ee) {
                logger.log(Level.WARNING, "geocoder.lookup.exception", ee);
            }
        }
        Address addr = new Address(street1,street2,city,state,zip,country,latitude,longitude);
        return addr;
    }
    
    public static String createTagCloud (List<SocialEventTag> tags) {
        return createTagCloud (context, tags);
    }
    
    public static String createTagCloud(String ctx, List<SocialEventTag> tags) {
        if (tags == null)
            return "";
        
        Collections.sort(tags);
                
        int maxsize = 250; // max font size in %
        int minsize = 100; // min font size in %
        
        // Get the min and max. We may want to move this to a method later
        int mincount = Integer.MAX_VALUE;
        int maxcount = 0;
        
        for (SocialEventTag tag: tags) {
            int count = tag.getRefCount();
            if (count < mincount)
                mincount = count;
            if (count > maxcount)
                maxcount = count;
        }
        int spread = maxcount - mincount;
        if (spread == 0) // Avoid division by zero
            spread = 1;
        
        // Determine font size increment
        float step = ((float) (maxsize - minsize))/spread;
        int size;
        StringBuilder strb = new StringBuilder();
        
        if (tags.isEmpty())
            return "No Tags Available";
        
        String tagUrl = ctx + "/tag/display?tag=";
        
        for (SocialEventTag tag: tags) {
            size = (int) (minsize + ((tag.getRefCount() - mincount)*step));
            strb.append("<a href=\"" + tagUrl + tag.getTag() +"\"" +
                    " style=\"font-size: " + size + "%" +
                    "\" title =\"" + tag.getRefCount() +
                    " events tagged with " + tag.getTag() + "\"" +
                    "> " + tag.getTag() + "</a>&nbsp; ");
        }
        return strb.toString();
    }
    
    public static List<SocialEvent> getPagedList (List<SocialEvent> list, int index) {
        int size = list.size();
        int numPages = getNumPages(list);
        if (size <= WebappConstants.ITEMS_PER_PAGE) {
            return list;
        }

        int startIndex = index * WebappConstants.ITEMS_PER_PAGE;
        if (startIndex > size) {
            startIndex -= WebappConstants.ITEMS_PER_PAGE;
        }

        int endIndex = startIndex + WebappConstants.ITEMS_PER_PAGE;
        if (endIndex > size)
            endIndex = size;

        List<SocialEvent> slist = list.subList(startIndex, endIndex);
        return slist;
    }
    
    public static int getNumPages (long size) {
        if (size % WebappConstants.ITEMS_PER_PAGE != 0)
            return (int) size/WebappConstants.ITEMS_PER_PAGE + 1;
        else
            return (int) size/WebappConstants.ITEMS_PER_PAGE;
    }
    
    public static int getNumPages (List list) {
        int size = list.size();
        return getNumPages(size);
    }
    
    public static String getFileExtension (String fileName) {
        if (fileName == null)
            return "";
        int dot = fileName.lastIndexOf(".");
        if (dot >= 0) {
            return fileName.substring(dot);
        }
        return "";
    }
    
    public static int getIntProperty(String prop) {
        if (prop == null)
            return 0;
        try {
            return Integer.parseInt(prop);
        }
        catch (Exception e) {}
        return 0;
    }
    
    public static int getMonthNumber (String month) {
        // We will look for both full names and names with first thrre letters
        if (month == null || month.length() < 3)
            return 0;
        
        month = month.toLowerCase().substring(0, 3);
        Integer im = monthMap.get(month);
        if (im != null)
            return im;
        return 0;
    }
    
    public static String getCacheKey (String path) {
        return getCacheKey(path, null);
    }
    
    /**
     * 
     * @param path - The request path
     * @param map - A map with the various parameter entries. 
     * @return - the cache key is the request is cacheable. A null value is returned
     *           if the request is not cacheable.
     */
    public static String getCacheKey(String path, Map<String, Object> map) {
        if (path == null)
            return null;
        
        // Currently we only cache the main event list - path = "/event/list.
        // For simplicity, only list based on event_date in ascending order is cached
        // TO DO: Add more complexity as we extend caching to distributed caching
        
        if (path.equals("/event/list")) {
            StringBuilder keysb = new StringBuilder(path);
            keysb.append("?");
            // No caching for specific days or zip codes
            if (map != null) {
                Integer day = (Integer)map.get("day");
                Integer month = (Integer) map.get("month");
                Integer year = (Integer) map.get("year");
                if (day != 0 || month != 0 || year != 0 || (map.get("zip") != null && ((String)map.get("zip")).length() >0)) {
                    return null;
                }
            }
            
            String order = null;
            Integer orderBy = null;
            if (map != null) {
                order = (String) map.get("orderType");
                orderBy = (Integer)map.get("orderBy");
            }
            if (order != null && order.equals(WebappConstants.ORDER_CREATED))
                return null;
            order = WebappConstants.ORDER_EVENT_DATE;
            keysb.append("order=" + order);
            
            if (orderBy != null && orderBy != WebappConstants.ORDER_BY_ASCENDING)
                return null;
            orderBy = WebappConstants.ORDER_BY_ASCENDING;
            keysb.append("&orderBy=" + orderBy);
            return keysb.toString();
        }
        return null;
    }
    
    /**
     * 
     * @param path request path for which the cache should be cleared
     */
    public static void clearCache (String path) {
        clearCache(path, null);
    }
    
    /**
     * 
     * @param path  request path for which the cache should be cleared
     * @param map a map of properties to generate the cache key
     * 
     * The cache is not synchronized with respect to get and put.  
     * 
     */
     
    public static void clearCache(String path, HashMap<String, Object> map) {
        if (cache == null || path == null)
            return;
        String key = getCacheKey(path, map);
        if (key != null) {
            logger.finer("WebappUtil.clearCache(): " + key);
            cache.invalidate(key);
        }
    }
    
    public static Cache getCache() {
        return cache;
    }
    
    public static String acquirePageContent (String path, ServletRequest request, 
            ServletResponse response) throws IOException, ServletException {
        ContentCacheResponseWrapper wrapper = new ContentCacheResponseWrapper (
                                (HttpServletResponse)response);
        request.getRequestDispatcher(path).include(request, wrapper);
        
        return new String(wrapper.getData());
    }

    public static long getCacheTimeToLiveInSecs() {
        return Integer.parseInt(System.getProperty("CacheTimeToLiveInSecs", ""+WebappConstants.CACHE_TIME_TO_LIVE_IN_SECS));
    }

    
    public static String parseValueFromHeader(String header, String parameterName) {
        String parameterValue = null;
        StringTokenizer st = new StringTokenizer(header, ";");
          while (st.hasMoreTokens()) {
              String token = st.nextToken();
              if (token.contains(parameterName)) {
                  int quoteIndex = token.indexOf("\"");
                  parameterValue = token.substring(quoteIndex+1,token.length()-1);
                  // logger.finer("parameter " + parameterName + " is "+ parameterValue);
                  break;
              }
          }
        return parameterValue;
    }

}
