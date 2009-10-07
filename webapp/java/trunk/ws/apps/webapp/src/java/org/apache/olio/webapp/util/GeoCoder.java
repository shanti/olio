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

import org.apache.olio.webapp.*;
import org.apache.olio.webapp.util.geocoder.ResultSet;
import org.apache.olio.webapp.util.geocoder.ResultType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.Document;


/**
 * <p>Service object that interacts with the Yahoo GeoCoding service.  For
 * information on the relevant APIs, see <a href="http://developer.yahoo.net/maps/rest/V1/geocode.html">
 * http://developer.yahoo.net/maps/rest/V1/geocode.html</a>.</p>
 */
public class GeoCoder {
    
    private String applicationId = APPLICATION_ID;
    //private Logger logger = WebappUtil.getLogger();
    private static Logger logger = Logger.getLogger(GeoCoder.class.getName());

    private String proxyHost = null;
    private int proxyPort = 0;
    private boolean proxySet = false;
    
    //private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static JAXBContext jaxbContext;

    /**
     * <p>The URL of the geocoding service we will be using.</p>
     */
    private static String SERVICE_URL;
    
    static {
        // Added for portability while tetsing on multiple machines
        SERVICE_URL = System.getProperty("webapp.geocoderURL");
        if (SERVICE_URL == null)
            SERVICE_URL = ServiceLocator.getInstance().getString("geocoderURL");
        logger.finer ("geocoder ServiceURL = " + SERVICE_URL);
        // Fall back to default
        if (SERVICE_URL == null)
            SERVICE_URL = "http://localhost:8000/Web20Emulator/geocode";
    }
    // ------------------------------------------------------ Manifest Constants
    
    /**
     * <p>The default application identifier required by the geocoding
     * service.  This may be overridden by setting the <code>applicationId</code>
     * property.</p>
     */
    static final String APPLICATION_ID =
           // "com.sun.javaee.blueprints.components.ui.geocoder";
            "org.apache.olio.components.ui.geocoder";
    
    private static ThreadLocal<Unmarshaller> unmarshallerTL = new ThreadLocal<Unmarshaller>();
    
    public GeoCoder() {
         
    }
    
