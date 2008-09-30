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
$eventlist = Events_Controller::getInstance();
$tagcloud = Tags_Controller::getInstance();
$url = RequestUrl::getInstance();
$signedinuser = $HTTP_SESSION_VARS["uname"];
$page= $_REQUEST['page'];
$flag = $_REQUEST['flag'];

$href = $url->getGetRequest();
if(!is_null($page)){
$href = substr($href, 0, strrpos($href,"&"));
}

if($href==""){
$href = "?";
}

if(!is_null($page)){
    $eventdate=$HTTP_SESSION_VARS["eventdate"];
    $zipcode=$HTTP_SESSION_VARS["zipcode"];
    $order=$HTTP_SESSION_VARS["order"];
    $numPages  =$HTTP_SESSION_VARS["numPages"];
    $HTTP_SESSION_VARS["currentpage"]=$page;
    $curr_page = $HTTP_SESSION_VARS["currentpage"];
    $prev_page = $HTTP_SESSION_VARS["currentpage"] - 1;
    $next_page = $HTTP_SESSION_VARS["currentpage"] + 1;
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
    $zipcode = $_REQUEST['zipcode'];
    $order = $_REQUEST['order'];
    $m= $_REQUEST['month'];
    $d= $_REQUEST['day'];
    $y= $_REQUEST['year'];

    if(!is_null($_REQUEST['month']) && !is_null($_REQUEST['day'])  && !is_null($_REQUEST['year']) ){
    $eventdate= $y."-".$m."-".$d;
    }
    $HTTP_SESSION_VARS["eventdate"]= $eventdate;
    $HTTP_SESSION_VARS["zipcode"] = $zipcode;
    $HTTP_SESSION_VARS["order"] = $order;
    $numPages  = $eventlist->getNumPages($zipcode,$eventdate,$connection);
    $HTTP_SESSION_VARS["numPages"] = $numPages;

    $prev_page = 1;
    $next_page = 2;
    $curr_page = 1;
    $offset = 0;
    session_unregister ("currentpage");
}
$indexEvents = $eventlist->getIndexEvents($zipcode,$order,$eventdate,$offset,null,$signedinuser,$connection);

ob_start();
require("../views/paginate.php");
$paginateView = ob_get_clean();

ob_start();
require("../views/index.php");
$fillContent = ob_get_clean();
if($flag == "authenticated"){
$fillMessage ="<font color=green>Successfully logged in!</font>";
}
require_once("../views/site.php");
?>
