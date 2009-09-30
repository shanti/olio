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

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This implementation uses a simple ConcurrentHashMap to hold cached items.
 * It should perform best if the application runs on a single JVM.
 * Since this cache resides on and is available to only one JVM, it's
 * effectiveness decreases as we scale up the number of nodes.
 */
public class SimpleMapCacheFactory extends CacheFactory {

    private int cacheExpireInSeconds = 120; // in seconds

    private static final ConcurrentHashMap<String,SoftReference<Object>> CACHE =
                                    new ConcurrentHashMap<String, SoftReference<Object>>();

    /**
     * Obtains the cache instance.
     * @return The cache instance
     * @param type
     */
    @Override
    public Cache createCache(String type) {
        return new MapCache(type);
    }

    @Override
    public int getCacheExpireInSeconds() {
        return cacheExpireInSeconds;
    }

    static class MapCache implements Cache {

        private String prefix;
        private long expiryTime;

        private MapCache(String type) {
            prefix = type + ':';
            expiryTime = -1;
        }

        public Object get(String key) {
            if (expiryTime > 0 && System.currentTimeMillis() > expiryTime) {
                String lkey = prefix + key;
                if (CACHE.get(lkey) != null)
                    CACHE.remove(lkey);
                return null;
            }
            Object value = null;
            SoftReference<Object> sr = CACHE.get(prefix + key);
            if (sr != null)
                value = sr.get();
            
            if (value != null)
                logHit(prefix, key);
            return value;
        }

        public void put(String key, Object value) {
            if (value != null) {
                CACHE.put(prefix + key, new SoftReference(value));
            }
            else
                CACHE.remove(prefix + key); 
            expiryTime = -1;
        }

        public void put(String key, Object value, long timeToLive) {
            if (value != null) {
                expiryTime = System.currentTimeMillis() + timeToLive*1000;
                CACHE.put(prefix + key, new SoftReference(value));
            }
            else {
                CACHE.remove(prefix + key);
                expiryTime = -1;
            }
        }

        public boolean needsRefresh(boolean cacheObjPresent, String key) {
           if (!cacheObjPresent || (expiryTime > 0 && System.currentTimeMillis() > expiryTime))
                return true;

            return false;
        }

        public void doneRefresh(String key, long timeToNextRefresh) throws CacheException {
            // no-op
        }

        public boolean isLocal() {
            return true;
        }

        public boolean invalidate(String key) {
            put(key, null);
            if (expiryTime == -1)
                return true; // well we live in hope right?
            return false;
        }
    }
}
