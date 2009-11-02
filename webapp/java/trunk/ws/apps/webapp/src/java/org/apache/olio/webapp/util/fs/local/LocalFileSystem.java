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

package org.apache.olio.webapp.util.fs.local;

import org.apache.olio.webapp.util.fs.FileSystem;
//import org.apache.olio.webapp.filestore.LocalFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 *
 * @author binujohn
 */
public class LocalFileSystem implements FileSystem {
    public static String FS_KEY = "localFS";
    
    private static Logger logger = Logger.getLogger(LocalFileSystem.class.getName());
    private String baseLocation;

    // Indices into the attributes array.
    private static final int ID = 0;
    private static final int TYPE = 1;

    /** Creates a new instance of LocalFileSystem */
    public LocalFileSystem() {
        //baseLocation = System.getProperty("olio.localfs.basedir", "/filestore");
        baseLocation = System.getProperty("webapp.image.directory", "/filestore");
        logger.finest("baseLocation = " + baseLocation);
    }

        private String getType(char t) {
        String type = null;
        switch (t) {
            case 'P' :
            case 'p' : type = "person"; break;
            case 'E' :
            case 'e' : type = "event";  break;
        }
        return type;
    }

    private String[] mapAttributes(String fileName) {
        // Do pattern matching and splitting.
        char prefix = fileName.charAt(0);
        String[] attrs = new String[2];
        attrs[TYPE] = getType(prefix);
        int dotIdx = fileName.lastIndexOf('.');
        char postfix = fileName.charAt(dotIdx - 1);
        if (Character.isDigit(postfix)) {
            attrs[ID] = fileName.substring(1, dotIdx);
            attrs[TYPE] += 's';
        } else if (postfix == 't' || postfix == 'T') {
            attrs[ID] = fileName.substring(1, dotIdx - 1);
            attrs[TYPE] += "Thumbs";
        } else if (postfix == 'l' || postfix == 'L') {
            attrs[ID] = fileName.substring(1, dotIdx - 2);
            attrs[TYPE] += "Lits";
        } else {
            logger.warning("Invalid file name pattern: " + fileName);
            attrs[TYPE] = null;
        }
        return attrs;
    }

    private String getFullPath(String fileName, String[] attrs) {
        if (attrs == null) {
            attrs = mapAttributes(fileName);
        }

        if (attrs[TYPE] == null) {
		logger.warning("Cannot process file : " + fileName);
		return(null);
        }
        //build path
        long id = Long.parseLong(attrs[ID]);
        String dirPrimaryPath = String.format("%03d", id % 1000l);
        String dirSecondaryPath = String.format("%03d", id / 1000000l % 1000l);

            StringBuilder path = new StringBuilder();
            path.append(baseLocation).append('/').append(attrs[TYPE]);
            path.append('/').append(dirPrimaryPath).append('/');
            path.append(dirSecondaryPath).append('/').append(fileName);
        logger.finest("getFullPath returning " + path.toString());
            return path.toString();
    }

    /**
     * Creates a file.
     *
     * @param name        The file name
     * @return An output stream to write the content of the file
     * @throws java.io.IOException Error creating the file
     */
    public OutputStream create(String name) throws IOException {
        return create (name, 0, true);
    }

     /**
     * Creates a file.
     *
     * @param name        The file name
     * @param replication The number of copies of the file to keep around
     * @return An output stream to write the content of the file
     * @throws java.io.IOException Error creating the file
     */   public OutputStream create(String name, int replication) throws IOException {
        return create (name, replication, true);
    }

    /**
     * Creates a file.
     *
     * @param name        The file name
     * @param replication The number of copies of the file to keep around
     * @param overwrite   Overwrite the file if already exists?
     * @return An output stream to write the content of the file
     * @throws java.io.IOException Error creating the file
     */
    public OutputStream create(String name, int replication, boolean overwrite) throws IOException {
        File fileToCreate = new File(name);
        String fileName = fileToCreate.getName();
        String[] attrs = mapAttributes(fileName);
        String destPath = getFullPath(name, attrs);

        if (destPath != null) {
            File dest = new File(destPath);
            if (overwrite || !dest.exists()) {
                File destDir = dest.getParentFile();
                if (!destDir.isDirectory())
                    if (!destDir.mkdirs()) {
                        logger.warning("Cannot create directory " +
                                        destDir.toString());
                        return null;
                    }

                FileOutputStream out = new FileOutputStream(dest);
                return out;
            }
        }
        return null;
    }

    /**
     * Checks whether a file with the name already exists.
     *
     * @param name The name of the file to check
     * @return Whether the file in question does indeed exist
     */
    public boolean exists(String name) {
        String fullPath = getFullPath(name, null);
        if (fullPath != null) {
            File f = new File(fullPath);
            return f.isFile();
        }
        return false;
    }

    /**
     * Opens the file for reading. Note, we cannot change an existing file
     * with this file architecture. To change, you need to create a new file.
     *
     * @param name The file name
     * @return An input stream allowing reading the content of the file
     * @throws java.io.IOException Error opening the file
     */
    public InputStream open(String name) throws IOException {
        logger.finest("Opening file " + name);
        String fullPath = getFullPath(name, null);
        if (fullPath != null) {
            File f = new File(fullPath);
            if (f.isFile())
                return new FileInputStream(f);
        }
        return null;
    }

    /**
     * Deletes a file.
     *
     * @param name The name of the file to delete
     * @throws java.io.IOException Error deleting the file
     */
    public void delete(String name) throws IOException {
        String fullPath = getFullPath(name, null);
        if (fullPath != null) {
            File f = new File(fullPath);
            f.delete();
        }
    }

    /**
     * Checks whether this file system is local.
     * @return Always true for the LocalFileSystem
     */
    public boolean isLocal() {
        return true;
    }
    
}
