#!/bin/bash

SCRIPTDIR=$(dirname ${BASH_SOURCE[0]})
cd $SCRIPTDIR
source config.sh

PID_DIR=./run
PIDFILE=$PID_DIR/$NAME.pid

printf "%-50s" "Checking $NAME..."
if [ -f $PIDFILE ]; then
    PID=`cat $PIDFILE`
    if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
        printf "%s\n" "Process dead but pidfile exists"
    else
        echo "Running"
    fi
else
    printf "%s\n" "Service not running"
fi





