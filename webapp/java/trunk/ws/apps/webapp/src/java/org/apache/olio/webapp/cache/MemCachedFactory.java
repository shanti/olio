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

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import java.util.Date;

/**
 * The MemCachedFactory uses Memcached as the object cache in the back.
 */
public class MemCachedFactory extends CacheFactory {

    // Danga's implementation of the client is thread safe and designed
    // for single instance use. So we won't waste instances. Keep it static.
    private static MemCachedClient cache = null;

    public MemCachedFactory() {
        if (cache == null) {
            // the env memcachedInstances is in the
            // form host1:port1, host2:port2, etc.
            String servers = locator.getString("memcachedInstances");
            String[] serverList = servers.split(",?[ *]");
            //SockIOPool pool = SockIOPool.getInstance("livePool");
            SockIOPool pool = SockIOPool.getInstance();
            
            // Set equal weight for all servers
            if (serverList.length > 1) {
                Integer[] wts = new Integer[serverList.length];
                for (int i=0; i<wts.length; i++)
                    wts[i] = Integer.valueOf(1);
                pool.setWeights(wts);
            }
            pool.setServers(serverList);
            
            // The following options have been set based on the Whalin doc
            pool.setInitConn( 8 );
            pool.setMinConn( 8 );
            pool.setMaxConn( 128 );
            pool.setMaxIdle( 1000 * 60 * 60 * 6 );

            // set some TCP settings
            // disable nagle
            // set the read timeout to 3 secs
            // and don't set a connect timeout
            pool.setNagle( false );
            pool.setSocketTO( 3000 );
            pool.setSocketConnectTO( 0 );

            pool.initialize();

            cache = new MemCachedClient();
            
            // Set thelog level to WARNING -- The defautl is INFO which causes unecessary logging
            

            //Logger log = cache.getLogger();
            
            //log.setLevel(Logger.LEVEL_WARN);
            
            // lets set some compression on for the client
        // compress anything larger than 1MB
            cache.setCompressEnable( true );
            cache.setCompressThreshold( 1024 * 1024 );
            
        }
    }
    
    /**
     * Obtains the cache instance.
     *
     * @return The cache instance
     * @param type
     */
    public Cache createCache(String type) {
        return new MemCache(type);
    }

    static class MemCache implements Cache {

        private String prefix;

        private MemCache(String type) {
            prefix = type + ':';
        }

        /**
         * Gets the cached value based on a key.
         *
         * @param key The key
         * @return The cached object, or null if none is available
         */
        public Object get(String key) {
            Object value = cache.get(prefix + key);
            if (value != null)
                logHit(prefix, key);
            return value;
        }

        /**
         * Sets a cached item using a key.
         *
         * @param key   The key
         * @param value The object to cache.
         */
        public void put(String key, Object value) {
            if (!cache.set(prefix + key, value))
                throw new CacheException("Error putting object key=" + prefix +
                                        key + ", value=" + value + " to cache");
        }
        
        /** Sets a cached item using a key and specifies a timeout
         *
         * @param key The key
         * @param value The object to cache
         * @param timeToLive The time to live for this object in seconds
         */
        public void put (String key, Object value, long timeToLive) {
            Date expiry = new Date (System.currentTimeMillis() + timeToLive * 1000);

            if (!cache.set(prefix + key, value, expiry))
                throw new CacheException("Error putting object key=" + prefix +
                                        key + ", value=" + value + " to cache");
        }

        public boolean needsRefresh(String key) {
            // Double checked lock. We first set the lock, then check whether we own the lock
            // If updateSema is not null, then we don't need to updatet he cache. 
            // updateSema is stroed with a timeout, so it will expire based on 
            // cache expiry time
            String updateSema = prefix + key + ".UpdateSema";
            String updateLock = prefix + key + ".UpdateLock";
            if (cache.get(updateSema) == null) {
               String lockId = CacheFactory.getInstance().getLockId();
               if (cache.get(updateLock) == null) {
                   Date expiryTime = new Date (System.currentTimeMillis() 
                   + CacheFactory.getInstance().getCacheLockExpireInSeconds()*1000);
                   
                   //if (!cache.set(updateLock, lockId, expiryTime)) {
                   if (!cache.set(updateLock, lockId, expiryTime)) {
                       throw new CacheException("Error setting updateLock - key = " + 
                               updateLock +" lockId = " + lockId);
                   }
                   else {
                       System.out.println ("Set updateLock = " + updateLock + 
                               " with lockID = " + lockId);
                   }
               
                   // Now that we have set the lock. Check whether anyone has overwritten it.
                   if (cache.get(updateLock) != null) {
                       if (cache.get(updateLock).equals(lockId))
                            return true;
                   }
                   else {
                       System.out.println ("Get updateLock = " + updateLock + 
                               " return null" );
                       throw new CacheException("Error getting updateLock - key = " + 
                               updateLock + " lockId = " + lockId);
                       
                   } 
               }
            }
            return false;
        }

        /** Resets  the cache lock 
         *
         * @param key The key
         * @param timeToNextRefresh The time to live for this object in seconds
         */
        public void doneRefresh(String key, long timeToNextRefresh) throws CacheException {
            String updateSema = prefix + key + ".UpdateSema";
            String updateLock = prefix + key + ".UpdateLock";
            String lockId = CacheFactory.getInstance().getLockId();
            
            if (!cache.get(updateLock).equals(lockId)) {
                throw new CacheException ("UpdateLock for key = " + (prefix + key) +
                        " is not locked by this process");
            }
            Date expiryTime = new Date (System.currentTimeMillis() + timeToNextRefresh * 1000);
            cache.set(updateSema, 1, expiryTime);
            cache.delete(updateLock);
        }

        public boolean isLocal() {
            return false;
        }
    }
}
