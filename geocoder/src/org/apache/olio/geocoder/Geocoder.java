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
 * Geocoder.java
 *
 * Assumes latitudes in the northern hemisphere are +ve (0-90) and 
 * are -ve (0-90) in the southern hemisphere.
 * 
 * Assumes longitudes to the west of 0degrees are -ve (0-180) and 
 *  to the east of 0degrees, +ve.
 *
 * For example, LAX is approximately +34degrees latitude and -118.5degrees longitude.
 */

package org.apache.olio.geocoder;

/**
 *
 * @author Prashant Srinivasan
 */
public class Geocoder {
    
    /** Creates a new instance of Geocoder */
    public Geocoder() {       
    }
    
    
    public LatLngDegrees returnCodes(GeocoderBean geoBean) {
        return new LatLngDegrees(37.416384, -122.024853);
    }    
}
