#!/bin/sh
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

# This script is in $FABAN_HOME/benchmarks/Web20Driver/bin
# we need to go up 4 levels to get to $FABAN_HOME.
if [ -n "$BINDIR" ]
then
    FABAN_HOME=`cd $BINDIR/../../.. > /dev/null 2>&1 && pwd`
    BENCH_HOME=`cd $BINDIR/.. > /dev/null 2>&1 &&pwd`
    export FABAN_HOME BENCH_HOME
fi

B=$BENCH_HOME/lib
L=$FABAN_HOME/lib
CLASSPATH=$B/mysql-connector-java-5.0.6-bin.jar:$B/Web20Driver.jar:\
$L/commons-httpclient-2.0.1.jar:$L/fabancommon.jar:$L/commons-logging.jar:\
$L/fabandriver.jar:$L/fabanagents.jar
export CLASSPATH

$JAVA_HOME/bin/java -server com.sun.web20.loader.LoadController com.mysql.jdbc.Driver \
 "jdbc:mysql://$DB_HOST/web20ror?user=web20&password=web20&relaxAutoCommit=true&sessionVariables=FOREIGN_KEY_CHECKS=0" $SCALE
EXIT_CODE=$?  
if [ "$EXIT_CODE" = 0 ] ; then
    echo "Database Load Successful"
else
    echo "ERROR: Database loader exited with code ${EXIT_CODE}."
fi
