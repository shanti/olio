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
 */
?>
<?php
$date = getDate();
$today = $date["mday"];
$days = Array("01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",
        "16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31");
for ($i=1; $i<=31; $i++) {
    if(!is_null($day) && $i == $day){
        echo '<option selected="selected" value="'.$day.'">'.$day.'</option>';
    }else if($i == $today && is_null($day)){
      echo '<option selected="selected" value="'.$days[$i-1].'">'.$days[$i-1].'</option>';
    }else{
      echo '<option value="'.$days[$i-1].'">'.$days[$i-1].'</option>';
    }
}
?>
