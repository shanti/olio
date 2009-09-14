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
        
?>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
      <meta http-equiv="content-type" content="text/html;charset=UTF-8" />
      <title>Web2.0 Php Application: index</title>    
      <script src="js/prototype.js" type="text/javascript"></script>
      <script src="js/effects.js" type="text/javascript"></script>
      <script src="js/dragdrop.js" type="text/javascript"></script>
      <script src="js/controls.js" type="text/javascript"></script>
      <link href="css/scaffold.css" media="screen" rel="stylesheet" type="text/css" />
      <link href="css/site.css" media="screen" rel="stylesheet" type="text/css" />
    </head>
    
    <body>
    <div id="outer_wrapper">
      <div id="header">
        <h1>PHP Performance Application</h1>    
        <div id="nav_wrapper">
          <a href="http://brazilian:8080/feedFromDB.php" id="rss_icon">
            <img alt="Rss-icon-large" src="/images/RSS-icon-large.gif" />
          </a>
          <? if(is_null($_SESSION["uname"])){ ?>
          <div id="inline_login">
            <a name="login"></a>
            <? readfile(Olio::$config['includes'] . "login.html"); ?>
          </div>
          <? } ?>
          <ul id="main_nav">
              <li ><a href="index.php" title="Home"><span>Home</span></a></li>  
              <? if(!is_null($_SESSION["uname"])){ ?>
              <li><a href="addEvent.php" title="Add Event"><span>Add Event</span></a></li>
              <li><a href="findUsers.php" title="Find Users"><span>Users</span></a></li>
              <li><a href="addPerson.php?username=<? echo $_SESSION["uname"];?>" title="Edit Profile"><span>Edit Profile</span></a></li>
              <? } else { ?>
              <li><a href="addPerson.php" title="Register"><span>Register</span></a></li>
              <? } ?>
          </ul>
        </div>
        <div class="clr"></div>
      </div>

      <div id="inner_wrapper">
        <div id="content">
          <div class="rounded_corner top_right"><span></span></div>
          <div class="rounded_corner top_left"><span></span></div>
          <div class="inside">
            <div id="messages">
              <?=$fillMessage;?>
            </div>
            <div id="yield">
              <?=$fillContent;?>          
            </div>
          </div>

          <div class="rounded_corner bottom_right"><span></span></div>
          <div class="rounded_corner bottom_left"><span></span></div>
        </div>

        <div id="sidebar">
          <div class="rounded_corner top_right"><span></span></div>
          <div class="rounded_corner top_left"><span></span></div>


          <div class="inside">
              <? if(!is_null($_SESSION["uname"])){ ?>
                        Hello, <strong><a href="users.php?username=<? echo $_SESSION["uname"];?>"><?=$_SESSION["uname"]?></a></strong>
                        <a href="logout.php"> (Logout)</a><br/>
              <? }else{ ?>
                        Not logged in.
              <? } ?>
              <hr />

              <div id="calendar">
                <?require("calendar.php");?>
		<?//require("../includes/calendar.php");?>
              </div>
              <hr />
              <div id="upcoming_subset">
              <? if(!is_null($_SESSION["uname"])){
                      require_once("yourUpcomingEvents.php");
              ?>
		<div style="text-align: right; padding-right: 25px;">
			<a href="upcomingEvents.php">more...</a>
    </div>
              <?}?>
              </div>
              <? if(!is_null($_SESSION["uname"])){ ?>
              <div id="requests_link">
              <a href="users.php?username=<? echo $_SESSION["uname"];?>#incoming_requests"><div id="rq">friendship requests (<?=$_SESSION["friendshipreqs"]?>)</div></a>
              </div>
              <?}?>
              <hr />
              <form id="tagSearchForm" method="get" action="taggedEvents.php" >
           		<input type="text"  size="20" name="tag" />
           		<input type="submit" value="Search Tags" name="tagsearchsubmit" />
     	      </form>
          </div>

          <div class="rounded_corner bottom_right"><span></span></div>
          <div class="rounded_corner bottom_left"><span></span></div>

        </div>

        <div id="footer">
          <? readfile(Olio::$config['includes'] . "footer.html"); ?>
        </div>
      </div>

      <div id="outer_reflection_wrapper">
        <div id="inner_reflection_wrapper">
          <div id="reflection"></div>
          <div id="reflec_right">&nbsp;</div>

        </div>
        <div id="reflec_left">&nbsp;</div>
      </div>

    </div>

    </body>
</html>
