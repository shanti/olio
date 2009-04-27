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
 * Class for accessing the MogileFS file system
 * Allows creation of classes, retrieval and storage of files, querying
 * existence of a file, etc.
 */

class MogileFS extends FileSystem {
	
	var $socket;
	/**
	 * Constructor
	 * 
	 * TODO
	 */
	function __construct() {
		$this->domain = Olio::$config['mogilefsDomain'];
		$this->hosts  = Olio::$config['mogilefsHosts'];
		$this->root   = Olio::$config['mogilefsRoot'];
		$this->error  = '';
	}


	/**
	 * Connect to a mogilefsd
	 * Scans through the list of daemons and tries to connect one.
	 */
	private function connect() {
		foreach ($this->hosts as $host) {
			list($ip,$port)=split(':',$host,2);
			if ($port==null)
				$port = 7001;
			$this->socket = fsockopen($ip, $port);
			if ($this->socket) {
				break;
			}
		}
	}

	/**
	 * Send a request to mogilefsd and parse the result.
	 * @private
	 */
	function doRequest($cmd, $args=array())	{
		$params=' domain='.urlencode($this->domain);
		foreach ($args as $key => $value)
			$params.='&'.urlencode($key)."=".urlencode($value);

		if ( ! $this->socket ) {
			$this->connect();
		}
		fwrite( $this->socket, $cmd . $params."\n" );
		$line = fgets( $this->socket );
		$words = explode( ' ', $line );
		if ( $words[0] == 'OK' ) {
			parse_str( trim( $words[1] ), $result );
		} else {
			$result = false;
			$this->error = join(" ",$words);
		}
		return $result;
	}

	/**
	 * Return a list of domains
	 */
	function getDomains() {
		$res = $this->doRequest( 'GET_DOMAINS' );
		if ( ! $res ) {
			return false;
		}
		$domains = array();
		for ( $i=1; $i <= $res['domains']; $i++ ) {
			$dom = 'domain'.$i;
			$classes = array();
			for ( $j=1; $j<=$res[$dom.'classes']; $j++ ) {
				$classes[$res[$dom.'class'.$j.'name']] = $res[$dom.'class'.$j.'mindevcount'];
			}
			$domains[] = array( 'name' => $res[$dom],
					'classes' => $classes );
		}
		return $domains;
	}

	/**
	 * Get an array of paths
	 */
	function getPaths($key)	{
		$res = $this->doRequest( "GET_PATHS", array("key" => $key));
		unset( $res['paths'] );
		return $res;
	}

	/** 
	 * Delete a file from system
	 */
	function delete($key) {
		$res = $this->doRequest( "DELETE", array("key" => $key));
		if ($res===false)
			return false;
		return true;
	}

	/**
	 * Rename a file
         */
	function rename($from, $to)	{
		$res = $this->doRequest( "RENAME", array("from_key"=>$from,"to_key"=>$to));
		if ($res===false) 
			return false;
		return true;
	}

	/**
	 * Get a file from the file service and return it as a string
	 * TODO
	 */
	function getFileData($key) {
		$paths = $this->getPaths($key);
		if ($paths == false)
			return false;
		foreach ( $paths as $path ) {
			$fh = fopen( $path, 'r' );
			$contents = '';

			if ( $fh ) {
				while (!feof($fh)) {
					$contents .= fread($fh, 8192);
				}
				fclose( $fh );
				return $contents;
			}
		}
		return false;
	}

	/**
	 * Get a file from the file service and send it directly to stdout
	 * uses fpassthru()
	 * TODO
	 */
	function getFileDataAndSend($key) {
		$paths = $this->getPaths( $key );
		if (!$paths) 
			return false;
		foreach ( $paths as $path ) {
			$fh = fopen( $path, 'r' );

			if ( $fh ) {
				$success = fpassthru( $fh );
			}
			fclose( $fh );
			return $success;
		}
		return false;
	}

	/**
	 * Save a file to the MogileFS
	 * TODO
	 */
	function saveFile($key, $class, $filename) {
		$res = $this->doRequest( "CREATE_OPEN", array("key"=>$key, "class"=>$class));

		if ( ! $res )
			return false;

		if ( preg_match( '/^http:\/\/([a-z0-9.-]*):([0-9]*)\/(.*)$/', $res['path'], $matches ) ) {
			$host = $matches[1];
			$port = $matches[2];
			$path = $matches[3];

			// $fout = fopen( $res['path'], 'w' );
			$fin = fopen( $filename, 'r' );
			$ch = curl_init();
			curl_setopt($ch,CURLOPT_PUT,1);
			curl_setopt($ch,CURLOPT_URL, $res['path']);
			curl_setopt($ch,CURLOPT_VERBOSE, 0);
			curl_setopt($ch,CURLOPT_INFILE, $fin);
			curl_setopt($ch,CURLOPT_INFILESIZE, filesize($filename));
			curl_setopt($ch,CURLOPT_TIMEOUT, 4);
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
			if(!curl_exec($ch)) {
				$this->error=curl_error($ch);
				curl_close($ch);
				return false;
			}
			curl_close($ch);

			$closeres = $this->doRequest( "CREATE_CLOSE", array(
				"key"	=> $key,
				"class" => $class,
				"devid" => $res['devid'],
				"fid"   => $res['fid'],
				"path"  => urldecode($res['path'])
				));
			if ($closeres===false) {
				return false;
			} else {
				return true;
			}
		}
	}
	
    function create($key, $replication_factor = 2, $overwrite = 'true') {
  		$this->saveFile($key, string($replication_factor), $key);
  	}
  	
    function exists($key) {
    	return $this->getPaths($key);
    }
    
    function open($key) { 
    	return $this->getFileData($key);
    }	
}
?>
