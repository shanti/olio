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
$se = $_REQUEST['id'];
$username = $HTTP_SESSION_VARS["uname"];
if(!is_null($username)){
    $deleteuser = "delete from PERSON_SOCIALEVENT where username='$username' and socialeventid='$se'";
    $connection->exec($deleteuser);
}
$listquery = "select username from PERSON_SOCIALEVENT where socialeventid = '$se'";
$listqueryresult = $connection->query($listquery);
$username = $HTTP_SESSION_VARS["uname"];
while($listqueryresult->next()) {
        $tmp_uname = $listqueryresult->get(1);
        $attendeeList = $attendeeList." ".'<a href="users.php?username='.$tmp_uname.'">'.$tmp_uname.'</a><br />';
}
$numofattendees = $HTTP_SESSION_VARS["numofattendees"] - 1;
$HTTP_SESSION_VARS["numofattendees"] = $numofattendees;
echo '<h2 class="smaller_heading">'.$numofattendees.' Attendees:</h2><br/><input name="attend" type="button" value="Attend" onclick="addAttendee();"/><br/><div id="attendees">'.$attendeeList.'</div>';
unset($listqueryresult);
?>
