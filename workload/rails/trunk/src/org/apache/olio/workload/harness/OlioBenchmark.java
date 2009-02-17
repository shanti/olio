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
 * $Id: OlioBenchmark.java,v 1.27 2008/03/20 22:31:40 akara Exp $
 *
 */
package org.apache.olio.workload.harness;

import com.sun.faban.common.Command;
import com.sun.faban.common.CommandHandle;
import com.sun.faban.common.NameValuePair;
import com.sun.faban.harness.DefaultFabanBenchmark;
import com.sun.faban.harness.RunContext;
import com.sun.faban.harness.engine.ApacheHttpdService;
import com.sun.faban.harness.engine.LighttpdService;
import com.sun.faban.harness.engine.MemcachedService;

import com.sun.faban.harness.engine.WebServerService;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sun.faban.harness.RunContext.*;

/**
 * Harness hook for the sample web benchmark. This class is not needed
 * for benchmarks implemented using the Faban Driver Framework if the
 * default behavior is sufficient. We just show the hooks you can
 * customize in this class. If the default behavior is desired, you can
 * leave out the benchmark-class element in benchmark.xml.
 *
 * @author Akara Sucharitakul
 */
public class OlioBenchmark extends DefaultFabanBenchmark {
    
    static Logger logger = Logger.getLogger(
                                        OlioBenchmark.class.getName());
    int totalRunningTimeInSecs = 0;
    private LinkedHashSet<String> hostsSet;
    private ArrayList<NameValuePair<Integer>> hostsPorts;    
    private String webServerBinPath, webServerLogPath, webServerConfPath;
    private String webServerPidPath, phpIniPath, cacheBinPath, dbConfPath;
    WebServerService webServerService;
    MemcachedService memcachedService = MemcachedService.getHandle();

