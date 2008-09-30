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
    
$web20config['includes'] = $_SERVER['DOCUMENT_ROOT'] . '/../includes/';

$web20config['dbDriver'] = 'PDO';  // Which DB driver to use.

// $web20config['dbTarget'] = 'myodbc3'; // ODBC target in odbc.ini.
$web20config['dbTarget'] = 'mysql:host=localhost;dbname=web20load'; // PDO target.

$web20config['dbUser'] = 'web20'; // DB user name.

$web20config['dbPass'] = 'web20'; // DB password.

$web20config['cacheSystem'] = 'MemCached';

$web20config['cacheExpire'] = 7200; // Expiry time for memcache on set function in seconds

$web20config['fileSystem'] = 'LocalFS';

$web20config['localfsRoot'] = '/filestore';

$web20config['memcachedHosts'] = array('MEMCACHED_HOST:port', 'MEMCACHED_HOST:port'); // memcached host ip

$web20config['geocoderURL'] = 'http://GEOCODER_HOST:8080/Web20Emulator/geocode'; //Geocoder URL

class Web20 {
    public static $config;
}

// Make the config available through the static in the class.
Web20::$config = $web20config;

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
