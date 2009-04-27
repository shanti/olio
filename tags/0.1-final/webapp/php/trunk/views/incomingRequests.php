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
 * This displays the incoming requests for the logged in user.
 *  
 * NOTE: This file is purposely edited to have no spaces in between the html tags
 * because the response text for friendCloud and incoming requests are separted by newline.
 * 
 */
?>
<li id="incoming_friend_request"><a href="users.php?username=<?echo $friendun1;?>"><?=$fn1." ".$ln1?></a><div id="approve_friend"><input type="button" value="Approve Friendship" onclick="approveFriendship('<?echo $personun1;?>','<?echo $friendun1;?>')" /></div><div id="reject_invite_friend"><input type="button" value="Reject invite" onclick="rejectInvite('<?echo $personun1;?>','<?echo $friendun1;?>')" /></div></li>
