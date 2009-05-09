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

package org.apache.olio.webapp.util;

import org.apache.olio.webapp.util.fs.FileSystem;
import org.apache.olio.webapp.util.fs.hadoop.HadoopFileSystem;
import org.apache.olio.webapp.util.fs.local.LocalFileSystem;
import java.io.IOException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.Context;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is an implementation of the Service Locator pattern. It is
 * used to looukup resources.
 * This implementation uses the "singleton" strategy and also the "caching"
 * strategy.
 * @author Akara Sucharitakul
 * @author Binu John
 */
public class ServiceLocator {
    
    private Context ctx;

    //used to hold references to Resources for re-use
    private ConcurrentHashMap<String, Object> cache =
            new ConcurrentHashMap<String, Object>();
    
    private static ServiceLocator instance = new ServiceLocator();
    
    private String fileSystemName = System.getProperty ("filesystem.name");
        
    public static ServiceLocator getInstance() {
        return instance;
    }
    
    private ServiceLocator() {
        try {
            InitialContext ic = new InitialContext();
            ctx = (Context) ic.lookup("java:comp/env");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //we can add more methods if we need to lookup other resource types in environment
    
    /**
     * @return the String value corresponding
     * to the env entry name, such as a class name for a factory to bind to
     */
    public String getString(String envName) {
        String strValue = (String) cache.get(envName);
        if (strValue == null) {
            // Check system property first. This will allow easy customizations
            String retVal = System.getProperty(envName);
            if (retVal != null) {
                cache.put(envName, retVal);
                return retVal;
            }
            
            if (ctx != null) {
                try {
                    Object o = ctx.lookup(envName);
                    if (o != null) {
                        strValue = o.toString();
                        cache.put(envName, strValue);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }   
        return strValue;
    }

    /**
     * Obtains a string but if not found, returns provided default value.
     * @param envName
     * @param defaultValue
     * @return
     */
    public String getString(String envName, String defaultValue) {
        String strValue = (String) cache.get(envName);
        if (strValue == null) {
            // Check system property first. This will allow easy customizations
            String retVal = System.getProperty(envName);
            if (retVal != null) {
                cache.put(envName, retVal);
                return retVal;
            }
            if (ctx != null) {
                try {
                    Object o = ctx.lookup(envName);
                    if (o != null)
                        strValue = o.toString();
                } catch (NameNotFoundException e) {
                    strValue = defaultValue;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (strValue == null)
            strValue = defaultValue;
        cache.put(envName, strValue);
        return strValue;
    }
    
    // Added for Distributed FS support
    // TO DO - Use dynamic loading.
    public FileSystem getFileSystem () throws IOException {
        String cacheKey = "filesystem";
        
        FileSystem fs = (FileSystem) cache.get(cacheKey);
        
        if (fs != null)
            return fs;
        
        synchronized (this) {
            fs = (FileSystem) cache.get(cacheKey);
        
            if (fs == null) {
                if (fileSystemName == null || fileSystemName.equals("local")) {
                    fs = new LocalFileSystem();
                    cache.put(cacheKey, fs);
                }
                else if (fileSystemName.equalsIgnoreCase("hadoop")) {
                    Properties props = new Properties();
                    props.put(HadoopFileSystem.FS_NAME, System.getProperty (HadoopFileSystem.FS_NAME));
                    fs = new HadoopFileSystem (props);
                    cache.put(cacheKey, fs);
                }
                else {
                    throw new IOException ("FileSystem - " + fileSystemName + " is not supported");
                }
            }
            
            return fs;
        }
    }
}
    
