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
 * This describes the pagination view.
 */
?>
<?
if($numPages > 1) {
if($prev_page > 0) {?>
   < < <a href='<?=$href?>&page=<?=$prev_page?>'><b>Previous</b></a> <?}
for($page = 1; $page <= $numPages; $page++)
{
   if($page == $curr_page) {?>
      &nbsp <b> <? echo $page;?> </b>
   <?}else {
      if($page<=10  || $page<=$curr_page) {?>
      &nbsp <a href='<?=$href?>&page=<?=$page?>'> <? echo $page;?> </a>
      <?}
   }
}
if( $numPages > 10 ) {?>
 ..........
<?}
if($next_page <= $numPages) { ?>
   <a href="<?=$href?>&page=<?=$next_page?>"><b>Next</b></a> > >
<?}
}
?>
