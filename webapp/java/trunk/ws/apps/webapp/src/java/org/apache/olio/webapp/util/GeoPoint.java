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

/**
 * <p>JavaBean describing a single result returned by the GeoCoding service.
 * All of the properties are optional.</p>
 */
public class GeoPoint {

    private double latitude = 0.0;
    private double longitude = 0.0;
    private String address = null;
    private String city = null;
    private String state = null;
    private String zip = null;
    private String country = null;


    // -------------------------------------------------------------- Properties

    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return this.zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return this.country;
    }
    public void setCountry(String country) {
        this.country = country;
    }


    /**
     * <p>Return a concise description of the location of this point (without
     * including the latitude and longitude).</p>
     */
    public String toString() {

        StringBuffer sb = new StringBuffer();
        if (address != null) {
            sb.append(address);
        }
        if (city != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(city);
        }
        if (state != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(state);
        }
        if (zip != null) {
            if (sb.length() > 0) {
                sb.append("  ");
            }
            sb.append(zip);
        }
        return sb.toString();
    }
}
