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
    
require_once("../etc/config.php");
$conn = DBConnection::getInstance();

$isUseravailable = $_REQUEST['user'];
$checkun = " select 'x' from PERSON where username='$isUseravailable' ";
$res = $conn->query($checkun);
if ($res->next()) {
    echo "<span style='color:red'><small>".$isUseravailable." already in use.....please try another username.</small></span></br> ";
} else {
    echo "<span style='color:red'><small>Congratulations!! ".$isUseravailable." is available. </small></span></br>";
}
?>
