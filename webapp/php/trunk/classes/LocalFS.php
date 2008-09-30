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
    
/**
 * Class for accessing the local file system.
 * Allows creation, retrieval and storage of files, querying
 * existence of a file, etc.
 */

class LocalFS extends FileSystem {
	
	function __construct() {
		$this->localFSRoot = Web20::$config['localfsRoot'];
	}
	
	function getRootDir() {
		return $this->localFSRoot;
	}

	function create($filename, $replication_factor='1',
	 								$overwrite = 'true') {
		$newfilename = $this->getNewFileName($filename); 

		if ($newfilename == $filename) {
			return true;
		}
		
		if ($overwrite) {
			return copy($filename, $newfilename);
		} else {
			if (file_exists($newfilename)) {
				return false;
			} else {
				return copy($filename, $newfilename);
			}
			
		}		
	}
	
	function getNewFileName($oldfilename) {
		return $this->localFSRoot . '/' . basename($oldfilename);
	}
	
	function getPaths($filename) {
	   return array(0 => $this->localFSRoot . '/' . $filename);
	}

	/* return true or false */
	function delete($filename) { return unlink($this->getNewFileName($filename)); }

	/* return true or false */
	function exists($filename) { return file_exists($this->getNewFileName($filename)); }

	function open($filename) { return file_get_contents($this->getNewFileName($filename)); }	
}
?>
