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
 * This is the Home page which displays all the events paginated 10 per page,
 * sorted by eventdate by default.
 * 
 */
?>
              <div id="event_list_wrapper"> 
                <div id="event_filtering">
                  <div style="float: right; margin-right: 10px;">
                    <form action="index.php" id="filter_event_list" method="get">Zipcode: 
                        <input id="zipcode" name="zipcode" type="text" value="" />
                        <br />Sort: 
                        <input id="order_created_at" name="order" type="radio" value="created_at" />
                        Created Date
                        <input checked="checked" id="order_event_date" name="order" type="radio" value="event_date" />
                        Event Date
                        <input name="commit" type="submit" value="Filter" />
                    </form>                
                  </div>
                  <div>
                    <h1 class="tight_heading">All Events</h1>
                    <?if(is_null($zipcode) || is_null($order) ) {?>
                    <h2 id="which_timezone" class="tight_heading">for all locations</h2>
                    <?}else if(!is_null($zipcode)  && !is_null($order)){ ?>
                    <h2 id="which_timezone" class="tight_heading">for zipcode: <?=$zipcode?></h2>
		    <?}?>
                  </div>
               </div>

              <hr class="clr" />
              <div id="event_table">
                  <ol id="event_list" style="list-style-type: none;">
<?=$indexEvents?>
</ol>
<div class="clr"></div>
<br />
<?=$paginateView?>
</div>
   <div class="clr"></div>
   <br />
</div>
<br />    
<div id="tag_cloud"><h2 class="tight_heading">Tag Cloud</h2>
    <div>
    <?=$tagcloud->getHomePageTagCloud($connection);?>
    </div>
</div>

