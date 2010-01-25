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
 * This page is to collect the details of the event. 
 * The uploaded literature file and the image are stored in LocalFS storage. 
 * All the file types are handled in a file called fileService.php. 
 * If you want to open any file, you just need to call this and pass the file name. 
 * Once you submit this page, the event gets added and will be directed to the home page. 
 * 
 */
?>
<script src="js/validateform.js" type="text/javascript"></script>
<script src="js/httpobject.js" type="text/javascript"></script>
<h1>New event</h1>
<form name="addEvent" action="addEventResult.php" method="POST" enctype="multipart/form-data" onsubmit="return checkEventFields()">
<fieldset id="event_form">
    <legend>Event Details</legend>
    <div id="basic_event_form">
      <p><label for="title">Title</label><br/>
      <? echo '<input id="title" name="title" size="30" type="text" value="'.$title.'" /></p>'; ?>
      <p><label for="summary">Summary</label><br />
      <? echo '<textarea cols="37" id="summary" name="summary" rows="20" >'.$summary.'</textarea></p>'; ?>
      <p><label for="description">Description</label><br/>
      <? echo '<textarea cols="37" id="description" name="description" rows="20" >'.$description.'</textarea></p>'; ?>
    </div>

    <div id="misc_event_form">
      <p>
      <label for="event_timestamp">Event date and time</label><br/>
      <select id="year" name="year">
      <?  
          require(Olio::$config['includes'] . "year.php"); 
      ?>
      </select>
      <select id="month" name="month">
      <? 
            require(Olio::$config['includes'] . "month.php"); 
      ?>
      </select>
      <select id="day" name="day">
      <? 
            require(Olio::$config['includes'] . "day.php"); 
      ?>
      </select>
      &mdash;<br/> 
      <select id="hour" name="hour">
      <? 
            require(Olio::$config['includes'] . "hour.php"); 
      ?>
      </select>
      : <select id="minute" name="minute">
      <? 
            require(Olio::$config['includes'] . "minute.php"); 
      ?>
      </select>
      </p>

      <p>
      <label for="telephone">Telephone</label><br/>
      <? echo '<input id="telephone" name="telephone" size="30" type="text" value="'.$telephone.'" onblur="isValidTelephone();" />'; ?>
      <p id="isvalidtelephone"></p>
      </p>
  
      <br /><hr /><br />
  
      <p>
      <label for="upload_image">Image</label><br/>
      <? echo '<input name="upload_image" id="upload_image" type="file" />'; ?>
      </p>

      <p>
      <label for="upload_literature">
  		Document <sup><em>(only PDF, Word, and plain text documents)</em></sup>
      </label><br/>
      <? echo '<input name="upload_literature" id="upload_literature" type="file" />'; ?>
      </p>

      <p><label for="tags">Tags</label><br/>
      <? echo '<input id="tags" name="tags" size="40" type="text" value="'.$tags.'"/>'; ?>
      </p>
    </div>
</fieldset>

<fieldset id="address_form">
<legend>Address</legend>
    <label for="street1">Street 1</label>
    <? echo '<input id="street1" name="street1" size="30" type="text" value="'.$street1.'" /><br />'; ?>

    <label for="street2">Street 2</label>
    <? echo '<input id="street2" name="street2" size="30" type="text" value="'.$street2.'" /><br />'; ?>

    <label for="zip">Zip</label>
    <? echo '<input id="zip" name="zip" size="30" type="text" value="'.$zip.'" onblur="isValidZip();fillCityState();" /><br />'; ?>
    <p id="isvalidzip"></p>

    <label for="city">City</label>
    <? echo '<input id="city" name="city" size="30" type="text" value="'.$city.'" /><br />'; ?>

    <label for="state">State</label>
    <? echo '<input id="state" name="state" size="30" type="text" value="'.$state.'" /><br />'; ?>

    <label for="country">Country</label>
    <select id="country" name="country">
    <?  
          readfile(Olio::$config['includes'] . "countries.html");
        if(!is_null($se)){
          echo '<option selected="selected" value="'.$country.'">'.$country.'</option>';
        }
    ?>
    </select><br />
</fieldset>
<div class="clr"></div>
<?if(is_null($se)){?>
<input type="submit" value="Create" name="addeventsubmit" />
<?}else{?>
<input type="submit" value="Update" name="addeventsubmitupdate"/>
<?}?>
<input type='reset' value='Reset' name="addeventreset" />
</form>
