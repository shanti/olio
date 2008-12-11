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

/*
 * GeocodeBean.java
 *
 *
 *  Used to encapsulate parameters from Geocode request.  
 *  Parameters in this bean are populated as per the Yahoo
 *  Geocoder specs at
 *
 *     http://developer.yahoo.com/maps/rest/V1/geocode.html
 *
 */

package org.apache.olio.geocoder;

/**
 *
 * @author Prashant Srinivasan
 *
 */
public class GeocoderBean {
    
    public enum OutputType {xml, php}
    
    private String appid;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String location;
    private OutputType output;
    
    /** Creates a new instance of GeocodeBean */
    public GeocoderBean() {
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public OutputType getOutput() {
        return output;
    }

    public void setOutput(String output) {   
        if (output == null) {
            this.output = OutputType.xml;
        } else if (output.equalsIgnoreCase("php")) {
            this.output = OutputType.php;
        }
    }    
}
