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
    
if(!is_null($_REQUEST['day']) ){
  $month = $_REQUEST['month'];
  $this_year = $_REQUEST['year'];
}else if(!is_null($_REQUEST['mon']) ){
  $month = $_REQUEST['mon'];
  if($month<10 && $month>0){
     $month = "0".$month;
  }  
  $this_year = $_REQUEST['yr'];
}else{
  $month = date("m");
  $this_year = date("Y");
  session_unregister ("mon");
  session_unregister ("yr");
}

// today
$today = date("d");
//Find the number of days in a month
$numDaysInAMonth = date('t',mktime(0,0,0,$month,1,$this_year));
//Find the name of the month to be displayed in the header
$monthName = date('M',mktime(0,0,0,$month,1,$this_year));
//Find the year to be displayed in the header
$year = date('Y',mktime(0,0,0,$month,1,$this_year));
//Find the first day of a week for month
$firstDay = date('w',mktime(0,0,0,$month,1,$this_year));
$numDaysInPrevMonth =  date('t',mktime(0,0,0,$month-1,1,$this_year));
$prevMonthDay = $numDaysInPrevMonth-$firstDay+1;
for($i=1; $i<=$firstDay; $i++){
$fillWithSpaces .='<td class="otherMonth">'.$prevMonthDay.'</td>';
$prevMonthDay++;
}
$prev_year= $year-1;
$next_year= $year+1;
//$prev_month = $month-1;
//$next_month = $month+1;

//$prev_month = $month==1?12:$month-1;
//$next_month = $month==12?1:$month+1;

switch($month)
 {
     case 1:
           //this is the first month of the year
           $prev_month = 12;
           $next_month = $month+1;
           break;

     case 12:
           //this is the last month of the year
          $prev_month = $month -1;
          $next_month = 1;
         break;

     default:
         //all other cases
$prev_month = $month-1;
$next_month = $month+1;
        break;
 }

ob_start();
require("../views/calendar.php");
$calendar = ob_get_clean();
echo $calendar;
?>

