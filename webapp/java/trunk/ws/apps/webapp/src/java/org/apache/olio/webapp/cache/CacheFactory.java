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

import org.apache.olio.webapp.util.ServiceLocator;
import org.apache.olio.webapp.util.WebappConstants;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A factory to get to different cache implementations.
 */
public abstract class CacheFactory {

    private static final Level HIT_LEVEL = Level.FINER;
    private static final Cache NULLCACHE = new NullCache();
    private static CacheFactory factory;
    private static Logger logger = Logger.getLogger(CacheFactory.class.getName());
    static ServiceLocator locator;
    private int cacheLockExpireInSeconds = 120; // in seconds
    private int cacheExpireInSeconds = 7200; // in seconds
    private String lockIdPrefix;

    /**
     * Obtains the configured cache factory, or the SimpleMapCacheFactory
     * if none is configured.
     * @return The cache factory
     */
    public synchronized static CacheFactory getInstance() {
        // Making this totally thread safe has it's tolls. So we will live with
        // some duplicate factory creation if it really happens.
        // This should cost less than synchronizing all the time.
        if (factory == null) {
            locator = ServiceLocator.getInstance();
            String factoryName = locator.getString("cacheFactoryClass",
                                                WebappConstants.DEFAULT_CACHE);
            try {
                factory = Class.forName(factoryName).
                            asSubclass(CacheFactory.class).newInstance();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error instantiating factory " +
                        factoryName + '.', e);
                throw new CacheException(e);
            }

            try {
                factory.cacheExpireInSeconds = Integer.valueOf(locator.getString("cacheExpire"));
                factory.cacheLockExpireInSeconds = Integer.valueOf(locator.getString("cacheLockExpire"));
            }
            catch (Exception e){}

            String pid = ManagementFactory.getRuntimeMXBean().getName();
            String hostname;
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                // If we cannot guess the hostname, we will try to make one up that has a pretty good
                // chance of being unique. Too VMs starting with the same ID and at the same time is 
                // going to be very rare.
                hostname = "localhost_" + System.currentTimeMillis();
            }
            factory.lockIdPrefix = hostname + ":" + pid + ".";
        }

        return factory;
    }
    
    public String getLockId() {
        return lockIdPrefix + Thread.currentThread().getId();
    }


    /**
     * This is a convenience method to obtain the cache directly.
     * If caching is allowed for a certain type, a cache instance will be
     * returned. Otherwise returns null.
     * @param type The checkCache key
     * @return The cache, or null if caching is not allowed
     */
    public static Cache getCache(String type) {
        Cache cache = NULLCACHE;
        locator = ServiceLocator.getInstance();

        // Whether to cache a type is determined by the env "cache<Type>"
        // i.e. to cache Person, the env cachePerson must be set to "true."
        boolean shouldCache = Boolean.parseBoolean(
                            locator.getString("cache" + type, "true"));
        if (shouldCache)
            cache = getInstance().createCache(type);
        return cache;
    }

    /**
     * Utility method to log cache hits, used for debugging.
     * @param type The type of the cache
     * @param key The key causing a cache hit.
     */
    static void logHit(String type, String key) {
        if (logger.isLoggable(HIT_LEVEL))
            logger.log(HIT_LEVEL, "Cache hit, type=" + type + " key=" + key);
    }
    
    
    public int getCacheLockExpireInSeconds() {
        return cacheLockExpireInSeconds;
    }

    public int getCacheExpireInSeconds() {
        return cacheExpireInSeconds;
    }

    /**
     * Obtains the cache instance. The type is used to avoid key conflicts,
     * i.e. ensuring you won't create the same key for different object types
     * or usages and thus replacing values you do not want to replace.
     * @return The cache instance
     * @param type The type of the cache or objects it holds.
     */
    public abstract Cache createCache(String type);

    /**
     * Stub cache that always return null so cache clients do not have to
     * check for a null on getCache all the time.
     */
    static class NullCache implements Cache {
        /**
         * Gets the cached value based on a key. Will always return null in
         * this version.
         *
         * @param key The key
         * @return The cached object, or null if none is available
         */
        public Object get(String key) {
            return null;
        }

        /**
         * Sets a cached item using a key. This implementation does nothing.
         *
         * @param key   The key
         * @param value The object to cache.
         */
        public void put(String key, Object value) {
        }

        /**
         * Sets a cached item using a key.
         * @param key The key
         * @param value The object to cache.
         * @param timeToLive Time to cache this object in seconds
         */
        public void put(String key, Object value, long timeToLive) {
        }

        public boolean needsRefresh(String key) {
            return false;
        }

        public void doneRefresh(String key, long timeToNextRefresh) throws CacheException {
        }

        public boolean isLocal() {
            return true;
        }
    }
}
