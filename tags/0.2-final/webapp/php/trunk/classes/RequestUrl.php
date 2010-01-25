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
 * PHP Template.
 * Author: Sheetal Patil. Sun Microsystems, Inc.
 */

class RequestUrl
{
    function getGetRequest(){
          $arg = array();
          $string = "?";
          $vars = $_GET;
          for ($i = 0; $i < func_num_args(); $i++)
            $arg[func_get_arg($i)] = func_get_arg(++$i);
          foreach (array_keys($arg) as $key)
            $vars[$key] = $arg[$key];
          foreach (array_keys($vars) as $key)
            if ($vars[$key] != "")
                $string.= $key . "=" . $vars[$key] . "&";

          return htmlspecialchars(substr($string, 0, -1));
    }

	static function getInstance() {
        $instance = new RequestUrl();
        return $instance;
    }
}
?>
