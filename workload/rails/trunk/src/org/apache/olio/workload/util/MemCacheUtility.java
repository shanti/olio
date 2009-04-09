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
 * MemCacheUtility.java
 *
 * Created on August 20, 2007, 10:23 PM
 *
 */

package org.apache.olio.workload.util;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.sun.faban.common.NameValuePair;
import com.sun.faban.common.TextTable;

import java.util.*;
import java.util.logging.Logger;



/**
 *
 * @author Kim LiChong
 */
public class MemCacheUtility {

    private static MemCachedClient cache = null;
    private String[] serverList = null;
    static Logger logger = Logger.getLogger(
            MemCacheUtility.class.getName());
    private long baseTime = Long.MIN_VALUE;


    /** This constructor creates a new instance of MemCacheUtility
     A memcache client is created with a pool of servers.
     * 
     * @param servers ArrayList of NameValuePair<Integer> of servers:port numbers.
     */
    public MemCacheUtility(ArrayList<NameValuePair<Integer>> servers) {
        if (cache == null) {
            // the env memcachedInstances is in the
            // form host1:port1, host2:port2, etc.
            // in an ArrayList 
            //String servers = locator.getString("memcachedInstances");


            serverList = new String[servers.size()];

            serverList = convertNameValueToStringArray(servers);

            //logger.info("size of the array is " + serverList.length);
            //String[] serverList = servers.split(",?[ *]");
            SockIOPool pool = SockIOPool.getInstance("livePool");
            pool.setServers(serverList);
            pool.initialize();

            cache = new MemCachedClient();
            //cache.setPoolName("livePool");
        }               
    }

    /** This constructor creates a new instance of MemCacheUtility
     A memcache client is created with a pool of servers.
     * 
     * @param servers String []  servers:port.
     */
    public MemCacheUtility(String[] servers) {
        if (cache == null) {
            SockIOPool pool = SockIOPool.getInstance("livePool");
            pool.setServers(servers);
            pool.initialize();

            cache = new MemCachedClient();
            //cache.setPoolName("livePool");
        }
    }

    /*
    * This method is a convenience method to convert ArrayList<NameValuePair<Integer> to
    * a String array of server:port server2:port.
    * @param servers  ArrayList<NameValuePair<Integer>>
    * @return String []
    */

    public static String[] convertNameValueToStringArray (
            ArrayList<NameValuePair<Integer>> servers) {
        String [] serverArr = new String[servers.size()];
        int index = 0;
        for (NameValuePair<Integer> serverEntry : servers) {
            serverArr[index++] = serverEntry.name + ":" + serverEntry.value;
        }
        return serverArr;
    }

    /** Retrieves memcache stats for each instance of MemCacheUtility.
     *  A TextTable will be produced for each server used to create an
     *  instance of MemCacheUtility.  Used to persist stats file for each server.
     *  Returning Map whose key is the servername, the value is a TextTable of statistics
     *  @return Map 
     */

    public Map<String, TextTable> getStats() {


        Map memcacheStats = cache.stats();
        //cache.stats() will return a Map whose key  is the name of the memcache server
        //and whose value is a Map with the memcache statistics


        //logger.info("Map size returning is " + memcacheStats.size());

        //produce a TextTable for each server listed
        Map<String, TextTable> returnMap = new HashMap<String, TextTable>();

        TextTable outputTextTable = null;

        Set<Map.Entry> statEntries = memcacheStats.entrySet();

        //set counter to allow to set number of columns to output
        for (Map.Entry statEntry : statEntries) {
            String key = (String) statEntry.getKey();
            Map statsMap = (Map) statEntry.getValue();
            //is this case, it is a Map with the statistics
            //get size so we know how big to make TextTable
            outputTextTable = new TextTable(statsMap.size(), 2);
            //set Header
            outputTextTable.setHeader(0, "Parameter");
            outputTextTable.setHeader(1, "Value");
            //outputTextTable.setHeader(2, "for " + key);
            //get this value's iterator
            Set<Map.Entry> statsMapEntries = statsMap.entrySet();
            int counter=0;
            for (Map.Entry statsMapEntry : statsMapEntries) {
                outputTextTable.setField(counter, 0,
                        (CharSequence) statsMapEntry.getKey());
                outputTextTable.setField(counter++, 1,
                        (CharSequence) statsMapEntry.getValue());
            }
            //add each TextTable for each server listed to return Map.
            returnMap.put(key, outputTextTable);

        }
        return returnMap;
    }

    /* This method is used for dynamic memcache stats gathering.
    * The TextTable will contain all memcache server instances in columns
    * and the server parameters in rows
    * @return TextTable
    *@see com.sun.faban.common.TextTable
    */

