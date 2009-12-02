#!/bin/sh
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
########
##Sample CLI script for GlassFish configuration for JDBC pool and resource and basic JVM options
##You must edit the appropriate values for your setup.
##It assumes that you are executing this script on the machine with your GlassFish instance,
##although it can be modified to be executed remotely assuming you have the asadmin binary somewhere on your local machine.
########
# Edit GLASSFISH_HOME to point to your installation of GlassFish
GLASSFISH_HOME=/disk_array/web20perf/java/glassfishv2_1_1/glassfish
########
##Create JDBC connection pool and resource
# Edit URL property and ServerName property to point to your machine with the mySQL database installation
# Pool properties are passed in the form of name:value, hence need an escape character for the ":" character for the URL property.
# i.e. --property User=olio:Password=olio:name:value:name2:value2 etc.
# This script assumes default ports with typical installation.
#######
$GLASSFISH_HOME/bin/asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --host localhost --port 4848 --user admin --property DatabaseName=bpwebapp:User=olio:Password=olio:URL="jdbc\:mysql\://jes-x4100-6\:3306/bpwebapp:ServerName=jes-x4100-6:port=3306" --restype javax.sql.DataSource BPwebappPool
$GLASSFISH_HOME/bin/asadmin create-jdbc-resource --connectionpoolid BPwebappPool --host localhost --port 4848 --user admin jdbc/BPWebappDB
## Create JVM options
# Edit image directory property to point to your filestore i.e. where you have loaded the images
$GLASSFISH_HOME/bin/asadmin create-jvm-options --host localhost --port 4848 --user admin "-Dwebapp.image.directory=/Users/klichong/downloads/filestore"
# Edit geocode property to point to your deployment of the geocoder
$GLASSFISH_HOME/bin/asadmin create-jvm-options --host localhost --port 4848 --user admin "-DgeocoderURL=http\://jes-x4600-1\:8080/Web20Emulator/geocode"
#####
# Example asadmin delete-jvm-options command in case of typographical errors:
# asadmin delete-jvm-options --host localhost --port 4848 --user admin -DcacheOlio=false
