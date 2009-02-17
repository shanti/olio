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
 * $Id: Loadable.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package org.apache.olio.workload.loader.framework;

public abstract class Loadable {

    // Sequence is set by the pool.
    int sequence;

    protected Loader loader = Loader.getInstance(getClass());
    LoadablePool<? extends Loadable> pool;

    /**
     * Obtains the sequence, starting from 0, of this loader.
     *
     * @return The sequence of this loadable.
     */
    protected int getSequence() {
        return sequence;
    }

    public abstract String getClearStatement();

    public abstract void prepare();

    public abstract void load();

    public void postLoad() {
        // Empty. We do not make it abstract.
        //  A majority of LoadObjects do not need this.
    }
}
