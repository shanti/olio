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
    
class ODBCConnection extends DBConnection {

    var $connection;

    private function ensureConnection() {
        if (!isset($this->dbTarget)) {
            $this->selectInstance();
        }
    	if (!isset($this->connection)) {
            $this->connection = odbc_connect($this->dbTarget,
                Olio::$config['dbUser'], Olio::$config['dbPass']);
            if (is_resource($this->connection))
                throw new Exception("Unable to connect ".$this->dbTarget."!");
        }
    }

    function query() {
    	if (func_num_args() > 1) {
    	    $args = func_get_args();
    	    $sql = $args[0];
    	    unset($args[0]); // remove the sql
    	    $args = array_values($args); // and reset the array index    		
    	} else {
    		$sql = func_get_arg(0);
    	}
        $this->ensureConnection();
        if (isset($args)) {
        	error_log("You are using prepared statements. This is extremely slow!");
        	$result = odbc_prepare($this->connection, $sql);
        	if (!odbc_execute($result, $args))
        		throw new Exception(odbc_errormsg($this->connection));
        	return new ODBCResult($this, $result);
        } else {
            return new ODBCResult($this, odbc_exec($this->connection, $sql));
        }
    }
    
    /**
     * Executes the supplied SQL statement and returns
     * the result of the call.
     * 
     * @access  public
     *  
     * @param   string  SQL to execute
     */  
    function exec() {
    	if (func_num_args() > 1) {
    	    $args = func_get_args();
    	    $sql = $args[0];
    	    unset($args[0]); // remove the sql
    	    $args = array_values($args); // and reset the array index    		
    	} else {
    		$sql = func_get_arg(0);
    	}
        $this->ensureConnection();
        if (isset($args)) {
        	$result = odbc_prepare($this->connection, $sql);
        	if (!odbc_execute($result, $args))
        		throw new Exception(odbc_errormsg($this->connection));
        	return odbc_num_rows($result);
        } else {
            return odbc_exec($this->connection, $sql);
        }
    }

    function beginTransaction() {
        $this->ensureConnection();
        odbc_autocommit($this->connection, false);
    }

    function commit() {
        odbc_commit($this->conneection);
        odbc_autocommit($this->connection, true);
    }

    function rollback() {
        odbc_rollback($this->connection);
        odbc_autocommit($this->connection, true);
    }
    
    function __destruct() {
        if (is_resource($this->connection)) {
            odbc_close($this->connection);
        }
        unset($this->connection);
    }
}
?>
