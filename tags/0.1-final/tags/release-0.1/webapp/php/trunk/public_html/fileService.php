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
 * psriniva 5/3/2007
 *
 * The mogile client(or the storage servers) do not
 * (of course) know the file types of the data that
 * they store.

 * This fileService attempts to guess the mime type
 * of the file based on it's key name.
 * It then passes on the mime-type and the file to
 * the client.
 */
require_once("../etc/config.php");
//$cache = CacheSystem::getInstance();


$mimeTypes = array();
$mimeTypes['png'] = 'image/png';
$mimeTypes['pdf'] = 'application/pdf';
$mimeTypes['gif'] = 'image/gif';
$mimeTypes['jpg'] = 'image/jpeg';
$mimeTypes['jpeg'] = 'image/jpeg';
$mimeTypes['odt'] = 'application/vnd.oasis.opendocument.text';

//TODO: Add mime types mappings for other formats.
//$mimeTypes['doc'] = 'application/vnd.oasis.opendocument.text';
//$mimeTypes['xls'] = 'application/vnd.oasis.opendocument.text';
//$mimeTypes['mp3'] = 'application/vnd.oasis.opendocument.text';
//$mimeTypes['rm'] = 'application/vnd.oasis.opendocument.text';

$fileName = $_GET['file'];

if ($fileName != "") {
	//$cacheFlag = $_GET['cache'];
	$pathInfo = pathinfo($fileName);
	$extension = $pathInfo['extension'];
	$contentType = $mimeTypes[$extension];
	header('Content-Type: ' . $contentType);

	$fs = FileSystem::getInstance();
	$paths = $fs->getPaths($fileName);
	//$cache->set($fileName, $paths, 0, Olio::$config['cacheExpire']);
	$path_keys = array_keys($paths);
	$pathkeycount = count($path_keys);
	$i=0;
	while ($i < $pathkeycount) {
		if (!($paths[$path_keys[$i]] == "")) {
			readfile($paths[$path_keys[$i]]);
			break;
		} else {
			$i++;
		}
	}
} else {
	$contentType = $mimeTypes['gif'];
	header('Content-Type: ' . $contentType);
	readfile(Olio::$config['includes'] . 'notavailable.gif');
}
// }else{
//        $path_keys = array_keys($memcachegetPaths);
//        $pathkeycount = count($path_keys);
//        $i=0;
//        while($i < $pathkeycount) {
//                If(!($paths[$path_keys[$i]] == "") ) {
//                        readfile($paths[$path_keys[$i]]);
	//                        break;
	//                }else{
	//                        $i++;
	//                }
	//        }
	// }

	//        if ($cacheFlag == "true"){
	//                $cache->set($filename, file_get_contents($filename), $olioconfig['cacheExpire']);
	//        }
	// } else {
	//    echo $content;
	// }
	?>
