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
$se= $_REQUEST['socialEventID'];
$comments = $_POST['comments'];
$cid = $_POST['editingcid'];
$editcomments = $_POST['editcomments'];
$tagslist = Tags_Controller::getInstance();
$events = Events_Controller::getInstance();
$username = $_SESSION["uname"];
$dateFormat = "l,  F j,  Y,  h:i A";
$txBegun = false;
if (isset($_POST['commentsratingsubmit']) ||
    (isset($_POST['editcommentsratingsubmit']) && isset($_POST['editingcid']))) {
    $connection = DBConnection::getWriteInstance();
    $connection->beginTransaction();
    $txBegun = true;
} else {
    $connection = DBConnection::getInstance();
}
$eventTaglist = $tagslist->getEventsPageTagCloud($connection,$se);
$numAttendees = $events->getNumAttendees($se,$connection);
$_SESSION["numofattendees"] = $numAttendees;
$rating = $_SESSION["rating"];
$query = "select title,description,submitterUserName,imagethumburl," .
                 "literatureurl,telephone,timezone,eventtimestamp,street1," .
                 "street2,city,state,zip,country,latitude,longitude,summary " .
                 "from SOCIALEVENT as se,ADDRESS as a where " .
                 "se.socialeventid = '$se' and se.ADDRESS_addressid=a.addressid";
        $result = $connection->query($query);

        // see if any rows were returned
        if ($result->next()) {
                        $x = trim($result->get(15));
                        $y = trim($result->get(16));
                        $title =trim($result->get(1));
                        $description = trim($result->get(2));
                        $submitter=$result->get(3);
                        $telephone = $result->get(6);
                        $street1 = $result->get(9);
                        $street2 = $result->get(10);
                        $city = $result->get(11);
                        $state = $result->get(12);
                        $zip = $result->get(13);
                        $country = $result->get(14);
                        $image = $result->get(4);
                        $eventDateTime = trim($result->get(8));
                        $eventTimestamp = trim($events->formatdatetime($dateFormat,$eventDateTime));
                        $literature =  $result->get(5);
                        $summary = $result->get(17);
                        $address="".$result->get(9)." ".$result->get(10).",".
                         $result->get(11).",".$result->get(12).",".
                         $result->get(13).",".$result->get(14);
        }
unset($result);
if (isset($_SESSION["uname"])) {
    // Ensure our user name comes in first, if already attending.
    $listquery = "select username from PERSON_SOCIALEVENT ".
                 "where socialeventid = '$se' and username = '$username' ".
                 "union select username from PERSON_SOCIALEVENT ".
                 "where socialeventid = '$se' limit 20";

} else {
    $listquery = "select username from PERSON_SOCIALEVENT ".
                 "where socialeventid = '$se' limit 20";
}
$listqueryresult = $connection->query($listquery);
while($listqueryresult->next()) {
        $tmp_uname = $listqueryresult->get(1);
        if (!isset($_SESSION["uname"]) && $tmp_uname == $username) {
                $unattend = true; // show unattend button if user is already registered.
        }
        $attendeeList = $attendeeList." ".'<a href="users.php?username='.$tmp_uname.'">'.$tmp_uname.'</a><br />';

}
unset($listqueryresult);

if (isset($_POST['commentsratingsubmit'])) {
	 $insertSql = "insert into COMMENTS_RATING (username,socialeventid,comments,ratings) values ('$username','$se','$comments','$rating')";
        $connection->exec($insertSql);
} else if (isset($_POST['editcommentsratingsubmit']) && isset($_POST['editingcid'])) {
	 $updateSql = "update COMMENTS_RATING set comments='$editcomments', ratings='$rating' where username='$username' and socialeventid='$se' and commentid='$cid'";
         $connection->exec($updateSql);
}
$commentsratingSql = "select commentid,username,comments,ratings,created_at,updated_at from COMMENTS_RATING where socialeventid ='$se'";
$commentsratingResult=$connection->query($commentsratingSql);
ob_start();
while($row1 = $commentsratingResult->getArray()) {
$tmp_commentid = $row1['commentid'];
$tmp_uname = $row1['username'];
$tmp_uname_comments = $row1['comments'];
$tmp_uname_rating = $row1['ratings'];
$tmp_uname_created_at = trim($events->formatdatetime($dateFormat,$row1['created_at'])); 
$tmp_uname_updated_at = trim($events->formatdatetime($dateFormat,$row1['updated_at']));
require("../views/commentsRating.php");
}
unset($commentsratingResult);

if ($txBegun) {
    $connection->commit();
}

$eventCommentsRating = ob_get_contents();
ob_end_clean();
ob_start();
require("../views/events.php");
$fillContent = ob_get_clean();
require_once("../views/site.php");
?>
