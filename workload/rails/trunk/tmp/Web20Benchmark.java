
/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html or
 * install_dir/legal/LICENSE
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at install_dir/legal/LICENSE.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: Web20Benchmark.java,v 1.8 2007/07/25 17:56:55 akara Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.web20.harness;

import com.sun.faban.driver.core.DriverContext;
import static com.sun.faban.harness.RunContext.*;
import com.sun.faban.harness.RunContext;

import com.sun.faban.harness.DefaultFabanBenchmark;
import com.sun.faban.common.Command;
import com.sun.faban.common.CommandHandle;

import java.io.File;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.sun.faban.harness.RemoteCallable;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import com.sun.faban.common.NameValuePair;
import com.sun.web20.util.MemCacheUtility;
import com.sun.faban.common.TextTable;
import java.io.FileWriter;
import java.util.Set;
import java.util.Iterator;

/**
 * Harness hook for the sample web benchmark. This class is not needed
 * for benchmarks implemented using the Faban Driver Framework if the
 * default behavior is sufficient. We just show the hooks you can
 * customize in this class. If the default behavior is desired, you can
 * leave out the benchmark-class element in benchmark.xml.
 *
 * @author Akara Sucharitakul
 */
public class Web20Benchmark extends DefaultFabanBenchmark {
    
    static Logger logger = Logger.getLogger(
                                        Web20Benchmark.class.getName());
    int totalRunningTimeInSecs = 0;
    private LinkedHashSet<String> hostsSet;
    private ArrayList<NameValuePair<Integer>> hostsPorts;    


    /**
     * Allows benchmark to validate the configuration file. Note that no
     * execution facility is available during validation.
     *
     * @throws Exception if any error occurred.
     * @see com.sun.faban.harness.RunContext#exec(com.sun.faban.common.Command)
     */
    public void validate() throws Exception {
        getParamRepository().setParameter("fa:runConfig/fd:driverConfig" +
                "[@name='UIDriver']/fd:properties/fd:property" +
                "[@name='resourcePath']",
                getBenchmarkDir() + "resources" + File.separator);
        super.validate();
    }

