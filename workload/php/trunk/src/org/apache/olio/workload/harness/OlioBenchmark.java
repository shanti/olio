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
import com.sun.faban.harness.DefaultFabanBenchmark2;
import com.sun.faban.harness.PreRun;

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
public class OlioBenchmark extends DefaultFabanBenchmark2 {
    
    static Logger logger = Logger.getLogger(
                                        OlioBenchmark.class.getName());
    int totalRunningTimeInSecs = 0;
    
    /**
     * This method is called to configure the specific benchmark run
     * Tasks done in this method include reading user parameters,
     * logging them and initializing various local variables.
     *
     * @throws Exception If configuration was not successful
     */
    @PreRun public void prerun() throws Exception {
        
        params = getParamRepository();

        //Obtaining configuration parameters
        String webserver = params.getParameter("webServer/type");
        // Set the appropriate server based on the type
        if (webserver != null && webserver.trim().length() > 0) {
            webserver = webserver.trim();
            if (webserver.equals("apache"))
                params.setParameter("webServer/fh:service/fh:name", "ApacheHttpdService");
            else if (webserver.equals("lighttpd"))
                params.setParameter("webServer/fh:service/fh:name", "LighttpdService");
        }

        String[] dbhosts = params.getParameter(
                            "dbServer/fa:hostConfig/fa:host").split(" ");
        
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
				throw (new Exception("DB load error, exited with value " + exitValue));
		}
        
        if (mediaHandle != null) {
            mediaHandle.waitFor();
			int exitValue = mediaHandle.exitValue();
			if (exitValue != 0)
				throw (new Exception("File load error, exited with value " + exitValue));
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

    }
}
