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
    
session_start();    
require_once("../etc/config.php");
$connection = DBConnection::getWriteInstance();

// 1. Get data from submission page.
$description=$_POST['description'];
$summary = $_POST['summary'];
$title=$_POST['title'];
$strt1= $_POST['street1'];
$street2= $_POST['street2'];
$cty  = $_POST['city'];
$street1 = str_replace(" ","+",$strt1);
$city = str_replace(" ","+",$cty);
$state = $_POST['state'];
$zip = $_POST['zip'];
$country = $_POST['country'];
$telephone = $_POST['telephone'];
$year = $_POST['year'];
$month = $_POST['month'];
$day = $_POST['day'];
$hour = $_POST['hour'];
$minute = $_POST['minute'];
$eventtime=$year."-".$month."-".$day." ".$hour.":".$minute.":00";
$eventdate=$year."-".$month."-".$day;
$tags=$_POST['tags'];
//echo "Tags = ".$tags."<br/>";

$image_name= basename($_FILES['upload_image']['name']);
$literature_name=basename($_FILES['upload_literature']['name']);


// 2. Get coordinates of the address.
$geocode = new Geocoder($street1, $city, $state, $zip);


// 3. Insert address and get the address id.
$insertaddr = "insert into ADDRESS (street1, street2, city, state, zip, country, latitude, longitude) ".
                      "values ('$strt1', '$street2', '$cty', '$state', '$zip', '$country', ".
                      "'$geocode->latitude', '$geocode->longitude')";
$connection->beginTransaction();
$connection->exec($insertaddr);
$cq = "select last_insert_id()";
$idres = $connection->query($cq);
while($idres->next()) {
    $addrid = $idres->get(1);
}
unset($idres);

// 4. Insert event and get the event id.
$usrnm = $_SESSION["uname"];
$evid =  $_SESSION["addEventSE"];

if (isset($_POST['addeventsubmit'])) {
	$insertse = "insert into SOCIALEVENT (title, description,summary, submitterUserName, ADDRESS_addressid,telephone, timezone, eventtimestamp, eventdate) values ('$title', '$description','$summary', '$usrnm', '$addrid', '$telephone', '$timezone', '$eventtime', '$eventdate')";
    $connection->exec($insertse);
    $idres = $connection->query($cq);
    while ($idres->next()) {
        $eventid=$idres->get(1);
    }
    unset($idres);
}else if (isset($_POST['addeventsubmitupdate'])) {
    $updse = "update SOCIALEVENT set title='$title',description='$description',summary='$summary', submitterUserName='$usrnm',ADDRESS_addressid='$addrid',telephone='$telephone',timezone='$timezone',eventtimestamp='$eventtime',eventdate='$eventdate' where socialeventid = '$evid'";
    $upd = $connection->exec($updse);
    if ($upd != 1)
    throw new Exception("Error updating event with image locations. Update returned $updated!");

}

// 5. Check tags. Insert if not available and get id, then insert relationship.
$tagList = preg_split("/[\s,]+/", trim($tags));
//echo "TagList = ".$tagList."<br/>";

// We need to sort the tags before insert/update. Different tag sequences
// can lead to deadlocks.
sort($tagList);

foreach ($tagList as $tag) {
    if (isset($_POST['addeventsubmit'])) {
        //echo "in foreach tag=" .$tag."<br/>";
        // Try to update first.
        $updatetagcount="update SOCIALEVENTTAG set refcount = refcount + 1 where tag='$tag'";
        $count = $connection->exec($updatetagcount);
        //echo "count=".$count."<br/>";
        if ($count == 0) { // Update did not find the tag, so we insert.
            $inserttag="insert into SOCIALEVENTTAG (tag,refcount) values ('$tag',1)";
            $inserttagresult = $connection->exec($inserttag);
            $idres = $connection->query($cq);
            while ($idres->next()) {
                $tagid = $idres->get(1);
            }
            unset($idres);
        } else { // Even if we update, we still need the tagid.
            $checktag = "select socialeventtagid from SOCIALEVENTTAG where tag='$tag'";
            $checktagresult = $connection->query($checktag);
            $rowsFound = false;
            while ($checktagresult->next()) {
                $rowsFound = true;
                $tagid = $checktagresult->get(1);
            }
            unset($checktagresult);
        }
        // Now, insert relationship.
        $inserttagid = "insert into SOCIALEVENTTAG_SOCIALEVENT (socialeventtagid, socialeventid) ".
                           "values ('$tagid', '$eventid')";
        $connection->exec($inserttagid);
    }

    if (isset($_POST['addeventsubmitupdate'])) {
        //echo "in foreach tag=" .$tag."<br/>";
        $checktag = "select socialeventtagid from SOCIALEVENTTAG where tag='$tag'";
        $checktagresult = $connection->query($checktag);
        $rowsFound = false;
        while ($checktagresult->next()) {
            $rowsFound = true;
            $tagid = $checktagresult->get(1);
        }
        unset($checktagresult);
        if(!$rowsFound){
            $inserttag="insert into SOCIALEVENTTAG (tag,refcount) values ('$tag',1)";
            $inserttagresult = $connection->exec($inserttag);
            $idres = $connection->query($cq);
            while ($idres->next()) {
                $tagid = $idres->get(1);
            }
            unset($idres);
            $inserttagid = "insert into SOCIALEVENTTAG_SOCIALEVENT (socialeventtagid, socialeventid) ".
                           "values ('$tagid', '$evid')";
            $connection->exec($inserttagid);
        }
    }
}

