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
import com.sun.faban.common.Utilities;
import com.sun.faban.harness.*;
import com.sun.faban.harness.services.ServiceContext;

import com.sun.faban.harness.RemoteCallable;
import com.sun.faban.harness.services.GetLogs;
import com.sun.faban.harness.services.ClearLogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the service to start/stop Nginx instances.
 * It also provides functionality to transfer the portion of the nginx
 * error_log for a run to the run output directory.
 * It can be used by any benchmark to manage nginx servers and
 * perform these operations remotely using this Service.
 *
 * @author Shanti Subramanyam
 * Based on ApacheHttpdService provided in faban sample services.
 * It is assumed that nginx resides in the same path on all hosts.
 */
public class NginxService {

    /** Injected service context. */
    @Context public ServiceContext ctx;
    private Logger logger = Logger.getLogger(NginxService.class.getName());
    private String[] myServers = new String[1];
    private String nginxCmd, confFile, errlogFile = null, acclogFile = null;
	private static String pidFile;
	private boolean getAccLog = false;
    CommandHandle nginxHandles[];
	private boolean skipService = false, skipLogs = false;

    /**
     * Configures the service.
     * Assumptions: 
	 * The log files are named access.log and error.log
	 * The pid file is named nginx.pid
	 * It is the user's responsibility to edit the nginx.conf file
	 * and set all parameters appropriately.
     */
    @Configure
    public void configure() throws ConfigurationException {
        myServers = ctx.getUniqueHosts();
		if (myServers == null) {
			skipService = true;
			return;
        }
        nginxCmd = ctx.getProperty("cmdPath");
        if (nginxCmd == null) {
           throw new ConfigurationException("cmdPath property not set. Cannot start/stop nginx");
        }
        String pidDir = ctx.getProperty("pidDir");
        if (pidDir != null && pidDir.trim().length() > 0) {
            if (!pidDir.endsWith(File.separator))
                pidDir = pidDir + File.separator;
            pidFile = pidDir + "nginx.pid";
            logger.fine("pidFile is " + pidFile);
        } else {
            throw new ConfigurationException("pidDir not set. Cannot start/stop nginx.");
        }
        confFile = ctx.getProperty("confPath");
        if (confFile == null) {
            logger.warning("confPath not set. Will use system default");
        } else
		    nginxCmd = nginxCmd + " -c " + confFile;
		getAccLog = Boolean.parseBoolean(ctx.getProperty("getAccLog"));

        String logsDir = ctx.getProperty("logsDir");
        if (logsDir != null && logsDir.trim().length() > 0) {
            if (!logsDir.endsWith(File.separator))
                logsDir = logsDir + File.separator;
            errlogFile = logsDir + "error.log";
            acclogFile = logsDir + "access.log";
        } else {
            logger.warning("logsDir not set. Cannot grab logs");
            skipLogs = true;
        }
        nginxHandles = new CommandHandle[myServers.length];
    }

