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
 * User's comments and rating for an event are collected here.
 * 
 */
?>
    <li class="event_comment" id="<? echo $tmp_commentid;?>">
    <? if ($tmp_uname == $_SESSION["uname"]) { ?>
    You
    <? }else{ 
       echo $tmp_uname;
    }
       echo "(".$tmp_uname_created_at.")";
    ?>
    <? if($tmp_uname_rating == 1){ ?>
    <span id="rating">
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
    </span>
    <? } else if($tmp_uname_rating == 2){  ?>
    <span id="rating">
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
    </span>
    <? } else if($tmp_uname_rating == 3){  ?>
    <span id="rating">
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
    </span>
    <? } else if($tmp_uname_rating == 4){  ?>
    <span id="rating">
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_off.png" />
    </span>
    <? } else if($tmp_uname_rating == 5){  ?>
    <span id="rating">
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
              <img alt="16-star-hot" src="images/star_on.png" />
    </span>
    <? } ?>
    <p id="comment_text<?php echo $tmp_commentid;?>"><?=$tmp_uname_comments ?></p>
  
  <div id="comment_links<?php echo $tmp_commentid;?>">
  <? if ($tmp_uname == $_SESSION["uname"]) { ?>
            <a href="#edit" id="edit<?php echo $tmp_commentid;?>" class='edit_comment' style='color:#999;' onclick="return ShowHideLayer('<?php echo "editing".$tmp_commentid;?>');">Edit</a>
            or
            <a href="#delete" id="delete<?php echo $tmp_commentid;?>" onclick="deleteCR(<?php echo $tmp_commentid;?>,<?php echo $se;?>);" class='edit_comment' style='color:#999;' >Delete</a>
		  your comment<br/>
<div id="editing<?php echo $tmp_commentid;?>" style="display: none;">
            <div id="editcomment_form<?php echo $tmp_commentid;?>">
                  <form id="editcommentsForm<?php echo $tmp_commentid;?>" method="post" action="events.php?socialEventID=<? echo $se;?>" >
                        <strong>Comment</strong><br/>
                        <textarea cols="40" id="editcomments<?php echo $tmp_commentid;?>" name="editcomments" rows="20"><?=$tmp_uname_comments?></textarea>
                        <br/>
                        <strong>Rating</strong><br />
                        <div id="editrating<?php echo $tmp_commentid;?>" class="simple_comment_rating">
                            <img border="0" src="images/star_off.png" onmouseout='editoutStars(1, 0.0);' name="editstar_1" id="editstar_1" onclick='editrateEvent(1,<?php echo $tmp_commentid;?>);' onmouseover='editoverStars(1, 0.0);' alt="1 star" title="1 star" align="absmiddle"><img
                            border="0" src="images/star_off.png" onmouseout='editoutStars(2, 0.0);' name="editstar_2" id="editstar_2" onclick='editrateEvent(2,<?php echo $tmp_commentid;?>);' onmouseover='editoverStars(2, 0.0);' alt="2 stars" title="2 stars" align="absmiddle"><img
                            border="0" src="images/star_off.png" onmouseout='editoutStars(3, 0.0);' name="editstar_3" id="editstar_3" onclick='editrateEvent(3,<?php echo $tmp_commentid;?>);' onmouseover='editoverStars(3, 0.0);' alt="3 stars" title="3 stars" align="absmiddle"><img
                            border="0" src="images/star_off.png" onmouseout='editoutStars(4, 0.0);' name="editstar_4" id="editstar_4" onclick='editrateEvent(4,<?php echo $tmp_commentid;?>);' onmouseover='editoverStars(4, 0.0);' alt="4 stars" title="4 stars" align="absmiddle"><img
                            border="0" src="images/star_off.png" onmouseout='editoutStars(5, 0.0);' name="editstar_5" id="editstar_5" onclick='editrateEvent(5,<?php echo $tmp_commentid;?>);' onmouseover='editoverStars(5, 0.0);' alt="5 stars" title="5 stars" align="absmiddle">
                       </div>
                       <div id="editratingText<?php echo $tmp_commentid;?>"></div>
                       <input type="submit" value="Comment" name="editcommentsratingsubmit<?php echo $tmp_commentid;?>">
                       <input type="hidden" value="<?php echo $tmp_commentid;?>" name="editingcid">
                 </form>
            </div>
            </div>   
            
        <?}?>
	    
 </div>
</li>	 
