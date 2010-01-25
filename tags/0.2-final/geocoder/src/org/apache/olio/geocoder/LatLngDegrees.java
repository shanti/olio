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
 * LatLngDegrees.java
 * @author Prashant Srinivasan
 *
 */

package org.apache.olio.geocoder;

public class LatLngDegrees {

    private double lat;

    private double lng;
    
    /** Creates a new instance of LatLngDegrees */
    public LatLngDegrees(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    
   public double getLat() {
       return lat;
   }
    public double getLng() {
        return lng;
    }
}
