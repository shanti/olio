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

package org.apache.olio.webapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 
 * @author Mark Basler
 * @author Binu John
 */
@Entity
@Table (name="ADDRESS")
public class Address implements java.io.Serializable {
    
    private int addressID;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private double latitude;
    private double longitude;
    private static final String COMMA=", ";
    
    public Address() { }
    public Address(String street1, String street2, String city,
            String state, String zip, String country, double latitude,
            double longitude){
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Very high allocationSize is set to work around a eclipseLink duplicate 
    // sequence generation issue faced under heavy load.
    @TableGenerator(name="ADDRESS_ID_GEN",
            table="ID_GEN",
            pkColumnName="GEN_KEY",
            valueColumnName="GEN_VALUE",
            pkColumnValue="ADDRESS_ID",
            allocationSize=20000)
    @GeneratedValue(strategy=GenerationType.TABLE,generator="ADDRESS_ID_GEN")
    @Id
    public int getAddressID() {
        return addressID;
    }
    
    public String getStreet1() {
        return street1;
    }
    
    public String getStreet2() {
        return street2;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getState() {
        return state;
    }
    public String getZip() {
        return zip;
    }
    public String getCountry() {
        return country;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    
    public void setStreet1(String street1) {
        this.street1 = street1;
    }
    public void setStreet2(String street2) {
        this.street2 = street2;
    }
    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }
    public void  setCountry(String country) {
        this.country=country;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        if(street1 != null) sb.append(street1);
        if(street2 != null && !street2.equals("")) sb.append(" " + street2);
        if(city != null) sb.append(COMMA + city);
        if(state != null) sb.append(COMMA + state);
        if(zip != null) sb.append(COMMA + zip);
        if(country != null) sb.append(COMMA + country);
        return sb.toString();
    }
}



