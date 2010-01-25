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

package org.apache.olio.webapp.util.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FileSystem interface that abstracts the file system interactions.
 * This provides support for multiple persistemce modes including local file systems
 * and distributed file systems likes Hadoop and Mogile FS.
 * This represents a non-heirarchical file system and the path value is specified as the 
 * 'key' to where the file is stored.
 *
 * @author Binu
 */
public interface FileSystem {
    /**
     * Create a file with the specified path. This overwrites the file if it exists. 
     * The default replication factor defined by the distributed file system is used.
     * @param path defines the key used to store the file.
     * @throws IOException if the system could not create the file.
     * @return OutputStream that can be used to write data
     **/
    public OutputStream create (String path) throws IOException;
    
    /**
     * Create a file with the specified path and replication factor. 
     * This overwrites the file if it exists.
     * @param path defines the key used to store the file.
     * @param replication defines the number of replicas to be created by the DFS.
     * @throws IOException if the system could not create the file.
     * @return OutputStream that can be used to write data
     **/
    public OutputStream create (String path, int replication) throws IOException;
    
    /**
     * Create a file with the specified path and replication factor. 
     * This overwrites the file if it exists.
     * @param path defines the key used to store the file.
     * @param replication defines the number of replicas to be created by the DFS.
     * @param overwrite defines whether the file can be overwritten. If set to true
     * the file will be overwritten.
     * @throws IOException if the system could not create the file.
     * @return OutputStream that can be used to write data.
     **/
    public OutputStream create (String path, int replication, boolean overwrite) throws IOException;
    
    /**
     * Check whether the file specified by the path exists. Returns true if it exists.
     * @param path defines the key used to store the file.
     * @return boolean returns true if the file exists, false otherwise.
     **/
    public boolean exists (String path);
    
    /**
     * Open a file for reading. 
     * @param path defines the key to the file.
     * @throws IOException if the system could not open the file.
     * @return InputStream that can be used to read data.
     **/
    public InputStream open (String path) throws IOException;
    
    /**
     * Delete a file.
     * @param path defines the key to the file.
     * @throws IOException if the system could not open the file.
     **/
    public void delete (String path) throws IOException;
    
    /**
     * Check whether the file system is local or distributed
     * @return true if local, false otherwise
     */
    public boolean isLocal();
}
