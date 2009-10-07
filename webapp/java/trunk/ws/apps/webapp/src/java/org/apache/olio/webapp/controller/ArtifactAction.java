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
 */
package org.apache.olio.webapp.controller;

import org.apache.olio.webapp.util.fs.FileSystem;
import org.apache.olio.webapp.util.ServiceLocator;
import org.apache.olio.webapp.util.WebappUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action class serves up images. The care is taken to serve images that are user-generated
 * @author Mark Basler
 * @author Inderjeet Singh
 * @author Binu John
 */
public class ArtifactAction implements Action {

    public static final String GIF_SUFFIX = ".gif";
    public static final String JPG_SUFFIX = ".jpg";
    public static final String PNG_SUFFIX = ".png";
    public static final boolean bDebug = false;
    private ServletContext context;
    private static int BUFFER_SIZE = Integer.parseInt(System.getProperty("webapp.artifactBufferSize", "8192"));
    private Logger logger = Logger.getLogger(ArtifactAction.class.getName());
    // Use a ThreadLocalBuffer rather than recreating buffer every time
    private static ThreadLocal<byte[]> tlBuf = new ThreadLocal<byte[]>() {

        protected synchronized byte[] initialValue() {
            return new byte[BUFFER_SIZE];
        }
    };

    public ArtifactAction(ServletContext context) {
        this.context = context;
    }

    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            logger.warning("Accessing artifact is NULL");
        } else {
            logger.finer("pathInfo = " + pathInfo);
        }

        // set proper contentType
        String mimeType = context.getMimeType(pathInfo);
        response.setContentType(mimeType);

        // Modified for DFS support
        FileSystem fs = ServiceLocator.getInstance().getFileSystem();

        // look for file in default location such as WEB-INF
        String imagePath = WebappUtil.getArtifactLocalionDir() + pathInfo;
        logger.finer("Image path = " + imagePath);
        File imageFile = new File(imagePath);
        /* Assume image exists -- This was done to reduce FileSystem interaction
        if(!fs.exists(imagePath)) {
        System.out.println ("Could not find file - " + imagePath);
        // not in default location, look in upload location
        imageFile=new File(context.getRealPath("artifacts/" + pathInfo));
        if (!fs.exists(imageFile.getCanonicalPath())) {
        WebappUtil.getLogger().log(Level.SEVERE, "image_does_not_exist", imageFile.getCanonicalPath());
        return null;
        }
        }
         ** End mod */

        FileInputStream fis = null;
        FileChannel in = null;
        WritableByteChannel out = null;

        // serve up image from proper location
        // Use local file system if the image is in default location.
        logger.finer("AcessArtifact -- imagePath = " + imagePath);
        InputStream is = null;
        try {
            is = fs.open(imagePath);
        } catch (FileNotFoundException nfe) {
            logger.severe("File not found - imagePath = " + imagePath);
        } catch (IOException ioe) {
            logger.severe("IOException - imagePath = " + imagePath);
        }
        if (is == null) {
            logger.severe("Could not open image: " + imagePath);
        } else {
            if (is instanceof FileInputStream) {
                logger.finer("Streaming from file --");
                try {
                    // NIO transferTo takes the slow route since we are passing along a wrapped
                    // buffer rather than the Socket output stream. 
                    // From experiments we did, it looks like 
                    // Using traditional I/O is more efficient than using NIO in our case - for
                    // small files.
                    /*
                    fis = (FileInputStream) (is);
                    in = fis.getChannel();
                    out = Channels.newChannel(response.getOutputStream());
                    in.transferTo(0, in.size(), out);
                     * */
                    byte[] buf = tlBuf.get();
                    int count;
                    while ((count = is.read(buf)) != -1) {
                        response.getOutputStream().write(buf, 0, count);
                    }
                } finally {
                    WebappUtil.closeIgnoringException(in);
                    WebappUtil.closeIgnoringException(fis);
                    WebappUtil.closeIgnoringException(out);
                }
            } else {
                if (bDebug) {
                    System.out.println("Not a FileInputStream");
                }
                InputStream iStream = null;
                try {
                    OutputStream oStream = response.getOutputStream();
                    iStream = fs.open(imageFile.getCanonicalPath());
                    // With the current implementation, we only support FS, so this is a plcae holder.
                    // TODO - Optimize this (if required) when DFS is supported
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = iStream.read(buffer)) != -1) {
                        oStream.write(buffer, 0, len);
                    }
                } finally {
                    WebappUtil.closeIgnoringException(iStream);
                }
            }
        }

        return null;
    }
}
