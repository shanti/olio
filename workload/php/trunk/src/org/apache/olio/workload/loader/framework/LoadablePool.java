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
 * 
 * $Id$
 */
package org.apache.olio.workload.loader.framework;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akara
 */
public class LoadablePool<T extends Loadable> {

    private static Logger logger = Logger.getLogger(LoadablePool.class.getName());
    LinkedBlockingDeque<T> pool = new LinkedBlockingDeque<T>();
    int sequence;
    int count = 0;
    int size;
    Class<T> clazz;

    public LoadablePool(int size, Class<T> clazz) {
        this.size = size;
        this.clazz = clazz;
    }

    public T getLoadable() throws Exception {
        T loadable = pool.poll();
        if (loadable == null) {
            if (count < size) {
                loadable = clazz.newInstance();
                loadable.pool = this;
                ++count;
            } else {
                for (;;) {
                    try {
                        loadable = pool.take();
                        break;
                    } catch (InterruptedException ex) {
                        logger.log(Level.WARNING, "getLoader interrupted", ex);
                    }
                }
            }
        }
        loadable.sequence = sequence++;
        return loadable;
    }

    @SuppressWarnings("unchecked")
    public void putLoader(Loadable loadable) {
        for (;;) {
            try {
                // User a LIFO model to keep the hot objects in cache.
                pool.putFirst((T) loadable);
                break;
            } catch (InterruptedException ex) {
                logger.log(Level.WARNING, "putLoader interrupted!", ex);
            }
        }
    }
}
