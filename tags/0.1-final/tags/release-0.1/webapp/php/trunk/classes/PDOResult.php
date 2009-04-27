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
    
class PDOResult implements DBResult {

    // We need to reference the connection so that
    // that it does not get destroyed before the result.
    var $connection;

    var $result;
    var $resultArray;
    var $cursorClosed;

    function __construct($connection, $result) {
        $this->connection = $connection;
    	$this->result = $result;
    	$this->cursorClosed = false;
    }

    function rows() {
        return $this->result->rowCount();
    }

    function next() {    	
    	$this->resultArray = $this->result->fetch();
    	if ($this->resultArray) {
    	    return true;
    	} else {
            $this->result->closeCursor();
            $this->cursorClosed = true;
     		return false;
    	}
    }

    function get($position) {
    	if (is_int($position))
    		--$position; // $position starts with 1, array index with 0    	
    	return $this->resultArray[$position];
    }

    function getArray() {
    	return $this->result->fetch();
    }

    function getObject() {
    	return $this->result->fetch(PDO::FETCH_OBJ);
    }

    function __destruct() {
    	if (!cursorClosed)
    	    $this->result->closeCursor();
    	unset($this->resultArray);
        unset($this->result);
        unset($this->connection);
    }
}
?>
