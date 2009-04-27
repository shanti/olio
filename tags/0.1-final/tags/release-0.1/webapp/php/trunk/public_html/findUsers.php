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
$connection = DBConnection::getInstance();
$isFriend = Users_Controller::getInstance();
$user = $_REQUEST['query'];
$reqUser = $_REQUEST['reqUser'];
$flag = $_REQUEST['flag'];
$loggedinuser = $_SESSION["uname"];
if(!is_null($user) && !empty($user)){
$users=$isFriend->findUser($user,$loggedinuser,$connection);
}
ob_start();
require("../views/findUsers.php");
$fillContent = ob_get_clean();
if($flag == "add"){
$fillMessage = "<font color=green>Friendship requested</font>";
}else if($flag == "delete"){
$fillMessage ="<font color=green>You have revoked your friendship request to ".$reqUser."</font>";
}
require_once("../views/site.php");
?>