    /**
     * This method is called to configure the specific benchmark run
     * Tasks done in this method include reading user parameters,
     * logging them and initializing various local variables.
     *
     * @throws Exception If configuration was not successful
     */
    public void configure() throws Exception {
        // Add additional configuration needs such as restarting/reconfiguring
        // servers here.
        boolean reloadDB = Boolean.parseBoolean(
                params.getParameter("dbServer/reloadDB"));
        boolean reloadMedia = Boolean.parseBoolean(
                params.getParameter("primaryStorage/reloadMedia"));
        
       //grab config files
        String coolstackHome = params.getParameter("coolstackHome");
        
        //cache servers get list of servers for the host        
        String otherServers = params.getParameter("otherServers/serverList");        
        
        // replacing all the newline characters and other white space
        // characters with a blank space
        otherServers = otherServers.replaceAll("\\s", " ");
        params.setParameter("otherServers/fa:hostConfig/fa:host", otherServers);

        // Find the patterns that have either hostname or hostname:port values
        Pattern p1 = Pattern.compile("([a-zA-Z_0-9-]+):?(\\w*)\\s*");
        Matcher m1 = p1.matcher(otherServers + ' '); // add a whitespace at end

        hostsSet = new LinkedHashSet<String>();
        hostsPorts = new ArrayList<NameValuePair<Integer>>();

        //  Fill up the hosts set with names of all the hosts
        for (boolean found = m1.find(); found; found = m1.find()) {
            NameValuePair<Integer> hostPort = new NameValuePair<Integer>();
            hostPort.name = m1.group(1);
            String port = m1.group(2);
            if (port != null && port.length() > 1)
                hostPort.value = new Integer(port);
            //logger.info("adding host:" + hostPort.name);
            //logger.info("with port number " + hostPort.value);            
            hostsSet.add(hostPort.name);
            hostsPorts.add(hostPort);
        }

        // Now extract the unique hosts
        StringBuffer hosts = new StringBuffer();
        for (String host : hostsSet) {
            hosts.append(host);
            hosts.append(' ');
        }
                
        // Update the unique hosts to the host filed and save
        params.setParameter("otherServers/fa:hostConfig/fa:host",
                                                    hosts.toString().trim());
        logger.info("Hosts: " + params.getParameter(
                                        "otherServers/fa:hostConfig/fa:host"));
        params.save();        
        //instantiate clients
        MemCacheUtility memUtility = new MemCacheUtility(hostsPorts);
        //output calls to Stats for interval period
        String [] serverArgs = MemCacheUtility.convertNameValueToStringArray(hostsPorts);
        //create output file for each server in hostsPorts
        File memcacheStatsFile = null;
        FileWriter fwriter = null;
                 
                 
        Map memcacheMap = memUtility.getStats();
        Set memcacheMapKeySet = memcacheMap.keySet();
        Iterator memcacheMapIter = memcacheMapKeySet.iterator();
        
        while (memcacheMapIter.hasNext()) {
           String serverKey = (String)memcacheMapIter.next();  // Get the next key.
           TextTable statsTable = (TextTable)memcacheMap.get(serverKey);  // Get the value for that key.            
           memcacheStatsFile = new File(RunContext.getOutDir() + "memCachestats.log." + serverKey);
           fwriter = new FileWriter(memcacheStatsFile);
           fwriter= (FileWriter)statsTable.format(fwriter);
           fwriter.flush();
        }

        fwriter.close();
                                       
        //dbserver information
        String dbhost = params.getParameter("dbServer/fa:hostConfig/fa:host");
        String webhost = params.getParameter("webServer/fa:hostConfig/fa:host");
                          
        boolean success = RunContext.getFile(webhost, coolstackHome + "/apache2/conf/httpd.conf", RunContext.getOutDir() + "httpd.conf." + webhost);        
        success = RunContext.getFile(webhost, coolstackHome + "/php5/lib/php.ini", RunContext.getOutDir() + "php.ini." + webhost);
        //logger.info("Trying to see if the cnf file exists on the dbserver");
        //get the mysql config file, if present
        if (RunContext.isDirectory(dbhost,"/etc") && RunContext.isFile(dbhost, "/etc/my.cnf")){
            success = RunContext.getFile(dbhost, "/etc/my.cnf", RunContext.getOutDir() + "my.cnf." + dbhost);
        }
        if (RunContext.isDirectory(dbhost,coolstackHome + "mysql_32bit") && RunContext.isFile(dbhost, coolstackHome + "mysql_32bit/my.cnf")){
            success = RunContext.getFile(dbhost, "/opt/coolstack/mysql_32bit/my.cnf", RunContext.getOutDir() + "my.cnf" + webhost);
        }
        logger.info("Finished checking");
                                         
        int scale = -1;
        if (reloadDB || reloadMedia)
            scale =Integer.parseInt(params.getParameter("dbServer/scale"));

        CommandHandle dbHandle = null;
        CommandHandle mediaHandle = null;
        if (reloadDB) {
            String dbHost = params.getParameter("dbServer/fa:hostConfig/fa:host");            
            String driver = params.getParameter("dbServer/dbDriver");
            String connectURL = params.getParameter("dbServer/connectURL");
            // Un-escape the URL.
            connectURL = connectURL.replace("&amp;", "&");
            Command c = new Command("com.sun.web20.loader.LoadController " +
                                    driver + ' ' + connectURL + ' ' + scale);
            c.setSynchronous(false);
            dbHandle = java(dbHost, c);
        }

        if (reloadMedia) {
            String mediaHost = params.getParameter("primaryStorage/fa:hostConfig/fa:host");
            Command c = new Command("loader.pl -d sfbay.sun.com -s " + scale);
            c.setSynchronous(false);
            c.setStreamHandling(Command.STDOUT, Command.TRICKLE_LOG);
            c.setStreamHandling(Command.STDERR, Command.TRICKLE_LOG);            
            mediaHandle = exec(mediaHost, c);
        }
        
        if (dbHandle != null)
            dbHandle.waitFor();
        
        if (mediaHandle != null)
            mediaHandle.waitFor();
        
        super.configure();
    }
    
