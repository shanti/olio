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
 * This page is to collect the information of a new user. 
 * Once the user submits the page, user gets registered and will be directed to users.php page.
 *
 */

?>
<script src="js/validateform.js" type="text/javascript"></script>
<script src="js/httpobject.js" type="text/javascript"></script>

<h1>New user</h1>
<form name="addperson" action="addPersonResult.php" method="POST" enctype="multipart/form-data" onsubmit="return checkUserFields()" >
<fieldset id="user_form">
<legend>User Details</legend>
<!--[form:users]-->
<label for="add_user_name">Username</label>
<? echo '<input id="add_user_name" name="add_user_name" type="text" value="'.$un.'" size="30" onblur="checkUser();"/>'; ?>
<p id="usercheck"></p>
<br />

<label for="password">Password</label>
<? echo '<input id="psword" name="psword" size="30" type="password"  value="'.$pwd.'" />'; ?>
<br />

<label for="passwordx">Confirm Password</label>
<? echo '<input id="passwordx" name="passwordx" size="30" type="password" onblur="checkPwdMatch();" />'; ?>
<p id="checkpwdmatch"></p>
<br />

<label for="first_name">Firstname</label>
<? echo '<input id="first_name" name="first_name" value="'.$fn.'" size="30" type="text"  />'; ?>

<br />

<label for="last_name">Lastname</label>
<? echo '<input id="last_name" name="last_name" value="'.$ln.'" size="30" type="text"  />'; ?>

<br />

<label for="email">Email</label>
<? echo '<input id="email" name="email" size="30" type="text" value="'.$email.'" onblur="isValidEmail();" />'; ?>
<p id="isvalidemail"></p>
<br />

<label for="telephone">Telephone</label>
<? echo '<input id="telephone" name="telephone" size="30" type="text" value="'.$tele.'" onblur="isValidTelephone();" />'; ?>
<p id="isvalidtelephone"></p>
<br />

<label for="user_image">Image</label>
<input id="user_image" name="user_image" type="file"  />
<br />

<label for="summary">Summary</label>
<? echo '<textarea cols="40" id="summary" name="summary" rows="20" >'.$summary.'</textarea>'; ?>
<br />

<label for="timezone">Timezone</label>
<select id="timezone" name="timezone">
<? 
   readfile(Olio::$config['includes'] . "timezones.html"); 
   if(!is_null($uname)){
	 echo '<option selected="selected" value="'.$tz.'">'.$tz.'</option>';
   }
  
?>

</select>
<!--[eoform:users]-->
</fieldset>

<fieldset id="address_form">
<legend>Address</legend>
<!--[form:address]-->
<label for="street1">Street 1</label>
<? echo '<input id="street1" name="street1" value="'.$street1.'" size="30" type="text"  />'; ?> 
<br />

<label for="street2">Street 2</label>
<? echo '<input id="street2" name="street2" value="'.$street2.'" size="30" type="text"  />'; ?>
<br />

<label for="zip">Zip</label>
<? echo '<input id="zip" name="zip" size="30" type="text" value="'.$zip.'" onblur="isValidZip();fillCityState();" />'; ?>
<p id="isvalidzip"></p>
<br />

<label for="city">City</label>
<? echo '<input id="city" name="city" value="'.$city.'" size="30" type="text"  />'; ?>
<br />

<label for="state">State</label>
<? echo '<input id="state" name="state" value="'.$state.'" size="30" type="text"  />'; ?>
<br />

<label for="country">Country</label>
<select id="country" name="country">
<? 
  readfile(Olio::$config['includes'] . "countries.html");
if(!is_null($uname)){
  echo '<option selected="selected" value="'.$country.'">'.$country.'</option>';
}
?>
</select>
<br />
<!--[eoform:address]-->
</fieldset>
<?
if(is_null($uname)){?>
<input type="submit" value="Create" name="addpersonsubmit" />
<?}else{?>
<input type="submit" value="Update" name="addpersonsubmitupdate" />
<?}?>
<input type="reset" value="Reset" name="addpersonreset" />

</form>


      
      
