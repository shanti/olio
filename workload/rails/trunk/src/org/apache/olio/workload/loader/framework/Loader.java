package org.apache.olio.workload.loader.framework;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    /** The number of errors before exiting. */
    public static final int ERROR_THRESHOLD = 100;

    public static final int LOAD_THREADS = 5;

    private static Logger logger =
            Logger.getLogger(Loader.class.getName());

    private static AtomicInteger errorCount = new AtomicInteger();

    private static ConcurrentHashMap<String, Loader> typeMap =
                            new ConcurrentHashMap<String, Loader>();

    // This is a single processing pool for processing data preps.
    private static ExecutorService processor =
                                Executors.newCachedThreadPool();

    private String name;
    AtomicInteger loadCount;

    // A Loadable type database loading pool.
    ExecutorService pool;
    ConcurrentLinkedQueue<Loadable> queue;

    /**
     * Obtains the instance of the loader for a given loadable type.
     * @param name The loadable type name
     * @return The loader for this type name, or a new loader if none exists
     */
    static Loader getInstance(String name) {
        // We may need to change this to a configurable thread pool size
        // on a per-type basis. This is the only place to change.

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
     * @param l The loadable to use
     */
    public static void clear(final Loadable l) {
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
                logger.log(Level.WARNING, l.loader.name + ": Interrupted while " +
                                                "waiting to clear table.", e);
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
     * @param l
     */
    public static void load(final Loadable l) {
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

    /**
     * Execute the post loads provided by the loadable.
     * @param l The loadable.
     */
    public static void postLoad(final Loadable l) {
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
