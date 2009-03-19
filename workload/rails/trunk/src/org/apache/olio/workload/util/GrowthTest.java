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

package org.apache.olio.workload.util;

/**
 * Code to test the growth function and match it to existing research.
 */
public class GrowthTest {

    // We use on average of 3.5 tags per event. Random 1..6 tags.
    // Once we know the tag count, we have to select tags.

    /*

    http://tagsonomy.com/index.php/dynamic-growth-of-tag-clouds/

    As of this writing, a little over 700 users have tagged it, with 450+
    unique tags, roughly two-thirds of which tags were (of course) used by
    one and only one user.

    It took only 10 users (not quite 1.5% of the current total) before the
    top 3 tags were tagging ontology folksonomy, conveying much the same
    sense, with only the use of tagging instead of tags making this
    different from the current set of 3.
    */


    public static double cumuLogistic(
            double x, double mean, double scale) {
        return 0.5d + Math.tanh((x - mean) / (2 * scale)) / 2d;
    }

    public static double cumuHalfLogistic(double x, double scale) {
        return (1d - Math.pow(Math.E, -x/scale)) / (1d + Math.pow(Math.E, -x/scale));
    }

    public static double sigmoid(double x, double mean, double scale) {
        return 1d / (1d + Math.pow(Math.E, -((x / scale) - mean)));
    }

    public static void main(String[] args) {

        int limit = 5000;
        int mean = 5000;
        int scale = 500;
        for (int x = 0; x < 10000; x += 100) {
            int y = (int) Math.round(limit * cumuLogistic(x, 5000, 1000));
            int y2 = (int) Math.round(limit * cumuHalfLogistic(x, 10000)); // Done
            int y3 = (int) Math.round(limit * sigmoid(x, 6, 1000));
            System.out.println("-> " + x + ',' + y + ',' + y2 + ',' + y3);
        }
    }

}
