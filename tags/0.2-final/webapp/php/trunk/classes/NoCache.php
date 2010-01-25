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

/*
 * Constructed by Damien Cooke (damien.cooke@sun.com)
 * The purpose of this apparently empty class is to have a cache system
 * that does no caching, but requires no code changes to turn it on or off.
 * There are two basic ways of doing this.  1 - have a config boolean that
 * requires a fair number of checks. 2 - have a fully featured cache system that
 * does not comit anything to or retrieve andything from the underlying cache.
 * What we have done is created stubs of the abstract methods from the base
 * class so the only cost is the method invocation.
 */

class NoCache extends CacheSystem
{ 
	function __construct()
    {
        return;
	}

	function delete($key, $timeout=0)
    {
		return;
	}

 	function flush()
    {
 		return;
 	}

 	function get($key)
    {
 		return null;
    }

	function set($key, $var, $compress=0, $expire)
    {
		return;
	}

	function add($key, $var, $compress=0, $expire=0)
    {
		return;
	}

	function replace($key, $var, $compress=0, $expire=0)
    {
		return;
	}

	function increment($key, $value=1)
    {
		return;
	}

	function decrement($key, $value=1)
    {
		return;
	}

	function needsRefresh($key)
    {
	    return true;
    }


	function doneRefresh($key, $timeToNextRefresh)
    {
	    return;
	}
}
?>
