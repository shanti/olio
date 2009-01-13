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
    
session_start();
require_once("../etc/config.php");
$connection = DBConnection::getWriteInstance();
$friends = Users_Controller::getInstance();
$person = $_REQUEST['person'];
$friend = $_REQUEST['friend'];
$frnd = $_REQUEST['frnd'];
$user = $_REQUEST['query'];
$flag = $_REQUEST['flag'];
if ($flag == "add"){
$sql = "insert into PERSON_PERSON (person_username,friends_username,is_accepted) values ('$friend','$person',0)";
}else if ($flag == "delete"){
$sql = "delete from PERSON_PERSON where person_username='$friend' and friends_username='$person' and is_accepted=0";
}else if($flag == "frnd"){
$sql = "delete from PERSON_PERSON where person_username='$person' and friends_username='$friend' and is_accepted=1";
}
$connection->exec($sql);
if($flag == "frnd"){
header("Location:friends.php?username=$person&flag=$flag&reqUser=$friend");
}else{
header("Location:findUsers.php?query=$user&flag=$flag&reqUser=$friend");
}
?>
