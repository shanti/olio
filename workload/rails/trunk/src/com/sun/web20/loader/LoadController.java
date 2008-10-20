package com.sun.web20.loader;

import com.sun.web20.util.ScaleFactors;

import java.util.logging.Logger;

import static com.sun.web20.loader.framework.Loader.*;

public class LoadController {

    static Logger logger = Logger.getLogger(LoadController.class.getName());

    public static void main(String[] args) throws Exception {
        setJDBCDriverClassName(args[0]);
        setConnectionURL(args[1]);
        ScaleFactors.setActiveUsers(Integer.parseInt(args[2]));

        // Clear the database
        logger.info("Clearing database tables.");
        clear(new Person(0, 0));
        clear(new Friends(0));
        clear(new Address());
        clear(new Tag(0));
        clear(new SocialEvent(0));
        clear(new EventTag(0));
        clear(new Attendees(0));
        clear(new Comments(0));
        clear(new Documents(0));
        clear(new Images(0, ""));

        // load person, friends, and addresses
        logger.info("Creating persons, friends, and addresses.");
        for (int i = 0; i < ScaleFactors.users; i++) {
            int imageId = ScaleFactors.events + i + 1;
            load(new Person(i, imageId));
            load(new Friends(i));
            load(new Address());
            load(new Images(imageId, "p"));
        }

        // load tags
        logger.info("Creating tags.");
        for (int i = 0; i < ScaleFactors.tagCount; i++) {
            load(new Tag(i));
        }

        // load events and all relationships to events
        logger.info("Creating events, attendees, comments.");
        for (int i = 0; i < ScaleFactors.events; i++) {
            load(new SocialEvent(i));
            load(new Documents(i));
            load(new Images(i, "e"));
            load(new EventTag(i));
            load(new Attendees(i));
            load(new Comments(i));
        }

        waitProcessing();
        logger.info("Done data creation.");

        // Now we need to check that all loading is done.
        shutdown();
        logger.info("Done data loading.");

        // We use a new set of connections and thread pools for postLoad.
        // This is to ensure all load tasks are done before this one starts.
        postLoad(new Tag(0));
        shutdown();
        logger.info("Done post-load.");
        System.exit(0); // Signal successful loading.
    }
}
