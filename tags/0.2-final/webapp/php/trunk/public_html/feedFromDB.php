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
?>
<?php header('Content-type: text/xml'); ?>
<?
echo '<?xml version="1.0" encoding="UTF-8"?>';
echo '<?xml-stylesheet type="text/xsl" href="xsl/feed.xsl"?>';
?>
<rss version="2.0">
<channel>
<title>BluePrints Events</title>
<description>Social Event Calendar</description>
<link>http://abhi.sfbay:8080/index.php/</link>
<copyright>Sun Microsystems - 2007 Sun Microsystems Inc.</copyright>

<?
require_once("../etc/config.php");

$connection = DBConnection::getInstance();
$q="SELECT socialeventid,title,description,UNIX_TIMESTAMP(createdtimestamp) AS pubDate FROM SOCIALEVENT ORDER BY pubDate DESC LIMIT 0,15";
$result1=$connection->query($q);

while($result=$result1->getArray()){
?>
     <item>
        <title> <?=htmlentities(strip_tags($result['title'])); ?></title>
        <description> <?=htmlentities(strip_tags($result['description'],'ENT_QUOTES'));?></description>
        <link>http://brazilian:8080/events.php?socialEventID=<?=$result['socialeventid'];?></link>
        <pubDate> <?=strftime( "%a, %d %b %Y %T %Z" , $result['pubDate']); ?></pubDate>
     </item>  
<? 
} 
?>  

</channel>
</rss>
