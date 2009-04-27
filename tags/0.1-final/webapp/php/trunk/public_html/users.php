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
 *
 */    
session_start();
require_once("../etc/config.php");
$username= $_REQUEST['username'];
$connection = DBConnection::getInstance();
$events = Events_Controller::getInstance();
$friends = Users_Controller::getInstance();
$friendCloud = $friends->getFriendCloud($username,$connection);
$incomingRequests = $friends->incomingRequests($username,$connection);
$outgoingRequests = $friends->outgoingRequests($username,$connection);
$flag = false;
$recentlyPostedEvents = $events->getRecentlyPostedEventsOfUser($username,$connection,$flag,null);
$sql = "select username, firstname, lastname, email, telephone, imagethumburl, summary, timezone, street1, street2, city, state, zip, country from PERSON as p, ADDRESS as a where p.ADDRESS_addressid=a.addressid and username='$username'"; 
$result = $connection->query($sql);
$row = $result->getArray();
$username = $row['username'];
$firstname = $row['firstname'];
$lastname = $row['lastname'];
$email = $row['email'];
$telephone = $row['telephone'];
$image = $row['imagethumburl'];
$summary = $row['summary'];
$timezone = $row['timezone'];
$street1 = $row['street1'];
$street2 = $row['street2'];
$city = $row['city'];
$state = $row['state'];
$zip = $row['zip'];
$country = $row['country'];
unset($result); 
ob_start();
require("../views/users.php");
$fillContent = ob_get_clean();
require_once("../views/site.php");
?>