    /**
     * Allows benchmark to validate the configuration file. Note that no
     * execution facility is available during validation.
     *
     * @throws Exception if any error occurred.
     * @see com.sun.faban.harness.RunContext#exec(com.sun.faban.common.Command)
     */
    public void validate() throws Exception {
        params = getParamRepository();
        params.setParameter("fa:runConfig/fd:driverConfig" +
                "[@name='UIDriver']/fd:properties/fd:property" +
                "[@name='resourcePath']",
                getBenchmarkDir() + "resources" + File.separator);

        //cache servers get list of servers for the host
        String cacheServers = params.getParameter("cacheServers/serverList");

        // replacing all the newline characters and other white space
        // characters with a blank space
        cacheServers = cacheServers.replaceAll("\\s", " ");

        // Find the patterns that have either hostname or hostname:port values
        Pattern p1 = Pattern.compile("([a-zA-Z_0-9-\\.]+):?(\\w*)\\s*");
        Matcher m1 = p1.matcher(cacheServers + ' '); // add a whitespace at end

        hostsSet = new LinkedHashSet<String>();
        hostsPorts = new ArrayList<NameValuePair<Integer>>();

        //need to get these data structures for restarting memcache servers for RunContext

        //  Fill up the hosts set with names of all the hosts
        for (boolean found = m1.find(); found; found = m1.find()) {
            NameValuePair<Integer> hostPort = new NameValuePair<Integer>();
            hostPort.name = m1.group(1);
            String port = m1.group(2);
            if (port != null && port.length() > 1)
                hostPort.value = new Integer(port);
            else
                hostPort.value = 11211;
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
        String cacheHosts = hosts.toString().trim();
        params.setParameter("cacheServers/fa:hostConfig/fa:host", cacheHosts);
        logger.info("Memcached Hosts: " + cacheHosts);

        params.save();
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
        
        //Obtaining configuration parameters
        String webserverType = params.getParameter("webServer/type");
 
        webServerBinPath = params.getParameter("webServer/hostBinPath");
        webServerLogPath = params.getParameter("webServer/hostLogPath");
        webServerConfPath = params.getParameter("webServer/hostConfPath");
        webServerPidPath = params.getParameter("webServer/hostPidPath");
        phpIniPath = params.getParameter("webServer/phpIniPath");
        cacheBinPath = params.getParameter("cacheServers/cacheBinPath");
        dbConfPath = params.getParameter("dbServer/dbConfPath");
        String dbhost = params.getParameter("dbServer/fa:hostConfig/fa:host");
        String webhost = params.getParameter("webServer/fa:hostConfig/fa:host");
        /*
       if (webserverType.equals("apache")) {
            webServerService = ApacheHttpdService.getHandle();                           
            RunContext.getFile(webhost, webServerConfPath +
                    File.separator + "httpd.conf",
                            RunContext.getOutDir() + "httpd.conf." + webhost);
       } else {
            webServerService = LighttpdService.getHandle();
            RunContext.getFile(webhost, webServerConfPath + 
                    File.separator + "lighttpd.conf",
                            RunContext.getOutDir() + "lighttpd.conf." + webhost);
       }
       */
        /* RunContext.getFile(webhost, phpIniPath + "/php.ini",
                            RunContext.getOutDir() + "php.ini." + webhost);

        RunContext.getFile(dbhost, dbConfPath + "/my.cnf",
                RunContext.getOutDir() + "my.cnf." + dbhost);
        */

        // Reloading database and media as necessary.
        boolean reloadDB = Boolean.parseBoolean(
                params.getParameter("dbServer/reloadDB"));
        boolean reloadMedia = Boolean.parseBoolean(
                params.getParameter("dataStorage/reloadMedia"));

        int scale = -1;
        if (reloadDB || reloadMedia)
            scale =Integer.parseInt(params.getParameter("dbServer/scale"));

        CommandHandle dbHandle = null;
        CommandHandle mediaHandle = null;
        if (reloadDB) {
            String dbHost =
                    params.getParameter("dbServer/fa:hostConfig/fa:host");
            String driver = params.getParameter("dbServer/dbDriver");
            String connectURL = params.getParameter("dbServer/connectURL");
            // Un-escape the URL.
            connectURL = connectURL.replace("&amp;", "&");
            Command c = new Command("-Dcommit.tx=false " +
                                    "com.sun.web20.loader.LoadController " +
                                    driver + ' ' + connectURL + ' ' + scale);
            c.setSynchronous(false);
            dbHandle = java(dbHost, c);
        }

        if (reloadMedia) {
            String mediaHost = params.getParameter(
                                        "dataStorage/fa:hostConfig/fa:host");
            Command c = new Command("loader.pl -d sfbay.sun.com -s " + scale);
            c.setSynchronous(false);
            c.setStreamHandling(Command.STDOUT, Command.TRICKLE_LOG);
            c.setStreamHandling(Command.STDERR, Command.TRICKLE_LOG);            
            mediaHandle = exec(mediaHost, c);
        }
        
        if (dbHandle != null) {
            dbHandle.waitFor();
			int exitValue = dbHandle.exitValue();
			if (exitValue != 0)
			   logger.severe("DB load error, exited with value " + exitValue);
		}
        
        if (mediaHandle != null) {
            mediaHandle.waitFor();
			int exitValue = mediaHandle.exitValue();
			if (exitValue != 0)
			   logger.severe("File load error, exited with value " + exitValue);
		}

        //start the memcache servers
        /*int index = 0;
        String memServers[] = new String[hostsPorts.size()];
        int ports[] = new int[hostsPorts.size()];
        for (NameValuePair<Integer> thisCacheServer : hostsPorts) {
            memServers[index] = thisCacheServer.name;
            ports[index++] = thisCacheServer.value;
        } 
        memcachedService.setup(memServers, ports, "-u mysql -m 256",
                cacheBinPath);
        if ( !memcachedService.restartServers())
            throw (new Exception("Memcached server(s) restart failed"));
        
        
        // Now start the web servers
        String ahosts[] = new String[1];
        ahosts[0] = webhost;
        webServerService.setup(ahosts, webServerBinPath, webServerLogPath, 
                webServerConfPath, webServerPidPath);
        if ( !webServerService.restartServers())
            throw (new Exception("Webserver(s) restart failed"));
        */
        super.configure();
    }

     /**
      * override DefaultBenchmark's start method so memcache stats can be
      * collected via the Web20benchmark harness class
      */
    public void start() throws Exception {

         //get the server list
         String cacheServers = params.getParameter("cacheServers/serverList");
         String[] cacheHosts = params.getTokenizedValue(
                                        "cacheServers/fa:hostConfig/fa:host");
         String rampUp = params.getParameter(
                                "fa:runConfig/fa:runControl/fa:rampUp");
         String steadyState = params.getParameter(
                                "fa:runConfig/fa:runControl/fa:steadyState");
         String rampDown = params.getParameter(
                                "fa:runConfig/fa:runControl/fa:rampDown");
         String interval = params.getParameter("fa:runConfig/" +
                 "fd:driverConfig[@name='UIDriver']/fd:stats/fd:interval");


         //calculate total running time, including rampUp, steadyState,
         // and rampDown
         this.totalRunningTimeInSecs = Integer.parseInt(rampUp) +
                 Integer.parseInt(steadyState) + Integer.parseInt(rampDown);

//         Command statsCommand = new Command(
//                 "com.sun.web20.util.MemCacheUtility " + cacheServers + " -s "
//                 + rampUp + " -e " + steadyState + " -i " + interval);
//         statsCommand.setSynchronous(false);
//         statsCommand.setStreamHandling(Command.STDOUT, Command.CAPTURE);
//         statsCommand.setOutputFile(Command.STDOUT,  RunContext.getOutDir() +
//                 "memcachestats.log." + getHostName(cacheHosts[0]));
         // Use the real name of the first host.

         super.start();

//         logger.info("Launching memcache stats collection with " +
//                        "interval time of " + interval);
//         RunContext.java(statsCommand);
     }
    
    /* override DefaultBenchmark's end method to collect apache log file
     * via the OlioBenchmark harness class
     */
    
    public void end () throws Exception {
     
        super.end();
        
        //stop the memcached servers
        //logger.info("Stopping Memcached servers");
        //memcachedService.stopServers();
        
        // xfer apache logs
        //logger.info("Transferring Apache error logs");
        //webServerService.xferLogs(totalRunningTimeInSecs);
   
        // stop apache servers
        //logger.info("Stopping Apache servers");
        //webServerService.stopServers();
    }
    
}
