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
    
$olioconfig['includes'] = $_SERVER['DOCUMENT_ROOT'] . '/../includes/';

$olioconfig['dbDriver'] = 'PDO';  // Which DB driver to use.
// $olioconfig['dbTarget'] = 'myodbc3'; // ODBC target in odbc.ini.

$olioconfig['dbTarget'] = 'mysql:host=localhost;dbname=olio'; // PDO target.
// For master/slave clusters, specify dbTarget as an array, listing the master first.
// $olioconfig['dbTarget'] = array('mysql:host=master;dbname=olio',
//                                  'mysql:host=slave1;dbname=olio',
//                                  'mysql:host=slave2;dbname=olio');

$olioconfig['dbUser'] = 'olio'; // DB user name.

$olioconfig['dbPass'] = 'olio'; // DB password.

$olioconfig['cacheSystem'] = 'MemCached';
// Use below line for no cache - WARNING: db load will increase
//$olioconfig['cacheSystem'] = 'NoCache';

$olioconfig['cacheExpire'] = 7200; // Expiry time for memcache on set function in seconds

$olioconfig['fileSystem'] = 'LocalFS';

$olioconfig['localfsRoot'] = '/filestore';

$olioconfig['memcachedHosts'] = array('MEMCACHED_HOST:11211'); // memcached host ip
// For multiple instances, use the below line
// $olioconfig['memcachedHosts'] = array('MEMCACHED_HOST1:port', 'MEMCACHED_HOST2:port'); // memcached host ip

$olioconfig['geocoderURL'] = 'http://GEOCODER_HOST:8080/geocoder/geocode'; //Geocoder URL

class Olio {
    public static $config;
}

// Make the config available through the static in the class.
Olio::$config = $olioconfig;

function __autoload($class_name)
    {
        $dir1 = $_SERVER['DOCUMENT_ROOT'] . '/../classes/';
        $dir2 = $_SERVER['DOCUMENT_ROOT'] . '/../controllers/';
        //class directories
        $directorys = array($dir1,$dir2);       
        //for each directory
        foreach($directorys as $directory)
        {
            //see if the file exsists
            if(file_exists($directory.$class_name . '.php'))
            {
                require_once($directory.$class_name . '.php');
                //only require the class once, so quit after to save effort (if you got more, then name them something else
                return;
            }           
        }
    }

function displayException($exception) {
    // header('HTTP/1.0 500 Internal Server Error');
    $message = $exception->__toString();
    echo($message . '\nPlease check server logs for detail');
    error_log($message);
}

set_exception_handler('displayException');
?>
