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

package org.apache.olio.workload.fsloader;

import org.apache.olio.workload.util.ScaleFactors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Formatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileLoader {

    private static Logger logger = Logger.getLogger(FileLoader.class.getName());

    public static void main(String[] args) throws Exception {
        String srcDir = args[0];
        String destDir = args[1];
        ScaleFactors.setActiveUsers(Integer.parseInt(args[2]));
        srcDir += File.separator;

        // Clear the dest dir
        File dest = new File(destDir);
        if (!dest.isDirectory()) {
            logger.severe(destDir + " Not a directory!");
            System.exit(1);
        }

        logger.info("Deleting files in " + destDir);
        File[] list = dest.listFiles();
        for (File f : list) {
            String name = f.getName();
            boolean delete = false;
            if (name.endsWith(".jpg"))
                delete = true;
            else if (name.endsWith(".JPG"))
                delete = true;
            else if (name.endsWith(".pdf"))
                delete = true;
            else if (name.endsWith(".PDF"))
                delete = true;
            if (delete && !f.delete())
                logger.warning("Error deleting file " + f.getName());
        }

        ArrayList<LoaderThread> loaders = new ArrayList<LoaderThread>();

        loaders.add(new LoaderThread(srcDir + "person.jpg",
                destDir + File.separator + "p%d.jpg", ScaleFactors.users));
        loaders.add(new LoaderThread(srcDir + "person_thumb.jpg",
                destDir + File.separator + "p%dt.jpg", ScaleFactors.users));
        loaders.add(new LoaderThread(srcDir + "event.jpg",
                destDir + File.separator + "e%d.jpg", ScaleFactors.events));
        loaders.add(new LoaderThread(srcDir + "event_thumb.jpg",
                destDir + File.separator + "e%dt.jpg", ScaleFactors.events));
        loaders.add(new LoaderThread(srcDir + "event.pdf",
                destDir + File.separator + "e%d.pdf", ScaleFactors.events));

        for (LoaderThread loader : loaders) {
            loader.join();
        }

        for (LoaderThread loader : loaders) {
            loader.close();
        }

        System.exit(0);
/*
        FileChannel img = new FileInputStream(
                                    srcDir + "person.jpg").getChannel();
        FileChannel thumb = new FileInputStream(
                                    srcDir + "person_thumb.jpg").getChannel();
        long imgSize = img.size();
        long thumbSize = thumb.size();

        logger.info("Loading user images...");
        for (int i = 1; i <= ScaleFactors.users; i++) {
            logger.finer("Loading files for user " + i);
            copyTo(img, imgSize, destDir + File.separator + "p" + i + ".jpg");
            copyTo(thumb, thumbSize,
                    destDir + File.separator + "p" + i + "t.jpg");
        }

        img.close();
        thumb.close();

        logger.info("Loading event images and files...");
        img = new FileInputStream(srcDir + "event.jpg").getChannel();
        thumb = new FileInputStream(srcDir + "event_thumb.jpg").getChannel();
        FileChannel lit = new FileInputStream(
                                    srcDir + "event.pdf").getChannel();

        imgSize = img.size();
        thumbSize = thumb.size();
        long litSize = lit.size();

        for (int i = 1; i <= ScaleFactors.events; i++) {
            logger.finer("Loading files for event " + i);
            copyTo(img, imgSize, destDir + File.separator + "e" + i + ".jpg");
            copyTo(thumb, thumbSize,
                    destDir + File.separator + "e" + i + "t.jpg");
            copyTo(lit, litSize, destDir + File.separator + "e" + i + ".pdf");
        }

        img.close();
        thumb.close();
        lit.close();
        System.exit(0);
*/
    }

    /*
    private static void copyTo(FileChannel src, long size, String destFile)
            throws IOException {
        FileChannel dest = (new FileOutputStream(destFile)).getChannel();
        src.transferTo(0, size, dest);
        dest.close();
    }
    */

    static class LoaderThread extends Thread {

        FileChannel src;
        int count;
        long size;
        String pattern;
        Formatter format;

        public LoaderThread(String src, String destPattern, int count)
                throws IOException {
            this.src = new FileInputStream(src).getChannel();
            size = this.src.size();
            this.count = count;
            this.pattern = destPattern;
            format = new Formatter();
            start();
        }

        public void run() {
            for (int i = 1; i <= count; i++) {
                String dest = format.format(pattern, i).toString();
                if (i % 1000 == 0) {
                    logger.info("Copying to " + dest);
                }
                ((StringBuilder) format.out()).setLength(0);
                try {
                    FileChannel destChannel = new FileOutputStream(dest).
                                                            getChannel();
                    src.transferTo(0, size, destChannel);
                    destChannel.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error writing file " + dest, e);
                }
            }
        }

        public void close() throws IOException {
            src.close();
        }
    }
}
