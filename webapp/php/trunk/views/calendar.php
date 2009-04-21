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
 * This is an event calendar. Onclick of any date, displays all the
 * events for that date paginated.
 * 
 */
?>
<script src="js/httpobject.js" type="text/javascript"></script>
<script type="text/javascript">
var updateMYLink = "calendar.php?mon=";
    function handleMonthYear() {
         if (http.readyState == 4) {
             result = http.responseText;
             document.getElementById("cal").innerHTML=result;
         }
    }
    function updateCalendar(month,year) {
         http.open("GET", updateMYLink + escape(month) + "&yr=" + escape(year), true);
         http.onreadystatechange = handleMonthYear;
         http.send(null);
    }
</script>
<div id="cal">
<table class="calendar" border="0" cellspacing="0" cellpadding="0">
    <caption class="monthName"></caption><thead>
<tr><th id="calheader" colspan="7">
      <a href="#prev_year" class="prev_year" onclick="updateCalendar(<?echo $month;?>,<? echo $prev_year;?>)"><</a>
      <a href="#prev_month" class="prev_month" onclick="updateCalendar(<? echo $prev_month;?>,<? echo ($prev_month==12?($year-1):$year);?>)"><</a>
      <?=$monthName." ".$year?>
      <a href="#next_month" class="next_month" onclick="updateCalendar(<?echo $next_month;?>,<? echo ($next_month==1?($year+1):$year);?>)">></a>
      <a href="#next_year" class="next_year" onclick="updateCalendar(<?echo $month;?>,<? echo $next_year;?>)">></a>
      </th></tr>

<tr class='dayName'>
            <th scope='col'><abbr title='Sunday'>S</abbr></th>
            <th scope='col'><abbr title='Monday'>M</abbr></th>
            <th scope='col'><abbr title='Tuesday'>T</abbr></th>
            <th scope='col'><abbr title='Wednesday'>W</abbr></th>
            <th scope='col'><abbr title='Thursday'>T</abbr></th>
            <th scope='col'><abbr title='Friday'>F</abbr></th>
            <th scope='col'><abbr title='Saturday'>S</abbr></th>
        </tr>
    </thead>
    <tbody><tr>


<? echo $fillWithSpaces;
for($i=1;$i<=$numDaysInAMonth;$i++){
    if($i<10){?>
    <td class='day'><a href='index.php?month=<?=$month?>&day=0<?=$i?>&year=<?=$this_year?>'><?=$i ?></a><br></td>
    <?}else{?>
    <td class='day'><a href='index.php?month=<?=$month?>&day=<?=$i?>&year=<?=$this_year?>'><?=$i ?></a><br></td>
    <?}
    $firstDay ++;
    if($firstDay==7){?>
        </tr><tr>
        <?$firstDay=0;
    }
}
if($firstDay<7 && $firstDay>0){
$nextMonthDay = 1;
for($i=$firstDay; $i<7; $i++){
$fillTrailingSpaces .='<td class="otherMonth">'.$nextMonthDay.'</td>';
$nextMonthDay++;
}
}
echo $fillTrailingSpaces;?>
</tr></tbody></table>
</div>