    private static synchronized JAXBContext getJAXBContext() {
        if (jaxbContext != null)
            return jaxbContext;
        
        try {
            jaxbContext = JAXBContext.newInstance("org.apache.olio.webapp.util.geocoder");
        } catch (JAXBException ex) {
            Logger.getLogger(GeoCoder.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return jaxbContext;
    }
    
    private Unmarshaller getUnmarshaller() {
        Unmarshaller u = unmarshallerTL.get();
        if (u == null) {
            try {
                JAXBContext ctx = getJAXBContext();
                if (ctx == null) {
                    throw new RuntimeException ("JAXBContext is null -- cannot process gecode information");
                }
                u = ctx.createUnmarshaller();
                unmarshallerTL.set(u);
                return u;
            } catch (JAXBException ex) {
                Logger.getLogger(GeoCoder.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    // -------------------------------------------------------------- Properties
    
    /**
     * <p>Return the application identifier to be passed to the geocoding
     * service.</p>
     */
    public String getApplicationId() {
        return this.applicationId;
    }
    
    /**
     * <p>Set the application identifier to be passed to the geocoding
     * service.</p>
     *
     * @param applicationId The new application identifier
     */
    public void setApplicationId(String applicationId) {
        if (applicationId == null) {
            throw new NullPointerException();
        }
        this.applicationId = applicationId;
    }
    
    /**
     * <p>Return the proxy host to use for network connections, or <code>null</code>
     * if the default proxy host for the application server's JVM should be
     * used instead.</p>
     */
    public String getProxyHost() {
        return this.proxyHost;
    }
    
    /**
     * Set the proxy host to use for network connections, or <code>null</code>
     * to use the default proxy host for the application server's JVM.</p>
     *
     * @param proxyHost The new proxy host
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        this.proxySet = false;
    }
    
    /**
     * <p>Return the proxy port to use for network connections, or <code>0</code>
     * if the default proxy port for the application server's JVM should be
     * used instead.</p>
     */
    public int getProxyPort() {
        return this.proxyPort;
    }
    
    /**
     * Set the proxy port to use for network connections, or <code>0</code>
     * to use the default proxy port for the application server's JVM.</p>
     *
     * @param proxyPort The new proxy port
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        this.proxySet = false;
    }
    
    
    // ---------------------------------------------------------- Public Methods
    
    
    /**
     * <p>Return an array of zero or more {@link GeoPoint} instances for results
     * that match a search for the specified location string.  This string can
     * be formatted in any of the following ways:</p>
     * <ul>
     * <li>city, state</li>
     * <li>city, state, zip</li>
     * <li>zip</li>
     * <li>street, city, state</li>
     * <li>street, city, state, zip</li>
     * <li>street, zip</li>
     * </ul>
     *
     * @param location Location string to search for
     *
     * @exception IllegalArgumentException if <code>location</code> does not
     *  conform to one of the specified patterns
     * @exception NullPointerException if <code>location</code> is <code>null</code>
     */
    public GeoPoint[] geoCode(String location) {
        
        // Bail out immediately if no location was specified
        if (location == null) {
            return null;
        }
        
        // Set the proxy configuration (if necessary)
        if (!proxySet) {
            setProxyConfiguration();
            proxySet = false;
        }
        
        // URL encode the specified location
        String applicationId = getApplicationId();
        try {
            applicationId = URLEncoder.encode(applicationId, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "geoCoder.encodeApplicationId", e);
            }
            throw new IllegalArgumentException(e.getMessage());
        }
        
        // URL encode the specified location
        
        //** change don't encode since String is constructed not with the location
        /*
        try {
            
            location = URLEncoder.encode(location, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "geoCoder.encodeLocation", e);
            }
            throw new IllegalArgumentException(e.getMessage());
         
        }
        */
        
        // Perform the actual service call and parse the response XML document,
        // then format and return the results
        Document document = null;
        StringBuffer sb = new StringBuffer(SERVICE_URL);
        sb.append("?appid=");
        sb.append(applicationId);
        /* this is for google map service - changing to emulated geocoder (yahoo)
        sb.append("&location=");
        sb.append(location);
         */
        
        //did URL construction in WebAppUtil.handleAddress
        sb.append(location);
        
        // We will set up a thread local JAXB unmarshaller for performance
        Unmarshaller u = getUnmarshaller();
        if (u == null)
            return null;
        
        try {
            URL url = new URL(sb.toString());
            try {
                ResultSet rs = (ResultSet) u.unmarshal(url.openStream());
                List<ResultType> list = rs.getResult();
                // Set up the geo points
                GeoPoint[] gps = new GeoPoint[list.size()];
                int i=0;
                for (ResultType r: list) {
                    GeoPoint gp = new GeoPoint();
                    gp.setAddress(r.getAddress());
                    gp.setLatitude (r.getLatitude().doubleValue());
                    gp.setLongitude (r.getLongitude().doubleValue());
                    gp.setCity(r.getCity());
                    gp.setState(r.getState());
                    gp.setZip(r.getZip());
                    gp.setCountry(r.getCountry());
                    gps[i++] = gp;
                }
                return gps;
            } catch (IOException ex) {
                Logger.getLogger(GeoCoder.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        } catch (JAXBException ex) {
            Logger.getLogger(GeoCoder.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(GeoCoder.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * <p>Attempt to set the system properties related to the HTTP proxy host
     * and port to be used, but swallow security exceptions if the security
     * policy that our container is running under forbids this.  In a JDK 1.5
     * environment, we'll be able to use the <code>java.net.Proxy</code> class
     * and deal with this on a per-connection basis.  Until then, oh well.</p>
     */
    private synchronized void setProxyConfiguration() {
        
        // NOTE - the system properties API gives no way to unset properties
        // after they have been set.  Therefore, only attempt to set things
        // if we have values for both proxyHost and proxyPort
        if ((proxyHost == null) || (proxyPort == 0)) {
            return;
        }
        
        // Log and swallow any security exception that occurs when attempting
        // to set these system properties.  The subsequent connection failure
        // will be ugly enough
        try {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", "" + proxyPort);
        } catch (SecurityException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "geoCoder.setProxy", e);
            }
        }
    }
}
