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
    
abstract class DBConnection {

    var $writeInstance;
    var $dbTarget;


    static function getInstance() {
        $classname = Olio::$config['dbDriver'] . 'Connection';
        $instance = new $classname;
        $instance->writeInstance = false;
        return $instance;
    }

    static function getWriteInstance() {
        $classname = Olio::$config['dbDriver'] . 'Connection';
        $instance = new $classname;
        $instance->writeInstance = true;
        return $instance;
    }

    function selectInstance() {
        $this->dbTarget = Olio::$config['dbTarget'];
        if (is_array($this->dbTarget)) {
            if ($this->writeInstance || count($this->dbTarget) == 1) {
                $this->dbTarget = $this->dbTarget[0];
            } else {
                $idx = (getmypid() % (count($this->dbTarget) - 1)) + 1;
                // $idx = rand(1, count($this->dbTarget) - 1);
                $this->dbTarget = $this->dbTarget[$idx];
            }
        }
    }
    
    abstract function query();
    
    abstract function exec();

    abstract function beginTransaction();

    abstract function commit();

    abstract function rollback();
}
?>
