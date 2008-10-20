/*
 * MemCacheUtility.java
 *
 * Created on August 20, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.web20.util;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import com.sun.faban.common.TextTable;
import com.sun.faban.common.NameValuePair;
import java.util.TimerTask;
import java.util.Timer;

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
            cache.setPoolName("livePool");
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
                cache.setPoolName("livePool");
            }
    }
        
    /*
     * This method is a convenience method to convert ArrayList<NameValuePair<Integer> to
     * a String array of server:port server2:port.
     * @param servers  ArrayList<NameValuePair<Integer>>
     * @return String []     
     */    

    public static String[] convertNameValueToStringArray (ArrayList<NameValuePair<Integer>> servers) {
         String [] serverArr = new String[servers.size()];
            
            Iterator serverIter = servers.iterator();
            int index = 0;
            
            while (serverIter.hasNext()) {
                NameValuePair<Integer> serverEntry = (NameValuePair) serverIter.next();
                serverArr[index] = serverEntry.name + ":" + serverEntry.value;  
                //logger.info(serverEntry.name + ":" + serverEntry.value);
                //logger.info("server list in string array " + serverArr[index]);
                index=+1;
            }
            return serverArr;
        
        
    }
    
    /** Retrieves memcache stats for each instance of MemCacheUtility.
     *  A TextTable will be produced for each server used to create an
     *  instance of MemCacheUtility.  Used to persist stats file for each server.
     *  Returning Map whose key is the servername, the value is a TextTable of statistics
     *  @return Map 
     */
    
   public Map getStats() {
       
       
       Map memcacheStats = cache.stats();
       //cache.stats() will return a Map whose key  is the name of the memcache server
       //and whose value is a Map with the memcache statistics
        
       
       //logger.info("Map size returning is " + memcacheStats.size());
       Set serverKeys = memcacheStats.keySet();         // The set of keys in the map       
                    
       //produce a TextTable for each server listed
       Map <String ,TextTable> returnMap = new HashMap<String, TextTable>();
       
       TextTable outputTextTable = null;
       
      Iterator keyIter = serverKeys.iterator();
      Iterator statsMapIter = null;
      Set statsMapKeys = null;                                
      
      //set counter to allow to set number of columns to output
      int counter = 0;
      
      while (keyIter.hasNext()) {
         String key = (String)keyIter.next();  // Get the next key.
         Map statsMap = (Map)memcacheStats.get(key);  // Get the value for that key.         
         //is this case, it is a Map with the statistics
         //get size so we know how big to make TextTable
         outputTextTable = new TextTable(statsMap.size(), 2);
         //set Header
         
         outputTextTable.setHeader(0, "Parameter");
         outputTextTable.setHeader(1, "Value");
         //outputTextTable.setHeader(2, "for " + key);
         //get this value's iterator
         statsMapKeys = statsMap.keySet();
         statsMapIter = statsMapKeys.iterator();
         counter=0; //reset counter
         while (statsMapIter.hasNext()) {
            String statsKey = (String)statsMapIter.next();  // Get the next key.
            String statsValue = (String)statsMap.get(statsKey);  // Get the value for that key.            
            outputTextTable.setField(counter,0,(String)statsKey);
            outputTextTable.setField(counter,1,(String)statsValue);            
            //logger.info("   (" + statsKey + "," + statsValue + ")");
            counter=counter+1;                          
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
       
       Map memcacheStats = cache.stats();
       //cache.stats() will return a Map whose key  is the name of the memcache server
       //and whose value is a Map with the memcache statistics
      TextTable outputTextTable = null;
      Set serverKeys = memcacheStats.keySet();         // The set of keys in the map       
      
      Iterator keyIter = serverKeys.iterator();
      Iterator statsMapIter = null;
      Set statsMapKeys = null;                                
      
      
      //need to determine the number of parameters in map, i.e. the number of rows in texttable
      // number of columns = size of serverKeys
      //the number of rows = size of map for a serverKey
      int numberOfRows = ((Map)memcacheStats.get(keyIter.next())).size();     
      outputTextTable = new TextTable(numberOfRows, serverKeys.size()+1);
                        
      //set counter to allow to set number of columns to output
      int counter = 0;
      int columnIndex = 0;
      
      //reset the iterator
      keyIter = serverKeys.iterator();
      
      while (keyIter.hasNext()) {
         String key = (String)keyIter.next();  // Get the next key.
         Map statsMap = (Map)memcacheStats.get(key);  // Get the value for that key.         
         //is this case, it is a Map with the statistics
         //get size so we know how big to make TextTable
         // the number of rows is the number of stats
         // the number of columns is how many server instances there are             
         //set Header
         
         outputTextTable.setHeader(0, "Parameter");         
         outputTextTable.setHeader(columnIndex+1, key);
         
         //get this value's iterator
         statsMapKeys = statsMap.keySet();
         statsMapIter = statsMapKeys.iterator();
         counter=0; //reset counter
         while (statsMapIter.hasNext()) {
            String statsKey = (String)statsMapIter.next();  
            String statsValue = (String)statsMap.get(statsKey);
            outputTextTable.setField(counter,0,(String)statsKey);
            outputTextTable.setField(counter,columnIndex+1,(String)statsValue);                        
            counter=counter+1;                          
         }
         columnIndex=columnIndex+1;                                                                    
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
   public static void main (String[] args) throws InterruptedException {
             
       if (args==null || args.length < 4) {//minimum amount of args - one server, -s, -e, -i
           System.out.println("Usage:  java com.sun.web20.MemCacheUtility server:port [server2:port server3:port] -s startTime -e endTime -i interval");
          System.out.println(" where startTime = ramp up time in seconds.  Statistics collection will NOT occur during ramp up time and will sleep for startTime period");
          System.out.println(" endTime = steady State time in seconds.  Statistics collection will only occur during the steady state period");
          System.out.println(" interval = time between statistics collection snapshots, in seconds.");
       }
                
       
       int startTime = 0;
       int endTime = 0;
       int intervalTime = 0;
       int numberOfServers = 0;
       for (int i=0;i<args.length-1;i++) {           
           if(args[i].contains(":")){                                
              //we have to know how many servers have been passed to arg line
               //we will only know this after processing all of the command line args
               //set a counter to see how many servers we have
               numberOfServers = numberOfServers +1;
           }
           if(args[i].contentEquals("-s"))
               startTime = Integer.parseInt(args[i+1])* 1000;               
           if(args[i].contentEquals("-e"))
               endTime = Integer.parseInt(args[i+1]) * 1000;               
           if(args[i].contentEquals("-i"))
               intervalTime = Integer.parseInt(args[i+1])* 1000;               
       }
       
       //finished processing all of the args.  populate serverList       
       String memCacheServers[] = new String [numberOfServers];
       for (int i=0;i<numberOfServers;i++) {           
           memCacheServers[i] = args[i];
       }
       
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
