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
?>
<script src="js/httpobject.js" type="text/javascript"></script>
<script type="text/javascript">
    var revokeLink = "revokeInvite.php?person=";
    var rejectLink = "rejectInvite.php?person=";
    var approveLink= "approveFriendship.php?person=";
    function handleApproveFriendship() {
             if (http.readyState == 4) {
                 results = http.responseText.split("\n");
                 document.getElementById("messages").innerHTML="<font color=green>Friendship approved.</font>";
                 document.getElementById("rq").innerHTML=results[0];
                 document.getElementById("friendCloud").innerHTML=results[1];
                 for(i=2; i < results.length - 1; i++) {
                    var ilist = ilist + results[i];
                 }
                 document.getElementById("incoming_list").innerHTML=ilist;
             }
    }

    function approveFriendship(person,friend) {
             http.open("GET", approveLink + escape(person) + "&friend=" + escape(friend) , true);
             http.onreadystatechange =  handleApproveFriendship;
             http.send(null);
    }

    function handleRevokeInvite() {
             if (http.readyState == 4) {
                 results = http.responseText.split("\n");
                 document.getElementById("messages").innerHTML=results[0];
                 for(i=1; i < results.length - 1; i++) {
                    var ilist = ilist + results[i];
                 }
                 document.getElementById("outgoing_list").innerHTML=ilist;
             }
    }

    function revokeInvite(person,friend) {
             http.open("GET", revokeLink + escape(person) + "&friend=" + escape(friend) , true);
             http.onreadystatechange =  handleRevokeInvite;
             http.send(null);
    }

    function handleRejectInvite() {
             if (http.readyState == 4) {
                 results = http.responseText.split("\n");
                 document.getElementById("messages").innerHTML=results[0];
                 document.getElementById("rq").innerHTML=results[1];
                 for(i=2; i < results.length - 1; i++) {
                    var ilist = ilist + results[i];
                 }
                 document.getElementById("incoming_list").innerHTML=ilist;
             }
    }

    function rejectInvite(person,friend) {
             http.open("GET", rejectLink + escape(person) + "&friend=" + escape(friend) , true);
             http.onreadystatechange =  handleRejectInvite;
             http.send(null);
    }
</script>

<div id="user_header">
    <div id="user_thumbnail">
        <div style='width: 150px; height: 150px; border: 1px solid #CCC; color: #666;
             text-align: center; vertical-align: middle; display: table-cell;'>
             <img src="fileService.php?cache=false&file=<?=$image;?>" height=90px width=90px />
        </div>
    </div>
    <div id="main_user_details">
        <h1 class="inline"><?=$username;?></h1>
            <? 
	      if ($username == $_SESSION["uname"]) {
              echo '(<a href="addPerson.php?username='.$username.'">Edit</a>)'; }
            ?>     
            <br />
        <h2 class="inline"><?=$firstname." ".$lastname;?></h2>
        <hr />
        <?=$timezone;?>
        <br />	
        <div id="user_address">
          <?=$street1." ".$street2; ?><br />
          <?=$city.",".$state." ".$zip; ?><br />
          <?=$country; ?>
        </div>
        <br />
        <span id="user_telephone"><?=$telephone; ?></span><br />
        <span id="user_email"><?=$email; ?></span><br />
    </div>
    <div id="profile_friendship_link"></div>
    <div class="clr"></div>
</div>

<div id="user_summary">
  <?=$summary; ?>
</div>
<hr class="clr" />
<div id="posted">
  <h1>Your Recently Posted Events</h1>
  <?=$recentlyPostedEvents?>
  <? echo '<a href="postedEvents.php?username='.$username.'">more...</a>'; ?>	
</div>
<div id="friend_cloud">
  <h2>Friend Cloud</h2>
  <p id="friendCloud"><?=$friendCloud;?></p>
  <p class="clr" />
</div>
<?if ($username == $_SESSION["uname"]) {?>
<a name="incoming_requests"></a>
<div id="incoming">
<fieldset id="incoming_requests">
    <legend>Incoming friendship invitations</legend>
    <ol id="incoming_list">
     <?=$incomingRequests?>
    </ol>
  </fieldset>
</div>
<a name="outgoing_requests"></a>
<div id="outgoing">
<fieldset id="outgoing_requests">
    <legend>Outgoing friendship invitations</legend>
    <ol id="outgoing_list">
    <?=$outgoingRequests?>
    </ol>
  </fieldset>
</div>
<? } ?>
<hr />

