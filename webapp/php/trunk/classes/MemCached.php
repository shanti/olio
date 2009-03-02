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
    
class MemCached extends CacheSystem {

	function __construct() {
	    // TODO: Support host:port arrays.	   
	    $this->memcache = new Memcache();
	    foreach (Olio::$config['memcachedHosts'] as $host) {
			list($ip,$port)=split(':',$host,2);
			if ($port == null)
				$port = 11211;				
	    	if (!$this->memcache->addServer($ip, $port))
	    		throw new Exception("Could not connect to $hosts:$port"); 
		}
	}
 
	function delete($key, $timeout=0) { 
		return $this->memcache->delete($key,$timeout=0);
	}

 	function flush() { 
 		return $this->memcache->flush();
 	}

 	function get($key) { 
 		return $this->memcache->get($key); 
    }
 
	function set($key, $var, $compress=0, $expire) {
		return $this->memcache->set($key, $var, $compress=0, $expire);
	}

	function add($key, $var, $compress=0, $expire=0) {
		return $this->memcache->add($key, $var, $compress=0, $expire=0);
	}

	function replace($key, $var, $compress=0, $expire=0) {
		return $this->memcache->replace($key, $var, $compress=0, 
										$expire=0);
	}

	function increment($key, $value=1) {
		return $this->memcache->increment($key, $value=1);
	}

	function decrement($key, $value=1) {
		return $this->memcache->decrement($key, $value=1);
	}
	
	function needsRefresh($key) {
	    $updateSema = $key . 'UpdateSema';
	    if (!$this->memcache->get($updateSema)) {
	        $updateLock = $key . 'UpdateLock';
	        $hostPid = php_uname('n') . ':' . getmypid();
            if ($this->memcache->add($updateLock, $hostPid, 0, 5)) {
                error_log($hostPid." obtained ".$updateLock);
                return true;
            }
	    }
	    return false;
    }

	
	function doneRefresh($key, $timeToNextRefresh) {
	    $updateSema = $key . 'UpdateSema';
	    $updateLock = $key . 'UpdateLock';
	    $hostPid = php_uname('n') . ':' . getmypid();
	    if ($this->memcache->get($updateLock) != $hostPid)
	    	throw new Exception('RefreshLock for ' . $key .
	    	                    ' not locked by this process!');
	    $this->memcache->set($updateSema, 1, 0, $timeToNextRefresh);
	    $this->memcache->delete($updateLock);
        error_log($hostPid." released ".$updateLock);
	}
}
?>
