<?php
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
    
abstract class CacheSystem {

    static function getInstance() {
        $classname = Olio::$config['cacheSystem'];
        return new $classname;
    }

    static function isCachingActive()
    {
        return Olio::$config['cacheSystem'] != 'NoCache';
    }
    
    abstract function flush();

    abstract function get($key);

    abstract function set($key, $var, $compress=0, $expire);
 
    abstract function add($key, $var, $compress=0, $expire=0);

    abstract function replace($key, $var, $compress=0, $expire=0);

    abstract function delete($key, $timeout=0); 

    abstract function increment($key, $value=1);

    abstract function decrement($key, $value=1);
    
    abstract function needsRefresh($key);
    
    abstract function doneRefresh($key, $timeToNextRefresh);
}
