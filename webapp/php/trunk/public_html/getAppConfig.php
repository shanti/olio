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
        
require_once("../etc/config.php");

echo "This is the configuration setup for the application........<br/>";
echo "<br/>";
echo "<b>Installation base for apache package                      : </b> ".$olioconfig['apacheinstallBase']."<br/>";
echo "<b>Installation base for php				   : </b> ".$olioconfig['phpinstallBase']."<br/>";
echo "<b>Installation base for mysql                               : </b> ".$olioconfig['mysqlinstallBase']."<br/>";
echo "<b>Installation base for memcached                           : </b> ".$olioconfig['memcachedinstallBase']."<br/>";
echo "<b>Installation base for mogilefs                            : </b> ".$olioconfig['mogilefsinstallBase']."<br/>";
echo "<b>The directory that directly corresponds to urlBase        : </b> ".$olioconfig['siteBase']."<br/>";
echo "<b>Base url for the site                                     : </b> ".$olioconfig['urlBase']."<br/>";
echo "<b>Expiry time for memcache on set function in seconds       : </b> ".$olioconfig['cacheExpire']."<br/>";
echo "<b>Mogilefs host ip & port number                            : </b> ".$olioconfig['mogilefsHost']."<br/>";
echo "<b>Memcached host ip                                         : </b> ".$olioconfig['memcachedHost']."<br/>";
echo "<b>Gecoder Emulator host ip				   : </b> ".$olioconfig['geocoderHost']."<br/>";
echo "<b>Flag set for 'onuseradd' tag                              : </b> ".$olioconfig['onuseradd']."<br/>";
echo "<b>Flag set for 'personrequested' tag                        : </b> ".$olioconfig['personrequested']."<br/>";
echo "<b>Flag set for 'top10events' tag                            : </b> ".$olioconfig['top10events']."<br/>";
echo "<b>Flag set for 'tagcloud' tag                               : </b> ".$olioconfig['tagcloud']."<br/>";
?>
