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
echo "<b>Installation base for apache package                      : </b> ".$web20config['apacheinstallBase']."<br/>";
echo "<b>Installation base for php				   : </b> ".$web20config['phpinstallBase']."<br/>";
echo "<b>Installation base for mysql                               : </b> ".$web20config['mysqlinstallBase']."<br/>";
echo "<b>Installation base for memcached                           : </b> ".$web20config['memcachedinstallBase']."<br/>";
echo "<b>Installation base for mogilefs                            : </b> ".$web20config['mogilefsinstallBase']."<br/>";
echo "<b>The directory that directly corresponds to urlBase        : </b> ".$web20config['siteBase']."<br/>";
echo "<b>Base url for the site                                     : </b> ".$web20config['urlBase']."<br/>";
echo "<b>Expiry time for memcache on set function in seconds       : </b> ".$web20config['cacheExpire']."<br/>";
echo "<b>Mogilefs host ip & port number                            : </b> ".$web20config['mogilefsHost']."<br/>";
echo "<b>Memcached host ip                                         : </b> ".$web20config['memcachedHost']."<br/>";
echo "<b>Gecoder Emulator host ip				   : </b> ".$web20config['geocoderHost']."<br/>";
echo "<b>Flag set for 'onuseradd' tag                              : </b> ".$web20config['onuseradd']."<br/>";
echo "<b>Flag set for 'personrequested' tag                        : </b> ".$web20config['personrequested']."<br/>";
echo "<b>Flag set for 'top10events' tag                            : </b> ".$web20config['top10events']."<br/>";
echo "<b>Flag set for 'tagcloud' tag                               : </b> ".$web20config['tagcloud']."<br/>";
?>
