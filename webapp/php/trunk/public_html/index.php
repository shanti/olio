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
require("../etc/config.php");

$signedinuser = $_SESSION["uname"];
$page= $_REQUEST['page'];
$flag = $_REQUEST['flag'];

$url = RequestUrl::getInstance();

$href = $url->getGetRequest();
if(!is_null($page)) {
    $href = substr($href, 0, strrpos($href,"&"));
} // else {
  //  error_log('$page is null.',0);
  //}

if($href=="") {
    $href = "?";
}

if(!is_null($page)){
    $eventdate=$_SESSION["eventdate"];
    $zipcode=$_SESSION["zipcode"];
    $order=$_SESSION["order"];
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
    $cacheType = 0;
} else {
    // error_log('$page is still null.',0);
    $zipcode = $_REQUEST['zipcode'];
    $order = $_REQUEST['order'];
    $m= $_REQUEST['month'];
    $d= $_REQUEST['day'];
    $y= $_REQUEST['year'];

    if(!is_null($_REQUEST['month']) && !is_null($_REQUEST['day'])  &&
            !is_null($_REQUEST['year'])) {
        $eventdate= $y."-".$m."-".$d;
    }    
    if (is_null($zipcode) && is_null($order) &&
            !isset($eventdate)) {
        //if (is_null($signedinuser)) { // Get whole page if not logged in...
        if($_SESSION["uname"] == ''){
            $cacheType = 2;
        } else { // And just the page content if logged in.
            $cacheType = 1;
        }
    } else {
        $_SESSION["eventdate"]= $eventdate;
        $_SESSION["zipcode"] = $zipcode;
        $_SESSION["order"] = $order;

        $connection = DBConnection::getInstance();
        $eventlist = Events_Controller::getInstance();
        $numPages  = $eventlist->getNumPages($zipcode,$eventdate,$connection);
        $tagcloud = Tags_Controller::getInstance();
        $_SESSION["numPages"] = $numPages;
        // error_log("numPages = $numPages",0);
        $cacheType = 0;
    }
    $prev_page = 1;
    $next_page = 2;
    $curr_page = 1;
    $offset = 0;
    session_unregister ("currentpage");
}

/*
 * In order to ensure that we do not loop indefinately we jimmy the $cacheType
 * if the cache is turned off.
 */
if(!CacheSystem::isCachingActive())
{
    $cacheType = 0;
}
switch ($cacheType) {
    case 0: noCachePage(); break;
    case 1: contentCachePage(); break;
    case 2: fullCachePage();
}


function fullCachePage() {
    // error_log('fullCachePage Called',0);
    global $signedinuser, $page, $flag, $url, $href, $eventdate, $zipcode;
    global $order, $numPages, $curr_page, $prev_page, $next_page, $offset;

    // error_log('Accesssing Cache subsystem to collect the page if possible.',0);
    $cache = CacheSystem::getInstance();
    $pageContent = $cache->get('Home');
    // error_log("Checking the Cache status for logged in and normal Home pages",0);
    if ($pageContent != '') {
        echo $pageContent;
        // error_log("Cache hit for Home page...",0);
    }

    $needsRefresh = false;
    for (;;) {
        // error_log('refresh logic accessed.');
        if ($cache->needsRefresh('Home')) {
            $needsRefresh = true;
            // error_log('Home needs refresh.');
            break;
        } else if ($pageContent == '') {
            error_log('index.php waiting for cache.');
            usleep(200000);
            $pageContent = $cache->get('Home');
            if ($pageContent != '') {
                echo $pageContent;
                break;
            } 
        } else {
            // error_log('.');
            break;
        }
    }

    if ($needsRefresh) {
        // error_log("Regenerating page................");
        $connection = DBConnection::getInstance();
        $eventlist = Events_Controller::getInstance();
        $numPages  = $eventlist->getNumPages($zipcode,$eventdate,$connection);
        $indexEvents = $eventlist->getIndexEvents($zipcode,$order,$eventdate,
                $offset,null,$signedinuser,$connection);
        $tagcloud = Tags_Controller::getInstance();

        ob_start();
        require("../views/paginate.php");
        $paginateView = ob_get_clean();

        ob_start();
        require("../views/index.php");
        $fillContent = ob_get_clean();

        ob_start();

        require("../views/site.php");

        // error_log('refreshing cache contents',0);
        $newPageContent = ob_get_contents();
        $cache->set('Home', $newPageContent, 0, 0);
        $cache->set('HomeContent', $fillContent, 0, 0);
        $cache->doneRefresh('Home', 300);

        if ($pageContent == '') { // Not displayed yet, just display now.
            // error_log("Display newly generated page");
            ob_end_flush();
        } else {
            // error_log("Display of cache generated page has occured");
            ob_end_clean(); // Otherwise just don't display the new one.
        }
    }
}

function contentCachePage() {
    // error_log('ContentCachePage Called',0);
    global $signedinuser, $page, $flag, $url, $href, $eventdate, $zipcode;
    global $order, $numPages, $curr_page, $prev_page, $next_page, $offset;

    $cache = CacheSystem::getInstance();

    for (;;) {
        $fillContent = $cache->get('HomeContent');
        if ($fillContent != '') {
            break;
        }
        usleep(20000);
        error_log('Retry loading fillContent from cache.');
    }

    if ($flag == "authenticated") {
        $fillMessage ="<font color=green>Successfully logged in!</font>";
    }
    require("../views/site.php");
}

function noCachePage() {
    // error_log('noCachePage Called',0);
    global $signedinuser, $page, $flag, $url, $href, $eventdate, $zipcode;
    global $order, $numPages, $curr_page, $prev_page, $next_page, $offset;

    $connection = DBConnection::getInstance();
    $eventlist = Events_Controller::getInstance();
    $tagcloud = Tags_Controller::getInstance();

    if (!is_null($page)) {
        $numPages  = $eventlist->getNumPages($zipcode,$eventdate,$connection);
    }
    $indexEvents = $eventlist->getIndexEvents($zipcode,$order,$eventdate,
                                        $offset,null,$signedinuser,$connection);

    ob_start();
    require("../views/paginate.php");
    $paginateView = ob_get_clean();

    ob_start();
    require("../views/index.php");
    $fillContent = ob_get_clean();

    if($flag == "authenticated") {
        $fillMessage ="<font color=green>Successfully logged in!</font>";
    }

    require("../views/site.php");
}
?>