// 6. Insert submitter to the event attendee list.
if (isset($_POST['addeventsubmit'])) {
    $insertPS = "insert into PERSON_SOCIALEVENT values('$usrnm','$eventid')";
    $connection->exec($insertPS);
}

// 7. Determine image and thumbnail file names.
$default_image = false;
if ($image_name != "") {
    $pos=strpos($image_name,'.');
    $img_ext = substr($image_name,$pos,strlen($image_name));
    if (isset($_POST['addeventsubmit'])) {
        $modified_image_name = "E".$eventid.$img_ext;
    }else if (isset($_POST['addeventsubmitupdate'])) {
        $modified_image_name = "E".$evid.$img_ext;
    }
    if (isset($_POST['addeventsubmit'])) {
        $imagethumb = "E".$eventid."T".$img_ext;
    }else if (isset($_POST['addeventsubmitupdate'])) {
        $imagethumb = "E".$evid."T".$img_ext;
    }
} else {
    if (isset($_POST['addeventsubmit'])) {
        $default_image = true;
        $modified_image_name = "";
        $imagethumb = "";
    }else if (isset($_POST['addeventsubmitupdate'])) {
        $imgq = "select imageurl,imagethumburl from SOCIALEVENT where socialeventid='$evid'";
        $imgqresult = $connection->query($imgq);
        while ($imgqresult->next()) {
            $modified_image_name = $imgqresult->get(1);
            $imagethumb = $imgqresult->get(2);
        }
        unset($imgqresult);
    }
}

// 8. Determine literature file names.
$default_literature = false;
if ($literature_name != "") {
    $pos=strpos($literature_name,'.');
    $lit_ext = substr($literature_name,$pos,strlen($literature_name));
    if (isset($_POST['addeventsubmit'])) {
        $modified_literature_name="E".$eventid."L".$lit_ext;
    }else if (isset($_POST['addeventsubmitupdate'])) {
        $modified_literature_name="E".$evid."L".$lit_ext;
    }
} else {
    if (isset($_POST['addeventsubmit'])) {
        $default_literature = true;
        $modified_literature_name="";
    }else if (isset($_POST['addeventsubmitupdate'])) {
        $litq = "select literatureurl from SOCIALEVENT where socialeventid='$evid'";
        $litqresult = $connection->query($litq);
        while ($litqresult->next()) {
            $modified_literature_name=$litqresult->get(1);
        }
        unset($litqresult);
    }
}

// We end the DB transaction here.
$connection->commit();

// 9. Generate thumbnail and save images to file storage (outside tx)
if ($image_name != "") {
    $resourcedir = '/tmp/';
    $user_image_location = $resourcedir . $modified_image_name;
    if (!move_uploaded_file($_FILES['upload_image']['tmp_name'], $user_image_location)) {
        throw new Exception("Error moving uploaded file to $modified_image_name");
    }
    $thumb_location = $resourcedir . $imagethumb;
    ImageUtil::createThumb($user_image_location, $thumb_location, 120, 120);
    if (!isset($fs))
    $fs = FileSystem::getInstance();
    if (!$fs->create($user_image_location, "NO_OP", "NO_OP")) {
        error_log("Error copying image " . $user_image_location);
    }
    if (!$fs->create($thumb_location, "NO_OP", "NO_OP")) {
        error_log("Error copying thumb " . $thumb_location);
    }
    unlink($user_image_location);
    unlink($thumb_location);
}

// 10. Save literature file to storage
if ($literature_name != "") {
    $lit_resourcedir = '/tmp/';
    $upload_literature_location = $lit_resourcedir . $modified_literature_name;
    if (!move_uploaded_file($_FILES['upload_literature']['tmp_name'], $upload_literature_location)) {
        throw new Exception("Error moving uploaded file to $upload_literature_location");
    }
    if (!isset($fs))
    $fs = FileSystem::getInstance();
    if (!$fs->create($upload_literature_location, "NO_OP", "NO_OP")) {
        error_log("Error copying literature " . $upload_literature_location);
    }
    unlink($upload_literature_location);
}


// 11. Update the image names back to the database.
// Note: this update is in it's own transaction, after the images are
// properly stored. It is a single statement transaction and with autocommit
// on, we do not need to start and commit.
if (isset($_POST['addeventsubmit'])) {
    $updatese = "update SOCIALEVENT set imageurl = '$modified_image_name', ".
                    "imagethumburl = '$imagethumb', ".
                    "literatureurl = '$modified_literature_name' ".
                    "where socialeventid = '$eventid'";
} else if (isset($_POST['addeventsubmitupdate'])) {
    //echo "imageurl = ".$modified_image_name;
    //echo "imagethumburl =".$imagethumb;
    //echo "literatureurl =".$modified_literature_name;
    //echo "evid =".$evid;
    $updatese = "update SOCIALEVENT set imageurl = '$modified_image_name', ".
                    "imagethumburl = '$imagethumb', ".
                    "literatureurl = '$modified_literature_name' ".
                    "where socialeventid = '$evid'";
}

$updated = $connection->exec($updatese);

// 12. Redirect the results.
if (isset($_POST['addeventsubmit'])) {
    header("Location:events.php?socialEventID=".$eventid);
}else if (isset($_POST['addeventsubmitupdate'])) {
    header("Location:events.php?socialEventID=".$evid);
}
?>
