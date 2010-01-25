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
*/
session_start();
require_once("../etc/config.php");
$se = $_REQUEST['socialEventID'];
$_SESSION["addEventSE"]=$se;
$connection = DBConnection::getInstance();
if(!is_null($se)){
    $q = "select title,description,summary,imageurl,literatureurl,telephone,timezone,eventtimestamp,submitterusername,street1,street2,city,state,zip,country from SOCIALEVENT as s,ADDRESS as a where s.socialeventid='$se' and s.ADDRESS_addressid=a.addressid";
    $result = $connection->query($q);
    $row = $result->getArray();
    $title = $row['title'];
    $description = $row['description'];
    $summary = $row['summary'];
    $image=$row['imageurl'];
    $literature=$row['literatureurl'];
    $telephone=$row['telephone'];
    $tz=$row['timezone'];
    $submitter=$row['submitterusername'];
    $eventdate=$row['eventtimestamp'];
    $year = substr($eventdate,0,4);
    $month = substr($eventdate,5,2);
    $day = substr($eventdate,8,2);
    $hour = substr($eventdate,11,2);
    $minute = substr($eventdate,14,2);
    $street1=$row['street1'];
    $street2=$row['street2'];
    $city=$row['city'];
    $state=$row['state'];
    $zip=$row['zip'];
    $country=$row['country'];
    unset($result);
    $q1="select tag from SOCIALEVENTTAG as st, SOCIALEVENTTAG_SOCIALEVENT as sst where sst.socialeventid='$se' and sst.socialeventtagid=st.socialeventtagid order by tag ASC";
    $result1 = $connection->query($q1);
    while($row1 = $result1->getArray()) {
        $tg = $row1['tag'];
        $tags = $tags." ".$tg;
    }
    unset($result1);
}
if(!is_null($se) && (is_null($_SESSION["uname"]) || !($_SESSION["uname"]==$submitter) )){
    $fillMessage = "<font color=red>You can only edit events you created.</font> ";
}else{
    ob_start();
    require("../views/addEvent.php");
    $fillContent = ob_get_clean();
}
require_once("../views/site.php");
?>
