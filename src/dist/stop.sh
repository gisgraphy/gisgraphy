#!/bin/bash

SCRIPTDIR=$(dirname ${BASH_SOURCE[0]})
cd $SCRIPTDIR
source config.sh

PID_DIR=./run
PIDFILE=$PID_DIR/$NAME.pid

printf "%-50s\n" "Stopping $NAME"
if [ -f $PIDFILE ]; then
   PID=$(cat $PIDFILE);
   printf "\n%s\n" "Found process with PID $PID";
   java -DSTOP.PORT=8079 -DSTOP.KEY=stopkey -Dfile.encoding=UTF-8 -Xmx2048m -Xms512m -jar start.jar --stop
   printf "%s\n" "Ok"
   rm -f $PIDFILE
else
   printf "%s\n" "pidfile not found"
fi




