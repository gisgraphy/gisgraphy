#!/bin/bash

SCRIPTDIR=$(dirname ${BASH_SOURCE[0]})
cd $SCRIPTDIR
source config.sh

PID_DIR=./run
PIDFILE=$PID_DIR/$NAME.pid

printf "%-50s" "killing $NAME"
if [ -f $PIDFILE ]; then
   PID=$(cat $PIDFILE);
   printf "\n%s\n" "Found process with PID $PID";
   kill -HUP $PID
   printf "%s\n" "Ok"
   rm -f $PIDFILE
else
   printf "%s\n" "pidfile not found"
fi




