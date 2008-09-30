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
 * Lists the details of event.
 * 
 */
?>      
<script src="js/httpobject.js" type="text/javascript"></script>
<script type="text/javascript">
    var deleteSElink = "deleteEvent.php?socialEventID=";
    function deleteSE(se) {
         http.open("GET", deleteSElink + escape(se), true);
         http.send(null);
    }
</script>
<li id="event_1_details" class="event_item even_event" style="padding: 7px;"
    onmouseover="Element.findChildren(this, 'extra_details', true, 'div').first().show();"
    onmouseout="Element.findChildren(this, 'extra_details', true, 'div').first().hide();">
      <div class="thumbnail_for_list">
        <div style="width: 90px; height: 90px; border:1px solid #CCC; color: #666;text-align: center; vertical-align: middle; display: table-cell;">
          <a href="events.php?socialEventID=<?echo $se;?>"><img src="fileService.php?cache=false&file=<?=$image?>" height=90px width=90px /></a>
        </div>
      </div>
      <div class="event_details_for_list">
        <h2 class="tight_heading">
        <a href="events.php?socialEventID=<?echo $se;?>"><?=$title?></a>
        </h2>
        <?=$ed?>
        <div class="extra_details" style="display: none;">
        <? 
         if ($submitter == $signedinuser) { ?>
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
            <br />
            Created: <?=$cd ?><br/>
            <br/><?=$summary ?>
        </div>
     </div>
     <div class="clr"></div>
     </li>
