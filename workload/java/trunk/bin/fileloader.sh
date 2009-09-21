
#!/bin/sh
##############################################################
#  Copyright ?? 2008 Sun Microsystems, Inc. All rights reserved
#
#  Use is subject to license terms.
#
#  $Id: fileloader.sh,v 1.1.1.1 2008/09/29 22:33:07 sp208304 Exp $
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
CLASSPATH=$B/OlioDriver.jar:$L/fabancommon.jar:$L/commons-logging.jar:$L/fabandriver.jar:$L/fabanagents.jar
export CLASSPATH

$JAVA_HOME/bin/java -server org.apache.olio.workload.fsloader.FileLoader \
    $BENCH_HOME/resources $TARGET $SCALE

EXIT_CODE=$?
if [ "$EXIT_CODE" = 0 ] ; then
    echo "File Load Successful"
else
    echo "ERROR: File loader exited with code ${EXIT_CODE}."
fi

