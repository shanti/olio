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
        
/*
 * PHP Template.
 * Author: Sheetal Patil. Sun Microsystems, Inc.
 * 
 * login.php processes the login request and forwards to the home page.
 *
 */

session_start();
require_once("../etc/config.php");

$connection = DBConnection::getInstance();
$register = Users_Controller::getInstance();

if ($_POST['submit'] == "Login") {
    $un=$_POST['user_name'];
    $pwd=$_POST['password'];
    $result = $register->authenticate($un,$pwd,$connection);
    if ($result->next()) {
          session_register("uname");
          $uname=$un;
          $sid=session_id();
          $_SESSION["uname"]=$uname;
          $_SESSION["sid"]=$sid;
          $success="authenticated";
    }
    unset($result);
    $numFriendshipReq = $register->numFriendshipRequests($un,$connection);
    $_SESSION["friendshipreqs"] = $numFriendshipReq;

}
if(!is_null($success)){
header("Location:index.php?flag=".$success);
}else{
header("Location:index.php");
}

?>