    /**
     * Starts up the Nginx web server.
     */
    @Start
    public void startup() {
		if (skipService) return;
        Command startCmd = new Command(nginxCmd);
        // startCmd.setSynchronous(false); // to run in bg

        for (int i = 0; i < myServers.length; i++) {
            String server = myServers[i];
            try {
                // Run the command in the foreground and wait for the start
                nginxHandles[i] = RunContext.exec(server, startCmd);

                if (checkServerStarted(server)) {
                    logger.fine("Completed nginx startup successfully on " +
                            server);
                } else {
                    logger.severe("Failed to find " + pidFile + " on " + server);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to start nginx server.", e);
            }
        }
    }

    /*
     * Check if server started by looking for pidfile
     * @param String hostName
     * @return boolean
     */
    private boolean checkServerStarted(String hostName) 
		throws Exception {

        boolean val = false;
        // We will check for the existence of the pid file

        // Just to make sure we don't wait for ever.
        // We try to read the msg 20 times before we give up
        // Sleep 1 sec between each try. So wait for 20 secs
        int attempts = 20;
        while (attempts > 0) {
            if (RunContext.isFile(hostName, pidFile)) {
                val = true;
                break;
            } else {
                // Sleep for some time
                try {
                    Thread.sleep(1000);
                    attempts--;
                } catch (Exception e) {
                    break;
                }
            }
        }
        return (val);
    }

    /*
     * Return nginx pid
     * It reads the pid file from the remote server host and
     * returns the pid stored in it.
     * @param String hostName
     * @return int pid
     * @throws Exception
     */
    private static int getPid(String hostName) throws Exception {
        int pid;
		final String pfile = Utilities.convertPath(pidFile);

		if (! RunContext.isFile(hostName, pfile))
			return -1;

        pid = RunContext.exec(hostName, new RemoteCallable<Integer>() {
            public Integer call() throws Exception {
                String pidval;

                // FileInputStream is = new FileInputStream(Utilities.convertPath(pidFile));
                FileInputStream is = new FileInputStream(pfile);
                BufferedReader bufR = new BufferedReader(new InputStreamReader(is));
                pidval = bufR.readLine();
                bufR.close();
                return (Integer.parseInt(pidval));
            }
        });
        return (pid);
    }

    /**
     * Shuts down the Nginx web server.
     * @throws IOException Error executing the shutdown
     * @throws InterruptedException Interrupted waiting for the shutdown
     */
    @Stop
    public void shutdown() throws IOException, InterruptedException {
		if (skipService) return;
        int pid = -1;
        for (String hostName : myServers) {
            //Retrieve the pid value
            try {
                pid = getPid(hostName);
                logger.fine("Found nginx pidvalue of " + pid +
                        " on host " + hostName);
            } catch (Exception ee) {
                logger.log(Level.WARNING, "Failed to read nginx pidfile on " +
                        hostName + " with " + ee);
                logger.log(Level.FINE, "Exception", ee);
            }
            if (pid <= 0) {
                continue;
            }
            // Now kill the server
            Command cmd = new Command("kill " + pid);
            try {
                RunContext.exec(hostName, cmd);
                // Check if the server truly stopped
                if (!checkServerStopped(hostName))
                        logger.severe("Cannot kill nginx pid " + pid + " on " + hostName);
            } catch (Exception re) {
                logger.log(Level.WARNING, "Failed to stop Nginx server on " + hostName);
                logger.log(Level.FINE, "Exception", re);
            }
        }
    }

    /*
     * Check if server stopped by looking for pidfile
     * @param String hostName
     * @return boolean
     */
    private static boolean checkServerStopped(String hostName) 
		throws Exception {
        boolean val = false;
        // We will check for the existence of the pid file

        // Just to make sure we don't wait for ever.
        // We try to read the msg 20 times before we give up
        // Sleep 1 sec between each try. So wait for 20 secs
        int attempts = 20;
        while (attempts > 0) {
            if ( !RunContext.isFile(hostName, pidFile)) {
                val = true;
                break;
            } else {
                // Sleep for some time
                try {
                    Thread.sleep(1000);
                    attempts--;
                } catch (Exception e) {
                    break;
                }
            }
        }
        return (val);
    }

    /**
     * Clears the Nginx web server logs - the access and error logs.
     */
    @ClearLogs
    public void clearLogs() {
		if (skipService || skipLogs) return;
        for (int i = 0; i < myServers.length; i++) {
            if (RunContext.isFile(myServers[i], errlogFile)) {
                if (!RunContext.deleteFile(myServers[i], errlogFile)) {
                    logger.log(Level.WARNING, "Delete of " + errlogFile +
                            " failed on " + myServers[i]);
                }
            }
            if (RunContext.isFile(myServers[i], acclogFile)) {
                if (!RunContext.deleteFile(myServers[i], acclogFile)) {
                    logger.log(Level.WARNING, "Delete of " + acclogFile +
                            " failed on " + myServers[i]);
                }
            }

            logger.fine("Logs cleared for " + myServers[i]);
        }
    }

    /*
     * transfer log files
     * This method copies over the config file and error log to the run output directory
     * It will optionally copy over the access log if the user has
     * requested it
     */
    @GetLogs
    public void getLogs() {
		if (skipService || skipLogs) return;
        for (int i = 0; i < myServers.length; i++) {
            String outFile = RunContext.getOutDir() + "nginx_err.log." +
                    RunContext.getHostName(myServers[i]);
            /*
             * copy the error_log to the master
             * TODO: Truncate the error log to contain only entries for
             * this run. However, since by default we clearLogs before
             * the run, this is okay
             */
            if (!RunContext.getFile(myServers[i], errlogFile, outFile)) {
                logger.warning("Could not copy " + errlogFile + " to " + outFile);
            }
			if (getAccLog) {
			String accFile = RunContext.getOutDir() + "nginx_acc.log." +
				RunContext.getHostName(myServers[i]);
            if (!RunContext.getFile(myServers[i], acclogFile, accFile))
                logger.warning("Could not copy " + acclogFile + " to " + accFile);
            }

            // Copy the configuration file
            if (confFile != null) {
                outFile = RunContext.getOutDir() + "nginx_conf.log." +
                        RunContext.getHostName(myServers[i]);
                if (!RunContext.getFile(myServers[i], confFile, outFile)) {
                    logger.warning("Could not copy " + confFile + " to " + outFile);
                }
            }
            logger.fine("getLogs completed for " + myServers[i]);
        }

    }
}
