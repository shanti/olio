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
$this_minute = $date["minutes"];
$minutes = Array("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",
       "16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32",
       "33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49",
       "50","51","52","53","54","55","56","57","58","59");
for ($i=1; $i<=60; $i++) {
    if(!is_null($minute) && $i == $minute){
        echo '<option selected="selected" value="'.$minute.'">'.$minute.'</option>';
    }else if($i == $this_minute && is_null($minute)){
      echo '<option selected="selected" value="'.$minutes[$i-1].'">'.$minutes[$i-1].'</option>';
    }else{
      echo '<option value="'.$minutes[$i-1].'">'.$minutes[$i-1].'</option>';
    }
}
?>
