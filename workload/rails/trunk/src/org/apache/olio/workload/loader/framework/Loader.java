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
 * $Id: Loader.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package org.apache.olio.workload.loader.framework;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * The loader, one instance per Loadable type loaded, is responsible
 * for loading the data into the database in the most efficient manner.
 * We use queues and thread pools to ensure multiple threads are loading
 * concurrently in large batches.
 *
 * @author Akara Sucharitakul
 */
public class Loader {

    /** The batch size of a single batch. */
    public static final int BATCHSIZE = 1000;

    /** The recycling pool size is 3 times the size of the batch. */
    public static final int POOLSIZE = 3 * BATCHSIZE;

    /** The number of errors before exiting. */
    public static final int ERROR_THRESHOLD = 50;

    public static final int LOAD_THREADS = 5;

    private static Logger logger =
            Logger.getLogger(Loader.class.getName());

    private static AtomicInteger errorCount = new AtomicInteger();

    private static ConcurrentHashMap<String, Loader> typeMap =
                            new ConcurrentHashMap<String, Loader>();

    private static ConcurrentHashMap<Class, LoadablePool> poolMap =
                            new ConcurrentHashMap<Class, LoadablePool>();

    private static ArrayList<Thread> mainLoaders = new ArrayList<Thread>();

    // This is a single processing pool for processing data preps.
    private static ExecutorService processor =
                                Executors.newCachedThreadPool();

    private String name;
    AtomicInteger loadCount;

    LoadablePool<? extends Loadable> loadablePool;

    // A Loadable type database loading pool.
    ExecutorService pool;
    ConcurrentLinkedQueue<Loadable> queue;

    /**
     * Obtains the instance of the loader for a given loadable type.
     * @param clazz The loadable type
     * @return The loader for this type name, or a new loader if none exists
     */
    static Loader getInstance(Class<? extends Loadable> clazz) {
        // We may need to change this to a configurable thread pool size
        // on a per-type basis. This is the only place to change.

        String name = clazz.getName();
        Loader loader = new Loader();
        Loader oldEntry = typeMap.putIfAbsent(name, loader);

        if (oldEntry != null)
            loader = oldEntry;

        loader.validate(name);
        return loader;
    }

    private synchronized void validate(String name) {
        if (this.name == null)
            this.name = name;
        if (loadCount == null)
            loadCount = new AtomicInteger(0);
        if (queue == null)
            queue = new ConcurrentLinkedQueue<Loadable>();

        // We may need to change this to a configurable thread pool size
        // on a per-type basis. This is the only place to change.
        if (pool == null)
            pool = Executors.newFixedThreadPool(LOAD_THREADS);
            // pool = Executors.newCachedThreadPool();
    }

    private static <T extends Loadable> LoadablePool<T>
            getLoadablePool(Class<T> clazz) {
        LoadablePool<T> pool = new LoadablePool<T>(3 * BATCHSIZE, clazz);
        @SuppressWarnings("unchecked")
                LoadablePool<T> oldEntry = poolMap.putIfAbsent(clazz, pool);

        if (oldEntry != null) {
            pool = oldEntry;
        }

        return pool;
    }

    /**
     * Sets the URL for the connection to the database.
     * @param url The connection URL
     */
    public static void setConnectionURL(String url) {
        ThreadConnection.connectionURL = url;
    }

