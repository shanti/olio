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
 * This describes the details of an event.
 * 
 */
?>
<script src="js/starrating.js" type="text/javascript"></script>
<script src="js/httpobject.js" type="text/javascript"></script>
<script type="text/javascript" src="http://api.maps.yahoo.com/ajaxymap?v=3.0&appid=com.sun.javaee.blueprints.components.ui.geocoder"></script>
<script type="text/javascript">
    var addAttendeelink = "addAttendee.php?id=<?php echo $se;?>";
    function handleAttendee() {
         if (http.readyState == 4) {
             result = http.responseText;
             document.getElementById("event_attendees").innerHTML=result;
         }
    }
    function addAttendee() {
         http.open("GET", addAttendeelink, true);
         http.onreadystatechange = handleAttendee;
         http.send(null);
    }
    var deleteAttendeelink = "deleteAttendee.php?id=<?php echo $se;?>";
    function deleteAttendee() {
         http.open("GET", deleteAttendeelink, true);
         http.onreadystatechange = handleAttendee;
         http.send(null);
    }
    var deleteCRlink = "deleteCommentsRating.php?commentid=";
    function handleCR() {
         if (http.readyState == 4) {
             result = http.responseText;
             document.getElementById("comment_list").innerHTML=result;
         }
    }
    function deleteCR(commentid,se) {
         http.open("GET", deleteCRlink + escape(commentid) + "&socialEventID=" + escape(se), true);
         http.onreadystatechange = handleCR;
         http.send(null);
    }
</script>

<div id="event_header">
  <div id="event_thumbnail">
    <img src="fileService.php?cache=false&file=<?=$image?>" height=150px width=150px /></div>

  <div id="main_event_details">
    <h1 class="inline"><?=$title ?></h1>
<? if ($submitter == $_SESSION["uname"]) { ?>
            <form method="post" action="addEvent.php?socialEventID=<?echo $se;?>" class="button-to">
            <div>
            <input type="submit" value="Edit" />
            </div>
            </form> 
            <form method="post" action="deleteEvent.php?socialEventID=<?echo $se;?>" class="button-to">
            <div>
            <input onclick="return confirm('Are you sure?');" type="submit" value="Delete" />
            </div>
            </form>
    <?}?>

    <hr />
    <?=$eventTimestamp?><br />
    <br />
    <? if(!$literature == "") {
        echo '<a href="fileService.php?file=' . $literature . '">Event Literature</a>';
    }?>
    <br />
    <div id="event_address">
      <?=$street1." ".$street2; ?><br />
      <?=$city.",".$state." ".$zip; ?><br />
      <?=$country; ?>
    </div>
    Contact: <span id="event_contact"><?=$telephone ?></span><br />
  </div>
  <div class="clr"></div>
</div>

<div id="event_attendees">
        <h2 class="smaller_heading"><?= $numAttendees?> Attendees:</h2><br/>
<? if ($unattend) { ?>
<input name="unattend" type="button" value="Unattend" onclick="deleteAttendee();"/>
<? } ?>
<? if (!$unattend && !is_null($_SESSION["uname"])) { ?>
<input name="attend" type="button" value="Attend" onclick="addAttendee();"/>
<? } ?>
<br/>
  <div id="attendees">
        <?=$attendeeList ?>
  </div>
</div>

<div id="event_description">
        <p><strong>Summary</strong></p>
        <p><?=$summary ?></p>
        <p><strong>Description</strong></p>
  <p><?=$description ?></p>
</div>

<div class="clr"></div>
<div id="event_map">
<div id="map" style="height: 480px; width: 100%;"></div>
<script type="text/javascript">
// Create a map object
        var map = new YMap(document.getElementById('map'));
        // Add map type control
        map.addTypeControl();
        // Add map zoom (long) control
        map.addZoomLong();
        // Add the Pan Control
        map.addPanControl();
        // Set map type to either of: YAHOO_MAP_SAT, YAHOO_MAP_HYB, YAHOO_MAP_REG
        map.setMapType(YAHOO_MAP_REG);
        // Display the map centered on a geocoded location
        map.drawZoomAndCenter(<? echo '"'.$street1." ".$street2." ".$city." ".$state." ".$zip." ".$country.'"';?>, 5);
        YEvent.Capture(map, EventsList.onEndGeoCode, setMarker);

        function setMarker(){
var currentGeoPoint = map.getCenterLatLon();
var marker = new YMarker(currentGeoPoint);
marker.addLabel("A");
map.addOverlay(marker);
}
</script>
</div>

<div id="event_tags">
  <h1 class="inline">Tags::</h1>
  <div id="tag_list"><?=$eventTaglist ?></div>
  <div class="clr"></div>
</div>

<div id="event_comments">
<h2 class="event_detail_heading">Comments</h2>
  <ol id="comment_list">
<?=$eventCommentsRating?>
</ol>
</div>
<div id="comment_add_link">
        <? if(!is_null($_SESSION["uname"])){ ?>
        <a href="#comment" id="commentsRatingToggle" onclick="return ShowHideLayer('commentsRatingBox');">Add a comment</a>
        <?} else {?>
    <br />
    <a href="index.php">Login</a> to leave a comment.
        <? } ?>
</div>
<div id="commentsRatingBox" style="display: none;">
<div id="comment_form">
      <form id="commentsForm" method="post" action="events.php?socialEventID=<? echo $se;?>" >
            <strong>Comment</strong><br/>
            <textarea cols="40" id="comments" name="comments" rows="20"></textarea>
<br/>
            <strong>Rating</strong><br />
            <div id="rating" class="simple_comment_rating">
               <img border="0" src="images/star_off.png" onmouseout="outStars(1, 0.0);" name="star_1" id="star_1" onclick='rateEvent(1);' onmouseover="overStars(1, 0.0);" ><img
                border="0" src="images/star_off.png" onmouseout="outStars(2, 0.0);" name="star_2" id="star_2" onclick='rateEvent(2);' onmouseover="overStars(2, 0.0);" ><img
                border="0" src="images/star_off.png" onmouseout="outStars(3, 0.0);" name="star_3" id="star_3" onclick='rateEvent(3);' onmouseover="overStars(3, 0.0);" ><img
                border="0" src="images/star_off.png" onmouseout="outStars(4, 0.0);" name="star_4" id="star_4" onclick='rateEvent(4);' onmouseover="overStars(4, 0.0);" ><img
                border="0" src="images/star_off.png" onmouseout="outStars(5, 0.0);" name="star_5" id="star_5" onclick='rateEvent(5);' onmouseover="overStars(5, 0.0);" >
           
	   </div>
           <div id="ratingText"></div>
           <input type="submit" value="Comment" name="commentsratingsubmit">
     </form>
    </div>
</div>
<hr class="clr" />

