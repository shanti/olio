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
 * $Id: OlioBenchmark.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */

package org.apache.olio.workload.harness;

import com.sun.faban.common.Command;
import com.sun.faban.common.CommandHandle;
import com.sun.faban.common.NameValuePair;
import com.sun.faban.harness.DefaultFabanBenchmark;
import com.sun.faban.harness.RunContext;
import com.sun.faban.harness.engine.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

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
    private List<NameValuePair<Integer>> memcacheServers;
    private String webServerBinPath, webServerLogPath, webServerConfPath;
    private String webServerPidPath, phpIniPath, cacheBinPath, dbConfPath;
    WebServerService webServerService;
    MemcachedService memcachedService = MemcachedService.getHandle();

    /**
     * This method is called to configure the specific benchmark run
     * Tasks done in this method include reading user parameters,
     * logging them and initializing various local variables.
     *
     * @throws Exception If configuration was not successful
     */
    public void configure() throws Exception {
        
        params = getParamRepository();

        //Obtaining configuration parameters
        String webserverType = params.getParameter("webServer/type");
 
        webServerBinPath = params.getParameter("webServer/hostBinPath");
        webServerLogPath = params.getParameter("webServer/hostLogPath");
        webServerConfPath = params.getParameter("webServer/hostConfPath");
        webServerPidPath = params.getParameter("webServer/hostPidPath");
        phpIniPath = params.getParameter("webServer/phpIniPath");
        cacheBinPath = params.getParameter("cacheServers/cacheBinPath");
        dbConfPath = params.getParameter("dbServer/dbConfPath");
        String[] dbhosts = params.getParameter(
                            "dbServer/fa:hostConfig/fa:host").split(" ");
        String[] webhosts = params.getParameter(
                            "webServer/fa:hostConfig/fa:host").split(" ");

        if ("apache".equals(webserverType)) {
            webServerService = ApacheHttpdService.getHandle();
            for (String webhost : webhosts) {
                RunContext.getFile(webhost, webServerConfPath +
                        File.separator + "httpd.conf", RunContext.getOutDir() +
                        "httpd_conf.log." + getHostName(webhost));
            }
        } else if ("lighttpd".equals(webserverType)) {
            webServerService = LighttpdService.getHandle();
            for (String webhost : webhosts) {
                RunContext.getFile(webhost, webServerConfPath +
                        File.separator + "lighttpd.conf",
                        RunContext.getOutDir() + "lighttpd_conf.log." +
                        getHostName(webhost));
            }
        } else if ("glassfish".equals(webserverType)) {
            webServerService = GlassfishService.getHandle();
            for (String webhost : webhosts) {
                RunContext.getFile(webhost, webServerConfPath +
                        File.separator + "domain.xml",
                        RunContext.getOutDir() + "domain_xml.log." +
                        getHostName(webhost));
            }
        }
        if (phpIniPath != null && phpIniPath.length() > 0)
            for (String webhost : webhosts) {
                RunContext.getFile(webhost, phpIniPath + "/php.ini",
                        RunContext.getOutDir() + "php_ini.log." +
                        getHostName(webhost));
            }

        for (String dbhost : dbhosts) {
            RunContext.getFile(dbhost, dbConfPath + "/my.cnf",
            RunContext.getOutDir() + "my_cnf.log." + getHostName(dbhost));
        }

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
            logger.info("Reloading the database for " + scale + " users!");
            String dbhost = dbhosts[0];
            String driver = params.getParameter("dbServer/dbDriver");
            String connectURL = params.getParameter("dbServer/connectURL");
            // Un-escape the URL.
            connectURL = connectURL.replace("&amp;", "&");

            boolean autoCommit = Boolean.parseBoolean(params.getParameter(
                                                    "dbServer/autoCommit"));
            ArrayList<String> cmdList = new ArrayList<String>();
            if (autoCommit)
                cmdList.add("-Dcommit.tx=false");
            cmdList.add("org.apache.olio.workload.loader.LoadController");
            cmdList.add(driver);
            cmdList.add(connectURL);
            cmdList.add(String.valueOf(scale));
            Command c = new Command(cmdList);
            c.setSynchronous(false);
            dbHandle = java(dbhost, c);
        }

        if (reloadMedia) {
            logger.info("Reloading images/media for " + scale + " users!");
            String mediaHost = params.getParameter(
                                        "dataStorage/fa:hostConfig/fa:host");
            String mediaDir = params.getParameter("dataStorage/mediaDir");
            Command c = new Command("org.apache.olio.workload.fsloader.FileLoader",
                        getBenchmarkDir() + "resources", mediaDir,
                        String.valueOf(scale));
            c.setSynchronous(false);
            mediaHandle = java(mediaHost, c);
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
        memcacheServers =
                 params.getHostPorts("cacheServers/fa:hostConfig/fa:hostPorts");

         // Assign the default port.
         for (NameValuePair<Integer> hostPort : memcacheServers) {
             if (hostPort.value == null)
                 hostPort.value = 11211;
         }

        int index = 0;
        String memServers[] = new String[memcacheServers.size()];
        int ports[] = new int[memcacheServers.size()];
        for (NameValuePair<Integer> thisCacheServer : memcacheServers) {
            memServers[index] = thisCacheServer.name;
            ports[index++] = thisCacheServer.value;
        } 
        memcachedService.setup(memServers, ports, "-u mysql -m 256",
                cacheBinPath);
        if ( !memcachedService.restartServers())
            throw (new Exception("Memcached server(s) restart failed"));
        
        // Now start the web servers
        if (webServerService != null) {
            webServerService.setup(webhosts, webServerBinPath, webServerLogPath,
                                    webServerConfPath, webServerPidPath);
            if (!webServerService.restartServers())
                throw (new Exception("Webserver(s) restart failed"));
        }

        //calculate total running time, including rampUp, steadyState,
        // and rampDown
        String rampUp = params.getParameter(
                               "fa:runConfig/fa:runControl/fa:rampUp");
        String steadyState = params.getParameter(
                               "fa:runConfig/fa:runControl/fa:steadyState");
        String rampDown = params.getParameter(
                               "fa:runConfig/fa:runControl/fa:rampDown");

        this.totalRunningTimeInSecs = Integer.parseInt(rampUp) +
                Integer.parseInt(steadyState) + Integer.parseInt(rampDown);

        super.configure();
    }

    /* override DefaultBenchmark's end method to collect webserver log file
     * via the OlioBenchmark harness class
     */
    public void end () throws Exception {
     
        super.end();
        //stop the memcached servers
        //logger.info("Stopping Memcached servers");
        //memcachedService.stopServers();

        if (webServerService != null) {
            // xfer logs
            logger.info("Transferring webserver error logs");
            webServerService.xferLogs(totalRunningTimeInSecs);

            // stop web servers
            logger.info("Stopping web servers");
            webServerService.stopServers();
        }
    }

    /* Override DefaultBenchmark's kill method to stop the servers.
     */
    public void kill() throws Exception {
        memcachedService.stopServers();
        if (webServerService != null)
            webServerService.kill();
        super.kill();
    }
}
