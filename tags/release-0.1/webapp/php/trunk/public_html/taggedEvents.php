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
$connection = DBConnection::getInstance();
$eventlist = Events_Controller::getInstance();
$url = RequestUrl::getInstance();
$tag=$_REQUEST['tag'];
$page= $_REQUEST['page'];
$dateFormat = "l,  F j,  Y,  h:i A";
$signedinuser=$_SESSION["uname"];

$href = $url->getGetRequest();
if(!is_null($page)){
  $href = substr($href, 0, strrpos($href,"&"));
}

if(!is_null($page)){
    $numPages  =$_SESSION["numPages"];
    $_SESSION["currentpage"]=$page;
    $curr_page = $_SESSION["currentpage"];
    $prev_page = $_SESSION["currentpage"] - 1;
    $next_page = $_SESSION["currentpage"] + 1;
    $offset = ($page * 10) - 10;
    if($offset < 0) {
    $offset = 0;
    }
    if($prev_page < 0) {
    $prev_page = 1;
    }
    if($next_page >  $numPages) {
    $next_page = $numPages;
    }
}else{
    if(!is_null($_REQUEST['count'])){
        $count=$_REQUEST['count'];
    }else{
        $query = "select refcount from SOCIALEVENTTAG where tag = '$tag'";
        $result = $connection->query($query);
        $row = $result->getArray();
        $count = $row['refcount'];
        unset($result);
    }    
    $numPages  = ceil($count / 10);;
    $_SESSION["numPages"] = $numPages;
    $prev_page = 1;
    $next_page = 2;
    $curr_page = 1;
    $offset = 0;
    session_unregister ("currentpage");
}

ob_start();
// $query = "select socialeventid from SOCIALEVENTTAG_SOCIALEVENT where socialeventtagid=(select socialeventtagid from SOCIALEVENTTAG where tag='$tag') limit $offset,10";
// Try out this query instead.
$query = "select ss.socialeventid ".
         "from SOCIALEVENTTAG s join SOCIALEVENTTAG_SOCIALEVENT ss ".
         "on s.socialeventtagid = ss.socialeventtagid ".
         "where s.tag = '$tag' limit $offset, 10";

$result = $connection->query($query);
$found = false;
while ($result->next()) {
        $found = true;
        $eventId[] = $result->get(1);
}
unset($result);
if (!$found) {
        echo "No rows found!";
} else {
        foreach ($eventId as $seid) {
            echo $ievents = $eventlist->getIndexEvents(null,null,null,null,$seid,$signedinuser,$connection);
        }
}
$indexEvents = ob_get_clean();

ob_start();
require("../views/paginate.php");
$paginateView = ob_get_clean();

ob_start();
require("../views/taggedEvents.php");
$fillContent = ob_get_clean();
require_once("../views/site.php");
?>
