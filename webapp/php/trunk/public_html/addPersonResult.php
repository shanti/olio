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
        
require_once("../etc/config.php");
$connection = DBConnection::getWriteInstance();

// 1. Get data from submission page.
$username=$_POST['add_user_name'];
$pwd    =$_POST['psword'];
$summary=$_POST['summary'];
$fname=$_POST['first_name'];
$lname=$_POST['last_name'];
$email=$_POST['email'];
$strt1= $_POST['street1'];
$street2= $_POST['street2'];
$cty  = $_POST['city'];
$street1=str_replace(" ","+",$strt1);
$city = str_replace(" ","+",$cty);
$state=$_POST['state'];
$zip=$_POST['zip'];
$country=$_POST['country'];
$telephone=$_POST['telephone'];
$timezone=$_POST['timezone'];
$image_name= basename($_FILES['user_image']['name']);

// 2. Get coordinates of the address.
$geocode = new Geocoder($street1, $city, $state, $zip);

// 3. Insert address and get the address id, or update the address.
if(isset($_POST['addpersonsubmit'])) {

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

    // At least temporary place holder for the image.
    $modified_image_name = Olio::$config['includes'] . "userphotomissing.gif";
    $imagethumb = Olio::$config['includes'] . "userphotomissing.gif";

    $insertsql = "insert into PERSON (username, password, firstname, lastname,".
                 " email, telephone, imageurl, imagethumburl, summary,".
                 " timezone,"."ADDRESS_addressid) values('$username', '$pwd',".
                 " '$fname', '$lname', '$email', '$telephone',".
                 " '$modified_image_name', '$imagethumb', '$summary',".
                 " '$timezone', '$addrid')";
	$insertresult = $connection->exec($insertsql);
    $idres = $connection->query($cq);
    while ($idres->next()) {
        $userid = $idres->get(1);
    }

} else if (isset($_POST['addpersonsubmitupdate'])) {

 	if ($summary == "" ) {
		$sumquery = "select summary from PERSON where username='$username' ";
		$sumresult = $connection->query($sumquery);
		while ($sumresult->next()) {
			$summary = $sumresult->get(1);
		}
		unset($sumresult);
	}

	$insertaddr = "insert into ADDRESS (street1, street2, city, state, zip, country, latitude, longitude) ".
                      "values ('$strt1', '$street2', '$cty', '$state', '$zip', '$country', ".
                      "'$geocode->latitude', '$geocode->longitude')";
	$connection->exec($insertaddr);
	$cq = "select last_insert_id()";
	$idres = $connection->query($cq);
	while($idres->next()) {
            $addrid = $idres->get(1);
	}
	unset($idres);

	$updatesql ="update PERSON set password='$pwd', firstname='$fname', lastname='$lname', email='$email', telephone='$telephone',";
    if ($summary != "") {
        $updatesql .= " summary='$summary',";
    }
    $updatesql .= " timezone='$timezone', ADDRESS_addressid='$addrid' where username='$username' ";
	$connection->exec($updatesql);

    $userid_query = "select userid from PERSON where username='$username'";
    $idres = $connection->query($userid_query);
    while ($idres->next()) {
        $userid = $idres.get(1);
    }
    unset($idres);
}

// 4. End the DB transaction here.
$connection->commit();

if ($image_name != "") {
    // 5. Determine the image id.
    $pos=strpos($image_name,'.');
    $img_ext = substr($image_name,$pos,strlen($image_name));
    $modified_image_name = "P".$userid.$img_ext;
    $resourcedir = '/tmp/';
    $imagethumb = "P".$userid."T".$img_ext;
    $user_image_location = $resourcedir . $modified_image_name;
    if (!move_uploaded_file($_FILES['user_image']['tmp_name'], $user_image_location)) {
        throw new Exception("Error moving uploaded file to $user_image_location");
    }

    // 6. Generate the thumbnails.
    $thumb_location = $resourcedir . $imagethumb;
    ImageUtil::createThumb($user_image_location, $thumb_location, 120, 120);

    // 7. Store the image.
    $fs = FileSystem::getInstance();

    if (!$fs->create($user_image_location, "NO_OP", "NO_OP")) {
        error_log("Error copying image " . $user_image_location);
    }
    if (!$fs->create($thumb_location, "NO_OP", "NO_OP")) {
        error_log("Error copying thumb " . $thumb_location);
    }
    unlink($user_image_location);
    unlink($thumb_location);

    // 8. Update the DB.
    $updateimage = "update PERSON set imageurl = '$modified_image_name', ".
                   "imagethumburl = '$imagethumb' ".
                   "where userid = '$userid'";
    $updated = $connection->exec($updateimage);
}

header("Location:users.php?username=".$username);
?>
