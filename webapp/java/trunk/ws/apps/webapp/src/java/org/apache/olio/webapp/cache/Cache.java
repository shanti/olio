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
package org.apache.olio.webapp.cache;

/**
 * The cache interface provides all operations necessary for the cache.
 * We could have extended java.util.Map but that would make a lot of
 * unnecessary work for the scope of this project. We can always implement that
 * interface later if desired.
 */
public interface Cache {

    /**
     * Gets the cached value based on a key.
     * @param key The key
     * @return The cached object, or null if none is available
     */
    Object get(String key);

    /**
     * Sets a cached item using a key.
     * @param key The key
     * @param value The object to cache.
     */
    void put(String key, Object value);
    
    /**
     * Sets a cached item using a key.
     * @param key The key
     * @param value The object to cache.
     * @param timeToLive Time to cache this object in seconds
     */
    void put(String key, Object value, long timeToLive);

    /**
     * Invalidates a cached item using a key
     * @param key
     * @return success
     */
    boolean invalidate(String key);

    /*
     * Check if cache needs refresh based on existence cached object and of Semaphore
     * @param key The key
     * @param cacheObjPresent false if the cache object for this key exists
     * @return true if the cache object needs a refresh
     */
    boolean needsRefresh (boolean cacheObjPresent, String key);
    
    void doneRefresh (String key, long timeToNextRefresh) throws CacheException;
    
    boolean isLocal();
}
