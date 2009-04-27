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
    
class ODBCResult implements DBResult {

    var $result;

    // We need to reference the connection so that
    // that it does not get destroyed before the result.
    var $connection;

    function __construct($connection, $result) {
        $this->result = $result;
        $this->connection = $connection;
    }

    function rows() {
        return odbc_num_rows($this->result);
    }

    function next() {
        return odbc_fetch_row($this->result);
    }

    function get($position) {
        return odbc_result($this->result, $position);
    }

    function getArray() {
        return odbc_fetch_array($this->result);
    }

    function getObject() {
        return odbc_fetch_object($this->result);
    }

    function __destruct() {
        odbc_free_result($this->result);
        unset($this->result);
        unset($this->connection);
    }
}
?>
