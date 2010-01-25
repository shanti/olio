#!/bin/sh
#
#  Licensed to the Apache Software Foundation (ASF) under one
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
##############################################################

#Script to run loader by hand

if [ -z "$1" ] ; then
    echo "Usage: $0 [concurrent users] <target directory>" >&2
    exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
    echo "Please set JAVA_HOME and restart command" >&2
    exit 1
fi

SCALE=$1

TARGET=$PWD

if [ -n "$2" ] ; then
    TARGET=$2
fi

BINDIR=`dirname $0`

# This script is in $FABAN_HOME/benchmarks/OlioDriver/bin
# we need to go up 4 levels to get to $FABAN_HOME.
if [ -n "$BINDIR" ]
then
    FABAN_HOME=`cd $BINDIR/../../.. > /dev/null 2>&1 && pwd`
    BENCH_HOME=`cd $BINDIR/.. > /dev/null 2>&1 &&pwd`
    export FABAN_HOME BENCH_HOME
fi

B=$BENCH_HOME/lib
L=$FABAN_HOME/lib
CLASSPATH=$B/OlioDriver.jar:$L/commons-httpclient-2.0.1.jar:\
$L/fabancommon.jar:$L/commons-logging.jar:$L/fabandriver.jar:$L/fabanagents.jar
export CLASSPATH

$JAVA_HOME/bin/java -server org.apache.olio.workload.fsloader.FileLoader \
    $BENCH_HOME/resources $TARGET $SCALE

EXIT_CODE=$?
if [ "$EXIT_CODE" = 0 ] ; then
    echo "File Load Successful"
else
    echo "ERROR: File loader exited with code ${EXIT_CODE}."
fi
