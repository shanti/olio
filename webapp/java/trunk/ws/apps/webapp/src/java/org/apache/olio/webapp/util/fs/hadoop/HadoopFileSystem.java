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


package org.apache.olio.webapp.util.fs.hadoop;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

 /**
 * Implementation of the Hadoop distributed file system that implements the FileSystem interface.
 * Even though Hadoop is a heirarchical file system, we implement a non-heirarchical
 * version here to be compatible with Mogile FS.
 *
 * @author Binu
 */

public class HadoopFileSystem implements org.apache.olio.webapp.util.fs.FileSystem {
    private Configuration config;
    public static String FS_KEY = "hadoopFS";
    public static String FS_NAME = "fs.default.name";
    private org.apache.hadoop.fs.FileSystem fs;
    
    /** Creates a new instance of HadoopFileSystem */
    public HadoopFileSystem(Properties props) throws IOException {
        config = new Configuration();
        String fsdefault = props.getProperty(FS_NAME);
        if (fsdefault != null)
            config.set(FS_NAME, fsdefault);
        fs = org.apache.hadoop.fs.FileSystem.get(config);
    }
    
    public Configuration getConfig() {
        return config;
    }

    public OutputStream create(String path) throws IOException {
        return create (path, 1, true);
    }

    public OutputStream create(String path, int replicationFactor) throws IOException {
        return create (path, replicationFactor, true);
    }

    public OutputStream create(String path, int replicationFactor, boolean overwrite) throws IOException {
        Path hPath = new Path (path);
        if (!overwrite) {
            if (fs.exists(hPath))
                throw new IOException ("path: " + path + " already exists");
        }
        return fs.create(hPath, (short)replicationFactor);
    }

    public boolean exists(String path) {
        try {
            return fs.exists(new Path(path));
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public InputStream open(String path) throws IOException {
        Path hPath = new Path (path);
        
        return fs.open(hPath);
    }

    public void delete(String path) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isLocal() {
        return false;
    }
    
}
