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
 * 
 * $Id: UIDriver.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */ 

CREATE TABLE `addresses` (
  `id` int(11) NOT NULL auto_increment,
  `street1` varchar(55) default NULL,
  `street2` varchar(55) default NULL,
  `city` varchar(55) default NULL,
  `state` varchar(25) default NULL,
  `zip` varchar(12) default NULL,
  `country` varchar(55) default NULL,
  `latitude` decimal(14,10) default NULL,
  `longitude` decimal(14,10) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `comments` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL,
  `event_id` int(11) default NULL,
  `rating` int(11) default NULL,
  `comment` text,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_comments_on_event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `documents` (
  `id` int(11) NOT NULL auto_increment,
  `size` int(11) default NULL,
  `content_type` varchar(255) default NULL,
  `filename` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_documents_on_filename` (`filename`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `events` (
  `id` int(11) NOT NULL auto_increment,
  `title` varchar(100) default NULL,
  `description` varchar(500) default NULL,
  `telephone` varchar(20) default NULL,
  `user_id` int(11) default NULL,
  `address_id` int(11) default NULL,
  `image_id` int(11) default NULL,
  `document_id` int(11) default NULL,
  `event_timestamp` datetime default NULL,
  `event_date` date default NULL,
  `created_at` datetime default NULL,
  `total_score` int(11) default NULL,
  `num_votes` int(11) default NULL,
  `disabled` tinyint(1) default NULL,
  `thumbnail` int(11) default NULL,
  `summary` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_events_on_id` (`id`),
  KEY `index_events_on_event_date` (`event_date`),
  KEY `index_events_on_event_timestamp` (`event_timestamp`),
  KEY `index_events_on_created_at` (`created_at`),
  KEY `index_events_on_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `events_users` (
  `event_id` int(11) default NULL,
  `user_id` int(11) default NULL,
  KEY `index_events_users_on_event_id` (`event_id`),
  KEY `index_events_users_on_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `geolocations` (
  `id` int(11) NOT NULL auto_increment,
  `zip` int(11) default NULL,
  `state_code` varchar(255) default NULL,
  `state` varchar(255) default NULL,
  `city` varchar(255) default NULL,
  `longitude` float default NULL,
  `latitude` float default NULL,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_geolocations_on_zip` (`zip`)
) ENGINE=InnoDB AUTO_INCREMENT=29357 DEFAULT CHARSET=latin1;

CREATE TABLE `images` (
  `id` int(11) NOT NULL auto_increment,
  `size` int(11) default NULL,
  `content_type` varchar(255) default NULL,
  `filename` varchar(255) default NULL,
  `height` int(11) default NULL,
  `width` int(11) default NULL,
  `parent_id` int(11) default NULL,
  `thumbnail` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_images_on_filename` (`filename`),
  KEY `index_images_on_thumbnail` (`thumbnail`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `invites` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) NOT NULL,
  `user_id_target` int(11) NOT NULL,
  `is_accepted` tinyint(1) default '0',
  PRIMARY KEY  (`id`),
  KEY `index_invites_on_user_id` (`user_id`),
  KEY `index_invites_on_user_id_target` (`user_id_target`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `schema_migrations` (
  `version` varchar(255) NOT NULL,
  UNIQUE KEY `unique_schema_migrations` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `taggings` (
  `id` int(11) NOT NULL auto_increment,
  `tag_id` int(11) default NULL,
  `taggable_id` int(11) default NULL,
  `taggable_type` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_taggings_on_tag_id_and_taggable_id_and_taggable_type` (`tag_id`,`taggable_id`,`taggable_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `tags` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_tags_on_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `users` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(25) default NULL,
  `password` varchar(25) default NULL,
  `firstname` varchar(25) default NULL,
  `lastname` varchar(25) default NULL,
  `email` varchar(90) default NULL,
  `telephone` varchar(25) default NULL,
  `summary` varchar(2500) default NULL,
  `timezone` varchar(100) default NULL,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  `address_id` int(11) default NULL,
  `image_id` int(11) default NULL,
  `thumbnail` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_users_on_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO schema_migrations (version) VALUES ('1');

INSERT INTO schema_migrations (version) VALUES ('10');

INSERT INTO schema_migrations (version) VALUES ('11');

INSERT INTO schema_migrations (version) VALUES ('12');

INSERT INTO schema_migrations (version) VALUES ('13');

INSERT INTO schema_migrations (version) VALUES ('14');

INSERT INTO schema_migrations (version) VALUES ('15');

INSERT INTO schema_migrations (version) VALUES ('16');

INSERT INTO schema_migrations (version) VALUES ('17');

INSERT INTO schema_migrations (version) VALUES ('18');

INSERT INTO schema_migrations (version) VALUES ('19');

INSERT INTO schema_migrations (version) VALUES ('2');

INSERT INTO schema_migrations (version) VALUES ('20');

INSERT INTO schema_migrations (version) VALUES ('21');

INSERT INTO schema_migrations (version) VALUES ('22');

INSERT INTO schema_migrations (version) VALUES ('23');

INSERT INTO schema_migrations (version) VALUES ('24');

INSERT INTO schema_migrations (version) VALUES ('25');

INSERT INTO schema_migrations (version) VALUES ('26');

INSERT INTO schema_migrations (version) VALUES ('3');

INSERT INTO schema_migrations (version) VALUES ('4');

INSERT INTO schema_migrations (version) VALUES ('5');

INSERT INTO schema_migrations (version) VALUES ('6');

INSERT INTO schema_migrations (version) VALUES ('7');

INSERT INTO schema_migrations (version) VALUES ('8');

INSERT INTO schema_migrations (version) VALUES ('9');
