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
    
class PDOConnection extends DBConnection {

    var $connection;
    
    private function ensureConnection() {
        if (!isset($this->dbTarget)) {
            $this->selectInstance();
        }
    	if (!isset($this->connection)) {
            $this->connection = new PDO($this->dbTarget,
                Olio::$config['dbUser'], Olio::$config['dbPass'], 
                array(PDO::ATTR_PERSISTENT => true));
            $this->connection->setAttribute(PDO::MYSQL_ATTR_USE_BUFFERED_QUERY, true);
            $this->connection->setAttribute( // throw exception on error.
                PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);                
            if (!$this->connection)
                throw new Exception("Unable to connect " . $this->dbTarget."!");
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
        	$stmt = $this->connection->prepare($sql);
        	$stmt->execute($args);
        	return new PDOResult($this, $stmt);
        } else {
        	$result = $this->connection->query($sql);
            return new PDOResult($this, $result);
        }
    }

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
        	$stmt = $this->connection->prepare($sql);
        	$stmt->execute($args);
        	$rows = $stmt->rowCount();
        	return $rows;
        } else {
            return $this->connection->exec($sql);
        }
    }

    function beginTransaction() {
        $this->ensureConnection();
        $this->connection->setAttribute(PDO::ATTR_AUTOCOMMIT, false);
        $this->connection->beginTransaction();
    }

    function commit() {
        $this->connection->commit();
        $this->connection->setAttribute(PDO::ATTR_AUTOCOMMIT, true);
    }

    function rollback() {
        $this->connection->rollBack();
        $this->connection->setAttribute(PDO::ATTR_AUTOCOMMIT, true);
    }

    
    function __destruct() {
        if (isset($this->connection)) {
            unset($this->connection);
        }
    }
}
?>
