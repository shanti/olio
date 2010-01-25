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
package org.apache.olio.services;

import com.sun.faban.common.Command;
import com.sun.faban.common.CommandHandle;
import com.sun.faban.common.NameValuePair;
import com.sun.faban.harness.*;


import com.sun.faban.harness.services.ClearLogs;
import com.sun.faban.harness.services.GetLogs;
import com.sun.faban.harness.services.ServiceContext;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.List;

/**
 * This class implements the service to manage Rails servers.
 * It only manages the rails application specific functionality and
 * passes the actual server handling to the underlying appserver classes.
 * It also provides functionality to transfer the portion of the app
 * production log for a run to the run output directory.
 * It can be used by any benchmark to manage rails servers and
 * perform these operations remotely using this Service.
 *
 * @author Shanti Subramanyam
 * 
 */
public class RailsService {
 /** Injected service context. */
    @Context public ServiceContext ctx;
    private Logger logger = Logger.getLogger(RailsService.class.getName());

    private String[] myServers;
	private List<NameValuePair<Integer>> hostPorts;
    private String serverType, appDir, rakePath;
    private String appLogFile, appConfFile;
	private Thin thin;

	private boolean skipService = false;

    @Configure public void configure() throws ConfigurationException {
        myServers = ctx.getUniqueHosts();
        hostPorts = ctx.getUniqueHostPorts();
		if (hostPorts == null || hostPorts.get(0) == null) {
			skipService = true; 
			return;
        }
		serverType = ctx.getProperty("type");
		if (serverType == null || serverType.trim().length() == 0)
			serverType = "other";
		else if (serverType.equals("thin")) {
            thin = new Thin(ctx);
        }
		appDir = ctx.getProperty("appDir");
		if (appDir == null) {
			logger.warning("appDir property not set. Assuming /export/home/oliorails");
			appDir = "/export/home/oliorails";
        }

        // We need to locate rake
		rakePath = ctx.getProperty("rakePath");
		if (rakePath == null || rakePath.trim().length() <= 0) {
			logger.warning("rakePath not set. Assuming /var/ruby/1.8/gem_home/bin");
			rakePath = "/var/ruby/1.8/gem_home/bin";
		}
		appLogFile = appDir + File.separator + "log" + 
                File.separator + "production.log";
        appConfFile = appDir + File.separator + "config" +
                File.separator + "environment.rb";
    }

    /**
     * Starts up the Rails server.
     */
    @Start
    public void startup() {
		if (skipService) return;
		if (serverType.equals("thin"))
			thin.startup();
    }

    /**
     * Shuts down the Rails server.
     * @throws IOException Error executing the shutdown
     * @throws InterruptedException Interrupted waiting for the shutdown
     */
    @Stop
    public void shutdown() throws IOException, InterruptedException {
        if (skipService) return;
		if (serverType.equals("thin"))
			thin.shutdown();
    }

    /**
     * Clears the Thin server logs 
     */
    @ClearLogs
    public void clearLogs() {
		if (skipService) return;
		String rake = rakePath + " tmp:clear";
		Command rakeCmd = new Command(rake);
		rakeCmd.setWorkingDirectory(appDir);
        rakeCmd.setStreamHandling(Command.STDOUT, Command.CAPTURE);

        for (String server : myServers) {
			// Clear app log
			if (RunContext.isFile(server, appLogFile))
				RunContext.deleteFile(server, appLogFile);
            try {
		        // Clear app cache
			    logger.fine("Now clearing application cache on " + server);
			    RunContext.exec(server, rakeCmd);
           } catch (Exception ee) {
			   logger.warning("Failed to run 'rake tmp:clear' on " + server);
           }
        }
		if (serverType.equals("thin"))
			thin.clearLogs();
    }

    /*
     * transfer log files
     * This method copies over the application error log to the run 
	 * output directory. It also copies the environment.rb file.
     */
    @GetLogs
    public void getLogs() {
        if (skipService) return;
		Command rcmd = new Command("rails_env.sh");
        rcmd.setStreamHandling(Command.STDOUT, Command.CAPTURE);
		
        for (int i = 0; i < myServers.length; i++) {
            String outFile = RunContext.getOutDir() + "rails_err.log." +
                RunContext.getHostName(myServers[i]);

            // copy the error_log to the master
            if (!RunContext.getFile(myServers[i], appLogFile, outFile)) {
                logger.warning("Could not copy " + appLogFile + " to " + outFile);
                return;
            }
            outFile = RunContext.getOutDir() + "rails_env.rb.log." +
                RunContext.getHostName(myServers[i]);
            if (!RunContext.getFile(myServers[i], appConfFile, outFile)) {
                logger.warning("Could not copy " + appConfFile + " to " + outFile);
                return;
            }
            try {
            // get ruby related config info
			CommandHandle ch = RunContext.exec(myServers[i], rcmd);
            outFile = RunContext.getOutDir() + "ruby_config.log." +
                RunContext.getHostName(myServers[i]);
			ch.fetchOutput(Command.STDOUT, outFile);
            } catch (Exception e) {
                logger.warning("Could not execute/fetch ruby config on " + myServers[i]);
            }
            logger.fine("getLogs Completed for " + myServers[i]);
        }
    }
}