     /**
     * override DefaultBenchmark's start method so memcache stats can be collected 
      * via the Web20benchmark harness class
     */
    public void start() throws Exception {
        super.start();
        
        //get the server list
         String otherServers = params.getParameter("otherServers/serverList");
       //get the run info
        
        String rampUp = params.getParameter("fa:runConfig/fa:runControl/fa:rampUp");
        String steadyState = params.getParameter("fa:runConfig/fa:runControl/fa:steadyState");
        String rampDown = params.getParameter("fa:runConfig/fa:runControl/fa:rampDown"); 
        
        //calculate total running time, including rampUp, steadyState, and rampDown
        this.totalRunningTimeInSecs = Integer.parseInt(rampUp) + Integer.parseInt(steadyState) + Integer.parseInt(rampDown);
        
        //for interval, get it from tools element
        String toolString = params.getParameter("otherServers/fa:hostConfig/fh:tools");        
        
        StringTokenizer strToken = new StringTokenizer(toolString,";");
        StringTokenizer intervalTk = null;
        String tool = null;
        String interval =null;
        
        while (strToken.hasMoreTokens()) {
            tool = strToken.nextToken();
            if (tool.contains("memcache")) {
                intervalTk = new StringTokenizer(tool);
                interval = intervalTk.nextToken();
                interval = intervalTk.nextToken();//need to do this twice to get interval period                
            }
        } 
        //logger.info("launching memcache command with interval time of " + interval);
       Command statsCommand = new Command(" com.sun.web20.util.MemCacheUtility " + otherServers + " -s " 
               + rampUp + " -e " + steadyState + " -i " + interval);        
        statsCommand.setStreamHandling(Command.STDOUT, Command.CAPTURE);   
        statsCommand.setOutputFile(Command.STDOUT,  RunContext.getOutDir()+"memCachestats.allServers.log.out");
        RunContext.java(statsCommand);                
    }
    
    /* override DefaultBenchmark's end method to collect apache log file
     * via the Web20Benchmark harness class
     */
    
    public void end () throws Exception {
        //grab the system time on the Faban master machine
        
        super.end();        
        String webhost = params.getParameter("webServer/fa:hostConfig/fa:host");
        //grab config files
        String coolstackHome = params.getParameter("coolstackHome");
        
        GregorianCalendar calendar = getGregorianCalendar(webhost);
        //gger.info("calendar is " + calendar.getTime());
        //format the end date
        SimpleDateFormat df = new SimpleDateFormat("MMM,dd,HH:mm:ss");
        String beginDate = df.format(calendar.getTime());
        //logger.info("Start Date: "+ beginDate);
        calendar.add(Calendar.SECOND, (totalRunningTimeInSecs*(-1)));
        //logger.info("Working backwards, start time must have been" +calendar.getTime());
        String endDate = df.format(calendar.getTime());
        //collect the log file
        boolean success = RunContext.getFile(webhost,coolstackHome + "/apache2/logs/error_log", RunContext.getOutDir() + "apache.error_log." + webhost);
        //parse the log file
        Command parseCommand = new Command("truncate_errorlog.sh \"" + beginDate + "\""+ " \"" + endDate + "\" " +
                //location
                RunContext.getOutDir() + " " +
                //host
                webhost);
        RunContext.exec(parseCommand);
       
        
    }
    
    
     public static GregorianCalendar getGregorianCalendar(String hostName) throws Exception {
        return exec(hostName, new RemoteCallable<GregorianCalendar>() {
            public GregorianCalendar call() {
                return new GregorianCalendar();
            }
        });
    } 
   
}