    public static void setJDBCDriverClassName(String driver)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        // Just load the DB driver class.
        Class.forName(driver).newInstance();
    }

    /**
     * Uses the loadable to clear the database through the loadable's
     * clear statement.
     * @param clazz The loadable class to use
     */
    public static void clear(Class<? extends Loadable> clazz) {
        Loadable loadable = null;
        try {
            loadable = clazz.newInstance();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error instantiating loader class.", ex);
            increaseErrorCount();
        }

        if (loadable != null) {
            final Loadable l = loadable;
        Future f = l.loader.pool.submit(new Runnable() {

            public void run() {
                ThreadConnection c = ThreadConnection.getInstance();
                try {
                    c.prepareStatement(l.getClearStatement());
                    c.executeUpdate();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, l.loader.name + ": " +
                            e.getMessage(), e);
                    increaseErrorCount();
                }
            }
        });
        while (!f.isDone() || f.isCancelled()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                    logger.log(Level.WARNING, l.loader.name + ": Interrupted " +
                            "while waiting to clear table.", e);
            }
        }
    }
    }

    /**
     * Loads the loadable into the database. Note that the loading is done
     * asynchronously and is divided into two phases: 1) The preparation
     * phase where all field values are generated and 2) Loading phase. These
     * may be performed by different threads. The waitProcessing method
     * will gracefully shut down the processing infrastructure and wait until
     * all preparation is done. Shutdown will wait until all data loading
     * is done.
     * @param clazz The loadable class
     * @param occurrences The number of load iterations
     */
    public static void load(Class<? extends Loadable> clazz, int occurrences) {

        final Class<? extends Loadable> c = clazz;
        final int occ = occurrences;
        Thread mainLoader = new Thread() {

            @Override
            public void run() {
                for (int i = 0; i < occ; i++) {
                    Loadable loadable = null;
                    try {
                        loadable = getLoadablePool(c).getLoadable();
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Error obtaining loadable", ex);
                        increaseErrorCount();
                    }
                    if (loadable != null) {
                        final Loadable l = loadable;
        processor.execute(new Runnable() {

            public void run() {
                try {
                    l.prepare();
                    l.loader.add(l);
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                    Loader.increaseErrorCount();
                }
            }
        });
    }
                }
            }
        };
        mainLoaders.add(mainLoader);
        mainLoader.start();
    }

    public static void exec(Runnable r) {
        processor.execute(r);
    }

    /**
     * Execute the post loads provided by the loadable.
     * @param clazz The loadable class
     */
    public static void postLoad(Class<? extends Loadable> clazz) {
        Loadable loadable = null;
        try {
            loadable = clazz.newInstance();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error instantiating loader class.", ex);
            increaseErrorCount();
        }

        if (loadable != null) {

            final Loadable l = loadable;
        l.loader.pool.submit(new Runnable() {

            public void run() {
                try {
                    l.postLoad();
                } catch (Exception e) {
                    logger.log(Level.WARNING, l.loader.name + ": " +
                                                    e.getMessage(), e);
                    increaseErrorCount();
                }
            }
        });
    }
    }


    private void add(Loadable l) {
        queue.add(l);
        int c = loadCount.incrementAndGet();
        if (c % BATCHSIZE == 0)
            flush(c);
    }

    private void flush(final int batchCount) {
        pool.submit(new Runnable() {
            public void run() {
                ThreadConnection c = ThreadConnection.getInstance();
                c.processBatch(name, batchCount, queue);
            }
        });
    }

    /**
     * Terminates the preparation infrastructure and waits until all data
     * preparation is done.
     */
    public static void waitProcessing() {
        // Wait for the main loaders
        for (Thread mainLoader : mainLoaders) {
            for (;;)
                try {
                    mainLoader.join();
                    break;
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
        }
        // We ensure the process pool is cleared, first.
        if (processor != null) {
        processor.shutdown();
        boolean terminated = false;
        while (!terminated)
            try {
                terminated = processor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
            }
        processor = null;
        }
    }

    /**
     * Terminates the preparation infrastructure (if still alive) and
     * then the loading infrastructure. Will return only after all the
     * loadables in the queue are loaded.
     */
    public static void shutdown() {
        waitProcessing();
        for (Loader entry : typeMap.values())
            entry.flush(0);
        for (Loader entry : typeMap.values())
            entry.pool.shutdown();
        for (Loader entry : typeMap.values()) {
            while (!entry.pool.isTerminated())
                try {
                    entry.pool.awaitTermination(1, TimeUnit.HOURS);
                } catch (InterruptedException e) {
                }
        }
        typeMap.clear();
        ThreadConnection.closeConnections();
    }

    /**
     * Increments the global error count. If the count is beyond the threshold,
     * the loader will terminate.
     */
    public static void increaseErrorCount() {
        if (errorCount.incrementAndGet() > ERROR_THRESHOLD)
            logger.severe("Error count exceeded threshold of " +
                    ERROR_THRESHOLD + "! Exiting.");
            System.err.println("Error count exceeded threshold of " +
                    ERROR_THRESHOLD + "! Exiting.");
            System.exit(2);
    }
}
