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
import com.sun.faban.common.NameValuePair;
import com.sun.faban.harness.*;

import com.sun.faban.harness.services.ServiceContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

/**
 * This class implements the service to start/stop Thin instances.
 * It also provides functionality to transfer the portion of the thin
 * error logs for a run to the run output directory.
 * The user can specify the number of thins to start. It will use
 * port numbers starting from the user-specified value for the instances.
 * Note that the same starting port# must be specified for ALL hosts
 * We just pick the first host's port (if not specified, defaults to 3000).
 * It can be used by any benchmark to manage thin servers and
 * perform these operations remotely using this Service.
 *
 * @author Shanti Subramanyam
 * Based on LighttpdService provided in faban sample services.
 * 
 */
public class Thin {
    private ServiceContext ctx;
    private Logger logger = Logger.getLogger(Thin.class.getName());

    private static final int PORT = 3000;
    private final String logName = "thin.log";
    private final String pidName = "thin.pid";
    private String logFileNames[];
    private static String pidFileNames[];
   
    private String[] myServers;
	private List<NameValuePair<Integer>> hostPorts;
    private String thinCmd, appDir, logsDir, pidsDir;
    private static int numInstances, startPort;
    CommandHandle thinHandles[];
    private boolean skipLogs = false;

    public Thin(ServiceContext ctx) throws ConfigurationException {
		this.ctx = ctx;
		String pidFile, errlogFile;

        myServers = ctx.getUniqueHosts();
        hostPorts = ctx.getUniqueHostPorts();
		startPort = hostPorts.get(0).value;
		if (startPort == 0)
			startPort = PORT;
        thinCmd = ctx.getProperty("cmdPath");
        if (thinCmd != null && thinCmd.trim().length() > 0) {
            if (!thinCmd.endsWith(" "))
                thinCmd = thinCmd + " ";
        } else {
            throw new ConfigurationException("cmdPath not set. Cannot start/stop thin");
        }
        pidsDir = ctx.getProperty("pidsDir");
        if (pidsDir != null && pidsDir.trim().length() > 0) {
            if (!pidsDir.endsWith(File.separator))
                pidsDir = pidsDir + File.separator;
        } else {
            throw new ConfigurationException("pidsDir not set. Cannot start/stop thin");
        }

        String ni = ctx.getProperty("numInstances");
        if (ni != null)
            numInstances = Integer.parseInt(ni);
		if (numInstances == 0)
			numInstances = 1;
		appDir = ctx.getProperty("appDir");
		if (appDir == null || appDir.trim().length() <= 0) {
			logger.warning("appDir property not set. Assuming /export/home/oliorails");
			appDir = "/export/home/oliorails";
        }
        logsDir = ctx.getProperty("logsDir");
        if (logsDir != null && logsDir.trim().length() > 0) {
            if (!logsDir.endsWith(File.separator))
                logsDir = logsDir + File.separator;
        } else {
            skipLogs = true;
        }

        pidFile = pidsDir + pidName;
		errlogFile = logsDir + logName;

        //Create all the pid and log filenames
        logFileNames = new String[numInstances];
        pidFileNames = new String[numInstances];
        for (int i = 0; i < numInstances; i++) {
            logFileNames[i] = logsDir + "thin." + (startPort + i) +  ".log";
            pidFileNames[i] = pidsDir + "thin." + (startPort + i) + ".pid";
        }

        thinCmd = thinCmd + "-p " + startPort + " -d -e production -s " + numInstances +
                " --log " + errlogFile + " --pid " + pidFile + " start";
        thinHandles = new CommandHandle[myServers.length];
    }

    /**
     * Starts up the Thin server.
     */
    public void startup() {
        Command startCmd = new Command(thinCmd);
		startCmd.setWorkingDirectory(appDir);
        startCmd.setStreamHandling(Command.STDOUT, Command.CAPTURE);

        for (int i = 0; i < myServers.length; i++) {
            String server = myServers[i];
            try {
                // Run the command in the foreground and wait for the start
                thinHandles[i] = RunContext.exec(server, startCmd);

                if (checkServerStarted(server)) {
                    logger.fine("Completed thin startup successfully on " +
                            server);
                } else {
                    logger.severe("Failed to find " + pidName + " on " + server);
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to start thin server.", e);
            }
        }
    }

    /*
     * Check if server started by looking for pidfiles
     * @param String hostName
     * @return boolean
     */
    private static boolean checkServerStarted(String hostName) throws Exception {
        boolean val = false;
        // We will check pid files for 1st and last instances;

        // Just to make sure we don't wait for ever.
        // We try to read the msg 20 times before we give up
        // Sleep 1 sec between each try. So wait for 20 secs
        int attempts = 20;
        while (attempts > 0) {
            if (RunContext.isFile(hostName, pidFileNames[0]) &&
                    RunContext.isFile(hostName, pidFileNames[numInstances - 1])) {
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
     * Return thin pids
     * It reads the pid file from the remote server host for the
     * specified instance and returns the pid stored in it.
     * @param String hostName
	 * @param int num - the instance# of the thin server
     * @return int pid if pidfile exists, otherwise -1
     * @throws Exception
     */
    private static int getPid(String hostName, int num) throws Exception {
        final String host = hostName;
        final String file = Utilities.convertPath(pidFileNames[num]);
        int pid;

        if ( !RunContext.isFile(host, file)) {
			return -1;
        }
        pid = RunContext.exec(host, new RemoteCallable<Integer>() {

            public Integer call() throws Exception {
                String pidval;

                FileInputStream is = new FileInputStream(file);
                BufferedReader bufR = new BufferedReader(new InputStreamReader(is));
                pidval = bufR.readLine();
                bufR.close();
                return (Integer.parseInt(pidval));
            }
        });
        return (pid);
    }

    /**
     * Shuts down the Thin web server.
     * @throws IOException Error executing the shutdown
     * @throws InterruptedException Interrupted waiting for the shutdown
     */
    public void shutdown() throws IOException, InterruptedException {
        for (String hostName : myServers) {
            //Retrieve the pid values
            for (int i = 0; i < numInstances; i++) {
                int pid = 0;
                try {
                    pid = getPid(hostName, i);
                } catch (Exception ee) {
                    logger.log(Level.WARNING, "Failed to read thin pidfile on " +
                            hostName + " with " + ee);
                    logger.log(Level.FINE, "Exception", ee);
                }
                // Now kill the server
                if (pid <= 0) {
                    continue;
                }
                Command cmd = new Command("kill " + pid);
				logger.fine("Now killing " + pid + " on " + hostName);
                try {
                    RunContext.exec(hostName, cmd);
                } catch (RemoteException re) {
                    logger.log(Level.WARNING, "Failed to stop Thin server(s) on " +
                            hostName);
                    logger.log(Level.FINE, "Exception: ", re);
                }
            }
        }
    }

    /**
     * Clears the Thin server logs 
     */
    public void clearLogs() {
        if (skipLogs) return;
        for (String server : myServers) {
            // Clear thin logs
            for (int i = 0; i < logFileNames.length; i++) {
                if (RunContext.isFile(server, logFileNames[i])) {
                    if (!RunContext.deleteFile(server, logFileNames[i])) {
                        logger.log(Level.WARNING, "Delete of " + logFileNames[i] +
                                " failed on " + server);
                    }
                }
            }
        }
    }

    /*
     * transfer log files
     */
    public void getLogs() {
    }
}
