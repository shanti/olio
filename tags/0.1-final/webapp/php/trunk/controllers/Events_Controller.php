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
 * This is events controller which computes the top events list on the home page.
 * Computes events for the selected calendar date.
 * Provides event details for a particular event.
 */

class Events_Controller {
    function formatdatetime($syntax,$DateTime) {
            $year = substr($DateTime,0,4);
            $month = substr($DateTime,5,2);
            $day = substr($DateTime,8,2);
            $hour = substr($DateTime,11,2);
            $min = substr($DateTime,14,2);
            $sec = substr($DateTime,17,2);
            return date($syntax, mktime($hour,$min,$sec,$month,$day,$year));
    }	

    function getHomePageEvents($zipcode,$order,$offset){
            if(is_null($zipcode) || is_null($order) ) {
              $query = "select socialeventid,title,summary,imagethumburl,createdtimestamp,eventdate,submitterusername From SOCIALEVENT where  eventtimestamp>=CURRENT_TIMESTAMP ORDER BY eventdate ASC limit $offset,10";
            }else if(!is_null($zipcode)  && !is_null($order) && $order == "created_at"){
              $query = "select socialeventid,title,summary,imagethumburl,createdtimestamp,eventdate,submitterusername From SOCIALEVENT as se,ADDRESS as a where se.eventtimestamp>=CURRENT_TIMESTAMP and se.ADDRESS_addressid=a.addressid and a.zip='$zipcode' ORDER BY se.createdtimestamp DESC limit $offset,10";
            }else if(!is_null($zipcode)  && !is_null($order) && $order == "event_date"){
              $query = "select socialeventid,title,summary,imagethumburl,createdtimestamp,eventdate,submitterusername From SOCIALEVENT as se,ADDRESS as a where se.eventtimestamp>=CURRENT_TIMESTAMP and se.ADDRESS_addressid=a.addressid and a.zip='$zipcode' ORDER BY se.eventdate ASC limit $offset,10";
            }      
            return $query;            
    }

    function getNumPages($zipcode,$eventdate,$connection){
            if(!is_null($eventdate)){
             $query = "select count(*) as count From SOCIALEVENT  where eventdate='$eventdate'";
            }else if(!is_null($_REQUEST['zipcode']) ){
            $query = "select count(*) as count From SOCIALEVENT as se,ADDRESS as a where se.eventtimestamp>=CURRENT_TIMESTAMP and se.ADDRESS_addressid=a.addressid and a.zip='$zipcode' ";
            }else{
            $query = "select count(*) as count From SOCIALEVENT where  eventtimestamp>=CURRENT_TIMESTAMP";
            }
            $result = $connection->query($query);
            $row = $result->getArray();
            $numEvents = $row['count'];
            //Calcuate total pages
            $numPages  = ceil($numEvents / 10);
            unset($result);
            return $numPages;
    }
   
    function getIndexEvents($zipcode,$order,$eventdate,$offset,$seid,$signedinuser,$connection){
            if(!is_null($seid)){
            $eventsQuery="select socialeventid,title,summary,imagethumburl,createdtimestamp,eventdate,submitterusername from SOCIALEVENT where socialeventid='$seid' ";
            }else if(!is_null($eventdate) ){
              $eventsQuery=" select socialeventid,title,summary,imagethumburl,createdtimestamp,eventdate,submitterusername From SOCIALEVENT as se,ADDRESS as a where se.ADDRESS_addressid=a.addressid and se.eventdate='$eventdate' ORDER BY se.eventdate ASC limit $offset,10";
            }else{
              $eventsQuery=$this->getHomePageEvents($zipcode,$order,$offset);
            }
            $result = $connection->query($eventsQuery);
            $dateFormat = "l,  F j,  Y,  h:i A";
            ob_start();
            while($row = $result->getArray()) {
                            $rowsFound = true;
                            $title = $row['title'];
                            $se = $row['socialeventid'];
                            $image = $row['imagethumburl'];
                            $summary = $row['summary'];
                            $submitter = $row['submitterusername'];
                            $ed = $row['eventdate'];
                            $cd = trim($this->formatdatetime($dateFormat,$row['createdtimestamp']));
                            require("../views/indexEvents.php");
            }
            unset($result);
            $indexEvents = ob_get_contents();
            ob_end_clean();
            return $indexEvents;
    }
     
    function getNumAttendees($se,$connection) {
            $query="select count(username) as count from PERSON_SOCIALEVENT where socialeventid = '$se'";
            $result = $connection->query($query);
            $row = $result->getArray();
            $count = $row['count'];
            unset($result);
            return $count;
    }
    
    function getRecentlyPostedEventsOfUser($user,$connection,$flag,$offset){
            if (!$flag){
              //$query="select socialeventid,title from SOCIALEVENT where submitterusername='$user' and createdtimestamp between date_add(now(),interval -15 day) and now() and eventtimestamp>=CURRENT_TIMESTAMP order by eventdate asc limit 10";
              $query="select socialeventid,title from SOCIALEVENT where submitterusername='$user' and createdtimestamp<=now() and eventtimestamp>=CURRENT_TIMESTAMP order by eventdate asc limit 10";
            $count = 1;
            }else if ($flag){
              //$query="select socialeventid,title from SOCIALEVENT where submitterusername='$user' and createdtimestamp between date_add(now(),interval -90 day) and now() and eventtimestamp>=CURRENT_TIMESTAMP order by eventdate asc limit 30";  
              $query="select socialeventid,title from SOCIALEVENT where submitterusername='$user' and createdtimestamp<=now() and eventtimestamp>=CURRENT_TIMESTAMP order by eventdate asc limit $offset,10";          
              $count = 1 + $offset;
            }
            $result = $connection->query($query);
            while($row = $result->getArray()) {
                $rowsFound = true;
                $title = $row['title'];
                $se = $row['socialeventid'];
                $recentPostedEvents = $recentPostedEvents." ".'<a href="events.php?socialEventID='.$se.'">'.$count.'. '.$title.'</a><br/>';                
                $count++;
            }
            unset($result);
            return $recentPostedEvents;
    }

    function getUpcomingEventsForUser($user,$connection,$flag,$offset){
            if (!$flag){
		$query = "select se.socialeventid,se.title From SOCIALEVENT as se,PERSON_SOCIALEVENT as ps where se.socialeventid=ps.socialeventid and se.eventtimestamp>=CURRENT_TIMESTAMP and ps.username='$user' ORDER BY se.eventdate ASC limit 3";
                $count = 1;
            }else if ($flag){
		$query = "select se.socialeventid,se.title From SOCIALEVENT as se,PERSON_SOCIALEVENT as ps where se.socialeventid=ps.socialeventid and se.eventtimestamp>=CURRENT_TIMESTAMP and ps.username='$user' ORDER BY se.eventdate ASC limit $offset,10";
		 $count = 1 + $offset;
            }
            $result = $connection->query($query);
            while($row = $result->getArray()) {
                $rowsFound = true;
                $title = $row['title'];
                $se = $row['socialeventid'];
                $upcomingEvents = $upcomingEvents." ".'<a href="events.php?socialEventID='.$se.'">'.$count.'. '.$title.'</a><br/>';
                $count++;
            }
            unset($result);
            return $upcomingEvents;
    }


    static function getInstance() {
        $instance = new Events_Controller();
        return $instance;
    }
    
}
?>
