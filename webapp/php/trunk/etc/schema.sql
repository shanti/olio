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
 */
create table PERSON(
   userid INTEGER NOT NULL AUTO_INCREMENT,
   username VARCHAR(25) NOT NULL,
   password VARCHAR(25) NOT NULL,
   firstname VARCHAR(25) NOT NULL,
   lastname VARCHAR(25) NOT NULL,
   email VARCHAR(90) NOT NULL,
   telephone VARCHAR(25) NOT NULL,
   imageurl VARCHAR(100) NOT NULL,
   imagethumburl VARCHAR(100) NOT NULL,
   summary VARCHAR(2500) NOT NULL,
   timezone VARCHAR(25) NOT NULL,
   ADDRESS_addressid INTEGER NOT NULL,
   primary key (userid)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX PERSON_USER_IDX on PERSON (username);

CREATE TABLE PERSON_PERSON (
Person_username VARCHAR(25) NOT NULL,
friends_username VARCHAR(25) NOT NULL,
is_accepted tinyint(1) NOT NULL,
PRIMARY KEY (Person_username, friends_username)
) ENGINE=InnoDB;
CREATE INDEX PERSON_USERNAME_IDX on PERSON_PERSON (Person_username);
CREATE INDEX FRIENDS_USERNAME_IDX on PERSON_PERSON (friends_username);

CREATE TABLE PERSON_SOCIALEVENT (
username VARCHAR(25) NOT NULL,
socialeventid INTEGER NOT NULL,
PRIMARY KEY (username, socialeventid)
)ENGINE=InnoDB;

CREATE INDEX SOCIALEVENT_PERSON_IDX on PERSON_SOCIALEVENT (socialeventid);

CREATE TABLE ADDRESS (
addressid INTEGER NOT NULL AUTO_INCREMENT,
street1 VARCHAR(55) NOT NULL,
street2 VARCHAR(55),
city VARCHAR(55) NOT NULL,
state VARCHAR(25) NOT NULL,
zip VARCHAR(12) NOT NULL,
country VARCHAR(55) NOT NULL,
latitude DECIMAL(14,10) NOT NULL,
longitude DECIMAL(14,10) NOT NULL,
primary key (addressid)
)ENGINE=InnoDB;

CREATE TABLE ID_GEN (
   gen_key VARCHAR(20) NOT NULL,
   gen_value INTEGER NOT NULL,
   primary key (gen_key)
)ENGINE=InnoDB;

CREATE TABLE SOCIALEVENT (
   socialeventid INTEGER NOT NULL AUTO_INCREMENT,
   title VARCHAR(100) NOT NULL,
   description VARCHAR(500) NOT NULL,
   submitterUserName VARCHAR(25) NOT NULL,
   ADDRESS_addressid INTEGER NOT NULL,
   totalscore INTEGER NOT NULL,
   numberofvotes INTEGER NOT NULL,
   imageurl VARCHAR(100) NOT NULL,
   imagethumburl VARCHAR(100) NOT NULL,
   literatureurl VARCHAR(100) NOT NULL,
   telephone VARCHAR(20) NOT NULL,
   timezone VARCHAR(100) NOT NULL,
   eventtimestamp VARCHAR(25) NOT NULL,
   createdtimestamp TIMESTAMP NOT NULL,
   disabled INTEGER NOT NULL,
   eventdate VARCHAR(10) NOT NULL,
   summary VARCHAR(100) NOT NULL,
   primary key (socialeventid),
   foreign key (ADDRESS_addressid) references ADDRESS(addressid)
)ENGINE=InnoDB;

CREATE INDEX SOCIALEVENT_DATE_IDX on SOCIALEVENT (eventdate);
CREATE INDEX SOCIALEVENT_EVENTTIMESTAMP_IDX on SOCIALEVENT (eventtimestamp);
CREATE INDEX SOCIALEVENT_CREATEDTIMESTAMP_IDX on SOCIALEVENT (createdtimestamp);
CREATE INDEX SOCIALEVENT_SUBMITTERUSERNAME_IDX  on SOCIALEVENT (submitterUserName);

create table SOCIALEVENTTAG(
   socialeventtagid INTEGER NOT NULL AUTO_INCREMENT,
   tag VARCHAR(30) NOT NULL,
   refcount INTEGER NOT NULL,
   primary key (socialeventtagid),
   unique(tag)
)ENGINE=InnoDB;

CREATE INDEX SOCIALEVENTTAG_TAG on SOCIALEVENTTAG (tag);

create table SOCIALEVENTTAG_SOCIALEVENT(
   socialeventtagid INTEGER NOT NULL,
   socialeventid INTEGER NOT NULL,
   unique(socialeventtagid, socialeventid),
   foreign key (socialeventid) references SOCIALEVENT(socialeventid),
   foreign key (socialeventtagid) references SOCIALEVENTTAG(socialeventtagid)
)ENGINE=InnoDB;

CREATE INDEX SOCIALEVENT_TAG on SOCIALEVENTTAG_SOCIALEVENT (socialeventid);

create table COMMENTS_RATING(
   commentid INTEGER NOT NULL AUTO_INCREMENT,
   username VARCHAR(25) NOT NULL,
   socialeventid INTEGER NOT NULL,
   comments VARCHAR(2500) NOT NULL,
   ratings INTEGER NOT NULL,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP,
   foreign key (socialeventid) references SOCIALEVENT(socialeventid),
   foreign key (username) references PERSON(username),
   primary key (commentid)
)ENGINE=InnoDB;

CREATE INDEX SOCIALEVENT_COMMENTS on COMMENTS_RATING (socialeventid);
