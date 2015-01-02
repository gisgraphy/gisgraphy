#!/bin/bash

SCRIPTDIR=$(dirname ${BASH_SOURCE[0]})
cd $SCRIPTDIR
if [ "$1" == "" ]; then
  echo error: not enough parameters given.
  echo usage: unwatch.sh [PORT]
  exit 1
fi

cd $SCRIPTDIR
NAME="respawner_$1"

PID_DIR=./run
PIDFILE=$PID_DIR/$NAME.pid

printf "%-50s" "Stopping $NAME"
if [ -f $PIDFILE ]; then
   PID=$(cat $PIDFILE);
   printf "\n%s\n" "Found process with PID $PID";
   kill -9 $PID
   printf "%s\n" "Ok"
   rm -f $PIDFILE
else
   printf "%s\n" "pidfile not found"
fi




