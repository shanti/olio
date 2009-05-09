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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author binujohn
 */
public class LocalFileSystem implements FileSystem {
    public static String FS_KEY = "localFS";
    
    /** Creates a new instance of LocalFileSystem */
    public LocalFileSystem() {
    }

    public OutputStream create(String path) throws IOException {
        return create (path, 0, true);
    }

    public OutputStream create(String path, int replication) throws IOException {
        return create (path, replication, true);
    }

    public OutputStream create(String path, int replication, boolean overwrite) throws IOException {
        // The replication factor is ignored for LocalFileSystem
        if (!overwrite) {
            File file = new File(path);
            if (file.exists())
                throw new IOException ("File exists - " + path);
        }
        
        return new FileOutputStream (path);
    }

    public boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public InputStream open(String path) throws IOException {
        /* Will not check for the existence of the file. */
        
        return new FileInputStream (path);
    }

    public void delete(String path) throws IOException {
        File file = new File(path);
        if (file.exists())
            file.delete();
    }

    public boolean isLocal() {
        return true;
    }
    
}
