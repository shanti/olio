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
 *  $Id$
 */
package org.apache.olio.workload.fsloader;

import com.sun.faban.harness.util.FileHelper;
import org.apache.olio.workload.util.ScaleFactors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Formatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileLoader {

    /**
     * The default number of loader threads. Since the threads are blocking
     * for I/O, this number may be quite high.
     */
    public static final int DEFAULT_LOADER_THREADS = 128;

    /**
     * The frequency to report status, in number of files.
     */
    public static final int REPORT_FREQUENCY = 10000;

    private static final String DIR_PATTERN = File.separator + "%03d" + File.separator + "%03d" + File.separator;

    private static Logger logger = Logger.getLogger(FileLoader.class.getName());
    private static ExecutorService threadPool;
    private static ArrayList<FileChannel> openFiles = new ArrayList<FileChannel>();
    private static LoaderPool loaderPool;

    public static void main(String[] args) throws Exception {
        String srcDir = args[0];
        String destDir = args[1];
        ScaleFactors.setActiveUsers(Integer.parseInt(args[2]));

        int loaderThreads;
        if (args.length > 3)
            loaderThreads = Integer.parseInt(args[3]);
        else
            loaderThreads = DEFAULT_LOADER_THREADS;

        // Throttle the job producer so that the objects in the work
        // queue are limited to 10x the worker threads.
        loaderPool = new LoaderPool(loaderThreads * 10);

        srcDir += File.separator;

        // Clear the dest dir
        File dest = new File(destDir);
        if (!dest.isDirectory()) {
            logger.severe(destDir + " Not a directory!");
            System.exit(1);
        }

        logger.info("Deleting files in " + destDir);

        File f = new File(dest, "persons");
        if (f.exists())
            FileHelper.recursiveDelete(f);

        f = new File(dest, "personThumbs");
        if (f.exists())
            FileHelper.recursiveDelete(f);

        f = new File(dest, "events");
        if (f.exists())
            FileHelper.recursiveDelete(f);

        f = new File(dest, "eventThumbs");
        if (f.exists())
            FileHelper.recursiveDelete(f);

        f = new File(dest, "eventLits");
        if (f.exists())
            FileHelper.recursiveDelete(f);

        threadPool = Executors.newFixedThreadPool(loaderThreads);

        logger.info("Loading files to " + destDir);

        load(srcDir + "person.jpg", destDir + File.separator +
                "persons", "p%d.jpg", ScaleFactors.users);
        load(srcDir + "person_thumb.jpg", destDir + File.separator +
                "personThumbs", "p%dt.jpg", ScaleFactors.users);
        load(srcDir + "event.jpg", destDir + File.separator +
                "events", "e%d.jpg", ScaleFactors.events);
        load(srcDir + "event_thumb.jpg", destDir + File.separator +
                "eventThumbs", "e%dt.jpg", ScaleFactors.events);
        load(srcDir + "event.pdf", destDir + File.separator +
                "eventLits", "e%dl.pdf", ScaleFactors.events);

        threadPool.shutdown();

        boolean terminated = false;
        while (!terminated)
            try {
                terminated = threadPool.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
            }

        for (FileChannel channel : openFiles) {
            channel.close();
        }

        System.exit(0);
    }

    private static void load(String srcFile, String destDir,
                             String destPattern, int count)
            throws FileNotFoundException {
        Formatter format = new Formatter();
        FileChannel srcChannel = new FileInputStream(srcFile).getChannel();
        openFiles.add(srcChannel);
        for (int i = 1; i <= count; i++) {
            int dir1 = i % 1000;
            int dir2 = i / 1000000 % 1000;
            String dirName =
                    format.format(destDir + DIR_PATTERN, dir1, dir2).toString();
            File dir = new File(dirName);
            dir.mkdirs();
            String dest = format.format(destPattern, i).toString();
            if (i % REPORT_FREQUENCY == 0) {
                logger.info("Copying to " + dest);
            }
            ((StringBuilder) format.out()).setLength(0);
            threadPool.submit(loaderPool.getLoader(srcChannel, dest, i));
        }
    }


    static class Loader implements Runnable {

        FileChannel src;
        String dest;
        int fileNo;

        public void run() {
            try {
                FileChannel destChannel =
                        new FileOutputStream(dest).getChannel();
                src.transferTo(0, src.size(), destChannel);
                destChannel.close();
                if (fileNo % REPORT_FREQUENCY == 0)
                    logger.info("Copying to " + dest);

            } catch (IOException e) {
                logger.log(Level.WARNING, "Error writing file " + dest, e);
            } finally {
                loaderPool.putLoader(this);
            }
        }
    }

    static class LoaderPool {

        LinkedBlockingDeque<Loader> pool = new LinkedBlockingDeque<Loader>();
        int count = 0;
        int size;

        public LoaderPool(int size) {
            this.size = size;
        }

        public Loader getLoader(FileChannel src, String dest, int fileNo) {
            Loader loader = pool.poll();
            if (loader == null) {
                if (count < size) {
                    loader = new Loader();
                    ++count;
                } else {
                    for (;;) {
                    try {
                        loader = pool.take();
                        break;
                    } catch (InterruptedException ex) {
                        logger.log(Level.WARNING, "getLoader interrupted", ex);
                    }
                    }
                }
            }
            loader.src = src;
            loader.dest = dest;
            loader.fileNo = fileNo;
            return loader;
        }

        public void putLoader(Loader loader) {
            for (;;) {
                try {
                    // User a LIFO model to keep the hot objects in cache.
                    pool.putFirst(loader);
                    break;
                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, "putLoader interrupted!", ex);
                }
            }
        }
    }
}
