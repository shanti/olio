#!/bin/sh
##############################################################
#  Copyright ?? 2008 Sun Microsystems, Inc. All rights reserved
#
#  Use is subject to license terms.
#
#  $Id: dbloader.sh,v 1.1.1.1 2008/09/29 22:33:07 sp208304 Exp $
##############################################################

#Script to run loader by hand

#Edit above lines if required
if [ -z "$2" ] ; then
    echo "Usage: $0 [dbserver] [concurrent users]" >&2
    exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
    echo "Please set JAVA_HOME and restart command" >&2
    exit 1
fi

SCALE=$2
DB_HOST=$1

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
CLASSPATH=$B/mysql-connector-java-5.0.6-bin.jar:$B/json.jar:$B/OlioDriver.jar:\
$L/commons-httpclient-2.0.1.jar:$L/fabancommon.jar:$L/commons-logging.jar:\
$L/fabandriver.jar:$L/fabanagents.jar
export CLASSPATH

$JAVA_HOME/bin/java -server org.apache.olio.workload.loader.LoadController com.mysql.jdbc.Driver \
"jdbc:mysql://$DB_HOST/olio?user=olio&password=olio&relaxAutoCommit=true&sessionVariables=FOREIGN_KEY_CHECKS=0" $SCALE
EXIT_CODE=$?
if [ "$EXIT_CODE" = 0 ] ; then
    echo "Database Load Successful"
else
    echo "ERROR: Database loader exited with code ${EXIT_CODE}."
fi
