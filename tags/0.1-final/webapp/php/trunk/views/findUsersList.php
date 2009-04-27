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
 * Lists the details of the registered users.
 * 
 */
?>

<li class="<?=$class?>" style="padding: 7px;">
    <div class="thumbnail_for_list">
    <a href="users.php?username=<?echo $username; ?>">
    <img src="fileService.php?cache=false&file=<?=$image?>" height=90px width=90px />
    </a>
    </div><div id="user_details_for_list">
    <h2 class="inline">
    <a href="users.php?username=<?echo $username; ?>"><?=$username?></a>
    </h2>//
    <h3 class="inline" style="padding-bottom: 5px;"><?=$firstname." ".$lastname?></h3>
    <br/>
    <?  if (($userInFriendsList==0)  && !($loggedinuser==$username) && ($invited ==0)){  ?>
             <form method="post" action="addDeleteFriend.php?flag=add&person=<?echo $loggedinuser;?>&friend=<?echo $username;?>&query=<? echo $user;?>" class="button-to">
	<div>
            	<input type="submit" value="Add Friend" />
	</div>
	</form>
        <? } else  if(($userInFriendsList==0)  && !($loggedinuser==$username) && !($invited ==0)){
        ?>
	<form method="post" action="addDeleteFriend.php?flag=delete&person=<?echo $loggedinuser;?>&friend=<?echo $username;?>&query=<? echo $user;?>" class="button-to">
          <div>
    		<input onclick="return confirm('Are you sure?');" type="submit" value="Revoke Invite" />
          </div>
        </form>
    <? } ?>
    </div>
    <div class="clr"></div>
</li>