    public TextTable getTemporaryStats() {
        Long time = System.currentTimeMillis();
        int elapsed = 0;
        if (baseTime == Long.MIN_VALUE)
            baseTime = time;
        else
            elapsed = (int) (time - baseTime);

        String elapsedSecs = String.format("%.3f", elapsed/1000d);

        Map memcacheStats = cache.stats();
        //cache.stats() will return a Map whose key  is the name of the memcache server
        //and whose value is a Map with the memcache statistics
        TextTable outputTextTable = null;
        Set<Map.Entry> serverEntries = memcacheStats.entrySet();

        //set counter to allow to set number of columns to output
        int counter = 0;
        int columnIndex = 0;

        //reset the iterator
        for (Map.Entry serverEntry : serverEntries) {
            String key = (String) serverEntry.getKey();
            Map statsMap = (Map) serverEntry.getValue();
            if (outputTextTable == null) {
                // One extra row for elapsed time, one extra header column.
                outputTextTable = new TextTable(statsMap.size(),
                        serverEntries.size() + 2);
            }
            //is this case, it is a Map with the statistics
            //get size so we know how big to make TextTable
            // the number of rows is the number of stats
            // the number of columns is how many server instances there are
            //set Header
            outputTextTable.setHeader(0, "Elapsed (sec)");
            outputTextTable.setHeader(1, "Parameter");
            outputTextTable.setHeader(columnIndex + 2, key);

            //get this value's iterator
            Set<Map.Entry> statsMapEntries = statsMap.entrySet();
            counter=0; //reset counter

            // Populate the rest of the table.
            for (Map.Entry statsMapEntry : statsMapEntries) {
                outputTextTable.setField(counter, 0, elapsedSecs);
                outputTextTable.setField(counter, 1,
                        (CharSequence) statsMapEntry.getKey());
                outputTextTable.setField(counter++, columnIndex + 2,
                        (CharSequence) statsMapEntry.getValue());
            }
            ++columnIndex;
        }
        return outputTextTable;
    }

    /*
    This main method is used to gather dynamic statistics on memcache server instances.
    *  It expects at least 4 arguments:
    *
    *  host:server host:server (additional server instances can be designated as host1:port1 host1:port2 OR host2:port etc.
    * -s start time:  the ramp up time, in seconds.  (status collection does not take place during the ramp up)
    * -e end time: the steady state, in seconds. (time to do the statistics data collection)
    * -i interval time: the snapshot period to collect the stats, in seconds.
    *
    *     Usage:  java com.sun.web20.MemCacheUtility server:port [server2:port server3:port] -s startTime -e endTime -i interval
    *     eg. java com.sun.web20.util.MemCacheUtility server1:12100 server2:12100 -s 300 -e 600 -i 3
    *     This will sleep for 300 seconds during ramp up, collect for 600 seconds with an interval of 3 seconds between
    *     each snapshot.
    *     @param args String []
    *
    */
    public static void main (String[] args) {

        if (args==null || args.length < 4) {//minimum amount of args - one server, -s, -e, -i
            System.out.println("Usage:  java org.apache.olio.workload.util.MemCacheUtility server:port [server2:port server3:port] -s startTime -e endTime -i interval");
            System.out.println(" where startTime = ramp up time in seconds.  Statistics collection will NOT occur during ramp up time and will sleep for startTime period");
            System.out.println(" endTime = steady State time in seconds.  Statistics collection will only occur during the steady state period");
            System.out.println(" interval = time between statistics collection snapshots, in seconds.");
        }


        int startTime = 0;
        int endTime = 0;
        int intervalTime = 0;
        LinkedHashSet<String> serverSet = new LinkedHashSet<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-s")) {
                if (args[i].length() > 2) // -sarg
                    startTime = Integer.parseInt(args[i].substring(2)) * 1000;
                else // -s arg
                    startTime = Integer.parseInt(args[++i]) * 1000;
            } else if (args[i].startsWith("-e")) {
                if (args[i].length() > 2) // -earg
                    endTime = Integer.parseInt(args[i].substring(2)) * 1000;
                else // -e arg
                    endTime = Integer.parseInt(args[++i]) * 1000;
            } else if (args[i].startsWith("-i")) {
                if (args[i].length() > 2) // -iarg
                    intervalTime =
                            Integer.parseInt(args[i].substring(2)) * 1000;
                else // -i arg
                    intervalTime = Integer.parseInt(args[++i])* 1000;
            } else if (args[i].contains(":")) {// host:port pair
                serverSet.add(args[i]);
            } else { // host only. Append default port 11211.
                serverSet.add(args[i] + ":11211");
            }
        }

        //finished processing all of the args.  populate server list
        String memCacheServers[] = new String[serverSet.size()];
        memCacheServers = serverSet.toArray(memCacheServers);

        logger.info("Starting memcache stats");

        //collect only during steady state
        MemCacheUtility memCacheUtil = new MemCacheUtility(memCacheServers);

        try {
            Timer timer = new Timer();
            MemCacheTask task = new MemCacheTask(memCacheUtil);
            timer.scheduleAtFixedRate(task, startTime, intervalTime);
            //only print stats for steady state period
            Thread.sleep(endTime);
            //wake up and stop printing stats
            timer.cancel();
        } catch (InterruptedException ex)  {
            ex.printStackTrace();
            return;
        }
    }

    /* class for TimerTask */

    private static class MemCacheTask extends TimerTask {

        private MemCacheUtility memCacheUtility;

        public MemCacheTask(MemCacheUtility memCacheUtil) {
            memCacheUtility = memCacheUtil;

        }

        public void run() {

            System.out.println(memCacheUtility.getTemporaryStats());

        }

    }




}
