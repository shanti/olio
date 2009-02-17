package org.apache.olio.workload.loader.framework;

import com.sun.faban.driver.util.Random;

import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class ThreadResource {

    Logger logger = Logger.getLogger(ThreadResource.class.getName());

    private static ThreadLocal<ThreadResource> resource =
            new ThreadLocal<ThreadResource>() {
        public ThreadResource initialValue() {
            return new ThreadResource();
        }
    };

    Random random;
    StringBuilder buffer;
    SimpleDateFormat dateFormat;

    private ThreadResource() {
        buffer = new StringBuilder(256);
        random = new Random();
    }

    public StringBuilder getBuffer() {
        buffer.setLength(0); // Make sure we clear it
        return buffer;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * DateFormat is not thread safe. We need to include it into the
     * ThreadResource.
     * @return The thread instance of DateFormat.
     */
    public DateFormat getDateFormat() {
        if (dateFormat == null)
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat;
    }

    public static ThreadResource getInstance() {
        return resource.get();
    }
}
